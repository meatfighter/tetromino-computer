package tetrominocomputer.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

public final class Js {
    
    public static ScriptEngine getScriptEngine() {
        
        if (Double.parseDouble(System.getProperty("java.specification.version")) < 15) {
            return new NashornScriptEngineFactory().getScriptEngine();
        }
        
        return new ScriptEngineManager().getEngineByName("nashorn");
    }
}
