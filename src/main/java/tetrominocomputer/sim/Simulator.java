package tetrominocomputer.sim;

import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import tetrominocomputer.tse.app.StructureListener;

public class Simulator {
        
    private final List<Bindings> bindingsPool = Collections.synchronizedList(new ArrayList<>());
    
    private final ScriptEngine scriptEngine;
    private final Map<String, Component> components;
    private final Map<String, Extents> componentExtents;
        
    public Simulator(final ScriptEngine scriptEngine, final Map<String, Component> components, 
            final Map<String, Extents> componentExtents) {
        this.scriptEngine = scriptEngine;
        this.components = components;
        this.componentExtents = componentExtents;
    }
    
    public void init(final Playfield playfield, final Component component, final String inputBits) {
        init(playfield, component, inputBits, playfield.getWidth() >> 1, playfield.getHeight() - 1, 
                playfield.getMaxValue());
    }
    
    public void init(final Playfield playfield, final Component component, final String inputBits, 
            final int originX, final int originY, final int cellValue) {
        
        final Terminal[] inputs = component.getInputs();
        if (inputs == null || inputBits == null) {
            return;
        }
                      
        final String inBits = inputBits.replaceAll("\\s*", "");
        for (int i = 0; i < inBits.length() && i < inputs.length; ++i) {
            final boolean one = inBits.charAt(i) == '1';
            final HorizontalLine[] horizontalLines = inputs[i].getHorizontalLines();
            for (int j = horizontalLines.length - 1; j >= 0; --j) {
                final HorizontalLine horizontalLine = horizontalLines[j];
                final int maxX = horizontalLine.getMaxX();                
                int y = originY - horizontalLine.getY();                          
                for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {
                    playfield.set(originX + x, y, cellValue);                    
                }
                if (one) {
                    --y;
                    for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {
                        playfield.set(originX + x, y, cellValue);                    
                    }                    
                }
            }
        }
    }
    
    public void addOutputs(final Playfield playfield, final Component component) {
        addOutputs(playfield, component, playfield.getWidth() >> 1, playfield.getHeight() - 1, playfield.getMaxValue());
    }
    
    public void addOutputs(final Playfield playfield, final Component component, final int originX, final int originY, 
            final int cellValue) {
        
        final Terminal[] outputs = component.getOutputs();
        if (outputs == null) {
            return;
        }
        
        for (int i = outputs.length - 1; i >= 0; --i) {
            final HorizontalLine[] horizontalLines = outputs[i].getHorizontalLines();
            for (int j = horizontalLines.length - 1; j >= 0; --j) {
                final HorizontalLine horizontalLine = horizontalLines[j];
                final int y = originY - horizontalLine.getY();
                final int maxX = horizontalLine.getMaxX();
                for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {
                    playfield.set(originX + x, y, cellValue);
                }
            }            
        }
    }
    
    public TerminalRectangle[][] readTerminals(final Terminal[] terminals, final int originX, final int originY, 
            final Playfield playfield) {
        
        if (terminals == null) {
            return new TerminalRectangle[0][];
        }
        
        final int ox = playfield.getWidth() >> 1;
        final int oy = playfield.getHeight() - 1;
        
        final TerminalRectangle[][] rectangles = new TerminalRectangle[terminals.length][];
        for (int i = terminals.length - 1; i >= 0; --i) {
            TerminalState state = TerminalState.UNKNOWN;            
            final HorizontalLine[] horizontalLines = terminals[i].getHorizontalLines();            
            outer: for (int j = horizontalLines.length - 1; j >= 0; --j) {
                final HorizontalLine horizontalLine = horizontalLines[j];
                final int maxX = horizontalLine.getMaxX();                
                int y = oy - horizontalLine.getY() - 1;                
                for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {
                    if (playfield.isSolid(ox + x, y)) {
                        state = TerminalState.ONE;
                        break outer;
                    }
                }
                ++y;
                for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {
                    if (playfield.isSolid(ox + x, y)) {
                        state = TerminalState.ZERO;
                        break outer;
                    }
                }
            }
            final TerminalRectangle[] rects = rectangles[i] = new TerminalRectangle[horizontalLines.length];
            for (int j = horizontalLines.length - 1; j >= 0; --j) {
                final HorizontalLine horizontalLine = horizontalLines[j];
                final int y = originY + horizontalLine.getY();
                rects[j] = new TerminalRectangle(originX + horizontalLine.getMinX(), y, 
                        horizontalLine.getMaxX() - horizontalLine.getMinX() + 1, state);
            }
        }

        return rectangles;
    }    
    
