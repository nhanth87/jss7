package org.restcomm.protocols.ss7.tcapAnsi;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.*;

/**
 * XStream helper for TCAP ANSI module XML serialization.
 * Replaces Javolution XMLBinding.
 */
public class TCAPAnsiXStreamHelper {
    private static final XStream xstream = new XStream(new DomDriver());
    
    static {
        xstream.addPermission(AnyTypePermission.ANY);
    }
    
    public static XStream getXStream() {
        return xstream;
    }
    
    public static void toXML(Object obj, Writer writer) {
        xstream.toXML(obj, writer);
    }
    
    public static String toXML(Object obj) {
        return xstream.toXML(obj);
    }
    
    public static Object fromXML(Reader reader) {
        return xstream.fromXML(reader);
    }
    
    public static Object fromXML(String xml) {
        return xstream.fromXML(xml);
    }
}
