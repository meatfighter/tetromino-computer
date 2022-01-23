package tetriscircuits;

public class Playfield {
    
                                          // 2^:  0   1   2   3   4   5
    private static final int[] POWERS_OF_TWO = {  1,  2,  4,  8, 16, 32 };
    private static final int[] MASKS = { 0x0000_0001, 0x0000_0003, 0x0000_000F, 0x0000_00FF, 0x0000_FFFF, 0xFFFF_FFFF };
    
    private final int width;
    private final int height;
    private final int power;
    private final int mask;
    private final int mask2;
    private final int shift;
    
    private final int[][] data;
    
    private int minX;
    private int maxX;
    private int minY;
    
    private int[] terminalYs;
    
    public Playfield(final int width, final int height, final int bitsPerCell) {
        
        outer: {
            for (int i = 0; i < POWERS_OF_TWO.length; ++i) {
                if (POWERS_OF_TWO[i] == bitsPerCell) {
                    power = i;
                    mask = MASKS[i];
                    break outer;
                }
            }
            throw new IllegalArgumentException("bitsPerCell must be either 1, 2, 4, 8, 16, or 32.");
        }
        
        this.width = width;
        this.height = height;
                
        shift = 5 - power;
        mask2 = POWERS_OF_TWO[shift] - 1;
        int w = width >> shift;
        if ((width & mask2) != 0) {
            ++w;
        }
        data = new int[height][w];
        
        minX = maxX = width >> 1;
        minY = height - 1;
        
        terminalYs = new int[width];
        for (int i = width - 1; i >= 0; --i) {
            terminalYs[i] = height - 1;
        }
    }
    
    public int[] getTerminalYs() {
        return terminalYs;
    }

    public int getMaxValue() {
        return mask;
    }
    
    public void clear() {
        for (int y = height - 1; y >= minY; --y) {
            final int[] row = data[y];
            for (int x = minX; x <= maxX; ++x) {
                row[x >> shift] = 0;
            }
        }
        minX = maxX = width >> 1;
        minY = height - 1;        
    }
    
    public void flatten() {
        minX = maxX = width >> 1;
        minY = height - 1;
        for (int x = width - 1; x >= 0; --x) {
            if (terminalYs[x] < height - 1) {                
                set(x, height - 1, get(x, terminalYs[x] - 1));
                set(x, height - 2, get(x, terminalYs[x]));
            }
        }
        for (int y = height - 3; y >= minY; --y) {
            final int[] row = data[y];
            for (int x = minX; x <= maxX; ++x) {
                row[x >> shift] = 0;
            }
        }
    }
    
    public boolean isEmpty(final int x, final int y) {
        return get(x, y) == 0;
    }    
    
    public boolean isSolid(final int x, final int y) {
        return get(x, y) != 0;
    }
    
    public int get(final int x, final int y) {
        if (x < 0 || x >= width || y >= height) {
            return mask;
        }
        if (y < 0) {
            return 0;
        }
        return (data[y][x >> shift] >> ((x & mask2) << power)) & mask;
    }
    
    public void set(final int x, final int y) {
        set(x, y, mask2);
    }
    
    public void set(final int x, final int y, final int color) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return;
        }
        if (x < minX) {
            minX = x;
        }
        if (x > maxX) {
            maxX = x;
        }
        final int[] row = data[y];
        final int s = (x & mask2) << power;
        row[x >> shift] = (row[x >> shift] & ~(mask << s)) | ((color & mask) << s);
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                final int color = get(x, y);
                if (color == 0) {
                    sb.append('.');
                } else {
                    sb.append((char)('A' + color - 1));
                }
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }    
}
