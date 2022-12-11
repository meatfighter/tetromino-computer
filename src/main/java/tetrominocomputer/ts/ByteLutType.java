package tetrominocomputer.ts;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ByteLutType {
    
    ONE_BYTE(1),
    TWO_BYTES(2),
    BIT_TWO_BYTES(3),
    TWO_BYTES_BIT(4),
    THREE_BYTES(5);
    
    private static final Map<Integer, ByteLutType> lutTypes;

    static {
        final Map<Integer, ByteLutType> types = new HashMap<>();
        for (final ByteLutType lutType : ByteLutType.values()) {
            types.put(lutType.value, lutType);
        }
        lutTypes = Collections.unmodifiableMap(types);
    }

    public static ByteLutType fromValue(final int value) {
        return lutTypes.get(value);
    }    
    
    private final int value;    
    
    ByteLutType(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}