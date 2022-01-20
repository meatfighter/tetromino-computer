package tetriscircuits;

import java.io.File;

public interface OpenListener {
    void openedFiles(String componentName, File tetrisScriptFile, String tetrisScript, File javaScriptFile, 
            String javaScript, String testBits);
}
