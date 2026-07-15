package org.restcomm.protocols.ss7.tcap.asn;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.BerCursor;
import org.mobicents.protocols.asn.BerSlice;
import org.mobicents.protocols.asn.BerTag;

/**
 * Supplemental DialoguePortion decoders split from {@link BerDialoguePortionDecoder}.
 *
 * <p>Covers:
 * <ul>
 *   <li>AARE-apdu [APPLICATION 1] – MAP TC-CONTINUE/TC-END response</li>
 *   <li>ABRT-apdu [APPLICATION 4] – dialogue abort</li>
 * </ul>
 */
final class BerDialoguePortionDecoderExtra {

    private BerDialoguePortionDecoderExtra() {}

    // ==================================================================
    // AARE-apdu [APPLICATION 1] — MAP TC-CONTINUE/TC-END response
    // ==================================================================

    /**
     * <pre>
     * AARE-apdu ::= [APPLICATION 1] IMPLICIT SEQUENCE {
     *   protocol-version         [0] BIT STRING DEFAULT {version1},
     *   application-context-name [1] OBJECT IDENTIFIER,
     *   result                   [2] Associate-result,
     *   result-source-diagnostic [3] Associate-source-diagnostic,
     *   user-information         [30] OPTIONAL
     * }
     *
     * Associate-result: INTEGER { accepted(0), reject-permanent(1) }
     * Associate-source-diagnostic: CHOICE {
     *   dialogue-service-user     [1] INTEGER,
     *   dialogue-service-provider [2] INTEGER
     * }
     * </pre>
     */
    static void decodeAARE(BerCursor body,
                           BerDialoguePortionDecoder.DialoguePDU out)
            throws AsnException {
        out.version1 = true;
        try {
            while (body.hasMore()) {
                body.readTag();
                if (body.tagClass() != BerTag.CONTEXT) {
                    body.skipValue();
                    continue;
                }
                switch (body.tag()) {
                    case BerDialoguePortionDecoder.TAG_PROTOCOL_VERSION: // [0]
                        body.skipValue();
                        break;

                    case BerDialoguePortionDecoder.TAG_APP_CONTEXT_NAME: { // [1]
                        BerSlice oidSlice = body.getOctetStringSlice();
                        out.appContextOid = BerDialoguePortionDecoder.decodeOidArcs(oidSlice);
                        body.skipValue();
                        break;
                    }

                    case BerDialoguePortionDecoder.TAG_RESULT: // [2]
                        out.result = body.readInt32();
                        body.skipValue();
                        break;

                    case BerDialoguePortionDecoder.TAG_RESULT_DIAG: { // [3]
                        BerCursor diagCursor = body.openConstructed();
                        try {
                            diagCursor.readTag();
                            out.resultSourceDiag = diagCursor.tag(); // 1 or 2
                            out.diagnosticValue = diagCursor.readInt32();
                            diagCursor.skipValue();
                        } finally {
                            diagCursor.release();
                        }
                        body.skipValue();
                        break;
                    }

                    case BerDialoguePortionDecoder.TAG_USER_INFO: // [30] LAZY
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
    // ABRT-apdu [APPLICATION 4] — dialogue abort
    // ==================================================================

    /**
     * <pre>
     * ABRT-apdu ::= [APPLICATION 4] IMPLICIT SEQUENCE {
     *   abort-source      [0] IMPLICIT INTEGER,
     *   user-information  [30] OPTIONAL
     * }
     * abort-source: dialogue-service-user(0) | dialogue-service-provider(1)
     * </pre>
     */
    static void decodeABRT(BerCursor body,
                           BerDialoguePortionDecoder.DialoguePDU out)
            throws AsnException {
        try {
            while (body.hasMore()) {
                body.readTag();
                if (body.tagClass() != BerTag.CONTEXT) {
                    body.skipValue();
                    continue;
                }
                if (body.tag() == 0x00) {
                    out.abortSource = body.readInt32();
                } else if (body.tag() == BerDialoguePortionDecoder.TAG_USER_INFO) {
                    out.userInfoSlice = body.getOctetStringSlice();
                }
                body.skipValue();
            }
        } finally {
            body.release();
        }
    }
}
