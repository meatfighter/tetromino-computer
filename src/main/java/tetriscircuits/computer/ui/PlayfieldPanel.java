package tetriscircuits.computer.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import static tetriscircuits.computer.ui.PlayfieldModel.PLAYFIELD_HEIGHT;
import static tetriscircuits.computer.ui.PlayfieldModel.PLAYFIELD_WIDTH;

public class PlayfieldPanel extends javax.swing.JPanel {
    
    private static final Color GRID_COLOR = new Color(0x151816);
    private static final Color BORDER_COLOR = new Color(0xFFFFFF);
    private static final Color BACKGROUND_COLOR = new Color(0x090909);
    
    public static final Color[] BLOCK_GRADIENT_BRIGHT_COLORS = convertRgbsToColors(new int[] {
        0xD163FF,
        0x7994FF,
        0xFF7471,
        0xFFEE99,
        0xC2FF9B,
        0xFFB481,
        0xAFF4FF,
    });

    public static final Color[] BLOCK_GRADIENT_DARK_COLORS = convertRgbsToColors(new int[] {
        0x9400DB,
        0x0054DA,
        0xD2001F,
        0xBBA200,
        0x00BE00,
        0xCA4800,
        0x00ACC3,
    });

    public static final Color[] BLOCK_HIGHLIGHT_COLORS = convertRgbsToColors(new int[] {
        0xDE8AFF,
        0xA1ADFF,
        0xFFA099,
        0xFFF1AD,
        0xD0FFB0,
        0xFFC8A2,
        0xC0F6FF,
    });

    public static final Color[] BLOCK_SHADOW_COLORS = convertRgbsToColors(new int[] {
        0x7900C1,
        0x0040C0,
        0xB2000B,
        0x8C7900,
        0x009200,
        0xA62800,
        0x00849A,
    });
    
    private static Paint[] GRADIENT_PAINTS = new Paint[BLOCK_GRADIENT_BRIGHT_COLORS.length];
    
    private static final int CELL_SIZE = 32;
    private static final int PLAYFIELD_PADDING = 10;
    
    private static final float GRID_STROKE_WIDTH = 2.5f;
    private static final Stroke GRID_STROKE = new BasicStroke(GRID_STROKE_WIDTH);
    
    private static final float BORDER_STROKE_WIDTH = 3.5f;
    private static final Stroke BORDER_STROKE = new BasicStroke(BORDER_STROKE_WIDTH);
    
