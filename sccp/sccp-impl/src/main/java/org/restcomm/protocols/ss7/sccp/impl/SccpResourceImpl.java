package org.restcomm.protocols.ss7.sccp.impl;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.sccp.ConcernedSignalingPointCode;
import org.restcomm.protocols.ss7.sccp.RemoteSignalingPointCode;
import org.restcomm.protocols.ss7.sccp.RemoteSubSystem;
import org.restcomm.protocols.ss7.sccp.SccpResource;
import org.restcomm.protocols.ss7.sccp.impl.oam.SccpOAMMessage;

/**
 * @author amit bhayani
 */
public class SccpResourceImpl implements SccpResource {

    private static final Logger logger = Logger.getLogger(SccpResourceImpl.class);

    protected RemoteSubSystemMap<Integer, RemoteSubSystem> remoteSsns = new RemoteSubSystemMap<Integer, RemoteSubSystem>();
    protected RemoteSignalingPointCodeMap<Integer, RemoteSignalingPointCode> remoteSpcs = new RemoteSignalingPointCodeMap<Integer, RemoteSignalingPointCode>();
    protected ConcernedSignalingPointCodeMap<Integer, ConcernedSignalingPointCode> concernedSpcs = new ConcernedSignalingPointCodeMap<Integer, ConcernedSignalingPointCode>();

    private final String name;
    private String persistDir = null;
    protected final boolean rspProhibitedByDefault;
    private final PersistentStorage persistenceStorage = new PersistentStorage();
    private final Ss7ExtSccpDetailedInterface ss7ExtSccpDetailedInterface;

    public SccpResourceImpl(String name, Ss7ExtSccpDetailedInterface ss7ExtSccpDetailedInterface) {
        this(name, false, ss7ExtSccpDetailedInterface);
    }

    public SccpResourceImpl(String name, boolean rspProhibitedByDefault, Ss7ExtSccpDetailedInterface ss7ExtSccpDetailedInterface) {
        this.name = name;
        this.rspProhibitedByDefault = rspProhibitedByDefault;
        this.ss7ExtSccpDetailedInterface = ss7ExtSccpDetailedInterface;
    }

    public String getPersistDir() {
        return persistDir;
    }

    public void setPersistDir(String persistDir) {
        this.persistDir = persistDir;
    }

    public void start() {
        this.persistenceStorage.setPersistDir(this.persistDir, this.name);
        this.load();

        logger.info("Started Sccp Resource");
    }

    public void stop() {
        this.store();
    }

    public void load() {
        PersistentStorage.ResourcesSet resources = this.persistenceStorage.load(ss7ExtSccpDetailedInterface);
        if (resources == null) {
            return;
        }
        for (RemoteSignalingPointCode rsp : resources.remoteSpcs.values()) {
            ((RemoteSignalingPointCodeImpl) rsp).setProhibitedState(rspProhibitedByDefault, rspProhibitedByDefault);
        }
        this.remoteSpcs = resources.remoteSpcs;
        this.remoteSsns = resources.remoteSsns;
        this.concernedSpcs = resources.concernedSpcs;
    }

    public synchronized void store() {
        this.persistenceStorage.store(remoteSpcs, remoteSsns, concernedSpcs);
    }

    public void addRemoteSsn(int remoteSsnid, int remoteSpc, int remoteSsn, int remoteSsnFlag,
                             boolean markProhibitedWhenSpcResuming) throws Exception {

        if (this.getRemoteSsn(remoteSsnid) != null) {
            throw new Exception(SccpOAMMessage.RSS_ALREADY_EXIST);
        }

        // TODO check if RemoteSignalingPointCode correspond to remoteSpc exist?

        RemoteSubSystemImpl rsscObj = new RemoteSubSystemImpl(remoteSpc, remoteSsn, remoteSsnFlag,
                markProhibitedWhenSpcResuming);

        synchronized (this) {
            RemoteSubSystemMap<Integer, RemoteSubSystem> newRemoteSsns = new RemoteSubSystemMap<Integer, RemoteSubSystem>();
            newRemoteSsns.putAll(this.remoteSsns);
            newRemoteSsns.put(remoteSsnid, rsscObj);
            this.remoteSsns = newRemoteSsns;
            this.store();
        }
    }

