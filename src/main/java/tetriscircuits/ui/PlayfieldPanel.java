package tetriscircuits.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PlayfieldPanel extends javax.swing.JPanel {
    
    private static final Color BACKGROUND = new Color(0x2B2B2B);
    private static final Color GRID = new Color(0x555555);
    private static final Color AXISES = new Color(0xA9B7C6);
    private static final Color CURSOR = new Color(0xFFFFFF);
    
    private final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
    
    private int cellSize = 12;
    private int playfieldWidth = 64;
    private int playfieldHeight = 128;

    private StructureRenderer structureRenderer;
    
    private Dimension minimalSize = new Dimension(playfieldWidth * cellSize, playfieldHeight * cellSize);
    
    private Cursor invisibleMouseCursor;
    
    private Integer lastCellX;
    private Integer lastCellY;
    
    private CircuitsFrame circuitsFrame;
    private CircuitsEditorPanel circuitsEditorPanel;
    
    private StructureRenderer cursorRenderer;
    
    private boolean mouseVisible = true;
    
    /**
     * Creates new form PlayfieldPanel
     */
    public PlayfieldPanel() {
        initComponents();
    }
    
    public void setCursorRenderer(final StructureRenderer cursorRenderer) {        
        this.cursorRenderer = cursorRenderer;
    }

    public void setCircuitsFrame(final CircuitsFrame circuitsFrame) {
        this.circuitsFrame = circuitsFrame; 
        invisibleMouseCursor = circuitsFrame.getToolkit().createCustomCursor(
                new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new java.awt.Point(0, 0), "null");
    }

    public void setCircuitsEditorPanel(final CircuitsEditorPanel circuitsEditorPanel) {
        this.circuitsEditorPanel = circuitsEditorPanel;
    }
    
    private void setMouseCursor(final boolean visible) {  
        if (visible != mouseVisible) {
            mouseVisible = visible;
            setCursor(visible ? DEFAULT_CURSOR : invisibleMouseCursor);
        }
    }
    
    public void runCompleted(final StructureRenderer structureRenderer) {
        this.structureRenderer = structureRenderer;
        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMaximumSize(null);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        
        final Dimension size = getSize();
        final int width = playfieldWidth * cellSize;
        final int height = playfieldHeight * cellSize;
        final int originY = size.height - 1 - height;
        final int originX = (size.width - width) >> 1;
        
        final int x = evt.getX();
        final int y = evt.getY();
        if (x >= originX && x < originX + width && y >= originY && y < originY + height) {
            final int cellX = (x - originX) / cellSize - (playfieldWidth >> 1);
            final int cellY = (playfieldHeight - 1) - (y - originY) / cellSize;
            if (lastCellX == null || cellX != lastCellX || cellY != lastCellY) { 
                repaintCursor(originX, originY);
                lastCellX = cellX;
                lastCellY = cellY;
                repaintCursor(originX, originY);
                circuitsFrame.getCoordinatesLabel().setText(String.format("%d:%d", lastCellX, lastCellY));
            }
            setMouseCursor(false);
        } else {
            repaintCursor(originX, originY);
            lastCellX = lastCellY = null;
            circuitsFrame.getCoordinatesLabel().setText("");
            setMouseCursor(true);
        }
    }//GEN-LAST:event_formMouseMoved
    
    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        switch (evt.getButton()) {
            case 1: {
                final Dimension size = getSize();
                final int width = playfieldWidth * cellSize;
                final int height = playfieldHeight * cellSize;
                final int originY = size.height - 1 - height;
                final int originX = (size.width - width) >> 1;
                final int x = evt.getX();
                final int y = evt.getY();
                if (x >= originX && x < originX + width && y >= originY && y < originY + height) {
                    final int cellX = (x - originX) / cellSize - (playfieldWidth >> 1);
                    final int cellY = (playfieldHeight - 1) - (y - originY) / cellSize;
                    circuitsEditorPanel.insertStructure(cellX, cellY);
                }
                break;
            }
            default:
                clearCursorRenderer();
                break;
        }
    }//GEN-LAST:event_formMousePressed

    public void clearCursorRenderer() {
        
        if (cursorRenderer == null) {
            return;
        }
        
        final StructureRenderer cr = cursorRenderer;
        cursorRenderer = null;
        final Dimension size = getSize();          
        final int originX = (size.width - playfieldWidth * cellSize) >> 1;
        final int originY = size.height - 1 - playfieldHeight * cellSize;
        cr.repaint(
                this, 
                originX + (lastCellX + (playfieldWidth >> 1)) * cellSize, 
                originY - (lastCellY - playfieldHeight + 1) * cellSize, 
                cellSize);
    }
    
    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
        formMouseMoved(evt);
    }//GEN-LAST:event_formMouseEntered

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
        
        setMouseCursor(true);      

        final Dimension size = getSize();          
        repaintCursor((size.width - playfieldWidth * cellSize) >> 1, size.height - 1 - playfieldHeight * cellSize);

        lastCellX = lastCellY = null;
        circuitsFrame.getCoordinatesLabel().setText("");
    }//GEN-LAST:event_formMouseExited

    private void repaintCursor(final int originX, final int originY) {
        if (lastCellX == null) {
            return;
        }
            
        if (cursorRenderer == null) {
            repaint(originX + (lastCellX + (playfieldWidth >> 1)) * cellSize, 
                    originY - (lastCellY - playfieldHeight + 1) * cellSize, 
                    cellSize + 1, 
                    cellSize + 1);
            return;
        }
        
        cursorRenderer.repaint(
                this, 
                originX + (lastCellX + (playfieldWidth >> 1)) * cellSize, 
                originY - (lastCellY - playfieldHeight + 1) * cellSize, 
                cellSize);
    }
    
    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        formMouseMoved(evt);
    }//GEN-LAST:event_formMouseDragged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    protected void paintComponent(final Graphics G) {
        
        final Graphics2D g = (Graphics2D)G;
        
        final Dimension size = getSize();
        
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, size.width, size.height);
                
        final int width = playfieldWidth * cellSize;
        final int height = playfieldHeight * cellSize;
        final int originX = (size.width - width) >> 1;
        final int originY = size.height - 1 - height;        
        final int centerX = originX + (width >> 1);
        final int centerY = originY + height - cellSize;
        
        g.setColor(GRID);
        for (int i = 0, x = originX; i <= playfieldWidth; ++i, x += cellSize) {
            g.drawLine(x, originY, x, originY + height);
        }
        for (int i = 0, y = originY; i <= playfieldHeight; ++i, y += cellSize) {
            g.drawLine(originX, y, originX + width, y);
        }
        
        g.setColor(AXISES);
        final int middleX = originX + (playfieldWidth >> 1) * cellSize;
        g.drawLine(middleX, originY, middleX, originY + height - 1);
        
        if (structureRenderer != null) {
            structureRenderer.render(g, centerX, centerY, cellSize);
        }
        
        if (lastCellX != null) {            
            if (cursorRenderer == null) {  
                g.setColor(CURSOR);
                g.drawRect(originX + (lastCellX + (playfieldWidth >> 1)) * cellSize,
                        originY - (lastCellY - playfieldHeight + 1) * cellSize, cellSize, cellSize);
            } else {
                cursorRenderer.render(g, 
                        originX + (lastCellX + (playfieldWidth >> 1)) * cellSize, 
                        originY - (lastCellY - playfieldHeight + 1) * cellSize, 
                        cellSize);
            }
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return minimalSize;
    }    

    @Override
    public Dimension getPreferredSize() {
        return minimalSize;
    }
}