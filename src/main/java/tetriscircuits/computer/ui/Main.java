package tetriscircuits.computer.ui;

import com.bulenkov.darcula.DarculaLaf;
import java.awt.EventQueue;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    
    private void launch() {
        EventQueue.invokeLater(this::createFrame);
    }
    
    private void createFrame() {
        try {
            UIManager.setLookAndFeel(new DarculaLaf()); 
        } catch (final UnsupportedLookAndFeelException e) {
        }
        final PlayfieldFrame frame = new PlayfieldFrame();        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(final String... args) {
        new Main().launch();
    }    
}
