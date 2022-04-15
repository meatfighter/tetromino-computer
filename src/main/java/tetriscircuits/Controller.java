package tetriscircuits;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
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
import static org.apache.commons.lang3.StringUtils.isBlank;
import tetriscircuits.parser.ParseException;
import tetriscircuits.parser.Parser;
import tetriscircuits.ui.PathPoint;
import tetriscircuits.ui.SvgGenerator;
import tetriscircuits.ui.TetriminoPath;

public class Controller {
    
    public static final String WORKSPACE_DIR = "workspace";
    public static final String DEFAULT_COMPONENT_NAME = "unnamed";
    
    private final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();        
    private final ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");      
       
    private final Map<String, Component> components = new ConcurrentHashMap<>();
    private final Map<String, Extents> componentExtents = new ConcurrentHashMap<>();
    private final Map<String, Structure> structures = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> dependencies = new ConcurrentHashMap<>();
    
    private final Simulator simulator = new Simulator(scriptEngine, components, componentExtents);
    
    private final List<Playfield> playfieldPool = Collections.synchronizedList(new ArrayList<>());
    
    private final ExecutorService executor = Executors.newWorkStealingPool();
    
    private final Object taskMonitor = new Object();
    private final Object loadMonitor = new Object();
    
    private volatile OutputListener outputListener;
    private volatile ProgressListener progressListener;
    private volatile BuildListener buildListener;
    private volatile RunListener runListener;
    private volatile OpenListener openListener;
    
    private int taskCount;
    private int loadCount;
    
    private volatile String savedComponentName;
    private volatile Component savedComponent;
    private volatile Extents savedExtents;
    private volatile Structure savedStructure;

    public void setOpenListener(final OpenListener openListener) {
        this.openListener = openListener;
    }
    
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
    
    private void createIsSsAndZs() {
        for (int i = 3; i <= 2001; ++i) {
            createIls(i);
            createIrs(i);
        }
        for (int i = 3; i <= 2001; i += 2) {
            createSs(i);
            createZs(i);
        }
    }
    
