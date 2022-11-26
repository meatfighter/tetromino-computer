package tetrominocomputer.assembler;

enum Operator {
    
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

    SEA(0b0101_0000, 2),
    SEB(0b0101_0001, 2),
    SMN(0b0010_1111, 3),    
    
    LDA(0b0100_0000),
    LDB(0b0100_0001),    
       
    STA(0b0011_0000),
    STB(0b0011_0001),    
    
    ADD(0b0001_0000),    
    AND(0b0001_0001),
    DEC(0b0001_0010),
    INC(0b0001_0011),
    LS2(0b0001_0100),
    LS3(0b0001_0101),
    LS4(0b0001_0110),
    OR (0b0001_0111),
    RS1(0b0001_1000),
    RS5(0b0001_1001),
    SUB(0b0001_1010),
    XOR(0b0001_1011),    
    
    JMP(0b0010_0000, 3),   
    BEQ(0b0010_0011, 3),
    BMI(0b0010_0101, 3),
    BNE(0b0010_0010, 3),
    BPL(0b0010_0100, 3),    
    JSR(0b0010_1000, 3), 
    
    RTS(0b0111_0000),
    
    PRINT_REGS(0b1111_1110), // TODO REMOVE
    PRINT(0b1111_1111, 3); // TODO REMOVE
    
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
