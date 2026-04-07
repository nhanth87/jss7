package org.restcomm.protocols.ss7.m3ua.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.*;

/**
 * XStream helper for M3UA module XML serialization.
 * Replaces Javolution XMLBinding.
 */
public class M3UAXStreamHelper {
    private static final XStream xstream = new XStream(new DomDriver());
    
    static {
        xstream.addPermission(AnyTypePermission.ANY);
        
        // Configure aliases for cleaner XML output
        xstream.alias("aspFactory", AspFactoryImpl.class);
        xstream.alias("as", AsImpl.class);
        xstream.alias("asp", AspImpl.class);
        xstream.alias("route", RouteMap.class);
        xstream.alias("routeAs", RouteAsImpl.class);
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
