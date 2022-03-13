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
        
        switch(mapType) {
            case ONE_BYTE:
                return readOneByte(in);
            case TWO_BYTES:
                return readTwoBytes(in);
            case BIT_TWO_BYTES:
                return readBitTwoBytes(in);
            default:
                return readTwoBytesBit(in);                
        }
    }
    
    private static Mapping readOneByte(final InputStream in) throws IOException {
        
        final int[] map = new int[256];
        for (int i = 0; i < 256; ++i) {
            final int b = in.read();
            if (b < 0) {
                throw new IOException("Unexpected end of file.");
            }
            map[i] = b;
        }
        return new Mapping(map);
    }
    
    private static Mapping readTwoBytes(final InputStream in) throws IOException {
        
        final int[][][] map = new int[256][256][2];
        for (int i = 0; i < 256; ++i) {
            for (int j = 0; j < 256; ++j) {
                final int a = in.read();
                final int b = in.read();
                if (a < 0 || b < 0) {
                    throw new IOException("Unexpected end of file.");
                }
                map[i][j][0] = a;
                map[i][j][1] = b;
            }
        }
        return new Mapping(map);
    }

    private static Mapping readBitTwoBytes(final InputStream in) throws IOException {
        
        final int[][][][] map = new int[2][256][256][3];
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 256; ++j) {
                for (int k = 0; k < 256; ++k) {
                    final int a = in.read();
                    final int b = in.read();
                    final int c = in.read();
                    if (a < 0 || b < 0 || c < 0) {
                        throw new IOException("Unexpected end of file.");
                    }
                    map[i][j][k][0] = a;
                    map[i][j][k][1] = b;
                    map[i][j][k][2] = c;
                }
            }
        }
        return new Mapping(map);
    }

    private static Mapping readTwoBytesBit(final InputStream in) throws IOException {
        
        final int[][][][] map = new int[256][256][2][3];
        for (int i = 0; i < 256; ++i) {
            for (int j = 0; j < 256; ++j) {
                for (int k = 0; k < 2; ++k) {
                    final int a = in.read();
                    final int b = in.read();
                    final int c = in.read();
                    if (a < 0 || b < 0 || c < 0) {
                        throw new IOException("Unexpected end of file.");
                    }
                    map[i][j][k][0] = a;
                    map[i][j][k][1] = b;
                    map[i][j][k][2] = c;
                }
            }
        }
        return new Mapping(map);
    }  
    
    private final MapType mapType;
    private final int[] one;
    private final int[][][] two;
    private final int[][][][] three;

    public Mapping(final int[] map) {
        mapType = MapType.ONE_BYTE;
        one = map;
        two = null;
        three = null;
    }

    public Mapping(final int[][][] map) {
        mapType = MapType.TWO_BYTES;
        one = null;
        two = map;
        three = null;
    }

    public Mapping(final int[][][][] map) {
        mapType = (map.length == 2) ? MapType.BIT_TWO_BYTES : MapType.TWO_BYTES_BIT;
        one = null;
        two = null;
        three = map;
    }

    public MapType getMapType() {
        return mapType;
    }

    public int[] getOne() {
        return one;
    }

    public int[][][] getTwo() {
        return two;
    }

    public int[][][][] getThree() {
        return three;
    }  
    
    public void write(final OutputStream out) throws IOException {
        switch (mapType) {
            case ONE_BYTE:
                writeOneByte(out);
                break;
            case TWO_BYTES:
                writeTwoBytes(out);
                break;
            case BIT_TWO_BYTES:
                writeBitTwoBytes(out);
                break;
            case TWO_BYTES_BIT:
                writeTwoBytesBit(out);
                break;
        }
    }
    
    private void writeOneByte(final OutputStream out) throws IOException {
        out.write(mapType.getValue());
        for (int i = 0; i < 256; ++i) {
            out.write(one[i]);
        }
    }
    
    private void writeTwoBytes(final OutputStream out) throws IOException {
        out.write(mapType.getValue());
        for (int i = 0; i < 256; ++i) {
            final int[][] p = two[(two.length == 2) ? (i & 1) : i];
            for (int j = 0; j < 256; ++j) {
                final int[] q = p[(p.length == 2) ? (j & 1) : j];
                for (int k = 0; k < 2; ++k) {
                    out.write(q[k]);
                }
            }
        }
    }
    
    private void writeBitTwoBytes(final OutputStream out) throws IOException {
        out.write(mapType.getValue());
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 256; ++j) {
                for (int k = 0; k < 256; ++k) {
                    for (int l = 0; l < 3; ++l) {
                        out.write(three[i][j][k][l]);
                    }
                }
            }
        }
    }
    
    private void writeTwoBytesBit(final OutputStream out) throws IOException {
        out.write(mapType.getValue());
        for (int i = 0; i < 256; ++i) {
            for (int j = 0; j < 256; ++j) {
                for (int k = 0; k < 2; ++k) {
                    for (int l = 0; l < 3; ++l) {
                        out.write(three[i][j][k][l]);
                    }
                }
            }
        }
    }
}