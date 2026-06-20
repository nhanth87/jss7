package org.restcomm.protocols.ss7.sccp.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.restcomm.protocols.ss7.Util;
import org.restcomm.protocols.ss7.sccp.ConcernedSignalingPointCode;
import org.restcomm.protocols.ss7.sccp.LongMessageRule;
import org.restcomm.protocols.ss7.sccp.Mtp3Destination;
import org.restcomm.protocols.ss7.sccp.Mtp3ServiceAccessPoint;
import org.restcomm.protocols.ss7.sccp.RemoteSignalingPointCode;
import org.restcomm.protocols.ss7.sccp.RemoteSubSystem;
import org.restcomm.protocols.ss7.sccp.SccpCongestionControlAlgo;
import org.restcomm.protocols.ss7.sccp.SccpProtocolVersion;
import org.restcomm.protocols.ss7.sccp.impl.router.RouterImpl;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Phase B: SCCP config persistence compatibility.
 * <p>
 * SCCP uses three distinct persist formats — all must round-trip without regression:
 * <ul>
 *   <li>{@code *sccprouter4.xml} — StAX attribute format ({@link RouterImpl})</li>
 *   <li>{@code *sccpresource3.xml} — StAX attribute format ({@link SccpResourceImpl})</li>
 *   <li>{@code *sccpmanagement.xml} — Jackson {@link SccpStackImpl.SccpConfig}</li>
 * </ul>
 */
public class SccpPhaseBCompatibilityTest {

    private static final String GOLDEN_STACK = "MapLoadServerSccpStack";

    @AfterMethod
    public void cleanupGoldenCopies() throws Exception {
        Path cwd = Paths.get(System.getProperty("user.dir"));
        Files.deleteIfExists(cwd.resolve(GOLDEN_STACK + "_sccprouter4.xml"));
        Files.deleteIfExists(cwd.resolve(GOLDEN_STACK + "_sccpresource3.xml"));
    }

    @Test(groups = { "sccp", "phaseB", "config" })
    public void sccpStackConfigMapperUsesXml11() throws Exception {
        SccpStackImpl.SccpConfig config = sampleSccpConfig();
        String xml = SCCPJacksonXMLHelper.toXML(config);
        assertTrue(xml.contains("1.1"), "SccpConfig persist should use XML 1.1 declaration");
    }

    @Test(groups = { "sccp", "phaseB", "config" })
    public void sccpConfigJacksonRoundTrip() throws Exception {
        SccpStackImpl.SccpConfig original = sampleSccpConfig();
        String xml = SCCPJacksonXMLHelper.toXML(original);
        assertTrue(xml.contains("SccpConfig"), "expected SccpConfig root element");

        SccpStackImpl.SccpConfig restored = SCCPJacksonXMLHelper.fromXML(
                new StringReader(xml), SccpStackImpl.SccpConfig.class);
        assertSccpConfigEquals(original, restored);
    }

    @Test(groups = { "sccp", "phaseB", "config" })
    public void sccpStackStoreLoadRoundTrip() throws Exception {
        SccpStackImpl stack = new SccpStackImpl("SccpPhaseBStack", null);
        stack.setPersistDir(Util.getTmpTestDir());
        stack.start();
        stack.setZMarginXudtMessage(200);
        stack.setSccpProtocolVersion(SccpProtocolVersion.ANSI);
        stack.setCongControlAlgo(SccpCongestionControlAlgo.levelDepended);
        stack.setSstTimerDuration_IncreaseFactor(1.5);
        stack.store();

        SccpStackImpl stack2 = new SccpStackImpl("SccpPhaseBStack", null);
        stack2.setPersistDir(Util.getTmpTestDir());
        stack2.start();

        assertEquals(stack2.getZMarginXudtMessage(), 200);
        assertEquals(stack2.getSccpProtocolVersion(), SccpProtocolVersion.ANSI);
        assertEquals(stack2.getCongControlAlgo(), SccpCongestionControlAlgo.levelDepended);
        assertEquals(stack2.getSstTimerDuration_IncreaseFactor(), 1.5, 0.001);
    }

