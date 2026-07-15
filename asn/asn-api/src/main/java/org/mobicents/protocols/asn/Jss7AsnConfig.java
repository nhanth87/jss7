/*
 * RestComm jSS7 - ASN runtime configuration
 */
package org.mobicents.protocols.asn;

/**
 * ASN.1 feature flags controlled via system properties.
 */
public final class Jss7AsnConfig {

    private Jss7AsnConfig() {
    }

    /**
     * When true, pilot decode paths may use {@link FlatAsnParser} instead of object-tree parsing.
     */
    public static boolean isFlatIndexEnabled() {
        return Boolean.parseBoolean(System.getProperty("jss7.asn.flatIndexEnabled", "true"));
    }

    /**
     * When true, SMSC/USSD hot-path MAP messages decode via {@link BerCursor} zero-copy
     * views (falling back to the classic {@link AsnInputStream} object-tree on any failure,
     * and for the ByteBuf-backed path). Default true; disable with
     * {@code -Djss7.asn.berCursorEnabled=false}.
     */
    public static boolean isBerCursorEnabled() {
        return Boolean.parseBoolean(System.getProperty("jss7.asn.berCursorEnabled", "true"));
    }
}
