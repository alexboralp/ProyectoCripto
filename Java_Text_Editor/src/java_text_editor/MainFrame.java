package java_text_editor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainFrame extends javax.swing.JFrame {

    File currentEditingFile = null;
    int fontSize = 14;
    static TDES tdes = null;

    public MainFrame() {
        initComponents();

        //Filter Files to display
        //Set JFileChooser to accept only text files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        fileOpener.setFileFilter(filter);

        //Launch the application on the middle of Screen
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e); //To change body of generated methods, choose Tools | Templates.
                int ans = JOptionPane.showConfirmDialog(rootPane, "Guardar cambios ?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (ans == JOptionPane.YES_OPTION) {
                    saveChanges();
                }
            }

        });
        try {
            this.setIconImage(ImageIO.read(MainFrame.class.getResource("/java_text_editor/genuine_coder_logo.png")));
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public MainFrame(File file) {
        initComponents();
        this.setLocationRelativeTo(null);

        currentEditingFile = file;
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        fileOpener.setFileFilter(filter);
        readTheParamFile(file);
        String key = JOptionPane.showInputDialog("Ingrese llave privada de 24 caracteres:", "");
        while (key.length() != 24){
            key = JOptionPane.showInputDialog("Longitud incorrecta. Ingrese llave privada de 24 caracteres:", "");
        }
        tdes = new TDES(key);
    }

    public void readTheParamFile(File file) {
        try {
            Scanner scn = new Scanner(file);
            String buffer = "";
            while (scn.hasNext()) {
                buffer += scn.nextLine() + "\n";
            }
            display.setText(buffer);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveChanges() {
        try {
            PrintWriter printWriter = new PrintWriter(currentEditingFile);
            printWriter.write(display.getText());
            printWriter.close();
//            JOptionPane.showMessageDialog(rootPane, "Saved", "Done", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileOpener = new javax.swing.JFileChooser();
        saveDialog = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        openButton = new javax.swing.JButton();
        encButton = new javax.swing.JButton();
        decButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        display = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        saveDialog.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        saveDialog.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Java Text Editor");

        jToolBar1.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar1.setRollover(true);

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java_text_editor/open_new_file.png"))); // NOI18N
        openButton.setText("Abrir");
        openButton.setFocusable(false);
        openButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(openButton);

        encButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java_text_editor/decrease_font.png"))); // NOI18N
        encButton.setText("Encriptar");
        encButton.setFocusable(false);
        encButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        encButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        encButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                encButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(encButton);

        decButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java_text_editor/increase_font.png"))); // NOI18N
        decButton.setText("Desencriptar");
        decButton.setFocusable(false);
        decButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        decButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        decButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(decButton);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java_text_editor/Save_icon.png"))); // NOI18N
        saveButton.setText("Guardar y Salir");
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveButton);

        display.setColumns(20);
        display.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        display.setLineWrap(true);
        display.setRows(5);
        jScrollPane1.setViewportView(display);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Genuine Coder");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        //Show File Open dialouge here
        int status = fileOpener.showOpenDialog(rootPane);
        if (status == JFileChooser.APPROVE_OPTION) {
            if (currentEditingFile != null) {
                // A file is opened and is being edited. Open the new file in new window  
                MainFrame newWindow = new MainFrame(fileOpener.getSelectedFile());
                newWindow.setVisible(true);
                newWindow.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                newWindow.pack();
                return;
            }
            currentEditingFile = fileOpener.getSelectedFile();
            System.out.println("Archivo seleccionado. Nombre = " + fileOpener.getSelectedFile().getName());

            try {
                //Now read the contents of file
                Scanner scn = new Scanner(new FileInputStream(currentEditingFile));
                String buffer = "";
                while (scn.hasNext()) {
                    buffer += scn.nextLine() + "\n";
                }
                System.out.println(buffer);
                buffer = buffer.substring(0, buffer.length() - 1);
                System.out.println(buffer);
                display.setText(buffer);
                //Key for the selected file is entered
                String key = JOptionPane.showInputDialog("Ingrese llave privada de 24 caracteres:", "");
                while (key.length() != 24){
                    key = JOptionPane.showInputDialog("Longitud incorrecta. Ingrese llave privada de 24 caracteres:", "");
                }
                tdes = new TDES(key);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.out.println("No se seleccionó archivo");
        }
    }//GEN-LAST:event_openButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        //If we are editing a file opened, then we have to save the contents on the same file, currentEditingFile
        if (currentEditingFile != null) {
            try {
                PrintWriter printWriter = new PrintWriter(currentEditingFile);
                printWriter.write(display.getText());
                printWriter.close();
                JOptionPane.showMessageDialog(rootPane, "Guardado en " + currentEditingFile.getName(), "Hecho", JOptionPane.INFORMATION_MESSAGE);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            setVisible(false); 
            dispose(); //Destroy the window
        } else {
            int status = saveDialog.showOpenDialog(rootPane);
            if (status == JFileChooser.APPROVE_OPTION) {
                //We got directory. Now needs file name
                String fileName = JOptionPane.showInputDialog("Nombre del archivo", "Untitled.txt");
                if (!fileName.contains(".txt")) {
                    fileName += ".txt";
                }
                File f = new File(saveDialog.getSelectedFile() + "\\" + fileName);
                if (f.exists()) {
                    JOptionPane.showMessageDialog(rootPane, "Archivo ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    try {
                        f.createNewFile();
                        PrintWriter printWriter = new PrintWriter(f);
                        printWriter.write(display.getText());
                        printWriter.close();
                        JOptionPane.showMessageDialog(rootPane, "Guardado", "Hecho", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                setVisible(false); 
                dispose(); //Destroy the window
            } else {
                JOptionPane.showMessageDialog(rootPane, "Ocurrió un error", "No se puede guardar", JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_saveButtonActionPerformed

    private void encButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_encButtonActionPerformed
        // TODO add your handling code here:
        String text = display.getText();
        String encText = tdes.enc(text);
        display.setText(encText);
    }//GEN-LAST:event_encButtonActionPerformed

    private void decButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decButtonActionPerformed
        // TODO add your handling code here:
        String text = display.getText();
        String decText = tdes.dec(text);
        display.setText(decText);
    }//GEN-LAST:event_decButtonActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
                String key = JOptionPane.showInputDialog("Ingrese llave privada de 24 caracteres:", "");
                while (key.length() != 24){
                    key = JOptionPane.showInputDialog("Longitud incorrecta. Ingrese llave privada de 24 caracteres:", "");
                }
                tdes = new TDES(key);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton decButton;
    private javax.swing.JTextArea display;
    private javax.swing.JButton encButton;
    private javax.swing.JFileChooser fileOpener;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton openButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JFileChooser saveDialog;
    // End of variables declaration//GEN-END:variables
}
