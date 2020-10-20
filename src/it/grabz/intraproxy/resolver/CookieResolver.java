/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy.resolver;

/**
 *
 * @author Dominic
 */
public class CookieResolver {
    public synchronized static String Resolve(String cookie)
    {
        String header = "";
        if (cookie != null)
        {
            String[] parts = cookie.split(";");            
            for(String part : parts)
            {
                if (part == null)
                {
                    continue;
                }
                String pl = part.toLowerCase().trim();
                if (!pl.startsWith("domain="))
                {
                    if (!"".equals(header))
                    {
                        header += ";";
                    }
                    header += part;
                }
            }
        }        
        return header;
    }
}
