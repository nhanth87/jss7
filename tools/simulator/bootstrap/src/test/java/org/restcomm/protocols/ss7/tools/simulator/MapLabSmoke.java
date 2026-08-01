package org.restcomm.protocols.ss7.tools.simulator;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.tools.simulator.management.TesterHostImpl;

/**
 * Headless lab smoke: create TesterHost, start SMS_TEST_SERVER stack, send one SRI-SM.
 * Run from simulator-ss7 dir with the same classpath as bin/run.sh.
 */
public final class MapLabSmoke {

    private static final Logger logger = LogManager.getLogger(MapLabSmoke.class);

    private MapLabSmoke() {
    }

    public static void main(String[] args) throws Exception {
        String simHome = args.length > 0 ? args[0] : System.getenv("SIMULATOR_HOME");
        if (simHome == null || simHome.isBlank()) {
            simHome = new File(".").getCanonicalPath();
        }
        String msisdn = args.length > 1 ? args[1] : "251911000099";
        int waitSec = args.length > 2 ? Integer.parseInt(args[2]) : 20;

        File dataDir = new File(simHome, "data");
        System.setProperty(TesterHostImpl.SIMULATOR_HOME_VAR, simHome);
        System.setProperty("simulator.home.dir", simHome);
        System.setProperty("simulator.data.dir", dataDir.getAbsolutePath() + File.separator);
        System.setProperty("log4j.configurationFile", "file:" + simHome + "/conf/log4j2.xml");

        logger.info("MapLabSmoke simHome={} msisdn={}", simHome, msisdn);

        Class.forName("org.restcomm.protocols.ss7.map.service.lsm.ExtGeographicalInformationImpl");
        Class.forName("org.restcomm.protocols.ss7.tools.simulator.common.ConfigurationData");
        logger.info("Classpath smoke: ExtGeographicalInformationImpl + ConfigurationData OK");

        TesterHostImpl host = new TesterHostImpl("main", dataDir.getAbsolutePath());
        logger.info("TesterHost created, task={}", host.getConfigurationData().getInstance_TestTask());

        host.start();
        logger.info("TesterHost started, waiting {}s for SCTP/M3UA...", waitSec);
        Thread.sleep(waitSec * 1000L);

        String result = host.getTestSmsServerMan().performSRIForSM(msisdn);
        logger.info("performSRIForSM({}) => {}", msisdn, result);
        System.out.println("MAP_SRI_RESULT=" + result);

        Thread.sleep(5000L);
        host.stop();
        logger.info("MapLabSmoke complete");
    }
}