    public void modifyRemoteSsn(int remoteSsnid, int remoteSpc, int remoteSsn, int remoteSsnFlag,
                                boolean markProhibitedWhenSpcResuming) throws Exception {
        RemoteSubSystemImpl rsscObj = (RemoteSubSystemImpl) this.remoteSsns.get(remoteSsnid);
        if (rsscObj == null) {
            throw new Exception(String.format(SccpOAMMessage.RSS_DOESNT_EXIST, this.name));
        }

        // TODO check if RemoteSignalingPointCode correspond to remoteSpc
        // exist?

        synchronized (this) {
            if (remoteSpc != -99)
                rsscObj.setRemoteSpc(remoteSpc);
            if (remoteSsn != -99)
                rsscObj.setRemoteSsn(remoteSsn);
            if (remoteSsnFlag != -99)
                rsscObj.setRemoteSsnFlag(remoteSsnFlag);
            rsscObj.setMarkProhibitedWhenSpcResuming(markProhibitedWhenSpcResuming);

            this.store();
        }
    }

    public void removeRemoteSsn(int remoteSsnid) throws Exception {

        if (this.getRemoteSsn(remoteSsnid) == null) {
            throw new Exception(String.format(SccpOAMMessage.RSS_DOESNT_EXIST, this.name));
        }

        synchronized (this) {
            RemoteSubSystemMap<Integer, RemoteSubSystem> newRemoteSsns = new RemoteSubSystemMap<Integer, RemoteSubSystem>();
            newRemoteSsns.putAll(this.remoteSsns);
            newRemoteSsns.remove(remoteSsnid);
            this.remoteSsns = newRemoteSsns;
            this.store();
        }
    }

    public RemoteSubSystem getRemoteSsn(int remoteSsnId) {
        return this.remoteSsns.get(remoteSsnId);
    }

    public RemoteSubSystem getRemoteSsn(int spc, int remoteSsn) {
        for (Map.Entry<Integer, RemoteSubSystem> e : this.remoteSsns.entrySet()) {
            RemoteSubSystem remoteSubSystem = e.getValue();
            if (remoteSubSystem.getRemoteSpc() == spc && remoteSsn == remoteSubSystem.getRemoteSsn()) {
                return remoteSubSystem;
            }

        }
        return null;
    }

    public Map<Integer, RemoteSubSystem> getRemoteSsns() {
        Map<Integer, RemoteSubSystem> remoteSsnsTmp = new HashMap<Integer, RemoteSubSystem>();
        remoteSsnsTmp.putAll(remoteSsns);
        return remoteSsnsTmp;
    }

    public void addRemoteSpc(int remoteSpcId, int remoteSpc, int remoteSpcFlag, int mask) throws Exception {
        if (this.getRemoteSpc(remoteSpcId) != null) {
            throw new Exception(SccpOAMMessage.RSPC_ALREADY_EXIST);
        }

        RemoteSignalingPointCodeImpl rspcObj = new RemoteSignalingPointCodeImpl(remoteSpc, remoteSpcFlag, mask,
                this.rspProhibitedByDefault);
        rspcObj.createRemoteSignalingPointCodeExt(ss7ExtSccpDetailedInterface);

        synchronized (this) {
            RemoteSignalingPointCodeMap<Integer, RemoteSignalingPointCode> newRemoteSpcs = new RemoteSignalingPointCodeMap<Integer, RemoteSignalingPointCode>();
            newRemoteSpcs.putAll(this.remoteSpcs);
            newRemoteSpcs.put(remoteSpcId, rspcObj);
            this.remoteSpcs = newRemoteSpcs;
            this.store();
        }
    }

