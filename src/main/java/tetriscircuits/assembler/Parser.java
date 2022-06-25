package tetriscircuits.assembler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isBlank;

class Parser {
    
    private static final Pattern NOT_WHITESPACE = Pattern.compile("[^\\s]+");
    
    public List<Token> parseTokens(final String filename, final InputStream in) throws IOException, ParseException {
        return parseTokens(filename, in, false);
    }
    
    public List<Token> parseTokens(final String filename, final InputStream in, final boolean includeComments) 
            throws IOException, ParseException {
        
        final List<Token> tokens = new ArrayList<>();
        
        int lineNumber = 1;
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(in))) {            
            String line = null;
            while ((line = br.readLine()) != null) {
                parseLine(tokens, filename, lineNumber++, line, includeComments);
            }
        }
        
        final Token token = new Token(filename, lineNumber, 0, "");
        token.setType(TokenType.END);
        tokens.add(token);
        tokens.add(token);
        
        return tokens;
    } 
    
    private void parseLine(final List<Token> tokens, final String filename, final int lineNumber, final String line, 
            final boolean includeComments) throws ParseException {
        
        final int commentIndex = line.indexOf(';');        
        final Matcher matcher = NOT_WHITESPACE.matcher((commentIndex < 0) ? line : line.substring(0, commentIndex));
        while (matcher.find()) {
            parseElement(tokens, filename, lineNumber, matcher.start() + 1, matcher.group(0));
        }        
        if (includeComments && commentIndex >= 0) {       
            parseComment(tokens, filename, lineNumber, commentIndex + 1, line.substring(commentIndex));
        }
    }
    
    private void parseElement(final List<Token> tokens, final String filename, final int lineNumber, 
            final int lineColumn, final String value) throws ParseException {
        
        if (parseNumber(tokens, filename, lineNumber, lineColumn, value)) {
            return;
        }
        
        if (parseKeyword(tokens, filename, lineNumber, lineColumn, value)) {
            return;
        }
        
        if (parseInstructionType(tokens, filename, lineNumber, lineColumn, value)) {
            return;
        }
        
        if (parseLabel(tokens, filename, lineNumber, lineColumn, value)) {
            return;
        }
        
        if (parseIdentifier(tokens, filename, lineNumber, lineColumn, value)) {
            return;
        }
        
        throw new ParseException(filename, lineNumber, lineColumn, "Unexpected token: " + value);
    }
    
    private boolean parseIdentifier(final List<Token> tokens, final String filename, final int lineNumber, 
            final int lineColumn, final String value) throws ParseException {
                     
        if (!isJumpIdentifier(value)) {
            return false;
        }
        
        Integer offset = null;
        final String[] tkns = value.split("[+-]");
        if (tkns.length == 2) {
            offset = Integer.parseInt(tkns[1]);
            if (value.contains("-")) {
                offset = -offset;
            }
        }
        
        final Token token = new Token(filename, lineNumber, lineColumn, value);
        token.setType(TokenType.IDENTIFIER);
        token.setStr(tkns[0].toUpperCase());
        token.setOffset(offset);
        tokens.add(token);
        return true;
    }    
    
    private boolean parseLabel(final List<Token> tokens, final String filename, final int lineNumber, 
            final int lineColumn, final String value) throws ParseException {
        
        if (!value.endsWith(":")) {
            return false;
        }
        
        final String v = value.substring(0, value.length() - 1);
        if (isBlank(v)) {
            throw new ParseException(filename, lineNumber, lineColumn, "Expected label name");
        }       
        if (!isIdentifier(v)) {
            throw new ParseException(filename, lineNumber, lineColumn, "Invalid label name");
        }
        
        final Token token = new Token(filename, lineNumber, lineColumn, value);
        token.setType(TokenType.LABEL);
        token.setStr(v.toUpperCase());
        tokens.add(token);
        return true;
    } 
    
    private boolean parseInstructionType(final List<Token> tokens, final String filename, final int lineNumber, 
            final int lineColumn, final String value) throws ParseException {
        
        final Operator operator;
        try {
            operator = Operator.valueOf(value.toUpperCase());
        } catch (final IllegalArgumentException e) {
            return false;
        }
        
        final Token token = new Token(filename, lineNumber, lineColumn, value);
        token.setType(TokenType.INSTRUCTION);
        token.setStr(value);
        token.setOperator(operator);
        tokens.add(token);
        return true;
    }
    
    private boolean parseKeyword(final List<Token> tokens, final String filename, final int lineNumber, 
            final int lineColumn, final String value) throws ParseException {
        
        final TokenType tokenType;
        switch(value.toUpperCase()) {
            case "DEFINE":
                tokenType = TokenType.DEFINE;
                break;
            case "SEGMENT":
                tokenType = TokenType.SEGMENT;
                break;    
            default:
                return false;
        }
         
        final Token token = new Token(filename, lineNumber, lineColumn, value);
        token.setType(tokenType);
        token.setStr(value);
        tokens.add(token);
        return true;
    }
    
    private boolean parseNumber(final List<Token> tokens, final String filename, final int lineNumber, 
            final int lineColumn, final String value) throws ParseException {
        
        if (!(value.length() == 2 || value.length() == 4)) {
            return false;
        }
        
        final int num;
        try {
            num = Integer.parseInt(value, 16);
        } catch (final NumberFormatException e) {
            return false;
        }
        
        final Token token = new Token(filename, lineNumber, lineColumn, value);
        token.setType((value.length() == 2) ? TokenType.BYTE : TokenType.WORD);
        token.setStr(value);
        token.setNum(num);
        tokens.add(token);
        
        return true;
    }
    
    private void parseComment(final List<Token> tokens, final String filename, final int lineNumber, 
            final int lineColumn, final String value) throws ParseException {
        
        final Token token = new Token(filename, lineNumber, lineColumn, value);
        token.setType(TokenType.COMMENT);
        token.setStr(value);
        tokens.add(token);        
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
    
    private boolean isJumpIdentifier(final String value) {                       
        final String[] tokens = value.split("[+-]");
        if (tokens.length > 2) {
            return false;
        }
        if (!isIdentifier(tokens[0])) {
            return false;
        }
        if (tokens.length == 2) {
            try {
                Integer.parseInt(tokens[1]);
            } catch (final NumberFormatException e) {
                return false;
            }
        }
        return true;
    }    
}
