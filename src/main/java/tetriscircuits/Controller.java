package tetriscircuits;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import tetriscircuits.parser.ParseException;
import tetriscircuits.parser.Parser;

public class Controller {
    
    private final Map<String, Component> loadedComponents = new ConcurrentHashMap<>();
    private final Map<String, Component> builtComponents = new ConcurrentHashMap<>();
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    private volatile OutputListener outputListener;
    private volatile ProgressListener progressListener;
    private volatile ProgressListener buildListener;
    
    private int taskCount;
    
    public void setOutputListener(final OutputListener outputListener) {
        this.outputListener = outputListener;
    }
    
    public void setProgressListener(final ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void setBuildListener(final ProgressListener buildListener) {
        this.buildListener = buildListener;
    }
    
    public void run(final String componentName, final String testBitStr) {
        
    }
    
    public void build(final String text) {
        final ProgressListener listener = buildListener;
        if (listener != null) {
            listener.update(true);
        }
        execute(() -> {
            buildText(text);
            if (listener != null) {
                listener.update(false);
            }
        });
    }
    
    private void buildText(final String text) {
        final OutputListener listener = outputListener;        
        builtComponents.clear();
        final Parser parser = new Parser();
        if (listener != null) {
            listener.clear();
            listener.append("Building.");
        }
        try {
            parser.parse(builtComponents, "todo", new ByteArrayInputStream(text.getBytes())); // TODO FILENAME
        } catch (final ParseException e) {
            if (listener != null) {
                listener.append("Build failed.");
                listener.append(e.toString());
            }
            return;
        } catch (final Exception e) {
            if (listener != null) {
                listener.append("Build failed.");
                listener.append(e.getMessage());
            }
            e.printStackTrace(); // TODO REMOVE
            return;
        }
        if (listener != null) {
            listener.append("Build success.");
        }
    }
    
    private synchronized void execute(final Runnable runnable) {
        ++taskCount;
        final ProgressListener listener = progressListener;
        if (listener != null) {
            listener.update(true);
        }
        executor.execute(() -> {
            runnable.run();
            synchronized(Controller.this) {
                if (--taskCount == 0 && listener != null) {
                    listener.update(false);
                }
            }
        });
    }
}
