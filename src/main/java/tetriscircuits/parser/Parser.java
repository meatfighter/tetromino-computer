package tetriscircuits.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isBlank;
import tetriscircuits.Border;
import tetriscircuits.Instruction;
import tetriscircuits.Point;
import tetriscircuits.Component;
import tetriscircuits.ComponentTest;
import tetriscircuits.Range;
import tetriscircuits.Terminal;
import tetriscircuits.TerminalType;
import tetriscircuits.Tetrimino;

public class Parser {
    
    private static final Pattern NOT_WHITESPACE = Pattern.compile("[^\\s]+");
    
    public Component parse(final Map<String, Component> components, final File file) 
            throws IOException, ParseException {
        return parse(components, file.getPath(), new FileInputStream(file));
    }

    public Component parse(final Map<String, Component> components, final String filename) 
            throws IOException, ParseException {
        return parse(components, filename, new FileInputStream(filename));
    }
    
    public Component parse(final Map<String, Component> components, final File file, final InputStream in) 
            throws IOException, ParseException {
        return parse(components, file.getPath(), in);
    }
    
    public Component parse(final Map<String, Component> components, final String filename, final InputStream in) 
            throws IOException, ParseException {
        
        final String componentName = deriveComponentName(filename);
        
        final List<Token> tokens = new ArrayList<>();
        
        int lineNumber = 1;
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(in))) {            
            String line = null;
            while ((line = br.readLine()) != null) {
                parseLine(tokens, filename, lineNumber++, removeComment(line));
            }
        }
        
        final Token token = new Token(filename, lineNumber, 0);
        token.setType(TokenType.END);
        tokens.add(token);
        tokens.add(token);
        
        return createComponent(componentName, tokens, components);
    }
    
    private String deriveComponentName(final String filename) {
        System.out.println(filename);
        final int index = filename.indexOf('.');
        if (index < 0) {
            return filename;
        }
        return filename.substring(0, index);
    }
    
    private String removeComment(final String line) {
        final int index = line.indexOf('#');
        if (index >= 0) {
            return line.substring(0, index);
        }
        return line;
    }
    
    private void parseLine(final List<Token> tokens, final String filename, final int lineNumber, final String line) 
            throws ParseException {
        
        if (isBlank(line)) {
            return;
        }
        
        final Matcher matcher = NOT_WHITESPACE.matcher(line);
        while (matcher.find()) {
            parseElement(tokens, filename, lineNumber, matcher.start() + 1, matcher.group(0));
        }
    }
    
    private void parseElement(final List<Token> tokens, final String filename, final int lineNumber, 
            final int lineColumn, final String value) throws ParseException {
        
        final int rangeIndex = value.indexOf("..");
        if (rangeIndex >= 0) {
            parseRange(tokens, filename, lineNumber, lineColumn, value, rangeIndex);
            return;
        }
        
        if (isIdentifier(value)) {
            final Token token = new Token(filename, lineNumber, lineColumn);
            token.setType(TokenType.STRING);
            token.setStr(value);
            tokens.add(token);
            return;
        }
        
        boolean[] bits = null;
        outer: {
            for (int i = 0; i < value.length(); ++i) {
                final char c = value.charAt(i);
                if (c != '0' && c != '1') {
                    break outer;
                }
            }
            bits = new boolean[value.length()];
            for (int i = 0; i < value.length(); ++i) {
                bits[i] = (value.charAt(i) == '1');
            }
        }
        
        try {
            final int num = Integer.parseInt(value);
            final Token token = new Token(filename, lineNumber, lineColumn);
            token.setType(TokenType.NUMBER);
            token.setStr(value);
            token.setBits(bits);
            token.setNum(num);
            tokens.add(token);
            return;
        } catch (final NumberFormatException e) {            
        }
        
        if (bits != null) {
            final Token token = new Token(filename, lineNumber, lineColumn);
            token.setType(TokenType.BITS);
            token.setStr(value);
            token.setBits(bits);
            tokens.add(token);
            return;
        }
        
        throw new ParseException(filename, lineNumber, lineColumn, "Unexpected token: " + value);
    }
    
    private boolean isIdentifier(final String value) {
        if (!Character.isJavaIdentifierStart(value.charAt(0))) {
            return false;
        }
        for (int i = 1; i < value.length(); ++i) {
            if (!Character.isJavaIdentifierPart(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    private void parseRange(final List<Token> tokens, final String filename, final int lineNumber, final int lineColumn, 
            final String value, final int rangeIndex) throws ParseException {
        
        final String left = value.substring(0, rangeIndex);
        final String right = value.substring(rangeIndex + 2);
        
        int rightValue = 0;
        try {
            rightValue = Integer.parseInt(right);
        } catch (final NumberFormatException e) {
            throw new ParseException(filename, lineNumber, lineColumn, "Invalid range: right value is not a number.");
        }
        
        int c = 0;
        for (; c < left.length() && Character.isLetter(left.charAt(c)); ++c) {            
        }
        if (c == left.length()) {
            throw new ParseException(filename, lineNumber, lineColumn, 
                    "Invalid range: left value does not container a number.");
        }
        
        final String str;
        final String leftNum;
        if (c > 0) {
            str = left.substring(0, c);
            if (!isIdentifier(str)) {
                throw new ParseException(filename, lineNumber, lineColumn, "Invalid range: bad left identifier.");
            }
            leftNum = left.substring(c);            
        } else {
            str = null;
            leftNum = left;
        }
        int leftValue = 0;
        try {
            leftValue = Integer.parseInt(leftNum);
        } catch (final NumberFormatException e) {
            throw new ParseException(filename, lineNumber, lineColumn, 
                    "Invalid range: left value does not contain a valid number.");
        }
        
        final Token token = new Token(filename, lineNumber, lineColumn);
        token.setType(TokenType.RANGE);
        token.setStr(str);
        token.setNum(leftValue);
        token.setNum2(rightValue);
        tokens.add(token);
    }
    
    private <T> T[][] toArray(final List<List<T>> as, final T[] array) {        
        final T[][] ts = (T[][])Array.newInstance(array.getClass(), as.size());
        for (int i = ts.length - 1; i >= 0; --i) {
            final List<T> list = as.get(i);
            ts[i] = list.toArray(array);
        }        
        return ts;
    }
    
    private Component createComponent(final String name, final List<Token> tokens, 
            final Map<String, Component> components) throws ParseException {
        
        int i = 0;
        
        final Component component = components.computeIfAbsent(name, n -> new Component(n));
        
        final List<Instruction> instructions = new ArrayList<>();
        final List<Terminal> inputs = new ArrayList<>();
        final List<Terminal> outputs = new ArrayList<>();
        Border border = null;
        
        outer: while (true) {
            final Token operationToken = tokens.get(i);  
            if (operationToken.getType() == TokenType.END) {
                break;
            } else if (operationToken.getType() != TokenType.STRING) {
                throw new ParseException(operationToken, "Operation expected.");
            }
            final String operation = operationToken.getStr();
            switch(operation) {
                case "border":
                    border = processBorder(tokens, i + 1);
                    i += 2;
                    break;
                case "in":
                    i = processTerminals(tokens, TerminalType.INPUT, inputs, i + 1);
                    break;
                case "out":
                    i = processTerminals(tokens, TerminalType.INPUT, outputs, i + 1);
                    break;
                default:
                    i = processInstruction(tokens, instructions, operationToken, components, i + 1);
                    break;
            }
        }
        
        component.setInstructions(instructions.toArray(new Instruction[instructions.size()]));
        component.setInputs(inputs.toArray(new Terminal[inputs.size()]));
        component.setInputs(outputs.toArray(new Terminal[outputs.size()]));
        
        return component;
    }
    
    private Border processBorder(final List<Token> tokens, final int index) throws ParseException {
        
        final Token rangeXToken = tokens.get(index);
        if (rangeXToken.getType() != TokenType.RANGE || rangeXToken.getStr() != null) {
            throw new ParseException(rangeXToken, "Expected X range.");
        }
        
        final Token maxYToken = tokens.get(index + 1);
        if (maxYToken.getType() != TokenType.NUMBER) {
            throw new ParseException(maxYToken, "Expected max Y.");
        }
        
        return new Border(new Range(rangeXToken.getNum(), rangeXToken.getNum2()), maxYToken.getNum());
    }
    
    private int processInstruction(final List<Token> tokens, final List<Instruction> instructions, 
            final Token operationToken, final Map<String, Component> components, 
            final int index) throws ParseException {
        
        final String operation = operationToken.getStr();
        final Tetrimino tetrimino = Tetrimino.fromName(operation);
        final Component component = (tetrimino == null) ? components.computeIfAbsent(operation, n -> new Component(n)) 
                : null;
        
        final List<Integer> moves = new ArrayList<>();
        int i = index;
        while (true) {
            final Token token = tokens.get(i);
            if (token.getType() != TokenType.NUMBER) {
                break;
            }
            ++i;
            moves.add(token.getNum());
        }
        
        if (moves.isEmpty()) {
            throw new ParseException(operationToken, "Missing moves.");
        }
        if (tetrimino == null && moves.size() != 2) {
            throw new ParseException(operationToken, "Invalid component coordinates.");
        }
        final int[] ms = new int[moves.size()];
        for (int j = ms.length - 1; j >= 0; --j) {
            ms[j] = moves.get(j);
        }
                        
        instructions.add(new Instruction(tetrimino, component, ms));
        
        return i;
    }
    
    private int processTerminals(final List<Token> tokens, final TerminalType type, final List<Terminal> terminals, 
            final int index) throws ParseException {
        
        final Token nameToken = tokens.get(index);
        if (nameToken.getType() != TokenType.STRING) {
            throw new ParseException(nameToken, "Missing terminal name.");
        }

        final Token rangeToken = tokens.get(index + 1);
        if (rangeToken.getType() != TokenType.NUMBER && rangeToken.getType() != TokenType.RANGE) {
            throw new ParseException(rangeToken, "Missing terminal range.");
        }

        final Range range;
        if (rangeToken.getType() == TokenType.NUMBER) {
            range = new Range(rangeToken.getNum());
        } else if (rangeToken.getStr() != null) {
            throw new ParseException(rangeToken, "Expected number or numerical range.");
        } else {
            range = new Range(rangeToken.getNum(), rangeToken.getNum2());
            if (range.getNum() > range.getNum2()) {
                throw new ParseException(rangeToken, "Expected min..max");
            }
        }                    
        
        return index + 2;
    }
}
