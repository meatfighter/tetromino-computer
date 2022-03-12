package tetriscircuits.computer.simulator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

// 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
// 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
// I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q

public final class GenerateTetrisExecutable {
    
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
    private static final int[][][] COPY_B_A = new int[256][256][2];
    private static final int[][][] DEC_16 = new int[256][256][2];
    private static final int[][][] INC_16 = new int[256][256][2];
    private static final int[][][] AND_A = new int[256][256][2];
    private static final int[][][] AND_B = new int[256][256][2];

    private static final int[][][] ADD_AB_FB = new int[256][256][2];
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

    private static final int[][][] SER_C = new int[256][2][2];
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
    private static final int[][][][] CMP_AND_C = new int[256][256][2][3];
    private static final int[][][][] C_COPY_A_B = new int[2][256][256][3];
    private static final int[][][][] C_COPY_B_A = new int[2][256][256][3];
    private static final int[][][][] COPY_A_B_C = new int[256][256][2][3];
    private static final int[][][][] COPY_B_A_C = new int[256][256][2][3];
    private static final int[][][][] AND_A_B_C = new int[256][256][2][3];
    private static final int[][][][] C_AND_A_NOT_B = new int[2][256][256][3]; 
    
    private static final int[][][][] INC_16_C = new int[256][256][2][3];
    
    private static final Object[][] NAMES = {
        { ZERO_C, "ZERO_C", },
        { C_ZERO, "C_ZERO", },
        { MINUS_C, "MINUS_C", },
        { C_MINUS, "C_MINUS", },
        
        { T_A_X_C, "T_A_X_C", },
        { T_B_X_C, "T_B_X_C", },
        { T_M_X_C, "T_M_X_C", },
        { T_N_X_C, "T_N_X_C", },        

        { T_X_A_C, "T_X_A_C", },
        { T_X_B_C, "T_X_B_C", },
        { T_X_M_C, "T_X_M_C", },
        { T_X_N_C, "T_X_N_C", },    
    
        { SWAP, "SWAP", },
        { COPY_B_A, "COPY_B_A", },
        { DEC_16, "DEC_16", },
        { INC_16, "INC_16", },
        { AND_A, "AND_A", },
        { AND_B, "AND_B", },
        
        { ADD_AB_FB, "ADD_AB_FB", },
        { AND_AB_FB, "AND_AB_FB", },
        { AND_NOT_AB_FB, "AND_NOT_AB_FB", },
        { OR_AB_FB, "OR_AB_FB", },
        { SUB_AB_FB, "SUB_AB_FB", },
        { XOR_AB_FB, "XOR_AB_FB", }, 
        
        { CLEAR, "CLEAR", },
        { DEC, "DEC", },
        { INC, "INC", },
        { LSH, "LSH", },
        { NOT, "NOT", },
        { RSH, "RSH", },
        
        { ADD_C, "ADD_C", },
        { AND_C, "AND_C", },
        { DEC_C, "DEC_C", },
        { INC_C, "INC_C", },
        { LSH_C, "LSH_C", },
        { NOT_C, "NOT_C", },
        { OR_C, "OR_C", },
        { RSH_C, "RSH_C", },
        { SUB_C, "SUB_C", },
        { XOR_C, "XOR_C", },
        
        { SER_C, "SER_C", },
        { THREE_C, "THREE_C", },        
    
        { SEA_C, "SEA_C", },
        { SEB_C, "SEB_C", },
        { SMN_C, "SMN_C", },
        { JMP_C, "JMP_C", },
        { BEQ_C, "BEQ_C", },
        { BMI_C, "BMI_C", },
        { BNE_C, "BNE_C", },
        { BPL_C, "BPL_C", },
        { JSR_C, "JSR_C", },
        { RTS_C, "RTS_C", },
        
        { STA_C, "STA_C", },
        { STB_C, "STB_C", },
        { STX_C, "STX_C", },
        { LDX_C, "LDX_C", },
        { LDB_C, "LDB_C", },
        
        { C_CMP, "C_CMP", },
        { CMP_C, "CMP_C", },
        { CMP_AND_C, "CMP_AND_C", },
        { C_COPY_A_B, "C_COPY_A_B", },
        { C_COPY_B_A, "C_COPY_B_A", },
        { COPY_A_B_C, "COPY_A_B_C", },
        { COPY_B_A_C, "COPY_B_A_C", },
        { AND_A_B_C, "AND_A_B_C", },
        { C_AND_A_NOT_B, "C_AND_A_NOT_B", },        
        
        { INC_16_C, "INC_16_C", }, 
    };
    
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
            SUB_C[a][0][1] = (a == 0b0001_0011) ? 1 : 0;
            SUB_C[a][1][0] = a;
            SUB_C[a][1][1] = (a == 0b0001_0011) ? 1 : 0;
            
