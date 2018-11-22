/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encriptador;

import java.util.Stack;

/**
 *
 * @author aborbon
 */
public class TextoEncriptado {
    
    Stack textoEnc;
    
    public TextoEncriptado(){
        super();
        textoEnc = new Stack();
    }
    
    public void push(byte[] textoEncriptado){
        textoEnc.add(textoEncriptado);
    }
    
    public byte[] pop(){
        if (!textoEnc.isEmpty())
            return (byte[]) textoEnc.pop();
        else
            return null;
    }
    
    public byte[] watch(){
        if (!textoEnc.isEmpty())
            return (byte[]) textoEnc.peek();
        else
            return null;
    }
    
}
