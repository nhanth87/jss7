package org.restcomm.protocols.ss7.tcap.asn;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.BerCursor;
import org.mobicents.protocols.asn.BerSlice;
import org.mobicents.protocols.asn.BerTag;
import org.restcomm.protocols.ss7.tcap.asn.comp.Component;
import org.restcomm.protocols.ss7.tcap.asn.comp.GeneralProblemType;
import org.restcomm.protocols.ss7.tcap.asn.comp.InvokeProblemType;
import org.restcomm.protocols.ss7.tcap.asn.comp.OperationCode;
import org.restcomm.protocols.ss7.tcap.asn.comp.ProblemType;
import org.restcomm.protocols.ss7.tcap.asn.comp.ReturnErrorProblemType;
import org.restcomm.protocols.ss7.tcap.asn.comp.ReturnResultProblemType;

/**
 * Supplemental component decoders split from {@link BerComponentDecoder}
 * to keep files under ~300 lines for agent readability.
 *
 * <p>Covers:
 * <ul>
 *   <li>ReturnResultNotLast (RRNL) – tag [7]</li>
 *   <li>ReturnError – tag [3]</li>
 *   <li>Reject – tag [4]</li>
 * </ul>
 */
final class BerComponentDecoderExtra {

    private BerComponentDecoderExtra() {}

    // ==================================================================
    // RETURN RESULT NOT LAST [7] IMPLICIT SEQUENCE
    // ==================================================================

    /**
     * <pre>
     * ReturnResultNotLast ::= [7] IMPLICIT SEQUENCE {
     *   invokeId   InvokeIdType,
     *   result     SEQUENCE { operationCode  OperationCode, parameter  ANY } OPTIONAL
     * }
     * </pre>
     */
    static Component decodeReturnResultNotLast(BerCursor outer) throws AsnException {
        ReturnResultImpl rrnl = new ReturnResultImpl();
        BerCursor body = outer.openConstructed();
        try {
            body.readTag();
            BerComponentDecoder.assertUniversalPrimitive(body, BerTag.INTEGER, "RRNL.invokeId");
            rrnl.setInvokeId(Long.valueOf(body.readInt32()));
            body.skipValue();
            if (!body.hasMore()) return finish(outer, rrnl);

            body.readTag();
            if (body.tagClass() == BerTag.UNIVERSAL && body.tag() == BerTag.SEQUENCE) {
                BerCursor resultBody = body.openConstructed();
                try {
                    resultBody.readTag();
                    OperationCode opCode = BerComponentDecoder.decodeOperationCode(resultBody);
                    rrnl.setOperationCode(opCode);
                    resultBody.skipValue();
                    if (resultBody.hasMore()) {
                        resultBody.readTag();
                        rrnl.setParameter(BerComponentDecoder.decodeParameter(resultBody));
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
        return finish(outer, rrnl);
    }

    // ==================================================================
    // RETURN ERROR [3] IMPLICIT SEQUENCE
    // ==================================================================

    /**
     * <pre>
     * ReturnError ::= [3] IMPLICIT SEQUENCE {
     *   invokeId  InvokeIdType,
     *   errorCode ErrorCode,
     *   parameter ANY OPTIONAL
     * }
     * </pre>
     */
    static Component decodeReturnError(BerCursor outer) throws AsnException {
        ReturnErrorImpl error = new ReturnErrorImpl();
        BerCursor body = outer.openConstructed();
        try {
            body.readTag();
            BerComponentDecoder.assertUniversalPrimitive(body, BerTag.INTEGER, "ReturnError.invokeId");
            error.setInvokeId(Long.valueOf(body.readInt32()));
            body.skipValue();
            if (!body.hasMore()) return finish(outer, error);

            body.readTag();
            error.setErrorCode(BerComponentDecoder.decodeErrorCode(body));
            body.skipValue();
            if (!body.hasMore()) return finish(outer, error);

            body.readTag();
            error.setParameter(BerComponentDecoder.decodeParameter(body));
            body.skipValue();
        } finally {
            body.release();
        }
        return finish(outer, error);
    }

    // ==================================================================
    // REJECT [4] IMPLICIT SEQUENCE
    // ==================================================================

    /**
     * <pre>
     * Reject ::= [4] IMPLICIT SEQUENCE {
     *   invokeId   InvokeIdType | NULL,
     *   problem    Problem
     * }
     *    Problem ::= CHOICE {
     *      generalProblem  [0] IMPLICIT GeneralProblem,
     *      invokeProblem   [1] IMPLICIT InvokeProblem,
     *      returnResultProblem [2] IMPLICIT ReturnResultProblem,
     *      returnErrorProblem  [3] IMPLICIT ReturnErrorProblem
     *    }
     * </pre>
     */
    static Component decodeReject(BerCursor outer) throws AsnException {
        RejectImpl reject = new RejectImpl();
        BerCursor body = outer.openConstructed();
        try {
            body.readTag();
            if (body.tagClass() == BerTag.UNIVERSAL && body.tag() == BerTag.NULL) {
                reject.setInvokeId(null);
            } else {
                BerComponentDecoder.assertUniversalPrimitive(body, BerTag.INTEGER, "Reject.invokeId");
                reject.setInvokeId(Long.valueOf(body.readInt32()));
            }
            body.skipValue();
            if (!body.hasMore()) return finish(outer, reject);

            body.readTag();
            BerComponentDecoder.assertContext(body, "Reject.problem");
            int problemTag = body.tag(); // 0=general, 1=invoke, 2=rr, 3=re
            int problemValue = body.readInt32();
            reject.setProblem(buildProblem(problemTag, problemValue));
            body.skipValue();
        } finally {
            body.release();
        }
        return finish(outer, reject);
    }

    // ==================================================================
    // PROBLEM BUILDER
    // ==================================================================

    private static ProblemImpl buildProblem(int tag, int value) throws AsnException {
        ProblemImpl p = new ProblemImpl();
        switch (tag) {
            case 0:
                p.setType(ProblemType.General);
                GeneralProblemType[] gpt = GeneralProblemType.values();
                p.setGeneralProblemType(value < gpt.length ? gpt[value] : GeneralProblemType.UnrecognizedComponent);
                break;
            case 1:
                p.setType(ProblemType.Invoke);
                InvokeProblemType[] ipt = InvokeProblemType.values();
                p.setInvokeProblemType(value < ipt.length ? ipt[value] : InvokeProblemType.UnrecognizedOperation);
                break;
            case 2:
                p.setType(ProblemType.ReturnResult);
                ReturnResultProblemType[] rrpt = ReturnResultProblemType.values();
                p.setReturnResultProblemType(value < rrpt.length ? rrpt[value] : ReturnResultProblemType.UnrecognizedInvokeID);
                break;
            case 3:
                p.setType(ProblemType.ReturnError);
                ReturnErrorProblemType[] rept = ReturnErrorProblemType.values();
                p.setReturnErrorProblemType(value < rept.length ? rept[value] : ReturnErrorProblemType.UnrecognizedInvokeID);
                break;
            default:
                throw new AsnException("Unknown problem tag: " + tag);
        }
        return p;
    }

    // ==================================================================
    // HELPERS
    // ==================================================================

    private static Component finish(BerCursor outer, Component comp) {
        outer.skipValue();
        return comp;
    }
}
