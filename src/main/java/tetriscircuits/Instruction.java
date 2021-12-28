package tetriscircuits;

public class Instruction {
    
    private final Structure structure;
    
    private final int tetriminoIndex;
    private final int rotation;
    
    private final int x;
    private final Integer y;
    private final Integer x2;

    public Instruction(final Structure structure, final int tetriminoIndex, final int rotation, final int x, 
            final Integer y, final Integer x2) {
        this.structure = structure;
        this.tetriminoIndex = tetriminoIndex;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
        this.x2 = x2;
    }

    public Instruction(final Structure structure, final int x) {
        this(structure, 0, 0, x, null, null);
    }
    
    public Instruction(final Structure structure, final int x, final int y) {
        this(structure, 0, 0, x, y, null);
    }
    
    public Instruction(final Structure structure, final int x, final int y, final int x2) {
        this(structure, 0, 0, x, y, x2);
    }
    
    public Instruction(final int tetriminoIndex, final int rotation, final int x) {
        this(null, tetriminoIndex, rotation, x, null, null);
    }
    
    public Instruction(final int tetriminoIndex, final int rotation, final int x, final int y) {
        this(null, tetriminoIndex, rotation, x, y, null);
    }
    
    public Instruction(final int tetriminoIndex, final int rotation, final int x, final int y, final int x2) {
        this(null, tetriminoIndex, rotation, x, y, x2);
    }

    public Structure getStructure() {
        return structure;
    }

    public int getTetriminoIndex() {
        return tetriminoIndex;
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
}