package tetriscircuits.assembler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assembler {
    
    public void launch(final String asmFilename, final String binFilename) throws Exception {
        try (final InputStream in = new FileInputStream(asmFilename);
                final OutputStream out = new FileOutputStream(binFilename)) {
             assemble(asmFilename, in, out);
        } catch (final Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void assemble(final String asmFilename, final InputStream in, final OutputStream out) 
            throws IOException, ParseException {
        
        final List<Token> tokens = new Parser().parseTokens(asmFilename, in);                        
        final Map<String, List<Integer>> constantUsages = new HashMap<>();
        final Map<String, Integer> constantByteValues = new HashMap();
        final Map<String, Integer> constantWordValues = new HashMap();
        final int[] bytes = new int[0x10000];
        int origin;
        
        int tokenIndex = 0;
        int address = 0;
        outer: while (true) {
            final Token token = tokens.get(tokenIndex);
            switch (token.getType()) {
                case DEFINE:
                    tokenIndex = pullConstant(tokens, tokenIndex, constantByteValues, constantWordValues);
                    break;
                case END:
                    break outer;
                case INSTRUCTION:                 
                    bytes[address] = token.getInstructionType().getOpcode();
                    address += token.getInstructionType().getLength();
                    break;
                case LABEL:
                    constantWordValues.put(token.getStr(), address);
                    ++tokenIndex;
                    break;
                case ORIGIN:
                    origin = pullAddress(tokens, tokenIndex);
                    tokenIndex += 2;
                    break;       
                case SEGMENT:
                    address = pullAddress(tokens, tokenIndex);
                    tokenIndex += 2;
                    break;
            }
        }
    }
    
    private int pullInstruction(final List<Token> tokens, final int index, 
            final Map<String, List<Integer>> constantUsages) throws ParseException {
        
    }
    
    private int pullConstant(final List<Token> tokens, final int index, final Map<String, Integer> constantByteValues, 
            final Map<String, Integer> constantWordValues) throws ParseException {
        
        final Token nameToken = tokens.get(index + 1);
        if (nameToken.getType() != TokenType.IDENTIFIER) {
            throw new ParseException(nameToken, "Expected constant name");
        }        
        
        final Token valueToken = tokens.get(index + 2);
        switch(valueToken.getType()) {
            case BYTE:
                constantByteValues.put(nameToken.getStr(), valueToken.getNum());
                break;
            case WORD:
                constantWordValues.put(nameToken.getStr(), valueToken.getNum());
                break;
            default:
                throw new ParseException(valueToken, "Expected constant value");
        }
        
        return index + 3;
    }
    
    private int pullAddress(final List<Token> tokens, final int index) throws ParseException {
        
        final Token addressToken = tokens.get(index + 1);
        switch(addressToken.getType()) {
            case BYTE:
            case WORD:
                return addressToken.getNum();
            default:
                throw new ParseException(addressToken, "Expected origin address.");
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
