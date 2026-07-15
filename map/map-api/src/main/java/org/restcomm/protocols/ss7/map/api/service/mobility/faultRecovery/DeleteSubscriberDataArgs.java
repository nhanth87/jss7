package org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery;

import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.EPSSubscriptionDataWithdraw;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBasicServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.GPRSSubscriptionDataWithdraw;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAInformationWithdraw;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SpecificCSIWithdraw;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ZoneCode;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSCode;

import java.util.ArrayList;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com">Fernando Mendioroz</a>
 */
public interface DeleteSubscriberDataArgs {

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
