package tetrominocomputer.tse.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;

public class JavaScriptDocumentFilter extends DocumentFilter {
    
    private long changeCount;
    
    public long getChangeCount() {
        return changeCount;
    }    

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        ++changeCount;
        super.insertString(fb, offset, string, attr);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        ++changeCount;
        super.remove(fb, offset, length);
    }
    
    @Override
    public void replace(final DocumentFilter.FilterBypass fb, final int offset, final int length, String text, 
            final AttributeSet attrs) throws BadLocationException {
        
        ++changeCount;
        
        final Element root = fb.getDocument().getDefaultRootElement();
        final int startIndex = root.getElementIndex(offset);
               
        if (!"\t".equals(text)) {
            fb.replace(offset, length, text, attrs);
            return;
        }
        
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