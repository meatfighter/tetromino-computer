package tetriscircuits;

public class HorizontalLine {

    private final int minX;
    private final int maxX;
    private final int y;
    
    public HorizontalLine(final int x, final int y) {
        this(x, x, y);
    }

    public HorizontalLine(final int minX, final int maxX, final int y) {
        if (minX > maxX) {
            this.minX = maxX;
            this.maxX = minX;            
        } else {
            this.minX = minX;
            this.maxX = maxX;
        }
        this.y = y;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getY() {
        return y;
    }
    
    @Override 
    public String toString() {
        return (minX == maxX) ? String.format("%d %d", minX, y) : String.format("%d..%d %d", minX, maxX, y);
    }
}
