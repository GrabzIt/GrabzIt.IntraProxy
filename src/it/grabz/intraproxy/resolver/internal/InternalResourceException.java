/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy.resolver.internal;

/**
 *
 * @author Dom
 */
public class InternalResourceException extends Exception {  
    private String resource;
    
    public InternalResourceException(String resource){
        this.resource = resource;
    }

    /**
     * @return the resource
     */
    public String getResource() {
        return resource;
    }
}
