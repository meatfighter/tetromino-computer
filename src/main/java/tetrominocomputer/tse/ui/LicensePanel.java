package tetrominocomputer.tse.ui;

import java.awt.EventQueue;
import tetrominocomputer.tse.app.License;

public class LicensePanel extends javax.swing.JPanel {

    /**
     * Creates new form LicensePanel
     */
    public LicensePanel() {
        initComponents();
        licenseTextArea.setText(License.getLicense());
        EventQueue.invokeLater(() -> {
            licenseScrollPane.getVerticalScrollBar().setValue(licenseScrollPane.getVerticalScrollBar().getMinimum());
            licenseScrollPane.getHorizontalScrollBar().setValue(licenseScrollPane.getHorizontalScrollBar()
                    .getMinimum());
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        licenseScrollPane = new javax.swing.JScrollPane();
        licenseTextArea = new javax.swing.JTextArea();

        setPreferredSize(null);

        licenseScrollPane.setPreferredSize(null);

        licenseTextArea.setColumns(80);
        licenseTextArea.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        licenseTextArea.setRows(50);
        licenseTextArea.setPreferredSize(null);
        licenseScrollPane.setViewportView(licenseTextArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(licenseScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(licenseScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane licenseScrollPane;
    private javax.swing.JTextArea licenseTextArea;
    // End of variables declaration//GEN-END:variables
}