package tetriscircuits.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.tuple.Pair;

public class Parser {
    
    private static final Object[][] TETRIMINOS_TABLE = {
        {  "t", 0, 0 },
        { "td", 0, 0 },
        { "tl", 0, 1 },
        { "tu", 0, 2 },
        { "tr", 0, 3 },
        
        {  "j", 1, 0 },
        { "jd", 1, 0 },
        { "jl", 1, 1 },
        { "ju", 1, 2 },
        { "jr", 1, 3 },
        
        {  "z", 2, 0 },
        { "zh", 2, 0 },
        { "zv", 2, 1 },
        
        {  "o", 3, 0 },
        { "os", 3, 0 },
        
        {  "s", 4, 0 },
        { "sh", 4, 0 },
        { "sv", 4, 1 },
        
        {  "l", 5, 0 },
        { "ld", 5, 0 },
        { "ll", 5, 1 },
        { "lu", 5, 2 },
        { "lr", 5, 3 },
        
        {  "i", 6, 0 },
        { "ih", 6, 0 },
        { "iv", 6, 1 },
    };
    
    private static final Map<String, Pair> TETRIMINOS = Collections.unmodifiableMap(new HashMap<>());
    
    static {
        for (final Object[] entry : TETRIMINOS_TABLE) {
            TETRIMINOS.put((String)entry[0], Pair.of(entry[1], entry[2]));
        }
    }    
    
    private static final Pattern NOT_WHITESPACE = Pattern.compile("[^\\s]+");
    
    public void parse(final File file) throws IOException, ParseException {
        parse(file.getPath(), new FileInputStream(file));
    }

    public void parse(final String filename) throws IOException, ParseException {
        parse(filename, new FileInputStream(filename));
    }
    
    public void parse(final File file, final InputStream in) throws IOException, ParseException {
        parse(file.getPath(), in);
    }
    
    public void parse(final String filename, final InputStream in) throws IOException, ParseException {
        
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
        
        processTokens(tokens);
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
    
    private void processTokens(final List<Token> tokens) throws ParseException {
        
    }
}
