package tetriscircuits.web;

import java.io.PrintStream;
import tetriscircuits.Tetrimino;
import tetriscircuits.ui.PathPoint;
import tetriscircuits.ui.TetriminoPath;

public class GenerateOrientationTable {
    
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
        
        final double svgHeight = 2 * MARGIN 
                + Tetrimino.TETRIMINOS.length * (5 * CELL_SIZE + FONT_SIZE + FONT_GAP)
                + (Tetrimino.TETRIMINOS.length - 1) * VERTICAL_SPACER - 4;
        
        final double svgWidth = 2 * MARGIN
                + 4 * (5 * CELL_SIZE + HORIZONTAL_SPACER) + 5;
        
        out.println("<?xml version=\"1.0\"?>");
        out.println("<?xml-stylesheet type=\"text/css\" href=\"svg.css\"?>");
        out.format("<svg width=\"%s\" height=\"%s\" xmlns=\"http://www.w3.org/2000/svg\" "
                + "xmlns:xlink=\"http://www.w3.org/1999/xlink\">%n", toString(svgWidth), toString(svgHeight));
        out.println("    <defs>");
        for (final TetriminoPath[] paths : TetriminoPath.TETRIMINO_PATHS) {
            for (final TetriminoPath path : paths) {
                final Tetrimino tetrimino = path.getTetrimino();
                final PathPoint[] points = path.getPoints();
                out.format("        <polygon id=\"%s\" points=\"", tetrimino.getName());
                for (int i = 0; i < points.length; ++i) {
                    final PathPoint p = points[i];
                    if (i != 0) {
                        out.print(" ");
                    }
                    final double x = CELL_SIZE * (p.getX() - 3.5) - (p.isRight() ? 1 : 0);
                    final double y = CELL_SIZE * (p.getY() - 3.5) - (p.isBottom() ? 1 : 0);
                    out.format("%s,%s", toString(x), toString(y));
                }
                out.format("\" class=\"%s\"/>%n", tetrimino.getGroupName());
            }
        }
        out.println("    </defs>");        
        
        out.format("    <g style=\"font-size: %dpx;\">%n", (int)(CELL_SIZE * 2.0 / 3.0));
        for (int i = 0; i < Tetrimino.TETRIMINOS.length; ++i) {
            for (int j = 0; j < Tetrimino.TETRIMINOS[i].length; ++j) {
                final Tetrimino tetrimino = Tetrimino.TETRIMINOS[i][j];
                final double ox = MARGIN + j * (5 * CELL_SIZE + ((j != 0) ? HORIZONTAL_SPACER : 0)) + LEFT_MARGIN;
                final double oy = MARGIN + i * (5 * CELL_SIZE + FONT_SIZE + FONT_GAP 
                            + ((i != 0) ? VERTICAL_SPACER : 0)) + TOP_MARGIN;
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
                out.format("        <use href=\"#%s\" x=\"%s\" y=\"%s\"/>%n", tetrimino.getName(), 
                            toString(ox + 2.5 * CELL_SIZE), toString(oy + 2.5 * CELL_SIZE));
                out.format("        <circle cx=\"%s\" cy=\"%s\" r=\"%s\" class=\"grid-center\"/>%n", 
                        toString(ox + 2.5 * CELL_SIZE - 0.5), toString(oy + 2.5 * CELL_SIZE - 0.5), CELL_SIZE / 4.0);
                out.format("        <text transform=\"translate(%s %s)\" class=\"axes\" "
                                + "dominant-baseline=\"middle\" text-anchor=\"middle\">%s</text>%n", 
                                toString(ox + 2.5 * CELL_SIZE), toString(oy - FONT_SIZE - FONT_GAP + 12), 
                                tetrimino.getName());
            }            
        }
        out.println("    </g>");
        out.format("    <g style=\"font-size: %dpx;\">%n", (int)(CELL_SIZE));
        for (int i = 0; i < Tetrimino.TETRIMINOS.length; ++i) {
            final String name = Tetrimino.TETRIMINOS[i][0].getGroupName().toUpperCase();
            final double ox = MARGIN + LEFT_MARGIN - 7;
            final double oy = MARGIN + i * (5 * CELL_SIZE + FONT_SIZE + FONT_GAP 
                            + ((i != 0) ? VERTICAL_SPACER : 0)) + TOP_MARGIN + 2.5 * CELL_SIZE;
            out.format("        <text transform=\"translate(%s %s)\" class=\"label\" "
                                + "dominant-baseline=\"middle\" text-anchor=\"end\">%s</text>%n", 
                                toString(ox), toString(oy), 
                                name);
            
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
        new GenerateOrientationTable().launch();
    }
}
