/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.*;
import java.io.*;

import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeMap;
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
public class TCPGunShotIncUsingTEAandPasswords {

    public static void main(String args[]) throws FileNotFoundException, IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        PasswordHash pHash = new PasswordHash();
        TreeMap<String, String[]> kmlInfo = new TreeMap<>();
        KMLCreater kmlCreater = new KMLCreater();
        Socket clientSocket = null;

        int serverPort = 7777; // the server port we are using

        // Create a new server socket
        ServerSocket listenSocket = new ServerSocket(serverPort);

        /*
             * Block waiting for a new connection request from a client.
             * When the request is received, "accept" it, and the rest
             * the tcp protocol handshake will then take place, making 
             * the socket ready for reading and writing.
            
         */
        Installer moe = new Installer("Moe", "moeh", "Moe",
                null, "Chief Sensor Installer", "D095A9CF10ABD3CDCC505831EC9E68FD2A98084410988BFEDE21CE1CC654172D", "CDB1CF26AD261FB2B601B814B8385032894C8E9E30FCC437592EB48813A356BD");
        Installer larry = new Installer("Larry", "larryh", "Larry",
                null, "Associate Sensor Installer", "1FDA1738DC3BB03A189659FD959B0A7466D80245933AAEEA59715F5F55B162BC", "1DE5495D95A18BB628EBE8147E7F61046737BCC926FE89460E630A959A21B214");
        Installer shemp = new Installer("Shemp", "shemph", "Shemp",
                null, "Associate Sensor Installer", "811D93F2F35768EAAC17B3A3095BC7E016477E744077B32C6DAEF145F73FC5F3", "CBFBF617A0D14B841E920A996CA297F1C9BF60E69A1BB2290B71CD1510DFDD3B");

