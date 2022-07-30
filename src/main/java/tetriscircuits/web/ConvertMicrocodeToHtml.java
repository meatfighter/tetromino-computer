package tetriscircuits.web;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ConvertMicrocodeToHtml {
    
    private static final Pattern CODE_PATTERN = Pattern.compile("([^\\s]+\\s+I\\+)(\\d+\\s*)(;.*)?");

    public void launch() throws Exception {
        final PrintStream out = System.out;
        printHeader(out);
        try (final BufferedReader br = new BufferedReader(new FileReader("microcode/microcode.txt"))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                processLine(out, line.trim());                
            }
        }
        printFooter(out);
    }
    
    private void printHeader(final PrintStream out) {
        out.println("<pre class=\"code\">");
    }
    
    private void processLine(final PrintStream out, final String line) {
        out.print("<span class=\"line\">");
        final Matcher matcher = CODE_PATTERN.matcher(line);
        if (matcher.find()) {
            out.print("<span class=\"normal\">");
            out.print(matcher.group(1));
            out.print("</span>");
            out.print("<span class=\"number\">");
            out.print(matcher.group(2));
            out.print("</span>");
            if (isNotBlank(matcher.group(3))) {
                out.print("<span class=\"comment\">");
                out.print(matcher.group(3));
                out.print("</span>");
            }
        } else if (line.startsWith(";")) {
            out.print("<span class=\"comment\">");
            out.print(line);
            out.print("</span>");
        }
        out.println("</span>");
    }
    
    private void printFooter(final PrintStream out) {
        out.println("</pre>");
    }
    
    public static void main(final String... args) throws Exception {
        new ConvertMicrocodeToHtml().launch();
    }
}
