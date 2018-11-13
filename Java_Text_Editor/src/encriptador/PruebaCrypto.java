/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encriptador;

import java.security.InvalidKeyException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aborbon
 */
public class PruebaCrypto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        //Texto a encriptar
        String textoClaro = "Texto en claro que se va a encriptar";
        
        //Variable donde se guardará el texto encriptado
        byte[] textoEncriptado = "El resultado de la encritación se guardará acá".getBytes();
        
        //Variable donde se guardará la desencriptación del texto anterior encriptado
        String textoDesencriptado = "Texto en claro que se obtiene al desencriptar";
        
        Encriptador e;
        try {
            //Constructor con el método, el tamaño del bloque y la contraseña
            e = new Encriptador("Twofish", 16, "1234567890123456");
            
            System.out.println("\nAl inicio:\n");
            System.out.println(textoClaro);
            System.out.println(new String(textoEncriptado));
            System.out.println(textoDesencriptado);
            
            textoEncriptado = e.Encriptar(textoClaro);

            System.out.println("\nDespués de encriptar:\n");
            System.out.println(textoClaro);
            System.out.println(new String(textoEncriptado));
            System.out.println(textoDesencriptado);
            
            textoDesencriptado = e.Desencriptar(textoEncriptado);
            
            System.out.println("\nDespués de desencriptar:\n");
            System.out.println(textoClaro);
            System.out.println(new String(textoEncriptado));
            System.out.println(textoDesencriptado);
            
        } catch (InvalidKeyException ex) {
            Logger.getLogger(PruebaCrypto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void inicializarEncriptador(){
        
    }
    
}
