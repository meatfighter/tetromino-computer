package tetriscircuits;

import java.util.HashMap;
import java.util.Map;
import tetriscircuits.parser.Parser;

public class Main {

    public void launch() throws Exception {
        final Map<String, Component> components = new HashMap<>();
        final Parser parser = new Parser();
        parser.parse(components, "circuits/components.txt");
        for (final Component component : components.values()) {
            System.out.println(component);
        }
    }
    
    public static void main(final String... args) throws Exception {
        new Main().launch();
    }
}
