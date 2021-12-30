package tetriscircuits.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.text.Element;

// https://stackoverflow.com/questions/18768649/java-code-to-display-lines-number-in-jtextarea
public class LineNumberingTextArea extends JTextArea {
    
    private JTextArea textArea;
    
    public LineNumberingTextArea(JTextArea textArea) {
        this.textArea = textArea;
        setForeground(new Color(0x888888));
        setBackground(new Color(0x313335));
        setFont(new Font("Monospaced", 0, 13));
        setEditable(false);
        
        // A9B7C6
    }
    
    public void updateLineNumbers() {
        final int caretPosition = textArea.getDocument().getLength();
        final Element root = textArea.getDocument().getDefaultRootElement();
        final StringBuilder lineNumbersTextBuilder = new StringBuilder();
        lineNumbersTextBuilder.append(" 1").append(System.lineSeparator());        
        for (int elementIndex = 2; elementIndex < root.getElementIndex(caretPosition) + 2; elementIndex++) {
            lineNumbersTextBuilder.append(' ').append(elementIndex).append(System.lineSeparator());
        }
        setText(lineNumbersTextBuilder.toString());
    }
}
