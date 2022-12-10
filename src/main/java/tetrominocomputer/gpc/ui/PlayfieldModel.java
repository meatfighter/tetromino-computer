package tetrominocomputer.gpc.ui;

public class PlayfieldModel {
    
    public static final int PLAYFIELD_WIDTH = 10;
    public static final int PLAYFIELD_HEIGHT = 20;
    
    private int[][] cells = new int[PLAYFIELD_HEIGHT][PLAYFIELD_WIDTH];
    
    private boolean startPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean downPressed;
    private boolean ccwRotatePressed;
    private boolean cwRotatePressed;

    public int[][] getCells() {
        return cells;
    }

    public void setCells(final int[][] cells) {
        this.cells = cells;
    }

    public boolean isStartPressed() {
        return startPressed;
    }

    public void setStartPressed(final boolean startPressed) {
        this.startPressed = startPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public void setLeftPressed(final boolean leftPressed) {
        this.leftPressed = leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public void setRightPressed(final boolean rightPressed) {
        this.rightPressed = rightPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public void setDownPressed(final boolean downPressed) {
        this.downPressed = downPressed;
    }

    public boolean isCcwRotatePressed() {
        return ccwRotatePressed;
    }

    public void setCcwRotatePressed(final boolean ccwRotatePressed) {
        this.ccwRotatePressed = ccwRotatePressed;
    }

    public boolean isCwRotatePressed() {
        return cwRotatePressed;
    }

    public void setCwRotatePressed(final boolean cwRotatePressed) {
        this.cwRotatePressed = cwRotatePressed;
    }
}