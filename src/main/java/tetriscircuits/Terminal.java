package tetriscircuits;

public class Terminal {

    private final TerminalType type;
    private final String name;
    private final Range[] ranges;

    public Terminal(final TerminalType type, final String name, final Range[] ranges) {
        this.type = type;
        this.name = name;
        this.ranges = ranges;
    }

    public TerminalType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Range[] getRanges() {
        return ranges;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(type).append(' ').append(name);
        for (final Range range : ranges) {
            sb.append(' ').append(range);
        }
        return sb.toString();
    }
}