package org.restcomm.protocols.ss7.map.api.service.lsm;

import java.io.Serializable;

/**
 *
 <code>
 AreaEventInfo ::= SEQUENCE {
  areaDefinition [0] AreaDefinition,
  occurrenceInfo [1] OccurrenceInfo OPTIONAL,
  intervalTime   [2] IntervalTime   OPTIONAL,
 ...}
 </code>
 *
 * @author amit bhayani
 *
 */
public interface AreaEventInfo extends Serializable {

    /**
     * AreaDefinition ::= SEQUENCE { areaList [0] AreaList, ...}
     * AreaList ::= SEQUENCE SIZE (1..10) OF Area
     */
    AreaDefinition getAreaDefinition();

    /**
     * OccurrenceInfo ::= ENUMERATED { oneTimeEvent (0), multipleTimeEvent (1), ...}
     */
    OccurrenceInfo getOccurrenceInfo();

    /**
     * IntervalTime ::= INTEGER (1..32767) -- minimum interval time between area reports in seconds
     */
    Integer getIntervalTime();
}
