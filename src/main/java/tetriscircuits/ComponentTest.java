package tetriscircuits;

public class ComponentTest {

    private final boolean[] inputs;
    private final boolean[] outputs;

    public ComponentTest(final boolean[] inputs, final boolean[] outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public boolean[] getInputs() {
        return inputs;
    }

    public boolean[] getOutputs() {
        return outputs;
    }
    
    private void appendBits(final StringBuilder sb, final boolean[] bits) {
        for (final boolean bit : bits) {
            sb.append(bit ? '1' : '0');
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("test ");
        appendBits(sb, inputs);
        sb.append(' ');
        appendBits(sb, outputs);
        return sb.toString();
    }
}