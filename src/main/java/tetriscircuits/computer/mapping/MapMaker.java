package tetriscircuits.computer.mapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
    
    public void launch() throws Exception {
        final Map<String, Component> components = new HashMap<>();
        final Map<String, ComponentMapping> mappings = new HashMap<>();
        createIsSsAndZs(components);
        loadComponents(new File(WORKSPACE_DIR), components);  
        final List<Component> order = orderComponents(components);
        final Playfield playfield = new Playfield(8192, 4096, 1);
        
        generateComponentMapping(components, mappings, playfield, components.get("ADD_AD_FB"));
    }
    
    private void generateComponentMapping(final Map<String, Component> components, 
            final Map<String, ComponentMapping> mappings, final Playfield playfield, final Component component) {
        
        final ComponentMappingType componentMappingType = solveComponentMappingType(components, mappings, playfield, 
                component);

        System.out.format("%s -> %s%n", component.getName(), componentMappingType);

        if (componentMappingType == null) {
            return;
        }
    }
    
    private ComponentMappingType solveComponentMappingType(final Map<String, Component> components, 
            final Map<String, ComponentMapping> mappings, final Playfield playfield, final Component component){
        
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
            setInputs(playfield, component, random.nextInt() & 0xFFFFFF);
            simulate(components, mappings, playfield, component);
            final int outputs = getOutputs(playfield, component);
            final int high = 0xFF & (outputs >> 16);
            final int low = 0xFF & outputs;
            if (high > 1 && low > 1) {
                throw new RuntimeException("Invalid output: " + component.getName());
            }
            if (low > 1) {
                return ComponentMappingType.BIT_TWO_BYTES;
            }
            if (high > 1) {
                return ComponentMappingType.TWO_BYTES_BIT;
            }
        }
    }
    
    private List<Component> orderComponents(final Map<String, Component> components) {
        final List<Component> order = new ArrayList<>();
        final Set<String> visited = new HashSet<>();
        
        for (final Component component : components.values()) {
            orderComponents(components, order, visited, component);
        }
        
        return order;
    }
    
    private void orderComponents(final Map<String, Component> components, final List<Component> order, 
            final Set<String> visited, final Component component) {
        
        if (component == null || visited.contains(component.getName()) || component.getName().startsWith("_")) {
            return;
        }
        
        for (final Instruction instruction : component.getInstructions()) {
            if (instruction.getComponentName() == null) {
                continue;
            }
            orderComponents(components, order, visited, components.get(instruction.getComponentName()));
        }
        
        order.add(component);
        visited.add(component.getName());
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
       
    public void simulate(final Map<String, Component> components, final Map<String, ComponentMapping> mappings, 
            final Playfield playfield, final Component component) {
        simulate(components, mappings, playfield, component, playfield.getWidth() >> 1, playfield.getHeight() - 1);
    }

    public void simulate(final Map<String, Component> components, final Map<String, ComponentMapping> mappings, 
            final Playfield playfield, final Component component, final int originX, final int originY) {
        
        if (mappings.containsKey(component.getName())) {
            emulate(components, mappings, playfield, component, originX, originY);
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
    
    public void emulate(final Map<String, Component> components, final Map<String, ComponentMapping> mappings,
            final Playfield playfield, final Component component, final int originX, final int originY) {

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
