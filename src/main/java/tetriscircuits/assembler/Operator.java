package tetriscircuits.assembler;

enum Operator {
    
    ADD(0b0001_0010),
    AND(0b0001_0110),
    BEQ(0b0010_0011, 3),
    BNE(0b0010_0010, 3),
    CLA(0b0000_00_00),
    CLB(0b0000_01_01),
    CLM(0b0000_10_10),
    CLN(0b0000_11_11),
    DEC(0b0001_0001),
    INC(0b0001_0000),
    JMP(0b0010_0000, 3),
    LDA(0b0100_0000),
    LDB(0b0100_0001),    
    LSH(0b0001_0100),
    NOT(0b0001_1001),
    OR(0b0001_0111),
    RSH(0b0001_0101),
    SEA(0b0101_0000, 2),
    SEB(0b0101_0001, 2),
    SMN(0b0110_0000, 3),
    STA(0b0011_0000),
    STB(0b0011_0001),
    SUB(0b0001_0011),
    TAB(0b0000_00_01),
    TAM(0b0000_00_10),
    TAN(0b0000_00_11),
    TBA(0b0000_01_00),
    TBM(0b0000_01_10),
    TBN(0b0000_01_11),
    TMA(0b0000_10_00),
    TMB(0b0000_10_01),
    TMN(0b0000_10_11),
    TNA(0b0000_11_00),
    TNB(0b0000_11_01),
    TNM(0b0000_11_10),
    XOR(0b0001_1000);
    
    private final int opcode;
    private final int length;
    
    Operator(final int opcode) {
        this(opcode, 1);
    }
    
    Operator(final int opcode, final int length) {
        this.opcode = opcode;
        this.length = length;
    }

    public int getLength() {
        return length;
    }
    
    public int getOpcode() {
        return opcode;
    }
}
