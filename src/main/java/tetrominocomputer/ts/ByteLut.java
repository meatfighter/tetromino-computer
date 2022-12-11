package tetrominocomputer.ts;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteLut {
    
    public static ByteLut read(final InputStream in) throws IOException {
        
        final int lutTypeValue = in.read();
        if (lutTypeValue < 0) {
            throw new IOException("Unexpected end of file.");
        }
        
        final ByteLutType lutType = ByteLutType.fromValue(lutTypeValue);
        if (lutType == null) {
            throw new IOException("Invalid LUT type.");
        }
        
        final int length;
        switch (lutType) {
            case ONE_BYTE:
                length = 256;
                break;
            case TWO_BYTES:
                length = 256 * 256 * 2;
                break;
            case THREE_BYTES:
                length = 256 * 256 * 256 * 3;
                break;                
            default:    
                length = 256 * 256 * 2 * 3;
                break;
        }
        final byte[] table = new byte[length];
        
        for (int i = 0; i < length; ++i) {
            final int b = in.read();
            if (b < 0) {
                throw new IOException("Unexpected end of file.");
            }
            table[i] = (byte)b;
        }
        
        return new ByteLut(lutType, table);
    }
    
    private final ByteLutType lutType;
    private final byte[] table;
    
    public ByteLut(final ComponentLut componentLut) {
        final int[] table = componentLut.getTable();
        switch (componentLut.getLutType()) {
            case BIT_TWO_BYTES:
                lutType = ByteLutType.BIT_TWO_BYTES;
                this.table = new byte[2 * 256 * 256 * 3];
                for (int i = 0; i < 2; ++i) {
                    for (int j = 0; j < 256; ++j) {
                        for (int k = 0; k < 256; ++k) {
                            int index = (i << 16) | (j << 8) | k;
                            final int value = table[index];
                            index *= 3;
                            this.table[index] = (byte)(0x01 & (value >> 16));
                            this.table[index + 1] = (byte)(0xFF & (value >> 8));
                            this.table[index + 2] = (byte)(0xFF & value);
                        }
                    }
                }
                break;
            case TWO_BYTES_BIT:
                lutType = ByteLutType.TWO_BYTES_BIT;
                this.table = new byte[256 * 256 * 2 * 3];
                for (int i = 0; i < 256; ++i) {
                    for (int j = 0; j < 256; ++j) {
                        for (int k = 0; k < 2; ++k) {
                            int index = (i << 9) | (j << 1) | k;
                            final int value = table[index];
                            index *= 3;
                            this.table[index] = (byte)(0xFF & (value >> 16));
                            this.table[index + 1] = (byte)(0xFF & (value >> 8));
                            this.table[index + 2] = (byte)(0x01 & value);
                        }
                    }
                }
                break;
            default:
                switch (table.length) {
                    case 256:
                        lutType = ByteLutType.ONE_BYTE;
                        this.table = new byte[256];
                        System.arraycopy(table, 0, this.table, 0, 256);
                        break;
                    case 256 * 256:
                        lutType = ByteLutType.TWO_BYTES;
                        this.table = new byte[256 * 256 * 2];
                        for (int i = 0; i < 256; ++i) {                            
                            for (int j = 0; j < 256; ++j) {
                                int index = (i << 8) | j;
                                final int value = table[index];
                                index <<= 1;
                                this.table[index] = (byte)(0xFF & (value >> 8));
                                this.table[index + 1] = (byte)(0xFF & value);
                            }
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Map not byte aligned.");
                }
                break;                
        }
    }

    public ByteLut(final ByteLutType lutType, final byte[] table) {
        this.lutType = lutType;
        this.table = table;
    }
    
    public ByteLutType getLutType() {
        return lutType;
    }
    
    public byte[] getTable() {
        return table;
    }
    
    public void write(final OutputStream out) throws IOException {
        out.write(lutType.getValue());
        for (int i = 0; i < table.length; ++i) {
            out.write(table[i]);
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ByteLut)) {
            return false;
        }
        
        final ByteLut lut = (ByteLut)obj;
        
        if (lutType != lut.lutType) {
            return false;
        }
        
        if (table == null) {
            return lut.table == null;
        }
        
        if (table.length != lut.table.length) {
            return false;
        }
        
        for (int i = table.length - 1; i >= 0; --i) {
            if (table[i] != lut.table[i]) {
                return false;
            }
        }
        
        return true;
    }
}