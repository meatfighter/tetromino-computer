package tetrominocomputer.sim;

public class TerminalRectangle {

    public final int x;
    public final int y;
    public final int width;
    final TerminalState state;
    
    public TerminalRectangle(final int x, final int y, final int width, final TerminalState state) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.state = state;
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
    
    public TerminalState getState() {
        return state;
    }
    
    @Override
    public String toString() {
        return String.format("%d %d %d %s", x, y, width, state);
    }    
}
