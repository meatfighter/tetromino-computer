package tetrominocomputer.gpc.app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tetrominocomputer.ts.ByteMapping;
import tetrominocomputer.mc.Instruction;
import tetrominocomputer.mc.LexerParser;

public final class ProcessorAndMemoryImpl implements ProcessorAndMemory {
    
    private Runnable[] cycleLeftRunnables;
    private Runnable[] cycleRightRunnables;
    private int[] memory;
    private boolean cycleLeft = true;
    
    @Override
    public int read(final int address) {
        return memory[(cycleLeft || address < 3) ? address : (address + 21)];            
    }

    @Override
    public void write(final int address, final int value) {
        memory[(cycleLeft || address < 3) ? address : (address + 21)] = value;
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
    
    private Runnable[] convertInstructionsToRunnables(final String programName, 
            final Map<String, Instruction[]> programs, final Map<String, ByteMapping> mappings) throws IOException {
                
        final List<Instruction> instructions = LexerParser.expand(programName, programs);
        final Runnable[] runnables = new Runnable[instructions.size()];
        
        for (int r = 0; r < runnables.length; ++r) {
            final Instruction instruction = instructions.get(r);
            final ByteMapping mapping = mappings.get(instruction.getComponent());
            if (mapping == null) {
                throw new IOException("Unknown component: " + instruction.getComponent());
            }
            final byte[] map = mapping.getMap();
            final int index = instruction.getIndex();
            switch (mapping.getMappingType()) {
                case ONE_BYTE: {
                    runnables[r] = () -> {
                        memory[index] = 0xFF & map[memory[index]];
                    };
                    break;
                }
                case TWO_BYTES: {
                    runnables[r] = () -> {
                        final int i = 512 * memory[index] + 2 * memory[index + 1];
                        memory[index] = 0xFF & map[i];
                        memory[index + 1] = 0xFF & map[i + 1];
                    };
                    break;
                }
                case TWO_BYTES_BIT: {
                    runnables[r] = () -> {
                        final int i = 1536 * memory[index] + 6 * memory[index + 1] + 3 * memory[index + 2];
                        memory[index] = 0xFF & map[i];
                        memory[index + 1] = 0xFF & map[i + 1];
                        memory[index + 2] = 0xFF & map[i + 2];
                    };
                    break;
                }
                default: {
                    runnables[r] = () -> {
                        final int i = 196608 * memory[index] + 768 * memory[index + 1] + 3 * memory[index + 2];
                        memory[index] = 0xFF & map[i];
                        memory[index + 1] = 0xFF & map[i + 1];
                        memory[index + 2] = 0xFF & map[i + 2];
                    };
                    break;
                }
            }
        }
        
        return runnables;
    }
    
    public void initMemory(final String binFilename) throws IOException {
        final File binFile = new File(binFilename);
        final int maxAddress = ((int) binFile.length()) - 3; 
        
        memory = new int[maxAddress + 24]; // machine code + 2 padding bytes + 21-byte state register
        
        try (final InputStream in = new BufferedInputStream(new FileInputStream(binFilename))) {
            for (int address = 0; address <= maxAddress; ++address) {                
                final int b = in.read();
                if (b < 0) {
                    throw new IOException("Unexpected end of file.");
                }
                memory[address] = b;
            }
            memory[maxAddress + 9] = in.read();         // P = main;
            memory[maxAddress + 10] = in.read();
            if (memory[maxAddress + 9] < 0 || memory[maxAddress + 10] < 0) {
                throw new IOException("Unexpected end of file.");
            }
        }
        
        memory[maxAddress + 13] = maxAddress >> 8;      // a = L-1;
        memory[maxAddress + 14] = 0xFF & maxAddress;
    }
    
    @Override
    public void init(final String binFilename, final String cycleLeftName, final String cycleRightName) 
            throws Exception {
        
        initMemory(binFilename);
        
        final Map<String, Instruction[]> programs = new LexerParser().parseAll();
        final Map<String, ByteMapping> mappings = loadMaps();
        cycleLeftRunnables = convertInstructionsToRunnables(cycleLeftName, programs, mappings);
        cycleRightRunnables = convertInstructionsToRunnables(cycleRightName, programs, mappings);        
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
