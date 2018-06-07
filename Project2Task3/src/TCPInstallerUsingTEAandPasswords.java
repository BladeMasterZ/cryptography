/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import org.json.JSONObject;

/**
 *
 * @author zhangxian
 */
public class TCPInstallerUsingTEAandPasswords {

    public static void main(String args[]) throws NoSuchAlgorithmException, CertificateException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        // arguments supply hostname
        Socket clientSocket = null;

        while (true) {
            String port = "localhost";
            try {
                int serverPort = 7777;
                clientSocket = new Socket(port, serverPort);

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

                BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
// get random #
                BigInteger rsaBigInteger = redom16digt();
                
                // all from professors code 
                String file = "GunshotSensorCert.cer";

                CertificateFactory cff = CertificateFactory.getInstance("X.509");
                FileInputStream fis1 = new FileInputStream(file); // 
                Certificate cf = cff.generateCertificate(fis1);
                PublicKey pk1 = cf.getPublicKey(); 
                // use Java API https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
                Cipher c1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");   
                  // use this mothod at stackover flow // 
                c1.init(Cipher.ENCRYPT_MODE, pk1);

                byte[] msg1 = c1.doFinal(rsaBigInteger.toByteArray());
                String hexBinary = DatatypeConverter.printHexBinary(msg1);
                out.println(hexBinary);
                out.flush();
// encrept json
                TEA tea = new TEA(rsaBigInteger.toByteArray());
                System.out.println("Enter your ID:");
                String id = typed.readLine();
                System.out.println("Enter your Password:");
                String pwd = typed.readLine();
                System.out.println("Enter sensor ID:");
                String sid = typed.readLine();
                System.out.println("Enter new sensor location:");
                String location = typed.readLine();
                while (!location.contains(",")) {
                    System.out.println("Enter new sensor location:");
                    location = typed.readLine();
                }

                //splite to Latitude and Longitude from stack overflow 
                String[] locationArray = location.trim().split("\\s*,\\s*");
                String latitude = locationArray[0];
                String longitude = locationArray[1];
                String height = locationArray[2];
                //parse to Json 
                JSONObject obj = new JSONObject();
                obj.put("ID", id);
                obj.put("passwd", pwd);
                obj.put("Sensor ID", sid);
                obj.put("Latitude", latitude);
                obj.put("Longitude", longitude);
                obj.put("Height", height);

                String jsonObj = obj.toString();
                
                //Start encript
                tea.encrypt(jsonObj.getBytes());
  
                out.println(Arrays.toString(tea.encrypt(jsonObj.getBytes())));
                out.flush();

                String data = in.readLine();
                
             
                String sendBackData = new String(tea.decrypt(fromString(data)));
                System.out.println(sendBackData);

            } catch (IOException e) {
                System.out.println("IO Exception:" + e.getMessage());
            } 
            finally {
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


    public static byte[] fromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        byte result[] = new byte[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Byte.parseByte(strings[i]);
        }
        return result;

    }

    public static BigInteger redom16digt() {
        Random rnd = new Random();
        BigInteger key = new BigInteger(16 * 8, rnd);
        return key;
    }
}
