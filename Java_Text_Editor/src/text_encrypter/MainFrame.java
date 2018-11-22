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

//Open source code used (but modified) for the text editor:
//Project: Java Text Editor from https://drive.google.com/file/d/0B1WF0QtbznAhT3hnVjBXbk1JazA/view
//Code Author: http://www.genuinecoder.com
//Copyright (c) 2016 Genuine Coder

public class MainFrame extends javax.swing.JFrame {

    //Aarchivo en el que se está trabajando
    File currentEditingFile = null;
    
    //Tamaño de la fuente
    //int fontSize = 14;
    
    //Variable que contiene el encriptador
    Encriptador encriptador = null;
    
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
    
    //Variable para identificar si se han dado cambios en el documento.
    boolean cambios;
    //Bandera para cancelar el abrir de una pantalla cuando se abre un archivo y cancela en escoger un metodo
    boolean banderaMetodo;
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
                
                if (cambios){
                    int ans = JOptionPane.showConfirmDialog(rootPane, "¿Guardar cambios?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (ans == JOptionPane.YES_OPTION) {
                        guardarArchivo();
                    }
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
        leerArchivo();
    }
    
    /**
     * Constructor que recibe los parámetros para abrir un archivo inicial
     * @param file El archivo que se debe abrir
     * @param key La llave con que se va a abrir el archivo
     * @param metodo El método de encriptación utilizado
     * @param longitudBloque La longitud del bloque encriptado
     * @param longitudLlave La longitud de la llave utilizada
     */
    public MainFrame(File file, String key, String metodo, int longitudBloque, int longitudLlave) {
        initComponents();
        
        inicializaVariables();
        
        this.setLocationRelativeTo(null);
        
        this.keyOriginal = key;
        this.key = key;
        this.metodo = metodo;
        this.longitudBloque = longitudBloque;
        this.longitudLlave = longitudLlave;
        crearEncriptador();

        currentEditingFile = file;
        leerArchivo();
    }
    
