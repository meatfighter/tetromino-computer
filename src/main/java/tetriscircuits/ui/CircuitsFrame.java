package tetriscircuits.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import tetriscircuits.BuildListener;
import tetriscircuits.Controller;
import tetriscircuits.OutputListener;
import tetriscircuits.Structure;

public class CircuitsFrame extends javax.swing.JFrame {
    
    private static final int COORDINATES_SPACES = 15;

    private Controller controller;
    private Map<String, Structure> structures;

    /**
     * Creates new form CircuitsFrame
     */
    public CircuitsFrame() {
        initComponents();
        toolBar.setLayout(new WrapLayout(WrapLayout.LEFT, 0, 0));
//        initTextField(componentsTextField, 32);
        initTextField(testTextField, 16);
        initEditableComboBox(compEditComboBox);  
    }
    
    private void initEditableComboBox(final JComboBox comboBox) {
        final JTextField textField = (JTextField)comboBox.getEditor().getEditorComponent();
        textField.setSelectionColor(new Color(0x3C3F41));        
        AutoCompletion.enable(comboBox);
//        comboBox.setPreferredSize(componentsTextField.getPreferredSize());
//        comboBox.setMinimumSize(componentsTextField.getMinimumSize());
//        comboBox.setMaximumSize(componentsTextField.getMaximumSize());
        compEditComboBox.setModel(new DefaultComboBoxModel(new String[0]));
    }
    
