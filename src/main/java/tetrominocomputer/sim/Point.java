package tetrominocomputer.sim;

public class Point {

    public final int x;
    public final int y;
    
    public Point(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    @Override
    public String toString() {
        return String.format("%d %d", x, y);
    }
}
