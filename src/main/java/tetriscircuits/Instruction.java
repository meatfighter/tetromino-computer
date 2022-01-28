package tetriscircuits;

public class Instruction {
    
    private final String componentName;
    private final Tetrimino tetrimino;
    
    private final String alias;
    private final int[] moves;
    
    private final boolean flatten;

    public Instruction(final Tetrimino tetrimino, final String componentName, final String alias, final int[] moves, 
            final boolean flatten) {
        this.tetrimino = tetrimino;
        this.componentName = componentName;
        this.alias = alias;
        this.moves = moves;
        this.flatten = flatten;
    }

    public Tetrimino getTetrimino() {
        return tetrimino;
    }

    public String getComponentName() {
        return componentName;
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
            return String.format("flatten %d", moves[0]);
        }
        final StringBuilder sb = new StringBuilder();
        if (componentName != null) {
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