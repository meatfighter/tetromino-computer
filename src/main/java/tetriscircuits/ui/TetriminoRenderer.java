package tetriscircuits.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import tetriscircuits.Point;
import tetriscircuits.Tetrimino;

public class TetriminoRenderer implements Icon {
    
    private static final int ICON_CELL_SIZE = 8;
    private static final int ICON_SIZE = 5 * ICON_CELL_SIZE;
    
    private static final boolean[][] MATRIX = new boolean[7][7];
    
    private static final Color[] COLORS = {
        new Color(0xB802FD),
        new Color(0x1801FF),
        new Color(0xFE103C),
        new Color(0xFFDE00),
        new Color(0x66FD00),
        new Color(0xFF7308),
        new Color(0x00E6FE),        
    };    
    
    public static final TetriminoRenderer TD = new TetriminoRenderer(Tetrimino.TD, COLORS[0]);
    public static final TetriminoRenderer TL = new TetriminoRenderer(Tetrimino.TL, COLORS[0]);
    public static final TetriminoRenderer TU = new TetriminoRenderer(Tetrimino.TU, COLORS[0]);
    public static final TetriminoRenderer TR = new TetriminoRenderer(Tetrimino.TR, COLORS[0]);
    
    public static final TetriminoRenderer JD = new TetriminoRenderer(Tetrimino.JD, COLORS[1]);
    public static final TetriminoRenderer JL = new TetriminoRenderer(Tetrimino.JL, COLORS[1]);
    public static final TetriminoRenderer JU = new TetriminoRenderer(Tetrimino.JU, COLORS[1]);
    public static final TetriminoRenderer JR = new TetriminoRenderer(Tetrimino.JR, COLORS[1]);
    
    public static final TetriminoRenderer ZH = new TetriminoRenderer(Tetrimino.ZH, COLORS[2]);
    public static final TetriminoRenderer ZV = new TetriminoRenderer(Tetrimino.ZV, COLORS[2]);
    
    public static final TetriminoRenderer OS = new TetriminoRenderer(Tetrimino.OS, COLORS[3]);
    
    public static final TetriminoRenderer SH = new TetriminoRenderer(Tetrimino.SH, COLORS[4]);
    public static final TetriminoRenderer SV = new TetriminoRenderer(Tetrimino.SV, COLORS[4]);
    
    public static final TetriminoRenderer LD = new TetriminoRenderer(Tetrimino.LD, COLORS[5]);
    public static final TetriminoRenderer LL = new TetriminoRenderer(Tetrimino.LL, COLORS[5]);
    public static final TetriminoRenderer LU = new TetriminoRenderer(Tetrimino.LU, COLORS[5]);
    public static final TetriminoRenderer LR = new TetriminoRenderer(Tetrimino.LR, COLORS[5]);
    
    public static final TetriminoRenderer IH = new TetriminoRenderer(Tetrimino.IH, COLORS[6]);
    public static final TetriminoRenderer IV = new TetriminoRenderer(Tetrimino.IV, COLORS[6]);
    
    public static final TetriminoRenderer[] T = { TD, TL, TU, TR };
    public static final TetriminoRenderer[] J = { JD, JL, JU, JR };
    public static final TetriminoRenderer[] Z = { ZH, ZV };
    public static final TetriminoRenderer[] O = { OS };
    public static final TetriminoRenderer[] S = { SH, SV };
    public static final TetriminoRenderer[] L = { LD, LL, LU, LR };
    public static final TetriminoRenderer[] I = { IH, IV };
    
    public static final TetriminoRenderer[][] TETRIMINO_RENDERERS = { T, J, Z, O, S, L, I };
    
    private static final Map<Tetrimino, TetriminoRenderer> RENDERERS;
    
    static {
        final Map<Tetrimino, TetriminoRenderer> map = new HashMap<>();
        for (final TetriminoRenderer[] renderers : TETRIMINO_RENDERERS) {
            for (final TetriminoRenderer renderer : renderers) {
                map.put(renderer.getTetrimino(), renderer);
            }
        }
        RENDERERS = Collections.unmodifiableMap(map);
    }
    
    public static TetriminoRenderer fromTetrimino(final Tetrimino tetrimino) {
        return RENDERERS.get(tetrimino);
    }
    
