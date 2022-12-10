package tetrominocomputer.mc;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public final class GenerateCyclePrograms {
    
    private void slideStateRegisterRight(final PrintStream out, final int maxAddress) {
        for (int address = 0; address < maxAddress; ++address) {
            fetchExecuteLoadStore(out, address);
            print(out, "SLIDE_STATE_REG_RIGHT", address);
        }
        fetchExecuteLoadStore(out, maxAddress);
    }
    
    private void slideStateRegisterLeft(final PrintStream out, final int maxAddress) {
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
       
    public int readMaxAddress(final String binFilename) throws IOException {
        return ((int) new File(binFilename).length()) - 3; 
    }    
       
    public void launch(final String binFilename, final String cycleLeftFilename, final String cycleRightfilename) 
            throws Exception {
        
        final int maxAddress = readMaxAddress(binFilename);

        try (final PrintStream out = new PrintStream(cycleLeftFilename)) {
            slideStateRegisterLeft(out, maxAddress);
            executeInstruction(out, 0x0000);   
        }
        
        try (final PrintStream out = new PrintStream(cycleRightfilename)) {
            slideStateRegisterRight(out, maxAddress);
            executeInstruction(out, maxAddress);    
        }
    }
    
    public static void main(final String... args) throws Exception {
        
        if (args.length != 3) {
            System.out.println("args: [ bin filename ] [ cycle left filename ] [ cycle right filename ]");
            return;
        }
        
        new GenerateCyclePrograms().launch(args[0], args[1], args[2]);
    }    
}