package tetriscircuits.computer;

import java.awt.EventQueue;
import static java.awt.EventQueue.invokeAndWait;
import static java.lang.Thread.sleep;
import static java.lang.Thread.yield;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import tetriscircuits.computer.ui.ImagePanel;
import tetriscircuits.ui.UiUtil;

public class Computer {

    private static final int MAX_FRAMES_LOST = 3;
    private static final int MIN_SLEEP_MILLIS = 2;
    
    public static final double GENERATED_FRAMES_PER_SECOND = 60.0988;
    public static final double MONITOR_FRAMES_PER_SECOND = 60;
    
    private static final long MIN_SLEEP_NANOS = 1_000_000L * MIN_SLEEP_MILLIS;

    public static final long NANOS_PER_GENERATED_FRAME = (long)Math.round(1_000_000_000L / GENERATED_FRAMES_PER_SECOND);
    private static final long MAX_GENERATED_LOST_NANOS = -MAX_FRAMES_LOST * NANOS_PER_GENERATED_FRAME;
        
    public static final long NANOS_PER_MONITOR_FRAME = (long)Math.round(1_000_000_000L / MONITOR_FRAMES_PER_SECOND);
    private static final long MAX_MONITOR_LOST_NANOS = -MAX_FRAMES_LOST * NANOS_PER_MONITOR_FRAME;
    
    private volatile ImagePanel imagePanel;
    
    public void launch() {
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
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(imagePanel);
        UiUtil.setIcons(frame);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        new Thread(this::runMonitorLoop).start();
    }
    
    private void runMonitorLoop() {
        long clock = System.nanoTime();
        while (true) {              
            try {
                invokeAndWait(imagePanel::updateImage);
                clock += NANOS_PER_MONITOR_FRAME;
                final long remainingTime = clock - System.nanoTime();
                if (remainingTime < MAX_GENERATED_LOST_NANOS) {
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

    public static void main(final String... args) {
        new Computer().launch();
    }
}
