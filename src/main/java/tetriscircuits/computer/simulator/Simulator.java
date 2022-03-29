package tetriscircuits.computer.simulator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import tetriscircuits.computer.Processor;

// 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
// 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
// I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q

public final class Simulator implements Processor {
    
    private static final int[][][] ZERO_C = new int[256][2][2];
    private static final int[][][] C_ZERO = new int[2][256][2];
    private static final int[][][] MINUS_C = new int[256][2][2];
    private static final int[][][] C_MINUS = new int[2][256][2];
    
    private static final int[][][] TAX_C = new int[256][2][2];
    private static final int[][][] TBX_C = new int[256][2][2];
    private static final int[][][] TMX_C = new int[256][2][2];
    private static final int[][][] TNX_C = new int[256][2][2];
    
    private static final int[][][] TXA_C = new int[256][2][2];
    private static final int[][][] TXB_C = new int[256][2][2];
    private static final int[][][] TXM_C = new int[256][2][2];
    private static final int[][][] TXN_C = new int[256][2][2];    

    private static final int[][][] SWAP = new int[256][256][2];
    private static final int[][][] COPY_A_B = new int[256][256][2];
    private static final int[][][] COPY_B_A = new int[256][256][2];
    private static final int[][][] DEC_16 = new int[256][256][2];
    private static final int[][][] INC_16 = new int[256][256][2];
    
    private static final int[][][] ADD_AB_FB = new int[256][256][2];
    private static final int[][][] AND_AB_AF = new int[256][256][2];
    private static final int[][][] AND_AB_FB = new int[256][256][2];
    private static final int[][][] AND_NOT_AB_FB = new int[256][256][2];
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

    private static final int[][][] SEX_C = new int[256][2][2];
    private static final int[][][] THREE_C = new int[256][2][2];
    
    private static final int[][][] SEA_C = new int[256][2][2];
    private static final int[][][] SEB_C = new int[256][2][2];
    private static final int[][][] SMN_C = new int[256][2][2];
    private static final int[][][] JMP_C = new int[256][2][2];
    private static final int[][][] BEQ_C = new int[256][2][2];
    private static final int[][][] BMI_C = new int[256][2][2];
    private static final int[][][] BNE_C = new int[256][2][2];
    private static final int[][][] BPL_C = new int[256][2][2];
    private static final int[][][] JSR_C = new int[256][2][2];
    private static final int[][][] RTS_C = new int[256][2][2];

    private static final int[][][] STA_C = new int[256][2][2];
    private static final int[][][] STB_C = new int[256][2][2];
    private static final int[][][] STX_C = new int[256][2][2];
    private static final int[][][] LDX_C = new int[256][2][2];
    private static final int[][][] LDB_C = new int[256][2][2];
    
    private static final int[][][][] C_CMP = new int[2][256][256][3];
    private static final int[][][][] CMP_C = new int[256][256][2][3];
    private static final int[][][][] C_CMP_AND = new int[2][256][256][3];
    private static final int[][][][] CMP_AND_C = new int[256][256][2][3];
    private static final int[][][][] C_COPY_A_B = new int[2][256][256][3];
    private static final int[][][][] C_COPY_B_A = new int[2][256][256][3];
    private static final int[][][][] COPY_A_B_C = new int[256][256][2][3];
    private static final int[][][][] COPY_B_A_C = new int[256][256][2][3];
    private static final int[][][][] AND_A_B_C = new int[256][256][2][3];
    private static final int[][][][] C_AND_A_NOT_B = new int[2][256][256][3]; 
    
    private static final int[][][][] INC_16_C = new int[256][256][2][3];
    
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
            
            TAX_C[a][0][0] = a;
            TAX_C[a][0][1] = ((a & 0b1111_11_00) == 0b0000_00_00) ? 1 : 0;
            TAX_C[a][1][0] = a;
            TAX_C[a][1][1] = ((a & 0b1111_11_00) == 0b0000_00_00) ? 1 : 0;
            
            TBX_C[a][0][0] = a;
            TBX_C[a][0][1] = ((a & 0b1111_11_00) == 0b0000_01_00) ? 1 : 0;
            TBX_C[a][1][0] = a;
            TBX_C[a][1][1] = ((a & 0b1111_11_00) == 0b0000_01_00) ? 1 : 0;
            
            TMX_C[a][0][0] = a;
            TMX_C[a][0][1] = ((a & 0b1111_11_00) == 0b0000_10_00) ? 1 : 0;
            TMX_C[a][1][0] = a;
            TMX_C[a][1][1] = ((a & 0b1111_11_00) == 0b0000_10_00) ? 1 : 0;

            TNX_C[a][0][0] = a;
            TNX_C[a][0][1] = ((a & 0b1111_11_00) == 0b0000_11_00) ? 1 : 0;
            TNX_C[a][1][0] = a;
            TNX_C[a][1][1] = ((a & 0b1111_11_00) == 0b0000_11_00) ? 1 : 0; 
                        
