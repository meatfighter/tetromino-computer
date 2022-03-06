package tetriscircuits.computer;

import java.io.IOException;

public interface Processor {
    void loadBinFile(String binFilename) throws IOException;
    void runInstruction();
    int readMemory(int address);
    void writeMemory(int address, int value);
}
