package org.restcomm.protocols.ss7.cap.api.isup;

import org.mobicents.protocols.asn.*;

/**
 * BearerCapability ::= CHOICE {
 *     bearerCap [0] IMPLICIT OCTET STRING (SIZE(2..4))
 *       -- Q.931 Bearer Capability IE (octet 3..6 only, no IE ID/length)
 * }
 *
 * CAP v2/3: chỉ có [0], nhưng reserved space cho extensions
 */
public final class BearerCapabilityCodec {

    private static final int TAG_BEARER_CAP = 0x00;

    public static final class Result {
        public BerSlice bearerCapSlice;  // raw Q.931 IE bytes (lazy)
        public void reset() { bearerCapSlice = null; }
    }

    // ── DECODE ───────────────────────────────────────────────────────

    public static void decode(BerCursor cursor, Result out)
            throws AsnException {
        if (cursor.tagClass() != BerTag.CONTEXT)
            throw new AsnException("BearerCapability: expected CONTEXT");
        if (cursor.tag() == TAG_BEARER_CAP) {
            out.bearerCapSlice = cursor.getOctetStringSlice();
        }
        cursor.skipValue();
    }

    // ── ENCODE ───────────────────────────────────────────────────────

    public static void encode(BerWriter w, byte[] bearerCapBytes) {
        w.writeOctetString(BerTag.CONTEXT, TAG_BEARER_CAP,
            bearerCapBytes, 0, bearerCapBytes.length);
    }

    private BearerCapabilityCodec() {}
}
