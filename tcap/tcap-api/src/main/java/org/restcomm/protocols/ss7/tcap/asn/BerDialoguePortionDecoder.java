package org.restcomm.protocols.ss7.tcap.asn;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.BerCursor;
import org.mobicents.protocols.asn.BerSlice;
import org.mobicents.protocols.asn.BerTag;

/**
 * Lazy decoder for TCAP DialoguePortion (Q.773) using {@link BerCursor}.
 *
 * <p>Wire format:
 * <pre>
 *   DialoguePortion ::= [APPLICATION 11] EXPLICIT ExternalPDU
 *
 *   ExternalPDU ::= [UNIVERSAL 8] IMPLICIT SEQUENCE {
 *       oid     OBJECT IDENTIFIER,
 *       dialog  [0] EXPLICIT DialoguePDU
 *   }
 *
 *   DialoguePDU ::= CHOICE {
 *       dialogueRequest  AARQ-apdu,   -- [APPLICATION 0]
 *       dialogueResponse AARE-apdu,   -- [APPLICATION 1]
 *       dialogueAbort    ABRT-apdu    -- [APPLICATION 4]
 *   }
 * </pre>
 *
 * <p>Split for agent readability: this file covers main entry + AARQ decode.
 * See {@link BerDialoguePortionDecoderExtra} for AARE + ABRT.
 */
public final class BerDialoguePortionDecoder {

    // ── Tag constants ────────────────────────────────────────────────
    static final int TAG_EXTERNAL_PDU     = 0x08; // [UNIVERSAL 8]
    public static final int TAG_AARQ      = 0x00; // [APPLICATION 0]
    public static final int TAG_AARE      = 0x01; // [APPLICATION 1]
    public static final int TAG_ABRT      = 0x04; // [APPLICATION 4]

    // Context tags inside AARQ/AARE/ABRT
    static final int TAG_PROTOCOL_VERSION = 0x00;
    static final int TAG_APP_CONTEXT_NAME = 0x01;
    static final int TAG_RESULT           = 0x02;
    static final int TAG_RESULT_DIAG      = 0x03;
    static final int TAG_USER_INFO        = 0x1E;

    // OID: dialogue-as-id = {itu-t q 773 as(1) dialogue-as(1) version1(1)}
    // Encoded: 00 11 86 05 01 01 01
    static final byte[] DIALOGUE_AS_OID = {
        0x00, 0x11, (byte) 0x86, 0x05, 0x01, 0x01, 0x01
    };

    private BerDialoguePortionDecoder() {}

    // ==================================================================
    // RESULT HOLDER
    // ==================================================================

    /**
     * Parsed DialoguePDU result. Reusable per thread – no allocation on hot path.
     */
    public static final class DialoguePDU {
        public int pduType = -1;        // TAG_AARQ | TAG_AARE | TAG_ABRT
        public long[] appContextOid;   // parsed OID arcs
        public boolean version1 = true;
        // AARE-specific
        public int result;
        public int resultSourceDiag;
        public int diagnosticValue;
        // ABRT-specific
        public int abortSource;
        // Lazy user-information slice
        public BerSlice userInfoSlice;

        public void reset() {
            pduType = -1;
            appContextOid = null;
            version1 = true;
            result = 0;
            resultSourceDiag = 0;
            diagnosticValue = 0;
            abortSource = 0;
            userInfoSlice = null;
        }
    }

    // ThreadLocal holder – avoid allocation
    private static final ThreadLocal<DialoguePDU> TL_PDU =
        ThreadLocal.withInitial(DialoguePDU::new);

    // ==================================================================
    // ENTRY POINT
    // ==================================================================

    /**
     * Decode DialoguePortion from a BerSlice (raw bytes of [APPLICATION 11] content).
     *
     * @param slice  content of the DialoguePortion TLV (after APPLICATION 11 header)
     * @return       DialoguePDU (ThreadLocal – copy if you need to keep it across threads)
     */
    public static DialoguePDU decode(BerSlice slice) throws AsnException {
        DialoguePDU pdu = TL_PDU.get();
        pdu.reset();

        BerCursor outer = BerCursor.wrap(slice.toByteArray(), 0, slice.length());
        try {
            outer.readTag();
            if (outer.tagClass() != BerTag.UNIVERSAL || outer.tag() != TAG_EXTERNAL_PDU) {
                throw new AsnException(
                    "DialoguePortion: expected UNIVERSAL 8 (ExternalPDU), got class="
                    + outer.tagClass() + " tag=" + outer.tag());
            }
            BerCursor extBody = outer.openConstructed();
            try {
                decodeExternalPDU(extBody, pdu);
            } finally {
                extBody.release();
            }
        } finally {
            outer.release();
        }
        return pdu;
    }

