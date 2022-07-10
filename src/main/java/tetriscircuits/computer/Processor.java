package tetriscircuits.computer;

public interface Processor {
    void init() throws Exception;
    void runInstruction();
    int readMemory(int address);
    void writeMemory(int address, int value);
}
