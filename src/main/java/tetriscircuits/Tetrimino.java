package tetriscircuits;

public class Tetrimino {

    public static final int[][] Td = { { -1,  0 }, {  0,  0 }, {  1,  0 }, {  0,  1 }, };
    public static final int[][] Tl = { {  0, -1 }, { -1,  0 }, {  0,  0 }, {  0,  1 }, };
    public static final int[][] Tu = { { -1,  0 }, {  0,  0 }, {  1,  0 }, {  0, -1 }, };
    public static final int[][] Tr = { {  0, -1 }, {  0,  0 }, {  1,  0 }, {  0,  1 }, };
    
    public static final int[][] Jd = { { -1,  0 }, {  0,  0 }, {  1,  0 }, {  1,  1 }, };
    public static final int[][] Jl = { {  0, -1 }, {  0,  0 }, { -1,  1 }, {  0,  1 }, };
    public static final int[][] Ju = { { -1, -1 }, { -1,  0 }, {  0,  0 }, {  1,  0 }, };
    public static final int[][] Jr = { {  0, -1 }, {  1, -1 }, {  0,  0 }, {  0,  1 }, };
    
    public static final int[][] Zh = { { -1,  0 }, {  0,  0 }, {  0,  1 }, {  1,  1 }, };
    public static final int[][] Zv = { {  1, -1 }, {  0,  0 }, {  1,  0 }, {  0,  1 }, };
    
    public static final int[][] Os = { { -1,  0 }, {  0,  0 }, { -1,  1 }, {  0,  1 }, };
    
    public static final int[][] Sh = { {  0,  0 }, {  1,  0 }, { -1,  1 }, {  0,  1 }, };
    public static final int[][] Sv = { {  0, -1 }, {  0,  0 }, {  1,  0 }, {  1,  1 }, };
    
    public static final int[][] Ld = { { -1,  0 }, {  0,  0 }, {  1,  0 }, { -1,  1 }, };
    public static final int[][] Ll = { { -1, -1 }, {  0, -1 }, {  0,  0 }, {  0,  1 }, };
    public static final int[][] Lu = { {  1, -1 }, { -1,  0 }, {  0,  0 }, {  1,  0 }, };
    public static final int[][] Lr = { {  0, -1 }, {  0,  0 }, {  0,  1 }, {  1,  1 }, };
    
    public static final int[][] Ih = { { -2,  0 }, { -1,  0 }, {  0,  0 }, {  1,  0 }, };
    public static final int[][] Iv = { {  0, -2 }, {  0, -1 }, {  0,  0 }, {  0,  1 }, };
    
    public static final int[][][] GROUP_T = { Td, Tl, Tu, Tr };
    public static final int[][][] GROUP_J = { Jd, Jl, Ju, Jr };
    public static final int[][][] GROUP_Z = { Zh, Zv };
    public static final int[][][] GROUP_O = { Os };
    public static final int[][][] GROUP_S = { Sh, Sv };
    public static final int[][][] GROUP_L = { Ld, Ll, Lu, Lr };
    public static final int[][][] GROUP_I = { Ih, Iv };
    
    public static final int[][][][] TETRIMINOS = { GROUP_T, GROUP_J, GROUP_Z, GROUP_O, GROUP_S, GROUP_L, GROUP_I };
    
    public static final int T = 0;
    public static final int J = 1;
    public static final int Z = 2;
    public static final int O = 3;
    public static final int S = 4;
    public static final int L = 5;
    public static final int I = 6;
}
