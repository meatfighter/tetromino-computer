package tetriscircuits.computer.simulator;

// 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
// 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
// I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q

public final class Simulator {
    
    private static final int[][][] ZERO_C = new int[256][2][2];
    private static final int[][][] C_ZERO = new int[2][256][2];
    private static final int[][][] MINUS_C = new int[256][2][2];
    private static final int[][][] C_MINUS = new int[2][256][2];
    
    private static final int[][][] T_A_X_C = new int[256][2][2];
    private static final int[][][] T_B_X_C = new int[256][2][2];
    private static final int[][][] T_M_X_C = new int[256][2][2];
    private static final int[][][] T_N_X_C = new int[256][2][2];
    
    private static final int[][][] T_X_A_C = new int[256][2][2];
    private static final int[][][] T_X_B_C = new int[256][2][2];
    private static final int[][][] T_X_M_C = new int[256][2][2];
    private static final int[][][] T_X_N_C = new int[256][2][2];    

    private static final int[][][] SWAP = new int[256][256][2];
    private static final int[][][] COPY_A_B = new int[256][256][2];
    private static final int[][][] COPY_B_A = new int[256][256][2];
    private static final int[][][] DEC_16 = new int[256][256][2];
    private static final int[][][] INC_16 = new int[256][256][2];
    private static final int[][][] AND_A = new int[256][256][2];
    private static final int[][][] AND_B = new int[256][256][2];

    private static final int[][][] ADD_AB_FB = new int[256][256][2];
    private static final int[][][] AND_AB_FB = new int[256][256][2];
    private static final int[][][] OR_AB_FB = new int[256][256][2];
    private static final int[][][] SUB_AB_FB = new int[256][256][2];
    private static final int[][][] XOR_AB_FB = new int[256][256][2];
    
    private static final int[] CLEAR = new int[256];
    private static final int[] DEC = new int[256];
    private static final int[] INC = new int[256];
    private static final int[] LSH = new int[256];
    private static final int[] NOT = new int[256];
    private static final int[] RSH = new int[256];
    
    private static final int[][][] ADD_C = new int[256][2][2];
    private static final int[][][] AND_C = new int[256][2][2];
    private static final int[][][] DEC_C = new int[256][2][2];
    private static final int[][][] INC_C = new int[256][2][2];
    private static final int[][][] LSH_C = new int[256][2][2];
    private static final int[][][] NOT_C = new int[256][2][2];
    private static final int[][][] OR_C = new int[256][2][2];
    private static final int[][][] RSH_C = new int[256][2][2];
    private static final int[][][] SUB_C = new int[256][2][2];
    private static final int[][][] XOR_C = new int[256][2][2];    
    
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
            
            ZERO_C[a][0][0] = a;
            ZERO_C[a][0][1] = (a == 0) ? 1 : 0;
            ZERO_C[a][1][0] = a;
            ZERO_C[a][1][1] = (a == 0) ? 1 : 0;  
            
            C_ZERO[0][a][0] = (a == 0) ? 1 : 0;
            C_ZERO[0][a][1] = a;
            C_ZERO[1][a][0] = (a == 0) ? 1 : 0;
            C_ZERO[1][a][1] = a;
            
            MINUS_C[a][0][0] = a;
            MINUS_C[a][0][1] = ((a & 0x80) != 0) ? 1 : 0;
            MINUS_C[a][1][0] = a;
            MINUS_C[a][1][1] = ((a & 0x80) != 0) ? 1 : 0;  
            
            C_MINUS[0][a][0] = ((a & 0x80) != 0) ? 1 : 0;
            C_MINUS[0][a][1] = a;
            C_MINUS[1][a][0] = ((a & 0x80) != 0) ? 1 : 0;
            C_MINUS[1][a][1] = a;      
            
            T_A_X_C[a][0][0] = a;
            T_A_X_C[a][0][1] = ((a & 0b1111_11_00) == 0b0000_00_00) ? 1 : 0;
            T_A_X_C[a][1][0] = a;
            T_A_X_C[a][1][1] = ((a & 0b1111_11_00) == 0b0000_00_00) ? 1 : 0;
            
            T_B_X_C[a][0][0] = a;
            T_B_X_C[a][0][1] = ((a & 0b1111_11_00) == 0b0000_01_00) ? 1 : 0;
            T_B_X_C[a][1][0] = a;
            T_B_X_C[a][1][1] = ((a & 0b1111_11_00) == 0b0000_01_00) ? 1 : 0;
            