    public TerminalRectangle[][] findTerminals(final Terminal[] terminals, final int originX, final int originY) {
        return findTerminals(terminals, originX, originY, (Boolean[])null);
    }
    
    public TerminalRectangle[][] findTerminals(final Terminal[] terminals, final int originX, final int originY, 
            final String testBitsStr) {
        
        if (terminals == null) {
            return new TerminalRectangle[0][];
        }
        
        final String inBits = (testBitsStr == null) ? null : testBitsStr.replaceAll("\\s*", "");
        final TerminalRectangle[][] rectangles = new TerminalRectangle[terminals.length][];
        for (int i = terminals.length - 1; i >= 0; --i) {
            TerminalState state = TerminalState.UNKNOWN;
            if (inBits != null && i < inBits.length()) {
                state = inBits.charAt(i) == '1' ? TerminalState.ONE : TerminalState.ZERO;
            }
            final HorizontalLine[] horizontalLines = terminals[i].getHorizontalLines();
            final TerminalRectangle[] rects = rectangles[i] = new TerminalRectangle[horizontalLines.length];
            for (int j = horizontalLines.length - 1; j >= 0; --j) {
                final HorizontalLine horizontalLine = horizontalLines[j];
                final int y = originY + horizontalLine.getY();
                rects[j] = new TerminalRectangle(originX + horizontalLine.getMinX(), y, 
                        horizontalLine.getMaxX() - horizontalLine.getMinX() + 1, state);
            }
        }

        return rectangles;
    }  
    
    public TerminalRectangle[][] findTerminals(final Terminal[] terminals, final int originX, final int originY, 
            final Boolean[] testBits) {
        
        if (terminals == null) {
            return new TerminalRectangle[0][];
        }
        
        final TerminalRectangle[][] rectangles = new TerminalRectangle[terminals.length][];
        for (int i = terminals.length - 1; i >= 0; --i) {
            TerminalState state = TerminalState.UNKNOWN;
            if (testBits != null && i < testBits.length && testBits[i] != null) {                
                state = testBits[i] ? TerminalState.ONE : TerminalState.ZERO;
            }
            final HorizontalLine[] horizontalLines = terminals[i].getHorizontalLines();
            final TerminalRectangle[] rects = rectangles[i] = new TerminalRectangle[horizontalLines.length];
            for (int j = horizontalLines.length - 1; j >= 0; --j) {
                final HorizontalLine horizontalLine = horizontalLines[j];
                final int y = originY + horizontalLine.getY();
                rects[j] = new TerminalRectangle(originX + horizontalLine.getMinX(), y, 
                        horizontalLine.getMaxX() - horizontalLine.getMinX() + 1, state);
            }
        }

        return rectangles;
    }
    
    public void simulate(final Playfield playfield, final Component component, final int depth)
            throws SimulatorException {
        simulate(playfield, component, playfield.getWidth() >> 1, playfield.getHeight() - 1, depth);
    }
    
    public void simulate(final Playfield playfield, final Component component, final int originX, final int originY, 
            final int depth) throws SimulatorException {
        simulate(playfield, component, component.getName(), originX, originY, depth, null);
    }
    
    // Used by buildAndRun
    public void simulate(final Playfield playfield, final Component component, final int depth, 
            final StructureListener listener) throws SimulatorException {
        simulate(playfield, component, component.getName(), playfield.getWidth() >> 1, playfield.getHeight() - 1, depth, 
                listener);
    }

    public void simulate(final Playfield playfield, final Component component, final String alias, final int originX, 
            final int originY, final int depth, final StructureListener listener) throws SimulatorException {
        
        if (depth <= 0 && !component.getName().startsWith("_")) {
            emulate(playfield, component, alias, originX, originY, listener);
        } else {
            final Instruction[] instructions = component.getInstructions();
            if (instructions == null) {
                return;
            }
            for (int i = 0; i < instructions.length; ++i) {
                simulate(playfield, instructions[i], originX, originY, depth, listener);
            }
        }
    }
    
