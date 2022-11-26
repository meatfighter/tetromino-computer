package tetriscircuits;

public class Structure {

    private final Tetromino tetromino;
    private final int x;
    private final int y;    

    private final String componentName;
    private final TerminalRectangle[][] inputs;
    private final TerminalRectangle[][] outputs;      
    
    private final Structure[] structures;
    
    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;
    
    public Structure(final Tetromino tetromino, final int x, final int y) {
        this(tetromino, x, y, null, null, null, tetromino.getMinX(), tetromino.getMaxX(), tetromino.getMinY(), 
                tetromino.getMaxY(), null);
    }
    
    public Structure(final String componentName, final int x, final int y, final TerminalRectangle[][] inputs, 
            final TerminalRectangle[][] outputs, final int minX, final int maxX, final int minY, final int maxY) {
        this(null, x, y, componentName, inputs, outputs, minX, maxX, minY, maxY, null);
    }    
    
    public Structure(final String componentName, final int x, final int y, final TerminalRectangle[][] inputs, 
            final TerminalRectangle[][] outputs, final int minX, final int maxX, final int minY, final int maxY,
            final Structure[] structures) {
        this(null, x, y, componentName, inputs, outputs, minX, maxX, minY, maxY, structures);
    }

    public Structure(final Tetromino tetromino, final int x, final int y, final String componentName, 
            final TerminalRectangle[][] inputs, final TerminalRectangle[][] outputs,
            final int minX, final int maxX, final int minY, final int maxY, final Structure[] structures) {
        this.tetromino = tetromino;
        this.x = x;
        this.y = y;
        this.componentName = componentName;
        this.inputs = inputs;
        this.outputs = outputs;
        this.structures = structures;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public Tetromino getTetromino() {
        return tetromino;
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

    public TerminalRectangle[][] getInputs() {
        return inputs;
    }

    public TerminalRectangle[][] getOutputs() {
        return outputs;
    }

    public Structure[] getStructures() {
        return structures;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }
}