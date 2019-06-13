/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy.resolver;

/**
 *
 * @author GrabzIt
 */
public class Resolved {
    private final String baseUrl;
    private final String resolvedUrl;
    private final boolean absolute;
    
    public Resolved(String baseUrl, String resolvedUrl, boolean absolute)
    {
        this.baseUrl = baseUrl;
        this.resolvedUrl = resolvedUrl;
        this.absolute = absolute;
    }

    /**
     * @return the baseUrl
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * @return the resolvedUrl
     */
    public String getResolvedUrl() {
        return resolvedUrl;
    }

    /**
     * @return the absolute
     */
    public boolean isAbsolute() {
        return absolute;
    }
}
