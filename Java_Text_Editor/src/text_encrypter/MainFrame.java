package text_encrypter;

import encriptador.Encriptador;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Stack;

public class MainFrame extends javax.swing.JFrame {

    File currentEditingFile = null;
    int fontSize = 14;
    
    Encriptador encriptador;
    String key, keyOriginal;
    int longitudBloque;
    int longitudLlave;
    
    String metodo;
    
    Stack byteEnc1;
    Stack byteEnc2;
    Stack txtEnc1;
    Stack txtEnc2;

    public MainFrame() {
        initComponents();
        
        inicializaVariables();

        //Launch the application on the middle of Screen
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e); //To change body of generated methods, choose Tools | Templates.
                int ans = JOptionPane.showConfirmDialog(rootPane, "¿Guardar cambios?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (ans == JOptionPane.YES_OPTION) {
                    saveChanges();
                }
            }

        });
        
    }

    public MainFrame(File file) {
        initComponents();
        
        inicializaVariables();
        
        this.setLocationRelativeTo(null);

        currentEditingFile = file;
        readTheParamFile(file);
    }
    
    private void inicializaVariables(){
        longitudBloque = 16;
        longitudLlave = 16;
        
        //Filter Files to display
        //Set JFileChooser to accept only text files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        fileOpener.setFileFilter(filter);
        
        metodo = "twofish";
        leerLlave();
        
        imprimirMetodoLongitudBloque();
        
        try {
            encriptador = new Encriptador(metodo,longitudBloque,key);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            this.setIconImage(ImageIO.read(MainFrame.class.getResource("/images/logo.png")));
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        byteEnc1 = new Stack();
        byteEnc2 = new Stack();
        txtEnc1  = new Stack();
        txtEnc2  = new Stack();
    }
    
    private void leerLlave(){
        
        keyOriginal = JOptionPane.showInputDialog("Ingrese la llave:", "");
        if (keyOriginal == null)
            keyOriginal = "";
        
        key = arreglaLlave(keyOriginal, longitudLlave);
    }
    
    private String arreglaLlave(String llave, int tamanno){
        String llaveArreglada = llave;
        
        while(llaveArreglada.length() < tamanno)
                llaveArreglada = llaveArreglada.concat(" ");
        
        if (llaveArreglada.length() > tamanno){
            JOptionPane.showMessageDialog(this, "La llave es demasiado larga, se usarán los primeros " + tamanno + " caracteres.");
            llaveArreglada = llaveArreglada.substring(0, tamanno);
        }
        
        return llaveArreglada;
    }

    public void readTheParamFile(File file) {
        try {
            Scanner scn = new Scanner(file);
            String buffer = "";
            while (scn.hasNext()) {
                buffer += scn.nextLine() + "\n";
            }
            textoEncriptado1.setText(buffer);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveChanges() {
        try {
            PrintWriter printWriter = new PrintWriter(currentEditingFile);
            encriptar1();
            printWriter.write(textoEncriptado1.getText()+textoEncriptado2.getText());
            printWriter.close();
//            JOptionPane.showMessageDialog(rootPane, "Saved", "Done", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void imprimirMetodoLongitudBloque(){
        lblEstado.setText("Método: " + metodo + "    Longitud del bloque: " + longitudBloque);
    }
    
    private void encriptar1(){
        String text = textoClaro.getText();
        if (!"".equals(text)){
            byte[] encText = encriptador.Encriptar(text);
            byteEnc1.add(encText);
            txtEnc1.add(new String(encText));
            textoEncriptado1.setText(textoEncriptado1.getText().concat((String)txtEnc1.peek()) + "\n");
        }
        textoClaro.setText("");
    }
    
    private void encriptar2(){
        String text = textoClaro.getText();
        if (!"".equals(text)){
            byte[] encText = encriptador.Encriptar(text);
            byteEnc2.add(encText);
            txtEnc2.add(new String(encText));
            textoEncriptado2.setText(((String)txtEnc2.peek()).concat("\n").concat(textoEncriptado2.getText()));
        }
        textoClaro.setText("");
    }
    
    private void desencriptar1(){
        String text = textoClaro.getText();
        
        if (!"".equals(text)){
            encriptar2();
        }
        
        if (!byteEnc1.isEmpty()){
            text = encriptador.Desencriptar((byte[])byteEnc1.pop());

            textoClaro.setText(text);

            //Se quita el texto de textoEncriptado1
            text = textoEncriptado1.getText();
            textoEncriptado1.setText(text.substring(0, text.length() - ((String)txtEnc1.pop()).length() - 1));
        }
    }
    
    private void desencriptar2(){
        String text = textoClaro.getText();
        
        if (!"".equals(text)){
            encriptar1();
        }
        
        if (!byteEnc2.isEmpty()){
            text = encriptador.Desencriptar((byte[])byteEnc2.pop());

            textoClaro.setText(text);

            //Se quita el texto de textoEncriptado2
            text = textoEncriptado2.getText();
            textoEncriptado2.setText(text.substring(((String)txtEnc2.pop()).length() + 1));
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
        lblEstado = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textoEncriptado1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        textoClaro = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        textoEncriptado2 = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuNuevo = new javax.swing.JMenuItem();
        mnuAbrir = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuOpciones = new javax.swing.JMenu();
        mnuMetodo = new javax.swing.JMenu();
        mnuMetBlowfish = new javax.swing.JMenuItem();
        mnuMetTwofish = new javax.swing.JMenuItem();
        mnuMetDES = new javax.swing.JMenuItem();
        mnuMetTDES = new javax.swing.JMenuItem();
        mnuPassword = new javax.swing.JMenuItem();

        saveDialog.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        saveDialog.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Text Encrypter");

        jPanel1.setLayout(new java.awt.BorderLayout());

        jToolBar1.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar1.setRollover(true);

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/open_new_file.png"))); // NOI18N
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

        encButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/encriptar.png"))); // NOI18N
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

        decButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/desencriptar.png"))); // NOI18N
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

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Save_icon.png"))); // NOI18N
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

        jPanel1.add(jToolBar1, java.awt.BorderLayout.NORTH);

        lblEstado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEstado.setText("Método: Twofish");
        jPanel1.add(lblEstado, java.awt.BorderLayout.SOUTH);
        lblEstado.getAccessibleContext().setAccessibleParent(jPanel1);

        jPanel2.setLayout(new java.awt.GridLayout(3, 1));

        textoEncriptado1.setColumns(20);
        textoEncriptado1.setLineWrap(true);
        textoEncriptado1.setRows(5);
        textoEncriptado1.setEnabled(false);
        jScrollPane1.setViewportView(textoEncriptado1);

        jPanel2.add(jScrollPane1);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(166, 106));

        textoClaro.setColumns(20);
        textoClaro.setLineWrap(true);
        textoClaro.setRows(5);
        textoClaro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textoClaroKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textoClaroKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(textoClaro);

        jPanel2.add(jScrollPane2);

        textoEncriptado2.setColumns(20);
        textoEncriptado2.setLineWrap(true);
        textoEncriptado2.setRows(5);
        textoEncriptado2.setEnabled(false);
        jScrollPane3.setViewportView(textoEncriptado2);

        jPanel2.add(jScrollPane3);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        mnuArchivo.setText("Archivo");

        mnuNuevo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        mnuNuevo.setText("Nuevo");
        mnuArchivo.add(mnuNuevo);

        mnuAbrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        mnuAbrir.setText("Abrir");
        mnuArchivo.add(mnuAbrir);
        mnuArchivo.add(jSeparator1);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Guardar y salir");
        mnuArchivo.add(jMenuItem1);

        mnuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuSalir.setText("Salir");
        mnuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalirActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuSalir);

        jMenuBar1.add(mnuArchivo);

        mnuOpciones.setText("Opciones");

        mnuMetodo.setText("Método");

        mnuMetBlowfish.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuMetBlowfish.setText("Blowfish");
        mnuMetBlowfish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMetBlowfishActionPerformed(evt);
            }
        });
        mnuMetodo.add(mnuMetBlowfish);

        mnuMetTwofish.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuMetTwofish.setText("Twofish");
        mnuMetTwofish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMetTwofishActionPerformed(evt);
            }
        });
        mnuMetodo.add(mnuMetTwofish);

        mnuMetDES.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuMetDES.setText("DES");
        mnuMetDES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMetDESActionPerformed(evt);
            }
        });
        mnuMetodo.add(mnuMetDES);

        mnuMetTDES.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuMetTDES.setText("TDES");
        mnuMetTDES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMetTDESActionPerformed(evt);
            }
        });
        mnuMetodo.add(mnuMetTDES);

        mnuOpciones.add(mnuMetodo);

        mnuPassword.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        mnuPassword.setText("Password");
        mnuPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPasswordActionPerformed(evt);
            }
        });
        mnuOpciones.add(mnuPassword);

        jMenuBar1.add(mnuOpciones);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
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
                textoEncriptado1.setText(buffer);
                //Key for the selected file is entered
                leerLlave();
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
                printWriter.write(textoEncriptado1.getText());
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
                        printWriter.write(textoEncriptado1.getText());
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
        encriptar1();
    }//GEN-LAST:event_encButtonActionPerformed
    
    private void decButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decButtonActionPerformed
        // TODO add your handling code here:
        String text = textoEncriptado1.getText();
        String decText = encriptador.Desencriptar(text.getBytes());
        textoClaro.setText(decText);
    }//GEN-LAST:event_decButtonActionPerformed

    private void mnuMetBlowfishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMetBlowfishActionPerformed
        // TODO add your handling code here:
        
        metodo = "blowfish";
        longitudBloque = 8;
        longitudLlave = 8;
        
        key = arreglaLlave(keyOriginal, longitudLlave);
        
        imprimirMetodoLongitudBloque();
        
        try {
            encriptador = new Encriptador(metodo, longitudBloque, key);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mnuMetBlowfishActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        // TODO add your handling code here:
        
        System.exit(0);
    }//GEN-LAST:event_mnuSalirActionPerformed

    private void textoClaroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textoClaroKeyPressed
        // TODO add your handling code here:
        
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            encriptar1();
        }else if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_DOWN){
            desencriptar1();
        }else if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_UP){
            desencriptar2();
        }
    }//GEN-LAST:event_textoClaroKeyPressed

    private void textoClaroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textoClaroKeyReleased
        // TODO add your handling code here:
        
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            textoClaro.setText("");
        }
    }//GEN-LAST:event_textoClaroKeyReleased

    private void mnuPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPasswordActionPerformed
        // TODO add your handling code here:
        
        leerLlave();
    }//GEN-LAST:event_mnuPasswordActionPerformed

    private void mnuMetTwofishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMetTwofishActionPerformed
        // TODO add your handling code here:
        
        metodo = "twofish";
        longitudBloque = 16;
        longitudLlave = 16;
        
        key = arreglaLlave(keyOriginal, longitudLlave);
        
        imprimirMetodoLongitudBloque();
        
        try {
            encriptador = new Encriptador(metodo, longitudBloque, key);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mnuMetTwofishActionPerformed

    private void mnuMetDESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMetDESActionPerformed
        // TODO add your handling code here:
        
        metodo = "des";
        longitudBloque = 8;
        longitudLlave = 8;
        
        key = arreglaLlave(keyOriginal, longitudLlave);
        
        imprimirMetodoLongitudBloque();
        
        try {
            encriptador = new Encriptador(metodo, longitudBloque, key);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mnuMetDESActionPerformed

    private void mnuMetTDESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMetTDESActionPerformed
        // TODO add your handling code here:
        
        metodo = "tripledes";
        longitudBloque = 8;
        longitudLlave = 24;
        
        key = arreglaLlave(keyOriginal, longitudLlave);
        
        imprimirMetodoLongitudBloque();
        
        try {
            encriptador = new Encriptador(metodo, longitudBloque, key);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mnuMetTDESActionPerformed
/**/
    
/**/
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
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton decButton;
    private javax.swing.JButton encButton;
    private javax.swing.JFileChooser fileOpener;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JMenuItem mnuAbrir;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuMetBlowfish;
    private javax.swing.JMenuItem mnuMetDES;
    private javax.swing.JMenuItem mnuMetTDES;
    private javax.swing.JMenuItem mnuMetTwofish;
    private javax.swing.JMenu mnuMetodo;
    private javax.swing.JMenuItem mnuNuevo;
    private javax.swing.JMenu mnuOpciones;
    private javax.swing.JMenuItem mnuPassword;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JButton openButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JFileChooser saveDialog;
    private javax.swing.JTextArea textoClaro;
    private javax.swing.JTextArea textoEncriptado1;
    private javax.swing.JTextArea textoEncriptado2;
    // End of variables declaration//GEN-END:variables
}
