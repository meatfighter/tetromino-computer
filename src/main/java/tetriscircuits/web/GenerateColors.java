package tetriscircuits.web;

public class GenerateColors {
    
    public void launch() {
        
    }    
    
    public static void main(final String... args) throws Exception {
        new GenerateColors().launch();
    }
}

final class LinearColor {
    
    private double r;
    private double g;
    private double b;

    public LinearColor() {        
    }
    
    public LinearColor(final double r, final double g, final double b) {
        this.r = r;
        this.g = b;
        this.b = b;
    }
    
    public LinearColor(final int sRGB) {
        setSRGB(sRGB);
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
        return 0.299 * r + 0.587 * g + 0.114 * b;
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
