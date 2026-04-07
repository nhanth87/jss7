package org.restcomm.protocols.ss7.sccp.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.*;

import org.restcomm.protocols.ss7.sccp.impl.router.LongMessageRuleMap;
import org.restcomm.protocols.ss7.sccp.impl.router.Mtp3DestinationMap;
import org.restcomm.protocols.ss7.sccp.impl.router.Mtp3ServiceAccessPointMap;
import org.restcomm.protocols.ss7.sccp.impl.router.RouterImpl;

/**
 * XStream helper for SCCP module XML serialization.
 * Replaces Javolution XMLBinding.
 */
public class SCCPXStreamHelper {
    private static final XStream xstream = new XStream(new DomDriver());
    
    static {
        xstream.addPermission(AnyTypePermission.ANY);
        
        // Register aliases for SCCP Resource classes
        xstream.alias("remoteSubSystemMap", RemoteSubSystemMap.class);
        xstream.alias("remoteSignalingPointCodeMap", RemoteSignalingPointCodeMap.class);
        xstream.alias("concernedSignalingPointCodeMap", ConcernedSignalingPointCodeMap.class);
        xstream.alias("remoteSubSystem", RemoteSubSystemImpl.class);
        xstream.alias("remoteSignalingPointCode", RemoteSignalingPointCodeImpl.class);
        xstream.alias("concernedSignalingPointCode", ConcernedSignalingPointCodeImpl.class);
        
        // Register aliases for SCCP Router classes
        xstream.alias("longMessageRuleMap", LongMessageRuleMap.class);
        xstream.alias("mtp3ServiceAccessPointMap", Mtp3ServiceAccessPointMap.class);
        xstream.alias("mtp3DestinationMap", Mtp3DestinationMap.class);
        xstream.alias("routerConfig", RouterImpl.RouterConfig.class);
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
