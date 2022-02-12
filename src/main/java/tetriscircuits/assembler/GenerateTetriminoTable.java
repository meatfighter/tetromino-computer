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
    
    private static final String[] NAMES = { 
        "Td", "Tl", "Tu", "Tr", 
        "Jd", "Jl", "Ju", "Jr", 
        "Zh", "Zv", "Zh", "Zv",
        "O",  "O",  "O",  "O",
        "Sh", "Sv", "Sh", "Sv",
        "Ld", "Ll", "Lu", "Lr",
        "Ih", "Iv", "Ih", "Iv",
    };        

    public static void main(final String... args) {
        System.out.println("tetriminos:");
        System.out.println("; Y0 X0  Y1 X1  Y2 X2  Y3 X3");
        for (int i = 0; i < TETRIMINOS.length; ++i) {
            final int[][] blocks = TETRIMINOS[i];
            System.out.print("  ");
            for (final int[] bs : blocks) {
                System.out.format("%02X %02X  ", 0xFF & bs[1], 0xFF & bs[0]);
            }
            System.out.format("; %02X %s%n", i, NAMES[i]);
            if ((i & 3) == 3) {
                System.out.println();
            }
        }
        System.out.print(";                  ");
        for (int i = 0; i < 20; ++i) {
            System.out.format("%s%s%d", (i == 0) ? "" : " ", (i < 10) ? " " : "", i);
        }
        System.out.println();
        System.out.print("playfieldRowsHigh: ");
        for (int i = 0; i < 20; ++i) {
            System.out.format("%s%02X", (i == 0) ? "" : " ", (0xFCCB + (i << 5)) >> 8);
        }
        System.out.println();
        System.out.print("playfieldRowsLow:  ");
        for (int i = 0; i < 20; ++i) {
            System.out.format("%s%02X", (i == 0) ? "" : " ", (0xFCCB + (i << 5)) & 0xFF);
        }
        System.out.println();
    }
}
