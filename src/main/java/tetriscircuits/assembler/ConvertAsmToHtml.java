package tetriscircuits.assembler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class ConvertAsmToHtml {
    
    private final PrintStream out = System.out;
    
    private int lineNumber;
    private int columnNumber;

    public void launch(final String asmFilename) throws Exception {        
        lineNumber = 0;
        columnNumber = 1;
        printHeader(asmFilename);
        try (final InputStream in = new FileInputStream(asmFilename)) {  
            for (final Token token : new Parser().parseTokens(asmFilename, in, true)) {
                processToken(token);
                if (token.getType() == TokenType.END) {
                    break;
                }
            }
        }
        printFooter();
    }
    
    private void processToken(final Token token) {
        
        while (lineNumber != token.getLineNumber()) {
            if (lineNumber > 0) {
                out.println("</span>");
            }
            ++lineNumber;
            columnNumber = 1;
            out.print("<span class=\"line\">");
        }
        
        if (token.getType() == TokenType.END) {
            out.println("</span>");
            return;
        }
        
        while (columnNumber < token.getLineColumn()) {
            out.print(' ');
            ++columnNumber;
        }
        
        final String spanClass;
        switch (token.getType()) {
            case BYTE:
            case WORD:    
                spanClass = "number";
                break;
            case COMMENT:
                spanClass = "comment";
                break;
            case DEFINE:
            case SEGMENT:
                spanClass = "keyword";
                break;
            case INSTRUCTION:
                spanClass = "normal";
                break;
            case LABEL:
                spanClass = "label";
                break;
            case IDENTIFIER:
                spanClass = "identifier";
                break;
            default:
                throw new RuntimeException("Unknown token type: " + token.getType());
        }

        if (token.getType() == TokenType.IDENTIFIER) {
            final int plusIndex = token.getRawStr().indexOf('+');
            if (plusIndex >= 0) {
                final String identifier = token.getRawStr().substring(0, plusIndex);
                final String offset = token.getRawStr().substring(plusIndex + 1);
                System.out.format("<span class=\"identifier\">%s</span>"
                        + "<span class=\"normal\">+</span><span class=\"number\">%s</span>", identifier, offset);
            } else {
                final int minusIndex = token.getRawStr().indexOf('-');
                if (minusIndex > 0) {
                    final String identifier = token.getRawStr().substring(0, minusIndex);
                    final String offset = token.getRawStr().substring(minusIndex + 1);
                    System.out.format("<span class=\"identifier\">%s</span>"
                            + "<span class=\"normal\">-</span><span class=\"number\">%s</span>", identifier, offset);
                } else {
                    System.out.format("<span class=\"identifier\">%s</span>", token.getRawStr());
                }
            }
        } else {
            System.out.format("<span class=\"%s\">%s</span>", spanClass, token.getRawStr());
        }
        columnNumber += token.getRawStr().length();
    }
    
    private void printHeader(final String asmFilename) {
        out.println("<pre class=\"code\">");
        out.format("<span class=\"filename\">%s</span>%n", asmFilename);
    }
    
    private void printFooter() {
        out.println("</pre>");
    }
    
    public static void main(final String... args) throws Exception {
        
        if (args.length != 1) {
            System.out.println("args: [ asm filename ]");
            return;
        }
        
        new ConvertAsmToHtml().launch(args[0]);
    }
}
