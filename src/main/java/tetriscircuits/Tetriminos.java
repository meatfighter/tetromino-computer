package tetriscircuits;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

public class Tetriminos {

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
    
    public static final String[][] FINE_NAMES = {
        { "td", "tl", "tu", "tr" },
        { "jd", "jl", "ju", "jr" },
        { "zh", "zv" },
        { "o" },
        { "sh", "sv" },
        { "ld", "ll", "lu", "lr" },
        { "ih", "iv" },
    };
    
    public static final int[][][][] TETRIMINOS = { GROUP_T, GROUP_J, GROUP_Z, GROUP_O, GROUP_S, GROUP_L, GROUP_I };
    
    public static final int T = 0;
    public static final int J = 1;
    public static final int Z = 2;
    public static final int O = 3;
    public static final int S = 4;
    public static final int L = 5;
    public static final int I = 6;
    
    public static final String[] NAMES = { "t", "j", "z", "o", "s", "l", "i" };
    
    private static final Object[][] TETRIMINOS_TABLE = {
        {  "t", 0, 0 },
        { "td", 0, 0 },
        { "tl", 0, 1 },
        { "tu", 0, 2 },
        { "tr", 0, 3 },
        
        {  "j", 1, 0 },
        { "jd", 1, 0 },
        { "jl", 1, 1 },
        { "ju", 1, 2 },
        { "jr", 1, 3 },
        
        {  "z", 2, 0 },
        { "zh", 2, 0 },
        { "zv", 2, 1 },
        
        {  "o", 3, 0 },
        { "os", 3, 0 },
        
        {  "s", 4, 0 },
        { "sh", 4, 0 },
        { "sv", 4, 1 },
        
        {  "l", 5, 0 },
        { "ld", 5, 0 },
        { "ll", 5, 1 },
        { "lu", 5, 2 },
        { "lr", 5, 3 },
        
        {  "i", 6, 0 },
        { "ih", 6, 0 },
        { "iv", 6, 1 },
    };
    
    public static final Map<String, Pair> INDEX_AND_ROTATIONS;
    
    static {
        final Map<String, Pair> indexAndRotations = new HashMap<>();
        for (final Object[] entry : TETRIMINOS_TABLE) {
            indexAndRotations.put((String)entry[0], Pair.of(entry[1], entry[2]));
        }
        INDEX_AND_ROTATIONS = Collections.unmodifiableMap(indexAndRotations);
    } 
}
