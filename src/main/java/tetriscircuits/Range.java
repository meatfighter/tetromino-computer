package tetriscircuits;

public class Range {
    
    private final int min;
    private final int max;
    
    public Range(final int value) {
        this(value, value);
    }

    public Range(final int min, final int max) {
        if (min > max) {
            this.min = max;
            this.max = min;
        } else {
            this.min = min;
            this.max = max;
        }
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public String toString() {
        return (min == max) ? Integer.toString(min) : String.format("%d..%d", min, max);
    } 
}