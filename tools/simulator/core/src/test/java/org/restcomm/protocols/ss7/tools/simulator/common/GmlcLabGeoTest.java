package org.restcomm.protocols.ss7.tools.simulator.common;

import java.util.Random;

import junit.framework.TestCase;

public class GmlcLabGeoTest extends TestCase {

    private String previousMaster;
    private String previousLegacy;

    @Override
    protected void setUp() {
        previousMaster = System.getProperty("gmlc.sim.addis");
        previousLegacy = System.getProperty("gmlc.sim.psi.addis");
    }

    @Override
    protected void tearDown() {
        restoreProperty("gmlc.sim.addis", previousMaster);
        restoreProperty("gmlc.sim.psi.addis", previousLegacy);
    }

    public void testAddisEnabledPsiSampleCannotUseStockUruguayLocation() {
        System.setProperty("gmlc.sim.addis", "true");
        System.clearProperty("gmlc.sim.psi.addis");

        assertTrue(GmlcLabGeo.enabled());
        for (int seed = 0; seed < 100; seed++) {
            GmlcLabGeo.Sample sample = GmlcLabGeo.randomSample(new Random(seed));

            assertEquals(636, sample.mcc());
            assertFalse(sample.mcc() == 748);
            assertTrue(sample.latitude() >= GmlcLabGeo.MIN_LATITUDE);
            assertTrue(sample.latitude() < GmlcLabGeo.MAX_LATITUDE);
            assertTrue(sample.longitude() >= GmlcLabGeo.MIN_LONGITUDE);
            assertTrue(sample.longitude() < GmlcLabGeo.MAX_LONGITUDE);
            assertTrue(sample.uncertaintyMeters() >= 50.0);
        }
    }

    public void testMasterFlagOverridesLegacyFlag() {
        System.setProperty("gmlc.sim.addis", "false");
        System.setProperty("gmlc.sim.psi.addis", "true");

        assertFalse(GmlcLabGeo.enabled());
    }

    private static void restoreProperty(String name, String value) {
        if (value == null) {
            System.clearProperty(name);
        } else {
            System.setProperty(name, value);
        }
    }
}
