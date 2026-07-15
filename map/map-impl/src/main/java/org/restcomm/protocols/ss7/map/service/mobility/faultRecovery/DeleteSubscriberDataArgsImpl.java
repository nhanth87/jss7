package org.restcomm.protocols.ss7.map.service.mobility.faultRecovery;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.DeleteSubscriberDataArgs;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.EPSSubscriptionDataWithdraw;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBasicServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.GPRSSubscriptionDataWithdraw;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAInformationWithdraw;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SpecificCSIWithdraw;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ZoneCode;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSCode;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.EPSSubscriptionDataWithdrawImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtBasicServiceCodeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.GPRSSubscriptionDataWithdrawImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.LSAInformationWithdrawImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SpecificCSIWithdrawImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ZoneCodeImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.SSCodeImpl;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com">Fernando Mendioroz</a>
 */
public class DeleteSubscriberDataArgsImpl extends SequenceBase implements DeleteSubscriberDataArgs, MAPAsnPrimitive {

    public static final String _PrimitiveName = "DeleteSubscriberDataArgs";

    protected static final int _TAG_imsi = 0;
    protected static final int _TAG_basicServiceList = 1;
    protected static final int _TAG_ss_List = 2;
    protected static final int _TAG_roamingRestrictionDueToUnsupportedFeature = 4;
    protected static final int _TAG_regionalSubscriptionIdentifier = 5;
    protected static final int _TAG_vbsGroupIndication = 7;
    protected static final int _TAG_vgcsGroupIndication = 8;
    protected static final int _TAG_camelSubscriptionInfoWithdraw = 9;
    protected static final int _TAG_extensionContainer = 6;
    protected static final int _TAG_gprsSubscriptionDataWithdraw = 10;
    protected static final int _TAG_roamingRestrictedInSgsnDueToUnsuppportedFeature = 11;
    protected static final int _TAG_lsaInformationWithdraw = 12;
    protected static final int _TAG_gmlc_ListWithdraw = 13;
    protected static final int _TAG_istInformationWithdraw = 14;
    protected static final int _TAG_specificCSI_Withdraw = 15;
    protected static final int _TAG_chargingCharacteristicsWithdraw = 16;
    protected static final int _TAG_stn_srWithdraw = 17;
    protected static final int _TAG_epsSubscriptionDataWithdraw = 18;
    protected static final int _TAG_apn_oi_replacementWithdraw = 19;
    protected static final int _TAG_csg_SubscriptionDeleted = 20;
    protected static final int _TAG_subscribedPeriodicTAU_RAU_TimerWithdraw = 22;
    protected static final int _TAG_subscribedPeriodicLAUTimerWithdraw = 23;
    protected static final int _TAG_subscribed_vsrvccWithdraw = 21;
    protected static final int _TAG_VPLMN_CSG_SubscriptionDeleted = 24;
    protected static final int _TAG_additionalMSISDN_Withdraw = 25;
    protected static final int _TAG_CS_to_PS_SRVCC_Withdraw = 26;
    protected static final int _TAG_imsiGroupIdList_Withdraw  = 27;
    protected static final int _TAG_userPlaneIntegrityProtectionWithdraw = 28;
    protected static final int _TAG_DL_Buffering_Suggested_Packet_Count_Withdraw = 29;
    protected static final int _TAG_UE_UsageTypeWithdraw = 30;
    protected static final int _TAG_reset_idsWithdraw = 31;
    protected static final int _TAG_iab_OperationWithdraw = 32;

