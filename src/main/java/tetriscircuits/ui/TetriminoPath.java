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
    
    public static final TetriminoPath TD = new TetriminoPath(Tetrimino.TD);
    public static final TetriminoPath TL = new TetriminoPath(Tetrimino.TL);
    public static final TetriminoPath TU = new TetriminoPath(Tetrimino.TU);
    public static final TetriminoPath TR = new TetriminoPath(Tetrimino.TR);
    
    public static final TetriminoPath JD = new TetriminoPath(Tetrimino.JD);
    public static final TetriminoPath JL = new TetriminoPath(Tetrimino.JL);
    public static final TetriminoPath JU = new TetriminoPath(Tetrimino.JU);
    public static final TetriminoPath JR = new TetriminoPath(Tetrimino.JR);
    
    public static final TetriminoPath ZH = new TetriminoPath(Tetrimino.ZH);
    public static final TetriminoPath ZV = new TetriminoPath(Tetrimino.ZV);
    
    public static final TetriminoPath OS = new TetriminoPath(Tetrimino.OS);
    
    public static final TetriminoPath SH = new TetriminoPath(Tetrimino.SH);
    public static final TetriminoPath SV = new TetriminoPath(Tetrimino.SV);
    
    public static final TetriminoPath LD = new TetriminoPath(Tetrimino.LD);
    public static final TetriminoPath LL = new TetriminoPath(Tetrimino.LL);
    public static final TetriminoPath LU = new TetriminoPath(Tetrimino.LU);
    public static final TetriminoPath LR = new TetriminoPath(Tetrimino.LR);
    
    public static final TetriminoPath IH = new TetriminoPath(Tetrimino.IH);
    public static final TetriminoPath IV = new TetriminoPath(Tetrimino.IV);
    
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
    private final PathPoint[] points;
    
    private TetriminoPath(final Tetrimino tetrimino) {
        
        this.tetrimino = tetrimino;
        
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

    public Tetrimino getTetrimino() {
        return tetrimino;
    }

    public PathPoint[] getPoints() {
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
