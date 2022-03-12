package tetriscircuits.computer.simulator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import tetriscircuits.computer.Processor;
import tetriscircuits.computer.parser.Parser;
import tetriscircuits.parser.ParseException;

public final class Simulator2 implements Processor {
    
    private Runnable[] tetrisDescend;
    private Runnable[] tetrisAscend;
    private int[] bytes;
    private boolean descend = true;
    
    private int read(final int index) {
        return bytes[index];
    }
    
    private void write(final int index, final int value) {
        bytes[index] = value;
    }
    
    private void apply(final int index, final int[] map) {
        bytes[index] = map[bytes[index]];
    }
    
    private void apply(final int index, final int[][][] map) {
        final int[] m = map[bytes[index]][bytes[index + 1]];
        bytes[index] = m[0];
        bytes[index + 1] = m[1];
    }

    private void apply(final int index, final int[][][][] map) {
        final int[] m = map[bytes[index]][bytes[index + 1]][bytes[index + 2]];
        bytes[index] = m[0];
        bytes[index + 1] = m[1];
        bytes[index + 2] = m[2];
    }

    @Override
    public int readMemory(int address) {
        return bytes[(descend || address < 3) ? address : (address + 21)];            
    }

    @Override
    public void writeMemory(int address, int value) {
        bytes[(descend || address < 3) ? address : (address + 21)] = value;
    }
    
    private Map<String, Mapping> loadMaps() throws IOException {
        final Map<String, Mapping> mappings = new HashMap<>();
        for (final File file : new File("maps").listFiles((dir, name) -> name.endsWith(".map"))) {
            final String filename = file.getName();
            try (final InputStream in = new BufferedInputStream(new FileInputStream(file))) {
                loadMap(mappings, filename.substring(0, filename.length() - 4), in);
            }
        }
        return mappings;
    } 
    
    private void loadMap(final Map<String, Mapping> mappings, final String componentName, final InputStream in) 
            throws IOException {
        switch(in.read()) {
            case 1:
                loadMap1(mappings, componentName, in);
                break;
            case 2:
                loadMap2(mappings, componentName, in);
                break;
            case 3:
                loadMap3(mappings, componentName, in);
                break;
            case 4:
                loadMap4(mappings, componentName, in);
                break;
            default:
                throw new IOException(String.format("%s: Invalid map type.", componentName));
        }
    }
    
    private void loadMap1(final Map<String, Mapping> mappings, final String componentName, final InputStream in) 
            throws IOException {
        
        final int[] map = new int[256];
        for (int i = 0; i < 256; ++i) {
            final int b = in.read();
            if (b < 0) {
                throw new IOException(String.format("%s: Unexpected end of file.", componentName));
            }
            map[i] = b;
        }
        mappings.put(componentName, new Mapping(map));
    }
    
    private void loadMap2(final Map<String, Mapping> mappings, final String componentName, final InputStream in) 
            throws IOException {
        
        final int[][][] map = new int[256][256][2];
        for (int i = 0; i < 256; ++i) {
            for (int j = 0; j < 256; ++j) {
                final int a = in.read();
                final int b = in.read();
                if (a < 0 || b < 0) {
                    throw new IOException(String.format("%s: Unexpected end of file.", componentName));
                }
                map[i][j][0] = a;
                map[i][j][1] = b;
            }
        }
        mappings.put(componentName, new Mapping(map));
    }

    private void loadMap3(final Map<String, Mapping> mappings, final String componentName, final InputStream in) 
            throws IOException {
        
        final int[][][][] map = new int[2][256][256][3];
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 256; ++j) {
                for (int k = 0; k < 256; ++k) {
                    final int a = in.read();
                    final int b = in.read();
                    final int c = in.read();
                    if (a < 0 || b < 0 || c < 0) {
                        throw new IOException(String.format("%s: Unexpected end of file.", componentName));
                    }
                    map[i][j][k][0] = a;
                    map[i][j][k][1] = b;
                    map[i][j][k][2] = c;
                }
            }
        }
        mappings.put(componentName, new Mapping(map));
    }

    private void loadMap4(final Map<String, Mapping> mappings, final String componentName, final InputStream in) 
            throws IOException {
        
        final int[][][][] map = new int[256][256][2][3];
        for (int i = 0; i < 256; ++i) {
            for (int j = 0; j < 256; ++j) {
                for (int k = 0; k < 2; ++k) {
                    final int a = in.read();
                    final int b = in.read();
                    final int c = in.read();
                    if (a < 0 || b < 0 || c < 0) {
                        throw new IOException(String.format("%s: Unexpected end of file.", componentName));
                    }
                    map[i][j][k][0] = a;
                    map[i][j][k][1] = b;
                    map[i][j][k][2] = c;
                }
            }
        }
        mappings.put(componentName, new Mapping(map));
    }  
    
    private Runnable[] loadExecutable(final Map<String, Mapping> mappings, final String filename) 
            throws IOException, ParseException {
        
        return new Parser().parse((componentName, index) -> {
            final Mapping mapping = mappings.get(componentName);
            if (mapping == null) {
                throw new IOException("Unknown component: " + componentName);
            }
            switch (mapping.getLength()) {
                case 1: {
                    final int[] map = mapping.getOne();                    
                    return () -> { 
                        bytes[index] = map[bytes[index]]; 
                    };
                }
                case 2: {
                    final int[][][] map = mapping.getTwo();   
                    final int index1 = index + 1;                    
                    return () -> {               
                        final int[] m = map[bytes[index]][bytes[index1]];
                        bytes[index] = m[0];
                        bytes[index1] = m[1];
                    };
                }
                case 3: {
                    final int[][][][] map = mapping.getThree();
                    final int index1 = index + 1;
                    final int index2 = index + 2;                    
                    return () -> {               
                        final int[] m = map[bytes[index]][bytes[index1]][bytes[index2]];
                        bytes[index] = m[0];
                        bytes[index1] = m[1];
                        bytes[index2] = m[2];
                    };
                }
            }
            throw new IOException("Invalid map length: " + componentName);
        }, filename);
    }
    
    private void loadInputData() throws IOException {
        final File file = new File("data/inputData.dat");
        bytes = new int[(int)file.length()];
        try (final InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            for (int i = 0; i < bytes.length; ++i) {
                bytes[i] = in.read();
            }
        }
    }
    
    @Override
    public void init() throws Exception {
        final Map<String, Mapping> mappings = loadMaps();
        tetrisDescend = loadExecutable(mappings, "executables/tetris-descend.tx");
        tetrisAscend = loadExecutable(mappings, "executables/tetris-ascend.tx");
        loadInputData();
    }

    @Override
    public void runInstruction() {
        if (descend) {
            descend = false;
            final int length = tetrisDescend.length;
            for (int i = 0; i < length; ++i) {
                tetrisDescend[i].run();
            }
        } else {
            descend = true;
            final int length = tetrisAscend.length;
            for (int i = 0; i < length; ++i) {
                tetrisAscend[i].run();
            }           
        } 
    }
    
    private class Mapping {
        
        private final int length;
        private final int[] one;
        private final int[][][] two;
        private final int[][][][] three;
        
        Mapping(final int[] map) {
            length = 1;
            one = map;
            two = null;
            three = null;
        }
        
        Mapping(final int[][][] map) {
            length = 2;
            one = null;
            two = map;
            three = null;
        }
        
        Mapping(final int[][][][] map) {
            length = 3;
            one = null;
            two = null;
            three = map;
        }

        public int getLength() {
            return length;
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
    }
}
