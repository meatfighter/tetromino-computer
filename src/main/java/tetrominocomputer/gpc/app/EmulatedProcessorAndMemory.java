package tetrominocomputer.gpc.app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import tetrominocomputer.util.Dirs;
import tetrominocomputer.util.Out;

public class EmulatedProcessorAndMemory implements ProcessorAndMemory {
    
    private static final String DEFAULT_BIN_FILENAME = "example.bin";

    private int[] memory; // up to 64 KiB of RAM

    private int A; // Accumulator - Input and lone output of all arithmetic and logic instructions.
    private int B; // Data Register - Input of some arithmetic and logic instructions.
    private int M; // High Memory Register - High byte of the 16-bit Memory Register, MN, which contains the source 
                   //                        address of load instructions and the destination address of store 
                   //                        instructions.
    private int N; // Low Memory Register - Low byte of MN.   
    private int P; // Program Counter - Address of the instruction to execute next.
    private int R; // Return Register - Address of the instruction to return to at the end of a subroutine.
    
    private boolean z; // Negative Flag - Indicates an arithmetic, logic, or load instruction produced a negatively 
                       //                 signed value.
    private boolean n; // Zero Flag - Indicates an arithmetic, logic, or load instruction produced zero.
    
    @Override
    public boolean init(final String[] args) throws Exception {
        
        String binFilename = DEFAULT_BIN_FILENAME;
        for (int i = 0; i < args.length - 1; ++i) {
            if ("-b".equals(args[i])) {
                binFilename = args[++i];
            }            
        }
        
        final File binFile = new File(Dirs.BIN + binFilename);
        if (!(binFile.exists() && binFile.isFile())) {
            Out.formatError("%nBinary file not found: %s%n%n", binFile);
            return false;
        }
        final int maxAddress = ((int) binFile.length()) - 3;
        if (maxAddress < 0) {
            Out.formatError("%nInvalid binary file.%n%n");
            return false;
        }
        
        memory = new int[maxAddress + 1];
        
        try (final InputStream in = new BufferedInputStream(new FileInputStream(binFile))){
            for (int address = 0; address <= maxAddress; ++address) {
                final int b = in.read();
                if (b < 0) {
                    throw new IOException("Unexpected end of file.");
                }
                memory[address] = b;
            }
            final int PH = in.read();
            final int PL = in.read();
            if (PH < 0 || PL < 0) {
                throw new IOException("Unexpected end of file.");
            }
            P = (PH << 8) | PL;
        }
        
        return true;
    }    

    @Override
    public void executeInstruction() {
        final int opcode = fetch();
        switch ((opcode >> 4) & 0b1111) {
            case 0b0000: // transfer instructions
                transfer((opcode >> 2) & 0b11, opcode & 0b11);
                break;
            case 0b0001: // arithmetic and logic instructions
                compute(opcode);
                break;
            case 0b0010: // branch instructions and 16-bit set
                branch(opcode);
                break;
            case 0b0011: // store instructions
                store(opcode);
                break;
            case 0b0100: // load instructions
                load(opcode);
                break;
            case 0b0101: // 8-bit set instructions
                set(opcode);
                break;
            case 0b0111: // return subroutine
                returnSubroutine();
                break;
        }        
    }
    
    private void transfer(final int source, final int destination) {
        setRegister(destination, getRegister(source));
    }
    
    private void setRegister(final int destination, final int value) {
        switch (destination) {
            case 0:
                A = value;
                break;
            case 1:
                B = value;
                break;
            case 2:
                M = value;
                break;
            case 3:
                N = value;
                break;
            default:
                throw new IllegalArgumentException("Invalid destination register.");                
        }
    }
    
    private int getRegister(final int source) {
        switch (source) {
            case 0:
                return A;
            case 1:
                return B;
            case 2:
                return M;
            case 3:
                return N;
            default:
                throw new IllegalArgumentException("Invalid source register.");
        }        
    }

    private void compute(final int function) {
        switch (function & 0b1111) {
            case 0b0000:   // ADD
                A += B;
                break;
            case 0b0001:   // AND
                A &= B;
                break;
            case 0b0010:   // DEC
                --A;
                break;
            case 0b0011:   // INC
                ++A;
                break;
            case 0b0100:   // LS2
                A <<= 2;
                break;
            case 0b0101:   // LS3
                A <<= 3;
                break;
            case 0b0110:   // LS4
                A <<= 4;
                break;
            case 0b0111:   // OR
                A |= B;
                break;
            case 0b1000:   // RS1
                A >>>= 1;
                break;
            case 0b1001:   // RS5
                A >>>= 5;
                break;
            case 0b1010:   // SUB
                A -= B;
                break;
            case 0b1011:   // XOR
                A ^= B;
                break;                
        }
        A &= 0xFF;
        z = (A == 0);
        n = (A & 0x80) != 0;
    }

    private void branch(final int bits) {
        if (bits == 0b0010_1111) { // SMN
            setMN();
            return;
        }
        
        final int target = (fetch() << 8) | fetch();
        switch ((bits & 0b0000_0110) >> 1) {
            case 1:
                if (((bits & 0b0000_0001) != 0) != z) { // BNE and BEQ
                    return;
                }
                break;
            case 2:
                if (((bits & 0b0000_0001) != 0) != n) { // BPL and BMI
                    return;
                }
                break;
        }
        if ((bits & 0b0000_1000) != 0) { // JSR
            R = P;
        }
        P = target;
    }
    
    private void returnSubroutine() {
        P = R;
    }
    
    private void set(final int opcode) {
        final int value = fetch();
        z = (value == 0);
        n = (value & 0x80) != 0;
        if ((opcode & 1) == 0) {
            A = value;
        } else {
            B = value;
        }
    }
    
    private void setMN() {
        M = fetch();
        N = fetch();
    }
    
    private void load(final int opcode) {
        final int value = read((M << 8) | N);
        z = (value == 0);
        n = (value & 0x80) != 0;
        if ((opcode & 1) == 0) {
            A = value;
        } else {
            B = value;
        }
    }
    
    private void store(final int opcode) {
        write((M << 8) | N, (opcode & 1) == 0 ? A : B);
    } 
    
    private int fetch() {
        final int value = read(P);
        P = (P + 1) & 0xFFFF;
        return value;
    }    

    @Override
    public int read(final int address) {
        return memory[address];
    }

    @Override
    public void write(final int address, final int value) {
        memory[address] = value;
    }    
}
