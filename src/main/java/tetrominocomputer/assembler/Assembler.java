package tetrominocomputer.assembler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Math.max;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assembler {
    
    private static class ConstantUsage {
        
        private final int address;
        private final Integer offset;

        public ConstantUsage(final int address, final Integer offset) {
            this.address = address;
            this.offset = offset;
        }

        public int getAddress() {
            return address;
        }

        public Integer getOffset() {
            return offset;
        }
    }
    
    public void launch(final String asmFilename, final String binFilename) throws Exception {
        try (final InputStream in = new BufferedInputStream(new FileInputStream(asmFilename));
                final OutputStream out = new BufferedOutputStream(new FileOutputStream(binFilename))) {
             assemble(asmFilename, in, out);
        } catch (final ParseException e) {
            System.out.println(e);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void assemble(final String asmFilename, final InputStream in, final OutputStream out) 
            throws IOException, ParseException {
        
        final List<Token> tokens = new Parser().parseTokens(asmFilename, in);                        
        final Map<String, List<ConstantUsage>> constantUsages = new HashMap<>();
        final Map<String, Integer> constantByteValues = new HashMap();
        final Map<String, Integer> constantWordValues = new HashMap();
        final int[] bytes = new int[0x10000];
        
        int tokenIndex = 0;
        int address = 0;
        int maxAddress = 0;
        outer: while (true) {
            final Token token = tokens.get(tokenIndex);
            System.out.println(token.getType());
            switch (token.getType()) {
                case BYTE:
                    bytes[address++ & 0xFFFF] = 0xFF & token.getNum();                    
                    ++tokenIndex;
                    break;
                case WORD:
                    bytes[address++ & 0xFFFF] = 0xFF & (token.getNum() >> 8);
                    bytes[address++ & 0xFFFF] = 0xFF & token.getNum();
                    ++tokenIndex;
                    break;
                case DEFINE:
                    tokenIndex = pullConstant(tokens, tokenIndex, constantByteValues, constantWordValues);
                    break;
                case END:
                    break outer;
                case INSTRUCTION:                                     
                    tokenIndex = pullInstruction(tokens, tokenIndex, bytes, address, constantUsages);                    
                    address += token.getOperator().getLength();
                    break;
                case LABEL:
                    if (constantWordValues.containsKey(token.getStr())) {
                        throw new ParseException(token, "Duplicate label");
                    }
                    constantWordValues.put(token.getStr(), address);
                    ++tokenIndex;
                    break;       
                case SEGMENT:
                    address = pullAddress(tokens, tokenIndex);
                    tokenIndex += 2;
                    break;
                default:
                    throw new ParseException(token, "Unexpected token");
            }
            maxAddress = max(maxAddress, address);
        }
        
        for (Map.Entry<String, List<ConstantUsage>> entry : constantUsages.entrySet()) {
            final Integer wordValue = constantWordValues.get(entry.getKey());
            if (wordValue != null) {
                for (final ConstantUsage usage : entry.getValue()) {
                    final int addr = usage.getAddress();
                    int value = wordValue;
                    if (usage.getOffset() != null) {
                        value += usage.getOffset();
                    }
                    bytes[addr] = 0xFF & (value >> 8);
                    bytes[addr + 1] = 0xFF & value;
                }
            } else {
                final Integer byteValue = constantByteValues.get(entry.getKey());
                if (byteValue != null) {
                    for (final ConstantUsage usage : entry.getValue()) {
                        int value = byteValue;
                        if (usage.getOffset() != null) {
                            value += usage.getOffset();
                        }
                        bytes[usage.getAddress()] = 0xFF & value;
                    }
                } else {
                    throw new ParseException(asmFilename, 0, 0, "Undefined constant or label: " + entry.getKey());
                }
            }
        }
        
        for (int i = 0; i < maxAddress; ++i) {
            out.write(bytes[i]);
        }
        
        Integer main = constantWordValues.get("MAIN");
        if (main == null) {
            main = 0;
        }        
        out.write(0xFF & (main >> 8));
        out.write(0xFF & main);
    }
    
    private int pullInstruction(final List<Token> tokens, final int index, final int[] bytes, final int address,
            final Map<String, List<ConstantUsage>> constantUsages) throws ParseException {
        
        final Token operatorToken = tokens.get(index);
        final Operator operator = operatorToken.getOperator();
        bytes[address & 0xFFFF] = operator.getOpcode();
        
        switch (operator.getLength()) {
            case 2: {
                final Token operandToken = tokens.get(index + 1);
                switch (operandToken.getType()) {
                    case BYTE:
                        bytes[(address + 1) & 0xFFFF] = 0xFF & operandToken.getNum();
                        break;
                    case IDENTIFIER:
                        constantUsages.computeIfAbsent(operandToken.getStr(), k -> new ArrayList<>()).add(
                                new ConstantUsage(address + 1, operandToken.getOffset()));
                        break;
                    default:
                        throw new ParseException(operandToken, "Expected immediate byte value or constant");
                }
                return index + 2;
            }
            case 3: {
                final Token operandToken = tokens.get(index + 1);
                switch (operandToken.getType()) {
                    case BYTE:
                        bytes[(address + 1) & 0xFFFF] = 0x00;
                        bytes[(address + 2) & 0xFFFF] = 0xFF & operandToken.getNum();
                        break;
                    case WORD:
                        bytes[(address + 1) & 0xFFFF] = 0xFF & (operandToken.getNum() >> 8);
                        bytes[(address + 2) & 0xFFFF] = 0xFF & operandToken.getNum();
                        break;
                    case IDENTIFIER:
                        constantUsages.computeIfAbsent(operandToken.getStr(), k -> new ArrayList<>()).add(
                                new ConstantUsage(address + 1, operandToken.getOffset()));
                        break;
                    default:
                        throw new ParseException(operandToken, "Expected immediate address, constant, or label");
                }
                return index + 2;
            }
        }
        
        return index + 1;
    }
    
    private int pullConstant(final List<Token> tokens, final int index, final Map<String, Integer> constantByteValues, 
            final Map<String, Integer> constantWordValues) throws ParseException {
        
        final Token nameToken = tokens.get(index + 1);
        if (nameToken.getType() != TokenType.IDENTIFIER) {
            throw new ParseException(nameToken, "Expected constant name");
        }   
        if (nameToken.getOffset() != null) {
            throw new ParseException(nameToken, "Unexpected offset");
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

    public static void main(final String... args) throws Exception {
        
        if (args.length != 2) {
            System.out.println("args: [ asm filename ] [ bin filename ]");
            return;
        }
        
        new Assembler().launch(args[0], args[1]);
    }
}
