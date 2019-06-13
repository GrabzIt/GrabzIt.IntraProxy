/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy;

import it.grabz.intraproxy.resolver.CookieResolver;
import it.grabz.intraproxy.resolver.URLResolver;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Dom
 */
public class ResponseTransformer {
    private static final int BUFFER_SIZE = 32768;
    
    public static void Transform(Request request, URLConnection conn, InputStream is, DataOutputStream out) throws IOException{        
        for (Map.Entry<String, List<String>> k : conn.getHeaderFields().entrySet()) {
            for (String v : k.getValue()){
                if (k.getKey() == null)
                {
                    out.writeBytes(v);         
                    out.writeBytes("\r\n");
                }
                else if (k.getKey().contains("Content-Type"))
                {
                    out.writeBytes(k.getKey());
                    out.writeBytes(": ");
                    out.writeBytes(v);
                    out.writeBytes("\r\n");
                }
                else if (k.getKey().contains("Set-Cookie"))
                {
                    out.writeBytes(k.getKey());
                    out.writeBytes(": ");
                    out.writeBytes(CookieResolver.Resolve(v));
                    out.writeBytes("\r\n");                    
                }
            }
        }             
        
        out.writeBytes("GrabzItIntraProxy: ");
        out.writeBytes("http://"+request.getHost());
        out.writeBytes("\r\n");                    
        
        
        out.writeBytes("\r\n");
        
        URLResolver.AddUrl(request);

        byte by[] = new byte[ BUFFER_SIZE ];
        int index = is.read( by, 0, BUFFER_SIZE );
        while ( index != -1 )
        {
          out.write( by, 0, index );
          index = is.read( by, 0, BUFFER_SIZE );
        }                    

        out.flush();
    }
}
