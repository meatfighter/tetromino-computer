package tetrominocomputer.ui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.PrintStream;
import static java.lang.Math.max;
import static java.lang.Math.min;
import tetrominocomputer.Structure;
import tetrominocomputer.TerminalRectangle;
import tetrominocomputer.TerminalState;
import tetrominocomputer.Tetromino;

public class SvgGenerator {

    public void generate(
            final OutputStream out, 
            final Structure[] structs,
            final double displayWidth,
            final double margin, 
            final double cellSize,
            final boolean renderGrid,
            final boolean renderInputTerminals,
            final boolean renderOutputTerminals,
            final boolean renderTetrominoes,
            final boolean renderYAxis,
            final boolean renderStructures,
            final boolean renderAxesNumbers,
            final boolean renderTerminalValues,
            final int leftPaddingCells,
            final int rightPaddingCells,
            final int topPaddingCells,
            final boolean renderOpenLeft,
            final boolean renderOpenRight,
            final boolean renderOpenTop,
            final boolean renderGridWithNonscalingStroke) {
        try (final PrintStream o = new PrintStream(out)) {
            generate(o, structs, displayWidth, margin, cellSize, renderGrid, renderInputTerminals, 
                    renderOutputTerminals, renderTetrominoes, renderYAxis, renderStructures, renderAxesNumbers, 
                    renderTerminalValues, leftPaddingCells, rightPaddingCells, topPaddingCells, renderOpenLeft, 
                    renderOpenRight, renderOpenTop, renderGridWithNonscalingStroke);
        }
    }
    
