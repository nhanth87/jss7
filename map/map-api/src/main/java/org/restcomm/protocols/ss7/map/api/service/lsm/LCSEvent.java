package org.restcomm.protocols.ss7.map.api.service.lsm;

/**
 <code>
 LCS-Event ::= ENUMERATED {
  emergencyCallOrigination (0),
  emergencyCallRelease (1),
  mo-lr (2),
  ...,
  deferredmt-lrResponse (3),
  deferredmo-lrTTTPInitiation (4),
  emergencyCallHandover (5) }

 -- deferredmt-lrResponse is applicable to the delivery of a location estimate
 -- for an LDR initiated earlier by either the network (via an MT-LR activate deferred location)
 -- or the UE (via a deferred MO-LR TTTP initiation)
 -- exception handling:
 -- a SubscriberLocationReport-Arg containing an unrecognized LCS-Event
 -- shall be rejected by a receiver with a return error cause of unexpected data value.
 </code>
 *
 * @author amit bhayani
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public enum LCSEvent {

    emergencyCallOrigination(0), emergencyCallRelease(1), molr(2), deferredmtlrResponse(3), deferredmolrTTTPInitiation (4), emergencyCallHandover (5);

    private final int event;

    private LCSEvent(int event) {
        this.event = event;
    }

    public int getEvent() {
        return this.event;
    }

    public static LCSEvent getLCSEvent(int event) {
        switch (event) {
            case 0:
                return emergencyCallOrigination;
            case 1:
                return emergencyCallRelease;
            case 2:
                return molr;
            case 3:
                return deferredmtlrResponse;
            case 4:
                return deferredmolrTTTPInitiation;
            case 5:
                return emergencyCallHandover;
            default:
                return null;
        }
    }
}