    private IMSI imsi;
    private ArrayList<ExtBasicServiceCode> basicServiceList;
    private ArrayList<SSCode> ssList;
    private boolean roamingRestrictionDueToUnsupportedFeature;
    private ZoneCode regionalSubscriptionIdentifier;
    private boolean vbsGroupIndication;
    private boolean vgcsGroupIndication;
    private boolean camelSubscriptionInfoWithdraw;
    private MAPExtensionContainer extensionContainer;
    private GPRSSubscriptionDataWithdraw gprsSubscriptionDataWithdraw;
    private boolean roamingRestrictedInSgsnDueToUnsuppportedFeature;
    private LSAInformationWithdraw lsaInformationWithdraw;
    private boolean gmlcListWithdraw;
    private boolean istInformationWithdraw;
    private SpecificCSIWithdraw specificCSIWithdraw;
    private boolean chargingCharacteristicsWithdraw;
    private boolean stnSrWithdraw;
    private EPSSubscriptionDataWithdraw epsSubscriptionDataWithdraw;
    private boolean apnOiReplacementWithdraw;
    private boolean csgSubscriptionDeleted;
    private boolean subscribedPeriodicTAURAUTimerWithdraw;
    private boolean subscribedPeriodicLAUTimerWithdraw;
    private boolean subscribedVsrvccWithdraw;
    private boolean vplmnCsgSubscriptionDeleted;
    private boolean additionalMSISDNWithdraw;
    private boolean csToPsSRVCCWithdraw;
    private boolean imsiGroupIdListWithdraw;
    private boolean userPlaneIntegrityProtectionWithdraw;
    private boolean dlBufferingSuggestedPacketCountWithdraw;
    private boolean ueUsageTypeWithdraw;
    private boolean resetIdsWithdraw;
    private boolean iabOperationWithdraw;

    public DeleteSubscriberDataArgsImpl() {
        super(_PrimitiveName);
    }

    public DeleteSubscriberDataArgsImpl(IMSI imsi, ArrayList<ExtBasicServiceCode> basicServiceList, ArrayList<SSCode> ssList,
                                        boolean roamingRestrictionDueToUnsupportedFeature, ZoneCode regionalSubscriptionIdentifier, boolean vbsGroupIndication,
                                        boolean vgcsGroupIndication, boolean camelSubscriptionInfoWithdraw, MAPExtensionContainer extensionContainer,
                                        GPRSSubscriptionDataWithdraw gprsSubscriptionDataWithdraw, boolean roamingRestrictedInSgsnDueToUnsuppportedFeature,
                                        LSAInformationWithdraw lsaInformationWithdraw, boolean gmlcListWithdraw, boolean istInformationWithdraw, SpecificCSIWithdraw specificCSIWithdraw,
                                        boolean chargingCharacteristicsWithdraw, boolean stnSrWithdraw, EPSSubscriptionDataWithdraw epsSubscriptionDataWithdraw,
                                        boolean apnOiReplacementWithdraw, boolean csgSubscriptionDeleted, boolean subscribedPeriodicTAURAUTimerWithdraw,
                                        boolean subscribedPeriodicLAUTimerWithdraw, boolean subscribedVsrvccWithdraw, boolean vplmnCsgSubscriptionDeleted,
                                        boolean additionalMSISDNWithdraw, boolean csToPsSRVCCWithdraw, boolean imsiGroupIdListWithdraw, boolean userPlaneIntegrityProtectionWithdraw,
                                        boolean dlBufferingSuggestedPacketCountWithdraw, boolean ueUsageTypeWithdraw, boolean resetIdsWithdraw, boolean iabOperationWithdraw) {
        super(_PrimitiveName);
        this.imsi = imsi;
        this.basicServiceList = basicServiceList;
        this.ssList = ssList;
        this.roamingRestrictionDueToUnsupportedFeature = roamingRestrictionDueToUnsupportedFeature;
        this.regionalSubscriptionIdentifier = regionalSubscriptionIdentifier;
        this.vbsGroupIndication = vbsGroupIndication;
        this.vgcsGroupIndication = vgcsGroupIndication;
        this.camelSubscriptionInfoWithdraw = camelSubscriptionInfoWithdraw;
        this.extensionContainer = extensionContainer;
        this.gprsSubscriptionDataWithdraw = gprsSubscriptionDataWithdraw;
        this.roamingRestrictedInSgsnDueToUnsuppportedFeature = roamingRestrictedInSgsnDueToUnsuppportedFeature;
        this.lsaInformationWithdraw = lsaInformationWithdraw;
        this.gmlcListWithdraw = gmlcListWithdraw;
        this.istInformationWithdraw = istInformationWithdraw;
        this.specificCSIWithdraw = specificCSIWithdraw;
        this.chargingCharacteristicsWithdraw = chargingCharacteristicsWithdraw;
        this.stnSrWithdraw = stnSrWithdraw;
        this.epsSubscriptionDataWithdraw = epsSubscriptionDataWithdraw;
        this.apnOiReplacementWithdraw = apnOiReplacementWithdraw;
        this.csgSubscriptionDeleted = csgSubscriptionDeleted;
        this.subscribedPeriodicTAURAUTimerWithdraw = subscribedPeriodicTAURAUTimerWithdraw;
        this.subscribedPeriodicLAUTimerWithdraw = subscribedPeriodicLAUTimerWithdraw;
        this.subscribedVsrvccWithdraw = subscribedVsrvccWithdraw;
        this.vplmnCsgSubscriptionDeleted = vplmnCsgSubscriptionDeleted;
        this.additionalMSISDNWithdraw = additionalMSISDNWithdraw;
        this.csToPsSRVCCWithdraw = csToPsSRVCCWithdraw;
        this.imsiGroupIdListWithdraw = imsiGroupIdListWithdraw;
        this.userPlaneIntegrityProtectionWithdraw = userPlaneIntegrityProtectionWithdraw;
        this.dlBufferingSuggestedPacketCountWithdraw = dlBufferingSuggestedPacketCountWithdraw;
        this.ueUsageTypeWithdraw = ueUsageTypeWithdraw;
        this.resetIdsWithdraw = resetIdsWithdraw;
        this.iabOperationWithdraw = iabOperationWithdraw;
    }

