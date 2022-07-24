package tetriscircuits.computer.parser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class Parser {
    
    private static final Pattern NOT_WHITESPACE = Pattern.compile("[^\\s]+");
    
    public Runnable[] parse(final RunnableSource runnableSource, final String filename) 
            throws IOException, ParseException {
        
        try (final InputStream in = new BufferedInputStream(new FileInputStream(filename))) {
            return parse(runnableSource, filename, in);
        }
    }
    
    public Runnable[] parse(final RunnableSource runnableSource, final String filename, final InputStream in) 
            throws IOException, ParseException {
        
        final List<Runnable> runnables = new ArrayList<>();
        final List<Token> tokens = parseTokens(filename, in);
        
        int i = 0;
        while (true) {
            final Token componentToken = tokens.get(i++);
            if (componentToken.getType() == TokenType.END) {
                break;
            } else if (componentToken.getType() != TokenType.STRING) {
                throw new ParseException(componentToken, "Component name expected.");
            }
            
            final Token indexToken = tokens.get(i++);
            if (indexToken.getType() != TokenType.NUMBER) {
                throw new ParseException(indexToken, "Component index expected.");
            } 
            
            runnables.add(runnableSource.createRunnable(componentToken.getStr(), indexToken.getNum()));
        }
        
        return runnables.toArray(new Runnable[runnables.size()]);
    }    
    
    private List<Token> parseTokens(final String filename, final InputStream in) throws IOException, ParseException {
        final List<Token> tokens = new ArrayList<>();
        
        int lineNumber = 1;
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(in))) {            
            String line = null;
            while ((line = br.readLine()) != null) {
                parseLine(tokens, filename, lineNumber++, removeComment(line));
            }
        }
        
        final Token token = new Token(filename, lineNumber, 0, 0);
        token.setType(TokenType.END);
        tokens.add(token);
        tokens.add(token);
        
        return tokens;
    } 
    
    private String removeComment(final String line) {
        final int index = line.indexOf(';');
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
}
