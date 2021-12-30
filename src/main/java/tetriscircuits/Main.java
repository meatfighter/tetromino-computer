package tetriscircuits;

import com.bulenkov.darcula.DarculaLaf;
import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.UIManager;
import tetriscircuits.parser.Parser;
import tetriscircuits.ui.CircuitsEditorPanel;
import tetriscircuits.ui.CircuitsFrame;

public class Main {

    public void launch() throws Exception {
//        final Map<String, Component> components = new HashMap<>();
//        final Parser parser = new Parser();
//        parser.parse(components, "circuits/components.txt");
//        for (final Component component : components.values()) {
//            System.out.println(component);
//        }

//        final Playfield playfield = new Playfield(26, 10, 2);
//        for(int i = 0; i < 26; ++i) {
//            playfield.set(i, 4, i + 1);
//        }
//        System.out.println(playfield);

        EventQueue.invokeLater(this::createFrame);
    }
    
    private void createFrame() {
        
        try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(new DarculaLaf()); 
        } catch (final Exception e) {
            e.printStackTrace();
        }
        final CircuitsFrame frame = new CircuitsFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(final String... args) throws Exception {
        new Main().launch();
    }
}
