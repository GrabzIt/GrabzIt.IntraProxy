/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 * @author GrabzIt
 */
public class IPChecker {
    private static String ip = "";
    
    public static String getIp() {
        if (!"".equals(ip))
        {
            return ip;
        }
        try
            {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(
                        whatismyip.openStream()));
                ip = in.readLine();
                return ip;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {                
                    }
                }
            }
        }
        catch(Exception e)
        {
        }
        return "";
    }    
}
