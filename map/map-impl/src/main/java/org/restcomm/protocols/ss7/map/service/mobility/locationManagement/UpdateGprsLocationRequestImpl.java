package org.restcomm.protocols.ss7.map.service.mobility.locationManagement;

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
import org.restcomm.protocols.ss7.map.api.primitives.GSNAddress;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.ADDInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.EPSInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SGSNCapability;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SMSRegisterRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UESRVCCCapability;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;
import org.restcomm.protocols.ss7.map.primitives.DiameterIdentityImpl;
import org.restcomm.protocols.ss7.map.primitives.GSNAddressImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.PlmnIdImpl;
import org.restcomm.protocols.ss7.map.service.mobility.MobilityMessageImpl;

/**
 *
 * @author Lasith Waruna Perera
 *
 */
public class UpdateGprsLocationRequestImpl extends MobilityMessageImpl implements UpdateGprsLocationRequest {

    private static final int TAG_sgsnCapability = 0;
    private static final int TAG_informPreviousNetworkEntity = 1;
    private static final int TAG_psLCSNotSupportedByUE = 2;
    private static final int TAG_vGmlcAddress = 3;
    private static final int TAG_addInfo = 4;
    private static final int TAG_epsInfo = 5;
    private static final int TAG_servingNodeTypeIndicator = 6;
    private static final int TAG_skipSubscriberDataUpdate = 7;
    private static final int TAG_usedRATType = 8;
    private static final int TAG_gprsSubscriptionDataNotNeeded = 9;
    private static final int TAG_nodeTypeIndicator = 10;
    private static final int TAG_areaRestricted = 11;
    private static final int TAG_ueReachableIndicator = 12;
    private static final int TAG_epsSubscriptionDataNotNeeded = 13;
    private static final int TAG_uesrvccCapability = 14;
    private static final int TAG_EPLMN_List = 15;
    private static final int TAG_mmeNumberForMTSMS = 16;
    private static final int TAG_smsRegisterRequest = 17;
    private static final int TAG_sms_Only = 18;
    private static final int TAG_sgsn_Name = 19;
    private static final int TAG_sgsn_Realm = 20;
    private static final int TAF_lgd_supportIndicator = 21;
    private static final int TAG_removalOfMMERegistrationForSMS = 22;
    private static final int TAG_adjacentPLMN_List = 23;

    public static final String _PrimitiveName = "UpdateGprsLocationRequest";

    private IMSI imsi;
    private ISDNAddressString sgsnNumber;
    private GSNAddress sgsnAddress;
    private MAPExtensionContainer extensionContainer;
    private SGSNCapability sgsnCapability;
    private boolean informPreviousNetworkEntity;
    private boolean psLCSNotSupportedByUE;
    private GSNAddress vGmlcAddress;
    private ADDInfo addInfo;
    private EPSInfo epsInfo;
    private boolean servingNodeTypeIndicator;
    private boolean skipSubscriberDataUpdate;
    private UsedRATType usedRATType;
    private boolean gprsSubscriptionDataNotNeeded;
    private boolean nodeTypeIndicator;
    private boolean areaRestricted;
    private boolean ueReachableIndicator;
    private boolean epsSubscriptionDataNotNeeded;
    private UESRVCCCapability uesrvccCapability;
    private ArrayList<PlmnId> ePLMNList;
    private ISDNAddressString mmeNumberForMTSMS;
    private SMSRegisterRequest smsRegisterRequest;
    private boolean smsOnly;
    private DiameterIdentity sgsnName;
    private DiameterIdentity sgsnRealm;
    private boolean lgdSupportIndicator;
    private boolean removalOfMMERegistrationForSMS;
    private ArrayList<PlmnId> adjacentPLMNList;

    public UpdateGprsLocationRequestImpl() {
        super();
    }

