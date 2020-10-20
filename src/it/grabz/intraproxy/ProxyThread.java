/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy;

import it.grabz.intraproxy.resolver.internal.InternalResourceException;
import it.grabz.intraproxy.resolver.ResolverException;
import it.grabz.intraproxy.resolver.internal.InternalResponse;
import it.grabz.intraproxy.security.IPFilter;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author GrabzIt
 */
public class ProxyThread extends Thread {
    private Socket socket = null;
    private int port; 
    
    public ProxyThread(Socket socket, int port) {
        super("ProxyThread");
        this.socket = socket;
        this.port = port;
    }

    @Override
    public void run() {
        DataOutputStream out = null;
        BufferedReader in = null;
        
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            Request request = null;            
            try
            {
                request = RequestHandler.Parse(socket, in, port);            
            }
            catch (InternalResourceException ex) 
            {                
                InternalResponse.Resolve(out, ex.getResource());
                return;
            }
            catch(ResolverException e)
            {
                InternalResponse.Error404(out);
                return;
            }
            
            if (!IPFilter.IsValid(socket.getInetAddress()))
            {
                InternalResponse.Error403(out);
                return;
            }           
            
            System.out.println("Request for: " + request.getUrl());

            try {
                URL url;
                try
                {
                    url = new URL(request.getUrl());
                }
                catch(MalformedURLException e)
                {
                    InternalResponse.Error404(out);
                    return;
                }
                
                URLConnection conn = url.openConnection();
                conn.addRequestProperty("Cookie", request.getCookie());
                conn.setDoInput(true);
                conn.setDoOutput(request.isIsPost());
                
                InputStream is = null;
                try 
                {
                    is = conn.getInputStream();
                    ResponseTransformer.Transform(request, conn, is, out);
                }
                finally
                {
                    if (is != null){
                        try
                        {
                            is.close();
                        }
                        catch(Exception ex){}
                    }
                }    
            }
            catch (NullPointerException e)
            {
                //response contains no data
                out.writeBytes("");
            }
            catch (FileNotFoundException | SocketException e)
            {
                InternalResponse.Error404(out);
            }
            catch (Exception e) {
                //can redirect this to error log
                System.err.println("Encountered error: " + e.getMessage());
                //encountered error - just send nothing back, so
                //processing can continue
                out.writeBytes("");
            }
        }
        catch (IOException e) { 
            System.out.println(e.getMessage());
        }
        finally
        {
            try
            {                                
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }            
                if (socket != null) {
                    socket.close();
                }
            }
            catch(Exception e){}
        }
    }        
}