    @Test(groups = { "sccp", "phaseB", "config" })
    public void goldenMapLoadRouter4Fixture() throws Exception {
        copyGoldenFixture(GOLDEN_STACK + "_sccprouter4.xml");

        SccpStackImpl stack = new SccpStackImpl(GOLDEN_STACK, null);
        RouterImpl router = new RouterImpl(GOLDEN_STACK, stack);
        router.start();

        Mtp3ServiceAccessPoint sap = router.getMtp3ServiceAccessPoint(1);
        assertNotNull(sap, "SAP id=1 from golden fixture");
        assertEquals(sap.getOpc(), 1);
        assertEquals(sap.getMtp3Id(), 1);
        assertEquals(sap.getNi(), 2);

        Mtp3Destination dest = sap.getMtp3Destination(1);
        assertNotNull(dest, "destination id=1 from golden fixture");
        assertEquals(dest.getFirstDpc(), 2);
        assertEquals(dest.getLastDpc(), 2);
        assertEquals(dest.getSlsMask(), 255);

        router.store();
        Path routerFile = Paths.get(System.getProperty("user.dir"), GOLDEN_STACK + "_sccprouter4.xml");
        byte[] saved = Files.readAllBytes(routerFile);
        router.removeAllResources();
        Files.write(routerFile, saved);
        router.load();

        sap = router.getMtp3ServiceAccessPoint(1);
        assertNotNull(sap);
        assertEquals(sap.getOpc(), 1);
        dest = sap.getMtp3Destination(1);
        assertNotNull(dest);
        assertEquals(dest.getFirstDpc(), 2);
    }

    @Test(groups = { "sccp", "phaseB", "config" })
    public void goldenMapLoadResource3Fixture() throws Exception {
        copyGoldenFixture(GOLDEN_STACK + "_sccpresource3.xml");

        SccpResourceImpl resource = new SccpResourceImpl(GOLDEN_STACK, new Ss7ExtSccpDetailedInterfaceDefault());
        resource.start();

        assertEquals(resource.getRemoteSpcs().size(), 1);
        RemoteSignalingPointCode rspc = resource.getRemoteSpc(1);
        assertNotNull(rspc);
        assertEquals(rspc.getRemoteSpc(), 2);
        assertFalse(rspc.isRemoteSpcProhibited());

        assertEquals(resource.getRemoteSsns().size(), 1);
        RemoteSubSystem rss = resource.getRemoteSsn(1);
        assertNotNull(rss);
        assertEquals(rss.getRemoteSsn(), 147);
        assertEquals(rss.getRemoteSpc(), 2);

        assertEquals(resource.getConcernedSpcs().size(), 0);

        resource.removeAllResources();
        resource.addRemoteSpc(1, 2, 0, 0);
        resource.addRemoteSsn(1, 2, 147, 0, false);
        resource.store();

        SccpResourceImpl resource2 = new SccpResourceImpl(GOLDEN_STACK, new Ss7ExtSccpDetailedInterfaceDefault());
        resource2.start();

        rspc = resource2.getRemoteSpc(1);
        assertNotNull(rspc);
        assertEquals(rspc.getRemoteSpc(), 2);
        rss = resource2.getRemoteSsn(1);
        assertNotNull(rss);
        assertEquals(rss.getRemoteSsn(), 147);

        resource.stop();
        resource2.stop();
    }

    @Test(groups = { "sccp", "phaseB", "config" })
    public void routerLongMessageRuleStoreRoundTrip() throws Exception {
        String name = "SccpPhaseBLongMsgRule";
        SccpStackImpl stack = new SccpStackImpl(name, null);
        RouterImpl router = new RouterImpl(name, stack);
        router.start();
        router.removeAllResources();

        router.addLongMessageRule(5, 201, 202, org.restcomm.protocols.ss7.sccp.LongMessageRuleType.XUDT_ENABLED);
        router.store();
        Path routerFile = Paths.get(System.getProperty("user.dir"), name + "_sccprouter4.xml");
        byte[] saved = Files.readAllBytes(routerFile);
        router.removeAllResources();
        Files.write(routerFile, saved);
        router.load();

        LongMessageRule rule = router.getLongMessageRule(5);
        assertNotNull(rule);
        assertEquals(rule.getFirstSpc(), 201);
        assertEquals(rule.getLastSpc(), 202);
        assertEquals(rule.getLongMessageRuleType(), org.restcomm.protocols.ss7.sccp.LongMessageRuleType.XUDT_ENABLED);

        Files.deleteIfExists(Paths.get(System.getProperty("user.dir"), name + "_sccprouter4.xml"));
    }

