package tetriscircuits.ui;

public class GoToPanel extends javax.swing.JPanel {

    private CircuitsFrame circuitsFrame;
    
    /**
     * Creates new form GoToPanel
     */
    public GoToPanel() {
        initComponents();
        lineNumberTextField.requestFocusInWindow();
    }
    
    public void setCircuitsFrame(final CircuitsFrame circuitsFrame) {
        this.circuitsFrame = circuitsFrame;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        goToButton = new javax.swing.JButton();
        lineLabel = new javax.swing.JLabel();
        lineNumberTextField = new javax.swing.JFormattedTextField();

        cancelButton.setMnemonic('C');
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        goToButton.setMnemonic('G');
        goToButton.setText("Go To");
        goToButton.setEnabled(false);
        goToButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToButtonActionPerformed(evt);
            }
        });

        lineLabel.setText("Line");

        lineNumberTextField.setColumns(20);
        lineNumberTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        lineNumberTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineNumberTextFieldActionPerformed(evt);
            }
        });
        lineNumberTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lineNumberTextFieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(goToButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lineLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lineNumberTextField)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lineLabel)
                    .addComponent(lineNumberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(goToButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        circuitsFrame.closeGoToDialog();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void lineNumberTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lineNumberTextFieldKeyReleased
        final String text = lineNumberTextField.getText().trim();
        if (text.isEmpty()) {
            goToButton.setEnabled(false);
        }
        try {
            if (Integer.parseInt(text) >= 1) {
                goToButton.setEnabled(true);
            }
        } catch (final NumberFormatException e) {
            goToButton.setEnabled(false);
        }
    }//GEN-LAST:event_lineNumberTextFieldKeyReleased

    private void goToButtonPressed() {
        circuitsFrame.goToLine(((Number)lineNumberTextField.getValue()).intValue());
    }
    
    private void goToButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToButtonActionPerformed
        goToButtonPressed();
    }//GEN-LAST:event_goToButtonActionPerformed

    private void lineNumberTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineNumberTextFieldActionPerformed
        goToButtonPressed();
    }//GEN-LAST:event_lineNumberTextFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton goToButton;
    private javax.swing.JLabel lineLabel;
    private javax.swing.JFormattedTextField lineNumberTextField;
    // End of variables declaration//GEN-END:variables
}