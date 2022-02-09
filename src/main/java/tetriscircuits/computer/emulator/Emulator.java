package tetriscircuits.computer.emulator;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

// P
// z
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
// 0010 000z aaaaaaaa aaaaaaaa
// z: jump if not zero

// 0011 000r
// [ M ] = A|B

// 0100 000r
// A|B = [ M ]

// 0101 000r vvvvvvvv
// A|B = v

// 0110 0000 vvvvvvvv vvvvvvvv
// MN = v

public class Emulator {
    
    public final int[] memory = new int[0x10000];
    
    public int P;
    public int A;
    public int B;
    public int M;
    public int N;
    public boolean zero;
    
    private void launch(final String binFilename) throws Exception {
        
        loadBinFile(binFilename);
        
        while (true) {
            final int opcode = fetch();
            switch ((opcode >> 4) & 0b1111) {
                case 0b0000:
                    transfer((opcode >> 2) & 0b11, opcode & 0b11);
                    break;
                case 0b0001:
                    compute(opcode);
                    break;
            }
        }
    }
    
    private void loadBinFile(final String binFilename) throws IOException {
        try (final InputStream in = new BufferedInputStream(new FileInputStream(binFilename))){
            int address = 0;
            while (address < memory.length) {
                final int b = in.read();
                if (b < 0) {
                    break;
                }
                memory[address++] = b;
            }
        }
    }
    
    private void transfer(final int source, final int destination) {
        setRegister(destination, 0);
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
                ++A;
                break;
            case 0b0001:
                --A;
                break;
            case 0b0010:
                A += B;
                break;
            case 0b0011:
                A -= B;
                break;
            case 0b0100:
                A <<= 1;
                break;
            case 0b0101:
                A >>= 1;
                break;
            case 0b0110:
                A &= B;
                break;
            case 0b0111:
                A |= B;
                break;
            case 0b1000:
                A ^= B;
                break;
            case 0b1001:
                A = ~A;
                break;
        }
        A &= 0xFF;
        B &= 0xFF;
        zero = (A == 0);
    }
     
    private void jump(final int bits) {
        final int target = (fetch() << 8) | fetch();
        final boolean value = (bits & 0b0000_0001) != 0;
        switch ((bits & 0b0000_0110) >> 1) {
            case 0:
                break;
            case 1:
                if (carry != value) {
                    return;
                }
                break;
            case 2:
                if (zero != value) {
                    return;
                }
                break;
            case 3:
                if (negative != value) {
                    return;
                }
                break;
        }
        if ((bits & 0b0000_1000) != 0) {
            
        }
        P = target;
    }
    
    private void loadImmediate() {
        A = fetch();
    }
    
    private void loadMemory() {
        A = readMemory((MH << 8) | ML);
    }
    
    private void storeMemory() {
        writeMemory((MH << 8) | ML, A);
    }
    
    private int fetch() {
        final int value = readMemory(P);
        P = (P + 1) & 0xFFFF;
        return value;
    }
    
    private void writeMemory(final int address, final int value) {
        memory[address & 0xFFFF] = value;
    }
    
    private int readMemory(final int address) {
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
