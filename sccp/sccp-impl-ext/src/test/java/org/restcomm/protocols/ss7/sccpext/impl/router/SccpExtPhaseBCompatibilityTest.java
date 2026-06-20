package org.restcomm.protocols.ss7.sccpext.impl.router;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.restcomm.protocols.ss7.sccp.LoadSharingAlgorithm;
import org.restcomm.protocols.ss7.sccp.OriginationType;
import org.restcomm.protocols.ss7.sccp.Rule;
import org.restcomm.protocols.ss7.sccp.RuleType;
import org.restcomm.protocols.ss7.sccp.impl.SccpStackImpl;
import org.restcomm.protocols.ss7.sccp.impl.router.RouterImpl;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Phase B: SCCP-Ext router rule persistence ({@code *sccprouter3_ext.xml}).
 * Custom StAX-like string format with nested {@code patternSccpAddress} / GT elements.
 */
public class SccpExtPhaseBCompatibilityTest {

    private static final String GOLDEN_NAME = "RouterStoreExtTest";

    @AfterMethod
    public void cleanup() throws Exception {
        Path cwd = Paths.get(System.getProperty("user.dir"));
        Files.deleteIfExists(cwd.resolve(GOLDEN_NAME + "_sccprouter3_ext.xml"));
        Files.deleteIfExists(cwd.resolve("MapLoadServerSccpStack_sccprouter3_ext.xml"));
    }

    @Test(groups = { "sccp", "phaseB", "config", "sccpext" })
    public void goldenRouterExtFixtureLoadAndRoundTrip() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/RouterStoreExtTest_sccprouter3_ext.xml")) {
            assertNotNull(in, "test resource RouterStoreExtTest_sccprouter3_ext.xml");
            Path target = Paths.get(System.getProperty("user.dir"), GOLDEN_NAME + "_sccprouter3_ext.xml");
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        SccpStackImpl stack = new SccpStackImpl(GOLDEN_NAME, null);
        RouterImpl router = new RouterImpl(GOLDEN_NAME, stack);
        RouterExtImpl routerExt = new RouterExtImpl(GOLDEN_NAME, stack, router);
        routerExt.start();

        SccpAddress addr = routerExt.getRoutingAddress(1);
        assertNotNull(addr);
        assertEquals(addr.getGlobalTitle().getDigits(), "123456789");
        assertEquals(addr.getSignalingPointCode(), 123);

        Rule rule = routerExt.getRule(11);
        assertNotNull(rule);
        assertEquals(rule.getRuleType(), RuleType.SOLITARY);
        assertEquals(rule.getLoadSharingAlgorithm(), LoadSharingAlgorithm.Bit0);
        assertEquals(rule.getOriginationType(), OriginationType.LOCAL);
        assertEquals(rule.getMask(), "K");
        assertEquals(rule.getPrimaryAddressId(), 1);
        assertEquals(rule.getNetworkId(), 3);
        assertEquals(rule.getPattern().getGlobalTitle().getDigits(), "*");

        Path extFile = Paths.get(System.getProperty("user.dir"), GOLDEN_NAME + "_sccprouter3_ext.xml");
        byte[] saved = Files.readAllBytes(extFile);
        routerExt.store();
        routerExt.removeAllResources();
        Files.write(extFile, saved);
        routerExt.load();

        addr = routerExt.getRoutingAddress(1);
        assertNotNull(addr);
        assertEquals(addr.getGlobalTitle().getDigits(), "123456789");

        rule = routerExt.getRule(11);
        assertNotNull(rule);
        assertEquals(rule.getPrimaryAddressId(), 1);
        assertEquals(rule.getMask(), "K");
    }

    @Test(groups = { "sccp", "phaseB", "config", "sccpext" })
    public void goldenMapLoadRouterExtFixture() throws Exception {
        Path golden = Paths.get(System.getProperty("user.dir"))
                .resolve("..").resolve("..").resolve("map").resolve("load")
                .resolve("MapLoadServerSccpStack_sccprouter3_ext.xml").normalize();
        assertTrue(Files.exists(golden), "map/load golden: " + golden);

        String stackName = "MapLoadServerSccpStack";
        Path target = Paths.get(System.getProperty("user.dir")).resolve(stackName + "_sccprouter3_ext.xml");
        Files.copy(golden, target, StandardCopyOption.REPLACE_EXISTING);

        SccpStackImpl stack = new SccpStackImpl(stackName, null);
        RouterImpl router = new RouterImpl(stackName, stack);
        RouterExtImpl routerExt = new RouterExtImpl(stackName, stack, router);
        routerExt.start();

        assertEquals(routerExt.getRules().size(), 2);
        assertEquals(routerExt.getRoutingAddresses().size(), 2);

        Rule rule1 = routerExt.getRule(1);
        assertNotNull(rule1);
        assertEquals(rule1.getPrimaryAddressId(), 1);
        assertEquals(rule1.getOriginationType(), OriginationType.REMOTE);

        SccpAddress addr1 = routerExt.getRoutingAddress(1);
        assertNotNull(addr1);
        assertEquals(addr1.getSignalingPointCode(), 1);
        assertEquals(addr1.getGlobalTitle().getDigits(), "-");
    }
}
