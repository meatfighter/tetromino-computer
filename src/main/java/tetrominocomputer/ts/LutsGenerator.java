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
import tetrominocomputer.sim.Point;
import tetrominocomputer.sim.Terminal;
import tetrominocomputer.sim.TerminalType;
import tetrominocomputer.sim.Tetromino;
import tetrominocomputer.util.Dirs;

public class LutsGenerator {
    
    public static final Pattern UPPERCASE_PATTERN = Pattern.compile("^[A-Z0-9_]+$");
    
    public void launch() throws Exception {                  
        saveLuts(computeLuts());
    }
    
    private void saveLuts(final Map<String, ComponentLut> luts) throws Exception {        
        for (final Map.Entry<String, ComponentLut> entry : luts.entrySet()) {
            final String name = entry.getKey();
            if (UPPERCASE_PATTERN.matcher(name).matches()) {
                saveLut(name, entry.getValue());
            }
        }
    }
    
    private void saveLut(final String name, final ComponentLut componentLut) throws Exception {
        try (final OutputStream out = new BufferedOutputStream(new FileOutputStream(
                String.format("%s%s.lut", Dirs.LUTS, name)))) {
            new ByteLut(componentLut).write(out);
        }
    }
    
    private Map<String, ComponentLut> computeLuts() throws Exception {
        
        final Map<String, Component> components = new ConcurrentHashMap<>();
        final Map<String, ComponentLut> luts = new ConcurrentHashMap<>();
        createIsSsAndZs(components);
        loadComponents(new File(Dirs.TS), components);
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
                            e.printStackTrace();
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
    
    private void returnPlayfield(final List<Playfield> playfieldPool, final Playfield playfield) {
        if (playfield == null) {
            return;
        }
        playfield.clear();
        playfieldPool.add(playfield);
    }
    
    private Playfield borrowPlayfield(final List<Playfield> playfieldPool) {
        Playfield playfield = null;
        synchronized (playfieldPool) {
            if (!playfieldPool.isEmpty()) {
                playfield = playfieldPool.remove(playfieldPool.size() - 1);
            }
        }
        if (playfield == null) {
            playfield = new Playfield(8192, 4096, 1);
        }
        return playfield;
    }    
    
    private void generateComponentLut(final Map<String, Component> components, 
            final Map<String, ComponentLut> luts, final Playfield playfield, final Component component) {
        
        final ComponentLutType componentLutType = solveComponentLutType(components, luts, playfield, 
                component);
        if (componentLutType == null) {
            return;
        }        
        
        System.out.println(component.getName());
        
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
    
    private void setInputs(final Playfield playfield, final Component component, final int inputBits) {
        setInputs(playfield, component, inputBits, playfield.getWidth() >> 1, playfield.getHeight() - 1, 
                playfield.getMaxValue());
    }
    
    private void setInputs(final Playfield playfield, final Component component, final int inputBits, 
            final int originX, final int originY, final int cellValue) {
        
        final Terminal[] inputs = component.getInputs();                      
        for (int i = inputs.length - 1, inBits = inputBits; i >= 0; --i, inBits >>= 1) {
            final boolean one = (inBits & 1) != 0;
            final HorizontalLine[] horizontalLines = inputs[i].getHorizontalLines();
            for (int j = horizontalLines.length - 1; j >= 0; --j) {
                final HorizontalLine horizontalLine = horizontalLines[j];
                final int maxX = horizontalLine.getMaxX();                
                int y = originY - horizontalLine.getY();
                if (one) {
                    --y;
                }
                for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {
                    playfield.set(originX + x, y, cellValue); 
                }
            }
        }
    } 
    
    private int getOutputs(final Playfield playfield, final Component component) {
        return getOutputs(playfield, component, playfield.getWidth() >> 1, playfield.getHeight() - 1);
    }
    
    private int getOutputs(final Playfield playfield, final Component component, final int originX, final int originY) {
        
        int out = 0;
        
        final Terminal[] outputs = component.getOutputs();        
        if (outputs == null) {
            return out;
        }
                
        outer: for (int i = 0; i < outputs.length; ++i) {
            out <<= 1;
            final HorizontalLine[] horizontalLines = outputs[i].getHorizontalLines();
            for (int j = horizontalLines.length - 1; j >= 0; --j) {
                final HorizontalLine horizontalLine = horizontalLines[j];
                final int maxX = horizontalLine.getMaxX();                
                int y = originY - horizontalLine.getY() - 1;                
                for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {                    
                    if (playfield.isSolid(originX + x, y)) {
                        out |= 1;
                        continue outer;
                    }
                }
            }
        }
        
        return out;
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
    
    private int moveHorizontally(final Playfield playfield, final Tetromino tetromino, final int startX, 
            final int startY, final int targetX) {        
        
        if (targetX < startX) {
            return moveLeft(playfield, tetromino, startX, startY, targetX);
        } 
        
        return moveRight(playfield, tetromino, startX, startY, targetX);
    }

    private int moveLeft(final Playfield playfield, final Tetromino tetromino, final int startX,
            final int startY, final int targetX) {

        final Point[] leftBlocks = tetromino.getLeftBlocks();
        for (int x = startX; x >= targetX; --x) {
            for (int i = leftBlocks.length - 1; i >= 0; --i) {
                final Point block = leftBlocks[i];
                if (playfield.get(x + block.x, startY + block.y) != 0) {
                    return x + 1;
                }
            }
        }
        
        return targetX;
    }

    private int moveRight(final Playfield playfield, final Tetromino tetromino, final int startX,
            final int startY, final int targetX) {

        final Point[] rightBlocks = tetromino.getRightBlocks();
        for (int x = startX; x <= targetX; ++x) {
            for (int i = rightBlocks.length - 1; i >= 0; --i) {
                final Point block = rightBlocks[i];
                if (playfield.get(x + block.x, startY + block.y) != 0) {
                    return x - 1;
                }
            }
        }
        
        return targetX;
    }
    
    private int moveDown(final Playfield playfield, final Tetromino tetromino, final int startX, final int startY,
            final int targetY) {
        
        final Point[] bottomBlocks = tetromino.getBottomBlocks();
        for (int y = startY; y <= targetY; ++y) {
            for (int i = bottomBlocks.length - 1; i >= 0; --i) {
                final Point block = bottomBlocks[i];
                if (playfield.get(startX + block.x, y + block.y) != 0) {
                    return y - 1;
                }
            }
        }
        
        return targetY;
    }
    
    private int hardDrop(final Playfield playfield, final Tetromino tetromino, final int startX, final int startY) {
        
        final Point[] bottomBlocks = tetromino.getBottomBlocks();
        for (int y = startY; y < playfield.getHeight(); ++y) {
            for (int i = bottomBlocks.length - 1; i >= 0; --i) {
                final Point block = bottomBlocks[i];
                if (playfield.get(startX + block.x, y + block.y) != 0) {
                    return y - 1;
                }
            }
        }
        
        return playfield.getHeight() - 1;
    }
    
    private void lock(final Playfield playfield, final Tetromino tetromino, final int lockX, final int lockY) {
        
        int value = tetromino.getGroupIndex() + 1;
        if (value > playfield.getMaxValue()) {
            value = playfield.getMaxValue();
        }
        
        final Point[] blocks = tetromino.getBlocks();
        for (int i = blocks.length - 1; i >= 0; --i) {
            final Point block = blocks[i];
            playfield.set(lockX + block.x, lockY + block.y, value);
        }
    }    
    
    private void addInstruction(final List<Instruction> instructions, final Tetromino tetromino, final int x) {
        instructions.add(new Instruction(tetromino, null, null, new int[] { x }));
    }
    
    private void setInputs(final Component component, final int x, final int y) {
        component.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                        new HorizontalLine[] { new HorizontalLine(x, y) })});
    }
    