        int counter = 0;
        System.out.println("Waiting for installers to visit...");
        while (true) {
            try {
                clientSocket = listenSocket.accept();
                // If we get here, then we are now connected to a client.

                // Set up "in" to read from the client socket
                Scanner in;
                in = new Scanner(clientSocket.getInputStream());
                String file = "GunshotSensorKeys.jks";
                // Set up "out" to write to the client socket
                PrintWriter out;
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                // get data from clent 
                String encrypt = in.nextLine();
                byte[] bytes = DatatypeConverter.parseHexBinary(encrypt);
                // use method for professor 
                char[] pwd1 = "123456".toCharArray();
                FileInputStream fis = new FileInputStream(file);
                KeyStore keyStore = KeyStore.getInstance("JKS");
                keyStore.load(fis, pwd1);
                fis.close();
                PrivateKey key = (PrivateKey) keyStore.getKey("myCool", pwd1);
                 // use Java API https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
                Cipher c2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                // use this mothod at stackover flow 
                c2.init(Cipher.DECRYPT_MODE, key);
                byte[] msg2 = c2.doFinal(bytes);
                // inti Tea
                TEA tea = new TEA(msg2);
                
                // all send back messsage
                String success = successSendBack(tea);
                String noAuthorizeMove = noAuthorizedMove(tea);
                String fall = fallSendBack(tea);
               

                String jsonArray = in.nextLine();

                byte[] result = fromString(jsonArray);
                // decrpt tea 
                tea.decrypt(result);

                String decryptString = new String(tea.decrypt(result));
                // counter
                counter = ++counter;

 
// get Json file 
                JSONObject obj = new JSONObject(decryptString);
                String id = obj.get("ID").toString();
                String pwd = obj.get("passwd").toString();
                String sid = obj.get("Sensor ID").toString();
                String Latitude = obj.get("Latitude").toString();
                String Longitude = obj.get("Longitude").toString();
                String height = obj.get("Height").toString();

                String location = Latitude + ", " + Longitude + ", " + height;

                String moeHashKey = pHash.getHashValue(moe.getSalt() + pwd);
                String larryHashKey = pHash.getHashValue(larry.getSalt() + pwd);
                String shempHashKey = pHash.getHashValue(shemp.getSalt() + pwd);
// varify pwd and user name 
                if (moe.getId().equals(id)) {
                    if (moe.getHashPwd().equals(moeHashKey)) {

                        if (!kmlInfo.containsKey(sid)) {

                            System.out.println("Got visit " + counter + " from " + moe.getId()
                                    + " " + moe.getTitle());
                            // treemap add 
                            String[] uid_Location = {id, location};
                            kmlInfo.put(sid, uid_Location);
                            ArrayList<String> placeMarks = getPlaceMark(kmlInfo, kmlCreater);
                            out.println(success);
                            // file location
                            String deskTopLocation = System.getProperty("user.home") + "/Desktop/Sensors.kml";

                            String fileContent = getKmlFile(kmlCreater, placeMarks);
                            try (FileWriter fw = new FileWriter(deskTopLocation)) {
                                fw.write(fileContent);
                                fw.flush();
                            }
                        } else {
                            System.out.println("Got visit " + counter + " from " + moe.getId()
                                    + " " + moe.getTitle() + ", a sensor has been moved");

                            String[] uid_Location = {id, location};
                            kmlInfo.put(sid, uid_Location);
                            ArrayList<String> placeMarks = getPlaceMark(kmlInfo, kmlCreater);

                            out.println(success);

                            // file location
                            String deskTopLocation = System.getProperty("user.home") + "/Desktop/Sensors.kml";

                            String fileContent = getKmlFile(kmlCreater, placeMarks);
                            try (FileWriter fw = new FileWriter(deskTopLocation)) {
                                fw.write(fileContent);
                                fw.flush();
                            }
                        }

                    } else {
                        out.println(fall);
                        System.out.println("illegal Password attempt. This may be an attack.");

                    }

                } else if (larry.getId().equals(id)) {
                    if (larry.getHashPwd().equals(larryHashKey)) {

                        if (!kmlInfo.containsKey(sid)) {
                            System.out.println("Got visit " + counter + " from " + larry.getId()
                                    + " " + larry.getTitle());
                            out.println(success);

                            String[] uid_Location1 = {id, location};
                            kmlInfo.put(sid, uid_Location1);
                            ArrayList<String> placeMarks = getPlaceMark(kmlInfo, kmlCreater);

                            // file location
                            String deskTopLocation = System.getProperty("user.home") + "/Desktop/Sensors.kml";

                            String fileContent = getKmlFile(kmlCreater, placeMarks);
                            try (FileWriter fw = new FileWriter(deskTopLocation)) {
                                fw.write(fileContent);
                                fw.flush();
                            }

                        } else {
                            System.out.println(larry.getId() + " has not authorized to move sensor");
                            out.println(noAuthorizeMove);

                        }

                    } else {
                        out.println(fall);
                        System.out.println("illegal Password attempt. This may be an attack.");

                    }

                } else if (shemp.getId().equals(id)) {
                    if (shemp.getHashPwd().equals(shempHashKey)) {
                        if (!kmlInfo.containsKey(sid)) {
                            System.out.println("Got visit " + counter + " from " + shemp.getId()
                                    + " " + larry.getTitle());
                            out.println(success);

                            String[] uid_Location1 = {id, location};
                            kmlInfo.put(sid, uid_Location1);
                            ArrayList<String> placeMarks = getPlaceMark(kmlInfo, kmlCreater);

                            // file location
                            String deskTopLocation = System.getProperty("user.home") + "/Desktop/Sensors.kml";

                            String fileContent = getKmlFile(kmlCreater, placeMarks);
                            try (FileWriter fw = new FileWriter(deskTopLocation)) {
                                fw.write(fileContent);
                                fw.flush();
                            }

                        } else {
                            System.out.println(shemp.getId() + " has not authorized to move sensor");
                            out.println(noAuthorizeMove);

                        }
                    } else {
                        System.out.println("illegal Password attempt. This may be an attack.");
                        out.println(fall);
                    }
                } else {
                    out.println(fall);
                    System.out.println("illegal Password attempt. This may be an attack.");

                }

                out.flush();
            } // Handle exceptions
            catch (IOException e) {
                System.out.println("IO Exception:" + e.getMessage());

                // If quitting (typically by you sending quit signal) clean up sockets
            } catch (Exception ex) {
                // ignore exception on close
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

    public static byte[] fromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        byte result[] = new byte[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Byte.parseByte(strings[i]);
        }
        return result;

    }

    private static String fallSendBack(TEA s) {
        String msg = "illegal ID or password";

        String toString = Arrays.toString(s.encrypt(msg.getBytes()));
        return toString;
    }

    private static String successSendBack(TEA s) {
        String msg = "Thank you. The sensor’s location was securely transmitted to GunshotSensing Inc.";

        String toString = Arrays.toString(s.encrypt(msg.getBytes()));
        return toString;
    }

    private static String noAuthorizedMove(TEA s) {
        String msg = "you not authorized to move sensor.";

        String toString = Arrays.toString(s.encrypt(msg.getBytes()));
        return toString;
    }


// to rewrite kml at place mark 
    private static ArrayList getPlaceMark(TreeMap<String, String[]> tm, KMLCreater kmlCreater) {
        ArrayList<String> placeMarks = new ArrayList<>();
        Iterator<String> iter = tm.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            String[] name_location = tm.get(key);
            String placeMark = kmlCreater.placeMark(key, name_location[0], name_location[1]);
            placeMarks.add(placeMark);
        }
        return placeMarks;
    }
// to rewrite kml att header and footer for kml
    private static String getKmlFile(KMLCreater kmlCreater, ArrayList<String> placeMarks) {
        StringBuilder placeMarksBuilder = new StringBuilder();
        placeMarksBuilder.append(kmlCreater.header);
        for (String placeMark : placeMarks) {
            placeMarksBuilder.append(placeMark);
        }
        placeMarksBuilder.append(kmlCreater.footer);
        return placeMarksBuilder.toString();
    }
}
