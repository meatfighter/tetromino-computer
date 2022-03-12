package tetriscircuits.computer.parser;

public interface RunnableSource {
    Runnable createRunnable(String componentName, int index);
}