    public void modifyRemoteSpc(int remoteSpcId, int remoteSpc, int remoteSpcFlag, int mask) throws Exception {
        RemoteSignalingPointCodeImpl remoteSignalingPointCode = (RemoteSignalingPointCodeImpl) this.getRemoteSpc(remoteSpcId);
        if (remoteSignalingPointCode == null) {
            throw new Exception(String.format(SccpOAMMessage.RSPC_DOESNT_EXIST, this.name));
        }

        synchronized (this) {
            if (remoteSpc != -99)
                remoteSignalingPointCode.setRemoteSpc(remoteSpc);
            if (remoteSpcFlag != -99)
                remoteSignalingPointCode.setRemoteSpcFlag(remoteSpcFlag);
            if (mask != -99)
                remoteSignalingPointCode.setMask(mask);

            this.store();
        }
    }

    public void removeRemoteSpc(int remoteSpcId) throws Exception {
        if (this.getRemoteSpc(remoteSpcId) == null) {
            throw new Exception(String.format(SccpOAMMessage.RSPC_DOESNT_EXIST, this.name));
        }

        synchronized (this) {
            RemoteSignalingPointCodeMap<Integer, RemoteSignalingPointCode> newRemoteSpcs = new RemoteSignalingPointCodeMap<Integer, RemoteSignalingPointCode>();
            newRemoteSpcs.putAll(this.remoteSpcs);
            newRemoteSpcs.remove(remoteSpcId);
            this.remoteSpcs = newRemoteSpcs;
            this.store();
        }
    }

    public RemoteSignalingPointCode getRemoteSpc(int remoteSpcId) {
        return this.remoteSpcs.get(remoteSpcId);
    }

    public RemoteSignalingPointCode getRemoteSpcByPC(int remotePC) {
        for (Map.Entry<Integer, RemoteSignalingPointCode> e : this.remoteSpcs.entrySet()) {
            RemoteSignalingPointCode remoteSignalingPointCode = e.getValue();
            if (remoteSignalingPointCode.getRemoteSpc() == remotePC) {
                return remoteSignalingPointCode;
            }
        }
        return null;
    }

    public Map<Integer, RemoteSignalingPointCode> getRemoteSpcs() {
        Map<Integer, RemoteSignalingPointCode> remoteSpcsTmp = new HashMap<Integer, RemoteSignalingPointCode>();
        remoteSpcsTmp.putAll(remoteSpcs);
        return remoteSpcsTmp;
    }

    public void addConcernedSpc(int concernedSpcId, int remoteSpc) throws Exception {
        if (this.getConcernedSpc(concernedSpcId) != null) {
            throw new Exception(SccpOAMMessage.CS_ALREADY_EXIST);
        }

        ConcernedSignalingPointCodeImpl concernedSpc = new ConcernedSignalingPointCodeImpl(remoteSpc);

        synchronized (this) {
            ConcernedSignalingPointCodeMap<Integer, ConcernedSignalingPointCode> newConcernedSpcs = new ConcernedSignalingPointCodeMap<Integer, ConcernedSignalingPointCode>();
            newConcernedSpcs.putAll(this.concernedSpcs);
            newConcernedSpcs.put(concernedSpcId, concernedSpc);
            this.concernedSpcs = newConcernedSpcs;
            this.store();
        }
    }

    public void removeConcernedSpc(int concernedSpcId) throws Exception {
        if (this.getConcernedSpc(concernedSpcId) == null) {
            throw new Exception(String.format(SccpOAMMessage.CS_DOESNT_EXIST, this.name));
        }

        synchronized (this) {
            ConcernedSignalingPointCodeMap<Integer, ConcernedSignalingPointCode> newConcernedSpcs = new ConcernedSignalingPointCodeMap<Integer, ConcernedSignalingPointCode>();
            newConcernedSpcs.putAll(this.concernedSpcs);
            newConcernedSpcs.remove(concernedSpcId);
            this.concernedSpcs = newConcernedSpcs;
            this.store();
        }
    }

