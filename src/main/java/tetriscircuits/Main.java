package tetriscircuits;

import com.bulenkov.darcula.DarculaLaf;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.UIManager;
import tetriscircuits.parser.Parser;
import tetriscircuits.ui.CircuitsEditorPanel;
import tetriscircuits.ui.CircuitsFrame;
import tetriscircuits.ui.LockedTetriminoRenderer;
import tetriscircuits.ui.TetriminoRenderer;

public class Main {

    public void launch() throws Exception {
//        final Map<String, Component> components = new HashMap<>();
//        final Parser parser = new Parser();
//        parser.parse(components, "circuits/components.txt");
//        for (final Component component : components.values()) {
//            System.out.println(component);
//        }
//
//        final Playfield playfield = new Playfield(64, 64, 1);
//        final Simulator simulator = new Simulator();
//        final List<LockedTetriminoRenderer> renderers = new ArrayList<>();
//        simulator.simulate(playfield, components.get("nand"), (tetrimino, x, y) -> {
//            renderers.add(new LockedTetriminoRenderer(TetriminoRenderer.fromTetrimino(tetrimino), x, y));
//        });
        
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
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);      
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.init(); 
        frame.setVisible(true);               
    }
    
    public static void main(final String... args) throws Exception {
        new Main().launch();
    }
}
