package tetriscircuits.ui;

public class BlockFaces {

    public final boolean top;    
    public final boolean right;
    public final boolean bottom;
    public final boolean left;

    public BlockFaces(final boolean top, final boolean right, final boolean bottom, final boolean left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public boolean isTop() {
        return top;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isBottom() {
        return bottom;
    }

    public boolean isLeft() {
        return left;
    }
    
    public String toString() {
        return String.format("%b %b %b %b", top, right, bottom, left);
    }
}
