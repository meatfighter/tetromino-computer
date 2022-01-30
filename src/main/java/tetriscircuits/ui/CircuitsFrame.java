package tetriscircuits.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import tetriscircuits.BuildListener;
import tetriscircuits.Controller;
import tetriscircuits.Structure;

public class CircuitsFrame extends javax.swing.JFrame {
    
    private static final int COORDINATES_SPACES = 15;
    
    public static final Icon QUESTION_ICON = new ImageIcon(CircuitsFrame.class.getResource("/icons/question.png"));
    public static final Icon EXCLAMATION_ICON = new ImageIcon(CircuitsFrame.class
            .getResource("/icons/exclamation.png"));

    private Controller controller;
    private Map<String, Structure> structures;
    
    private String circuitsDirectory;
    private File tetrisScriptFile;
    private File javaScriptFile;
    private String componentName;
    
    private JDialog goToDialog;
    private JDialog findReplaceDialog;
    
    private String lastFindWhat;
    private boolean lastBackwards; 
    private boolean lastMatchCase;
    private boolean lastRegex; 
    private boolean lastWrapAround;
    
    private long tetrisScriptChangeCount;
    private long javaScriptChangeCount;

    /**
     * Creates new form CircuitsFrame
     */
    public CircuitsFrame() {
        initComponents();
        toolBar.setLayout(new WrapLayout(WrapLayout.LEFT, 0, 0));
        UiUtil.setTextFieldColumns(testTextField, 32);
        initEditableComboBox(compEditComboBox); 
        setComponentName(Controller.DEFAULT_COMPONENT_NAME);
    }
    
    private void initEditableComboBox(final JComboBox comboBox) {
        final JTextField textField = (JTextField)comboBox.getEditor().getEditorComponent();
        textField.setSelectionColor(new Color(0x3C3F41));        
        AutoCompletion.enable(comboBox);
        comboBox.setPreferredSize(testTextField.getPreferredSize());
        comboBox.setMinimumSize(testTextField.getMinimumSize());
        comboBox.setMaximumSize(testTextField.getMaximumSize());
        compEditComboBox.setModel(new DefaultComboBoxModel(new String[0]));
    }
    