    public UpdateGprsLocationRequestImpl(IMSI imsi, ISDNAddressString sgsnNumber, GSNAddress sgsnAddress,
            MAPExtensionContainer extensionContainer, SGSNCapability sgsnCapability, boolean informPreviousNetworkEntity,
            boolean psLCSNotSupportedByUE, GSNAddress vGmlcAddress, ADDInfo addInfo, EPSInfo epsInfo,
            boolean servingNodeTypeIndicator, boolean skipSubscriberDataUpdate, UsedRATType usedRATType,
            boolean gprsSubscriptionDataNotNeeded, boolean nodeTypeIndicator, boolean areaRestricted,
            boolean ueReachableIndicator, boolean epsSubscriptionDataNotNeeded, UESRVCCCapability uesrvccCapability,
            ArrayList<PlmnId> ePLMNList, ISDNAddressString mmeNumberForMTSMS, SMSRegisterRequest smsRegisterRequest,
            boolean smsOnly, DiameterIdentity sgsnName, DiameterIdentity sgsnRealm, boolean lgdSupportIndicator,
            boolean removalOfMMERegistrationForSMS, ArrayList<PlmnId> adjacentPLMNList, long mapProtocolVersion) {
        super();
        this.imsi = imsi;
        this.sgsnNumber = sgsnNumber;
        this.sgsnAddress = sgsnAddress;
        this.extensionContainer = extensionContainer;
        this.sgsnCapability = sgsnCapability;
        this.informPreviousNetworkEntity = informPreviousNetworkEntity;
        this.psLCSNotSupportedByUE = psLCSNotSupportedByUE;
        this.vGmlcAddress = vGmlcAddress;
        this.addInfo = addInfo;
        this.epsInfo = epsInfo;
        this.servingNodeTypeIndicator = servingNodeTypeIndicator;
        this.skipSubscriberDataUpdate = skipSubscriberDataUpdate;
        this.usedRATType = usedRATType;
        this.gprsSubscriptionDataNotNeeded = gprsSubscriptionDataNotNeeded;
        this.nodeTypeIndicator = nodeTypeIndicator;
        this.areaRestricted = areaRestricted;
        this.ueReachableIndicator = ueReachableIndicator;
        this.epsSubscriptionDataNotNeeded = epsSubscriptionDataNotNeeded;
        this.uesrvccCapability = uesrvccCapability;
        this.ePLMNList = ePLMNList;
        this.mmeNumberForMTSMS = mmeNumberForMTSMS;
        this.smsRegisterRequest = smsRegisterRequest;
        this.smsOnly = smsOnly;
        this.sgsnName = sgsnName;
        this.sgsnRealm = sgsnRealm;
        this.lgdSupportIndicator = lgdSupportIndicator;
        this.removalOfMMERegistrationForSMS = removalOfMMERegistrationForSMS;
        this.adjacentPLMNList = adjacentPLMNList;
    }

    @Override
    public MAPMessageType getMessageType() {
        return MAPMessageType.updateGprsLocation_Request;
    }

    @Override
    public int getOperationCode() {
        return MAPOperationCode.updateGprsLocation;
    }

    @Override
    public IMSI getImsi() {
        return this.imsi;
    }

    @Override
    public ISDNAddressString getSgsnNumber() {
        return this.sgsnNumber;
    }

    @Override
    public GSNAddress getSgsnAddress() {
        return this.sgsnAddress;
    }

    @Override
    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    @Override
    public SGSNCapability getSGSNCapability() {
        return this.sgsnCapability;
    }

    @Override
    public boolean getInformPreviousNetworkEntity() {
        return this.informPreviousNetworkEntity;
    }

    @Override
    public boolean getPsLCSNotSupportedByUE() {
        return this.psLCSNotSupportedByUE;
    }

    @Override
    public GSNAddress getVGmlcAddress() {
        return this.vGmlcAddress;
    }

    @Override
    public ADDInfo getADDInfo() {
        return this.addInfo;
    }

    @Override
    public EPSInfo getEPSInfo() {
        return this.epsInfo;
    }

    @Override
    public boolean getServingNodeTypeIndicator() {
        return this.servingNodeTypeIndicator;
    }

    @Override
    public boolean getSkipSubscriberDataUpdate() {
        return this.skipSubscriberDataUpdate;
    }

    @Override
    public UsedRATType getUsedRATType() {
        return this.usedRATType;
    }

    @Override
    public boolean getGprsSubscriptionDataNotNeeded() {
        return this.gprsSubscriptionDataNotNeeded;
    }

    @Override
    public boolean getNodeTypeIndicator() {
        return this.nodeTypeIndicator;
    }

    @Override
    public boolean getAreaRestricted() {
        return this.areaRestricted;
    }

    @Override
    public boolean getUeReachableIndicator() {
        return this.ueReachableIndicator;
    }

    @Override
    public boolean getEpsSubscriptionDataNotNeeded() {
        return this.epsSubscriptionDataNotNeeded;
    }

    @Override
    public UESRVCCCapability getUESRVCCCapability() {
        return this.uesrvccCapability;
    }

    @Override
    public ArrayList<PlmnId> getEPLMNList() {
        return this.ePLMNList;
    }

