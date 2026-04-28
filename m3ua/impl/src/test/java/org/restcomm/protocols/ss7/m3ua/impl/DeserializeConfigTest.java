package org.restcomm.protocols.ss7.m3ua.impl;

import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl.M3UAConfig;
import java.io.StringReader;

public class DeserializeConfigTest {
    public static void main(String[] args) throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<m3uaConfig>\n" +
            "  <timeBetweenHeartbeat>10000</timeBetweenHeartbeat>\n" +
            "  <statisticsEnabled>false</statisticsEnabled>\n" +
            "  <statisticsTaskDelay>5000</statisticsTaskDelay>\n" +
            "  <statisticsTaskPeriod>5000</statisticsTaskPeriod>\n" +
            "  <routingKeyManagementEnabled>false</routingKeyManagementEnabled>\n" +
            "  <useLsbForLinksetSelection>false</useLsbForLinksetSelection>\n" +
            "  <aspFactoryList>\n" +
            "    <aspFactory name=\"asp1\" started=\"true\" associationName=\"ass1\">\n" +
            "      <heartBeatEnabled>false</heartBeatEnabled>\n" +
            "    </aspFactory>\n" +
            "  </aspFactoryList>\n" +
            "  <asList>\n" +
            "    <as minAspActiveForLb=\"1\">\n" +
            "      <name>as1</name>\n" +
            "      <routingContext>\n" +
            "        <rcs>\n" +
            "          <rcs>101</rcs>\n" +
            "        </rcs>\n" +
            "      </routingContext>\n" +
            "      <trafficModeType>\n" +
            "        <mode>2</mode>\n" +
            "      </trafficModeType>\n" +
            "      <defaultTrafficModeType>\n" +
            "        <mode>2</mode>\n" +
            "      </defaultTrafficModeType>\n" +
            "    </as>\n" +
            "  </asList>\n" +
            "  <route>\n" +
            "    <routeEntry>\n" +
            "      <key>1:2:3</key>\n" +
            "      <value>\n" +
            "        <trafficModeType>\n" +
            "          <mode>2</mode>\n" +
            "        </trafficModeType>\n" +
            "        <asArraytemp>as1</asArraytemp>\n" +
            "      </value>\n" +
            "    </routeEntry>\n" +
            "  </route>\n" +
            "</m3uaConfig>";

        M3UAConfig config = M3UAJacksonXMLHelper.fromXML(new StringReader(xml), M3UAConfig.class);
        System.out.println("Parse OK! timeBetweenHeartbeat=" + config.timeBetweenHeartbeat);
        System.out.println("aspFactories size=" + config.aspFactories.size());
        System.out.println("appServers size=" + config.appServers.size());
        System.out.println("routeEntries size=" + config.routeEntries.size());
    }
}
