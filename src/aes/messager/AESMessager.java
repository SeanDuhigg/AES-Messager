package aes.messager;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

/**
 * A program that allows 2 users on the same network subnet to send encrypted messages back and forth
 * @author Sean
 */
public class AESMessager{
    
    private static Socket clientSocket; 
    private static final String secret = "1234567812345678"; //for encryption
    
    public static void main(String[] args) {
               
        int port, partnerPort;
        System.out.println("Are you endpoint 1 or 2?");
        Scanner scan = new Scanner(System.in);
        int input = scan.nextInt();
        System.out.println("What is your partner's IP address?");
        Scanner scan1 = new Scanner(System.in);
        String ipAddr = scan1.nextLine();
        if(input == 1){
            port = 10001;
            partnerPort = 10002;
        }else if(input == 2){
            port = 10002;
            partnerPort = 10001;
        }else{
            System.out.println("Bad input");
            port = 0;
            partnerPort = 0;
        }
        
        ThreadPool networkService;
        String answer = "";
        String message = "";
        try{
            
            networkService = new ThreadPool(port, 10);
                       
        }catch(IOException e){
            System.out.println(e);
            return;
        }
        Thread mainThread = new Thread(networkService);
        mainThread.start(); // Start the thread.
              
        System.out.println("Give me a message to send to the other endpoint(hit x to end):");
        while(!message.equals("x")){
            
            Scanner endpoint = new Scanner(System.in);
            message = endpoint.nextLine();
            if(!message.equals("x")){
            
                //Crypto here
                try{
                
                    // Get and encrypt the message
                    SecretKeySpec key = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");
                    Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
                    cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
                    byte[] enc = cipher.doFinal(message.getBytes());
                
                    //convert the byte array to Base64 string
                    String base64String = Base64.getEncoder().encodeToString(enc);
                    System.out.println("Encrypted/encoded message looks like this: "+ base64String); //comment out this line to get a more fluid messaging experience
                    
                    //send the message
                    clientSocket = new Socket(ipAddr, partnerPort);
                    OutputStream os = clientSocket.getOutputStream();
                    PrintWriter pw = new PrintWriter(os, true);
                    pw.println(base64String);
           
                }catch(Exception e){
                    System.out.println(e);
                }
            }//if(!message.equals)
        }//while(!message.equals)
        
        //stop the ns and close the thread pool
        networkService.stopService();
    }
}

