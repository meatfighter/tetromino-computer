package tetrominocomputer.assembler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateInstructionDetectors {

    private final int[] lowerLines = new int[8];
    private final int[] upperLines = new int[8];
    private final List<String> tsLines = new ArrayList<>();    
    private final List<String> jsLines = new ArrayList<>();
    
    private final Pattern lowerPattern = Pattern.compile("il5 -(\\d+) 0");
    private final Pattern upperPattern = Pattern.compile("il5 -(\\d+) 163");
    
    private int coLine;
    
    public void launch() throws Exception {
        loadTetrominoScript();
        loadJavaScript();
        saveFiles();
    }
    
    private void saveFiles() throws Exception {
        for (Operator operator : Operator.values()) {
            final String name = operator.name();
            if (name.startsWith("T") || name.startsWith("PRINT")) {
                continue;
            }
            saveFiles(name, operator.getOpcode());
        }
    }
    
    private void saveFiles(final String name, final int opcode) throws Exception {
        saveTS(name, opcode);
        saveJS(name, opcode);
    }
    
    private void saveTS(final String name, final int opcode) throws Exception {
        for (int i = 0; i < 8; ++i) {
            final int index = -(5 + 10 * i);
            final boolean bit = ((opcode >> i) & 1) != 0;
            tsLines.set(lowerLines[i], String.format("%s %d 0", bit ? "not" : "il5", index));
            tsLines.set(upperLines[i], String.format("%s %d 163", bit ? "not" : "il5", index));
        }
        saveFile(String.format("ts/computer/%s_C.t", name), tsLines);
    }
    
    private void saveJS(final String name, final int opcode) throws Exception {
        final StringBuilder sb = new StringBuilder();        
        for (int i = 7; i >= 0; --i) {
            if (i == 7) {
                sb.append("co0 = ");
            } else {
                sb.append(" && ");
            }
            if (((opcode >> i) & 1) == 0) {
                sb.append("!");
            }
            sb.append("ii").append(i);            
        }
        sb.append(";");
        jsLines.set(coLine, sb.toString());
        saveFile(String.format("ts/computer/%s_C.js", name), jsLines);
    }    
    
    private void saveFile(final String filename, final List<String> lines) throws Exception {
        try (final PrintStream out = new PrintStream(filename)) {
            for (final String line : lines) {
                out.println(line);
            }
        }
    }
    
    private void loadJavaScript() throws Exception {
        try (final BufferedReader br = new BufferedReader(new FileReader("ts/computer/ZERO_C.js"))) {
            int lineNumber = 0;
            String line = null;
            while ((line = br.readLine()) != null) {
                jsLines.add(line);
                if (line.startsWith("co0 = ")) {
                    coLine = lineNumber;
                }
                ++lineNumber;
            }
        }
    }
    
    private void loadTetrominoScript() throws Exception {
        try (final BufferedReader br = new BufferedReader(new FileReader("ts/computer/ZERO_C.t"))) {
            int lineNumber = 0;
            String line = null;
            while ((line = br.readLine()) != null) {
                tsLines.add(line);
                final Matcher lowerMatcher = lowerPattern.matcher(line);
                if (lowerMatcher.find()) {
                    lowerLines[(Integer.parseInt(lowerMatcher.group(1)) - 5) / 10] = lineNumber;                    
                }
                final Matcher upperMatcher = upperPattern.matcher(line);
                if (upperMatcher.find()) {
                    upperLines[(Integer.parseInt(upperMatcher.group(1)) - 5) / 10] = lineNumber;                    
                }
                ++lineNumber;
            }
        }
    }
    
    public static void main(final String... args) throws Exception {
        new GenerateInstructionDetectors().launch();
    }
}
