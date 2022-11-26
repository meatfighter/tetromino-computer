package tetrominocomputer.ui;

import java.awt.Dimension;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

// https://stackoverflow.com/questions/417480/hide-certain-actions-from-swings-undo-manager
// https://stackoverflow.com/questions/7156038/jtextpane-line-wrapping
public class CustomTextPane extends JTextPane {
    
    public NoStyleUndoManage createUndoManager() {
        return new NoStyleUndoManage();
    }
    
    public class NoStyleUndoManage extends UndoManager {

        @Override
        public synchronized void undo() throws CannotUndoException {
            do {
                UndoableEdit edit = editToBeUndone();
                if (edit instanceof AbstractDocument.DefaultDocumentEvent) {
                    AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) edit;
                    if (event.getType() == DocumentEvent.EventType.CHANGE) {
                        super.undo();
                        continue;
                    }
                }
                break;
            } while (true);

            super.undo();
        }

        @Override
        public synchronized void redo() throws CannotRedoException {
            super.redo();
            final int caretPosition = getCaretPosition();

            do {
                final UndoableEdit edit = editToBeRedone();
                if (edit instanceof AbstractDocument.DefaultDocumentEvent) {
                    AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) edit;
                    if (event.getType() == DocumentEvent.EventType.CHANGE) {
                        super.redo();
                        continue;
                    }
                }
                break;
            } while (true);

            setCaretPosition(caretPosition);
        }    
    }  
        
    @Override
    public boolean getScrollableTracksViewportWidth() {
        // Only track viewport width when the viewport is wider than the preferred width
        return getUI().getPreferredSize(this).width
                <= getParent().getSize().width;
    }

    @Override
    public Dimension getPreferredSize() {
        // Avoid substituting the minimum width for the preferred width when the viewport is too narrow
        return getUI().getPreferredSize(this);
    }    
}
