package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation;

import org.restcomm.protocols.ss7.map.api.MAPException;

import java.io.Serializable;

/**
 *
 <code>
 NR-CGI ::= OCTET STRING (SIZE (8))
  -- Octets are coded as described in 3GPP TS 38.413 [153].

 NR-CGI ::= SEQUENCE {
 pLMNIdentity       PLMNIdentity,
 nRCellIdentity     NRCellIdentity,
 iE-Extensions      ProtocolExtensionContainer { {NR-CGI-ExtIEs} } OPTIONAL,
 ...
 }
 </code>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface NRCellGlobalId extends Serializable {

    byte[] getData();

    int getMCC() throws MAPException;

    int getMNC() throws MAPException;

    long getNCI() throws MAPException;
}
