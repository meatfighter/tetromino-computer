package tetriscircuits.assembler;

public class GenerateTetriminoTable {

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
    
    private static final int[][][] TETRIMINOS = {
        BLOCKS_TD, BLOCKS_TL, BLOCKS_TU, BLOCKS_TR,
        BLOCKS_JD, BLOCKS_JL, BLOCKS_JU, BLOCKS_JR,
        BLOCKS_ZH, BLOCKS_ZV, BLOCKS_ZH, BLOCKS_ZV,
        BLOCKS_OS, BLOCKS_OS, BLOCKS_OS, BLOCKS_OS,
        BLOCKS_SH, BLOCKS_SV, BLOCKS_SH, BLOCKS_SV,
        BLOCKS_LD, BLOCKS_LL, BLOCKS_LU, BLOCKS_LR,
        BLOCKS_IH, BLOCKS_IV, BLOCKS_IH, BLOCKS_IV,
    };

    public static void main(final String... args) {
        for (final int[][] blocks : TETRIMINOS) {
            System.out.print("  ");
            for (final int[] bs : blocks) {
                System.out.format("%02X %02X  ", 0xFF & bs[0], 0xFF & bs[1]);
            }
            System.out.println();
        }
    }
}
