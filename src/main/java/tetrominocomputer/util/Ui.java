package tetrominocomputer.util;

import com.bulenkov.darcula.DarculaLaf;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import tetrominocomputer.tse.ui.CircuitsFrame;

import static javax.swing.JOptionPane.CLOSED_OPTION;
import javax.swing.JTextArea;

public final class Ui {
    
    public static final Icon ERROR_ICON = new ImageIcon(CircuitsFrame.class.getResource("/icons/error.png"));
    public static final Icon INFORMATION_ICON = new ImageIcon(CircuitsFrame.class
            .getResource("/icons/information.png"));
    public static final Icon WARNING_ICON = new ImageIcon(CircuitsFrame.class.getResource("/icons/warning.png"));
    public static final Icon QUESTION_ICON = new ImageIcon(CircuitsFrame.class.getResource("/icons/question.png"));
    
    private static final List<Image> LOGOS = new ArrayList<>();
    
    private static float fontSizeMultiplier = 1f;
    
    static {
        try {
            for (int i = 16; i <= 128; i <<= 1) {
                LOGOS.add(ImageIO.read(Ui.class.getResourceAsStream(
                        String.format("/icons/logo%dx%d.png", i, i))));
            }
        } catch (final IOException e) {            
        }
    }
    
    public static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new DarculaLaf()); 
        } catch (final UnsupportedLookAndFeelException e) {
        }
    }
    
    public static void initFontSize(final float fontSizeMultiplier) {  

        if (fontSizeMultiplier == 1f) {
            return;
        }
        
        Ui.fontSizeMultiplier = fontSizeMultiplier;
        
        final UIDefaults defaults = UIManager.getDefaults();
        final Set<Object> keys = new HashSet<>();
        for (final Enumeration<Object> e = defaults.keys(); e.hasMoreElements(); ) {
            keys.add(e.nextElement());
        }
        keys.forEach(key -> {
            final Object value = defaults.get(key);
            if (value instanceof Font) {
                final Font font = (Font) value;
                final int size = Math.round(font.getSize() * fontSizeMultiplier);
                if (value instanceof FontUIResource) {
                    defaults.put(key, new FontUIResource(font.getName(), font.getStyle(), size));
                } else {
                    defaults.put(key, new Font(font.getName(), font.getStyle(), size));
                }
            }
        });
    }
    
    public static void setMonospaced(final Component component) {
        setFont(component, Font.MONOSPACED, Font.PLAIN, 13);
    }
    
    public static void setMonospaced(final Component component, final int style) {
        setFont(component, Font.MONOSPACED, style, 13);
    }
    
    public static void setFont(Component component, final String name, final int style, final int size) {
        component.setFont(new Font(name, style, Math.round(fontSizeMultiplier * size)));
    }

    public static void initIcons(final Window window) {
        window.setIconImages(LOGOS);
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
    
    public static void resetTextFieldColumns(final JTextField textField) {
        textField.setMinimumSize(null);
        textField.setMaximumSize(null);
    }
    
    public static void setTextAreaRows(final JTextArea textArea, final int rows) {
        textArea.setRows(Math.round(rows / fontSizeMultiplier));
    }

    public static void showMessageDialog(final Component parentComponent, final Object message, final String title, 
            final int messageType) {
        
        final Icon icon;
        switch (messageType) {
            case JOptionPane.ERROR_MESSAGE:
                icon = ERROR_ICON;
                break;
            case JOptionPane.INFORMATION_MESSAGE:
                icon = INFORMATION_ICON;
                break;
            case JOptionPane.WARNING_MESSAGE:
                icon = WARNING_ICON;
                break;
            case JOptionPane.QUESTION_MESSAGE:
                icon = QUESTION_ICON;
                break;   
            default:
                icon = null;
                break;
        }

        final JOptionPane optionPane = new JOptionPane(message, messageType, JOptionPane.DEFAULT_OPTION, icon);  
        final JDialog dialog = optionPane.createDialog(parentComponent, title);
        initIcons(dialog);
        dialog.setResizable(true);
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        dialog.setVisible(true);
        dialog.dispose();
    }
    
    public static int showConfirmDialog(final Component parentComponent, final Object message, final String title, 
            final int optionType) {
        
        final JOptionPane optionPane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, optionType, 
                QUESTION_ICON);  
        final JDialog dialog = optionPane.createDialog(parentComponent, title);
        initIcons(dialog);
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
    
    private Ui() {        
    }
}