    @Override
    public IMSI getImsi() {
        return imsi;
    }

    @Override
    public ArrayList<ExtBasicServiceCode> getBasicServiceList() {
        return basicServiceList;
    }

    @Override
    public ArrayList<SSCode> getSsList() {
        return ssList;
    }

    @Override
    public boolean getRoamingRestrictionDueToUnsupportedFeature() {
        return roamingRestrictionDueToUnsupportedFeature;
    }

    @Override
    public ZoneCode getRegionalSubscriptionIdentifier() {
        return regionalSubscriptionIdentifier;
    }

    @Override
    public boolean getVbsGroupIndication() {
        return vbsGroupIndication;
    }

    @Override
    public boolean getVgcsGroupIndication() {
        return vgcsGroupIndication;
    }

    @Override
    public boolean getCamelSubscriptionInfoWithdraw() {
        return camelSubscriptionInfoWithdraw;
    }

    @Override
    public MAPExtensionContainer getExtensionContainer() {
        return extensionContainer;
    }

    @Override
    public GPRSSubscriptionDataWithdraw getGPRSSubscriptionDataWithdraw() {
        return gprsSubscriptionDataWithdraw;
    }

    @Override
    public boolean getRoamingRestrictedInSgsnDueToUnsuppportedFeature() {
        return roamingRestrictedInSgsnDueToUnsuppportedFeature;
    }

    @Override
    public LSAInformationWithdraw getLSAInformationWithdraw() {
        return lsaInformationWithdraw;
    }

    @Override
    public boolean getGmlcListWithdraw() {
        return gmlcListWithdraw;
    }

    @Override
    public boolean getIstInformationWithdraw() {
        return istInformationWithdraw;
    }

    @Override
    public SpecificCSIWithdraw getSpecificCSIWithdraw() {
        return specificCSIWithdraw;
    }

    @Override
    public boolean getChargingCharacteristicsWithdraw() {
        return chargingCharacteristicsWithdraw;
    }

    @Override
    public boolean getStnSrWithdraw() {
        return stnSrWithdraw;
    }

    @Override
    public EPSSubscriptionDataWithdraw getEPSSubscriptionDataWithdraw() {
        return epsSubscriptionDataWithdraw;
    }

    @Override
    public boolean getApnOiReplacementWithdraw() {
        return apnOiReplacementWithdraw;
    }

    @Override
    public boolean getCsgSubscriptionDeleted() {
        return csgSubscriptionDeleted;
    }

    @Override
    public boolean getSubscribedPeriodicTAURAUTimerWithdraw() {
        return subscribedPeriodicTAURAUTimerWithdraw;
    }

    @Override
    public boolean getSubscribedPeriodicLAUTimerWithdraw() {
        return subscribedPeriodicLAUTimerWithdraw;
    }

    @Override
    public boolean getSubscribedVsrvccWithdraw() {
        return subscribedVsrvccWithdraw;
    }

    @Override
    public boolean getVplmnCsgSubscriptionDeleted() {
        return vplmnCsgSubscriptionDeleted;
    }

    @Override
    public boolean getAdditionalMSISDNWithdraw() {
        return additionalMSISDNWithdraw;
    }

    @Override
    public boolean getCsToPsSRVCCWithdraw() {
        return csToPsSRVCCWithdraw;
    }

    @Override
    public boolean getImsiGroupIdListWithdraw() {
        return imsiGroupIdListWithdraw;
    }

