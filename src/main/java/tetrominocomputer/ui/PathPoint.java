package tetrominocomputer.ui;

public class PathPoint {
    
    public final int x;
    public final int y;
    public final boolean right;
    public final boolean bottom;
    
    public PathPoint(final int x, final int y, final boolean right, final boolean bottom) {
        this.x = x;
        this.y = y;
        this.right = right;
        this.bottom = bottom;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isBottom() {
        return bottom;
    }
    
    @Override
    public String toString() {
        return String.format("%d %d %b %b", x, y, right, bottom);
    }    
}
