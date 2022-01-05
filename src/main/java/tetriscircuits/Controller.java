package tetriscircuits;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
    
    private final Map<String, Component> components = new ConcurrentHashMap<>();    
    private final Map<String, Structure> structures = new ConcurrentHashMap<>();
    
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

    public OutputListener getOutputListener() {
        return outputListener;
    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public BuildListener getBuildListener() {
        return buildListener;
    }

    public RunListener getRunListener() {
        return runListener;
    }
    
    public void loadComponents() {
        execute(() -> {
            for (final File file : new File("components").listFiles(file -> file.isFile())) {
                execute(() -> loadComponent(file));
            }
        });
    }
    
    public void loadComponent(final File file) {
        final String filename = file.getName();
        final OutputListener listener = outputListener;        
        final Parser parser = new Parser();
        if (listener != null) {
            listener.append("Loading " + filename);
        }
        try {
            createStructure(parser.parse(components, filename, new FileInputStream(file)));
        } catch (final ParseException e) {
            if (listener != null) {
                listener.append("Failed to load " + filename);
                listener.append(e.toString());
            }
            return;
        } catch (final Exception e) {
            if (listener != null) {
                listener.append("Failed to load " + filename);
                listener.append(e.getMessage());
            }
            return;
        }
        if (listener != null) {
            listener.append("Loaded " + filename);
        }        
    }
    
    public void buildAndRun(final String text, final String componentName, final String testBitStr) {
        final BuildListener listener = buildListener;
        if (listener != null) {
            listener.buildStarted();
        }
        execute(() -> buildText(text, testBitStr));
    }
    
    public void run(final String componentName, final String testBitStr) {
        execute(() -> runComponent(componentName, testBitStr.trim()));
    }
    
    private void runComponent(final String componentName, final String testBitStr) {
        runComponent(componentName, testBitStr, true);
    }
    
    private void runComponent(final String componentName, final String testBitStr, final boolean clearOutput) {
        
        final OutputListener outListener = outputListener;
        final RunListener listener = runListener;
        
        if (outListener != null && clearOutput) {
            outListener.clear();
        }
        
        final List<LockedTetrimino> lockedTetriminos = new ArrayList<>();         
        
        Component component = components.get(componentName);
        int minX = 0;
        int maxX = 0;
        int maxY = 0;
        if (component == null) {
            if (outListener != null) {
                outListener.append("Error: Unknown component: " + componentName);
            }
        } else {                
            final Playfield playfield = borrowPlayfield();
            try {
                simulator.init(playfield, component, testBitStr);
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
            final boolean[] testBits = new boolean[testBitStr.length()];
            for (int i = testBitStr.length() - 1; i >= 0; --i) {
                testBits[i] = testBitStr.charAt(i) == '1';
            }
            listener.runCompleted(new Structure(
                    lockedTetriminos.toArray(new LockedTetrimino[lockedTetriminos.size()]),
                    component == null ? new Rectangle[0][] : simulator.findTerminals(component.getInputRanges(), 0, 0),
                    component == null ? new Rectangle[0][] : simulator.findTerminals(component.getOutputRanges(), 0, 0),
                    testBits, minX, maxX, 0, maxY));
        }        
    }
    
    public void build(final String text) {
        final BuildListener listener = buildListener;
        if (listener != null) {
            listener.buildStarted();
        }
        execute(() -> buildText(text));
    }
    
    private void buildText(final String text) {
        buildText(text, null);
    }
    
    private void buildText(final String text, final String testBitStr) {
        final OutputListener listener = outputListener;        
        final Parser parser = new Parser();
        if (listener != null) {
            listener.clear();
            listener.append("Building.");
        }
        try {
            createStructure(parser.parse(components, "[unnamed]", new ByteArrayInputStream(text.getBytes())), 
                    testBitStr); // TODO FILENAME
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
            return;
        }
        if (listener != null) {
            listener.append("Build success.");
        }
    }
    
    private void createStructure(final Component component) {
        createStructure(component, null);
    }
    
    private void createStructure(final Component component, final String testBitStr) {
        final OutputListener listener = outputListener;
        final Range[][] inputRanges = component.getInputRanges();                
        final Playfield playfield = borrowPlayfield();
        try {
            if (inputRanges == null) {
                if (listener != null) {
                    listener.append("Error: Invalid input ranges for " + component.getName() + ".");
                }
                return;
            }
            final boolean[] testBits = new boolean[inputRanges.length];
            final StringBuilder sb = new StringBuilder();
            for (int i = testBits.length - 1; i >= 0; --i) {
                sb.append('1');
                testBits[i] = true;
            }                                               
            simulator.init(playfield, component, sb.toString());
            final List<LockedTetrimino> lockedTetriminos = new ArrayList<>();
            simulator.simulate(playfield, component, lockedTetrimino -> lockedTetriminos.add(lockedTetrimino));
            simulator.addOutputs(playfield, component);
            final int minX = playfield.getMinX() - (playfield.getWidth() >> 1);
            final int maxX = playfield.getMaxX() - (playfield.getWidth() >> 1);
            final int maxY = playfield.getHeight() - 1 - playfield.getMinY();  
            structures.put(component.getName(), new Structure(
                    lockedTetriminos.toArray(new LockedTetrimino[lockedTetriminos.size()]),
                    simulator.findTerminals(component.getInputRanges(), 0, 0),
                    simulator.findTerminals(component.getOutputRanges(), 0, 0),
                    testBits, minX, maxX, 0, maxY));
        } catch(final StackOverflowError e) {                    
            if (listener != null) {
                listener.append("Error: The definition of " + component.getName() + " contains itself.");
            }
        } finally {
            returnPlayfield(playfield);
        }        
        
        final Set<String> names = new HashSet<>(components.keySet());
        final List<String> ns = new ArrayList<>(names);
        final String[] componentNames = ns.toArray(new String[ns.size()]);
        Arrays.sort(componentNames);  
        
        final BuildListener buildListener = this.buildListener;
        if (buildListener != null) {
            buildListener.buildCompleted(componentNames, new HashMap<>(structures));
        }
        
        if (component.getName() != null && testBitStr != null) {
            execute(() -> runComponent(component.getName(), testBitStr.trim(), false));
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
