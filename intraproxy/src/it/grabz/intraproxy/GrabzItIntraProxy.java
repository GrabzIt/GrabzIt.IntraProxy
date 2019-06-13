/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy;

import it.grabz.intraproxy.resolver.SSLResolver;
import it.grabz.intraproxy.security.IPFilter;
import java.io.IOException;
import java.net.ServerSocket;

/**
 *
 * @author GrabzIt
 */
public class GrabzItIntraProxy {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        Config config = new Config();
        int port = config.getPort();
        if (!IPFilter.Load())
        {
            System.out.println("Shutting down GrabzIt IntraProxy, due to critical error!");
            return;
        }
        
        SSLResolver.Resolve();

        ServerSocket serverSocket = null;
        
        try 
        {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            System.out.println("GrabzIt IntraProxy started on: " + port);
        } 
        catch (IOException e)
        {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }
        
        if (serverSocket != null)
        {
            while (true)
            {
                new ProxyThread(serverSocket.accept(), port).start();
            }
        }
    }
}