            T_M_X_C[a][0][0] = a;
            T_M_X_C[a][0][1] = ((a & 0b1111_11_00) == 0b0000_10_00) ? 1 : 0;
            T_M_X_C[a][1][0] = a;
            T_M_X_C[a][1][1] = ((a & 0b1111_11_00) == 0b0000_10_00) ? 1 : 0;

            T_N_X_C[a][0][0] = a;
            T_N_X_C[a][0][1] = ((a & 0b1111_11_00) == 0b0000_11_00) ? 1 : 0;
            T_N_X_C[a][1][0] = a;
            T_N_X_C[a][1][1] = ((a & 0b1111_11_00) == 0b0000_11_00) ? 1 : 0; 
                        
            T_X_A_C[a][0][0] = a;
            T_X_A_C[a][0][1] = ((a & 0b1111_00_11) == 0b0000_00_00) ? 1 : 0;
            T_X_A_C[a][1][0] = a;
            T_X_A_C[a][1][1] = ((a & 0b1111_00_11) == 0b0000_00_00) ? 1 : 0;
            
            T_X_B_C[a][0][0] = a;
            T_X_B_C[a][0][1] = ((a & 0b1111_00_11) == 0b0000_00_01) ? 1 : 0;
            T_X_B_C[a][1][0] = a;
            T_X_B_C[a][1][1] = ((a & 0b1111_00_11) == 0b0000_00_01) ? 1 : 0;
            
            T_X_M_C[a][0][0] = a;
            T_X_M_C[a][0][1] = ((a & 0b1111_00_11) == 0b0000_00_10) ? 1 : 0;
            T_X_M_C[a][1][0] = a;
            T_X_M_C[a][1][1] = ((a & 0b1111_00_11) == 0b0000_00_10) ? 1 : 0;

            T_X_N_C[a][0][0] = a;
            T_X_N_C[a][0][1] = ((a & 0b1111_00_11) == 0b0000_00_11) ? 1 : 0;
            T_X_N_C[a][1][0] = a;
            T_X_N_C[a][1][1] = ((a & 0b1111_00_11) == 0b0000_00_11) ? 1 : 0; 
            
            ADD_C[a][0][0] = a;
            ADD_C[a][0][1] = (a == 0b0001_0010) ? 1 : 0;
            ADD_C[a][1][0] = a;
            ADD_C[a][1][1] = (a == 0b0001_0010) ? 1 : 0;
            
            AND_C[a][0][0] = a;
            AND_C[a][0][1] = (a == 0b0001_0110) ? 1 : 0;
            AND_C[a][1][0] = a;
            AND_C[a][1][1] = (a == 0b0001_0110) ? 1 : 0;
            
            DEC_C[a][0][0] = a;
            DEC_C[a][0][1] = (a == 0b0001_0001) ? 1 : 0;
            DEC_C[a][1][0] = a;
            DEC_C[a][1][1] = (a == 0b0001_0001) ? 1 : 0;
            
            INC_C[a][0][0] = a;
            INC_C[a][0][1] = (a == 0b0001_0000) ? 1 : 0;
            INC_C[a][1][0] = a;
            INC_C[a][1][1] = (a == 0b0001_0000) ? 1 : 0;            
            
            LSH_C[a][0][0] = a;
            LSH_C[a][0][1] = (a == 0b0001_0100) ? 1 : 0;
            LSH_C[a][1][0] = a;
            LSH_C[a][1][1] = (a == 0b0001_0100) ? 1 : 0;
            
            NOT_C[a][0][0] = a;
            NOT_C[a][0][1] = (a == 0b0001_1001) ? 1 : 0;
            NOT_C[a][1][0] = a;
            NOT_C[a][1][1] = (a == 0b0001_1001) ? 1 : 0;
            
            OR_C[a][0][0] = a;
            OR_C[a][0][1] = (a == 0b0001_0111) ? 1 : 0;
            OR_C[a][1][0] = a;
            OR_C[a][1][1] = (a == 0b0001_0111) ? 1 : 0;
            
            RSH_C[a][0][0] = a;
            RSH_C[a][0][1] = (a == 0b0001_0101) ? 1 : 0;
            RSH_C[a][1][0] = a;
            RSH_C[a][1][1] = (a == 0b0001_0101) ? 1 : 0; 
            
            SUB_C[a][0][0] = a;
            SUB_C[a][0][1] = (a == 0b0001_0111) ? 1 : 0;
            SUB_C[a][1][0] = a;
            SUB_C[a][1][1] = (a == 0b0001_0111) ? 1 : 0;
            
