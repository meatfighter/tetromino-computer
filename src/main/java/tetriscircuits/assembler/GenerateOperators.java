package tetriscircuits.assembler;

import java.util.HashSet;
import java.util.Set;

public class GenerateOperators {

    private static final String[] REGS_8 = { "A", "B", "C", "D", "I", "J", "M", "N" };
    
    public void launch() throws Exception {

        final Set<Op> ops = new HashSet<>();
        for (int s = REGS_8.length - 1; s >= 0; --s) {
            for (int d = REGS_8.length - 1; d >= 0; --d) {
                if (s == d) {
                    addOp(ops, String.format("ZR%s", REGS_8[s]), (s << 3) | d);
                } else {
                    addOp(ops, String.format("T%s%s", REGS_8[s], REGS_8[d]), (s << 3) | d);
                }
            }
        }
        
        ops.forEach((op) -> {
            System.out.println(op);
        });
    }
    
    private void addOp(final Set<Op> ops, final String name) {
        addOp(ops, name, 1);
    }
    
    private void addOp(final Set<Op> ops, final String name, final int length) throws IllegalArgumentException {
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
        private final int opcode;
        private final int length;
        
        public Op(final String name, final int opcode) {
            this(name, opcode, 1);
        }
        
        public Op(final String name, final int opcode, final int length) {
            this.name = name;
            this.opcode = opcode;
            this.length = length;
        }

        public String getName() {
            return name;
        }

        public int getLength() {
            return length;
        }
        
        public int getOpcode() {
            return opcode;
        }
        
        private String formatBin(final int value) {
            return String.format("0b%8s", Integer.toBinaryString(value)).replace(' ', '0');
        }        
        
        @Override
        public String toString() {
            return String.format("%s(%s%s),", name, formatBin(opcode), length == 1 ? "" : ", " + length);
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
