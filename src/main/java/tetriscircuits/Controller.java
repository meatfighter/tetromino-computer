package tetriscircuits;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import static java.lang.Math.max;
import static java.lang.Math.min;
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
import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import tetriscircuits.parser.ParseException;
import tetriscircuits.parser.Parser;

public class Controller {
    
    private final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();        
    private final ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");      
    
    private final Simulator simulator = new Simulator(scriptEngine);
    
    private final Map<String, Component> components = new ConcurrentHashMap<>();    
    private final Map<String, Structure> structures = new ConcurrentHashMap<>();
    
    private final List<Playfield> playfieldPool = Collections.synchronizedList(new ArrayList<>());
    
    private final ExecutorService executor = Executors.newWorkStealingPool();
    
    private final Object taskMonitor = new Object();
    private final Object loadMonitor = new Object();
    
    private volatile OutputListener outputListener;
    private volatile ProgressListener progressListener;
    private volatile BuildListener buildListener;
    private volatile RunListener runListener;
    
    private int taskCount;
    private int loadCount;
    
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
    
    private void createBuffers() {
        for (int i = 3; i <= 100; ++i) {
            createBuffer(i);
        }
    }
    
    private void createBuffer(final int length) {                
        final Component buffer = new Component(String.format("buffer%d", length));
        final List<Instruction> instructions = new ArrayList<>();        
        switch(length % 4) {
            case 0:
                buffer.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                        new HorizontalLine[] { new HorizontalLine(0, 0) })});
                break;
            case 1:
                instructions.add(new Instruction(Tetrimino.LR, buffer, new int[] { -1 }));
                buffer.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                        new HorizontalLine[] { new HorizontalLine(-1, 0, 0) })});
                break;
            case 2:
                instructions.add(new Instruction(Tetrimino.OS, buffer, new int[] { 0 }));
                buffer.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                        new HorizontalLine[] { new HorizontalLine(-1, 0, 0) })});
                break;
            case 3:
                instructions.add(new Instruction(Tetrimino.JL, buffer, new int[] { 0 }));
                buffer.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                        new HorizontalLine[] { new HorizontalLine(-1, 0, 0) })});
                break;
        }
        for (int i = length >> 2; i > 0; --i) {
            instructions.add(new Instruction(Tetrimino.IV, buffer, new int[] { 0 }));
        }
        buffer.setInstructions(instructions.toArray(new Instruction[instructions.size()]));
        buffer.setOutputs(new Terminal[] { new Terminal(TerminalType.OUTPUT, "o", 
                new HorizontalLine[] { new HorizontalLine(0, length) })});  
        try {
            buffer.setCompiledScript(((Compilable)scriptEngine).compile("o=i;"));
        } catch (final ScriptException e) {
            e.printStackTrace(); // TODO
        }
        components.put(buffer.getName(), buffer);        
    }
    
    public void loadComponents() {
        execute(() -> {
            createBuffers();
            final OutputListener listener = outputListener;
            final Map<String, Files> files = new HashMap<>();
            for (final File file : new File("components").listFiles(
                    file -> file.isFile() && (file.getName().endsWith(".def") || file.getName().endsWith(".js")))) {
                final String filename = file.getName();
                final String componentName = filename.substring(0, filename.indexOf('.'));
                Files fs = files.get(componentName);
                if (fs == null) {
                    fs = new Files();
                    files.put(componentName, fs);
                }
                if (filename.endsWith(".def")) {
                    fs.setDefFile(file);
                } else {
                    fs.setJsFile(file);
                }
            }
            synchronized(loadMonitor) {
                loadCount = files.size();
                for (Map.Entry<String, Files> entry : files.entrySet()) {
                    final Files fs = entry.getValue();
                    execute(() -> {
                        loadComponentJavaScript(entry.getKey(), fs.getJsFile());
                        loadComponentDefinition(entry.getKey(), fs.getDefFile());
                        synchronized(loadMonitor) {
                            if (--loadCount == 0) {
                                createStructures(0);
                                notifyStructuresCreated();
                            }
                        }
                    });
                }
            }
        });
    }
    
    private void loadComponentJavaScript(final String componentName, final File file) {
     
        final OutputListener listener = outputListener;
        if (listener != null) {
            if (file == null) {
                listener.append("Error: " + componentName + " missing JavaScript file.");
                return;
            } else {
                listener.append("Loading " + componentName + " JavaScript file...");
            }
        }
        
        try (final BufferedReader br = new BufferedReader(new FileReader(file))) {
            loadComponentJavaScript(componentName, file, br);
        } catch (final Exception e) {
            if (listener != null) {
                listener.append("Failed to load " + componentName + " Javascript.");
                listener.append(e.getMessage());
            }
            return;
        }
        
        if (listener != null) {
            listener.append("Loaded " + componentName + " Javascript.");
        } 
    }
    
    private void loadComponentJavaScript(final String componentName, final File file, final Reader reader) 
            throws ScriptException {
        
        components.computeIfAbsent(componentName, k -> new Component(componentName)).setCompiledScript(
                ((Compilable)scriptEngine).compile(reader));
    }
    
    private void loadComponentDefinition(final String componentName, final File file) {
        
        final OutputListener listener = outputListener;
        if (listener != null) {
            if (file == null) {
                listener.append("Error: " + componentName + " missing definition file.");
                return;
            } else {
                listener.append("Loading " + componentName + " definition file...");
            }
        }
        
        final String filename = file.getName();        
        final Parser parser = new Parser();
        
        try {
            parser.parse(components, filename, new FileInputStream(file));
        } catch (final ParseException e) {
            if (listener != null) {
                listener.append("Failed to load " + componentName + " defintion file.");
                listener.append(e.toString());
            }
            return;
        } catch (final Exception e) {
            if (listener != null) {
                listener.append("Failed to load " + componentName + " defintion file.");
                listener.append(e.getMessage());
            }
            return;
        }
        if (listener != null) {
            listener.append("Loaded " + componentName + " definition file.");
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
        
        final List<Structure> structs = new ArrayList<>();         
        
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
                simulator.simulate(playfield, component, 2,//Integer.MAX_VALUE, // TODO SET DEPTH
                        structure -> structs.add(structure));
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
            
            final TerminalRectangle[][] inputs;
            final TerminalRectangle[][] outputs;
            if (component == null) {
                inputs = new TerminalRectangle[0][];
                outputs = new TerminalRectangle[0][];
            } else {
                inputs = simulator.findTerminals(component.getInputs(), 0, 0, testBitStr);
                outputs = simulator.findTerminals(component.getOutputs(), 0, 0);
            }
            
            for (int i = inputs.length - 1; i >= 0; --i) {
                final TerminalRectangle[] ins = inputs[i];
                for (int j = ins.length - 1; j >= 0; --j) {
                    final TerminalRectangle input = ins[j];
                    minX = min(minX, input.x);
                    maxX = max(maxX, input.x + input.width - 1);
                    maxY = max(maxY, input.y + 1);
                }
            }
            
            for (int i = outputs.length - 1; i >= 0; --i) {
                final TerminalRectangle[] outs = outputs[i];
                for (int j = outs.length - 1; j >= 0; --j) {
                    final TerminalRectangle output = outs[j];
                    minX = min(minX, output.x);
                    maxX = max(maxX, output.x + output.width - 1);
                    maxY = max(maxY, output.y + 1);
                }
            }
            
            listener.runCompleted(new Structure(componentName, 0, 0, inputs, outputs, minX, maxX, 0, maxY,
                    structs.toArray(new Structure[structs.size()])));
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
                    2, testBitStr); // TODO FILENAME AND DEPTH
            notifyStructuresCreated();
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
    
    private void createStructures(final int depth) {
        for (final Component component : components.values()) {
            createStructure(component, depth);
        }
    }
    
    private void createStructure(final Component component, final int depth) {
        createStructure(component, depth, null);
    }
    
    private void createStructure(final Component component, final int depth, final String testBitStr) {
        final OutputListener listener = outputListener;
        final Terminal[] inTerms = component.getInputs();                
        final Playfield playfield = borrowPlayfield();
        try {
            if (inTerms == null) {
                if (listener != null) {
                    listener.append("Error: Invalid input ranges for " + component.getName() + ".");
                }
                return;
            }
            final boolean[] testBits = new boolean[inTerms.length];
            final StringBuilder sb = new StringBuilder();
            for (int i = testBits.length - 1; i >= 0; --i) {
                sb.append('1');
                testBits[i] = true;
            }                                               
            simulator.init(playfield, component, sb.toString());
            final List<Structure> structs = new ArrayList<>();
            simulator.simulate(playfield, component, depth, structure -> structs.add(structure));
            simulator.addOutputs(playfield, component);
            int minX = playfield.getMinX() - (playfield.getWidth() >> 1);
            int maxX = playfield.getMaxX() - (playfield.getWidth() >> 1);
            int maxY = playfield.getHeight() - 1 - playfield.getMinY();  
            
            final TerminalRectangle[][] inputs = simulator.findTerminals(component.getInputs(), 0, 0);           
            for (int i = inputs.length - 1; i >= 0; --i) {
                final TerminalRectangle[] ins = inputs[i];
                for (int j = ins.length - 1; j >= 0; --j) {
                    final TerminalRectangle input = ins[j];
                    minX = min(minX, input.x);
                    maxX = max(maxX, input.x + input.width - 1);
                    maxY = max(maxY, input.y + 1);
                }
            }
            
            final TerminalRectangle[][] outputs = simulator.findTerminals(component.getOutputs(), 0, 0);
            for (int i = outputs.length - 1; i >= 0; --i) {
                final TerminalRectangle[] outs = outputs[i];
                for (int j = outs.length - 1; j >= 0; --j) {
                    final TerminalRectangle output = outs[j];
                    minX = min(minX, output.x);
                    maxX = max(maxX, output.x + output.width - 1);
                    maxY = max(maxY, output.y + 1);
                }
            }            
                        
            structures.put(component.getName(), new Structure(component.getName(), 0, 0, inputs, outputs, minX, maxX, 0, 
                    maxY, structs.toArray(new Structure[structs.size()])));
        } catch(final StackOverflowError e) {                    
            if (listener != null) {
                listener.append("Error: The definition of " + component.getName() + " contains itself.");
            }
        } finally {
            returnPlayfield(playfield);
        }        
        
        if (component.getName() != null && testBitStr != null) {
            execute(() -> runComponent(component.getName(), testBitStr.trim(), false));
        }        
    }
    
    private void notifyStructuresCreated() {
        final Set<String> names = new HashSet<>(components.keySet());
        final List<String> ns = new ArrayList<>(names);
        final String[] componentNames = ns.toArray(new String[ns.size()]);
        Arrays.sort(componentNames);  
        
        final BuildListener buildListener = this.buildListener;
        if (buildListener != null) {
            buildListener.buildCompleted(componentNames, new HashMap<>(structures));
        }
    }
    
    private void execute(final Runnable runnable) {
        synchronized(taskMonitor) {
            ++taskCount;
            final ProgressListener listener = progressListener;
            if (listener != null) {
                listener.update(true);
            }
            executor.execute(() -> {
                runnable.run();
                synchronized(taskMonitor) {
                    if (--taskCount == 0 && listener != null) {
                        listener.update(false);
                    }
                }
            });
        }
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
    
    private class Files {
        
        private File defFile;
        private File jsFile;

        public File getDefFile() {
            return defFile;
        }

        public void setDefFile(final File defFile) {
            this.defFile = defFile;
        }

        public File getJsFile() {
            return jsFile;
        }

        public void setJsFile(final File jsFile) {
            this.jsFile = jsFile;
        }
    }
}
