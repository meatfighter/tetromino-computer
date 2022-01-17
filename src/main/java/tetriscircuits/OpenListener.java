package tetriscircuits;

import java.io.File;

public interface OpenListener {
    void openedFile(String componentName, File file, String text);
}
