
package org.restcomm.protocols.ss7.tools.simulator.level1;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

import org.mobicents.protocols.api.IpChannelType;
import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.IPSPType;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.restcomm.protocols.ss7.mtp.RoutingLabelFormat;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "m3uaConfigurationData")
public class M3uaConfigurationData {

    protected static final String STORE_PCAP_TRACE = "storePcapTrace";
    protected static final String IS_SCTP_SERVER = "isSctpServer";
    protected static final String LOCAL_HOST = "localHost";
    protected static final String LOCAL_HOST_2 = "localHost2";
    protected static final String LOCAL_PORT = "localPort";
    protected static final String LOCAL_PORT_2 = "localPort2";
    protected static final String REMOTE_HOST = "remoteHost";
    protected static final String REMOTE_HOST_2 = "remoteHost2";
    protected static final String REMOTE_PORT = "remotePort";
    protected static final String REMOTE_PORT_2 = "remotePort2";
    protected static final String IP_CHANNEL_TYPE = "ipChannelType";
    protected static final String EXTRA_HOST_ADDRESSES = "extraHostAddresses";

    protected static final String M3UA_FUNCTIONALITY = "m3uaFunctionality";
    protected static final String M3UA_EXCHANGE_TYPE = "m3uaExchangeType";
    protected static final String M3UA_IPSPType = "m3uaIPSPType";
    protected static final String ROUTING_LABEL_FORMAT = "routingLabelFormat";
    protected static final String DPC = "dpc";
    protected static final String DPC_2 = "dpc2";
    protected static final String OPC = "opc";
    protected static final String OPC_2 = "opc2";
    protected static final String SI = "si";
    protected static final String ROUTING_CONTEXT = "routingConext";
    protected static final String NETWORK_APPEARANCE = "networkAppearance";
    protected static final String TRAFFIC_MODE_TYPE = "trafficModeType";

    private boolean storePcapTrace = false;
    private boolean isSctpServer = false;
    // Defaults match classic ussdgateway ss7-simulator/main_simulator2.xml
    private String localHost = "127.0.0.1";
    private int localPort = 8011;
    private String remoteHost = "127.0.0.1";
    private int remotePort = 8012;
    private String localHost2 = "";
    private int localPort2 = 0;
    private String remoteHost2 = "";
    private int remotePort2 = 0;
    private IpChannelType ipChannelType = IpChannelType.SCTP;
    private String[] extraHostAddresses = new String[0];
    private int dpc = 2;
    private int opc = 1;
    private int dpc2 = 0;
    private int opc2 = 0;
    private int si = 3;
    private long routingContext = 101;
    private long networkAppearance = 102;
    private int trafficModeType = TrafficModeType.Loadshare;

    private Functionality m3uaFunctionality = Functionality.IPSP;
    private ExchangeType m3uaExchangeType = ExchangeType.SE;
    private IPSPType m3uaIPSPType = IPSPType.CLIENT;
    private RoutingLabelFormat routingLabelFormat = RoutingLabelFormat.ITU;

    public boolean getStorePcapTrace() {
        return storePcapTrace;
    }

    public void setStorePcapTrace(boolean val) {
        storePcapTrace = val;
    }

    public boolean getIsSctpServer() {
        return isSctpServer;
    }

    public void setIsSctpServer(boolean val) {
        isSctpServer = val;
    }

    public String getLocalHost() {
        return localHost;
    }

    public String getLocalHost2() {
        return localHost2;
    }

    public void setLocalHost(String val) {
        localHost = val;
    }

    public void setLocalHost2(String val) {
        localHost2 = val;
    }

    public int getLocalPort() {
        return localPort;
    }

    public int getLocalPort2() {
        return localPort2;
    }

    public void setLocalPort(int val) {
        localPort = val;
    }

    public void setLocalPort2(int val) {
        localPort2 = val;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public String getRemoteHost2() {
        return remoteHost2;
    }

    public void setRemoteHost(String val) {
        remoteHost = val;
    }

    public void setRemoteHost2(String val) {
        remoteHost2 = val;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public int getRemotePort2() {
        return remotePort2;
    }

    public void setRemotePort(int val) {
        remotePort = val;
    }

    public void setRemotePort2(int val) {
        remotePort2 = val;
    }

    public IpChannelType getIpChannelType() {
        return ipChannelType;
    }

    public void setIpChannelType(IpChannelType val) {
        ipChannelType = val;
    }

    public String[] getSctpExtraHostAddressesArray() {
        return extraHostAddresses;
    }

    public void setSctpExtraHostAddressesArray(String[] val) {
        extraHostAddresses = val;
    }

    public String getSctpExtraHostAddresses() {
        if (extraHostAddresses == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : extraHostAddresses) {
                if (sb.length() != 0) {
                    sb.append(" ");
                }
                sb.append(s);
            }
            return sb.toString();
        }
    }

    public void setSctpExtraHostAddresses(String val) {
        if (val == null)
            return;

        String[] ss = val.split(" ");
        List<String> fl = new ArrayList<String>();
        for (String s : ss) {
            if (s.length() != 0) {
                fl.add(s);
            }
        }
        extraHostAddresses = fl.toArray(new String[0]);
    }

    public int getDpc() {
        return dpc;
    }

    public int getDpc2() {
        return dpc2;
    }

    public void setDpc(int val) {
        dpc = val;
    }

    public void setDpc2(int val) {
        dpc2 = val;
    }

    public int getOpc() {
        return opc;
    }

    public int getOpc2() {
        return opc2;
    }

    public void setOpc(int val) {
        opc = val;
    }

    public void setOpc2(int val) {
        opc2 = val;
    }

    public int getSi() {
        return si;
    }

    public void setSi(int val) {
        si = val;
    }

    public long getRoutingContext() {
        return routingContext;
    }

    public void setRoutingContext(long val) {
        routingContext = val;
    }

    public long getNetworkAppearance() {
        return networkAppearance;
    }

    public void setNetworkAppearance(long val) {
        networkAppearance = val;
    }

    public int getTrafficModeType() {
        return trafficModeType;
    }

    public void setTrafficModeType(int val) {
        trafficModeType = val;
    }

    public Functionality getM3uaFunctionality() {
        return m3uaFunctionality;
    }

    public void setM3uaFunctionality(Functionality val) {
        m3uaFunctionality = val;
    }

    public ExchangeType getM3uaExchangeType() {
        return m3uaExchangeType;
    }

    public void setM3uaExchangeType(ExchangeType val) {
        m3uaExchangeType = val;
    }

    public IPSPType getM3uaIPSPType() {
        return m3uaIPSPType;
    }

    public void setM3uaIPSPType(IPSPType val) {
        m3uaIPSPType = val;
    }

    public RoutingLabelFormat getRoutingLabelFormat() {
        return routingLabelFormat;
    }

    public void setRoutingLabelFormat(RoutingLabelFormat val) {
        routingLabelFormat = val;
    }

}
