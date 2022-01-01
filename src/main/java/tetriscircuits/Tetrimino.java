package tetriscircuits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tetrimino {
    
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
    
    public static final Tetrimino TD;
    public static final Tetrimino TL;
    public static final Tetrimino TU;
    public static final Tetrimino TR;
    
    public static final Tetrimino JD;
    public static final Tetrimino JL;
    public static final Tetrimino JU;
    public static final Tetrimino JR;
    
    public static final Tetrimino ZH;
    public static final Tetrimino ZV;
    
    public static final Tetrimino OS;
    
    public static final Tetrimino SH;
    public static final Tetrimino SV;
    
    public static final Tetrimino LD;
    public static final Tetrimino LL;
    public static final Tetrimino LU;
    public static final Tetrimino LR;
    
    public static final Tetrimino IH;
    public static final Tetrimino IV;
    
    private static final Map<String, Tetrimino> FROM_NAME = new HashMap<>();
    private static final Map<String, Tetrimino> FROM_NAME_TO_TETRIMINO;
    
    private final String name;
    private final int index;
    
    private final String groupName;
    private final int groupIndex;
       
    private final Point[] blocks;
    private final Point[] leftBlocks;
    private final Point[] rightBlocks;
    private final Point[] bottomBlocks;
    
    private Tetrimino cw;
    private Tetrimino ccw;
    
    private static void setMatrix(final int[][] blocks) {
        for (int i = MATRIX.length - 1; i >= 0; --i) {
            final boolean[] row = MATRIX[i];
            for (int j = row.length - 1; j >= 0; --j) {
                row[j] = false;
            }
        }
        for (int[] bs : blocks) {
            MATRIX[2 + bs[1]][2 + bs[0]] = true;
        }
    }
    
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
        TD = new Tetrimino("td", 0, "t", 0, BLOCKS_TD);
        TL = new Tetrimino("tl", 1, "t", 0, BLOCKS_TL);
        TU = new Tetrimino("tu", 2, "t", 0, BLOCKS_TU);
        TR = new Tetrimino("tr", 3, "t", 0, BLOCKS_TR);
        
        JD = new Tetrimino("jd", 0, "j", 1, BLOCKS_JD);
        JL = new Tetrimino("jl", 1, "j", 1, BLOCKS_JL);
        JU = new Tetrimino("ju", 2, "j", 1, BLOCKS_JU);
        JR = new Tetrimino("jr", 3, "j", 1, BLOCKS_JR);

        ZH = new Tetrimino("zh", 0, "z", 2, BLOCKS_ZH);
        ZV = new Tetrimino("zv", 1, "z", 2, BLOCKS_ZV);
        
        OS = new Tetrimino("os", 0, "o", 3, BLOCKS_OS);
        
        SH = new Tetrimino("sh", 0, "s", 4, BLOCKS_SH);
        SV = new Tetrimino("sv", 1, "s", 4, BLOCKS_SV);

        LD = new Tetrimino("ld", 0, "t", 5, BLOCKS_LD);
        LL = new Tetrimino("ll", 1, "t", 5, BLOCKS_LL);
        LU = new Tetrimino("lu", 2, "t", 5, BLOCKS_LU);
        LR = new Tetrimino("lr", 3, "t", 5, BLOCKS_LR);
        
        IH = new Tetrimino("ih", 0, "i", 6, BLOCKS_IH);
        IV = new Tetrimino("iv", 1, "i", 6, BLOCKS_IV);        
    }
    
    public static final Tetrimino[] T = { TD, TL, TU, TR };
    public static final Tetrimino[] J = { JD, JL, JU, JR };
    public static final Tetrimino[] Z = { ZH, ZV };
    public static final Tetrimino[] O = { OS };
    public static final Tetrimino[] S = { SH, SV };
    public static final Tetrimino[] L = { LD, LL, LU, LR };
    public static final Tetrimino[] I = { IH, IV };
    
    public static final Tetrimino[][] TETRIMINOS = { T, J, Z, O, S, L, I };
    
    static {
        for (final Tetrimino[] tetriminos : TETRIMINOS) {
            FROM_NAME.put(tetriminos[0].getGroupName().toLowerCase(), tetriminos[0]);
            for (int i = tetriminos.length - 1; i >= 0; --i) {
                int index = i + 1;
                if (index >= tetriminos.length) {
                    index = 0;
                } 
                tetriminos[i].setCw(tetriminos[index]);
                index = i - 1;
                if (index < 0) {
                    index = tetriminos.length - 1;
                } 
                tetriminos[i].setCcw(tetriminos[index]);
            }
        }
        FROM_NAME_TO_TETRIMINO = Collections.unmodifiableMap(FROM_NAME);
    }
    
    public static Tetrimino fromName(final String name) {
        return FROM_NAME_TO_TETRIMINO.get(name.toLowerCase());
    }
    
    public Tetrimino(final String name, final int index, final String groupName, final int groupIndex, 
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
        
        setMatrix(blocks);
        this.leftBlocks = findLeftBlocks();
        this.rightBlocks = findRightBlocks();
        this.bottomBlocks = findBottomBlocks();
        
        FROM_NAME.put(name.toLowerCase(), this);
    }

    public void setCw(final Tetrimino cw) {
        this.cw = cw;
    }

    public void setCcw(final Tetrimino ccw) {
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

    public Tetrimino getCw() {
        return cw;
    }

    public Tetrimino getCcw() {
        return ccw;
    }
}
