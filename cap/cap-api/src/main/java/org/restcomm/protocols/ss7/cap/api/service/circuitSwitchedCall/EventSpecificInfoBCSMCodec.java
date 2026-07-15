package org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall;

import org.mobicents.protocols.asn.*;

/**
 * EventSpecificInformationBCSM ::= CHOICE {
 *   -- O-BCSM events
 *   routeSelectFailureSpecificInfo  [2] IMPLICIT SEQUENCE { ... },
 *   oCalledPartyBusySpecificInfo    [3] IMPLICIT SEQUENCE { ... },
 *   oNoAnswerSpecificInfo           [4] IMPLICIT SEQUENCE { ... },
 *   oAnswerSpecificInfo             [5] IMPLICIT SEQUENCE { ... },
 *   oMidCallSpecificInfo            [6] IMPLICIT SEQUENCE { ... },
 *   oDisconnectSpecificInfo         [7] IMPLICIT SEQUENCE { ... },
 *   -- T-BCSM events
 *   tBusySpecificInfo               [8] IMPLICIT SEQUENCE { ... },
 *   tNoAnswerSpecificInfo           [9] IMPLICIT SEQUENCE { ... },
 *   tAnswerSpecificInfo             [10] IMPLICIT SEQUENCE { ... },
 *   tMidCallSpecificInfo            [11] IMPLICIT SEQUENCE { ... },
 *   tDisconnectSpecificInfo         [12] IMPLICIT SEQUENCE { ... },
 *   oTermSeizedSpecificInfo         [13] IMPLICIT SEQUENCE { ... },
 *   callAcceptedSpecificInfo        [14] IMPLICIT SEQUENCE { ... },
 *   oAbandonSpecificInfo            [15] IMPLICIT SEQUENCE { ... },
 *   -- Extended (CAP4)
 *   oChangeOfPositionSpecificInfo   [50] IMPLICIT SEQUENCE { ... },
 *   tChangeOfPositionSpecificInfo   [51] IMPLICIT SEQUENCE { ... }
 * }
 *
 * Strategy: tất cả alternatives đều là SEQUENCE.
 * Decode hot fields (cause, legID) eagerly; phần còn lại giữ BerSlice lazy.
 */
public final class EventSpecificInfoBCSMCodec {

    public enum EventType {
        ROUTE_SELECT_FAILURE(2),
        O_CALLED_PARTY_BUSY(3),
        O_NO_ANSWER(4),
        O_ANSWER(5),
        O_MID_CALL(6),
        O_DISCONNECT(7),
        T_BUSY(8),
        T_NO_ANSWER(9),
        T_ANSWER(10),
        T_MID_CALL(11),
        T_DISCONNECT(12),
        O_TERM_SEIZED(13),
        CALL_ACCEPTED(14),
        O_ABANDON(15),
        O_CHANGE_OF_POSITION(50),
        T_CHANGE_OF_POSITION(51);

        public final int tag;
        EventType(int tag) { this.tag = tag; }
        public static EventType fromTag(int tag) throws AsnException {
            for (EventType t : values())
                if (t.tag == tag) return t;
            throw new AsnException(
                "EventSpecificInfoBCSM: unknown tag=" + tag);
        }
    }

    public static final class Result {
        public EventType type;
        // Hot fields — eagerly decoded khi present
        public BerSlice causeSlice;        // [0] Cause (nhiều event có)
        public int      legId    = -1;     // [0] LegType (mid-call, disconnect)
        public boolean  busySubscriber;    // [0] tBusy
        public boolean  callForwarded;     // [1] tBusy
        // Raw slice cho toàn bộ SEQUENCE content (lazy)
        public BerSlice rawSlice;

        public void reset() {
            type           = null;
            causeSlice     = null;
            legId          = -1;
            busySubscriber = false;
            callForwarded  = false;
            rawSlice       = null;
        }
    }

    // ── DECODE ───────────────────────────────────────────────────────

    /**
     * Decode EventSpecificInformationBCSM CHOICE.
     * cursor.readTag() đã được gọi.
     */
    public static void decode(BerCursor cursor, Result out)
            throws AsnException {
        if (cursor.tagClass() != BerTag.CONTEXT)
            throw new AsnException(
                "EventSpecificInfoBCSM: expected CONTEXT");
        out.type = EventType.fromTag(cursor.tag());
        // Giữ raw slice TRƯỚC — để lazy decode toàn bộ nếu cần
        out.rawSlice = cursor.getOctetStringSlice();
        // Eager decode hot fields dựa theo event type
        switch (out.type) {
            case ROUTE_SELECT_FAILURE:
                decodeRouteSelectFailure(out);
                break;
            case O_CALLED_PARTY_BUSY:
            case T_BUSY:
                decodeBusy(out);
                break;
            case O_DISCONNECT:
            case T_DISCONNECT:
                decodeDisconnect(out);
                break;
            case O_NO_ANSWER:
            case T_NO_ANSWER:
                decodeNoAnswer(out);
                break;
            default:
                // Answer, MidCall, v.v. → giữ rawSlice, decode lazy
                break;
        }
        cursor.skipValue();
    }

