package tetrominocomputer.web;

import java.io.PrintStream;
import tetrominocomputer.Tetromino;
import tetrominocomputer.ui.PathPoint;
import tetrominocomputer.ui.TetrominoPath;

public class GenerateOrientationTable2 {
    
    public static final double CELL_SIZE = 20;
    public static final double MARGIN = 15.5;
    public static final double FONT_SIZE = 20;
    public static final double FONT_GAP = 4;
    public static final double VERTICAL_SPACER = 15;
    public static final double HORIZONTAL_SPACER = 15;
    public static final double TOP_MARGIN = 18;
    public static final double LEFT_MARGIN = 18;

    public void launch() throws Exception {
        
        final PrintStream out = System.out;
        
        final double svgHeight = 2 * MARGIN + FONT_SIZE + FONT_GAP
                + Tetromino.TETROMINOES.length * (5 * CELL_SIZE)
                + (Tetromino.TETROMINOES.length - 1) * VERTICAL_SPACER - 4;
        
        final double svgWidth = 2 * MARGIN
                + 4 * (5 * CELL_SIZE + HORIZONTAL_SPACER) + 5;
        
        out.println("<?xml version=\"1.0\"?>");
        out.println("<?xml-stylesheet type=\"text/css\" href=\"svg.css\"?>");
        out.format("<svg width=\"%s\" height=\"%s\" xmlns=\"http://www.w3.org/2000/svg\" "
                + "xmlns:xlink=\"http://www.w3.org/1999/xlink\">%n", toString(svgWidth), toString(svgHeight));
        out.println("    <defs>");
        for (final TetrominoPath[] paths : TetrominoPath.TETROMINO_PATHS) {
            for (final TetrominoPath path : paths) {
                final Tetromino tetromino = path.getTetromino();
                final PathPoint[] points = path.getPoints();
                out.format("        <polygon id=\"%s\" points=\"", tetromino.getName());
                for (int i = 0; i < points.length; ++i) {
                    final PathPoint p = points[i];
                    if (i != 0) {
                        out.print(" ");
                    }
                    final double x = CELL_SIZE * (p.getX() - 3.5) - (p.isRight() ? 1 : 0);
                    final double y = CELL_SIZE * (p.getY() - 3.5) - (p.isBottom() ? 1 : 0);
                    out.format("%s,%s", toString(x), toString(y));
                }
                out.format("\" class=\"%s\"/>%n", tetromino.getGroupName());
            }
        }
        out.println("    </defs>");        
        
        out.format("    <g style=\"font-size: %dpx;\">%n", (int)(0.8 * CELL_SIZE));
        for (int i = 0; i < Tetromino.TETROMINOES.length; ++i) {
            for (int j = 0; j < 4; ++j) {
                final Tetromino tetromino = Tetromino.TETROMINOES[i][j % Tetromino.TETROMINOES[i].length];
                final double ox = MARGIN + j * (5 * CELL_SIZE + ((j != 0) ? HORIZONTAL_SPACER : 0)) + LEFT_MARGIN;
                final double oy = MARGIN + i * (5 * CELL_SIZE + ((i != 0) ? VERTICAL_SPACER : 0)) + TOP_MARGIN;
                for (int y = 0; y < 6; ++y) {                    
                    out.format("        <line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" class=\"grid\"/>%n", 
                            toString(ox), toString(oy + CELL_SIZE * y), 
                            toString(ox + 5 * CELL_SIZE), toString(oy + CELL_SIZE * y));
                }
                for (int x = 0; x < 6; ++x) {
                    out.format("        <line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" class=\"grid\"/>%n", 
                            toString(ox + CELL_SIZE * x), toString(oy),
                            toString(ox + CELL_SIZE * x), toString(oy + 5 * CELL_SIZE));
                }
                out.format("        <use href=\"#%s\" x=\"%s\" y=\"%s\"/>%n", tetromino.getName(), 
                            toString(ox + 2.5 * CELL_SIZE), toString(oy + 2.5 * CELL_SIZE));
                out.format("        <circle cx=\"%s\" cy=\"%s\" r=\"%s\" class=\"grid-center\"/>%n", 
                        toString(ox + 2.5 * CELL_SIZE - 0.5), toString(oy + 2.5 * CELL_SIZE - 0.5), CELL_SIZE / 4.0);
                if (i == 0) {
                    out.format("        <text x=\"%s\" y=\"%s\" dy=\"0.25em\" class=\"axes\" "
                            + "text-anchor=\"middle\">%s</text>%n", toString(ox + 2.5 * CELL_SIZE), 
                            toString(oy - FONT_SIZE - FONT_GAP + 12), Integer.toString(j));
                }
            }            
        }
        out.println("    </g>");
        out.format("    <g style=\"font-size: %dpx;\">%n", (int)(0.8 * CELL_SIZE));
        for (int i = 0; i < Tetromino.TETROMINOES.length; ++i) {
            final String name = Integer.toString(i);
            final double ox = MARGIN + LEFT_MARGIN - 7;
            final double oy = MARGIN + i * (5 * CELL_SIZE + ((i != 0) ? VERTICAL_SPACER : 0)) 
                    + TOP_MARGIN + 2.5 * CELL_SIZE;
            out.format("        <text x=\"%s\" y=\"%s\" dy=\"0.25em\" class=\"axes\" text-anchor=\"end\">%s</text>%n", 
                    toString(ox), toString(oy), name);            
        }
        out.println("    </g>");
        
        out.println("</svg>");
    }
    
    private String toString(final double x) {
        if ((int)x == x) {
            return Integer.toString((int)x);
        } 
        return String.format("%s", x);
    }    
    
    public static void main(final String... args) throws Exception {
        new GenerateOrientationTable2().launch();
    }
}
