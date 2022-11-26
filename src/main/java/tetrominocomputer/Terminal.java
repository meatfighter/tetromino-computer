package tetrominocomputer;

public class Terminal {

    private final TerminalType type;
    private final String name;
    private final HorizontalLine[] horizontalLines;

    public Terminal(final TerminalType type, final String name, final HorizontalLine[] horizontalLines) {
        this.type = type;
        this.name = name;
        this.horizontalLines = horizontalLines;
    }

    public TerminalType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public HorizontalLine[] getHorizontalLines() {
        return horizontalLines;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder().append(type).append(' ').append(name);
        for (final HorizontalLine horizontalLine : horizontalLines) {
            sb.append(' ').append(horizontalLine);
        }        
        return sb.toString();
    }
}