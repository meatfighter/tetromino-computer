package tetrominocomputer.tse.app;

import java.awt.EventQueue;
import java.io.PrintStream;
import javax.swing.JFrame;
import org.apache.commons.io.output.NullOutputStream;
import tetrominocomputer.tse.ui.CircuitsFrame;
import tetrominocomputer.util.Out;
import tetrominocomputer.util.Ui;

import static org.burningwave.core.assembler.StaticComponentContainer.Modules;

public class TetrominoScriptEditor {
    
    private static final float DEFAULT_FONT_SIZE_MULTIPLIER = 1f;
    
    private final Controller controller = new Controller();

    public void launch(final float fontSizeMultiplier) throws Exception {
        License.getLicense();
        initDarcula();        
        EventQueue.invokeLater(() -> createFrame(fontSizeMultiplier));
    }
    
    // https://github.com/bulenkov/Darcula/issues/23#issuecomment-920747794
    private void initDarcula() {
        final PrintStream out = System.out;
        try {
            System.setOut(new PrintStream(NullOutputStream.NULL_OUTPUT_STREAM));
            if (Modules != null) {
                Modules.exportAllToAll();
            }
        } finally {
            System.setOut(out);
        }
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
        for (int i = 0; i < args.length - 1; ++i) {
            if ("-m".equals(args[i])) {
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
                    Out.printlnError("Invalid font size multiplier.");
                    return;
                }
            }
        }        
        
        new TetrominoScriptEditor().launch(fontSizeMultiplier);
    }
}
