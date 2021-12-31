package tetriscircuits.ui;

public class BlockFaces {

    public final boolean top;    
    public final boolean right;
    public final boolean bottom;
    public final boolean left;
    
    public final boolean topLeft;
    public final boolean topRight;
    public final boolean bottomLeft;
    public final boolean bottomRight;

    public BlockFaces(final boolean top, final boolean right, final boolean bottom, final boolean left,
            final boolean topLeft, final boolean topRight, final boolean bottomLeft, final boolean bottomRight) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
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

    public boolean isTopLeft() {
        return topLeft;
    }

    public boolean isTopRight() {
        return topRight;
    }

    public boolean isBottomLeft() {
        return bottomLeft;
    }

    public boolean isBottomRight() {
        return bottomRight;
    }
    
    public String toString() {
        return String.format("%b %b %b %b %b %b %b %b", top, right, bottom, left, topLeft, topRight, bottomLeft, 
                bottomRight);
    }
}
