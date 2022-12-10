package tetrominocomputer.mc;

public class Instruction {
    
    private final String component;
    private final int index;
    
    public Instruction(final String component, final int index) {
        this.component = component;
        this.index = index;
    }

    public String getComponent() {
        return component;
    }

    public int getIndex() {
        return index;
    }
    
    @Override
    public String toString() {
        return String.format("%s %d", component, index);
    }
}
