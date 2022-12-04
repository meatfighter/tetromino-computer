package tetrominocomputer.ui;

import com.bulenkov.darcula.DarculaLaf;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.CLOSED_OPTION;
import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.getRootFrame;
import javax.swing.JRootPane;
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

    public static void showMessageDialog(final Component parentComponent, final Object message, final String title, 
            final int messageType, final Icon icon) {

        final JOptionPane optionPane = new JOptionPane(message, messageType, JOptionPane.DEFAULT_OPTION, icon);  
        final JDialog dialog = optionPane.createDialog(parentComponent, title);
        setIcons(dialog);
        dialog.setResizable(true);
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        dialog.setVisible(true);
        dialog.dispose();
    }
    
    public static int showConfirmDialog(final Component parentComponent, final Object message, final String title, 
            final int optionType, final int messageType, final Icon icon) {
        
        final JOptionPane optionPane = new JOptionPane(message, messageType, optionType, icon);  
        final JDialog dialog = optionPane.createDialog(parentComponent, title);
        setIcons(dialog);
        dialog.setResizable(true); 
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        dialog.setVisible(true);
        dialog.dispose();
        
        final Object selectedValue = optionPane.getValue();      
        if (selectedValue instanceof Integer) {
            return (int) selectedValue;
        }
        return CLOSED_OPTION;
    }

    private UiUtil() {        
    }
}
