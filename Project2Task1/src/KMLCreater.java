/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zhangxian
 */
public class KMLCreater {
    String header;
    String footer;
    String placemark;
    String location;
    String uid;
    String sid;
    
    public KMLCreater() {
        header =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +"<kml xmlns=\"http://earth.google.com/kml/2.2\">\n"
                        + "\t<Document>\n" + "\t\t<Style id=\"style1\">\n"
                        + "\t\t\t<IconStyle>\n" + "\t\t\t\t<Icon>\n"
                        + "\t\t\t\t\t<href>https://lh3.googleusercontent.com/MSOuW3ZjC7uflJAMst-cykSOEOwI_cVz96s2rtWTN4-Vu1NOBw80pTqrTe06R_AMfxS2=w170</href>\n"
                        + "\t\t\t\t</Icon> \n" + "\t\t\t</IconStyle> \n"
                        + "\t\t</Style> \n";

        footer = "\t</Document>\n" + "</kml>";
    }
    public String placeMark(String sid,String uid,String location){
    return                    placemark ="\t\t<Placemark>\n" + "\t\t\t<name>"+sid+"</name>\n"
                              + "\t\t\t<description>"+uid+"</description>\n"
                              + "\t\t\t<styleUrl>#style1</styleUrl>\n"
                              + "\t\t\t<Point>\n" + "\t\t\t\t<coordinates>"
                              + location +"</coordinates>\n"
                              +"\t\t\t</Point>\n"+ "\t\t</Placemark>\n";
    }
    
}
