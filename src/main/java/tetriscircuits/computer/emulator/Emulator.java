package tetriscircuits.computer.emulator;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

// P
// c n z
// A B M N
//
// T [ A B M N ] [ A B M N ]
// 0000 ss dd

// 00 A
// 01 B
// 10 M
// 11 N

// 00010fff

// A <<= 1
// A >>= 1
// ++A
// --A
// A += B
// A -= B
// A &= B
// A |= B

// JMP
// 00100ffv aaaaaaaa aaaaaaaa

// ff:
// 00 - no condition
// 01 - c == v
// 10 - z == v
// 11 - n == v

// 0011 00rr
// [ M ] = r

// 0100 00rr
// r = [ M ]

// 0101 00rr vvvvvvvv
// r = v

// 0110 000r vvvvvvvv vvvvvvvv
// r = v

// 0 AB
// 1 MN

public class Emulator {
    
    public final int[] memory = new int[0x10000];
    
    public int P;
    public int S;
    public int A;
    public int B;
    public int C;
    public int D;
    public int E;
    public int F;
    public int M;
    public int N;
    public boolean carry;
    public boolean zero;
    public boolean negative;    
    
    private void launch(final String binFilename) throws Exception {
        
        loadBinFile(binFilename);
        
        while (true) {
            System.out.format("%02X%n", memory[0]);
            final int opcode = fetch();
            switch (opcode >> 6) {
                case 0:
                    transfer((opcode & 0b0011_1000) >> 3, opcode & 0b0000_0111);
                    break;
                case 1:
                    compute(opcode);
                    break;
                case 2:
                    jump(opcode);
                    break;
                default:
                    switch ((opcode >> 4) & 3) {
                        case 0:
                            loadImmediate();
                            break;
                        case 1:
                            loadMemory();
                            break;
                        case 2:
                            storeMemory();
                            break;
                        case 3:
                            switch (opcode) {
                                case 0b1111_1000:
                                    P = (E << 8) | F;
                                    break;
                                case 0b1111_1001:
                                    MH = fetch();
                                    ML = fetch();
                                    break;
                                default:
                                    updateFlags(opcode & 0b0000_0111);
                                    break;
                            }
                            break;
                    }
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
    
    private void setRegister(final int index, final int value) {
        switch (index) {
            case 0:
                A = value;
                break;
            case 1:
                B = value;
                break;
            case 2:
                C = value;
                break;
            case 3:
                D = value;
                break;
            case 4:
                E = value;
                break;
            case 5:
                F = value;
                break;
            case 6:
                M = value;
                break;
            case 7:
                N = value;
                break;
        }
    }
    
    private int getRegister(final int index) {
        switch (index) {
            case 0:
                return A;
            case 1:
                return B;
            case 2:
                return C;
            case 3:
                return D;
            case 4:
                return E;
            case 5:
                return F;
            case 6:
                return M;
            case 7:
                return N;
        }
        throw new RuntimeException("Invalid register index.");
    }
     
    private void compute(final int function) {
        int v;
        switch (function & 0b0000_1111) {
            case 0:
                v = invert();
                break;
            case 1:
                v = negate();
                break;
            case 2:
                v = increment();
                break;
            case 3:
                v = decrement();
                break;
            case 4:
                v = unsignedRightShift();
                break;
            case 5:
                v = signedRightShift();
                break;
            case 6:
                v = leftShift();
                break;

            case 8:
                v = add();
                break;
            case 9:
                v = addWithCarry();
                break;                
            case 10:
                v = subtract();
                break;
            case 11:
                v = subtractWithBorrow();
                break;                
            case 12:
                v = and();
                break;
            case 13:
                v = or();
                break;
            case 14:
                v = xor();
                break;
            default:
                throw new RuntimeException("Invalid function.");
        }        
        v &= 0xFF;
        zero = (v == 0);
        negative = (v & 0x80) != 0;
        setRegister(3 & (function >> 4), v);
    }
    
    private int invert() {
        return ~C;
    }
    
    private int negate() {
        return -C;
    }    
    
    private int unsignedRightShift() {
        carry = (C & 0x01) != 0;
        return C >>> 1;
    }
    
    private int signedRightShift() {
        carry = (C & 0x01) != 0;
        return C >> 1;
    }
    
    private int leftShift() {
        carry = (C & 0x80) != 0;
        return C << 1;
    }
    
    private int decrement() {
        return C - 1;
    }
    
    private int increment() {
        return C + 1;
    }

    private int subtract() {
        final int v = C - D;
        carry = (v & 0x100) != 0;
        return v;
    }
    
    private int subtractWithBorrow() {
        final int v = C - D - (carry ? 1 : 0);
        carry = (v & 0x100) != 0;
        return v;
    }    
    
    private int add() {
        final int v = C + D;
        carry = (v & 0x100) != 0;
        return v;
    }
    
    private int addWithCarry() {
        final int v = C + D + (carry ? 1 : 0);
        carry = (v & 0x100) != 0;
        return v;
    }    
    
    private int and() {
        return C & D;
    }
    
    private int or() {
        return C | D;
    }    
    
    private int xor() {
        return C ^ D;
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
    
    private void updateFlags(final int bits) {
        final boolean value = (bits & 1) != 0;
        switch (bits >> 1) {
            case 0:                
                break;
            case 1:
                carry = value;
                break;
            case 2:
                zero = value;
                break;
            case 3:
                negative = value;
                break;
            default:
                throw new RuntimeException("Invalid opcode.");
        }
    }
    
    private int fetch() {
        final int value = readMemory(P);
        incrementP();
        return value;
    }
    
    private void incrementP() {
        P = (P + 1) & 0xFFFF;
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