    private void initTextField(final JTextField textField, final int columns) {
        textField.setColumns(columns);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns; ++i) {
            sb.append('M');
        }
        textField.setText(sb.toString());
        textField.setMinimumSize(textField.getPreferredSize());
        textField.setMaximumSize(textField.getPreferredSize());
        textField.setText("");
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
                buildButton.setEnabled(false);
                playButton.setEnabled(false);
            }
            @Override
            public void buildCompleted(final String[] componentNames, final Map<String, Structure> structures) {
                if (!EventQueue.isDispatchThread()) {
                    EventQueue.invokeLater(() -> buildCompleted(componentNames, structures));
                    return;
                }
                CircuitsFrame.this.structures = structures;
                buildButton.setEnabled(true);
                playButton.setEnabled(true);
                final String selectedItem = (String)compEditComboBox.getSelectedItem();
                compEditComboBox.setModel(new DefaultComboBoxModel(componentNames));
                if (selectedItem != null) {
                    EventQueue.invokeLater(() -> compEditComboBox.setSelectedItem(selectedItem));
                } 
            }
        });
    }
    
    public void init() {
        circuitsEditorPanel.init();
        circuitsEditorPanel.setCircuitsFrame(this);
    }
    
    public void zoom(final int delta) {
        int value = (int)cellSizeSpinner.getValue() + delta;
        final int maximum = (int)((SpinnerNumberModel)cellSizeSpinner.getModel()).getMaximum();
        if (value < 0) {
            value = 0;
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
        controller.buildAndRun(tetrisScript, javaScript, "[unnamed]", testTextField.getText(), 
                (int)depthSpinner.getValue()); // TODO
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
        buildButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        testTextField = new javax.swing.JTextField();
        playButton = new javax.swing.JButton();
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
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        newMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        undoMenuItem = new javax.swing.JMenuItem();
        redoMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        translateMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(null);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        circuitsEditorPanel.setPreferredSize(null);

        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setMaximumSize(null);
        toolBar.setMinimumSize(null);
        toolBar.setName(""); // NOI18N
        toolBar.setPreferredSize(null);

        buildButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/build.png"))); // NOI18N
        buildButton.setToolTipText("Build Code");
        buildButton.setContentAreaFilled(false);
        buildButton.setFocusable(false);
        buildButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buildButton.setPreferredSize(null);
        buildButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buildButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildButtonActionPerformed(evt);
            }
        });
        toolBar.add(buildButton);
        toolBar.add(jSeparator4);

        jSeparator5.setMaximumSize(new java.awt.Dimension(4, 10));
        jSeparator5.setMinimumSize(new java.awt.Dimension(4, 10));
        jSeparator5.setPreferredSize(new java.awt.Dimension(4, 10));
        jSeparator5.setSeparatorSize(new java.awt.Dimension(4, 10));
        toolBar.add(jSeparator5);

        testTextField.setColumns(15);
        testTextField.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        testTextField.setToolTipText("Test Bit String");
        testTextField.setPreferredSize(null);
        toolBar.add(testTextField);

        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/play.png"))); // NOI18N
        playButton.setToolTipText("Run Component and Test");
        playButton.setContentAreaFilled(false);
        playButton.setFocusable(false);
        playButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        playButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });
        toolBar.add(playButton);
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

        playfieldHeightSpinner.setModel(new javax.swing.SpinnerNumberModel(256, 4, 4096, 1));
        playfieldHeightSpinner.setToolTipText("Playfield Height");
        playfieldHeightSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                playfieldHeightSpinnerStateChanged(evt);
            }
        });

        playfieldWidthSpinner.setModel(new javax.swing.SpinnerNumberModel(256, 4, 4096, 1));
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

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cursorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(cursorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );

        coordinatesLabel.getAccessibleContext().setAccessibleName("");
        coordinatesLabel.getAccessibleContext().setAccessibleDescription("");

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");
        fileMenu.setMinimumSize(null);

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open...");
        openMenuItem.setToolTipText("");
        fileMenu.add(openMenuItem);
        fileMenu.add(jSeparator1);

        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newMenuItem.setMnemonic('n');
        newMenuItem.setText("New...");
        fileMenu.add(newMenuItem);
        fileMenu.add(jSeparator2);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setMnemonic('v');
        saveAsMenuItem.setText("Save As...");
        saveAsMenuItem.setToolTipText("");
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(jSeparator3);

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

        menuBar.add(editMenu);

        toolsMenu.setMnemonic('T');
        toolsMenu.setText("Tools");

        translateMenuItem.setMnemonic('T');
        translateMenuItem.setText("Translate");
        toolsMenu.add(translateMenuItem);

        menuBar.add(toolsMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");

        aboutMenuItem.setMnemonic('A');
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(circuitsEditorPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 999, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(circuitsEditorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        circuitsEditorPanel.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

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

    private void buildButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buildButtonActionPerformed
        circuitsEditorPanel.build((int)depthSpinner.getValue());
    }//GEN-LAST:event_buildButtonActionPerformed

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        final String componentName = "[unnamed]"; // TODO
        if (!componentName.isEmpty()) {
            controller.run(componentName, testTextField.getText().trim(), (int)depthSpinner.getValue());
        } else {
            final OutputListener outputListener = controller.getOutputListener();
            outputListener.clear();
            outputListener.append("Error: No component specified.");
        }
    }//GEN-LAST:event_playButtonActionPerformed

    private void addComponentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addComponentButtonActionPerformed
        final String name = compEditComboBox.getSelectedItem().toString();
        final Structure structure = structures.get(name);
        if (structure != null) {
            circuitsEditorPanel.setCursorRenderer(new StructureRenderer(structure), name);
        }
    }//GEN-LAST:event_addComponentButtonActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_formKeyPressed

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
        playButtonActionPerformed(null);
    }//GEN-LAST:event_depthSpinnerStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton addComponentButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton buildButton;
    private javax.swing.JSpinner cellSizeSpinner;
    private tetriscircuits.ui.CircuitsEditorPanel circuitsEditorPanel;
    private javax.swing.JComboBox<String> compEditComboBox;
    private javax.swing.JLabel coordinatesLabel;
    private javax.swing.JLabel cursorLabel;
    private javax.swing.JSpinner depthSpinner;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton ihButton;
    private javax.swing.JButton ivButton;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JButton jdButton;
    private javax.swing.JButton jlButton;
    private javax.swing.JButton jrButton;
    private javax.swing.JButton juButton;
    private javax.swing.JButton ldButton;
    private javax.swing.JButton llButton;
    private javax.swing.JButton lrButton;
    private javax.swing.JButton luButton;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JButton oButton;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JButton playButton;
    private javax.swing.JSpinner playfieldHeightSpinner;
    private javax.swing.JSpinner playfieldWidthSpinner;
    private javax.swing.JMenuItem redoMenuItem;
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
    private javax.swing.JButton tuButton;
    private javax.swing.JMenuItem undoMenuItem;
    private javax.swing.JButton zhButton;
    private javax.swing.JButton zvButton;
    // End of variables declaration//GEN-END:variables
}
