package org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery;

import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;

import java.io.Serializable;

/**
 *
 <code>
  SendingNode-Number ::= CHOICE {
    hlr-Number         ISDN-AddressString,
    css-Number     [1] ISDN-AddressString
 }
 </code>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface SendingNodeNumber extends Serializable {

    ISDNAddressString getHlrNumber();

    ISDNAddressString getCssNumber();
}
