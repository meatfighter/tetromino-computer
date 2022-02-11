package tetriscircuits.assembler;

public class GeneratePlayfieldScreen {

    public void launch() {
        for (int y = 0; y < 32; ++y) {
            for (int x = 0; x < 32; ++x) {
                if (x != 0) {
                    System.out.print(' ');
                }
                if (x >= 12 && x <= 21 && y >= 6 && y <= 25) {
                    System.out.format("00");
                } else {
                    System.out.format("02");
                }
            }
            System.out.println();
        }
    }    

    public static void main(final String... args) {
        new GeneratePlayfieldScreen().launch();
    }
}
