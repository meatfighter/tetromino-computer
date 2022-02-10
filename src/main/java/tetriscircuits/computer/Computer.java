package tetriscircuits.computer;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import tetriscircuits.computer.ui.ImagePanel;
import tetriscircuits.ui.UiUtil;

public class Computer {

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
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new ImagePanel());
        UiUtil.setIcons(frame);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.repaint();
    }

    public static void main(final String... args) {
        new Computer().launch();
    }
}
