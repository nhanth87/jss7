package org.restcomm.protocols.ss7.map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.BerCursor;
import org.mobicents.protocols.asn.BerTag;
import org.mobicents.protocols.asn.Jss7AsnConfig;
import org.restcomm.protocols.ss7.map.datacoding.CBSDataCodingSchemeImpl;
import org.restcomm.protocols.ss7.map.primitives.USSDStringImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;

/**
 * Shared support for wiring SMSC/USSD hot-path MAP messages onto {@link org.mobicents.protocols.asn.BerCursor}
 * zero-copy decode with a safe fallback to the classic {@link AsnInputStream} object-tree.
 * <p>
 * Contract used by each wired {@code *Impl.decodeData(ais, length)}:
 * <pre>{@code
 * if (MapBerSupport.useBer(ais)) {
 *     byte[] buf = ais.getBuffer();
 *     int off = MapBerSupport.absOffset(ais);
 *     try {
 *         berDecode(buf, off, length);   // BerCursor navigation, sets fields (views on octets)
 *         ais.advance(length);           // consume — BerCursor read from a snapshot, not the stream
 *         return;
 *     } catch (Throwable t) {            // ANY failure -> legacy path, stream untouched
 *         MapBerSupport.logFallback("MsgName", t);
 *     }
 * }
 * this._decode(ais, length);
 * }</pre>
 * The BerCursor path reads from {@code ais.getBuffer()} at {@code absOffset(ais)} without
 * consuming the stream, so on failure the legacy {@code _decode} decodes normally.
 * Only the heap-backed path is taken; ByteBuf-backed streams fall through to legacy.
 */
public final class MapBerSupport {

    private static final Logger logger = LogManager.getLogger(MapBerSupport.class);

    private MapBerSupport() {
    }

    /** True when BerCursor decode should be attempted for this stream (enabled + heap-backed). */
    public static boolean useBer(AsnInputStream ais) {
        return Jss7AsnConfig.isBerCursorEnabled() && !ais.isByteBufBacked() && ais.getBuffer() != null;
    }

    /** Absolute offset of the current read position within {@link AsnInputStream#getBuffer()}. */
    public static int absOffset(AsnInputStream ais) {
        return ais.getStartOffset() + ais.position();
    }

    /** Records a fallback to the classic decoder (debug level — expected for exotic encodings). */
    public static void logFallback(String messageName, Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.debug("BerCursor decode of " + messageName + " failed; falling back to AsnInputStream: " + t);
        }
    }

    /**
     * Builds an {@link AsnInputStream} over the current TLV's value bytes, carrying the
     * cursor's tag metadata, so a CHOICE/primitive {@code decodeData(ais, len)} can read it.
     * Copies the (small) value — used for context-tagged CHOICE fields like SM-RP-DA/OA.
     */
    public static AsnInputStream taggedValueStream(BerCursor c) {
        byte[] v = java.util.Arrays.copyOfRange(c.heapBuffer(), c.valueOffset(), c.valueOffset() + c.valueLength());
        return new AsnInputStream(v, c.tagClass(), c.isPrimitive(), c.tag());
    }

    /** Result of {@link #decodeUssdArg}: the two mandatory fields shared by all USSD messages. */
    public static final class UssdArg {
        public CBSDataCodingSchemeImpl dcs;
        public USSDStringImpl ussdString;
        /** Optional msisdn [0] (decoded via BerCursor; null if absent). */
        public ISDNAddressStringImpl msisdn;
    }

    /**
     * Zero-copy decode of the USSD argument SEQUENCE {@code { ussd-DataCodingScheme
     * OCTET(1), ussd-String OCTET }} shared by Process/Unstructured-SS
     * request/response messages. The ussd-String is bound as a zero-copy view into
     * {@code buf}. Throws (→ caller falls back to the classic decoder) if any
     * optional/extra field (msisdn / alertingPattern) is present.
     */
    public static UssdArg decodeUssdArg(byte[] buf, int offset, int length) throws AsnException {
        BerCursor c = BerCursor.wrapHeap(buf, offset, length);
        if (!c.hasMore())
            throw new AsnException("USSD arg: empty SEQUENCE");
        c.readTag(); // ussd-DataCodingScheme OCTET STRING (1)
        if (c.tagClass() != BerTag.UNIVERSAL || !c.isPrimitive() || c.valueLength() != 1)
            throw new AsnException("USSD arg: bad ussd-DataCodingScheme");
        UssdArg r = new UssdArg();
        r.dcs = new CBSDataCodingSchemeImpl(c.heapBuffer()[c.valueOffset()] & 0xFF);
        c.skipValue();

        if (!c.hasMore())
            throw new AsnException("USSD arg: missing ussd-String");
        c.readTag(); // ussd-String OCTET STRING
        if (c.tagClass() != BerTag.UNIVERSAL || !c.isPrimitive())
            throw new AsnException("USSD arg: bad ussd-String");
        r.ussdString = new USSDStringImpl(r.dcs);
        r.ussdString.setData(c.heapBuffer(), c.valueOffset(), c.valueLength());
        c.skipValue();

        // Optional msisdn [0] ISDN-AddressString — decode via BerCursor (the common load-test
        // field). alertingPattern (universal OCTET STRING, encoded before msisdn) is rare, so
        // any non-[0] optional field falls back to the classic decoder.
        while (c.hasMore()) {
            c.readTag();
            if (c.tagClass() == BerTag.CONTEXT && c.isPrimitive() && c.tag() == 0) {
                ISDNAddressStringImpl m = new ISDNAddressStringImpl();
                try {
                    m.decodeData(taggedValueStream(c), c.valueLength());
                } catch (MAPParsingComponentException e) {
                    throw new AsnException("USSD arg: bad msisdn: " + e.getMessage());
                }
                r.msisdn = m;
                c.skipValue();
            } else {
                throw new AsnException("USSD arg: unsupported optional field, fallback");
            }
        }
        return r;
    }
}
