package tetriscircuits.ui;

import java.awt.geom.Path2D;
import tetriscircuits.Point;
import tetriscircuits.Tetrimino;

public final class TetriminoPaths {
    
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;
    
    private static final boolean[][] MATRIX = new boolean[7][7];
    
    private static Path2D createPath(final Tetrimino tetrimino) {
        
        for (int i = MATRIX.length - 1; i >= 0; --i) {
            final boolean[] row = MATRIX[i];
            for (int j = row.length - 1; j >= 0; --j) {
                row[j] = false;
            }
        }
        final Point[] blocks = tetrimino.getBlocks();
        for (Point block : blocks) {
            MATRIX[3 + block.y][3 + block.x] = true;
        }
        
        int startX = 0;
        int startY = 0;
        int direction = RIGHT;
        outer: for (int i = 0; i < MATRIX.length; ++i) {
            final boolean[] row = MATRIX[i];
            for (int j = 0; j < row.length; ++j) {
                if (row[j]) {
                    startX = j;
                    startY = i;
                    break outer;
                }
            }            
        }
        
        final Path2D path = new Path2D.Float();
        path.moveTo(startX, startY);
        
        
        
        return path;
    }
    
    private TetriminoPaths() {        
    }
}
