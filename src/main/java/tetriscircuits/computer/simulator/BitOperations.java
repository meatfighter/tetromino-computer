package tetriscircuits.computer.simulator;

// 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
// 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
// I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q

public final class BitOperations {
    
    public static final int[] SWAP = new int[0x10000];
    public static final int[] COPY_A_B = new int[0x10000];
    public static final int[] COPY_B_A = new int[0x10000];
    public static final int[] DEC_16 = new int[0x10000];
    public static final int[] INC_16 = new int[0x10000];
    public static final int[] AND_A = new int[0x10000];
    public static final int[] AND_B = new int[0x10000];
        
    public static final int[] C_CMP = new int[0x20000];
    public static final int[] CMP_C = new int[0x20000];
    public static final int[] C_CMP_AND = new int[0x20000];
    public static final int[] CMP_AND_C = new int[0x20000];    
    public static final int[] C_COPY_A_B = new int[0x20000];
    public static final int[] C_COPY_B_A = new int[0x20000];
    public static final int[] COPY_A_B_C = new int[0x20000];
    public static final int[] COPY_B_A_C = new int[0x20000];   
    
    static {
        for (int a = 0xFF; a >= 0; --a) {
            final int a8 = a << 8;
            for (int b = 0xFF; b >= 0; --b) {
                final int i = a8 | b;
                SWAP[i] = (b << 8) | a;
                COPY_A_B[i] = (a << 8) | a;
                COPY_B_A[i] = (b << 8) | b;
                DEC_16[i] = 0xFFFF & (i - 1);
                INC_16[i] = 0xFFFF & (i + 1);
                AND_A[i] = ((a & b) << 8) | b;
                AND_B[i] = (a << 8) | (a & b);
            }
        }
        
        for (int c = 1; c >= 0; --c) {
            for (int a = 0xFF; a >= 0; --a) {
                for (int b = 0xFF; b >= 0; --b) {                    
                    final int C_i = (c << 16) | (a << 8) | b;
                    final int i_C = (a << 9) | (b << 1) | c;
                    C_CMP[C_i] = (((a == b) ? 1 : 0) << 16) | (a << 8) | b;
                    CMP_C[i_C] = (a << 9) | (b << 1) | ((a == b) ? 1 : 0);
                    C_CMP_AND[C_i] = (((a == b) ? (i_C >> 16) : 0) << 16) | (a << 8) | b;
                    CMP_AND_C[i_C] = (a << 9) | (b << 1) | ((a == b) ? (i_C & 1) : 0);
                    C_COPY_A_B[C_i] = (c == 1) ? ((c << 16) | (a << 8) | a) : C_i;
                    C_COPY_B_A[C_i] = (c == 1) ? ((c << 16) | (b << 8) | b) : C_i;
                    COPY_A_B_C[i_C] = (c == 1) ? ((a << 9) | (a << 1) | c) : i_C;
                    COPY_B_A_C[i_C] = (c == 1) ? ((b << 9) | (b << 1) | c) : i_C;
                }
            }
        }
    }
    
