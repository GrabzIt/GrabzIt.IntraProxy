/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy.resolver.internal.vars;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dominic
 */
public class VariableResolver {
    private static final List<IVar> vars = new ArrayList<IVar>();
    
    static
    {
        vars.add(new IPVar());
        vars.add(new RemoteIPVar());
        vars.add(new PortVar());
    }
    
    public static String Resolve(String text)
    {
        if (text == null)
        {
            return text;
        }
        
        for(IVar var : vars)
        {
            text = var.replace(text);
        }
        
        return text;
    }
}
