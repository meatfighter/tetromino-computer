package tetriscircuits.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;

public class CodeDocumentFilter extends DocumentFilter {
    
    @Override
    public void replace(final DocumentFilter.FilterBypass fb, final int offset, final int length, String text, 
            final AttributeSet attrs) throws BadLocationException {
        
        if (!"\t".equals(text)) {
            fb.replace(offset, length, text, attrs);
            return;
        }
        
        final Document doc = fb.getDocument();
        final Element root = doc.getDefaultRootElement();
        final int startIndex = root.getElementIndex(offset);
        final int endIndex = root.getElementIndex(offset + length);
        if (startIndex == endIndex) {
            fb.replace(offset, length, "    ", attrs);
            return;
        }
        
        for (int i = startIndex; i <= endIndex; ++i) {
            fb.insertString(root.getElement(i).getStartOffset(), "    ", null);
        }
    }
}
