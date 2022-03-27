package tetriscircuits.computer.mapping;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import tetriscircuits.Component;
import tetriscircuits.HorizontalLine;
import tetriscircuits.Instruction;
import tetriscircuits.Playfield;
import tetriscircuits.Point;
import tetriscircuits.Terminal;
import tetriscircuits.TerminalType;
import tetriscircuits.Tetrimino;
import tetriscircuits.parser.ParseException;
import tetriscircuits.parser.Parser;

public class MapMaker {
    
    public static final String WORKSPACE_DIR = "workspace";
    
    public static final Pattern UPPERCASE_PATTERN = Pattern.compile("^[A-Z0-9_]+$");
    
    public void launch() throws Exception {                  
        saveMaps(computeMappings());
        compareMaps();
        findMissingMaps();
    }
    
    private void findMissingMaps() throws Exception {
        for (final File file : new File("maps").listFiles()) {
            if (!(file.isFile() && file.getName().endsWith(".map"))) {
                continue;
            }
            if (!new File("maps2/" + file.getName()).exists()) {
                System.out.format("Missing: %s%n", file.getName());
            }
        }
    }
    
    private void compareMaps() throws Exception {
        for (final File fileA : new File("maps2").listFiles()) {
            if (!(fileA.isFile() && fileA.getName().endsWith(".map"))) {
                continue;
            }
            final File fileB = new File("maps/" + fileA.getName());
            if (!fileB.exists()) {
                continue;
            }            
            final ByteMapping mappingA = loadByteMapping(fileA);
            final ByteMapping mappingB = loadByteMapping(fileB);
            if (!mappingA.equals(mappingB)) {
                System.out.format("Mismatch: %s%n", fileA);
            }
        }
    }
    
    private void saveMaps(final Map<String, ComponentMapping> mappings) throws Exception {        
        for (final Map.Entry<String, ComponentMapping> entry : mappings.entrySet()) {
            final String name = entry.getKey();
            if (UPPERCASE_PATTERN.matcher(name).matches()) {
                saveMap(name, entry.getValue());
            }
        }
    }
    
    private ByteMapping loadByteMapping(final File file) throws Exception {
        try (final InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            return ByteMapping.read(in);
        }
    }
    
    private void saveMap(final String name, final ComponentMapping componentMapping) throws Exception {
        try (final OutputStream out = new BufferedOutputStream(new FileOutputStream(
                String.format("maps2/%s.map", name)))) {
            new ByteMapping(componentMapping).write(out);
        }
    }
    
    private Map<String, ComponentMapping> computeMappings() throws Exception {
        
        final Map<String, Component> components = new ConcurrentHashMap<>();
        final Map<String, ComponentMapping> mappings = new ConcurrentHashMap<>();
        createIsSsAndZs(components);
        loadComponents(new File(WORKSPACE_DIR), components);
        final Map<String, Set<String>> dependencies = findDependencies(components);
        final Map<String, Set<String>> reverseDependencies = reverseDependencies(dependencies);
        final List<Playfield> playfieldPool = Collections.synchronizedList(new ArrayList<>());
        final List<String> removeList = new ArrayList<>();
        final ExecutorService executor = Executors.newWorkStealingPool();
        
        /*outer:*/ while (true) {
            
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
                            generateComponentMapping(components, mappings, playfield, component);
                        } catch (final Exception e) {
                            e.printStackTrace();
                        } finally {
                            returnPlayfield(playfieldPool, playfield);
                        }
                        synchronized (removeList) {
                            removeList.add(name);
                            removeList.notifyAll();
                        }
                    });
                    
//                    if ("C_CMP".equals(name)) {
//                        break outer; // TODO TESTING
//                    }
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
                
                for (final String remove : removeList) {
                    final Set<String> names = reverseDependencies.get(remove);
                    if (names == null) {
                        continue;
                    }
                    for (final String name : names) {
                        dependencies.get(name).remove(remove);
                    }
                }
                
                removeList.clear();
            }
        }
        
        executor.shutdown();        
        executor.awaitTermination(1, TimeUnit.DAYS);
        
