package tetriscircuits.ui;

import java.awt.Color;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class CodeDocumentFilter extends DocumentFilter {
    
    private static final Color NORMAL_COLOR = new Color(0xBBBBBB);
    private static final Color COMMENT_COLOR = new Color(0x808080);
    private static final Color KEYWORD_COLOR = new Color(0xCC7832);
    
    private static final SimpleAttributeSet NORMAL_ATTRIBS = new SimpleAttributeSet();
    private static final SimpleAttributeSet COMMENT_ATTRIBS = new SimpleAttributeSet();
    private static final SimpleAttributeSet KEYWORD_ATTRIBS = new SimpleAttributeSet();
    
    static {                
        StyleConstants.setForeground(NORMAL_ATTRIBS, NORMAL_COLOR);
        StyleConstants.setForeground(COMMENT_ATTRIBS, COMMENT_COLOR);
        StyleConstants.setForeground(KEYWORD_ATTRIBS, KEYWORD_COLOR);
    }    

    @Override
    public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr) 
            throws BadLocationException {
        fb.insertString(offset, string, attr);
    }

    @Override
    public void remove(final FilterBypass fb, final int offset, final int length) throws BadLocationException {
        fb.remove(offset, length);
        final StyledDocument doc = (StyledDocument)fb.getDocument();
        final Element root = doc.getDefaultRootElement();
        processLine(doc, root.getElement(root.getElementIndex(offset)));
    }
    
    @Override
    public void replace(final DocumentFilter.FilterBypass fb, final int offset, final int length, String text, 
            final AttributeSet attrs) throws BadLocationException {

        final StyledDocument doc = (StyledDocument)fb.getDocument();
        final Element root = doc.getDefaultRootElement();
        final int startIndex = root.getElementIndex(offset);
        
        if ("\t".equals(text)) {
            final int endIndex = root.getElementIndex(offset + length);
            if (startIndex == endIndex) {
                fb.replace(offset, length, "    ", attrs);
                return;
            }
            for (int i = startIndex; i <= endIndex; ++i) {
                fb.insertString(root.getElement(i).getStartOffset(), "    ", null);
            }            
            return;
        }
        
        fb.replace(offset, length, text, attrs);
        
        final int endIndex = root.getElementIndex(offset + text.length());
        for (int i = startIndex; i <= endIndex; ++i) {
            processLine(doc, root.getElement(i));
        }
    }
    
    private void processLine(final StyledDocument doc, final Element line) throws BadLocationException {
        final int startOffset = line.getStartOffset();
        final int endOffset = line.getEndOffset();
        final int lineLength = endOffset - startOffset;
        doc.setCharacterAttributes(startOffset, lineLength, NORMAL_ATTRIBS, true);
        final String lineText = doc.getText(startOffset, lineLength);
        for (int j = 0; j < lineText.length(); ++j) {
            if (lineText.charAt(j) == '#') {
                doc.setCharacterAttributes(startOffset + j, lineLength - j, COMMENT_ATTRIBS, true);
                break;
            }
        }
    }
}
