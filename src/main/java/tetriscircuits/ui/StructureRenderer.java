package tetriscircuits.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import tetriscircuits.LockedElement;
import tetriscircuits.TerminalRectangle;
import tetriscircuits.Structure;
import tetriscircuits.TerminalState;
import tetriscircuits.Tetrimino;

public class StructureRenderer {
    
    private static final Color TERMINAL_FILL = new Color(0xFFFFF0);
    private static final Color TERMINAL_LINE = TERMINAL_FILL.darker();
    
    private static final Color TEXT_COLOR = Color.BLACK;
    
    private static final Map<String, StructureRenderer> STRUCTURE_RENDERERS;
    
    static {
        final Map<String, StructureRenderer> structureRenderers = new HashMap<>();
        for (final Tetrimino[] tetriminos : Tetrimino.TETRIMINOS) {
            for (final Tetrimino ts : tetriminos) {
                structureRenderers.put(ts.getName(), new StructureRenderer(new Structure(new LockedElement[] { 
                    new LockedElement(ts, 0, 0) }, new TerminalRectangle[0][], new TerminalRectangle[0][], new boolean[0], 
                        -2, 2, -2, 2)));
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
        
        final LockedElement[] lockedElements = structure.getLockedTetriminos();
        for (int i = lockedElements.length - 1; i >= 0; --i) {
            final LockedElement lockedElement = lockedElements[i];
            final String componentName = lockedElement.getComponentName();
            if (componentName == null) {
                TetriminoRenderer.fromTetrimino(lockedElement.getTetrimino()).render(g, 
                        x + cellSize * (cellX + lockedElement.getX()), 
                        y - cellSize * (cellY + lockedElement.getY()), 
                        cellSize);
            } 
        }
        
        final int inputsY = renderTerminals(g, x, y, cellSize, structure.getInputs());
        final int outputsY = renderTerminals(g, x, y, cellSize, structure.getOutputs());
        int averageY = 0;
        int countY = 0;
        if (inputsY != 0) {
            averageY += inputsY;
            ++countY;
        }
        if (outputsY != 0) {
            averageY += outputsY;
            ++countY;
        }
        if (countY == 0) {
            averageY = y;
        } else {
            averageY /= countY;
        }
        averageY += cellSize >> 1;
        
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        for (int i = lockedElements.length - 1; i >= 0; --i) {
            final LockedElement lockedElement = lockedElements[i];
            final String componentName = lockedElement.getComponentName();
            if (componentName != null) {
                g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (cellSize * 5) >> 3));
                final FontMetrics fontMetrics = g.getFontMetrics();
                final Rectangle2D rect = fontMetrics.getStringBounds(componentName, g);                  
                final int width = (int)rect.getWidth();
                final int height = (int)rect.getHeight();
                final int rectX = x - (width >> 1);
                g.setColor(TERMINAL_FILL);
                g.fillRect(rectX, averageY, width, height);
                g.setColor(TERMINAL_LINE);
                g.drawRect(rectX, averageY, width, height);
                g.setColor(TEXT_COLOR);
                g.drawString(componentName, rectX, averageY + fontMetrics.getAscent());
            }
        }
    }
    
    private int renderTerminals(final Graphics g, final int x, final int y, int cellSize, 
            final TerminalRectangle[][] terminals) {
        
        int averageY = 0;
        int countY = 0;
        
        for (int i = terminals.length - 1; i >= 0; --i) {            
            final TerminalRectangle[] terms = terminals[i];            
            for (int j = terms.length - 1; j >= 0; --j) {
                final TerminalRectangle terminal = terms[j];                
                final int px = x + cellSize * (cellX + terminal.x);
                final int py = y - cellSize * (cellY + terminal.y + 1);
                final int width = cellSize * terminal.width;
                final TerminalState state = terminal.getState();
                averageY += py;
                ++countY;
                if (state == TerminalState.ZERO) {
                    g.setColor(TERMINAL_FILL);
                    g.fillRect(px, py + cellSize, width, cellSize);
                } else if (state == TerminalState.ONE) {
                    g.setColor(TERMINAL_FILL);
                    g.fillRect(px, py, width, cellSize);
                }
                g.setColor(TERMINAL_LINE);
                g.drawRect(px, py, width, 2 * cellSize);
            }
        }  
        
        if (countY > 0) {
            averageY /= countY;
        }
        return averageY;
    }
    
    public void repaint(final PlayfieldPanel panel, final int x, final int y, int cellSize) {
        final int width = cellSize * (structure.getMaxX() - structure.getMinX() + 1);
        final int height = cellSize * (structure.getMaxY() - structure.getMinY() + 1);              
        final int originX = x + cellSize * (cellX + structure.getMinX());
        final int originY = y - cellSize * (cellY + structure.getMaxY());
        panel.repaint(originX, originY, width + 1, height + 1);
    }
}
