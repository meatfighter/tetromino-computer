package tetrominocomputer.computer.simulator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MirrorTetrominoScript {
    
    public static final String INPUT_FILENAME = "ts/computer/copyCAB.t";
    
    private static final Pattern SINGLE_PATTERN = Pattern.compile("((o)|(ih)) (-?\\d+)");
    private static final Pattern SWAP_PATTERN = Pattern.compile("((swap)|(true)|(false)|(xnor)|(and)) (-?\\d+) (\\d+)");
    private static final Pattern IR_PATTERN = Pattern.compile("ir(\\d+) (-?\\d+) (\\d+)");
    private static final Pattern IL_PATTERN = Pattern.compile("il(\\d+) (-?\\d+) (\\d+)");
    private static final Pattern JU_PATTERN = Pattern.compile("ju (-?\\d+)");
    private static final Pattern LR_PATTERN = Pattern.compile("lr (-?\\d+)");
    private static final Pattern MUX_LEFT_PATTERN = Pattern.compile("muxLeft (-?\\d+) (\\d+)");
    private static final Pattern MUX_RIGHT_PATTERN = Pattern.compile("muxRight (-?\\d+) (\\d+)");

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
            final String obj = swapMatcher.group(1);
            final int x = Integer.parseInt(swapMatcher.group(7));
            final int y = Integer.parseInt(swapMatcher.group(8));
            System.out.format("%s %d %d%n", obj, -x + 1, y);
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
        
        final Matcher juMatcher = JU_PATTERN.matcher(line);
        if (juMatcher.find()) {            
            final int x = Integer.parseInt(juMatcher.group(1));
            System.out.format("lu %d%n", -x);
            return;
        }
        
        final Matcher lrMatcher = LR_PATTERN.matcher(line);
        if (lrMatcher.find()) {            
            final int x = Integer.parseInt(lrMatcher.group(1));
            System.out.format("jl %d%n", -x);
            return;
        }
        
        final Matcher muxLeftMatcher = MUX_LEFT_PATTERN.matcher(line);
        if (muxLeftMatcher.find()) {
            final int x = Integer.parseInt(muxLeftMatcher.group(1));
            final int y = Integer.parseInt(muxLeftMatcher.group(2));
            System.out.format("muxRight %d %d%n", -x + 1, y);
            return;
        }
        
        final Matcher muxRightMatcher = MUX_RIGHT_PATTERN.matcher(line);
        if (muxRightMatcher.find()) {
            final int x = Integer.parseInt(muxRightMatcher.group(1));
            final int y = Integer.parseInt(muxRightMatcher.group(2));
            System.out.format("muxLeft %d %d%n", -x + 1, y);
            return;
        }
    }
    
    public static void main(final String... args) throws Exception {
        new MirrorTetrominoScript().launch();
    }
}