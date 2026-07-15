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
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.ExtSupportedFeatures;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedFeatures;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBearerServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtTeleserviceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ODBGeneralData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OfferedCamel4CSIs;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.RegionalSubscriptionResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SupportedCamelPhases;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSCode;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.service.mobility.MobilityMessageImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.ExtSupportedFeaturesImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SupportedFeaturesImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.SSCodeImpl;

/**
 * @author daniel bichara
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class InsertSubscriberDataResponseImpl extends MobilityMessageImpl implements InsertSubscriberDataResponse {

    public static final String _PrimitiveName = "InsertSubscriberDataResponse";

    // MAP V1 & V2 & V3:
    protected static final int _TAG_teleserviceList = 1;
    protected static final int _TAG_bearerServiceList = 2;
    protected static final int _TAG_SS_List = 3;
    protected static final int _TAG_odb_GeneralData = 4;

    // MAP V2 & V3:
    protected static final int _TAG_regionalSubscriptionResponse = 5;

    // MAP V3:
    protected static final int _TAG_supportedCamelPhases = 6;
    protected static final int _TAG_extContainer = 7;
    protected static final int _TAG_offeredCamel4CSIs = 8;
    protected static final int _TAG_supportedFeatures = 9;
    protected static final int _TAG_extSupportedFeatures = 10;

    private ArrayList<ExtTeleserviceCode> teleserviceList = null;
    private ArrayList<ExtBearerServiceCode> bearerServiceList = null;
    private ArrayList<SSCode> ssList = null;
    private ODBGeneralData odbGeneralData = null;
    private RegionalSubscriptionResponse regionalSubscriptionResponse = null;
    private SupportedCamelPhases supportedCamelPhases = null;
    private MAPExtensionContainer extensionContainer = null;
    private OfferedCamel4CSIs offeredCamel4CSIs = null;
    private SupportedFeatures supportedFeatures = null;
    private ExtSupportedFeatures extSupportedFeatures = null;

    private long mapProtocolVersion;

    // For incoming messages
    public InsertSubscriberDataResponseImpl(long mapProtocolVersion) {
        this.mapProtocolVersion = mapProtocolVersion;
    }

    // For outgoing messages - MAP V2
    public InsertSubscriberDataResponseImpl(long mapProtocolVersion, ArrayList<ExtTeleserviceCode> teleserviceList,
                                            ArrayList<ExtBearerServiceCode> bearerServiceList, ArrayList<SSCode> ssList, ODBGeneralData odbGeneralData,
                                            RegionalSubscriptionResponse regionalSubscriptionResponse) {
        this.mapProtocolVersion = mapProtocolVersion;
        this.teleserviceList = teleserviceList;
        this.bearerServiceList = bearerServiceList;
        this.ssList = ssList;
        this.odbGeneralData = odbGeneralData;
        this.regionalSubscriptionResponse = regionalSubscriptionResponse;
    }

    // For outgoing messages - MAP V3
    public InsertSubscriberDataResponseImpl(long mapProtocolVersion, ArrayList<ExtTeleserviceCode> teleserviceList,
                                            ArrayList<ExtBearerServiceCode> bearerServiceList, ArrayList<SSCode> ssList, ODBGeneralData odbGeneralData,
                                            RegionalSubscriptionResponse regionalSubscriptionResponse, SupportedCamelPhases supportedCamelPhases,
                                            MAPExtensionContainer extensionContainer, OfferedCamel4CSIs offeredCamel4CSIs, SupportedFeatures supportedFeatures,
                                            ExtSupportedFeatures extSupportedFeatures) {

        this.mapProtocolVersion = mapProtocolVersion;
        this.teleserviceList = teleserviceList;
        this.bearerServiceList = bearerServiceList;
        this.ssList = ssList;
        this.odbGeneralData = odbGeneralData;
        this.regionalSubscriptionResponse = regionalSubscriptionResponse;

        if (mapProtocolVersion >= 3) {
            this.supportedCamelPhases = supportedCamelPhases;
            this.extensionContainer = extensionContainer;
            this.offeredCamel4CSIs = offeredCamel4CSIs;
            this.supportedFeatures = supportedFeatures;
            this.extSupportedFeatures = extSupportedFeatures;
        }
    }

    @Override
    public MAPMessageType getMessageType() {
        return MAPMessageType.insertSubscriberData_Response;
    }

    @Override
    public int getOperationCode() {
        return MAPOperationCode.insertSubscriberData;
    }

    @Override
    public ArrayList<ExtTeleserviceCode> getTeleserviceList() {
        return this.teleserviceList;
    }

    @Override
    public ArrayList<ExtBearerServiceCode> getBearerServiceList() {
        return this.bearerServiceList;
    }

    @Override
    public ArrayList<SSCode> getSSList() {
        return this.ssList;
    }

    @Override
    public ODBGeneralData getODBGeneralData() {
        return this.odbGeneralData;
    }

    @Override
    public RegionalSubscriptionResponse getRegionalSubscriptionResponse() {
        return this.regionalSubscriptionResponse;
    }

    @Override
    public SupportedCamelPhases getSupportedCamelPhases() {
        return this.supportedCamelPhases;
    }

    @Override
    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    @Override
    public OfferedCamel4CSIs getOfferedCamel4CSIs() {
        return this.offeredCamel4CSIs;
    }

    @Override
    public SupportedFeatures getSupportedFeatures() {
        return this.supportedFeatures;
    }

    @Override
    public ExtSupportedFeatures getExtSupportedFeatures() {
        return this.extSupportedFeatures;
    }

    public long getMapProtocolVersion() {
        return this.mapProtocolVersion;
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
        ExtTeleserviceCodeImpl teleserviceItem;
        this.teleserviceList = null;
        this.extensionContainer = null;
        this.bearerServiceList = null;
        this.ssList = null;
        this.odbGeneralData = null;
        this.regionalSubscriptionResponse = null;
        this.supportedCamelPhases = null;
        this.offeredCamel4CSIs = null;
        this.supportedFeatures = null;
        this.extSupportedFeatures = null;

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
                        case _TAG_teleserviceList:
                            // teleserviceList [1] TeleserviceList OPTIONAL
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".teleserviceList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            AsnInputStream ais2 = ais.readSequenceStream();
                            this.teleserviceList = new ArrayList<>();
                            while (true) {
                                if (ais2.available() == 0)
                                    break;

                                int tag2 = ais2.readTag();
                                if (tag2 != Tag.STRING_OCTET || ais2.getTagClass() != Tag.CLASS_UNIVERSAL
                                        || !ais2.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad teleserviceCode element tag or tagClass or is not primitive ",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                teleserviceItem = new ExtTeleserviceCodeImpl();
                                teleserviceItem.decodeAll(ais2);
                                this.teleserviceList.add(teleserviceItem);
                            }
                            if (this.teleserviceList.size() < 1 || this.teleserviceList.size() > 20) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter teleserviceList size must be from 1 to 20, found: "
                                        + this.teleserviceList.size(), MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_bearerServiceList:
                            // bearerServiceList [2] BearerServiceList OPTIONAL
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".bearerServiceList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            AsnInputStream ais3 = ais.readSequenceStream();
                            this.bearerServiceList = new ArrayList<>();
                            while (true) {
                                if (ais3.available() == 0)
                                    break;

                                int tag2 = ais3.readTag();
                                if (tag2 != Tag.STRING_OCTET || ais3.getTagClass() != Tag.CLASS_UNIVERSAL
                                        || !ais3.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad bearerServiceList element tag or tagClass or is not primitive ",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                ExtBearerServiceCodeImpl extBearerServiceCode = new ExtBearerServiceCodeImpl();
                                extBearerServiceCode.decodeAll(ais3);
                                this.bearerServiceList.add(extBearerServiceCode);
                            }
                            if (this.bearerServiceList.size() < 1 || this.bearerServiceList.size() > 50) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter bearerServiceList size must be from 1 to 50, found: "
                                        + this.bearerServiceList.size(), MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;
                        case _TAG_SS_List:
                            // ss-List [3] SS-List OPTIONAL
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".ssList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                            AsnInputStream ais4 = ais.readSequenceStream();
                            this.ssList = new ArrayList<>();
                            while (true) {
                                if (ais4.available() == 0)
                                    break;

                                int tag2 = ais4.readTag();
                                if (tag2 != Tag.STRING_OCTET || ais4.getTagClass() != Tag.CLASS_UNIVERSAL
                                        || !ais4.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad ssListList element tag or tagClass or is not primitive ",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                SSCodeImpl ssCode = new SSCodeImpl();
                                ssCode.decodeAll(ais4);
                                this.ssList.add(ssCode);
                            }
                            if (this.ssList.size() < 1 || this.ssList.size() > 30) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ": Parameter ssList size must be from 1 to 30, found: " + this.ssList.size(),
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            break;

                        case _TAG_odb_GeneralData:
                            // odb-GeneralData [4] ODB-GeneralData OPTIONAL
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".odbGeneralData: Parameter odbGeneralData is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.odbGeneralData = new ODBGeneralDataImpl();
                            ((ODBGeneralDataImpl) this.odbGeneralData).decodeAll(ais);
                            break;

                        case _TAG_regionalSubscriptionResponse:
                            // regionalSubscriptionResponse [5] RegionalSubscriptionResponse OPTIONAL
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".regionalSubscriptionResponse: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            this.regionalSubscriptionResponse = RegionalSubscriptionResponse.getInstance((int) ais
                                    .readInteger());
                            break;

                        case _TAG_supportedCamelPhases:
                            // supportedCamelPhases [6] SupportedCamelPhases OPTIONAL
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".supportedCamelPhases: Parameter supportedCamelPhases is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.supportedCamelPhases = new SupportedCamelPhasesImpl();
                            ((SupportedCamelPhasesImpl) this.supportedCamelPhases).decodeAll(ais);
                            break;

                        case _TAG_extContainer:
                            // extensionContainer [7] ExtensionContainer OPTIONAL
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".extensionContainer: Parameter extensionContainer is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.extensionContainer = new MAPExtensionContainerImpl();
                            ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                            break;

                        case _TAG_offeredCamel4CSIs:
                            // offeredCamel4CSIs [8] OfferedCamel4CSIs OPTIONAL
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".offeredCamel4CSIs: Parameter offeredCamel4CSIs is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.offeredCamel4CSIs = new OfferedCamel4CSIsImpl();
                            ((OfferedCamel4CSIsImpl) this.offeredCamel4CSIs).decodeAll(ais);
                            break;

                        case _TAG_supportedFeatures:
                            // supportedFeatures [9] SupportedFeatures OPTIONAL
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".supportedFeatures: Parameter supportedFeatures is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.supportedFeatures = new SupportedFeaturesImpl();
                            ((SupportedFeaturesImpl) this.supportedFeatures).decodeAll(ais);
                            break;

                        case _TAG_extSupportedFeatures:
                            // ext-SupportedFeatures [10] Ext-SupportedFeatures OPTIONAL
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".extSupportedFeatures: Parameter extSupportedFeatures is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.extSupportedFeatures = new ExtSupportedFeaturesImpl();
                            ((ExtSupportedFeaturesImpl) this.extSupportedFeatures).decodeAll(ais);
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
        if (this.teleserviceList != null && (this.teleserviceList.size() < 1 || this.teleserviceList.size() > 20)) {
            throw new MAPException("teleserviceList size must be from 1 to 20, found: " + this.teleserviceList.size());
        }
        if (this.bearerServiceList != null && (this.bearerServiceList.size() < 1 || this.bearerServiceList.size() > 50)) {
            throw new MAPException("bearerServiceList size must be from 1 to 50, found: " + this.bearerServiceList.size());
        }
        if (this.ssList != null && (this.ssList.size() < 1 || this.ssList.size() > 30)) {
            throw new MAPException("ssList size must be from 1 to 30, found: " + this.ssList.size());
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
                throw new MAPException("AsnException when encoding " + _PrimitiveName + ".teleserviceList: " + e.getMessage(),
                        e);
            }
        }
        if (this.bearerServiceList != null) {
            try {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_bearerServiceList);
                int pos = asnOutputStream.StartContentDefiniteLength();
                for (ExtBearerServiceCode item : this.bearerServiceList) {
                    ((ExtBearerServiceCodeImpl) item).encodeAll(asnOutputStream);
                }
                asnOutputStream.FinalizeContent(pos);
            } catch (AsnException e) {
                throw new MAPException(
                        "AsnException when encoding " + _PrimitiveName + ".bearerServiceList: " + e.getMessage(), e);
            }
        }
        if (this.ssList != null) {
            try {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_SS_List);
                int pos = asnOutputStream.StartContentDefiniteLength();
                for (SSCode item : this.ssList) {
                    ((SSCodeImpl) item).encodeAll(asnOutputStream);
                }
                asnOutputStream.FinalizeContent(pos);
            } catch (AsnException e) {
                throw new MAPException("AsnException when encoding " + _PrimitiveName + ".ssList: " + e.getMessage(), e);
            }
        }
        if (this.odbGeneralData != null) {
            ((ODBGeneralDataImpl) this.odbGeneralData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_odb_GeneralData);
        }

        if (mapProtocolVersion >= 2) {
            if (this.regionalSubscriptionResponse != null) {
                try {
                    asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_regionalSubscriptionResponse,
                            this.regionalSubscriptionResponse.getCode());
                } catch (IOException e) {
                    throw new MAPException("IOException while encoding " + _PrimitiveName
                            + " parameter regionalSubscriptionResponse", e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException while encoding " + _PrimitiveName
                            + " parameter regionalSubscriptionResponse", e);
                }
            }
        }

        if (mapProtocolVersion >= 3) {
            if (this.supportedCamelPhases != null) {
                ((SupportedCamelPhasesImpl) this.supportedCamelPhases).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_supportedCamelPhases);
            }
            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_extContainer);
            if (this.offeredCamel4CSIs != null) {
                ((OfferedCamel4CSIsImpl) this.offeredCamel4CSIs).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_offeredCamel4CSIs);
            }
            if (this.supportedFeatures != null) {
                ((SupportedFeaturesImpl) this.supportedFeatures).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_supportedFeatures);
            }
            if (this.extSupportedFeatures != null) {
                ((ExtSupportedFeaturesImpl) this.extSupportedFeatures).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_extSupportedFeatures);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.teleserviceList != null) {
            sb.append("teleserviceList=[");
            boolean firstItem = true;
            for (ExtTeleserviceCode be : this.teleserviceList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be.toString());
            }
            sb.append("], ");
        }

        if (this.bearerServiceList != null) {
            sb.append("bearerServiceList=[");
            boolean firstItem = true;
            for (ExtBearerServiceCode be : this.bearerServiceList) {
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

        if (this.odbGeneralData != null) {
            sb.append("odbGeneralData=");
            sb.append(odbGeneralData);
            sb.append(", ");
        }

        if (this.regionalSubscriptionResponse != null) {
            sb.append("regionalSubscriptionResponse=");
            sb.append(regionalSubscriptionResponse);
            sb.append(", ");
        }

        if (this.supportedCamelPhases != null) {
            sb.append("supportedCamelPhases=");
            sb.append(supportedCamelPhases);
            sb.append(", ");
        }

        if (this.extensionContainer != null) {
            sb.append("extensionContainer=");
            sb.append(extensionContainer);
            sb.append(", ");
        }

        if (this.offeredCamel4CSIs != null) {
            sb.append("offeredCamel4CSIs=");
            sb.append(offeredCamel4CSIs);
            sb.append(", ");
        }

        if (this.supportedFeatures != null) {
            sb.append("supportedFeatures=");
            sb.append(supportedFeatures);
            sb.append(", ");
        }

        if (this.extSupportedFeatures != null) {
            sb.append("extSupportedFeatures=");
            sb.append(extSupportedFeatures);
            sb.append(", ");
        }

        sb.append("mapProtocolVersion=");
        sb.append(mapProtocolVersion);

        sb.append("]");

        return sb.toString();
    }

}
