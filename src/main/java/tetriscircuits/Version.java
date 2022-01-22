package tetriscircuits;

public final class Version {
    
    private static final String YEAR = "2022";
    private static final String VERSION = YEAR + ".01.22";
    
    public static String getVersion() {
        return VERSION;
    }
    
    public static String getYear() {
        return YEAR;
    }
    
    private Version() {        
    }
}
