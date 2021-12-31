package tetriscircuits.ui;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class CircuitsEditorPanel extends javax.swing.JPanel {

    private static final KeyStroke UNDO_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
    private static final KeyStroke REDO_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK);
    private static final KeyStroke UNINDENT_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, Event.SHIFT_MASK);
    private static final KeyStroke COMMENT_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, Event.CTRL_MASK);
    
    
    private final UndoManager undoManager;
    
    private LineNumberingTextArea lineNumberingTextArea;
    
    /**
     * Creates new form NewJPanel
     */
    public CircuitsEditorPanel() {
        initComponents();       
        lineNumberingTextArea = new LineNumberingTextArea(codeTextPane);
        codeScrollPane.setRowHeaderView(lineNumberingTextArea);
        codeTextPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) { 
                lineNumberingTextArea.updateLineNumbers();
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                lineNumberingTextArea.updateLineNumbers();
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                lineNumberingTextArea.updateLineNumbers();
            }
        });
        ((AbstractDocument)codeTextPane.getDocument()).setDocumentFilter(new CodeDocumentFilter());
        
        undoManager = ((CustomTextPane)codeTextPane).createUndoManager();
        codeTextPane.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(final UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });
        codeTextPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(UNDO_KEYSTROKE, "undoKeyStroke");
        codeTextPane.getActionMap().put("undoKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                undo();
            }
        });
        codeTextPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(REDO_KEYSTROKE, "redoKeyStroke");
        codeTextPane.getActionMap().put("redoKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                redo();
            }
        });
        codeTextPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(UNINDENT_KEYSTROKE, "unindentKeyStroke");
        codeTextPane.getActionMap().put("unindentKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                final Document doc = codeTextPane.getDocument();
                final Element root = doc.getDefaultRootElement();
                final int startIndex = root.getElementIndex(codeTextPane.getSelectionStart());
                final int endIndex = root.getElementIndex(codeTextPane.getSelectionEnd());
                for (int i = startIndex; i <= endIndex; ++i) {
                    final Element element = root.getElement(i);
                    final int startOffset = element.getStartOffset();
                    final int endOffset = element.getEndOffset();
                    if (endOffset - startOffset < 4) {
                        continue;
                    }
                    try {
                        final String line = doc.getText(startOffset, endOffset - startOffset);
                        if (line.startsWith("    ")) {
                            doc.remove(startOffset, 4);
                        }
                    } catch (final BadLocationException e) {
                    }
                }                
            }
        });
        codeTextPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(COMMENT_KEYSTROKE, "commentKeyStroke");
        codeTextPane.getActionMap().put("commentKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                final StyledDocument doc = codeTextPane.getStyledDocument();
                final Element root = doc.getDefaultRootElement();
                final int startIndex = root.getElementIndex(codeTextPane.getSelectionStart());
                final int endIndex = root.getElementIndex(codeTextPane.getSelectionEnd());
                final int lineCount = endIndex - startIndex + 1;
                int comments = 0;
                for (int i = startIndex; i <= endIndex; ++i) {
                    final Element element = root.getElement(i);
                    final int startOffset = element.getStartOffset();
                    final int endOffset = element.getEndOffset();
                    try {
                        final String line = doc.getText(startOffset, endOffset - startOffset);
                        if (line.startsWith("#")) {
                            ++comments;
                        }
                    } catch (final BadLocationException e) {
                    }
                }
                final boolean toggle = lineCount == 1;
                final boolean comment = comments <= (lineCount >> 1);
                for (int i = startIndex; i <= endIndex; ++i) {
                    final Element element = root.getElement(i);
                    final int startOffset = element.getStartOffset();
                    final int endOffset = element.getEndOffset();
                    try {
                        final String line = doc.getText(startOffset, endOffset - startOffset);
                        if (toggle) {
                            if (line.startsWith("#")) {
                                doc.remove(startOffset, 1);
                            } else {                                
                                doc.insertString(startOffset, "#", null);
                            }
                            CodeDocumentFilter.processLine(doc, element);
                        } else if (comment) {
                            if (!line.startsWith("#")) {
                                doc.insertString(startOffset, "#", null);
                                CodeDocumentFilter.processLine(doc, element);
                            }                            
                        } else {
                            if (line.startsWith("#")) {                                
                                doc.remove(startOffset, 1);
                                CodeDocumentFilter.processLine(doc, element);
                            }                            
                        }                        
                    } catch (final BadLocationException e) {
                        e.printStackTrace(); // TODO REMOVE
                    }
                }
            }
        });        
    }
    
    public void undo() {
        try {
            undoManager.undo();
        } catch (final CannotUndoException cue) {
        }        
    }
    
    public void redo() {
        try {
            undoManager.redo();
        } catch (final CannotRedoException cre) {
        }
    }
    
    public void tetriminoButtonPressed(final ActionEvent evt) {
        final TetriminoRenderer tetriminoRenderer = (TetriminoRenderer)((JButton)evt.getSource()).getIcon();
        final StyledDocument doc = codeTextPane.getStyledDocument();        
        try {            
            final int caretPos = codeTextPane.getCaretPosition();
            doc.insertString(caretPos, ((caretPos > 0 
                    && !Character.isWhitespace(doc.getText(caretPos - 1, 1).charAt(0))) ? "\n    " : "") 
                    + tetriminoRenderer.getTetrimino().getName() + " ", null);
        } catch (final BadLocationException e) {
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        horizontalSplitPane = new javax.swing.JSplitPane();
        verticalSplitPane = new javax.swing.JSplitPane();
        outputScrollPane = new javax.swing.JScrollPane();
        outputTextArea = new javax.swing.JTextArea();
        codeScrollPane = new javax.swing.JScrollPane();
        codeTextPane = new tetriscircuits.ui.CustomTextPane();
        playfieldScrollPane = new javax.swing.JScrollPane();
        playfieldPanel = new tetriscircuits.ui.PlayfieldPanel();

        verticalSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        outputTextArea.setBackground(new java.awt.Color(43, 43, 43));
        outputTextArea.setColumns(20);
        outputTextArea.setForeground(new java.awt.Color(169, 183, 198));
        outputTextArea.setRows(5);
        outputScrollPane.setViewportView(outputTextArea);

        verticalSplitPane.setRightComponent(outputScrollPane);

        codeTextPane.setBackground(new java.awt.Color(43, 43, 43));
        codeTextPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 8, 2, 2));
        codeTextPane.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        codeTextPane.setForeground(new java.awt.Color(169, 183, 198));
        codeScrollPane.setViewportView(codeTextPane);

        verticalSplitPane.setLeftComponent(codeScrollPane);

        horizontalSplitPane.setLeftComponent(verticalSplitPane);

        javax.swing.GroupLayout playfieldPanelLayout = new javax.swing.GroupLayout(playfieldPanel);
        playfieldPanel.setLayout(playfieldPanelLayout);
        playfieldPanelLayout.setHorizontalGroup(
            playfieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 770, Short.MAX_VALUE)
        );
        playfieldPanelLayout.setVerticalGroup(
            playfieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 763, Short.MAX_VALUE)
        );

        playfieldScrollPane.setViewportView(playfieldPanel);

        horizontalSplitPane.setRightComponent(playfieldScrollPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(horizontalSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1010, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(horizontalSplitPane)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane codeScrollPane;
    private javax.swing.JTextPane codeTextPane;
    private javax.swing.JSplitPane horizontalSplitPane;
    private javax.swing.JScrollPane outputScrollPane;
    private javax.swing.JTextArea outputTextArea;
    private tetriscircuits.ui.PlayfieldPanel playfieldPanel;
    private javax.swing.JScrollPane playfieldScrollPane;
    private javax.swing.JSplitPane verticalSplitPane;
    // End of variables declaration//GEN-END:variables
}