    private String getComponentDirectory() {
        if (circuitsDirectory == null) {
            circuitsDirectory = System.getProperty("user.dir") + File.separator + Controller.WORKSPACE_DIR;
            final File componentsDir = new File(circuitsDirectory);
            if (!(componentsDir.exists() && componentsDir.isDirectory())) {
                circuitsDirectory = System.getProperty("user.dir");
            }            
        }
        return circuitsDirectory;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(final Controller controller) {
        this.controller = controller;
        circuitsEditorPanel.setController(controller);
        controller.setBuildListener(new BuildListener() {
            @Override
            public void buildStarted() {
                if (!EventQueue.isDispatchThread()) {
                    EventQueue.invokeLater(this::buildStarted);
                    return;
                }
                runButton.setEnabled(false);
            }
            @Override
            public void buildCompleted(final String[] componentNames, final Map<String, Structure> structures) {
                if (!EventQueue.isDispatchThread()) {
                    EventQueue.invokeLater(() -> buildCompleted(componentNames, structures));
                    return;
                }
                CircuitsFrame.this.structures = structures;
                runButton.setEnabled(true);
                final String selectedItem = (String)compEditComboBox.getSelectedItem();
                compEditComboBox.setModel(new DefaultComboBoxModel(componentNames));
                if (selectedItem != null) {
                    EventQueue.invokeLater(() -> compEditComboBox.setSelectedItem(selectedItem));
                } 
            }
        });
        controller.setOpenListener((compName, tsFile, tetrisScript, jsFile, javaScript, testBits) -> {
            setComponentName(compName);
            tetrisScriptFile = tsFile;
            circuitsEditorPanel.setTetrisScript(tetrisScript);
            javaScriptFile = jsFile;
            circuitsEditorPanel.setJavaScript(javaScript);
            testTextField.setText(testBits);
            tetrisScriptChangeCount = circuitsEditorPanel.getTetrisScriptChangeCount();
            javaScriptChangeCount = circuitsEditorPanel.getJavaScriptChangeCount();
        });
    }
    
    public void init() {
        circuitsEditorPanel.init();
        circuitsEditorPanel.setCircuitsFrame(this);
    }
    
    public void zoom(final int delta) {
        int value = (int)cellSizeSpinner.getValue() + delta;
        final int maximum = (int)((SpinnerNumberModel)cellSizeSpinner.getModel()).getMaximum();
        if (value < 1) {
            value = 1;
        } else if (value > maximum) {
            value = maximum;
        } 
        cellSizeSpinner.setValue(value);
    }
    
    public void clearComponentCoordinates() {
        coordinatesLabel.setText(leftPadCoordinatesString(""));
    }
    
    public void setComponentCoordinates(final int cellX, final int cellY) {
        coordinatesLabel.setText(leftPadCoordinatesString(String.format("%d:%d", cellX, cellY)));
    }
    
    public void clearCursorCoordinates() {
        cursorLabel.setText(rightPadCoordinatesString(""));
    }
    
    public void setCursorCoordinates(final int lineNumber, final int columnNumber) {
        cursorLabel.setText(rightPadCoordinatesString(String.format("%d:%d", lineNumber, columnNumber)));
    }
    
    private String leftPadCoordinatesString(final String str) {
        final StringBuilder sb = new StringBuilder();
        for (int i = COORDINATES_SPACES - str.length() - 1; i >= 0; --i) {
            sb.append(' ');
        }
        sb.append(str);
        return sb.toString();
    }
    
    private String rightPadCoordinatesString(final String str) {
        final StringBuilder sb = new StringBuilder(str);
        for (int i = COORDINATES_SPACES - str.length() - 1; i >= 0; --i) {
            sb.append(' ');
        }
        return sb.toString();
    }
    
    public void buildAndRun(final String tetrisScript, final String javaScript) {        
        controller.buildAndRun(componentName, tetrisScript, javaScript, testTextField.getText(), 
                (int)depthSpinner.getValue());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        circuitsEditorPanel = new tetriscircuits.ui.CircuitsEditorPanel();
        toolBar = new javax.swing.JToolBar();
        testTextField = new javax.swing.JTextField();
        runButton = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        compEditComboBox = new javax.swing.JComboBox<>();
        addComponentButton = new javax.swing.JButton();
        tdButton = new javax.swing.JButton();
        tlButton = new javax.swing.JButton();
        tuButton = new javax.swing.JButton();
        trButton = new javax.swing.JButton();
        jdButton = new javax.swing.JButton();
        separator4 = new javax.swing.JToolBar.Separator();
        jlButton = new javax.swing.JButton();
        juButton = new javax.swing.JButton();
        jrButton = new javax.swing.JButton();
        separator5 = new javax.swing.JToolBar.Separator();
        zhButton = new javax.swing.JButton();
        zvButton = new javax.swing.JButton();
        separator6 = new javax.swing.JToolBar.Separator();
        oButton = new javax.swing.JButton();
        separator7 = new javax.swing.JToolBar.Separator();
        shButton = new javax.swing.JButton();
        svButton = new javax.swing.JButton();
        separator8 = new javax.swing.JToolBar.Separator();
        ldButton = new javax.swing.JButton();
        llButton = new javax.swing.JButton();
        luButton = new javax.swing.JButton();
        lrButton = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        ivButton = new javax.swing.JButton();
        ihButton = new javax.swing.JButton();
        bottomPanel = new javax.swing.JPanel();
        cellSizeSpinner = new javax.swing.JSpinner();
        jSeparator8 = new javax.swing.JSeparator();
        coordinatesLabel = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        playfieldHeightSpinner = new javax.swing.JSpinner();
        playfieldWidthSpinner = new javax.swing.JSpinner();
        jSeparator10 = new javax.swing.JSeparator();
        depthSpinner = new javax.swing.JSpinner();
        cursorLabel = new javax.swing.JLabel();
        centerButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        openMenuItem = new javax.swing.JMenuItem();
        closeMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        undoMenuItem = new javax.swing.JMenuItem();
        redoMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        findMenuItem = new javax.swing.JMenuItem();
        findNextMenuItem = new javax.swing.JMenuItem();
        findPreviousMenuItem = new javax.swing.JMenuItem();
        replaceMenuItem = new javax.swing.JMenuItem();
        goToMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        translateMenuItem = new javax.swing.JMenuItem();
        translateToCenterMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();
        licenseMenuItem = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("TetrisScript Editor");
        setPreferredSize(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        circuitsEditorPanel.setPreferredSize(null);

        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setMargin(new java.awt.Insets(0, 4, 0, 4));
        toolBar.setMaximumSize(null);
        toolBar.setMinimumSize(null);
        toolBar.setName(""); // NOI18N
        toolBar.setPreferredSize(null);

        testTextField.setColumns(15);
        testTextField.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        testTextField.setToolTipText("Test Bit String");
        testTextField.setPreferredSize(null);
        toolBar.add(testTextField);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/run.png"))); // NOI18N
        runButton.setToolTipText("Run");
        runButton.setContentAreaFilled(false);
        runButton.setFocusable(false);
        runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        toolBar.add(runButton);
        toolBar.add(jSeparator6);

        compEditComboBox.setEditable(true);
        compEditComboBox.setFont(new java.awt.Font("Monospaced", 1, 13)); // NOI18N
        compEditComboBox.setModel(new DefaultComboBoxModel(new String[] { "" }));
        compEditComboBox.setMaximumSize(null);
        compEditComboBox.setMinimumSize(null);
        compEditComboBox.setPreferredSize(null);
        compEditComboBox.setPrototypeDisplayValue("MMMMMMMMMMMMMMM");
        toolBar.add(compEditComboBox);

        addComponentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        addComponentButton.setToolTipText("Add Component");
        addComponentButton.setContentAreaFilled(false);
        addComponentButton.setFocusable(false);
        addComponentButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addComponentButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addComponentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addComponentButtonActionPerformed(evt);
            }
        });
        toolBar.add(addComponentButton);

