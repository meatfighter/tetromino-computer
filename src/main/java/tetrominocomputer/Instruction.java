package tetrominocomputer;

public class Instruction {
    
    private final String componentName;
    private final Tetromino tetromino;
    
    private final String alias;
    private final int[] moves;

    public Instruction(final Tetromino tetromino, final String componentName, final String alias, final int[] moves) {
        this.tetromino = tetromino;
        this.componentName = componentName;
        this.alias = alias;
        this.moves = moves;
    }

    public Tetromino getTetromino() {
        return tetromino;
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
            sb.append(tetromino.getName());
        }
        for (final int move : moves) {
            sb.append(' ').append(move);
        }
        return sb.toString();
    }
}