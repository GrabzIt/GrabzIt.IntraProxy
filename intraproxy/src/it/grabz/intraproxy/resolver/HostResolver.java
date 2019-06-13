/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy.resolver;

import it.grabz.intraproxy.LRUCache;
import java.net.InetAddress;
import java.net.URI;

/**
 *
 * @author Dom
 */
public class HostResolver {
    private static LRUCache<String, Boolean> cache = new LRUCache<String, Boolean>(10000);
    
    public static synchronized boolean IsValid(URI uri)
    {
        if (uri.getHost() == null || "".equals(uri.getHost()))
        {
            return false;
        }
        
        if (cache.containsKey(uri.getHost()))
        {
            return cache.get(uri.getHost());
        }
        
        try
        {
            InetAddress inet = InetAddress.getByName(uri.getHost());
            if (inet.getHostAddress() != null)
            {
                cache.put(uri.getHost(), true);
            }
            else
            {
                cache.put(uri.getHost(), inet.isReachable(5000));          
            }
        }
        catch (Exception e)
        {
            cache.put(uri.getHost(), false);           
        }
        
        return cache.get(uri.getHost());
    }
}
