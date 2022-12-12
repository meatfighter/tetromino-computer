package tetrominocomputer.ts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import tetrominocomputer.sim.Component;
import tetrominocomputer.sim.HorizontalLine;
import tetrominocomputer.sim.Instruction;
import tetrominocomputer.sim.Playfield;
import tetrominocomputer.sim.Point;
import tetrominocomputer.sim.Terminal;
import tetrominocomputer.sim.TerminalType;
import tetrominocomputer.sim.Tetromino;

public abstract class AbstractSimulator {
    
    protected void returnPlayfield(final List<Playfield> playfieldPool, final Playfield playfield) {
        if (playfield == null) {
            return;
        }
        playfield.clear();
        playfieldPool.add(playfield);
    }
    
    protected Playfield borrowPlayfield(final List<Playfield> playfieldPool) {
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
           
    protected void setInputs(final Playfield playfield, final Component component, final int inputBits) {
        setInputs(playfield, component, inputBits, playfield.getWidth() >> 1, playfield.getHeight() - 1, 
                playfield.getMaxValue());
    }
    
    protected void setInputs(final Playfield playfield, final Component component, final int inputBits, 
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
    
    protected int getOutputs(final Playfield playfield, final Component component) {
        return getOutputs(playfield, component, playfield.getWidth() >> 1, playfield.getHeight() - 1);
    }
    
    protected int getOutputs(final Playfield playfield, final Component component, final int originX, final int originY) {
        
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
       
    protected int moveHorizontally(final Playfield playfield, final Tetromino tetromino, final int startX, 
            final int startY, final int targetX) {        
        
        if (targetX < startX) {
            return moveLeft(playfield, tetromino, startX, startY, targetX);
        } 
        
        return moveRight(playfield, tetromino, startX, startY, targetX);
    }

    protected int moveLeft(final Playfield playfield, final Tetromino tetromino, final int startX,
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

    protected int moveRight(final Playfield playfield, final Tetromino tetromino, final int startX,
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
    
    protected int moveDown(final Playfield playfield, final Tetromino tetromino, final int startX, final int startY,
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
    
    protected int hardDrop(final Playfield playfield, final Tetromino tetromino, final int startX, final int startY) {
        
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
    
    protected void lock(final Playfield playfield, final Tetromino tetromino, final int lockX, final int lockY) {
        
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
    
    protected void addInstruction(final List<Instruction> instructions, final Tetromino tetromino, final int x) {
        instructions.add(new Instruction(tetromino, null, null, new int[] { x }));
    }
    
    protected void setInputs(final Component component, final int x, final int y) {
        component.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                        new HorizontalLine[] { new HorizontalLine(x, y) })});
    }
    
    protected void setInputs(final Component component, final int minX, final int maxX, final int y) {
        component.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                        new HorizontalLine[] { new HorizontalLine(minX, maxX, y) })});
    }
    
    protected void setOutputs(final Component component, final int x, final int y) {
        component.setOutputs(new Terminal[] { new Terminal(TerminalType.OUTPUT, "o", 
                        new HorizontalLine[] { new HorizontalLine(x, y) })});
    }    
    
    protected void createIsSsAndZs(final Map<String, Component> components) {
        for (int i = 3; i <= 2001; ++i) {
            createIls(components, i);
            createIrs(components, i);
        }
        for (int i = 3; i <= 2001; i += 2) {
            createSs(components, i);
            createZs(components, i);
        }
    }
    
    protected void createIls(final Map<String, Component> components, final int length) {                
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
    
    protected void createIrs(final Map<String, Component> components, final int length) {                
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
    
    protected void createSs(final Map<String, Component> components, final int length) {
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
    
    protected void createZs(final Map<String, Component> components, final int length) {
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
}
