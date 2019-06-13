/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Dom
 */
public class IPFilter {
    private static HashMap<String, Boolean> validIPs = new HashMap<String, Boolean>();
    private static boolean loaded = false;
    
    public static boolean Load()
    {
        URL request;
        try {
            request = new URL("http://api.grabz.it/services/getservers.ashx");
            URLConnection connection = (URLConnection) request.openConnection();
            InputStream in = null;
            
            try
            {
                in = connection.getInputStream();
                JAXBContext context = JAXBContext.newInstance(Servers.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();            
                Servers servers = (Servers) unmarshaller.unmarshal(in);
                for(String server : servers.servers)
                {
                    validIPs.put(server, Boolean.TRUE);              
                }
                loaded = true;
            }
            finally
            {
                if (in != null)
                {
                    in.close();
                }
            }
        }
        catch (JAXBException | IOException ex) {            
            //"Error reading GrabzIt server details!
        }
        return true;
    }
    
    public static boolean IsOnline()
    {
        return loaded;
    }
    
    public static synchronized boolean IsValid(InetAddress address)
    {
        try {
            if (!loaded || address.isAnyLocalAddress() || address.isLoopbackAddress())
            {
                return true;
            }
            return validIPs.containsKey(encrypt(address.getHostAddress()));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            return false;
        }
    }
    
    private static String encrypt(String value) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        if (value == null || value.isEmpty())
        {
            return "";
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(value.getBytes("ASCII"));

        byte[] hash = md.digest();
        
        StringBuilder sb = new StringBuilder(hash.length); 
        for(byte b : hash)
        { 
            sb.append(String.format("%02x", b&0xff)); 
        }

        return sb.toString();
    }
}