    public void generate(
            final PrintStream out, 
            final Structure[] structs,
            final double displayWidth,
            final double margin, 
            final double cellSize,
            final boolean renderGrid,
            final boolean renderInputTerminals,
            final boolean renderOutputTerminals,
            final boolean renderTetrominoes,
            final boolean renderYAxis,
            final boolean renderStructures,
            final boolean renderAxesNumbers,
            final boolean renderTerminalValues,
            final int leftPaddingCells,
            final int rightPaddingCells,
            final int topPaddingCells,
            final boolean renderOpenLeft,
            final boolean renderOpenRight,
            final boolean renderOpenTop,
            final boolean renderGridWithNonscalingStroke) {
        
        double viewBoxWidth = 2.0 * margin;
        double viewBoxHeight = 0;
        for (int i = 0; i < structs.length; ++i) {
            final Structure struct = structs[i];
            final int maxX = struct.getMaxX() + rightPaddingCells;
            final int minX = struct.getMinX() - leftPaddingCells;
            final int maxY = struct.getMaxY() + topPaddingCells;
            final int minY = struct.getMinY();
            final int cellsWidth = maxX - minX + 1;
            final int cellsHeight = maxY - minY + 1;
            final double gridWidth = cellSize * cellsWidth;
            final double gridHeight = cellSize * cellsHeight + (renderOpenTop ? cellSize / 2.0 : 0);
            viewBoxWidth += ((i > 0) ? 1.25 * cellSize : 0) + (renderAxesNumbers 
                    ? (int)Math.round(cellSize * 3.0 * Integer.toString(maxY).length() / 8.0) : 0) + gridWidth;
            
            final double gridY = margin + (topPaddingCells == 0 && renderTerminalValues ? cellSize : 0);
            viewBoxHeight = max(viewBoxHeight, gridY + gridHeight + margin + (renderTerminalValues ? cellSize : 0)
                + (renderAxesNumbers ? (minX < -9 || maxX > 99) 
                ? (int)Math.round(cellSize * 3.0 * Integer.toString(maxY).length() / 8.0) 
                : (int)(2.0 * cellSize / 3.0) : 0));
        }
        
        final double displayHeight = displayWidth * viewBoxHeight / viewBoxWidth;
        final double svgWidth = (displayWidth > 0) ? displayWidth : viewBoxWidth;
        final double svgHeight = (displayHeight > 0) ? displayHeight : viewBoxHeight;        
        out.println("<?xml version=\"1.0\"?>");
        out.println("<?xml-stylesheet type=\"text/css\" href=\"svg.css\"?>");
        out.format("<svg width=\"%s\" height=\"%s\"%s xmlns=\"http://www.w3.org/2000/svg\" "
                + "xmlns:xlink=\"http://www.w3.org/1999/xlink\">%n", toString(svgWidth), toString(svgHeight),
                (svgWidth != viewBoxWidth || svgHeight != viewBoxHeight) 
                        ? String.format(" viewBox=\"0 0 %s %s\"", toString(viewBoxWidth), toString(viewBoxHeight)) 
                        : "");
        if (renderTetrominoes) {
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
                        final double x = cellSize * (p.getX() - 3.5) - (p.isRight() ? 1 : 0);
                        final double y = cellSize * (p.getY() - 3.5) - (p.isBottom() ? 1 : 0);
                        out.format("%s,%s", toString(x), toString(y));
                    }
                    out.format("\" class=\"%s\"/>%n", tetromino.getGroupName());
                }
            }
            out.println("    </defs>");
        }
        
        double offsetX = margin;
        for (int q = 0; q < structs.length; ++q) {
            final Structure struct = structs[q];
            final Structure[] structures = struct.getStructures();
            final int maxX = struct.getMaxX() + rightPaddingCells;
            final int minX = struct.getMinX() - leftPaddingCells;
            final int maxY = struct.getMaxY() + topPaddingCells;
            final int minY = struct.getMinY();
            final int cellsWidth = maxX - minX + 1;
            final int cellsHeight = maxY - minY + 1;
            final double gridWidth = cellSize * cellsWidth + (renderOpenLeft ? cellSize / 2.0 : 0) 
                    + (renderOpenRight ? cellSize / 2.0 : 0);
            final double gridHeight = cellSize * cellsHeight + (renderOpenTop ? cellSize / 2.0 : 0);
            final double gridX = offsetX 
                    + (renderAxesNumbers ? (int)Math.round(cellSize * 3.0 * Integer.toString(maxY).length() / 8.0) : 0);
            final double gridY = margin + (topPaddingCells == 0 && renderTerminalValues ? cellSize : 0);
            final double ox = gridX + cellSize * (leftPaddingCells - struct.getMinX() + 1);
            final double oy = gridY + gridHeight;

            if (renderGrid) {
                for (int y = 0; y <= maxY + 1; ++y) {
                    final double lineY = gridY + cellSize * y + (renderOpenTop ? cellSize / 2.0 : 0);
                    out.format("    <line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" class=\"grid\"%s/>%n", 
                            toString(gridX), toString(lineY), toString(gridX + gridWidth), toString(lineY),
                            renderGridWithNonscalingStroke ? " vector-effect=\"non-scaling-stroke\"" : "");
                } 
                for (int x = minX; x <= maxX + 1; ++x) {
                    final double lineX = gridX + cellSize * (x - minX) + (renderOpenLeft ? cellSize / 2.0 : 0);
                    out.format("    <line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" class=\"%s\"%s/>%n", toString(lineX), 
                            toString(gridY + 1), toString(lineX), toString(gridY + gridHeight - 1), 
                            (renderYAxis && x == 0) ? "grid-axis" : "grid", 
                            renderGridWithNonscalingStroke ? " vector-effect=\"non-scaling-stroke\"" : "");
                }           
            }

            if (renderAxesNumbers) {
                final double xAxis = gridX - cellSize / 8.0;            
                out.format("    <g style=\"font-size: %dpx;\">%n", (int)(2.0 * cellSize / 3.0));
                for (int y = 0; y <= maxY; ++y) {
                    final double lineY = gridY + cellSize * y + (renderOpenTop ? cellSize / 2.0 : 0);
                    out.format("        <text x=\"%s\" y=\"%s\" class=\"axes\" dy=\"0.25em\" "
                            + "text-anchor=\"end\">%s</text>%n", toString(xAxis), 
                            toString(lineY + cellSize / 2.0), maxY - y);
                }
                if (minX < -9 || maxX > 99) {
                    final double yAxis = gridY + gridHeight + cellSize / 8.0;
                    for (int x = minX; x <= maxX; ++x) {
                        final double lineX = gridX + cellSize * (x - minX) + (renderOpenLeft ? cellSize / 2.0 : 0);
                        out.format("        <text transform=\"translate(%s %s) rotate(-90)\" class=\"axes\" "
                                + "dy=\"0.25em\" text-anchor=\"end\">%s</text>%n", 
                                toString(lineX + cellSize / 2.0), toString(yAxis), x);
                    }
                } else {
                    final double yAxis = gridY + gridHeight + 7.0 * cellSize / 16.0;
                    for (int x = minX; x <= maxX; ++x) {
                        final double lineX = gridX + cellSize * (x - minX) + (renderOpenLeft ? cellSize / 2.0 : 0);
                        out.format("        <text x=\"%s\" y=\"%s\" class=\"axes\" "
                                + "dy=\"0.25em\" text-anchor=\"middle\">%s</text>%n", 
                                toString(lineX + cellSize / 2.0), toString(yAxis), x);
                    }
                }
                out.println("    </g>");
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
                                + "dy=\"0.25em\" text-anchor=\"middle\">%s</text>%n", 
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
                                struct.getY() + s.getY(), false, false, false);
                        renderTerminals(out, ox, oy, cellSize, s.getOutputs(), struct.getX() + s.getX(), 
                                struct.getY() + s.getY(), true, false, false);
                    }
                }  
                g.dispose();
            }

            if (renderInputTerminals || renderOutputTerminals) {
                out.format("    <g style=\"font-size: %dpx;\">%n", (int)cellSize);
                if (renderInputTerminals) {
                    renderTerminals(out, ox, oy, cellSize, struct.getInputs(), struct.getX(), struct.getY(), true, 
                            renderTerminalValues, true);
                }
                if (renderOutputTerminals) {
                    renderTerminals(out, ox, oy, cellSize, struct.getOutputs(), struct.getX(), struct.getY(), false, 
                            renderTerminalValues, false);
                }        
                out.println("    </g>");
            }

            if (renderTetrominoes) {
                for (int i = structures.length - 1; i >= 0; --i) {
                    final Structure s = structures[i];
                    final Tetromino tetromino = s.getTetromino();
                    if (tetromino == null) {
                        continue;
                    }
                    final double x = struct.getX() + s.getX() - 0.5;
                    final double y = struct.getY() + s.getY() + 0.5;
                    out.format("    <use href=\"#%s\" x=\"%s\" y=\"%s\"/>%n", tetromino.getName(), 
                            toString(ox + cellSize * x), toString(oy - cellSize * y));
                }
            }
            
            offsetX = gridX + gridWidth + 1.25 * cellSize;
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
            final boolean renderState,
            final boolean renderValue,
            final boolean renderValueBelow) {
        
        if (terminals == null) {
            return;
        }
        
        for (int i = terminals.length - 1; i >= 0; --i) {            
            final TerminalRectangle[] terms = terminals[i];
            double minX = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxY = -Double.MAX_VALUE;
            boolean zero = false;
            boolean one = false;
            for (int j = terms.length - 1; j >= 0; --j) {
                final TerminalRectangle terminal = terms[j];                
                final double px = ox + cellSize * (offsetX + terminal.x - 1);
                final double py = oy - cellSize * (offsetY + terminal.y + 2);
                final double width = cellSize * terminal.width;
                minX = min(minX, px);
                maxX = max(maxX, px + width);
                minY = min(minY, py);
                maxY = max(maxY, py + 2 * cellSize);
                final TerminalState state = terminal.getState();
                if (state == TerminalState.ZERO) {
                    zero = true;
                    if (renderState) {
                        out.format("        <rect x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" "
                                + "class=\"terminal-fill\"/>%n", toString(px), toString(py + cellSize), 
                                toString(width - 1), toString(cellSize - 1));
                    }
                } else if (state == TerminalState.ONE) {
                    one = true;
                    if (renderState) {
                        out.format("        <rect x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" "
                                + "class=\"terminal-fill\"/>%n", toString(px), toString(py), toString(width - 1), 
                                toString(2.0 * cellSize - 1));
                    }
                }
                out.format("        <rect x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" class=\"terminal-stroke\"/>%n", 
                        toString(px), toString(py), toString(width - 1), toString(2 * cellSize - 1));
            }
            if (renderValue) {
                out.format("        <text x=\"%s\" y=\"%s\" class=\"terminals\" dy=\"0.25em\" "
                        + "text-anchor=\"middle\">%s</text>%n", 
                            toString((minX + maxX) / 2.0), 
                            toString(renderValueBelow ? maxY + 5.0 * cellSize / 8.0 : minY - cellSize / 2.0), 
                            one ? "1" : zero ? "0" : "?");
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
