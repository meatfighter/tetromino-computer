package tetriscircuits.web;

public class GenerateMovingRegister {

    private static final double MARGIN = 5.5;  
    private static final double SQUARE_SIZE = 32;    

    public void launch() throws Exception {
        
        final int N = 3;
        final int[] a = { 0x09, 0xAD, 0x62, 0xFF, 0x05, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, };
        
        final boolean[] rs = new boolean[a.length];
        for (int i = N - 1; i >= 0; --i) {
            rs[i] = true;
        }
        
        final int FINAL_FRAME = 2 * N * (a.length - N) - 1;
        
        printHeader(a);
        int frame = 0;
        for (int r = 0; r < a.length - N; ++r) {
            for (int s = 2; s >= 0; --s) {
                swap(a, rs, r + s);
                printFrameHeader(frame++, FINAL_FRAME, s == 0 ? 0.75 : 0.25);
                printRects(rs);
                printText(a);
                printBorder(a);
                printFrameFooter();                
            }
        }
        for (int r = a.length - N - 1; r >= 0; --r) {
            for (int s = 0; s < 3; ++s) {
                swap(a, rs, r + s);
                printFrameHeader(frame++, FINAL_FRAME, s == 2 ? 0.75 : 0.25);
                printRects(rs);
                printText(a);
                printBorder(a);
                printFrameFooter();                
            }
        }
        printFooter();
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
    
    private void swap(final int[] a, final boolean[] rs, final int index) {
        final int at = a[index];
        a[index] = a[index + 1];
        a[index + 1] = at;
        
        final boolean rst = rs[index];
        rs[index] = rs[index + 1];
        rs[index + 1] = rst;
    }
    
    private void printBorder(final int[] a) {
        for (int i = 0; i <= a.length; ++i) {
            final double x = MARGIN + SQUARE_SIZE * i;
            printLine(x, MARGIN, x, MARGIN + SQUARE_SIZE);
        }
        printLine(MARGIN, MARGIN, SQUARE_SIZE * a.length + 0.5 * SQUARE_SIZE, MARGIN);
        printLine(MARGIN, MARGIN + SQUARE_SIZE, SQUARE_SIZE * a.length + 0.5 * SQUARE_SIZE, MARGIN + SQUARE_SIZE);
    }
    
    private void printRects(final boolean[] rs) {
        int s = 0;        
        while (s != rs.length) {
            int e = s;
            while (++e != rs.length && rs[s] == rs[e]) {                                
            }
            printRect(MARGIN + SQUARE_SIZE * s, MARGIN, SQUARE_SIZE * (e - s), SQUARE_SIZE, rs[s]);
            s = e;
        }
    }
    
    private void printRect(final double x, final double y, final double width, final double height, 
            final boolean register) {        
        System.out.format("        <rect x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" class=\"%s\" />%n", x, y, width, 
                height, register ? "array-register" : "array-tape");
    }
    
    private void printLine(final double x1, final double y1, final double x2, final double y2) {
        System.out.format("        <line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" class=\"array\" />%n", x1, y1, x2, y2);
    }
    
    private void printText(final int[] a) {
        for (int i = a.length - 1; i >= 0; --i) {
            printText(a, i);
        }
    }
    
    private void printText(final int[] a, final int i) {
        printText(MARGIN + 4.75 + SQUARE_SIZE * i, MARGIN + 21.75, a[i]);
    }
    
    private void printText(final double x, final double y, final int value) {
        printText(x, y, ((value < 0x10) ? "0" : "") + Integer.toHexString(value).toUpperCase());
    }
    
    private void printText(final double x, final double y, final String text) {
        System.out.format("        <text x=\"%s\" y=\"%s\" class=\"array\">%s</text>%n", x, y, text);
    }
    
    private void printHeader(final int[] a) {
        final double WIDTH = SQUARE_SIZE * a.length + 0.5 * SQUARE_SIZE + 2 * MARGIN;
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
        new GenerateMovingRegister().launch();
    }
}
