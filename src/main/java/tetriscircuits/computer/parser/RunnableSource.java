package tetriscircuits.computer.parser;

import java.io.IOException;

public interface RunnableSource {
    Runnable createRunnable(String componentName, int index) throws IOException;
}
