package tetrominocomputer.gpc.app;

import com.bulenkov.darcula.DarculaLaf;

import java.awt.EventQueue;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import tetrominocomputer.gpc.ui.PlayfieldFrame;
import tetrominocomputer.gpc.ui.PlayfieldModel;
import tetrominocomputer.util.Dirs;

public class Computer {
    
    private static final String DEFAULT_BIN_FILENAME = Dirs.BIN + "example.bin";
    private static final String DEFAULT_CYCLE_LEFT_NAME = "CYCLE_LEFT";
    private static final String DEFAULT_CYCLE_RIGHT_NAME = "CYCLE_RIGHT";

    private static final double MAX_FRAMES_PER_SECOND = 10;
    private static final int MAX_FRAMES_LOST = 3;
    private static final int MIN_SLEEP_MILLIS = 2;
    private static final int SECONDS_PER_SAMPLE_FPS = 5;
    
    private static final long NANOS_PER_FRAME = (long)Math.round(1_000_000_000L / MAX_FRAMES_PER_SECOND);
    private static final long MIN_SLEEP_NANOS = 1_000_000L * MIN_SLEEP_MILLIS;    
    private static final long MAX_LOST_NANOS = -MAX_FRAMES_LOST * NANOS_PER_FRAME;
    private static final double NANOS_PER_SAMPLE_FPS = SECONDS_PER_SAMPLE_FPS * 1.0E9;
    
    private final ProcessorAndMemory processor = new ProcessorAndMemoryImpl();
    private final PlayfieldModel playfieldModel = new PlayfieldModel();
    
    private volatile PlayfieldFrame playfieldFrame;
    
    public void launch(final String binFilename, final String cycleLeftName, final String cycleRightName) 
            throws Exception {
        
        processor.init(binFilename, cycleLeftName, cycleRightName);
        EventQueue.invokeAndWait(this::createFrame);
        runGameLoop();
    }

    private void createFrame() {
        try {
            UIManager.setLookAndFeel(new DarculaLaf()); 
        } catch (final UnsupportedLookAndFeelException e) {
        }
        playfieldFrame = new PlayfieldFrame();        
        playfieldFrame.pack();
        playfieldFrame.setLocationRelativeTo(null);
        playfieldFrame.setVisible(true);        
    }
    
    private void update() {
        processor.write(0x00FD, 0);
        do {
            processor.runInstruction();
        } while (processor.read(0x00FD) == 0);
        
        final int[][] cells = playfieldModel.getCells();
        for (int y = 19; y >= 0; --y) {
            for (int x = 9; x >= 0; --x) {
                cells[y][x] = processor.read(11 * (2 + y) + x);
            }
        }
        playfieldFrame.update(playfieldModel);        
        processor.write(0x00FE, playfieldModel.isLeftPressed() ? 1 : 0);
        processor.write(0x00FF, playfieldModel.isRightPressed() ? 1 : 0);
        processor.write(0x0170, playfieldModel.isStartPressed() ? 1 : 0);
        processor.write(0x0171, playfieldModel.isCcwRotatePressed() ? 1 : 0);
        processor.write(0x0172, playfieldModel.isCwRotatePressed() ? 1 : 0);
        processor.write(0x0173, playfieldModel.isDownPressed() ? 1 : 0);
    }
    
    private void runGameLoop() {
        int frames = 0;
        long clock = System.nanoTime();
        long framesStart = System.nanoTime();        
        while (true) {            
            update();          
            
            try {
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
            } catch (final Exception e) {
                e.printStackTrace();
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
    
    private static String defaultArg(final String[] args, final int index, final String defaultValue) {
        return (args.length > index) ? args[index] : defaultValue;
    }

    public static void main(final String... args) throws Exception {               
        new Computer().launch(
                defaultArg(args, 0, DEFAULT_BIN_FILENAME), 
                defaultArg(args, 1, DEFAULT_CYCLE_LEFT_NAME),
                defaultArg(args, 2, DEFAULT_CYCLE_RIGHT_NAME));
    }
}