            XOR_C[a][0][0] = a;
            XOR_C[a][0][1] = (a == 0b0001_0101) ? 1 : 0;
            XOR_C[a][1][0] = a;
            XOR_C[a][1][1] = (a == 0b0001_0101) ? 1 : 0;            

            CLEAR[a] = 0;
            DEC[a] = 0xFF & (a - 1);
            INC[a] = 0xFF & (a + 1);
            LSH[a] = 0xFF & (a << 1);
            NOT[a] = (a == 0) ? 1 : 0;
            RSH[a] = 0xFF & (a >> 1);            
            
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

                ADD_AB_FB[a][b][0] = 0xFF & (a + b);
                ADD_AB_FB[a][b][1] = b;
                
                AND_AB_FB[a][b][0] = 0xFF & (a & b);
                AND_AB_FB[a][b][1] = b;
                
                OR_AB_FB[a][b][0] = 0xFF & (a | b);
                OR_AB_FB[a][b][1] = b;
                
                SUB_AB_FB[a][b][0] = 0xFF & (a - b);
                SUB_AB_FB[a][b][1] = b;
                
                XOR_AB_FB[a][b][0] = 0xFF & (a ^ b);
                XOR_AB_FB[a][b][1] = b;
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
        apply(address + 6, SWAP);
        apply(address + 7, SWAP);
        apply(address + 8, SWAP);
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, MINUS_C);       // s0 = (m < 0);
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 7, SWAP);
        apply(address + 8, SWAP);
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, C_COPY_A_B);    // if (r) n = s0;
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, ZERO_C);        // s0 = (m == 0);
        apply(address + 17, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 17, C_COPY_A_B);    // if (r) z = s0;
        apply(address + 17, SWAP);
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 16, C_AND_A_NOT_B); // s0 = r & !d;
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, C_COPY_A_B);    // if (s0) A = m;
        apply(address + 17, SWAP);
        apply(address + 15, AND_A_B_C);     // s0 = r & d;
        apply(address + 19, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 19, C_COPY_A_B);    // if (s0) B = m;
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
        apply(address + 15, SWAP);
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
        apply(address + 12, SWAP);           
        apply(address + 17, SWAP);          // order restored 
    }
    
    private void transfer(final int address) {       
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);
        apply(address + 14, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, SWAP);        
        apply(address + 3, SWAP);
        apply(address + 4, SWAP);
        apply(address + 5, SWAP);
        apply(address + 6, SWAP);
        apply(address + 7, SWAP);
        apply(address + 8, SWAP);
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, T_M_X_C);       // s1 = (I == TMx);
        apply(address + 13, C_COPY_A_B);    // if (s1) s0 = M;
        apply(address + 15, SWAP);
        apply(address + 13, SWAP);
        apply(address + 12, SWAP);
        apply(address + 13, T_N_X_C);       // s1 = (I == TNx);
        apply(address + 14, C_COPY_A_B);    // if (s1) s0 = N;
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 19, SWAP);        
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);        
        apply(address + 13, SWAP);
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 17, T_A_X_C);       // s1 = (I == TAx);
        apply(address + 18, C_COPY_A_B);    // if (s1) s0 = A;
        apply(address + 20, SWAP);
        apply(address + 18, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, T_B_X_C);       // s1 = (I == TBx);
        apply(address + 19, C_COPY_A_B);    // if (s1) s0 = B;
        apply(address + 18, T_X_B_C);       // s1 = (I == TxB);
        apply(address + 19, C_COPY_B_A);    // if (s1) B = s0;
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 20, SWAP);
        apply(address + 17, T_X_A_C);       // s1 = (I == TxA);
        apply(address + 18, C_COPY_B_A);    // if (s1) A = s0;        
        apply(address + 16, SWAP);
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);
        apply(address + 13, SWAP);        
        apply(address + 17, SWAP);
        apply(address + 16, SWAP);
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);
        apply(address + 19, SWAP);
        apply(address + 18, SWAP);
        apply(address + 17, SWAP);
        apply(address + 16, SWAP);
        apply(address + 13, T_X_N_C);       // s1 = (I == TxN);
        apply(address + 14, C_COPY_B_A);    // if (s1) N = s0;
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);
        apply(address + 15, SWAP);
        apply(address + 12, T_X_M_C);       // s1 = (I == TxM);
        apply(address + 13, C_COPY_B_A);    // if (s1) M = s0;        
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [j  k  m  w  r  P1 P0 a1 a0 i  s1 M  s0 N  d  n  z  A  B  R1 R0] Q        
    }
    
    private void runALU(final int address) {
        
        // execute after transfer()
        
        apply(address + 14, SWAP);
        apply(address + 16, SWAP);
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);
        apply(address + 19, SWAP);
        apply(address + 18, SWAP);
        apply(address + 17, SWAP);
        apply(address + 16, SWAP);        
        apply(address + 20, SWAP);
        apply(address + 19, SWAP); // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        apply(address + 18, SWAP); // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        apply(address + 17, SWAP); // I  J  K [j  k  m  w  r  P1 P0 a1 a0 i  s1 d  s0 A  B  M  N  n  z  R1 R0] Q
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [j  k  m  w  r  P1 P0 a1 a0 i  d  s1 s0 A  B  M  N  n  z  R1 R0] Q 
        
        apply(address + 14, CLEAR);         // d = 0;
        
        apply(address + 12, ADD_C);         // s1 = (i == ADD);
        apply(address + 13, SWAP);
        apply(address + 13, OR_AB_FB);      // d |= s1;
        apply(address + 13, COPY_B_A);      // s0 = A;
        
        
        
        // i s1 d s0 A B
        // i d s1 s0 A B
        // i d s1 A s0 B
        
