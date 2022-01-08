package tetriscircuits;

public class LockedElement {

    private final int x;
    private final int y;
    
    private final Tetrimino tetrimino;
    
    private final String componentName;
    private final boolean[] inputValues;
    private final boolean[] outputValues;

    public LockedElement(final Tetrimino tetrimino, final int x, final int y) {
        this.tetrimino = tetrimino;
        this.x = x;
        this.y = y;
        this.componentName = null;
        this.inputValues = null;
        this.outputValues = null;
    }
    
    public LockedElement(final String componentName, final int x, final int y, final boolean[] inputValues, 
            final boolean[] outputValues) {
        this.componentName = componentName;
        this.x = x;
        this.y = y;
        this.tetrimino = null;
        this.inputValues = inputValues;
        this.outputValues = outputValues;
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

    public String getComponentName() {
        return componentName;
    }

    public boolean[] getInputValues() {
        return inputValues;
    }

    public boolean[] getOutputValues() {
        return outputValues;
    }
}
