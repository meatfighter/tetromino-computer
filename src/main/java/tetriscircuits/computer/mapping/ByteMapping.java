package tetriscircuits.computer.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteMapping {
    
    public static ByteMapping read(final InputStream in) throws IOException {
        
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
            default:
                length = 256 * 256 * 2 * 3;
                break;
        }
        final int[] map = new int[length];
        
        for (int i = 0; i < length; ++i) {
            final int b = in.read();
            if (b < 0) {
                throw new IOException("Unexpected end of file.");
            }
            map[i] = b;
        }
        
        return new ByteMapping(mapType, map);
    }
    
    private final ByteMappingType mapType;
    private final int[] map;

    public ByteMapping(final ByteMappingType mapType, final int[] map) {
        this.mapType = mapType;
        this.map = map;
    }
    
    public ByteMapping(final int[] map) {
        this(ByteMappingType.ONE_BYTE, map);
    }

    public ByteMapping(final int[][][] map) {
        mapType = ByteMappingType.TWO_BYTES;
        this.map = new int[256 * 256 * 2];
        int index = 0;
        for (int i = 0; i < 256; ++i) {
            final int[][] p = map[(map.length == 2) ? (i & 1) : i];
            for (int j = 0; j < 256; ++j) {
                final int[] q = p[(p.length == 2) ? (j & 1) : j];
                for (int k = 0; k < 2; ++k) {
                    this.map[index++] = q[k];
                }
            }
        }
    }

    public ByteMapping(final int[][][][] map) {
        this.map = new int[256 * 256 * 2 * 3];
        if (map.length == 2) {
            mapType = ByteMappingType.BIT_TWO_BYTES;
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
        } else {
            mapType = ByteMappingType.TWO_BYTES_BIT;
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
        }        
    }

    public ByteMappingType getMapType() {
        return mapType;
    }

    public int[] getMap() {
        return map;
    }
    
    public void write(final OutputStream out) throws IOException {
        out.write(mapType.getValue());
        for (int i = 0; i < map.length; ++i) {
            out.write(map[i]);
        }
    }
}