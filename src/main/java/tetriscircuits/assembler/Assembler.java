package tetriscircuits.assembler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assembler {
    
    public void launch(final String asmFilename, final String binFilename) throws Exception {
                
    }
    
    private int[] assemble(final InputStream in) throws IOException {
        final Map<String, Integer> labels = new HashMap<>();
        final List<Integer> binary = new ArrayList<>();
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = br.readLine()) != null) {                
                assembleLine(binary, line);
            }
        }
        final int[] bin = new int[binary.size()];
        for (int i = bin.length - 1; i >= 0; --i) {
            bin[i] = binary.get(i);
        }
        return bin;
    }
    
    private void assembleLine(final List<Integer> binary, final String line) {
        String text = line;
        final int semicolon = text.indexOf(';');
        if (semicolon >= 0) {
            text = text.substring(0, semicolon);
        }
        text = text.trim().toUpperCase();
        if (text.isEmpty()) {
            return;
        }
        final String[] tokens = text.split("\\s+");
        switch (tokens[0]) {
            case "ADC":
                addByte(binary, 0b0100_1000);
                break;
            case "AND":
                addByte(binary, 0b0100_1001);
                break;
            case "ASL":
                addByte(binary, 0b0100_0100);
                break;
                
//1000effv aaaaaaaa aaaaaaaa
//
//ff:
//00 - none
//01 - c == v
//10 - z == v
//11 - n == v                
                
            case "BCC":
                addByte(binary, 0b1000_0010);
                addAddress(binary, tokens[1]);
                break;
        }
    }
    
    private void addByte(final List<Integer> binary, final int value) {
        if (binary.size() >= 0x10000) {
            handleError("asm file too large.");
        }
        binary.add(value);
    }
    
    private void handleError(final String message) {
        System.out.format("Error: %s%n", message);
        System.exit(0);
    }
    
    public static void main(final String... args) throws Exception {
        
        if (args.length != 2) {
            System.out.println("args: [ asm filename ] [ bin filename ]");
            return;
        }
        
        new Assembler().launch(args[0], args[1]);
    }
}
