/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.*;
import java.io.*;
import java.util.Arrays;
import org.json.JSONObject;

/**
 *
 * @author zhangxian
 */
public class TCPInstallerUsingTEAandPasswords {

    public static void main(String args[])  {
        // arguments supply hostname
        Socket clientSocket = null;

        while (true) {
            try {
                String port = "localhost";
                int serverPort = 7777;
                clientSocket = new Socket(port, serverPort);

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

                BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Enter symmetric key as a 16-digit integer.");

                String sKey = typed.readLine();
                // length check
                while (sKey.length() != 16) {
                    System.out.println("Enter symmetric key as a 16-digit integer.");
                    sKey = typed.readLine();
                }
                TEA tea = new TEA(sKey.getBytes());
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

                //splite to Latitude and Longitude;from stack over flaw 
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
               

                // send back data 
                String sendBackData = new String(tea.decrypt(fromString(data)));


                System.out.println(sendBackData);
            

            } catch (IOException  e) {
          
            } catch ( NullPointerException ex) {
                // check validation 
                System.out.println("illegal symmetric key used, clent socket closed." );
                try {
                    clientSocket.close();
                    return;
                } catch (IOException ex1) {
                    
                }
            }finally {
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
    public static void checkExist(String input) {
        if (input.trim() == null) {
            System.out.print("please enter input");
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



}
