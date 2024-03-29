package tetrominocomputer.tse.ui;

import java.awt.EventQueue;
import tetrominocomputer.util.Ui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class HtmlExportPanel extends javax.swing.JPanel {
    
    public static final String COMMAND_EXPORT = "export";
    public static final String COMMAND_CANCEL = "cancel";    

    private final HtmlExportModel model = new HtmlExportModel();
    
    private ActionListener actionListener;
    
    /**
     * Creates new form HtmlExportPanel
     */
    public HtmlExportPanel() {
        initComponents();
        Ui.setTextFieldColumns(fileTextField, 50);
        initComponentValues();
    }
    
    public void init() {
        EventQueue.invokeLater(() -> Ui.resetTextFieldColumns(fileTextField));
    }
    
    public void setModel(final HtmlExportModel model) {
        this.model.set(model);
        initComponentValues();
    }
    
    public HtmlExportModel getModel() {
        return model.copy();
    }
    
    public ActionListener getActionListener() {
        return actionListener;
    }
    
    public void setActionListener(final ActionListener actionListener) {
        this.actionListener = actionListener;
    }
    
    public final void initComponentValues() {
        stdOutCheckBox.setSelected(model.isStdout());
        fileTextField.setText(model.getFilename());
        
        stdOutCheckBoxActionPerformed(null);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        stdOutCheckBox = new javax.swing.JCheckBox();
        fileLabel = new javax.swing.JLabel();
        fileTextField = new javax.swing.JTextField();
        defaultFileButton = new javax.swing.JButton();
        browseFileButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Output"));

        stdOutCheckBox.setText("Stdout");
        stdOutCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stdOutCheckBoxActionPerformed(evt);
            }
        });

        fileLabel.setText("File:");

        defaultFileButton.setMnemonic('d');
        defaultFileButton.setText("Default");
        defaultFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultFileButtonActionPerformed(evt);
            }
        });

        browseFileButton.setMnemonic('b');
        browseFileButton.setText("Browse...");
        browseFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseFileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(stdOutCheckBox)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(fileLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fileTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseFileButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(defaultFileButton)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(stdOutCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileLabel)
                    .addComponent(fileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(defaultFileButton)
                    .addComponent(browseFileButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        closeButton.setMnemonic('c');
        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        exportButton.setMnemonic('x');
        exportButton.setText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(exportButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeButton)
                    .addComponent(exportButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void stdOutCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stdOutCheckBoxActionPerformed
        final boolean enabled = !stdOutCheckBox.isSelected();
        fileLabel.setEnabled(enabled);
        fileTextField.setEnabled(enabled);
        browseFileButton.setEnabled(enabled);
        defaultFileButton.setEnabled(enabled);
    }//GEN-LAST:event_stdOutCheckBoxActionPerformed

    private void browseFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseFileButtonActionPerformed
        final File file = new File(fileTextField.getText());
        
        final JFileChooser fileChooser = new JFileChooser();                
        fileChooser.setCurrentDirectory(file.getParentFile());
        fileChooser.setSelectedFile(file);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);       
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setAcceptAllFileFilterUsed(true);
        final FileNameExtensionFilter svgFilter 
                = new FileNameExtensionFilter("HyperText Markup Language (*.html)", "html");
        fileChooser.addChoosableFileFilter(svgFilter);        
        fileChooser.setFileFilter(svgFilter);
        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        fileTextField.setText(fileChooser.getSelectedFile().toString());
    }//GEN-LAST:event_browseFileButtonActionPerformed

    private void defaultFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultFileButtonActionPerformed
        fileTextField.setText(HtmlExportModel.DEFAULT_FILENAME);
    }//GEN-LAST:event_defaultFileButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        
        if (!stdOutCheckBox.isSelected()) {
            if (isBlank(fileTextField.getText())) {
                Ui.showMessageDialog(this, "Filename not specified.", "Invalid File", JOptionPane.ERROR_MESSAGE);
                return;
            }
            final File file = new File(fileTextField.getText());
            if (file.isDirectory()) {
                Ui.showMessageDialog(this, "Path refers to a directory.", "Invalid File", 
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (file.exists() && Ui.showConfirmDialog(this, String.format("%s already exists. Replace it?",
                    file.getName()), "Overwrite Existing File", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        model.setStdout(stdOutCheckBox.isSelected());
        model.setFilename(fileTextField.getText());        
        
        if (actionListener != null) {
            actionListener.actionPerformed(new ActionEvent(this, 0, COMMAND_EXPORT));
        }
    }//GEN-LAST:event_exportButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        if (actionListener != null) {
            actionListener.actionPerformed(new ActionEvent(this, 0, COMMAND_CANCEL));
        }
    }//GEN-LAST:event_closeButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseFileButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton defaultFileButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JTextField fileTextField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JCheckBox stdOutCheckBox;
    // End of variables declaration//GEN-END:variables
}