//        final ComponentMapping mapping = mappings.get("C_CMP");
//        System.out.println(mapping.getMappingType());
//        final int[] map = mapping.getMap();
//        for (int c = 0; c <= 1; ++c) {
//            for (int a = 0; a <= 0xFF; ++a) {
//                for (int b = 0; b <= 0xFF; ++b) {
//                    final int i = (c << 16) | (a << 8) | b;
//                    final int v = map[i];
//                    final int C = 1 & (v >> 16);
//                    final int A = 0xFF & (v >> 8);
//                    final int B = 0xFF & v;
//                    if (C != ((a == b) ? 1 : 0) || a != A || b != B) {
//                        System.out.format("a = %02X, b = %02X, c = %X, A = %02X, B = %02X, C = %X%n", a, b, c, A, B, C);
//                    }
//                }
//            }
//        }
//        for (int i = 0; i < 0x1FF; ++i) {
//            System.out.format("%02X, %X -> %04X%n", (i >> 1), i & 1, map[i]);
//        }
//        System.out.format("00 -> %d%n", map[0]);
//        System.out.format("01 -> %d%n", map[1]);
//        System.out.format("10 -> %d%n", map[2]);
//        System.out.format("11 -> %d%n", map[3]);        
//        for (int i = 0; i < 0x1FFFF; ++i) {
//            final int ai = 0xFF & (i >> 9);
//            final int bi = 0xFF & (i >> 1);
//            final int ci = 0x01 & i;
//            final int v = map[i];
//            final int ao = 0xFF & (v >> 16);
//            final int bo = 0xFF & (v >> 8);
//            final int co = 0xFF & v;
//            System.out.format("ai = %02X, bi = %02X, ci = %X, ao = %02X, bo = %02X, co = %X%n", ai, bi, ci, ao, bo, co);
//        }
        
