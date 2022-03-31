package tetriscircuits.computer.simulator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import tetriscircuits.computer.Processor;
import tetriscircuits.computer.mapping.ByteMapping2;
import tetriscircuits.computer.parser.Parser;
import tetriscircuits.parser.ParseException;

public final class Simulator3 implements Processor {
    
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
    
    private Map<String, ByteMapping2> loadMaps() throws IOException {
        final Map<String, ByteMapping2> mappings = new HashMap<>();
        for (final File file : new File("maps2").listFiles((dir, name) -> name.endsWith(".map"))) {
            final String filename = file.getName();
            try (final InputStream in = new BufferedInputStream(new FileInputStream(file))) {
                mappings.put(filename.substring(0, filename.length() - 4), ByteMapping2.read(in));
            } catch (final IOException e) {
                throw new IOException("Failed to load " + filename, e);
            }
        }
        return mappings;
    } 
    
    private Runnable[] loadExecutable(final Map<String, ByteMapping2> mappings, final String filename) 
            throws IOException, ParseException {
        
        return new Parser().parse((componentName, index) -> {
            final ByteMapping2 mapping = mappings.get(componentName);
            if (mapping == null) {
                throw new IOException("Unknown component: " + componentName);
            }
            switch (mapping.getMappingType()) {
                case ONE_BYTE: {
                    final byte[] map = mapping.getMap();
                    return (Runnable) () -> {
                        bytes[index] = 0xFF & map[bytes[index]];
                    };
                }
                case TWO_BYTES: {
                    final byte[] map = mapping.getMap();   
                    final int index1 = index + 1;                    
                    return (Runnable) () -> {
                        final int i = 512 * bytes[index] + 2 * bytes[index1];
                        bytes[index] = 0xFF & map[i];
                        bytes[index1] = 0xFF & map[i + 1];
                    };
                }
                case TWO_BYTES_BIT: {
                    final byte[] map = mapping.getMap();
                    final int index1 = index + 1;
                    final int index2 = index + 2;
                    return (Runnable) () -> {
                        final int i = 1536 * bytes[index] + 6 * bytes[index1] + 3 * bytes[index2];
                        bytes[index] = 0xFF & map[i];
                        bytes[index1] = 0xFF & map[i + 1];
                        bytes[index2] = 0xFF & map[i + 2];
                    };
                }
                default: {
                    final byte[] map = mapping.getMap();
                    final int index1 = index + 1;
                    final int index2 = index + 2;
                    return (Runnable) () -> {
                        final int i = 196608 * bytes[index] + 768 * bytes[index1] + 3 * bytes[index2];                        
                        bytes[index] = 0xFF & map[i];
                        bytes[index1] = 0xFF & map[i + 1];
                        bytes[index2] = 0xFF & map[i + 2];
                    };
                }                
            }
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
        final Map<String, ByteMapping2> mappings = loadMaps();
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
}
