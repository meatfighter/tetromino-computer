package tetrominocomputer.ui;

public class SvgExportModel {

    private boolean stdout;
    private String filename;
    
    private boolean allPossibleValues;
    private String inputValue;
    
    private boolean absoluteWidth;
    private double displayWidth;
    private int cellSize;
    private int cellDepth;
    private double margin;
    private boolean tetrominoes;
    private boolean structures;
    private boolean inputNodes;
    private boolean outputNodes;
    private boolean nodeValues;
    
    private boolean gridVisible;
    private boolean nonscalingStroke;
    private boolean yAxis;
    private boolean axesNumbers;
    private boolean openLeft;
    private boolean openRight;
    private boolean openTop;
    private int padLeft;
    private int padRight;
    private int padTop;
    
    private boolean cancelled;

    public boolean isStdout() {
        return stdout;
    }

    public void setStdout(final boolean stdout) {
        this.stdout = stdout;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public boolean isAllPossibleValues() {
        return allPossibleValues;
    }

    public void setAllPossibleValues(final boolean allPossibleValues) {
        this.allPossibleValues = allPossibleValues;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(final String inputValue) {
        this.inputValue = inputValue;
    }

    public boolean isAbsoluteWidth() {
        return absoluteWidth;
    }

    public void setAbsoluteWidth(final boolean absoluteWidth) {
        this.absoluteWidth = absoluteWidth;
    }

    public double getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayWidth(final double displayWidth) {
        this.displayWidth = displayWidth;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(final int cellSize) {
        this.cellSize = cellSize;
    }

    public int getCellDepth() {
        return cellDepth;
    }

    public void setCellDepth(final int cellDepth) {
        this.cellDepth = cellDepth;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(final double margin) {
        this.margin = margin;
    }

    public boolean isTetrominoes() {
        return tetrominoes;
    }

    public void setTetrominoes(final boolean tetrominoes) {
        this.tetrominoes = tetrominoes;
    }

    public boolean isStructures() {
        return structures;
    }

    public void setStructures(final boolean structures) {
        this.structures = structures;
    }

    public boolean isInputNodes() {
        return inputNodes;
    }

    public void setInputNodes(final boolean inputNodes) {
        this.inputNodes = inputNodes;
    }

    public boolean isOutputNodes() {
        return outputNodes;
    }

    public void setOutputNodes(final boolean outputNodes) {
        this.outputNodes = outputNodes;
    }

    public boolean isNodeValues() {
        return nodeValues;
    }

    public void setNodeValues(final boolean nodeValues) {
        this.nodeValues = nodeValues;
    }

    public boolean isGridVisible() {
        return gridVisible;
    }

    public void setGridVisible(final boolean gridVisible) {
        this.gridVisible = gridVisible;
    }

    public boolean isNonscalingStroke() {
        return nonscalingStroke;
    }

    public void setNonscalingStroke(final boolean nonscalingStroke) {
        this.nonscalingStroke = nonscalingStroke;
    }

    public boolean isyAxis() {
        return yAxis;
    }

    public void setyAxis(final boolean yAxis) {
        this.yAxis = yAxis;
    }

    public boolean isAxesNumbers() {
        return axesNumbers;
    }

    public void setAxesNumbers(final boolean axesNumbers) {
        this.axesNumbers = axesNumbers;
    }

    public boolean isOpenLeft() {
        return openLeft;
    }

    public void setOpenLeft(final boolean openLeft) {
        this.openLeft = openLeft;
    }

    public boolean isOpenRight() {
        return openRight;
    }

    public void setOpenRight(final boolean openRight) {
        this.openRight = openRight;
    }

    public boolean isOpenTop() {
        return openTop;
    }

    public void setOpenTop(final boolean openTop) {
        this.openTop = openTop;
    }

    public int getPadLeft() {
        return padLeft;
    }

    public void setPadLeft(final int padLeft) {
        this.padLeft = padLeft;
    }

    public int getPadRight() {
        return padRight;
    }

    public void setPadRight(final int padRight) {
        this.padRight = padRight;
    }

    public int getPadTop() {
        return padTop;
    }

    public void setPadTop(final int padTop) {
        this.padTop = padTop;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}