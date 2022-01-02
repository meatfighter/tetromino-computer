package tetriscircuits.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import tetriscircuits.LockedTetrimino;
import tetriscircuits.Point;
import tetriscircuits.Structure;
import tetriscircuits.Tetrimino;

public class StructureRenderer {
    
    private static final Color INPUT_FILL = new Color(0xDDEEFF);
    private static final Color OUTPUT_FILL = new Color(0x7F000000, true);    
    
    private static final Map<String, StructureRenderer> STRUCTURE_RENDERERS;
    
    static {
        final Map<String, StructureRenderer> structureRenderers = new HashMap<>();
        for (final Tetrimino[] tetriminos : Tetrimino.TETRIMINOS) {
            for (final Tetrimino ts : tetriminos) {
                structureRenderers.put(ts.getName(), new StructureRenderer(new Structure(new LockedTetrimino[] { 
                    new LockedTetrimino(ts, 0, 0) }, new Point[0], new Point[0], -2, 2, -2, 2)));
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
        
        final Point[] inputs = structure.getInputs();
        for (int i = inputs.length - 1; i >= 0; --i) {
            final Point input = inputs[i];
            g.setColor(INPUT_FILL);
            g.fillRect(
                    x + cellSize * (cellX + input.x), 
                    y - cellSize * (cellY + input.y), 
                    cellSize, cellSize);
        }        
        
        final LockedTetrimino[] lockedTetriminos = structure.getLockedTetriminos();
        for (int i = lockedTetriminos.length - 1; i >= 0; --i) {
            final LockedTetrimino lockedTetrimino = lockedTetriminos[i];
            TetriminoRenderer.fromTetrimino(lockedTetrimino.getTetrimino()).render(g, 
                    x + cellSize * (cellX + lockedTetrimino.getX()), 
                    y - cellSize * (cellY + lockedTetrimino.getY()), 
                    cellSize);
        }
        
        final Point[] outputs = structure.getOutputs();
        for (int i = outputs.length - 1; i >= 0; --i) {
            final Point output = outputs[i];
            g.setColor(OUTPUT_FILL);
            g.fillRect(
                    x + cellSize * (cellX + output.x), 
                    y - cellSize * (cellY + output.y), 
                    cellSize, cellSize);
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
