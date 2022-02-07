package tetriscircuits.assembler;

import java.util.HashSet;
import java.util.Set;

public class GenerateOperators {

    private static final String[] ALL_REGS_8 = { "A", "B", "C", "D", "I", "J", "M", "N" };
    private static final String[] GEN_REGS = { "A", "B", "C", "D" };
    
    private static final String[] ALUS_1 = { "INV", "NEG", "INC", "DEC", "USR", "SSR", "SHL" };
    private static final String[] ALUS_2 = { "ADD", "ADC", "SUB", "SBB", "AND", "OR", "XOR" };
    
    public void launch() throws Exception {

        final Set<Op> ops = new HashSet<>();
        
        for (int s = ALL_REGS_8.length - 1; s >= 0; --s) {
            for (int d = ALL_REGS_8.length - 1; d >= 0; --d) {
                if (s == d) {
                    addOp(ops, String.format("ZR%s", ALL_REGS_8[s]), (s << 3) | d);
                } else {
                    addOp(ops, String.format("T%s%s", ALL_REGS_8[s], ALL_REGS_8[d]), (s << 3) | d);
                }
            }
        }
        
        for (int d = GEN_REGS.length - 1; d >= 0; --d) {
            for (int i = ALUS_1.length - 1; i >= 0; --i) {
                addOp(ops, String.format("%s%s", ALUS_1[i], GEN_REGS[d]), 0b0100_0000 | (d << 4) | i);
            }
            for (int i = ALUS_2.length - 1; i >= 0; --i) {
                addOp(ops, String.format("%s%s", ALUS_2[i], GEN_REGS[d]), 0b0100_1000 | (d << 4) | i);
            }
        }
        
        ops.stream().sorted().forEach((op) -> {
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
    
    private static class Op implements Comparable<Op> {
        
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

        @Override
        public int compareTo(final Op o) {
            return name.compareTo(o.name);
        }
    }
}
