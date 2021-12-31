package tetriscircuits.ui;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class CodeDocumentFilter extends DocumentFilter {

    private static final Pattern keywordPattern = Pattern.compile("(def|in|out|test)($|\\s)");
    private static final Pattern numberPattern 
            = Pattern.compile("(([0-9]+)|([0-9]+\\.\\.[0-9]+)|([A-Za-z0-9]+[0-9]+\\.\\.[0-9]+))($|\\s)");
    private static final Pattern declarePattern = Pattern.compile("(^|\\s)def\\s+([a-zA-Z_][a-zA-Z0-9_]*)($|\\s)");
    
    private static final Color NORMAL_COLOR = new Color(0xBBBBBB);
    private static final Color COMMENT_COLOR = new Color(0x808080);
    private static final Color KEYWORD_COLOR = new Color(0xCC7832);
    private static final Color NUMBER_COLOR = new Color(0x6897BB);
    private static final Color DECLARE_COLOR = new Color(0xFFC66D);
    
    private static final SimpleAttributeSet NORMAL_ATTRIBS = new SimpleAttributeSet();
    private static final SimpleAttributeSet COMMENT_ATTRIBS = new SimpleAttributeSet();
    private static final SimpleAttributeSet KEYWORD_ATTRIBS = new SimpleAttributeSet();
    private static final SimpleAttributeSet NUMBER_ATTRIBS = new SimpleAttributeSet();
    private static final SimpleAttributeSet DECLARE_ATTRIBS = new SimpleAttributeSet();
    
    static {                
        StyleConstants.setForeground(NORMAL_ATTRIBS, NORMAL_COLOR);
        StyleConstants.setForeground(COMMENT_ATTRIBS, COMMENT_COLOR);
        StyleConstants.setForeground(KEYWORD_ATTRIBS, KEYWORD_COLOR);
        StyleConstants.setForeground(NUMBER_ATTRIBS, NUMBER_COLOR);
        StyleConstants.setForeground(DECLARE_ATTRIBS, DECLARE_COLOR);
    }   
    
    public static void processLine(final StyledDocument doc, final Element line) throws BadLocationException {
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
        
        final Matcher keywordMatcher = keywordPattern.matcher(lineStr);
        while (keywordMatcher.find()) {
            final int start = keywordMatcher.start(1);
            if (start == 0 || Character.isWhitespace(lineStr.charAt(start - 1))) {
                doc.setCharacterAttributes(startOffset + start, 
                        keywordMatcher.end(1) - start, KEYWORD_ATTRIBS, true);
            }
        }
        
        final Matcher numberMatcher = numberPattern.matcher(lineStr);
        while (numberMatcher.find()) {
            final int start = numberMatcher.start(1);
            if (start == 0 || lineStr.charAt(start - 1) == '-' || Character.isWhitespace(lineStr.charAt(start - 1))) {
                doc.setCharacterAttributes(startOffset + start, 
                        numberMatcher.end(1) - start, NUMBER_ATTRIBS, true);
            }
        }
        
        final Matcher declareMatcher = declarePattern.matcher(lineStr);
        while (declareMatcher.find()) {
            final int start = declareMatcher.start(2);
            doc.setCharacterAttributes(startOffset + start, 
                        declareMatcher.end(2) - start, DECLARE_ATTRIBS, true);
        }
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
}
