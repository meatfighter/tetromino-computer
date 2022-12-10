package tetrominocomputer.gpc.app;

public interface ProcessorAndMemory {
    void init(String binFilename, String cycleLeftName, String cycleRightName) throws Exception;
    void runInstruction();
    int read(int address);
    void write(int address, int value);
}