    @Override
    public boolean getUserPlaneIntegrityProtectionWithdraw() {
        return userPlaneIntegrityProtectionWithdraw;
    }

    @Override
    public boolean getDlBufferingSuggestedPacketCountWithdraw() {
        return dlBufferingSuggestedPacketCountWithdraw;
    }

    @Override
    public boolean getUeUsageTypeWithdraw() {
        return ueUsageTypeWithdraw;
    }

    @Override
    public boolean getResetIdsWithdraw() {
        return resetIdsWithdraw;
    }

    @Override
    public boolean getIabOperationWithdraw() {
        return iabOperationWithdraw;
    }

    @Override
    protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.imsi = null;
        this.basicServiceList = null;
        this.ssList = null;
        this.roamingRestrictionDueToUnsupportedFeature = false;
        this.regionalSubscriptionIdentifier = null;
        this.vbsGroupIndication = false;
        this.vgcsGroupIndication = false;
        this.camelSubscriptionInfoWithdraw = false;
        this.extensionContainer = null;
        this.gprsSubscriptionDataWithdraw = null;
        this.roamingRestrictedInSgsnDueToUnsuppportedFeature = false;
        this.lsaInformationWithdraw = null;
        this.gmlcListWithdraw = false;
        this.istInformationWithdraw = false;
        this.specificCSIWithdraw = null;
        this.chargingCharacteristicsWithdraw = false;
        this.stnSrWithdraw = false;
        this.epsSubscriptionDataWithdraw = null;
        this.apnOiReplacementWithdraw = false;
        this.csgSubscriptionDeleted = false;
        this.subscribedPeriodicTAURAUTimerWithdraw = false;
        this.subscribedPeriodicLAUTimerWithdraw = false;
        this.subscribedVsrvccWithdraw = false;
        this.vplmnCsgSubscriptionDeleted = false;
        this.additionalMSISDNWithdraw = false;
        this.csToPsSRVCCWithdraw = false;
        this.imsiGroupIdListWithdraw = false;
        this.userPlaneIntegrityProtectionWithdraw = false;
        this.dlBufferingSuggestedPacketCountWithdraw = false;
        this.ueUsageTypeWithdraw = false;
        this.resetIdsWithdraw = false;
        this.iabOperationWithdraw = false;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            switch (num) {

                default:
                    if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                        switch (tag) {
                            case _TAG_imsi:
                                // imsi [0] IMSI
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".imsi: Parameter 0 bad tag or tag class or not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.imsi = new IMSIImpl();
                                ((IMSIImpl) this.imsi).decodeAll(ais);
                                break;
                            case _TAG_basicServiceList:
                                // basicServiceList [1] BasicServiceList OPTIONAL
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ".basicServiceList: " +
                                            "Parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);

                                AsnInputStream ais1 = ais.readSequenceStream();
                                this.basicServiceList = new ArrayList<>();
                                while (true) {
                                    if (ais1.available() == 0)
                                        break;

                                    ais1.readTag();

                                    ExtBasicServiceCodeImpl extBasicServiceCode = new ExtBasicServiceCodeImpl();
                                    extBasicServiceCode.decodeAll(ais1);
                                    this.basicServiceList.add(extBasicServiceCode);
                                }
                                if (this.basicServiceList.isEmpty() || this.basicServiceList.size() > 70) {
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": Parameter basicServiceList size must be from 1 to 70, found: " + this.ssList.size(),
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                }
                                break;

                            case _TAG_ss_List:
                                // ss-List [2] SS-List OPTIONAL
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ".ssList: Parameter is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                AsnInputStream ais2 = ais.readSequenceStream();
                                this.ssList = new ArrayList<>();
                                while (true) {
                                    if (ais2.available() == 0)
                                        break;

                                    int tag2 = ais2.readTag();
                                    if (tag2 != Tag.STRING_OCTET || ais2.getTagClass() != Tag.CLASS_UNIVERSAL || !ais2.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + ": bad ssList element tag or tagClass or is not primitive ",
                                                MAPParsingComponentExceptionReason.MistypedParameter);

                                    SSCodeImpl ssCode = new SSCodeImpl();
                                    ssCode.decodeAll(ais2);
                                    this.ssList.add(ssCode);
                                }
                                if (this.ssList.isEmpty() || this.ssList.size() > 30) {
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": Parameter ssList size must be from 1 to 30, found: " + this.ssList.size(),
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                }
                                break;

                            case _TAG_roamingRestrictionDueToUnsupportedFeature:
                                // roamingRestrictionDueToUnsupportedFeature [4] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".roamingRestrictionDueToUnsupportedFeature: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.roamingRestrictionDueToUnsupportedFeature = true;
                                break;
                            case _TAG_regionalSubscriptionIdentifier:
                                // regionalSubscriptionIdentifier [5] ZoneCode OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".regionalSubscriptionIdentifier: Parameter regionalSubscriptionIdentifier is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.regionalSubscriptionIdentifier = new ZoneCodeImpl();
                                ((ZoneCodeImpl) this.regionalSubscriptionIdentifier).decodeAll(ais);
                                break;
                            case _TAG_vbsGroupIndication:
                                // vbsGroupIndication [7] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException(
                                            "Error while decoding " + _PrimitiveName + ".vbsGroupIndication: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.vbsGroupIndication = true;
                                break;
                            case _TAG_vgcsGroupIndication:
                                // vgcsGroupIndication [8] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".vgcsGroupIndication: Parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.vgcsGroupIndication = true;
                                break;
                            case _TAG_camelSubscriptionInfoWithdraw:
                                // camelSubscriptionInfoWithdraw [9] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".camelSubscriptionInfoWithdraw: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.camelSubscriptionInfoWithdraw = true;
                                break;
                            case _TAG_extensionContainer:
                                // extensionContainer [6] ExtensionContainer OPTIONAL
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".extensionContainer: Parameter extensionContainer is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.extensionContainer = new MAPExtensionContainerImpl();
                                ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                                break;
                            case _TAG_gprsSubscriptionDataWithdraw:
                                // gprsSubscriptionDataWithdraw [10] GPRSSubscriptionDataWithdraw OPTIONAL
                                AsnInputStream ais3 = ais.readSequenceStream();
                                ais3.readTag();
                                this.gprsSubscriptionDataWithdraw = new GPRSSubscriptionDataWithdrawImpl();
                                ((GPRSSubscriptionDataWithdrawImpl) this.gprsSubscriptionDataWithdraw).decodeAll(ais3);
                                break;
                            case _TAG_roamingRestrictedInSgsnDueToUnsuppportedFeature:
                                // roamingRestrictedInSgsnDueToUnsuppportedFeature [11] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".roamingRestrictedInSgsnDueToUnsuppportedFeature: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.roamingRestrictedInSgsnDueToUnsuppportedFeature = true;
                                break;
                            case _TAG_lsaInformationWithdraw:
                                // lsaInformationWithdraw [12] LSAInformationWithdraw OPTIONAL
                                AsnInputStream ais4 = ais.readSequenceStream();
                                ais4.readTag();
                                this.lsaInformationWithdraw = new LSAInformationWithdrawImpl();
                                ((LSAInformationWithdrawImpl) this.lsaInformationWithdraw).decodeAll(ais4);
                                break;
                            case _TAG_gmlc_ListWithdraw:
                                // gmlc-ListWithdraw [13] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".gmlcListWithdraw: Parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.gmlcListWithdraw = true;
                                break;
                            case _TAG_istInformationWithdraw:
                                // istInformationWithdraw [14] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".istInformationWithdraw: Parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.istInformationWithdraw = true;
                                break;
                            case _TAG_specificCSI_Withdraw:
                                // specificCSI-Withdraw [15] SpecificCSI-Withdraw OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".specificCSIWithdraw: Parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                                this.specificCSIWithdraw = new SpecificCSIWithdrawImpl();
                                ((SpecificCSIWithdrawImpl) this.specificCSIWithdraw).decodeAll(ais);
                                break;
                            case _TAG_chargingCharacteristicsWithdraw:
                                // chargingCharacteristicsWithdraw [16] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".chargingCharacteristicsWithdraw: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.chargingCharacteristicsWithdraw = true;
                                break;
                            case _TAG_stn_srWithdraw:
                                // stn-srWithdraw [17] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".stnSrWithdraw: Parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.stnSrWithdraw = true;
                                break;
                            case _TAG_epsSubscriptionDataWithdraw:
                                // epsSubscriptionDataWithdraw [18] EPS-SubscriptionDataWithdraw OPTIONAL
                                AsnInputStream ais5 = ais.readSequenceStream();
                                ais5.readTag();
                                this.epsSubscriptionDataWithdraw = new EPSSubscriptionDataWithdrawImpl();
                                ((EPSSubscriptionDataWithdrawImpl) this.epsSubscriptionDataWithdraw).decodeAll(ais5);
                                break;
                            case _TAG_apn_oi_replacementWithdraw:
                                // apn-oi-replacementWithdraw [19] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".apnOiReplacementWithdraw: Parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.apnOiReplacementWithdraw = true;
                                break;
                            case _TAG_csg_SubscriptionDeleted:
                                // csg-SubscriptionDeleted [20] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".csgSubscriptionDeleted: Parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.csgSubscriptionDeleted = true;
                                break;
                            case _TAG_subscribedPeriodicTAU_RAU_TimerWithdraw:
                                // subscribedPeriodicTAU-RAU-TimerWithdraw [22] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".subscribedPeriodicTAURAUTimerWithdraw: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.subscribedPeriodicTAURAUTimerWithdraw = true;
                                break;
                            case _TAG_subscribedPeriodicLAUTimerWithdraw:
                                // subscribedPeriodicLAU-TimerWithdraw [23] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".subscribedPeriodicLAUTimerWithdraw: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.subscribedPeriodicLAUTimerWithdraw = true;
                                break;
                            case _TAG_subscribed_vsrvccWithdraw:
                                // subscribed-vsrvccWithdraw [21] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".subscribedVsrvccWithdraw: Parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.subscribedVsrvccWithdraw = true;
                                break;
                            case _TAG_VPLMN_CSG_SubscriptionDeleted:
                                // vplmn-Csg-SubscriptionDeleted [24] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".vplmnCsgSubscriptionDeleted: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.vplmnCsgSubscriptionDeleted = true;
                                break;
                            case _TAG_additionalMSISDN_Withdraw:
                                // additionalMSISDN-Withdraw [25] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".additionalMSISDNWithdraw: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.additionalMSISDNWithdraw = true;
                                break;
                            case _TAG_CS_to_PS_SRVCC_Withdraw:
                                // cs-to-ps-SRVCC-Withdraw [26] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".csToPsSRVCCWithdraw: Parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.csToPsSRVCCWithdraw = true;
                                break;
                            case _TAG_imsiGroupIdList_Withdraw:
                                // imsiGroupIdList-Withdraw [27] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".imsiGroupIdListWithdraw: Parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.imsiGroupIdListWithdraw = true;
                                break;
                            case _TAG_userPlaneIntegrityProtectionWithdraw:
                                // userPlaneIntegrityProtectionWithdraw [28] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".userPlaneIntegrityProtectionWithdraw: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.userPlaneIntegrityProtectionWithdraw = true;
                                break;
                            case _TAG_DL_Buffering_Suggested_Packet_Count_Withdraw:
                                // dl-Buffering-Suggested-Packet-Count-Withdraw [29] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".dlBufferingSuggestedPacketCountWithdraw: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.dlBufferingSuggestedPacketCountWithdraw = true;
                                break;
                            case _TAG_UE_UsageTypeWithdraw:
                                // ue-UsageTypeWithdraw [30] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".ueUsageTypeWithdraw: Parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.ueUsageTypeWithdraw = true;
                                break;
                            case _TAG_reset_idsWithdraw:
                                // reset-idsWithdraw [31] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".resetIdsWithdraw: Parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.resetIdsWithdraw = true;
                                break;
                            case _TAG_iab_OperationWithdraw:
                                // iab-OperationWithdraw [32] NULL OPTIONAL
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".iabOperationWithdraw: Parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.iabOperationWithdraw = true;
                                break;

                            default:
                                ais.advanceElement();
                                break;
                        }
                    } else {

                        ais.advanceElement();
                    }
                    break;
            }

            num++;
        }

        if (num < 1)
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ": Needs at least 1 mandatory parameter, found " + num,
                    MAPParsingComponentExceptionReason.MistypedParameter);
    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        try {
            if (this.imsi == null)
                throw new MAPException("Error when encoding " + _PrimitiveName + ", IMSI parameter must not be null within DeleteSubscriberDataArgs");

            ((IMSIImpl) this.imsi).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_imsi);

            if (this.basicServiceList != null) {
                try {
                    asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_basicServiceList);
                    int pos = asnOutputStream.StartContentDefiniteLength();
                    for (ExtBasicServiceCode item : this.basicServiceList) {
                        ((ExtBasicServiceCodeImpl) item).encodeAll(asnOutputStream);
                    }
                    asnOutputStream.FinalizeContent(pos);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + ".basicServiceList: " + e.getMessage(), e);
                }
            }

            if (this.ssList != null) {
                try {
                    asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_ss_List);
                    int pos = asnOutputStream.StartContentDefiniteLength();
                    for (SSCode item : this.ssList) {
                        ((SSCodeImpl) item).encodeAll(asnOutputStream);
                    }
                    asnOutputStream.FinalizeContent(pos);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + ".ssList: " + e.getMessage(), e);
                }
            }
            if (roamingRestrictionDueToUnsupportedFeature)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_roamingRestrictionDueToUnsupportedFeature);
            if (this.regionalSubscriptionIdentifier != null)
                ((ZoneCodeImpl) this.regionalSubscriptionIdentifier).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_regionalSubscriptionIdentifier);
            if (vbsGroupIndication)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_vbsGroupIndication);
            if (vgcsGroupIndication)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_vgcsGroupIndication);
            if (camelSubscriptionInfoWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_camelSubscriptionInfoWithdraw);
            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_extensionContainer);

            if (this.gprsSubscriptionDataWithdraw != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_gprsSubscriptionDataWithdraw);
                int pos = asnOutputStream.StartContentDefiniteLength();
                ((GPRSSubscriptionDataWithdrawImpl) this.gprsSubscriptionDataWithdraw).encodeAll(asnOutputStream);
                asnOutputStream.FinalizeContent(pos);
            }
            if (roamingRestrictedInSgsnDueToUnsuppportedFeature)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_roamingRestrictedInSgsnDueToUnsuppportedFeature);
            if (this.lsaInformationWithdraw != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_lsaInformationWithdraw);
                int pos = asnOutputStream.StartContentDefiniteLength();
                ((LSAInformationWithdrawImpl) this.lsaInformationWithdraw).encodeAll(asnOutputStream);
                asnOutputStream.FinalizeContent(pos);
            }
            if (gmlcListWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_gmlc_ListWithdraw);
            if (istInformationWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_istInformationWithdraw);
            if (this.specificCSIWithdraw != null)
                ((SpecificCSIWithdrawImpl) this.specificCSIWithdraw).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_specificCSI_Withdraw);
            if (chargingCharacteristicsWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_chargingCharacteristicsWithdraw);
            if (stnSrWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_stn_srWithdraw);
            if (this.epsSubscriptionDataWithdraw != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_epsSubscriptionDataWithdraw);
                int pos = asnOutputStream.StartContentDefiniteLength();
                ((EPSSubscriptionDataWithdrawImpl) this.epsSubscriptionDataWithdraw).encodeAll(asnOutputStream);
                asnOutputStream.FinalizeContent(pos);
            }
            if (apnOiReplacementWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_apn_oi_replacementWithdraw);
            if (csgSubscriptionDeleted)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_csg_SubscriptionDeleted);
            if (subscribedPeriodicTAURAUTimerWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_subscribedPeriodicTAU_RAU_TimerWithdraw);
            if (subscribedPeriodicLAUTimerWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_subscribedPeriodicLAUTimerWithdraw);
            if (subscribedVsrvccWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_subscribed_vsrvccWithdraw);
            if (vplmnCsgSubscriptionDeleted)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_VPLMN_CSG_SubscriptionDeleted);
            if (additionalMSISDNWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_additionalMSISDN_Withdraw);
            if (csToPsSRVCCWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_CS_to_PS_SRVCC_Withdraw);
            if (imsiGroupIdListWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_imsiGroupIdList_Withdraw);
            if (userPlaneIntegrityProtectionWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_userPlaneIntegrityProtectionWithdraw);
            if (dlBufferingSuggestedPacketCountWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_DL_Buffering_Suggested_Packet_Count_Withdraw);
            if (ueUsageTypeWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_UE_UsageTypeWithdraw);
            if (resetIdsWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_reset_idsWithdraw);
            if (iabOperationWithdraw)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_iab_OperationWithdraw);

        } catch (IOException e) {
            throw new MAPException("IOException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.imsi != null) {
            sb.append("imsi=");
            sb.append(imsi);
            sb.append(", ");
        }
        if (this.basicServiceList != null) {
            sb.append("basicServiceList=[");
            boolean firstItem = true;
            for (ExtBasicServiceCode be : this.basicServiceList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be.toString());
            }
            sb.append("], ");
        }
        if (this.ssList != null) {
            sb.append("ssList=[");
            boolean firstItem = true;
            for (SSCode be : this.ssList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be.toString());
            }
            sb.append("], ");
        }
        if (this.roamingRestrictionDueToUnsupportedFeature) {
            sb.append("roamingRestrictionDueToUnsupportedFeature, ");
        }
        if (this.regionalSubscriptionIdentifier != null) {
            sb.append("regionalSubscriptionIdentifier=");
            sb.append(regionalSubscriptionIdentifier);
            sb.append(", ");
        }
        if (this.vbsGroupIndication) {
            sb.append("vbsGroupIndication, ");
        }
        if (this.vgcsGroupIndication) {
            sb.append("vgcsGroupIndication, ");
        }
        if (this.camelSubscriptionInfoWithdraw) {
            sb.append("camelSubscriptionInfoWithdraw, ");
        }
        if (this.extensionContainer != null) {
            sb.append("extensionContainer=");
            sb.append(extensionContainer);
            sb.append(", ");
        }
        if (this.gprsSubscriptionDataWithdraw != null) {
            sb.append("gprsSubscriptionDataWithdraw=");
            sb.append(gprsSubscriptionDataWithdraw);
            sb.append(", ");
        }
        if (this.roamingRestrictedInSgsnDueToUnsuppportedFeature) {
            sb.append("roamingRestrictedInSgsnDueToUnsuppportedFeature, ");
        }
        if (this.lsaInformationWithdraw != null) {
            sb.append("lsaInformationWithdraw=");
            sb.append(lsaInformationWithdraw);
            sb.append(", ");
        }
        if (this.gmlcListWithdraw) {
            sb.append("gmlcListWithdraw, ");
        }
        if (this.istInformationWithdraw) {
            sb.append("istInformationWithdraw, ");
        }
        if (this.specificCSIWithdraw != null) {
            sb.append("specificCSIWithdraw=");
            sb.append(specificCSIWithdraw);
            sb.append(", ");
        }
        if (this.chargingCharacteristicsWithdraw) {
            sb.append("chargingCharacteristicsWithdraw, ");
        }
        if (this.stnSrWithdraw) {
            sb.append("stnSrWithdraw, ");
        }
        if (this.epsSubscriptionDataWithdraw != null) {
            sb.append("epsSubscriptionDataWithdraw=");
            sb.append(epsSubscriptionDataWithdraw);
            sb.append(", ");
        }
        if (this.apnOiReplacementWithdraw) {
            sb.append("apnOiReplacementWithdraw, ");
        }
        if (this.csgSubscriptionDeleted) {
            sb.append("csgSubscriptionDeleted, ");
        }
        if (this.subscribedPeriodicTAURAUTimerWithdraw) {
            sb.append("subscribedPeriodicTAURAUTimerWithdraw, ");
        }
        if (this.subscribedPeriodicLAUTimerWithdraw) {
            sb.append("subscribedPeriodicLAUTimerWithdraw, ");
        }
        if (this.subscribedVsrvccWithdraw) {
            sb.append("subscribedVsrvccWithdraw, ");
        }
        if (this.vplmnCsgSubscriptionDeleted) {
            sb.append("vplmnCsgSubscriptionDeleted, ");
        }
        if (this.additionalMSISDNWithdraw) {
            sb.append("additionalMSISDNWithdraw, ");
        }
        if (this.csToPsSRVCCWithdraw) {
            sb.append("csToPsSRVCCWithdraw, ");
        }
        if (this.imsiGroupIdListWithdraw) {
            sb.append("imsiGroupIdListWithdraw, ");
        }
        if (this.userPlaneIntegrityProtectionWithdraw) {
            sb.append("userPlaneIntegrityProtectionWithdraw, ");
        }
        if (this.dlBufferingSuggestedPacketCountWithdraw) {
            sb.append("dlBufferingSuggestedPacketCountWithdraw, ");
        }
        if (this.ueUsageTypeWithdraw) {
            sb.append("ueUsageTypeWithdraw, ");
        }
        if (this.resetIdsWithdraw) {
            sb.append("resetIdsWithdraw, ");
        }
        if (this.iabOperationWithdraw) {
            sb.append("iabOperationWithdraw, ");
        }

        sb.append("]");

        return sb.toString();
    }
}
