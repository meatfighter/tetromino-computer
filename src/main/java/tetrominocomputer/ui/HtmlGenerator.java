package tetrominocomputer.ui;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlGenerator {
    
    private enum Style {
        NORMAL("normal"),
        KEYWORD("keyword"),
        NUMBER("number"),
        COMMENT("comment"),
        END("end");
        
        private final String name;
        
        private Style(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    private static final Pattern KEYWORD_PATTERN = Pattern.compile("(in|out)($|\\s)");
    private static final Pattern NUMBER_PATTERN 
            = Pattern.compile("((-?[0-9]+)|(-?[0-9]+\\.\\.-?[0-9]+))($|\\s)");    

    public void generate(final OutputStream out, final String componentName, final String tetrominoScript) {
        try (final PrintStream o = new PrintStream(out)) {
            generate(o, componentName, tetrominoScript);
        }
    }
    
    public void generate(final PrintStream out, final String componentName, final String tetrominoScript) {
        out.format("<pre class=\"code\">%n");
        out.format("<span class=\"filename\">%s.t</span>%n", componentName);        
        try (final Scanner scanner = new Scanner(tetrominoScript)) {
            while (scanner.hasNextLine()) {
                processLine(out, scanner.nextLine().trim());
            }
        }
        out.format("</pre>%n");
    }
    
    private void processLine(final PrintStream out, final String line) {
        
        out.format("<span class=\"line\">");
        
        if (!line.isEmpty()) {        
            final Style[] styles = new Style[line.length() + 1];
            for (int i = styles.length - 1; i >= 0; --i) {
                styles[i] = Style.NORMAL;
            }
            applyStyle(styles, line, KEYWORD_PATTERN, Style.KEYWORD);
            applyStyle(styles, line, NUMBER_PATTERN, Style.NUMBER);
            final int index = line.indexOf('#');
            if (index >= 0) {
                for (int i = line.length() - 1; i >= index; --i) {
                    styles[i] = Style.COMMENT;
                }
            }
            styles[styles.length - 1] = Style.END;

            Style style = styles[0];
            int s = 0;
            int e = 1;
            while (true) {
                if (styles[e] == style) {
                    ++e;
                    continue;
                }
                out.format("<span class=\"%s\">%s</span>", style, line.substring(s, e));
                if (styles[e] == Style.END) {
                    break;
                }
                style = styles[e];
                s = e;
                ++e;
            }
        }
        
        out.format("</span>%n");
    }
    
    private void applyStyle(final Style[] styles, final String line, final Pattern pattern, final Style style) {
        final Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            if (matcher.start() != 0 && !Character.isWhitespace(line.charAt(matcher.start() - 1))) {
                continue;
            }
            for (int i = matcher.start(); i < matcher.end(); ++i) {
                styles[i] = style;
            }
        }
    }
}
