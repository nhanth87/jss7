package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation;

import java.io.Serializable;

import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.NetworkNodeDiameterAddress;

/**
 *
<code>
ModificationRequestFor-IP-SM-GW-Data ::= SEQUENCE {
  modifyRegistrationStatus   [0] ModificationInstruction OPTIONAL,
  extensionContainer         [1] ExtensionContainer OPTIONAL,
  ip-sm-gw-DiameterAddress   [2] NetworkNodeDiameterAddress OPTIONAL
 -- ip-sm-gw-DiameterAddress may be present when ModificationInstruction is "activate"
 ...
}
</code>
 *
 *
 * @author sergey vetyutnev
 *
 */
public interface ModificationRequestForIPSMGWData extends Serializable {

    ModificationInstruction getModifyRegistrationStatus();

    MAPExtensionContainer getExtensionContainer();

    NetworkNodeDiameterAddress getIpSmGwDiameterAddress();
}
