/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy.resolver.internal.vars;

import it.grabz.intraproxy.IPChecker;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dominic
 */
public class RemoteIPVar implements IVar {
    @Override
    public String replace(String replace) {
        if (replace == null)
        {
            return replace;
        }
        return replace.replace("{RemoteIPAddress}", IPChecker.getIp());
    }
}
