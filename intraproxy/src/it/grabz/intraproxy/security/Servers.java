/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.grabz.intraproxy.security;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Skinners
 */
@XmlRootElement(name="WebResult")
class Servers {
    @XmlElementWrapper(name="Servers")
    @XmlElement( name="Server" )
    public String[] servers;
}
