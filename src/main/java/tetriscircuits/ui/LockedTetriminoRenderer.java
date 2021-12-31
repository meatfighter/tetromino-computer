package tetriscircuits.ui;

import java.awt.Graphics;

public class LockedTetriminoRenderer {
    
    private final TetriminoRenderer tetriminoRenderer;
    private final int cellX;
    private final int cellY;
    
    public LockedTetriminoRenderer(final TetriminoRenderer tetriminoRenderer, final int cellX, final int cellY) {
        this.tetriminoRenderer = tetriminoRenderer;
        this.cellX = cellX;
        this.cellY = cellY;
    }

    public TetriminoRenderer getTetriminoRenderer() {
        return tetriminoRenderer;
    }

    public int getCellX() {
        return cellX;
    }

    public int getCellY() {
        return cellY;
    }

    public void render(final Graphics g, final int x, final int y, int cellSize) {
        tetriminoRenderer.render(g, x + cellSize * cellX, y + cellSize * cellY, cellSize);
    }
}
