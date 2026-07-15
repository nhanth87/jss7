package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import java.io.IOException;
import java.util.ArrayList;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPMessageType;
import org.restcomm.protocols.ss7.map.api.MAPOperationCode;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.DiameterIdentity;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.NAEAPreferredCI;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.UEUsageType;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.ResetId;
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
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataRequest;
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
import org.restcomm.protocols.ss7.map.primitives.DiameterIdentityImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.NAEAPreferredCIImpl;
import org.restcomm.protocols.ss7.map.service.mobility.MobilityMessageImpl;
import org.restcomm.protocols.ss7.map.service.mobility.authentication.UEUsageTypeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.faultRecovery.ResetIdImpl;

/**
 * @author daniel bichara
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com">Fernando Mendioroz</a>
 */
public class InsertSubscriberDataRequestImpl extends MobilityMessageImpl implements InsertSubscriberDataRequest {

    public static final String _PrimitiveName = "InsertSubscriberDataRequest";

    // MAP V1 & V2 & V3:
    protected static final int _TAG_imsi = 0;
    protected static final int _TAG_msisdn = 1;
    protected static final int _TAG_category = 2;
    protected static final int _TAG_subscriberStatus = 3;
    protected static final int _TAG_bearerServiceList = 4;
    protected static final int _TAG_teleserviceList = 6;
    protected static final int _TAG_provisionedSS = 7;

    // MAP V2 & V3:
    protected static final int _TAG_odb_Data = 8;
    protected static final int _TAG_roamingRestrictionDueToUnsupportedFeature = 9;
    protected static final int _TAG_regionalSubscriptionData = 10;

    // MAP V3:
    protected static final int _TAG_vbsSubscriptionData = 11;
    protected static final int _TAG_vgcsSubscriptionData = 12;
    protected static final int _TAG_vlrCamelSubscriptionInfo = 13;
    protected static final int _TAG_extContainer = 14;
    protected static final int _TAG_naea_PreferredCI = 15;
    protected static final int _TAG_gprsSubscriptionData = 16;
    protected static final int _TAG_roamingRestrictedInSgsnDueToUnsupportedFeature = 23;
    protected static final int _TAG_networkAccessMode = 24;
    protected static final int _TAG_lsaInformation = 25;
    protected static final int _TAG_lmu_Indicator = 21;
    protected static final int _TAG_lcsInformation = 22;
    protected static final int _TAG_istAlertTimer = 26;
    protected static final int _TAG_superChargerSupportedInHLR = 27;
    protected static final int _TAG_mc_SS_Info = 28;
    protected static final int _TAG_cs_AllocationRetentionPriority = 29;
    protected static final int _TAG_sgsn_CAMEL_SubscriptionInfo = 17;
    protected static final int _TAG_chargingCharacteristics = 18;
    protected static final int _TAG_accessRestrictionData = 19;
    protected static final int _TAG_ics_Indicator = 20;
    protected static final int _TAG_eps_SubscriptionData = 31;
    protected static final int _TAG_csg_SubscriptionDataList = 32;
    protected static final int _TAG_ue_ReachabilityRequestIndicator = 33;
    protected static final int _TAG_sgsn_Number = 34;
    protected static final int _TAG_mme_Name = 35;
    protected static final int _TAG_subscribedPeriodicRAUTAUtimer = 36;
    protected static final int _TAG_vplmnLIPAAllowed = 37;
    protected static final int _TAG_mdtUserConsent = 38;
    protected static final int _TAG_subscribedPeriodicLAUtimer = 39;
    protected static final int _TAG_VPLMNCSGSubscriptionDataList = 40;
    protected static final int _TAG_AdditionalMSISDN = 41;
    protected static final int _TAG_PSandSMSOnlyServiceProvision = 42;
    protected static final int _TAG_SMSInSGSNAllowed = 43;
    protected static final int _TAG_CsToPsSRVCCAllowedIndicator = 44;
    protected static final int _TAG_PCSCFRestorationRequest = 45;
    protected static final int _TAG_adjacentAccessRestrictionDataList = 46;
    protected static final int _TAG_IMSI_GroupId_List = 47;
    private static final int _TAG_UE_Usage_Type = 48;
    private static final int _TAG_userPlaneIntegrityProtectionIndicator = 49;
    private static final int _TAG_DL_Buffering_Suggested_Packet_Count = 50;
    private static final int _TAG_Reset_Id_List = 51;
    private static final int _TAG_EDRX_Cycle_Length_List = 52;
    private static final int _TAG_Ext_AccessRestrictionData = 53;
    private static final int _TAG_iabOperationAllowedIndicator = 54;

    private IMSI imsi = null;
    private ISDNAddressString msisdn = null;
    private Category category = null;
    private SubscriberStatus subscriberStatus = null;
    private ArrayList<ExtBearerServiceCode> bearerServiceList = null;
    private ArrayList<ExtTeleserviceCode> teleserviceList = null;
    private ArrayList<ExtSSInfo> provisionedSS = null;
    private ODBData odbData = null;
    private boolean roamingRestrictionDueToUnsupportedFeature = false;
    private ArrayList<ZoneCode> regionalSubscriptionData = null;
    private ArrayList<VoiceBroadcastData> vbsSubscriptionData = null;
    private ArrayList<VoiceGroupCallData> vgcsSubscriptionData = null;
    private VlrCamelSubscriptionInfo vlrCamelSubscriptionInfo = null;
    private MAPExtensionContainer extensionContainer = null;
    private NAEAPreferredCI naeaPreferredCI = null;
    private GPRSSubscriptionData gprsSubscriptionData = null;
    private boolean roamingRestrictedInSgsnDueToUnsupportedFeature = false;
    private NetworkAccessMode networkAccessMode = null;
    private LSAInformation lsaInformation = null;
    private boolean lmuIndicator = false;
    private LCSInformation lcsInformation = null;
    private Integer istAlertTimer = null;
    private AgeIndicator superChargerSupportedInHLR = null;
    private MCSSInfo mcSsInfo = null;
    private CSAllocationRetentionPriority csAllocationRetentionPriority = null;
    private SGSNCAMELSubscriptionInfo sgsnCamelSubscriptionInfo = null;
    private ChargingCharacteristics chargingCharacteristics = null;
    private AccessRestrictionData accessRestrictionData = null;
    private Boolean icsIndicator = null;
    private EPSSubscriptionData epsSubscriptionData = null;
    private ArrayList<CSGSubscriptionData> csgSubscriptionDataList = null;
    private boolean ueReachabilityRequestIndicator = false;
    private ISDNAddressString sgsnNumber = null;
    private DiameterIdentity mmeName = null;
    private Long subscribedPeriodicRAUTAUtimer = null;
    private boolean vplmnLIPAAllowed = false;
    private Boolean mdtUserConsent = null;
    private Long subscribedPeriodicLAUtimer = null;
    private ArrayList<CSGSubscriptionData> vplmnCSGSubscriptionDataList = null;
    private ISDNAddressString additionalMSISDN = null;
    private boolean psAndSMSOnlyServiceProvision = false;
    private boolean smsInSGSNAllowed = false;
    private boolean csToPsSRVCCAllowedIndicator = false;
    private boolean pcscfRestorationRequest = false;
    private ArrayList<AdjacentAccessRestrictionData> adjacentAccessRestrictionDataList = null;
    private ArrayList<IMSIGroupId> imsiGroupIdList = null;
    private UEUsageType ueUsageType = null;
    private boolean userPlaneIntegrityProtectionIndicator = false;
    private Long dlBufferingSuggestedPacketCount = null;
    private ArrayList<ResetId> resetIdList = null;
    private ArrayList<EDRXCycleLength> eDRXCycleLengthList = null;
    private ExtAccessRestrictionData extAccessRestrictionData = null;
    private boolean iabOperationAllowedIndicator = false;
    private long mapProtocolVersion;

    // For incoming messages
    public InsertSubscriberDataRequestImpl(long mapProtocolVersion) {
        this.mapProtocolVersion = mapProtocolVersion;
    }

    // For outgoing messages - MAP V2
    public InsertSubscriberDataRequestImpl(long mapProtocolVersion, IMSI imsi, ISDNAddressString msisdn, Category category,
            SubscriberStatus subscriberStatus, ArrayList<ExtBearerServiceCode> bearerServiceList,
            ArrayList<ExtTeleserviceCode> teleserviceList, ArrayList<ExtSSInfo> provisionedSS, ODBData odbData,
            boolean roamingRestrictionDueToUnsupportedFeature, ArrayList<ZoneCode> regionalSubscriptionData,
            ArrayList<VoiceBroadcastData> vbsSubscriptionData, ArrayList<VoiceGroupCallData> vgcsSubscriptionData,
            VlrCamelSubscriptionInfo vlrCamelSubscriptionInfo) {
        this.mapProtocolVersion = mapProtocolVersion;
        this.imsi = imsi;
        this.msisdn = msisdn;
        this.category = category;
        this.subscriberStatus = subscriberStatus;
        this.bearerServiceList = bearerServiceList;
        this.teleserviceList = teleserviceList;
        this.provisionedSS = provisionedSS;
        this.odbData = odbData;
        this.roamingRestrictionDueToUnsupportedFeature = roamingRestrictionDueToUnsupportedFeature;
        this.regionalSubscriptionData = regionalSubscriptionData;
        this.vbsSubscriptionData = vbsSubscriptionData;
        this.vgcsSubscriptionData = vgcsSubscriptionData;
        this.vlrCamelSubscriptionInfo = vlrCamelSubscriptionInfo;
    }

