package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement;

import java.util.ArrayList;

import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.MobilityMessage;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSCode;

/**
 *
<code>
MAP V1-2-3:

MAP V2: DeleteSubscriberData ::= OPERATION
--Timer m
ARGUMENT deleteSubscriberDataArg DeleteSubscriberDataArg
RESULT deleteSubscriberDataRes DeleteSubscriberDataRes
-- optional
-- deleteSubscriberDataRes must be absent in version 1
ERRORS { DataMissing, UnexpectedDataValue, UnidentifiedSubscriber}

 MAP V2:
 DeleteSubscriberDataArg ::= SEQUENCE {
 imsi                            [0] IMSI,
 basicServiceList                [1] BasicServiceList OPTIONAL,
 ss-List                         [2] SS-List OPTIONAL,
 roamingRestrictionDueToUnsupportedFeature [4] NULL OPTIONAL,
 -- roamingRestrictionDueToUnsupportedFeature must be absent
 -- in version 1
 regionalSubscriptionIdentifier  [5] ZoneCode OPTIONAL,
 -- regionalSubscriptionIdentifier must be absent in version 1
 ...

 MAP V3: deleteSubscriberData OPERATION ::= {
 --Timer m
 ARGUMENT DeleteSubscriberDataArg
 RESULT DeleteSubscriberDataRes
 --optional
 ERRORS { dataMissing | unexpectedDataValue | unidentifiedSubscriber}
 CODE local:8
 }

MAP V3:
DeleteSubscriberDataArg ::= SEQUENCE {
  imsi                                            [0] IMSI,
  basicServiceList                                [1] BasicServiceList OPTIONAL,
  -- The exception handling for reception of unsupported/not allocated
  -- basicServiceCodes is defined in section 6.8.2
  ss-List                                         [2] SS-List OPTIONAL,
  roamingRestrictionDueToUnsupportedFeature       [4] NULL OPTIONAL,
  regionalSubscriptionIdentifier                  [5] ZoneCode OPTIONAL,
  vbsGroupIndication                              [7] NULL OPTIONAL,
  vgcsGroupIndication                             [8] NULL OPTIONAL,
  camelSubscriptionInfoWithdraw                   [9] NULL OPTIONAL,
  extensionContainer                              [6] ExtensionContainer OPTIONAL,
  ...,
  gprsSubscriptionDataWithdraw                    [10] GPRSSubscriptionDataWithdraw OPTIONAL,
  roamingRestrictedInSgsnDueToUnsuppportedFeature [11] NULL OPTIONAL,
  lsaInformationWithdraw                          [12] LSAInformationWithdraw OPTIONAL,
  gmlc-ListWithdraw                               [13] NULL OPTIONAL,
  istInformationWithdraw                          [14] NULL OPTIONAL,
  specificCSI-Withdraw                            [15] SpecificCSI-Withdraw OPTIONAL,
  chargingCharacteristicsWithdraw                 [16] NULL OPTIONAL,
  stn-srWithdraw                                  [17] NULL OPTIONAL,
  epsSubscriptionDataWithdraw                     [18] EPS-SubscriptionDataWithdraw OPTIONAL,
  apn-oi-replacementWithdraw                      [19] NULL OPTIONAL,
  csg-SubscriptionDeleted                         [20] NULL OPTIONAL,
  subscribedPeriodicTAU-RAU-TimerWithdraw         [22] NULL OPTIONAL,
  subscribedPeriodicLAU-TimerWithdraw             [23] NULL OPTIONAL,
  subscribed-vsrvccWithdraw                       [21] NULL OPTIONAL,
  vplmn-Csg-SubscriptionDeleted                   [24] NULL OPTIONAL,
  additionalMSISDN-Withdraw                       [25] NULL OPTIONAL,
  cs-to-ps-SRVCC-Withdraw                         [26] NULL OPTIONAL,
  imsiGroupIdList-Withdraw                        [27] NULL OPTIONAL,
  userPlaneIntegrityProtectionWithdraw            [28] NULL OPTIONAL,
  dl-Buffering-Suggested-Packet-Count-Withdraw    [29] NULL OPTIONAL,
  ue-UsageTypeWithdraw                            [30] NULL OPTIONAL,
  reset-idsWithdraw                               [31] NULL OPTIONAL,
  iab-OperationWithdraw                           [32] NULL OPTIONAL }
 }
}

BasicServiceList ::= SEQUENCE SIZE (1..70) OF Ext-BasicServiceCode

SS-List ::= SEQUENCE SIZE (1..30) OF SS-Code
</code>
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com">Fernando Mendioroz</a>
 */
public interface DeleteSubscriberDataRequest extends MobilityMessage {

    IMSI getImsi();

    ArrayList<ExtBasicServiceCode> getBasicServiceList();

    ArrayList<SSCode> getSsList();

    boolean getRoamingRestrictionDueToUnsupportedFeature();

    ZoneCode getRegionalSubscriptionIdentifier();

    boolean getVbsGroupIndication();

    boolean getVgcsGroupIndication();

    boolean getCamelSubscriptionInfoWithdraw();

    MAPExtensionContainer getExtensionContainer();

    GPRSSubscriptionDataWithdraw getGPRSSubscriptionDataWithdraw();

    boolean getRoamingRestrictedInSgsnDueToUnsuppportedFeature();

    LSAInformationWithdraw getLSAInformationWithdraw();

    boolean getGmlcListWithdraw();

    boolean getIstInformationWithdraw();

    SpecificCSIWithdraw getSpecificCSIWithdraw();

    boolean getChargingCharacteristicsWithdraw();

    boolean getStnSrWithdraw();

    EPSSubscriptionDataWithdraw getEPSSubscriptionDataWithdraw();

    boolean getApnOiReplacementWithdraw();

    boolean getCsgSubscriptionDeleted();

    boolean getSubscribedPeriodicTAURAUTimerWithdraw();

    boolean getSubscribedPeriodicLAUTimerWithdraw();

    boolean getSubscribedVsrvccWithdraw();

    boolean getVplmnCsgSubscriptionDeleted();

    boolean getAdditionalMSISDNWithdraw();

    boolean getCsToPsSRVCCWithdraw();

    boolean getImsiGroupIdListWithdraw();

    boolean getUserPlaneIntegrityProtectionWithdraw();

    boolean getDlBufferingSuggestedPacketCountWithdraw();

    boolean getUeUsageTypeWithdraw();

    boolean getResetIdsWithdraw();

    boolean getIabOperationWithdraw();
}