    // ==================================================================
    // ExternalPDU content
    // ==================================================================

    private static void decodeExternalPDU(BerCursor body, DialoguePDU out)
            throws AsnException {
        // ── oid OBJECT IDENTIFIER ────────────────────────────────────
        body.readTag();
        if (body.tagClass() != BerTag.UNIVERSAL || body.tag() != BerTag.OID) {
            throw new AsnException("ExternalPDU: expected OID");
        }
        BerSlice oidSlice = body.getOctetStringSlice();
        // Optional: validate dialogue-as-id OID
        if (!oidSlice.equalsBytes(DIALOGUE_AS_OID)) {
            // Not structured dialogue OID – skip gracefully
        }
        body.skipValue();
        if (!body.hasMore()) return;

        // ── dialog [0] EXPLICIT DialoguePDU ─────────────────────────
        body.readTag();
        if (body.tagClass() != BerTag.CONTEXT || body.tag() != 0x00) {
            throw new AsnException("ExternalPDU: expected [0] EXPLICIT dialog");
        }
        BerCursor dialogWrapper = body.openConstructed();
        try {
            dialogWrapper.readTag();
            decodeDialoguePDU(dialogWrapper, out);
        } finally {
            dialogWrapper.release();
        }
        body.skipValue();
    }

    // ==================================================================
    // DialoguePDU CHOICE dispatch
    // ==================================================================

    private static void decodeDialoguePDU(BerCursor cursor, DialoguePDU out)
            throws AsnException {
        if (cursor.tagClass() != BerTag.APPLICATION) {
            throw new AsnException(
                "DialoguePDU: expected APPLICATION tag, got class=" + cursor.tagClass());
        }
        out.pduType = cursor.tag();
        switch (cursor.tag()) {
            case TAG_AARQ:
                decodeAARQ(cursor.openConstructed(), out);
                break;
            case TAG_AARE:
                BerDialoguePortionDecoderExtra.decodeAARE(cursor.openConstructed(), out);
                break;
            case TAG_ABRT:
                BerDialoguePortionDecoderExtra.decodeABRT(cursor.openConstructed(), out);
                break;
            default:
                throw new AsnException("DialoguePDU: unknown tag=" + cursor.tag());
        }
        cursor.skipValue();
    }

    // ==================================================================
    // AARQ-apdu [APPLICATION 0] — MAP TC-BEGIN open
    // ==================================================================

    /**
     * <pre>
     * AARQ-apdu ::= [APPLICATION 0] IMPLICIT SEQUENCE {
     *   protocol-version         [0] IMPLICIT BIT STRING DEFAULT {version1},
     *   application-context-name [1] OBJECT IDENTIFIER,
     *   user-information         [30] IMPLICIT SEQUENCE OF EXTERNAL OPTIONAL
     * }
     * </pre>
     */
    static void decodeAARQ(BerCursor body, DialoguePDU out) throws AsnException {
        out.version1 = true;
        try {
            while (body.hasMore()) {
                body.readTag();
                if (body.tagClass() != BerTag.CONTEXT) {
                    body.skipValue();
                    continue;
                }
                switch (body.tag()) {
                    case TAG_PROTOCOL_VERSION: { // [0] BIT STRING
                        BerSlice bsVer = body.getOctetStringSlice();
                        if (bsVer.length() >= 2) {
                            byte[] raw = bsVer.toByteArray();
                            out.version1 = (raw[1] & 0x80) != 0;
                        }
                        body.skipValue();
                        break;
                    }
                    case TAG_APP_CONTEXT_NAME: { // [1] OID
                        BerSlice oidSlice = body.getOctetStringSlice();
                        out.appContextOid = decodeOidArcs(oidSlice);
                        body.skipValue();
                        break;
                    }
                    case TAG_USER_INFO: // [30] LAZY – just save slice
                        out.userInfoSlice = body.getOctetStringSlice();
                        body.skipValue();
                        break;
                    default:
                        body.skipValue();
                }
            }
        } finally {
            body.release();
        }
    }

    // ==================================================================
    // OID decode (long[] arcs)
    // ==================================================================

    static long[] decodeOidArcs(BerSlice slice) {
        byte[] raw = slice.toByteArray();
        int count = 2;
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
