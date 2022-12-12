package tetrominocomputer.ts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import tetrominocomputer.sim.Component;
import tetrominocomputer.sim.Instruction;
import tetrominocomputer.sim.Playfield;
import tetrominocomputer.sim.Terminal;
import tetrominocomputer.sim.Tetromino;
import tetrominocomputer.util.Dirs;
import tetrominocomputer.util.Out;

public class Tester extends AbstractSimulator {
    
    private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
    private final List<Bindings> bindingsPool = Collections.synchronizedList(new ArrayList<>());
    
    public void launch() throws Exception {                  
        Out.timeTask("Loading components...", () -> {
            final Map<String, Component> components = new ConcurrentHashMap<>();
            createIsSsAndZs(components);
            loadComponents(new File(Dirs.TS), components);
            
            System.out.println();
            System.out.println("Loaded components.");
            System.out.println();
            
            testComponents(components);            
            return null;
        });
    }
        
    private void testComponents(final Map<String, Component> components) throws Exception {
        
        System.out.println("Testing components...");
        System.out.println();        
                
        final ExecutorService executor = Executors.newWorkStealingPool();
        final List<Playfield> playfieldPool = Collections.synchronizedList(new ArrayList<>());

        components.forEach((componentName, component) -> {
            executor.execute(() -> {
                final Playfield playfield = borrowPlayfield(playfieldPool);
                try {
                    testComponent(components, component, playfield);
                } catch (final Exception e) {
                    e.printStackTrace();
                } finally {
                    returnPlayfield(playfieldPool, playfield);
                }
            });
        });
     
        executor.shutdown();        
        executor.awaitTermination(1, TimeUnit.DAYS);
    }
    
    private void testComponent(final Map<String, Component> components, final Component component, 
            final Playfield playfield) throws ScriptException {
        
//        System.out.println(component.getName());
        if (component.getCompiledScript() == null) {
//            System.out.format("Skipping %s.%n", component.getName());
            return;
        }
        
        if (!"COPY_A_B_C".equals(component.getName())) { // TODO TESTING
            return;
        }
        
        System.out.format("testing: %s%n", component.getName());
        for (int inputBits = (1 << component.getInputs().length) - 1; inputBits >= 0; --inputBits) {
            testComponent(components, component, playfield, inputBits);
        }        
        System.out.format("PASSED: %s%n", component.getName());
    }
    
    private void testComponent(final Map<String, Component> components, final Component component, 
            final Playfield playfield, final int inputBits) throws ScriptException {
        
        playfield.clear();
        setInputs(playfield, component, inputBits);
        simulate(components, playfield, component);
        final int outputBits = getOutputs(playfield, component);
                
        int outBits = 0;
        
        final Terminal[] inputs = component.getInputs();
        final Terminal[] outputs = component.getOutputs();
        final CompiledScript compiledScript = component.getCompiledScript();        
        final Bindings bindings = borrowBindings();
        try {
            for (int i = inputs.length - 1, inBits = inputBits; i >= 0; --i, inBits >>= 1) {
                bindings.put(inputs[i].getName(), (inBits & 1) != 0);
            }
            for (int i = outputs.length - 1; i >= 0; --i) {
                bindings.put(outputs[i].getName(), false);
            }
            compiledScript.eval(bindings);
            
            for (int i = 0; i < outputs.length; ++i) {
                outBits = (outBits << 1) | ((Boolean) bindings.get(outputs[i].getName()) ? 1 : 0);
            }
        } finally {
            returnBindings(bindings);
        }
        
        if (outputBits != outBits) {
            System.err.format("FAILED: %s -- input = %s, output = %s, expected output = %s%n", component.getName(),
                    Out.toBinaryString(inputBits, inputs.length),
                    Out.toBinaryString(outputBits, outputs.length),
                    Out.toBinaryString(outBits, outputs.length));
            System.exit(0);
        }
    }
    
    private void simulate(final Map<String, Component> components, final Playfield playfield, 
            final Component component) {
        simulate(components, playfield, component, playfield.getWidth() >> 1, playfield.getHeight() - 1);
    }

    private void simulate(final Map<String, Component> components, final Playfield playfield, final Component component, 
            final int originX, final int originY) {
                
        final Instruction[] instructions = component.getInstructions();
        if (instructions == null) {
            return;
        }
        
        for (int i = 0; i < instructions.length; ++i) {
            simulate(components, playfield, instructions[i], originX, originY);
        }
    }    
    
    private void simulate(final Map<String, Component> components, final Playfield playfield, 
            final Instruction instruction, final int originX, final int originY) {

        final int[] moves = instruction.getMoves();
        
        final String componentName = instruction.getComponentName();
        if (componentName != null) {
            final Component component = components.getOrDefault(componentName, null);
            if (component != null) {          
                simulate(components, playfield, component, originX + moves[0], originY - moves[1]);                
            }
            return;
        }
        
        final Tetromino tetromino = instruction.getTetromino();
        int x = originX + moves[0];
        int y = playfield.getMinY() - 3;
        
        for (int i = 1; i < moves.length; ++i) {
            if ((i & 1) == 1) {
                y = moveDown(playfield, tetromino, x, y, originY - moves[i]);
            } else {
                x = moveHorizontally(playfield, tetromino, x, y, originX + moves[i]);
            }
        }
        
        y = hardDrop(playfield, tetromino, x, y);
        lock(playfield, tetromino, x, y);
    }    
    
    private void loadComponents(final File directory, final Map<String, Component> components) 
            throws IOException, LexerParserException, ScriptException {
        
        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                loadComponents(file, components);
                continue;
            }
            final String tsFilename = file.getName();
            if (!tsFilename.endsWith(".t")) {
                continue;
            }            
            System.out.println(tsFilename);
            new LexerParser().parse(components, file);
            final String componentName = tsFilename.substring(0, tsFilename.length() - 2);
            final Component component = components.get(componentName);
            final File jsFile = new File(file.getParent() + File.separator + componentName + ".js");
            if (!(jsFile.isFile() && jsFile.exists())) {
                System.out.format("WARNING: %s missing JavaScript file.", componentName);
                continue;
            }
            loadJavaScript(component, jsFile);
        }
    } 
    
    private void loadJavaScript(final Component component, final File jsFile) throws ScriptException, IOException {        
        try (final BufferedReader br = new BufferedReader(new FileReader(jsFile))) {
            component.setCompiledScript(((Compilable) scriptEngine).compile(br));
        }
    }
    
    private void returnBindings(final Bindings bindings) {
        bindings.clear();
        bindingsPool.add(bindings);
    }
    
    private Bindings borrowBindings() {
        Bindings bindings = null;
        synchronized(bindingsPool) {
            if (!bindingsPool.isEmpty()) {
                bindings = bindingsPool.remove(bindingsPool.size() - 1);
            }
        }
        if (bindings == null) {
            bindings = scriptEngine.createBindings();          
        }
        return bindings;
    }    
    
    public static void main(final String... args) throws Exception {
        new Tester().launch();
    }
}