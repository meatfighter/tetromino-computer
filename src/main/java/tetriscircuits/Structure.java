package tetriscircuits;

public class Structure {
    
    private final int blockColorIndex;

    private final Tetrimino tetrimino;
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
    
    public Structure(final int blockColorIndex, final int x, final int y) {
        this(null, x, y, null, null, null, 0, 0, 0, 0, null, blockColorIndex);
    }
    
    public Structure(final Tetrimino tetrimino, final int x, final int y) {
        this(tetrimino, x, y, null, null, null, tetrimino.getMinX(), tetrimino.getMaxX(), tetrimino.getMinY(), 
                tetrimino.getMaxY(), null, -1);
    }
    
    public Structure(final String componentName, final int x, final int y, final TerminalRectangle[][] inputs, 
            final TerminalRectangle[][] outputs, final int minX, final int maxX, final int minY, final int maxY) {
        this(null, x, y, componentName, inputs, outputs, minX, maxX, minY, maxY, null, -1);
    }    
    
    public Structure(final String componentName, final int x, final int y, final TerminalRectangle[][] inputs, 
            final TerminalRectangle[][] outputs, final int minX, final int maxX, final int minY, final int maxY,
            final Structure[] structures) {
        this(null, x, y, componentName, inputs, outputs, minX, maxX, minY, maxY, structures, -1);
    }

    public Structure(final Tetrimino tetrimino, final int x, final int y, final String componentName, 
            final TerminalRectangle[][] inputs, final TerminalRectangle[][] outputs,
            final int minX, final int maxX, final int minY, final int maxY, final Structure[] structures,
            final int blockColorIndex) {
        this.tetrimino = tetrimino;
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
        this.blockColorIndex = blockColorIndex;
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

    public int getBlockColorIndex() {
        return blockColorIndex;
    }
}