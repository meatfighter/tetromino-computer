package tetrominocomputer.computer.mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ByteMappingType {
    
    ONE_BYTE(1),
    TWO_BYTES(2),
    BIT_TWO_BYTES(3),
    TWO_BYTES_BIT(4),
    THREE_BYTES(5);
    
    private static final Map<Integer, ByteMappingType> mapTypes;

    static {
        final Map<Integer, ByteMappingType> types = new HashMap<>();
        for (ByteMappingType mapType : ByteMappingType.values()) {
            types.put(mapType.value, mapType);
        }
        mapTypes = Collections.unmodifiableMap(types);
    }

    public static ByteMappingType fromValue(final int value) {
        return mapTypes.get(value);
    }    
    
    private final int value;    
    
    ByteMappingType(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}