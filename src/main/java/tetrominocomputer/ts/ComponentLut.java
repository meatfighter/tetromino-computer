package tetrominocomputer.ts;

public class ComponentLut {
    
    private final ComponentLutType lutType;
    private final int[] table; // the index may be compressed, but the value is not
    
    public ComponentLut(final int inputBits) {
        table = new int[1 << inputBits];
        lutType = ComponentLutType.ANY;
    }
    
    public ComponentLut(final ComponentLutType lutType) {        
        if (lutType == ComponentLutType.ANY) {
            throw new IllegalArgumentException("Invalid LUT type: " + lutType);
        }
        table = new int[1 << 17];
        this.lutType = lutType;        
    }

    public ComponentLutType getLutType() {
        return lutType;
    }

    public int[] getTable() {
        return table;
    }
    
    public int setInputBit(final int input, final int bit) {
        switch (lutType) {
            case ANY:
                return input | (1 << bit);
            case BIT_TWO_BYTES:
                if (bit <= 16) {
                    return input | (1 << bit);
                }
                break;
            case TWO_BYTES_BIT:
                if (bit == 0) {
                    return input | 1;
                }
                if (bit >= 8) {
                    return input | (1 << (bit - 7));
                }
                break;
        }
        return input;
    }
}