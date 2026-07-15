package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation;

import org.restcomm.protocols.ss7.map.api.MAPException;

import java.io.Serializable;

/**
 *
 <code>
 NR-TA-Id ::= OCTET STRING (SIZE (6))
 -- Octets are coded as described in 3GPP TS 38.413 [153].
 </code>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface NRTAId extends Serializable {

    byte[] getData();

    int getMCC() throws MAPException;

    int getMNC() throws MAPException;

    int getNrTAC() throws MAPException;
}

