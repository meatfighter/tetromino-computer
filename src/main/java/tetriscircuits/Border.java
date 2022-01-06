package tetriscircuits;

public class Border {
    
    private final Range rangeX;
    private final int maxY;

    public Border(final Range rangeX, final int maxY) {
        this.rangeX = rangeX;
        this.maxY = maxY;
    }

    public Range getRangeX() {
        return rangeX;
    }

    public int getMaxY() {
        return maxY;
    }
    
    @Override
    public String toString() {
        return String.format("border %s %d", rangeX, maxY);
    }
}
