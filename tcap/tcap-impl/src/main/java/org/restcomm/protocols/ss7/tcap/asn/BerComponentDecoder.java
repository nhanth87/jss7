package org.restcomm.protocols.ss7.tcap.asn;

import java.util.List;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.BerCursor;
import org.mobicents.protocols.asn.BerSlice;
import org.mobicents.protocols.asn.BerTag;
import org.restcomm.protocols.ss7.tcap.asn.comp.Component;
import org.restcomm.protocols.ss7.tcap.asn.comp.ErrorCode;
import org.restcomm.protocols.ss7.tcap.asn.comp.OperationCode;
import org.restcomm.protocols.ss7.tcap.asn.comp.Parameter;

/**
 * Zero-copy TCAP component decoder using {@link BerCursor}.
 *
 * <p>Replaces {@code ComponentPortionImpl.decode(AsnInputStream)} on the new codec
 * path. Components are decoded in a single pass over the buffer with minimal
 * object allocation.
 *
 * <p>TCAP Component tags (ITU-T Q.773):
 * <pre>
 *   [1] IMPLICIT SEQUENCE = Invoke
 *   [2] IMPLICIT SEQUENCE = ReturnResultLast
 *   [3] IMPLICIT SEQUENCE = ReturnError
 *   [4] IMPLICIT SEQUENCE = Reject
 *   [7] IMPLICIT SEQUENCE = ReturnResultNotLast
 * </pre>
 *
 * <p>Split for agent readability: this file covers Invoke + ReturnResultLast.
 * See {@link BerComponentDecoderExtra} for remaining component types.
 */
public final class BerComponentDecoder {

    // ── TCAP Component tags ──────────────────────────────────────────
    static final int TAG_INVOKE         = 0x01;
    static final int TAG_RRL            = 0x02;
    static final int TAG_RETURN_ERROR   = 0x03;
    static final int TAG_REJECT         = 0x04;
    static final int TAG_RRNL           = 0x07;

    private BerComponentDecoder() {}

    // ==================================================================
    // ENTRY POINT
    // ==================================================================

    /**
     * Decode a ComponentPortion (outermost SEQUENCE OF Component).
     *
     * @param cursor  BerCursor positioned on componentPortion content
     * @param out     list to receive decoded components (caller provides to avoid allocation)
     */
    public static void decodeComponents(BerCursor cursor,
                                         List<Component> out)
            throws AsnException {
        while (cursor.hasMore()) {
            cursor.readTag();
            if (cursor.tagClass() != BerTag.CONTEXT) {
                cursor.skipValue();
                continue;
            }
            Component comp = decodeSingleComponent(cursor);
            if (comp != null) out.add(comp);
        }
    }

    // ==================================================================
    // DISPATCH
    // ==================================================================

    private static Component decodeSingleComponent(BerCursor cursor)
            throws AsnException {
        switch (cursor.tag()) {
            case TAG_INVOKE:       return decodeInvoke(cursor);
            case TAG_RRL:          return decodeReturnResultLast(cursor);
            case TAG_RRNL:         return BerComponentDecoderExtra.decodeReturnResultNotLast(cursor);
            case TAG_RETURN_ERROR: return BerComponentDecoderExtra.decodeReturnError(cursor);
            case TAG_REJECT:       return BerComponentDecoderExtra.decodeReject(cursor);
            default:
                cursor.skipValue();
                return null;
        }
    }

    // ==================================================================
    // INVOKE [1] IMPLICIT SEQUENCE
    // ==================================================================

    /**
     * <pre>
     * Invoke ::= [1] IMPLICIT SEQUENCE {
     *   invokeId        InvokeIdType,
     *   linkedId        [0] IMPLICIT InvokeIdType OPTIONAL,
     *   operationCode   OperationCode,
     *   parameter       ANY OPTIONAL
     * }
     * </pre>
     */
    static Component decodeInvoke(BerCursor outer) throws AsnException {
        InvokeImpl invoke = new InvokeImpl();
        BerCursor body = outer.openConstructed();
        try {
            // ── invokeId ────────────────────────────────────────────────
            body.readTag();
            assertUniversalPrimitive(body, BerTag.INTEGER, "Invoke.invokeId");
            invoke.setInvokeId(Long.valueOf(body.readInt32()));
            body.skipValue();
            if (!body.hasMore()) return finish(outer, invoke);

            // ── linkedId [0] OPTIONAL ───────────────────────────────────
            body.readTag();
            if (body.tagClass() == BerTag.CONTEXT && body.tag() == 0x00) {
                invoke.setLinkedId(Long.valueOf(body.readInt32()));
                body.skipValue();
                if (!body.hasMore()) return finish(outer, invoke);
                body.readTag();
            }

            // ── operationCode ──────────────────────────────────────────
            OperationCode opCode = decodeOperationCode(body);
            invoke.setOperationCode(opCode);
            body.skipValue();
            if (!body.hasMore()) return finish(outer, invoke);

            // ── parameter (ANY) OPTIONAL ───────────────────────────────
            body.readTag();
            Parameter param = decodeParameter(body);
            invoke.setParameter(param);
            body.skipValue();
        } finally {
            body.release();
        }
        return finish(outer, invoke);
    }

    // ==================================================================
    // RETURN RESULT LAST [2] IMPLICIT SEQUENCE
    // ==================================================================

