package tetrominocomputer.util;

import java.io.File;

public interface Dirs {
    
    String USER = System.getProperty("user.dir");

    String CODE = USER + File.separator + "code" + File.separator;
    
    String ASM = CODE + "asm" + File.separator;
    String BIN = CODE + "bin" + File.separator;
    String LUTS = CODE + "luts" + File.separator;
    String MC = CODE + "mc" + File.separator;
    String TS = CODE + "ts" + File.separator;
}
