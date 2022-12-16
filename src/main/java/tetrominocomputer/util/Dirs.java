package tetrominocomputer.util;

import java.io.File;
import org.apache.commons.lang3.StringUtils;

public interface Dirs {
    
    String USER = System.getProperty("user.dir");

    String CODE = USER + File.separator + "code" + File.separator;
    
    String ASM = CODE + "asm" + File.separator;
    String BIN = CODE + "bin" + File.separator;
    String LUTS = CODE + "luts" + File.separator;
    String MC = CODE + "mc" + File.separator;
    String TS = CODE + "ts" + File.separator;
    
    String WEB = USER + File.separator + "web" + File.separator;
    
    static File toFile(final String dirName) {
        String name = StringUtils.trimToEmpty(dirName);
        if (!(name.endsWith("/") || name.endsWith("\\") || name.endsWith(File.separator))) {
            name += File.separator;
        }
        return new File(name);
    }
}