            TXA_C[a][0][0] = a;
            TXA_C[a][0][1] = ((a & 0b1111_00_11) == 0b0000_00_00) ? 1 : 0;
            TXA_C[a][1][0] = a;
            TXA_C[a][1][1] = ((a & 0b1111_00_11) == 0b0000_00_00) ? 1 : 0;
            
            TXB_C[a][0][0] = a;
            TXB_C[a][0][1] = ((a & 0b1111_00_11) == 0b0000_00_01) ? 1 : 0;
            TXB_C[a][1][0] = a;
            TXB_C[a][1][1] = ((a & 0b1111_00_11) == 0b0000_00_01) ? 1 : 0;
            
            TXM_C[a][0][0] = a;
            TXM_C[a][0][1] = ((a & 0b1111_00_11) == 0b0000_00_10) ? 1 : 0;
            TXM_C[a][1][0] = a;
            TXM_C[a][1][1] = ((a & 0b1111_00_11) == 0b0000_00_10) ? 1 : 0;

            TXN_C[a][0][0] = a;
            TXN_C[a][0][1] = ((a & 0b1111_00_11) == 0b0000_00_11) ? 1 : 0;
            TXN_C[a][1][0] = a;
            TXN_C[a][1][1] = ((a & 0b1111_00_11) == 0b0000_00_11) ? 1 : 0; 
            
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
            SUB_C[a][0][1] = (a == 0b0001_0011) ? 1 : 0;
            SUB_C[a][1][0] = a;
            SUB_C[a][1][1] = (a == 0b0001_0011) ? 1 : 0;
            
            XOR_C[a][0][0] = a;
            XOR_C[a][0][1] = (a == 0b0001_1000) ? 1 : 0;
            XOR_C[a][1][0] = a;
            XOR_C[a][1][1] = (a == 0b0001_1000) ? 1 : 0;

            SEX_C[a][0][0] = a;
            SEX_C[a][0][1] = ((a & 0b1111_1110) == 0b0101_0000) ? 1 : 0;
            SEX_C[a][1][0] = a;
            SEX_C[a][1][1] = ((a & 0b1111_1110) == 0b0101_0000) ? 1 : 0;
            
            THREE_C[a][0][0] = a;
            THREE_C[a][0][1] = ((a & 0b1111_0000) == 0b0010_0000) ? 1 : 0;
            THREE_C[a][1][0] = a;
            THREE_C[a][1][1] = ((a & 0b1111_0000) == 0b0010_0000) ? 1 : 0;
            
            SEA_C[a][0][0] = a;
            SEA_C[a][0][1] = (a == 0b0101_0000) ? 1 : 0;
            SEA_C[a][1][0] = a;
            SEA_C[a][1][1] = (a == 0b0101_0000) ? 1 : 0;

            SEB_C[a][0][0] = a;
            SEB_C[a][0][1] = (a == 0b0101_0001) ? 1 : 0;
            SEB_C[a][1][0] = a;
            SEB_C[a][1][1] = (a == 0b0101_0001) ? 1 : 0;

            SMN_C[a][0][0] = a;
            SMN_C[a][0][1] = (a == 0b0010_1111) ? 1 : 0;
            SMN_C[a][1][0] = a;
            SMN_C[a][1][1] = (a == 0b0010_1111) ? 1 : 0; 
            
            JMP_C[a][0][0] = a;
            JMP_C[a][0][1] = (a == 0b0010_0000) ? 1 : 0;
            JMP_C[a][1][0] = a;
            JMP_C[a][1][1] = (a == 0b0010_0000) ? 1 : 0; 
            
            BEQ_C[a][0][0] = a;
            BEQ_C[a][0][1] = (a == 0b0010_0011) ? 1 : 0;
            BEQ_C[a][1][0] = a;
            BEQ_C[a][1][1] = (a == 0b0010_0011) ? 1 : 0; 
            
            BMI_C[a][0][0] = a;            
            BMI_C[a][0][1] = (a == 0b0010_0101) ? 1 : 0;
            BMI_C[a][1][0] = a;
            BMI_C[a][1][1] = (a == 0b0010_0101) ? 1 : 0; 
            
            BNE_C[a][0][0] = a;            
            BNE_C[a][0][1] = (a == 0b0010_0010) ? 1 : 0;
            BNE_C[a][1][0] = a;
            BNE_C[a][1][1] = (a == 0b0010_0010) ? 1 : 0;
            
            BPL_C[a][0][0] = a;            
            BPL_C[a][0][1] = (a == 0b0010_0100) ? 1 : 0;
            BPL_C[a][1][0] = a;
            BPL_C[a][1][1] = (a == 0b0010_0100) ? 1 : 0;
            
            JSR_C[a][0][0] = a;            
            JSR_C[a][0][1] = (a == 0b0010_1000) ? 1 : 0;
            JSR_C[a][1][0] = a;
            JSR_C[a][1][1] = (a == 0b0010_1000) ? 1 : 0; 
            
            RTS_C[a][0][0] = a;
            RTS_C[a][0][1] = (a == 0b0111_0000) ? 1 : 0;
            RTS_C[a][1][0] = a;
            RTS_C[a][1][1] = (a == 0b0111_0000) ? 1 : 0; 

