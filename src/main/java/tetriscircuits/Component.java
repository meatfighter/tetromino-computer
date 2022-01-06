package tetriscircuits;

public class Component {
    
    private final String name;
    
    private Instruction[] instructions;
    private Border border;
    private Terminal[] inputs;
    private Terminal[] outputs;
    
    public Component(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Border getBorder() {
        return border;
    }

    public void setBorder(final Border border) {
        this.border = border;
    }

    public Instruction[] getInstructions() {
        return instructions;
    }

    public void setInstructions(final Instruction[] instructions) {
        this.instructions = instructions;
    }

    public Terminal[] getInputs() {
        return inputs;
    }

    public void setInputs(final Terminal[] inputs) {
        this.inputs = inputs;
    }

    public Terminal[] getOutputs() {
        return outputs;
    }

    public void setOutputs(final Terminal[] outputs) {
        this.outputs = outputs;
    }

    @Override 
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Instruction instruction : instructions) {
            sb.append(instruction).append(System.lineSeparator());
        }
        sb.append(border).append(System.lineSeparator());
        for (final Terminal terminal : inputs) {
            sb.append(terminal).append(System.lineSeparator());
        }
        for (final Terminal terminal : outputs) {
            sb.append(terminal).append(System.lineSeparator());
        }
        return sb.toString();
    }
}