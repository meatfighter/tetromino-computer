package tetrominocomputer.tse.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tetrominocomputer.sim.Point;
import tetrominocomputer.sim.Tetromino;

public final class TetrominoPath {
    
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;
    
    private static final boolean[][] MATRIX = new boolean[7][7];
    
    public static final TetrominoPath TD = new TetrominoPath(Tetromino.TD);
    public static final TetrominoPath TL = new TetrominoPath(Tetromino.TL);
    public static final TetrominoPath TU = new TetrominoPath(Tetromino.TU);
    public static final TetrominoPath TR = new TetrominoPath(Tetromino.TR);
    
    public static final TetrominoPath JD = new TetrominoPath(Tetromino.JD);
    public static final TetrominoPath JL = new TetrominoPath(Tetromino.JL);
    public static final TetrominoPath JU = new TetrominoPath(Tetromino.JU);
    public static final TetrominoPath JR = new TetrominoPath(Tetromino.JR);
    
    public static final TetrominoPath ZH = new TetrominoPath(Tetromino.ZH);
    public static final TetrominoPath ZV = new TetrominoPath(Tetromino.ZV);
    
    public static final TetrominoPath OS = new TetrominoPath(Tetromino.OS);
    
    public static final TetrominoPath SH = new TetrominoPath(Tetromino.SH);
    public static final TetrominoPath SV = new TetrominoPath(Tetromino.SV);
    
    public static final TetrominoPath LD = new TetrominoPath(Tetromino.LD);
    public static final TetrominoPath LL = new TetrominoPath(Tetromino.LL);
    public static final TetrominoPath LU = new TetrominoPath(Tetromino.LU);
    public static final TetrominoPath LR = new TetrominoPath(Tetromino.LR);
    
    public static final TetrominoPath IH = new TetrominoPath(Tetromino.IH);
    public static final TetrominoPath IV = new TetrominoPath(Tetromino.IV);
    
    public static final TetrominoPath[] T = { TD, TL, TU, TR };
    public static final TetrominoPath[] J = { JD, JL, JU, JR };
    public static final TetrominoPath[] Z = { ZH, ZV };
    public static final TetrominoPath[] O = { OS };
    public static final TetrominoPath[] S = { SH, SV };
    public static final TetrominoPath[] L = { LD, LL, LU, LR };
    public static final TetrominoPath[] I = { IH, IV };
    
    public static final TetrominoPath[][] TETROMINO_PATHS = { T, J, Z, O, S, L, I }; 
    
    private static final Map<Tetromino, TetrominoPath> PATHS;
    
    static {
        final Map<Tetromino, TetrominoPath> map = new HashMap<>();
        for (final TetrominoPath[] paths : TETROMINO_PATHS) {
            for (final TetrominoPath path : paths) {
                map.put(path.getTetromino(), path);
            }
        }
        PATHS = Collections.unmodifiableMap(map);
    }
    
    public static TetrominoPath fromTetromino(final Tetromino tetromino) {
        return PATHS.get(tetromino);
    }    
        
    private final Tetromino tetromino;
    private final PathPoint[] points;
    
    private TetrominoPath(final Tetromino tetromino) {
        
        this.tetromino = tetromino;
        
        for (int i = MATRIX.length - 1; i >= 0; --i) {
            final boolean[] row = MATRIX[i];
            for (int j = row.length - 1; j >= 0; --j) {
                row[j] = false;
            }
        }
        final Point[] blocks = tetromino.getBlocks();
        for (Point block : blocks) {
            MATRIX[3 + block.y][3 + block.x] = true;
        }
        
        final List<PathPoint> path = new ArrayList<>();
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
        path.add(new PathPoint(startX, startY, false, false));
        
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
            
            final int nextDirection;
            if (direction == RIGHT || direction == LEFT) {
                if (!MATRIX[y][x] && MATRIX[y][x - 1]) {
                    nextDirection = DOWN;
                } else {
                    nextDirection = UP;
                }
            } else {
                if (MATRIX[y][x] && !MATRIX[y - 1][x]) {
                    nextDirection = RIGHT;
                } else {
                    nextDirection = LEFT;
                }
            }
            
            if (x == startX && y == startY) {
                break;
            } else {
                switch (direction) {
                    case UP:
                        if (nextDirection == RIGHT) {
                            path.add(new PathPoint(x, y, false, false));
                        } else {
                            path.add(new PathPoint(x, y, false, true));
                        }
                        break;
                    case RIGHT:
                        if (nextDirection == DOWN) {
                            path.add(new PathPoint(x, y, true, false));
                        } else {
                            path.add(new PathPoint(x, y, false, false));
                        }
                        break;
                    case DOWN:
                        if (nextDirection == RIGHT) {
                            path.add(new PathPoint(x, y, true, false));
                        } else {
                            path.add(new PathPoint(x, y, true, true));
                        }
                        break;
                    case LEFT:
                        if (nextDirection == DOWN) {
                            path.add(new PathPoint(x, y, true, true));
                        } else {
                            path.add(new PathPoint(x, y, false, true));
                        }
                        break;
                }
            }
            
            direction = nextDirection;            
        }
        
        points = new PathPoint[path.size()];
        path.toArray(points);
    }

    public Tetromino getTetromino() {
        return tetromino;
    }

    public PathPoint[] getPoints() {
        return points;
    }
    
    public static void main(final String... args) {
        for (final TetrominoPath[] paths : TETROMINO_PATHS) {
            for (TetrominoPath path : paths) {
                System.out.format("%s: %s%n", path.getTetromino().getName(), Arrays.asList(path.getPoints()));
            }
        }
    }
}
