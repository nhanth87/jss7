package org.restcomm.protocols.ss7.m3ua.impl;

import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl.M3UAConfig;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DeserializeM3uaConfigV2Test {
    public static void main(String[] args) throws Exception {
        String xml = new String(Files.readAllBytes(Paths.get(
            "C:/Users/Windows/Desktop/ethiopia-working-dir/product/ussd-loadtest/map-level/configs/jackson-format/Mtp3UserPart_m3ua1.xml")));

        M3UAConfig config = M3UAJacksonXMLHelper.fromXML(new StringReader(xml), M3UAConfig.class);
        System.out.println("V2 TEST PASSED!");
        System.out.println("timeBetweenHeartbeat=" + config.timeBetweenHeartbeat);
        System.out.println("aspFactories size=" + config.aspFactories.size());
        System.out.println("appServers size=" + config.appServers.size());
        if (config.appServers.size() > 0) {
            AsImpl as = (AsImpl) config.appServers.get(0);
            System.out.println("appServer[0].name=" + as.name);
            System.out.println("appServer[0].functionality=" + as.getFunctionality());
            System.out.println("appServer[0].exchangeType=" + as.getExchangeType());
            System.out.println("appServer[0].ipspType=" + as.getIpspType());
        }
    }
}