    // routeSelectFailureSpecificInfo [2]:
    //   SEQUENCE { failureCause [0] IMPLICIT Cause OPTIONAL }
    private static void decodeRouteSelectFailure(Result out)
            throws AsnException {
        BerCursor body = out.rawSlice.cursor();
        try {
            while (body.hasMore()) {
                body.readTag();
                if (body.tagClass() == BerTag.CONTEXT && body.tag() == 0x00) {
                    out.causeSlice = body.getOctetStringSlice();
                }
                body.skipValue();
            }
        } finally {
            body.release();
        }
    }

    // oCalledPartyBusySpecificInfo [3] / tBusySpecificInfo [8]:
    //   SEQUENCE {
    //     busyCause          [0] IMPLICIT Cause OPTIONAL,
    //     callForwarded      [3] IMPLICIT NULL OPTIONAL,
    //     busySubscriberInfo [4] IMPLICIT BusySubscriberInfo OPTIONAL,
    //     ...
    //   }
    private static void decodeBusy(Result out) throws AsnException {
        BerCursor body = out.rawSlice.cursor();
        try {
            while (body.hasMore()) {
                body.readTag();
                if (body.tagClass() == BerTag.CONTEXT) {
                    switch (body.tag()) {
                        case 0x00: out.causeSlice = body.getOctetStringSlice(); break;
                        case 0x03: out.callForwarded = true; break;
                        case 0x04: out.busySubscriber = true; break;
                        default: break;
                    }
                }
                body.skipValue();
            }
        } finally {
            body.release();
        }
    }

    // oDisconnectSpecificInfo [7] / tDisconnectSpecificInfo [12]:
    //   SEQUENCE { releaseCause [0] IMPLICIT Cause OPTIONAL }
    private static void decodeDisconnect(Result out) throws AsnException {
        BerCursor body = out.rawSlice.cursor();
        try {
            while (body.hasMore()) {
                body.readTag();
                if (body.tagClass() == BerTag.CONTEXT && body.tag() == 0x00)
                    out.causeSlice = body.getOctetStringSlice();
                body.skipValue();
            }
        } finally {
            body.release();
        }
    }

    // oNoAnswerSpecificInfo / tNoAnswerSpecificInfo:
    //   SEQUENCE { callForwarded [0] IMPLICIT NULL OPTIONAL }
    private static void decodeNoAnswer(Result out) throws AsnException {
        BerCursor body = out.rawSlice.cursor();
        try {
            while (body.hasMore()) {
                body.readTag();
                if (body.tagClass() == BerTag.CONTEXT && body.tag() == 0x00)
                    out.callForwarded = true;
                body.skipValue();
            }
        } finally {
            body.release();
        }
    }

    // ── ENCODE ───────────────────────────────────────────────────────

    /** Encode routeSelectFailure */
    public static void encodeRouteSelectFailure(BerWriter w, byte[] cause) {
        int mark = w.beginSequence(BerTag.CONTEXT,
            EventType.ROUTE_SELECT_FAILURE.tag);
        if (cause != null)
            w.writeOctetString(BerTag.CONTEXT, 0x00, cause, 0, cause.length);
        w.endSequence(mark, BerTag.CONTEXT,
            EventType.ROUTE_SELECT_FAILURE.tag);
    }

    /** Encode oDisconnect / tDisconnect */
    public static void encodeDisconnect(BerWriter w, EventType type,
                                         byte[] releaseCause) {
        int mark = w.beginSequence(BerTag.CONTEXT, type.tag);
        if (releaseCause != null)
            w.writeOctetString(BerTag.CONTEXT, 0x00,
                releaseCause, 0, releaseCause.length);
        w.endSequence(mark, BerTag.CONTEXT, type.tag);
    }

    /** Encode từ raw slice (passthrough — không re-encode) */
    public static void encodeRaw(BerWriter w, EventType type,
                                  BerSlice rawSlice) {
        w.writeOctetString(BerTag.CONTEXT, type.tag,
            rawSlice.toByteArray(), 0, rawSlice.length());
    }

    private EventSpecificInfoBCSMCodec() {}
}
