package tetriscircuits;

public class Component {
    
    private final String name;
    
    private Instruction[] instructions;
    
    private Point[][] inputs;
    private Range[][] inputRanges;
    
    private Point[][] outputs;
    private Range[][] outputRanges;
    
    public Component(final String name) {
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

    public ComponentTest[] getTests() {
        return tests;
    }

    public void setTests(final ComponentTest[] tests) {
        this.tests = tests;
    }

    public Range[][] getInputRanges() {
        return inputRanges;
    }

    public void setInputRanges(Range[][] inputRanges) {
        this.inputRanges = inputRanges;
    }

    public Range[][] getOutputRanges() {
        return outputRanges;
    }

    public void setOutputRanges(Range[][] outputRanges) {
        this.outputRanges = outputRanges;
    }
    
    private void appendTerminals(final StringBuilder sb, final String name, final Range[][] terminals) {
        if (terminals == null) {
            return;
        }
        for (final Range[] terms : terminals) {
            sb.append("    ").append(name);
            for (final Range term : terms) {
                sb.append(' ').append(term);
            }
            sb.append(System.lineSeparator());
        }
    }
    
    @Override 
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("def ").append(name).append(System.lineSeparator());
        for (final Instruction instruction : instructions) {
            sb.append("    ").append(instruction).append(System.lineSeparator());
        }
        appendTerminals(sb, "in", inputRanges);
        appendTerminals(sb, "out", outputRanges);
        for (final ComponentTest test : tests) {
            sb.append("    ").append(test).append(System.lineSeparator());
        }
        return sb.toString();
    }
}