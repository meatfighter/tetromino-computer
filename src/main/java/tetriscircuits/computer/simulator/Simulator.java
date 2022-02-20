package tetriscircuits.computer.simulator;

// 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
// 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
// I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q

public final class Simulator {

    private static final int[][][] SWAP = new int[256][256][2];
    private static final int[][][] COPY_A_B = new int[256][256][2];
    private static final int[][][] COPY_B_A = new int[256][256][2];
    private static final int[][][] DEC_16 = new int[256][256][2];
    private static final int[][][] INC_16 = new int[256][256][2];
    private static final int[][][] AND_A = new int[256][256][2];
    private static final int[][][] AND_B = new int[256][256][2];
    
    private static final int[][][][] C_CMP = new int[2][256][256][3];
    private static final int[][][][] CMP_C = new int[256][256][2][3];
    private static final int[][][][] C_CMP_AND = new int[2][256][256][3];
    private static final int[][][][] CMP_AND_C = new int[256][256][2][3];
    private static final int[][][][] C_COPY_A_B = new int[2][256][256][3];
    private static final int[][][][] C_COPY_B_A = new int[2][256][256][3];
    private static final int[][][][] COPY_A_B_C = new int[256][256][2][3];
    private static final int[][][][] COPY_B_A_C = new int[256][256][2][3];
    private static final int[][][][] AND_A_B_C = new int[256][256][2][3];
    private static final int[][][][] AND_C_A_B = new int[2][256][256][3];
    private static final int[][][][] AND_A_NOT_B_C = new int[256][256][2][3];
    private static final int[][][][] C_AND_A_NOT_B = new int[2][256][256][3];    
    
    static {
        for (int a = 0xFF; a >= 0; --a) {
            for (int b = 0xFF; b >= 0; --b) {
                SWAP[a][b][0] = b;
                SWAP[a][b][1] = a;
                
                COPY_A_B[a][b][0] = a;
                COPY_A_B[a][b][1] = a;
                
                COPY_B_A[a][b][0] = b;
                COPY_B_A[a][b][1] = b;
                
                final int value = (a << 8) | b;
                final int dec16 = 0xFFFF & (value - 1);
                final int inc16 = 0xFFFF & (value + 1);
                DEC_16[a][b][0] = dec16 >> 8;
                DEC_16[a][b][1] = dec16 & 0xFF;
                INC_16[a][b][0] = inc16 >> 8;
                INC_16[a][b][1] = inc16 & 0xFF;
                
                AND_A[a][b][0] = a & b;
                AND_A[a][b][1] = b;
                
                AND_B[a][b][0] = a;
                AND_B[a][b][1] = a & b;
                
                C_CMP[0][a][b][0] = (a == b) ? 1 : 0;
                C_CMP[0][a][b][1] = a;
                C_CMP[0][a][b][2] = b;
                C_CMP[1][a][b][0] = (a == b) ? 1 : 0;
                C_CMP[1][a][b][1] = a;
                C_CMP[1][a][b][2] = b;
                
                CMP_C[a][b][0][0] = a;
                CMP_C[a][b][0][1] = b;
                CMP_C[a][b][0][2] = (a == b) ? 1 : 0;
                CMP_C[a][b][1][0] = a;
                CMP_C[a][b][1][1] = b;
                CMP_C[a][b][1][2] = (a == b) ? 1 : 0;
                
                C_CMP_AND[0][a][b][0] = 0;
                C_CMP_AND[0][a][b][1] = a;
                C_CMP_AND[0][a][b][2] = b;
                C_CMP_AND[1][a][b][0] = (a == b) ? 1 : 0;
                C_CMP_AND[1][a][b][1] = a;
                C_CMP_AND[1][a][b][2] = b;
                
                CMP_AND_C[a][b][0][0] = a;
                CMP_AND_C[a][b][0][1] = b;
                CMP_AND_C[a][b][0][2] = 0;
                CMP_AND_C[a][b][1][0] = a;
                CMP_AND_C[a][b][1][1] = b;
                CMP_AND_C[a][b][1][2] = (a == b) ? 1 : 0;
                
                C_COPY_A_B[0][a][b][0] = 0;
                C_COPY_A_B[0][a][b][1] = a;
                C_COPY_A_B[0][a][b][2] = b;
                C_COPY_A_B[1][a][b][0] = 1;
                C_COPY_A_B[1][a][b][1] = a;
                C_COPY_A_B[1][a][b][2] = a;
                
                C_COPY_B_A[0][a][b][0] = 0;
                C_COPY_B_A[0][a][b][1] = a;
                C_COPY_B_A[0][a][b][2] = b;
                C_COPY_B_A[1][a][b][0] = 1;
                C_COPY_B_A[1][a][b][1] = b;
                C_COPY_B_A[1][a][b][2] = b;

                COPY_A_B_C[a][b][0][0] = a;
                COPY_A_B_C[a][b][0][1] = b;
                COPY_A_B_C[a][b][0][2] = 0;
                COPY_A_B_C[a][b][1][0] = a;
                COPY_A_B_C[a][b][1][1] = a;
                COPY_A_B_C[a][b][1][2] = 1;
                
                COPY_B_A_C[a][b][0][0] = a;
                COPY_B_A_C[a][b][0][1] = b;
                COPY_B_A_C[a][b][0][2] = 0;
                COPY_B_A_C[a][b][1][0] = b;
                COPY_B_A_C[a][b][1][1] = b;
                COPY_B_A_C[a][b][1][2] = 1;
                
                AND_A_B_C[a][b][0][0] = a;
                AND_A_B_C[a][b][0][1] = b;
                AND_A_B_C[a][b][0][2] = a & b;
                AND_A_B_C[a][b][1][0] = a;
                AND_A_B_C[a][b][1][1] = b;
                AND_A_B_C[a][b][1][2] = a & b;
                
                AND_C_A_B[0][a][b][0] = a & b;
                AND_C_A_B[0][a][b][1] = a;
                AND_C_A_B[0][a][b][2] = b;
                AND_C_A_B[1][a][b][0] = a & b;
                AND_C_A_B[1][a][b][1] = a;
                AND_C_A_B[1][a][b][2] = b;
                
                AND_A_NOT_B_C[a][b][0][0] = a;
                AND_A_NOT_B_C[a][b][0][1] = b;
                AND_A_NOT_B_C[a][b][0][2] = a & (b == 0 ? 1 : 0);
                AND_A_NOT_B_C[a][b][1][0] = a;
                AND_A_NOT_B_C[a][b][1][1] = b;
                AND_A_NOT_B_C[a][b][1][2] = a & (b == 0 ? 1 : 0);
                
                C_AND_A_NOT_B[0][a][b][0] = a & (b == 0 ? 1 : 0);
                C_AND_A_NOT_B[0][a][b][1] = a;
                C_AND_A_NOT_B[0][a][b][2] = b;
                C_AND_A_NOT_B[1][a][b][0] = a & (b == 0 ? 1 : 0);
                C_AND_A_NOT_B[1][a][b][1] = a;
                C_AND_A_NOT_B[1][a][b][2] = b;                
            }
        }
    }
    
