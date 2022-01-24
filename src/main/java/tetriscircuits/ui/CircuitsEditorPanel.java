package tetriscircuits.ui;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import static java.lang.Math.min;
import static java.lang.Math.round;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
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
import static org.apache.commons.lang3.StringUtils.isBlank;
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
    
    private boolean tetrisScriptHasFocus = true;
    private boolean javaScriptHasFocus;
    
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
                            TetrisScriptDocumentFilter.applySyntaxHighlighting(doc, element);
                        } else if (comment) {
                            if (!line.startsWith("#")) {
                                doc.insertString(startOffset, "#", null);
                                TetrisScriptDocumentFilter.applySyntaxHighlighting(doc, element);
                            }                            
                        } else {
                            if (line.startsWith("#")) {                                
                                doc.remove(startOffset, 1);
                                TetrisScriptDocumentFilter.applySyntaxHighlighting(doc, element);
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
    
    public void reset() {
        tetrisScriptTextPane.setText("");
        javaScriptTextArea.setText("");
        outputTextArea.setText("");
        playfieldPanel.reset();
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
                clearOutput();
            }
            @Override
            public void append(final String text) {
                if (!EventQueue.isDispatchThread()) {
                    EventQueue.invokeLater(() -> append(text));
                    return;
                }
                appendOutput(text);
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
    
    public void clearOutput() {
        outputTextArea.setText("");
    }
    
    public void appendOutput(final String text) {
        outputTextArea.append(text + "\n");
    }

    public void setCircuitsFrame(final CircuitsFrame circuitsFrame) {
        this.circuitsFrame = circuitsFrame;
        playfieldPanel.setCircuitsFrame(circuitsFrame);
        playfieldPanel.setCircuitsEditorPanel(this);
    }
    
    public void init() {                
        lowerSplitPane.setDividerLocation(0.2);
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
    
    public void clearTetrisScript() {
        tetrisScriptTextPane.setText("");
    }
    
    public void setTetrisScript(final String tetrisScript) {
        tetrisScriptTextPane.setText(tetrisScript);
        TetrisScriptDocumentFilter.applySyntaxHighlighting(tetrisScriptTextPane.getStyledDocument());
    }
    
    public void clearJavaScript() {
        javaScriptTextArea.setText("");
    }
    
    public void setJavaScript(final String javaScript) {
        javaScriptTextArea.setText(javaScript);
    }
    
    public void centerPlayfield() {  
        final Dimension viewportSize = playfieldScrollPane.getViewport().getSize();   
        final JScrollBar horizontalScrollBar = playfieldScrollPane.getHorizontalScrollBar();
        final JScrollBar verticalScrollBar = playfieldScrollPane.getVerticalScrollBar();
        horizontalScrollBar.setValue(horizontalScrollBar.getMinimum() + ((horizontalScrollBar.getMaximum() 
                - horizontalScrollBar.getMinimum() - viewportSize.width) >> 1));
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
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
    
    public void goToLine(final int lineNumber) {        
        if (javaScriptHasFocus) {
            goToLine(lineNumber, javaScriptTextArea, javaScriptScrollPane);
        } else {
            goToLine(lineNumber, tetrisScriptTextPane, tetrisScriptScrollPane);
        }
    }
    
    private void goToLine(final int lineNumber, final JTextComponent textComponent, final JScrollPane scrollPane) {
        textComponent.requestFocusInWindow();
        final Document doc = textComponent.getDocument();
        final Element root = doc.getDefaultRootElement();
        int lineNum = lineNumber - 1;
        if (lineNum >= root.getElementCount()) {
            lineNum = root.getElementCount() - 1;
        }
        if (lineNum < 0) {
            lineNum = 0;
        } 
        final Element line = root.getElement(lineNum);
        textComponent.setCaretPosition(line.getStartOffset());
        
        EventQueue.invokeLater(() -> {
            try {
                final Rectangle rect = textComponent.modelToView(line.getStartOffset());
                final JViewport viewport = scrollPane.getViewport();
                final JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
                scrollBar.setValue(rect.y - ((viewport.getHeight() - rect.height) >> 1));            
            } catch (final BadLocationException e) { 
            }
        });
    }
    
    private void scrollToCaretPosition(final JTextComponent textComponent, final JScrollPane scrollPane) {
        textComponent.requestFocusInWindow();
        EventQueue.invokeLater(() -> {
            try {
                final Rectangle rect = textComponent.modelToView(textComponent.getCaretPosition());
                final JViewport viewport = scrollPane.getViewport();
                final JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
                scrollBar.setValue(rect.y - ((viewport.getHeight() - rect.height) >> 1));            
            } catch (final BadLocationException e) { 
            }
        });
    }
    
    public void insertStructure(final int cellX, final int cellY) {               
        try {            
            final StyledDocument doc = tetrisScriptTextPane.getStyledDocument(); 
            final Element root = doc.getDefaultRootElement();
            final Element caretLine = root.getElement(root.getElementIndex(tetrisScriptTextPane.getCaretPosition()));
            final int offset;
            boolean endWithNewline = false;
            if (tetrisScriptTextPane.getCaretPosition() == caretLine.getStartOffset()) {
                if (caretLine.getStartOffset() == 0) {
                    offset = 0;                    
                    endWithNewline = doc.getLength() > 0;
                } else {
                    offset = caretLine.getStartOffset() - 1;
                }
            } else {
                offset = caretLine.getEndOffset() - 1;
            }
            
            String prefix = "";
            if (offset > 0 && !Character.isWhitespace(doc.getText(offset - 1, 1).charAt(0))) {
                prefix = (componentName == null) ? " " : "\n";
            }     
            String line = String.format("%s%s%d", prefix, (componentName == null) ? "" : (componentName + " "), cellX);
            if (aggregateComponent) {
                line += " " + cellY;
            } 
            if (endWithNewline) {
                line += "\n";
            }
            doc.insertString(offset, line, null); 
            final Element newLine = root.getElement(root.getElementIndex(offset + 1));
            TetrisScriptDocumentFilter.applySyntaxHighlighting(doc, newLine);
            tetrisScriptTextPane.setCaretPosition(newLine.getEndOffset() - 1);
            if (componentName != null) {
                circuitsFrame.buildAndRun(tetrisScriptTextPane.getText(), javaScriptTextArea.getText());
            }
            clearCursorRenderer();            
        } catch (final BadLocationException e) {
        }
    }
    
    public void undo() {
        try {
            if (tetrisScriptHasFocus) {
                tetrisScriptUndoManager.undo();
            } else if (javaScriptHasFocus) {
                javaScriptUndoManager.undo();
            }
        } catch (final CannotUndoException cue) {
        }        
    }
    
    public void redo() {
        try {
            if (tetrisScriptHasFocus) {
                tetrisScriptUndoManager.redo();
            } else if (javaScriptHasFocus) {
                javaScriptUndoManager.redo();
            }
        } catch (final CannotRedoException cre) {
        }
    }
    
    public void save(final String compName, final File tsFile, final File jsFile) {
        controller.save(compName, tsFile, tetrisScriptTextPane.getText(), jsFile, javaScriptTextArea.getText());
    }
    
    public void build(final String componentName, final int depth) {
        controller.build(componentName, tetrisScriptTextPane.getText(), javaScriptTextArea.getText(), depth);
    }
    
    public void buildAndRun(final String componentName, final String testBitsStr, final int depth) {
        controller.buildAndRun(componentName, tetrisScriptTextPane.getText(), javaScriptTextArea.getText(), testBitsStr,
                depth);
    }
    
    public boolean isTetrisScriptSelected() {
        return tetrisScriptTextPane.getSelectionStart() != tetrisScriptTextPane.getSelectionEnd();
    }
    
    public String getTetrisScriptLines(final boolean selection) {
        final int selectionStart = tetrisScriptTextPane.getSelectionStart();
        final int selectionEnd = tetrisScriptTextPane.getSelectionEnd();
        if (!selection || selectionStart == selectionEnd) {
            return tetrisScriptTextPane.getText();
        }
        
        final Document doc = tetrisScriptTextPane.getDocument();
        final Element root = doc.getDefaultRootElement();
        final int start = root.getElementIndex(selectionStart);
        final int end = root.getElementIndex(selectionEnd);
        final StringBuilder sb = new StringBuilder();
        try {
            for (int i = start; i <= end; ++i) {
                final Element line = root.getElement(i);
                sb.append(doc.getText(line.getStartOffset(), line.getEndOffset() - line.getStartOffset()));
            }
        } catch (final BadLocationException e) {                
        }
        return sb.toString();
    }
    
    public void replaceTetrisScriptLines(final String replacement, final boolean selection) {        
        final StyledDocument doc = tetrisScriptTextPane.getStyledDocument();
        final int selectionStart = tetrisScriptTextPane.getSelectionStart();
        final int selectionEnd = tetrisScriptTextPane.getSelectionEnd();
        if (!selection || selectionStart == selectionEnd) {
            tetrisScriptTextPane.setText(replacement.substring(0, replacement.length() - 1));
            TetrisScriptDocumentFilter.applySyntaxHighlighting(doc);
            return;
        }
                
        final Element root = doc.getDefaultRootElement();
        final Element start = root.getElement(root.getElementIndex(selectionStart));
        final Element end = root.getElement(root.getElementIndex(selectionEnd));        
        try {
            doc.remove(start.getStartOffset(), end.getEndOffset() - start.getStartOffset());
            doc.insertString(start.getStartOffset(), replacement, null);
            TetrisScriptDocumentFilter.applySyntaxHighlighting(doc, selectionStart, selectionEnd);
        } catch (final BadLocationException e) {
            e.printStackTrace();
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
        tetrisScriptTextPane.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                tetrisScriptTextPaneCaretUpdate(evt);
            }
        });
        tetrisScriptTextPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tetrisScriptTextPaneFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tetrisScriptTextPaneFocusLost(evt);
            }
        });
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
        javaScriptTextArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                javaScriptTextAreaCaretUpdate(evt);
            }
        });
        javaScriptTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                javaScriptTextAreaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                javaScriptTextAreaFocusLost(evt);
            }
        });
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
        playfieldScrollPane.setWheelScrollingEnabled(false);

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

    private void tetrisScriptTextPaneCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_tetrisScriptTextPaneCaretUpdate
        updateCursorCoordinates((JTextComponent)evt.getSource());
    }//GEN-LAST:event_tetrisScriptTextPaneCaretUpdate

    private void tetrisScriptTextPaneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tetrisScriptTextPaneFocusLost
        circuitsFrame.clearCursorCoordinates();
    }//GEN-LAST:event_tetrisScriptTextPaneFocusLost

    private void javaScriptTextAreaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_javaScriptTextAreaCaretUpdate
        updateCursorCoordinates((JTextComponent)evt.getSource());
    }//GEN-LAST:event_javaScriptTextAreaCaretUpdate

    private void javaScriptTextAreaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_javaScriptTextAreaFocusLost
        circuitsFrame.clearCursorCoordinates();
    }//GEN-LAST:event_javaScriptTextAreaFocusLost

    private void tetrisScriptTextPaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tetrisScriptTextPaneFocusGained
        tetrisScriptHasFocus = true;
        javaScriptHasFocus = false;
    }//GEN-LAST:event_tetrisScriptTextPaneFocusGained

    private void javaScriptTextAreaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_javaScriptTextAreaFocusGained
        javaScriptHasFocus = true;
        tetrisScriptHasFocus = false;
    }//GEN-LAST:event_javaScriptTextAreaFocusGained

    private void updateCursorCoordinates(final JTextComponent textComponent) {
        final Document doc = textComponent.getDocument();
        final Element root = doc.getDefaultRootElement();
        final int caretPos = textComponent.getCaretPosition();
        final int lineNumber = root.getElementIndex(caretPos);
        final Element line = root.getElement(lineNumber);
        final int columnNumber = caretPos - line.getStartOffset();
        circuitsFrame.setCursorCoordinates(lineNumber + 1, columnNumber + 1);
    }
    
    public void findNext(final String findWhat, final boolean backwards, final boolean matchCase, final boolean regex, 
            final boolean wrapAround) {        
        if (javaScriptHasFocus) {
            findNext(javaScriptTextArea, javaScriptScrollPane, findWhat, backwards, matchCase, regex, wrapAround);
        } else {
            findNext(tetrisScriptTextPane, tetrisScriptScrollPane, findWhat, backwards, matchCase, regex, wrapAround);
        }
    }
    
    public void findNext(final JTextComponent textComponent, final JScrollPane scrollPane, final String findWhat, 
            final boolean backwards, final boolean matchCase, final boolean regex, final boolean wrapAround) {
        
        final int location = findNextLocation(textComponent, findWhat, backwards, matchCase, regex, wrapAround);
        if (location < 0) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        circuitsFrame.requestFocus();
        textComponent.setCaretPosition(location);
        scrollToCaretPosition(textComponent, scrollPane);
    }
    
    public void replace(final String findWhat, final String replaceWith, final boolean backwards, 
            final boolean matchCase, final boolean regex, final boolean wrapAround) {
        if (javaScriptHasFocus) {
            replace(javaScriptTextArea, javaScriptScrollPane, findWhat, replaceWith, backwards, matchCase, regex, 
                    wrapAround);
        } else {
            replace(tetrisScriptTextPane, tetrisScriptScrollPane, findWhat, replaceWith, backwards, matchCase, regex, 
                    wrapAround);
            TetrisScriptDocumentFilter.applySyntaxHighlighting(tetrisScriptTextPane.getStyledDocument());
        }
    }
    
    public void replace(final JTextComponent textComponent, final JScrollPane scrollPane, final String findWhat, 
            final String replaceWith, final boolean backwards, final boolean matchCase, final boolean regex, 
            final boolean wrapAround) {
        
        final int location = findNextLocation(textComponent, findWhat, backwards, matchCase, regex, wrapAround);
        if (location < 0) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        final StringBuilder sb = new StringBuilder(textComponent.getText().replaceAll("[\n\r]+", "\n"));
        sb.replace(location, location + findWhat.length(), replaceWith);
        textComponent.setText(sb.toString());
        
        circuitsFrame.requestFocus();
        textComponent.setCaretPosition(location);
        scrollToCaretPosition(textComponent, scrollPane);
    }

    public void replaceAll(final String findWhat, final String replaceWith, final boolean backwards, 
            final boolean matchCase, final boolean regex, final boolean wrapAround) {
        if (javaScriptHasFocus) {
            replaceAll(javaScriptTextArea, javaScriptScrollPane, findWhat, replaceWith, backwards, matchCase, regex, 
                    wrapAround);
        } else {
            replaceAll(tetrisScriptTextPane, tetrisScriptScrollPane, findWhat, replaceWith, backwards, matchCase, regex, 
                    wrapAround);
        }
    } 
    
    public void replaceAll(final JTextComponent textComponent, final JScrollPane scrollPane, final String findWhat, 
            final String replaceWith, final boolean backwards, final boolean matchCase, final boolean regex, 
            final boolean wrapAround) {
        
        final List<Integer> locations = findAll(textComponent, findWhat, matchCase, regex);
        if (locations.isEmpty()) {
            Toolkit.getDefaultToolkit().beep();                    
            return;
        }
        
        final int caretPosition = textComponent.getCaretPosition();
        final int replaceLength = findWhat.length();
        final StringBuilder sb = new StringBuilder(textComponent.getText().replaceAll("[\n\r]+", "\n"));
        for (int i = locations.size() - 1; i >= 0; --i) {
            final int location = locations.get(i);
            if (!wrapAround) {
                if (backwards) {
                    if (location > caretPosition) {
                        continue;
                    }
                } else if (location < caretPosition) {
                    continue;
                }
            }
            sb.replace(location, location + replaceLength, replaceWith);
        }
        textComponent.setText(sb.toString());        
        
        if (!javaScriptHasFocus) {
            TetrisScriptDocumentFilter.applySyntaxHighlighting((StyledDocument)textComponent.getDocument());
        }
    }
    
    private int findNextLocation(final JTextComponent textComponent, final String findWhat, final boolean backwards, 
            final boolean matchCase, final boolean regex, final boolean wrapAround) {
        
        final List<Integer> locations = findAll(textComponent, findWhat, matchCase, regex);
        if (locations.isEmpty()) {
            return -1;
        }
        
        final int caretPosition = textComponent.getCaretPosition();
        if (backwards) {
            for (int i = locations.size() - 1; i >= 0; --i) {
                final int location = locations.get(i);
                if (location < caretPosition) {
                    return location;
                }
            }
            if (wrapAround) {
                for (int i = locations.size() - 1; i >= 0; --i) {
                    final int location = locations.get(i);
                    if (location >= caretPosition) {
                        return location;
                    } else {
                        return -1;
                    }
                }
            }
        } else {
            for (int i = 0; i < locations.size(); ++i) {
                final int location = locations.get(i);
                if (location > caretPosition) {
                    return location;
                }
            }
            if (wrapAround) {
                for (int i = 0; i < locations.size(); ++i) {
                    final int location = locations.get(i);
                    if (location <= caretPosition) {
                        return location;
                    } else {
                        return -1;
                    }
                }
            }
        }
        
        return -1;
    }
    
    private List<Integer> findAll(final JTextComponent textComponent, final String findWhat, final boolean matchCase, 
            final boolean regex) {
        
        final List<Integer> locations = new ArrayList<>();
        if (isBlank(findWhat)) {
            return locations;
        }
        
        String text = textComponent.getText().replaceAll("[\n\r]+", "\n");
        String findStr = findWhat.trim();
        
        if (regex) {
            try {
                final Matcher matcher = Pattern.compile(findStr).matcher(text);
                while (matcher.find()) {
                    locations.add(matcher.start());
                }
            } catch (final PatternSyntaxException e) {                
            }
        } else {
            if (!matchCase) {
                text = text.toLowerCase();
                findStr = findStr.toLowerCase();
            }            
            for (int i = 0; i < text.length(); ) {
                final int index = text.indexOf(findStr, i);
                if (index < 0) {
                    break;
                }
                locations.add(index);
                i = index + 1;
            }
        }        
        
        return locations;
    }
    
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
