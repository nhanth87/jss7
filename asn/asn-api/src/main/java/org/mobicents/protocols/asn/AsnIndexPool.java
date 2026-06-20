/*
 * RestComm jSS7 - Flat ASN.1 Index Pool
 */
package org.mobicents.protocols.asn;

/**
 * Thread-local pool of reusable {@link AsnMessageIndex} instances.
 */
public final class AsnIndexPool {

    private static final ThreadLocal<AsnMessageIndex> INDEX_POOL =
            ThreadLocal.withInitial(AsnMessageIndex::new);

    private AsnIndexPool() {
    }

    public static AsnMessageIndex get() {
        return INDEX_POOL.get();
    }
}
