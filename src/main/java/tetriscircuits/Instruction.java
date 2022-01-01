package tetriscircuits;

public class Instruction {
    
    private final Component component;
    private final Tetrimino tetrimino;
    
    private final int[] moves;

    public Instruction(final Tetrimino tetrimino, final Component component, final int[] moves) {
        this.tetrimino = tetrimino;
        this.component = component;
        this.moves = moves;
    }

    public Tetrimino getTetrimino() {
        return tetrimino;
    }

    public Component getComponent() {
        return component;
    }

    public int[] getMoves() {
        return moves;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (component != null) {
            sb.append(component.getName());
        } else {
            sb.append(tetrimino.getName());
        }
        for (final int move : moves) {
            sb.append(' ').append(move);
        }
        return sb.toString();
    }
}