    private void createSs(final int length) {
        final Component ss = new Component(String.format("s%d", length));
        final List<Instruction> instructions = new ArrayList<>();
        for (int i = ((length - 1) >> 1) - 1, x = -1; i >= 0; --i, x -= 2) {
            instructions.add(new Instruction(Tetrimino.SH, null, null, new int[] { x }));
        }
        ss.setInstructions(instructions.toArray(new Instruction[instructions.size()]));
        ss.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                new HorizontalLine[] { new HorizontalLine(-2, -1, 0), new HorizontalLine(0, 1) })});
        ss.setOutputs(new Terminal[] { new Terminal(TerminalType.OUTPUT, "o", 
                new HorizontalLine[] { new HorizontalLine(1 - length, 1), new HorizontalLine(2 - length, 0, 2) })});
        try {
            ss.setCompiledScript(((Compilable)scriptEngine).compile("o=i;"));
        } catch (final ScriptException e) {
            e.printStackTrace(); // TODO
        }
        components.put(ss.getName(), ss);
    }    
    
    private void createZs(final int length) {
        final Component zs = new Component(String.format("z%d", length));
        final List<Instruction> instructions = new ArrayList<>();
        for (int i = ((length - 1) >> 1) - 1, x = 1; i >= 0; --i, x += 2) {
            instructions.add(new Instruction(Tetrimino.ZH, null, null, new int[] { x }));
        }
        zs.setInstructions(instructions.toArray(new Instruction[instructions.size()]));
        zs.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                new HorizontalLine[] { new HorizontalLine(0, 1), new HorizontalLine(1, 2, 0) })});
        zs.setOutputs(new Terminal[] { new Terminal(TerminalType.OUTPUT, "o", 
                new HorizontalLine[] { new HorizontalLine(0, length - 2, 2), new HorizontalLine(length - 1, 1) })});
        try {
            zs.setCompiledScript(((Compilable)scriptEngine).compile("o=i;"));
        } catch (final ScriptException e) {
            e.printStackTrace(); // TODO
        }
        components.put(zs.getName(), zs);
    }
    
    private void addInstruction(final List<Instruction> instructions, final Tetrimino tetrimino, final int x) {
        instructions.add(new Instruction(tetrimino, null, null, new int[] { x }));
    }
    
    private void setInputs(final Component component, final int x, final int y) {
        component.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                        new HorizontalLine[] { new HorizontalLine(x, y) })});
    }
    
    private void setInputs(final Component component, final int minX, final int maxX, final int y) {
        component.setInputs(new Terminal[] { new Terminal(TerminalType.INPUT, "i", 
                        new HorizontalLine[] { new HorizontalLine(minX, maxX, y) })});
    }
    
    private void setOutputs(final Component component, final int x, final int y) {
        component.setOutputs(new Terminal[] { new Terminal(TerminalType.OUTPUT, "o", 
                        new HorizontalLine[] { new HorizontalLine(x, y) })});
    }
    
    private void setOutputs(final Component component, final int minX, final int maxX, final int y) {
        component.setOutputs(new Terminal[] { new Terminal(TerminalType.OUTPUT, "o", 
                        new HorizontalLine[] { new HorizontalLine(minX, maxX, y) })});
    }

    private void createIls(final int length) {                
        final Component is = new Component(String.format("il%d", length));
        final List<Instruction> instructions = new ArrayList<>();
        final int remainder = length % 4;
        switch(length % 4) {
            case 1:
                addInstruction(instructions, Tetrimino.JL, 1);
                break;
            case 2:
                addInstruction(instructions, Tetrimino.OS, 1);
                break;
            case 3:
                addInstruction(instructions, Tetrimino.LR, 0);
                break;
        }
        if (remainder == 0) {
            setInputs(is, 0, 0);
        } else {
            setInputs(is, 0, 1, 0);
        }
        for (int i = length >> 2; i > 0; --i) {
            addInstruction(instructions, Tetrimino.IV, 0);
        }
        is.setInstructions(instructions.toArray(new Instruction[instructions.size()]));
        setOutputs(is, 0, length); 
        try {
            is.setCompiledScript(((Compilable)scriptEngine).compile("o=i;"));
        } catch (final ScriptException e) {
        }
        components.put(is.getName(), is);        
    }    
    
    private void createIrs(final int length) {                
        final Component is = new Component(String.format("ir%d", length));
        final List<Instruction> instructions = new ArrayList<>();
        final int remainder = length % 4;
        switch(length % 4) {
            case 1:
                addInstruction(instructions, Tetrimino.LR, -1);
                break;
            case 2:
                addInstruction(instructions, Tetrimino.OS, 0);
                break;
            case 3:
                addInstruction(instructions, Tetrimino.JL, 0);
                break;
        }
        if (remainder == 0) {
            setInputs(is, 0, 0);
        } else {
            setInputs(is, -1, 0, 0);
        }
        for (int i = length >> 2; i > 0; --i) {
            addInstruction(instructions, Tetrimino.IV, 0);
        }
        is.setInstructions(instructions.toArray(new Instruction[instructions.size()]));
        setOutputs(is, 0, length); 
        try {
            is.setCompiledScript(((Compilable)scriptEngine).compile("o=i;"));
        } catch (final ScriptException e) {
        }
        components.put(is.getName(), is);        
    }
    
    public void openComponent(final String componentName, final File tetrisScriptFile, final File javaScriptFile) {
        executeAfter(() -> {
            components.remove(componentName);
            componentExtents.remove(componentName);
            structures.remove(componentName);
            
            final String tetrisScript = readFile(tetrisScriptFile);
            final String javaScript = readFile(javaScriptFile);
            buildScripts(componentName, tetrisScript, javaScript, 0, null);
            final Component component = components.get(componentName);
            final StringBuilder testBits = new StringBuilder();
            if (component != null) {
                final Terminal[] inputs = component.getInputs();
                if (inputs != null) {
                    for (int i = 0; i < inputs.length; ++i) {
                        testBits.append('0');
                        if ((i & 3) == 3) {
                            testBits.append(' ');
                        }
                    }
                }
            }
            
            buildScripts(componentName, tetrisScript, javaScript, testBits.toString(), 1, null);
            
            savedComponentName = componentName;
            savedComponent = components.get(componentName).clone();
            savedExtents = componentExtents.get(componentName);
            savedStructure = structures.get(componentName);
            
            final OpenListener listener = openListener;
            if (listener != null) {
                listener.openedFiles(componentName, tetrisScriptFile, tetrisScript, javaScriptFile, javaScript, 
                        testBits.toString());
            }            
        });
    }
    
    private String readFile(final File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            final OutputListener listener = outputListener;
            if (listener != null) {
                listener.append("Failed to find " + file);
            }
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        try (final BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(line);
            }
        } catch (final IOException e) {
            final OutputListener listener = outputListener;
            if (listener != null) {
                listener.append("Failed to read " + file + ": " + e.getMessage());
            }
            return "";
        }
        return sb.toString();
    }
    
    private void findSourceFiles(final File directory, final Map<String, Files> files) {
        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                findSourceFiles(file, files);
                continue;
            }
            if (!(file.getName().endsWith(".t") || file.getName().endsWith(".js"))) {
                continue;
            }
            final String filename = file.getName();
            final String componentName = filename.substring(0, filename.indexOf('.'));
            Files fs = files.get(componentName);
            if (fs == null) {
                fs = new Files();
                files.put(componentName, fs);
            }
            if (filename.endsWith(".t")) {
                fs.setTFile(file);
            } else {
                fs.setJsFile(file);
            }
        }
    }
    
    public void loadComponents() {
        execute(() -> {
            components.clear();
            componentExtents.clear();
            structures.clear();
            dependencies.clear();
            createIsSsAndZs();
            final OutputListener listener = outputListener;
            final Map<String, Files> files = new HashMap<>();
            findSourceFiles(new File(WORKSPACE_DIR), files);
            synchronized(loadMonitor) {
                loadCount = files.size();
                for (Map.Entry<String, Files> entry : files.entrySet()) {
                    final Files fs = entry.getValue();
                    execute(() -> {
                        loadComponentJavaScript(entry.getKey(), fs.getJsFile());
                        loadComponentTetrisScript(entry.getKey(), fs.getTFile());
                        synchronized(loadMonitor) {
                            if (--loadCount == 0) {
                                updateComponentExtents();
                                createStructures(0);
                                notifyStructuresCreated(null);
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
            if (file == null || !file.exists()) {
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
    
    private void loadComponentTetrisScript(final String componentName, final File file) {
        
        final OutputListener listener = outputListener;
        if (listener != null) {
            if (file == null || !file.exists()) {
                listener.append("Error: " + componentName + " missing TetrisScript file.");
                return;
            } else {
                listener.append("Loading " + componentName + " TetrisScript file...");
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
            listener.append("Loaded " + componentName + " TetrisScript file.");
        }        
    }
    
    private void updateComponentExtents() {        
        componentExtents.clear();
        for (final Component component : components.values()) {
            getComponentExtents(componentExtents, component.getName());
        }        
    }
    
    private void updateDependencies() {        
        final Map<String, Set<String>> depends = new HashMap<>();
        for (final Component component : components.values()) {
            getDependencies(depends, component.getName());
        }
        
        dependencies.clear();
        for (Map.Entry<String, Set<String>> entry : depends.entrySet()) {
            final String key = entry.getKey();
            for (final String value : entry.getValue()) {
                dependencies.computeIfAbsent(value, v -> new HashSet<>()).add(key);
            }
        }
    }
    
    private Set<String> getDependencies(final Map<String, Set<String>> depends, final String componentName) {
        
        Set<String> ds = depends.get(componentName);
        if (ds != null) {
            return ds;
        }
        
        final Component component = components.getOrDefault(componentName, null);
        if (component == null) {
            return null;
        }
        
        ds = new HashSet<>();   
        depends.put(componentName, ds);
        
        final Instruction[] instructions = component.getInstructions();
        if (instructions == null) {
            return ds;
        }
        for (final Instruction instruction : instructions) {
            if (instruction.getTetrimino() != null) {
                continue;
            }
            final String compName = instruction.getComponentName();
            if (compName == null || components.getOrDefault(compName, null) == null) {
                continue;
            }
            ds.add(compName);
            final Set<String> set = getDependencies(depends, compName);
            if (set != null) {
                ds.addAll(set);
            }            
        }
        
        return ds;
    }
    
    private Extents getComponentExtents(final Map<String, Extents> componentExtents, final String componentName) {
        
        if (componentName == null) {
            return null;
        }

        Extents extents = componentExtents.getOrDefault(componentName, null);
        if (extents != null) {
            return extents;
        }

        final Component component = components.getOrDefault(componentName, null);
        if (component == null) {
            return null;
        }
        
        int minX = Integer.MAX_VALUE;        
        int maxX = Integer.MIN_VALUE;
        int maxY = 0;

        if (component.getInstructions() != null) {
            for (final Instruction instruction : component.getInstructions()) {

                Extents e = null;
                
                final Tetrimino tetrimino = instruction.getTetrimino();
                if (tetrimino != null) {
                    e = tetrimino.getExtents();
                } else {                         
                    try {
                        e = getComponentExtents(componentExtents, instruction.getComponentName());
                    } catch (final StackOverflowError s) {
                    }                
                }

                if (e == null) {
                    continue;
                }

                final int[] moves = instruction.getMoves();
                for (int i = 0; i < moves.length; ++i) {
                    final int move = moves[i];
                    if ((i & 1) == 0) {
                        minX = min(minX, move + e.getMinX());
                        maxX = max(maxX, move + e.getMaxX());
                    } else {
                        maxY = max(maxY, move + e.getMaxY());
                    }
                }
            }
        }
        
        if (component.getInputs() != null) {
            for (final Terminal terminal : component.getInputs()) {
                for (final HorizontalLine horizontalLine : terminal.getHorizontalLines()) {
                    minX = min(minX, horizontalLine.getMinX());
                    maxX = max(maxX, horizontalLine.getMaxX());
                    maxY = max(maxY, horizontalLine.getY() + 1); 
                }
            }
        }
        
        if (component.getOutputs() != null) {
            for (final Terminal terminal : component.getOutputs()) {
                for (final HorizontalLine horizontalLine : terminal.getHorizontalLines()) {
                    minX = min(minX, horizontalLine.getMinX());
                    maxX = max(maxX, horizontalLine.getMaxX());
                    maxY = max(maxY, horizontalLine.getY() + 1); 
                }
            }
        }
        
        extents = new Extents(minX, maxX, 0, maxY);
        componentExtents.put(component.getName(), extents);   
        
        return extents;
    }
    
    public void close() {
        executeAfter(() -> restoreLastSaved());
    }
    
    private void restoreLastSaved() {
        if (savedComponentName != null) {
            if (savedComponent != null) {
                components.put(savedComponentName, savedComponent);
            }
            if (savedExtents != null) {
                componentExtents.put(savedComponentName, savedExtents);
            }
            if (savedStructure != null) {
                structures.put(savedComponentName, savedStructure);
            }
        }
        savedComponentName = null;
        savedComponent = null;
        savedExtents = null;
        savedStructure = null;
        notifyStructuresCreated(null);
    }
    
    public void save(final String compName, final File tsFile, final String tetrisScript, final File jsFile, 
            final String javaScript, final Runnable runnable) {        
        execute(() -> saveScripts(compName, tsFile, tetrisScript, jsFile, javaScript, runnable));
    }
    
    private void saveScripts(final String compName, final File tsFile, final String tetrisScript, final File jsFile, 
            final String javaScript, final Runnable runnable) {
        
        final OutputListener outListener = outputListener;
        if (outListener != null) {
            outListener.clear();
        }
        
        saveScript(tsFile, tetrisScript);
        saveScript(jsFile, javaScript);
        
        savedComponentName = compName;
        savedComponent = components.get(compName);
        savedExtents = componentExtents.get(compName);
        savedStructure = structures.get(compName);
        
        if (runnable != null) {
            runnable.run();
        }
    }
    
    private void saveScript(final File file, final String script) {
        final OutputListener outListener = outputListener;        
        try (final BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(script);
        } catch (final IOException e) {
            if (outListener != null) {
                outListener.append(String.format("Failed to write to %s: %s", file, e.getMessage()));
            }
            return;
        }
        if (outListener != null) {
            outListener.append("Saved to " + file);
        }
    }
    
    public void export(final String componentName, final String tetrisScript, final String javaScript, 
            final String testBitStr, final int depth, final int cellSize) {
        execute(() -> exportImage(componentName, tetrisScript, javaScript, testBitStr, depth, cellSize));
    }
    
    private void exportImage(final String componentName, final String tetrisScript, final String javaScript, 
            final String testBitStr, final int depth, final double cellSize) {
        final BuildListener listener = buildListener;
        if (listener != null) {
            listener.buildStarted();
        }
        execute(() -> buildScripts(componentName, tetrisScript, javaScript, testBitStr, depth, 
                () -> exportComponent(componentName, testBitStr, depth, false, cellSize)));
    }
    
    private void exportComponent(final String componentName, final String testBitStr, final int depth, 
            final boolean clearOutput, final double cellSize) {
        
        final OutputListener outListener = outputListener;        
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
                simulator.simulate(playfield, component, depth, new StructureListener() {
                    @Override
                    public void clear() {
                        structs.clear();
                    }
                    @Override
                    public void structureLocked(final Structure lockedStructure) {
                        structs.add(lockedStructure);
                    }
                });
                minX = playfield.getMinX() - (playfield.getWidth() >> 1);
                maxX = playfield.getMaxX() - (playfield.getWidth() >> 1);
                maxY = playfield.getHeight() - 1 - playfield.getMinY();
                if (outListener != null) {
                    if (isBlank(testBitStr)) {
                        outListener.append("Ran " + componentName + " with no inputs.");
                    } else {
                        outListener.append("Ran " + componentName + " with " + testBitStr + ".");
                    }
                }
            } finally {
                returnPlayfield(playfield);
            }
        }
        
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

        final Extents extents = (component == null) ? null : componentExtents.getOrDefault(component.getName(), 
                null);
        if (extents != null) {
            minX = extents.getMinX();
            maxX = extents.getMaxX();
            maxY = extents.getMaxY();
        } else {
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
        }

        final Structure struct = new Structure(componentName, 0, 0, inputs, outputs, minX, maxX, 0, maxY,
                structs.toArray(new Structure[structs.size()]));
        new SvgGenerator().generate(System.out, struct, 15.5, cellSize, true, true, true, true, true, true);
    }   
    
    public void buildAndRun(final String componentName, final String tetrisScript, final String javaScript, 
            final String testBitStr, final int depth) {
        final BuildListener listener = buildListener;
        if (listener != null) {
            listener.buildStarted();
        }
        execute(() -> buildScripts(componentName, tetrisScript, javaScript, testBitStr, depth, null));
    }
    
    public void run(final String componentName, final String testBitStr, final int depth) {
        execute(() -> runComponent(componentName, testBitStr.trim(), depth));
    }
    
    private void runComponent(final String componentName, final String testBitStr, final int depth) {
        runComponent(componentName, testBitStr, depth, true);
    }
    
    private void runComponent(final String componentName, final String testBitStr, final int depth, 
            final boolean clearOutput) {
        
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
                simulator.simulate(playfield, component, depth, new StructureListener() {
                    @Override
                    public void clear() {
                        structs.clear();
                    }
                    @Override
                    public void structureLocked(final Structure lockedStructure) {
                        structs.add(lockedStructure);
                    }
                });
                minX = playfield.getMinX() - (playfield.getWidth() >> 1);
                maxX = playfield.getMaxX() - (playfield.getWidth() >> 1);
                maxY = playfield.getHeight() - 1 - playfield.getMinY();
                if (outListener != null) {
                    if (isBlank(testBitStr)) {
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
            
            final Extents extents = (component == null) ? null : componentExtents.getOrDefault(component.getName(), 
                    null);
            if (extents != null) {
                minX = extents.getMinX();
                maxX = extents.getMaxX();
                maxY = extents.getMaxY();
            } else {
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
            }
            
            listener.runCompleted(new Structure(componentName, 0, 0, inputs, outputs, minX, maxX, 0, maxY,
                    structs.toArray(new Structure[structs.size()])));
        }        
    }
    
    public void build(final String componentName, final String tetrisScript, final String javaScript, final int depth) {
        final BuildListener listener = buildListener;
        if (listener != null) {
            listener.buildStarted();
        }
        execute(() -> buildScripts(componentName, tetrisScript, javaScript, depth, null));
    }
    
    private void buildScripts(final String componentName, final String tetrisScript, final String javaScript, 
            final int depth, final Runnable runTask) {
        buildScripts(componentName, tetrisScript, javaScript, (String)null, depth, runTask);
    }
    
    private void buildScripts(final String componentName, final String tetrisScript, final String javaScript, 
            final String testBitStr, final int depth, final Runnable runTask) {
        final OutputListener listener = outputListener;        
        final Parser parser = new Parser();
        if (listener != null) {
            listener.clear();
            listener.append("Building.");
        }
        try {
            final Component component = parser.parse(components, componentName, 
                    new ByteArrayInputStream(tetrisScript.getBytes()));
            component.setCompiledScript(((Compilable)scriptEngine).compile(javaScript));
            updateComponentExtents();
            createStructure(component, depth, testBitStr, runTask);
            notifyStructuresCreated(componentName);
        } catch (final ParseException e) {
            e.printStackTrace(); // TODO REMOVE
            if (listener != null) {
                listener.append("Build failed.");
                listener.append(e.toString());
            }
            notifyStructuresCreated(componentName);
            return;
        } catch (final Exception e) {
            e.printStackTrace(); // TODO REMOVE
            if (listener != null) {
                listener.append("Build failed.");
                listener.append(e.getMessage());
            }
            notifyStructuresCreated(componentName);
            return;
        }
        if (listener != null) {
            listener.append("Build success.");
        }
    }
    
    public String translateToCenter(final String componentName, final String tetrisScript) {
        final OutputListener listener = outputListener;
        if (listener != null) {
            listener.clear();
        }
        final Parser parser = new Parser();
        try {
            parser.parse(components, componentName, new ByteArrayInputStream(tetrisScript.getBytes()));
            updateComponentExtents();
            final Extents extents = componentExtents.get(componentName);
            final int tx = -((extents.getMaxX() + extents.getMinX()) >> 1);
            if (listener != null) {
                listener.append("Translated " + tx);
            }
            return translate(componentName, tetrisScript, tx, 0);            
        } catch (final ParseException e) {
            if (listener != null) {
                listener.append("Build failed.");
                listener.append(e.toString());
            }
            notifyStructuresCreated(componentName);            
        } catch (final IOException e) {
            if (listener != null) {
                listener.append("Build failed.");
                listener.append(e.getMessage());
            }
            notifyStructuresCreated(componentName);
        }
        return tetrisScript;
    }
    
    private void createStructures(final int depth) {
        structures.clear();
        for (final Component component : components.values()) {
            createStructure(component, depth);
        }
    }
    
    private void createStructure(final Component component, final int depth) {
        createStructure(component, depth, null, null);
    }
    
    private void createStructure(final Component component, final int depth, final String testBitStr, 
            final Runnable nextTask) {
        
        final OutputListener listener = outputListener;
        final Playfield playfield = borrowPlayfield();
        try {                        
            simulator.init(playfield, component, testBitStr);
            final List<Structure> structs = new ArrayList<>();
            simulator.simulate(playfield, component, 0, new StructureListener() {
                @Override
                public void clear() {
                    structs.clear();
                }
                @Override
                public void structureLocked(final Structure lockedStructure) {
                    structs.add(lockedStructure);
                }
            });            
            simulator.addOutputs(playfield, component);

            final Extents extents = componentExtents.get(component.getName());
            final TerminalRectangle[][] inputs = simulator.findTerminals(component.getInputs(), 0, 0); 
            final TerminalRectangle[][] outputs = simulator.findTerminals(component.getOutputs(), 0, 0);
            
            int minX;
            int maxX;
            int maxY;
            if (extents != null) {
                minX = extents.getMinX();
                maxX = extents.getMaxX();
                maxY = extents.getMaxY();
            } else {
                minX = playfield.getMinX() - (playfield.getWidth() >> 1);
                maxX = playfield.getMaxX() - (playfield.getWidth() >> 1);
                maxY = playfield.getHeight() - 1 - playfield.getMinY();  
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
            }
            structures.put(component.getName(), new Structure(component.getName(), 0, 0, inputs, outputs, minX, maxX, 0, 
                    maxY, structs.toArray(new Structure[structs.size()])));
        } catch(final StackOverflowError e) {                    
            if (listener != null) {
                listener.append("Error: Definition of " + component.getName() + " contains itself.");
            }
        } finally {
            returnPlayfield(playfield);
        }        
        
        if (nextTask != null) {
            execute(nextTask);
        } else if (component.getName() != null && testBitStr != null) {
            execute(() -> runComponent(component.getName(), testBitStr.trim(), depth, false));
        }        
    }
    
    private int findComponentNameSortGroup(final String componentName) {
        if (componentName.length() >= 2) {
            switch(componentName.charAt(0)) {
                case 'i': 
                    if (componentName.length() >= 3) {
                        final char c = componentName.charAt(1);
                        if (c == 'l' || c == 'r') {
                            final char d = componentName.charAt(2);
                            if (d >= '0' && d <= '9') {
                                return 1;
                            }
                        }
                    }
                    break;
                case 's': {
                    final char c = componentName.charAt(1);
                    if (c >= '0' && c <= '9') {
                        return 2;
                    }
                    break;
                }
                case 'z': {
                    final char c = componentName.charAt(1);
                    if (c >= '0' && c <= '9') {
                        return 3;
                    }
                    break;
                }
                case '_':
                    return 4;
            }
        }
        return 0;
    }
    
    private void notifyStructuresCreated(final String currentComponentName) {        
        final Set<String> names = new HashSet<>(components.keySet());        
        if (currentComponentName != null) {
            names.remove(currentComponentName);
            updateDependencies();
            final Set<String> depends = dependencies.getOrDefault(currentComponentName, null);
            if (depends != null) {
                names.removeAll(depends);
            }
        }
        final List<String> ns = new ArrayList<>(names);
        final String[] componentNames = ns.toArray(new String[ns.size()]);
        Arrays.sort(componentNames, (a, b) -> {
            final int groupA = findComponentNameSortGroup(a);
            final int groupB = findComponentNameSortGroup(b);
            if (groupA == groupB) {
                switch(groupA) {
                    case 1: 
                        return Integer.compare(Integer.parseInt(a.substring(2)), Integer.parseInt(b.substring(2)));
                    case 2:
                    case 3:
                        return Integer.compare(Integer.parseInt(a.substring(1)), Integer.parseInt(b.substring(1)));
                    default: {
                        final int result = a.compareToIgnoreCase(b);
                        if (result != 0) {
                            return result;
                        }
                        return b.compareTo(a);
                    }
                }                
            }
            if (groupA < groupB) {
                return -1;
            }            
            return 1;
        }); 
        
        final BuildListener listener = this.buildListener;
        if (listener != null) {
            final Map structs = new HashMap<>(structures);
            if (currentComponentName != null) {
                structs.remove(currentComponentName);
            }
            listener.buildCompleted(componentNames, structs);
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
                    taskMonitor.notifyAll();
                }
            });
        }
    }
    
    private void executeAfter(final Runnable runnable) {
        new Thread(() -> {
            synchronized(taskMonitor) {
                while (taskCount != 0) {
                    try {
                        taskMonitor.wait();
                    } catch (final InterruptedException e) {                        
                    }
                }
            }
            execute(runnable);
        }).start();
    }
    
    public String translate(final String filename, final String tetrisScript, 
            final int offsetX, final int offsetY) throws IOException, ParseException {
        return new Parser().translate(components, filename, tetrisScript, offsetX, offsetY);
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
            playfield = new Playfield(8192, 4096, 1);
        }
        return playfield;
    }
    
    private class Files {
        
        private File tFile;
        private File jsFile;

        public File getTFile() {
            return tFile;
        }

        public void setTFile(final File tFile) {
            this.tFile = tFile;
        }

        public File getJsFile() {
            return jsFile;
        }

        public void setJsFile(final File jsFile) {
            this.jsFile = jsFile;
        }
    }
}
