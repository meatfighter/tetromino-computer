package tetrominocomputer.gpc.app;

import com.bulenkov.darcula.DarculaLaf;

import java.awt.EventQueue;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import tetrominocomputer.gpc.ui.PlayfieldFrame;
import tetrominocomputer.gpc.ui.PlayfieldModel;

public final class GeneralPurposeComputer {
    
    private static final String DEFAULT_PROCESSOR_AND_MEMORY_CLASS_NAME 
            = "tetrominocomputer.gpc.app.MCProcessorAndMemory";

    private static final double MAX_FRAMES_PER_SECOND = 10;
    private static final int MAX_FRAMES_LOST = 3;
    private static final int MIN_SLEEP_MILLIS = 2;
    private static final int SECONDS_PER_SAMPLE_FPS = 5;
    
    private static final long NANOS_PER_FRAME = (long)Math.round(1_000_000_000L / MAX_FRAMES_PER_SECOND);
    private static final long MIN_SLEEP_NANOS = 1_000_000L * MIN_SLEEP_MILLIS;    
    private static final long MAX_LOST_NANOS = -MAX_FRAMES_LOST * NANOS_PER_FRAME;
    private static final double NANOS_PER_SAMPLE_FPS = SECONDS_PER_SAMPLE_FPS * 1.0E9;
    
    private final PlayfieldModel playfieldModel = new PlayfieldModel();

    private volatile PlayfieldFrame playfieldFrame;
    
    private ProcessorAndMemory processorAndMemory;
    
    private void launch(final String[] args) throws Exception {
        initProcessorAndMemory(args);
        EventQueue.invokeAndWait(this::createFrame);
        loop();
    }  
    
    private void initProcessorAndMemory(final String[] args) throws Exception {
        
        String processorAndMemoryClassName = DEFAULT_PROCESSOR_AND_MEMORY_CLASS_NAME;
        for (int i = 0; i < args.length; i += 2) {
            if (i == args.length - 1) {
                break;
            }
            if ("-c".equals(args[i])) {
                processorAndMemoryClassName = args[i + 1];
            }            
        }
        
        processorAndMemory = (ProcessorAndMemory) Class.forName(processorAndMemoryClassName).newInstance();
        processorAndMemory.init(args);
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
    
    private void loop() throws Exception {
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
       
    private void update() {
        processorAndMemory.write(0x00FD, 0);
        do {
            processorAndMemory.executeInstruction();
        } while (processorAndMemory.read(0x00FD) == 0);
        
        final int[][] cells = playfieldModel.getCells();
        for (int y = 19; y >= 0; --y) {
            for (int x = 9; x >= 0; --x) {
                cells[y][x] = processorAndMemory.read(11 * (2 + y) + x);
            }
        }
        playfieldFrame.update(playfieldModel);        
        processorAndMemory.write(0x00FE, playfieldModel.isLeftPressed() ? 1 : 0);
        processorAndMemory.write(0x00FF, playfieldModel.isRightPressed() ? 1 : 0);
        processorAndMemory.write(0x0170, playfieldModel.isStartPressed() ? 1 : 0);
        processorAndMemory.write(0x0171, playfieldModel.isCcwRotatePressed() ? 1 : 0);
        processorAndMemory.write(0x0172, playfieldModel.isCwRotatePressed() ? 1 : 0);
        processorAndMemory.write(0x0173, playfieldModel.isDownPressed() ? 1 : 0);
    }
    
    public static void main(final String... args) throws Exception {               
        new GeneralPurposeComputer().launch(args);
    }
}