        tdButton.setIcon(TetriminoRenderer.TD);
        tdButton.setToolTipText("td");
        tdButton.setContentAreaFilled(false);
        tdButton.setFocusable(false);
        tdButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tdButton.setIconTextGap(0);
        tdButton.setMargin(null);
        tdButton.setPreferredSize(new java.awt.Dimension(40, 40));
        tdButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tdButtonActionPerformed(evt);
            }
        });
        toolBar.add(tdButton);

        tlButton.setIcon(TetriminoRenderer.TL);
        tlButton.setToolTipText("tl");
        tlButton.setContentAreaFilled(false);
        tlButton.setFocusable(false);
        tlButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tlButton.setMargin(null);
        tlButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tlButtonActionPerformed(evt);
            }
        });
        toolBar.add(tlButton);

        tuButton.setIcon(TetriminoRenderer.TU);
        tuButton.setToolTipText("tu");
        tuButton.setContentAreaFilled(false);
        tuButton.setFocusable(false);
        tuButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tuButton.setMargin(null);
        tuButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tuButtonActionPerformed(evt);
            }
        });
        toolBar.add(tuButton);

        trButton.setIcon(TetriminoRenderer.TR);
        trButton.setToolTipText("tr");
        trButton.setContentAreaFilled(false);
        trButton.setFocusable(false);
        trButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        trButton.setMargin(null);
        trButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        trButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trButtonActionPerformed(evt);
            }
        });
        toolBar.add(trButton);

        jdButton.setIcon(TetriminoRenderer.JD);
        jdButton.setToolTipText("jd");
        jdButton.setContentAreaFilled(false);
        jdButton.setFocusable(false);
        jdButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jdButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jdButtonActionPerformed(evt);
            }
        });
        toolBar.add(jdButton);
        toolBar.add(separator4);

        jlButton.setIcon(TetriminoRenderer.JL);
        jlButton.setToolTipText("jl");
        jlButton.setContentAreaFilled(false);
        jlButton.setFocusable(false);
        jlButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jlButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jlButtonActionPerformed(evt);
            }
        });
        toolBar.add(jlButton);

        juButton.setIcon(TetriminoRenderer.JU);
        juButton.setToolTipText("ju");
        juButton.setContentAreaFilled(false);
        juButton.setFocusable(false);
        juButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        juButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        juButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juButtonActionPerformed(evt);
            }
        });
        toolBar.add(juButton);

        jrButton.setIcon(TetriminoRenderer.JR);
        jrButton.setToolTipText("jr");
        jrButton.setContentAreaFilled(false);
        jrButton.setFocusable(false);
        jrButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jrButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jrButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrButtonActionPerformed(evt);
            }
        });
        toolBar.add(jrButton);
        toolBar.add(separator5);

        zhButton.setIcon(TetriminoRenderer.ZH);
        zhButton.setToolTipText("zh");
        zhButton.setContentAreaFilled(false);
        zhButton.setFocusable(false);
        zhButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zhButton.setMargin(null);
        zhButton.setName(""); // NOI18N
        zhButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zhButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zhButtonActionPerformed(evt);
            }
        });
        toolBar.add(zhButton);

        zvButton.setIcon(TetriminoRenderer.ZV);
        zvButton.setToolTipText("zv");
        zvButton.setContentAreaFilled(false);
        zvButton.setFocusable(false);
        zvButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zvButton.setMargin(null);
        zvButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zvButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zvButtonActionPerformed(evt);
            }
        });
        toolBar.add(zvButton);
        toolBar.add(separator6);

        oButton.setIcon(TetriminoRenderer.OS);
        oButton.setToolTipText("o");
        oButton.setContentAreaFilled(false);
        oButton.setFocusable(false);
        oButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        oButton.setMargin(null);
        oButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        oButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oButtonActionPerformed(evt);
            }
        });
        toolBar.add(oButton);
        toolBar.add(separator7);

        shButton.setIcon(TetriminoRenderer.SH);
        shButton.setToolTipText("sh");
        shButton.setContentAreaFilled(false);
        shButton.setFocusable(false);
        shButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        shButton.setMargin(null);
        shButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        shButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shButtonActionPerformed(evt);
            }
        });
        toolBar.add(shButton);

        svButton.setIcon(TetriminoRenderer.SV);
        svButton.setToolTipText("sv");
        svButton.setContentAreaFilled(false);
        svButton.setFocusable(false);
        svButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        svButton.setMargin(null);
        svButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        svButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                svButtonActionPerformed(evt);
            }
        });
        toolBar.add(svButton);
        toolBar.add(separator8);

        ldButton.setIcon(TetriminoRenderer.LD);
        ldButton.setToolTipText("ld");
        ldButton.setContentAreaFilled(false);
        ldButton.setFocusable(false);
        ldButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ldButton.setMargin(null);
        ldButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ldButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ldButtonActionPerformed(evt);
            }
        });
        toolBar.add(ldButton);

        llButton.setIcon(TetriminoRenderer.LL);
        llButton.setToolTipText("ll");
        llButton.setContentAreaFilled(false);
        llButton.setFocusable(false);
        llButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        llButton.setMargin(null);
        llButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        llButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                llButtonActionPerformed(evt);
            }
        });
        toolBar.add(llButton);

        luButton.setIcon(TetriminoRenderer.LU);
        luButton.setToolTipText("lu");
        luButton.setContentAreaFilled(false);
        luButton.setFocusable(false);
        luButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        luButton.setMargin(null);
        luButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        luButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                luButtonActionPerformed(evt);
            }
        });
        toolBar.add(luButton);

        lrButton.setIcon(TetriminoRenderer.LR);
        lrButton.setToolTipText("lr");
        lrButton.setContentAreaFilled(false);
        lrButton.setFocusable(false);
        lrButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lrButton.setMargin(null);
        lrButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lrButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lrButtonActionPerformed(evt);
            }
        });
        toolBar.add(lrButton);
        toolBar.add(jSeparator9);

        ivButton.setIcon(TetriminoRenderer.IV);
        ivButton.setToolTipText("iv");
        ivButton.setContentAreaFilled(false);
        ivButton.setFocusable(false);
        ivButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ivButton.setMargin(null);
        ivButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ivButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ivButtonActionPerformed(evt);
            }
        });
        toolBar.add(ivButton);

        ihButton.setIcon(TetriminoRenderer.IH);
        ihButton.setToolTipText("ih");
        ihButton.setContentAreaFilled(false);
        ihButton.setFocusable(false);
        ihButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ihButton.setMargin(null);
        ihButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ihButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ihButtonActionPerformed(evt);
            }
        });
        toolBar.add(ihButton);

        bottomPanel.setPreferredSize(null);

        cellSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(16, 1, 32, 1));
        cellSizeSpinner.setToolTipText("Cell Size");
        cellSizeSpinner.setMaximumSize(null);
        cellSizeSpinner.setPreferredSize(null);
        cellSizeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cellSizeSpinnerStateChanged(evt);
            }
        });

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);

        coordinatesLabel.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        coordinatesLabel.setText("               ");
        coordinatesLabel.setToolTipText("Playfield Coordinates");
        coordinatesLabel.setMaximumSize(null);
        coordinatesLabel.setMinimumSize(null);
        coordinatesLabel.setPreferredSize(null);

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);

        playfieldHeightSpinner.setModel(new javax.swing.SpinnerNumberModel(4096, 4, 4096, 1));
        playfieldHeightSpinner.setToolTipText("Playfield Height");
        playfieldHeightSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                playfieldHeightSpinnerStateChanged(evt);
            }
        });

        playfieldWidthSpinner.setModel(new javax.swing.SpinnerNumberModel(8192, 4, 8192, 1));
        playfieldWidthSpinner.setToolTipText("Playfield Width");
        playfieldWidthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                playfieldWidthSpinnerStateChanged(evt);
            }
        });

        jSeparator10.setOrientation(javax.swing.SwingConstants.VERTICAL);

        depthSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 0, 256, 1));
        depthSpinner.setToolTipText("Rendering Recursive Depth");
        depthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                depthSpinnerStateChanged(evt);
            }
        });

        cursorLabel.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        cursorLabel.setText("               ");
        cursorLabel.setToolTipText("Cursor Coordinates");
        cursorLabel.setMaximumSize(null);
        cursorLabel.setMinimumSize(null);
        cursorLabel.setName(""); // NOI18N
        cursorLabel.setPreferredSize(null);

        centerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/center.png"))); // NOI18N
        centerButton.setToolTipText("Center Playfield");
        centerButton.setBorder(null);
        centerButton.setBorderPainted(false);
        centerButton.setContentAreaFilled(false);
        centerButton.setFocusPainted(false);
        centerButton.setFocusable(false);
        centerButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        centerButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/center-pressed.png"))); // NOI18N
        centerButton.setRequestFocusEnabled(false);
        centerButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/center-rollover.png"))); // NOI18N
        centerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                centerButtonActionPerformed(evt);
            }
        });

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cursorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(centerButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(depthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playfieldWidthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playfieldHeightSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cellSizeSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(coordinatesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cellSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coordinatesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playfieldHeightSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playfieldWidthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(depthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cursorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(centerButton)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        coordinatesLabel.getAccessibleContext().setAccessibleName("");
        coordinatesLabel.getAccessibleContext().setAccessibleDescription("");

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");
        fileMenu.setMinimumSize(null);

        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newMenuItem.setMnemonic('n');
        newMenuItem.setText("New...");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newMenuItem);
        fileMenu.add(jSeparator5);

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open...");
        openMenuItem.setToolTipText("");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        closeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        closeMenuItem.setMnemonic('C');
        closeMenuItem.setText("Close");
        closeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(closeMenuItem);
        fileMenu.add(jSeparator1);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveAsMenuItem.setMnemonic('v');
        saveAsMenuItem.setText("Save As...");
        saveAsMenuItem.setToolTipText("");
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(jSeparator3);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");
        editMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                editMenuMenuSelected(evt);
            }
        });

        undoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        undoMenuItem.setMnemonic('u');
        undoMenuItem.setText("Undo");
        undoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(undoMenuItem);

        redoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        redoMenuItem.setMnemonic('r');
        redoMenuItem.setText("Redo");
        redoMenuItem.setToolTipText("");
        redoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(redoMenuItem);
        editMenu.add(jSeparator2);

        findMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        findMenuItem.setMnemonic('F');
        findMenuItem.setText("Find...");
        findMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(findMenuItem);

        findNextMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        findNextMenuItem.setMnemonic('N');
        findNextMenuItem.setText("Find Next");
        findNextMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findNextMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(findNextMenuItem);

        findPreviousMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.SHIFT_MASK));
        findPreviousMenuItem.setMnemonic('v');
        findPreviousMenuItem.setText("Find Previous");
        findPreviousMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findPreviousMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(findPreviousMenuItem);

        replaceMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        replaceMenuItem.setMnemonic('R');
        replaceMenuItem.setText("Replace...");
        replaceMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(replaceMenuItem);

        goToMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        goToMenuItem.setMnemonic('G');
        goToMenuItem.setText("Go To...");
        goToMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(goToMenuItem);

        menuBar.add(editMenu);

        toolsMenu.setMnemonic('T');
        toolsMenu.setText("Tools");

        translateMenuItem.setMnemonic('T');
        translateMenuItem.setText("Translate...");
        translateMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                translateMenuItemActionPerformed(evt);
            }
        });
        toolsMenu.add(translateMenuItem);

        translateToCenterMenuItem.setMnemonic('C');
        translateToCenterMenuItem.setText("Translate to Center");
        translateToCenterMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                translateToCenterMenuItemActionPerformed(evt);
            }
        });
        toolsMenu.add(translateToCenterMenuItem);

        menuBar.add(toolsMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");

        helpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        helpMenuItem.setText("Help...");
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(helpMenuItem);

        licenseMenuItem.setMnemonic('L');
        licenseMenuItem.setText("License...");
        licenseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                licenseMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(licenseMenuItem);
        helpMenu.add(jSeparator11);

        aboutMenuItem.setMnemonic('A');
        aboutMenuItem.setText("About...");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(circuitsEditorPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 999, Short.MAX_VALUE)
                    .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 999, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(circuitsEditorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 813, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        circuitsEditorPanel.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        promptSaveChanges(() -> System.exit(0), "Save changes before exit?");
    }//GEN-LAST:event_exitMenuItemActionPerformed

    // returns true if handled
    private void promptSaveChanges(final Runnable runnable, final String question) {
        if (tetrisScriptChangeCount != circuitsEditorPanel.getTetrisScriptChangeCount()
                || javaScriptChangeCount != circuitsEditorPanel.getJavaScriptChangeCount()) {
            switch (JOptionPane.showConfirmDialog(this, question, "Unsaved Changes", 
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, QUESTION_ICON)) {
                case JOptionPane.YES_OPTION:
                    save(runnable);
                    break;
                case JOptionPane.NO_OPTION:
                    runnable.run();
                    break;
                default:
                    return;
            }
        }
        runnable.run();
    }    
    
    private void undoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoMenuItemActionPerformed
        circuitsEditorPanel.undo();
    }//GEN-LAST:event_undoMenuItemActionPerformed

    private void redoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoMenuItemActionPerformed
        circuitsEditorPanel.redo();
    }//GEN-LAST:event_redoMenuItemActionPerformed

    private void tdButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tdButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_tdButtonActionPerformed

    private void tlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tlButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_tlButtonActionPerformed

    private void tuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tuButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_tuButtonActionPerformed

    private void trButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_trButtonActionPerformed

    private void jdButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jdButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_jdButtonActionPerformed

    private void jlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jlButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_jlButtonActionPerformed

    private void juButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_juButtonActionPerformed

    private void jrButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_jrButtonActionPerformed

    private void zhButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zhButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_zhButtonActionPerformed

    private void zvButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zvButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_zvButtonActionPerformed

    private void oButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_oButtonActionPerformed

    private void shButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_shButtonActionPerformed

    private void svButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_svButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_svButtonActionPerformed

    private void ldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ldButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_ldButtonActionPerformed

    private void llButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_llButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_llButtonActionPerformed

    private void luButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_luButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_luButtonActionPerformed

    private void lrButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lrButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_lrButtonActionPerformed

    private void ivButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ivButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_ivButtonActionPerformed

    private void ihButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ihButtonActionPerformed
        circuitsEditorPanel.tetriminoButtonPressed(evt);
    }//GEN-LAST:event_ihButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        circuitsEditorPanel.buildAndRun(componentName, testTextField.getText().trim(), (int)depthSpinner.getValue());
    }//GEN-LAST:event_runButtonActionPerformed

    private void addComponentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addComponentButtonActionPerformed
        final String name = compEditComboBox.getSelectedItem().toString();
        final Structure structure = structures.get(name);
        if (structure != null) {
            circuitsEditorPanel.setCursorRenderer(new StructureRenderer(structure), name);
        }
    }//GEN-LAST:event_addComponentButtonActionPerformed

    private void cellSizeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cellSizeSpinnerStateChanged
        circuitsEditorPanel.setCellSize((int)cellSizeSpinner.getValue());
    }//GEN-LAST:event_cellSizeSpinnerStateChanged

    private void playfieldHeightSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_playfieldHeightSpinnerStateChanged
        circuitsEditorPanel.setPlayfieldHeight((int)playfieldHeightSpinner.getValue());
    }//GEN-LAST:event_playfieldHeightSpinnerStateChanged

    private void playfieldWidthSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_playfieldWidthSpinnerStateChanged
        circuitsEditorPanel.setPlayfieldWidth((int)playfieldWidthSpinner.getValue());
    }//GEN-LAST:event_playfieldWidthSpinnerStateChanged

    private void depthSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_depthSpinnerStateChanged
        runButtonActionPerformed(null);
    }//GEN-LAST:event_depthSpinnerStateChanged

    private void translateMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_translateMenuItemActionPerformed
        
        final JDialog translateDialog = new JDialog(this, "Translate");
        final TranslatePanel translatePanel = new TranslatePanel(translateDialog);
        translatePanel.setTranslateSelection(circuitsEditorPanel.isTetrisScriptSelected());
        translateDialog.setContentPane(translatePanel);
        translateDialog.setModal(true);
        translateDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);        
        translateDialog.pack();
        translateDialog.setLocationRelativeTo(this);
        translateDialog.setVisible(true);
        
        if (!translatePanel.isOk()) {
            return;            
        }
        
        circuitsEditorPanel.clearOutput();
        circuitsEditorPanel.appendOutput(String.format("Translating %s by (%d, %d)...", 
                translatePanel.isTranslateSelection() ? "selection" : "all", 
                translatePanel.getOffsetX(),
                translatePanel.getOffsetY()));
        try {
            circuitsEditorPanel.replaceTetrisScriptLines(controller.translate(componentName, 
                    circuitsEditorPanel.getTetrisScriptLines(translatePanel.isTranslateSelection()), 
                    translatePanel.getOffsetX(), translatePanel.getOffsetY()), translatePanel.isTranslateSelection());
        } catch (final Exception e) {
            circuitsEditorPanel.appendOutput("Failed to translate: " + e.getMessage());
            return;
        }
        runButtonActionPerformed(evt);
        circuitsEditorPanel.appendOutput("Translated.");        
    }//GEN-LAST:event_translateMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        final JDialog aboutDialog = new JDialog(this, "About TetrisScript Editor", true);
        aboutDialog.setContentPane(new AboutPanel());
        aboutDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        aboutDialog.pack();
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void licenseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_licenseMenuItemActionPerformed
        final JDialog licenseDialog = new JDialog(this, "License", true);
        licenseDialog.setContentPane(new LicensePanel());
        licenseDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        licenseDialog.pack();
        licenseDialog.setLocationRelativeTo(this);
        licenseDialog.setVisible(true);
    }//GEN-LAST:event_licenseMenuItemActionPerformed

    private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuItemActionPerformed
        try {
            Desktop.getDesktop().browse(new URI("https://meatfighter.com/")); // TODO TSE HELP URL
        } catch (final Exception e) {
        }
    }//GEN-LAST:event_helpMenuItemActionPerformed

    private void centerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_centerButtonActionPerformed
        circuitsEditorPanel.centerPlayfield();
    }//GEN-LAST:event_centerButtonActionPerformed

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        final JFileChooser fileChooser = new JFileChooser(getComponentDirectory());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        final FileFilter tsFileFilter = new FileNameExtensionFilter("TetrisScript file (*.t)", "t");
        fileChooser.addChoosableFileFilter(tsFileFilter);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JavaScript file (*.js)", "js"));
        fileChooser.setFileFilter(tsFileFilter);
		
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        reset();
                
		final File selectedFile = fileChooser.getSelectedFile();
        final File tsFile;
        final File jsFile;
        final String compName;
        if (selectedFile.getName().endsWith(".js")) {
            jsFile = selectedFile;
            compName = selectedFile.getName().substring(0, selectedFile.getName().length() - 3);
            tsFile = new File(selectedFile.getAbsoluteFile().getParent() + File.separator + compName 
                    + ".t");
        } else {
            tsFile = selectedFile;
            compName = selectedFile.getName().substring(0, selectedFile.getName().length() - 2);
            jsFile = new File(selectedFile.getAbsoluteFile().getParent() + File.separator + compName 
                    + ".js");
        }
        
        controller.openComponent(compName, tsFile, jsFile);
    }//GEN-LAST:event_openMenuItemActionPerformed
   
    private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed
        promptSaveChanges(() -> EventQueue.invokeLater(() -> {
            reset();
            saveAs(null, "New");
        }), "Save changes before new?");
    }//GEN-LAST:event_newMenuItemActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        save(null);
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void save(final Runnable runnable) {
        if (Controller.DEFAULT_COMPONENT_NAME.equals(componentName)) {
            saveAs(runnable, "Save");
            return;
        }
        
        tetrisScriptChangeCount = circuitsEditorPanel.getTetrisScriptChangeCount();
        javaScriptChangeCount = circuitsEditorPanel.getJavaScriptChangeCount();
        circuitsEditorPanel.save(componentName, tetrisScriptFile, javaScriptFile, runnable);
    }
    
    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed
        saveAs(null, "Save");
    }//GEN-LAST:event_saveAsMenuItemActionPerformed

    private void saveAs(final Runnable runnable, final String title) {
        final JFileChooser fileChooser = new JFileChooser(getComponentDirectory());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        final FileFilter tsFileFilter = new FileNameExtensionFilter("TetrisScript file (*.t)", "t");
        fileChooser.addChoosableFileFilter(tsFileFilter);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JavaScript file (*.js)", "js"));
        fileChooser.setFileFilter(tsFileFilter);
        fileChooser.setDialogTitle(title);
                
        File tsFile = null;
        File jsFile = null;
        String compName = null;
        outer: while (true) {
            if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            
            File selectedFile = fileChooser.getSelectedFile();            
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(this, "Invalid file name.", "Save Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            
            if (selectedFile.getName().indexOf('.') < 0) {
                final FileFilter fileFilter = fileChooser.getFileFilter();
                String suffix = ".t";
                if (fileFilter instanceof FileNameExtensionFilter) {
                    if ("js".equals(((FileNameExtensionFilter)fileFilter).getExtensions()[0])) {
                        suffix = ".js";
                    }
                }
                selectedFile = new File(selectedFile.getPath() + suffix);
            }
            
            if (selectedFile.getName().endsWith(".js")) {
                jsFile = selectedFile;
                compName = selectedFile.getName().substring(0, selectedFile.getName().length() - 3);
                tsFile = new File(selectedFile.getAbsoluteFile().getParent() + File.separator + compName + ".t");
            } else if (selectedFile.getName().endsWith(".t")) {
                tsFile = selectedFile;
                compName = selectedFile.getName().substring(0, selectedFile.getName().length() - 2);
                jsFile = new File(selectedFile.getAbsoluteFile().getParent() + File.separator + compName + ".js");
            } else {
                tsFile = selectedFile;
                compName = selectedFile.getName().substring(0, selectedFile.getName().indexOf('.'));
                jsFile = new File(selectedFile.getAbsoluteFile().getParent() + File.separator + compName + ".js");
            }           

            String existingFileName = null;
            if (tsFile.exists()) {
                existingFileName = tsFile.getName();
            } else if (jsFile.exists()) {
                existingFileName = jsFile.getName();
            } else {
                break;
            }
            
            switch(JOptionPane.showConfirmDialog(this, String.format("%s already exists. Replace it?", 
                    existingFileName), "Overwrite Existing File", JOptionPane.YES_NO_CANCEL_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, QUESTION_ICON)) {
                case JOptionPane.YES_OPTION:
                    break outer;
                case JOptionPane.NO_OPTION:
                    continue;
                case JOptionPane.CANCEL_OPTION:
                    return;    
            }                    
        }

        tetrisScriptFile = tsFile;
        javaScriptFile = jsFile;
        setComponentName(compName);
        
        tetrisScriptChangeCount = circuitsEditorPanel.getTetrisScriptChangeCount();
        javaScriptChangeCount = circuitsEditorPanel.getJavaScriptChangeCount();
        circuitsEditorPanel.save(compName, tsFile, jsFile, runnable);
    }
    
    private void closeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuItemActionPerformed
        promptSaveChanges(() -> EventQueue.invokeLater(this::reset), "Save changes before close?");
    }//GEN-LAST:event_closeMenuItemActionPerformed

    private void findMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findMenuItemActionPerformed
        showFindReplaceDialog(false);
    }//GEN-LAST:event_findMenuItemActionPerformed

    private void findNextMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findNextMenuItemActionPerformed
        circuitsEditorPanel.findNext(lastFindWhat, lastBackwards, lastMatchCase, lastRegex, lastWrapAround);
    }//GEN-LAST:event_findNextMenuItemActionPerformed

    private void findPreviousMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findPreviousMenuItemActionPerformed
        circuitsEditorPanel.findNext(lastFindWhat, !lastBackwards, lastMatchCase, lastRegex, lastWrapAround);
    }//GEN-LAST:event_findPreviousMenuItemActionPerformed

    private void replaceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceMenuItemActionPerformed
        showFindReplaceDialog(true);
    }//GEN-LAST:event_replaceMenuItemActionPerformed

    private void showFindReplaceDialog(final boolean replaceEnabled) {
        final String title = replaceEnabled ? "Replace" : "Find";
        
        if (findReplaceDialog != null) {
            final FindReplacePanel findReplacePanel = (FindReplacePanel)findReplaceDialog.getContentPane();            
            findReplaceDialog.setTitle(title);
            if (!findReplaceDialog.isVisible()) {
                findReplacePanel.setReplaceEnabled(true);
                findReplaceDialog.pack();
                findReplacePanel.setReplaceEnabled(replaceEnabled);
                findReplaceDialog.setLocationRelativeTo(this);
                findReplaceDialog.setVisible(true);
            }
            findReplacePanel.setReplaceEnabled(replaceEnabled);
            findReplaceDialog.requestFocus();
            findReplacePanel.init();
            return;
        }
        
        findReplaceDialog = new JDialog(this, title);        
        final FindReplacePanel findReplacePanel = new FindReplacePanel();
        findReplacePanel.setCircuitsFrame(this);
        findReplaceDialog.setContentPane(findReplacePanel);
        findReplaceDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        findReplaceDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                closeFindReplaceDialog();
            }
        });
        findReplacePanel.setReplaceEnabled(true);
        findReplaceDialog.pack();
        findReplacePanel.setReplaceEnabled(replaceEnabled);
        findReplaceDialog.setLocationRelativeTo(this);
        findReplaceDialog.setVisible(true);
    }
    
    public void findNext(final String findWhat, final boolean backwards, final boolean matchCase, final boolean regex, 
            final boolean wrapAround) {
        lastFindWhat = findWhat;
        lastBackwards = backwards;
        lastMatchCase = matchCase;
        lastRegex = regex;
        lastWrapAround = wrapAround;
        circuitsEditorPanel.findNext(findWhat, backwards, matchCase, regex, wrapAround);
    }
    
    public void replace(final String findWhat, final String replaceWith, final boolean backwards, 
            final boolean matchCase, final boolean regex, final boolean wrapAround) {
        lastFindWhat = findWhat;
        lastBackwards = backwards;
        lastMatchCase = matchCase;
        lastRegex = regex;
        lastWrapAround = wrapAround;
        circuitsEditorPanel.replace(findWhat, replaceWith, backwards, matchCase, regex, wrapAround);
    }

    public void replaceAll(final String findWhat, final String replaceWith, final boolean backwards, 
            final boolean matchCase, final boolean regex, final boolean wrapAround) {
        circuitsEditorPanel.replaceAll(findWhat, replaceWith, backwards, matchCase, regex, wrapAround);
    }    
    
    public void closeFindReplaceDialog() {
        if (findReplaceDialog != null) {
            findReplaceDialog.setVisible(false);
        }
    }
    
    private void goToMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToMenuItemActionPerformed
        if (goToDialog != null) {
            if (!goToDialog.isVisible()) {
                goToDialog.pack();
                goToDialog.setLocationRelativeTo(this);
                goToDialog.setVisible(true);
            }
            goToDialog.requestFocus();
            EventQueue.invokeLater(() -> ((GoToPanel)goToDialog.getContentPane()).init());
            return;
        }
                
        goToDialog = new JDialog(this, "Go To");
        final GoToPanel goToPanel = new GoToPanel();
        goToPanel.setCircuitsFrame(this);
        goToDialog.setContentPane(goToPanel);
        goToDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        goToDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                closeGoToDialog();
            }
        });
        goToDialog.pack();
        goToDialog.setLocationRelativeTo(this);
        goToDialog.setVisible(true);
        EventQueue.invokeLater(() -> ((GoToPanel)goToDialog.getContentPane()).init());        
    }//GEN-LAST:event_goToMenuItemActionPerformed

    private void editMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_editMenuMenuSelected
        final boolean findEnabled = isNotBlank(lastFindWhat);
        findNextMenuItem.setEnabled(findEnabled);
        findPreviousMenuItem.setEnabled(findEnabled);
    }//GEN-LAST:event_editMenuMenuSelected

    private void translateToCenterMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_translateToCenterMenuItemActionPerformed
        try {
            circuitsEditorPanel.replaceTetrisScriptLines(controller.translateToCenter(componentName, 
                    circuitsEditorPanel.getTetrisScriptLines(false)), false);
        } catch (final Exception e) {
            circuitsEditorPanel.appendOutput("Failed to translate: " + e.getMessage());
            return;
        }
        runButtonActionPerformed(evt);
    }//GEN-LAST:event_translateToCenterMenuItemActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        exitMenuItemActionPerformed(null);
    }//GEN-LAST:event_formWindowClosing

    public void goToLine(final int lineNumber) {
        closeGoToDialog();
        circuitsEditorPanel.goToLine(lineNumber);
    }    
    
    public void closeGoToDialog() {
        if (goToDialog != null) {
            goToDialog.setVisible(false);
        }
    }
    
    private void setComponentName(final String componentName) {
        this.componentName = componentName;
        String title = getTitle(); 
        final int index = title.indexOf(" - ");
        if (index >= 0) {
            title = title.substring(index + 3);
            setTitle(title);
        }
        if (isNotBlank(componentName)) {
            setTitle(String.format("%s - %s", componentName, title));
        }
    }
    
    private void reset() { 
        controller.close();
        circuitsEditorPanel.reset();
        circuitsEditorPanel.centerPlayfield();
        setComponentName(Controller.DEFAULT_COMPONENT_NAME);        
        tetrisScriptFile = javaScriptFile = null;
        testTextField.setText("");
        depthSpinner.setValue(1);
        playfieldWidthSpinner.setValue(8192);
        playfieldHeightSpinner.setValue(4096);
        cellSizeSpinner.setValue(16);
        controller.loadComponents();
        tetrisScriptChangeCount = circuitsEditorPanel.getTetrisScriptChangeCount();
        javaScriptChangeCount = circuitsEditorPanel.getJavaScriptChangeCount();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton addComponentButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JSpinner cellSizeSpinner;
    private javax.swing.JButton centerButton;
    private tetriscircuits.ui.CircuitsEditorPanel circuitsEditorPanel;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JComboBox<String> compEditComboBox;
    private javax.swing.JLabel coordinatesLabel;
    private javax.swing.JLabel cursorLabel;
    private javax.swing.JSpinner depthSpinner;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem findMenuItem;
    private javax.swing.JMenuItem findNextMenuItem;
    private javax.swing.JMenuItem findPreviousMenuItem;
    private javax.swing.JMenuItem goToMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JButton ihButton;
    private javax.swing.JButton ivButton;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JButton jdButton;
    private javax.swing.JButton jlButton;
    private javax.swing.JButton jrButton;
    private javax.swing.JButton juButton;
    private javax.swing.JButton ldButton;
    private javax.swing.JMenuItem licenseMenuItem;
    private javax.swing.JButton llButton;
    private javax.swing.JButton lrButton;
    private javax.swing.JButton luButton;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JButton oButton;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JSpinner playfieldHeightSpinner;
    private javax.swing.JSpinner playfieldWidthSpinner;
    private javax.swing.JMenuItem redoMenuItem;
    private javax.swing.JMenuItem replaceMenuItem;
    private javax.swing.JButton runButton;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JToolBar.Separator separator4;
    private javax.swing.JToolBar.Separator separator5;
    private javax.swing.JToolBar.Separator separator6;
    private javax.swing.JToolBar.Separator separator7;
    private javax.swing.JToolBar.Separator separator8;
    private javax.swing.JButton shButton;
    private javax.swing.JButton svButton;
    private javax.swing.JButton tdButton;
    private javax.swing.JTextField testTextField;
    private javax.swing.JButton tlButton;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JButton trButton;
    private javax.swing.JMenuItem translateMenuItem;
    private javax.swing.JMenuItem translateToCenterMenuItem;
    private javax.swing.JButton tuButton;
    private javax.swing.JMenuItem undoMenuItem;
    private javax.swing.JButton zhButton;
    private javax.swing.JButton zvButton;
    // End of variables declaration//GEN-END:variables
}
