package org.restcomm.protocols.ss7.map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.testng.annotations.Test;

public class MAPPersistFileCollisionTest {

    @Test
    public void migratesLegacySharedManagementXml() throws Exception {
        File dir = Files.createTempDirectory("map-persist-collision").toFile();
        try {
            File legacy = new File(dir, "Simulator_management.xml");
            Files.writeString(legacy.toPath(),
                    "<mapStackConfiguration>\n"
                            + "  <shortTimer>11111</shortTimer>\n"
                            + "  <mediumTimer>22222</mediumTimer>\n"
                            + "  <longTimer>33333</longTimer>\n"
                            + "</mapStackConfiguration>\n",
                    StandardCharsets.UTF_8);

            assertTrue(MAPStackConfigurationManagement.looksLikeMapPersist(legacy));
            assertFalse(MAPStackConfigurationManagement.looksLikeMapPersist(
                    write(dir, "Simulator_tcapmanagement.xml",
                            "<TCAPConfig><dialogTimeout>60000</dialogTimeout></TCAPConfig>\n")));

            MAPStackConfigurationManagement cfg = new MAPStackConfigurationManagement();
            cfg.setConfigFileName("Simulator");
            cfg.setPersistDir(dir.getAbsolutePath());
            cfg.load();

            assertEquals(cfg.getShortTimer(), 11111);
            assertEquals(cfg.getMediumTimer(), 22222);
            assertEquals(cfg.getLongTimer(), 33333);
            assertTrue(new File(dir, "Simulator_mapmanagement.xml").exists());
        } finally {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
            dir.delete();
        }
    }

    private static File write(File dir, String name, String content) throws Exception {
        File f = new File(dir, name);
        Files.writeString(f.toPath(), content, StandardCharsets.UTF_8);
        return f;
    }
}
