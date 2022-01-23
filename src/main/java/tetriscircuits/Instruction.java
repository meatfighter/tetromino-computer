package tetriscircuits;

public class Instruction {
    
    private final Component component;
    private final Tetrimino tetrimino;
    
    private final String alias;
    private final int[] moves;
    
    private final boolean flatten;

    public Instruction(final Tetrimino tetrimino, final Component component, final String alias, final int[] moves, 
            final boolean flatten) {
        this.tetrimino = tetrimino;
        this.component = component;
        this.alias = alias;
        this.moves = moves;
        this.flatten = flatten;
    }

    public Tetrimino getTetrimino() {
        return tetrimino;
    }

    public Component getComponent() {
        return component;
    }

    public String getAlias() {
        return alias;
    }

    public int[] getMoves() {
        return moves;
    }

    public boolean isFlatten() {
        return flatten;
    }

    @Override
    public String toString() {
        if (flatten) {
            return "flatten";
        }
        final StringBuilder sb = new StringBuilder();
        if (component != null) {
            sb.append(alias);
        } else {
            sb.append(tetrimino.getName());
        }
        for (final int move : moves) {
            sb.append(' ').append(move);
        }
        return sb.toString();
    }
}