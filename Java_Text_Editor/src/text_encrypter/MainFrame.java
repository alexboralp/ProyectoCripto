package text_encrypter;

import encriptador.Encriptador;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Stack;

public class MainFrame extends javax.swing.JFrame {

    //Aarchivo en el que se está trabajando
    File currentEditingFile = null;
    
    //Tamaño de la fuente
    //int fontSize = 14;
    
    //Variable que contiene el encriptador
    Encriptador encriptador;
    
    //La llave arreglada y la llave original que escribió el usuario
    String key, keyOriginal;
    
    //Longitudes tanto del bloque como de la llave que dependen de cada método de encriptación
    int longitudBloque;
    int longitudLlave;
    
    //Variable para guardar el método con el que se está encriptando
    String metodo;
    
    //Pilas para guardar el texto encriptado como arreglo de bytes y los textos como String
    Stack byteEnc1;
    Stack byteEnc2;
    Stack txtEnc1;
    Stack txtEnc2;

    /**
     * Constructor vació de la ventana
     */
    public MainFrame() {
        initComponents();
        
        inicializaVariables();

        //Launch the application on the middle of Screen
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e); //To change body of generated methods, choose Tools | Templates.
                int ans = JOptionPane.showConfirmDialog(rootPane, "¿Guardar cambios?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (ans == JOptionPane.YES_OPTION) {
                    guardarArchivo();
                }
            }

        });
    }

    /**
     * Constructor que recibe el archivo inicial que debe abrir
     * @param file Archivo que debe abrir el programa
     */
    public MainFrame(File file) {
        initComponents();
        
        inicializaVariables();
        
        this.setLocationRelativeTo(null);

        currentEditingFile = file;
        leerArchivo(file);
    }
    
    /*
     *Inicializa las variables: las longitudes del bloque y de la llave,
     *la extensión de los archivos, el método inicial e inicializa el encriptador,
     *se define la llave inicial
     */
    private void inicializaVariables(){
        longitudBloque = 16;
        longitudLlave = 16;
        
        //Filter Files to display
        //Set JFileChooser to accept only text files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Encrypted file", "enc", "encf");
        fileOpener.setFileFilter(filter);
        
        metodo = "twofish";
        key="";
        
        imprimirMetodoLongitudBloque();
        
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
    
    public void limpiarVariables(){
        byteEnc1.clear();
        byteEnc2.clear();
        txtEnc1.clear();
        txtEnc2.clear();
        textoEncriptado1.setText("");
        textoEncriptado2.setText("");
        textoClaro.setText("");
    }

    public void leerArchivo(File file) {
        
        FileInputStream fos = null;
        
        limpiarVariables();
        
        try{
            fos = new FileInputStream(currentEditingFile);

            int i = 0;
            int salir = 0;
            while(salir != -1){
                byte[] resp = new byte[longitudBloque];
                salir = fos.read(resp, i*longitudBloque, longitudBloque);
                if (salir != -1){
                    byteEnc1.add(resp);
                    txtEnc1.add(new String(resp));
                    textoEncriptado1.setText(textoEncriptado1.getText().concat(((String)txtEnc1.peek()).concat("\n")));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex){ }
        }
    }

    public void guardarArchivo() {
        
        encriptar1();
        
        if (currentEditingFile == null)
            guardarComoArchivo();
        else{
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(currentEditingFile);
                for(int i = 0; i < byteEnc1.size(); i++){
                    fos.write((byte[])byteEnc1.elementAt(i));//, i*longitudBloque, longitudBloque);
                }

                for(int i = 0; i < byteEnc2.size(); i++){
                    fos.write((byte[])byteEnc2.elementAt(i));//, offset + i*longitudBloque, longitudBloque);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException ex){ }
            }
        }
    }
    
    public void guardarComoArchivo() {
        
        int status = saveDialog.showOpenDialog(rootPane);
        if (status == JFileChooser.APPROVE_OPTION) {
            //We got directory. Now needs file name
            String fileName = JOptionPane.showInputDialog("Nombre del archivo", "Untitled.txt");
            if (!fileName.contains(".enc")) {
                fileName += ".enc";
            }
            File f = new File(saveDialog.getSelectedFile() + "\\" + fileName);
            if (f.exists()) {
                JOptionPane.showMessageDialog(rootPane, "Archivo ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    f.createNewFile();
                    currentEditingFile = f;
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                guardarArchivo();
            }
        } else {
            System.out.println("No se seleccionó archivo");
        }
    }
    
    public int leerNombreArchivo(){
        int status = fileOpener.showOpenDialog(rootPane);
        if (status == JFileChooser.APPROVE_OPTION) {
            currentEditingFile = fileOpener.getSelectedFile();
            System.out.println("Archivo seleccionado. Nombre = " + fileOpener.getSelectedFile().getName());
        }
        
        return status;
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
            if (text.length() > ((String)txtEnc1.peek()).length() - 1)
                textoEncriptado1.setText(text.substring(0, text.length() - ((String)txtEnc1.pop()).length() - 1));
            else
                textoEncriptado1.setText("");
        }
    }
    
    private void desencriptar2(){
        String text = textoClaro.getText();
        
        if (!"".equals(text))
            encriptar1();
        
        if (!byteEnc2.isEmpty()){
            text = encriptador.Desencriptar((byte[])byteEnc2.pop());

            textoClaro.setText(text);

            //Se quita el texto de textoEncriptado2
            text = textoEncriptado2.getText();
            if(text.length() > ((String)txtEnc2.peek()).length() + 1)
                textoEncriptado2.setText(text.substring(((String)txtEnc2.pop()).length() + 1));
            else
                textoEncriptado2.setText("");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileOpener = new javax.swing.JFileChooser();
        saveDialog = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnGuardar = new javax.swing.JButton();
        btnNuevo = new javax.swing.JButton();
        btnAbrir = new javax.swing.JButton();
        btnEncriptar = new javax.swing.JButton();
        btnDesencriptar1 = new javax.swing.JButton();
        btnDesencriptar2 = new javax.swing.JButton();
        btnGuardarSalir = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
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
        mnuGuardar = new javax.swing.JMenuItem();
        mnuGuardarSalir = new javax.swing.JMenuItem();
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

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/guardar.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.setFocusable(false);
        btnGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnGuardar);

        btnNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/nuevo.png"))); // NOI18N
        btnNuevo.setText("Nuevo");
        btnNuevo.setFocusable(false);
        btnNuevo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNuevo);

        btnAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/abrir.png"))); // NOI18N
        btnAbrir.setText("Abrir");
        btnAbrir.setFocusable(false);
        btnAbrir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbrir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAbrir);

        btnEncriptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/encriptar.png"))); // NOI18N
        btnEncriptar.setText("Encriptar");
        btnEncriptar.setFocusable(false);
        btnEncriptar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEncriptar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEncriptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEncriptarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEncriptar);

        btnDesencriptar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/desencriptar1.png"))); // NOI18N
        btnDesencriptar1.setText("Desencriptar");
        btnDesencriptar1.setFocusable(false);
        btnDesencriptar1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDesencriptar1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDesencriptar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesencriptar1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDesencriptar1);

        btnDesencriptar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/desencriptar2.png"))); // NOI18N
        btnDesencriptar2.setText("Desencriptar");
        btnDesencriptar2.setFocusable(false);
        btnDesencriptar2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDesencriptar2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnDesencriptar2);

        btnGuardarSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/guardarsalir.png"))); // NOI18N
        btnGuardarSalir.setText("Guardar y Salir");
        btnGuardarSalir.setFocusable(false);
        btnGuardarSalir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardarSalir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardarSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarSalirActionPerformed(evt);
            }
        });
        jToolBar1.add(btnGuardarSalir);

        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/salir.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.setFocusable(false);
        btnSalir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSalir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSalir);

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
        mnuAbrir.setText("Abrir...");
        mnuArchivo.add(mnuAbrir);
        mnuArchivo.add(jSeparator1);

        mnuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        mnuGuardar.setText("Guardar");
        mnuArchivo.add(mnuGuardar);

        mnuGuardarSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mnuGuardarSalir.setText("Guardar y salir");
        mnuArchivo.add(mnuGuardarSalir);

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
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirActionPerformed
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
            
            leerArchivo(currentEditingFile);
            
            //Key for the selected file is entered
            leerLlave();

            try {
                encriptador = new Encriptador(metodo, longitudBloque, key);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("No se seleccionó archivo");
        }
    }//GEN-LAST:event_btnAbrirActionPerformed

    private void btnGuardarSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarSalirActionPerformed
        guardarArchivo();
        
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnGuardarSalirActionPerformed

    private void btnEncriptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEncriptarActionPerformed
        // TODO add your handling code here:
        encriptar1();
    }//GEN-LAST:event_btnEncriptarActionPerformed
    
    private void btnDesencriptar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesencriptar1ActionPerformed
        // TODO add your handling code here:
        String text = textoEncriptado1.getText();
        String decText = encriptador.Desencriptar(text.getBytes());
        textoClaro.setText(decText);
    }//GEN-LAST:event_btnDesencriptar1ActionPerformed

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
        
        this.setVisible(false); 
        this.dispose(); //Destroy the window
    }//GEN-LAST:event_mnuSalirActionPerformed

    private void textoClaroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textoClaroKeyPressed
        // TODO add your handling code here:
        
        if (evt.getKeyCode() == KeyEvent.VK_ENTER || 
                (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_DOWN) || 
                (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_UP)){
        
            if ("".equals(key)){
                leerLlave();

                try {
                    encriptador = new Encriptador(metodo,longitudBloque,key);
                } catch (InvalidKeyException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (evt.getKeyCode() == KeyEvent.VK_ENTER){
                encriptar1();
            }else if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_DOWN){
                desencriptar1();
            }else if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_UP){
                desencriptar2();
            }
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

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        // TODO add your handling code here:
        
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        // TODO add your handling code here:
        
        limpiarVariables();
        keyOriginal = "";
        key = "";
    }//GEN-LAST:event_btnNuevoActionPerformed
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
    private javax.swing.JButton btnAbrir;
    private javax.swing.JButton btnDesencriptar1;
    private javax.swing.JButton btnDesencriptar2;
    private javax.swing.JButton btnEncriptar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnGuardarSalir;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnSalir;
    private javax.swing.JFileChooser fileOpener;
    private javax.swing.JMenuBar jMenuBar1;
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
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuGuardarSalir;
    private javax.swing.JMenuItem mnuMetBlowfish;
    private javax.swing.JMenuItem mnuMetDES;
    private javax.swing.JMenuItem mnuMetTDES;
    private javax.swing.JMenuItem mnuMetTwofish;
    private javax.swing.JMenu mnuMetodo;
    private javax.swing.JMenuItem mnuNuevo;
    private javax.swing.JMenu mnuOpciones;
    private javax.swing.JMenuItem mnuPassword;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JFileChooser saveDialog;
    private javax.swing.JTextArea textoClaro;
    private javax.swing.JTextArea textoEncriptado1;
    private javax.swing.JTextArea textoEncriptado2;
    // End of variables declaration//GEN-END:variables
}
