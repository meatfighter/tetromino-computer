package tetrominocomputer.mc;

class Token {

    private final String filename;
    private final int lineNumber;
    private final int lineColumn;
    private final int length;
    
    private TokenType type;
    private String str;
    private int num;

    public Token(final String filename, final int lineNumber, final int lineColumn, final int length) {
        this.filename = filename;
        this.lineNumber = lineNumber;
        this.lineColumn = lineColumn;
        this.length = length;
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
  
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s:[%d,%d] %s", filename, lineNumber, lineColumn, type));
        switch (type) {
            case STRING:
                sb.append(": ").append(str);
                break;
            case NUMBER:
                sb.append(": ").append(num);
                break;
        }
        return sb.toString();
    }
}