package tetriscircuits.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ConvertMetaTetrisScriptToHtml {
    
    private static final Pattern CODE_PATTERN = Pattern.compile("([^\\s]+\\s+)(\\d+\\s*)(;.*)?");

    public void launch(final File file, final boolean ignoreSwaps) throws Exception {
        final PrintStream out = System.out;
        printHeader(out);
        printFilename(out, file);
        try (final BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                processLine(out, line.trim(), ignoreSwaps);                
            }
        }
        printFooter(out);
    }
    
    private void printHeader(final PrintStream out) {
        out.println("<pre class=\"code\">");
    }
    
    private void printFilename(final PrintStream out, final File file) {
        out.format("<span class=\"filename\">%s</span>%n", file.getName());
    }
    
    private void processLine(final PrintStream out, final String line, final boolean ignoreSwaps) {        
        final Matcher matcher = CODE_PATTERN.matcher(line);                
        if (matcher.find()) {
            if (ignoreSwaps && matcher.group(1).startsWith("SWAP")) {
                return;
            }
            out.print("<span class=\"line\">");
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
            out.print("<span class=\"line\">");
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
        if (args.length != 2) {
            System.out.println("args: [ MetaTetrisScript filename ] [ ignore SWAPs (true/false) ]");
            return;
        }        
        
        new ConvertMetaTetrisScriptToHtml().launch(new File(args[0]), Boolean.parseBoolean(args[1]));
    }
}
