/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy.resolver;

/**
 *
 * @author Dominic
 */
public class ResolvedReferer {
    private String url;
    private boolean changed;
    
    public ResolvedReferer(String url, boolean changed){
        this.url = url;
        this.changed = changed;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the changed
     */
    public boolean isChanged() {
        return changed;
    }
}