    private final Tetrimino tetrimino;    
    private final Color fillColor;
    private final Color lineColor;
    
    private final BlockFaces[] blockFaces;
    
    public TetriminoRenderer(final Tetrimino tetrimino, final Color color) {
        this.tetrimino = tetrimino;
        this.fillColor = color;
        this.lineColor = color.darker();
        
        for (int i = MATRIX.length - 1; i >= 0; --i) {
            final boolean[] row = MATRIX[i];
            for (int j = row.length - 1; j >= 0; --j) {
                row[j] = false;
            }
        }
        final Point[] blocks = tetrimino.getBlocks();
        for (Point block : blocks) {
            MATRIX[3 + block.y][3 + block.x] = true;
        }
        blockFaces = new BlockFaces[blocks.length];
        for (int i = blocks.length - 1; i >= 0; --i) {
            final Point block = blocks[i];
            blockFaces[i] = new BlockFaces(
                    !MATRIX[3 + block.y - 1][3 + block.x], 
                    !MATRIX[3 + block.y][3 + block.x + 1], 
                    !MATRIX[3 + block.y + 1][3 + block.x], 
                    !MATRIX[3 + block.y][3 + block.x - 1],
                    
                    MATRIX[3 + block.y][3 + block.x - 1] && MATRIX[3 + block.y - 1][3 + block.x] 
                            && !MATRIX[3 + block.y - 1][3 + block.x - 1],
                    MATRIX[3 + block.y][3 + block.x + 1] && MATRIX[3 + block.y - 1][3 + block.x] 
                            && !MATRIX[3 + block.y - 1][3 + block.x + 1],
                    MATRIX[3 + block.y][3 + block.x - 1] && MATRIX[3 + block.y + 1][3 + block.x] 
                            && !MATRIX[3 + block.y + 1][3 + block.x - 1],
                    MATRIX[3 + block.y][3 + block.x + 1] && MATRIX[3 + block.y + 1][3 + block.x] 
                            && !MATRIX[3 + block.y + 1][3 + block.x + 1]);
        }
    }

    public Tetrimino getTetrimino() {
        return tetrimino;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public BlockFaces[] getBlockFaces() {
        return blockFaces;
    }
        
    public void render(final Graphics g, final int x, final int y, int cellSize) {
        
        final Point[] blocks = tetrimino.getBlocks();        
        for (int i = blocks.length - 1; i >= 0; --i) {
            final Point block = blocks[i];
            final int ox = x - (cellSize >> 1) + cellSize * block.x;
            final int oy = y - (cellSize >> 1) + cellSize * block.y;
            g.setColor(fillColor);
            g.fillRect(ox, oy, cellSize, cellSize);
            if (cellSize >= 4) {
                g.setColor(lineColor);
                final BlockFaces faces = blockFaces[i];
                
                if (faces.top) {
                    g.drawLine(ox, oy, ox + cellSize - 1, oy);
                }
                if (faces.left) {
                    g.drawLine(ox, oy, ox, oy + cellSize - 1);
                }
                if (faces.bottom) {
                    g.drawLine(ox, oy + cellSize - 1, ox + cellSize - 1, oy + cellSize - 1);
                }
                if (faces.right) {
                    g.drawLine(ox + cellSize - 1, oy, ox + cellSize - 1, oy + cellSize - 1);                    
                } 
             
                if (faces.topLeft) {
                    g.fillRect(ox, oy, 1, 1);
                }
                if (faces.topRight) {
                    g.fillRect(ox + cellSize - 1, oy, 1, 1);
                }
                if (faces.bottomLeft) {
                    g.fillRect(ox, oy + cellSize - 1, 1, 1);
                }
                if (faces.bottomRight) {
                    g.fillRect(ox + cellSize - 1, oy + cellSize - 1, 1, 1);
                }
            }
        }        
    }

    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        
        final int origin = ICON_SIZE >> 1;
        
        render(g, origin + x, origin + y, ICON_CELL_SIZE);
    }

    @Override
    public int getIconWidth() {
        return ICON_SIZE;
    }

    @Override
    public int getIconHeight() {
        return ICON_SIZE;
    }
}
