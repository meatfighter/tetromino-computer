package tetriscircuits;

import org.apache.commons.lang3.tuple.Pair;

public class Instruction {
    
    private final Structure structure;
    
    private final int index;
    private final int rotation;
    
    private final int x;
    private final Integer y;
    private final Integer x2;

    public Instruction(final Pair<Integer,Integer> indexAndRotation, final Structure structure, final int x, 
            final Integer y, final Integer x2) {
        
        if (indexAndRotation == null) {
            this.index = indexAndRotation.getLeft();
            this.rotation = indexAndRotation.getRight();
            this.structure = null;
        } else {
            this.index = 0;
            this.rotation = 0;
            this.structure = structure;
        }
        this.x = x;
        this.y = y;
        this.x2 = x2;
    }

    public Structure getStructure() {
        return structure;
    }

    public int getIndex() {
        return index;
    }

    public int getRotation() {
        return rotation;
    }

    public int getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Integer getX2() {
        return x2;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (structure != null) {
            sb.append(structure.getName());
        } else {
            sb.append(Tetrimino.FINE_NAMES[index][rotation]);
        }
        sb.append(' ').append(x);
        if (y != null) {
            sb.append(' ').append(y);
        }
        if (x2 != null) {
            sb.append(' ').append(x2);
        }
        return sb.toString();
    }
}