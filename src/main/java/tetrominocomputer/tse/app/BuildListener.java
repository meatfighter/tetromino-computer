package tetrominocomputer.tse.app;

import tetrominocomputer.sim.Structure;
import java.util.Map;

public interface BuildListener {
    void buildStarted();
    void buildCompleted(String[] componentNames, Map<String, Structure> structures);
}
