package tetrominocomputer.tse.app;

import java.io.File;

public interface OpenListener {
    void openedFiles(String componentName, File tetrominoScriptFile, String tetrominoScript, File javaScriptFile, 
            String javaScript, String testBits);
}
