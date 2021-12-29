package tetriscircuits;

import org.apache.commons.lang3.tuple.Pair;

public class Instruction {
    
    private final Component component;
    
    private final int index;
    private final int rotation;
    
    private final int[] moves;

    public Instruction(final Pair<Integer,Integer> indexAndRotation, final Component component, final int[] moves) {
        
        if (indexAndRotation != null) {
            this.index = indexAndRotation.getLeft();
            this.rotation = indexAndRotation.getRight();
            this.component = null;
        } else {
            this.index = 0;
            this.rotation = 0;
            this.component = component;
        }
        this.moves = moves;
    }

    public Component getComponent() {
        return component;
    }

    public int getIndex() {
        return index;
    }

    public int getRotation() {
        return rotation;
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
            sb.append(Tetrimino.FINE_NAMES[index][rotation]);
        }
        for (final int move : moves) {
            sb.append(' ').append(move);
        }
        return sb.toString();
    }
}