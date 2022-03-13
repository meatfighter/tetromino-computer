package tetriscircuits.computer.mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum MapType {
    
    ONE_BYTE(1),
    TWO_BYTES(2),
    BIT_TWO_BYTES(3),
    TWO_BYTES_BIT(4);
    
    private static final Map<Integer, MapType> mapTypes;

    static {
        final Map<Integer, MapType> types = new HashMap<>();
        for (MapType mapType : MapType.values()) {
            types.put(mapType.value, mapType);
        }
        mapTypes = Collections.unmodifiableMap(types);
    }

    public static MapType fromValue(final int value) {
        return mapTypes.get(value);
    }    
    
    private final int value;    
    
    MapType(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}