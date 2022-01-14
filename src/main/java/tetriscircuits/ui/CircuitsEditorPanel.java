package tetriscircuits.ui;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import static java.lang.Math.round;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
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
    
    private final UndoManager tetrisScriptUndoManager;
    private final UndoManager javaScriptUndoManager;
    
    private CircuitsFrame circuitsFrame;
    private Controller controller;
    
    private String componentName;
    private boolean aggregateComponent;
    
    /**
     * Creates new form NewJPanel
     */
    public CircuitsEditorPanel() {
        initComponents();
        
        javaScriptUndoManager = new UndoManager();
        javaScriptTextArea.getDocument().addUndoableEditListener(e -> javaScriptUndoManager.addEdit(e.getEdit()));
        createUndoRedoUnIndentHandlers(javaScriptScrollPane, javaScriptTextArea, new JavaScriptDocumentFilter());
        
        tetrisScriptUndoManager = ((CustomTextPane)tetrisScriptTextPane).createUndoManager();
        tetrisScriptTextPane.getDocument().addUndoableEditListener(e -> tetrisScriptUndoManager.addEdit(e.getEdit()));
        createUndoRedoUnIndentHandlers(tetrisScriptScrollPane, tetrisScriptTextPane, new TetrisScriptDocumentFilter());
        tetrisScriptTextPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(COMMENT_KEYSTROKE, "commentKeyStroke");
        tetrisScriptTextPane.getActionMap().put("commentKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                final StyledDocument doc = tetrisScriptTextPane.getStyledDocument();
                final Element root = doc.getDefaultRootElement();
                final int startIndex = root.getElementIndex(tetrisScriptTextPane.getSelectionStart());
                final int endIndex = root.getElementIndex(tetrisScriptTextPane.getSelectionEnd());
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
                            TetrisScriptDocumentFilter.processLine(doc, element);
                        } else if (comment) {
                            if (!line.startsWith("#")) {
                                doc.insertString(startOffset, "#", null);
                                TetrisScriptDocumentFilter.processLine(doc, element);
                            }                            
                        } else {
                            if (line.startsWith("#")) {                                
                                doc.remove(startOffset, 1);
                                TetrisScriptDocumentFilter.processLine(doc, element);
                            }                            
                        }                        
                    } catch (final BadLocationException e) {
                        e.printStackTrace(); // TODO REMOVE
                    }
                }
            }
        });         
    }
    
    private void createUndoRedoUnIndentHandlers(final JScrollPane scrollPane, final JTextComponent textComponent, 
            final DocumentFilter documentFilter) {
        
        scrollPane.setRowHeaderView(new LineNumberingTextArea(textComponent));                
        
        ((AbstractDocument)textComponent.getDocument()).setDocumentFilter(documentFilter);
        
        textComponent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(UNDO_KEYSTROKE, "undoKeyStroke");
        textComponent.getActionMap().put("undoKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                undo();
            }
        });
        
        textComponent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(REDO_KEYSTROKE, "redoKeyStroke");
        textComponent.getActionMap().put("redoKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                redo();
            }
        });
        
        textComponent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(UNINDENT_KEYSTROKE, "unindentKeyStroke");
        textComponent.getActionMap().put("unindentKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                final Document doc = textComponent.getDocument();
                final Element root = doc.getDefaultRootElement();
                final int startIndex = root.getElementIndex(textComponent.getSelectionStart());
                final int endIndex = root.getElementIndex(textComponent.getSelectionEnd());
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
        lowerSplitPane.setDividerLocation(0.2);
        verticalSplitPane.setDividerLocation(0.6);
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
    
    public void setPlayfieldWidth(final int playfieldWidth) {
        playfieldPanel.setPlayfieldWidth(playfieldWidth);
    }
    
    public void setPlayfieldHeight(final int playfieldHeight) {
        playfieldPanel.setPlayfieldHeight(playfieldHeight);
    }
    
    public void setCellSize(final int cellSize) {    
               
        final Dimension viewportSize = playfieldScrollPane.getViewport().getSize();       
        final JScrollBar horizontalScrollBar = playfieldScrollPane.getHorizontalScrollBar();
        final JScrollBar verticalScrollBar = playfieldScrollPane.getVerticalScrollBar();
        final double centerX = (horizontalScrollBar.getValue() + (viewportSize.width >> 1)) 
                / (double)horizontalScrollBar.getMaximum();
        final double centerY = (verticalScrollBar.getValue() + (viewportSize.height >> 1)) 
                / (double)verticalScrollBar.getMaximum();
        final boolean horizontalVisible = horizontalScrollBar.isVisible();
        final boolean horizontalAtTop = horizontalVisible && (horizontalScrollBar.getValue() == 0);
        final boolean horizontalAtBottom = horizontalVisible && (horizontalScrollBar.getValue() + viewportSize.width 
                == horizontalScrollBar.getMaximum());
        final boolean verticalVisible = verticalScrollBar.isVisible();        
        final boolean verticalAtTop = verticalVisible && (verticalScrollBar.getValue() == 0);
        final boolean verticalAtBottom = verticalVisible && (verticalScrollBar.getValue() + viewportSize.height 
                == verticalScrollBar.getMaximum());
        
        playfieldPanel.setCellSize(cellSize);
        
        EventQueue.invokeLater(() -> {
            final Dimension size = playfieldScrollPane.getViewport().getSize();            
            
            if (horizontalAtTop) {
                horizontalScrollBar.setValue(horizontalScrollBar.getMinimum());
            } else if (horizontalAtBottom) {
                horizontalScrollBar.setValue(horizontalScrollBar.getMaximum());
            } else if (!horizontalVisible && horizontalScrollBar.isVisible()) {
                horizontalScrollBar.setValue((horizontalScrollBar.getMaximum() - size.width) >> 1);
            } else {
                horizontalScrollBar.setValue((int)round(centerX * horizontalScrollBar.getMaximum()) 
                        - (size.width >> 1));
            }
            
            if (verticalAtTop) {
                verticalScrollBar.setValue(verticalScrollBar.getMinimum());
            } else if (verticalAtBottom || (!verticalVisible && verticalScrollBar.isVisible())) {
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            } else {
                verticalScrollBar.setValue((int)round(centerY * verticalScrollBar.getMaximum()) - (size.height >> 1));
            }
        });
    }
        
    public void clearCursorRenderer() {
        componentName = null;
        playfieldPanel.clearCursorRenderer();
    }     
    
    public void setCursorRenderer(final StructureRenderer cursorRenderer, final String name) {
        aggregateComponent = true;
        componentName = name;
        playfieldPanel.setCursorRenderer(cursorRenderer);
    }  
    
    public void tetriminoButtonPressed(final ActionEvent evt) {
        aggregateComponent = false;
        componentName = ((TetriminoRenderer)((JButton)evt.getSource())
                .getIcon()).getTetrimino().getName();
        playfieldPanel.setCursorRenderer(StructureRenderer.fromTetrimino(componentName));
    }   
    
    public void insertStructure(final int cellX, final int cellY) {               
        try {            
            final StyledDocument doc = tetrisScriptTextPane.getStyledDocument(); 
            final Element root = doc.getDefaultRootElement();
            final int caretPos = tetrisScriptTextPane.getCaretPosition();
            String prefix = "";
            if (caretPos > 0 && !Character.isWhitespace(doc.getText(caretPos - 1, 1).charAt(0))) {
                prefix = (componentName == null) ? " " : "\n";
            }     
            String line = String.format("%s%s%d", prefix, (componentName == null) ? "" : (componentName + " "), cellX);
            if (aggregateComponent) {
                line += " " + cellY;
            } 
            doc.insertString(caretPos, line, null);
            TetrisScriptDocumentFilter.processLine(doc, root.getElement(root.getElementIndex(caretPos + line.length())));
            if (componentName != null) {
                circuitsFrame.buildAndRun(tetrisScriptTextPane.getText());
            }
            clearCursorRenderer();            
        } catch (final BadLocationException e) {
        }
    }
    
    public void undo() {
        try {
            if (tetrisScriptTextPane.isFocusOwner()) {
                tetrisScriptUndoManager.undo();
            } else if (javaScriptTextArea.isFocusOwner()) {
                javaScriptUndoManager.undo();
            }
        } catch (final CannotUndoException cue) {
        }        
    }
    
    public void redo() {
        try {
            if (tetrisScriptTextPane.isFocusOwner()) {
                tetrisScriptUndoManager.redo();
            } else if (javaScriptTextArea.isFocusOwner()) {
                javaScriptUndoManager.redo();
            }
        } catch (final CannotRedoException cre) {
        }
    }
    
    public void build(final int depth) {
        controller.build(tetrisScriptTextPane.getText(), javaScriptTextArea.getText(), depth);
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
        tetrisScriptScrollPane = new javax.swing.JScrollPane();
        tetrisScriptTextPane = new tetriscircuits.ui.CustomTextPane();
        lowerSplitPane = new javax.swing.JSplitPane();
        javaScriptScrollPane = new javax.swing.JScrollPane();
        javaScriptTextArea = new javax.swing.JTextArea();
        outputScrollPane = new javax.swing.JScrollPane();
        outputTextArea = new javax.swing.JTextArea();
        playfieldScrollPane = new javax.swing.JScrollPane();
        playfieldPanel = new tetriscircuits.ui.PlayfieldPanel();

        setMaximumSize(null);
        setPreferredSize(null);

        horizontalSplitPane.setMaximumSize(null);
        horizontalSplitPane.setPreferredSize(null);

        verticalSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        verticalSplitPane.setMaximumSize(null);
        verticalSplitPane.setPreferredSize(null);

        tetrisScriptScrollPane.setMaximumSize(null);
        tetrisScriptScrollPane.setMinimumSize(null);
        tetrisScriptScrollPane.setPreferredSize(null);

        tetrisScriptTextPane.setBackground(new java.awt.Color(43, 43, 43));
        tetrisScriptTextPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 8, 2, 2));
        tetrisScriptTextPane.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        tetrisScriptTextPane.setForeground(new java.awt.Color(169, 183, 198));
        tetrisScriptTextPane.setMaximumSize(null);
        tetrisScriptTextPane.setPreferredSize(null);
        tetrisScriptScrollPane.setViewportView(tetrisScriptTextPane);

        verticalSplitPane.setLeftComponent(tetrisScriptScrollPane);

        lowerSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        javaScriptScrollPane.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N

        javaScriptTextArea.setBackground(new java.awt.Color(43, 43, 43));
        javaScriptTextArea.setColumns(40);
        javaScriptTextArea.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        javaScriptTextArea.setForeground(new java.awt.Color(169, 183, 198));
        javaScriptTextArea.setRows(10);
        javaScriptTextArea.setTabSize(4);
        javaScriptTextArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 8, 2, 2));
        javaScriptScrollPane.setViewportView(javaScriptTextArea);

        lowerSplitPane.setLeftComponent(javaScriptScrollPane);

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
        outputTextArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 8, 2, 2));
        outputTextArea.setMaximumSize(null);
        outputTextArea.setPreferredSize(null);
        outputScrollPane.setViewportView(outputTextArea);

        lowerSplitPane.setBottomComponent(outputScrollPane);

        verticalSplitPane.setRightComponent(lowerSplitPane);

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
            .addGap(0, 929, Short.MAX_VALUE)
        );
        playfieldPanelLayout.setVerticalGroup(
            playfieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1024, Short.MAX_VALUE)
        );

        playfieldScrollPane.setViewportView(playfieldPanel);

        horizontalSplitPane.setRightComponent(playfieldScrollPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(horizontalSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 981, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(horizontalSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane horizontalSplitPane;
    private javax.swing.JScrollPane javaScriptScrollPane;
    private javax.swing.JTextArea javaScriptTextArea;
    private javax.swing.JSplitPane lowerSplitPane;
    private javax.swing.JScrollPane outputScrollPane;
    private javax.swing.JTextArea outputTextArea;
    private tetriscircuits.ui.PlayfieldPanel playfieldPanel;
    private javax.swing.JScrollPane playfieldScrollPane;
    private javax.swing.JScrollPane tetrisScriptScrollPane;
    private javax.swing.JTextPane tetrisScriptTextPane;
    private javax.swing.JSplitPane verticalSplitPane;
    // End of variables declaration//GEN-END:variables
}
