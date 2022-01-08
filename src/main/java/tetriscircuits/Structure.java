package tetriscircuits;

public class Structure {

    private final LockedElement[] lockedTetriminos;
    private final TerminalRectangle[][] inputs;
    private final TerminalRectangle[][] outputs;
    private final boolean[] testBits;
    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;    

    public Structure(final LockedElement[] lockedTetriminos, final TerminalRectangle[][] inputs, 
            final TerminalRectangle[][] outputs, final boolean[] testBits, final int minX, final int maxX, 
            final int minY, final int maxY) {
        this.lockedTetriminos = lockedTetriminos;
        this.inputs = inputs;
        this.outputs = outputs;
        this.testBits = testBits;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public TerminalRectangle[][] getInputs() {
        return inputs;
    }

    public TerminalRectangle[][] getOutputs() {
        return outputs;
    }

    public LockedElement[] getLockedTetriminos() {
        return lockedTetriminos;
    }

    public boolean[] getTestBits() {
        return testBits;
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