/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author GrabzIt
 */
public class Config {
    private Properties prop = null;
    
    public Config()
    {
        InputStream input = null;
 
	try { 
		input = new FileInputStream("config.properties");
                prop = new Properties();
		prop.load(input);
	} catch (IOException ex) {
	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
			}
		}
	}
    }
    
    public int getPort()
    {
        if (prop == null)
        {
            return 10000;
        }
        
        return Integer.parseInt(prop.getProperty("port", "10000"));
    }
}
