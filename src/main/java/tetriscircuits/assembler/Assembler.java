package tetriscircuits.assembler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assembler {
    
    public void launch(final String asmFilename, final String binFilename) throws Exception {
                
    }
    
    private void assemble(final String asmFilename, final InputStream in, final OutputStream out) throws IOException {
        
        final Parser parser = new Parser();
        final List<Token> tokens;
        try {
            tokens = parser.parseTokens(asmFilename, in);
        } catch (final ParseException e) {
            System.out.println(e);
            System.exit(0);
            return;
        }
                        
        final Map<String, List<Integer>> constantUsages = new HashMap<>();
        final Map<String, Integer> constantValues = new HashMap();
        final int[] bytes = new int[0x10000];
        int origin;
        
        outer: for (final Token token : tokens) {
            switch (token.getType()) {
                case TokenType.END:
                    break outer;
            }
        }
    }
    

    
    private void addByte(final List<Integer> binary, final int value) {
        if (binary.size() >= 0x10000) {
            handleError("asm file too large.");
        }
        binary.add(value);
    }
    
    private void handleError(final String message) {
        System.out.format("Error: %s%n", message);
        System.exit(0);
    }
    
    public static void main(final String... args) throws Exception {
        
        if (args.length != 2) {
            System.out.println("args: [ asm filename ] [ bin filename ]");
            return;
        }
        
        new Assembler().launch(args[0], args[1]);
    }
}
