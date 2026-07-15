package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement;

import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;

import java.io.Serializable;

/**
<code>
 EDRX-Cycle-Length ::= SEQUENCE {
   rat-Type                [0] Used-RAT-Type,
   eDRX-Cycle-Length-Value [1] EDRX-Cycle-Length-Value,
 }
 -- The eDRX-Cycle-Length contains the subscribed eDRX-Cycle-Length applicable to a
 -- a specific RAT Type.
</code>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface EDRXCycleLength extends Serializable {

    UsedRATType getUsedRATType();

    EDRXCycleLengthValue getEDRXCycleLengthValue();
}
