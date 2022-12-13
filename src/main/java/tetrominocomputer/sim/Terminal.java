package tetrominocomputer.sim;

public class Terminal implements Comparable<Terminal> {

    private final TerminalType type;
    private final String name;
    private final HorizontalLine[] horizontalLines;
    private final int minX;

    public Terminal(final TerminalType type, final String name, final HorizontalLine[] horizontalLines) {
        this.type = type;
        this.name = name;
        this.horizontalLines = horizontalLines;
        
        int min = Integer.MAX_VALUE;
        for (final HorizontalLine horizontalLine : horizontalLines) {
            if (min > horizontalLine.getMinX()) {
                min = horizontalLine.getMinX();
            }
        }
        minX = min;
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
    public int compareTo(final Terminal o) {
        return Integer.compare(minX, o.minX);
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