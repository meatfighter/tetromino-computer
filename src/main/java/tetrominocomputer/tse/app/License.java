package tetrominocomputer.tse.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class License {
    
    private static final String LICENSE;
    
    static {
        final StringBuilder sb = new StringBuilder();
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(
                License.class.getResourceAsStream("/license/lgpl-2.1.txt")))) {            
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        LICENSE = sb.toString();
    }
    
    public static String getLicense() {
        return LICENSE;
    }
    
    private License() {        
    }
}
