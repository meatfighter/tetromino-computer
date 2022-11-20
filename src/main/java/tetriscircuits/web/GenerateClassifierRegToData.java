package tetriscircuits.web;

import java.util.Random;

public class GenerateClassifierRegToData {
    
    private enum CellType {
        REGISTER,
        TAPE,
        DARK_TAPE,
    }
    
    private class Cell {

        private CellType cellType = CellType.REGISTER;
        private int value;
        
        public Cell() {            
        }
        
        public Cell(final CellType cellType, final int value) {
            this.cellType = cellType;
            this.value = value;
        }
        
        public CellType getCellType() {
            return cellType;
        }

        public void setCellType(final CellType cellType) {
            this.cellType = cellType;
        }

        public int getValue() {
            return value;
        }

        public void setValue(final int value) {
            this.value = value;
        }
    }

    private static final double MARGIN = 5.5;  
    private static final double SQUARE_SIZE = 32;    

    public void launch() throws Exception {
        
        final Random random = new Random(0x33DEADBEEFC0FFEEL);
        
        final int N = 3;
        final int L = 10;
        final Cell[] cells = new Cell[N + L];
        for (int i = cells.length - 1; i >= 0; --i) {
            if (i < N) {
                cells[i] = new Cell(CellType.REGISTER, random.nextInt(256));
            } else {
                cells[i] = new Cell(CellType.TAPE, i - N);
            }            
        }
        
        int frame = 0;
        ++frame;
        for (int i = 0; i < L; ++i) {
            ++frame;
            for (int j = N - 1; j >= 0; --j) {
                ++frame;
            }
        }
        final int FINAL_FRAME = frame - 1;
        frame = 0;
        
        printHeader(cells);        
        printFrameHeader(frame++, FINAL_FRAME, 0.25);
        printRects(cells);
        printText(cells);
        printBorder(cells);
        printFrameFooter();
        
        for (int i = 0; i < L; ++i) {
            
            cells[i + N].setCellType(CellType.DARK_TAPE);
            randomizeRegister(random, cells, i, N);
            printFrameHeader(frame++, FINAL_FRAME, 0.75);
            printRects(cells);
            printText(cells);
            printBorder(cells);
            printFrameFooter();
            
            for (int j = N - 1; j >= 0; --j) {
                swap(cells, i + j);
                printFrameHeader(frame++, FINAL_FRAME, j == 0 && i == L - 1 ? 3 : 0.25);
                printRects(cells);
                printText(cells);
                printBorder(cells);
                printFrameFooter();
            }
        }
        printFooter();
    }
    
    private void randomizeRegister(final Random random, final Cell[] cells, final int index, final int N) {
        for (int i = N - 1; i >= 0; --i) {
            cells[index + i].setValue(random.nextInt(256));
        }
    }
    
    private void printFrameHeader(final int frame, final int finalFrame, final double duration) {
        System.out.println("    <g visibility=\"hidden\">");
        System.out.format("        <animate id=\"frame%s\" attributeName=\"visibility\" values=\"visible\" "
                + "begin=\"%s\" dur=\"%ss\"/>%n", frame, 
                (frame == 0) ? String.format("0s;frame%s.end", finalFrame) : String.format("frame%s.end", frame - 1), 
                duration);
    }
    
    private void printFrameFooter() {
        System.out.println("    </g>");
    }
    
    private void swap(final Cell[] cells, final int index) {
        final Cell cell = cells[index];
        cells[index] = cells[index + 1];
        cells[index + 1] = cell;
    }
    
    private void printBorder(final Cell[] cells) {
        for (int i = 0; i <= cells.length; ++i) {
            final double x = MARGIN + SQUARE_SIZE * i;
            printLine(x, MARGIN, x, MARGIN + SQUARE_SIZE);
        }
        printLine(MARGIN, MARGIN, SQUARE_SIZE * cells.length + 0.5 * SQUARE_SIZE, MARGIN);
        printLine(MARGIN, MARGIN + SQUARE_SIZE, SQUARE_SIZE * cells.length + 0.5 * SQUARE_SIZE, MARGIN + SQUARE_SIZE);
    }
    
    private void printRects(final Cell[] cells) {
        int s = 0;        
        while (s != cells.length) {
            int e = s;
            while (++e != cells.length && cells[s].getCellType() == cells[e].getCellType()) {                                
            }
            printRect(MARGIN + SQUARE_SIZE * s, MARGIN, SQUARE_SIZE * (e - s), SQUARE_SIZE, cells[s]);
            s = e;
        }
    }
    
    private void printRect(final double x, final double y, final double width, final double height, 
            final Cell cell) {    
        
        final String clazz;
        switch(cell.getCellType()) {
            case DARK_TAPE:
                clazz = "array-dark-tape";
                break;
            case TAPE:
                clazz = "array-tape";
                break;
            default:
                clazz = "array-register";
                break;
        }
        
        System.out.format("        <rect x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" class=\"%s\" />%n", x, y, width, 
                height, clazz);
    }
    
    private void printLine(final double x1, final double y1, final double x2, final double y2) {
        System.out.format("        <line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" class=\"array\" />%n", x1, y1, x2, y2);
    }
    
    private void printText(final Cell[] cells) {
        for (int i = cells.length - 1; i >= 0; --i) {
            printText(cells, i);
        }
    }
    
    private void printText(final Cell[] cells, final int i) {
        printText(MARGIN + 4.75 + SQUARE_SIZE * i, MARGIN + 21.75, cells[i].getValue());
    }
    
    private void printText(final double x, final double y, final int value) {
        printText(x, y, ((value < 0x10) ? "0" : "") + Integer.toHexString(value).toUpperCase());
    }
    
    private void printText(final double x, final double y, final String text) {
        System.out.format("        <text x=\"%s\" y=\"%s\" class=\"array\">%s</text>%n", x, y, text);
    }
    
    private void printHeader(final Cell[] cells) {
        final double WIDTH = SQUARE_SIZE * cells.length + 0.5 * SQUARE_SIZE + 2 * MARGIN;
        final double HEIGHT = SQUARE_SIZE + 2 * MARGIN;
        
        System.out.println("<?xml version=\"1.0\"?>");
        System.out.println("<?xml-stylesheet type=\"text/css\" href=\"svg.css\"?>");
        System.out.format("<svg width=\"%s\" height=\"%s\" xmlns=\"http://www.w3.org/2000/svg\" "
                + "xmlns:xlink=\"http://www.w3.org/1999/xlink\" style=\"background-color: white\">%n", WIDTH, HEIGHT);
    }
    
    private void printFooter() {
        System.out.println("</svg>");
    }
    
    public static void main(final String... args) throws Exception {
        new GenerateClassifierRegToData().launch();
    }
}