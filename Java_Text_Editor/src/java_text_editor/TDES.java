/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java_text_editor;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
        
        

/**
 *
 * @author valeb
 */
public class TDES {
    
    byte[] key;
    Key deskey = null;
    DESedeKeySpec spec;
    Cipher cipher;
    
    public TDES(String keyText){
        key = keyText.getBytes();
        try {
            spec = new DESedeKeySpec(key);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);
            
            cipher = Cipher.getInstance("desede"+"/ECB/PKCS5Padding");
                       
        } catch (InvalidKeyException ex) {
            Logger.getLogger(TDES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TDES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(TDES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(TDES.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
        
    
    public String enc(String text) {
        byte[] data = text.getBytes();
        
        try {

            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            
            byte[] CipherText = cipher.doFinal(data);
            
            System.out.println(CipherText);
            
            StringBuffer hexCiphertext = new StringBuffer();
            for (int i=0;i<CipherText.length;i++){
                hexCiphertext.append(Integer.toString((CipherText[i]&0xff)+0x100,16).substring(1));
            }
            
            System.out.println("Ciphertext is "+hexCiphertext);
            
            return new String(hexCiphertext);
            
        } catch (InvalidKeyException ex) {
            Logger.getLogger(TDES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(TDES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(TDES.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Error";
    }
    
    
    public String dec(String text) {
        byte[] data = text.getBytes();
        
        try {
            
            Cipher cipher = Cipher.getInstance("desede"+"/ECB/PKCS5Padding");
          
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            int len = text.length();
            byte[] res = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                res[i / 2] = (byte) ((Character.digit(text.charAt(i), 16) << 4)
                                     + Character.digit(text.charAt(i+1), 16));
            }
            
            System.out.println(res);
                   
            byte [] plaintext = cipher.doFinal(res);
            
            System.out.println("Plaintext is "+new String(plaintext));
            
            return new String(plaintext);
            
        } catch (InvalidKeyException ex) {
            Logger.getLogger(TDES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TDES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(TDES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(TDES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(TDES.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Error";
    }
}
