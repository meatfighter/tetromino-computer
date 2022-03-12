package tetriscircuits.computer;

import java.io.IOException;

public interface Processor {
    void init() throws Exception;
    void runInstruction();
    int readMemory(int address);
    void writeMemory(int address, int value);
}
