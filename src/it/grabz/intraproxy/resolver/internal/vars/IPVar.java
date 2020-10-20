/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy.resolver.internal.vars;

import it.grabz.intraproxy.security.IPFilter;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author Dominic
 */
public class IPVar implements IVar {
    @Override
    public String replace(String replace) {
        if (replace == null)
        {
            return replace;
        }
        try {
            String ip = "???.???.???.???";
            if (IPFilter.IsOnline())
            {
                ip = InetAddress.getLocalHost().getHostAddress();
            }
            return replace.replace("{IPAddress}", ip);
        } catch (UnknownHostException ex) {
            return replace;
        }
    }
}
