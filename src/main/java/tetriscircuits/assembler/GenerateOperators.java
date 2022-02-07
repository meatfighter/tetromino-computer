package tetriscircuits.assembler;

import java.util.HashSet;
import java.util.Set;

public class GenerateOperators {

    private static final String[] REGS_8 = { "A", "B", "C", "D", "I", "J", "M", "N" };
    
    public void launch() throws Exception {

        final Set<Op> ops = new HashSet<>();
        for (int s = REGS_8.length - 1; s >= 0; --s) {
            for (int d = REGS_8.length - 1; d >= 0; --d) {
                System.out.format("%08b%n", (s << 3) | d);
            }
        }
    }
    
    public void addOp(final Set<Op> ops, final String name) {
        addOp(ops, name, 1);
    }
    
    public void addOp(final Set<Op> ops, final String name, final int length) throws IllegalArgumentException {
        final Op op = new Op(name, length);
        if (ops.contains(op)) {
            throw new IllegalArgumentException("Duplicate: " + name);
        }
        ops.add(op);
    }
    
    public static void main(final String... args) throws Exception {
        new GenerateOperators().launch();
    }
    
    private static class Op {
        
        private final String name;
        private final int length;
        
        public Op(final String name) {
            this(name, 1);
        }
        
        public Op(final String name, final int length) {
            this.name = name;
            this.length = length;
        }

        public String getName() {
            return name;
        }

        public int getLength() {
            return length;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return name.equals(((Op)obj).name);
        }
        
        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}
