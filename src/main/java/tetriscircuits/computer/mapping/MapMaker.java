package tetriscircuits.computer.mapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tetriscircuits.Component;
import tetriscircuits.HorizontalLine;
import tetriscircuits.Instruction;
import tetriscircuits.Playfield;
import tetriscircuits.Terminal;
import tetriscircuits.TerminalType;
import tetriscircuits.Tetrimino;
import tetriscircuits.parser.ParseException;
import tetriscircuits.parser.Parser;

public class MapMaker {
    
    public static final String WORKSPACE_DIR = "workspace";
    
    public void launch() throws Exception {
        final Map<String, Component> components = new HashMap<>();
        createIsSsAndZs(components);
        loadComponents(new File(WORKSPACE_DIR), components);
        
    }
    
    private void setInputs(final Playfield playfield, final Component component, final int inputBits) {
        setInputs(playfield, component, inputBits, playfield.getWidth() >> 1, playfield.getHeight() - 1, 
                playfield.getMaxValue());
    }
    
    private void setInputs(final Playfield playfield, final Component component, final int inputBits, 
            final int originX, final int originY, final int cellValue) {
        
        playfield.clear();
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
