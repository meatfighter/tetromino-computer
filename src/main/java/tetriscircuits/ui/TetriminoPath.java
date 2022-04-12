package tetriscircuits.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tetriscircuits.Point;
import tetriscircuits.Tetrimino;

public final class TetriminoPath {
    
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;
    
    private static final boolean[][] MATRIX = new boolean[7][7];
    
    public static final TetriminoPath TD = new TetriminoPath(Tetrimino.TD, 0);
    public static final TetriminoPath TL = new TetriminoPath(Tetrimino.TL, 0);
    public static final TetriminoPath TU = new TetriminoPath(Tetrimino.TU, 0);
    public static final TetriminoPath TR = new TetriminoPath(Tetrimino.TR, 0);
    
    public static final TetriminoPath JD = new TetriminoPath(Tetrimino.JD, 1);
    public static final TetriminoPath JL = new TetriminoPath(Tetrimino.JL, 1);
    public static final TetriminoPath JU = new TetriminoPath(Tetrimino.JU, 1);
    public static final TetriminoPath JR = new TetriminoPath(Tetrimino.JR, 1);
    
    public static final TetriminoPath ZH = new TetriminoPath(Tetrimino.ZH, 2);
    public static final TetriminoPath ZV = new TetriminoPath(Tetrimino.ZV, 2);
    
    public static final TetriminoPath OS = new TetriminoPath(Tetrimino.OS, 3);
    
    public static final TetriminoPath SH = new TetriminoPath(Tetrimino.SH, 4);
    public static final TetriminoPath SV = new TetriminoPath(Tetrimino.SV, 4);
    
    public static final TetriminoPath LD = new TetriminoPath(Tetrimino.LD, 5);
    public static final TetriminoPath LL = new TetriminoPath(Tetrimino.LL, 5);
    public static final TetriminoPath LU = new TetriminoPath(Tetrimino.LU, 5);
    public static final TetriminoPath LR = new TetriminoPath(Tetrimino.LR, 5);
    
    public static final TetriminoPath IH = new TetriminoPath(Tetrimino.IH, 6);
    public static final TetriminoPath IV = new TetriminoPath(Tetrimino.IV, 6);
    
    public static final TetriminoPath[] T = { TD, TL, TU, TR };
    public static final TetriminoPath[] J = { JD, JL, JU, JR };
    public static final TetriminoPath[] Z = { ZH, ZV };
    public static final TetriminoPath[] O = { OS };
    public static final TetriminoPath[] S = { SH, SV };
    public static final TetriminoPath[] L = { LD, LL, LU, LR };
    public static final TetriminoPath[] I = { IH, IV };
    
    public static final TetriminoPath[][] TETRIMINO_PATHS = { T, J, Z, O, S, L, I }; 
    
    private static final Map<Tetrimino, TetriminoPath> PATHS;
    
    static {
        final Map<Tetrimino, TetriminoPath> map = new HashMap<>();
        for (final TetriminoPath[] paths : TETRIMINO_PATHS) {
            for (final TetriminoPath path : paths) {
                map.put(path.getTetrimino(), path);
            }
        }
        PATHS = Collections.unmodifiableMap(map);
    }
    
    public static TetriminoPath fromTetrimino(final Tetrimino tetrimino) {
        return PATHS.get(tetrimino);
    }    
        
    private final Tetrimino tetrimino;
    private final int colorIndex;
    private final Point[] points;
    
    private TetriminoPath(final Tetrimino tetrimino, final int colorIndex) {
        
        this.tetrimino = tetrimino;
        this.colorIndex = colorIndex;        
        
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
        
        final List<Point> path = new ArrayList<>();
        int startX = 0;
        int startY = 0;        
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
        path.add(new Point(startX, startY));
        
        int direction = RIGHT;
        int x = startX;
        int y = startY;
        
        while (true) {
            
            outer: while (true) {
                
                switch (direction) {
                    case UP:
                        if (MATRIX[y - 1][x] && !MATRIX[y - 1][x - 1]) {
                            --y;
                        } else {
                            break outer;
                        }
                        break;
                    case RIGHT:
                        if (MATRIX[y][x] && !MATRIX[y - 1][x]) {
                            ++x;
                        } else {                            
                            break outer;
                        }
                        break;
                    case DOWN:
                        if (!MATRIX[y][x] && MATRIX[y][x - 1]) {
                            ++y;
                        } else {
                            break outer;
                        }
                        break;
                    case LEFT:
                        if (!MATRIX[y][x - 1] && MATRIX[y - 1][x - 1]) {
                            --x;
                        } else {                            
                            break outer;
                        }
                        break;
                }
                
            }
            
            if (x == startX && y == startY) {
                break;
            } else {
                path.add(new Point(x, y));
            }
            
            if (direction == RIGHT || direction == LEFT) {
                if (!MATRIX[y][x] && MATRIX[y][x - 1]) {
                    direction = DOWN;
                } else {
                    direction = UP;
                }
            } else {
                if (MATRIX[y][x] && !MATRIX[y - 1][x]) {
                    direction = RIGHT;
                } else {
                    direction = LEFT;
                }
            }            
        }
        
        points = new Point[path.size()];
        path.toArray(points);
    }

    public Tetrimino getTetrimino() {
        return tetrimino;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public Point[] getPoints() {
        return points;
    }
    
    public static void main(final String... args) {
        for (final TetriminoPath[] paths : TETRIMINO_PATHS) {
            for (TetriminoPath path : paths) {
                System.out.format("%s: %s%n", path.getTetrimino().getName(), Arrays.asList(path.getPoints()));
            }
        }
    }
}
