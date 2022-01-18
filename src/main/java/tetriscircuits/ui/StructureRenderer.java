package tetriscircuits.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import tetriscircuits.TerminalRectangle;
import tetriscircuits.Structure;
import tetriscircuits.TerminalState;
import tetriscircuits.Tetrimino;

public class StructureRenderer {
    
    private static final Color TERMINAL_FILL = new Color(0xFFFFF0);
    private static final Color TERMINAL_LINE = TERMINAL_FILL.darker();
    
    private static final Color COMPONENT_FILL = new Color(0x7F000000, true);
    
    private static final Color TEXT_COLOR = TERMINAL_FILL;
    
    private static final Map<String, StructureRenderer> STRUCTURE_RENDERERS;
    
    private static final AffineTransform ROTATION90 = AffineTransform.getQuadrantRotateInstance(3);
    
    static {
        final Map<String, StructureRenderer> structureRenderers = new HashMap<>();
        for (final Tetrimino[] tetriminos : Tetrimino.TETRIMINOS) {
            for (final Tetrimino tetrimino : tetriminos) {
                structureRenderers.put(tetrimino.getName(), new StructureRenderer(new Structure(tetrimino, 0, 0)));
            }
        }
        STRUCTURE_RENDERERS = Collections.unmodifiableMap(structureRenderers);
    }
    
    private final Structure structure;
    private final int cellX;
    private final int cellY;

    public Structure getStructure() {
        return structure;
    }

    public int getCellX() {
        return cellX;
    }

    public int getCellY() {
        return cellY;
    }
    
    public static StructureRenderer fromTetrimino(final String tetriminoName) {
        return STRUCTURE_RENDERERS.get(tetriminoName);
    }

    public StructureRenderer(final Structure structure) {
        this(structure, 0, 0);
    }
    
    public StructureRenderer(final Structure structure, final int cellX, final int cellY) {
        this.structure = structure;
        this.cellX = cellX;
        this.cellY = cellY;
    }
    
    public void render(final Graphics2D g, final int x, final int y, int cellSize) {
        
        if (structure.getTetrimino() != null) {
            TetriminoRenderer.fromTetrimino(structure.getTetrimino()).render(g, 
                    x + cellSize * (cellX + structure.getX()), 
                    y - cellSize * (cellY + structure.getY()), 
                    cellSize);
            return;
        }
        
        final Structure[] structures = structure.getStructures();
        if (structures == null) {
            return;
        }
        
        final Font normalFont = new Font(Font.MONOSPACED, Font.PLAIN, (cellSize << 1) / 3);
        final Font rotatedFont = normalFont.deriveFont(ROTATION90);
        g.setFont(normalFont);                
        
        final FontMetrics fontMetrics = g.getFontMetrics();           
                
        for (int i = structures.length - 1; i >= 0; --i) {
            final Structure struct = structures[i];
            final Tetrimino tetrimino = struct.getTetrimino();
            if (tetrimino == null) {                
                final int fillHeight = cellSize * (struct.getMaxY() - struct.getMinY() + 1);
                final int fillWidth = cellSize * (struct.getMaxX() - struct.getMinX() + 1);
                final int fillX = x + cellSize * (cellX + struct.getX() + struct.getMinX());
                final int fillY = y - cellSize * (cellY + struct.getY() - struct.getMinY() - 1) - fillHeight;                
                g.setColor(COMPONENT_FILL);                
                g.fillRect(fillX, fillY + cellSize, fillWidth, fillHeight - (cellSize << 1));
                final Rectangle2D nameBounds = fontMetrics.getStringBounds(struct.getComponentName(), g);
                g.setColor(TEXT_COLOR);
                if (nameBounds.getWidth() >= fillWidth - 10) {                    
                    g.setFont(rotatedFont);
                    g.drawString(struct.getComponentName(), fillX + (fillWidth >> 1) + fontMetrics.getDescent(), 
                            fillY + fillHeight - ((fillHeight - (int)nameBounds.getWidth()) >> 1));
                    g.setFont(normalFont);
                } else {
                    g.drawString(struct.getComponentName(), fillX + ((fillWidth - (int)nameBounds.getWidth()) >> 1), 
                            fillY + (fillHeight >> 1) + fontMetrics.getDescent());
                }
                renderTerminals(g, x, y, cellSize, struct.getInputs(), cellX + struct.getX(), cellY + struct.getY());
                renderTerminals(g, x, y, cellSize, struct.getOutputs(), cellX + struct.getX(), cellY + struct.getY());
            }
        }

        renderTerminals(g, x, y, cellSize, structure.getInputs(), cellX, cellY);
        renderTerminals(g, x, y, cellSize, structure.getOutputs(), cellX, cellY);
        
        for (int i = structures.length - 1; i >= 0; --i) {
            final Structure struct = structures[i];
            final Tetrimino tetrimino = struct.getTetrimino();
            if (tetrimino != null) {
                TetriminoRenderer.fromTetrimino(tetrimino).render(g, 
                        x + cellSize * (cellX + struct.getX()), 
                        y - cellSize * (cellY + struct.getY()), 
                        cellSize);
            } 
        }   
    }
    
    private void renderTerminals(final Graphics g, final int x, final int y, int cellSize, 
            final TerminalRectangle[][] terminals, final int offsetX, final int offsetY) {
        
        if (terminals == null) {
            return;
        }
        
        for (int i = terminals.length - 1; i >= 0; --i) {            
            final TerminalRectangle[] terms = terminals[i];            
            for (int j = terms.length - 1; j >= 0; --j) {
                final TerminalRectangle terminal = terms[j];                
                final int px = x + cellSize * (offsetX + terminal.x);
                final int py = y - cellSize * (offsetY + terminal.y + 1);
                final int width = cellSize * terminal.width;
                final TerminalState state = terminal.getState();
                if (state == TerminalState.ZERO) {
                    g.setColor(TERMINAL_FILL);
                    g.fillRect(px, py + cellSize, width, cellSize);
                } else if (state == TerminalState.ONE) {
                    g.setColor(TERMINAL_FILL);
                    g.fillRect(px, py, width, cellSize << 1);
                }
                g.setColor(TERMINAL_LINE);
                g.drawRect(px, py, width - 1, 2 * cellSize - 1);
            }
        }  
    }
    
    public void repaint(final PlayfieldPanel panel, final int x, final int y, int cellSize) {
        final int width = cellSize * (structure.getMaxX() - structure.getMinX() + 1);
        final int height = cellSize * (structure.getMaxY() - structure.getMinY() + 1);              
        final int originX = x + cellSize * (cellX + structure.getMinX());
        final int originY = y - cellSize * (cellY + structure.getMaxY());
        panel.repaint(originX, originY, width + 1, height + 1);
    }
}