    private void simulate(final Playfield playfield, final Instruction instruction, final int originX, 
            final int originY, final int depth, final StructureListener listener) throws SimulatorException {

        final int[] moves = instruction.getMoves();
        
        final String componentName = instruction.getComponentName();
        if (componentName != null) {
            final Component component = components.getOrDefault(componentName, null);
            if (component != null) {          
                simulate(playfield, component, instruction.getAlias(), originX + moves[0], originY - moves[1], 
                        depth - 1, listener);                
            }
            return;
        }
        
        final Tetromino tetromino = instruction.getTetromino();
        int x = originX + moves[0];
        int y = playfield.getMinY() - 3;
        
        for (int i = 1; i < moves.length; ++i) {
            if ((i & 1) == 1) {
                y = moveDown(playfield, tetromino, x, y, originY - moves[i]);
            } else {
                x = moveHorizontally(playfield, tetromino, x, y, originX + moves[i]);
            }
        }
        
        y = hardDrop(playfield, tetromino, x, y);
        lock(playfield, tetromino, x, y);
        
        if (listener != null) {
            listener.structureLocked(new Structure(tetromino, x - (playfield.getWidth() >> 1), 
                    playfield.getHeight() - 1 - y));
        }
    }
    
    public void emulate(final Playfield playfield, final Component component, final String alias, final int originX, 
            final int originY, final StructureListener listener) throws SimulatorException {

        final Terminal[] inputs = component.getInputs();        
        if (inputs == null) {
            return;
        }
        
        final Boolean[] inputValues = new Boolean[inputs.length];
        outer: for (int i = inputs.length - 1; i >= 0; --i) {
            final HorizontalLine[] horizontalLines = inputs[i].getHorizontalLines();
            for (int j = horizontalLines.length - 1; j >= 0; --j) {
                final HorizontalLine horizontalLine = horizontalLines[j];
                final int maxX = horizontalLine.getMaxX();                
                int y = originY - horizontalLine.getY() - 1;                
                for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {
                    if (playfield.isSolid(originX + x, y)) {
                        inputValues[i] = true;
                        continue outer;
                    }
                }
                ++y;
                for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {
                    if (playfield.isSolid(originX + x, y)) {
                        inputValues[i] = false;
                        continue outer;
                    }
                }
            }
        }
        
        final Terminal[] outputs = component.getOutputs();
        if (outputs == null) {
            return;
        }
        
        final Boolean[] outputValues = new Boolean[outputs.length];
        
        final CompiledScript compiledScript = component.getCompiledScript();
        if (compiledScript != null) {
            final Bindings bindings = borrowBindings();
            try {
                for (int i = inputs.length - 1; i >= 0; --i) {
                    bindings.put(inputs[i].getName(), inputValues[i]);
                }
                for (int i = outputs.length - 1; i >= 0; --i) {
                    bindings.put(outputs[i].getName(), false);
                }
                compiledScript.eval(bindings);
                for (int i = outputs.length - 1; i >= 0; --i) {
                    final Object value = bindings.get(outputs[i].getName());
                    if (value != null) {
                        if (value instanceof Boolean) {
                            outputValues[i] = (Boolean) value;
                        } else {
                            throw new SimulatorException(String.format(
                                    "Output node %s in %s is %s rather than Boolean.", outputs[i].getName(), 
                                    component.getName(), value.getClass().getName()));
                        }
                    }
                }
            } catch(final ScriptException e) {
                throw new SimulatorException(e);
            } finally {
                returnBindings(bindings);
            }
        } 
        
        for (int i = outputs.length - 1; i >= 0; --i) {
            if (outputValues[i] == null) {
                continue;
            }
            final int color = playfield.getMaxValue();
            final HorizontalLine[] horizontalLines = outputs[i].getHorizontalLines();
            for (int j = horizontalLines.length - 1; j >= 0; --j) {
                final HorizontalLine horizontalLine = horizontalLines[j];
                final int maxX = horizontalLine.getMaxX();
                int y = originY - horizontalLine.getY();                
                for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {
                    playfield.set(originX + x, y, color);
                }
                if (outputValues[i]) {
                    --y;
                    for (int x = horizontalLine.getMinX(); x <= maxX; ++x) {
                        playfield.set(originX + x, y, color);
                    }                    
                }
            }
        }
        
        if (listener != null) { 
            final Extents extents = componentExtents.getOrDefault(component.getName(), null);
            final TerminalRectangle[][] inputRects = findTerminals(inputs, 0, 0, inputValues);
            final TerminalRectangle[][] outputRects = findTerminals(outputs, 0, 0, outputValues);
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;
            if (extents != null) {
                minX = extents.getMinX();
                maxX = extents.getMaxX();
                minY = 0;
                maxY = extents.getMaxY();
            } else {
                for (int i = inputRects.length - 1; i >= 0; --i) {
                    final TerminalRectangle[] ins = inputRects[i];
                    for (int j = ins.length - 1; j >= 0; --j) {
                        final TerminalRectangle input = ins[j];
                        minX = min(minX, input.x);
                        maxX = max(maxX, input.x + input.width - 1);
                        minY = min(minY, input.y);
                        maxY = max(maxY, input.y + 1);
                    }
                }
                for (int i = outputRects.length - 1; i >= 0; --i) {
                    final TerminalRectangle[] outs = outputRects[i];
                    for (int j = outs.length - 1; j >= 0; --j) {
                        final TerminalRectangle output = outs[j];
                        minX = min(minX, output.x);
                        maxX = max(maxX, output.x + output.width - 1);
                        minY = min(minY, output.y);
                        maxY = max(maxY, output.y + 1);
                    }
                } 
            }            
            
            listener.structureLocked(new Structure(alias, originX - (playfield.getWidth() >> 1), 
                    playfield.getHeight() - 1 - originY, inputRects, outputRects, minX, maxX, minY, maxY));
        }
    }
    
