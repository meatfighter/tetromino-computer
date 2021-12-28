package tetriscircuits;

import tetriscircuits.Point;
import tetriscircuits.Instruction;

public class Structure {
    
    private final String name;
    
    private Instruction[] instructions;
    private Point[][] inputs;
    private Point[][] outputs;
    
    public Structure(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Instruction[] getInstructions() {
        return instructions;
    }

    public void setInstructions(final Instruction[] instructions) {
        this.instructions = instructions;
    }

    public Point[][] getInputs() {
        return inputs;
    }

    public void setInputs(final Point[][] inputs) {
        this.inputs = inputs;
    }

    public Point[][] getOutputs() {
        return outputs;
    }

    public void setOutputs(final Point[][] outputs) {
        this.outputs = outputs;
    }
}