//        for (int a = 0; a <= 0x01; ++a) {
//            for (int b = 0; b <= 0x01; ++b) {
//                for (int c = 0; c <= 0x01; ++c) {
//                    int inputBits = c;
//                    for (int i = 7; i >= 0; --i) {
//                        inputBits |= ((a >> i) & 1) << (2 * i + 2);
//                        inputBits |= ((b >> i) & 1) << (2 * i + 1);                        
//                    }
//                    final int outputBits = map[inputBits];
//                    int sum = 0;
//                    int aOut = 0;
//                    for (int i = 7; i >= 0; --i) {
//                        aOut |= ((outputBits >> (i * 2 + 1)) & 1) << i;
//                    }
//                    for (int i = 8; i >= 0; --i) {
//                        sum |= ((outputBits >> (i * 2)) & 1) << i;
//                    }
//                    System.out.format("inputBits = %02X, outputBits = %02X, a = %02X, b = %02X, c = %X, sum = %02X, aOut = %02X%n", 
//                            inputBits, outputBits, a, b, c, sum, aOut);
//                }
//            }
//        }
        
        return mappings;
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
    
    private void generateComponentMapping(final Map<String, Component> components, 
            final Map<String, ComponentMapping> mappings, final Playfield playfield, final Component component) {
        
        final ComponentMappingType componentMappingType = solveComponentMappingType(components, mappings, playfield, 
                component);
        if (componentMappingType == null) {
            return;
        }        
        
        System.out.println(component.getName()); // TODO REMOVE
        
        switch (componentMappingType) {
            case BIT_TWO_BYTES:
                generateComponentMappingBitTwoBytes(components, mappings, playfield, component);
                break;
            case TWO_BYTES_BIT:
                generateComponentMappingTwoBytesBit(components, mappings, playfield, component);                
                break;
            default:
                generateComponentMappingAny(components, mappings, playfield, component);
                break;
        }
    }
    
    private void generateComponentMappingAny(final Map<String, Component> components, 
            final Map<String, ComponentMapping> mappings, final Playfield playfield, final Component component) {
        
        final ComponentMapping componentMapping = new ComponentMapping(component.getInputs().length);
        final int[] map = componentMapping.getMap();
        for (int inputBits = map.length - 1; inputBits >= 0; --inputBits) {
            playfield.clear();
            setInputs(playfield, component, inputBits);
            simulate(components, mappings, playfield, component);
            map[inputBits] = getOutputs(playfield, component);
        }
        mappings.put(component.getName(), componentMapping);
    }
    
    private void generateComponentMappingBitTwoBytes(final Map<String, Component> components, 
            final Map<String, ComponentMapping> mappings, final Playfield playfield, final Component component) {
        
        final ComponentMapping componentMapping = new ComponentMapping(ComponentMappingType.BIT_TWO_BYTES);
        final int[] map = componentMapping.getMap();
        for (int inputBits = map.length - 1; inputBits >= 0; --inputBits) {
            playfield.clear();
            setInputs(playfield, component, inputBits);
            simulate(components, mappings, playfield, component);
            map[inputBits] = getOutputs(playfield, component);
        }
        mappings.put(component.getName(), componentMapping);
    }

    private void generateComponentMappingTwoBytesBit(final Map<String, Component> components, 
            final Map<String, ComponentMapping> mappings, final Playfield playfield, final Component component) {
        
        final ComponentMapping componentMapping = new ComponentMapping(ComponentMappingType.TWO_BYTES_BIT);
        final int[] map = componentMapping.getMap();
        for (int inputBits = map.length - 1; inputBits >= 0; --inputBits) {
            playfield.clear();
            setInputs(playfield, component, ((inputBits & 0x1FFFE) << 7) | (inputBits & 1));
            simulate(components, mappings, playfield, component);
            map[inputBits] = getOutputs(playfield, component);
        }
        mappings.put(component.getName(), componentMapping);
    }    
    
    private ComponentMappingType solveComponentMappingType(final Map<String, Component> components, 
            final Map<String, ComponentMapping> mappings, final Playfield playfield, final Component component) {
        
        final int inputsCount = component.getInputs().length;
        if (inputsCount <= 20) {
            return ComponentMappingType.ANY;
        }
        if (inputsCount != 24) {
            return null;
        }
        
        final Random random = ThreadLocalRandom.current();
        while (true) {
            playfield.clear();
            final int inputs = random.nextInt() & 0xFFFFFF;
            setInputs(playfield, component, inputs);
            simulate(components, mappings, playfield, component);
            final int outputs = getOutputs(playfield, component);
            final int high = 0xFF & (outputs >> 16);
            final int low = 0xFF & outputs;
            if (high > 1 && low > 1) {
                throw new RuntimeException(String.format("Invalid output: name=%s, inputs=%X, outputs=%X%n", 
                        component.getName(), inputs, outputs));
            }
            if (low > 1) {
                return ComponentMappingType.BIT_TWO_BYTES;
            }
            if (high > 1) {
                return ComponentMappingType.TWO_BYTES_BIT;
            }
        }
    }
    
    private Map<String, Set<String>> findDependencies(final Map<String, Component> components) {        
        final Map<String, Set<String>> depends = new HashMap<>();
        for (final Component component : components.values()) {
            findDependencies(components, depends, component.getName());
        }
        return depends;
    }
    
    private Map<String, Set<String>> reverseDependencies(final Map<String, Set<String>> depends) {
        final Map<String, Set<String>> dependencies = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : depends.entrySet()) {
            final String key = entry.getKey();
            for (final String value : entry.getValue()) {
                dependencies.computeIfAbsent(value, v -> new HashSet<>()).add(key);
            }
        }
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
            if (instruction.getTetrimino() != null) {
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
       
    private void simulate(final Map<String, Component> components, final Map<String, ComponentMapping> mappings, 
            final Playfield playfield, final Component component) {
        simulate(components, mappings, playfield, component, playfield.getWidth() >> 1, playfield.getHeight() - 1);
    }

    private void simulate(final Map<String, Component> components, final Map<String, ComponentMapping> mappings, 
            final Playfield playfield, final Component component, final int originX, final int originY) {
        
        if (!component.getName().startsWith("_") && mappings.containsKey(component.getName())) {
            emulate(mappings, playfield, component, originX, originY);
            return;
        } 
        
        final Instruction[] instructions = component.getInstructions();
        if (instructions == null) {
            return;
        }
        
        for (int i = 0; i < instructions.length; ++i) {
            simulate(components, mappings, playfield, instructions[i], originX, originY);
        }
    } 
    
    private void simulate(final Map<String, Component> components, final Map<String, ComponentMapping> mappings, 
            final Playfield playfield, final Instruction instruction, final int originX, final int originY) {

        final int[] moves = instruction.getMoves();
        
        final String componentName = instruction.getComponentName();
        if (componentName != null) {
            final Component component = components.getOrDefault(componentName, null);
            if (component != null) {          
                simulate(components, mappings, playfield, component, originX + moves[0], originY - moves[1]);                
            }
            return;
        }
        
        final Tetrimino tetrimino = instruction.getTetrimino();
        int x = originX + moves[0];
        int y = playfield.getMinY() - 3;
        
        for (int i = 1; i < moves.length; ++i) {
            if ((i & 1) == 1) {
                y = moveDown(playfield, tetrimino, x, y, originY - moves[i]);
            } else {
                x = moveHorizontally(playfield, tetrimino, x, y, originX + moves[i]);
            }
        }
        
        y = hardDrop(playfield, tetrimino, x, y);
        lock(playfield, tetrimino, x, y);
    }    
    
    private void emulate(final Map<String, ComponentMapping> mappings, final Playfield playfield, 
            final Component component, final int originX, final int originY) {

        final ComponentMapping mapping = mappings.get(component.getName());
        if (mapping == null) {
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
                        input = mapping.setInputBit(input, inputs.length - 1 - i);
                        continue outer;
                    }
                }
            }
        }
        
        final int output = mapping.getMap()[input];
        
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
    
    private int moveHorizontally(final Playfield playfield, final Tetrimino tetrimino, final int startX, 
            final int startY, final int targetX) {        
        
        if (targetX < startX) {
            return moveLeft(playfield, tetrimino, startX, startY, targetX);
        } 
        
        return moveRight(playfield, tetrimino, startX, startY, targetX);
    }

    private int moveLeft(final Playfield playfield, final Tetrimino tetrimino, final int startX,
            final int startY, final int targetX) {

        final Point[] leftBlocks = tetrimino.getLeftBlocks();
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

    private int moveRight(final Playfield playfield, final Tetrimino tetrimino, final int startX,
            final int startY, final int targetX) {

        final Point[] rightBlocks = tetrimino.getRightBlocks();
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
    
    private int moveDown(final Playfield playfield, final Tetrimino tetrimino, final int startX, final int startY,
            final int targetY) {
        
        final Point[] bottomBlocks = tetrimino.getBottomBlocks();
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
    
    private int hardDrop(final Playfield playfield, final Tetrimino tetrimino, final int startX, final int startY) {
        
        final Point[] bottomBlocks = tetrimino.getBottomBlocks();
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
    
    private void lock(final Playfield playfield, final Tetrimino tetrimino, final int lockX, final int lockY) {
        
        int value = tetrimino.getGroupIndex() + 1;
        if (value > playfield.getMaxValue()) {
            value = playfield.getMaxValue();
        }
        
        final Point[] blocks = tetrimino.getBlocks();
        for (int i = blocks.length - 1; i >= 0; --i) {
            final Point block = blocks[i];
            playfield.set(lockX + block.x, lockY + block.y, value);
        }
    }    
    
    private void addInstruction(final List<Instruction> instructions, final Tetrimino tetrimino, final int x) {
        instructions.add(new Instruction(tetrimino, null, null, new int[] { x }));
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
                addInstruction(instructions, Tetrimino.JL, 1);
                break;
            case 2:
                addInstruction(instructions, Tetrimino.OS, 1);
                break;
            case 3:
                addInstruction(instructions, Tetrimino.LR, 0);
                break;
        }
        if (remainder == 0) {
            setInputs(is, 0, 0);
        } else {
            setInputs(is, 0, 1, 0);
        }
        for (int i = length >> 2; i > 0; --i) {
            addInstruction(instructions, Tetrimino.IV, 0);
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
                addInstruction(instructions, Tetrimino.LR, -1);
                break;
            case 2:
                addInstruction(instructions, Tetrimino.OS, 0);
                break;
            case 3:
                addInstruction(instructions, Tetrimino.JL, 0);
                break;
        }
        if (remainder == 0) {
            setInputs(is, 0, 0);
        } else {
            setInputs(is, -1, 0, 0);
        }
        for (int i = length >> 2; i > 0; --i) {
            addInstruction(instructions, Tetrimino.IV, 0);
        }
        is.setInstructions(instructions.toArray(new Instruction[instructions.size()]));
        setOutputs(is, 0, length); 
        components.put(is.getName(), is);        
    }    
    
    private void createSs(final Map<String, Component> components, final int length) {
        final Component ss = new Component(String.format("s%d", length));
        final List<Instruction> instructions = new ArrayList<>();
        for (int i = ((length - 1) >> 1) - 1, x = -1; i >= 0; --i, x -= 2) {
            instructions.add(new Instruction(Tetrimino.SH, null, null, new int[] { x }));
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
            instructions.add(new Instruction(Tetrimino.ZH, null, null, new int[] { x }));
        }
        zs.setInstructions(instructions.toArray(new Instruction[instructions.size()]));
        zs.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                new HorizontalLine[] { new HorizontalLine(0, 1), new HorizontalLine(1, 2, 0) })});
        zs.setOutputs(new Terminal[] { new Terminal(TerminalType.OUTPUT, "o", 
                new HorizontalLine[] { new HorizontalLine(0, length - 2, 2), new HorizontalLine(length - 1, 1) })});
        components.put(zs.getName(), zs);
    }    
    
    private void loadComponents(final File directory, final Map<String, Component> components) 
            throws IOException, ParseException {
        
        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                loadComponents(file, components);
                continue;
            }
            if (!file.getName().endsWith(".t")) {
                continue;
            }
            new Parser().parse(components, file);
        }
    }    
    
    public static void main(final String... args) throws Exception {
        new MapMaker().launch();
    }
}
