package aes.messager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Sean
 */
public class ClientHandler implements Runnable{
    
    private final Socket socket;
    private String secret = "1234567812345678";
    
    ClientHandler(Socket socket){
        this.socket = socket;
    }
    public void run(){
    try{
                //open input stream and get the message
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String message = br.readLine();
                
                //convert the Base64 string back to a byte array for decryption                
                System.out.println("Encrypted/encoded message looks like this: "+ message); //comment out this line to get a more fluid messaging experience
                byte[] cipherText = Base64.getDecoder().decode(message);
                                
                //decryption
                try{
                    SecretKeySpec key = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");
                    Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
                    cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
                    String plainText = new String(cipher.doFinal(cipherText));
                    System.out.println("And decrypted you get back your original message: " +plainText);
                }catch(Exception e){
                    System.out.println(e);
                }    
       
                
        }catch(Exception e) {
                e.printStackTrace();
        }
    }
}
