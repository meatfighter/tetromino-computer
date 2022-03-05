package tetriscircuits.computer;

import com.bulenkov.darcula.DarculaLaf;

import java.awt.EventQueue;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import tetriscircuits.computer.emulator.Emulator;
import tetriscircuits.computer.ui.PlayfieldFrame;

import static java.lang.Thread.sleep;
import static java.lang.Thread.yield;
import tetriscircuits.computer.ui.PlayfieldModel;


public class Computer {

    private static final double FRAMES_PER_SECOND = 3;
    private static final int MAX_FRAMES_LOST = 3;
    private static final int MIN_SLEEP_MILLIS = 2;    
    
    private static final long NANOS_PER_FRAME = (long)Math.round(1_000_000_000L / FRAMES_PER_SECOND);
    private static final long MIN_SLEEP_NANOS = 1_000_000L * MIN_SLEEP_MILLIS;    
    private static final long MAX_LOST_NANOS = -MAX_FRAMES_LOST * NANOS_PER_FRAME;
    
    private final Emulator emulator = new Emulator();
    private final PlayfieldModel playfieldModel = new PlayfieldModel();
    
    private volatile PlayfieldFrame playfieldFrame;
    
    public void launch() throws Exception {
        emulator.loadBinFile("tetris.bin");
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
        final int[] memory = emulator.getMemory();
        memory[0x00FD] = 0;
        do {
            emulator.runInstruction();
        } while (memory[0x00FD] == 0);
        
        final int[][] cells = playfieldModel.getCells();
        for (int y = 19; y >= 0; --y) {
            for (int x = 9; x >= 0; --x) {
                cells[y][x] = memory[11 * (2 + y) + x];
            }
        }
        playfieldFrame.update(playfieldModel);
        System.out.println(System.currentTimeMillis());
    }
    
    private void runGameLoop() {
        long clock = System.nanoTime();
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
                            yield();
                        } while (clock - System.nanoTime() > 0);
                    } else {                        
                        sleep(remainingTime / 1_000_000L);
                    }
                }
            } catch (final Exception e) {                
            }
        }
    }    

    public static void main(final String... args) throws Exception {
        new Computer().launch();
    }
}
