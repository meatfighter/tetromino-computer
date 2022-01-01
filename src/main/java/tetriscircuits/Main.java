package tetriscircuits;

import com.bulenkov.darcula.DarculaLaf;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.UIManager;
import tetriscircuits.ui.CircuitsFrame;

public class Main {
    
    private final Controller controller = new Controller();

    public void launch() throws Exception {       
        EventQueue.invokeLater(this::createFrame);
    }
    
    private void createFrame() {        
        try {
            UIManager.setLookAndFeel(new DarculaLaf()); 
        } catch (final Exception e) {
            e.printStackTrace();
        }
        final CircuitsFrame frame = new CircuitsFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);      
        frame.pack();
        frame.setLocationRelativeTo(null);        
        frame.init();        
        frame.setController(controller);
        frame.setVisible(true);               
    }
    
    public static void main(final String... args) throws Exception {
        new Main().launch();
    }
}
