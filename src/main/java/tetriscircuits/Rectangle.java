package tetriscircuits;

public class Rectangle {

    public final int x;
    public final int y;
    public final int width;
    public final int height;
    
    public Rectangle(final int x, final int y, final int width, final int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    @Override
    public String toString() {
        return String.format("%d %d %d %d", x, y, width, height);
    }    
}
