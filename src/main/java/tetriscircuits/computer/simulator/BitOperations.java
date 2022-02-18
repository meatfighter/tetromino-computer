package tetriscircuits.computer.simulator;

// I J K [i j k m w r P1 P0 s1 s0 a1 a0 M N A B R1 R0]

public final class BitOperations {
    
    public static final int[] SWAP = new int[0x10000];
    public static final int[] COPY_A_B = new int[0x10000];
    public static final int[] COPY_B_A = new int[0x10000];
    public static final int[] DEC_16 = new int[0x10000];
    public static final int[] INC_16 = new int[0x10000];
    public static final int[] CMP_A = new int[0x10000];
    public static final int[] CMP_B = new int[0x10000];
    public static final int[] AND_A = new int[0x10000];
    public static final int[] AND_B = new int[0x10000];
    
    static {
        for(int a = 0xFF; a >= 0; --a) {
            final int a8 = a << 8;
            for (int b = 0xFF; b >= 0; --b) {
                final int i = a8 | b;
                SWAP[i] = (b << 8) | a;
                COPY_A_B[i] = (a << 8) | a;
                COPY_B_A[i] = (b << 8) | b;
                DEC_16[i] = 0xFFFF & (i - 1);
                INC_16[i] = 0xFFFF & (i + 1);
                CMP_A[i] = (((a == b) ? 0xFF : 0x00) << 8) | b;
                CMP_B[i] = (a << 8) | ((a == b) ? 0xFF : 0x00);
                AND_A[i] = ((a & b) << 8) | b;
                AND_A[i] = (a << 8) | (a & b);
            }
        }
    }

    private BitOperations() {        
    }
}
