package tetrominocomputer.computer.ui;

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
        
        final PlayfieldModel playfieldModel = new PlayfieldModel();
        final int[][] cells = playfieldModel.getCells();
        for (int i = 0; i < 7; ++i) {
            cells[10][i + 2] = i + 1;
        }        
        frame.update(playfieldModel);
    }
    
    public static void main(final String... args) {
        new Main().launch();
    }    
}
