package org.restcomm.protocols.ss7.map.api.service.sms;

import java.io.Serializable;

import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.lsm.AdditionalNumber;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.NetworkNodeDiameterAddress;

/**
 *
<code>
 LocationInfoWithLMSI ::= SEQUENCE {
 networkNode-Number                         [1] ISDN-AddressString,
 lmsi                                       LMSI                           OPTIONAL,
 extensionContainer                         ExtensionContainer             OPTIONAL,
 ...,
 gprsNodeIndicator                          [5] NULL                       OPTIONAL,
 -- gprsNodeIndicator is set only if the SGSN number is sent as the Network Node Number
 additional-Number                          [6] Additional-Number          OPTIONAL,
 networkNodeDiameterAddress                 [7] NetworkNodeDiameterAddress OPTIONAL,
 additionalNetworkNodeDiameterAddress       [8] NetworkNodeDiameterAddress OPTIONAL,
 thirdNumber                                [9] Additional-Number          OPTIONAL,
 thirdNetworkNodeDiameterAddress           [10] NetworkNodeDiameterAddress OPTIONAL,
 imsNodeIndicator                          [11] NULL                       OPTIONAL,
 -- gprsNodeIndicator and imsNodeIndicator shall not both be present.
 -- additionalNumber and thirdNumber shall not both contain the same type of number.
 smsf-3gpp-Number                          [12] ISDN-AddressString         OPTIONAL,
 smsf-3gpp-DiameterAddress                 [13] NetworkNodeDiameterAddress OPTIONAL,
 smsf-non-3gpp-Number                      [14] ISDN-AddressString         OPTIONAL,
 smsf-non-3gpp-DiameterAddress             [15] NetworkNodeDiameterAddress OPTIONAL,
 smsf-3gpp-address-indicator               [16] NULL                       OPTIONAL,
 smsf-non-3gpp-address-indicator           [17] NULL                       OPTIONAL
 --
 -- If smsf-supportIndicator was not included in the request, in RoutingInfoForSM-Arg,
 -- then smsf-3gpp Number/DiameterAddress, smsf-non-3gpp Number/DiameterAddress and
 -- smsf-address-indicator and smsf-non-3gpp-address-indicator shall be absent.
 --
 -- If smsf-3gpp-address-indicator is present, it indicates that the networkNode-Number
 -- (and networkNodeDiameterAddress, if present) contains the address of an SMSF for
 -- 3GPP access.
 --
 -- If smsf-non-3gpp-address-indicator is present, it indicates that the
 -- networkNode-Number (and networkNodeDiameterAddress, if present) contains the
 -- address of an SMSF for non 3GPP access.
 --
 -- At most one of gprsNodeIndicator, imsNodeIndicator, smsf-3gpp-address-indicator
 -- and smsf-non-3gpp-address-indicator shall be present. Absence of all these
 -- indicators indicate that the networkNode-Number (and networkNodeDiameterAddress,
 -- if present) contains the address of an MSC/MME.
}
</code>
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public interface LocationInfoWithLMSI extends Serializable {

    ISDNAddressString getNetworkNodeNumber();

    LMSI getLMSI();

    MAPExtensionContainer getExtensionContainer();

    boolean getGprsNodeIndicator();

    AdditionalNumber getAdditionalNumber();

    NetworkNodeDiameterAddress getNetworkNodeDiameterAddress();

    NetworkNodeDiameterAddress getAdditionalNetworkNodeDiameterAddress();

    AdditionalNumber getThirdNumber();

    NetworkNodeDiameterAddress getThirdNetworkNodeDiameterAddress();

    boolean getImsNodeIndicator();

    ISDNAddressString getSmsf3gppNumber();

    NetworkNodeDiameterAddress getSmsf3gppDiameterAddress();

    ISDNAddressString getSmsfNon3gppNumber();

    NetworkNodeDiameterAddress getSmsfNon3gppDiameterAddress();

    boolean getSmsf3gppAddressIndicator();

    boolean getSmsfNon3gppAddressIndicator();
}
