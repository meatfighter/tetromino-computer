package tetriscircuits.computer.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Mapping {
    
    public static Mapping read(final InputStream in) throws IOException {
        
        final int mapTypeValue = in.read();
        if (mapTypeValue < 0) {
            throw new IOException("Unexpected end of file.");
        }
        
        final MapType mapType = MapType.fromValue(mapTypeValue);
        if (mapType == null) {
            throw new IOException("Invalid map type.");
        }
        
        switch (mapTypeValue) {
            
        }
    }
    
    private final MapType mapType;
    private final int[] map;

    public Mapping(final MapType mapType, final int[] map) {
        this.mapType = mapType;
        this.map = map;
    }
    
    public Mapping(final int[] map) {
        this(MapType.ONE_BYTE, map);
    }

    public Mapping(final int[][][] map) {
        mapType = MapType.TWO_BYTES;
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

    public Mapping(final int[][][][] map) {
        this.map = new int[256 * 256 * 2 * 3];
        if (map.length == 2) {
            mapType = MapType.BIT_TWO_BYTES;
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
            mapType = MapType.TWO_BYTES_BIT;
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

    public MapType getMapType() {
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