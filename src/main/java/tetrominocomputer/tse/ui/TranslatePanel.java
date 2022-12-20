package tetrominocomputer.tse.ui;

import java.awt.Window;
import java.text.ParseException;

public class TranslatePanel extends javax.swing.JPanel {

    private Window window;
    private int x;
    private int y;
    private boolean translateSelection;
    private boolean ok;
    
    /**
     * Creates new form TranslatePanel
     */
    public TranslatePanel(final Window window) {
        this.window = window;
        initComponents();
        xTextField.requestFocusInWindow();
        xTextField.selectAll();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        translateButton = new javax.swing.JButton();
        xLabel = new javax.swing.JLabel();
        xTextField = new javax.swing.JFormattedTextField();
        yLabel = new javax.swing.JLabel();
        yTextField = new javax.swing.JFormattedTextField();
        translateSelectionCheckBox = new javax.swing.JCheckBox();

        cancelButton.setMnemonic('C');
        cancelButton.setText("Cancel");
        cancelButton.setNextFocusableComponent(xTextField);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        translateButton.setMnemonic('T');
        translateButton.setText("Translate");
        translateButton.setNextFocusableComponent(cancelButton);
        translateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                translateButtonActionPerformed(evt);
            }
        });

        xLabel.setText("X");

        xTextField.setColumns(5);
        xTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        xTextField.setText("0");
        xTextField.setFocusCycleRoot(true);
        xTextField.setNextFocusableComponent(yTextField);

        yLabel.setText("Y");

        yTextField.setColumns(5);
        yTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        yTextField.setText("0");
        yTextField.setNextFocusableComponent(translateSelectionCheckBox);

        translateSelectionCheckBox.setSelected(true);
        translateSelectionCheckBox.setText("Translate selection");
        translateSelectionCheckBox.setNextFocusableComponent(translateButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(translateButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(yLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(translateSelectionCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xLabel)
                    .addComponent(xTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yLabel)
                    .addComponent(yTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(translateSelectionCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(translateButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        window.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void translateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_translateButtonActionPerformed
        try {
            xTextField.commitEdit();            
        } catch (final ParseException e) {            
        }
        final Long X = (Long)xTextField.getValue();
        if (X != null) {
            x = X.intValue();
        }
        try {
            yTextField.commitEdit();        
        } catch (final ParseException e) {            
        }        
        final Long Y = (Long)yTextField.getValue();
        if (Y != null) {
            y = Y.intValue();
        }        
        translateSelection = translateSelectionCheckBox.isSelected();
        ok = true;
        window.dispose();
    }//GEN-LAST:event_translateButtonActionPerformed

    public int getOffsetX() {
        return x;
    }

    public int getOffsetY() {
        return y;
    }

    public boolean isTranslateSelection() {
        return translateSelection;
    }
    
    public void setTranslateSelection(final boolean translateSelection) {
        translateSelectionCheckBox.setSelected(translateSelection);
    }

    public boolean isOk() {
        return ok;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton translateButton;
    private javax.swing.JCheckBox translateSelectionCheckBox;
    private javax.swing.JLabel xLabel;
    private javax.swing.JFormattedTextField xTextField;
    private javax.swing.JLabel yLabel;
    private javax.swing.JFormattedTextField yTextField;
    // End of variables declaration//GEN-END:variables
}