    /**
     * <pre>
     * ReturnResultLast ::= [2] IMPLICIT SEQUENCE {
     *   invokeId   InvokeIdType,
     *   result     SEQUENCE { operationCode  OperationCode, parameter  ANY } OPTIONAL
     * }
     * </pre>
     */
    static Component decodeReturnResultLast(BerCursor outer) throws AsnException {
        ReturnResultLastImpl rrl = new ReturnResultLastImpl();
        BerCursor body = outer.openConstructed();
        try {
            body.readTag();
            assertUniversalPrimitive(body, BerTag.INTEGER, "RRL.invokeId");
            rrl.setInvokeId(Long.valueOf(body.readInt32()));
            body.skipValue();
            if (!body.hasMore()) return finish(outer, rrl);

            body.readTag();
            if (body.tagClass() == BerTag.UNIVERSAL && body.tag() == BerTag.SEQUENCE) {
                BerCursor resultBody = body.openConstructed();
                try {
                    resultBody.readTag();
                    OperationCode opCode = decodeOperationCode(resultBody);
                    rrl.setOperationCode(opCode);
                    resultBody.skipValue();
                    if (resultBody.hasMore()) {
                        resultBody.readTag();
                        rrl.setParameter(decodeParameter(resultBody));
                        resultBody.skipValue();
                    }
                } finally {
                    resultBody.release();
                }
            }
            body.skipValue();
        } finally {
            body.release();
        }
        return finish(outer, rrl);
    }

    // ==================================================================
    // OPERATION CODE
    // ==================================================================

    /**
     * <pre>
     * OperationCode ::= CHOICE {
     *   localValue   INTEGER,
     *   globalValue  OBJECT IDENTIFIER
     * }
     * </pre>
     */
    static OperationCode decodeOperationCode(BerCursor cursor) throws AsnException {
        OperationCodeImpl opCode = new OperationCodeImpl();
        if (cursor.tagClass() == BerTag.CONTEXT) {
            if (cursor.tag() == 0x00) {
                opCode.setLocalOperationCode(Long.valueOf(cursor.readInt32()));
            } else if (cursor.tag() == 0x01) {
                opCode.setGlobalOperationCode(decodeOidArcs(cursor.getOctetStringSlice()));
            }
        } else if (cursor.tagClass() == BerTag.UNIVERSAL) {
            if (cursor.tag() == BerTag.INTEGER) {
                opCode.setLocalOperationCode(Long.valueOf(cursor.readInt32()));
            } else if (cursor.tag() == BerTag.OID) {
                opCode.setGlobalOperationCode(decodeOidArcs(cursor.getOctetStringSlice()));
            }
        }
        return opCode;
    }

    // ==================================================================
    // ERROR CODE
    // ==================================================================

    /**
     * <pre>
     * ErrorCode ::= CHOICE {
     *   localValue   INTEGER,
     *   globalValue  OBJECT IDENTIFIER
     * }
     * </pre>
     */
    static ErrorCode decodeErrorCode(BerCursor cursor) throws AsnException {
        ErrorCodeImpl errCode = new ErrorCodeImpl();
        if (cursor.tagClass() == BerTag.CONTEXT) {
            if (cursor.tag() == 0x00) {
                errCode.setLocalErrorCode(Long.valueOf(cursor.readInt32()));
            } else if (cursor.tag() == 0x01) {
                errCode.setGlobalErrorCode(decodeOidArcs(cursor.getOctetStringSlice()));
            }
        }
        return errCode;
    }

    // ==================================================================
    // PARAMETER
    // ==================================================================

    /**
     * Creates a {@link ParameterImpl} from the current cursor position.
     * Reads the tag/class/primitive metadata and raw bytes.
     */
    static Parameter decodeParameter(BerCursor cursor) {
        ParameterImpl param = new ParameterImpl();
        param.setTag(cursor.tag());
        param.setTagClass(cursor.tagClass());
        param.setPrimitive(cursor.isPrimitive());
        param.setData(cursor.getOctetString());
        return param;
    }

    // ==================================================================
    // GUARDS
    // ==================================================================

    static void assertUniversalPrimitive(BerCursor cursor,
                                          int expectedTag,
                                          String context) throws AsnException {
        if (cursor.tagClass() != BerTag.UNIVERSAL
                || !cursor.isPrimitive()
                || cursor.tag() != expectedTag) {
            throw new AsnException(
                String.format("[%s] Expected UNIVERSAL PRIMITIVE tag=%d, "
                    + "got class=%d primitive=%b tag=%d",
                    context, expectedTag,
                    cursor.tagClass(), cursor.isPrimitive(), cursor.tag())
            );
        }
    }

    static void assertContext(BerCursor cursor, String context) throws AsnException {
        if (cursor.tagClass() != BerTag.CONTEXT) {
            throw new AsnException(
                String.format("[%s] Expected CONTEXT tag, got class=%d tag=%d",
                    context, cursor.tagClass(), cursor.tag())
            );
        }
    }

    // ==================================================================
    // HELPERS
    // ==================================================================

    /** Advances the outer cursor and returns the component. */
    private static Component finish(BerCursor outer, Component comp) {
        outer.skipValue();
        return comp;
    }

    /**
     * Decodes OID arcs from a raw slice. Returns a new array (not cached).
     */
    static long[] decodeOidArcs(BerSlice slice) {
        byte[] raw = slice.toByteArray();
        int count = 2; // first two arcs encoded in first byte
        for (int i = 1; i < raw.length; i++) {
            if ((raw[i] & 0x80) == 0) count++;
        }
        long[] arcs = new long[count];
        int first = raw[0] & 0xFF;
        arcs[0] = first / 40;
        arcs[1] = first % 40;
        int idx = 2;
        int arc = 0;
        for (int i = 1; i < raw.length; i++) {
            int b = raw[i] & 0xFF;
            arc = (arc << 7) | (b & 0x7F);
            if ((b & 0x80) == 0) {
                arcs[idx++] = arc;
                arc = 0;
            }
        }
        return arcs;
    }
}
