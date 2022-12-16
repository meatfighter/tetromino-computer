package tetrominocomputer.ts;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import tetrominocomputer.sim.Component;
import tetrominocomputer.sim.HorizontalLine;
import tetrominocomputer.sim.Instruction;
import tetrominocomputer.sim.Playfield;
import tetrominocomputer.sim.Terminal;
import tetrominocomputer.sim.Tetromino;
import tetrominocomputer.util.Dirs;
import tetrominocomputer.util.Out;

public class LutsGenerator extends AbstractSimulator {
    
    public static final Pattern UPPERCASE_PATTERN = Pattern.compile("^[A-Z0-9_]+$");
    
    public void launch(final File tsDir, final File lutsDir) throws Exception {                  
        Out.timeTask(String.format("Cleaning directory: %s", lutsDir), () -> {
            if (cleanLutsDirectory(lutsDir)) {
                saveLuts(lutsDir, generateLuts(tsDir));
            }
            return null;
        });
    }
    
    private boolean cleanLutsDirectory(final File lutsDir) {
        for (final File file : lutsDir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".lut") && !file.delete()) {
                Out.formatError("Failed to delete file: %s%n%n", file);
                return false;
            }
        }
        return true;
    }
    
    private void saveLuts(final File lutsDir, final Map<String, ComponentLut> luts) throws Exception {
        
        Out.format("%nSaving lookup tables...%n%n");
        
        for (final Map.Entry<String, ComponentLut> entry : luts.entrySet()) {
            final String name = entry.getKey();
            if (UPPERCASE_PATTERN.matcher(name).matches()) {
                saveLut(lutsDir, name, entry.getValue());
            }
        }
        
        Out.format("Saved lookup tables.%n%n");
    }
    
    private void saveLut(final File lutsDir, final String name, final ComponentLut componentLut) throws Exception {
        try (final OutputStream out = new BufferedOutputStream(new FileOutputStream(
                String.format("%s%s.lut", lutsDir, name)))) {
            new ByteLut(componentLut).write(out);
        }
    }
    
    private Map<String, ComponentLut> generateLuts(final File tsDir) throws Exception {
        
        Out.format("Generating lookup tables for:%n%n");
        
        final Map<String, Component> components = new ConcurrentHashMap<>();
        final Map<String, ComponentLut> luts = new ConcurrentHashMap<>();
        createIsSsAndZs(components);
        loadComponents(tsDir, components);
        final Map<String, Set<String>> dependencies = findDependencies(components);
        final Map<String, Set<String>> reverseDependencies = reverseDependencies(dependencies);
        final List<Playfield> playfieldPool = Collections.synchronizedList(new ArrayList<>());
        final List<String> removeList = new ArrayList<>();
        final ExecutorService executor = Executors.newWorkStealingPool();
        
        while (true) {
            
            boolean remaining = false;
            for (final Iterator<Map.Entry<String, Set<String>>> i = dependencies.entrySet().iterator(); i.hasNext(); ) {
                final Map.Entry<String, Set<String>> entry = i.next();
                final String name = entry.getKey();
                remaining = true;
                if (entry.getValue().isEmpty()) {
                    i.remove();
                    final Component component = components.get(name);
                    executor.execute(() -> {
                        Playfield playfield = null;
                        try {
                            playfield = borrowPlayfield(playfieldPool);
                            generateComponentLut(components, luts, playfield, component);
                        } catch (final Exception e) {
                            Out.printStackTrace(e);
                            Out.printlnError();
                            System.exit(0);
                        } finally {
                            returnPlayfield(playfieldPool, playfield);
                        }
                        synchronized (removeList) {
                            removeList.add(name);
                            removeList.notifyAll();
                        }
                    });
                }
            }
            
            if (!remaining) {
                break;
            }
            
            synchronized (removeList) {
                while (removeList.isEmpty()) {
                    try {
                        removeList.wait();
                    } catch (final InterruptedException e) {                        
                    }
                }
                
                removeList.forEach(remove -> {
                    final Set<String> names = reverseDependencies.get(remove);
                    if (names != null) {
                        names.forEach(name -> dependencies.get(name).remove(remove));
                    }
                });
                
                removeList.clear();
            }
        }
        
        executor.shutdown();        
        executor.awaitTermination(1, TimeUnit.DAYS);
        
        return luts;
    }
       
    private void generateComponentLut(final Map<String, Component> components, 
            final Map<String, ComponentLut> luts, final Playfield playfield, final Component component) {
        
        final ComponentLutType componentLutType = solveComponentLutType(components, luts, playfield, 
                component);
        if (componentLutType == null) {
            return;
        }        
        
        Out.println(component.getName());
        
        switch (componentLutType) {
            case BIT_TWO_BYTES:
                generateComponentLutBitTwoBytes(components, luts, playfield, component);
                break;
            case TWO_BYTES_BIT:
                generateComponentLutTwoBytesBit(components, luts, playfield, component);                
                break;
            default:
                generateComponentLutAny(components, luts, playfield, component);
                break;
        }
    }
    
    private void generateComponentLutAny(final Map<String, Component> components, 
            final Map<String, ComponentLut> luts, final Playfield playfield, final Component component) {
        
        final ComponentLut componentLut = new ComponentLut(component.getInputs().length);
        final int[] table = componentLut.getTable();
        for (int inputBits = table.length - 1; inputBits >= 0; --inputBits) {
            playfield.clear();
            setInputs(playfield, component, inputBits);
            simulate(components, luts, playfield, component);
            table[inputBits] = getOutputs(playfield, component);
        }
        luts.put(component.getName(), componentLut);
    }
    
    private void generateComponentLutBitTwoBytes(final Map<String, Component> components, 
            final Map<String, ComponentLut> luts, final Playfield playfield, final Component component) {
        
        final ComponentLut componentLut = new ComponentLut(ComponentLutType.BIT_TWO_BYTES);
        final int[] table = componentLut.getTable();
        for (int inputBits = table.length - 1; inputBits >= 0; --inputBits) {
            playfield.clear();
            setInputs(playfield, component, inputBits);
            simulate(components, luts, playfield, component);
            table[inputBits] = getOutputs(playfield, component);
        }
        luts.put(component.getName(), componentLut);
    }

    private void generateComponentLutTwoBytesBit(final Map<String, Component> components, 
            final Map<String, ComponentLut> luts, final Playfield playfield, final Component component) {
        
        final ComponentLut lut = new ComponentLut(ComponentLutType.TWO_BYTES_BIT);
        final int[] table = lut.getTable();
        for (int inputBits = table.length - 1; inputBits >= 0; --inputBits) {
            playfield.clear();
            setInputs(playfield, component, ((inputBits & 0x1FFFE) << 7) | (inputBits & 1));
            simulate(components, luts, playfield, component);
            table[inputBits] = getOutputs(playfield, component);
        }
        luts.put(component.getName(), lut);
    }    
    
    private ComponentLutType solveComponentLutType(final Map<String, Component> components, 
            final Map<String, ComponentLut> luts, final Playfield playfield, final Component component) {
        
        final int inputsCount = component.getInputs().length;
        if (inputsCount <= 20) {
            return ComponentLutType.ANY;
        }
        if (inputsCount != 24) {
            return null;
        }
        
        final Random random = ThreadLocalRandom.current();
        while (true) {
            playfield.clear();
            final int inputs = random.nextInt() & 0xFFFFFF;
            setInputs(playfield, component, inputs);
            simulate(components, luts, playfield, component);
            final int outputs = getOutputs(playfield, component);
            final int high = 0xFF & (outputs >> 16);
            final int low = 0xFF & outputs;
            if (high > 1 && low > 1) {
                throw new RuntimeException(String.format("Invalid output: name=%s, inputs=%X, outputs=%X%n", 
                        component.getName(), inputs, outputs));
            }
            if (low > 1) {
                return ComponentLutType.BIT_TWO_BYTES;
            }
            if (high > 1) {
                return ComponentLutType.TWO_BYTES_BIT;
            }
        }
    }
    
    private Map<String, Set<String>> findDependencies(final Map<String, Component> components) {        
        final Map<String, Set<String>> depends = new HashMap<>();
        components.values().forEach(component -> {
            findDependencies(components, depends, component.getName());
        });
        return depends;
    }
    
    private Map<String, Set<String>> reverseDependencies(final Map<String, Set<String>> depends) {
        final Map<String, Set<String>> dependencies = new HashMap<>();
        depends.entrySet().forEach(entry -> {
            final String key = entry.getKey();
            entry.getValue().forEach(value -> {
                dependencies.computeIfAbsent(value, v -> new HashSet<>()).add(key);
            });
        });
        return dependencies;
    }
    
    private Set<String> findDependencies(final Map<String, Component> components, 
            final Map<String, Set<String>> depends, final String componentName) {
        
        Set<String> ds = depends.get(componentName);
        if (ds != null) {
            return ds;
        }
        
        final Component component = components.getOrDefault(componentName, null);
        if (component == null) {
            return null;
        }
        
        ds = new HashSet<>();   
        depends.put(componentName, ds);
        
        final Instruction[] instructions = component.getInstructions();
        if (instructions == null) {
            return ds;
        }
        for (final Instruction instruction : instructions) {
            if (instruction.getTetromino() != null) {
                continue;
            }
            final String compName = instruction.getComponentName();
            if (compName == null || components.getOrDefault(compName, null) == null) {
                continue;
            }
            ds.add(compName);
            final Set<String> set = findDependencies(components, depends, compName);
            if (set != null) {
                ds.addAll(set);
            }            
        }
        
        return ds;
    }
          
    private void simulate(final Map<String, Component> components, final Map<String, ComponentLut> luts, 
            final Playfield playfield, final Component component) {
        simulate(components, luts, playfield, component, playfield.getWidth() >> 1, playfield.getHeight() - 1);
    }

    private void simulate(final Map<String, Component> components, final Map<String, ComponentLut> luts, 
            final Playfield playfield, final Component component, final int originX, final int originY) {
        
        if (!component.getName().startsWith("_") && luts.containsKey(component.getName())) {
            emulate(luts, playfield, component, originX, originY);
            return;
        } 
        
        final Instruction[] instructions = component.getInstructions();
        if (instructions == null) {
            return;
        }
        
        for (int i = 0; i < instructions.length; ++i) {
            simulate(components, luts, playfield, instructions[i], originX, originY);
        }
    } 
    
    private void simulate(final Map<String, Component> components, final Map<String, ComponentLut> luts, 
            final Playfield playfield, final Instruction instruction, final int originX, final int originY) {

        final int[] moves = instruction.getMoves();
        
        final String componentName = instruction.getComponentName();
        if (componentName != null) {
            final Component component = components.getOrDefault(componentName, null);
            if (component != null) {          
                simulate(components, luts, playfield, component, originX + moves[0], originY - moves[1]);                
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
    
    private void emulate(final Map<String, ComponentLut> luts, final Playfield playfield, 
            final Component component, final int originX, final int originY) {

        final ComponentLut lut = luts.get(component.getName());
        if (lut == null) {
            return;
        }
        
        final Terminal[] inputs = component.getInputs();        
        if (inputs == null) {
            return;
        }
        
        int input = 0;
        outer: for (int i = inputs.length - 1; i >= 0; --i) {
            final HorizontalLine[] horizontalLines = inputs[i].getHorizontalLines();
            for (int j = horizontalLines.length - 1; j >= 0; --j) {
                final HorizontalLine horizontalLine = horizontalLines[j];
                final int maxX = horizontalLine.getMaxX();                
                int y = originY - horizontalLine.getY() - 1;                
                for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {
                    if (playfield.isSolid(originX + x, y)) {
                        input = lut.setInputBit(input, inputs.length - 1 - i);
                        continue outer;
                    }
                }
            }
        }
        
        final int output = lut.getTable()[input];
        
        final Terminal[] outputs = component.getOutputs();
        if (outputs == null) {
            return;
        }
        
        for (int i = outputs.length - 1; i >= 0; --i) {
            final int color = playfield.getMaxValue();
            final HorizontalLine[] horizontalLines = outputs[i].getHorizontalLines();
            for (int j = horizontalLines.length - 1; j >= 0; --j) {
                final HorizontalLine horizontalLine = horizontalLines[j];
                final int maxX = horizontalLine.getMaxX();
                int y = originY - horizontalLine.getY();                
                if (((output >> (outputs.length - 1 - i)) & 1) != 0) {
                    --y;
                }
                for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {
                    playfield.set(originX + x, y, color);
                }
            }
        }
    }    
        
    private void loadComponents(final File directory, final Map<String, Component> components) 
            throws IOException, LexerParserException {
        
        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                loadComponents(file, components);
                continue;
            }
            if (!file.getName().endsWith(".t")) {
                continue;
            }
            new LexerParser().parse(components, file);
        }
    }    
    
    public static void main(final String... args) throws Exception {
        
        String tsDirName = Dirs.TS;
        String lutsDirName = Dirs.LUTS;
        for (int i = 0; i < args.length - 1; ++i) {            
            switch (args[i]) {
                case "-t":
                    tsDirName = args[++i];
                    break;
                case "-l":
                    lutsDirName = args[++i];
                    break;
            }
        }
        
        final File tsDir = new File(tsDirName);
        if (!(tsDir.exists() && tsDir.isDirectory())) {
            Out.formatError("Cannot find TS directory: %s%n%n", tsDir);
            return;
        }
        
        final File lutsDir = new File(lutsDirName);
        if (!(lutsDir.exists() && lutsDir.isDirectory())) {
            Out.formatError("Cannot find lookup tables directory: %s%n%n", lutsDir);
            return;
        }
        
        new LutsGenerator().launch(tsDir, lutsDir);
    }
}