    @Override
    public ISDNAddressString getMmeNumberForMTSMS() {
        return this.mmeNumberForMTSMS;
    }

    @Override
    public SMSRegisterRequest getSMSRegisterRequest() {
        return this.smsRegisterRequest;
    }

    @Override
    public boolean getSmsOnly() {
        return this.smsOnly;
    }

    @Override
    public DiameterIdentity getSgsnName() {
        return this.sgsnName;
    }

    @Override
    public DiameterIdentity getSgsnRealm() {
        return this.sgsnRealm;
    }

    @Override
    public boolean getLgdSupportIndicator() {
        return this.lgdSupportIndicator;
    }

    @Override
    public boolean getRemovalOfMMERegistrationForSMS() {
        return this.removalOfMMERegistrationForSMS;
    }

    @Override
    public ArrayList<PlmnId> getAdjacentPLMNList() {
        return this.adjacentPLMNList;
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
            throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": ", e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": ", e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    @Override
    public void decodeData(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException {
        try {
            this._decode(asnInputStream, length);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": ", e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": ", e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.imsi = null;
        this.sgsnNumber = null;
        this.sgsnAddress = null;
        this.extensionContainer = null;
        this.sgsnCapability = null;
        this.informPreviousNetworkEntity = false;
        this.psLCSNotSupportedByUE = false;
        this.vGmlcAddress = null;
        this.addInfo = null;
        this.epsInfo = null;
        this.servingNodeTypeIndicator = false;
        this.skipSubscriberDataUpdate = false;
        this.usedRATType = null;
        this.gprsSubscriptionDataNotNeeded = false;
        this.nodeTypeIndicator = false;
        this.areaRestricted = false;
        this.ueReachableIndicator = false;
        this.epsSubscriptionDataNotNeeded = false;
        this.uesrvccCapability = null;
        this.ePLMNList = null;
        this.mmeNumberForMTSMS = null;
        this.smsRegisterRequest = null;
        this.smsOnly = false;
        this.sgsnName = null;
        this.sgsnRealm = null;
        this.lgdSupportIndicator = false;
        this.removalOfMMERegistrationForSMS = false;
        this.adjacentPLMNList = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            switch (num) {
                case 0: // imsi
                    if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive() || tag != Tag.STRING_OCTET)
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".imsi: Parameter 0 bad tag or tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.imsi = new IMSIImpl();
                    ((IMSIImpl) this.imsi).decodeAll(ais);
                    break;
                case 1: // sgsnNumber
                    if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive() || tag != Tag.STRING_OCTET)
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".sgsnNumber: Parameter 0 bad tag or tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.sgsnNumber = new ISDNAddressStringImpl();
                    ((ISDNAddressStringImpl) this.sgsnNumber).decodeAll(ais);
                    break;
                case 2: // sgsnAddress
                    if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive() || tag != Tag.STRING_OCTET)
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".sgsnAddress: Parameter 2 bad tag or tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.sgsnAddress = new GSNAddressImpl();
                    ((GSNAddressImpl) this.sgsnAddress).decodeAll(ais);
                    break;

                default:
                    if (ais.getTagClass() == Tag.CLASS_UNIVERSAL) {
                        switch (tag) {
                            case Tag.SEQUENCE:
                                // extensionContainer
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".extensionContainer: Parameter extensionContainer is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.extensionContainer = new MAPExtensionContainerImpl();
                                ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                                break;
                            default:
                                ais.advanceElement();
                                break;
                        }
                    } else if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {

                        switch (tag) {
                            case TAG_sgsnCapability:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".sgsnCapability: Parameter is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.sgsnCapability = new SGSNCapabilityImpl();
                                ((SGSNCapabilityImpl) this.sgsnCapability).decodeAll(ais);
                                break;
                            case TAG_informPreviousNetworkEntity: // informPreviousNetworkEntity
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".informPreviousNetworkEntity: Parameter is  not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.informPreviousNetworkEntity = true;
                                break;
                            case TAG_psLCSNotSupportedByUE:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".tpsLCSNotSupportedByUE: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.psLCSNotSupportedByUE = true;
                                break;
                            case TAG_vGmlcAddress:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".vGmlcAddress: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.vGmlcAddress = new GSNAddressImpl();
                                ((GSNAddressImpl) this.vGmlcAddress).decodeAll(ais);
                                break;
                            case TAG_addInfo:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".addInfo: Parameter is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.addInfo = new ADDInfoImpl();
                                ((ADDInfoImpl) this.addInfo).decodeAll(ais);
                                break;
                            case TAG_epsInfo:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".epsInfo: Parameter is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                AsnInputStream ais2 = ais.readSequenceStream();
                                ais2.readTag();
                                this.epsInfo = new EPSInfoImpl();
                                ((EPSInfoImpl) this.epsInfo).decodeAll(ais2);
                                break;
                            case TAG_servingNodeTypeIndicator:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".servingNodeTypeIndicator: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.servingNodeTypeIndicator = true;
                                break;
                            case TAG_skipSubscriberDataUpdate:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".skipSubscriberDataUpdate: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.skipSubscriberDataUpdate = true;
                                break;
                            case TAG_usedRATType:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".usedRATType: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                int raType = (int) ais.readInteger();
                                this.usedRATType = UsedRATType.getInstance(raType);
                                break;
                            case TAG_gprsSubscriptionDataNotNeeded:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".gprsSubscriptionDataNotNeeded: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.gprsSubscriptionDataNotNeeded = true;
                                break;
                            case TAG_nodeTypeIndicator:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".nodeTypeIndicator: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.nodeTypeIndicator = true;
                                break;
                            case TAG_areaRestricted:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".areaRestricted: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.areaRestricted = true;
                                break;
                            case TAG_ueReachableIndicator:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".ueReachableIndicator: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.ueReachableIndicator = true;
                                break;
                            case TAG_epsSubscriptionDataNotNeeded:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".epsSubscriptionDataNotNeeded: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.epsSubscriptionDataNotNeeded = true;
                                break;
                            case TAG_uesrvccCapability:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".uesrvccCapability: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                int srvccCapability = (int) ais.readInteger();
                                this.uesrvccCapability = UESRVCCCapability.getInstance(srvccCapability);
                                break;
                            case TAG_EPLMN_List:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".ePLMNList: Parameter is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                AsnInputStream ais3 = ais.readSequenceStream();
                                PlmnId plmnId;
                                this.ePLMNList = new ArrayList<>();
                                while (true) {
                                    if (ais3.available() == 0)
                                        break;

                                    int tag2 = ais3.readTag();
                                    if (tag2 != Tag.STRING_OCTET || ais3.getTagClass() != Tag.CLASS_UNIVERSAL || !ais3.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + ": bad tag or tagClass or is not primitive when decoding trackingAreaIdList",
                                                MAPParsingComponentExceptionReason.MistypedParameter);
                                    plmnId = new PlmnIdImpl();
                                    ((PlmnIdImpl) plmnId).decodeAll(ais3);
                                    this.ePLMNList.add(plmnId);
                                }
                                break;
                            case TAG_mmeNumberForMTSMS:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".mmeNumberForMTSMS: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.mmeNumberForMTSMS = new ISDNAddressStringImpl();
                                ((ISDNAddressStringImpl) this.mmeNumberForMTSMS).decodeAll(ais);
                                break;
                            case TAG_smsRegisterRequest:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".smsRegisterRequest: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                int smsRegisterReq = (int) ais.readInteger();
                                this.smsRegisterRequest = SMSRegisterRequest.getInstance(smsRegisterReq);
                                break;
                            case TAG_sms_Only:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".smsOnly: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.smsOnly = true;
                                break;
                            case TAG_sgsn_Name:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".sgsnName: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.sgsnName = new DiameterIdentityImpl();
                                ((DiameterIdentityImpl) this.sgsnName).decodeAll(ais);
                                break;
                            case TAG_sgsn_Realm:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".sgsnRealm: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.sgsnRealm = new DiameterIdentityImpl();
                                ((DiameterIdentityImpl) this.sgsnRealm).decodeAll(ais);
                                break;
                            case TAF_lgd_supportIndicator:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".lgdSupportIndicator: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.lgdSupportIndicator = true;
                                break;
                            case TAG_removalOfMMERegistrationForSMS:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".removalOfMMERegistrationForSMS: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.removalOfMMERegistrationForSMS = true;
                                break;
                            case TAG_adjacentPLMN_List:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".adjacentPLMNList: Parameter is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                AsnInputStream ais4= ais.readSequenceStream();
                                PlmnId adjPlmnId;
                                this.adjacentPLMNList = new ArrayList<>();
                                while (true) {
                                    if (ais4.available() == 0)
                                        break;

                                    int tag2 = ais4.readTag();
                                    if (tag2 != Tag.STRING_OCTET || ais4.getTagClass() != Tag.CLASS_UNIVERSAL || !ais4.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + ": bad tag or tagClass or is not primitive when decoding trackingAreaIdList",
                                                MAPParsingComponentExceptionReason.MistypedParameter);
                                    adjPlmnId = new PlmnIdImpl();
                                    ((PlmnIdImpl) adjPlmnId).decodeAll(ais4);
                                    this.adjacentPLMNList.add(adjPlmnId);
                                }
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

        if (this.imsi == null || this.sgsnNumber == null || this.sgsnAddress == null)
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": imsi or sgsnNumber or sgsnAddress is null ", MAPParsingComponentExceptionReason.MistypedParameter);

    }

    @Override
    public void encodeAll(AsnOutputStream asnOutputStream) throws MAPException {
        try {
            this.encodeAll(asnOutputStream, this.getTagClass(), this.getTag());
        } catch (Exception e) {
            e.printStackTrace();
            throw new MAPException(e);
        }
    }

    @Override
    public void encodeAll(AsnOutputStream asnOutputStream, int tagClass, int tag) throws MAPException {
        try {
            asnOutputStream.writeTag(tagClass, this.getIsPrimitive(), tag);
            int pos = asnOutputStream.StartContentDefiniteLength();
            this.encodeData(asnOutputStream);
            asnOutputStream.FinalizeContent(pos);

        } catch (AsnException e) {
            e.printStackTrace();
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MAPException("Exception when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        try {
            if (this.imsi == null || this.sgsnNumber == null || this.sgsnAddress == null)
                throw new MAPException("imsi, sgsnNumber and sgsnAddress parameter must not be null");

            ((IMSIImpl) this.imsi).encodeAll(asnOutputStream);

            ((ISDNAddressStringImpl) this.sgsnNumber).encodeAll(asnOutputStream);

            ((GSNAddressImpl) this.sgsnAddress).encodeAll(asnOutputStream);

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream);

            if (this.sgsnCapability != null)
                ((SGSNCapabilityImpl) this.sgsnCapability).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, TAG_sgsnCapability);

            if (informPreviousNetworkEntity)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, TAG_informPreviousNetworkEntity);

            if (psLCSNotSupportedByUE)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, TAG_psLCSNotSupportedByUE);

            if (this.vGmlcAddress != null)
                ((GSNAddressImpl) this.vGmlcAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, TAG_vGmlcAddress);

            if (this.addInfo != null)
                ((ADDInfoImpl) this.addInfo).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, TAG_addInfo);

