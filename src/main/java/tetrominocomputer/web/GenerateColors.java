package tetrominocomputer.web;

public class GenerateColors {
    
    private static final int[] SRGBS = {
        0xB802FD,
        0x0000FF,
        0xFE103C,
        0xFFDE00,
        0x66FD00,
        0xFF7308,
        0x00E6FE,
    };
    
    private static final String[] NAMES = { "t", "j", "z", "o", "s", "l", "i" };
    
    public void launch() {
        
        final LinearColor[] colors = new LinearColor[SRGBS.length];
        final LinearColor[] darks = new LinearColor[SRGBS.length];
        
        for (int i = 0; i < SRGBS.length; ++i) {
            colors[i] = new LinearColor(SRGBS[i]);
            colors[i].maxBrigten();             
        }
        
        colors[1].brighten(1.0 / 3.0);
        
        for (int i = 0; i < SRGBS.length; ++i) {
            darks[i] = new LinearColor(colors[i]);
            darks[i].darken(0.7 * darks[i].getY());
        }
        
        for (int i = 0; i < SRGBS.length; ++i) {
            System.out.format("polygon.%s {%n", NAMES[i]);
            System.out.format("    fill: #%s;%n", colors[i].toSRGBString());
            System.out.format("    stroke: #%s;%n", darks[i].toSRGBString());
            System.out.format("    stroke-width: 1;%n"); 
            System.out.format("    stroke-linecap: square;%n");
            System.out.format("}%n");
            System.out.format("%n");
        }
    }    
    
    public static void main(final String... args) throws Exception {
        new GenerateColors().launch();
    }
}

final class LinearColor {
    
    private static final double FR = 0.299;
    private static final double FG = 0.587;
    private static final double FB = 0.114;
    
    private static final double IR = FR / 65536.0;
    private static final double IG = FG / 65536.0;
    private static final double IB = FB / 65536.0;    
    
    private double r;
    private double g;
    private double b;

    public LinearColor() {        
    }
    
    public LinearColor(final LinearColor color) {
        r = color.r;
        g = color.g;
        b = color.b;
    }
    
    public LinearColor(final double r, final double g, final double b) {
        this.r = r;
        this.g = b;
        this.b = b;
    }
    
    public LinearColor(final int sRGB) {
        setSRGB(sRGB);
    }
    
    public void maxBrigten() {
        while (r < 1.0 && g < 1.0 && b < 1.0) {
            r = Math.min(1.0, r + IR);
            g = Math.min(1.0, g + IG);
            b = Math.min(1.0, b + IB);
        }
    }
    
    public void brighten(final double targetY) {
        while (getY() < targetY) {
            r = Math.min(1.0, r + IR);
            g = Math.min(1.0, g + IG);
            b = Math.min(1.0, b + IB);
        }
    }
    
    public void darken(final double targetY) {
        while (getY() > targetY) {
            r = Math.max(0.0, r - IR);
            g = Math.max(0.0, g - IG);
            b = Math.max(0.0, b - IB);
        }
    }
    
    public double getR() {
        return r;
    }

    public void setR(final double r) {
        this.r = r;
    }

    public double getG() {
        return g;
    }

    public void setG(final double g) {
        this.g = g;
    }

    public double getB() {
        return b;
    }

    public void setB(final double b) {
        this.b = b;
    }
    
    public double getY() {
        return FR * r + FG * g + FB * b;
    }
    
    public void setSRGB(final int sRGB) {
        r = toLFromInt(0xFF & (sRGB >> 16));
        g = toLFromInt(0xFF & (sRGB >> 8));
        b = toLFromInt(0xFF & sRGB);
    }
    
    public int getSRGB() {
        return (toSInt(r) << 16) | (toSInt(g) << 8) | toSInt(b);
    }
    
    private int toSInt(final double L) {
        return (int)Math.round(255.0 * toS(L));
    }
    
    private double toS(final double L) {
        
        if (L < 0.0 || L > 1.0) {
            throw new IllegalArgumentException("L = " + L);
        } 
        
        if (L <= 0.00313066844250063) {
            return 12.92 * L;
        } 
        
        return 1.055 * Math.pow(L, 1.0 / 2.4) - 0.055;
    }
    
    private double toLFromInt(final int s) {
        return toL(s / 255.0);
    }
    
    private double toL(final double S) {
        
        if (S < 0.0 || S > 1.0) {
            throw new IllegalArgumentException("S = " + S);
        } 
        
        if (S <= 0.0404482362771082) {
            return S / 12.92;
        }
        
        return Math.pow((S + 0.055) / 1.055, 2.4);
    }
    
    @Override
    public String toString() {
        return String.format("R=%f, G=%f, B=%f, Y=%f, sRGB=%s", r, g, b, getY(), toSRGBString());
    }
    
    public String toSRGBString() {
        String s = Integer.toString(getSRGB(), 16).toUpperCase();
        while (s.length() < 6) {
            s = "0" + s;
        }
        return s;
    }
}