            XOR_C[a][0][0] = a;
            XOR_C[a][0][1] = (a == 0b0001_1000) ? 1 : 0;
            XOR_C[a][1][0] = a;
            XOR_C[a][1][1] = (a == 0b0001_1000) ? 1 : 0;

            SER_C[a][0][0] = a;
            SER_C[a][0][1] = ((a & 0b1111_1110) == 0b0101_0000) ? 1 : 0;
            SER_C[a][1][0] = a;
            SER_C[a][1][1] = ((a & 0b1111_1110) == 0b0101_0000) ? 1 : 0;
            
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
                               
                C_AND_A_NOT_B[0][a][b][0] = a & (b == 0 ? 1 : 0);
                C_AND_A_NOT_B[0][a][b][1] = a;
                C_AND_A_NOT_B[0][a][b][2] = b;
                C_AND_A_NOT_B[1][a][b][0] = a & (b == 0 ? 1 : 0);
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
    
    private static String toString(final Object obj) {
        for (final Object[] name : NAMES) {
            if (obj == name[0]) {
                return name[1].toString();
            }
        }
        throw new RuntimeException("Unknown object.");
    }
        
    private int[] bytes;
    private int maxAddress;
    private boolean descend = true;
    private PrintStream out;
    
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
        apply(address + 8, INC_16);         // ++P;
        apply(address + 12, SER_C);         // s1 = (i == SEx);
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
        apply(address + 12, T_M_X_C);       // s1 = (i == TMx);
        apply(address + 13, C_COPY_A_B);    // if (s1) s0 = M;
        apply(address + 15, SWAP);
        apply(address + 13, SWAP);
        apply(address + 12, SWAP);
        apply(address + 13, T_N_X_C);       // s1 = (i == TNx);
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
        apply(address + 17, T_A_X_C);       // s1 = (i == TAx);
        apply(address + 18, C_COPY_A_B);    // if (s1) s0 = A;
        apply(address + 20, SWAP);
        apply(address + 18, SWAP);
        apply(address + 17, SWAP);
        apply(address + 18, T_B_X_C);       // s1 = (i == TBx);
        apply(address + 19, C_COPY_A_B);    // if (s1) s0 = B;
        apply(address + 18, T_X_B_C);       // s1 = (i == TxB);
        apply(address + 19, C_COPY_B_A);    // if (s1) B = s0;
        apply(address + 17, SWAP);
        apply(address + 18, SWAP);
        apply(address + 20, SWAP);
        apply(address + 17, T_X_A_C);       // s1 = (i == TxA);
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
        apply(address + 13, T_X_N_C);       // s1 = (i == TxN);
        apply(address + 14, C_COPY_B_A);    // if (s1) N = s0;
        apply(address + 12, SWAP);
        apply(address + 13, SWAP);
        apply(address + 15, SWAP);
        apply(address + 12, T_X_M_C);       // s1 = (i == TxM);
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
       
    private void apply(final int index, final int[] map) {
        out.format("%s %d%n", toString(map), index);
    }
    
    private void apply(final int index, final int[][][] map) {
        out.format("%s %d%n", toString(map), index);
    }

