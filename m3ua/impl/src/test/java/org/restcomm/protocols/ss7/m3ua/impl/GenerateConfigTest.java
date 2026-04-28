package org.restcomm.protocols.ss7.m3ua.impl;

import org.restcomm.protocols.ss7.m3ua.impl.parameter.*;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl.M3UAConfig;
import java.lang.reflect.Field;

public class GenerateConfigTest {
    public static void main(String[] args) throws Exception {
        M3UAConfig config = new M3UAConfig();
        config.timeBetweenHeartbeat = 10000;
        config.statisticsEnabled = false;
        config.statisticsTaskDelay = 5000;
        config.statisticsTaskPeriod = 5000;
        config.routingKeyManagementEnabled = false;
        config.useLsbForLinksetSelection = false;
        config.aspFactories = new java.util.concurrent.CopyOnWriteArrayList<>();
        config.appServers = new java.util.concurrent.CopyOnWriteArrayList<>();
        config.routeEntries = new java.util.ArrayList<>();

        AspFactoryImpl aspFactory = new AspFactoryImpl();
        aspFactory.name = "asp1";
        aspFactory.started = true;
        aspFactory.associationName = "ass1";
        config.aspFactories.add(aspFactory);

        AsImpl as = new AsImpl();
        as.name = "as1";
        as.minAspActiveForLb = 1;

        RoutingContextImpl rc = new RoutingContextImpl();
        setField(rc, "rcs", new long[]{101});
        as.routingContext = rc;

        TrafficModeTypeImpl tm = new TrafficModeTypeImpl(2);
        as.trafficModeType = tm;
        as.defaultTrafficModeType = tm;

        AspImpl asp = new AspImpl();
        asp.name = "asp1";
        asp.setAspFactoryName("asp1");
        asp.setAsName("as1");
        // Skip adding to appServerProcs to avoid serialization issues

        config.appServers.add(as);

        M3UAManagementImpl.RouteEntry routeEntry = new M3UAManagementImpl.RouteEntry();
        routeEntry.key = "1:2:3";
        RouteAsImpl routeAs = new RouteAsImpl();
        routeAs.setTrafficModeType(tm);
        setField(routeAs, "asArraytemp", "as1");
        routeEntry.value = routeAs;
        config.routeEntries.add(routeEntry);

        String xml = M3UAJacksonXMLHelper.toXML(config);
        System.out.println(xml);
    }

    static void setField(Object obj, String fieldName, Object value) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(obj, value);
    }
}