    private void setInputs(final Component component, final int minX, final int maxX, final int y) {
        component.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                        new HorizontalLine[] { new HorizontalLine(minX, maxX, y) })});
    }
    
    private void setOutputs(final Component component, final int x, final int y) {
        component.setOutputs(new Terminal[] { new Terminal(TerminalType.OUTPUT, "o", 
                        new HorizontalLine[] { new HorizontalLine(x, y) })});
    }    
    
    private void createIsSsAndZs(final Map<String, Component> components) {
        for (int i = 3; i <= 2001; ++i) {
            createIls(components, i);
            createIrs(components, i);
        }
        for (int i = 3; i <= 2001; i += 2) {
            createSs(components, i);
            createZs(components, i);
        }
    }
    
    private void createIls(final Map<String, Component> components, final int length) {                
        final Component is = new Component(String.format("il%d", length));
        final List<Instruction> instructions = new ArrayList<>();
        final int remainder = length % 4;
        switch(length % 4) {
            case 1:
                addInstruction(instructions, Tetromino.JL, 1);
                break;
            case 2:
                addInstruction(instructions, Tetromino.OS, 1);
                break;
            case 3:
                addInstruction(instructions, Tetromino.LR, 0);
                break;
        }
        if (remainder == 0) {
            setInputs(is, 0, 0);
        } else {
            setInputs(is, 0, 1, 0);
        }
        for (int i = length >> 2; i > 0; --i) {
            addInstruction(instructions, Tetromino.IV, 0);
        }
        is.setInstructions(instructions.toArray(new Instruction[instructions.size()]));
        setOutputs(is, 0, length); 
        components.put(is.getName(), is);        
    }  
    
    private void createIrs(final Map<String, Component> components, final int length) {                
        final Component is = new Component(String.format("ir%d", length));
        final List<Instruction> instructions = new ArrayList<>();
        final int remainder = length % 4;
        switch(length % 4) {
            case 1:
                addInstruction(instructions, Tetromino.LR, -1);
                break;
            case 2:
                addInstruction(instructions, Tetromino.OS, 0);
                break;
            case 3:
                addInstruction(instructions, Tetromino.JL, 0);
                break;
        }
        if (remainder == 0) {
            setInputs(is, 0, 0);
        } else {
            setInputs(is, -1, 0, 0);
        }
        for (int i = length >> 2; i > 0; --i) {
            addInstruction(instructions, Tetromino.IV, 0);
        }
        is.setInstructions(instructions.toArray(new Instruction[instructions.size()]));
        setOutputs(is, 0, length); 
        components.put(is.getName(), is);        
    }    
    
    private void createSs(final Map<String, Component> components, final int length) {
        final Component ss = new Component(String.format("s%d", length));
        final List<Instruction> instructions = new ArrayList<>();
        for (int i = ((length - 1) >> 1) - 1, x = -1; i >= 0; --i, x -= 2) {
            instructions.add(new Instruction(Tetromino.SH, null, null, new int[] { x }));
        }
        ss.setInstructions(instructions.toArray(new Instruction[instructions.size()]));
        ss.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                new HorizontalLine[] { new HorizontalLine(-2, -1, 0), new HorizontalLine(0, 1) })});
        ss.setOutputs(new Terminal[] { new Terminal(TerminalType.OUTPUT, "o", 
                new HorizontalLine[] { new HorizontalLine(1 - length, 1), new HorizontalLine(2 - length, 0, 2) })});
        components.put(ss.getName(), ss);
    }    
    
    private void createZs(final Map<String, Component> components, final int length) {
        final Component zs = new Component(String.format("z%d", length));
        final List<Instruction> instructions = new ArrayList<>();
        for (int i = ((length - 1) >> 1) - 1, x = 1; i >= 0; --i, x += 2) {
            instructions.add(new Instruction(Tetromino.ZH, null, null, new int[] { x }));
        }
        zs.setInstructions(instructions.toArray(new Instruction[instructions.size()]));
        zs.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                new HorizontalLine[] { new HorizontalLine(0, 1), new HorizontalLine(1, 2, 0) })});
        zs.setOutputs(new Terminal[] { new Terminal(TerminalType.OUTPUT, "o", 
                new HorizontalLine[] { new HorizontalLine(0, length - 2, 2), new HorizontalLine(length - 1, 1) })});
        components.put(zs.getName(), zs);
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
        new LutsGenerator().launch();
    }
}