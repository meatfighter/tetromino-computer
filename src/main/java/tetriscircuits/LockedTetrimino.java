package tetriscircuits;

public class LockedTetrimino {
    
    private final Tetrimino tetrimino;
    private final int x;
    private final int y;

    public LockedTetrimino(final Tetrimino tetrimino, final int x, final int y) {
        this.tetrimino = tetrimino;
        this.x = x;
        this.y = y;
    }

    public Tetrimino getTetrimino() {
        return tetrimino;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
