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
    
    private void appendTerminals(final StringBuilder sb, final String name, final Point[][] terminals) {
        if (terminals == null) {
            return;
        }
        for (final Point[] terms : terminals) {
            sb.append("    ").append(name);
            for (final Point term : terms) {
                sb.append(' ').append(term);
            }
            sb.append(System.lineSeparator());
        }
    }
    
    @Override 
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("struct ").append(name).append(System.lineSeparator());
        for (final Instruction instruction : instructions) {
            sb.append("    ").append(instruction).append(System.lineSeparator());
        }
        appendTerminals(sb, "in", inputs);
        appendTerminals(sb, "out", outputs);
        return sb.toString();
    }
}