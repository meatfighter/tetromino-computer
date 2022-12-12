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
import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import tetrominocomputer.sim.Component;
import tetrominocomputer.sim.Instruction;
import tetrominocomputer.sim.Playfield;
import tetrominocomputer.sim.Tetromino;
import tetrominocomputer.util.Dirs;
import tetrominocomputer.util.Out;

public class Tester extends AbstractSimulator {
    
    private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
    
    public void launch() throws Exception {                  
        Out.timeTask("Loading components...", () -> {
            final Map<String, Component> components = new ConcurrentHashMap<>();
            createIsSsAndZs(components);
            loadComponents(new File(Dirs.TS), components);
            
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
                } finally {
                    returnPlayfield(playfieldPool, playfield);
                }
            });
        });
     
        executor.shutdown();        
        executor.awaitTermination(1, TimeUnit.DAYS);
    }
    
    private void testComponent(final Map<String, Component> components, final Component component, 
            final Playfield playfield) {
        
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
    
    public static void main(final String... args) throws Exception {
        new Tester().launch();
    }
}