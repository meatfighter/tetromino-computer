package tetriscircuits.ui;

import com.bulenkov.darcula.DarculaLaf;
import java.awt.Image;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class UiUtil {
    
    private static final List<Image> ICONS = new ArrayList<>();
    
    static {
        try {
            for (int i = 16; i <= 128; i <<= 1) {
                ICONS.add(ImageIO.read(UiUtil.class.getResourceAsStream(
                        String.format("/icons/logo%dx%d.png", i, i))));
            }
        } catch (final IOException e) {            
        }
    }
    
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new DarculaLaf()); 
        } catch (final UnsupportedLookAndFeelException e) {
        }
    }

    public static void setIcons(final Window window) {
        window.setIconImages(ICONS);
    }
    
    public static void setTextFieldColumns(final JTextField textField, final int columns) {
        textField.setColumns(columns);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns; ++i) {
            sb.append('M');
        }
        textField.setText(sb.toString());
        textField.setMinimumSize(textField.getPreferredSize());
        textField.setMaximumSize(textField.getPreferredSize());
        textField.setText("");
    }

    private UiUtil() {        
    }
}