            STA_C[a][0][0] = a;
            STA_C[a][0][1] = (a == 0b0011_0000) ? 1 : 0;
            STA_C[a][1][0] = a;
            STA_C[a][1][1] = (a == 0b0011_0000) ? 1 : 0;
            
            STB_C[a][0][0] = a;
            STB_C[a][0][1] = (a == 0b0011_0001) ? 1 : 0;
            STB_C[a][1][0] = a;
            STB_C[a][1][1] = (a == 0b0011_0001) ? 1 : 0;

            STX_C[a][0][0] = a;
            STX_C[a][0][1] = ((a & 0b1111_1110) == 0b0011_0000) ? 1 : 0;
            STX_C[a][1][0] = a;
            STX_C[a][1][1] = ((a & 0b1111_1110) == 0b0011_0000) ? 1 : 0; 
            
            LDX_C[a][0][0] = a;
            LDX_C[a][0][1] = ((a & 0b1111_1110) == 0b0100_0000) ? 1 : 0;
            LDX_C[a][1][0] = a;
            LDX_C[a][1][1] = ((a & 0b1111_1110) == 0b0100_0000) ? 1 : 0;            
            
            LDB_C[a][0][0] = a;
            LDB_C[a][0][1] = (a == 0b0100_0001) ? 1 : 0;
            LDB_C[a][1][0] = a;
            LDB_C[a][1][1] = (a == 0b0100_0001) ? 1 : 0;            

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
                AND_A_B_C[a][b][0][2] = a & b & 1;
                AND_A_B_C[a][b][1][0] = a;
                AND_A_B_C[a][b][1][1] = b;
                AND_A_B_C[a][b][1][2] = a & b & 1;
                
                C_AND_A_NOT_B[0][a][b][0] = a & ~b & 1;
                C_AND_A_NOT_B[0][a][b][1] = a;
                C_AND_A_NOT_B[0][a][b][2] = b;
                C_AND_A_NOT_B[1][a][b][0] = a & ~b & 1;
                C_AND_A_NOT_B[1][a][b][1] = a;
                C_AND_A_NOT_B[1][a][b][2] = b;
                                  
                INC_16_C[a][b][0][0] = a;
                INC_16_C[a][b][0][1] = b;
                INC_16_C[a][b][0][2] = 0;
                INC_16_C[a][b][1][0] = 0xFF & ((((a << 8) | b) + 1) >> 8);
                INC_16_C[a][b][1][1] = 0xFF & (((a << 8) | b) + 1);
                INC_16_C[a][b][1][2] = 1;

                ADD_AB_FB[a][b][0] = 0xFF & (a + b);
                ADD_AB_FB[a][b][1] = b;
                
                AND_AB_AF[a][b][0] = a;
                AND_AB_AF[a][b][1] = 0xFF & (a & b);                
                
                AND_AB_FB[a][b][0] = 0xFF & (a & b);
                AND_AB_FB[a][b][1] = b;
                
                AND_NOT_AB_FB[a][b][0] = 0xFF & (a & (b == 0 ? 1 : 0));
                AND_NOT_AB_FB[a][b][1] = b;                
                
                OR_AB_FB[a][b][0] = 0xFF & (a | b);
                OR_AB_FB[a][b][1] = b;
                
                SUB_AB_FB[a][b][0] = 0xFF & (a - b);
                SUB_AB_FB[a][b][1] = b;
                
