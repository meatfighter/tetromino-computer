package tetriscircuits;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import tetriscircuits.parser.ParseException;
import tetriscircuits.parser.Parser;

public class Controller {
    
    private final Simulator simulator = new Simulator();
    
    private final Map<String, Component> loadedComponents = new ConcurrentHashMap<>();
    private final Map<String, Component> builtComponents = new ConcurrentHashMap<>();
    
    private final List<Playfield> playfieldPool = Collections.synchronizedList(new ArrayList<>());
    
    private final ExecutorService executor = Executors.newWorkStealingPool();
    
    private volatile OutputListener outputListener;
    private volatile ProgressListener progressListener;
    private volatile ProgressListener buildListener;
    private volatile RunListener runListener;
    
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

    public void setRunListener(final RunListener runListener) {
        this.runListener = runListener;
    }
    
    public void run(final String componentName, final String testBitStr) {
        final ProgressListener listener = buildListener;
        if (listener != null) {
            listener.update(true);
        }
        execute(() -> {
            runComponent(componentName, testBitStr.trim());
            if (listener != null) {
                listener.update(false);
            }
        });
    }
    
    private void runComponent(final String componentName, final String testBitStr) {
        
        final OutputListener outListener = outputListener;
        final RunListener listener = runListener;
        
        if (outListener != null) {
            outListener.clear();
        }
        
        final List<Point> inputs = new ArrayList<>();
        final List<Point> outputs = new ArrayList<>();
        final List<LockedTetrimino> lockedTetriminos = new ArrayList<>();         
        
        Component component = builtComponents.get(componentName);
        if (component == null) {
            component = loadedComponents.get(componentName);
        }
        if (component == null) {
            if (outListener != null) {
                outListener.append("Error: Unknown component: " + componentName);
            }
        } else {                
            final Playfield playfield = borrowPlayfield();
            try {
                simulator.init(playfield, component, testBitStr, p -> inputs.add(p));
                simulator.findOutputs(playfield, component, p -> outputs.add(p));
                simulator.simulate(playfield, component, lockedTetrimino -> lockedTetriminos.add(lockedTetrimino));
                if (outListener != null) {
                    if (testBitStr.isEmpty()) {
                        outListener.append("Ran " + componentName + " with no inputs.");
                    } else {
                        outListener.append("Ran " + componentName + " with " + testBitStr + ".");
                    }
                }
            } finally {
                returnPlayfield(playfield);
            }
        }
        
        if (listener != null) {            
            listener.runCompleted(inputs, outputs, lockedTetriminos);
        }        
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
    
    private void returnPlayfield(final Playfield playfield) {
        playfield.clear();
        playfieldPool.add(playfield);
    }
    
    private Playfield borrowPlayfield() {
        Playfield playfield = null;
        synchronized(playfieldPool) {
            if (!playfieldPool.isEmpty()) {
                playfield = playfieldPool.remove(playfieldPool.size() - 1);
            }
        }
        if (playfield == null) {
            playfield = new Playfield(4096, 2048, 1);            
        }
        return playfield;
    }
}
