package tetriscircuits.computer.emulator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import tetriscircuits.computer.Processor;

// P R
// z n
// A B M N
//
// T [ A B M N ] [ A B M N ]
// 0000 ss dd

// 00 A
// 01 B
// 10 M
// 11 N

// 0001ffff
// 0000 ++A
// 0001 --A
// 0010 A += B
// 0011 A -= B
// 0100 A <<= 1
// 0101 A >>= 1
// 0110 A &= B
// 0111 A |= B
// 1000 A ^= B
// 1001 ~A

// JMP
// 0010 sffv aaaaaaaa aaaaaaaa
// ff:
// 01: test z == v
// 10: test n == v
// s: R = P on jump

// 0011 000r
// [ M ] = A|B

// 0100 000r
// A|B = [ M ], sets n and z

// 0101 000r vvvvvvvv
// A|B = v, sets n and z

// 0010 1111 vvvvvvvv vvvvvvvv
// MN = v

// 0111 0000
// P = R

public class Emulator implements Processor {
    
    private final int[] memory = new int[0x10000];
    
    private int P;
    private int R;
    private int A;
    private int B;
    private int M;
    private int N;
    private boolean z;
    private boolean n;
    
    int generateNextPseudorandomNumber(int value) {
        return ((((value >> 9) & 1) ^ ((value >> 1) & 1)) << 15) | (value >> 1);
    }
    
    private void launch(final String binFilename) throws Exception {
        
//        int seed = 0x8988;
//        for (int i = 0; i < 5; ++i) {
//            System.out.format("%04X%n", seed);
//            seed = generateNextPseudorandomNumber(seed);
//        }
        
        init();
        
        while (true) {
                        
//            System.out.format("P: %04X, A: %02X, B: %02X, M: %02X, N: %02X, Z: %b, %02X %02X %02X%n", 
//                    P, A, B, M, N, z, memory[0], memory[1], memory[2]);
//            Thread.sleep(10);
            
            runInstruction();
        }
    }
    
    @Override
    public void runInstruction() {
        final int opcode = fetch();
        switch ((opcode >> 4) & 0b1111) {
            case 0b0000:
                transfer((opcode >> 2) & 0b11, opcode & 0b11);
                break;
            case 0b0001:
                compute(opcode);
                break;
            case 0b0010:
                jump(opcode);
                break;
            case 0b0011:
                store(opcode);
                break;
            case 0b0100:
                load(opcode);
                break;
            case 0b0101:
                loadByteImmediate(opcode);
                break;
            case 0b0111:
                returnSubroutine();
                break;
            case 0b1111:
                print(opcode);
                break;
        }
        
            System.out.format("P: %04X, A: %02X, B: %02X, M: %02X, N: %02X, R: %04X, z: %b, n: %b%n", 
                    P, A, B, M, N, R, z, n);        
    }
    
    @Override
    public void init() throws Exception {
        final File binFile = new File("bne-loop.bin");
        final int maxAddress = (int)binFile.length() - 3;        
        
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
            case 0b0000:
                A += B;
                break;
            case 0b0001:
                A &= B;
                break;
            case 0b0010:
                --A;
                break;
            case 0b0011:
                ++A;
                break;
            case 0b0100:
                A <<= 2;
                break;
            case 0b0101:
                A <<= 3;
                break;
            case 0b0110:
                A <<= 4;
                break;
            case 0b0111:
                A |= B;
                break;
            case 0b1000:
                A >>>= 1;
                break;
            case 0b1001:
                A >>>= 5;
                break;
            case 0b1010:
                A -= B;
                break;
            case 0b1011:
                A ^= B;
                break;                
        }
        A &= 0xFF;
        z = (A == 0);
        n = (A & 0x80) != 0;
    }
     
    private void jump(final int bits) {
        if (bits == 0b0010_1111) {
            loadMN();
            return;
        }
        
        final int target = (fetch() << 8) | fetch();
        switch ((bits & 0b0000_0110) >> 1) {
            case 1:
                if (((bits & 0b0000_0001) != 0) != z) {
                    return;
                }
                break;
            case 2:
                if (((bits & 0b0000_0001) != 0) != n) {
                    return;
                }
                break;
        }
        if ((bits & 0b0000_1000) != 0) {
            R = P;
        }
        P = target;
    }
    
    private void returnSubroutine() {
        P = R;
    }
    
    private void loadByteImmediate(final int opcode) {
        final int value = fetch();
        z = (value == 0);
        n = (value & 0x80) != 0;
        if ((opcode & 1) == 0) {
            A = value;
        } else {
            B = value;
        }
    }
    
    private void loadMN() {
        M = fetch();
        N = fetch();
    }
    
    private void print(final int opcode) {
        if ((opcode & 0b0000_1111) == 0b0000_1110) {
            System.out.format("A = %02X, B = %02X, M = %02X, N = %02X, P = %04X, R = %04X, z = %b, n = %b%n",
                    A, B, M, N, P, R, z, n);
        } else {
            final int v = readMemory((fetch() << 8) | fetch());
            System.out.format("%d %02X%n", v, v);
        }
    }
    
    private void load(final int opcode) {
        final int value = readMemory((M << 8) | N);
        z = (value == 0);
        n = (value & 0x80) != 0;
        if ((opcode & 1) == 0) {
            A = value;
        } else {
            B = value;
        }
    }
    
    private void store(final int opcode) {
        writeMemory((M << 8) | N, (opcode & 1) == 0 ? A : B);
    }

    private int fetch() {
        final int value = readMemory(P);
        P = (P + 1) & 0xFFFF;
        return value;
    }
    
    @Override
    public void writeMemory(final int address, final int value) {
        memory[address & 0xFFFF] = value;
    }
    
    @Override
    public int readMemory(final int address) {
        return memory[address & 0xFFFF];
    }
    
    public static void main(final String... args) throws Exception {
        
        if (args.length != 1) {
            System.out.println("args: [ bin filename]");
            return;
        }
        
        new Emulator().launch(args[0]);
    }
}