                XOR_AB_FB[a][b][0] = 0xFF & (a ^ b);
                XOR_AB_FB[a][b][1] = b;
            }
        }
    }
    
    private int[] bytes;
    private int maxAddress;
    private boolean descend = true;
    
    private void ascendMemoryCycle() {
        for (int address = 0; address < maxAddress; ++address) {
            runMemoryCycle(address);
            ascend(address);
        }
        runMemoryCycle(maxAddress);
    }
    
    private void descendMemoryCycle() {
        for (int address = maxAddress; address > 0; --address) {
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
        apply(address + 2, SWAP);
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
        apply(address + 8, AND_AB_AF);      // s1 &= r;
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
        apply(address + 7, AND_AB_AF);      // s1 &= w;
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
        apply(address + 8, INC_16);         // ++P;
        apply(address + 12, SEX_C);         // s1 = (i == SEx);
        apply(address + 12, SWAP);
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 8, INC_16_C);       // if (s1) ++P;
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, SWAP);
        apply(address + 12, THREE_C);       // s1 = (i == [3 byte instruction]);
        apply(address + 12, SWAP);
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 8, INC_16_C);       // if (s1) ++P;
        apply(address + 8, INC_16_C);       // if (s1) ++P;
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, SWAP);        
        apply(address + 12, TMX_C);       // s1 = (i == TMx);
        apply(address + 13, C_COPY_A_B);    // if (s1) s0 = M;
        apply(address + 15, SWAP);
        apply(address + 13, SWAP);
        apply(address + 12, SWAP);
        apply(address + 13, TNX_C);       // s1 = (i == TNx);
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
        apply(address + 17, TAX_C);       // s1 = (i == TAx);
        apply(address + 18, C_COPY_A_B);    // if (s1) s0 = A;
        apply(address + 20, SWAP);
        apply(address + 18, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, TBX_C);       // s1 = (i == TBx);
        apply(address + 19, C_COPY_A_B);    // if (s1) s0 = B;
        apply(address + 18, TXB_C);       // s1 = (i == TxB);
        apply(address + 19, C_COPY_B_A);    // if (s1) B = s0;
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 20, SWAP);
        apply(address + 17, TXA_C);       // s1 = (i == TxA);
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
        apply(address + 13, TXN_C);       // s1 = (i == TxN);
        apply(address + 14, C_COPY_B_A);    // if (s1) N = s0;
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);
        apply(address + 15, SWAP);
        apply(address + 12, TXM_C);       // s1 = (i == TxM);
        apply(address + 13, C_COPY_B_A);    // if (s1) M = s0;        
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [j  k  m  w  r  P1 P0 a1 a0 i  s1 M  s0 N  d  n  z  A  B  R1 R0] Q  
        
        
//        System.out.println("transfer:");
//        System.out.format("i = %02X, j = %02X, k = %02X, m = %02X, w = %02X, r = %02X%n",
//                readReg(12), readReg(3), readReg(4), readReg(5), readReg(6), readReg(7));
//        System.out.format("P1 = %02X, P0 = %02X, s1 = %02X, s0 = %02X, a1 = %02X, a0 = %02X%n",
//                readReg(8), readReg(9), readReg(13), readReg(15), readReg(10), readReg(11));
//        System.out.format("M = %02X, N = %02X, d = %02X, n = %02X, z = %02X%n",
//                readReg(14), readReg(16), readReg(17), readReg(18), readReg(19));
//        System.out.format("A = %02X, B = %02X, R1 = %02X, R0 = %02X%n",
//                readReg(20), readReg(21), readReg(22), readReg(23));        
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
        
        apply(address + 14, CLEAR);         // d = 0;      
        
        apply(address + 12, ADD_C);         // s1 = (i == ADD);
        apply(address + 13, SWAP);
        apply(address + 13, OR_AB_FB);      // d |= s1;
        apply(address + 15, COPY_B_A);      // s0 = A;
        apply(address + 15, SWAP);
        apply(address + 16, ADD_AB_FB);     // s0 += B;
        apply(address + 15, SWAP);
        apply(address + 14, C_COPY_A_B);    // if (s1) A = s0;
        apply(address + 13, SWAP);
        
        apply(address + 12, AND_C);         // s1 = (i == AND);
        apply(address + 13, SWAP);
        apply(address + 13, OR_AB_FB);      // d |= s1;
        apply(address + 15, COPY_B_A);      // s0 = A;
        apply(address + 15, SWAP);
        apply(address + 16, AND_AB_FB);     // s0 &= B;
        apply(address + 15, SWAP);
        apply(address + 14, C_COPY_A_B);    // if (s1) A = s0;
        apply(address + 13, SWAP);
        
        apply(address + 12, DEC_C);         // s1 = (i == DEC);
        apply(address + 13, SWAP);
        apply(address + 13, OR_AB_FB);      // d |= s1;
        apply(address + 15, COPY_B_A);      // s0 = A;
        apply(address + 15, DEC);           // --s0;
        apply(address + 14, C_COPY_A_B);    // if (s1) A = s0;
        apply(address + 13, SWAP);        

        apply(address + 12, INC_C);         // s1 = (i == INC);
        apply(address + 13, SWAP);
        apply(address + 13, OR_AB_FB);      // d |= s1;
        apply(address + 15, COPY_B_A);      // s0 = A;
        apply(address + 15, INC);           // ++s0;
        apply(address + 14, C_COPY_A_B);    // if (s1) A = s0;
        apply(address + 13, SWAP);

        apply(address + 12, LSH_C);         // s1 = (i == LSH);
        apply(address + 13, SWAP);
        apply(address + 13, OR_AB_FB);      // d |= s1;
        apply(address + 15, COPY_B_A);      // s0 = A;
        apply(address + 15, LSH);           // s0 <<= 1;
        apply(address + 14, C_COPY_A_B);    // if (s1) A = s0;
        apply(address + 13, SWAP);

        apply(address + 12, NOT_C);         // s1 = (i == NOT);
        apply(address + 13, SWAP);
        apply(address + 13, OR_AB_FB);      // d |= s1;
        apply(address + 15, COPY_B_A);      // s0 = A;
        apply(address + 15, NOT);           // s0 = ~s0;
        apply(address + 14, C_COPY_A_B);    // if (s1) A = s0;
        apply(address + 13, SWAP); 
        
        apply(address + 12, OR_C);          // s1 = (i == OR);
        apply(address + 13, SWAP);
        apply(address + 13, OR_AB_FB);      // d |= s1;
        apply(address + 15, COPY_B_A);      // s0 = A;
        apply(address + 15, SWAP);
        apply(address + 16, OR_AB_FB);      // s0 |= B;
        apply(address + 15, SWAP);
        apply(address + 14, C_COPY_A_B);    // if (s1) A = s0;
        apply(address + 13, SWAP);

        apply(address + 12, RSH_C);         // s1 = (i == RSH);
        apply(address + 13, SWAP);
        apply(address + 13, OR_AB_FB);      // d |= s1;
        apply(address + 15, COPY_B_A);      // s0 = A;
        apply(address + 15, RSH);           // s0 >>= 1;
        apply(address + 14, C_COPY_A_B);    // if (s1) A = s0;
        apply(address + 13, SWAP);

        apply(address + 12, SUB_C);         // s1 = (i == SUB);
        apply(address + 13, SWAP);
        apply(address + 13, OR_AB_FB);      // d |= s1;
        apply(address + 15, COPY_B_A);      // s0 = A;
        apply(address + 15, SWAP);
        apply(address + 16, SUB_AB_FB);     // s0 -= B;
        apply(address + 15, SWAP);
        apply(address + 14, C_COPY_A_B);    // if (s1) A = s0;
        apply(address + 13, SWAP);

        apply(address + 12, XOR_C);         // s1 = (i == XOR);
        apply(address + 13, SWAP);
        apply(address + 13, OR_AB_FB);      // d |= s1;
        apply(address + 15, COPY_B_A);      // s0 = A;
        apply(address + 15, SWAP);
        apply(address + 16, XOR_AB_FB);     // s0 ^= B;
        apply(address + 15, SWAP);
        apply(address + 14, C_COPY_A_B);    // if (s1) A = s0;
        apply(address + 13, SWAP);
        
        apply(address + 15, CLEAR);         // so = 0; since it is not a flag
        
        apply(address + 15, C_MINUS);       // so = (A < 0);
        apply(address + 19, SWAP);
        apply(address + 18, SWAP);
        apply(address + 17, SWAP);
        apply(address + 16, SWAP);
        apply(address + 14, C_COPY_A_B);    // if (d) n = s0;
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 19, SWAP);
        apply(address + 15, C_ZERO);        // so = (A == 0);
        apply(address + 20, SWAP);
        apply(address + 19, SWAP);
        apply(address + 18, SWAP);
        apply(address + 17, SWAP);
        apply(address + 16, SWAP);
        apply(address + 14, C_COPY_A_B);    // if (d) z = s0;
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 19, SWAP);
        apply(address + 20, SWAP);        
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [j  k  m  w  r  P1 P0 a1 a0 i  s1 d  s0 A  B  M  N  n  z  R1 R0] Q
        
