package tetrominocomputer.computer.simulator;

public class BitList {

    private static final int[] MASKS = new int[33];
    
    static {
        int value = 0;
        for (int i = 0; i < MASKS.length; ++i) {
            MASKS[i] = value;
            value = (value << 1) | 1;
        }
    }
    
    //       00 11
    //       AA BB CC DD   
    // AA BB CC DD AA BB CC DD
    
    private final int[] bits;
    
    public BitList(final int size) {
        bits = new int[(size >> 4) + 3];
    }
    
    public int read(final int i, final int length) {
        final int index = i + 32;
        return (int)(MASKS[length] & (bits[index >> 4] >> (32 - (index & 0x0F) - length)));
    }
    
    public void write(final int i, final int length, final int value) {
        final int index = i + 32;
        final int j = index >> 4;
        final int shift = 32 - (index & 0x0F) - length;
        bits[j] = (bits[j] & ~(MASKS[length] << shift)) | (value << shift);        
        bits[j - 1] = (0xFFFF_0000 & bits[j - 1]) | (bits[j] >>> 16);
        bits[j + 1] = (bits[j] << 16) | (0x0000_FFFF & bits[j + 1]);
    }
    
    public void apply(final int index, final int length, final int[] map) {
        write(index, length, map[read(index, length)]);
    }
}
