package tetriscircuits;

public class Structure {

    private final LockedTetrimino[] lockedTetriminos;
    private final Point[] inputs;
    private final Point[] outputs;
    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;

    public Structure(final LockedTetrimino[] lockedTetriminos, final Point[] inputs, final Point[] outputs, 
            final int minX, final int maxX, final int minY, final int maxY) {
        this.lockedTetriminos = lockedTetriminos;
        this.inputs = inputs;
        this.outputs = outputs;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public Point[] getInputs() {
        return inputs;
    }

    public Point[] getOutputs() {
        return outputs;
    }

    public LockedTetrimino[] getLockedTetriminos() {
        return lockedTetriminos;
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