package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation;

import java.io.Serializable;

/**
 <code>
 RequestedNodes ::= BIT STRING {
 mme (0),
 sgsn (1)} (SIZE (1..8))
 -- Other bits than listed above shall be discarded.
 </code>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface RequestedNodes extends Serializable {

    boolean getMme();

    boolean getSgsn();
}
