package tetriscircuits;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import tetriscircuits.parser.ParseException;
import tetriscircuits.parser.Parser;

public class Controller {
    
    private final Simulator simulator = new Simulator();
    
    private final Map<String, Component> loadedComponents = new ConcurrentHashMap<>();
    private final Map<String, Component> builtComponents = new ConcurrentHashMap<>();
    
    private final Map<String, Structure> loadedStructures = new ConcurrentHashMap<>();
    private final Map<String, Structure> builtStructures = new ConcurrentHashMap<>();
    
    private final List<Playfield> playfieldPool = Collections.synchronizedList(new ArrayList<>());
    
    private final ExecutorService executor = Executors.newWorkStealingPool();
    
    private volatile OutputListener outputListener;
    private volatile ProgressListener progressListener;
    private volatile BuildListener buildListener;
    private volatile RunListener runListener;
    
    private int taskCount;
    
    public void setOutputListener(final OutputListener outputListener) {
        this.outputListener = outputListener;
    }
    
    public void setProgressListener(final ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void setBuildListener(final BuildListener buildListener) {
        this.buildListener = buildListener;
    }

    public void setRunListener(final RunListener runListener) {
        this.runListener = runListener;
    }
    
    public void run(final String componentName, final String testBitStr) {
        execute(() -> runComponent(componentName, testBitStr.trim()));
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
        int minX = 0;
        int maxX = 0;
        int maxY = 0;
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
                minX = playfield.getMinX() - (playfield.getWidth() >> 1);
                maxX = playfield.getMaxX() - (playfield.getWidth() >> 1);
                maxY = playfield.getHeight() - 1 - playfield.getMinY();
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
            listener.runCompleted(new Structure(
                    lockedTetriminos.toArray(new LockedTetrimino[lockedTetriminos.size()]),
                    inputs.toArray(new Point[inputs.size()]),
                    outputs.toArray(new Point[outputs.size()]),
                    minX, maxX, 0, maxY));
        }        
    }
    
    public void build(final String text) {
        final BuildListener listener = buildListener;
        if (listener != null) {
            listener.buildStarted();
        }
        execute(() -> {
            buildText(text);
            createStructures();            
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
    
    private void createStructures() {
        builtStructures.clear();
        final List<Component> components = new ArrayList<>(builtComponents.values());
        if (components.isEmpty()) {
            return;
        }
        
        final AtomicInteger counter = new AtomicInteger(components.size());
        for (final Component component : components) {
            execute(() -> {
                final Playfield playfield = borrowPlayfield();
                try {
                    final List<LockedTetrimino> lockedTetriminos = new ArrayList<>();
                    simulator.simulate(playfield, component, lockedTetrimino -> lockedTetriminos.add(lockedTetrimino));
                    final int minX = playfield.getMinX() - (playfield.getWidth() >> 1);
                    final int maxX = playfield.getMaxX() - (playfield.getWidth() >> 1);
                    final int maxY = playfield.getHeight() - 1 - playfield.getMinY();  
                    builtStructures.put(component.getName(), new Structure(
                            lockedTetriminos.toArray(new LockedTetrimino[lockedTetriminos.size()]),
                            new Point[0], new Point[0], minX, maxX, 0, maxY));
                } finally {
                    returnPlayfield(playfield);
                    if (counter.decrementAndGet() == 0) {
                        finishedCreatingStructures();
                    }
                }                
            });
        }
    }
    
    private void finishedCreatingStructures() {
        
        final Set<String> names = new HashSet<>(loadedComponents.keySet());
        names.addAll(builtComponents.keySet());
        final List<String> ns = new ArrayList<>(names);
        final String[] componentNames = ns.toArray(new String[ns.size()]);
        Arrays.sort(componentNames);  
        
        final Map<String, Structure> structures = new HashMap<>(loadedStructures);
        structures.putAll(builtStructures);
        
        final BuildListener listener = buildListener;
        if (listener != null) {
            listener.buildCompleted(componentNames, structures);
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
