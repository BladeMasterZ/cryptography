/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;
import org.json.JSONObject;

/**
 *
 * @author zhangxian
 */
public class TCPInstallerUsingTEAandPasswords {
        public static void main(String args[]) {
  while(true){         
        // arguments supply hostname
         Socket clientSocket = null;
        try {
           BigInteger e = new BigInteger("65537"); // public key 
           BigInteger n = new BigInteger("6322445714982675228243524273195059608402654691709514432285852950168218844873071915902635351011397773325637034999370496345983288092907487815622569128115875904562853680760794585411985508563319091481727498710204722658471279186132333487122827667");
           RSAExample rsa = new RSAExample();
            String port = "localhost";
            int serverPort = 7777;
            clientSocket = new Socket(port, serverPort);

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));


            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
// random #
            BigInteger rsaBigInteger = rsa.redom16digt();
// encrypt 
            BigInteger encrypt = rsa.encrypt(rsaBigInteger, e, n);
            String hexBinary = DatatypeConverter.printHexBinary(encrypt.toByteArray()); 
// send string 
            out.println(hexBinary);
            out.flush();
 //  Tea encrypt          
            TEA tea = new TEA( rsaBigInteger.toByteArray());
            System.out.println("Enter your ID:");
            String id = typed.readLine() ;
            System.out.println("Enter your Password:");
            String pwd = typed.readLine() ;
            System.out.println("Enter sensor ID:");
            String sid = typed.readLine() ;
            System.out.println("Enter new sensor location:");
            String location = typed.readLine() ;
            while (!location.contains(",")){
                System.out.println("Enter new sensor location:");
                location = typed.readLine() ;
            }
            
            //splite to Latitude and Longitude from stack over flow 
            String[] locationArray = location.trim().split("\\s*,\\s*");
            String latitude =locationArray [0];
            String longitude =locationArray [1];
            String height =locationArray [2];
            //parse to Json 
            JSONObject obj = new JSONObject();
            obj.put("ID", id);
            obj.put("passwd", pwd);                                                                                                                                                                                                             
            obj.put("Sensor ID", sid);
            obj.put("Latitude",latitude );
            obj.put("Longitude", longitude);
            obj.put("Height", height);
            
            String jsonObj = obj.toString();
        
            
            //Start encript
            tea.encrypt(jsonObj.getBytes());
            out.println(Arrays.toString(tea.encrypt(jsonObj.getBytes())));
            out.flush();
            String data = in.readLine();
            // send back data 
            String sendBackData = new String(tea.decrypt(fromString(data)));
            System.out.println(sendBackData);
           
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
  } 
    }

 // change string to byte from stackover flow    
    public static byte[] fromString(String string) {
    String[] strings = string.replace("[", "").replace("]", "").split(", ");
    byte result[] = new byte[strings.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = Byte.parseByte(strings[i]); 
    }
    return result;
    
  }
    
}
