package org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery;

import java.util.ArrayList;

import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.NetworkResource;
import org.restcomm.protocols.ss7.map.api.service.mobility.MobilityMessage;

/**
 *

 <code>

 MAP V1-2:
 reset OPERATION ::= {
  --Timer m
  ARGUMENT ResetArg
  CODE local:37
 }

 ResetArg ::= SEQUENCE {
  networkResource   NetworkResource OPTIONAL,
  -- networkResource must be present in version 1
  -- networkResource must be absent in version greater 1

  hlr-Number        ISDN-AddressString,
  hlr-List          HLR-List OPTIONAL,
  ...
 }

 ResetArg ::= SEQUENCE {
  sendingNodenumber          SendingNode-Number,
  hlr-List                   HLR-List                            OPTIONAL,
  -- The hlr-List parameter shall only be applicable for a restart of the HSS/HLR.
  extensionContainer         [0] ExtensionContainer              OPTIONAL,
  ...,
  reset-Id-List              [1] Reset-Id-List                   OPTIONAL,
  subscriptionData           [2] InsertSubscriberDataArg         OPTIONAL,
  subscriptionDataDeletion   [3] DeleteSubscriberDataArg         OPTIONAL
 }

 SendingNode-Number ::= CHOICE {
  hlr-Number ISDN-AddressString,
  css-Number [1] ISDN-AddressString
 }

 HLR-List ::= SEQUENCE SIZE (1..50) OF HLR-Id
 HLR-Id ::= IMSI
 -- leading digits of IMSI, i.e. (MCC, MNC, leading digits of
 -- MSIN) forming HLR Id defined in TS 3GPP TS 23.003 [17].

 Reset-Id-List ::= SEQUENCE SIZE (1..50) OF
  Reset-Id
 Reset-Id ::= OCTET STRING (SIZE (1..4))
 -- Reset-Ids shall be unique within the HPLMN.



 </code>
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com">Fernando Mendioroz</a>
 */
public interface ResetRequest extends MobilityMessage {

    NetworkResource getNetworkResource();

    ISDNAddressString getHlrNumber();

    ArrayList<IMSI> getHlrList();

    SendingNodeNumber getSendingNodenumber();

    MAPExtensionContainer getExtensionContainer();

    ArrayList<ResetId> getResetIdList();

    InsertSubscriberDataArgs getSubscriptionData();

    DeleteSubscriberDataArgs getSubscriptionDataDeletion();
}
