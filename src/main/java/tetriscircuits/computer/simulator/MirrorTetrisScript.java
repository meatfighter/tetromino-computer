package tetriscircuits.computer.simulator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MirrorTetrisScript {
    
    public static final String INPUT_FILENAME = "workspace/computer/func_AB_FB.t";
    
    private static final Pattern SINGLE_PATTERN = Pattern.compile("((o)|(ih)) (-?\\d+)");
    private static final Pattern SWAP_PATTERN = Pattern.compile("swap (-?\\d+) (\\d+)");
    private static final Pattern IR_PATTERN = Pattern.compile("ir(\\d+) (-?\\d+) (\\d+)");
    private static final Pattern IL_PATTERN = Pattern.compile("il(\\d+) (-?\\d+) (\\d+)");

    public void launch() throws Exception {
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(INPUT_FILENAME)))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                processLine(line);
            }
        }
    }
    
    private void processLine(final String line) throws Exception {
        
        final Matcher singleMatcher = SINGLE_PATTERN.matcher(line);
        if (singleMatcher.find()) {
            final String obj = singleMatcher.group(1);
            final int x = Integer.parseInt(singleMatcher.group(4));
            System.out.format("%s %d%n", obj, -x + 1);
            return;
        }
        
        final Matcher swapMatcher = SWAP_PATTERN.matcher(line);
        if (swapMatcher.find()) {
            final int x = Integer.parseInt(swapMatcher.group(1));
            final int y = Integer.parseInt(swapMatcher.group(2));
            System.out.format("swap %d %d%n", -x + 1, y);
            return;
        }
        
        final Matcher irMatcher = IR_PATTERN.matcher(line);
        if (irMatcher.find()) {            
            final int len = Integer.parseInt(irMatcher.group(1));
            final int x = Integer.parseInt(irMatcher.group(2));
            final int y = Integer.parseInt(irMatcher.group(3));
            System.out.format("il%d %d %d%n", len, -x, y);
            return;
        }
        
        final Matcher ilMatcher = IL_PATTERN.matcher(line);
        if (ilMatcher.find()) {            
            final int len = Integer.parseInt(ilMatcher.group(1));
            final int x = Integer.parseInt(ilMatcher.group(2));
            final int y = Integer.parseInt(ilMatcher.group(3));
            System.out.format("ir%d %d %d%n", len, -x, y);
            return;
        }
    }
    
    public static void main(final String... args) throws Exception {
        new MirrorTetrisScript().launch();
    }
}