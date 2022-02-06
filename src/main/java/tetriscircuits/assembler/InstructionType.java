package tetriscircuits.assembler;

enum InstructionType {
    
    ADC,
    ADD,
    AND,    
    BCC(3),
    BCS(3),
    BEQ(3),
    BMI(3),
    BNE(3),
    BPL(3),
    CLC,
    DEC,    
    INC,
    JMP(3),    
    JSCC(3),
    JSCS(3),
    JSEQ(3),
    JSMI(3),
    JSNE(3),
    JSPL(3),
    JSR(3),    
    LD,
    OR,
    RTS,
    SBC,
    SEC,
    SET(2),
    SL,
    SSR,
    ST,
    SUB,
    TAB,
    TAC,
    TAD,
    TAE,
    TAF,
    TAMH,
    TAML,
    TBA,
    TBC,
    TBD,
    TBE,
    TBF,
    TBMH,
    TBML,
    TCA,
    TCB,
    TCD,
    TCE,
    TCF,
    TCMH,
    TCML,
    TDA,
    TDB,
    TDC,
    TDE,
    TDF,
    TDMH,
    TDML,
    TEA,
    TEB,
    TEC,
    TED,
    TEF,
    TEMH,
    TEML,
    TFA,
    TFB,
    TFC,
    TFD,
    TFE,
    TFMH,
    TFML,
    USR,
    XOR;
    
    private final int length;
    
    InstructionType() {
        this(1);
    }
    
    InstructionType(final int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }
}
