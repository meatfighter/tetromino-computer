package tetriscircuits.computer;

import com.bulenkov.darcula.DarculaLaf;

import java.awt.EventQueue;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import tetriscircuits.computer.ui.PlayfieldFrame;

import tetriscircuits.computer.emulator.Emulator;
import tetriscircuits.computer.simulator.Simulator;
import tetriscircuits.computer.simulator.Simulator2;
import tetriscircuits.computer.simulator.Simulator3;
import tetriscircuits.computer.ui.PlayfieldModel;


public class Computer {

    private static final double MAX_FRAMES_PER_SECOND = 5.5;
    private static final int MAX_FRAMES_LOST = 3;
    private static final int MIN_SLEEP_MILLIS = 2;
    private static final int SECONDS_PER_SAMPLE_FPS = 5;
    
    private static final long NANOS_PER_FRAME = (long)Math.round(1_000_000_000L / MAX_FRAMES_PER_SECOND);
    private static final long MIN_SLEEP_NANOS = 1_000_000L * MIN_SLEEP_MILLIS;    
    private static final long MAX_LOST_NANOS = -MAX_FRAMES_LOST * NANOS_PER_FRAME;
    private static final double NANOS_PER_SAMPLE_FPS = SECONDS_PER_SAMPLE_FPS * 1.0E9;
    
    private final Processor processor = new Simulator3(); //new Emulator();
    private final PlayfieldModel playfieldModel = new PlayfieldModel();
    
    private volatile PlayfieldFrame playfieldFrame;
    
    public void launch() throws Exception {
        processor.init();
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
        processor.writeMemory(0x00FD, 0);
        do {
            processor.runInstruction();
        } while (processor.readMemory(0x00FD) == 0);
        
//        printExtendedPlayfield();
        
        final int[][] cells = playfieldModel.getCells();
        for (int y = 19; y >= 0; --y) {
            for (int x = 9; x >= 0; --x) {
                cells[y][x] = processor.readMemory(11 * (2 + y) + x);
            }
        }
        playfieldFrame.update(playfieldModel);        
        processor.writeMemory(0x00FE, playfieldModel.isLeftPressed() ? 0xFF : 00);
        processor.writeMemory(0x00FF, playfieldModel.isRightPressed() ? 0xFF : 00);
        processor.writeMemory(0x0170, playfieldModel.isStartPressed() ? 0xFF : 00);
        processor.writeMemory(0x0171, playfieldModel.isCcwRotatePressed() ? 0xFF : 00);
        processor.writeMemory(0x0172, playfieldModel.isCwRotatePressed() ? 0xFF : 00);
        processor.writeMemory(0x0173, playfieldModel.isDownPressed() ? 0xFF : 00);
    }
    
    private void printExtendedPlayfield() {
        for (int r = 0; r <= 22; ++r) {
            final StringBuilder sb = new StringBuilder();
            for (int c = 0; c <= 10; ++c) {
                sb.append(String.format("%02X ", processor.readMemory(r * 11 + c)));
            }
            System.out.println(sb);
        }
        System.out.println();
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
        new Computer().launch();
    }
}
