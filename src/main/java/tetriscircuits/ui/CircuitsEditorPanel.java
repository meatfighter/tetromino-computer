package tetriscircuits.ui;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JViewport;
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
import tetriscircuits.Controller;
import tetriscircuits.OutputListener;
import tetriscircuits.RunListener;
import tetriscircuits.Structure;

public class CircuitsEditorPanel extends javax.swing.JPanel {

    private static final KeyStroke UNDO_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
    private static final KeyStroke REDO_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK);
    private static final KeyStroke UNINDENT_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, Event.SHIFT_MASK);
    private static final KeyStroke COMMENT_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, Event.CTRL_MASK);
    
    
    private final UndoManager undoManager;
    
    private LineNumberingTextArea lineNumberingTextArea;
    
    private CircuitsFrame circuitsFrame;
    private Controller controller;
    
    private String componentName;
    
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

    public Controller getController() {
        return controller;
    }

    public void setController(final Controller controller) {
        this.controller = controller;
        controller.setOutputListener(new OutputListener() {
            @Override
            public void clear() {
                if (!EventQueue.isDispatchThread()) {
                    EventQueue.invokeLater(this::clear);
                    return;
                }
                outputTextArea.setText("");
            }
            @Override
            public void append(final String text) {
                if (!EventQueue.isDispatchThread()) {
                    EventQueue.invokeLater(() -> append(text));
                    return;
                }
                outputTextArea.append(text + "\n");
            }            
        });
        controller.setRunListener(new RunListener() {
            @Override
            public void runCompleted(final Structure structure) {
                if (!EventQueue.isDispatchThread()) {
                    EventQueue.invokeLater(() -> runCompleted(structure));
                    return;
                }           
                playfieldPanel.runCompleted(new StructureRenderer(structure));
            }
        });
    }

    public void setCircuitsFrame(final CircuitsFrame circuitsFrame) {
        this.circuitsFrame = circuitsFrame;
        playfieldPanel.setCircuitsFrame(circuitsFrame);
        playfieldPanel.setCircuitsEditorPanel(this);
    }
    
    public void init() {        
        verticalSplitPane.setDividerLocation(0.8);
        horizontalSplitPane.setDividerLocation(0.4);
        
        EventQueue.invokeLater(() -> {
            final JViewport viewPort = playfieldScrollPane.getViewport();
            final Rectangle bounds = viewPort.getViewRect();
            final Dimension size = viewPort.getViewSize();
            viewPort.setViewPosition(new java.awt.Point((size.width - bounds.width) >> 1, size.height - bounds.height));
        });
        
        final String ESCAPE_PRESSED = "escapePressed";
        getActionMap().put(ESCAPE_PRESSED, new AbstractAction("ESCAPE_PRESSED") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                clearCursorRenderer();
            }
        });
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
                ESCAPE_PRESSED);
    }
        
    public void clearCursorRenderer() {
        componentName = null;
        playfieldPanel.clearCursorRenderer();
    }     
    
    public void setCursorRenderer(final StructureRenderer cursorRenderer, final String name) {
        componentName = name;
        playfieldPanel.setCursorRenderer(cursorRenderer);
    }  
    
    public void tetriminoButtonPressed(final ActionEvent evt) {
        componentName = ((TetriminoRenderer)((JButton)evt.getSource())
                .getIcon()).getTetrimino().getName();
        playfieldPanel.setCursorRenderer(StructureRenderer.fromTetrimino(componentName));
    }   
    
    public void insertStructure(final int cellX, final int cellY) {               
        try {            
            final StyledDocument doc = codeTextPane.getStyledDocument(); 
            final Element root = doc.getDefaultRootElement();
            final int caretPos = codeTextPane.getCaretPosition();
            String prefix = "";
            if (caretPos > 0 && !Character.isWhitespace(doc.getText(caretPos - 1, 1).charAt(0))) {
                prefix = (componentName == null) ? " " : "\n    ";
            }     
            final String line = String.format("%s%s%d %d", prefix, 
                    (componentName == null) ? "" : (componentName + " "), cellX, cellY);
            doc.insertString(caretPos, line, null);
            CodeDocumentFilter.processLine(doc, root.getElement(root.getElementIndex(caretPos + line.length())));
            if (componentName != null) {
                circuitsFrame.buildAndRun(codeTextPane.getText());
            }
            clearCursorRenderer();            
        } catch (final BadLocationException e) {
        }
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
    
    public void build() {
        controller.build(codeTextPane.getText());
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

        setMaximumSize(null);
        setPreferredSize(null);

        horizontalSplitPane.setMaximumSize(null);
        horizontalSplitPane.setPreferredSize(null);

        verticalSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        verticalSplitPane.setMaximumSize(null);
        verticalSplitPane.setPreferredSize(null);

        outputScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outputScrollPane.setMaximumSize(null);
        outputScrollPane.setMinimumSize(null);
        outputScrollPane.setPreferredSize(null);

        outputTextArea.setEditable(false);
        outputTextArea.setBackground(new java.awt.Color(43, 43, 43));
        outputTextArea.setColumns(40);
        outputTextArea.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        outputTextArea.setForeground(new java.awt.Color(169, 183, 198));
        outputTextArea.setRows(10);
        outputTextArea.setTabSize(4);
        outputTextArea.setMaximumSize(null);
        outputTextArea.setPreferredSize(null);
        outputTextArea.setRequestFocusEnabled(false);
        outputScrollPane.setViewportView(outputTextArea);

        verticalSplitPane.setRightComponent(outputScrollPane);

        codeScrollPane.setMaximumSize(null);
        codeScrollPane.setMinimumSize(null);
        codeScrollPane.setPreferredSize(null);

        codeTextPane.setBackground(new java.awt.Color(43, 43, 43));
        codeTextPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 8, 2, 2));
        codeTextPane.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        codeTextPane.setForeground(new java.awt.Color(169, 183, 198));
        codeTextPane.setMaximumSize(null);
        codeTextPane.setPreferredSize(null);
        codeScrollPane.setViewportView(codeTextPane);

        verticalSplitPane.setLeftComponent(codeScrollPane);

        horizontalSplitPane.setLeftComponent(verticalSplitPane);

        playfieldScrollPane.setMaximumSize(null);
        playfieldScrollPane.setMinimumSize(null);
        playfieldScrollPane.setPreferredSize(null);

        playfieldPanel.setMaximumSize(null);
        playfieldPanel.setMinimumSize(null);
        playfieldPanel.setName(""); // NOI18N
        playfieldPanel.setPreferredSize(null);

        javax.swing.GroupLayout playfieldPanelLayout = new javax.swing.GroupLayout(playfieldPanel);
        playfieldPanel.setLayout(playfieldPanelLayout);
        playfieldPanelLayout.setHorizontalGroup(
            playfieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 32768, 32768)
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
            .addComponent(horizontalSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(horizontalSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
