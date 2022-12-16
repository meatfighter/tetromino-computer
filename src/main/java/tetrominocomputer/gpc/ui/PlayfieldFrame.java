package tetrominocomputer.gpc.ui;

import static java.awt.EventQueue.invokeAndWait;
import static java.awt.EventQueue.isDispatchThread;
import java.awt.event.KeyEvent;
import tetrominocomputer.util.Ui;

public class PlayfieldFrame extends javax.swing.JFrame {

    private static final int LEFT_KEY_CODE = KeyEvent.VK_LEFT;
    private static final int RIGHT_KEY_CODE = KeyEvent.VK_RIGHT;
    private static final int DOWN_KEY_CODE = KeyEvent.VK_DOWN;
    private static final int START_KEY_CODE = KeyEvent.VK_ENTER;
    private static final int CCW_ROTATE_CODE = KeyEvent.VK_Z;
    private static final int CW_ROTATE_CODE = KeyEvent.VK_X;
    
    private final PlayfieldPanel playfieldPanel = new PlayfieldPanel();

    private boolean left;
    private boolean right;
    private boolean down;
    private boolean ccwRotate;
    private boolean cwRotate;
    private boolean start;
    
    private int leftAsserted;
    private int rightAsserted;
    private int downAsserted;    
    private int ccwRotateAsserted;
    private int cwRotateAsserted;
    private int startAsserted;
    
    private boolean lastCcwRotate;
    private boolean lastCwRotate;
    private boolean lastStart;
  
    
    /**
     * Creates new form PlayfieldFrame
     */
    public PlayfieldFrame() {
        initComponents();
        Ui.initIcons(this);
        setContentPane(playfieldPanel);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        switch (evt.getKeyCode()) {
            case LEFT_KEY_CODE:
                left = true;
                if (leftAsserted == 0) {
                    leftAsserted = 1;
                }
                break;
            case RIGHT_KEY_CODE:                
                right = true;
                if (rightAsserted == 0) {
                    rightAsserted = 1;
                }
                break;
            case DOWN_KEY_CODE:                
                down = true;
                if (downAsserted == 0) {
                    downAsserted = 1;
                }
                break;    
            case START_KEY_CODE:
                start = true;
                if (startAsserted == 0) {
                    startAsserted = 1;
                }
                break;
            case CCW_ROTATE_CODE:                
                ccwRotate = true;
                if (ccwRotateAsserted == 0) {
                    ccwRotateAsserted = 1;
                }
                break;
            case CW_ROTATE_CODE:                
                cwRotate = true;
                if (cwRotateAsserted == 0) {
                    cwRotateAsserted = 1;
                }
                break;                 
        }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        switch (evt.getKeyCode()) {
            case LEFT_KEY_CODE:                
                left = false;
                if (leftAsserted == 2) {
                    leftAsserted = 0;
                }
                break;
            case RIGHT_KEY_CODE:
                right = false;
                if (rightAsserted == 2) {
                    rightAsserted = 0;
                }
                break;
            case DOWN_KEY_CODE:
                down = false;
                if (downAsserted == 2) {
                    downAsserted = 0;
                }
                break;                
            case START_KEY_CODE:
                start = false;
                if (startAsserted == 2) {
                    startAsserted = 0;
                }
                break;                
            case CCW_ROTATE_CODE:                
                ccwRotate = false;
                if (ccwRotateAsserted == 2) {
                    ccwRotateAsserted = 0;
                }
                break;
            case CW_ROTATE_CODE:                
                cwRotate = false;
                if (cwRotateAsserted == 2) {
                    cwRotateAsserted = 0;
                }
                break; 
        }
    }//GEN-LAST:event_formKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public void update(final PlayfieldModel playfieldModel) {
        if (!isDispatchThread()) {
            try {
                invokeAndWait(() -> update(playfieldModel));
            } catch (final Exception e) {                
            }
            return;
        }
        
        playfieldModel.setLeftPressed(left || leftAsserted == 1);
        if (leftAsserted == 1) {
            leftAsserted = left ? 2 : 0;
        }
        
        playfieldModel.setRightPressed(right || rightAsserted == 1);
        if (rightAsserted == 1) {
            rightAsserted = right ? 2 : 0;
        }
        
        playfieldModel.setDownPressed(down || downAsserted == 1);
        if (downAsserted == 1) {
            downAsserted = down ? 2 : 0;
        }        
        
        playfieldModel.setCcwRotatePressed((ccwRotate || ccwRotateAsserted == 1) && !lastCcwRotate);
        lastCcwRotate = ccwRotate || ccwRotateAsserted == 1;
        if (ccwRotateAsserted == 1) {
            ccwRotateAsserted = ccwRotate ? 2 : 0;
        }
        
        playfieldModel.setCwRotatePressed((cwRotate || cwRotateAsserted == 1) && !lastCwRotate);
        lastCwRotate = cwRotate || cwRotateAsserted == 1;
        if (cwRotateAsserted == 1) {
            cwRotateAsserted = cwRotate ? 2 : 0;
        }        
        
        playfieldModel.setStartPressed((start || startAsserted == 1) && !lastStart);
        lastStart = start || startAsserted == 1;
        if (startAsserted == 1) {
            startAsserted = start ? 2 : 0;
        }
        
        playfieldPanel.update(playfieldModel);
    }
    
    public void setFramesPerSecond(final double framesPerSecond) {
        if (!isDispatchThread()) {
            try {
                invokeAndWait(() -> setFramesPerSecond(framesPerSecond));
            } catch (final Exception e) {                
            }
            return;
        }
        
        setTitle(String.format("Frames/sec: %.2f", framesPerSecond));
    }
}