//        System.out.println("runALU:");
//        System.out.format("i = %02X, j = %02X, k = %02X, m = %02X, w = %02X, r = %02X%n",
//                readReg(12), readReg(3), readReg(4), readReg(5), readReg(6), readReg(7));
//        System.out.format("P1 = %02X, P0 = %02X, s1 = %02X, s0 = %02X, a1 = %02X, a0 = %02X%n",
//                readReg(8), readReg(9), readReg(13), readReg(15), readReg(10), readReg(11));
//        System.out.format("M = %02X, N = %02X, d = %02X, n = %02X, z = %02X%n",
//                readReg(18), readReg(19), readReg(14), readReg(20), readReg(21));
//        System.out.format("A = %02X, B = %02X, R1 = %02X, R0 = %02X%n",
//                readReg(16), readReg(17), readReg(22), readReg(23));
    }
    
    private void setAndJump(final int address) {
        
        // execute after runALU()
                      
        apply(address + 12, SEA_C);         // s1 = (i == SEA);
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);
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
        apply(address + 12, C_COPY_A_B);    // if (s1) A = j;
        apply(address + 16, SWAP);
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);
        apply(address + 11, SEB_C);         // s1 = (i == SEB);
        apply(address + 12, C_COPY_A_B);    // if (s1) B = j;
        apply(address + 17, SWAP);
        apply(address + 16, SWAP);
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);        
        apply(address + 11, SMN_C);         // s1 = (i == SMN);
        apply(address + 12, C_COPY_A_B);    // if (s1) M = j;
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [k  m  w  r  P1 P0 a1 a0 i  s1 j  M  B  A  d  s0 N  n  z  R1 R0] Q
        
        apply(address + 3, SWAP);
        apply(address + 4, SWAP);
        apply(address + 5, SWAP);
        apply(address + 6, SWAP);
        apply(address + 7, SWAP);
        apply(address + 8, SWAP);
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        
        apply(address + 18, SWAP);
        apply(address + 17, SWAP);
        apply(address + 16, SWAP);
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);
        apply(address + 13, SWAP);
        
        apply(address + 11, C_COPY_A_B);    // if (s1) N = k;        

        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [m  w  r  P1 P0 a1 a0 i  s1 k  N  j  M  B  A  d  s0 n  z  R1 R0] Q
               
        apply(address + 7, SWAP);
        apply(address + 8, SWAP);
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        
        apply(address + 9, JMP_C);          // s1 = (i == JMP);
        apply(address + 10, C_COPY_A_B);    // if (s1) P0 = k;
        
        apply(address + 13, SWAP);
        apply(address + 12, SWAP);
        apply(address + 11, SWAP);
        
        apply(address + 6, SWAP);
        apply(address + 7, SWAP);
        apply(address + 8, SWAP);
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        
        apply(address + 9, C_COPY_A_B);     // if (s1) P1 = j;
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [m  w  r  a1 a0 i  s1 j  P1 k  P0 N  M  B  A  d  s0 n  z  R1 R0] Q 
        
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
        apply(address + 8, BEQ_C);          // s1 = (i == BEQ);
        apply(address + 9, AND_AB_FB);      // s1 &= z;
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 9, C_COPY_A_B);     // if (s1) P1 = j;
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, C_COPY_A_B);    // if (s1) P0 = k;
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 9, SWAP);
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 8, BNE_C);          // s1 = (i == BNE);
        apply(address + 9, AND_NOT_AB_FB);  // s1 &= !z;
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 9, C_COPY_A_B);     // if (s1) P1 = j;
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, C_COPY_A_B);    // if (s1) P0 = k;
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 9, SWAP);        
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 19, SWAP);
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [m  w  r  a1 a0 i  s1 j  P1 k  P0 N  M  B  A  d  s0 z  n  R1 R0] Q

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
        apply(address + 8, BMI_C);          // s1 = (i == BMI);
        apply(address + 9, AND_AB_FB);      // s1 &= n;
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 9, C_COPY_A_B);     // if (s1) P1 = j;
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, C_COPY_A_B);    // if (s1) P0 = k;
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 9, SWAP);
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 8, BPL_C);          // s1 = (i == BPL);
        apply(address + 9, AND_NOT_AB_FB);  // s1 &= !n;
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 9, C_COPY_A_B);     // if (s1) P1 = j;
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, C_COPY_A_B);    // if (s1) P0 = k;
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 9, SWAP);
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 19, SWAP);
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [m  w  r  a1 a0 i  s1 j  P1 k  P0 N  M  B  A  d  s0 n  z  R1 R0] Q 

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
        apply(address + 22, SWAP);
        apply(address + 21, SWAP);
        apply(address + 20, SWAP);
        apply(address + 19, SWAP);
        apply(address + 18, SWAP);
        apply(address + 17, SWAP);
        apply(address + 16, SWAP);
        apply(address + 15, SWAP);        
        apply(address + 8, JSR_C);          // s1 = (i == JSR);       
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, SWAP);
        apply(address + 13, C_COPY_A_B);    // if (s1) R0 = P0;
        apply(address + 12, SWAP);
        apply(address + 12, C_COPY_A_B);    // if (s1) P0 = k;
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 10, C_COPY_A_B);    // if (s1) R1 = P1;
        apply(address + 9, SWAP);
        apply(address + 9, C_COPY_A_B);     // if (s1) P1 = j;
                
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [m  w  r  a1 a0 i  s1 j  P1 R1 k  P0 R0 N  M  B  A  d  s0 n  z]  Q        
        
        apply(address + 8, RTS_C);          // s1 = (i == RTS); 
        apply(address + 9, SWAP);
        apply(address + 10, C_COPY_B_A);    // if (s1) P1 = R1;
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, SWAP);
        apply(address + 13, C_COPY_B_A);    // if (s1) P0 = R0;
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [m  w  r  a1 a0 i  j  P1 R1 k  s1 P0 R0 N  M  B  A  d  s0 n  z]  Q
        
        apply(address + 8, SWAP);
        apply(address + 7, SWAP);
        apply(address + 6, SWAP);
        apply(address + 5, SWAP);
        apply(address + 4, SWAP);
        apply(address + 3, SWAP);
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 9, SWAP); 
        apply(address + 8, SWAP);
        apply(address + 7, SWAP);
        apply(address + 6, SWAP);
        apply(address + 5, SWAP);
        apply(address + 4, SWAP);
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
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 19, SWAP);
        apply(address + 20, SWAP);
        apply(address + 21, SWAP);        
        apply(address + 22, SWAP);
        apply(address + 10, SWAP);
        apply(address + 9, SWAP);
        apply(address + 8, SWAP);        
        apply(address + 12, SWAP);
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 9, SWAP);
        apply(address + 14, SWAP);
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [j  k  m  w  r  P1 P0 a1 a0 i  s1 M  N  B  A  d  s0 n  z  R1 R0] Q 
        
