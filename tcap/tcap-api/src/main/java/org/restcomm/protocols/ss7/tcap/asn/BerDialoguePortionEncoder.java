package org.restcomm.protocols.ss7.tcap.asn;

import org.mobicents.protocols.asn.BerTag;
import org.mobicents.protocols.asn.BerWriter;

/**
 * Zero-copy DialoguePortion encoder using {@link BerWriter} (backward encode).
 *
 * <p>Covers:
 * <ul>
 *   <li>AARQ – TC-BEGIN open (dialogueRequest)</li>
 *   <li>AARE – TC-CONTINUE/TC-END response (dialogueResponse)</li>
 *   <li>ABRT – TC-ABORT (dialogueAbort)</li>
 * </ul>
 *
 * <p>All methods write into a {@link BerWriter} which the caller obtains via
 * {@link BerWriter#get()}. The result can be flushed to a {@code ByteBuf} or
 * {@code NettyAsnOutputStream}.
 */
public final class BerDialoguePortionEncoder {

    // OID: dialogue-as-id = {itu-t q 773 as(1) dialogue-as(1) version1(1)}
    static final byte[] DIALOGUE_AS_OID_ENCODED = {
        0x00, 0x11, (byte) 0x86, 0x05, 0x01, 0x01, 0x01
    };

    // BIT STRING version1: unusedBits=0, bit 0 set
    static final byte[] VERSION1_BITSTRING = {0x00, (byte) 0x80};

    // TAG aliases
    private static final int TAG_AARQ = 0x00;
    private static final int TAG_AARE = 0x01;
    private static final int TAG_ABRT = 0x04;

    private BerDialoguePortionEncoder() {}

    // ==================================================================
    // AARQ (TC-BEGIN open)
    // ==================================================================

    /**
     * Encode [APPLICATION 11] DialoguePortion containing AARQ-apdu.
     *
     * @param writer        {@link BerWriter#get()} from caller
     * @param appCtxOidEnc  encoded OID bytes for application context
     * @param userInfo      user-information bytes (MAP open data), null if none
     */
    public static void encodeAARQ(BerWriter writer,
                                   byte[] appCtxOidEnc,
                                   byte[] userInfo) {
        int markApp11 = writer.beginSequence(BerTag.APPLICATION, 0x0B);
        int markExt   = writer.beginSequence(BerTag.UNIVERSAL,   0x08);
        int markCtx0  = writer.beginSequence(BerTag.CONTEXT,     0x00);
        int markAARQ  = writer.beginSequence(BerTag.APPLICATION, TAG_AARQ);

        // [30] user-information OPTIONAL (innermost – write first)
        if (userInfo != null && userInfo.length > 0) {
            writer.writeOctetString(BerTag.CONTEXT, 0x1E,
                userInfo, 0, userInfo.length);
        }
        // [1] application-context-name OID
        writer.writeOctetString(BerTag.CONTEXT, 0x01,
            appCtxOidEnc, 0, appCtxOidEnc.length);
        // [0] protocol-version BIT STRING
        writer.writeOctetString(BerTag.CONTEXT, 0x00,
            VERSION1_BITSTRING, 0, VERSION1_BITSTRING.length);

        writer.endSequence(markAARQ, BerTag.APPLICATION, TAG_AARQ);
        writer.endSequence(markCtx0, BerTag.CONTEXT,     0x00);
        writer.writeOctetString(BerTag.UNIVERSAL, BerTag.OID,
            DIALOGUE_AS_OID_ENCODED, 0, DIALOGUE_AS_OID_ENCODED.length);
        writer.endSequence(markExt,   BerTag.UNIVERSAL,   0x08);
        writer.endSequence(markApp11, BerTag.APPLICATION, 0x0B);
    }

    // ==================================================================
    // AARE (TC-CONTINUE / TC-END response)
    // ==================================================================

