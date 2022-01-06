package tetriscircuits;

public enum TerminalType {
    
    INPUT("in"),
    OUTPUT("out");    
    
    private final String name; 
    
    private TerminalType(final String name) {
        this.name = name;
    }   

    public String getName() {
        return name;
    }
}
