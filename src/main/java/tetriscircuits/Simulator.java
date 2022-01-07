package tetriscircuits;

public class Simulator {
        
    public void init(final Playfield playfield, final Component component, final String inputBits) {
        init(playfield, component, inputBits, playfield.getWidth() >> 1, playfield.getHeight() - 1, 
                playfield.getMaxValue());
    }
    
    public void init(final Playfield playfield, final Component component, final String inputBits, 
            final int originX, final int originY, final int cellValue) {
        
        final Terminal[] inputs = component.getInputs();
        if (inputs == null) {
            return;
        }
                    
        for (int i = 0; i < inputBits.length() && i < inputs.length; ++i) {
            if (inputBits.charAt(i) == '1') {
                final HorizontalLine[] horizontalLines = inputs[i].getHorizontalLines();
                for (int j = horizontalLines.length - 1; j >= 0; --j) {
                    final HorizontalLine horizontalLine = horizontalLines[j];
                    final int y = originY - horizontalLine.getY();
                    final int maxX = horizontalLine.getMaxX();
                    for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {
                        playfield.set(originX + x, y, cellValue);
                    }
                }
            }
        }
    }
    
    public void addOutputs(final Playfield playfield, final Component component) {
        addOutputs(playfield, component, playfield.getWidth() >> 1, playfield.getHeight() - 1, playfield.getMaxValue());
    }
    
    public void addOutputs(final Playfield playfield, final Component component, final int originX, final int originY, 
            final int cellValue) {
        
        final Terminal[] outputs = component.getOutputs();
        if (outputs == null) {
            return;
        }
        
        for (int i = outputs.length - 1; i >= 0; --i) {
            final HorizontalLine[] horizontalLines = outputs[i].getHorizontalLines();
            for (int j = horizontalLines.length - 1; j >= 0; --j) {
                final HorizontalLine horizontalLine = horizontalLines[j];
                final int y = originY - horizontalLine.getY();
                final int maxX = horizontalLine.getMaxX();
                for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {
                    playfield.set(originX + x, y, cellValue);
                }
            }            
        }
    }
    
    public Rectangle[][] findTerminals(final Terminal[] terminals, final int originX, final int originY) {
        
        if (terminals == null) {
            return new Rectangle[0][];
        }
        
        final Rectangle[][] rectangles = new Rectangle[terminals.length][];
        for (int i = terminals.length - 1; i >= 0; --i) {
            final HorizontalLine[] horizontalLines = terminals[i].getHorizontalLines();
            final Rectangle[] rects = rectangles[i] = new Rectangle[horizontalLines.length];
            for (int j = horizontalLines.length - 1; j >= 0; --j) {
                final HorizontalLine horizontalLine = horizontalLines[j];
                final int y = originY + horizontalLine.getY();
                rects[j] = new Rectangle(originX + horizontalLine.getMinX(), y, 
                        horizontalLine.getMaxX() - horizontalLine.getMinX() + 1, 1);
            }
        }

        return rectangles;
    }    
    
    public void simulate(final Playfield playfield, final Component component, final int depth) {
        simulate(playfield, component, playfield.getWidth() >> 1, playfield.getHeight() - 1, depth);
    }
    
    public void simulate(final Playfield playfield, final Component component, final int originX, final int originY, 
            final int depth) {
        simulate(playfield, component, originX, originY, depth, null);
    }
    
    public void simulate(final Playfield playfield, final Component component, final int depth, 
            final LockedElementListener listener) {
        simulate(playfield, component, playfield.getWidth() >> 1, playfield.getHeight() - 1, depth, listener);
    }

    public void simulate(final Playfield playfield, final Component component, final int originX, final int originY, 
            final int depth, final LockedElementListener listener) {
        simulate(playfield, component.getInstructions(), originX, originY, depth, listener);        
    }
    
    public void simulate(final Playfield playfield, final Instruction[] instructions, final int depth) {
        simulate(playfield, instructions, playfield.getWidth() >> 1, playfield.getHeight() - 1, depth);
    }
    
    public void simulate(final Playfield playfield, final Instruction[] instructions, final int originX, 
            final int originY, final int depth) {
        simulate(playfield, instructions, originX, originY, depth, null);
    }
    
    public void simulate(final Playfield playfield, final Instruction[] instructions, final int originX, 
            final int originY, final int depth, final LockedElementListener listener) {
        if (instructions == null) {
            return;
        }
        for (int i = 0; i < instructions.length; ++i) {    
            simulate(playfield, instructions[i], originX, originY, depth, listener);
        }
    }
    
    public void simulate(final Playfield playfield, final Instruction instruction, final int originX, final int originY,
            final int depth, final LockedElementListener listener) {
    
        final int[] moves = instruction.getMoves();
        
        final Component component = instruction.getComponent();
        if (component != null) {   
            if (depth == 0) {
                // TODO SIMULATE
            } else {
                simulate(playfield, component, originX + moves[0], originY - moves[1], depth - 1, listener);
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
        
        if (listener != null) {
            listener.elementLocked(new LockedElement(tetrimino, x - (playfield.getWidth() >> 1), 
                    playfield.getHeight() - 1 - y));
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
}
