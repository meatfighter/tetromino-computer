package tetrominocomputer.mc;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import tetrominocomputer.util.Dirs;
import tetrominocomputer.util.Out;

public final class CycleProgramsGenerator {
    
    private static final String DEFAULT_BIN_FILENAME = Dirs.BIN + "example.bin";
    private static final String DEFAULT_CYCLE_LEFT_PROGRAM_FILENAME = Dirs.MC + "CYCLE_LEFT.mc";
    private static final String DEFAULT_CYCLE_RIGHT_PROGRAM_FILENAME = Dirs.MC + "CYCLE_RIGHT.mc";    
    
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
        
        Out.timeTask("Generating cycle programs...", () -> {
            
            final int maxAddress = readMaxAddress(binFilename);

            try (final PrintStream out = new PrintStream(cycleLeftFilename)) {
                slideStateRegisterLeft(out, maxAddress);
                executeInstruction(out, 0x0000);   
            }

            try (final PrintStream out = new PrintStream(cycleRightfilename)) {
                slideStateRegisterRight(out, maxAddress);
                executeInstruction(out, maxAddress);    
            }
            
            System.out.println("Generated cycle programs.");
            System.out.println();
            
            return null;
        });
    }
    
    public static void main(final String... args) throws Exception {
        
        if (args.length == 2 || args.length > 3) {
            System.out.println("args: [[ bin filename ]] [[ cycle left program filename ]] "
                    + "[[ cycle right program filename ]]");
            return;
        } 
        
        final String binFilename = (args.length == 0) ? DEFAULT_BIN_FILENAME : args[0];
        
        final String cycleLeftProgramFilename;
        final String cycleRightProgramFilename;                
        if (args.length != 3) {            
            cycleLeftProgramFilename = DEFAULT_CYCLE_LEFT_PROGRAM_FILENAME;
            cycleRightProgramFilename = DEFAULT_CYCLE_RIGHT_PROGRAM_FILENAME;
        } else {
            cycleLeftProgramFilename = args[1];
            cycleRightProgramFilename = args[2];
        }        
        
        new CycleProgramsGenerator().launch(binFilename, cycleLeftProgramFilename, cycleRightProgramFilename);
    }    
}