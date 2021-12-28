package tetriscircuits;

import tetriscircuits.parser.Parser;

public class Main {

    public void launch() throws Exception {
        final Parser parser = new Parser();
        parser.parse("circuits/components.txt");
    }
    
    public static void main(final String... args) throws Exception {
        new Main().launch();
    }
}
