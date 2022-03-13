package tetriscircuits.computer.mapping;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MapMaker {
    
    public static final String WORKSPACE_DIR = "workspace";
    
    public void launch() throws Exception {
        final Map<String, File> tetrisScriptFiles = findTetrisScriptFiles();
        for (Map.Entry<String, File> entry : tetrisScriptFiles.entrySet()) {
            System.out.format("%s -> %s%n", entry.getKey(), entry.getValue());
        }
    }
    
    private Map<String, File> findTetrisScriptFiles() {
        final Map<String, File> files = new HashMap<>();
        findTetrisScriptFiles(new File(WORKSPACE_DIR), files);
        return files;
    }
    
    private void findTetrisScriptFiles(final File directory, final Map<String, File> files) {
        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                findTetrisScriptFiles(file, files);
                continue;
            }
            if (!file.getName().endsWith(".t")) {
                continue;
            }
            final String filename = file.getName();
            final String componentName = filename.substring(0, filename.indexOf('.'));
            files.put(componentName, file);
        }
    }    
    
    public static void main(final String... args) throws Exception {
        new MapMaker().launch();
    }
}
