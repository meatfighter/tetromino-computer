package tetrominocomputer.computer.simulator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public final class GenerateMemoryCodePrograms {
    
    private static final String CYCLE_LEFT_FILENAME = "MemoryCodes/CYCLE_LEFT.tm";
    private static final String CYCLE_RIGHT_FILENAME = "MemoryCodes/CYCLE_RIGHT.tm";
              
    private int[] bytes;
    private int maxAddress;
    
    private void slideStateRegisterRight(final PrintStream out) {
        for (int address = 0; address < maxAddress; ++address) {
            fetchExecuteLoadStore(out, address);
            print(out, "SLIDE_STATE_REG_RIGHT", address);
        }
        fetchExecuteLoadStore(out, maxAddress);
    }
    
    private void slideStateRegisterLeft(final PrintStream out) {
        for (int address = maxAddress; address > 0; --address) {
            fetchExecuteLoadStore(out, address);
            print(out, "SLIDE_STATE_REG_LEFT", address);
        }
        fetchExecuteLoadStore(out, 0x0000);
    }    
    
    private void fetchExecuteLoadStore(final PrintStream out, final int address) {
       print(out, "FETCH", address);
       print(out, "EXECUTE_LOAD_STORE", address);
    }
        
    private void executeInstruction(final PrintStream out, final int address) {
        print(out, "ASSIGN_LOADED", address);
        print(out, "INCREMENT_P", address);
        print(out, "DECODE_EXECUTE_TRANSFER", address);
        print(out, "DECODE_EXECUTE_ARITHMETIC_LOGIC", address);
        print(out, "DECODE_EXECUTE_SET", address);
        print(out, "DECODE_EXECUTE_BRANCH", address);
        print(out, "DECODE_LOAD_STORE", address);
    }
    
    private void print(final PrintStream out, final String name, final int index) {
        out.format("%s %d%n", name, index);
    }
       
    public void loadBinFile(final String binFilename) throws IOException {
        final File binFile = new File(binFilename);
        maxAddress = (int)binFile.length() - 3; 
        
        bytes = new int[maxAddress + 24]; // machine code + 2 padding bytes + 21-byte state register
        
        try (final InputStream in = new BufferedInputStream(new FileInputStream(binFilename))){
            for (int address = 0; address <= maxAddress; ++address) {                
                final int b = in.read();
                if (b < 0) {
                    throw new IOException("Unexpected end of file.");
                }
                bytes[address] = b;
            }
            bytes[maxAddress + 9] = in.read();         // P = main;
            bytes[maxAddress + 10] = in.read();
            if (bytes[maxAddress + 9] < 0 || bytes[maxAddress + 10] < 0) {
                throw new IOException("Unexpected end of file.");
            }
        }
        
        bytes[maxAddress + 13] = maxAddress >> 8;      // a = L-1;
        bytes[maxAddress + 14] = 0xFF & maxAddress;
    }    
    
    private void writeInputData() throws IOException {
        try (final OutputStream out = new BufferedOutputStream(new FileOutputStream("data/inputData.dat"))) {
            for (int i = 0; i < bytes.length; ++i) {
                out.write(bytes[i]);
            }
        }
    }
    
    public void launch() throws Exception {
        loadBinFile("asm/tetris.bin");
        writeInputData();

        try (final PrintStream out = new PrintStream(CYCLE_LEFT_FILENAME)) {
            slideStateRegisterLeft(out);
            executeInstruction(out, 0x0000);   
        }
        
        try (final PrintStream out = new PrintStream(CYCLE_RIGHT_FILENAME)) {
            slideStateRegisterRight(out);
            executeInstruction(out, maxAddress);    
        }
    }
    
    public static void main(final String... args) throws Exception {
        new GenerateMemoryCodePrograms().launch();
    }    
}