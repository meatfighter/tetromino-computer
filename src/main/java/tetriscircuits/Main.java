package tetriscircuits;

import java.util.HashMap;
import java.util.Map;
import tetriscircuits.parser.Parser;

public class Main {

    public void launch() throws Exception {
        final Map<String, Structure> structures = new HashMap<>();
        final Parser parser = new Parser();
        parser.parse(structures, "circuits/components.txt");
        for (final Structure structure : structures.values()) {
            System.out.println(structure);
        }
    }
    
    public static void main(final String... args) throws Exception {
        new Main().launch();
    }
}
