/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy.resolver;

import it.grabz.intraproxy.resolver.internal.InternalResourceException;
import it.grabz.intraproxy.LRUCache;
import it.grabz.intraproxy.Request;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

    
/**
 *
 * @author Dom
 */
public class URLResolver {
    private static final LRUCache<String, String> cache = new LRUCache<String, String>(10000);
    private static final LRUCache<String, String> refererToUrlCache = new LRUCache<String, String>(10000);
    
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private static final String RELATIVE = "/../";
    private static final String GRABZIT_PROTOCOL = "grabzit://";
    
    public static synchronized void AddUrl(Request request)
    {
        if (!request.isAbsolute())
        {
            return;
        }
        cache.put(request.getIpAddress(), request.getBaseUrl());
    }

    public static synchronized void AddReferrer(String requestUrl, String referer)
    {
        if (refererToUrlCache.containsKey(requestUrl) || referer == null || "".equals(referer))
        {
            return;
        }
        refererToUrlCache.put(requestUrl, referer);
    }
    
    public static synchronized ResolvedReferer GetOriginalReferrer(String referer)
    {
        if (refererToUrlCache.containsKey(referer))
        {
            String val = refererToUrlCache.get(referer);
            return new ResolvedReferer(val, !(referer.equals(val)));
        }
        return new ResolvedReferer(referer, false);       
    }
    
    public static synchronized Resolved Resolve(String ipAddress, String relativeUrl) throws ResolverException, InternalResourceException
    {
        if (relativeUrl.startsWith(GRABZIT_PROTOCOL))
        {
            throw new InternalResourceException(relativeUrl.replace(GRABZIT_PROTOCOL, ""));
        }
        
        relativeUrl = urlDecode(relativeUrl);
        relativeUrl = sanitizeUrl(relativeUrl);
        
        if (isAbs(relativeUrl))
        {            
            return new Resolved(relativeUrl, relativeUrl, true);
        }
        if (isHostAware(relativeUrl))
        {
            relativeUrl = sanitizeHost(relativeUrl);
            return new Resolved(HTTP + trimSlashes(relativeUrl), HTTP + trimSlashes(relativeUrl), false);
        }        
        
        if (cache.containsKey(ipAddress))
        {
            if (relativeUrl.startsWith(HTTP))
            {
                relativeUrl = relativeUrl.replaceFirst(HTTP, "/");
            }
            return ResolveFromBase(ipAddress, cache.get(ipAddress).toString(), relativeUrl);
        }
        throw new ResolverException();
    }
    
    public static Resolved ResolveFromBase(String ipAddress, String baseUrl, String relativeUrl) throws ResolverException, InternalResourceException
    {
        if (relativeUrl.startsWith(GRABZIT_PROTOCOL))
        {
            throw new InternalResourceException(relativeUrl.replace(GRABZIT_PROTOCOL, ""));
        }
        
        String host = getRefererHost(baseUrl);
        
        relativeUrl = urlDecode(relativeUrl);
        relativeUrl = sanitizeUrl(relativeUrl);
        relativeUrl = removeReferrer(host, relativeUrl);
        
        try
        {                
            if (isAbs(relativeUrl))
            {
                return new Resolved(relativeUrl, relativeUrl, true);
            }            
            if (isHostAware(relativeUrl))
            {
                relativeUrl = sanitizeHost(relativeUrl);
                return new Resolved(HTTP + trimSlashes(relativeUrl), HTTP + trimSlashes(relativeUrl), false);
            }
            
            URI refUri = new URI(host);
            if (!refUri.isAbsolute())
            {
                throw new ResolverException();
            }
            
            if (relativeUrl.startsWith(HTTP))
            {
                relativeUrl = relativeUrl.replaceFirst(HTTP, "/");
            }
            
            if (relativeUrl.startsWith(HTTPS))
            {
                relativeUrl = relativeUrl.replaceFirst(HTTPS, "/");
            }  
            
            if (relativeUrl.startsWith(RELATIVE))
            {
                refUri = new URI(baseUrl);
                relativeUrl = relativeUrl.replaceFirst(RELATIVE, "../");
            }              
            
            return new Resolved(baseUrl, refUri.resolve(relativeUrl).toString(), false);
        }
        catch (URISyntaxException e)
        {
        }
        throw new ResolverException();
    }

    private static String removeReferrer(String host, String relativeUrl) {
        //ensure really is relative
        if (!"".equals(host) && relativeUrl.startsWith(host))
        {
            relativeUrl = relativeUrl.replace(host, "");
        }
        return relativeUrl;
    }

    private static String getRefererHost(String baseUrl) {
        if (baseUrl == null || "".equals(baseUrl))
        {
            return "";
        }
        
        boolean isHttps = false;
        baseUrl = baseUrl.replace(HTTP, "");
        if (baseUrl.startsWith(HTTPS))
        {
            isHttps = true;
            baseUrl = baseUrl.replace(HTTPS, "");
        }
        int index = baseUrl.indexOf("/");
        if (index > -1)
        {
            baseUrl = baseUrl.substring(0, index);
        }
        if (!baseUrl.endsWith("/"))
        {
            baseUrl += "/";
        }
        
        if (isHttps)
        {
            return "https://" + baseUrl;
        }
        
        return "http://" + baseUrl;
    }

    private static boolean isAbs(String relativeUrl) {
        try 
        {
            URI uri = new URI(relativeUrl);
            return uri.isAbsolute() && HostResolver.IsValid(uri);
        }
        catch (URISyntaxException e)
        {
        }
        return false;
    }
    
    private static boolean isHostAware(String relativeUrl) {
        try 
        {
            if (relativeUrl.startsWith("http"))
            {
                return false;
            }
            else
            {
                URI uri = new URI("http://"+trimSlashes(relativeUrl));
                return HostResolver.IsValid(uri);
            }
        }
        catch (Exception e)
        {
        }
        return false;
    }
    
    private static String sanitizeHost(String relativeUrl) {
        try 
        {
            URI uri = new URI(HTTP+trimSlashes(relativeUrl));
            String tmpUrl = relativeUrl;
            tmpUrl = tmpUrl.replaceFirst(uri.getHost(), "");
            if (!isHostAware(tmpUrl))
            {
                return relativeUrl;
            }
            return tmpUrl;
        }
        catch (Exception e)
        {
        }
        return relativeUrl;
    }    

    private static String urlDecode(String relativeUrl) throws ResolverException {
        try {
            relativeUrl = URLDecoder.decode(relativeUrl, "UTF-8");
        } catch (UnsupportedEncodingException ex) {            
        } catch (IllegalArgumentException e){
            throw new ResolverException();
        }
        
        return relativeUrl;
    }

    private static String trimSlash(String relativeUrl) {
        if (relativeUrl.startsWith("/"))
        {
            return relativeUrl.substring(1);
        }
        return relativeUrl;
    }
    
    private static String trimSlashes(String url)
    {
        while(!url.equals(trimSlash(url)))
        {
            url = trimSlash(url);
        }
        return url;
    }

    private static String sanitizeUrl(String relativeUrl) {
        relativeUrl = relativeUrl.replace(" ", "+");
        relativeUrl = relativeUrl.replace("|", "%7C");
        return relativeUrl;
    }
}