    private final BitList bitList = new BitList((0x0400 + 3 + 21) << 3);
    
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
        apply(address, 13, INC_16);
        for (int i = 23; i >= 3; --i) {
            apply(address, i, SWAP);
        }
    }
    
    private void descend(final int address) {
        apply(address, 13, DEC_16);
        for (int i = 3; i <= 23; ++i) {
            apply(address, i, SWAP);
        }
    }    
    
    private void readOrWriteMemoryIfMNEqualsA(final int address) {          
        apply(address, 14, SWAP);
        apply(address, 12, SWAP);
        apply(address, 13, SWAP);
        apply(address, 12, CMP_C);     // s0.7 = (M == a1);
        apply(address, 14, SWAP);
        apply(address, 15, SWAP);
        apply(address, 14, CMP_AND_C); // s0.7 = (M == a1) & (N == a0);
        apply(address, 15, SWAP);
        apply(address, 14, SWAP);
        apply(address, 13, SWAP);
        apply(address, 12, SWAP);
        apply(address, 14, SWAP);      // order restored
        
        apply(address, 0, SWAP);
        apply(address, 1, SWAP);
        apply(address, 2, SWAP);
        apply(address, 3, SWAP);
        apply(address, 4, SWAP);
        apply(address, 11, COPY_B_A);  // s1 = s0;
        apply(address, 10, SWAP);
        apply(address, 9, SWAP);
        apply(address, 8, AND_B);      // s1 &= r;
        apply(address, 8, SWAP);
        apply(address, 7, SWAP);
        apply(address, 5, COPY_A_B_C); // if (s1.7) m = I;
        apply(address, 7, SWAP);
        apply(address, 8, SWAP);
        apply(address, 9, SWAP);
        apply(address, 10, SWAP);
        apply(address, 11, COPY_B_A);  // s1 = s0;
        apply(address, 10, SWAP);
        apply(address, 9, SWAP);
        apply(address, 8, SWAP);
        apply(address, 7, AND_B);      // s1 &= w;
        apply(address, 7, SWAP);
        apply(address, 5, COPY_B_A_C); // if (s1.7) I = m;
        apply(address, 7, SWAP);
        apply(address, 8, SWAP);
        apply(address, 9, SWAP);
        apply(address, 10, SWAP);
        apply(address, 4, SWAP);
        apply(address, 3, SWAP);
        apply(address, 2, SWAP);
        apply(address, 1, SWAP);
        apply(address, 0, SWAP);       // order restored
    }
    
    private void copyInstructionIfPEqualsA(final int address) {       
        apply(address, 12, SWAP);
        apply(address, 11, SWAP);
        apply(address, 10, SWAP);
        apply(address, 11, SWAP);        
        apply(address, 9, CMP_C);      // s1.7 = (P1 == a1);
        apply(address, 11, SWAP);
        apply(address, 10, SWAP);
        apply(address, 11, SWAP);
        apply(address, 12, SWAP);      // order restored
        
        apply(address, 13, SWAP);
        apply(address, 12, SWAP);
        apply(address, 11, SWAP);
        apply(address, 10, CMP_AND_C); // s1.7 = (P1 == a1) & (P0 == a0);
        apply(address, 11, SWAP);
        apply(address, 12, SWAP);
        apply(address, 13, SWAP);      // order restored
        
        apply(address, 10, SWAP);
        apply(address, 9, SWAP);
        apply(address, 8, SWAP);
        apply(address, 7, SWAP);
        apply(address, 6, SWAP);
        apply(address, 5, SWAP);
        apply(address, 4, SWAP);
        apply(address, 0, SWAP);
        apply(address, 1, SWAP);
        apply(address, 2, COPY_A_B_C); // if (s1.7) i = I;
        apply(address, 1, SWAP);
        apply(address, 0, SWAP);
        apply(address, 4, SWAP);
        apply(address, 1, SWAP);
        apply(address, 2, SWAP);
        apply(address, 3, COPY_A_B_C); // if (s1.7) j = J;
        apply(address, 2, SWAP);
        apply(address, 1, SWAP);
        apply(address, 2, SWAP);
        apply(address, 3, SWAP);
        apply(address, 5, SWAP);
        apply(address, 4, COPY_A_B_C); // if (s1.7) k = K;
        apply(address, 3, SWAP);
        apply(address, 2, SWAP);
        apply(address, 6, SWAP);
        apply(address, 7, SWAP);
        apply(address, 8, SWAP);
        apply(address, 9, SWAP);
        apply(address, 10, SWAP);      // order restored
    }
    
    private void apply(final int address, final int b, final int[] operation) {
        bitList.apply((address + b) << 3, operation.length == 0x10000 ? 16 : 17, operation);
    }    

    private BitOperations() {        
    }
    
    public void launch() {
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q
               
        bitList.write((0x0034 + 21) << 3, 8, 123);
        
        bitList.write(6 << 3, 8, 32);
        bitList.write(8 << 3, 8, 0xFF);
        
        bitList.write(15 << 3, 8, 0x00);
        bitList.write(16 << 3, 8, 0x34);
        
        for (int i = 0; i < 3; ++i) {
            ascendMemoryCycle();
        }
        
        long startTime = System.nanoTime();
        for (int i = 0; i < 10_000; ++i) {
            ascendMemoryCycle();
        }
        long endTime = System.nanoTime();
        System.out.format("%f%n", (endTime - startTime) / 1_000_000_000.0);
        
//        
//        System.out.println(bitList.read(0x1234 << 3, 8));
//        System.out.println(bitList.read((0xFFFF + 6) << 3, 8));
//        System.out.println(bitList.read((0xFFFF + 13) << 3, 8));
//        System.out.println(bitList.read((0xFFFF + 14) << 3, 8));
    }
    
    public static void main(final String... args) {
        new BitOperations().launch();
    }
}
