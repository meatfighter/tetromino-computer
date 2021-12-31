package tetriscircuits;

public class Simulator {
    
    public void simulate(final Playfield playfield, final Component component, final int cellX, final int cellY) 
            throws SimulatorException {
        simulate(playfield, component, cellX, cellY, null);
    }

    public void simulate(final Playfield playfield, final Component component, final int cellX, final int cellY,
            final TetriminoLockListener listener) throws SimulatorException {
        
        final Instruction[] instructions = component.getInstructions();
        for (int i = 0; i < instructions.length; ++i) {
            final Instruction instruction = instructions[i];            
            final int[] moves = instruction.getMoves();
        }
    }
}
