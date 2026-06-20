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
}
