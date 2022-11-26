package tetrominocomputer.computer.simulator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tetrominocomputer.computer.Processor;
import tetrominocomputer.computer.mapping.ByteMapping;
import tetrominocomputer.computer.memorycode.Instruction;
import tetrominocomputer.computer.memorycode.Parser;
import tetrominocomputer.parser.ParseException;

public final class Simulator2 implements Processor {
    
    private static final String TETRISMEMORYSCRIPTS_DIR = "MemoryCodes";
    
    private Runnable[] cycleLeftRunnables;
    private Runnable[] cycleRightRunnables;
    private int[] bytes;
    private boolean cycleLeft = true;
    
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
        return bytes[(cycleLeft || address < 3) ? address : (address + 21)];            
    }

    @Override
    public void writeMemory(int address, int value) {
        bytes[(cycleLeft || address < 3) ? address : (address + 21)] = value;
    }
    
    private Map<String, ByteMapping> loadMaps() throws IOException {
        final Map<String, ByteMapping> mappings = new HashMap<>();
        for (final File file : new File("maps2").listFiles((dir, name) -> name.endsWith(".map"))) {
            final String filename = file.getName();
            try (final InputStream in = new BufferedInputStream(new FileInputStream(file))) {
                mappings.put(filename.substring(0, filename.length() - 4), ByteMapping.read(in));
            } catch (final IOException e) {
                throw new IOException("Failed to load " + filename, e);
            }
        }
        return mappings;
    } 
    
    private Runnable[] loadExecutable(final Map<String, ByteMapping> mappings, final String script) 
            throws IOException, ParseException {
        
        final Parser parser = new Parser();
        final Map<String, Instruction[]> components = parser.parseAll(TETRISMEMORYSCRIPTS_DIR);
        final List<Instruction> instructions = parser.expand(script, components);
        final Runnable[] runnables = new Runnable[instructions.size()];
        
        for (int r = 0; r < runnables.length; ++r) {
            final Instruction instruction = instructions.get(r);
            final ByteMapping mapping = mappings.get(instruction.getComponent());
            if (mapping == null) {
                throw new IOException("Unknown component: " + instruction.getComponent());
            }
            final int index = instruction.getIndex();
            switch (mapping.getMappingType()) {
                case ONE_BYTE: {
                    final int[] map = mapping.getMap();
                    runnables[r] = () -> {
                        bytes[index] = map[bytes[index]];
                    };
                    break;
                }
                case TWO_BYTES: {
                    final int[] map = mapping.getMap();   
                    final int index1 = index + 1;                    
                    runnables[r] = () -> {
                        final int i = 512 * bytes[index] + 2 * bytes[index1];
                        bytes[index] = map[i];
                        bytes[index1] = map[i + 1];
                    };
                    break;
                }
                case TWO_BYTES_BIT: {
                    final int[] map = mapping.getMap();
                    final int index1 = index + 1;
                    final int index2 = index + 2;
                    runnables[r] = () -> {
                        final int i = 1536 * bytes[index] + 6 * bytes[index1] + 3 * bytes[index2];
                        bytes[index] = map[i];
                        bytes[index1] = map[i + 1];
                        bytes[index2] = map[i + 2];
                    };
                    break;
                }
                default: {
                    final int[] map = mapping.getMap();
                    final int index1 = index + 1;
                    final int index2 = index + 2;
                    runnables[r] = () -> {
                        final int i = 196608 * bytes[index] + 768 * bytes[index1] + 3 * bytes[index2];                        
                        bytes[index] = map[i];
                        bytes[index1] = map[i + 1];
                        bytes[index2] = map[i + 2];
                    };
                    break;
                }                
            }
        }
        
        return runnables;
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
        final Map<String, ByteMapping> mappings = loadMaps();
        cycleLeftRunnables = loadExecutable(mappings, "CYCLE_LEFT");
        cycleRightRunnables = loadExecutable(mappings, "CYCLE_RIGHT");
        loadInputData();
    }

    @Override
    public void runInstruction() {
        if (cycleLeft) {
            cycleLeft = false;
            final int length = cycleLeftRunnables.length;
            for (int i = 0; i < length; ++i) {
                cycleLeftRunnables[i].run();
            }
        } else {
            cycleLeft = true;
            final int length = cycleRightRunnables.length;
            for (int i = 0; i < length; ++i) {
                cycleRightRunnables[i].run();
            }           
        } 
    }
}