    public void modifyConcernedSpc(int concernedSpcId, int remoteSpc) throws Exception {
        ConcernedSignalingPointCodeImpl concernedSignalingPointCode = (ConcernedSignalingPointCodeImpl) this
                .getConcernedSpc(concernedSpcId);

        if (concernedSignalingPointCode == null) {
            throw new Exception(String.format(SccpOAMMessage.CS_DOESNT_EXIST, this.name));
        }

        synchronized (this) {
            concernedSignalingPointCode.setRemoteSpc(remoteSpc);
            this.store();
        }
    }

    public ConcernedSignalingPointCode getConcernedSpc(int concernedSpcId) {
        return this.concernedSpcs.get(concernedSpcId);
    }

    public ConcernedSignalingPointCode getConcernedSpcByPC(int remotePC) {
        for (Map.Entry<Integer, ConcernedSignalingPointCode> e : this.concernedSpcs.entrySet()) {
            ConcernedSignalingPointCode concernedSubSystem = e.getValue();
            if (concernedSubSystem.getRemoteSpc() == remotePC) {
                return concernedSubSystem;
            }
        }
        return null;
    }

    public Map<Integer, ConcernedSignalingPointCode> getConcernedSpcs() {
        Map<Integer, ConcernedSignalingPointCode> concernedSpcsTmp = new HashMap<Integer, ConcernedSignalingPointCode>();
        concernedSpcsTmp.putAll(concernedSpcs);
        return concernedSpcsTmp;
    }

    public void removeAllResources() {
        synchronized (this) {
            if (this.remoteSsns.size() == 0 && this.remoteSpcs.size() == 0 && this.concernedSpcs.size() == 0)
                // no resources allocated - nothing to do
                return;

            remoteSsns = new RemoteSubSystemMap<Integer, RemoteSubSystem>();
            remoteSpcs = new RemoteSignalingPointCodeMap<Integer, RemoteSignalingPointCode>();
            concernedSpcs = new ConcernedSignalingPointCodeMap<Integer, ConcernedSignalingPointCode>();

            // We store the cleared state
            this.store();
        }
    }

    protected static class PersistentStorage {

        private static final Logger logger = Logger.getLogger(SccpResourceImpl.class);

        private static final String SCCP_RESOURCE_PERSIST_DIR_KEY = "sccpresource.persist.dir";
        private static final String USER_DIR_KEY = "user.dir";
        private static final String PERSIST_FILE_NAME = "sccpresource3.xml";

        private String persistFile;

        protected PersistentStorage() {
        }

        private void setPersistDir(String persistDir, String stackName) {
            if (persistDir != null) {
                this.persistFile = persistDir + File.separator + stackName + "_" + PERSIST_FILE_NAME;
            } else {
                this.persistFile = System.getProperty(SCCP_RESOURCE_PERSIST_DIR_KEY, System.getProperty(USER_DIR_KEY))
                        + File.separator + stackName + "_" + PERSIST_FILE_NAME;
            }

            logger.info(String.format("SCCP Resource configuration file path %s", this.persistFile));
        }

        /**
         * Configuration class for XStream persistence
         */
        public static class ResourcesConfig {
            public RemoteSubSystemMap<Integer, RemoteSubSystem> remoteSsns;
            public RemoteSignalingPointCodeMap<Integer, RemoteSignalingPointCode> remoteSpcs;
            public ConcernedSignalingPointCodeMap<Integer, ConcernedSignalingPointCode> concernedSpcs;
        }