    // For outgoing messages - MAP V3
    public InsertSubscriberDataRequestImpl(long mapProtocolVersion, IMSI imsi, ISDNAddressString msisdn, Category category,
            SubscriberStatus subscriberStatus, ArrayList<ExtBearerServiceCode> bearerServiceList,
            ArrayList<ExtTeleserviceCode> teleserviceList, ArrayList<ExtSSInfo> provisionedSS, ODBData odbData,
            boolean roamingRestrictionDueToUnsupportedFeature, ArrayList<ZoneCode> regionalSubscriptionData,
            ArrayList<VoiceBroadcastData> vbsSubscriptionData, ArrayList<VoiceGroupCallData> vgcsSubscriptionData,
            VlrCamelSubscriptionInfo vlrCamelSubscriptionInfo, MAPExtensionContainer extensionContainer,
            NAEAPreferredCI naeaPreferredCI, GPRSSubscriptionData gprsSubscriptionData,
            boolean roamingRestrictedInSgsnDueToUnsupportedFeature, NetworkAccessMode networkAccessMode,
            LSAInformation lsaInformation, boolean lmuIndicator, LCSInformation lcsInformation, Integer istAlertTimer,
            AgeIndicator superChargerSupportedInHLR, MCSSInfo mcSsInfo,
            CSAllocationRetentionPriority csAllocationRetentionPriority, SGSNCAMELSubscriptionInfo sgsnCamelSubscriptionInfo,
            ChargingCharacteristics chargingCharacteristics, AccessRestrictionData accessRestrictionData, Boolean icsIndicator,
            EPSSubscriptionData epsSubscriptionData, ArrayList<CSGSubscriptionData> csgSubscriptionDataList,
            boolean ueReachabilityRequestIndicator, ISDNAddressString sgsnNumber, DiameterIdentity mmeName,
            Long subscribedPeriodicRAUTAUtimer, boolean vplmnLIPAAllowed, Boolean mdtUserConsent,
            Long subscribedPeriodicLAUtimer, ArrayList<CSGSubscriptionData> vplmnCSGSubscriptionDataList,
            ISDNAddressString additionalMSISDN, boolean psAndSMSOnlyServiceProvision, boolean smsInSGSNAllowed,
            boolean csToPsSRVCCAllowedIndicator, boolean pcscfRestorationRequest, ArrayList<AdjacentAccessRestrictionData> adjacentAccessRestrictionDataList,
            ArrayList<IMSIGroupId> imsiGroupIdList, UEUsageType ueUsageType, boolean userPlaneIntegrityProtectionIndicator,
            Long dlBufferingSuggestedPacketCount, ArrayList<ResetId> resetIdList, ArrayList<EDRXCycleLength> eDRXCycleLengthList,
            ExtAccessRestrictionData extAccessRestrictionData, boolean iabOperationAllowedIndicator) {

        this.mapProtocolVersion = mapProtocolVersion;
        this.imsi = imsi;
        this.msisdn = msisdn;
        this.category = category;
        this.subscriberStatus = subscriberStatus;
        this.bearerServiceList = bearerServiceList;
        this.teleserviceList = teleserviceList;
        this.provisionedSS = provisionedSS;
        this.odbData = odbData;
        this.roamingRestrictionDueToUnsupportedFeature = roamingRestrictionDueToUnsupportedFeature;
        this.regionalSubscriptionData = regionalSubscriptionData;
        this.vbsSubscriptionData = vbsSubscriptionData;
        this.vgcsSubscriptionData = vgcsSubscriptionData;
        this.vlrCamelSubscriptionInfo = vlrCamelSubscriptionInfo;

        if (mapProtocolVersion >= 3) {
            this.extensionContainer = extensionContainer;
            this.naeaPreferredCI = naeaPreferredCI;
            this.gprsSubscriptionData = gprsSubscriptionData;
            this.roamingRestrictedInSgsnDueToUnsupportedFeature = roamingRestrictedInSgsnDueToUnsupportedFeature;
            this.networkAccessMode = networkAccessMode;
            this.lsaInformation = lsaInformation;
            this.lmuIndicator = lmuIndicator;
            this.lcsInformation = lcsInformation;
            this.istAlertTimer = istAlertTimer;
            this.superChargerSupportedInHLR = superChargerSupportedInHLR;
            this.mcSsInfo = mcSsInfo;
            this.csAllocationRetentionPriority = csAllocationRetentionPriority;
            this.sgsnCamelSubscriptionInfo = sgsnCamelSubscriptionInfo;
            this.chargingCharacteristics = chargingCharacteristics;
            this.accessRestrictionData = accessRestrictionData;
            this.icsIndicator = icsIndicator;
            this.epsSubscriptionData = epsSubscriptionData;
            this.csgSubscriptionDataList = csgSubscriptionDataList;
            this.ueReachabilityRequestIndicator = ueReachabilityRequestIndicator;
            this.sgsnNumber = sgsnNumber;
            this.mmeName = mmeName;
            this.subscribedPeriodicRAUTAUtimer = subscribedPeriodicRAUTAUtimer;
            this.vplmnLIPAAllowed = vplmnLIPAAllowed;
            this.mdtUserConsent = mdtUserConsent;
            this.subscribedPeriodicLAUtimer = subscribedPeriodicLAUtimer;
            this.vplmnCSGSubscriptionDataList = vplmnCSGSubscriptionDataList;
            this.additionalMSISDN = additionalMSISDN;
            this.psAndSMSOnlyServiceProvision = psAndSMSOnlyServiceProvision;
            this.smsInSGSNAllowed = smsInSGSNAllowed;
            this.csToPsSRVCCAllowedIndicator = csToPsSRVCCAllowedIndicator;
            this.pcscfRestorationRequest = pcscfRestorationRequest;
            this.adjacentAccessRestrictionDataList = adjacentAccessRestrictionDataList;
            this.imsiGroupIdList = imsiGroupIdList;
            this.ueUsageType = ueUsageType;
            this.userPlaneIntegrityProtectionIndicator = userPlaneIntegrityProtectionIndicator;
            this.dlBufferingSuggestedPacketCount = dlBufferingSuggestedPacketCount;
            this.resetIdList = resetIdList;
            this.eDRXCycleLengthList = eDRXCycleLengthList;
            this.extAccessRestrictionData = extAccessRestrictionData;
            this.iabOperationAllowedIndicator = iabOperationAllowedIndicator;
        }
    }

    public long getMapProtocolVersion() {
        return this.mapProtocolVersion;
    }

    @Override
    public MAPMessageType getMessageType() {
        return MAPMessageType.insertSubscriberData_Request;
    }

    @Override
    public int getOperationCode() {
        return MAPOperationCode.insertSubscriberData;
    }

    @Override
    public IMSI getImsi() {
        return this.imsi;
    }

    @Override
    public ISDNAddressString getMsisdn() {
        return this.msisdn;
    }

    @Override
    public Category getCategory() {
        return this.category;
    }

    @Override
    public SubscriberStatus getSubscriberStatus() {
        return this.subscriberStatus;
    }

    @Override
    public ArrayList<ExtBearerServiceCode> getBearerServiceList() {
        return this.bearerServiceList;
    }

    @Override
    public ArrayList<ExtTeleserviceCode> getTeleserviceList() {
        return this.teleserviceList;
    }

    @Override
    public ArrayList<ExtSSInfo> getProvisionedSS() {
        return this.provisionedSS;
    }

    @Override
    public ODBData getODBData() {
        return this.odbData;
    }

    @Override
    public boolean getRoamingRestrictionDueToUnsupportedFeature() {
        return this.roamingRestrictionDueToUnsupportedFeature;
    }

    @Override
    public ArrayList<ZoneCode> getRegionalSubscriptionData() {
        return this.regionalSubscriptionData;
    }

    @Override
    public ArrayList<VoiceBroadcastData> getVbsSubscriptionData() {
        return this.vbsSubscriptionData;
    }

    @Override
    public ArrayList<VoiceGroupCallData> getVgcsSubscriptionData() {
        return this.vgcsSubscriptionData;
    }

    @Override
    public VlrCamelSubscriptionInfo getVlrCamelSubscriptionInfo() {
        return this.vlrCamelSubscriptionInfo;
    }

    @Override
    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    @Override
    public NAEAPreferredCI getNAEAPreferredCI() {
        return this.naeaPreferredCI;
    }

    @Override
    public GPRSSubscriptionData getGPRSSubscriptionData() {
        return this.gprsSubscriptionData;
    }

    @Override
    public boolean getRoamingRestrictedInSgsnDueToUnsupportedFeature() {
        return this.roamingRestrictedInSgsnDueToUnsupportedFeature;
    }

    @Override
    public NetworkAccessMode getNetworkAccessMode() {
        return this.networkAccessMode;
    }

    @Override
    public LSAInformation getLSAInformation() {
        return this.lsaInformation;
    }

    @Override
    public boolean getLmuIndicator() {
        return this.lmuIndicator;
    }

    @Override
    public LCSInformation getLCSInformation() {
        return this.lcsInformation;
    }

    @Override
    public Integer getIstAlertTimer() {
        return this.istAlertTimer;
    }

    @Override
    public AgeIndicator getSuperChargerSupportedInHLR() {
        return this.superChargerSupportedInHLR;
    }

