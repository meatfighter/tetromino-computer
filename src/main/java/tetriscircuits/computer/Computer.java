package tetriscircuits.computer;

import java.awt.EventQueue;
import static java.awt.EventQueue.invokeAndWait;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import static java.lang.Thread.sleep;
import static java.lang.Thread.yield;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import tetriscircuits.computer.emulator.Emulator;
import tetriscircuits.computer.ui.ImagePanel;
import tetriscircuits.ui.UiUtil;

public class Computer {

    private static final double FRAMES_PER_SECOND = 60;
    private static final int MAX_FRAMES_LOST = 3;
    private static final int MIN_SLEEP_MILLIS = 2;    
    
    private static final long NANOS_PER_FRAME = (long)Math.round(1_000_000_000L / FRAMES_PER_SECOND);
    private static final long MIN_SLEEP_NANOS = 1_000_000L * MIN_SLEEP_MILLIS;    
    private static final long MAX_LOST_NANOS = -MAX_FRAMES_LOST * NANOS_PER_FRAME;
    
    private final Emulator emulator = new Emulator();
    
    private volatile ImagePanel imagePanel;
    
    public void launch() throws Exception {
        emulator.loadBinFile("tetris.bin");
        EventQueue.invokeLater(this::createFrame);
    }

    private void createFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
        }
        imagePanel = new ImagePanel();
        final JFrame frame = new JFrame("Tetris Running On Tetris");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(imagePanel);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                imagePanel.keyReleased(e);
            }
            @Override
            public void keyPressed(final KeyEvent e) {
                imagePanel.keyPressed(e);
            }            
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(final WindowEvent e) {
                new Thread(() -> runGameLoop()).start();
            }
        });
        UiUtil.setIcons(frame);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void updateEmulator() {
        final int[] memory = emulator.getMemory();
        memory[0xFB00] = 0;
        while (memory[0xFB00] == 0) {
            emulator.runInstruction();
        }
    }
    
    private void runGameLoop() {
        long clock = System.nanoTime();
        while (true) {
            updateEmulator();
            try {
                final int[] memory = emulator.getMemory();
                invokeAndWait(() -> imagePanel.updateImage(memory));
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