        /**
         * Persist
         */
        private void store(RemoteSignalingPointCodeMap<Integer, RemoteSignalingPointCode> remoteSpcs, 
                          RemoteSubSystemMap<Integer, RemoteSubSystem> remoteSsns, 
                          ConcernedSignalingPointCodeMap<Integer, ConcernedSignalingPointCode> concernedSpcs) {
            try {
                ResourcesConfig config = new ResourcesConfig();
                config.remoteSsns = remoteSsns;
                config.remoteSpcs = remoteSpcs;
                config.concernedSpcs = concernedSpcs;

                String xml = SCCPXStreamHelper.toXML(config);
                try (FileWriter writer = new FileWriter(this.persistFile)) {
                    writer.write(xml);
                }
            } catch (Exception e) {
                logger.error("Error while persisting the Sccp Resource state in file", e);
            }
        }

        /**
         * Load and create from persisted file
         */
        protected ResourcesSet load(Ss7ExtSccpDetailedInterface ss7ExtSccpDetailedInterface) {
            ResourcesSet resources = null;
            try {
                File f = new File(this.persistFile);
                if (f.exists()) {
                    // we have V3 config (XStream format)
                    resources = loadVer3(this.persistFile);
                } else {
                    // Try legacy format (sccpresource2.xml)
                    String s1 = this.persistFile.replace("3.xml", "2.xml");
                    f = new File(s1);
                    if (f.exists()) {
                        logger.warn("Legacy SCCP Resource config format (v2) not supported, using defaults");
                    } else {
                        // Try legacy format (sccpresource.xml)
                        s1 = this.persistFile.replace("3.xml", ".xml");
                        f = new File(s1);
                        if (f.exists()) {
                            logger.warn("Legacy SCCP Resource config format (v1) not supported, using defaults");
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                logger.warn(String.format("Failed to load the SS7 configuration file. \n%s", e.getMessage()));
            } catch (IOException e) {
                logger.error(String.format("Failed to load the SS7 configuration file. \n%s", e.getMessage()));
            }

            if (resources != null)
                addExtensionToRemoteSignalingPointCodeMap(resources.remoteSpcs, ss7ExtSccpDetailedInterface);

            return resources;
        }

        protected void addExtensionToRemoteSignalingPointCodeMap(
                RemoteSignalingPointCodeMap<Integer, RemoteSignalingPointCode> remoteSpcs,
                Ss7ExtSccpDetailedInterface ss7ExtSccpDetailedInterface) {
            for (Map.Entry<Integer, RemoteSignalingPointCode> entry : remoteSpcs.entrySet()) {
                ((RemoteSignalingPointCodeImpl) entry.getValue())
                        .createRemoteSignalingPointCodeExt(ss7ExtSccpDetailedInterface);
            }
        }

        protected ResourcesSet loadVer3(String fn) throws FileNotFoundException {
            try (FileReader reader = new FileReader(fn)) {
                ResourcesConfig config = (ResourcesConfig) SCCPXStreamHelper.fromXML(reader);
                if (config != null) {
                    return new ResourcesSet(config.remoteSpcs, config.remoteSsns, config.concernedSpcs);
                }
            }
            return null;
        }

        static class ResourcesSet {
            final RemoteSignalingPointCodeMap<Integer, RemoteSignalingPointCode> remoteSpcs;
            final RemoteSubSystemMap<Integer, RemoteSubSystem> remoteSsns;
            final ConcernedSignalingPointCodeMap<Integer, ConcernedSignalingPointCode> concernedSpcs;

            public ResourcesSet(RemoteSignalingPointCodeMap<Integer, RemoteSignalingPointCode> remoteSpcs,
                                RemoteSubSystemMap<Integer, RemoteSubSystem> remoteSsns,
                                ConcernedSignalingPointCodeMap<Integer, ConcernedSignalingPointCode> concernedSpcs) {
                this.remoteSpcs = remoteSpcs;
                this.remoteSsns = remoteSsns;
                this.concernedSpcs = concernedSpcs;
            }
        }
    }
}
