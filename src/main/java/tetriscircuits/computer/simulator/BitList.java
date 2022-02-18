package tetriscircuits.computer.simulator;

public class BitList {

    private static final long[] MASKS = new long[65];
    
    static {
        long value = 0;
        for (int i = 0; i < MASKS.length; ++i) {
            MASKS[i] = value;
            value = (value << 1) | 1L;
        }
    }
    
    // 00 11 22 33
    // AA BB CC DD EE FF GG HH
    
    // 0000 0000 0011 1111 1111 2222 2222 2233
    // 0123 4567 8901 2345 6789 0123 4567 8901
    // aaaa aaaa bbbb bbbb cccc cccc dddd dddd
    
    private final long[] bits;
    
    public BitList(final int size) {
        bits = new long[(size >> 5) + 2];
    }
    
    public int read(final int index, final int length) {
        return (int)(MASKS[length] & (bits[index >> 5] >> (64 - (index & 0x1F) - length)));
    }
    
    public void write(final int index, final int length, final int value) {
        final int i = index >> 5;
        final int shift = 64 - (index & 0x1F) - length;
        bits[i] = (bits[i] & ~(MASKS[length] << shift)) | (((long)value) << shift);
        bits[i + 1] = (bits[i] << 32) | (0x00000000_FFFFFFFFL & bits[i + 1]);
    }
    
    public void apply(final int index, final int length, final int[] map) {
        write(index, length, map[read(index, length)]);
    }
}
