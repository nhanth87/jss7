package org.restcomm.protocols.ss7.map.api.service.lsm;

/**
 <code>
  TerminationCause ::= ENUMERATED {
   normal (0),
   errorundefined (1),
   internalTimeout (2),
   congestion (3),
   mt-lrRestart (4),
   privacyViolation (5),
   ...,
   shapeOfLocationEstimateNotSupported (6),
   subscriberTermination (7),
   uETermination (8),
   networkTermination (9) }

  -- mt-lrRestart shall be used to trigger the GMLC to restart the location procedure,
  -- either because the sending node knows that the terminal has moved under coverage
  -- of another MSC or SGSN (e.g. Send Identification received), or because the subscriber
  -- has been deregistered due to a Cancel Location received from HLR.
  --
  -- exception handling:
  -- an unrecognized value shall be treated the same as value 1 (errorundefined)

 </code>
 *
 * @author amit bhayani
 *
 */
public enum TerminationCause {

    normal(0), errorUndefined(1), internalTimeout(2), congestion(3), mtlrRestart(4), privacyViolation(5),
    shapeOfLocationEstimateNotSupported (6), subscriberTermination (7), uETermination (8), networkTermination (9);

    private final int cause;

    TerminationCause(int cause) {
        this.cause = cause;
    }

    public int getCause() {
        return this.cause;
    }

    public static TerminationCause getTerminationCause(int cause) {
        switch (cause) {
            case 0:
                return normal;
            case 1:
                return errorUndefined;
            case 2:
                return internalTimeout;
            case 3:
                return congestion;
            case 4:
                return mtlrRestart;
            case 5:
                return privacyViolation;
            case 6:
                return shapeOfLocationEstimateNotSupported;
            case 7:
                return subscriberTermination;
            case 8:
                return uETermination;
            case 9:
                return networkTermination;
            default:
                return null;
        }
    }
}