    @Override
    public MCSSInfo getMcSsInfo() {
        return this.mcSsInfo;
    }

    @Override
    public CSAllocationRetentionPriority getCSAllocationRetentionPriority() {
        return this.csAllocationRetentionPriority;
    }

    @Override
    public SGSNCAMELSubscriptionInfo getSgsnCamelSubscriptionInfo() {
        return this.sgsnCamelSubscriptionInfo;
    }

    @Override
    public ChargingCharacteristics getChargingCharacteristics() {
        return this.chargingCharacteristics;
    }

    @Override
    public AccessRestrictionData getAccessRestrictionData() {
        return this.accessRestrictionData;
    }

    @Override
    public Boolean getIcsIndicator() {
        return this.icsIndicator;
    }

    @Override
    public EPSSubscriptionData getEpsSubscriptionData() {
        return this.epsSubscriptionData;
    }

    @Override
    public ArrayList<CSGSubscriptionData> getCsgSubscriptionDataList() {
        return this.csgSubscriptionDataList;
    }

    @Override
    public boolean getUeReachabilityRequestIndicator() {
        return this.ueReachabilityRequestIndicator;
    }

    @Override
    public ISDNAddressString getSgsnNumber() {
        return this.sgsnNumber;
    }

    @Override
    public DiameterIdentity getMmeName() {
        return this.mmeName;
    }

    @Override
    public Long getSubscribedPeriodicRAUTAUtimer() {
        return this.subscribedPeriodicRAUTAUtimer;
    }

    @Override
    public boolean getVplmnLIPAAllowed() {
        return this.vplmnLIPAAllowed;
    }

    @Override
    public Boolean getMdtUserConsent() {
        return this.mdtUserConsent;
    }

    @Override
    public Long getSubscribedPeriodicLAUtimer() {
        return this.subscribedPeriodicLAUtimer;
    }

    @Override
    public ArrayList<CSGSubscriptionData> getVPLMNCSGSubscriptionDataList() {
        return this.vplmnCSGSubscriptionDataList;
    }

    @Override
    public ISDNAddressString getAdditionalMSISDN() {
        return this.additionalMSISDN;
    }

    @Override
    public boolean getPSandSMSOnlyServiceProvision() {
        return this.psAndSMSOnlyServiceProvision;
    }

    @Override
    public boolean getSMSInSGSNAllowed() {
        return this.smsInSGSNAllowed;
    }

    @Override
    public boolean getCsToPsSRVCCAllowedIndicator() {
        return this.csToPsSRVCCAllowedIndicator;
    }

    @Override
    public boolean getPCSCFRestorationRequest() {
        return this.pcscfRestorationRequest;
    }

    @Override
    public ArrayList<AdjacentAccessRestrictionData> getAdjacentAccessRestrictionDataList() {
        return this.adjacentAccessRestrictionDataList;
    }

    @Override
    public ArrayList<IMSIGroupId> getIMSIGroupIdList() {
        return this.imsiGroupIdList;
    }

    @Override
    public UEUsageType getUEUsageType() {
        return this.ueUsageType;
    }

    @Override
    public boolean getUserPlaneIntegrityProtectionIndicator() {
        return this.userPlaneIntegrityProtectionIndicator;
    }

    @Override
    public Long getDLBufferingSuggestedPacketCount() {
        return this.dlBufferingSuggestedPacketCount;
    }

    @Override
    public ArrayList<ResetId> getResetIdList() {
        return this.resetIdList;
    }

    @Override
    public ArrayList<EDRXCycleLength> getEDRXCycleLengthList() {
        return this.eDRXCycleLengthList;
    }

    @Override
    public ExtAccessRestrictionData getExtAccessRestrictionData() {
        return this.extAccessRestrictionData;
    }

    @Override
    public boolean getIabOperationAllowedIndicator() {
        return this.iabOperationAllowedIndicator;
    }

    @Override
    public int getTag() throws MAPException {
        return Tag.SEQUENCE;
    }

    @Override
    public int getTagClass() {
        return Tag.CLASS_UNIVERSAL;
    }

    @Override
    public boolean getIsPrimitive() {
        return false;
    }