            if (this.epsInfo != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, TAG_epsInfo);
                int pos = asnOutputStream.StartContentDefiniteLength();
                ((EPSInfoImpl) this.epsInfo).encodeAll(asnOutputStream);
                asnOutputStream.FinalizeContent(pos);
            }

            if (servingNodeTypeIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, TAG_servingNodeTypeIndicator);

            if (skipSubscriberDataUpdate)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, TAG_skipSubscriberDataUpdate);

            if (this.usedRATType != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, TAG_usedRATType, this.usedRATType.getCode());

            if (gprsSubscriptionDataNotNeeded)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, TAG_gprsSubscriptionDataNotNeeded);

            if (nodeTypeIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, TAG_nodeTypeIndicator);

            if (areaRestricted)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, TAG_areaRestricted);

            if (ueReachableIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, TAG_ueReachableIndicator);

            if (epsSubscriptionDataNotNeeded)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, TAG_epsSubscriptionDataNotNeeded);

            if (this.uesrvccCapability != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, TAG_uesrvccCapability, this.uesrvccCapability.getCode());

            if (this.ePLMNList != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, TAG_EPLMN_List);
                int pos = asnOutputStream.StartContentDefiniteLength();
                for (PlmnId plmnId : this.ePLMNList) {
                    ((PlmnIdImpl) plmnId).encodeAll(asnOutputStream);
                }
                asnOutputStream.FinalizeContent(pos);
            }

            if (this.mmeNumberForMTSMS != null)
                ((ISDNAddressStringImpl) this.mmeNumberForMTSMS).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, TAG_mmeNumberForMTSMS);

            if (this.smsRegisterRequest != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, TAG_smsRegisterRequest, this.smsRegisterRequest.getCode());

            if (smsOnly)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, TAG_sms_Only);

            if (this.sgsnName != null)
                ((DiameterIdentityImpl) this.sgsnName).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, TAG_sgsn_Name);

            if (this.sgsnRealm != null)
                ((DiameterIdentityImpl) this.sgsnRealm).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, TAG_sgsn_Realm);

            if (lgdSupportIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, TAF_lgd_supportIndicator);

            if (removalOfMMERegistrationForSMS)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, TAG_removalOfMMERegistrationForSMS);

            if (adjacentPLMNList != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, TAG_adjacentPLMN_List);
                int pos = asnOutputStream.StartContentDefiniteLength();
                for (PlmnId plmnId : this.adjacentPLMNList) {
                    ((PlmnIdImpl) plmnId).encodeAll(asnOutputStream);
                }
                asnOutputStream.FinalizeContent(pos);
            }

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
            sb.append(this.imsi);
            sb.append(", ");
        }

        if (this.sgsnNumber != null) {
            sb.append("sgsnNumber=");
            sb.append(this.sgsnNumber);
            sb.append(", ");
        }

        if (this.sgsnAddress != null) {
            sb.append("sgsnAddress=");
            sb.append(this.sgsnAddress);
            sb.append(", ");
        }

        if (this.extensionContainer != null) {
            sb.append("extensionContainer=");
            sb.append(this.extensionContainer);
            sb.append(", ");
        }

        if (this.sgsnCapability != null) {
            sb.append("sgsnCapability=");
            sb.append(this.sgsnCapability);
            sb.append(", ");
        }

        if (this.informPreviousNetworkEntity) {
            sb.append("informPreviousNetworkEntity, ");
        }

        if (this.psLCSNotSupportedByUE) {
            sb.append("psLCSNotSupportedByUE, ");
        }

        if (this.vGmlcAddress != null) {
            sb.append("vGmlcAddress=");
            sb.append(this.vGmlcAddress);
            sb.append(", ");
        }

        if (this.addInfo != null) {
            sb.append("addInfo=");
            sb.append(this.addInfo);
            sb.append(", ");
        }

        if (this.epsInfo != null) {
            sb.append("epsInfo=");
            sb.append(this.epsInfo);
            sb.append(", ");
        }

        if (this.servingNodeTypeIndicator) {
            sb.append("servingNodeTypeIndicator, ");
        }

        if (this.skipSubscriberDataUpdate) {
            sb.append("skipSubscriberDataUpdate, ");
        }

        if (this.usedRATType != null) {
            sb.append("usedRATType=");
            sb.append(this.usedRATType);
            sb.append(", ");
        }

        if (this.gprsSubscriptionDataNotNeeded) {
            sb.append("gprsSubscriptionDataNotNeeded, ");
        }

        if (this.nodeTypeIndicator) {
            sb.append("nodeTypeIndicator, ");
        }

        if (this.areaRestricted) {
            sb.append("areaRestricted, ");
        }

        if (this.ueReachableIndicator) {
            sb.append("ueReachableIndicator, ");
        }

        if (this.epsSubscriptionDataNotNeeded) {
            sb.append("epsSubscriptionDataNotNeeded, ");
        }

        if (this.uesrvccCapability != null) {
            sb.append("uesrvccCapability=");
            sb.append(this.uesrvccCapability);
            sb.append(", ");
        }

        if (this.ePLMNList != null) {
            sb.append("EPLMN-List=");
            sb.append(this.ePLMNList);
            sb.append(", ");
        }

        if (this.mmeNumberForMTSMS != null) {
            sb.append("mmeNumberForMTSMS=");
            sb.append(this.mmeNumberForMTSMS);
            sb.append(", ");
        }

        if (this.smsRegisterRequest != null) {
            sb.append("smsRegisterRequest=");
            sb.append(this.smsRegisterRequest);
            sb.append(", ");
        }

        if (this.smsOnly) {
            sb.append("sms-Only, ");
        }

        if (this.sgsnName != null) {
            sb.append("sgsnName=");
            sb.append(this.sgsnName);
            sb.append(", ");
        }

        if (this.sgsnRealm != null) {
            sb.append("sgsnRealm=");
            sb.append(this.sgsnRealm);
            sb.append(", ");
        }

        if (this.lgdSupportIndicator) {
            sb.append("lgdSupportIndicator, ");
        }

        if (this.removalOfMMERegistrationForSMS) {
            sb.append("removalOfMMERegistrationForSMS, ");
        }

        if (this.adjacentPLMNList != null) {
            sb.append("adjacentPLMNList=");
            sb.append(this.adjacentPLMNList);
            sb.append(", ");
        }

        sb.append("]");

        return sb.toString();
    }

}
