package tetriscircuits.ui;

import java.awt.Color;
import java.awt.Graphics;
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
    
    public void render(final Graphics g, final int x, final int y, int cellSize) {
        
        final LockedElement[] lockedTetriminos = structure.getLockedTetriminos();
        for (int i = lockedTetriminos.length - 1; i >= 0; --i) {
            final LockedElement lockedTetrimino = lockedTetriminos[i];
            TetriminoRenderer.fromTetrimino(lockedTetrimino.getTetrimino()).render(g, 
                    x + cellSize * (cellX + lockedTetrimino.getX()), 
                    y - cellSize * (cellY + lockedTetrimino.getY()), 
                    cellSize);
        }
        
        renderTerminals(g, x, y, cellSize, structure.getInputs());
        renderTerminals(g, x, y, cellSize, structure.getOutputs());
    }
    
    private void renderTerminals(final Graphics g, final int x, final int y, int cellSize, 
            final TerminalRectangle[][] terminals) {
        
        for (int i = terminals.length - 1; i >= 0; --i) {            
            final TerminalRectangle[] terms = terminals[i];            
            for (int j = terms.length - 1; j >= 0; --j) {
                final TerminalRectangle terminal = terms[j];                
                final int px = x + cellSize * (cellX + terminal.x);
                final int py = y - cellSize * (cellY + terminal.y + 1);
                final int width = cellSize * terminal.width;
                final TerminalState state = terminal.getState();
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
    }
    
    public void repaint(final PlayfieldPanel panel, final int x, final int y, int cellSize) {
        final int width = cellSize * (structure.getMaxX() - structure.getMinX() + 1);
        final int height = cellSize * (structure.getMaxY() - structure.getMinY() + 1);              
        final int originX = x + cellSize * (cellX + structure.getMinX());
        final int originY = y - cellSize * (cellY + structure.getMaxY());
        panel.repaint(originX, originY, width, height);
    }
}
