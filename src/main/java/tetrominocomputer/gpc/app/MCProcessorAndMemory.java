package tetrominocomputer.gpc.app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tetrominocomputer.ts.ByteLut;
import tetrominocomputer.mc.Instruction;
import tetrominocomputer.mc.LexerParser;
import tetrominocomputer.util.Dirs;
import tetrominocomputer.util.Out;

public final class MCProcessorAndMemory implements ProcessorAndMemory {
    
    private static final String DEFAULT_BIN_FILENAME = "example.bin";
    private static final String DEFAULT_CYCLE_LEFT_PROGRAM_NAME = "CYCLE_LEFT";
    private static final String DEFAULT_CYCLE_RIGHT_PROGRAM_NAME = "CYCLE_RIGHT";    
    
    private Runnable[] cycleLeftRunnables;
    private Runnable[] cycleRightRunnables;
    private int[] memory;
    private boolean cycleLeft = true;
    
    @Override
    public boolean init(final String[] args) throws Exception {
        
        String binFilename = DEFAULT_BIN_FILENAME;
        String cycleLeftProgramName = DEFAULT_CYCLE_LEFT_PROGRAM_NAME;
        String cycleRightProgramName = DEFAULT_CYCLE_RIGHT_PROGRAM_NAME;

        for (int i = 0; i < args.length - 1; ++i) {
            switch (args[i]) {
                case "-b":
                    binFilename = args[++i];
                    break;
                case "-l":
                    cycleLeftProgramName = removeFileExtension(args[++i]);
                    break;
                case "-r":
                    cycleRightProgramName = removeFileExtension(args[++i]);
                    break;
            }            
        }
        
        if (!initMemory(binFilename)) {
            return false;
        }
        
        final Map<String, Instruction[]> programs = new LexerParser().parseAll();
        if (!programs.containsKey(cycleLeftProgramName)) {
            Out.formatError("%nCycle left file not found.%n%n");
            return false;
        }
        if (!programs.containsKey(cycleRightProgramName)) {
            Out.formatError("%nCycle right file not found.%n%n");
            return false;
        }
        
        final Map<String, ByteLut> luts = loadLuts();
        cycleLeftRunnables = convertProgramToRunnables(cycleLeftProgramName, programs, luts);
        cycleRightRunnables = convertProgramToRunnables(cycleRightProgramName, programs, luts);
        
        return true;
    }  
    
    private String removeFileExtension(final String filename) {
        final int index = filename.lastIndexOf('.');
        return (index < 0) ? filename : filename.substring(0, index);
    }
    
    public boolean initMemory(final String binFilename) throws IOException {
        
        final File binFile = new File(Dirs.BIN + binFilename);
        if (!(binFile.exists() && binFile.isFile())) {
            Out.formatError("%nBinary file not found: %s%n%n", binFile);
            return false;
        }
        final int maxAddress = ((int) binFile.length()) - 3;
        if (maxAddress < 0) {
            Out.formatError("%nInvalid binary file.%n%n");
            return false;
        }
        
        memory = new int[maxAddress + 24]; // machine code + 2 padding bytes + 21-byte state register
        
        try (final InputStream in = new BufferedInputStream(new FileInputStream(binFile))) {
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
        
        return true;
    }    

    private Map<String, ByteLut> loadLuts() throws IOException {
        final Map<String, ByteLut> luts = new HashMap<>();
        for (final File file : new File(Dirs.LUTS).listFiles((dir, name) -> name.endsWith(".lut"))) {
            final String filename = file.getName();
            try (final InputStream in = new BufferedInputStream(new FileInputStream(file))) {
                luts.put(filename.substring(0, filename.length() - 4), ByteLut.read(in));
            } catch (final IOException e) {
                throw new IOException("Failed to load " + filename, e);
            }
        }
        return luts;
    }
    
    private Runnable[] convertProgramToRunnables(final String programName, 
            final Map<String, Instruction[]> programs, final Map<String, ByteLut> luts) throws IOException {
        
        final List<Instruction> instructions = LexerParser.expand(programName, programs);
        final Runnable[] runnables = new Runnable[instructions.size()];
        
        for (int r = 0; r < runnables.length; ++r) {
            final Instruction instruction = instructions.get(r);
            final ByteLut lut = luts.get(instruction.getComponent());
            if (lut == null) {
                throw new IOException("Unknown component: " + instruction.getComponent());
            }
            final byte[] table = lut.getTable();
            final int index = instruction.getIndex();
            switch (lut.getLutType()) {
                case ONE_BYTE: {
                    runnables[r] = () -> {
                        memory[index] = 0xFF & table[memory[index]];
                    };
                    break;
                }
                case TWO_BYTES: {
                    runnables[r] = () -> {
                        final int i = 512 * memory[index] + 2 * memory[index + 1];
                        memory[index] = 0xFF & table[i];
                        memory[index + 1] = 0xFF & table[i + 1];
                    };
                    break;
                }
                case TWO_BYTES_BIT: {
                    runnables[r] = () -> {
                        final int i = 1536 * memory[index] + 6 * memory[index + 1] + 3 * memory[index + 2];
                        memory[index] = 0xFF & table[i];
                        memory[index + 1] = 0xFF & table[i + 1];
                        memory[index + 2] = 0xFF & table[i + 2];
                    };
                    break;
                }
                default: {
                    runnables[r] = () -> {
                        final int i = 196608 * memory[index] + 768 * memory[index + 1] + 3 * memory[index + 2];
                        memory[index] = 0xFF & table[i];
                        memory[index + 1] = 0xFF & table[i + 1];
                        memory[index + 2] = 0xFF & table[i + 2];
                    };
                    break;
                }
            }
        }
        
        return runnables;
    }    
    
    @Override
    public void executeInstruction() {
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

    @Override
    public int read(final int address) {
        return memory[(cycleLeft || address < 3) ? address : (address + 21)];            
    }

    @Override
    public void write(final int address, final int value) {
        memory[(cycleLeft || address < 3) ? address : (address + 21)] = value;
    }
}
