package org.mobicents.protocols.asn;

/**
 * ThreadLocal pool for {@link AsnInputStream} reuse on hot decode paths.
 */
public final class AsnStreamPool {

    private static final ThreadLocal<AsnInputStream> POOL =
            ThreadLocal.withInitial(() -> new AsnInputStream(new byte[0]));

    private AsnStreamPool() {
    }

    public static AsnInputStream borrow(byte[] data) {
        AsnInputStream ais = POOL.get();
        ais.reset(data);
        return ais;
    }

    public static AsnInputStream borrowSlice(byte[] buffer, int offset, int length) {
        AsnInputStream ais = POOL.get();
        ais.resetSlice(buffer, offset, length);
        return ais;
    }

    public static AsnInputStream borrowTagged(byte[] data, int tagClass, boolean isPrimitive, int tag) {
        AsnInputStream ais = POOL.get();
        ais.resetTagged(data, tagClass, isPrimitive, tag);
        return ais;
    }
}
