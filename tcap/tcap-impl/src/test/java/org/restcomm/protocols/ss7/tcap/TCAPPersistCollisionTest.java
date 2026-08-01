package org.restcomm.protocols.ss7.tcap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.restcomm.protocols.ss7.sccp.impl.SccpHarness;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Simulator names MAP + TCAP both {@code Simulator}; they must not share
 * {@code *_management.xml} or TCAP loads MAP {@code shortTimer} XML and errors.
 */
public class TCAPPersistCollisionTest extends SccpHarness {

    private TCAPStackImpl tcapStack;
    private File persistDir;

    @BeforeMethod
    public void setUp() throws Exception {
        this.sccpStack1Name = "TCAPPersistCollisionSccp1";
        this.sccpStack2Name = "TCAPPersistCollisionSccp2";
        super.setUp();
        persistDir = Files.createTempDirectory("tcap-persist-collision").toFile();
    }

    @AfterMethod
    public void tearDown() {
        if (tcapStack != null) {
            try {
                tcapStack.stop();
            } catch (Exception ignore) {
            }
            tcapStack = null;
        }
        try {
            super.tearDown();
        } catch (Exception ignore) {
        }
        if (persistDir != null) {
            File[] files = persistDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
            persistDir.delete();
        }
    }

    @Test
    public void mapLegacyManagementXmlIsNotLoadedAsTcap() throws Exception {
        File legacy = new File(persistDir, "Simulator_management.xml");
        Files.writeString(legacy.toPath(),
                "<mapStackConfiguration>\n"
                        + "  <shortTimer>10000</shortTimer>\n"
                        + "  <mediumTimer>30000</mediumTimer>\n"
                        + "  <longTimer>600000</longTimer>\n"
                        + "</mapStackConfiguration>\n",
                StandardCharsets.UTF_8);

        assertFalse(TCAPStackImpl.looksLikeTcapPersist(legacy));

        tcapStack = new TCAPStackImpl("Simulator", super.sccpProvider1, 8);
        tcapStack.setPersistDir(persistDir.getAbsolutePath());
        assertNull(tcapStack.resolvePersistFileForLoad());

        tcapStack.start();
        assertTrue(tcapStack.isStarted());
        assertEquals(tcapStack.getDialogIdleTimeout(), TCAPStackImpl._DIALOG_TIMEOUT);

        File tcapFile = new File(persistDir, "Simulator_tcapmanagement.xml");
        // start() does not store unless migrated; store explicitly then verify suffix
        tcapStack.store();
        assertTrue(tcapFile.exists());
        String xml = Files.readString(tcapFile.toPath(), StandardCharsets.UTF_8);
        assertTrue(xml.contains("dialogTimeout") || xml.contains("TCAPConfig"));
        assertFalse(xml.contains("shortTimer"));
    }
}
