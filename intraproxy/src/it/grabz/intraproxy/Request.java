package it.grabz.intraproxy;

import it.grabz.intraproxy.resolver.CookieResolver;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dom
 */
public class Request {
    private final String url;
    private final boolean isPost;
    private final String ipAddress;
    private final String baseUrl;
    private final boolean absolute;
    private final String cookie;
    private final String host;
    
    public Request(String url, boolean isPost, String ipAddress, String baseUrl, boolean absolute, String cookie, String host){
        this.url = url;
        this.isPost = isPost;
        this.ipAddress = ipAddress;
        this.baseUrl = baseUrl;
        this.absolute = absolute;
        this.cookie = cookie;
        this.host = host;
    }

    public String getUrl() {
        return url;
    }

    /**
     * @return the isPost
     */
    public boolean isIsPost() {
        return isPost;
    }

    /**
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @return the baseUrl
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * @return the absolute
     */
    public boolean isAbsolute() {
        return absolute;
    }

    /**
     * @return the cookie
     */
    public String getCookie() {
        return CookieResolver.Resolve(cookie);
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }
}