//        System.out.println("setAndJump:");
//        System.out.format("i = %02X, j = %02X, k = %02X, m = %02X, w = %02X, r = %02X%n",
//                readReg(12), readReg(3), readReg(4), readReg(5), readReg(6), readReg(7));
//        System.out.format("P1 = %02X, P0 = %02X, s1 = %02X, s0 = %02X, a1 = %02X, a0 = %02X%n",
//                readReg(8), readReg(9), readReg(13), readReg(19), readReg(10), readReg(11));
//        System.out.format("M = %02X, N = %02X, d = %02X, n = %02X, z = %02X%n",
//                readReg(14), readReg(15), readReg(18), readReg(20), readReg(21));
//        System.out.format("A = %02X, B = %02X, R1 = %02X, R0 = %02X%n",
//                readReg(17), readReg(16), readReg(22), readReg(23));        
    }
    
    private void loadAndStore(final int address) {
        
        // run after setAndJump()
        
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);
        apply(address + 5, SWAP);
        apply(address + 6, SWAP);
        apply(address + 7, SWAP);
        apply(address + 8, SWAP);
        apply(address + 9, SWAP);
        apply(address + 10, SWAP);
        apply(address + 11, SWAP);
        apply(address + 12, SWAP);
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [j  k  w  r  P1 P0 a1 a0 i  s1 m  B  M  N  A  d  s0 n  z  R1 R0] Q  
        
        apply(address + 11, STB_C);         // s1 = (i == STB);
        apply(address + 12, C_COPY_B_A);    // if (s1) m = B;
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 19, SWAP);
        apply(address + 20, SWAP);
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);

        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [j  k  w  r  P1 P0 a1 a0 i  s1 m  A  M  N  d  s0 n  z  B  R1 R0] Q  
        
        apply(address + 11, STA_C);         // s1 = (i == STA);
        apply(address + 12, C_COPY_B_A);    // if (s1) m = A;
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);
        apply(address + 16, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 19, SWAP);
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);
        apply(address + 13, SWAP);
        apply(address + 12, SWAP);        
        apply(address + 13, SWAP);
        apply(address + 12, SWAP);
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 9, SWAP);
        apply(address + 8, SWAP);
        apply(address + 7, SWAP);
        apply(address + 6, SWAP);
        apply(address + 5, SWAP);
               
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [j  k  m  w  r  P1 P0 a1 a0 i  d  s1 M  N  s0 n  z  A  B  R1 R0] Q
        
        apply(address + 12, LDB_C);         // d = (i == STA);
        apply(address + 13, SWAP);
        apply(address + 14, SWAP);
        apply(address + 15, SWAP);        
        apply(address + 16, SWAP);
        apply(address + 15, SWAP);
        apply(address + 14, SWAP);        
        apply(address + 11, SWAP);
        apply(address + 10, SWAP);
        apply(address + 9, SWAP);
        apply(address + 8, SWAP);
        apply(address + 7, SWAP);
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [j  k  m  w  i  r  P1 P0 a1 a0 s1 s0 M  N  d  n  z  A  B  R1 R0] Q