    @Override
    public void decodeAll(AsnInputStream asnInputStream) throws MAPParsingComponentException {
        try {
            int length = asnInputStream.readLength();
            this._decode(asnInputStream, length);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    public void decodeData(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException {
        try {
            this._decode(asnInputStream, length);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        ExtBearerServiceCodeImpl bearerItem;
        ExtTeleserviceCodeImpl teleserviceItem;
        ExtSSInfoImpl serviceItem;
        this.imsi = null;
        this.msisdn = null;
        this.category = null;
        this.subscriberStatus = null;
        this.bearerServiceList = null;
        this.teleserviceList = null;
        this.provisionedSS = null;
        this.roamingRestrictionDueToUnsupportedFeature = false;
        this.extensionContainer = null;
        this.gprsSubscriptionData = null;
        this.chargingCharacteristics = null;
        this.roamingRestrictedInSgsnDueToUnsupportedFeature = false;
        this.networkAccessMode = null;
        this.odbData = null;
        this.regionalSubscriptionData = null;
        this.vbsSubscriptionData = null;
        this.vgcsSubscriptionData = null;
        this.vlrCamelSubscriptionInfo = null;
        this.naeaPreferredCI = null;
        this.lsaInformation = null;
        this.lmuIndicator = false;
        this.lcsInformation = null;
        this.istAlertTimer = null;
        this.superChargerSupportedInHLR = null;
        this.mcSsInfo = null;
        this.csAllocationRetentionPriority = null;
        this.sgsnCamelSubscriptionInfo = null;
        this.accessRestrictionData = null;
        this.icsIndicator = null;
        this.epsSubscriptionData = null;
        this.csgSubscriptionDataList = null;
        this.ueReachabilityRequestIndicator = false;
        this.sgsnNumber = null;
        this.mmeName = null;
        this.subscribedPeriodicRAUTAUtimer = null;
        this.vplmnLIPAAllowed = false;
        this.mdtUserConsent = null;
        this.subscribedPeriodicLAUtimer = null;
        this.vplmnCSGSubscriptionDataList = null;
        this.additionalMSISDN = null;
        this.psAndSMSOnlyServiceProvision = false;
        this.smsInSGSNAllowed = false;
        this.csToPsSRVCCAllowedIndicator = false;
        this.pcscfRestorationRequest = false;
        this.adjacentAccessRestrictionDataList = null;
        this.imsiGroupIdList = null;
        this.ueUsageType = null;
        this.userPlaneIntegrityProtectionIndicator = false;
        this.dlBufferingSuggestedPacketCount = null;
        this.resetIdList = null;
        this.eDRXCycleLengthList = null;
        this.extAccessRestrictionData = null;
        this.iabOperationAllowedIndicator = false;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0) {
                break;
            }

            int tag = ais.readTag();

            switch (ais.getTagClass()) {
                case Tag.CLASS_CONTEXT_SPECIFIC:
                    switch (tag) {
                        case _TAG_imsi:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".imsi: is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                            this.imsi = new IMSIImpl();
                            ((IMSIImpl) this.imsi).decodeAll(ais);
                            break;
                        case _TAG_msisdn:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".msisdn: is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                            this.msisdn = new ISDNAddressStringImpl();
                            ((ISDNAddressStringImpl) this.msisdn).decodeAll(ais);
                            break;
                        case _TAG_category:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".category: is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                            this.category = new CategoryImpl();
                            ((CategoryImpl) this.category).decodeAll(ais);
                            break;
                        case _TAG_subscriberStatus:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".subscriberStatus: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            this.subscriberStatus = SubscriberStatus.getInstance((int) ais.readInteger());
                            break;
                        case _TAG_bearerServiceList:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".bearerServiceList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            AsnInputStream ais2 = ais.readSequenceStream();
                            this.bearerServiceList = new ArrayList<>();
                            while (true) {
                                if (ais2.available() == 0)
                                    break;

                                int tag2 = ais2.readTag();
                                if (tag2 != Tag.STRING_OCTET || ais2.getTagClass() != Tag.CLASS_UNIVERSAL || !ais2.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad bearerServiceCode element tag or tagClass or is not primitive ",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                bearerItem = new ExtBearerServiceCodeImpl();
                                (bearerItem).decodeAll(ais2);
                                this.bearerServiceList.add(bearerItem);
                            }
                            if (this.bearerServiceList.isEmpty() || this.bearerServiceList.size() > 50) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter bearerServiceList size must be from 1 to 50, found: "
                                        + this.bearerServiceList.size(), MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_teleserviceList:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".teleserviceList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            AsnInputStream ais3 = ais.readSequenceStream();
                            this.teleserviceList = new ArrayList<>();
                            while (true) {
                                if (ais3.available() == 0)
                                    break;

                                int tag3 = ais3.readTag();
                                if (tag3 != Tag.STRING_OCTET || ais3.getTagClass() != Tag.CLASS_UNIVERSAL || !ais3.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad teleserviceCode tag or tagClass or is not primitive ",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                teleserviceItem = new ExtTeleserviceCodeImpl();
                                (teleserviceItem).decodeAll(ais3);
                                this.teleserviceList.add(teleserviceItem);
                            }
                            if (this.teleserviceList.isEmpty() || this.teleserviceList.size() > 20) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter teleserviceList size must be from 1 to 20, found: "
                                        + this.teleserviceList.size(), MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_provisionedSS:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".provisionedSS: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            AsnInputStream ais4 = ais.readSequenceStream();
                            this.provisionedSS = new ArrayList<>();
                            while (true) {
                                if (ais4.available() == 0)
                                    break;

                                ais4.readTag();

                                serviceItem = new ExtSSInfoImpl();
                                (serviceItem).decodeAll(ais4);
                                this.provisionedSS.add(serviceItem);
                            }
                            if (this.provisionedSS.isEmpty() || this.provisionedSS.size() > 30) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter provisionedSS size must be from 1 to 30, found: "
                                        + this.provisionedSS.size(), MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_odb_Data:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".odbData: is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                            this.odbData = new ODBDataImpl();
                            ((ODBDataImpl) this.odbData).decodeAll(ais);
                            break;
                        case _TAG_roamingRestrictionDueToUnsupportedFeature:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".roamingRestrictionDueToUnsupportedFeature: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            ais.readNull();
                            this.roamingRestrictionDueToUnsupportedFeature = true;
                            break;
                        case _TAG_regionalSubscriptionData:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".regionalSubscriptionData: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            AsnInputStream ais5 = ais.readSequenceStream();
                            this.regionalSubscriptionData = new ArrayList<>();
                            while (true) {
                                if (ais5.available() == 0)
                                    break;

                                int tag5 = ais5.readTag();
                                if (tag5 != Tag.STRING_OCTET || ais5.getTagClass() != Tag.CLASS_UNIVERSAL || !ais5.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad regionalSubscriptionData tag or tagClass or is not primitive ",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                ZoneCodeImpl zoneCode = new ZoneCodeImpl();
                                (zoneCode).decodeAll(ais5);
                                this.regionalSubscriptionData.add(zoneCode);
                            }
                            if (this.regionalSubscriptionData.isEmpty() || this.regionalSubscriptionData.size() > 10) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter regionalSubscriptionData size must be from 1 to 10, found: "
                                        + this.regionalSubscriptionData.size(),
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_vbsSubscriptionData:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".vbsSubscriptionData: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            AsnInputStream ais6 = ais.readSequenceStream();
                            this.vbsSubscriptionData = new ArrayList<>();
                            while (true) {
                                if (ais6.available() == 0)
                                    break;

                                int tag6 = ais6.readTag();
                                if (tag6 != Tag.SEQUENCE || ais6.getTagClass() != Tag.CLASS_UNIVERSAL || ais6.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad vbsSubscriptionData element tag or tagClass or is primitive ",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                VoiceBroadcastDataImpl voiceBroadcastData = new VoiceBroadcastDataImpl();
                                (voiceBroadcastData).decodeAll(ais6);
                                this.vbsSubscriptionData.add(voiceBroadcastData);
                            }
                            if (this.vbsSubscriptionData.isEmpty() || this.vbsSubscriptionData.size() > 50) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter vbsSubscriptionData size must be from 1 to 50, found: "
                                        + this.vbsSubscriptionData.size(), MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_vgcsSubscriptionData:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".vgcsSubscriptionData: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            AsnInputStream ais7 = ais.readSequenceStream();
                            this.vgcsSubscriptionData = new ArrayList<>();
                            while (true) {
                                if (ais7.available() == 0)
                                    break;

                                int tag7 = ais7.readTag();
                                if (tag7 != Tag.SEQUENCE || ais7.getTagClass() != Tag.CLASS_UNIVERSAL || ais7.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad vgcsSubscriptionData element tag or tagClass or is primitive ",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                VoiceGroupCallDataImpl voiceGroupCallData = new VoiceGroupCallDataImpl();
                                (voiceGroupCallData).decodeAll(ais7);
                                vgcsSubscriptionData.add(voiceGroupCallData);
                            }
                            if (this.vgcsSubscriptionData.isEmpty() || this.vgcsSubscriptionData.size() > 50) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter vgcsSubscriptionData size must be from 1 to 50, found: "
                                        + this.vgcsSubscriptionData.size(),
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_vlrCamelSubscriptionInfo:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".vlrCamelSubscriptionInfo: Parameter vlrCamelSubscriptionInfo is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            this.vlrCamelSubscriptionInfo = new VlrCamelSubscriptionInfoImpl();
                            ((VlrCamelSubscriptionInfoImpl) this.vlrCamelSubscriptionInfo).decodeAll(ais);
                            break;
                        case _TAG_extContainer:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".extensionContainer: Parameter extensionContainer is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.extensionContainer = new MAPExtensionContainerImpl();
                            ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                            break;
                        case _TAG_naea_PreferredCI:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".naea_PreferredCI: is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            this.naeaPreferredCI = new NAEAPreferredCIImpl();
                            ((NAEAPreferredCIImpl) this.naeaPreferredCI).decodeAll(ais);
                            break;
                        case _TAG_gprsSubscriptionData:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".gprsSubscriptionData: is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.gprsSubscriptionData = new GPRSSubscriptionDataImpl();
                            ((GPRSSubscriptionDataImpl) this.gprsSubscriptionData).decodeAll(ais);
                            break;
                        case _TAG_roamingRestrictedInSgsnDueToUnsupportedFeature:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".roamingRestrictedInSgsnDueToUnsupportedFeature: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            ais.readNull();
                            this.roamingRestrictedInSgsnDueToUnsupportedFeature = true;
                            break;
                        case _TAG_networkAccessMode:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".networkAccessMode: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            this.networkAccessMode = NetworkAccessMode.getInstance((int) ais.readInteger());
                            break;
                        case _TAG_lsaInformation:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".lsaInformation: is primitive", MAPParsingComponentExceptionReason.MistypedParameter);

                            this.lsaInformation = new LSAInformationImpl();
                            ((LSAInformationImpl) this.lsaInformation).decodeAll(ais);
                            break;
                        case _TAG_lmu_Indicator:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".lmu_Indicator: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            ais.readNull();
                            this.lmuIndicator = true;
                            break;
                        case _TAG_lcsInformation:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".lcsInformation: is primitive", MAPParsingComponentExceptionReason.MistypedParameter);

                            this.lcsInformation = new LCSInformationImpl();
                            ((LCSInformationImpl) this.lcsInformation).decodeAll(ais);
                            break;
                        case _TAG_istAlertTimer:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".istAlertTimer: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            this.istAlertTimer = (int) ais.readInteger();
                            if (this.istAlertTimer < 15 || this.istAlertTimer > 255) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter istAlertTimer must be from 15 to 255, parsed: " + this.istAlertTimer,
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_superChargerSupportedInHLR:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".lcsInformation: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.superChargerSupportedInHLR = new AgeIndicatorImpl();
                            ((AgeIndicatorImpl) this.superChargerSupportedInHLR).decodeAll(ais);
                            break;
                        case _TAG_mc_SS_Info:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".mcSsInfo: is primitive", MAPParsingComponentExceptionReason.MistypedParameter);

                            this.mcSsInfo = new MCSSInfoImpl();
                            ((MCSSInfoImpl) this.mcSsInfo).decodeAll(ais);
                            break;
                        case _TAG_cs_AllocationRetentionPriority:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".csAllocationRetentionPriority: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.csAllocationRetentionPriority = new CSAllocationRetentionPriorityImpl();
                            ((CSAllocationRetentionPriorityImpl) this.csAllocationRetentionPriority).decodeAll(ais);
                            break;
                        case _TAG_sgsn_CAMEL_SubscriptionInfo:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".sgsnCamelSubscriptionInfo: is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            this.sgsnCamelSubscriptionInfo = new SGSNCAMELSubscriptionInfoImpl();
                            ((SGSNCAMELSubscriptionInfoImpl) this.sgsnCamelSubscriptionInfo).decodeAll(ais);
                            break;
                        case _TAG_chargingCharacteristics:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".chargingCharacteristics: bad tag or tag class or not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.chargingCharacteristics = new ChargingCharacteristicsImpl();
                            ((ChargingCharacteristicsImpl) this.chargingCharacteristics).decodeAll(ais);
                            break;
                        case _TAG_accessRestrictionData:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".accessRestrictionData: bad tag or tag class or not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            this.accessRestrictionData = new AccessRestrictionDataImpl();
                            ((AccessRestrictionDataImpl) this.accessRestrictionData).decodeAll(ais);
                            break;
                        case _TAG_ics_Indicator:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".icsIndicator: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            this.icsIndicator = ais.readBoolean();
                            break;
                        case _TAG_eps_SubscriptionData:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".epsSubscriptionData: is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            this.epsSubscriptionData = new EPSSubscriptionDataImpl();
                            ((EPSSubscriptionDataImpl) this.epsSubscriptionData).decodeAll(ais);
                            break;
                        case _TAG_csg_SubscriptionDataList:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".csg_SubscriptionDataList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            AsnInputStream ais8 = ais.readSequenceStream();
                            this.csgSubscriptionDataList = new ArrayList<>();
                            while (true) {
                                if (ais8.available() == 0)
                                    break;

                                int tag8 = ais8.readTag();
                                if (tag8 != Tag.SEQUENCE || ais8.getTagClass() != Tag.CLASS_UNIVERSAL || ais8.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad csgSubscriptionDataList element tag or tagClass or is primitive ",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                CSGSubscriptionDataImpl csgSubscriptionData = new CSGSubscriptionDataImpl();
                                (csgSubscriptionData).decodeAll(ais8);
                                csgSubscriptionDataList.add(csgSubscriptionData);
                            }
                            if (this.csgSubscriptionDataList.isEmpty() || this.csgSubscriptionDataList.size() > 50) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter csgSubscriptionDataList size must be from 1 to 50, found: "
                                        + this.csgSubscriptionDataList.size(),
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_ue_ReachabilityRequestIndicator:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".ue_ReachabilityRequestIndicator: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            ais.readNull();
                            this.ueReachabilityRequestIndicator = true;
                            break;
                        case _TAG_sgsn_Number:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".sgsnNumber: is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                            this.sgsnNumber = new ISDNAddressStringImpl();
                            ((ISDNAddressStringImpl) this.sgsnNumber).decodeAll(ais);
                            break;
                        case _TAG_mme_Name:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".mmeName: bad tag or tag class or not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.mmeName = new DiameterIdentityImpl();
                            ((DiameterIdentityImpl) this.mmeName).decodeAll(ais);
                            break;
                        case _TAG_subscribedPeriodicRAUTAUtimer:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".subscribedPeriodicRAUTAUtimer: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            this.subscribedPeriodicRAUTAUtimer = ais.readInteger();
                            break;
                        case _TAG_vplmnLIPAAllowed:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".vplmnLIPAAllowed: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            ais.readNull();
                            this.vplmnLIPAAllowed = true;
                            break;
                        case _TAG_mdtUserConsent:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".mdtUserConsent: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            this.mdtUserConsent = ais.readBoolean();
                            break;
                        case _TAG_subscribedPeriodicLAUtimer:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".subscribedPeriodicLAUtimer: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            this.subscribedPeriodicLAUtimer = ais.readInteger();
                            break;
                        case _TAG_VPLMNCSGSubscriptionDataList:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".vplmnCSGSubscriptionDataList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            AsnInputStream ais9 = ais.readSequenceStream();
                            this.vplmnCSGSubscriptionDataList = new ArrayList<>();
                            while (true) {
                                if (ais9.available() == 0)
                                    break;

                                int tag9 = ais9.readTag();
                                if (tag9 != Tag.SEQUENCE || ais9.getTagClass() != Tag.CLASS_UNIVERSAL || ais9.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad vplmnCSGSubscriptionDataList element tag or tagClass or is primitive ",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                CSGSubscriptionDataImpl csgSubscriptionData = new CSGSubscriptionDataImpl();
                                (csgSubscriptionData).decodeAll(ais9);
                                vplmnCSGSubscriptionDataList.add(csgSubscriptionData);
                            }
                            if (this.vplmnCSGSubscriptionDataList.isEmpty() || this.vplmnCSGSubscriptionDataList.size() > 50) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter vplmnCSGSubscriptionDataList size must be from 1 to 50, found: "
                                        + this.vplmnCSGSubscriptionDataList.size(),
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_AdditionalMSISDN:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".additionalMSISDN: is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                            this.additionalMSISDN = new ISDNAddressStringImpl();
                            ((ISDNAddressStringImpl) this.additionalMSISDN).decodeAll(ais);
                            break;
                        case _TAG_PSandSMSOnlyServiceProvision:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".psAndSMSOnlyServiceProvision: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            ais.readNull();
                            this.psAndSMSOnlyServiceProvision = true;
                            break;
                        case _TAG_SMSInSGSNAllowed:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".smsInSGSNAllowed: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            ais.readNull();
                            this.smsInSGSNAllowed = true;
                            break;
                        case _TAG_CsToPsSRVCCAllowedIndicator:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".csToPsSRVCCAllowedIndicator: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            ais.readNull();
                            this.csToPsSRVCCAllowedIndicator = true;
                            break;
                        case _TAG_PCSCFRestorationRequest:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".pcscfRestorationRequest: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            ais.readNull();
                            this.pcscfRestorationRequest = true;
                            break;
                        case _TAG_adjacentAccessRestrictionDataList:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".vplmnCSGSubscriptionDataList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            AsnInputStream ais10 = ais.readSequenceStream();
                            this.adjacentAccessRestrictionDataList = new ArrayList<>();
                            while (true) {
                                if (ais10.available() == 0)
                                    break;

                                int tag10 = ais10.readTag();
                                if (tag10 != Tag.SEQUENCE || ais10.getTagClass() != Tag.CLASS_UNIVERSAL || ais10.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad adjacentAccessRestrictionDataList element tag or tagClass or is primitive ",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                AdjacentAccessRestrictionDataImpl adjacentAccessRestrictionData = new AdjacentAccessRestrictionDataImpl();
                                (adjacentAccessRestrictionData).decodeAll(ais10);
                                adjacentAccessRestrictionDataList.add(adjacentAccessRestrictionData);
                            }
                            if (this.adjacentAccessRestrictionDataList.isEmpty() || this.adjacentAccessRestrictionDataList.size() > 50) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter adjacentAccessRestrictionDataList size must be from 1 to 50, found: "
                                        + this.adjacentAccessRestrictionDataList.size(),
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_IMSI_GroupId_List:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".imsiGroupIdList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            AsnInputStream ais11 = ais.readSequenceStream();
                            this.imsiGroupIdList = new ArrayList<>();
                            while (true) {
                                if (ais11.available() == 0)
                                    break;

                                int tag11 = ais11.readTag();
                                if (tag11 != Tag.SEQUENCE || ais11.getTagClass() != Tag.CLASS_UNIVERSAL || ais11.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad imsiGroupIdList element tag or tagClass or is primitive ",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                IMSIGroupIdImpl imsiGroupId = new IMSIGroupIdImpl();
                                (imsiGroupId).decodeAll(ais11);
                                imsiGroupIdList.add(imsiGroupId);
                            }
                            if (this.imsiGroupIdList.isEmpty() || this.imsiGroupIdList.size() > 50) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter imsiGroupIdList size must be from 1 to 50, found: "
                                        + this.imsiGroupIdList.size(),
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_UE_Usage_Type:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".ueUsageType: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            this.ueUsageType = new UEUsageTypeImpl();
                            ((UEUsageTypeImpl) this.ueUsageType).decodeAll(ais);
                            break;
                        case _TAG_userPlaneIntegrityProtectionIndicator:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".userPlaneIntegrityProtectionIndicator: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            ais.readNull();
                            this.userPlaneIntegrityProtectionIndicator = true;
                            break;
                        case _TAG_DL_Buffering_Suggested_Packet_Count:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".dlBufferingSuggestedPacketCount: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            this.dlBufferingSuggestedPacketCount = ais.readInteger();
                            break;
                        case _TAG_Reset_Id_List:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".resetIdList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            AsnInputStream ais12 = ais.readSequenceStream();
                            this.resetIdList = new ArrayList<>();
                            while (true) {
                                if (ais12.available() == 0)
                                    break;

                                int tag12 = ais12.readTag();
                                if (tag12 != Tag.STRING_OCTET || ais12.getTagClass() != Tag.CLASS_UNIVERSAL || !ais12.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad resetIdList element tag or tagClass or is not primitive ",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                ResetIdImpl resetId = new ResetIdImpl();
                                (resetId).decodeAll(ais12);
                                resetIdList.add(resetId);
                            }
                            if (this.resetIdList.isEmpty() || this.resetIdList.size() > 50) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter resetIdList size must be from 1 to 50, found: "
                                        + this.resetIdList.size(),
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_EDRX_Cycle_Length_List:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".eDRXCycleLengthList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            AsnInputStream ais13 = ais.readSequenceStream();
                            this.eDRXCycleLengthList = new ArrayList<>();
                            while (true) {
                                if (ais13.available() == 0)
                                    break;

                                int tag13 = ais13.readTag();
                                if (tag13 != Tag.SEQUENCE || ais13.getTagClass() != Tag.CLASS_UNIVERSAL || ais13.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad eDRXCycleLengthList element tag or tagClass or is primitive ",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                EDRXCycleLengthImpl eDRXCycleLength = new EDRXCycleLengthImpl();
                                (eDRXCycleLength).decodeAll(ais13);
                                eDRXCycleLengthList.add(eDRXCycleLength);
                            }
                            if (this.eDRXCycleLengthList.isEmpty() || this.eDRXCycleLengthList.size() > 8) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter eDRXCycleLengthList size must be from 1 to 50, found: "
                                        + this.eDRXCycleLengthList.size(),
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_Ext_AccessRestrictionData:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".extAccessRestrictionData: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            this.extAccessRestrictionData = new ExtAccessRestrictionDataImpl();
                            ((ExtAccessRestrictionDataImpl) this.extAccessRestrictionData).decodeAll(ais);
                            break;
                        case _TAG_iabOperationAllowedIndicator:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".iabOperationAllowedIndicator: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            ais.readNull();
                            this.iabOperationAllowedIndicator = true;
                            break;

                        default:
                            ais.advanceElement();
                            break;
                    }
                    break;

                default:
                    ais.advanceElement();
                    break;
            }

            num++;
        }

        if (num == 0)
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": Needs at least 1 parameter, found " + num, MAPParsingComponentExceptionReason.MistypedParameter);
    }

    @Override
    public void encodeAll(AsnOutputStream asnOutputStream) throws MAPException {
        this.encodeAll(asnOutputStream, this.getTagClass(), this.getTag());
    }

    @Override
    public void encodeAll(AsnOutputStream asnOutputStream, int tagClass, int tag) throws MAPException {
        try {
            asnOutputStream.writeTag(tagClass, this.getIsPrimitive(), tag);
            int pos = asnOutputStream.StartContentDefiniteLength();
            this.encodeData(asnOutputStream);
            asnOutputStream.FinalizeContent(pos);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {

        if (this.bearerServiceList != null && (this.bearerServiceList.isEmpty() || this.bearerServiceList.size() > 50))
            throw new MAPException("bearerServiceList size must be from 1 to 50, found: " + this.bearerServiceList.size());

        if (this.teleserviceList != null && (this.teleserviceList.isEmpty() || this.teleserviceList.size() > 20))
            throw new MAPException("teleserviceList size must be from 1 to 20, found: " + this.teleserviceList.size());

        if (this.provisionedSS != null && (this.provisionedSS.isEmpty() || this.provisionedSS.size() > 30))
            throw new MAPException("provisionedSS size must be from 1 to 30, found: " + this.provisionedSS.size());

        if (this.regionalSubscriptionData != null
                && (this.regionalSubscriptionData.isEmpty() || this.regionalSubscriptionData.size() > 10))
            throw new MAPException("regionalSubscriptionData size must be from 1 to 10, found: "
                    + this.regionalSubscriptionData.size());

        if (this.vbsSubscriptionData != null && (this.vbsSubscriptionData.isEmpty() || this.vbsSubscriptionData.size() > 50))
            throw new MAPException("vbsSubscriptionData size must be from 1 to 50, found: " + this.vbsSubscriptionData.size());

        if (this.vgcsSubscriptionData != null
                && (this.vgcsSubscriptionData.isEmpty() || this.vgcsSubscriptionData.size() > 50))
            throw new MAPException("vgcsSubscriptionData size must be from 1 to 50, found: " + this.vgcsSubscriptionData.size());

        if (this.istAlertTimer != null && (this.istAlertTimer < 15 || this.istAlertTimer > 255))
            throw new MAPException("istAlertTimer must be from 15 to 255, found: " + this.istAlertTimer);

        if (this.imsi != null)
            ((IMSIImpl) this.imsi).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_imsi);

        if (this.msisdn != null)
            ((ISDNAddressStringImpl) this.msisdn).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_msisdn);

        if (this.category != null)
            ((CategoryImpl) this.category).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_category);

        if (this.subscriberStatus != null) {
            try {
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_subscriberStatus, this.subscriberStatus.getCode());
            } catch (IOException e) {
                throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter subscriberStatus", e);
            } catch (AsnException e) {
                throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter subscriberStatus", e);
            }
        }

        if (this.bearerServiceList != null) {
            try {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_bearerServiceList);
                int pos = asnOutputStream.StartContentDefiniteLength();
                for (ExtBearerServiceCode bearerItem : this.bearerServiceList) {
                    ((ExtBearerServiceCodeImpl) bearerItem).encodeAll(asnOutputStream);
                }
                asnOutputStream.FinalizeContent(pos);
            } catch (AsnException e) {
                throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
            }
        }

        if (this.teleserviceList != null) {
            try {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_teleserviceList);
                int pos = asnOutputStream.StartContentDefiniteLength();
                for (ExtTeleserviceCode teleserviceItem : this.teleserviceList) {
                    ((ExtTeleserviceCodeImpl) teleserviceItem).encodeAll(asnOutputStream);
                }
                asnOutputStream.FinalizeContent(pos);
            } catch (AsnException e) {
                throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
            }
        }

        if (this.provisionedSS != null) {
            try {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_provisionedSS);
                int pos = asnOutputStream.StartContentDefiniteLength();
                for (ExtSSInfo serviceItem : this.provisionedSS) {
                    ((ExtSSInfoImpl) serviceItem).encodeAll(asnOutputStream);
                }
                asnOutputStream.FinalizeContent(pos);
            } catch (AsnException e) {
                throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
            }
        }

        if (mapProtocolVersion >= 2) {
            if (this.odbData != null) {
                ((ODBDataImpl) this.odbData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_odb_Data);
            }

            if (this.roamingRestrictionDueToUnsupportedFeature) {
                try {
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_roamingRestrictionDueToUnsupportedFeature);
                } catch (IOException e) {
                    throw new MAPException("IOException when encoding " + _PrimitiveName
                            + " parameter roamingRestrictionDueToUnsupportedFeature: " + e.getMessage(), e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName
                            + " parameter roamingRestrictionDueToUnsupportedFeature: " + e.getMessage(), e);
                }
            }

            if (this.regionalSubscriptionData != null) {
                try {
                    asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_regionalSubscriptionData);
                    int pos = asnOutputStream.StartContentDefiniteLength();
                    for (ZoneCode elem : this.regionalSubscriptionData) {
                        ((ZoneCodeImpl) elem).encodeAll(asnOutputStream);
                    }
                    asnOutputStream.FinalizeContent(pos);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + ": regionalSubscriptionData"
                            + e.getMessage(), e);
                }
            }
        }

        if (this.vbsSubscriptionData != null) {
            try {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_vbsSubscriptionData);
                int pos = asnOutputStream.StartContentDefiniteLength();
                for (VoiceBroadcastData elem : this.vbsSubscriptionData) {
                    ((VoiceBroadcastDataImpl) elem).encodeAll(asnOutputStream);
                }
                asnOutputStream.FinalizeContent(pos);
            } catch (AsnException e) {
                throw new MAPException("AsnException when encoding " + _PrimitiveName + ".vbsSubscriptionData: "
                        + e.getMessage(), e);
            }
        }

        if (this.vgcsSubscriptionData != null) {
            try {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_vgcsSubscriptionData);
                int pos = asnOutputStream.StartContentDefiniteLength();
                for (VoiceGroupCallData elem : this.vgcsSubscriptionData) {
                    ((VoiceGroupCallDataImpl) elem).encodeAll(asnOutputStream);
                }
                asnOutputStream.FinalizeContent(pos);
            } catch (AsnException e) {
                throw new MAPException("AsnException when encoding " + _PrimitiveName + ".vgcsSubscriptionData: "
                        + e.getMessage(), e);
            }
        }

