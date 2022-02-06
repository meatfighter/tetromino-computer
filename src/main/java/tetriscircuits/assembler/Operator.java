package tetriscircuits.assembler;

enum Operator {
    
    ADC(0b0100_1001),
    ADD(0b0100_1000),
    AND(0b0100_1100),    
    BCC(0b10000_010, 3),
    BCS(0b10000_011, 3),
    BEQ(0b10000_101, 3),
    BMI(0b10000_111, 3),
    BNE(0b10000_100, 3),
    BPL(0b10000_110, 3),
    CLC(0b11110_010),
    CLN(0b11110_110),
    CLZ(0b11110_100),
    DEC(0b0100_0011),    
    INC(0b0100_0010),
    JMP(0b1000_0000, 3),    
    JSCC(0b10001_010, 3),
    JSCS(0b10001_011, 3),
    JSEQ(0b10001_101, 3),
    JSMI(0b10001_111, 3),
    JSNE(0b10001_100, 3),
    JSPL(0b10001_110, 3),
    JSR(0b10001_000, 3),    
    LD(0b1101_0000),
    OR(0b0100_1101),
    RTS(0b1111_1000),
    SBC(0b0100_1011),
    SEC(0b11110_011),
    SEN(0b11110_111),
    SEZ(0b11110_101),
    SET(2),
    SL(0b0100_0110),
    SSR(0b0100_0101),
    ST(0b1110_0000),
    SUB(0b0100_1010),
    TAB(0b00_000_001),
    TAC(0b00_000_010),
    TAD(0b00_000_011),
    TAE(0b00_000_100),
    TAF(0b00_000_101),
    TAMH(0b00_000_110),
    TAML(0b00_000_111),
    TBA(0b00_001_000),
    TBC(0b00_001_010),
    TBD(0b00_001_011),
    TBE(0b00_001_100),
    TBF(0b00_001_101),
    TBMH(0b00_001_110),
    TBML(0b00_001_111),
    TCA(0b00_010_000),
    TCB(0b00_010_001),
    TCD(0b00_010_011),
    TCE(0b00_010_100),
    TCF(0b00_010_101),
    TCMH(0b00_010_110),
    TCML(0b00_010_111),
    TDA(0b00_011_000),
    TDB(0b00_011_001),
    TDC(0b00_011_010),
    TDE(0b00_011_100),
    TDF(0b00_011_101),
    TDMH(0b00_011_110),
    TDML(0b00_011_111),
    TEA(0b00_100_000),
    TEB(0b00_100_001),
    TEC(0b00_100_010),
    TED(0b00_100_011),
    TEF(0b00_100_101),
    TEMH(0b00_100_110),
    TEML(0b00_100_111),
    TFA(0b00_101_000),
    TFB(0b00_101_001),
    TFC(0b00_101_010),
    TFD(0b00_101_011),
    TFE(0b00_101_100),
    TFMH(0b00_101_110),
    TFML(0b00_101_111),
    TMHA(0b00_110_000),
    TMHB(0b00_110_001),
    TMHC(0b00_110_010),
    TMHD(0b00_110_011),
    TMHE(0b00_110_100),
    TMHF(0b00_110_101),
    TMHML(0b00_110_111),
    TMLA(0b00_111_000),
    TMLB(0b00_111_001),
    TMLC(0b00_111_010),
    TMLD(0b00_111_011),
    TMLE(0b00_111_100),
    TMLF(0b00_111_101),
    TMLMH(0b00_111_110),
    USR(0b0100_0100),
    XOR(0b0100_1110);
    
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
