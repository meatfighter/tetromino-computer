package tetriscircuits;

public class Instruction {
    
    private final String componentName;
    private final Tetrimino tetrimino;
    
    private final String alias;
    private final int[] moves;

    public Instruction(final Tetrimino tetrimino, final String componentName, final String alias, final int[] moves) {
        this.tetrimino = tetrimino;
        this.componentName = componentName;
        this.alias = alias;
        this.moves = moves;
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

    @Override
    public String toString() {
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