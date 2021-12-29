package tetriscircuits;

public class Playfield {
    
                                                // 2^:  0   1   2   3   4   5
    private static final int[] POWERS_OF_TWO = {  1,  2,  4,  8, 16, 32 };
    private static final int[] MASKS = { 0b000001, 0b000011, 0b000111, 0b001111, 0b011111, 0b111111 };
    
    private final int width;
    private final int height;
    private final int power;
    private final int mask;
    private final int shift;
    
    private final int[][] data;
    
    public Playfield(final int width, final int height, final int bitsPerCell) {
        
        outer: {
            for (int i = 0; i < POWERS_OF_TWO.length; ++i) {
                if (POWERS_OF_TWO[i] == bitsPerCell) {
                    power = i;
                    mask = MASKS[i];
                    break outer;
                }
            }
            throw new IllegalArgumentException("bisPerCell must be a power of 2.");
        }
        
        this.width = width;
        this.height = height;
        
        shift = 5 - power;
        
        int w = width >> shift;
        if ((w << shift) < width) {
            ++w;
        }
        data = new int[height][w];
    }
    
    public int get(final int x, final int y) {
        if (x < 0 || x >= width || y >= height) {
            return 1;
        }
        if (y < 0) {
            return 0;
        }
        return (data[y][x >> shift] >> ((x & 7) << power)) & 0x0F;
    }
}
