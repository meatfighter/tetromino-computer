package tetriscircuits.computer.metatetrisscript;

import java.io.BufferedInputStream;
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

public class Parser {
    
    private static final Pattern NOT_WHITESPACE = Pattern.compile("[^\\s]+");
              
    public List<Instruction> expand(final String script, final Map<String, Instruction[]> scripts) 
            throws IOException {
        return expand(script, scripts, 0, null);
    }
    
    public List<Instruction> expand(final String script, final Map<String, Instruction[]> scripts, final int offset) 
            throws IOException{
        return expand(script, scripts, offset, null);
    }
    
    public List<Instruction> expand(final String script, final Map<String, Instruction[]> scripts, final int offset, 
            final List<Instruction> instructions) throws IOException {
        
        final List<Instruction> insts = (instructions == null) ? new ArrayList<>() : instructions;
        
        final Instruction[] is = scripts.get(script);
        if (is == null) {
            insts.add(new Instruction(script, offset));
        } else {
            for (final Instruction instruction : is) {
                expand(instruction.getComponent(), scripts, offset + instruction.getIndex(), insts);
            }
        }
        
        return insts;
    }
    
    public Map<String, Instruction[]> parseAll(final String filename) throws IOException, ParseException {
        return parseAll(new File(filename), null);
    }
    
    public Map<String, Instruction[]> parseAll(final File file) throws IOException, ParseException {
        return parseAll(file, null);
    }
    
    public Map<String, Instruction[]> parseAll(final String filename, final Map<String, Instruction[]> scripts) 
            throws IOException, ParseException {
        return parseAll(new File(filename), scripts);
    }
    
    public Map<String, Instruction[]> parseAll(final File file, final Map<String, Instruction[]> scripts) 
            throws IOException, ParseException {
        
        final Map<String, Instruction[]> ss = (scripts == null) ? new HashMap<>() : scripts;
        
        if (file == null) {
            return ss;
        } else if (file.isDirectory()) {
            for (final File f : file.listFiles()) {
                parseAll(f, ss);
            }            
        } else if (file.isFile()) {
            final String name = file.getName();
            if (name.endsWith(".mt")) {
                ss.put(name.substring(0, name.length() - 3), parse(file));                
            }
        }
        
        return ss;
    }
    
    public Instruction[] parse(final File file) throws IOException, ParseException {        
        try (final InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            return parse(file.toString(), in);
        }
    }
    
    public Instruction[] parse(final String filename) throws IOException, ParseException {        
        try (final InputStream in = new BufferedInputStream(new FileInputStream(filename))) {
            return parse(filename, in);
        }
    }
    
    public Instruction[] parse(final File file, final InputStream in) throws IOException, ParseException {
        return parse(file.toString(), in);
    }
    
    public Instruction[] parse(final String filename, final InputStream in) throws IOException, ParseException {
        
        final List<Instruction> instructions = new ArrayList<>();
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
            
            instructions.add(new Instruction(componentToken.getStr(), indexToken.getNum()));
        }
        
        return instructions.toArray(new Instruction[instructions.size()]);
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
            final Token token = new Token(filename, lineNumber, lineColumn, 
                    value.length());
            token.setType(TokenType.STRING);
            token.setStr(value);
            tokens.add(token);
            return;
        }
        
        try {
            final int num = Integer.parseInt(value);
            final Token token = new Token(filename, lineNumber, lineColumn, 
                    value.length());
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