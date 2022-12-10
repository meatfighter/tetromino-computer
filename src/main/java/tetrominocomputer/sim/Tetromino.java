package tetrominocomputer.sim;

import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Tetromino {
    
    private static final boolean[][] MATRIX = new boolean[5][5];
    
    private static final int[][] BLOCKS_TD = { { -1,  0 }, {  0,  0 }, {  1,  0 }, {  0,  1 }, };
    private static final int[][] BLOCKS_TL = { {  0, -1 }, { -1,  0 }, {  0,  0 }, {  0,  1 }, };
    private static final int[][] BLOCKS_TU = { { -1,  0 }, {  0,  0 }, {  1,  0 }, {  0, -1 }, };
    private static final int[][] BLOCKS_TR = { {  0, -1 }, {  0,  0 }, {  1,  0 }, {  0,  1 }, };
    
    private static final int[][] BLOCKS_JD = { { -1,  0 }, {  0,  0 }, {  1,  0 }, {  1,  1 }, };
    private static final int[][] BLOCKS_JL = { {  0, -1 }, {  0,  0 }, { -1,  1 }, {  0,  1 }, };
    private static final int[][] BLOCKS_JU = { { -1, -1 }, { -1,  0 }, {  0,  0 }, {  1,  0 }, };
    private static final int[][] BLOCKS_JR = { {  0, -1 }, {  1, -1 }, {  0,  0 }, {  0,  1 }, };
    
    private static final int[][] BLOCKS_ZH = { { -1,  0 }, {  0,  0 }, {  0,  1 }, {  1,  1 }, };
    private static final int[][] BLOCKS_ZV = { {  1, -1 }, {  0,  0 }, {  1,  0 }, {  0,  1 }, };
    
    private static final int[][] BLOCKS_OS = { { -1,  0 }, {  0,  0 }, { -1,  1 }, {  0,  1 }, };
    
    private static final int[][] BLOCKS_SH = { {  0,  0 }, {  1,  0 }, { -1,  1 }, {  0,  1 }, };
    private static final int[][] BLOCKS_SV = { {  0, -1 }, {  0,  0 }, {  1,  0 }, {  1,  1 }, };
    
    private static final int[][] BLOCKS_LD = { { -1,  0 }, {  0,  0 }, {  1,  0 }, { -1,  1 }, };
    private static final int[][] BLOCKS_LL = { { -1, -1 }, {  0, -1 }, {  0,  0 }, {  0,  1 }, };
    private static final int[][] BLOCKS_LU = { {  1, -1 }, { -1,  0 }, {  0,  0 }, {  1,  0 }, };
    private static final int[][] BLOCKS_LR = { {  0, -1 }, {  0,  0 }, {  0,  1 }, {  1,  1 }, };
    
    private static final int[][] BLOCKS_IH = { { -2,  0 }, { -1,  0 }, {  0,  0 }, {  1,  0 }, };
    private static final int[][] BLOCKS_IV = { {  0, -2 }, {  0, -1 }, {  0,  0 }, {  0,  1 }, };
    
    public static final Tetromino TD;
    public static final Tetromino TL;
    public static final Tetromino TU;
    public static final Tetromino TR;
    
    public static final Tetromino JD;
    public static final Tetromino JL;
    public static final Tetromino JU;
    public static final Tetromino JR;
    
    public static final Tetromino ZH;
    public static final Tetromino ZV;
    
    public static final Tetromino OS;
    
    public static final Tetromino SH;
    public static final Tetromino SV;
    
    public static final Tetromino LD;
    public static final Tetromino LL;
    public static final Tetromino LU;
    public static final Tetromino LR;
    
    public static final Tetromino IH;
    public static final Tetromino IV;
    
    private static final Map<String, Tetromino> FROM_NAME = new HashMap<>();
    private static final Map<String, Tetromino> FROM_NAME_TO_TETROMINO;
    
    private final String name;
    private final int index;
    
    private final String groupName;
    private final int groupIndex;
       
    private final Point[] blocks;
    private final Point[] leftBlocks;
    private final Point[] rightBlocks;
    private final Point[] bottomBlocks;
    
    private final Extents extents;
    
    private Tetromino cw;
    private Tetromino ccw;
    
    private static Point[] findLeftBlocks() {
        final List<Point> leftBlocks = new ArrayList<>();
        for (int y = MATRIX.length - 1; y >= 0; --y) {
            final boolean[] row = MATRIX[y];
            for (int x = 0; x < row.length; ++x) {
                if (row[x]) {
                    leftBlocks.add(new Point(x - 2, y - 2));
                    break;
                }
            }
        }
        return leftBlocks.toArray(new Point[leftBlocks.size()]);
    }
    
    private static Point[] findRightBlocks() {
        final List<Point> rightBlocks = new ArrayList<>();
        for (int y = MATRIX.length - 1; y >= 0; --y) {
            final boolean[] row = MATRIX[y];
            for (int x = row.length - 1; x >= 0; --x) {
                if (row[x]) {
                    rightBlocks.add(new Point(x - 2, y - 2));
                    break;
                }
            }
        }
        return rightBlocks.toArray(new Point[rightBlocks.size()]);
    }   
    
    private static Point[] findBottomBlocks() {
        final List<Point> bottomBlocks = new ArrayList<>();
        for (int x = MATRIX[0].length - 1; x >= 0; --x) {
            for (int y = MATRIX.length - 1; y >= 0; --y) {
                if (MATRIX[y][x]) {
                    bottomBlocks.add(new Point(x - 2, y - 2));
                    break;
                }
            }
        }
        return bottomBlocks.toArray(new Point[bottomBlocks.size()]);
    }    
    
    static {
        TD = new Tetromino("td", 0, "t", 0, BLOCKS_TD);
        TL = new Tetromino("tl", 1, "t", 0, BLOCKS_TL);
        TU = new Tetromino("tu", 2, "t", 0, BLOCKS_TU);
        TR = new Tetromino("tr", 3, "t", 0, BLOCKS_TR);
        
        JD = new Tetromino("jd", 0, "j", 1, BLOCKS_JD);
        JL = new Tetromino("jl", 1, "j", 1, BLOCKS_JL);
        JU = new Tetromino("ju", 2, "j", 1, BLOCKS_JU);
        JR = new Tetromino("jr", 3, "j", 1, BLOCKS_JR);

        ZH = new Tetromino("zh", 0, "z", 2, BLOCKS_ZH);
        ZV = new Tetromino("zv", 1, "z", 2, BLOCKS_ZV);
        
        OS = new Tetromino( "o", 0, "o", 3, BLOCKS_OS);
        
        SH = new Tetromino("sh", 0, "s", 4, BLOCKS_SH);
        SV = new Tetromino("sv", 1, "s", 4, BLOCKS_SV);

        LD = new Tetromino("ld", 0, "l", 5, BLOCKS_LD);
        LL = new Tetromino("ll", 1, "l", 5, BLOCKS_LL);
        LU = new Tetromino("lu", 2, "l", 5, BLOCKS_LU);
        LR = new Tetromino("lr", 3, "l", 5, BLOCKS_LR);
        
        IH = new Tetromino("ih", 0, "i", 6, BLOCKS_IH);
        IV = new Tetromino("iv", 1, "i", 6, BLOCKS_IV);        
    }
    
    public static final Tetromino[] T = { TD, TL, TU, TR };
    public static final Tetromino[] J = { JD, JL, JU, JR };
    public static final Tetromino[] Z = { ZH, ZV };
    public static final Tetromino[] O = { OS };
    public static final Tetromino[] S = { SH, SV };
    public static final Tetromino[] L = { LD, LL, LU, LR };
    public static final Tetromino[] I = { IH, IV };
    
    public static final Tetromino[][] TETROMINOES = { T, J, Z, O, S, L, I };
    
    static {
        for (final Tetromino[] tetrominoes : TETROMINOES) {
            FROM_NAME.put(tetrominoes[0].getGroupName().toLowerCase(), tetrominoes[0]);
            for (int i = tetrominoes.length - 1; i >= 0; --i) {
                int index = i + 1;
                if (index >= tetrominoes.length) {
                    index = 0;
                } 
                tetrominoes[i].setCw(tetrominoes[index]);
                index = i - 1;
                if (index < 0) {
                    index = tetrominoes.length - 1;
                } 
                tetrominoes[i].setCcw(tetrominoes[index]);
            }
        }
        FROM_NAME_TO_TETROMINO = Collections.unmodifiableMap(FROM_NAME);
    }
    
    public static Tetromino fromName(final String name) {
        return FROM_NAME_TO_TETROMINO.get(name.toLowerCase());
    }
    
    private Tetromino(final String name, final int index, final String groupName, final int groupIndex, 
            final int[][] blocks) {
        this.name = name;
        this.index = index;
        this.groupName = groupName;
        this.groupIndex = groupIndex;
        
        this.blocks = new Point[blocks.length];
        for (int i = blocks.length - 1; i >= 0; --i) {
            final int[] bs = blocks[i];
            this.blocks[i] = new Point(bs[0], bs[1]);
        }
        
        for (int i = MATRIX.length - 1; i >= 0; --i) {
            final boolean[] row = MATRIX[i];
            for (int j = row.length - 1; j >= 0; --j) {
                row[j] = false;
            }
        }
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int[] bs : blocks) {
            minX = min(minX, bs[0]);
            minY = min(minY, bs[1]);
            maxX = max(maxX, bs[0]);
            maxY = max(maxY, bs[1]);
            MATRIX[2 + bs[1]][2 + bs[0]] = true;
        }
        this.extents = new Extents(minX, maxX, -maxY, -minY);
                
        this.leftBlocks = findLeftBlocks();
        this.rightBlocks = findRightBlocks();
        this.bottomBlocks = findBottomBlocks();
        
        FROM_NAME.put(name.toLowerCase(), this);
    }

    public void setCw(final Tetromino cw) {
        this.cw = cw;
    }

    public void setCcw(final Tetromino ccw) {
        this.ccw = ccw;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public Point[] getBlocks() {
        return blocks;
    }

    public Point[] getLeftBlocks() {
        return leftBlocks;
    }

    public Point[] getRightBlocks() {
        return rightBlocks;
    }

    public Point[] getBottomBlocks() {
        return bottomBlocks;
    }

    public Tetromino getCw() {
        return cw;
    }

    public Tetromino getCcw() {
        return ccw;
    }

    public Extents getExtents() {
        return extents;
    }
    
    public int getMinX() {
        return extents.getMinX();
    }
    
    public int getMaxX() {
        return extents.getMaxX();
    }
    
    public int getMinY() {
        return extents.getMinY();
    }
    
    public int getMaxY() {
        return extents.getMaxY();
    }
}