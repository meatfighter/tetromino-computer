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
}