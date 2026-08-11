package org.restcomm.protocols.ss7.map;

import java.util.concurrent.atomic.LongAdder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.BerCursor;
import org.mobicents.protocols.asn.BerTag;
import org.mobicents.protocols.asn.Jss7AsnConfig;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSCode;
import org.restcomm.protocols.ss7.map.datacoding.CBSDataCodingSchemeImpl;
import org.restcomm.protocols.ss7.map.primitives.CellGlobalIdOrServiceAreaIdFixedLengthImpl;
import org.restcomm.protocols.ss7.map.primitives.CellGlobalIdOrServiceAreaIdOrLAIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.LAIFixedLengthImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive;
import org.restcomm.protocols.ss7.map.primitives.USSDStringImpl;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.service.supplementary.SSCodeImpl;

import java.util.ArrayList;

import java.io.IOException;

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
 *         MapBerSupport.recordBerOk();
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

    /** Always-on (cheap) counters — independent of {@code asn.telemetry.enabled}. */
    private static final LongAdder BER_OK = new LongAdder();
    private static final LongAdder BER_FALLBACK = new LongAdder();

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

    /** Cumulative successful BerCursor MAP decodes (hot path). */
    public static long berOkCount() {
        return BER_OK.sum();
    }

    /** Cumulative BerCursor → AsnInputStream fallbacks (should stay near-zero on USSD load). */
    public static long berFallbackCount() {
        return BER_FALLBACK.sum();
    }

    /** Record a successful BerCursor decode for load-test / ops observability. */
    public static void recordBerOk() {
        BER_OK.increment();
    }

    /** Records a fallback to the classic decoder (debug level — expected for exotic encodings). */
    public static void logFallback(String messageName, Throwable t) {
        BER_FALLBACK.increment();
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

    @FunctionalInterface
    public interface TopLevelDecoder {
        void decode(AsnInputStream input, int length)
                throws MAPParsingComponentException, IOException, AsnException;
    }

    @FunctionalInterface
    public interface BerDecoder {
        void decode(byte[] buf, int offset, int length)
                throws AsnException, IOException, MAPParsingComponentException;
    }

    /**
     * SRI-SM-style dispatch: try {@link BerDecoder} on the heap snapshot; on any failure
     * clear fields and run the classic {@link TopLevelDecoder}. The stream is advanced
     * only after a successful BerCursor decode.
     */
    public static void decodeDispatch(String messageName, AsnInputStream input, int length,
            BerDecoder berDecoder, Runnable clearFields, TopLevelDecoder legacy)
            throws MAPParsingComponentException, IOException, AsnException {
        if (useBer(input)) {
            try {
                berDecoder.decode(input.getBuffer(), absOffset(input), length);
                input.advance(length);
                recordBerOk();
                return;
            } catch (Throwable t) {
                logFallback(messageName, t);
                if (clearFields != null)
                    clearFields.run();
            }
        }
        legacy.decode(input, length);
    }

    /**
     * Performs bounded BerCursor dispatch for large MAP top-level messages while retaining
     * the complete classic field decoder. The cursor pass is side-effect free: malformed BER
     * falls back with the input position and target object untouched. Once validation succeeds,
     * the classic decoder remains the single source of truth for known optionals and unknown
     * extension skipping.
     *
     * @deprecated use {@link #decodeDispatch} with a real {@code _decodeBer} that understands
     *             known optionals; this walk-only helper is insufficient for hot-path decode.
     */
    @Deprecated
    public static void decodeTopLevel(String messageName, AsnInputStream input, int length,
            TopLevelDecoder decoder) throws MAPParsingComponentException, IOException, AsnException {
        decodeDispatch(messageName, input, length, (buf, off, len) -> {
            throw new AsnException(messageName + ": decodeTopLevel is walk-only; use decodeDispatch");
        }, null, decoder);
    }

    /** Decode a nested MAP primitive from the current TLV value, then skip it. */
    public static void decodeNested(BerCursor c, MAPAsnPrimitive dest) throws MAPParsingComponentException {
        dest.decodeData(taggedValueStream(c), c.valueLength());
        c.skipValue();
    }

    /**
     * Decode an explicitly tagged CHOICE: open the constructed wrapper, read the inner
     * CHOICE tag, then {@code decodeData} the value.
     */
    public static void decodeExplicitChoice(BerCursor c, MAPAsnPrimitive dest)
            throws AsnException, MAPParsingComponentException {
        if (c.isPrimitive())
            throw new AsnException("expected constructed explicit CHOICE");
        BerCursor inner = c.openConstructed();
        if (!inner.hasMore())
            throw new AsnException("empty explicit CHOICE");
        inner.readTag();
        dest.decodeData(taggedValueStream(inner), inner.valueLength());
        c.skipValue();
    }

    public static int readInt(BerCursor c) {
        return c.readInt32();
    }

    public static void readNull(BerCursor c) throws AsnException {
        if (!c.isPrimitive())
            throw new AsnException("NULL must be primitive");
        c.skipValue();
    }

    public static int firstOctet(BerCursor c) throws AsnException {
        if (c.valueLength() < 1)
            throw new AsnException("empty OCTET STRING");
        int v = c.heapBuffer()[c.valueOffset()] & 0xFF;
        c.skipValue();
        return v;
    }

    public static void unknownTag(String name, BerCursor c) throws AsnException {
        throw new AsnException(name + ": unknown tag class=" + c.tagClass() + " tag=" + c.tag());
    }

    public static ArrayList<SSCode> decodeSsList(BerCursor c) throws AsnException, MAPParsingComponentException {
        if (c.isPrimitive())
            throw new AsnException("ssList is primitive");
        BerCursor inner = c.openConstructed();
        ArrayList<SSCode> list = new ArrayList<>();
        while (inner.hasMore()) {
            inner.readTag();
            if (inner.tagClass() != BerTag.UNIVERSAL || !inner.isPrimitive() || inner.tag() != BerTag.OCTET_STRING)
                throw new AsnException("bad ssList element");
            SSCodeImpl sc = new SSCodeImpl();
            sc.decodeData(taggedValueStream(inner), inner.valueLength());
            inner.skipValue();
            list.add(sc);
        }
        c.skipValue();
        return list;
    }

    public static CellGlobalIdOrServiceAreaIdOrLAI decodeCellIdOrSai(BerCursor c, String name)
            throws AsnException, MAPParsingComponentException {
        if (c.isPrimitive()) {
            int len = c.valueLength();
            if (len == 7) {
                CellGlobalIdOrServiceAreaIdFixedLengthImpl val = new CellGlobalIdOrServiceAreaIdFixedLengthImpl();
                val.decodeData(taggedValueStream(c), len);
                c.skipValue();
                return new CellGlobalIdOrServiceAreaIdOrLAIImpl(val);
            }
            if (len == 5) {
                LAIFixedLengthImpl val = new LAIFixedLengthImpl();
                val.decodeData(taggedValueStream(c), len);
                c.skipValue();
                return new CellGlobalIdOrServiceAreaIdOrLAIImpl(val);
            }
            throw new AsnException(name + ": cellIdOrSai length must be 5 or 7");
        }
        CellGlobalIdOrServiceAreaIdOrLAIImpl cgi = new CellGlobalIdOrServiceAreaIdOrLAIImpl();
        decodeExplicitChoice(c, cgi);
        return cgi;
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
