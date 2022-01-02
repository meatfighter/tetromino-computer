package tetriscircuits.ui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import tetriscircuits.Controller;

public class CircuitsFrame extends javax.swing.JFrame {

    private Controller controller;

    /**
     * Creates new form CircuitsFrame
     */
    public CircuitsFrame() {
        initComponents();
        initTextField(componentsTextField);
        initTextField(testTextField);
    }
    
    private void initTextField(final JTextField textField) {
        textField.setText("MMMMMMMMMMMMMMM");
        textField.setMinimumSize(componentsTextField.getPreferredSize());
        textField.setMaximumSize(componentsTextField.getPreferredSize());
        textField.setText("");
    }

    public Controller getController() {
        return controller;
    }

    public void setController(final Controller controller) {
        this.controller = controller;
        circuitsEditorPanel.setController(controller);
    }
    
    public void init() {
        circuitsEditorPanel.init();
        circuitsEditorPanel.setCircuitsFrame(this);
    }

    public JLabel getCoordinatesLabel() {
        return coordinatesLabel;
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
        componentsTextField = new javax.swing.JTextField();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        testTextField = new javax.swing.JTextField();
        playButton = new javax.swing.JButton();
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
        coordinatesLabel = new javax.swing.JLabel();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        circuitsEditorPanel.setPreferredSize(null);

        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setName(""); // NOI18N
        toolBar.setPreferredSize(null);

        buildButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/build.png"))); // NOI18N
        buildButton.setToolTipText("Build Code");
        buildButton.setContentAreaFilled(false);
        buildButton.setFocusable(false);
        buildButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buildButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buildButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildButtonActionPerformed(evt);
            }
        });
        toolBar.add(buildButton);
        toolBar.add(jSeparator4);

        componentsTextField.setColumns(15);
        componentsTextField.setToolTipText("Component Name");
        componentsTextField.setPreferredSize(null);
        toolBar.add(componentsTextField);

        jSeparator5.setMaximumSize(new java.awt.Dimension(4, 10));
        jSeparator5.setMinimumSize(new java.awt.Dimension(4, 10));
        jSeparator5.setPreferredSize(new java.awt.Dimension(4, 10));
        jSeparator5.setSeparatorSize(new java.awt.Dimension(4, 10));
        toolBar.add(jSeparator5);

        testTextField.setColumns(15);
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

        coordinatesLabel.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        coordinatesLabel.setPreferredSize(null);

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(coordinatesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(coordinatesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

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

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1024, Short.MAX_VALUE)
                    .addComponent(circuitsEditorPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1024, Short.MAX_VALUE)
                    .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(circuitsEditorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 810, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

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
        circuitsEditorPanel.build();
    }//GEN-LAST:event_buildButtonActionPerformed

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        controller.run(componentsTextField.getText(), testTextField.getText());
    }//GEN-LAST:event_playButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton buildButton;
    private tetriscircuits.ui.CircuitsEditorPanel circuitsEditorPanel;
    private javax.swing.JTextField componentsTextField;
    private javax.swing.JLabel coordinatesLabel;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton ihButton;
    private javax.swing.JButton ivButton;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
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
    private javax.swing.JButton trButton;
    private javax.swing.JButton tuButton;
    private javax.swing.JMenuItem undoMenuItem;
    private javax.swing.JButton zhButton;
    private javax.swing.JButton zvButton;
    // End of variables declaration//GEN-END:variables
}
