package tetrominocomputer.computer.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

// Converts GenerateTetrisExecutable.java to an all-in-one MemoryCode (prints output)

//         apply(address + 16, ADD_AB_FB);     // s0 += B;

public class CreateAllMemoryCodeProgram {
    
    private static final String SRC_DIR = "src";
    private static final String SOURCE_FILE = "GenerateExecutable.java";
    
    private static final int COMMENT_COLUMN = 25;
    
    private static final Pattern APPLY_PATTERN = Pattern.compile(
            "^\\s+apply\\s*\\(\\s*address\\s*\\+\\s*(\\d+)\\s*,\\s*(\\w+)\\s*\\)\\s*;\\s*(//.*)?$");
    
    public void launch() throws Exception {
        try (final BufferedReader br = new BufferedReader(new FileReader(locateSourceFile()))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                processLine(line);
            }
        }
    }
    
    private void processLine(final String line) {
        final Matcher matcher = APPLY_PATTERN.matcher(line);
        if (matcher.matches()) {
            final String instruction = String.format("%s %s", matcher.group(2), matcher.group(1));
            final String comment = matcher.group(3);            
            if (isNotBlank(comment)) {
                final StringBuilder sb = new StringBuilder(instruction);
                while (sb.length() < COMMENT_COLUMN - 1) {
                    sb.append(' ');
                }
                sb.append(';');
                sb.append(comment.substring(2));
                System.out.println(sb);
            } else {
                System.out.println(instruction);
            }
        }
    }
    
    private File locateSourceFile() {
        return locateSourceFile(new File(SRC_DIR));
    }
    
    private File locateSourceFile(final File file) {
        if (file == null) {
            return null;
        }
        if (file.isFile() && SOURCE_FILE.equals(file.getName())) {
            return file;
        }
        if (file.isDirectory()) {
            for (final File f : file.listFiles()) {
                final File result = locateSourceFile(f);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
    
    public static void main(final String... args) throws Exception {
        new CreateAllMemoryCodeProgram().launch();
    }
}
