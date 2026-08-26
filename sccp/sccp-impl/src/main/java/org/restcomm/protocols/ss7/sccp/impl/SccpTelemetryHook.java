package org.restcomm.protocols.ss7.sccp.impl;

/**
 * Optional telemetry seam for the Nextgen STP transit plane (owner mandate
 * 2026-08-26, P1): lets a hosting application count relayed / GTT-translated /
 * ACL-denied unit-data messages without touching the data path.
 *
 * <p>Static by design — one JVM hosts one SCCP stack in this product line.
 * A {@code null} listener (the default) costs nothing and changes no behavior;
 * listener exceptions are swallowed so telemetry can never break relay.</p>
 */
public final class SccpTelemetryHook {

    /** One event per egress unit-data handed to MTP3 after successful encode. */
    public interface Listener {
        void onRelayed(int incomingOpc, int dpc);

        /** One successful GTT translation (CdPA rewritten toward the hidden target). */
        void onGttTranslated(int incomingOpc, int dpc);

        /** One GTT miss — no matching rule for a GT-addressed message. */
        void onGttUnrouted(int incomingOpc);

        /** One inbound message denied by the incoming ACL. */
        void onAclDenied(int incomingOpc);
    }

    private static volatile Listener listener;

    private SccpTelemetryHook() {
    }

    public static void set(Listener l) {
        listener = l;
    }

    public static Listener get() {
        return listener;
    }

    public static void fireRelayed(int incomingOpc, int dpc) {
        Listener l = listener;
        if (l != null) {
            try {
                l.onRelayed(incomingOpc, dpc);
            } catch (Throwable ignored) {
            }
        }
    }

    public static void fireGttTranslated(int incomingOpc, int dpc) {
        Listener l = listener;
        if (l != null) {
            try {
                l.onGttTranslated(incomingOpc, dpc);
            } catch (Throwable ignored) {
            }
        }
    }

    public static void fireGttUnrouted(int incomingOpc) {
        Listener l = listener;
        if (l != null) {
            try {
                l.onGttUnrouted(incomingOpc);
            } catch (Throwable ignored) {
            }
        }
    }

    public static void fireAclDenied(int incomingOpc) {
        Listener l = listener;
        if (l != null) {
            try {
                l.onAclDenied(incomingOpc);
            } catch (Throwable ignored) {
            }
        }
    }
}
