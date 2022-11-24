package tetriscircuits;

import java.awt.EventQueue;
import javax.swing.JFrame;
import tetriscircuits.ui.CircuitsFrame;
import tetriscircuits.ui.UiUtil;

public class Main {
    
    private final Controller controller = new Controller();

    public void launch() throws Exception {
        License.getLicense();
        EventQueue.invokeLater(this::createFrame);
    }
    
    private void createFrame() {        
        UiUtil.setLookAndFeel();
        
        final CircuitsFrame frame = new CircuitsFrame();        
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        UiUtil.setIcons(frame);
        frame.pack();
        frame.setLocationRelativeTo(null);        
        frame.init();        
        frame.setController(controller);
        frame.setVisible(true);   
        
        controller.loadComponents();
    }
    
    public static void main(final String... args) throws Exception {
        new Main().launch();
    }
}
