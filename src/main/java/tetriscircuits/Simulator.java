package tetriscircuits;

public class Simulator {
    
    public void simulate(final Playfield playfield, final Component component) {
        simulate(playfield, component, playfield.getWidth() >> 1, playfield.getHeight() - 1);
    }
    
    public void simulate(final Playfield playfield, final Component component, final int originX, final int originY) {
        simulate(playfield, component, originX, originY, null);
    }
    
    public void simulate(final Playfield playfield, final Component component, final TetriminoLockListener listener) {
        simulate(playfield, component, playfield.getWidth() >> 1, playfield.getHeight() - 1, listener);
    }

    public void simulate(final Playfield playfield, final Component component, final int originX, final int originY,
            final TetriminoLockListener listener) {
        simulate(playfield, component.getInstructions(), originX, originY, listener);        
    }
    
    public void simulate(final Playfield playfield, final Instruction[] instructions) {
        simulate(playfield, instructions, playfield.getWidth() >> 1, playfield.getHeight() - 1);
    }
    
    public void simulate(final Playfield playfield, final Instruction[] instructions, final int originX, 
            final int originY) {
        simulate(playfield, instructions, originX, originY, null);
    }
    
    public void simulate(final Playfield playfield, final Instruction[] instructions, final int originX, 
            final int originY, final TetriminoLockListener listener) {
        for (int i = 0; i < instructions.length; ++i) {            
            simulate(playfield, instructions[i], originX, originY, listener);
        }
    }
    
    public void simulate(final Playfield playfield, final Instruction instruction, final int originX, final int originY,
            final TetriminoLockListener listener) {
    
        final int[] moves = instruction.getMoves();
        
        final Component component = instruction.getComponent();
        if (component != null) {            
            simulate(playfield, component, moves[0] + originX, moves[1] + originY, listener);
            return;
        }
        
        final Tetrimino tetrimino = instruction.getTetrimino();
        int x = moves[0] + originX;
        int y = 0;
        
        for (int i = 1; i < moves.length; ++i) {
            if ((i & 1) == 1) {
                y = moveDown(playfield, tetrimino, x, y, originY + moves[i]);
            } else {
                x = moveHorizontally(playfield, tetrimino, x, y, originX + moves[i]);
            }
        }
        
        y = hardDrop(playfield, tetrimino, x, y);
        lock(playfield, tetrimino, x, y);
        
        if (listener != null) {
            listener.tetriminoLocked(tetrimino, x, y);
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