    private int moveHorizontally(final Playfield playfield, final Tetromino tetromino, final int startX, 
            final int startY, final int targetX) {        
        
        if (targetX < startX) {
            return moveLeft(playfield, tetromino, startX, startY, targetX);
        } 
        
        return moveRight(playfield, tetromino, startX, startY, targetX);
    }

    private int moveLeft(final Playfield playfield, final Tetromino tetromino, final int startX,
            final int startY, final int targetX) {

        final Point[] leftBlocks = tetromino.getLeftBlocks();
        for (int x = startX; x >= targetX; --x) {
            for (int i = leftBlocks.length - 1; i >= 0; --i) {
                final Point block = leftBlocks[i];
                if (playfield.get(x + block.x, startY + block.y) != 0) {
                    return x + 1;
                }
            }
        }
        
        return targetX;
    }

    private int moveRight(final Playfield playfield, final Tetromino tetromino, final int startX,
            final int startY, final int targetX) {

        final Point[] rightBlocks = tetromino.getRightBlocks();
        for (int x = startX; x <= targetX; ++x) {
            for (int i = rightBlocks.length - 1; i >= 0; --i) {
                final Point block = rightBlocks[i];
                if (playfield.get(x + block.x, startY + block.y) != 0) {
                    return x - 1;
                }
            }
        }
        
        return targetX;
    }
    
    private int moveDown(final Playfield playfield, final Tetromino tetromino, final int startX, final int startY,
            final int targetY) {
        
        final Point[] bottomBlocks = tetromino.getBottomBlocks();
        for (int y = startY; y <= targetY; ++y) {
            for (int i = bottomBlocks.length - 1; i >= 0; --i) {
                final Point block = bottomBlocks[i];
                if (playfield.get(startX + block.x, y + block.y) != 0) {
                    return y - 1;
                }
            }
        }
        
        return targetY;
    }
    
    private int hardDrop(final Playfield playfield, final Tetromino tetromino, final int startX, final int startY) {
        
        final Point[] bottomBlocks = tetromino.getBottomBlocks();
        for (int y = startY; y < playfield.getHeight(); ++y) {
            for (int i = bottomBlocks.length - 1; i >= 0; --i) {
                final Point block = bottomBlocks[i];
                if (playfield.get(startX + block.x, y + block.y) != 0) {
                    return y - 1;
                }
            }
        }
        
        return playfield.getHeight() - 1;
    }
    
    private void lock(final Playfield playfield, final Tetromino tetromino, final int lockX, final int lockY) {
        
        int value = tetromino.getGroupIndex() + 1;
        if (value > playfield.getMaxValue()) {
            value = playfield.getMaxValue();
        }
        
        final Point[] blocks = tetromino.getBlocks();
        for (int i = blocks.length - 1; i >= 0; --i) {
            final Point block = blocks[i];
            playfield.set(lockX + block.x, lockY + block.y, value);
        }
    }
    
    private void returnBindings(final Bindings bindings) {
        bindings.clear();
        bindingsPool.add(bindings);
    }
    
    private Bindings borrowBindings() {
        Bindings bindings = null;
        synchronized(bindingsPool) {
            if (!bindingsPool.isEmpty()) {
                bindings = bindingsPool.remove(bindingsPool.size() - 1);
            }
        }
        if (bindings == null) {
            bindings = scriptEngine.createBindings();          
        }
        return bindings;
    }     
}
