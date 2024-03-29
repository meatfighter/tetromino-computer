package tetrominocomputer.ts;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tetrominocomputer.sim.Instruction;
import tetrominocomputer.sim.Component;
import tetrominocomputer.sim.HorizontalLine;
import tetrominocomputer.sim.Terminal;
import tetrominocomputer.sim.TerminalType;
import tetrominocomputer.sim.Tetromino;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class LexerParser {
    
    private static final Pattern NOT_WHITESPACE = Pattern.compile("[^\\s]+");
    
    public String translate(final Map<String, Component> components, final String filename, 
            final String tetrominoScript, final int offsetX, final int offsetY) 
            throws IOException, LexerParserException {
        
        final List<Token> tokens = tokenize(filename, new ByteArrayInputStream(tetrominoScript.getBytes()));
        createComponent(filename, tokens, components);
        
        final Map<Integer, List<Token>> toks = new HashMap<>();
        tokens.stream().filter(token -> token.getValueType() != TokenValueType.UNKNOWN).forEachOrdered(
                token ->  toks.computeIfAbsent(token.getLineNumber(), lineNumber -> new ArrayList<>()).add(token)
        );
                
        final StringBuilder text = new StringBuilder();
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(tetrominoScript.getBytes())))) {            
            int lineNumber = 1;
            String line;
            while ((line = br.readLine()) != null) {
                final StringBuilder sb = new StringBuilder(line);
                final List<Token> ts = toks.get(lineNumber);
                if (ts != null) {
                    for (int i = ts.size() - 1; i >= 0; --i) {
                        final Token token = ts.get(i);
                        int num = token.getNum();
                        int num2 = token.getNum2();
                        if (offsetX != 0 && token.getValueType() == TokenValueType.X) {
                            num += offsetX;
                            num2 += offsetX;
                        } else if (offsetY != 0 && token.getValueType() == TokenValueType.Y) {
                            num += offsetY;
                            num2 += offsetY;
                        } else {
                            continue;
                        }
                        final int start = token.getLineColumn() - 1;
                        sb.replace(start, start + token.getLength(), (token.getType() == TokenType.NUMBER) 
                                ? Integer.toString(num) : String.format("%d..%d", num, num2));
                    }
                }
                text.append(sb).append('\n');
                ++lineNumber;
            }
        }
        
        return text.toString();
    }
    
    public Component parse(final Map<String, Component> components, final File file) 
            throws IOException, LexerParserException {
        return parse(components, file.getName(), new FileInputStream(file));
    }

    public Component parse(final Map<String, Component> components, final String filename) 
            throws IOException, LexerParserException {
        return parse(components, filename, new FileInputStream(filename));
    }
    
    public Component parse(final Map<String, Component> components, final File file, final InputStream in) 
            throws IOException, LexerParserException {
        return parse(components, file.getName(), in);
    }
    
    public Component parse(final Map<String, Component> components, final String filename, final InputStream in) 
            throws IOException, LexerParserException {
        return createComponent(deriveComponentName(filename), tokenize(filename, in), components);
    }
    
    private List<Token> tokenize(final String filename, final InputStream in) throws IOException, LexerParserException {
        final List<Token> tokens = new ArrayList<>();
        
        int lineNumber = 1;
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(in))) {            
            String line;
            while ((line = br.readLine()) != null) {
                tokenizeLine(tokens, filename, lineNumber++, removeComment(line));
            }
        }
        
        final Token token = new Token(filename, lineNumber, 0, 0);
        token.setType(TokenType.END);
        tokens.add(token);
        tokens.add(token);
        
        return tokens;
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
    
    private void tokenizeLine(final List<Token> tokens, final String filename, final int lineNumber, final String line) 
            throws LexerParserException {
        
        if (isBlank(line)) {
            return;
        }
        
        final Matcher matcher = NOT_WHITESPACE.matcher(line);
        while (matcher.find()) {
            tokenizeElement(tokens, filename, lineNumber, matcher.start() + 1, matcher.group(0));
        }
    }
    
    private void tokenizeElement(final List<Token> tokens, final String filename, final int lineNumber, 
            final int lineColumn, final String value) throws LexerParserException {
        
        final int rangeIndex = value.indexOf("..");
        if (rangeIndex >= 0) {
            tokenizeRange(tokens, filename, lineNumber, lineColumn, value, rangeIndex);
            return;
        }
        
        if (isIdentifier(value)) {
            final Token token = new Token(filename, lineNumber, lineColumn, value.length());
            token.setType(TokenType.STRING);
            token.setStr(value);
            tokens.add(token);
            return;
        }
        
        try {
            final int num = Integer.parseInt(value);
            final Token token = new Token(filename, lineNumber, lineColumn, value.length());
            token.setType(TokenType.NUMBER);
            token.setStr(value);
            token.setNum(num);
            tokens.add(token);
            return;
        } catch (final NumberFormatException e) {            
        }
        
        throw new LexerParserException(filename, lineNumber, lineColumn, "Unexpected token: " + value);
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
    
    private void tokenizeRange(final List<Token> tokens, final String filename, final int lineNumber, 
            final int lineColumn, final String value, final int rangeIndex) throws LexerParserException {
        
        final String left = value.substring(0, rangeIndex);
        final String right = value.substring(rangeIndex + 2);

        int leftValue = 0;
        try {
            leftValue = Integer.parseInt(left);
        } catch (final NumberFormatException e) {
            throw new LexerParserException(filename, lineNumber, lineColumn, 
                    "Invalid range: left value is not a number.");
        }
        
        int rightValue = 0;
        try {
            rightValue = Integer.parseInt(right);
        } catch (final NumberFormatException e) {
            throw new LexerParserException(filename, lineNumber, lineColumn, 
                    "Invalid range: right value is not a number.");
        }
        
        final Token token = new Token(filename, lineNumber, lineColumn, value.length());
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
            final Map<String, Component> components) throws LexerParserException {
        
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
                throw new LexerParserException(operationToken, "Operation expected.");
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
                throw new LexerParserException(token, component.getName() + " has duplicate terminal name: " 
                        + terminal.getName());
            }
        }
    }
    
    private int processInstruction(final List<Token> tokens, final List<Instruction> instructions, 
            final Token operationToken, final Map<String, Component> components, 
            final int index) throws LexerParserException {
        
        final String operation = operationToken.getStr();
        final Tetromino tetromino = Tetromino.fromName(operation);
        final Component component = (tetromino == null) ? components.computeIfAbsent(operation, n -> new Component(n)) 
                : null;
        
        String alias = null;
        String name = null;
        int i = index;
        if (component != null) {
            name = alias = component.getName();
            final Token aliasToken = tokens.get(i);
            if (aliasToken.getType() == TokenType.STRING) {
                ++i;
                alias = aliasToken.getStr();
            }
        }        
        
        final List<Integer> moves = new ArrayList<>();
        while (true) {
            final Token token = tokens.get(i);
            if (token.getType() != TokenType.NUMBER) {
                break;
            }            
            ++i;
            token.setValueType((moves.size() & 1) == 0 ? TokenValueType.X : TokenValueType.Y);
            moves.add(token.getNum());            
        }
        
        if (moves.isEmpty()) {
            throw new LexerParserException(operationToken, "Missing moves.");
        }
        if (tetromino == null && moves.size() != 2) {
            throw new LexerParserException(operationToken, "Invalid component coordinates.");
        }
        final int[] ms = new int[moves.size()];
        for (int j = ms.length - 1; j >= 0; --j) {
            ms[j] = moves.get(j);
        }
                        
        instructions.add(new Instruction(tetromino, name, alias, ms));
        
        return i;
    }
    
    private int processTerminals(final List<Token> tokens, final TerminalType type, final List<Terminal> terminals, 
            final int index) throws LexerParserException {
        
        int i = index;
        
        final Token nameToken = tokens.get(i++);
        if (nameToken.getType() != TokenType.STRING) {
            throw new LexerParserException(nameToken, "Missing terminal name.");
        }
        
        final List<HorizontalLine> horizontalLines = new ArrayList<>();
        while (true) {
            final Token xToken = tokens.get(i++);
            if (xToken.getType() != TokenType.NUMBER && xToken.getType() != TokenType.RANGE) {
                break;
            }
            xToken.setValueType(TokenValueType.X);
            
            final Token yToken = tokens.get(i++);
            if (yToken.getType() != TokenType.NUMBER) {
                throw new LexerParserException(yToken, "Expected Y number.");
            }
            yToken.setValueType(TokenValueType.Y);
            
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