    private static final int PREFERRED_WIDTH = 2 * PLAYFIELD_PADDING + CELL_SIZE * PLAYFIELD_WIDTH;
    private static final int PREFERRED_HEIGHT = 2 * PLAYFIELD_PADDING + CELL_SIZE * PLAYFIELD_HEIGHT;
    private static final Dimension PREFERRED_SIZE = new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT);
    
    private static final Path2D.Float GRID_PATH = new Path2D.Float();
    private static final Shape BORDER_SHAPE = new Rectangle2D.Float(PLAYFIELD_PADDING - BORDER_STROKE_WIDTH / 2, 
            PLAYFIELD_PADDING - BORDER_STROKE_WIDTH / 2, CELL_SIZE * PLAYFIELD_WIDTH + BORDER_STROKE_WIDTH, 
            CELL_SIZE * PLAYFIELD_HEIGHT + BORDER_STROKE_WIDTH); 
        
    private static final Shape BLOCK_HIGHLIGHT_SHAPE = new Rectangle2D.Float(0, 0, CELL_SIZE, CELL_SIZE);
    private static final Path2D.Float BLOCK_SHADOW_SHAPE = new Path2D.Float();
    
    private static final float BLOCK_CENTER_FRACTION = 0.8125f;
    private static final Shape BLOCK_CENTER_SHAPE = new Rectangle2D.Float((1 - BLOCK_CENTER_FRACTION) * CELL_SIZE / 2, 
            (1 - BLOCK_CENTER_FRACTION) * CELL_SIZE / 2, BLOCK_CENTER_FRACTION * CELL_SIZE, 
            BLOCK_CENTER_FRACTION * CELL_SIZE);
    
    static {
        for (int i = BLOCK_GRADIENT_BRIGHT_COLORS.length - 1; i >= 0; --i) {
            GRADIENT_PAINTS[i] = new GradientPaint(
                    (1 - BLOCK_CENTER_FRACTION) * CELL_SIZE / 2 + BLOCK_CENTER_FRACTION * CELL_SIZE / 2,
                    (1 - BLOCK_CENTER_FRACTION) * CELL_SIZE / 2,
                    BLOCK_GRADIENT_BRIGHT_COLORS[i],
                    (1 - BLOCK_CENTER_FRACTION) * CELL_SIZE / 2 + BLOCK_CENTER_FRACTION * CELL_SIZE / 2,
                    (1 - BLOCK_CENTER_FRACTION) * CELL_SIZE / 2 + BLOCK_CENTER_FRACTION * CELL_SIZE,
                    BLOCK_GRADIENT_DARK_COLORS[i]);
        }
        
        final float maxX = PLAYFIELD_PADDING + CELL_SIZE * PLAYFIELD_WIDTH;
        for (int i = PLAYFIELD_HEIGHT - 1; i >= 1; --i) {
            float y = PLAYFIELD_PADDING + i * CELL_SIZE;
            GRID_PATH.moveTo(PLAYFIELD_PADDING, y);
            GRID_PATH.lineTo(maxX, y);
        }
        final float maxY = PLAYFIELD_PADDING + CELL_SIZE * PLAYFIELD_HEIGHT;
        for (int i = PLAYFIELD_WIDTH - 1; i >= 1; --i) {
            float x = PLAYFIELD_PADDING + i * CELL_SIZE;
            GRID_PATH.moveTo(x, PLAYFIELD_PADDING);
            GRID_PATH.lineTo(x, maxY);
        }
        
        BLOCK_SHADOW_SHAPE.moveTo(0, 32);
        BLOCK_SHADOW_SHAPE.lineTo(32, 0);
        BLOCK_SHADOW_SHAPE.lineTo(32, 32);
        BLOCK_SHADOW_SHAPE.closePath();
    }
    
    private static Color[] convertRgbsToColors(final int[] rgbs) {
        final Color[] colors = new Color[rgbs.length];
        for (int i = rgbs.length - 1; i >= 0; --i) {
            colors[i] = new Color(rgbs[i]);
        }
        return colors;
    }
    
    private int[][] cells = new int[PLAYFIELD_HEIGHT][PLAYFIELD_WIDTH];
    
    public PlayfieldPanel() {
        initComponents();
    }    

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public void update(final PlayfieldModel playfieldModel) {
        final int[][] cells = playfieldModel.getCells();
        playfieldModel.setCells(this.cells);
        this.cells = cells;
        repaint();
    }
    
    @Override
    protected void paintComponent(final Graphics G) {
        
        final Graphics2D g = (Graphics2D)G; 
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);        
        
        final Dimension size = getSize();
        final int wH = PREFERRED_WIDTH * size.height;
        final int hW = PREFERRED_HEIGHT * size.width;
        final int ox;
        final int oy;
        final int width;
        final int height;
        if (wH == hW) {
            ox = 0;
            oy = 0;
            width = size.width;
            height = size.height;
        } else if (wH < hW) {
            height = size.height;
            width = (int)Math.round(PREFERRED_WIDTH * height / (double)PREFERRED_HEIGHT);
            oy = 0;
            ox = (size.width - width) / 2;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, ox, height);
            g.fillRect(ox + width, 0, size.width - (ox + width), height);
        } else {
            width = size.width;
            height = (int)Math.round(PREFERRED_HEIGHT * width / (double)PREFERRED_WIDTH);
            ox = 0;
            oy = (size.height - height) / 2;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, oy);
            g.fillRect(0, oy + height, width, size.height - (oy + height));
        }
             
        g.translate(ox, oy);
        g.scale(width / (double)PREFERRED_WIDTH, height / (double)PREFERRED_HEIGHT);
        
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, PREFERRED_WIDTH, PREFERRED_HEIGHT);
        
        g.setColor(GRID_COLOR);
        g.setStroke(GRID_STROKE);
        g.draw(GRID_PATH);
        
        g.setColor(BORDER_COLOR);
        g.setStroke(BORDER_STROKE);
        g.draw(BORDER_SHAPE);
        
        for (int i = PLAYFIELD_HEIGHT - 1; i >= 0; --i) {
            float ty = PLAYFIELD_PADDING + i * CELL_SIZE;
            for (int j = PLAYFIELD_WIDTH - 1; j >= 0; --j) {
                int index = cells[i][j];
                if (index == 0) {
                    continue;
                }
                --index;
                float tx = PLAYFIELD_PADDING + j * CELL_SIZE;
                g.translate(tx, ty);
                g.setColor(BLOCK_HIGHLIGHT_COLORS[index]);
                g.fill(BLOCK_HIGHLIGHT_SHAPE);
                g.setColor(BLOCK_SHADOW_COLORS[index]);
                g.fill(BLOCK_SHADOW_SHAPE);
                
                final Paint paint = g.getPaint();
                g.setPaint(GRADIENT_PAINTS[index]);
                g.fill(BLOCK_CENTER_SHAPE);
                g.setPaint(paint);
                
                g.translate(-tx, -ty);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }
}
