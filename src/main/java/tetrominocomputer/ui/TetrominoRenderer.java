package tetrominocomputer.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import tetrominocomputer.Point;
import tetrominocomputer.Tetromino;

public final class TetrominoRenderer implements Icon {
    
    private static final int ICON_CELL_SIZE = 5;
    private static final int ICON_SIZE = 5 * ICON_CELL_SIZE;
    
    private static final boolean[][] MATRIX = new boolean[7][7];
    
    public static final int[] BLOCK_RGBS = {
        0xBA08FF,
        0x0072FF,
        0xFF113C,
        0xFFDE00,
        0x68FF06,
        0xFF7308,
        0x04E6FF,
        0xFFFFF0,
    };

    public static final int[] DARK_BLOCK_RGBS = {
        0x8D00D3,
        0x004ED3,
        0xC9001A,
        0xAD9600,
        0x00B100,
        0xC03F00,
        0x00A0B7,
        0xABAB9D,
    };    
    
    private static final Color[] COLORS = new Color[16];
    private static final Color[] DARK_COLORS = new Color[16];
    static {
        for (int i = 0, j = 0; i < COLORS.length; ++i) {
            COLORS[i] = new Color(BLOCK_RGBS[j]);
            DARK_COLORS[i] = new Color(DARK_BLOCK_RGBS[j]);
            if (j + 1 < BLOCK_RGBS.length) {
                ++j;
            }
        }
    }
    
    public static final TetrominoRenderer TD = new TetrominoRenderer(Tetromino.TD, 0);
    public static final TetrominoRenderer TL = new TetrominoRenderer(Tetromino.TL, 0);
    public static final TetrominoRenderer TU = new TetrominoRenderer(Tetromino.TU, 0);
    public static final TetrominoRenderer TR = new TetrominoRenderer(Tetromino.TR, 0);
    
    public static final TetrominoRenderer JD = new TetrominoRenderer(Tetromino.JD, 1);
    public static final TetrominoRenderer JL = new TetrominoRenderer(Tetromino.JL, 1);
    public static final TetrominoRenderer JU = new TetrominoRenderer(Tetromino.JU, 1);
    public static final TetrominoRenderer JR = new TetrominoRenderer(Tetromino.JR, 1);
    
    public static final TetrominoRenderer ZH = new TetrominoRenderer(Tetromino.ZH, 2);
    public static final TetrominoRenderer ZV = new TetrominoRenderer(Tetromino.ZV, 2);
    
    public static final TetrominoRenderer OS = new TetrominoRenderer(Tetromino.OS, 3);
    
    public static final TetrominoRenderer SH = new TetrominoRenderer(Tetromino.SH, 4);
    public static final TetrominoRenderer SV = new TetrominoRenderer(Tetromino.SV, 4);
    
    public static final TetrominoRenderer LD = new TetrominoRenderer(Tetromino.LD, 5);
    public static final TetrominoRenderer LL = new TetrominoRenderer(Tetromino.LL, 5);
    public static final TetrominoRenderer LU = new TetrominoRenderer(Tetromino.LU, 5);
    public static final TetrominoRenderer LR = new TetrominoRenderer(Tetromino.LR, 5);
    
    public static final TetrominoRenderer IH = new TetrominoRenderer(Tetromino.IH, 6);
    public static final TetrominoRenderer IV = new TetrominoRenderer(Tetromino.IV, 6);
    
    public static final TetrominoRenderer[] T = { TD, TL, TU, TR };
    public static final TetrominoRenderer[] J = { JD, JL, JU, JR };
    public static final TetrominoRenderer[] Z = { ZH, ZV };
    public static final TetrominoRenderer[] O = { OS };
    public static final TetrominoRenderer[] S = { SH, SV };
    public static final TetrominoRenderer[] L = { LD, LL, LU, LR };
    public static final TetrominoRenderer[] I = { IH, IV };
    
    public static final TetrominoRenderer[][] TETROMINO_RENDERERS = { T, J, Z, O, S, L, I };
    
    private static final Map<Tetromino, TetrominoRenderer> RENDERERS;
    
    static {
        final Map<Tetromino, TetrominoRenderer> map = new HashMap<>();
        for (final TetrominoRenderer[] renderers : TETROMINO_RENDERERS) {
            for (final TetrominoRenderer renderer : renderers) {
                map.put(renderer.getTetromino(), renderer);
            }
        }
        RENDERERS = Collections.unmodifiableMap(map);
    }
    
    public static TetrominoRenderer fromTetromino(final Tetromino tetromino) {
        return RENDERERS.get(tetromino);
    }
    
    public static void renderBlock(final Graphics g, final int x, final int y, int colorIndex, 
            int cellSize) {
        
        g.setColor(COLORS[colorIndex]);
        g.fillRect(x, y, cellSize, cellSize);
        g.setColor(DARK_COLORS[colorIndex]);
        g.drawRect(x, y, cellSize, cellSize);
    }
    
    private final Tetromino tetromino;    
    private final Color fillColor;
    private final Color lineColor;
    
    private final BlockFaces[] blockFaces;
    
    private TetrominoRenderer(final Tetromino tetromino, final int colorIndex) {
        
        this.tetromino = tetromino;
        this.fillColor = COLORS[colorIndex];
        this.lineColor = DARK_COLORS[colorIndex];
        
        for (int i = MATRIX.length - 1; i >= 0; --i) {
            final boolean[] row = MATRIX[i];
            for (int j = row.length - 1; j >= 0; --j) {
                row[j] = false;
            }
        }
        final Point[] blocks = tetromino.getBlocks();
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

    public Tetromino getTetromino() {
        return tetromino;
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
        final Point[] blocks = tetromino.getBlocks();        
        for (int i = blocks.length - 1; i >= 0; --i) {
            final Point block = blocks[i];
            final int ox = x + cellSize * block.x;
            final int oy = y + cellSize * block.y;
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
        
        final int origin = (ICON_SIZE - ICON_CELL_SIZE) >> 1;
        
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
