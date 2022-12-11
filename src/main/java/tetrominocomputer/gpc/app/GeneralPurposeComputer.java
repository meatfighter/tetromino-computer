package tetrominocomputer.gpc.app;

import com.bulenkov.darcula.DarculaLaf;

import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import tetrominocomputer.gpc.ui.PlayfieldFrame;
import tetrominocomputer.gpc.ui.PlayfieldModel;
import tetrominocomputer.mc.Instruction;
import tetrominocomputer.mc.LexerParser;
import tetrominocomputer.ts.ByteLut;
import tetrominocomputer.util.Dirs;

public final class GeneralPurposeComputer {
    
    private static final String DEFAULT_BIN_FILENAME = Dirs.BIN + "example.bin";
    private static final String DEFAULT_CYCLE_LEFT_COMPONENT_NAME = "CYCLE_LEFT";
    private static final String DEFAULT_CYCLE_RIGHT_COMPONENT_NAME = "CYCLE_RIGHT";

    private static final double MAX_FRAMES_PER_SECOND = 10;
    private static final int MAX_FRAMES_LOST = 3;
    private static final int MIN_SLEEP_MILLIS = 2;
    private static final int SECONDS_PER_SAMPLE_FPS = 5;
    
    private static final long NANOS_PER_FRAME = (long)Math.round(1_000_000_000L / MAX_FRAMES_PER_SECOND);
    private static final long MIN_SLEEP_NANOS = 1_000_000L * MIN_SLEEP_MILLIS;    
    private static final long MAX_LOST_NANOS = -MAX_FRAMES_LOST * NANOS_PER_FRAME;
    private static final double NANOS_PER_SAMPLE_FPS = SECONDS_PER_SAMPLE_FPS * 1.0E9;
    
    private final PlayfieldModel playfieldModel = new PlayfieldModel();
    
    private final Runnable[] cycleLeftRunnables;
    private final Runnable[] cycleRightRunnables;
    private final int[] memory;

    private volatile PlayfieldFrame playfieldFrame;
    
    private boolean cycleLeft = true;    
    
    private GeneralPurposeComputer(final String binFilename, final String cycleLeftComponentName, final String cycleRightComponentName) 
            throws Exception {
        
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
        
        final Map<String, Instruction[]> programs = new LexerParser().parseAll();
        final Map<String, ByteLut> luts = loadLuts();
        cycleLeftRunnables = convertProgramToRunnables(cycleLeftComponentName, programs, luts);
        cycleRightRunnables = convertProgramToRunnables(cycleRightComponentName, programs, luts);                

        EventQueue.invokeAndWait(this::createFrame);
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
    
    private Runnable[] convertProgramToRunnables(final String programName, final Map<String, Instruction[]> programs, 
            final Map<String, ByteLut> luts) throws IOException {
                
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
    
    private void createFrame() {
        try {
            UIManager.setLookAndFeel(new DarculaLaf()); 
        } catch (final UnsupportedLookAndFeelException ignored) {
        }
        playfieldFrame = new PlayfieldFrame();        
        playfieldFrame.pack();
        playfieldFrame.setLocationRelativeTo(null);
        playfieldFrame.setVisible(true);        
    }    
    
    private int read(final int address) {
        return memory[(cycleLeft || address < 3) ? address : (address + 21)];            
    }

    private void write(final int address, final int value) {
        memory[(cycleLeft || address < 3) ? address : (address + 21)] = value;
    }
    
    private void update() {
        write(0x00FD, 0);
        do {
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
        } while (read(0x00FD) == 0);
        
        final int[][] cells = playfieldModel.getCells();
        for (int y = 19; y >= 0; --y) {
            for (int x = 9; x >= 0; --x) {
                cells[y][x] = read(11 * (2 + y) + x);
            }
        }
        playfieldFrame.update(playfieldModel);        
        write(0x00FE, playfieldModel.isLeftPressed() ? 1 : 0);
        write(0x00FF, playfieldModel.isRightPressed() ? 1 : 0);
        write(0x0170, playfieldModel.isStartPressed() ? 1 : 0);
        write(0x0171, playfieldModel.isCcwRotatePressed() ? 1 : 0);
        write(0x0172, playfieldModel.isCwRotatePressed() ? 1 : 0);
        write(0x0173, playfieldModel.isDownPressed() ? 1 : 0);
    }
    
    private void launch() throws Exception {
        int frames = 0;
        long clock = System.nanoTime();
        long framesStart = System.nanoTime();        
        while (true) {            
            update();          
            
            clock += NANOS_PER_FRAME;
            final long remainingTime = clock - System.nanoTime();
            if (remainingTime < MAX_LOST_NANOS) {
                clock = System.nanoTime();       
            } else if (remainingTime > 0) {
                if (remainingTime < MIN_SLEEP_NANOS) {                        
                    do {
                        Thread.yield();
                    } while (clock - System.nanoTime() > 0);
                } else {                        
                    Thread.sleep(remainingTime / 1_000_000L);
                }
            }
            
            ++frames;
            final double framesDuration = System.nanoTime() - framesStart;
            if (framesDuration > NANOS_PER_SAMPLE_FPS) {
                playfieldFrame.setFramesPerSecond(frames / (framesDuration / 1.0E9));
                frames = 0;
                framesStart = System.nanoTime();
            }
        }
    }    
    
    public static void main(final String... args) throws Exception {               
        
        if (args.length == 2 || args.length > 3) {
            System.out.println("args: [[ bin filename ]] [[ cycle left component name ]] "
                    + "[[ cycle right component name ]]");
            return;
        } 
        
        final String binFilename = (args.length == 0) ? DEFAULT_BIN_FILENAME : args[0];
        
        final String cycleLeftComponentName;
        final String cycleRightComponentName;                
        if (args.length != 3) {            
            cycleLeftComponentName = DEFAULT_CYCLE_LEFT_COMPONENT_NAME;
            cycleRightComponentName = DEFAULT_CYCLE_RIGHT_COMPONENT_NAME;
        } else {
            cycleLeftComponentName = args[1];
            cycleRightComponentName = args[2];
        } 
        
        new GeneralPurposeComputer(binFilename, cycleLeftComponentName, cycleRightComponentName).launch();
    }
}
