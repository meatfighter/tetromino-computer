package tetrominocomputer.computer.memorycode;

public class ParseException extends RuntimeException {

    private final String filename;
    private final int lineNumber;
    private final int lineColumn;
    
    public ParseException(final Token token, final String message) {
        this(token.getFilename(), token.getLineNumber(), token.getLineColumn(), message);
    }
    
    public ParseException(final String filename, final int lineNumber, final int lineColumn, final String message) {
        super(message);
        this.filename = filename;
        this.lineNumber = lineNumber;
        this.lineColumn = lineColumn;
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

    @Override
    public String toString() {
        return String.format("%s:[%d,%d] error: %s", filename, lineNumber, lineColumn, getMessage());
    }
}