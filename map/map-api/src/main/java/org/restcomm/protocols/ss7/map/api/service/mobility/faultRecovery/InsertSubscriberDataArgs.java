package org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery;

import org.restcomm.protocols.ss7.map.api.primitives.DiameterIdentity;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.NAEAPreferredCI;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.UEUsageType;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.AgeIndicator;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.AccessRestrictionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.AdjacentAccessRestrictionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CSAllocationRetentionPriority;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CSGSubscriptionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.Category;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ChargingCharacteristics;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.EDRXCycleLength;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.EPSSubscriptionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtAccessRestrictionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBearerServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtSSInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtTeleserviceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.GPRSSubscriptionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.IMSIGroupId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LCSInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.MCSSInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.NetworkAccessMode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ODBData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SGSNCAMELSubscriptionInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SubscriberStatus;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.VlrCamelSubscriptionInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.VoiceBroadcastData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.VoiceGroupCallData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ZoneCode;

import java.util.ArrayList;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com">Fernando Mendioroz</a>
 */
public interface InsertSubscriberDataArgs {

    IMSI getImsi();

    ISDNAddressString getMsisdn();

    Category getCategory();

    SubscriberStatus getSubscriberStatus();

    ArrayList<ExtBearerServiceCode> getBearerServiceList();

    ArrayList<ExtTeleserviceCode> getTeleserviceList();

    ArrayList<ExtSSInfo> getProvisionedSS();

    ODBData getODBData();

    boolean getRoamingRestrictionDueToUnsupportedFeature();

    ArrayList<ZoneCode> getRegionalSubscriptionData();

    ArrayList<VoiceBroadcastData> getVbsSubscriptionData();

    ArrayList<VoiceGroupCallData> getVgcsSubscriptionData();

    VlrCamelSubscriptionInfo getVlrCamelSubscriptionInfo();

    MAPExtensionContainer getExtensionContainer();

    NAEAPreferredCI getNAEAPreferredCI();

    GPRSSubscriptionData getGPRSSubscriptionData();

    boolean getRoamingRestrictedInSgsnDueToUnsupportedFeature();

    NetworkAccessMode getNetworkAccessMode();

    LSAInformation getLSAInformation();

    boolean getLmuIndicator();

    LCSInformation getLCSInformation();

    Integer getIstAlertTimer();

    AgeIndicator getSuperChargerSupportedInHLR();

    MCSSInfo getMcSsInfo();

    CSAllocationRetentionPriority getCSAllocationRetentionPriority();

    SGSNCAMELSubscriptionInfo getSgsnCamelSubscriptionInfo();

    ChargingCharacteristics getChargingCharacteristics();

    AccessRestrictionData getAccessRestrictionData();

    Boolean getIcsIndicator();

    EPSSubscriptionData getEpsSubscriptionData();

    ArrayList<CSGSubscriptionData> getCsgSubscriptionDataList();

    boolean getUeReachabilityRequestIndicator();

    ISDNAddressString getSgsnNumber();

    DiameterIdentity getMmeName();

    Long getSubscribedPeriodicRAUTAUtimer();

    boolean getVplmnLIPAAllowed();

    Boolean getMdtUserConsent();

    Long getSubscribedPeriodicLAUtimer();

    ArrayList<CSGSubscriptionData> getVPLMNCSGSubscriptionDataList();

    ISDNAddressString getAdditionalMSISDN();

    boolean getPSandSMSOnlyServiceProvision();

    boolean getSMSInSGSNAllowed();

    boolean getCsToPsSRVCCAllowedIndicator();

    boolean getPCSCFRestorationRequest();

    ArrayList<AdjacentAccessRestrictionData> getAdjacentAccessRestrictionDataList();

    ArrayList<IMSIGroupId> getIMSIGroupIdList();

    UEUsageType getUEUsageType();

    boolean getUserPlaneIntegrityProtectionIndicator();

    Long getDLBufferingSuggestedPacketCount();

    ArrayList<ResetId> getResetIdList();

    ArrayList<EDRXCycleLength> getEDRXCycleLengthList();

    ExtAccessRestrictionData getExtAccessRestrictionData();

    boolean getIabOperationAllowedIndicator();
}
