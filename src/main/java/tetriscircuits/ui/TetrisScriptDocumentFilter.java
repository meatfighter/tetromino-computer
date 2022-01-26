package tetriscircuits.ui;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class TetrisScriptDocumentFilter extends DocumentFilter {

    private static final Pattern KEYWORD_PATTERN = Pattern.compile("(in|out|flatten)($|\\s)");
    private static final Pattern NUMBER_PATTERN 
            = Pattern.compile("((-?[0-9]+)|(-?[0-9]+\\.\\.-?[0-9]+))($|\\s)");
    
    public static final Color NORMAL_COLOR = new Color(0xBBBBBB);
    public static final Color COMMENT_COLOR = new Color(0x808080);
    public static final Color KEYWORD_COLOR = new Color(0xCC7832);
    public static final Color NUMBER_COLOR = new Color(0x6897BB);
    public static final Color DECLARE_COLOR = new Color(0xFFC66D);
    
    public static final SimpleAttributeSet NORMAL_ATTRIBS = new SimpleAttributeSet();
    public static final SimpleAttributeSet COMMENT_ATTRIBS = new SimpleAttributeSet();
    public static final SimpleAttributeSet KEYWORD_ATTRIBS = new SimpleAttributeSet();
    public static final SimpleAttributeSet NUMBER_ATTRIBS = new SimpleAttributeSet();
    public static final SimpleAttributeSet DECLARE_ATTRIBS = new SimpleAttributeSet();
    
    static {                
        StyleConstants.setForeground(NORMAL_ATTRIBS, NORMAL_COLOR);
        StyleConstants.setForeground(COMMENT_ATTRIBS, COMMENT_COLOR);
        StyleConstants.setForeground(KEYWORD_ATTRIBS, KEYWORD_COLOR);
        StyleConstants.setForeground(NUMBER_ATTRIBS, NUMBER_COLOR);
        StyleConstants.setForeground(DECLARE_ATTRIBS, DECLARE_COLOR);
    }   
    
    private long changeCount;
    
    public static void applySyntaxHighlighting(final StyledDocument doc) {
        final Element root = doc.getDefaultRootElement();
        final int end = root.getElementIndex(doc.getLength());
        try {
            for (int i = 0; i <= end; ++i) {
                applySyntaxHighlighting(doc, root.getElement(i));                
            }
        } catch (final BadLocationException e) {                
        }
    }
    
    public static void applySyntaxHighlighting(final StyledDocument doc, final int selectionStart, 
            final int selectionEnd) {
        
        final Element root = doc.getDefaultRootElement();
        final int start = root.getElementIndex(selectionStart);
        final int end = root.getElementIndex(selectionEnd);        
        try {
            for (int i = start; i <= end; ++i) {
                applySyntaxHighlighting(doc, root.getElement(i));                
            }
        } catch (final BadLocationException e) {                
        }
    }
    
    public static void applySyntaxHighlighting(final StyledDocument doc, final Element line) 
            throws BadLocationException {
        
        final int startOffset = line.getStartOffset();
        final int endOffset = line.getEndOffset();        
        final int lineLength = endOffset - startOffset;
        doc.setCharacterAttributes(startOffset, lineLength, NORMAL_ATTRIBS, true);
        final String lineText = doc.getText(startOffset, lineLength);
        final int commentIndex = lineText.indexOf('#');
        
        final String lineStr;
        if (commentIndex >= 0) {
            lineStr = lineText.substring(0, commentIndex);
            doc.setCharacterAttributes(startOffset + commentIndex, lineLength - commentIndex, COMMENT_ATTRIBS, true);
        } else {
            lineStr = lineText;
        }
        
        final Matcher keywordMatcher = KEYWORD_PATTERN.matcher(lineStr);
        while (keywordMatcher.find()) {
            final int start = keywordMatcher.start(1);
            if (start == 0 || Character.isWhitespace(lineStr.charAt(start - 1))) {
                doc.setCharacterAttributes(startOffset + start, 
                        keywordMatcher.end(1) - start, KEYWORD_ATTRIBS, true);
            }
        }
        
        final Matcher numberMatcher = NUMBER_PATTERN.matcher(lineStr);
        while (numberMatcher.find()) {
            final int start = numberMatcher.start(1);
            if (start == 0 || lineStr.charAt(start - 1) == '-' || Character.isWhitespace(lineStr.charAt(start - 1))) {
                doc.setCharacterAttributes(startOffset + start, 
                        numberMatcher.end(1) - start, NUMBER_ATTRIBS, true);
            }
        }
    }

    public long getChangeCount() {
        return changeCount;
    }

    @Override
    public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr) 
            throws BadLocationException {
        
        ++changeCount;
        
        fb.insertString(offset, string, attr);
    }

    @Override
    public void remove(final FilterBypass fb, final int offset, final int length) throws BadLocationException {
        
        ++changeCount;
        
        fb.remove(offset, length);
        final StyledDocument doc = (StyledDocument)fb.getDocument();
        final Element root = doc.getDefaultRootElement();
        applySyntaxHighlighting(doc, root.getElement(root.getElementIndex(offset)));
    }
    
    @Override
    public void replace(final DocumentFilter.FilterBypass fb, final int offset, final int length, String text, 
            final AttributeSet attrs) throws BadLocationException {
        
        ++changeCount;

        final StyledDocument doc = (StyledDocument)fb.getDocument();
        final Element root = doc.getDefaultRootElement();
        final int startIndex = root.getElementIndex(offset);
        
        if ("\n".equals(text)) {            
            final Element line = root.getElement(root.getElementIndex(offset));
            final String lineText = doc.getText(line.getStartOffset(), line.getEndOffset() - line.getStartOffset());
            if (lineText.startsWith("    ") && !isBlank(lineText)) {
                fb.insertString(offset, "\n    ", null);
            } else {
                fb.insertString(offset, text, null);
            }
            return;
        }
        
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
            applySyntaxHighlighting(doc, root.getElement(i));
        }
    }
}