    private final int[] bytes = new int[1024 + 3 + 21];
    
    private void ascendMemoryCycle() {
        for (int address = 0; address <= 0x03FE; ++address) {
            runMemoryCycle(address);
            ascend(address);
        }
        runMemoryCycle(0x03FF);
    }
    
    private void descendMemoryCycle() {
        for (int address = 0x03FF; address > 0; --address) {
            runMemoryCycle(address);
            descend(address);
        }
        runMemoryCycle(0x0000);
    }    
    
    private void runMemoryCycle(final int address) {
       copyInstructionIfPEqualsA(address);
       readOrWriteMemoryIfMNEqualsA(address);
    }
    
    private void ascend(final int address) {       
        apply(address + 13, INC_16);
        apply(address + 23, SWAP);
        apply(address + 22, SWAP);
        apply(address + 21, SWAP);
        apply(address + 20, SWAP);
        apply(address + 19, SWAP);
        apply(address + 18, SWAP);
        apply(address + 17, SWAP);
        apply(address + 16, SWAP);
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);
        apply(address + 13, SWAP);
        apply(address + 12, SWAP);
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 9, SWAP);
        apply(address + 8, SWAP);
        apply(address + 7, SWAP);
        apply(address + 6, SWAP);
        apply(address + 5, SWAP);
        apply(address + 4, SWAP);
        apply(address + 3, SWAP);
    }
    
    private void descend(final int address) {
        apply(address + 13, DEC_16);
        apply(address + 3, SWAP);
        apply(address + 4, SWAP);
        apply(address + 5, SWAP);
        apply(address + 6, SWAP);
        apply(address + 7, SWAP);
        apply(address + 8, SWAP);
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 19, SWAP);
        apply(address + 20, SWAP);
        apply(address + 21, SWAP);
        apply(address + 22, SWAP);
        apply(address + 23, SWAP);
    }    
    
    private void copyInstructionIfPEqualsA(final int address) {
        apply(address + 12, SWAP);
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);        
        apply(address + 9, CMP_C);          // s1 = (P1 == a1);
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, SWAP);          // order restored
        
        apply(address + 13, SWAP);
        apply(address + 12, SWAP);
        apply(address + 11, SWAP);
        apply(address + 10, CMP_AND_C);     // s1 = (P1 == a1) & (P0 == a0);
        apply(address + 11, SWAP);
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);          // order restored
        
        apply(address + 10, SWAP);
        apply(address + 9, SWAP);
        apply(address + 8, SWAP);
        apply(address + 7, SWAP);
        apply(address + 6, SWAP);
        apply(address + 5, SWAP);
        apply(address + 4, SWAP);
        apply(address + 0, SWAP);
        apply(address + 1, SWAP);
        apply(address + 2, COPY_A_B_C);     // if (s1) i = I;
        apply(address + 1, SWAP);
        apply(address + 0, SWAP);
        apply(address + 4, SWAP);
        apply(address + 1, SWAP);
        apply(address + 2, SWAP);
        apply(address + 3, COPY_A_B_C);     // if (s1) j = J;
        apply(address + 2, SWAP);
        apply(address + 1, SWAP);
        apply(address + 2, SWAP);
        apply(address + 3, SWAP);
        apply(address + 5, SWAP);
        apply(address + 4, COPY_A_B_C);     // if (s1) k = K;
        apply(address + 3, SWAP);
        apply(address + 2, SWAP);
        apply(address + 6, SWAP);
        apply(address + 7, SWAP);
        apply(address + 8, SWAP);
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);          // order restored
    }    
    
    private void readOrWriteMemoryIfMNEqualsA(final int address) {          
        apply(address + 14, SWAP);
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);
        apply(address + 12, CMP_C);         // s0 = (M == a1);
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 14, CMP_AND_C);     // s0 = (M == a1) & (N == a0);
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);
        apply(address + 13, SWAP);
        apply(address + 12, SWAP);
        apply(address + 14, SWAP);          // order restored
        
        apply(address + 0, SWAP);
        apply(address + 1, SWAP);
        apply(address + 2, SWAP);
        apply(address + 3, SWAP);
        apply(address + 4, SWAP);
        apply(address + 11, COPY_B_A);      // s1 = s0;
        apply(address + 10, SWAP);
        apply(address + 9, SWAP);
        apply(address + 8, AND_B);          // s1 &= r;
        apply(address + 8, SWAP);
        apply(address + 7, SWAP);
        apply(address + 5, COPY_A_B_C);     // if (s1) m = I;
        apply(address + 7, SWAP);
        apply(address + 8, SWAP);
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, COPY_B_A);      // s1 = s0;
        apply(address + 10, SWAP);
        apply(address + 9, SWAP);
        apply(address + 8, SWAP);
        apply(address + 7, AND_B);          // s1 &= w;
        apply(address + 7, SWAP);
        apply(address + 5, COPY_B_A_C);     // if (s1) I = m;
        apply(address + 7, SWAP);
        apply(address + 8, SWAP);
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 4, SWAP);
        apply(address + 3, SWAP);
        apply(address + 2, SWAP);
        apply(address + 1, SWAP);
        apply(address + 0, SWAP);           // order restored
    }  
    
    private void finishLoad(final int address) {
        apply(address + 8, SWAP);
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);
        apply(address + 14, SWAP);
        apply(address + 15, C_AND_A_NOT_B); // s0 = r & !d;        
        apply(address + 6, SWAP);
        apply(address + 7, SWAP);
        apply(address + 8, SWAP);
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, C_COPY_A_B);    // if (s0) A = m;
        apply(address + 19, SWAP);
        apply(address + 17, SWAP);
        apply(address + 16, SWAP);
        apply(address + 14, AND_A_B_C);     // s0 = r & d;
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 19, C_COPY_A_B);    // if (s0) B = m;       
        apply(address + 20, SWAP);
        apply(address + 19, SWAP);
        apply(address + 18, SWAP);
        apply(address + 17, SWAP);
        apply(address + 16, SWAP);
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);
        apply(address + 13, SWAP);
        apply(address + 12, SWAP);
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 9, SWAP);
        apply(address + 8, SWAP);
        apply(address + 7, SWAP);
        apply(address + 6, SWAP);
        apply(address + 14, SWAP);
        apply(address + 13, SWAP);
        apply(address + 12, SWAP);
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 9, SWAP);
        apply(address + 8, SWAP);
        apply(address + 19, SWAP);
        apply(address + 18, SWAP);
        apply(address + 17, SWAP);
        apply(address + 16, SWAP);
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);
        apply(address + 13, SWAP);
        apply(address + 12, SWAP);          // order restored
    }
    
    private int read(final int index) {
        return bytes[index];
    }
    
    private void write(final int index, final int value) {
        bytes[index] = value;
    }
    
    private void apply(final int index, final int[] map) {
        bytes[index] = map[bytes[index]];
    }
    
    private void apply(final int index, final int[][][] map) {
        final int[] m = map[bytes[index]][bytes[index + 1]];
        bytes[index] = m[0];
        bytes[index + 1] = m[1];
    }

    private void apply(final int index, final int[][][][] map) {
        final int[] m = map[bytes[index]][bytes[index + 1]][bytes[index + 2]];
        bytes[index] = m[0];
        bytes[index + 1] = m[1];
        bytes[index + 2] = m[2];
    }
    
    private Simulator() {        
    }
    
    public void launch() {
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q
         
        write(6, 123);
        write(8, 0xFF);
        
        write(17, 1);
        
        finishLoad(0);
        
        System.out.println(read(6));
        
        
//        for (int i = 0; i < 3; ++i) {
//            ascendMemoryCycle();
//            descendMemoryCycle();
//        }
//        
//        long startTime = System.nanoTime();
//        for (int i = 0; i < 50_000; ++i) {
//            ascendMemoryCycle();
//            descendMemoryCycle();
//        }
//        long endTime = System.nanoTime();
//        System.out.format("%f%n", (endTime - startTime) / 1_000_000_000.0);
        
    }
    
    public static void main(final String... args) {
        new Simulator().launch();
    }    
}
