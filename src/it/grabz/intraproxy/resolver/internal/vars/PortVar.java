/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy.resolver.internal.vars;

import it.grabz.intraproxy.Config;

/**
 *
 * @author GrabzIt
 */
public class PortVar implements IVar{
    private final Config config = new Config();
    
    @Override
    public String replace(String replace) {
        if (replace == null)
        {
            return replace;
        }        
        return replace.replace("{Port}", Integer.toString(config.getPort()));
    }    
}
