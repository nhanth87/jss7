package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement;

import java.io.Serializable;

/**
<code>
 EDRX-Cycle-Length-Value ::= OCTET STRING (SIZE (1))
   -- The EDRX-Cycle-Length-Value shall be encoded as specified in clause 7.3.216 of 3GPP TS 29.272.
</code>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface EDRXCycleLengthValue extends Serializable {

    byte[] getData();
}
