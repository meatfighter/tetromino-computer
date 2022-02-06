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
        
        if (parseComma(tokens, filename, lineNumber, lineColumn, value)) {
            return;
        }
        
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
                     
        if (!isIdentifier(value)) {
            return false;
        }
        
        final Token token = new Token(filename, lineNumber, lineColumn, value.length());
        token.setType(TokenType.IDENTIFIER);
        token.setStr(value.toUpperCase());
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
        
        final Token token = new Token(filename, lineNumber, lineColumn, value.length());
        token.setType(TokenType.LABEL);
        token.setStr(v);
        tokens.add(token);
        return true;
    }
    
    private boolean parseComma(final List<Token> tokens, final String filename, final int lineNumber, 
            final int lineColumn, final String value) throws ParseException {
        
        if (!",".equals(value)) {
            return false;
        }
        
        final Token token = new Token(filename, lineNumber, lineColumn, value.length());
        token.setType(TokenType.COMMA);
        token.setStr(value);
        tokens.add(token);
        return true;
    }    
    
    private boolean parseInstructionType(final List<Token> tokens, final String filename, final int lineNumber, 
            final int lineColumn, final String value) throws ParseException {
        
        final InstructionType instructionType;
        try {
            instructionType = InstructionType.valueOf(value.toUpperCase());
        } catch (final IllegalArgumentException e) {
            return false;
        }
        
        final Token token = new Token(filename, lineNumber, lineColumn, value.length());
        token.setType(TokenType.INSTRUCTION);
        token.setStr(value);
        token.setInstructionType(instructionType);
        tokens.add(token);
        return true;
    }
    
    private boolean parseKeyword(final List<Token> tokens, final String filename, final int lineNumber, 
            final int lineColumn, final String value) throws ParseException {
        
        final TokenType tokenType;
        switch(value.toUpperCase()) {
            case ".DATA":
                tokenType = TokenType.DATA;
                break;
            case ".DEFINE":
                tokenType = TokenType.DEFINE;
                break;            
            case ".ORIGIN":
                tokenType = TokenType.ORIGIN;
                break;
            case ".SEGMENT":
                tokenType = TokenType.SEGMENT;
                break;    
            default:
                return false;
        }
         
        final Token token = new Token(filename, lineNumber, lineColumn, value.length());
        token.setType(tokenType);
        token.setStr(value);
        tokens.add(token);
        return true;
    }
    
    private boolean parseNumber(final List<Token> tokens, final String filename, final int lineNumber, 
            final int lineColumn, final String value) throws ParseException {
        
        int num = 0;
        TokenType tokenType = null;
        
        int start = 0;
        int base = 0;
        if (value.startsWith("$")) {
            start = 1;
            base = 16;
        } else if (value.startsWith("#$")) {
            start = 2;
            base = 16;
        } else if (value.startsWith("%")) {
            start = 1;
            base = 2;
        } else if (value.startsWith("#%")) {
            start = 2;
            base = 2;
        } else if (value.startsWith("-")) {
            start = 0;
            base = 10;
        } else {
            final char c = value.charAt(0);
            if (c >= '0' && c <= '9') {
                start = 0;
                base = 10;
            } else {
                return false;
            }
        }
        
        final String v = value.substring(start).replaceAll("_", "");
        if (isBlank(v)) {
            throw new ParseException(filename, lineNumber, lineColumn, "Expected number");
        }
        if (base == 16) {
            tokenType = (v.length() <= 2) ? TokenType.BYTE : TokenType.WORD;
        } 
        try {
            num = Integer.parseInt(v, base);
        } catch (final NumberFormatException e) {
            throw new ParseException(filename, lineNumber, lineColumn, "Invalid number: " + value);
        }
        if (base == 10) {
            tokenType = (num >= Byte.MIN_VALUE && num <= Byte.MAX_VALUE) ? TokenType.BYTE : TokenType.WORD;            
        }
        
        final Token token = new Token(filename, lineNumber, lineColumn, value.length());
        token.setType(tokenType);
        token.setStr(value);
        token.setNum(num);
        tokens.add(token);
        
        return true;
    }

    private String removeComment(final String line) {
        final int index = line.indexOf(';');
        if (index >= 0) {
            return line.substring(0, index);
        }
        return line;
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
