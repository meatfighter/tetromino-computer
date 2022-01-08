package tetriscircuits.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isBlank;
import tetriscircuits.Instruction;
import tetriscircuits.Component;
import tetriscircuits.HorizontalLine;
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
        
        try {
            final int num = Integer.parseInt(value);
            final Token token = new Token(filename, lineNumber, lineColumn);
            token.setType(TokenType.NUMBER);
            token.setStr(value);
            token.setNum(num);
            tokens.add(token);
            return;
        } catch (final NumberFormatException e) {            
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

        int leftValue = 0;
        try {
            leftValue = Integer.parseInt(left);
        } catch (final NumberFormatException e) {
            throw new ParseException(filename, lineNumber, lineColumn, "Invalid range: left value is not a number.");
        }
        
        int rightValue = 0;
        try {
            rightValue = Integer.parseInt(right);
        } catch (final NumberFormatException e) {
            throw new ParseException(filename, lineNumber, lineColumn, "Invalid range: right value is not a number.");
        }
        
        final Token token = new Token(filename, lineNumber, lineColumn);
        token.setType(TokenType.RANGE);
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
        
        outer: while (true) {
            final Token operationToken = tokens.get(i);  
            if (operationToken.getType() == TokenType.END) {
                break;
            } else if (operationToken.getType() != TokenType.STRING) {
                throw new ParseException(operationToken, "Operation expected.");
            }
            final String operation = operationToken.getStr();
            switch(operation) {
                case "in":
                    i = processTerminals(tokens, TerminalType.INPUT, inputs, i + 1);
                    break;
                case "out":
                    i = processTerminals(tokens, TerminalType.OUTPUT, outputs, i + 1);
                    break;
                default:
                    i = processInstruction(tokens, instructions, operationToken, components, i + 1);
                    break;
            }
        }
        
        component.setInstructions(instructions.toArray(new Instruction[instructions.size()]));
        component.setInputs(inputs.toArray(new Terminal[inputs.size()]));
        component.setOutputs(outputs.toArray(new Terminal[outputs.size()]));
        
        verifyUniqueTerminalNames(tokens.get(0), component);
        
        // TODO REMOVE
        System.out.println(component);
        
        return component;
    }
    
    private void verifyUniqueTerminalNames(final Token token, final Component component) {
        final Set<String> names = new HashSet<>();
        verifyUniqueTerminalNames(token, component, names, component.getInputs());
    }
    
    private void verifyUniqueTerminalNames(final Token token, final Component component, final Set<String> names, 
            final Terminal[] terminals) {
        for (final Terminal terminal : terminals) {
            if (names.contains(terminal.getName())) {
                throw new ParseException(token, component.getName() + " has duplicate terminal name: " 
                        + terminal.getName());
            }
        }
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
        
        int i = index;
        
        final Token nameToken = tokens.get(i++);
        if (nameToken.getType() != TokenType.STRING) {
            throw new ParseException(nameToken, "Missing terminal name.");
        }
        
        final List<HorizontalLine> horizontalLines = new ArrayList<>();
        while (true) {
            final Token xToken = tokens.get(i++);
            if (xToken.getType() != TokenType.NUMBER && xToken.getType() != TokenType.RANGE) {
                break;
            }
            
            final Token yToken = tokens.get(i++);
            if (yToken.getType() != TokenType.NUMBER) {
                throw new ParseException(yToken, "Expected Y number.");
            }
            
            if (xToken.getType() == TokenType.NUMBER) {
                horizontalLines.add(new HorizontalLine(xToken.getNum(), yToken.getNum()));
            } else {
                horizontalLines.add(new HorizontalLine(xToken.getNum(), xToken.getNum2(), yToken.getNum()));
            }
        }
        
        terminals.add(new Terminal(type, nameToken.getStr(), horizontalLines.toArray(
                new HorizontalLine[horizontalLines.size()])));

        return i - 1;
    }
}