//        System.out.println("loadAndStore:");
//        System.out.format("i = %02X, j = %02X, k = %02X, m = %02X, w = %02X, r = %02X%n",
//                readReg(7), readReg(3), readReg(4), readReg(5), readReg(6), readReg(8));
//        System.out.format("P1 = %02X, P0 = %02X, s1 = %02X, s0 = %02X, a1 = %02X, a0 = %02X%n",
//                readReg(9), readReg(10), readReg(13), readReg(14), readReg(11), readReg(12));
//        System.out.format("M = %02X, N = %02X, d = %02X, n = %02X, z = %02X%n",
//                readReg(15), readReg(16), readReg(17), readReg(18), readReg(19));
//        System.out.format("A = %02X, B = %02X, R1 = %02X, R0 = %02X%n",
//                readReg(20), readReg(21), readReg(22), readReg(23));
        
        apply(address + 7, LDX_C);         // r = (i == LDx);
        apply(address + 6, SWAP);
        apply(address + 6, STX_C);         // w = (i == STx);
        apply(address + 5, SWAP);
        apply(address + 4, SWAP);
        apply(address + 3, SWAP);
//        apply(address + 13, CLEAR);        // s1 = 0;
//        apply(address + 14, CLEAR);        // s0 = 0;
        apply(address + 12, SWAP);
        apply(address + 11, SWAP);
        apply(address + 13, SWAP);
        apply(address + 12, SWAP);         // order restored
        
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q
        
