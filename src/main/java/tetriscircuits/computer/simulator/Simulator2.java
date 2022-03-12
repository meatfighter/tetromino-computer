package tetriscircuits.computer.simulator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import tetriscircuits.computer.Processor;

public final class Simulator2 implements Processor {
    
    private int[] bytes;
    
    private int read(final int index) {
        return bytes[index];
    }
    
    private void write(final int index, final int value) {
        bytes[index] = value;
    }
    
    private void apply(final int index, final int[] map) {
        bytes[index] = map[bytes[index]];
    }
    
    private void apply(final int index, final int[][][] map) {
        final int[] m = map[bytes[index]][bytes[index + 1]];
        bytes[index] = m[0];
        bytes[index + 1] = m[1];
    }

    private void apply(final int index, final int[][][][] map) {
        final int[] m = map[bytes[index]][bytes[index + 1]][bytes[index + 2]];
        bytes[index] = m[0];
        bytes[index + 1] = m[1];
        bytes[index + 2] = m[2];
    }

    @Override
    public int readMemory(int address) {
        //return bytes[(descend || address < 3) ? address : (address + 21)];    
        return 0; // TODO
    }

    @Override
    public void writeMemory(int address, int value) {
        //bytes[(descend || address < 3) ? address : (address + 21)] = value;
    }
        
    public Simulator2() {        
    }    
    
    @Override
    public void init() throws Exception {
        
    }

    @Override
    public void runInstruction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
