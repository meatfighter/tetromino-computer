package tetriscircuits.ui;

import java.io.OutputStream;
import java.io.PrintStream;
import tetriscircuits.Structure;
import tetriscircuits.Tetrimino;

public class SvgGenerator {

    public void generate(final OutputStream out, final Structure struct, final double margin, final double cellSize) {
        try (final PrintStream o = new PrintStream(out)) {
            generate(o, struct, margin, cellSize);
        }
    }
    
    private void generate(final PrintStream out, final Structure struct, final double margin, final double cellSize) {
        
        final int maxX = struct.getMaxX();
        final int minX = struct.getMinX();
        final int maxY = struct.getMaxY();
        final double cellsWidth = maxX - minX + 1;
        final double cellsHeight = maxY + 1;
        final double gridWidth = cellSize * cellsWidth;
        final double gridHeight = cellSize * cellsHeight;
        final double gridX = margin;
        final double gridY = margin;
        final int svgWidth = (int)Math.round(2 * margin + gridWidth);
        final int svgHeight = (int)Math.round(2 * margin + gridHeight);

        out.println("<?xml version=\"1.0\"?>");
        out.println("<?xml-stylesheet type=\"text/css\" href=\"test.css\"?>");
        out.format("<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\" "
                + "xmlns:xlink=\"http://www.w3.org/1999/xlink\">%n", svgWidth, svgHeight);
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
                    final double x = cellSize * (p.getX() - 3.5) - (p.isRight() ? 1 : 0);
                    final double y = cellSize * (p.getY() - 3.5) - (p.isBottom() ? 1 : 0);
                    out.format("%s,%s", toString(x), toString(y));
                }
                out.format("\" class=\"%s\"/>%n", tetrimino.getGroupName());
            }
        }
        out.println("    </defs>");
        for (int x = minX; x <= maxX + 1; ++x) {
            final double lineX = gridX + cellSize * (x - minX);
            out.format("    <line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" class=\"grid\"/>%n", toString(lineX), 
                    toString(gridY), toString(lineX), toString(gridY + gridHeight));
        }
        for (int y = 0; y <= maxY + 1; ++y) {
            final double lineY = gridY + cellSize * y;
            out.format("    <line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" class=\"grid\"/>%n", toString(gridX), 
                    toString(lineY), toString(gridX + gridWidth), toString(lineY));
        }        
        
        final double ox = gridX + gridWidth / 2.0;
        final double oy = gridY + gridHeight;
        for (final Structure s : struct.getStructures()) {
            final Tetrimino tetrimino = s.getTetrimino();
            final double x = s.getX() - 0.5;
            final double y = s.getY() - 1.5;
            out.format("    <use href=\"#%s\" x=\"%s\" y=\"%s\"/>%n", tetrimino.getName(), toString(ox + cellSize * x), 
                    toString(oy - cellSize * y));
        }
        out.println("</svg>");        
    }
    
    private String toString(final double x) {
        if ((int)x == x) {
            return Integer.toString((int)x);
        } 
        return String.format("%s", x);
    }    
}
