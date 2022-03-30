package tetriscircuits.computer.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteMapping2 {
    
    public static ByteMapping2 read(final InputStream in) throws IOException {
        
        final int mapTypeValue = in.read();
        if (mapTypeValue < 0) {
            throw new IOException("Unexpected end of file.");
        }
        
        final ByteMappingType mapType = ByteMappingType.fromValue(mapTypeValue);
        if (mapType == null) {
            throw new IOException("Invalid map type.");
        }
        
        final int length;
        switch (mapType) {
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
        final byte[] map = new byte[length];
        
        for (int i = 0; i < length; ++i) {
            final int b = in.read();
            if (b < 0) {
                throw new IOException("Unexpected end of file.");
            }
            map[i] = (byte)b;
        }
        
        return new ByteMapping2(mapType, map);
    }
    
    private final ByteMappingType mappingType;
    private final byte[] map;
    
    public ByteMapping2(final ComponentMapping componentMapping) {
        final int[] map = componentMapping.getMap();
        switch (componentMapping.getMappingType()) {
            case BIT_TWO_BYTES:
                mappingType = ByteMappingType.BIT_TWO_BYTES;
                this.map = new byte[2 * 256 * 256 * 3];
                for (int i = 0; i < 2; ++i) {
                    for (int j = 0; j < 256; ++j) {
                        for (int k = 0; k < 256; ++k) {
                            int index = (i << 16) | (j << 8) | k;
                            final int value = map[index];
                            index *= 3;
                            this.map[index] = (byte)(0x01 & (value >> 16));
                            this.map[index + 1] = (byte)(0xFF & (value >> 8));
                            this.map[index + 2] = (byte)(0xFF & value);
                        }
                    }
                }
                break;
            case TWO_BYTES_BIT:
                mappingType = ByteMappingType.TWO_BYTES_BIT;
                this.map = new byte[256 * 256 * 2 * 3];
                for (int i = 0; i < 256; ++i) {
                    for (int j = 0; j < 256; ++j) {
                        for (int k = 0; k < 2; ++k) {
                            int index = (i << 9) | (j << 1) | k;
                            final int value = map[index];
                            index *= 3;
                            this.map[index] = (byte)(0xFF & (value >> 16));
                            this.map[index + 1] = (byte)(0xFF & (value >> 8));
                            this.map[index + 2] = (byte)(0x01 & value);
                        }
                    }
                }
                break;
            default:
                switch (map.length) {
                    case 256:
                        mappingType = ByteMappingType.ONE_BYTE;
                        this.map = new byte[256];
                        System.arraycopy(map, 0, this.map, 0, 256);
                        break;
                    case 256 * 256:
                        mappingType = ByteMappingType.TWO_BYTES;
                        this.map = new byte[256 * 256 * 2];
                        for (int i = 0; i < 256; ++i) {                            
                            for (int j = 0; j < 256; ++j) {
                                int index = (i << 8) | j;
                                final int value = map[index];
                                index <<= 1;
                                this.map[index] = (byte)(0xFF & (value >> 8));
                                this.map[index + 1] = (byte)(0xFF & value);
                            }
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Map not byte aligned.");
                }
                break;                
        }
    }

    public ByteMapping2(final ByteMappingType mappingType, final byte[] map) {
        this.mappingType = mappingType;
        this.map = map;
    }
    
    public ByteMapping2(final byte[] map) {
        this(ByteMappingType.ONE_BYTE, map);
    }

    public ByteMapping2(final byte[][][] map) {
        mappingType = ByteMappingType.TWO_BYTES;
        this.map = new byte[256 * 256 * 2];
        int index = 0;
        for (int i = 0; i < 256; ++i) {
            final byte[][] p = map[(map.length == 2) ? (i & 1) : i];
            for (int j = 0; j < 256; ++j) {
                final byte[] q = p[(p.length == 2) ? (j & 1) : j];
                for (int k = 0; k < 2; ++k) {
                    this.map[index++] = q[k];
                }
            }
        }
    }

    public ByteMapping2(final byte[][][][] map) {        
        if (map.length == 2) {
            this.map = new byte[256 * 256 * 2 * 3];
            mappingType = ByteMappingType.BIT_TWO_BYTES;
            int index = 0;
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 256; ++j) {
                    for (int k = 0; k < 256; ++k) {
                        for (int l = 0; l < 3; ++l) {
                            this.map[index++] = map[i][j][k][l];
                        }
                    }
                }
            }
        } else if (map[0][0].length == 2) {
            this.map = new byte[256 * 256 * 2 * 3];
            mappingType = ByteMappingType.TWO_BYTES_BIT;
            int index = 0;
            for (int i = 0; i < 256; ++i) {
                for (int j = 0; j < 256; ++j) {
                    for (int k = 0; k < 2; ++k) {
                        for (int l = 0; l < 3; ++l) {
                            this.map[index++] = map[i][j][k][l];
                        }
                    }
                }
            }
        } else {
            this.map = new byte[256 * 256 * 256 * 3];
            mappingType = ByteMappingType.THREE_BYTES;
            int index = 0;
            for (int i = 0; i < 256; ++i) {
                for (int j = 0; j < 256; ++j) {
                    for (int k = 0; k < 256; ++k) {
                        for (int l = 0; l < 3; ++l) {
                            this.map[index++] = map[i][j][k][l];
                        }
                    }
                }
            }
        }       
    }

    public ByteMappingType getMappingType() {
        return mappingType;
    }

    public byte[] getMap() {
        return map;
    }
    
    public void write(final OutputStream out) throws IOException {
        out.write(mappingType.getValue());
        for (int i = 0; i < map.length; ++i) {
            out.write(map[i]);
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ByteMapping2)) {
            return false;
        }
        
        final ByteMapping2 mapping = (ByteMapping2)obj;
        
        if (mappingType != mapping.mappingType) {
            return false;
        }
        
        if (map == null) {
            return mapping.map == null;
        }
        
        if (map.length != mapping.map.length) {
            return false;
        }
        
        for (int i = map.length - 1; i >= 0; --i) {
            if (map[i] != mapping.map[i]) {
                return false;
            }
        }
        
        return true;
    }
}