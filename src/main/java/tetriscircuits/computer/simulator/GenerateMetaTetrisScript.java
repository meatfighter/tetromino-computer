package tetriscircuits.computer.simulator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import tetriscircuits.computer.mapping.ByteMapping;

public final class GenerateMetaTetrisScript {
       
    private static String toString(final Object obj) {
        for (final Object[] name : NAMES) {
            if (obj == name[0]) {
                return name[1].toString();
            }
        }
        throw new RuntimeException("Unknown object.");
    }
        
    private int[] bytes;
    private int maxAddress;
    private boolean descend = true;
    
    private final List<NameAndIndex> NAME_AND_INDICES = new ArrayList<>();
    
    private void ascendMemoryCycle() {
        for (int address = 0; address < maxAddress; ++address) {
            runMemoryCycle(address);
            ascend(address);
        }
        runMemoryCycle(maxAddress);
    }
    
    private void descendMemoryCycle() {
        for (int address = maxAddress; address > 0; --address) {
            runMemoryCycle(address);
            descend(address);
        }
        runMemoryCycle(0x0000);
    }    
    
    private void runMemoryCycle(final int address) {
       copyInstructionIfPEqualsA(address);
       readOrWriteMemoryIfMNEqualsA(address);
    }
        
    private void executeInstruction(final int address) {
        finishLoad(address);
        transfer(address);
        runALU(address);
        setAndJump(address);
        loadAndStore(address);
    }
       
    private void apply(final int index, final int[] map) {
        NAME_AND_INDICES.add(new NameAndIndex(toString(map), index));
    }
    
    private void apply(final int index, final int[][][] map) {
        NAME_AND_INDICES.add(new NameAndIndex(toString(map), index));
    }

    private void apply(final int index, final int[][][][] map) {
        NAME_AND_INDICES.add(new NameAndIndex(toString(map), index));
    }
      
    public void loadBinFile(final String binFilename) throws IOException {
        final File binFile = new File(binFilename);
        maxAddress = (int)binFile.length() - 3; 
        
        bytes = new int[maxAddress + 24];
        
        try (final InputStream in = new BufferedInputStream(new FileInputStream(binFilename))){
            for (int address = 0; address <= maxAddress; ++address) {                
                final int b = in.read();
                if (b < 0) {
                    throw new IOException("Unexpected end of file.");
                }
                bytes[address] = b;
            }
            bytes[maxAddress + 9] = in.read();
            bytes[maxAddress + 10] = in.read();
            if (bytes[maxAddress + 9] < 0 || bytes[maxAddress + 10] < 0) {
                throw new IOException("Unexpected end of file.");
            }
        }
        
        bytes[maxAddress + 13] = maxAddress >> 8;
        bytes[maxAddress + 14] = 0xFF & maxAddress;
    }    
    
    private void writeMaps() throws IOException {
        for (final Object[] name : NAMES) {
            final String filename = String.format("maps/%s.map", name[1]);
            try (final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename))) {
                if (name[0].getClass() == int[].class) {
                    new ByteMapping((int[])name[0]).write(out);
                } else if (name[0].getClass() == int[][][].class) {
                    new ByteMapping((int[][][])name[0]).write(out);
                } else {
                    new ByteMapping((int[][][][])name[0]).write(out);
                }
            } catch (final IOException e) {
                throw new IOException("Failed to write: " + filename, e);
            }
        }
    }
    
    private void writeInputData() throws IOException {
        try (final OutputStream out = new BufferedOutputStream(new FileOutputStream("data/inputData.dat"))) {
            for (int i = 0; i < bytes.length; ++i) {
                out.write(bytes[i]);
            }
        }
    }
    
    public void runInstruction() {
        if (descend) {
            descend = false;
            descendMemoryCycle();
            executeInstruction(0x0000);
        } else {
            descend = true;
            ascendMemoryCycle();
            executeInstruction(maxAddress);            
        }        
    }

    public void launch() throws Exception {
        loadBinFile("asm/tetris.bin");
        writeInputData();
        writeMaps();
        
        NAME_AND_INDICES.clear();
        descendMemoryCycle();
        executeInstruction(0x0000);

        try (final PrintStream out = new PrintStream(new FileOutputStream("executables/tetris-descend.tx"))) {
            for (final NameAndIndex nameAndIndex : NAME_AND_INDICES) {
                out.println(nameAndIndex);
            }
        }
        
        NAME_AND_INDICES.clear();
        ascendMemoryCycle();
        executeInstruction(maxAddress);
        
        try (final PrintStream out = new PrintStream(new FileOutputStream("executables/tetris-ascend.tx"))) {
            for (final NameAndIndex nameAndIndex : NAME_AND_INDICES) {
                out.println(nameAndIndex);
            }
        }        
    }
    
    public static void main(final String... args) throws Exception {
        new GenerateMetaTetrisScript().launch();
    }    
    
    private static class NameAndIndex {
    
        private final String name;
        private final int index;
        
        public NameAndIndex(final String name, final int index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }
        
        @Override
        public String toString() {
            return String.format("%s %d", name, index);
        }
    }    
}