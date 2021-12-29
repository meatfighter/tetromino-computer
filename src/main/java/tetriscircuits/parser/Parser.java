package tetriscircuits.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.tuple.Pair;
import tetriscircuits.Instruction;
import tetriscircuits.Point;
import tetriscircuits.Structure;
import tetriscircuits.Tetrimino;

public class Parser {
    
    private static final Pattern NOT_WHITESPACE = Pattern.compile("[^\\s]+");
    
    public void parse(final Map<String, Structure> structures, final File file) throws IOException, ParseException {
        parse(structures, file.getPath(), new FileInputStream(file));
    }

    public void parse(final Map<String, Structure> structures, final String filename) 
            throws IOException, ParseException {
        parse(structures, filename, new FileInputStream(filename));
    }
    
    public void parse(final Map<String, Structure> structures, final File file, final InputStream in) 
            throws IOException, ParseException {
        parse(structures, file.getPath(), in);
    }
    
    public void parse(final Map<String, Structure> structures, final String filename, final InputStream in) 
            throws IOException, ParseException {
        
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
        
        processTokens(tokens, structures);
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
        
        if (":".equals(value)) {
            final Token token = new Token(filename, lineNumber, lineColumn);
            token.setType(TokenType.COLON);
            tokens.add(token);
            return;
        }
        
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
    
    private void processTokens(final List<Token> tokens, final Map<String, Structure> structures) 
            throws ParseException {
        
        final Map<String, Token> unknownStructures = new HashMap<>();
        
        outer: for (int i = 0; i < tokens.size(); ) {
            final Token token = tokens.get(i);
            switch(token.getType()) {
                case STRING:
                    switch(token.getStr()) {
                        case "struct":
                            i = processStruct(tokens, structures, unknownStructures, i + 1);
                            break;
                        default:
                            throw new ParseException(token, "Unknown keyword.");
                    }
                    break;
                case END:
                    break outer;
                default:
                    throw new ParseException(token, "Keyword expected.");
            }            
        }
        
        if (!unknownStructures.isEmpty()) {
            for (Map.Entry<String, Token> entry : unknownStructures.entrySet()) {
                throw new ParseException(entry.getValue(), "Undefined struct: " + entry.getKey());
            }
        }
    }
    
    private Point[][] toArray(final List<List<Point>> points) {
        final Point[][] ps = new Point[points.size()][];
        for (int i = ps.length - 1; i >= 0; --i) {
            final List<Point> list = points.get(i);
            ps[i] = list.toArray(new Point[list.size()]);
        }        
        return ps;
    }
    
    private int processStruct(final List<Token> tokens, final Map<String, Structure> structures, 
            final Map<String, Token> unknownStructures, final int index) throws ParseException {
        
        int i = index;
        
        final Token nameToken = tokens.get(i++);
        if (nameToken.getType() != TokenType.STRING) {
            throw new ParseException(nameToken, "struct missing name.");
        }
        final String name = nameToken.getStr();        
        unknownStructures.remove(name);
        Structure structure = structures.get(name);
        if (structure == null) {            
            structure = new Structure(name);
            structures.put(name, structure);
        } 
        
        final List<Instruction> instructions = new ArrayList<>();
        final List<List<Point>> inputs = new ArrayList<>();
        final List<List<Point>> outputs = new ArrayList<>();
        
        outer: while (true) {
            final Token operationToken = tokens.get(i);  
            if (operationToken.getType() == TokenType.END) {
                break;
            } else if (operationToken.getType() != TokenType.STRING) {
                throw new ParseException(operationToken, "Operation expected.");
            }
            final String operation = operationToken.getStr();
            switch(operation) {
                case "struct":
                case "def":    
                    break outer;
                case "in":
                    i = processStructTerminals(tokens, inputs, operationToken, i + 1);
                    break;
                case "out":
                    i = processStructTerminals(tokens, outputs, operationToken, i + 1);
                    break;
                default:
                    i = processInstruction(tokens, instructions, operationToken, structures, unknownStructures, i + 1);
                    break;
            }
        }
        
        if (instructions.isEmpty()) {
            throw new ParseException(nameToken, "struct has no instructions.");
        }
        structure.setInstructions(instructions.toArray(new Instruction[instructions.size()]));
        structure.setInputs(toArray(inputs));
        structure.setOutputs(toArray(outputs));
        
        return i;
    }
    
    private int processInstruction(final List<Token> tokens, final List<Instruction> instructions, 
            final Token operationToken, final Map<String, Structure> structures, 
            final Map<String, Token> unknownStructures, final int index) throws ParseException {
        
        final String operation = operationToken.getStr();
        final Pair indexAndRotation = Tetrimino.INDEX_AND_ROTATIONS.get(operation);
        final Structure structure = structures.get(operation);
        if (indexAndRotation == null && structure == null) {
            unknownStructures.put(operation, operationToken);
        }        
        
        final Token tokenX = tokens.get(index);
        if (tokenX.getType() != TokenType.NUMBER) {
            throw new ParseException(tokenX, "Number expected for x.");
        }
        final int x = tokenX.getNum();
        final Integer y;
        final Integer x2;
        int i = index + 1;
        
        final Token tokenY = tokens.get(i);
        if (tokenY.getType() == TokenType.NUMBER) {
            ++i;
            y = tokenY.getNum();            
            final Token tokenX2 = tokens.get(i);
            if (tokenX2.getType() == TokenType.NUMBER) {
                ++i;
                x2 = tokenX2.getNum();
            } else {
                x2 = null;
            }
        } else {
            y = null;
            x2 = null;
        }
        
        instructions.add(new Instruction(indexAndRotation, structure, x, y, x2));
        
        return i;
    }
    
    private int processStructTerminals(final List<Token> tokens, List<List<Point>> terminals, 
            final Token operationToken, final int index) {
        
        int i = index;
        
        final List<Point> terms = new ArrayList<>();
        while (true) {
            final Token tokenX = tokens.get(i);
            final Token tokenY = tokens.get(i + 1);
            
            if (tokenX.getType() != TokenType.NUMBER && tokenX.getType() != TokenType.RANGE) {
                break;
            }
            
            if (tokenY.getType() != TokenType.NUMBER && tokenY.getType() != TokenType.RANGE) {
                throw new ParseException(tokenY, "Expected number or range.");
            }
            
            i += 2;
            
            int x1;
            int x2;
            if (tokenX.getType() == TokenType.NUMBER) {
                x1 = x2 = tokenX.getNum();
            } else {
                x1 = tokenX.getNum();
                x2 = tokenX.getNum2();
                if (x2 < x1) {
                    final int t = x1;
                    x1 = x2;
                    x2 = t;
                }
            }
            
            int y1;
            int y2;
            if (tokenY.getType() == TokenType.NUMBER) {
                y1 = y2 = tokenY.getNum();
            } else {
                y1 = tokenY.getNum();
                y2 = tokenY.getNum2();
                if (y2 < y1) {
                    final int t = y1;
                    y1 = y2;
                    y2 = t;
                }
            }
            
            
            for (int y = y1; y <= y2; ++y) {
                for (int x = x1; x <= x2; ++x) {
                    terms.add(new Point(x, y));
                }
            }            
        }
        
        if (terms.isEmpty()) {
            throw new ParseException(operationToken, "Missing terminal coordinates.");
        }
        terminals.add(terms);
        
        return i;
    }
}
