package tetriscircuits;

public class Extents {
    
    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;

    public Extents(final int minX, final int maxX, final int minY, final int maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }
    
    @Override
    public String toString() {
        return String.format("%d %d %d %d", minX, maxX, minY, maxY);
    }
}