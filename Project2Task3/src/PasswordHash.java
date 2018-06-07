/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.xml.bind.DatatypeConverter;



/**
 *
 * @author zhangxian
 */
public class PasswordHash {
// hash value
    public String getHashValue(String inputString) {

        MessageDigest md = null;
        byte[] dt = null;
        String result = null;
        try {
            // md is a MessageDigest object that implements the SHA-256 digest algorithm.
            md = MessageDigest.getInstance("SHA-256");
            md.reset();
            // Updates the digest using the byte array of originalString.
            md.update(inputString.getBytes(StandardCharsets.UTF_8));
            // Performs a final update on the digest using the specified array of bytes, then completes the digest computation.
            dt = md.digest();    
            result = DatatypeConverter.printHexBinary(dt);
            return result;

        } catch (NoSuchAlgorithmException e) {
            System.out.println (e);
            return null;
        }
    }
/// get auto random salt
    public String autoGen () {
        SecureRandom random = new SecureRandom();

        int seed = random.nextInt(10000);
        String i =  Integer.toString(seed);
        return i;
    }
    
    

    public static void main(String[] args) {
     PasswordHash phash = new PasswordHash();   

        try {
            // Set up "in" to read from the client socket
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("#Enter user ID:");
            String uid = typed.readLine();
            System.out.println("#Enter password:");
            String pwd = typed.readLine();
            System.out.println("#Generating a random number for salt using SecureRandom:");

            System.out.println("#User ID = "+uid);
             // get String salt 
            String autoGen =phash.getHashValue(phash.autoGen ());
         
            
            System.out.println("#Salt = "+ autoGen);
            
            //hash hash salt + pwd
            String hashV = phash.getHashValue(autoGen + pwd);
            
     

            System.out.println("#Hash of salt + password = "+ hashV);
while (true){ 
            System.out.println("#Enter user ID for authentication testing");
            String uidVerfy = typed.readLine();
            System.out.println("#Enter password for authentication testing");
            String pwdVerify = typed.readLine();
            // get String salt 
            String hashV2 = phash.getHashValue(autoGen + pwdVerify);
            
            // compare anf contrast 
            if (uid.equals(uidVerfy) && hashV2.equals(hashV) ){
                
            System.out.println("Validated user id and password pair");    

            } else {
             System.out.println("Not able to validate this user id, password pair.");  
            } 
        }
    
    
        } catch (IOException ex) {
            System.out.println("IO Exception:" + ex.getMessage());
        }

    }
}
