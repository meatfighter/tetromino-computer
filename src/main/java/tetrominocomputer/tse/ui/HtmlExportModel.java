package tetrominocomputer.tse.ui;

import tetrominocomputer.util.Dirs;

public class HtmlExportModel {
    
    public static String DEFAULT_FILENAME = Dirs.WEB + "snippet.html";
    
    private boolean stdout;
    private String filename = DEFAULT_FILENAME; 
    
    public HtmlExportModel() {        
    }
    
    public HtmlExportModel(final HtmlExportModel model) {
        set(model);
    }
    
    public final void set(final HtmlExportModel model) {
        stdout = model.isStdout();
        filename = model.getFilename();
    }
    
    public HtmlExportModel copy() {
        return new HtmlExportModel(this);
    }

    public boolean isStdout() {
        return stdout;
    }

    public void setStdout(boolean stdout) {
        this.stdout = stdout;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "HtmlExportModel{" + "stdout=" + stdout + ", filename=" + filename + '}';
    }
}
