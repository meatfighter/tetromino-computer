package tetriscircuits.ui;

import java.io.OutputStream;
import java.io.PrintStream;

public class HtmlGenerator {

    public void generate(final OutputStream out, final String tetrisScript) {
        try (final PrintStream o = new PrintStream(out)) {
            generate(o, tetrisScript);
        }
    }
    
    public void generate(final PrintStream out, final String tetrisScript) {
        out.println(tetrisScript);
    }
}