        if (this.vlrCamelSubscriptionInfo != null) {
            ((VlrCamelSubscriptionInfoImpl) this.vlrCamelSubscriptionInfo).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                    _TAG_vlrCamelSubscriptionInfo);
        }

        if (mapProtocolVersion >= 3) {

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_extContainer);

            if (this.naeaPreferredCI != null) {
                ((NAEAPreferredCIImpl) this.naeaPreferredCI)
                        .encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_naea_PreferredCI);
            }

            if (this.gprsSubscriptionData != null)
                ((GPRSSubscriptionDataImpl) this.gprsSubscriptionData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_gprsSubscriptionData);

            if (this.roamingRestrictedInSgsnDueToUnsupportedFeature) {
                try {
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_roamingRestrictedInSgsnDueToUnsupportedFeature);
                } catch (IOException e) {
                    throw new MAPException("IOException when encoding " + _PrimitiveName
                            + " parameter roamingRestrictedInSgsnDueToUnsupportedFeature: " + e.getMessage(), e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName
                            + " parameter roamingRestrictedInSgsnDueToUnsupportedFeature: " + e.getMessage(), e);
                }
            }

            if (this.networkAccessMode != null) {
                try {
                    asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_networkAccessMode, this.networkAccessMode.getCode());
                } catch (IOException e) {
                    throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter networkAccessMode", e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter networkAccessMode", e);
                }
            }

            if (this.lsaInformation != null) {
                ((LSAInformationImpl) this.lsaInformation).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_lsaInformation);
            }

            if (this.lmuIndicator) {
                try {
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_lmu_Indicator);
                } catch (IOException e) {
                    throw new MAPException("IOException when encoding " + _PrimitiveName + " parameter lmuIndicator: "
                            + e.getMessage(), e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + " parameter lmuIndicator: "
                            + e.getMessage(), e);
                }
            }

            if (this.lcsInformation != null) {
                ((LCSInformationImpl) this.lcsInformation).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_lcsInformation);
            }

            if (this.istAlertTimer != null) {
                try {
                    asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_istAlertTimer, this.istAlertTimer);
                } catch (IOException e) {
                    throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter istAlertTimer", e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter istAlertTimer", e);
                }
            }

            if (this.superChargerSupportedInHLR != null) {
                ((AgeIndicatorImpl) this.superChargerSupportedInHLR).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_superChargerSupportedInHLR);
            }

            if (this.mcSsInfo != null) {
                ((MCSSInfoImpl) this.mcSsInfo).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_mc_SS_Info);
            }

            if (this.csAllocationRetentionPriority != null) {
                ((CSAllocationRetentionPriorityImpl) this.csAllocationRetentionPriority).encodeAll(asnOutputStream,
                        Tag.CLASS_CONTEXT_SPECIFIC, _TAG_cs_AllocationRetentionPriority);
            }

            if (this.sgsnCamelSubscriptionInfo != null) {
                ((SGSNCAMELSubscriptionInfoImpl) this.sgsnCamelSubscriptionInfo).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_sgsn_CAMEL_SubscriptionInfo);
            }

            if (this.chargingCharacteristics != null)
                ((ChargingCharacteristicsImpl) this.chargingCharacteristics).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_chargingCharacteristics);

            if (this.accessRestrictionData != null) {
                ((AccessRestrictionDataImpl) this.accessRestrictionData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_accessRestrictionData);
            }

            if (this.icsIndicator != null) {
                try {
                    asnOutputStream.writeBoolean(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_ics_Indicator, this.icsIndicator);
                } catch (IOException e) {
                    throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter icsIndicator", e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter icsIndicator", e);
                }
            }

            if (this.epsSubscriptionData != null) {
                ((EPSSubscriptionDataImpl) this.epsSubscriptionData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_eps_SubscriptionData);
            }

            if (this.csgSubscriptionDataList != null) {
                try {
                    asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_csg_SubscriptionDataList);
                    int pos = asnOutputStream.StartContentDefiniteLength();
                    for (CSGSubscriptionData elem : this.csgSubscriptionDataList) {
                        ((CSGSubscriptionDataImpl) elem).encodeAll(asnOutputStream);
                    }
                    asnOutputStream.FinalizeContent(pos);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + ".csgSubscriptionDataList: "
                            + e.getMessage(), e);
                }
            }

            if (this.ueReachabilityRequestIndicator) {
                try {
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_ue_ReachabilityRequestIndicator);
                } catch (IOException e) {
                    throw new MAPException("IOException when encoding " + _PrimitiveName
                            + " parameter ueReachabilityRequestIndicator: " + e.getMessage(), e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName
                            + " parameter ueReachabilityRequestIndicator: " + e.getMessage(), e);
                }
            }

            if (this.sgsnNumber != null) {
                ((ISDNAddressStringImpl) this.sgsnNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_sgsn_Number);
            }

            if (this.mmeName != null) {
                ((DiameterIdentityImpl) this.mmeName).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_mme_Name);
            }

            if (this.subscribedPeriodicRAUTAUtimer != null) {
                try {
                    asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_subscribedPeriodicRAUTAUtimer,
                            this.subscribedPeriodicRAUTAUtimer);
                } catch (IOException e) {
                    throw new MAPException("IOException while encoding " + _PrimitiveName
                            + " parameter subscribedPeriodicRAUTAUtimer", e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException while encoding " + _PrimitiveName
                            + " parameter subscribedPeriodicRAUTAUtimer", e);
                }
            }

            if (this.vplmnLIPAAllowed) {
                try {
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_vplmnLIPAAllowed);
                } catch (IOException e) {
                    throw new MAPException("IOException when encoding " + _PrimitiveName + " parameter vplmnLIPAAllowed: "
                            + e.getMessage(), e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + " parameter vplmnLIPAAllowed: "
                            + e.getMessage(), e);
                }
            }

            if (this.mdtUserConsent != null) {
                try {
                    asnOutputStream.writeBoolean(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_mdtUserConsent, this.mdtUserConsent);
                } catch (IOException e) {
                    throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter mdtUserConsent", e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter mdtUserConsent", e);
                }
            }

            if (this.subscribedPeriodicLAUtimer != null) {
                try {
                    asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_subscribedPeriodicLAUtimer,
                            this.subscribedPeriodicLAUtimer);
                } catch (IOException e) {
                    throw new MAPException("IOException while encoding " + _PrimitiveName
                            + " parameter subscribedPeriodicLAUtimer", e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException while encoding " + _PrimitiveName
                            + " parameter subscribedPeriodicLAUtimer", e);
                }
            }

            if (this.vplmnCSGSubscriptionDataList != null) {
                try {
                    asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_VPLMNCSGSubscriptionDataList);
                    int pos = asnOutputStream.StartContentDefiniteLength();
                    for (CSGSubscriptionData elem : this.vplmnCSGSubscriptionDataList) {
                        ((CSGSubscriptionDataImpl) elem).encodeAll(asnOutputStream);
                    }
                    asnOutputStream.FinalizeContent(pos);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + ".vplmnCSGSubscriptionDataList: "
                            + e.getMessage(), e);
                }
            }

            if (this.additionalMSISDN != null)
                ((ISDNAddressStringImpl) this.additionalMSISDN).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_AdditionalMSISDN);

            if (this.psAndSMSOnlyServiceProvision) {
                try {
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_PSandSMSOnlyServiceProvision);
                } catch (IOException e) {
                    throw new MAPException("IOException when encoding " + _PrimitiveName
                            + " parameter psAndSMSOnlyServiceProvision: " + e.getMessage(), e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName
                            + " parameter psAndSMSOnlyServiceProvision: " + e.getMessage(), e);
                }
            }

            if (this.smsInSGSNAllowed) {
                try {
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SMSInSGSNAllowed);
                } catch (IOException e) {
                    throw new MAPException("IOException when encoding " + _PrimitiveName
                            + " parameter smsInSGSNAllowed: " + e.getMessage(), e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName
                            + " parameter smsInSGSNAllowed: " + e.getMessage(), e);
                }
            }

            if (this.csToPsSRVCCAllowedIndicator) {
                try {
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_CsToPsSRVCCAllowedIndicator);
                } catch (IOException e) {
                    throw new MAPException("IOException when encoding " + _PrimitiveName
                            + " parameter csToPsSRVCCAllowedIndicator: " + e.getMessage(), e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName
                            + " parameter csToPsSRVCCAllowedIndicator: " + e.getMessage(), e);
                }
            }

            if (this.pcscfRestorationRequest) {
                try {
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_PCSCFRestorationRequest);
                } catch (IOException e) {
                    throw new MAPException("IOException when encoding " + _PrimitiveName
                            + " parameter pcscfRestorationRequest: " + e.getMessage(), e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName
                            + " parameter pcscfRestorationRequest: " + e.getMessage(), e);
                }
            }

            if (this.adjacentAccessRestrictionDataList != null) {
                try {
                    asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_adjacentAccessRestrictionDataList);
                    int pos = asnOutputStream.StartContentDefiniteLength();
                    for (AdjacentAccessRestrictionData elem : this.adjacentAccessRestrictionDataList) {
                        ((AdjacentAccessRestrictionDataImpl) elem).encodeAll(asnOutputStream);
                    }
                    asnOutputStream.FinalizeContent(pos);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + ".adjacentAccessRestrictionDataList: "
                            + e.getMessage(), e);
                }
            }

            if (this.imsiGroupIdList != null) {
                try {
                    asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_IMSI_GroupId_List);
                    int pos = asnOutputStream.StartContentDefiniteLength();
                    for (IMSIGroupId elem : this.imsiGroupIdList) {
                        ((IMSIGroupIdImpl) elem).encodeAll(asnOutputStream);
                    }
                    asnOutputStream.FinalizeContent(pos);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + ".imsiGroupIdList: "
                            + e.getMessage(), e);
                }
            }

            if (this.ueUsageType != null) {
                ((UEUsageTypeImpl) this.ueUsageType).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_UE_Usage_Type);
            }

            if (this.userPlaneIntegrityProtectionIndicator) {
                try {
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_userPlaneIntegrityProtectionIndicator);
                } catch (IOException e) {
                    throw new MAPException("IOException when encoding " + _PrimitiveName + " parameter userPlaneIntegrityProtectionIndicator: "
                            + e.getMessage(), e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + " parameter userPlaneIntegrityProtectionIndicator: "
                            + e.getMessage(), e);
                }
            }

            if (this.dlBufferingSuggestedPacketCount != null) {
                try {
                    asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_DL_Buffering_Suggested_Packet_Count,
                            this.dlBufferingSuggestedPacketCount);
                } catch (IOException e) {
                    throw new MAPException("IOException while encoding " + _PrimitiveName
                            + " parameter dlBufferingSuggestedPacketCount", e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException while encoding " + _PrimitiveName
                            + " parameter dlBufferingSuggestedPacketCount", e);
                }
            }

            if (this.resetIdList != null) {
                try {
                    asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_Reset_Id_List);
                    int pos = asnOutputStream.StartContentDefiniteLength();
                    for (ResetId elem : this.resetIdList) {
                        ((ResetIdImpl) elem).encodeAll(asnOutputStream);
                    }
                    asnOutputStream.FinalizeContent(pos);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + ".resetIdList: " + e.getMessage(), e);
                }
            }

            if (this.eDRXCycleLengthList != null) {
                try {
                    asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_EDRX_Cycle_Length_List);
                    int pos = asnOutputStream.StartContentDefiniteLength();
                    for (EDRXCycleLength elem : this.eDRXCycleLengthList) {
                        ((EDRXCycleLengthImpl) elem).encodeAll(asnOutputStream);
                    }
                    asnOutputStream.FinalizeContent(pos);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + ".eDRXCycleLengthList: " + e.getMessage(), e);
                }
            }

            if (this.extAccessRestrictionData != null) {
                ((ExtAccessRestrictionDataImpl) this.extAccessRestrictionData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_Ext_AccessRestrictionData);
            }

            if (this.iabOperationAllowedIndicator) {
                try {
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_iabOperationAllowedIndicator);
                } catch (IOException e) {
                    throw new MAPException("IOException when encoding " + _PrimitiveName
                            + " parameter iabOperationAllowedIndicator: " + e.getMessage(), e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName
                            + " parameter iabOperationAllowedIndicator: " + e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.imsi != null) {
            sb.append("imsi=");
            sb.append(this.imsi);
            sb.append(", ");
        }

        if (this.msisdn != null) {
            sb.append("msisdn=");
            sb.append(this.msisdn);
            sb.append(", ");
        }

        if (this.category != null) {
            sb.append("category=");
            sb.append(this.category);
            sb.append(", ");
        }

        if (this.subscriberStatus != null) {
            sb.append("subscriberStatus=");
            sb.append(this.subscriberStatus);
            sb.append(", ");
        }

        if (this.bearerServiceList != null) {
            sb.append("bearerServiceList=[");
            boolean firstItem = true;
            for (ExtBearerServiceCode be : this.bearerServiceList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be);
            }
            sb.append("], ");
        }

        if (this.teleserviceList != null) {
            sb.append("teleserviceList=[");
            boolean firstItem = true;
            for (ExtTeleserviceCode be : this.teleserviceList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be);
            }
            sb.append("], ");
        }

        if (this.provisionedSS != null) {
            sb.append("provisionedSS=[");
            boolean firstItem = true;
            for (ExtSSInfo be : this.provisionedSS) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be);
            }
            sb.append("], ");
        }

        if (this.odbData != null) {
            sb.append("odbData=");
            sb.append(this.odbData);
            sb.append(", ");
        }

        if (this.roamingRestrictionDueToUnsupportedFeature) {
            sb.append("roamingRestrictionDueToUnsupportedFeature, ");
        }

        if (this.regionalSubscriptionData != null) {
            sb.append("regionalSubscriptionData=[");
            boolean firstItem = true;
            for (ZoneCode be : this.regionalSubscriptionData) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be);
            }
            sb.append("], ");
        }

        if (this.vbsSubscriptionData != null) {
            sb.append("vbsSubscriptionData=[");
            boolean firstItem = true;
            for (VoiceBroadcastData be : this.vbsSubscriptionData) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be);
            }
            sb.append("], ");
        }

        if (this.vgcsSubscriptionData != null) {
            sb.append("vgcsSubscriptionData=[");
            boolean firstItem = true;
            for (VoiceGroupCallData be : this.vgcsSubscriptionData) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be);
            }
            sb.append("], ");
        }

        if (this.vlrCamelSubscriptionInfo != null) {
            sb.append("vlrCamelSubscriptionInfo=");
            sb.append(this.vlrCamelSubscriptionInfo);
            sb.append(", ");
        }

        if (this.extensionContainer != null) {
            sb.append("extensionContainer=");
            sb.append(this.extensionContainer);
            sb.append(", ");
        }

        if (this.naeaPreferredCI != null) {
            sb.append("naeaPreferredCI=");
            sb.append(this.naeaPreferredCI);
            sb.append(", ");
        }

        if (this.gprsSubscriptionData != null) {
            sb.append("gprsSubscriptionData=");
            sb.append(this.gprsSubscriptionData);
            sb.append(", ");
        }

        if (this.roamingRestrictedInSgsnDueToUnsupportedFeature) {
            sb.append("roamingRestrictedInSgsnDueToUnsupportedFeature, ");
        }

        if (this.networkAccessMode != null) {
            sb.append("networkAccessMode=");
            sb.append(this.networkAccessMode);
            sb.append(", ");
        }

        if (this.lsaInformation != null) {
            sb.append("lsaInformation=");
            sb.append(this.lsaInformation);
            sb.append(", ");
        }

        if (this.lmuIndicator) {
            sb.append("lmuIndicator, ");
        }

        if (this.lcsInformation != null) {
            sb.append("lcsInformation=");
            sb.append(this.lcsInformation);
            sb.append(", ");
        }

        if (this.lcsInformation != null) {
            sb.append("lcsInformation=");
            sb.append(this.lcsInformation);
            sb.append(", ");
        }

        if (this.istAlertTimer != null) {
            sb.append("istAlertTimer=");
            sb.append(this.istAlertTimer);
            sb.append(", ");
        }

        if (this.superChargerSupportedInHLR != null) {
            sb.append("superChargerSupportedInHLR=");
            sb.append(this.superChargerSupportedInHLR);
            sb.append(", ");
        }

        if (this.mcSsInfo != null) {
            sb.append("mcSsInfo=");
            sb.append(this.mcSsInfo);
            sb.append(", ");
        }

        if (this.csAllocationRetentionPriority != null) {
            sb.append("csAllocationRetentionPriority=");
            sb.append(this.csAllocationRetentionPriority);
            sb.append(", ");
        }

        if (this.sgsnCamelSubscriptionInfo != null) {
            sb.append("sgsnCamelSubscriptionInfo=");
            sb.append(this.sgsnCamelSubscriptionInfo);
            sb.append(", ");
        }

        if (this.chargingCharacteristics != null) {
            sb.append("chargingCharacteristics=");
            sb.append(this.chargingCharacteristics);
            sb.append(", ");
        }

        if (this.accessRestrictionData != null) {
            sb.append("accessRestrictionData=");
            sb.append(this.accessRestrictionData);
            sb.append(", ");
        }

        if (this.icsIndicator != null) {
            sb.append("icsIndicator=");
            sb.append(this.icsIndicator);
            sb.append(", ");
        }

        if (this.epsSubscriptionData != null) {
            sb.append("epsSubscriptionData=");
            sb.append(this.epsSubscriptionData);
            sb.append(", ");
        }

        if (this.csgSubscriptionDataList != null) {
            sb.append("csgSubscriptionDataList=[");
            boolean firstItem = true;
            for (CSGSubscriptionData be : this.csgSubscriptionDataList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be);
            }
            sb.append("], ");
        }

        if (this.ueReachabilityRequestIndicator) {
            sb.append("ueReachabilityRequestIndicator, ");
        }

        if (this.sgsnNumber != null) {
            sb.append("sgsnNumber=");
            sb.append(this.sgsnNumber);
            sb.append(", ");
        }

        if (this.mmeName != null) {
            sb.append("mmeName=");
            sb.append(this.mmeName);
            sb.append(", ");
        }

        if (this.subscribedPeriodicRAUTAUtimer != null) {
            sb.append("subscribedPeriodicRAUTAUtimer=");
            sb.append(this.subscribedPeriodicRAUTAUtimer);
            sb.append(", ");
        }

        if (this.vplmnLIPAAllowed) {
            sb.append("vplmnLIPAAllowed, ");
        }

        if (this.mdtUserConsent != null) {
            sb.append("mdtUserConsent=");
            sb.append(this.mdtUserConsent);
            sb.append(", ");
        }

        if (this.subscribedPeriodicLAUtimer != null) {
            sb.append("subscribedPeriodicLAUtimer=");
            sb.append(this.subscribedPeriodicLAUtimer);
            sb.append(", ");
        }

        if (this.vplmnCSGSubscriptionDataList != null) {
            sb.append("vplmnCSGSubscriptionDataList=[");
            boolean firstItem = true;
            for (CSGSubscriptionData be : this.vplmnCSGSubscriptionDataList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be);
            }
            sb.append("], ");
        }

        if (this.additionalMSISDN != null) {
            sb.append("additionalMSISDN=");
            sb.append(this.additionalMSISDN);
            sb.append(", ");
        }

        if (this.psAndSMSOnlyServiceProvision) {
            sb.append("psAndSMSOnlyServiceProvision, ");
        }

        if (this.smsInSGSNAllowed) {
            sb.append("smsInSGSNAllowed, ");
        }

        if (csToPsSRVCCAllowedIndicator) {
            sb.append("csToPsSRVCCAllowedIndicator, ");
        }

        if (this.pcscfRestorationRequest) {
            sb.append("pcscfRestorationRequest, ");
        }

        if (this.adjacentAccessRestrictionDataList != null) {
            sb.append("adjacentAccessRestrictionDataList=[");
            boolean firstItem = true;
            for (AdjacentAccessRestrictionData be : this.adjacentAccessRestrictionDataList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be);
            }
            sb.append("], ");
        }

        if (this.imsiGroupIdList != null) {
            sb.append("imsiGroupIdList=[");
            boolean firstItem = true;
            for (IMSIGroupId be : this.imsiGroupIdList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be);
            }
            sb.append("], ");
        }

        if (this.ueUsageType != null) {
            sb.append("ueUsageType=");
            sb.append(this.ueUsageType);
            sb.append(", ");
        }

        if (this.userPlaneIntegrityProtectionIndicator) {
            sb.append("userPlaneIntegrityProtectionIndicator, ");
        }

        if (this.dlBufferingSuggestedPacketCount != null) {
            sb.append("dlBufferingSuggestedPacketCount=");
            sb.append(this.dlBufferingSuggestedPacketCount);
            sb.append(", ");
        }

        if (this.resetIdList != null) {
            sb.append("resetIdList=[");
            boolean firstItem = true;
            for (ResetId be : this.resetIdList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be);
            }
            sb.append("], ");
        }

        if (this.eDRXCycleLengthList != null) {
            sb.append("eDRXCycleLengthList=[");
            boolean firstItem = true;
            for (EDRXCycleLength be : this.eDRXCycleLengthList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be);
            }
            sb.append("], ");
        }

        if (this.extAccessRestrictionData != null) {
            sb.append("extAccessRestrictionData=");
            sb.append(this.extAccessRestrictionData);
            sb.append(", ");
        }

        if (this.iabOperationAllowedIndicator) {
            sb.append("iabOperationAllowedIndicator, ");
        }

        sb.append("mapProtocolVersion=");
        sb.append(this.mapProtocolVersion);

        sb.append("]");

        return sb.toString();
    }

}
