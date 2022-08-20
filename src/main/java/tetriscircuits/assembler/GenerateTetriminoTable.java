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
        "td", "tl", "tu", "tr", 
        "jd", "jl", "ju", "jr", 
        "zh", "zv", "zh", "zv",
        "o ", "o ", "o ", "o ",
        "sh", "sv", "sh", "sv",
        "ld", "ll", "lu", "lr",
        "ih", "iv", "ih", "iv",
    };        

    public static void main(final String... args) {
//        System.out.println("tetriminos:");
//        System.out.println("; X0 Y0  X1 Y1  X2 Y2  X3 Y3");
//        for (int i = 0; i < TETRIMINOS.length; ++i) {
//            final int[][] blocks = TETRIMINOS[i];
//            System.out.print("  ");
//            for (final int[] bs : blocks) {
//                System.out.format("%02X %02X  ", 0xFF & bs[0], 0xFF & bs[1]);
//            }
//            System.out.format("; %02X %s%n", i, NAMES[i]);
//            if ((i & 3) == 3) {
//                System.out.println();
//            }
//        }
//        System.out.print(";                ");
//        for (int i = 0; i < 21; ++i) {
//            System.out.format("%s%s%d", (i == 0) ? "" : "   ", (i < 10) ? " " : "", i);
//        }
//        System.out.println();
//        System.out.print("playfieldRows: ");
//        for (int i = 0; i < 21; ++i) {
//            System.out.format("%s%04X", (i == 0) ? "" : " ", 0xFCCC + (i << 5));
//        }
//        System.out.println();

        final int ROW_LENGTH = 11;
        
        System.out.println("tetriminos:");
        System.out.println(";  0  1  2  3");
        for (int i = 0; i < TETRIMINOS.length; ++i) {
            final int[][] blocks = TETRIMINOS[i];
            System.out.print("  ");
            for (final int[] bs : blocks) {
                System.out.format("%02X ", 0xFF & (bs[0] + ROW_LENGTH * bs[1]));
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("{ ");
            boolean first = true;
            for (final int[] bs : blocks) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(String.format("%3d", bs[0] + ROW_LENGTH * bs[1]));
            }
            sb.append(" }");
            System.out.format("; %2d %s %s%n", i, NAMES[i], sb);
            if ((i & 3) == 3) {
                System.out.println();
            }
        }
    }
}
