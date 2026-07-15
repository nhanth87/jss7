package org.restcomm.protocols.ss7.map.service.mobility.faultRecovery;

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
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.NetworkResource;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.DeleteSubscriberDataArgs;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.InsertSubscriberDataArgs;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.ResetId;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.ResetRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.SendingNodeNumber;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.service.mobility.MobilityMessageImpl;

/**
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com">Fernando Mendioroz</a>
 */
public class ResetRequestImpl extends MobilityMessageImpl implements ResetRequest {

    public static final String _PrimitiveName = "ResetRequest";
    public static final int _TAG_extensionContainer = 0;
    private static final int _TAG_reset_Id_List = 1;
    private static final int _TAG_subscriptionData = 2;
    private static final int _TAG_subscriptionDataDeletion = 3;

    private NetworkResource networkResource;
    private ISDNAddressString hlrNumber;
    private ArrayList<IMSI> hlrList;
    private SendingNodeNumber sendingNodenumber;

    private MAPExtensionContainer extensionContainer;

    private ArrayList<ResetId> resetIdList;

    private InsertSubscriberDataArgs subscriptionData;

    private DeleteSubscriberDataArgs subscriptionDataDeletion;
    private long mapProtocolVersion;

    private boolean rel18Update;

    public ResetRequestImpl(long mapProtocolVersion) {
        this.mapProtocolVersion = mapProtocolVersion;
    }

    public ResetRequestImpl(NetworkResource networkResource, ISDNAddressString hlrNumber, ArrayList<IMSI> hlrList, long mapProtocolVersion) {
        this.networkResource = networkResource;
        this.hlrNumber = hlrNumber;
        this.hlrList = hlrList;

        this.rel18Update = false;

        if (mapProtocolVersion == 1 || mapProtocolVersion == 2)
            this.mapProtocolVersion = mapProtocolVersion;
    }

    public ResetRequestImpl(SendingNodeNumber sendingNodenumber,  ArrayList<IMSI> hlrList, MAPExtensionContainer extensionContainer,
            ArrayList<ResetId> resetIdList, InsertSubscriberDataArgs subscriptionData, DeleteSubscriberDataArgs subscriptionDataDeletion) {
        this.sendingNodenumber = sendingNodenumber;
        this.hlrList = hlrList;
        this.extensionContainer = extensionContainer;
        this.resetIdList = resetIdList;
        this.subscriptionData = subscriptionData;
        this.subscriptionDataDeletion = subscriptionDataDeletion;

        this.mapProtocolVersion = 2;
        this.rel18Update = true;// FIXME (this is a temporal hack)
    }

    public long getMapProtocolVersion() {
        return this.mapProtocolVersion;
    }

    @Override
    public MAPMessageType getMessageType() {
        return MAPMessageType.reset_Request;
    }

    @Override
    public int getOperationCode() {
        return MAPOperationCode.reset;
    }

    @Override
    public NetworkResource getNetworkResource() {
        return this.networkResource;
    }

    @Override
    public ISDNAddressString getHlrNumber() {
        if (this.sendingNodenumber != null) {
            if (this.sendingNodenumber.getHlrNumber() != null)
                this.hlrNumber = this.sendingNodenumber.getHlrNumber();
        }
        return this.hlrNumber;
    }

    @Override
    public ArrayList<IMSI> getHlrList() {
        return this.hlrList;
    }

    @Override
    public SendingNodeNumber getSendingNodenumber() {
        return this.sendingNodenumber;
    }

    @Override
    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    @Override
    public ArrayList<ResetId> getResetIdList() {
        return this.resetIdList;
    }

    @Override
    public InsertSubscriberDataArgs getSubscriptionData() {
        return this.subscriptionData;
    }