    /*
     *Inicializa las variables: las longitudes del bloque y de la llave,
     *la extensión de los archivos, el método inicial e inicializa el encriptador,
     *se define la llave inicial
     */
    private void inicializaVariables(){
        cambios = false;
        
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
    
    /*
     * Método que lee la llave por parte del usuario.
     */
    private void leerLlave(){
        
        keyOriginal = JOptionPane.showInputDialog("Ingrese la llave:", "");
        if (keyOriginal == null)
            keyOriginal = "";
        
        key = arreglaLlave(keyOriginal, longitudLlave);
    }
    
    /*
     * Método que lee el método de encriptación por parte del usuario,
     * por el momento el programa sólo acepta cuatro métodos: "Blowfish", "Twofish", "DES", "TripleDES"
     */
    private void leerMetodo(){
        String[] metodos = { "Blowfish", "Twofish", "DES", "TripleDES" };
        
        banderaMetodo = true;
        String met = (String) JOptionPane.showInputDialog(this, 
                "¿Elija el método de encriptación?", "Método",
                JOptionPane.QUESTION_MESSAGE, null, 
                metodos, metodos[1]);
        if (met==null){
            banderaMetodo = false;
        }
        if (metodos[0].equals(met))
            definirMetodo("blowfish", 8, 8);
        else if (metodos[1].equals(met))
            definirMetodo("twofish", 16, 16);
        else if (metodos[2].equals(met))
            definirMetodo("des", 8, 8);
        else if (metodos[3].equals(met))
            definirMetodo("tripledes", 8, 24);
    }
    
    /*
     * Método que arregla la llave para que tenga la longitud necesaria,
     * si la llave es de menor longitud la rellena de espacios en blanco,
     * si es de mayor longitud entonces la corta.
     */
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
    
    /*
     * Método que limpia las variables utilizadas para encriptar y desencriptar,
     * también limpia los textos de la vista gráfica.
     */
    private void limpiarVariables(){
        byteEnc1.clear();
        byteEnc2.clear();
        txtEnc1.clear();
        txtEnc2.clear();
        textoEncriptado1.setText("");
        textoEncriptado2.setText("");
        textoClaro.setText("");
        cambios = false;
    }

    /*
     * Método para leer un archivo encriptado
     */
    private void leerArchivo() {
        FileInputStream fos = null;
        
        limpiarVariables();
        
        try{
            fos = new FileInputStream(currentEditingFile);
            System.out.println(currentEditingFile.getPath());

            int salir = 0;
            while(salir != -1){
                int bloques = salir = fos.read();
                if (bloques != -1){
                    byte[] resp = new byte[bloques * longitudBloque];
                    salir = fos.read(resp);
                    if (salir != -1){
                        byteEnc1.add(resp);
                        txtEnc1.add(new String(resp));
                        textoEncriptado1.setText(textoEncriptado1.getText().concat(((String)txtEnc1.peek()).concat("\n")));
                    }
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

    /*
     * Método que guarda un archivo encriptado
     */
    private void guardarArchivo() {
        if (encriptador == null){
            leerLlave();               
            leerMetodo();
            crearEncriptador();
        }
        encriptar1();
        
        if (currentEditingFile == null)
            guardarComoArchivo();
        else{
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(currentEditingFile);

                for(int i = 0; i < byteEnc1.size(); i++){
                    fos.write(((byte[])byteEnc1.elementAt(i)).length/longitudBloque);
                    fos.write((byte[])byteEnc1.elementAt(i));//, i*longitudBloque, longitudBloque);
                }

                for(int i = 0; i < byteEnc2.size(); i++){
                    fos.write(((byte[])byteEnc2.elementAt(i)).length/longitudBloque);
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
                    cambios = false;
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException ex){ }
            }
        }
    }
    
    /*
     * Método que guarda un archivo encriptado con otro nombre
     */
    private void guardarComoArchivo() {
        int status = saveDialog.showOpenDialog(rootPane);
        if (status == JFileChooser.APPROVE_OPTION) {
            //We got directory. Now needs file name
            boolean archivoExiste = true;
            while (archivoExiste == true){
                String fileName = JOptionPane.showInputDialog("Nombre del archivo", "Untitled.enc");
                if (!fileName.contains(".enc")) {
                    fileName += ".enc";
                }
                File f = new File(saveDialog.getSelectedFile() + "\\" + fileName);

                if (f.exists()) {
                    JOptionPane.showMessageDialog(rootPane, "El archivo ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    archivoExiste = false;
                    try {
                        f.createNewFile();
                        currentEditingFile = f;

                        guardarArchivo();
                    } catch (IOException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            System.out.println("No se seleccionó archivo");
        }
    }
    
    /*
     * Método que lee el nombre del archivo
     */
    private int leerNombreArchivo(){
        int status = fileOpener.showOpenDialog(rootPane);
        if (status == JFileChooser.APPROVE_OPTION) {
            currentEditingFile = fileOpener.getSelectedFile();
            System.out.println("Archivo seleccionado. Nombre = " + fileOpener.getSelectedFile().getName());
        }
        
        return status;
    }
    
    /*
     * Método que imprime en la barra de estado el método que se está utilizando y la longitud del bloque
     */
    private void imprimirMetodoLongitudBloque(){
        lblEstado.setText("Método: " + metodo + "    Longitud del bloque: " + longitudBloque);
    }
    
    /*
     * Método que encripta el texto en claro en pantalla y lo coloca en el campo de texto de arriba
     */
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
    
    /*
     * Método que encripta el texto en claro en pantalla y lo coloca en el campo de texto de abajo
     */
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
    
    /*
     * Método que desencripta una línea del campo de texto de arriba y lo muestra como texto en claro,
     * si había texto en claro entonces primero lo encripta y lo pasa para abajo
     */
    private void desencriptar1(){
        String text = textoClaro.getText();
        
        if (!"".equals(text))
            encriptar2();
        
        if (!byteEnc1.isEmpty()){
            text = encriptador.Desencriptar((byte[])byteEnc1.pop());

            textoClaro.setText(limpiarTexto(text));

            //Se quita el texto de textoEncriptado1
            text = textoEncriptado1.getText();
            if (text.length() > ((String)txtEnc1.peek()).length() - 1)
                textoEncriptado1.setText(text.substring(0, text.length() - ((String)txtEnc1.pop()).length() - 1));
            else
                textoEncriptado1.setText("");
        }
    }
    
    /*
     * Método que desencripta una línea del campo de texto de abajo y lo muestra como texto en claro,
     * si había texto en claro entonces primero lo encripta y lo pasa para arriba
     */
    private void desencriptar2(){
        String text = textoClaro.getText();
        
        if (!"".equals(text))
            encriptar1();
        
        if (!byteEnc2.isEmpty()){
            text = encriptador.Desencriptar((byte[])byteEnc2.pop());

            textoClaro.setText(limpiarTexto(text));

            //Se quita el texto de textoEncriptado2
            text = textoEncriptado2.getText();
            if(text.length() > ((String)txtEnc2.peek()).length() + 1)
                textoEncriptado2.setText(text.substring(((String)txtEnc2.pop()).length() + 1));
            else
                textoEncriptado2.setText("");
        }
    }
    
    /*
     * Método que recibe un texto desencriptado y le quita los espacios en blanco del final,
     * dichos espacios fueron agregados para completar la cantidad de bytes que se necesitan.
     */
    private String limpiarTexto(String text){
        String resp = text;
        
        while(resp.endsWith(" ")){
            resp = resp.substring(0, resp.length() - 2);
        }
        
        return resp;
    }
    
    /*
     * Método que abre un archivo, lo desencripta y lo muestra en pantalla
     */
    private void abrirArchivo(){
        //Show File Open dialogue here
        int status = fileOpener.showOpenDialog(rootPane);
        if (status == JFileChooser.APPROVE_OPTION) {
            //Key for the selected file is entered
            leerLlave();
            if(keyOriginal==""){
                return;
            }
            leerMetodo();
            if(!banderaMetodo){
                return;
            }
            
            if (currentEditingFile != null) {
                // A file is opened and is being edited. Open the new file in new window  
                MainFrame newWindow = new MainFrame(fileOpener.getSelectedFile(), key, metodo, longitudBloque, longitudLlave);
                newWindow.setVisible(true);
                newWindow.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                newWindow.pack();
                return;
            }
            currentEditingFile = fileOpener.getSelectedFile();

            crearEncriptador();
            
            leerArchivo();
        } else {
            System.out.println("No se seleccionó archivo");
        }
    }
    
    /*
     * Método que crea un nuevo documento, si ya hay uno abierto entonces lo abre
     * en una nueva ventana.
     */
    private void nuevoDocumento(){
        limpiarVariables();
        leerLlave();
        leerMetodo();
        if (cambios) {
            MainFrame newWindow = new MainFrame(fileOpener.getSelectedFile());
            newWindow.setVisible(true);
            newWindow.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            newWindow.pack();
            salir();
        }
    }
    
    /*
     * Método para salir del programa
     */
    private void salir(){
        this.setVisible(false);
        this.dispose();
    }
    
    /*
     * Método que define el método escogido por el usuario
     */
    private void definirMetodo(String met, int longitudB, int longitudL){
        //Se debe desencriptar con el encriptador anterior y encriptar con el nuevo
        
        Encriptador encriptadorAnterior = encriptador;
        
        metodo = met;
        longitudBloque = longitudB;
        longitudLlave = longitudL;
        
        System.out.println(keyOriginal + " " + longitudB + " " + longitudL);
        key = arreglaLlave(keyOriginal, longitudLlave);
        
        imprimirMetodoLongitudBloque();
        
        crearEncriptador();
        
        //Limpia lo que estaba encriptado en los textos con el método anterior
        textoEncriptado1.setText("");
        textoEncriptado2.setText("");
        
        //Desencripta y encripta nuevamente
        for (int i = 0; i < byteEnc1.size(); i++ )
            byteEnc1.setElementAt(encriptador.Encriptar(encriptadorAnterior.Desencriptar((byte[])byteEnc1.get(i))), i);
        for (int i = 0; i < byteEnc2.size(); i++ )
            byteEnc2.setElementAt(encriptador.Encriptar(encriptadorAnterior.Desencriptar((byte[])byteEnc2.get(i))), i);
        //Define los textos encriptados y los muestra en pantalla
        for (int i = 0; i < byteEnc1.size(); i++ ){
            txtEnc1.setElementAt(new String((byte[])byteEnc1.get(i)), i);
            textoEncriptado1.setText(textoEncriptado1.getText().concat(((String)txtEnc1.get(i)).concat("\n")));
        }
        for (int i = 0; i < byteEnc2.size(); i++ ){
            txtEnc2.setElementAt(new String((byte[])byteEnc2.get(i)), i);
            textoEncriptado2.setText(textoEncriptado2.getText().concat(((String)txtEnc2.get(i)).concat("\n")));
        }
        
        
    }
    
    /*
     * Método que construye el objeto que encripta.
     */
    private void crearEncriptador(){
        try {
            encriptador = new Encriptador(metodo,longitudBloque,key);
        } catch (InvalidKeyException ex) {
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
        btnNuevo = new javax.swing.JButton();
        btnAbrir = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
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
        jMenuItem1 = new javax.swing.JMenuItem();
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

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/guardar.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.setFocusable(false);
        btnGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnGuardar);

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
        btnDesencriptar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesencriptar2ActionPerformed(evt);
            }
        });
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
        mnuNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNuevoActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuNuevo);

        mnuAbrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        mnuAbrir.setText("Abrir");
        mnuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAbrirActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuAbrir);
        mnuArchivo.add(jSeparator1);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Guardar como...");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        mnuArchivo.add(jMenuItem1);

        mnuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        mnuGuardar.setText("Guardar");
        mnuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGuardarActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuGuardar);

        mnuGuardarSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mnuGuardarSalir.setText("Guardar y salir");
        mnuGuardarSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGuardarSalirActionPerformed(evt);
            }
        });
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
        abrirArchivo();
    }//GEN-LAST:event_btnAbrirActionPerformed

    private void btnGuardarSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarSalirActionPerformed
        guardarArchivo();      
        salir();
    }//GEN-LAST:event_btnGuardarSalirActionPerformed
    
    private void mnuMetBlowfishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMetBlowfishActionPerformed
        // TODO add your handling code here:
        definirMetodo("blowfish", 8, 8);
    }//GEN-LAST:event_mnuMetBlowfishActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        // TODO add your handling code here:
        
        salir(); //Destroy the window
    }//GEN-LAST:event_mnuSalirActionPerformed

    private void textoClaroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textoClaroKeyPressed
        // TODO add your handling code here:
        
        // Si se presiona una tecla cualquiera en el texto en claro entonces se marca que se produjo un cambio
        cambios = true;
        
        //Si se precionó el enter, ctrl-down o ctrl-up
        if (evt.getKeyCode() == KeyEvent.VK_ENTER || 
                (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_DOWN) || 
                (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_UP)){
        
            // Si no se había definido la llave entonces es lo primero que se debe hacer
            if ("".equals(key)){
                leerLlave();
                
                leerMetodo();

                crearEncriptador();
            }

            if (evt.getKeyCode() == KeyEvent.VK_ENTER){ //Si se presionó el enter
                encriptar1();
            }else if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_DOWN){ // Si se presionó el ctrl-down
                desencriptar1();
            }else if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_UP){ // Si se presionó el ctrl-up
                desencriptar2();
            }
        }
    }//GEN-LAST:event_textoClaroKeyPressed

    private void textoClaroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textoClaroKeyReleased
        // TODO add your handling code here:
        
        // Al liberar la tecla del enter entonces se borra el texto en claro
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            textoClaro.setText("");
        }
    }//GEN-LAST:event_textoClaroKeyReleased

    private void mnuPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPasswordActionPerformed
        // TODO add your handling code here:
        
        //Se debe desencriptar con el encriptador anterior y encriptar con el nuevo
        
        Encriptador encriptadorAnterior = encriptador;
        
        leerLlave();
        
        crearEncriptador();
        
        //Limpia lo que estaba encriptado en los textos con el método anterior
        textoEncriptado1.setText("");
        textoEncriptado2.setText("");
        
        //Desencripta y encripta nuevamente
        for (int i = 0; i < byteEnc1.size(); i++ )
            byteEnc1.setElementAt(encriptador.Encriptar(encriptadorAnterior.Desencriptar((byte[])byteEnc1.get(i))), i);
        for (int i = 0; i < byteEnc2.size(); i++ )
            byteEnc2.setElementAt(encriptador.Encriptar(encriptadorAnterior.Desencriptar((byte[])byteEnc2.get(i))), i);
        //Define los textos encriptados y los muestra en pantalla
        for (int i = 0; i < byteEnc1.size(); i++ ){
            txtEnc1.setElementAt(new String((byte[])byteEnc1.get(i)), i);
            textoEncriptado1.setText(textoEncriptado1.getText().concat(((String)txtEnc1.get(i)).concat("\n")));
        }
        for (int i = 0; i < byteEnc2.size(); i++ ){
            txtEnc2.setElementAt(new String((byte[])byteEnc2.get(i)), i);
            textoEncriptado2.setText(textoEncriptado2.getText().concat(((String)txtEnc2.get(i)).concat("\n")));
        }
    }//GEN-LAST:event_mnuPasswordActionPerformed

    private void mnuMetTwofishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMetTwofishActionPerformed
        // TODO add your handling code here:
        
        definirMetodo("twofish", 16, 16);
    }//GEN-LAST:event_mnuMetTwofishActionPerformed

    private void mnuMetDESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMetDESActionPerformed
        // TODO add your handling code here:
        
        definirMetodo("des", 8, 8);
    }//GEN-LAST:event_mnuMetDESActionPerformed

    private void mnuMetTDESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMetTDESActionPerformed
        // TODO add your handling code here:
        
        definirMetodo("tripledes", 8, 24);
    }//GEN-LAST:event_mnuMetTDESActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        
        guardarArchivo();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        // TODO add your handling code here:
        
        nuevoDocumento();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnDesencriptar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesencriptar1ActionPerformed
        // TODO add your handling code here:
        
        desencriptar1();
    }//GEN-LAST:event_btnDesencriptar1ActionPerformed

    private void btnDesencriptar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesencriptar2ActionPerformed
        // TODO add your handling code here:
        
        desencriptar2();
    }//GEN-LAST:event_btnDesencriptar2ActionPerformed

    private void btnEncriptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEncriptarActionPerformed
        // TODO add your handling code here:
        
        //Si no se ha definido la llave es lo primero que se debe hacer
        if ("".equals(key)){
            leerLlave();
            
            leerMetodo();

            crearEncriptador();
        }
        
        encriptar1();
    }//GEN-LAST:event_btnEncriptarActionPerformed

    private void mnuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAbrirActionPerformed
        // TODO add your handling code here:
        
        abrirArchivo();
    }//GEN-LAST:event_mnuAbrirActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        // TODO add your handling code here:
        
        guardarArchivo();
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuGuardarSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarSalirActionPerformed
        // TODO add your handling code here:
        
        guardarArchivo();
        
        salir();
    }//GEN-LAST:event_mnuGuardarSalirActionPerformed

    private void mnuNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNuevoActionPerformed
        // TODO add your handling code here:
        
        nuevoDocumento();
    }//GEN-LAST:event_mnuNuevoActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        // TODO add your handling code here:
        
        salir();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        
        guardarComoArchivo();
    }//GEN-LAST:event_jMenuItem1ActionPerformed
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
            @Override
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
