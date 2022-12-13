package tetrominocomputer.ts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
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
    
    private static final double DEFAULT_FRACTION = 0.05;
    
    private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
    private final List<Bindings> bindingsPool = Collections.synchronizedList(new ArrayList<>());
    
    public void launch(final double frac) throws Exception {                  
        Out.timeTask("Loading components...", () -> {
            final Map<String, Component> components = new ConcurrentHashMap<>();
            createIsSsAndZs(components);
            loadComponents(new File(Dirs.TS), components);
            
            System.out.println("Loaded components.");
            System.out.println();
            
            testComponents(components, frac); 
            
            System.out.println();
            
            return null;
        });
    }
        
    private void testComponents(final Map<String, Component> components, final double frac) throws Exception {
        
        System.out.println("Testing components...");
        System.out.println();        
                
        final ExecutorService executor = Executors.newWorkStealingPool();
        final List<Playfield> playfieldPool = Collections.synchronizedList(new ArrayList<>());

        components.forEach((componentName, component) -> {
            executor.execute(() -> {
                final Playfield playfield = borrowPlayfield(playfieldPool);
                try {
                    testComponent(components, component, playfield, frac);
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
            final Playfield playfield, final double frac) throws ScriptException {
        
        if (component.getCompiledScript() == null) {
            return;
        }
        
        final double expect = 1.0 / frac;
        final Random random = ThreadLocalRandom.current();
        if (isTwoBytesBit(components, playfield, component)) {
            for (double i = 0x1FFFF; i >= 0; i -= expect) {
                final int inputBits = Math.max(0, (int) (i - expect * random.nextDouble()));
                if (!testComponent(components, component, playfield, ((inputBits & 0x1FFFE) << 7) | (inputBits & 1))) {
                    return;
                }
            }
        } else {
            final int max = Math.min(0x1FFFF, (1 << component.getInputs().length) - 1);
            if (max > 0x7FFF) {
                for (double i = max; i >= 0; i -= expect) {
                    if (!testComponent(components, component, playfield, 
                            Math.max(0, (int) (i - expect * random.nextDouble())))) {
                        return;
                    }
                }
            } else {
                for (int i = max; i >= 0; --i) {
                    if (!testComponent(components, component, playfield, i)) {
                        return;
                    }
                }
            }
        }
        
        System.out.format("PASSED: %s%n", component.getName());
    }
    
    private boolean testComponent(final Map<String, Component> components, final Component component, 
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
                final Object value = bindings.get(outputs[i].getName());                
                if (value == null) {
                    System.err.format("FAILED: %s -- Output node %s is not assigned a value.", component.getName(), 
                            outputs[i].getName());
                    return false;
                } else if (value instanceof Boolean) {
                    outBits = (outBits << 1) | ((Boolean) value ? 1 : 0);
                } else {
                    System.err.format("FAILED: %s -- Output node %s is %s rather than Boolean.",
                            component.getName(), outputs[i].getName(), value.getClass().getName());
                    return false;
                }                
            }
        } finally {
            returnBindings(bindings);
        }
        
        if (outputBits != outBits) {
            System.err.format("FAILED: %s -- input = %s, output = %s, expected output = %s%n", component.getName(),
                    Out.toBinaryString(inputBits, inputs.length),
                    Out.toBinaryString(outputBits, outputs.length),
                    Out.toBinaryString(outBits, outputs.length));
            return false;
        }
        
        return true;
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
    
    private boolean isTwoBytesBit(final Map<String, Component> components, 
            final Playfield playfield, final Component component) {
        
        final int inputsCount = component.getInputs().length;
        if (inputsCount != 24) {
            return false;
        }
        
        final Random random = ThreadLocalRandom.current();
        while (true) {
            playfield.clear();
            final int inputs = random.nextInt() & 0xFFFFFF;
            setInputs(playfield, component, inputs);
            simulate(components, playfield, component);
            final int outputs = getOutputs(playfield, component);
            final int high = 0xFF & (outputs >> 16);
            final int low = 0xFF & outputs;
            if (high > 1 && low > 1) {
                throw new RuntimeException(String.format("Invalid output: name=%s, inputs=%X, outputs=%X%n", 
                        component.getName(), inputs, outputs));
            }
            if (low > 1) {
                return false;
            }
            if (high > 1) {
                return true;
            }
        }
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
            new LexerParser().parse(components, file);
            final String componentName = tsFilename.substring(0, tsFilename.length() - 2);
            final Component component = components.get(componentName);
            final File jsFile = new File(file.getParent() + File.separator + componentName + ".js");
            if (!(jsFile.isFile() && jsFile.exists())) {
                System.out.format("WARNING: %s missing JavaScript file.%n", componentName);
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
        
        double frac = DEFAULT_FRACTION;
        for (int i = 0; i < args.length; ++i) {
            if ("-f".equals(args[i]) && i != args.length - 1) {
                try {
                    frac = Double.parseDouble(args[i + 1]);
                } catch (final NumberFormatException e) {
                    System.err.println("Invalid fraction.");
                    System.err.println();
                    return;
                }
            }
        }
        
        new Tester().launch(frac);
    }
}