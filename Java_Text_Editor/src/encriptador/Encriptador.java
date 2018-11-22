/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encriptador;

import gnu.crypto.cipher.CipherFactory;
import gnu.crypto.cipher.IBlockCipher;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aborbon
 */
public class Encriptador {
    
    //Variable que hace el enlace con el proyecto gnu.crypto
    IBlockCipher cipher;
    
    // Variable para guardar el tamaño del bloque que se utilizará
    int tamannoBloque;
    
    /**
     * Constructor que recibe el método de encriptación, el tamaño del bloque y la contraseña
     * @param metodo Alguno de los métodos de encriptación permitidos por gnu.crypto
     * @param tamannoBloque Tamaño del bloque con el que se va a trabajar, debe ser un tamaño permitido para el método que se escoge
     * @param contrasenna La llave o contraseña con la que se va a encriptar
     * @throws InvalidKeyException Se lanza esta excepción si la llave no es válida, podría ser porque no tiene un tamaño correcto.
     */
    public Encriptador(String metodo, int tamannoBloque, String contrasenna) throws InvalidKeyException{
        cipher = CipherFactory.getInstance(metodo);
        this.tamannoBloque = tamannoBloque;
        byte[] key_bytes = contrasenna.getBytes();
        Map attributes = new HashMap();
        attributes.put(IBlockCipher.CIPHER_BLOCK_SIZE, tamannoBloque);
        attributes.put(IBlockCipher.KEY_MATERIAL, key_bytes);
        cipher.init(attributes);
    }
    
    /**
     * Se devuelve el tamaño del bloque con el que se está trabajando
     * @return El tamaño del bloque.
     */
    public int getTamannoBloque(){
        return this.tamannoBloque;
    }
    
    /**
     * Método que realiza la encriptación de un texto en claro
     * @param textoClaro El texto que se desea encriptar
     * @return Un arreglo de bytes que representa el texto encriptado
     */
    public byte[] Encriptar(String textoClaro){
        byte[] byteTextoOriginal = textoClaro.getBytes();
        
        int tamanno = byteTextoOriginal.length;
        int pasos;
        if (tamanno % this.tamannoBloque == 0)
            pasos = tamanno / this.tamannoBloque;
        else
            pasos = tamanno / this.tamannoBloque + 1;
        
        byte[] resultado = new byte[pasos * this.tamannoBloque];
        
        for (int i = 0; i < pasos; i++){
            byte[] pt = new byte[this.tamannoBloque];
            //pt = Arrays.copyOfRange(byteTextoOriginal, (i+1)*this.tamannoBloque + j);
            for (int j = 0; j < this.tamannoBloque && i*this.tamannoBloque + j < byteTextoOriginal.length; j++)
                pt[j] = byteTextoOriginal[i*this.tamannoBloque + j];
            cipher.encryptBlock(pt, 0, resultado, i * this.tamannoBloque);
        }
        
        return resultado;
    }
    
    /**
     * Método que desencripta un texto encriptado
     * @param textoEncriptado El texto que se desea desencriptar
     * @return El texto desencriptado
     */
    public String Desencriptar(byte[] textoEncriptado){
        byte[] resultado = new byte[this.tamannoBloque];
        byte[] resultadofinal = new byte[textoEncriptado.length];
        
        for (int i = 0; i + this.tamannoBloque <= textoEncriptado.length; i += this.tamannoBloque){
            cipher.decryptBlock(textoEncriptado, i, resultado, 0);
            System.arraycopy(resultado, 0, resultadofinal, i, this.tamannoBloque);
        }
        
        return new String(resultadofinal);
    }
    
    /*
     * Función que completa un string para que la cantidad de letras calce con un múltiplo del número del tamaño bloque
     */
    public String completaString(String stringOriginal){
        int tamannoOriginal = stringOriginal.length();
        int tamanno = obtenerMultiplo(tamannoOriginal);
        
        String arreglado = stringOriginal;
        
        for (int i = 0; i < tamanno - tamannoOriginal; i++)
            arreglado = arreglado.concat(" ");
        
        return arreglado;
    }
    
    /*
     * Método que obtiene el múltiplo del tamaño del bloque más cercano mayor o igual al número original dado
     */
    private int obtenerMultiplo(int numeroOriginal){
        int multiplo = numeroOriginal / this.tamannoBloque;
        
        if (numeroOriginal % this.tamannoBloque != 0)
            multiplo ++;
        
        multiplo *= this.tamannoBloque;
        
        return multiplo;
    }
}
