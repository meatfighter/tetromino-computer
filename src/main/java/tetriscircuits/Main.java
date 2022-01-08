package tetriscircuits;

import com.bulenkov.darcula.DarculaLaf;
import java.awt.EventQueue;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;
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
        
        controller.loadComponents();
    }
    
    public static void main(final String... args) throws Exception {
        new Main().launch();

//        ScriptEngineManager manager = new ScriptEngineManager();
//        
//        ScriptEngine engine = manager.getEngineByName("nashorn");
//        
//        final CompiledScript compiledScript = ((Compilable)engine).compile("b = !a;");
//        
//        final Bindings bindings = engine.createBindings();
//        bindings.put("a", true);
//        bindings.put("b", false);        
//        compiledScript.eval(bindings);
//        System.out.println(bindings.get("b"));
        
        

    }
}