//        System.out.println("loadAndStore2:");
//        System.out.format("i = %02X, j = %02X, k = %02X, m = %02X, w = %02X, r = %02X%n",
//                readReg(3), readReg(4), readReg(5), readReg(6), readReg(7), readReg(8));
//        System.out.format("P1 = %02X, P0 = %02X, s1 = %02X, s0 = %02X, a1 = %02X, a0 = %02X%n",
//                readReg(9), readReg(10), readReg(11), readReg(12), readReg(13), readReg(14));
//        System.out.format("M = %02X, N = %02X, d = %02X, n = %02X, z = %02X%n",
//                readReg(15), readReg(16), readReg(17), readReg(18), readReg(19));
//        System.out.format("A = %02X, B = %02X, R1 = %02X, R0 = %02X%n",
//                readReg(20), readReg(21), readReg(22), readReg(23));
    }
    
    private void executeInstruction(final int address) {
        finishLoad(address);
        transfer(address);
        runALU(address);
        setAndJump(address);
        loadAndStore(address);
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
    
    private int readReg(final int offset) {
        return bytes[descend ? maxAddress + offset : offset];
    }
    
    private void printRegisters() {
        System.out.format("i = %02X, j = %02X, k = %02X, m = %02X, w = %02X, r = %02X%n",
                readReg(3), readReg(4), readReg(5), readReg(6), readReg(7), readReg(8));
        System.out.format("P1 = %02X, P0 = %02X, s1 = %02X, s0 = %02X, a1 = %02X, a0 = %02X%n",
                readReg(9), readReg(10), readReg(11), readReg(12), readReg(13), readReg(14));
        System.out.format("M = %02X, N = %02X, d = %02X, n = %02X, z = %02X%n",
                readReg(15), readReg(16), readReg(17), readReg(18), readReg(19));
        System.out.format("A = %02X, B = %02X, R1 = %02X, R0 = %02X%n",
                readReg(20), readReg(21), readReg(22), readReg(23));
    }
    
    // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
    // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
    // I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q   
    @Override
    public void init() throws IOException {
        final File binFile = new File("asm/tetris.bin");
        maxAddress = (int)binFile.length() - 3; 
        
        bytes = new int[maxAddress + 24];
        
        try (final InputStream in = new BufferedInputStream(new FileInputStream(binFile))){
            for (int address = 0; address <= maxAddress; ++address) {                
                final int b = in.read();
                if (b < 0) {
                    throw new IOException("Unexpected end of file.");
                }
                bytes[address] = b;
            }
            bytes[maxAddress + 9] = in.read();
            bytes[maxAddress + 10] = in.read();
            if (bytes[maxAddress + 9] < 0 || bytes[maxAddress + 10] < 0) {
                throw new IOException("Unexpected end of file.");
            }
        }
        
        bytes[maxAddress + 13] = maxAddress >> 8;
        bytes[maxAddress + 14] = 0xFF & maxAddress;
        
//        System.out.format("maxAddress = %04X%n", maxAddress);
    }    

    int cycleCounter = 0;
    
    @Override
    public void runInstruction() {
//        System.out.format("cycle: %d%n", cycleCounter++);
        if (descend) {
            descend = false;
//            System.out.println("descend");
            descendMemoryCycle();
//            printRegisters();
            executeInstruction(0x0000);
//            printRegisters();
        } else {
            descend = true;
//            System.out.println("ascend");
            ascendMemoryCycle();
//            printRegisters();
            executeInstruction(maxAddress);            
//            printRegisters();
        }        
    }

    @Override
    public int readMemory(int address) {
        return bytes[(descend || address < 3) ? address : (address + 21)];    
    }

    @Override
    public void writeMemory(int address, int value) {
        bytes[(descend || address < 3) ? address : (address + 21)] = value;
    }
    
    public void launch() {
                
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q
        
        //write(18, 1);
        //write(19, 1);
//        write(22, 0x66);
//        write(23, 0x77);
//        write(20, 0x88);
//        write(21, 0x99);
//        
//        write(3, 0b0100_0001);
//        write(4, 0x44);
//        write(5, 0x55);
        
//        executeInstruction(0);
        
        
        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q 

//        System.out.format("m = %02X, w = %02X, r = %02X, d = %02X, A = %02X, B = %02X, MN = %04X, P = %04X, R = %04X%n",
//                read(6),
//                read(7),
//                read(8),
//                read(17),
//                read(20),
//                read(21),
//                (read(15) << 8) | read(16),
//                (read(9) << 8) | read(10), 
//                (read(22) << 8) | read(23));
        
//        System.out.format("A = %02X, B = %02X, MN = %04X, P = %04X, R = %04X%n", 
//                read(20),
//                read(21),
//                (read(14) << 8) | read(15),
//                (read(8) << 8) | read(9), 
//                (read(22) << 8) | read(23));
                
//        
//        transfer(0);
//        
//        System.out.println(read(20));
//        System.out.println(read(21));
//        System.out.println(read(14));
//        System.out.println(read(16));
        
        
        for (int i = 0; i < 3; ++i) {            
            ascendMemoryCycle();
            executeInstruction(0x03FF);
            descendMemoryCycle();
            executeInstruction(0x0000);
        }
        
        long startTime = System.nanoTime();
        for (int i = 0; i < 50_000; ++i) {
            ascendMemoryCycle();
            executeInstruction(0x03FF);
            descendMemoryCycle();
            executeInstruction(0x0000);
        }
        long endTime = System.nanoTime();
        System.out.format("%f%n", (endTime - startTime) / 1_000_000_000.0);

        // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
        // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
        // I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q

//        write(6, 0x3F);
//        write(8, 0);
//        write(17, 0);
//        write(18, 1);
//        write(19, 1);
//        write(20, 11);
//        write(21, 22);  
//        
//        System.out.println("m = " + read(6));
//        System.out.println("r = " + read(8));
//        System.out.println("d = " + read(17));
//        System.out.println("n = " + read(18));
//        System.out.println("z = " + read(19));
//        System.out.println("A = " + read(20));
//        System.out.println("B = " + read(21));        
//        
//        finishLoad(0);
//        System.out.println();
//        
//        System.out.println("m = " + read(6));
//        System.out.println("r = " + read(8));
//        System.out.println("d = " + read(17));
//        System.out.println("n = " + read(18));
//        System.out.println("z = " + read(19));
//        System.out.println("A = " + read(20));
//        System.out.println("B = " + read(21));
    }
    
    public static void main(final String... args) {
        new Simulator().launch();
    }    
}