    /**
     * Encode [APPLICATION 11] DialoguePortion containing AARE-apdu.
     *
     * @param writer       {@link BerWriter#get()} from caller
     * @param appCtxOidEnc encoded OID bytes
     * @param result       0=accepted, 1=reject-permanent
     * @param srcDiag      1=dialogue-service-user, 2=dialogue-service-provider
     * @param diagValue    diagnostic value (0=null, 1=no-reason, 2=app-ctx-not-supported)
     * @param userInfo     OPTIONAL user-information bytes
     */
    public static void encodeAARE(BerWriter writer,
                                   byte[] appCtxOidEnc,
                                   int result,
                                   int srcDiag,
                                   int diagValue,
                                   byte[] userInfo) {
        int markApp11 = writer.beginSequence(BerTag.APPLICATION, 0x0B);
        int markExt   = writer.beginSequence(BerTag.UNIVERSAL,   0x08);
        int markCtx0  = writer.beginSequence(BerTag.CONTEXT,     0x00);
        int markAARE  = writer.beginSequence(BerTag.APPLICATION, TAG_AARE);

        // [30] user-information OPTIONAL
        if (userInfo != null && userInfo.length > 0) {
            writer.writeOctetString(BerTag.CONTEXT, 0x1E,
                userInfo, 0, userInfo.length);
        }
        // [3] result-source-diagnostic
        {
            int markDiag = writer.beginSequence(BerTag.CONTEXT, 0x03);
            writer.writeInt32(BerTag.CONTEXT, srcDiag, diagValue);
            writer.endSequence(markDiag, BerTag.CONTEXT, 0x03);
        }
        // [2] result
        writer.writeInt32(BerTag.CONTEXT, 0x02, result);
        // [1] application-context-name
        writer.writeOctetString(BerTag.CONTEXT, 0x01,
            appCtxOidEnc, 0, appCtxOidEnc.length);
        // [0] protocol-version
        writer.writeOctetString(BerTag.CONTEXT, 0x00,
            VERSION1_BITSTRING, 0, VERSION1_BITSTRING.length);

        writer.endSequence(markAARE, BerTag.APPLICATION, TAG_AARE);
        writer.endSequence(markCtx0, BerTag.CONTEXT,     0x00);
        writer.writeOctetString(BerTag.UNIVERSAL, BerTag.OID,
            DIALOGUE_AS_OID_ENCODED, 0, DIALOGUE_AS_OID_ENCODED.length);
        writer.endSequence(markExt,   BerTag.UNIVERSAL,   0x08);
        writer.endSequence(markApp11, BerTag.APPLICATION, 0x0B);
    }

    // ==================================================================
    // ABRT (TC-ABORT)
    // ==================================================================

    /**
     * Encode [APPLICATION 11] DialoguePortion containing ABRT-apdu.
     *
     * @param writer      {@link BerWriter#get()} from caller
     * @param abortSource 0=dialogue-service-user, 1=dialogue-service-provider
     * @param userInfo    OPTIONAL
     */
    public static void encodeABRT(BerWriter writer,
                                   int abortSource,
                                   byte[] userInfo) {
        int markApp11 = writer.beginSequence(BerTag.APPLICATION, 0x0B);
        int markExt   = writer.beginSequence(BerTag.UNIVERSAL,   0x08);
        int markCtx0  = writer.beginSequence(BerTag.CONTEXT,     0x00);
        int markABRT  = writer.beginSequence(BerTag.APPLICATION, TAG_ABRT);

        if (userInfo != null && userInfo.length > 0) {
            writer.writeOctetString(BerTag.CONTEXT, 0x1E,
                userInfo, 0, userInfo.length);
        }
        // [0] abort-source
        writer.writeInt32(BerTag.CONTEXT, 0x00, abortSource);

        writer.endSequence(markABRT, BerTag.APPLICATION, TAG_ABRT);
        writer.endSequence(markCtx0, BerTag.CONTEXT,     0x00);
        writer.writeOctetString(BerTag.UNIVERSAL, BerTag.OID,
            DIALOGUE_AS_OID_ENCODED, 0, DIALOGUE_AS_OID_ENCODED.length);
        writer.endSequence(markExt,   BerTag.UNIVERSAL,   0x08);
        writer.endSequence(markApp11, BerTag.APPLICATION, 0x0B);
    }
}
