package org.restcomm.protocols.ss7.map.api.service.lsm;

import java.io.Serializable;

/**
 *
 <code>
 LocationType ::= SEQUENCE {
  locationEstimateType      [0] LocationEstimateType,
  ...,
  deferredLocationEventType [1] DeferredLocationEventType OPTIONAL
 }
 </code>
 *
 * @author amit bhayani
 *
 */
public interface LocationType extends Serializable {

    LocationEstimateType getLocationEstimateType();

    DeferredLocationEventType getDeferredLocationEventType();

}
