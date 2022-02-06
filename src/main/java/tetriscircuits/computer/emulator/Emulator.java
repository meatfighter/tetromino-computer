package tetriscircuits.computer.emulator;

//P
//c z n
//A B C D E F MH ML
//
//T [ A B C D E F MH ML ] [ A B C D E F MH ML ]
//00 sss ddd

// A   000
// B   001
// C   010
// D   011
// E   100
// F   101
// MH  110
// ML  111

//
//01 00ffff
//0b0100_0000 A = ~C            // z, n
//0b0100_0001 A = -C            // z, n
//0b0100_0010 A = C + 1         // z, n (not c)
//0b0100_0011 A = C - 1         // z, n (not c)
//0b0100_0100 A = C >>> 1       // c, z, n
//0b0100_0101 A = C >> 1        // c, z, n
//0b0100_0110 A = C << 1        // c, z, n

//0b0100_1000 A = C + D         // c, z, n
//0b0100_1001 A = C + D + carry // c, z, n
//0b0100_1010 A = C - D         // c, z, n
//0b0100_1011 A = C - D - carry // c, z, n
//0b0100_1100 A = C & D         // z, n
//0b0100_1101 A = C | D         // z, n
//0b0100_1110 A = C ^ D         // z, n

//
//GOTO
//1000effv aaaaaaaa aaaaaaaa
//
//ff:
//00 - none
//01 - c == v
//10 - z == v
//11 - n == v
//
//e: EF = P
//
//1100 0000 aaaaaaaa
//A = V
//
//1101 0000
//A = [M]
//
//1110 0000
//[M] = A
//

//1111 0ffv
//ff:
//00 - none
//01 - c = v
//10 - z = v
//11 - n = v

//1111 1000
// P = EF

public class Emulator {
    
    public static final int MEMORY[] = new int[0x10000];
    
    public int P;
    public int A;
    public int B;
    public int C;
    public int D;
    public int E;
    public int F;
    public int MH;
    public int ML;
    public boolean carry;
    public boolean zero;
    public boolean negative;    
    
    private void launch() throws Exception {
        while (true) {
            final int opcode = fetch();
            switch (opcode >> 6) {
                case 0:
                    transfer((opcode & 0b0011_1000) >> 3, opcode & 0b0000_0111);
                    break;
                case 1:
                    compute(opcode & 0b0011_1111);
                    break;
                case 2:
                    jump(opcode & 0b0011_1111);
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
                            if (opcode == 01111_1000) {
                                P = (E << 8) | F;
                            } else {
                                updateFlags(opcode & 0b0000_0111);
                            }
                            break;
                    }
                    break;
            } 
        }
    }
    
    private void transfer(final int source, final int destination) {
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
                MH = value;
                break;
            case 7:
                ML = value;
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
                return MH;
            case 7:
                return ML;
        }
        throw new RuntimeException("Invalid register index.");
    }
     
    private void compute(final int function) {
        switch (function) {
            case 0:
                invert();
                break;
            case 1:
                negate();
                break;
            case 2:
                increment();
                break;
            case 3:
                decrement();
                break;
            case 4:
                unsignedRightShift();
                break;
            case 5:
                signedRightShift();
                break;
            case 6:
                leftShift();
                break;

            case 8:
                add();
                break;
            case 9:
                addWithCarry();
                break;                
            case 10:
                subtract();
                break;
            case 11:
                subtractWithBorrow();
                break;                
            case 12:
                and();
                break;
            case 13:
                or();
                break;
            case 14:
                xor();
                break;
            default:
                throw new RuntimeException("Invalid function.");
        }        
        A &= 0xFF;
        zero = (A == 0);
        negative = (A & 0x80) != 0;
    }
    
    private void invert() {
        A = ~C;
    }
    
    private void negate() {
        A = -C;
    }    
    
    private void unsignedRightShift() {
        carry = (C & 0x01) != 0;
        A = C >>> 1;
    }
    
    private void signedRightShift() {
        carry = (C & 0x01) != 0;
        A = C >> 1;
    }
    
    private void leftShift() {
        carry = (C & 0x80) != 0;
        A = C << 1;
    }
    
    private void decrement() {
        A = C - 1;
    }
    
    private void increment() {
        A = C + 1;
    }

    private void subtract() {
        A = C - D;
        carry = (A & 0x100) != 0;
    }
    
    private void subtractWithBorrow() {
        A = C - D - (carry ? 1 : 0);
        carry = (A & 0x100) != 0;
    }    
    
    private void add() {
        A = C + D;
        carry = (A & 0x100) != 0;
    }
    
    private void addWithCarry() {
        A = C + D + (carry ? 1 : 0);
        carry = (A & 0x100) != 0;
    }    
    
    private void and() {
        A = C & D;
    }
    
    private void or() {
        A = C | D;
    }    
    
    private void xor() {
        A = C ^ D;
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
            E = P >> 8;
            F = P & 0xFF;
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
        MEMORY[address & 0xFFFF] = value;
    }
    
    private int readMemory(final int address) {
        return MEMORY[address & 0xFFFF];
    }
    
    public static void main(final String... args) throws Exception {
        new Emulator().launch();
    }
}
