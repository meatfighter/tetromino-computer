package tetriscircuits.assembler;

public class TestRandomizer {

    public void launch() {
        for (int i = 0; i < 32; ++i) {            
            int v = i;
            v <<= 3;
            v &= 0xFF;
            v -= i;
            v &= 0xFF;
            v >>= 5;
            v &= 0xFF;
            System.out.format("%d: %d%n", i, v);
        }
    }
    
    public static void main(final String... args) throws Exception {
        new TestRandomizer().launch();
    }
}