//    DEC(0b0001_0001),
//    INC(0b0001_0000),
//    LSH(0b0001_0100),    
//    NOT(0b0001_1001),
//    RSH(0b0001_0101),
//    
//    d = 0;

//    loop:
//
//    s1 = test(i);
//    d |= s1;
//
//    s0 = A;
//    s0 = f(s0);
//    if (s1) A = s0;

//    ... repeat ...

//    s0 = minus(A);
//    if (d) n = s0;

//    s0 = zero(A);
//    if (d) z = s0;

//    ADD(0b0001_0010),    
//    AND(0b0001_0110),
//    OR(0b0001_0111),
//    SUB(0b0001_0011),
//    XOR(0b0001_1000),        

//    d = 0;

//    loop:
//
//    s1 = test(i);
//    d |= s1;
//
//    s0 = A;
//    s0 = f(s0, B); 
//    if (s1) A = s0;

//    ... repeat ...

//    s0 = minus(A);
//    if (d) n = s0;

//    s0 = zero(A);
//    if (d) z = s0; 
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
        
        // TODO:
        // apply(address + 9, INC_16);         // ++P;

//    DEC(0b0001_0001),
//    INC(0b0001_0000),
//    LSH(0b0001_0100),    
//    NOT(0b0001_1001),
//    RSH(0b0001_0101),
//    
//    d = 0;

//    loop:
//
//    s1 = test(i);
//    d |= s1;
//
//    so = A;
//    s0 = f(s0);
//    if (s1) A = s0;

//    ... repeat ...

//    s0 = minus(A);
//    if (d) n = s0;

//    s0 = zero(A);
//    if (d) z = s0;

//    ADD(0b0001_0010),    
//    AND(0b0001_0110),
//    OR(0b0001_0111),
//    SUB(0b0001_0011),
//    XOR(0b0001_1000),        

//    d = 0;

//    loop:
//
//    s1 = test(i);
//    d |= s1;
//
//    s0 = A;
//    s0 = f(s0, B); 
//    if (s1) A = s0;

//    ... repeat ...

//    s0 = minus(A);
//    if (d) n = s0;

//    s0 = zero(A);
//    if (d) z = s0;    
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [j  k  m  w  r  P1 P0 a1 a0 i  s1 M  s0 N  d  n  z  A  B  R1 R0] Q 
                 
//        write(20, 1); // A
//        write(21, 2); // B
//        write(15, 3); // M
//        write(16, 4); // N
//        
//        write(3, 0b0000_11_10);
//        
//        transfer(0);
//        
//        System.out.println(read(20));
//        System.out.println(read(21));
//        System.out.println(read(14));
//        System.out.println(read(16));
        
        
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

        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q

        write(6, 0x3F);
        write(8, 0);
        write(17, 0);
        write(18, 1);
        write(19, 1);
        write(20, 11);
        write(21, 22);  
        
        System.out.println("m = " + read(6));
        System.out.println("r = " + read(8));
        System.out.println("d = " + read(17));
        System.out.println("n = " + read(18));
        System.out.println("z = " + read(19));
        System.out.println("A = " + read(20));
        System.out.println("B = " + read(21));        
        
        finishLoad(0);
        System.out.println();
        
        System.out.println("m = " + read(6));
        System.out.println("r = " + read(8));
        System.out.println("d = " + read(17));
        System.out.println("n = " + read(18));
        System.out.println("z = " + read(19));
        System.out.println("A = " + read(20));
        System.out.println("B = " + read(21));
    }
    
    public static void main(final String... args) {
        new Simulator().launch();
    }    
}
