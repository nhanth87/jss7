package org.restcomm.protocols.ss7.tcap.asn.comp;

import org.mobicents.protocols.asn.*;

/**
 * Problem ::= CHOICE {
 *     generalProblem      [0] IMPLICIT GeneralProblem,
 *     invokeProblem       [1] IMPLICIT InvokeProblem,
 *     returnResultProblem [2] IMPLICIT ReturnResultProblem,
 *     returnErrorProblem  [3] IMPLICIT ReturnErrorProblem
 * }
 *
 * GeneralProblem ::= INTEGER {
 *     unrecognisedComponent(0), mistypedComponent(1), badlyStructuredComponent(2)
 * }
 * InvokeProblem ::= INTEGER {
 *     duplicateInvocation(0), unrecognisedOperation(1),
 *     mistypedArgument(2), resourceLimitation(3),
 *     initiatingRelease(4), unrecognisedLinkedID(5),
 *     linkedResponseUnexpected(6), unexpectedLinkedOperation(7)
 * }
 * ReturnResultProblem ::= INTEGER {
 *     unrecognisedInvocation(0), resultResponseUnexpected(1), mistypedResult(2)
 * }
 * ReturnErrorProblem ::= INTEGER {
 *     unrecognisedInvocation(0), errorResponseUnexpected(1), unrecognisedError(2),
 *     unexpectedError(3), mistypedParameter(4)
 * }
 */
public final class ProblemCodec {

    public enum ProblemType {
        GENERAL(0), INVOKE(1), RETURN_RESULT(2), RETURN_ERROR(3);
        public final int tag;
        ProblemType(int tag) { this.tag = tag; }
        public static ProblemType fromTag(int tag) throws AsnException {
            for (ProblemType t : values())
                if (t.tag == tag) return t;
            throw new AsnException("Problem: unknown tag=" + tag);
        }
    }

    public static final class Result {
        public ProblemType type;
        public int         value;  // specific problem code
        public void reset() { type = null; value = -1; }
    }

    // ── DECODE ───────────────────────────────────────────────────────

    /**
     * Decode Problem CHOICE.
     * cursor.readTag() đã được gọi bởi caller.
     */
    public static void decode(BerCursor cursor, Result out)
            throws AsnException {
        if (cursor.tagClass() != BerTag.CONTEXT)
            throw new AsnException("Problem: expected CONTEXT");
        out.type  = ProblemType.fromTag(cursor.tag());
        out.value = cursor.readInt32();
        cursor.skipValue();
    }

    // ── ENCODE ───────────────────────────────────────────────────────

    public static void encode(BerWriter w, ProblemType type, int value) {
        // CHOICE: ghi trực tiếp [type.tag] IMPLICIT INTEGER
        w.writeInt32(BerTag.CONTEXT, type.tag, value);
    }

    private ProblemCodec() {}
}
