/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeMap;
import javax.xml.bind.DatatypeConverter;
import org.json.JSONObject;

/**
 *
 * @author zhangxian
 */
public class TCPGunShotIncUsingTEAandPasswords {

    public TCPGunShotIncUsingTEAandPasswords() {

    }

    public static void main(String args[]) {
        ServerSocket listenSocket = null;
        BigInteger n = new BigInteger("6322445714982675228243524273195059608402654691709514432285852950168218844873071915902635351011397773325637034999370496345983288092907487815622569128115875904562853680760794585411985508563319091481727498710204722658471279186132333487122827667");
        BigInteger d = new BigInteger("6040652774606027009043693118244378785109794866656742842852296692368176620696282574517591820482012954126037026161108732454723185907830694635642931513663067864759793405700448054473463075635067003539999422353717977304932184594538408427676618113");
        RSAExample rsa = new RSAExample();
        PasswordHash pHash = new PasswordHash();
        TreeMap<String, String[]> kmlInfo = new TreeMap<>();
        KMLCreater kmlCreater = new KMLCreater();
        Socket clientSocket = null;

        int serverPort = 7777; // the server port we are using
        try {
            // Create a new server socket
            listenSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());

            // If quitting (typically by you sending quit signal) clean up sockets
        }
// get user ready 
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

                // Set up "out" to write to the client socket
                PrintWriter out;
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

                String encrypt = in.nextLine();
                byte[] bytes = DatatypeConverter.parseHexBinary(encrypt);
                BigInteger biEncrypt = new BigInteger(bytes);
// decryt by RSA
                BigInteger stringDencrypt = rsa.dencrypt(biEncrypt, n, d);

                TEA tea = new TEA(stringDencrypt.toByteArray());
// senf back msg              
                String success = successSendBack(tea);
                String noAuthorizeMove = noAuthorizedMove(tea);
                String fall = fallSendBack(tea);
               
// get String json from client 
                String jsonArray = in.nextLine();

                byte[] result = fromString(jsonArray);

                tea.decrypt(result);

                String decryptString = new String(tea.decrypt(result));

                counter = ++counter;

// start parsing 
                JSONObject obj = new JSONObject(decryptString);
                String id = obj.get("ID").toString();
                String pwd = obj.get("passwd").toString();
                String sid = obj.get("Sensor ID").toString();
                String Latitude = obj.get("Latitude").toString();
                String Longitude = obj.get("Longitude").toString();
                String height = obj.get("Height").toString();

                String location = Latitude + ", " + Longitude + ", " + height;
// add salt 
                String moeHashKey = pHash.getHashValue(moe.getSalt() + pwd);
                String larryHashKey = pHash.getHashValue(larry.getSalt() + pwd);
                String shempHashKey = pHash.getHashValue(shemp.getSalt() + pwd);

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
// transfer String to byte
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


/// creat placeMark 
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
// creat kml file 
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
