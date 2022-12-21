package tetrominocomputer.mc;

import java.io.File;
import java.io.PrintStream;
import tetrominocomputer.util.Dirs;
import tetrominocomputer.util.Out;

public final class CycleProgramsGenerator {
    
    private static final String DEFAULT_BIN_FILENAME = "example.bin";
    private static final String DEFAULT_CYCLE_LEFT_PROGRAM_FILENAME = "CYCLE_LEFT.mc";
    private static final String DEFAULT_CYCLE_RIGHT_PROGRAM_FILENAME = "CYCLE_RIGHT.mc";    
    
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
       
    public void launch(final int maxAddress, final String cycleLeftFilename, final String cycleRightfilename) 
            throws Exception {
        
        Out.timeTask("Generating cycle programs...", () -> {

            try (final PrintStream out = new PrintStream(Dirs.MC + cycleLeftFilename)) {
                slideStateRegisterLeft(out, maxAddress);
                executeInstruction(out, 0x0000);   
            }

            try (final PrintStream out = new PrintStream(Dirs.MC + cycleRightfilename)) {
                slideStateRegisterRight(out, maxAddress);
                executeInstruction(out, maxAddress);    
            }
            
            Out.format("Generated cycle programs.%n%n");
            
            return null;
        });
    }
    
    public static void main(final String... args) throws Exception {
                
        String binFilename = DEFAULT_BIN_FILENAME;
        String cycleLeftProgramFilename = DEFAULT_CYCLE_LEFT_PROGRAM_FILENAME;
        String cycleRightProgramFilename = DEFAULT_CYCLE_RIGHT_PROGRAM_FILENAME;

        for (int i = 0; i < args.length - 1; ++i) {
            switch (args[i]) {
                case "-b":
                    binFilename = args[++i];
                    break;
                case "-l":
                    cycleLeftProgramFilename = args[++i];
                    break;
                case "-r":
                    cycleRightProgramFilename = args[++i];
                    break;
            }            
        }
        
        final File binFile = new File(Dirs.BIN + binFilename);
        if (!(binFile.exists() && binFile.isFile())) {
            Out.formatError("Binary file not found: %s%n", binFile);
            return;
        }
        final int maxAddress = ((int) binFile.length()) - 3;
        if (maxAddress < 0) {
            Out.printlnError("Invalid binary file.");
            return;
        }
        
        new CycleProgramsGenerator().launch(maxAddress, cycleLeftProgramFilename, cycleRightProgramFilename);
    }    
}