    @Override
    public DeleteSubscriberDataArgs getSubscriptionDataDeletion() {
        return this.subscriptionDataDeletion;
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
        this.networkResource = null;
        this.hlrNumber = null;
        this.hlrList = null;
        this.sendingNodenumber = null;
        this.extensionContainer = null;
        this.resetIdList = null;
        this.subscriptionData = null;
        this.subscriptionDataDeletion = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0) {
                break;
            }

            int tag = ais.readTag();

            switch (num) {
                case 0:
                    if (tag != Tag.ENUMERATED && tag != Tag.STRING_OCTET) {
                        this.sendingNodenumber = new SendingNodeNumberImpl();
                        ((SendingNodeNumberImpl) this.sendingNodenumber).decodeAll(ais);
                    } else if (tag == Tag.ENUMERATED) {
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ".networkResource: is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        int i1 = (int) ais.readInteger();
                        this.networkResource = NetworkResource.getInstance(i1);
                    } else {
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ".hlrNumber: is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.sendingNodenumber = new SendingNodeNumberImpl();
                        ((SendingNodeNumberImpl) this.sendingNodenumber).decodeAll(ais);
                    }
                    break;
                default:
                    switch (ais.getTagClass()) {
                        case Tag.CLASS_UNIVERSAL:
                            switch (tag) {
                                case Tag.STRING_OCTET:
                                    if (!ais.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ".hlrNumber: is not primitive",
                                                MAPParsingComponentExceptionReason.MistypedParameter);
                                    this.hlrNumber = new ISDNAddressStringImpl();
                                    ((ISDNAddressStringImpl) this.hlrNumber).decodeAll(ais);
                                    break;
                                case Tag.SEQUENCE:
                                    if (ais.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ".hlrList: Parameter is primitive",
                                                MAPParsingComponentExceptionReason.MistypedParameter);
                                    AsnInputStream ais2 = ais.readSequenceStream();
                                    this.hlrList = new ArrayList<>();
                                    while (true) {
                                        if (ais2.available() == 0)
                                            break;

                                        int tag2 = ais2.readTag();
                                        if (tag2 != Tag.STRING_OCTET || ais2.getTagClass() != Tag.CLASS_UNIVERSAL || !ais2.isTagPrimitive())
                                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                    + ": bad hlrList element tag or tagClass or is not primitive ", MAPParsingComponentExceptionReason.MistypedParameter);

                                        IMSIImpl imsi = new IMSIImpl();
                                        imsi.decodeAll(ais2);
                                        this.hlrList.add(imsi);
                                    }
                                    if (this.hlrList.isEmpty() || this.hlrList.size() > 50) {
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + ": Parameter hlrList size must be from 1 to 50, found: " + this.hlrList.size(),
                                                MAPParsingComponentExceptionReason.MistypedParameter);
                                    }
                                    break;

                                default:
                                    ais.advanceElement();
                                    break;
                            }
                            break;

