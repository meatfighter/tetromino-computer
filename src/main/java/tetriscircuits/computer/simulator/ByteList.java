package tetriscircuits.computer.simulator;

public final class ByteList {
    
    private final int[] bytes;
    
    public ByteList(final int length) {
        bytes = new int[length];
    }

    public int read(final int index) {
        return bytes[index];
    }
    
    public void write(final int index, final int value) {
        bytes[index] = value;
    }
    
    public void apply(final int index, final int[] map) {
        bytes[index] = map[bytes[index]];
    }
    
    public void apply(final int index, final int[][][] map) {
        final int[] m = map[bytes[index]][bytes[index + 1]];
        bytes[index] = m[0];
        bytes[index + 1] = m[1];
    }

    public void apply(final int index, final int[][][][] map) {
        final int[] m = map[bytes[index]][bytes[index + 1]][bytes[index + 2]];
        bytes[index] = m[0];
        bytes[index + 1] = m[1];
        bytes[index + 2] = m[2];
    }    
}
