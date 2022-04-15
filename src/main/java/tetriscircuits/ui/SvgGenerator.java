package tetriscircuits.ui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.PrintStream;
import tetriscircuits.Structure;
import tetriscircuits.TerminalRectangle;
import tetriscircuits.TerminalState;
import tetriscircuits.Tetrimino;

public class SvgGenerator {

    public void generate(
            final OutputStream out, 
            final Structure struct, 
            final double margin, 
            final double cellSize,
            final boolean renderGrid,
            final boolean renderInputTerminals,
            final boolean renderOutputTerminals,
            final boolean renderTetriminos,
            final boolean renderYAxis,
            final boolean renderStructures) {
        try (final PrintStream o = new PrintStream(out)) {
            generate(o, struct, margin, cellSize, renderGrid, renderInputTerminals, renderOutputTerminals, 
                    renderTetriminos, renderYAxis, renderStructures);
        }
    }
    
    private void generate(
            final PrintStream out, 
            final Structure struct, 
            final double margin, 
            final double cellSize,
            final boolean renderGrid,
            final boolean renderInputTerminals,
            final boolean renderOutputTerminals,
            final boolean renderTetriminos,
            final boolean renderYAxis,
            final boolean renderStructures) {
        
        final Structure[] structures = struct.getStructures();
        final int maxX = struct.getMaxX();
        final int minX = struct.getMinX();
        final int maxY = struct.getMaxY();
        final int minY = struct.getMinY();
        final int cellsWidth = maxX - minX + 1;
        final int cellsHeight = maxY - minY + 1;
        final double gridWidth = cellSize * cellsWidth;
        final double gridHeight = cellSize * cellsHeight;
        final double gridX = margin;
        final double gridY = margin;
        final int svgWidth = (int)Math.round(2 * margin + gridWidth);
        final int svgHeight = (int)Math.round(2 * margin + gridHeight);
        final double ox = gridX + gridWidth / 2.0 + ((cellsWidth & 1) == 0 ? cellSize : cellSize / 2.0);
        final double oy = gridY + gridHeight;
        
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
        
        if (renderGrid) {
            for (int y = 0; y <= maxY + 1; ++y) {
                final double lineY = gridY + cellSize * y;
                out.format("    <line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" class=\"grid\"/>%n", toString(gridX), 
                        toString(lineY), toString(gridX + gridWidth), toString(lineY));
            }            
            for (int x = minX; x <= maxX + 1; ++x) {
                final double lineX = gridX + cellSize * (x - minX);
                out.format("    <line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" class=\"%s\"/>%n", toString(lineX), 
                        toString(gridY), toString(lineX), toString(gridY + gridHeight), 
                        (renderYAxis && x == 0) ? "grid-axis" : "grid");
            }           
        }
        
        if (renderStructures) {
            final int fontSize = (int)(2.0 * cellSize / 3.0);
            final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            final Graphics2D g = image.createGraphics();
            final FontMetrics fontMetrics = g.getFontMetrics(new Font(Font.MONOSPACED, Font.PLAIN, fontSize));
            out.format("    <g style=\"font-size: %dpx;\">%n", fontSize);
            for (int i = structures.length - 1; i >= 0; --i) {
                final Structure s = structures[i];
                final String componentName = s.getComponentName();
                if (componentName != null) {                
                    final double fillHeight = cellSize * (s.getMaxY() - s.getMinY() + 1);
                    final double fillWidth = cellSize * (s.getMaxX() - s.getMinX() + 1);
                    final double fillX = ox + cellSize * (struct.getX() + s.getX() + s.getMinX() - 1);
                    final double fillY = oy - cellSize * (struct.getY() + s.getY() - s.getMinY()) - fillHeight;                
                    out.println("        <g>");
                    out.format("            <rect x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" class=\"component\"/>%n", 
                                toString(fillX), toString(fillY + cellSize), toString(fillWidth - 1), 
                                toString(fillHeight - 2.0 * cellSize - 1));                    
                    final Rectangle2D nameBounds = fontMetrics.getStringBounds(componentName, g);
                    out.format("            <text transform=\"translate(%s %s)%s\" class=\"component\" "
                            + "dominant-baseline=\"middle\" text-anchor=\"middle\">%s</text>%n", 
                            toString(fillX + fillWidth / 2.0), toString(fillY + fillHeight / 2.0),
                            (nameBounds.getWidth() >= fillWidth - 10) ? " rotate(-90)" : "",
                            componentName);
                    out.println("        </g>");
                }
            }
            out.println("    </g>");
            for (int i = structures.length - 1; i >= 0; --i) {
                final Structure s = structures[i];        
                final String componentName = struct.getComponentName();
                if (componentName != null) {                
                    renderTerminals(out, ox, oy, cellSize, s.getInputs(), struct.getX() + s.getX(), 
                            struct.getY() + s.getY(), false);
                    renderTerminals(out, ox, oy, cellSize, s.getOutputs(), struct.getX() + s.getX(), 
                            struct.getY() + s.getY(), true);
                }
            }  
            g.dispose();
        }
        
        if (renderInputTerminals) {
            renderTerminals(out, ox, oy, cellSize, struct.getInputs(), struct.getX(), struct.getY(), true);
        }
        
        if (renderOutputTerminals) {
            renderTerminals(out, ox, oy, cellSize, struct.getOutputs(), struct.getX(), struct.getY(), true);
        }        
        
        if (renderTetriminos) {
            for (int i = structures.length - 1; i >= 0; --i) {
                final Structure s = structures[i];
                final Tetrimino tetrimino = s.getTetrimino();
                if (tetrimino == null) {
                    continue;
                }
                final double x = struct.getX() + s.getX() - 0.5;
                final double y = struct.getY() + s.getY() + 0.5;
                out.format("    <use href=\"#%s\" x=\"%s\" y=\"%s\"/>%n", tetrimino.getName(), 
                        toString(ox + cellSize * x), toString(oy - cellSize * y));
            }
        }
        
        out.println("</svg>");        
    }
    
    private void renderTerminals(
            final PrintStream out, 
            final double ox, 
            final double oy, 
            final double cellSize, 
            final TerminalRectangle[][] terminals, 
            final double offsetX, 
            final double offsetY, 
            final boolean renderState) {
        
        if (terminals == null) {
            return;
        }
        
        for (int i = terminals.length - 1; i >= 0; --i) {            
            final TerminalRectangle[] terms = terminals[i];            
            for (int j = terms.length - 1; j >= 0; --j) {
                final TerminalRectangle terminal = terms[j];                
                final double px = ox + cellSize * (offsetX + terminal.x - 1);
                final double py = oy - cellSize * (offsetY + terminal.y + 2);
                final double width = cellSize * terminal.width;
                if (renderState) {
                    final TerminalState state = terminal.getState();
                    if (state == TerminalState.ZERO) {
                        out.format("    <rect x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" class=\"terminal-fill\"/>%n", 
                                toString(px), toString(py + cellSize), toString(width - 1), toString(cellSize - 1));
                    } else if (state == TerminalState.ONE) {
                        out.format("    <rect x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" class=\"terminal-fill\"/>%n", 
                                toString(px), toString(py), toString(width - 1), toString(2.0 * cellSize - 1));
                    }
                }
                out.format("    <rect x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" class=\"terminal-stroke\"/>%n", 
                        toString(px), toString(py), toString(width - 1), toString(2 * cellSize - 1));
            }
        }  
    }    
    
    private String toString(final double x) {
        if ((int)x == x) {
            return Integer.toString((int)x);
        } 
        return String.format("%s", x);
    }    
}
