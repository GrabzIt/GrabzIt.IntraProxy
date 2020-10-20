package it.grabz.intraproxy;


import it.grabz.intraproxy.resolver.internal.InternalResourceException;
import it.grabz.intraproxy.resolver.Resolved;
import it.grabz.intraproxy.resolver.ResolvedReferer;
import it.grabz.intraproxy.resolver.ResolverException;
import it.grabz.intraproxy.resolver.URLResolver;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dom
 */
public class RequestHandler {
    private static final String COOKIE_HEADER = "Cookie: ";
    private static final String HOST_HEADER = "Host: ";
    
    public static Request Parse(Socket socket, BufferedReader in, int port) throws IOException, ResolverException, InternalResourceException
    {
        String inputLine;
        int cnt = 0;
        String urlToCall = "";
        String referer = "";
        String cookie = "";
        String host = "";
        boolean isPost = false;
        //begin get request from client

        while ((inputLine = in.readLine()) != null) {
            try {
                StringTokenizer tok = new StringTokenizer(inputLine);
                tok.nextToken();
            } catch (Exception e) {
                break;
            }
            //parse the first line of the request to find the url
            if (cnt == 0) {
                String[] tokens = inputLine.split(" ");
                urlToCall = tokens[1];
                isPost = "POST".equals(tokens[0]);
                //can redirect this to output log
                if (urlToCall != null && urlToCall.length() > 0 && urlToCall.startsWith("/"))
                {
                    urlToCall = urlToCall.substring(1);
                }                
            }

            if (inputLine.startsWith("Referer: ")){
                String portPart = ":"+port+"/";
                referer = inputLine.substring(inputLine.indexOf(portPart) + portPart.length());
            }
            
            if (inputLine.startsWith(COOKIE_HEADER)){
                cookie = inputLine.substring(COOKIE_HEADER.length());
            }
            
            if (inputLine.startsWith(HOST_HEADER)){
                host = inputLine.substring(HOST_HEADER.length());
            }            
            
            cnt++;
        }

        ResolvedReferer resolvedReferer = URLResolver.GetOriginalReferrer(referer);        
        Resolved resolved = ResolveURL(resolvedReferer.getUrl(), urlToCall, socket);
        if (!resolvedReferer.isChanged())
        {
            URLResolver.AddReferrer(urlToCall, resolved.getResolvedUrl());
        }
        
        return new Request(resolved.getResolvedUrl(), isPost, socket.getInetAddress().getHostAddress(), resolved.getBaseUrl(), resolved.isAbsolute(), cookie, host);
    }
    
    private static Resolved ResolveURL(String referer, String urlToCall, Socket socket) throws ResolverException, InternalResourceException
    {
        if (!"".equals(referer))
        {            
            try
            {
                return URLResolver.ResolveFromBase(socket.getInetAddress().getHostAddress(), referer, urlToCall);
            }   
            catch (ResolverException ex) 
            {       
                //Attempt to lookup with saved info
                return URLResolver.Resolve(socket.getInetAddress().getHostAddress(), urlToCall);
            }        
        }
        else
        {
            return URLResolver.Resolve(socket.getInetAddress().getHostAddress(), urlToCall);
        }
    }
}
