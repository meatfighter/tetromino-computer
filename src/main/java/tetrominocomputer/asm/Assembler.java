package tetrominocomputer.asm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;
import tetrominocomputer.util.Dirs;
import tetrominocomputer.util.Out;

public class Assembler {
    
    private static final String DEFAULT_ASM_FILENAME = Dirs.ASM + "example.asm";
    private static final String DEFAULT_BIN_FILENAME = Dirs.BIN + "example.bin";
    
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
               
        Out.timeTask(String.format("Assembling %s", asmFilename), () -> {
            
            final File asmFile = new File(asmFilename);
            if (!(asmFile.isFile() && asmFile.exists())) {
                printFailure("File not found: %s", asmFilename);
                return null;
            }
            
            final File binFile = new File(binFilename);
            if (binFile.exists()) {
                if (binFile.isDirectory()) {
                    printFailure("Binary is existing directory: %s", binFilename);
                    return null;
                }
                if (binFile.isFile() && !binFile.delete()) {
                    printFailure("Failed to delete file: %s", binFilename);
                    return null;
                }
            }

            try (final InputStream in = new BufferedInputStream(new FileInputStream(asmFile));
                    final OutputStream out = new BufferedOutputStream(new FileOutputStream(binFile))) {
                assemble(asmFilename, in, out);
                System.out.println("BUILD SUCCESS");
                System.out.format("Created %s%n%n", binFilename);
            } catch (final LexerException e) {
                printFailure(e.toString());
            }
            
            return null;
        });
    }
    
    private void printFailure(final String message, final Object... args) {
        System.err.println("BUILD FAILURE");
        System.err.format(message, args);
        System.err.format("%n%n");
    }
    
    private void assemble(final String asmFilename, final InputStream in, final OutputStream out) 
            throws IOException, LexerException {
        
        final List<Token> tokens = new Lexer().tokenize(asmFilename, in);                        
        final Map<String, List<ConstantUsage>> constantUsages = new HashMap<>();
        final Map<String, Integer> constantByteValues = new HashMap();
        final Map<String, Integer> constantWordValues = new HashMap();
        final int[] bytes = new int[0x10000];
        
        int tokenIndex = 0;
        int address = 0;
        int maxAddress = 0;
        outer: while (true) {
            final Token token = tokens.get(tokenIndex);
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
                        throw new LexerException(token, "Duplicate label");
                    }
                    constantWordValues.put(token.getStr(), address);
                    ++tokenIndex;
                    break;       
                case SEGMENT:
                    address = pullAddress(tokens, tokenIndex);
                    tokenIndex += 2;
                    break;
                default:
                    throw new LexerException(token, "Unexpected token");
            }
            maxAddress = max(maxAddress, address);
        }
        
        constantUsages.entrySet().forEach(entry -> {
            final Integer wordValue = constantWordValues.get(entry.getKey());
            if (wordValue != null) {
                entry.getValue().forEach(usage -> {
                    final int addr = usage.getAddress();
                    int value = wordValue;
                    if (usage.getOffset() != null) {
                        value += usage.getOffset();
                    }
                    bytes[addr] = 0xFF & (value >> 8);
                    bytes[addr + 1] = 0xFF & value;
                });
            } else {
                final Integer byteValue = constantByteValues.get(entry.getKey());
                if (byteValue != null) {
                    entry.getValue().forEach(usage -> {
                        int value = byteValue;
                        if (usage.getOffset() != null) {
                            value += usage.getOffset();
                        }
                        bytes[usage.getAddress()] = 0xFF & value;
                    });
                } else {
                    throw new LexerException(asmFilename, 0, 0, "Undefined constant or label: " + entry.getKey());
                }
            }
        });
        
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
            final Map<String, List<ConstantUsage>> constantUsages) throws LexerException {
        
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
                        throw new LexerException(operandToken, "Expected immediate byte value or constant");
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
                        throw new LexerException(operandToken, "Expected immediate address, constant, or label");
                }
                return index + 2;
            }
        }
        
        return index + 1;
    }
    
    private int pullConstant(final List<Token> tokens, final int index, final Map<String, Integer> constantByteValues, 
            final Map<String, Integer> constantWordValues) throws LexerException {
        
        final Token nameToken = tokens.get(index + 1);
        if (nameToken.getType() != TokenType.IDENTIFIER) {
            throw new LexerException(nameToken, "Expected constant name");
        }   
        if (nameToken.getOffset() != null) {
            throw new LexerException(nameToken, "Unexpected offset");
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
                throw new LexerException(valueToken, "Expected constant value");
        }
        
        return index + 3;
    }
    
    private int pullAddress(final List<Token> tokens, final int index) throws LexerException {
        
        final Token addressToken = tokens.get(index + 1);
        switch(addressToken.getType()) {
            case BYTE:
            case WORD:
                return addressToken.getNum();
            default:
                throw new LexerException(addressToken, "Expected origin address.");
        }
    }

    public static void main(final String... args) throws Exception {
        
        String asmFilename = DEFAULT_ASM_FILENAME;
        String binFilename = DEFAULT_BIN_FILENAME;
        for (int i = 0; i < args.length; i++) {
            if ("-o".equals(args[i]) && i != args.length - 1) {
                binFilename = args[++i];
            } else {
                asmFilename = args[i];
            }
        }
               
        new Assembler().launch(asmFilename, binFilename);
    }
}
