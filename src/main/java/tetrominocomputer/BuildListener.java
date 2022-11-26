package tetrominocomputer;

import java.util.Map;

public interface BuildListener {
    void buildStarted();
    void buildCompleted(String[] componentNames, Map<String, Structure> structures);
}
