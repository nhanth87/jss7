package org.restcomm.protocols.ss7.map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

/**
 * XStream helper for MAP module XML serialization.
 * Replaces Javolution XMLBinding.
 */
public class MAPXStreamHelper {
    private static final XStream xstream = new XStream(new DomDriver());
    
    static {
        // Configure security - allow all types for now (can be restricted later)
        xstream.addPermission(AnyTypePermission.ANY);
        
        // Process annotations from all MAP implementation classes
        // Note: Individual classes will have @XStreamAlias annotations
    }
    
    public static XStream getXStream() {
        return xstream;
    }
    
    public static String toXML(Object obj) {
        return xstream.toXML(obj);
    }
    
    public static Object fromXML(String xml) {
        return xstream.fromXML(xml);
    }
}
