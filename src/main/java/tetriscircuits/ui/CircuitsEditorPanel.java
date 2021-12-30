package tetriscircuits.ui;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
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
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class CircuitsEditorPanel extends javax.swing.JPanel {

    private static final KeyStroke UNDO_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
    private static final KeyStroke REDO_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK);
    private static final KeyStroke UNINDENT_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, Event.SHIFT_MASK);
    private static final KeyStroke COMMENT_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, Event.CTRL_MASK);
    
    private LineNumberingTextArea lineNumberingTextArea;
    
    /**
     * Creates new form NewJPanel
     */
    public CircuitsEditorPanel() {
        initComponents();
        lineNumberingTextArea = new LineNumberingTextArea(codeTextArea);
        codeScrollPane.setRowHeaderView(lineNumberingTextArea);
        codeTextArea.getDocument().addDocumentListener(new DocumentListener() {
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
        ((AbstractDocument)codeTextArea.getDocument()).setDocumentFilter(new CodeDocumentFilter());
        
        final UndoManager undoManager = new UndoManager();
        codeTextArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(final UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });
        codeTextArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(UNDO_KEYSTROKE, "undoKeyStroke");
        codeTextArea.getActionMap().put("undoKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    undoManager.undo();
                } catch (final CannotUndoException cue) {
                }
            }
        });
        codeTextArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(REDO_KEYSTROKE, "redoKeyStroke");
        codeTextArea.getActionMap().put("redoKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    undoManager.redo();
                } catch (final CannotRedoException cre) {
                }
            }
        });
        codeTextArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(UNINDENT_KEYSTROKE, "unindentKeyStroke");
        codeTextArea.getActionMap().put("unindentKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                final Document doc = codeTextArea.getDocument();
                final Element root = doc.getDefaultRootElement();
                final int startIndex = root.getElementIndex(codeTextArea.getSelectionStart());
                final int endIndex = root.getElementIndex(codeTextArea.getSelectionEnd());
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
        codeTextArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(COMMENT_KEYSTROKE, "commentKeyStroke");
        codeTextArea.getActionMap().put("commentKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
            final Document doc = codeTextArea.getDocument();
                final Element root = doc.getDefaultRootElement();
                final int startIndex = root.getElementIndex(codeTextArea.getSelectionStart());
                final int endIndex = root.getElementIndex(codeTextArea.getSelectionEnd());
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
                        } else if (comment) {
                            if (!line.startsWith("#")) {
                                doc.insertString(startOffset, "#", null);
                            }                            
                        } else {
                            if (line.startsWith("#")) {
                                doc.remove(startOffset, 1);
                            }                            
                        }
                    } catch (final BadLocationException e) {
                    }
                }
            }
        });        
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        playfieldPanel = new tetriscircuits.ui.PlayfieldPanel();
        splitPane2 = new javax.swing.JSplitPane();
        codeScrollPane = new javax.swing.JScrollPane();
        codeTextArea = new javax.swing.JTextArea();
        outputScrollPane = new javax.swing.JScrollPane();
        outputTextArea = new javax.swing.JTextArea();

        javax.swing.GroupLayout playfieldPanelLayout = new javax.swing.GroupLayout(playfieldPanel);
        playfieldPanel.setLayout(playfieldPanelLayout);
        playfieldPanelLayout.setHorizontalGroup(
            playfieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 804, Short.MAX_VALUE)
        );
        playfieldPanelLayout.setVerticalGroup(
            playfieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 766, Short.MAX_VALUE)
        );

        splitPane.setRightComponent(playfieldPanel);

        splitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        codeTextArea.setBackground(new java.awt.Color(43, 43, 43));
        codeTextArea.setColumns(20);
        codeTextArea.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        codeTextArea.setForeground(new java.awt.Color(169, 183, 198));
        codeTextArea.setRows(5);
        codeTextArea.setTabSize(4);
        codeTextArea.setText("This is a test. Hello World!!!");
        codeTextArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 8, 2, 2));
        codeScrollPane.setViewportView(codeTextArea);

        splitPane2.setLeftComponent(codeScrollPane);

        outputTextArea.setBackground(new java.awt.Color(43, 43, 43));
        outputTextArea.setColumns(20);
        outputTextArea.setForeground(new java.awt.Color(169, 183, 198));
        outputTextArea.setRows(5);
        outputScrollPane.setViewportView(outputTextArea);

        splitPane2.setRightComponent(outputScrollPane);

        splitPane.setLeftComponent(splitPane2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1010, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane codeScrollPane;
    private javax.swing.JTextArea codeTextArea;
    private javax.swing.JScrollPane outputScrollPane;
    private javax.swing.JTextArea outputTextArea;
    private tetriscircuits.ui.PlayfieldPanel playfieldPanel;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JSplitPane splitPane2;
    // End of variables declaration//GEN-END:variables
}
