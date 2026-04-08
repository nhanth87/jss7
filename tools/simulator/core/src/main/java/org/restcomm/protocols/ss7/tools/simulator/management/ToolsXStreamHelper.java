package org.restcomm.protocols.ss7.tools.simulator.management;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.*;

import org.restcomm.protocols.ss7.tools.simulator.common.ConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level1.M3uaConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level1.M3uaConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.level1.DialogicConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level1.DialogicConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.level2.SccpConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level2.SccpConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.level3.MapConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level3.MapConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.level3.CapConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.ussd.TestUssdClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.ussd.TestUssdClientConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.tests.ussd.TestUssdServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.ussd.TestUssdServerConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TestSmsClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TestSmsClientConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TestSmsServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TestSmsServerConfigurationData_OldFormat;
import org.restcomm.protocols.ss7.tools.simulator.tests.cap.TestCapScfConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.cap.TestCapSsfConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.ati.TestAtiClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.ati.TestAtiServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.checkimei.TestCheckImeiClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.checkimei.TestCheckImeiServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.lcs.TestLcsClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.lcs.TestLcsServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.psi.TestPsiServerConfigurationData;

/**
 * XStream helper for TOOLS simulator module XML serialization.
 * Replaces Javolution XMLBinding.
 */
public class ToolsXStreamHelper {
    private static final XStream xstream = new XStream(new DomDriver());
    
    static {
        xstream.addPermission(AnyTypePermission.ANY);
        
        // Configure aliases for cleaner XML output
        xstream.alias("configurationData", ConfigurationData.class);
        xstream.alias("m3ua", M3uaConfigurationData.class);
        xstream.alias("m3ua", M3uaConfigurationData_OldFormat.class);
        xstream.alias("dialogic", DialogicConfigurationData.class);
        xstream.alias("dialogic", DialogicConfigurationData_OldFormat.class);
        xstream.alias("sccp", SccpConfigurationData.class);
        xstream.alias("sccp", SccpConfigurationData_OldFormat.class);
        xstream.alias("map", MapConfigurationData.class);
        xstream.alias("map", MapConfigurationData_OldFormat.class);
        xstream.alias("cap", CapConfigurationData.class);
        xstream.alias("testUssdClient", TestUssdClientConfigurationData.class);
        xstream.alias("testUssdClient", TestUssdClientConfigurationData_OldFormat.class);
        xstream.alias("testUssdServer", TestUssdServerConfigurationData.class);
        xstream.alias("testUssdServer", TestUssdServerConfigurationData_OldFormat.class);
        xstream.alias("testSmsClient", TestSmsClientConfigurationData.class);
        xstream.alias("testSmsClient", TestSmsClientConfigurationData_OldFormat.class);
        xstream.alias("testSmsServer", TestSmsServerConfigurationData.class);
        xstream.alias("testSmsServer", TestSmsServerConfigurationData_OldFormat.class);
        xstream.alias("testCapScf", TestCapScfConfigurationData.class);
        xstream.alias("testCapSsf", TestCapSsfConfigurationData.class);
        xstream.alias("testAtiClient", TestAtiClientConfigurationData.class);
        xstream.alias("testAtiServer", TestAtiServerConfigurationData.class);
        xstream.alias("testCheckImeiClient", TestCheckImeiClientConfigurationData.class);
        xstream.alias("testCheckImeiServer", TestCheckImeiServerConfigurationData.class);
        xstream.alias("testLcsClient", TestLcsClientConfigurationData.class);
        xstream.alias("testLcsServer", TestLcsServerConfigurationData.class);
        xstream.alias("testPsiServer", TestPsiServerConfigurationData.class);
        
        // Instance type aliases
        xstream.alias("instance_L1", Instance_L1.class);
        xstream.alias("instance_L2", Instance_L2.class);
        xstream.alias("instance_L3", Instance_L3.class);
        xstream.alias("instance_TestTask", Instance_TestTask.class);
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