    private void apply(final int index, final int[][][][] map) {
        out.format("%s %d%n", toString(map), index);
    }
    
    // 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
    // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
    // I  J  K [i  j  k  m  w  r  P1 P0 s1 s0 a1 a0 M  N  d  n  z  A  B  R1 R0] Q   
    public void loadBinFile(final String binFilename) throws IOException {
        final File binFile = new File(binFilename);
        maxAddress = (int)binFile.length() - 3; 
        
        bytes = new int[maxAddress + 24];
        
        try (final InputStream in = new BufferedInputStream(new FileInputStream(binFilename))){
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
    
    public void runInstruction() {
        if (descend) {
            descend = false;
            descendMemoryCycle();
            executeInstruction(0x0000);
        } else {
            descend = true;
            ascendMemoryCycle();
            executeInstruction(maxAddress);            
        }        
    }
    
    private void saveMap(final String fileName, final int[] map) throws IOException {
        try (final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName))) {
            saveMap(out, map);
        }
    }
    
    private void saveMap(final OutputStream out, final int[] map) throws IOException {
        out.write(1);
        for (int i = 0; i < 256; ++i) {
            out.write(map[i]);
        }
    }
    
    private void saveMap(final String fileName, final int[][][] map) throws IOException {
        try (final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName))) {
            saveMap(out, map);
        }
    }
    
    private void saveMap(final OutputStream out, final int[][][] map) throws IOException {
        out.write(2);
        for (int i = 0; i < 256; ++i) {
            final int[][] p = map[(map.length == 2) ? (i & 1) : i];
            for (int j = 0; j < 256; ++j) {
                final int[] q = p[(p.length == 2) ? (j & 1) : j];
                for (int k = 0; k < 2; ++k) {
                    out.write(q[k]);
                }
            }
        }
    }

    private void saveMap(final String fileName, final int[][][][] map) throws IOException {
        try (final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName))) {
            saveMap(out, map);
        }
    }

    private void saveMap(final OutputStream out, final int[][][][] map) throws IOException {
        if (map.length == 2) {
            out.write(3);
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 256; ++j) {
                    for (int k = 0; k < 256; ++k) {
                        for (int l = 0; l < 3; ++l) {
                            out.write(map[i][j][k][l]);
                        }
                    }
                }
            }
        } else {
            out.write(4);
            for (int i = 0; i < 256; ++i) {
                for (int j = 0; j < 256; ++j) {
                    for (int k = 0; k < 2; ++k) {
                        for (int l = 0; l < 3; ++l) {
                            out.write(map[i][j][k][l]);
                        }
                    }
                }
            }
        }
    } 
    
    private void saveMaps() throws IOException {
        for (final Object[] name : NAMES) {
            final String filename = String.format("maps/%s.map", name[1]);
            System.out.println(filename);
            if (name[0].getClass() == int[].class) {
                saveMap(filename, (int[])name[0]);
            } else if (name[0].getClass() == int[][][].class) {
                saveMap(filename, (int[][][])name[0]);
            } else {
                saveMap(filename, (int[][][][])name[0]);
            }
        }
    }
    
    private void saveInputData() throws IOException {
        try (final OutputStream out = new BufferedOutputStream(new FileOutputStream("data/inputData.dat"))) {
            for (int i = 0; i < bytes.length; ++i) {
                out.write(bytes[i]);
            }
        }
    }
    
    private void saveTetrisExecutable() throws IOException {
        try (final PrintStream o = new PrintStream(new FileOutputStream("executable/tetris.tx"))) {
            out = o;
            runInstruction();
            runInstruction();            
        }
    }
    
    public void launch() throws Exception {
        loadBinFile("asm/tetris.bin");
//        saveInputData();
//        saveMaps();
        saveTetrisExecutable();        
    }
    
    public static void main(final String... args) throws Exception {
        new GenerateTetrisExecutable().launch();
    }    
}
