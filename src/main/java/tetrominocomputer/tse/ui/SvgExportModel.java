package tetrominocomputer.tse.ui;

import java.io.File;

public class SvgExportModel {
    
    public static String DEFAULT_FILENAME = System.getProperty("user.dir") + File.separator + "web" + File.separator 
            + "test.svg";

    private boolean stdout;
    private String filename = DEFAULT_FILENAME;
    
    private boolean allPossibleValues;
    private String inputValue = "";
    
    private boolean absoluteWidth = true;
    private double displayWidth;
    private int cellSize = 20;
    private int depth = 16;
    private double margin = 15.5;
    private boolean tetrominoes = true;
    private boolean structures;
    private boolean inputNodes = true;
    private boolean outputNodes = true;
    private boolean nodeValues = true;
    
    private boolean gridVisible = true;
    private boolean nonscalingStroke = true;
    private boolean yAxis;
    private boolean axesNumbers;
    private boolean openLeft;
    private boolean openRight;
    private boolean openTop;
    private int padLeft = 1;
    private int padRight = 1;
    private int padTop;
    
    public SvgExportModel() {        
    }
    
    public SvgExportModel(final SvgExportModel model) {
        set(model);     
    }
    
    public final void set(final SvgExportModel model) {
        stdout = model.isStdout();
        filename = model.getFilename();
        allPossibleValues = model.isAllPossibleValues();
        inputValue = model.getInputValue();
        absoluteWidth = model.isAbsoluteWidth();
        displayWidth = model.getDisplayWidth();
        cellSize = model.getCellSize();
        depth = model.getDepth();
        margin = model.getMargin();
        tetrominoes = model.isTetrominoes();
        structures = model.isStructures();
        inputNodes = model.isInputNodes();
        outputNodes = model.isOutputNodes();
        nodeValues = model.isNodeValues();
        gridVisible = model.isGridVisible();
        nonscalingStroke = model.isNonscalingStroke();
        yAxis = model.isyAxis();
        axesNumbers = model.isAxesNumbers();
        openLeft = model.isOpenLeft();
        openRight = model.isOpenRight();
        openTop = model.isOpenTop();
        padLeft = model.getPadLeft();
        padRight = model.getPadRight();
        padTop = model.getPadTop();
    }
    
    public SvgExportModel copy() {
        return new SvgExportModel(this);
    }

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

    public int getDepth() {
        return depth;
    }

    public void setDepth(final int depth) {
        this.depth = depth;
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

    @Override
    public String toString() {
        return "SvgExportModel{" + "stdout=" + stdout + ", filename=" + filename + ", allPossibleValues=" 
                + allPossibleValues + ", inputValue=" + inputValue + ", absoluteWidth=" + absoluteWidth 
                + ", displayWidth=" + displayWidth + ", cellSize=" + cellSize + ", depth=" + depth 
                + ", margin=" + margin + ", tetrominoes=" + tetrominoes + ", structures=" + structures 
                + ", inputNodes=" + inputNodes + ", outputNodes=" + outputNodes + ", nodeValues=" + nodeValues 
                + ", gridVisible=" + gridVisible + ", nonscalingStroke=" + nonscalingStroke + ", yAxis=" + yAxis 
                + ", axesNumbers=" + axesNumbers + ", openLeft=" + openLeft + ", openRight=" + openRight + ", openTop=" 
                + openTop + ", padLeft=" + padLeft + ", padRight=" + padRight + ", padTop=" + padTop + '}';
    }
}