package tetriscircuits.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import tetriscircuits.LockedTetrimino;
import tetriscircuits.Point;
import tetriscircuits.Rectangle;
import tetriscircuits.Structure;
import tetriscircuits.Tetrimino;

public class StructureRenderer {
    
    private static final Color[] TERMINAL_FILLS = {
        new Color(0x7F_000000, true), 
        new Color(0x7F_FFFFFF, true), 
    };   
    
    private static final Map<String, StructureRenderer> STRUCTURE_RENDERERS;
    
    static {
        final Map<String, StructureRenderer> structureRenderers = new HashMap<>();
        for (final Tetrimino[] tetriminos : Tetrimino.TETRIMINOS) {
            for (final Tetrimino ts : tetriminos) {
                structureRenderers.put(ts.getName(), new StructureRenderer(new Structure(new LockedTetrimino[] { 
                    new LockedTetrimino(ts, 0, 0) }, new Rectangle[0][], new Rectangle[0][], new boolean[0], 
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
        
        final LockedTetrimino[] lockedTetriminos = structure.getLockedTetriminos();
        for (int i = lockedTetriminos.length - 1; i >= 0; --i) {
            final LockedTetrimino lockedTetrimino = lockedTetriminos[i];
            TetriminoRenderer.fromTetrimino(lockedTetrimino.getTetrimino()).render(g, 
                    x + cellSize * (cellX + lockedTetrimino.getX()), 
                    y - cellSize * (cellY + lockedTetrimino.getY()), 
                    cellSize);
        }

        final Rectangle[][] inputs = structure.getInputs();
        final boolean[] testBits = structure.getTestBits();
        for (int i = inputs.length - 1; i >= 0; --i) {
            g.setColor(TERMINAL_FILLS[(i >= testBits.length) ? 0 : (testBits[i] ? 1 : 0)]);
            final Rectangle[] input = inputs[i];
            for (int j = input.length - 1; j >= 0; --j) {
                final Rectangle in = input[j];
                g.fillRect(
                    x + cellSize * (cellX + in.x), 
                    y - cellSize * (cellY + in.y), 
                    cellSize * in.width, cellSize * in.height);
            }                        
        }
        
        final Rectangle[][] outputs = structure.getOutputs();
        for (int i = outputs.length - 1; i >= 0; --i) {
            g.setColor(TERMINAL_FILLS[1]);
            final Rectangle[] output = outputs[i];
            for (int j = output.length - 1; j >= 0; --j) {
                final Rectangle out = output[j];
                g.fillRect(
                    x + cellSize * (cellX + out.x), 
                    y - cellSize * (cellY + out.y), 
                    cellSize * out.width, cellSize * out.height);
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
