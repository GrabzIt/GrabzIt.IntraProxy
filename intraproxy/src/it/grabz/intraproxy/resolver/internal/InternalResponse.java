/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy.resolver.internal;

import it.grabz.intraproxy.resolver.internal.vars.VariableResolver;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author GrabzIy
 */
public class InternalResponse {  
    private static final List<String> Resources;
    
    static{
        Resources = new ArrayList<String>();
        Resources.add("403.html");
        Resources.add("404.html");
        Resources.add("501.html");
        Resources.add("dashboard.html");
        Resources.add("logo.png");
    }
    
    private static boolean IsValid(String resource)
    {
        return Resources.contains(resource);
    }
    
    public static void Error403(DataOutputStream out) throws IOException
    {
        out.writeBytes("HTTP/1.1 403 Forbidden\r\n");
        outputHTMLPage(out, "403.html");        
    }
    
    public static void Error404(DataOutputStream out) throws IOException
    {
        out.writeBytes("HTTP/1.1 404 Not Found\r\n");
        outputHTMLPage(out, "404.html");
    }    
    
    public static void Error501(DataOutputStream out) throws IOException
    {
        out.writeBytes("HTTP/1.1 501 Not Implemented\r\n");
        outputHTMLPage(out, "501.html");
    }   

    public static void Resolve(DataOutputStream out, String resource) throws IOException {
        if (!IsValid(resource))
        {
            Error404(out);
            return;
        }
        out.writeBytes("HTTP/1.1 200 OK\r\n");
        if (resource.endsWith(".png"))
        {
            outputPNG(out, resource);
            return;
        }        
        outputHTMLPage(out, resource);
    }    

    private static void outputPNG(DataOutputStream out, String fileName) throws IOException {
        out.writeBytes("Content-Type: image/png\r\n\r\n");
        ClassLoader classLoader = InternalResponse.class.getClassLoader();
        out.write(stream2Bytes(classLoader.getResourceAsStream(fileName)));
    }    
    
    private static byte[] stream2Bytes(InputStream ins)
    {
      byte [] availableBytes = new byte [0];    
      try
      {
          byte [] buffer = new byte[4096];
          ByteArrayOutputStream outs = new ByteArrayOutputStream();
          
          int read = 0;
          while ((read = ins.read(buffer)) != -1 ) {
            outs.write(buffer, 0, read);
          }
          
          ins.close();
          outs.close();
          availableBytes = outs.toByteArray();
          
      } catch (Exception e) { 
      }
      
      return availableBytes;
    }
    
    private static void outputHTMLPage(DataOutputStream out, String pageName) throws IOException {
        out.writeBytes("Content-Type: text/html\r\n\r\n");
        String text = getFile(pageName);
        out.writeBytes(VariableResolver.Resolve(text));
    }
    
    private static String getFile(String fileName) 
    {
        ClassLoader classLoader = InternalResponse.class.getClassLoader();
        BufferedReader br=new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(fileName)));
        StringBuilder builder = new StringBuilder();
        try
        {
            String line=null;
            while((line=br.readLine())!=null){
                builder.append(line).append("\n");
            }
        }
        catch(IOException e)
        {
        }

        return builder.toString(); 
     }    
}
