package tetrominocomputer.sim;

public class SimulatorException extends Exception {
    
    public SimulatorException() {        
    }
    
    public SimulatorException(final String message) {
        super(message);
    }
    
    public SimulatorException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public SimulatorException(final Throwable cause) {
        super(cause);
    }
}