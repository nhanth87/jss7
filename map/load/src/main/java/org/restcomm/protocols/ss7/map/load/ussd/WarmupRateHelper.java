package org.restcomm.protocols.ss7.map.load.ussd;

import java.util.ArrayList;
import java.util.List;

/**
 * Default warmup ramp for MAP USSD load client (first 60s).
 * Steps: 1 → 100 → 500 → 1000 → 2000 → 3000 → 5000 → 7000 → 10000 TPS (capped at target).
 */
public final class WarmupRateHelper {

    public static final int[] STEPS = {1, 100, 500, 1000, 2000, 3000, 5000, 7000, 10000};
    public static final long WARMUP_MS = 60_000L;

    /** Enabled by default; set false via -Dwarmup=false */
    public static boolean enabled = !"false".equalsIgnoreCase(System.getProperty("warmup", "true"));

    private WarmupRateHelper() {
    }

    public static double tpsAt(long elapsedMs, double targetTps) {
        double target = Math.max(1.0, targetTps);
        if (!enabled || elapsedMs >= WARMUP_MS) {
            return target;
        }
        List<Integer> steps = buildSteps((int) target);
        double progress = Math.max(0.0, Math.min(1.0, (double) elapsedMs / WARMUP_MS));
        int idx = (int) (progress * steps.size());
        if (idx >= steps.size()) {
            idx = steps.size() - 1;
        }
        return Math.max(1.0, steps.get(idx));
    }

    static List<Integer> buildSteps(int targetTps) {
        List<Integer> steps = new ArrayList<>();
        for (int step : STEPS) {
            if (step <= targetTps) {
                steps.add(step);
            } else {
                if (steps.isEmpty() || steps.get(steps.size() - 1) != targetTps) {
                    steps.add(targetTps);
                }
                break;
            }
        }
        if (steps.isEmpty()) {
            steps.add(1);
            if (targetTps > 1) {
                steps.add(targetTps);
            }
        } else if (steps.get(steps.size() - 1) < targetTps) {
            steps.add(targetTps);
        }
        return steps;
    }

    public static String summary(int targetTps) {
        if (!enabled) {
            return "warmup off — full " + targetTps + " TPS from start";
        }
        StringBuilder sb = new StringBuilder("warmup 60s: ");
        List<Integer> steps = buildSteps(targetTps);
        for (int i = 0; i < steps.size(); i++) {
            if (i > 0) {
                sb.append(" → ");
            }
            sb.append(steps.get(i));
        }
        sb.append(" TPS");
        return sb.toString();
    }
}
