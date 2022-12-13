package tetrominocomputer.tse.app;

public interface OutputListener {
    void clear();
    void format(String text, Object... args);
}
