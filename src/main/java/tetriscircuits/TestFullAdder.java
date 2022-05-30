package tetriscircuits;

// TODO REMOVE THIS CLASS

public class TestFullAdder {


    public static void main(final String... args) {
        
        for (int i = 0; i <= 1; ++i) {
            for (int j = 0; j <= 1; ++j) {
                for (int k = 0; k <= 1; ++k) {                   
                    System.out.format("%d%d%d %d%d%d%n", i, j, k, 
                            (i & j) | (k & (i ^ j)), 
                            
                            
                            ~( ~(i | j) | ~(k | ~(i ^ j)) ),
                            
                            
                            (i & j) | (k & (i | j))
                            );
                }
            }
        }
    }
}
