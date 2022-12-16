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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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
    
    private static final double DEFAULT_ALL_FRACTION = 0.1;
    private static final double DEFAULT_SINGLE_FRACTION = 1.0;
    
    private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
    private final List<Bindings> bindingsPool = Collections.synchronizedList(new ArrayList<>());
    
    public void launch(final double frac, final String componentName, final File tsDir) throws Exception {
        
        Out.timeTask("Loading components...", () -> {
            final Map<String, Component> components = new ConcurrentHashMap<>();
            createIsSsAndZs(components);
            final int skipped = loadComponents(tsDir, components);
            if (skipped > 0) {
                Out.println();
            }            
            Out.format("Loaded components.%n%n");
            
            if (componentName != null) {
                if (!components.containsKey(componentName)) {
                    Out.formatError("Component not found: %s%n%n", componentName);
                    return null;
                }
                testComponent(components, componentName, frac);
            } else {            
                testComponents(components, frac, skipped); 
            }
            
            Out.println();
            
            return null;
        });
    }
        
    private void testComponents(final Map<String, Component> components, final double frac, final int skipped) {
        
        Out.format("Testing components...%n%n");
                
        final ExecutorService executor = Executors.newWorkStealingPool();
        final List<Playfield> playfieldPool = Collections.synchronizedList(new ArrayList<>());

        final AtomicInteger passed = new AtomicInteger();
        final AtomicInteger failed = new AtomicInteger();
        components.values().forEach(component -> {
            executor.execute(() -> {
                final Playfield playfield = borrowPlayfield(playfieldPool);
                try {
                    final Boolean result = testComponent(components, component, playfield, frac, new AtomicBoolean());
                    if (result != null) {
                        if (result) {
                            passed.incrementAndGet();
                        } else {
                            failed.incrementAndGet();
                        }
                    }
                } catch (final Exception e) {
                    Out.printStackTrace(e);
                } finally {
                    returnPlayfield(playfieldPool, playfield);
                }
            });
        });
     
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (final InterruptedException ignored) {            
        }
        
        Out.format("%nTests: %d, Passed: %d, Failed: %d, Skipped: %d%n", passed.get() + failed.get(), passed.get(), 
                failed.get(), skipped);
    }
    
    private void testComponent(final Map<String, Component> components, final String componentName, final double frac) {
        
        Out.format("Testing %s...%n%n", componentName);
                
        final Component component = components.get(componentName);
        final ExecutorService executor = Executors.newWorkStealingPool();
        final List<Playfield> playfieldPool = Collections.synchronizedList(new ArrayList<>());
        
        final boolean twoBytesBit;
        {
            final Playfield playfield = borrowPlayfield(playfieldPool);
            try {
                twoBytesBit = isTwoBytesBit(components, playfield, component);
            } finally {
                returnPlayfield(playfieldPool, playfield);
            }
        }
                
        final AtomicBoolean cancelled = new AtomicBoolean();
        final int max = Math.min(0x1FFFF, (1 << component.getInputs().length) - 1);
        final int range = 1 + (max / Runtime.getRuntime().availableProcessors());
        for (int high = max; high >= 0 && !cancelled.get(); high -= range) {
            final int h = high;
            final int low = Math.max(0, high - range);
            executor.execute(() -> {
                final Playfield playfield = borrowPlayfield(playfieldPool);
                try {
                    if (!testComponent(components, component, playfield, h, low, (range > 0x7FFF) ? frac : 1.0, 
                            twoBytesBit, cancelled)) {
                        cancelled.set(true);
                    }                    
                } catch (final Exception e) {
                    Out.printStackTrace(e);    
                } finally {
                    returnPlayfield(playfieldPool, playfield);
                }
            });
        }        
      
        executor.shutdown();        
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (final InterruptedException ignored) {
        }
        
        if (!cancelled.get()) {
            Out.format("PASSED: %s%n%n", component.getName());
            Out.format("Tests: 1, Passed: 1, Failed: 0, Skipped: 0%n");
        } else {
            Out.format("%nTests: 1, Passed: 0, Failed: 1, Skipped: 0%n");
        }
    }    
    
    private Boolean testComponent(final Map<String, Component> components, final Component component, 
            final Playfield playfield, final double frac, final AtomicBoolean cancelled) {
        
        if (component.getCompiledScript() == null) {
            return null;
        }
        
        final boolean twoBytesBit = isTwoBytesBit(components, playfield, component);
        final int max = Math.min(0x1FFFF, (1 << component.getInputs().length) - 1);
        if (testComponent(components, component, playfield, max, 0, (max > 0x7FFF) ? frac : 1.0, twoBytesBit, 
                cancelled)) {
            Out.format("PASSED: %s%n", component.getName());
            return Boolean.TRUE;
        }
        
        return Boolean.FALSE;
    }
    
    private boolean testComponent(final Map<String, Component> components, final Component component, 
            final Playfield playfield, final double max, final double min, final double frac, 
            final boolean twoBytesBit, final AtomicBoolean cancelled) {
        
        final double expect = 1.0 / frac;
        final Random random = ThreadLocalRandom.current();
        for (double i = max; i >= min && !cancelled.get(); i -= expect) {
            final int inputBits = Math.max(0, (int) (i - ((frac < 1.0) ? expect * random.nextDouble() : 0)));
            if (!testComponent(components, component, playfield, 
                    twoBytesBit ? ((inputBits & 0x1FFFE) << 7) | (inputBits & 1) : inputBits)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean testComponent(final Map<String, Component> components, final Component component, 
            final Playfield playfield, final int inputBits) {
        
        final CompiledScript compiledScript = component.getCompiledScript();
        if (compiledScript == null) {
            Out.formatError("FAILED: %s -- No JavaScript.%n", component.getName());
            return false;
        }

        playfield.clear();
        setInputs(playfield, component, inputBits);
        simulate(components, playfield, component);
        final int outputBits = getOutputs(playfield, component);
                
        int outBits = 0;

        final Terminal[] inputs = component.getInputs();
        final Terminal[] outputs = component.getOutputs();
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
                    Out.formatError("FAILED: %s -- Output node %s is not assigned a value.", component.getName(), 
                            outputs[i].getName());
                    return false;
                } else if (value instanceof Boolean) {
                    outBits = (outBits << 1) | ((Boolean) value ? 1 : 0);
                } else {
                    Out.formatError("FAILED: %s -- Output node %s is %s rather than Boolean.",
                            component.getName(), outputs[i].getName(), value.getClass().getName());
                    return false;
                }                
            }
        } catch (final ScriptException e) {
            Out.formatError("FAILED: %s -- JavaScript error: %s", component.getName(), e.getMessage());
            return false;
        } finally {
            returnBindings(bindings);
        }
        
        if (outputBits != outBits) {
            Out.formatError("FAILED: %s -- input = %s, output = %s, expected output = %s%n", component.getName(),
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
    
    private boolean isTwoBytesBit(final Map<String, Component> components, final Playfield playfield, 
            final Component component) {
        
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
    
    private int loadComponents(final File directory, final Map<String, Component> components) 
            throws IOException, LexerParserException, ScriptException {
        
        int skipped = 0;
        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                skipped += loadComponents(file, components);
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
            if (!(jsFile.isFile() && jsFile.exists() && jsFile.length() > 0)) {
                ++skipped;
                Out.format("WARNING: %s missing JavaScript file.%n", componentName);
                continue;
            }
            loadJavaScript(component, jsFile);
        }
        
        return skipped;
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
        
        double frac = -1.0;
        String componentName = null;
        String tsDirName = Dirs.TS;
        for (int i = 0; i < args.length - 1; ++i) {   
            switch (args[i]) {
                case "-f": {
                    boolean error = false;
                    try {
                        frac = Double.parseDouble(args[++i]);
                        error = frac <= 0 || Double.isInfinite(frac) || Double.isNaN(frac);
                    } catch (final NumberFormatException e) {
                        error = true;
                    }
                    if (error) {
                        Out.formatError("Invalid fraction.%n%n");
                        return;
                    }
                    break;
                }
                case "-n":
                    componentName = args[++i];
                    break;
                case "-t":
                    tsDirName = args[++i];
                    break;
            }
        }
        
        final File tsDir = Dirs.toFile(tsDirName);
        if (!(tsDir.exists() && tsDir.isDirectory())) {
            Out.formatError("Cannot find TS directory: %s%n%n", tsDir);
            return;
        }       
        
        if (frac <= 0) {
            frac = (componentName == null) ? DEFAULT_ALL_FRACTION : DEFAULT_SINGLE_FRACTION;
        }
        
        new Tester().launch(frac, componentName, tsDir);
    }
}