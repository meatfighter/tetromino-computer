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
import tetrominocomputer.computer.mapping.ByteMapping2;
import tetrominocomputer.mc.Instruction;
import tetrominocomputer.mc.LexerParser;
import tetrominocomputer.ts.LexerParserException;

public final class Simulator3 implements Processor {
    
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
    
    private Runnable[] loadProgram(final String programName, final Map<String, Instruction[]> programs, 
            final Map<String, ByteMapping2> mappings) throws IOException {
                
        final List<Instruction> instructions = LexerParser.expand(programName, programs);
        final Runnable[] runnables = new Runnable[instructions.size()];
        
        for (int r = 0; r < runnables.length; ++r) {
            final Instruction instruction = instructions.get(r);
            final ByteMapping2 mapping = mappings.get(instruction.getComponent());
            if (mapping == null) {
                throw new IOException("Unknown component: " + instruction.getComponent());
            }
            final byte[] map = mapping.getMap();
            final int index = instruction.getIndex();
            switch (mapping.getMappingType()) {
                case ONE_BYTE: {
                    runnables[r] = () -> {
                        bytes[index] = 0xFF & map[bytes[index]];
                    };
                    break;
                }
                case TWO_BYTES: {
                    runnables[r] = () -> {
                        final int i = 512 * bytes[index] + 2 * bytes[index + 1];
                        bytes[index] = 0xFF & map[i];
                        bytes[index + 1] = 0xFF & map[i + 1];
                    };
                    break;
                }
                case TWO_BYTES_BIT: {
                    runnables[r] = () -> {
                        final int i = 1536 * bytes[index] + 6 * bytes[index + 1] + 3 * bytes[index + 2];
                        bytes[index] = 0xFF & map[i];
                        bytes[index + 1] = 0xFF & map[i + 1];
                        bytes[index + 2] = 0xFF & map[i + 2];
                    };
                    break;
                }
                default: {
                    runnables[r] = () -> {
                        final int i = 196608 * bytes[index] + 768 * bytes[index + 1] + 3 * bytes[index + 2];
                        bytes[index] = 0xFF & map[i];
                        bytes[index + 1] = 0xFF & map[i + 1];
                        bytes[index + 2] = 0xFF & map[i + 2];
                    };
                    break;
                }
            }
        }
        
        return runnables;
    }
    
    public void loadBinFile(final String binFilename) throws IOException {
        final File binFile = new File(binFilename);
        maxAddress = (int)binFile.length() - 3; 
        
        bytes = new int[maxAddress + 24]; // machine code + 2 padding bytes + 21-byte state register
        
        try (final InputStream in = new BufferedInputStream(new FileInputStream(binFilename))){
            for (int address = 0; address <= maxAddress; ++address) {                
                final int b = in.read();
                if (b < 0) {
                    throw new IOException("Unexpected end of file.");
                }
                bytes[address] = b;
            }
            bytes[maxAddress + 9] = in.read();         // P = main;
            bytes[maxAddress + 10] = in.read();
            if (bytes[maxAddress + 9] < 0 || bytes[maxAddress + 10] < 0) {
                throw new IOException("Unexpected end of file.");
            }
        }
        
        bytes[maxAddress + 13] = maxAddress >> 8;      // a = L-1;
        bytes[maxAddress + 14] = 0xFF & maxAddress;
    }
    
    @Override
    public void init(final String binFilename, final String cycleLeftName, final String cycleRightName) 
            throws Exception {
        
        final Map<String, Instruction[]> programs = new LexerParser().parseAll();
        final Map<String, ByteMapping2> mappings = loadMaps();
        cycleLeftRunnables = loadProgram(cycleLeftName, programs, mappings);
        cycleRightRunnables = loadProgram(cycleRightName, programs, mappings);
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
