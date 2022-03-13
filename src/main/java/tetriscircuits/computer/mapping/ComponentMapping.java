package tetriscircuits.computer.mapping;

public class ComponentMapping {
    
    private final ComponentMappingType mappingType;
    private final int[] map;
    
    public ComponentMapping(final int inputBits) {
        if (inputBits > 16) {
            throw new IllegalArgumentException("Invalid number of input bits: " + inputBits);
        }
        map = new int[1 << inputBits];
        mappingType = ComponentMappingType.ANY;
    }
    
    public ComponentMapping(final ComponentMappingType mappingType) {        
        if (mappingType == ComponentMappingType.ANY) {
            throw new IllegalArgumentException("Invalid mapping type: " + mappingType);
        }
        map = new int[1 << 17];
        this.mappingType = mappingType;        
    }

    public ComponentMappingType getMappingType() {
        return mappingType;
    }

    public int[] getMap() {
        return map;
    }
    
    public int setInputBit(final int input, final int bit) {
        switch (mappingType) {
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