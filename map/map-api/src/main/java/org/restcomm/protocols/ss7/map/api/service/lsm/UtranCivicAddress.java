package org.restcomm.protocols.ss7.map.api.service.lsm;

import java.io.Serializable;

/**
 <code>
 UtranCivicAddress ::= OCTET STRING
 -- Refers to the civic address defined in 3GPP TS 25.413.
 -- This is composed of the CivicAddress only as defined in 3GPP TS 25.413.

 9.2.3.59 Civic Address
 This information element contains a location estimate for the target UE expressed as a Civic address.
 IE/Group Name  Presence Range  IE type and reference   Semantics description
 Civic Address  O               OCTET STRING            This IE contains a UTF-8 encoded PIDF-LO XML document
                                                        as defined in IETF RFC 4119 [69].
                                                        The document shall only contain a civic address using the namespaces
                                                        "urn:ietf:params:xml:ns:pidf:geopriv10:civicAddr" per IETF RFC 5139 and
                                                        "urn:ietf:params:xml:ns:pidf:geopriv10:civicAddr:ext" per IETF RFC 6848.
 </code>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public interface UtranCivicAddress extends Serializable {

    byte[] getData();
}
