package tetriscircuits;

public interface BuildListener {
    void buildStarted();
    void buildCompleted(String[] componentNames);
}
