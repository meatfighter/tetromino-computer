package tetrominocomputer.asm;

public class Token {

    private final String filename;
    private final int lineNumber;
    private final int lineColumn;
    private final String rawStr;
    private final int length;
    
    private TokenType type;
    private String str;
    private int num;
    private Integer offset;
    private Operator operator;    

    public Token(final String filename, final int lineNumber, final int lineColumn, final String rawStr) {
        this.filename = filename;
        this.lineNumber = lineNumber;
        this.lineColumn = lineColumn;
        this.rawStr = rawStr;
        this.length = rawStr.length();
    }    

    public String getFilename() {
        return filename;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getLineColumn() {
        return lineColumn;
    }

    public int getLength() {
        return length;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(final TokenType type) {
        this.type = type;
    }

    public String getStr() {
        return str;
    }

    public void setStr(final String str) {
        this.str = str;
    }

    public int getNum() {
        return num;
    }

    public void setNum(final int num) {
        this.num = num;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(final Integer offset) {
        this.offset = offset;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getRawStr() {
        return rawStr;
    }
}