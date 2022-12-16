package tetrominocomputer.tse.app;

import java.awt.EventQueue;
import javax.swing.JFrame;
import tetrominocomputer.tse.ui.CircuitsFrame;
import tetrominocomputer.util.Ui;

public class TetrominoScriptEditor {
    
    private static final float DEFAULT_FONT_SIZE_MULTIPLIER = 1f;
    
    private final Controller controller = new Controller();

    public void launch(final float fontSizeMultiplier) throws Exception {
        License.getLicense();
        EventQueue.invokeLater(() -> createFrame(fontSizeMultiplier));
    }
    
    private void createFrame(final float fontSizeMultiplier) {
        Ui.initLookAndFeel();        
        Ui.initFontSize(fontSizeMultiplier);        
        
        final CircuitsFrame frame = new CircuitsFrame();        
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Ui.initIcons(frame);
        frame.pack();
        frame.setLocationRelativeTo(null);        
        frame.init();        
        frame.setController(controller);
        frame.setVisible(true);   
        
        controller.loadComponents();
    }
    
    public static void main(final String... args) throws Exception {
        
        float fontSizeMultiplier = DEFAULT_FONT_SIZE_MULTIPLIER;
        for (int i = 0; i < args.length; ++i) {
            if ("-f".equals(args[i]) && i != args.length - 1) {
                boolean error = false;
                try {
                    fontSizeMultiplier = Float.parseFloat(args[++i]);
                    if (fontSizeMultiplier < 0.5f || fontSizeMultiplier > 4f) {
                        error = true;
                    }
                } catch (final NumberFormatException e) {
                    error = true;
                }
                if (error) {
                    System.err.println("Invalid font size multiplier.");
                    return;
                }
            }
        }        
        
        new TetrominoScriptEditor().launch(fontSizeMultiplier);
    }
}
