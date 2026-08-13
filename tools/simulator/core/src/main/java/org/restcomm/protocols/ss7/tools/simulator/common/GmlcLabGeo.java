package org.restcomm.protocols.ss7.tools.simulator.common;

import java.util.Random;

/**
 * Digicom-ET GMLC lab: random Addis Ababa GAD points for ATI / PSI / LCS simulator
 * responses so HTTP {@code estimate} has lat/lon suitable for Google Maps.
 *
 * <p>Enable with {@code -Dgmlc.sim.addis=true} (preferred). Legacy
 * {@code -Dgmlc.sim.psi.addis=true} still enables the same behaviour.
 */
public final class GmlcLabGeo {

    public static final String BUILD_MARKER = "gmlc-addis-v1";
    public static final int MCC = 636;
    public static final int MNC = 1;
    public static final double MIN_LATITUDE = 8.95;
    public static final double MAX_LATITUDE = 9.11;
    public static final double MIN_LONGITUDE = 38.68;
    public static final double MAX_LONGITUDE = 38.86;

    public record Sample(int mcc, int mnc, int lac, int cellId,
            double latitude, double longitude, double uncertaintyMeters) {
    }

    private GmlcLabGeo() {
    }

    public static boolean enabled() {
        String master = System.getProperty("gmlc.sim.addis");
        if (master != null && !master.isEmpty()) {
            return Boolean.parseBoolean(master);
        }
        String legacy = System.getProperty("gmlc.sim.psi.addis");
        if (legacy != null && !legacy.isEmpty()) {
            return Boolean.parseBoolean(legacy);
        }
        return false;
    }

    /** Rough city bounding box around Addis Ababa (Ethiopia). */
    public static double[] randomAddis(Random rand) {
        double lat = MIN_LATITUDE + rand.nextDouble() * (MAX_LATITUDE - MIN_LATITUDE);
        double lon = MIN_LONGITUDE + rand.nextDouble() * (MAX_LONGITUDE - MIN_LONGITUDE);
        return new double[] { lat, lon };
    }

    public static Sample randomSample(Random rand) {
        double[] point = randomAddis(rand);
        return new Sample(MCC, MNC, randomLac(rand), randomCellId(rand),
                point[0], point[1], randomUncertaintyMeters(rand));
    }

    public static int randomLac(Random rand) {
        return 1000 + rand.nextInt(8000);
    }

    public static int randomCellId(Random rand) {
        return 10000 + rand.nextInt(50000);
    }

    public static double randomUncertaintyMeters(Random rand) {
        return 50.0 + rand.nextDouble() * 200.0;
    }
}
