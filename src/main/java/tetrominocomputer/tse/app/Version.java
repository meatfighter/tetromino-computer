package tetrominocomputer.tse.app;

public final class Version {
    
    private static final String YEAR = "2023";
    private static final String VERSION = "1.0.0";
    
    public static String getVersion() {
        return VERSION;
    }
    
    public static String getYear() {
        return YEAR;
    }
    
    private Version() {        
    }
}