                        case Tag.CLASS_CONTEXT_SPECIFIC:
                            switch (tag) {
                                case _TAG_extensionContainer:
                                    if (ais.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + ".extensionContainer: is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                                    this.extensionContainer = new MAPExtensionContainerImpl();
                                    ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                                    break;
                                case _TAG_reset_Id_List:
                                    if (ais.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + ".resetIdList: Parameter is primitive",
                                                MAPParsingComponentExceptionReason.MistypedParameter);

                                    AsnInputStream ais1 = ais.readSequenceStream();
                                    this.resetIdList = new ArrayList<>();
                                    while (true) {
                                        if (ais1.available() == 0)
                                            break;

                                        int tag1 = ais1.readTag();
                                        if (tag1 != Tag.STRING_OCTET || ais1.getTagClass() != Tag.CLASS_UNIVERSAL || !ais1.isTagPrimitive())
                                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                    + ": bad resetIdList element tag or tagClass or is not primitive ",
                                                    MAPParsingComponentExceptionReason.MistypedParameter);

                                        ResetIdImpl resetId = new ResetIdImpl();
                                        (resetId).decodeAll(ais1);
                                        resetIdList.add(resetId);
                                    }
                                    if (this.resetIdList.isEmpty() || this.resetIdList.size() > 50) {
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + ": Parameter resetIdList size must be from 1 to 50, found: "
                                                + this.resetIdList.size(),
                                                MAPParsingComponentExceptionReason.MistypedParameter);
                                    }
                                    break;
                                case _TAG_subscriptionData:
                                    if (ais.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + ".subscriptionData: Parameter is primitive",
                                                MAPParsingComponentExceptionReason.MistypedParameter);
                                    this.subscriptionData = new InsertSubscriberDataArgsImpl();
                                    ((InsertSubscriberDataArgsImpl) subscriptionData).decodeAll(ais);
                                    break;
                                case _TAG_subscriptionDataDeletion:
                                    if (ais.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + ".subscriptionDataDeletion: Parameter is primitive",
                                                MAPParsingComponentExceptionReason.MistypedParameter);
                                    this.subscriptionDataDeletion = new DeleteSubscriberDataArgsImpl();
                                    ((DeleteSubscriberDataArgsImpl) subscriptionDataDeletion).decodeAll(ais);
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
                    break;
            }
            num++;
        }

        if (this.hlrNumber == null && this.sendingNodenumber == null)
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ": either hlrNumber or sendingNodenumber " +
                    "are mandatory but neither are present", MAPParsingComponentExceptionReason.MistypedParameter);
    }

    @Override
    public void encodeAll(AsnOutputStream asnOutputStream) throws MAPException {
        this.encodeAll(asnOutputStream, this.getTagClass(), this.getTag());
    }

    @Override
    public void encodeAll(AsnOutputStream asnOutputStream, int tagClass, int tag) throws MAPException {
        try {
            asnOutputStream.writeTag(tagClass, this.getIsPrimitive(), tag);
            int posk = asnOutputStream.StartContentDefiniteLength();
            this.encodeData(asnOutputStream);
            asnOutputStream.FinalizeContent(posk);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {

        if (!this.rel18Update) { // TODO (to be reviewed, as this is a temporal hack)

            if (this.mapProtocolVersion == 1) {

                if (this.networkResource == null)
                    throw new MAPException("For MAP version 1 networkResource must be present in " + _PrimitiveName + ", but it is empty");

                try {
                    asnOutputStream.writeInteger(Tag.CLASS_UNIVERSAL, Tag.ENUMERATED, this.networkResource.getCode());
                } catch (IOException e) {
                    throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter hlrList", e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter hlrList", e);
                }
            }

            if (this.hlrNumber == null)
                throw new MAPException("hlrNumber must not be null for" + _PrimitiveName);

            if (this.hlrList != null && (this.hlrList.isEmpty() || this.hlrList.size() > 50))
                throw new MAPException("hlrList size must be from 1 to 50, found: " + this.hlrList.size());

            ((ISDNAddressStringImpl) this.hlrNumber).encodeAll(asnOutputStream);

            if (this.hlrList != null) {
                try {
                    asnOutputStream.writeTag(Tag.CLASS_UNIVERSAL, false, Tag.SEQUENCE);
                    int pos = asnOutputStream.StartContentDefiniteLength();
                    for (IMSI imsi : this.hlrList) {
                        ((IMSIImpl) imsi).encodeAll(asnOutputStream);
                    }
                    asnOutputStream.FinalizeContent(pos);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + " parameter hlrList: " + e.getMessage(), e);
                }
            }

        } else {

            if (this.sendingNodenumber == null)
                throw new MAPException("Error when encoding " + _PrimitiveName + ": sendingNodenumber must not be null");

            if (this.hlrList != null && (this.hlrList.isEmpty() || this.hlrList.size() > 50))
                throw new MAPException("Error when encoding " + _PrimitiveName + ": hlrList size must be from 1 to 50, found: " + this.hlrList.size());

            if (this.hlrList != null && this.resetIdList != null)
                throw new MAPException("Error when encoding " + _PrimitiveName + ": hlrList shall not be present if resetIdList is present ");

            if (this.resetIdList != null && (this.resetIdList.isEmpty() || this.resetIdList.size() > 50))
                throw new MAPException("Error when encoding " + _PrimitiveName + ": resetIdList size must be from 1 to 50, found: " + this.resetIdList.size());

            if (this.subscriptionData != null) {
                if (this.resetIdList == null)
                    throw new MAPException("Error when encoding " + _PrimitiveName + ": subscriptionData shall be absent if resetIdList is absent");
                if (this.subscriptionDataDeletion != null)
                    throw new MAPException("Error when encoding " + _PrimitiveName + ": subscriptionData and subscriptionDataDeletion can not be both present");
            }

            if (this.subscriptionDataDeletion != null) {
                if (this.resetIdList == null)
                    throw new MAPException("Error when encoding " + _PrimitiveName + ": subscriptionDataDeletion shall be absent if resetIdList is absent");
            }

            ((SendingNodeNumberImpl) this.sendingNodenumber).encodeAll(asnOutputStream);

            if (this.hlrList != null) {
                try {
                    asnOutputStream.writeTag(Tag.CLASS_UNIVERSAL, false, Tag.SEQUENCE);
                    int pos = asnOutputStream.StartContentDefiniteLength();
                    for (IMSI imsi : this.hlrList) {
                        ((IMSIImpl) imsi).encodeAll(asnOutputStream);
                    }
                    asnOutputStream.FinalizeContent(pos);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + " parameter hlrList: " + e.getMessage(), e);
                }
            }

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_extensionContainer);

            if (this.resetIdList != null) {
                try {
                    asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_reset_Id_List);
                    int pos = asnOutputStream.StartContentDefiniteLength();
                    for (ResetId elem : this.resetIdList) {
                        ((ResetIdImpl) elem).encodeAll(asnOutputStream);
                    }
                    asnOutputStream.FinalizeContent(pos);
                } catch (AsnException e) {
                    throw new MAPException("AsnException when encoding " + _PrimitiveName + ".resetIdList: " + e.getMessage(), e);
                }
            }

            if (this.subscriptionData != null)
                ((InsertSubscriberDataArgsImpl) this.subscriptionData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_subscriptionData);

            if (this.subscriptionDataDeletion != null)
                ((DeleteSubscriberDataArgsImpl) this.subscriptionDataDeletion).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_subscriptionDataDeletion);
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.networkResource != null) {
            sb.append("networkResource=");
            sb.append(this.networkResource);
            sb.append(", ");
        }

        if (this.hlrNumber != null) {
            sb.append("hlrNumber=");
            sb.append(this.hlrNumber);
            sb.append(", ");
        }

        if (this.sendingNodenumber != null) {
            sb.append("sendingNodenumber=");
            sb.append(this.sendingNodenumber);
            sb.append(", ");
        }

        if (this.hlrList != null) {
            sb.append("hlrList=[");
            boolean firstItem = true;
            for (IMSI imsi : this.hlrList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(imsi.toString());
            }
            sb.append("], ");
        }

        if (this.extensionContainer != null) {
            sb.append("extensionContainer=");
            sb.append(this.extensionContainer);
            sb.append(", ");
        }

        if (this.resetIdList != null) {
            sb.append("resetIdList=");
            sb.append(this.resetIdList);
            sb.append(", ");
        }

        if (this.subscriptionData != null) {
            sb.append("subscriptionData=");
            sb.append(this.subscriptionData);
            sb.append(", ");
        }

        if (this.subscriptionDataDeletion != null) {
            sb.append("subscriptionDataDeletion=");
            sb.append(this.subscriptionDataDeletion);
            sb.append(", ");
        }

        sb.append("mapProtocolVersion=");
        sb.append(this.mapProtocolVersion);

        sb.append("]");

        return sb.toString();
    }

}

