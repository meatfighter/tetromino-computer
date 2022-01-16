package tetriscircuits.ui;

import java.awt.Image;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public final class WindowUtil {
    
    private static final List<Image> ICONS = new ArrayList<>();
    
    static {
        try {
            for (int i = 16; i <= 128; i <<= 1) {
                ICONS.add(ImageIO.read(WindowUtil.class.getResourceAsStream(
                        String.format("/icons/logo%dx%d.png", i, i))));
            }
        } catch (final IOException e) {            
        }
    }

    public static void standardize(Window window) {
        window.setIconImages(ICONS);
    }

    private WindowUtil() {        
    }
}
