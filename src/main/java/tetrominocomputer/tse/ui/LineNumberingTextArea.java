package tetrominocomputer.tse.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import tetrominocomputer.util.Ui;

// https://stackoverflow.com/questions/18768649/java-code-to-display-lines-number-in-jtextarea
public class LineNumberingTextArea extends JTextArea {

    private static final Color FOREGROUND = new Color(0x888888);    
    private static final Color BACKGROUND = new Color(0x313335);
    
    private final JTextComponent textComponent;
    
    private int lastMaxLineNumber;
    
    private static int getDigits(final int number) {
        if (number < 100000) {
            if (number < 100) {
                if (number < 10) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                if (number < 1000) {
                    return 3;
                } else {
                    if (number < 10000) {
                        return 4;
                    } else {
                        return 5;
                    }
                }
            }
        } else {
            if (number < 10000000) {
                if (number < 1000000) {
                    return 6;
                } else {
                    return 7;
                }
            } else {
                if (number < 100000000) {
                    return 8;
                } else {
                    if (number < 1000000000) {
                        return 9;
                    } else {
                        return 10;
                    }
                }
            }
        }
    }
    
    public LineNumberingTextArea(final JTextComponent textComponent) {
        this.textComponent = textComponent;
        textComponent.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) { 
                updateLineNumbers();
            }
            @Override
            public void removeUpdate(final DocumentEvent e) {
                updateLineNumbers();
            }
            @Override
            public void changedUpdate(final DocumentEvent e) {
                updateLineNumbers();
            }
        });
        setForeground(FOREGROUND);
        setBackground(BACKGROUND);    
        setSelectedTextColor(FOREGROUND);
        setSelectionColor(BACKGROUND);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, FOREGROUND), 
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));                
        Ui.setMonospaced(this);
        setEditable(false);        
    }    
    
    public void updateLineNumbers() { 
        final Document doc = textComponent.getDocument();
        final int maxLineNumber = doc.getDefaultRootElement().getElementIndex(doc.getLength()) + 1;
        
        if (maxLineNumber == lastMaxLineNumber) {
            return;
        }
        lastMaxLineNumber = maxLineNumber;
        
        final int maxDigits = getDigits(maxLineNumber);
                
        final StringBuilder sb = new StringBuilder();
        for (int i = maxDigits - 2; i >= 0; --i) {
            sb.append(' ');
        }
        sb.append("1").append(System.lineSeparator());        
        for (int lineNumber = 2; lineNumber <= maxLineNumber; ++lineNumber) {
            for (int i = maxDigits - getDigits(lineNumber) - 1; i >= 0; --i) {
                sb.append(' ');
            }
            sb.append(lineNumber).append(System.lineSeparator());
        }
        setText(sb.toString());
    }
}