    @Test(groups = { "sccp", "phaseB", "config" })
    public void resourceConcernedSpcStoreRoundTrip() throws Exception {
        SccpResourceImpl resource = new SccpResourceImpl("SccpPhaseBConcerned", new Ss7ExtSccpDetailedInterfaceDefault());
        resource.setPersistDir(Util.getTmpTestDir());
        resource.start();
        resource.removeAllResources();

        resource.addRemoteSpc(1, 6034, 0, 0);
        resource.addRemoteSsn(1, 6034, 8, 0, false);
        resource.addConcernedSpc(1, 603);
        resource.store();

        SccpResourceImpl resource2 = new SccpResourceImpl("SccpPhaseBConcerned", new Ss7ExtSccpDetailedInterfaceDefault());
        resource2.setPersistDir(Util.getTmpTestDir());
        resource2.start();

        assertEquals(resource2.getConcernedSpcs().size(), 1);
        ConcernedSignalingPointCode cspc = resource2.getConcernedSpc(1);
        assertNotNull(cspc);
        assertEquals(cspc.getRemoteSpc(), 603);

        resource.stop();
        resource2.stop();
    }

    private static SccpStackImpl.SccpConfig sampleSccpConfig() {
        SccpStackImpl.SccpConfig config = new SccpStackImpl.SccpConfig();
        config.zMarginXudtMessage = 272;
        config.connEstTimerDelay = 120000;
        config.iasTimerDelay = 90000;
        config.iarTimerDelay = 90000;
        config.relTimerDelay = 10000;
        config.repeatRelTimerDelay = 10000;
        config.intTimerDelay = 30000;
        config.guardTimerDelay = 240000;
        config.resetTimerDelay = 10000;
        config.reassemblyTimerDelay = 10000;
        config.maxDataMessage = 2560;
        config.periodOfLogging = 60000;
        config.removeSpc = true;
        config.respectPc = false;
        config.canRelay = false;
        config.timerExecutorsThreadCount = 4;
        config.previewMode = false;
        config.sccpProtocolVersion = SccpProtocolVersion.ITU.name();
        config.congControl_TIMER_A = 500;
        config.congControl_TIMER_D = 2000;
        config.congControl_Algo = SccpCongestionControlAlgo.levelDepended.name();
        config.congControl_blockingOutgoingSccpMessages = true;
        config.sstTimerDuration_Min = 5000;
        config.sstTimerDuration_Max = 120000;
        config.sstTimerDuration_IncreaseFactor = 1.5;
        return config;
    }

    private static void assertSccpConfigEquals(SccpStackImpl.SccpConfig a, SccpStackImpl.SccpConfig b) {
        assertEquals(b.zMarginXudtMessage, a.zMarginXudtMessage);
        assertEquals(b.connEstTimerDelay, a.connEstTimerDelay);
        assertEquals(b.iasTimerDelay, a.iasTimerDelay);
        assertEquals(b.iarTimerDelay, a.iarTimerDelay);
        assertEquals(b.relTimerDelay, a.relTimerDelay);
        assertEquals(b.repeatRelTimerDelay, a.repeatRelTimerDelay);
        assertEquals(b.intTimerDelay, a.intTimerDelay);
        assertEquals(b.guardTimerDelay, a.guardTimerDelay);
        assertEquals(b.resetTimerDelay, a.resetTimerDelay);
        assertEquals(b.reassemblyTimerDelay, a.reassemblyTimerDelay);
        assertEquals(b.maxDataMessage, a.maxDataMessage);
        assertEquals(b.periodOfLogging, a.periodOfLogging);
        assertEquals(b.removeSpc, a.removeSpc);
        assertEquals(b.respectPc, a.respectPc);
        assertEquals(b.canRelay, a.canRelay);
        assertEquals(b.timerExecutorsThreadCount, a.timerExecutorsThreadCount);
        assertEquals(b.previewMode, a.previewMode);
        assertEquals(b.sccpProtocolVersion, a.sccpProtocolVersion);
        assertEquals(b.congControl_TIMER_A, a.congControl_TIMER_A);
        assertEquals(b.congControl_TIMER_D, a.congControl_TIMER_D);
        assertEquals(b.congControl_Algo, a.congControl_Algo);
        assertEquals(b.congControl_blockingOutgoingSccpMessages, a.congControl_blockingOutgoingSccpMessages);
        assertEquals(b.sstTimerDuration_Min, a.sstTimerDuration_Min);
        assertEquals(b.sstTimerDuration_Max, a.sstTimerDuration_Max);
        assertEquals(b.sstTimerDuration_IncreaseFactor, a.sstTimerDuration_IncreaseFactor, 0.001);
    }

    private static void copyGoldenFixture(String fileName) throws Exception {
        Path golden = Paths.get(System.getProperty("user.dir"))
                .resolve("..").resolve("..").resolve("map").resolve("load").resolve(fileName).normalize();
        assertTrue(Files.exists(golden), "golden fixture missing: " + golden);
        Path target = Paths.get(System.getProperty("user.dir")).resolve(fileName);
        Files.copy(golden, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
