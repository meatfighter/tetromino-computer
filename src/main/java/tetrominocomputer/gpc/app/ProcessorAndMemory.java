package tetrominocomputer.gpc.app;

public interface ProcessorAndMemory {
    void init(String[] args) throws Exception;
    void executeInstruction();
    int read(int address);
    void write(int address, int value);
}
