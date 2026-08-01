package org.restcomm.protocols.ss7.map;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * read/write MAP layer configuration *.xml file (Jackson XML).
 */
@JacksonXmlRootElement(localName = "mapStackConfiguration")
public class MAPStackConfigurationManagement {

    /**
     * Distinct from TCAP/CAP {@code *_management.xml} — simulator names MAP/TCAP/CAP
     * all {@code Simulator}, so a shared suffix caused cross-layer load failures.
     */
    private static final String PERSIST_FILE_NAME = "mapmanagement.xml";
    /** Pre-split shared name; migrated when content looks like MAP. */
    private static final String LEGACY_PERSIST_FILE_NAME = "management.xml";
    private static final String MAP_MANAGEMENT_PERSIST_DIR_KEY = "mapmanagement.persist.dir";
    private static final String USER_DIR_KEY = "user.dir";
    private static final String DEFAULT_CONFIG_FILE_NAME = "MapStack";

    private static MAPStackConfigurationManagement instance = new MAPStackConfigurationManagement();

    private transient String persistFile = "";
    private transient String configFileName = DEFAULT_CONFIG_FILE_NAME;
    private transient String persistDir = null;

    private int shortTimer = 10000;
    private int mediumTimer = 30000;
    private int longTimer = 600000;

    public MAPStackConfigurationManagement() {
    }

    public static MAPStackConfigurationManagement getInstance() {
        return instance;
    }

    public void setPersistDir(String persistDir) {
        this.persistDir = persistDir;
        this.setPersistFile();
    }

    private void setPersistFile() {
        String dir = persistDir != null
                ? persistDir
                : System.getProperty(MAP_MANAGEMENT_PERSIST_DIR_KEY, System.getProperty(USER_DIR_KEY));
        this.persistFile = dir + File.separator + this.configFileName + "_" + PERSIST_FILE_NAME;
    }

    private File legacyPersistFile() {
        String dir = persistDir != null
                ? persistDir
                : System.getProperty(MAP_MANAGEMENT_PERSIST_DIR_KEY, System.getProperty(USER_DIR_KEY));
        return new File(dir, this.configFileName + "_" + LEGACY_PERSIST_FILE_NAME);
    }

    /**
     * Persist
     */
    public void store() {
        // Skip when not bound to a file. During load(), Jackson deserializes into a fresh
        // instance whose setters (setShortTimer/…) call store() before persistFile is set —
        // writing to an empty path threw FileNotFoundException on every startup.
        if (persistFile == null || persistFile.isBlank()) {
            return;
        }
        try (Writer writer = new FileWriter(persistFile)) {
            MAPJacksonHelper.getXmlMapper().writeValue(writer, this);
        } catch (IOException e) {
            System.err.println(String.format("Error while persisting the MAP Resource state in file=%s", persistFile));
            e.printStackTrace();
        }
    }

    /**
     * Load from persisted file. Called from MAPStackImpl.
     */
    public void load() {
        try {
            setPersistFile();
            File file = resolvePersistFileForLoad();
            if (file == null) {
                return;
            }
            try (Reader reader = new FileReader(file)) {
                MAPStackConfigurationManagement loaded =
                        MAPJacksonHelper.getXmlMapper().readValue(reader, MAPStackConfigurationManagement.class);
                this.shortTimer = loaded.shortTimer;
                this.mediumTimer = loaded.mediumTimer;
                this.longTimer = loaded.longTimer;
            }
            if (!file.getPath().equals(persistFile)) {
                store();
            }
        } catch (Exception e) {
            System.err.println(String.format("Error while load the MAP Resource state from file=%s", persistFile));
            e.printStackTrace();
        }
    }

    File resolvePersistFileForLoad() {
        File primary = new File(persistFile);
        if (primary.exists()) {
            return primary;
        }
        File legacy = legacyPersistFile();
        if (legacy.exists() && looksLikeMapPersist(legacy)) {
            return legacy;
        }
        return null;
    }

    static boolean looksLikeMapPersist(File file) {
        try {
            String xml = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            if (xml.contains("TCAPConfig") || xml.contains("timercircuitswitchedcallcontrol")) {
                return false;
            }
            return xml.contains("mapStackConfiguration") || xml.contains("<shortTimer")
                    || xml.contains("<mediumTimer") || xml.contains("<longTimer");
        } catch (Exception e) {
            return false;
        }
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    public int getShortTimer() {
        return shortTimer;
    }

    public int getMediumTimer() {
        return mediumTimer;
    }

    public int getLongTimer() {
        return longTimer;
    }

    public void setShortTimer(int shortTimer) {
        this.shortTimer = shortTimer;
        this.store();
    }

    public void setMediumTimer(int mediumTimer) {
        this.mediumTimer = mediumTimer;
        this.store();
    }

    public void setLongTimer(int longTimer) {
        this.longTimer = longTimer;
        this.store();
    }
}
