package org.restcomm.protocols.ss7.map.service.sms;

import java.io.IOException;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.BerCursor;
import org.mobicents.protocols.asn.BerTag;
import org.restcomm.protocols.ss7.map.MapBerSupport;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPMessageType;
import org.restcomm.protocols.ss7.map.api.MAPOperationCode;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.Time;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.NetworkNodeDiameterAddress;
import org.restcomm.protocols.ss7.map.api.service.sms.CorrelationID;
import org.restcomm.protocols.ss7.map.api.service.sms.MtForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.SM_RP_DA;
import org.restcomm.protocols.ss7.map.api.service.sms.SM_RP_OA;
import org.restcomm.protocols.ss7.map.api.service.sms.SmsSignalInfo;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.TimeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.NetworkNodeDiameterAddressImpl;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class MtForwardShortMessageRequestImpl extends SmsMessageImpl implements MtForwardShortMessageRequest {

    protected static final int _TAG_smsOverIPOnlyIndicator = 0;
    protected static final int _TAG_correlationID = 1;
    protected static final int _TAG_maximumRetransmissionTime = 2;
    protected static final int _TAG_smsGmscAddress = 3;
    protected static final int _TAG_smsGmscDiameterAddress = 4;

    protected String _PrimitiveName = "MtForwardShortMessageRequest";

    private SM_RP_DA sM_RP_DA;
    private SM_RP_OA sM_RP_OA;
    private SmsSignalInfoImpl sM_RP_UI;
    private boolean moreMessagesToSend;
    private MAPExtensionContainer extensionContainer;
    private Integer smDeliveryTimer;
    private Time smDeliveryStartTime;
    private boolean smsOverIPOnlyIndicator;
    private CorrelationID correlationID;
    private Time maximumRetransmissionTime;
    private ISDNAddressString smsGmscAddress;
    private NetworkNodeDiameterAddress smsGmscDiameterAddress;

    public MtForwardShortMessageRequestImpl() {
    }

    public MtForwardShortMessageRequestImpl(SM_RP_DA sM_RP_DA, SM_RP_OA sM_RP_OA, SmsSignalInfo sM_RP_UI,
            boolean moreMessagesToSend, MAPExtensionContainer extensionContainer, Integer smDeliveryTimer,
            Time smDeliveryStartTime, boolean smsOverIPOnlyIndicator, CorrelationID correlationID,
            Time maximumRetransmissionTime, ISDNAddressString smsGmscAddress, NetworkNodeDiameterAddress smsGmscDiameterAddress) {
        this.sM_RP_DA = sM_RP_DA;
        this.sM_RP_OA = sM_RP_OA;
        this.sM_RP_UI = (SmsSignalInfoImpl) sM_RP_UI;
        this.moreMessagesToSend = moreMessagesToSend;
        this.extensionContainer = extensionContainer;
        this.smDeliveryTimer = smDeliveryTimer;
        this.smDeliveryStartTime = smDeliveryStartTime;
        this.smsOverIPOnlyIndicator = smsOverIPOnlyIndicator;
        this.correlationID = correlationID;
        this.maximumRetransmissionTime = maximumRetransmissionTime;
        this.smsGmscAddress = smsGmscAddress;
        this.smsGmscDiameterAddress = smsGmscDiameterAddress;
    }

    public MAPMessageType getMessageType() {
        return MAPMessageType.mtForwardSM_Request;
    }

    public int getOperationCode() {
        return MAPOperationCode.mt_forwardSM;
    }

    public SM_RP_DA getSM_RP_DA() {
        return this.sM_RP_DA;
    }

    public SM_RP_OA getSM_RP_OA() {
        return this.sM_RP_OA;
    }

    public SmsSignalInfo getSM_RP_UI() {
        return this.sM_RP_UI;
    }

    public boolean getMoreMessagesToSend() {
        return this.moreMessagesToSend;
    }

    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    public Integer getSmDeliveryTimer() {
        return this.smDeliveryTimer;
    }

    @Override
    public Time getSmDeliveryStartTime() {
        return this.smDeliveryStartTime;
    }

    public boolean getSmsOverIPOnlyIndicator() {
        return this.smsOverIPOnlyIndicator;
    }

    @Override
    public CorrelationID getCorrelationID() {
        return this.correlationID;
    }

    @Override
    public Time getMaximumRetransmissionTime() {
        return this.maximumRetransmissionTime;
    }

    @Override
    public ISDNAddressString getSmsGmscAddress() {
        return this.smsGmscAddress;
    }

    @Override
    public NetworkNodeDiameterAddress getSmsGmscDiameterAddress() {
        return this.smsGmscDiameterAddress;
    }

    public int getTag() throws MAPException {
        return Tag.SEQUENCE;
    }

    public int getTagClass() {
        return Tag.CLASS_UNIVERSAL;
    }

    public boolean getIsPrimitive() {
        return false;
    }

    public void decodeAll(AsnInputStream asnInputStream) throws MAPParsingComponentException {
        try {
            int length = asnInputStream.readLength();
            this._decodeDispatch(asnInputStream, length);
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
            this._decodeDispatch(asnInputStream, length);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    private void _decodeDispatch(AsnInputStream asnInputStream, int length)
            throws MAPParsingComponentException, IOException, AsnException {
        if (MapBerSupport.useBer(asnInputStream)) {
            byte[] buf = asnInputStream.getBuffer();
            int off = MapBerSupport.absOffset(asnInputStream);
            try {
                this._decodeBer(buf, off, length);
                asnInputStream.advance(length);
                MapBerSupport.recordBerOk();
                return;
            } catch (Throwable t) {
                MapBerSupport.logFallback("MtForwardShortMessageRequest", t);
                this.sM_RP_DA = null;
                this.sM_RP_OA = null;
                this.sM_RP_UI = null;
            }
        }
        this._decode(asnInputStream, length);
    }

    private void _decodeBer(byte[] buf, int offset, int length)
            throws AsnException, IOException, MAPParsingComponentException {
        BerCursor c = BerCursor.wrapHeap(buf, offset, length);
        if (!c.hasMore())
            throw new AsnException(_PrimitiveName + ": missing sm-RP-DA");
        c.readTag();
        if (c.tagClass() != BerTag.CONTEXT || !c.isPrimitive())
            throw new AsnException(_PrimitiveName + ": bad sm-RP-DA tag");
        SM_RP_DAImpl da = new SM_RP_DAImpl();
        da.decodeData(MapBerSupport.taggedValueStream(c), c.valueLength());
        this.sM_RP_DA = da;
        c.skipValue();

        if (!c.hasMore())
            throw new AsnException(_PrimitiveName + ": missing sm-RP-OA");
        c.readTag();
        if (c.tagClass() != BerTag.CONTEXT || !c.isPrimitive())
            throw new AsnException(_PrimitiveName + ": bad sm-RP-OA tag");
        SM_RP_OAImpl oa = new SM_RP_OAImpl();
        oa.decodeData(MapBerSupport.taggedValueStream(c), c.valueLength());
        this.sM_RP_OA = oa;
        c.skipValue();

        if (!c.hasMore())
            throw new AsnException(_PrimitiveName + ": missing sm-RP-UI");
        c.readTag();
        if (c.tagClass() != BerTag.UNIVERSAL || !c.isPrimitive() || c.tag() != BerTag.OCTET_STRING)
            throw new AsnException(_PrimitiveName + ": bad sm-RP-UI tag");
        SmsSignalInfoImpl ui = new SmsSignalInfoImpl();
        ui.setData(c.heapBuffer(), c.valueOffset(), c.valueLength());
        this.sM_RP_UI = ui;
        c.skipValue();

        if (c.hasMore())
            throw new AsnException(_PrimitiveName + ": optional fields present, fallback");
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.sM_RP_DA = null;
        this.sM_RP_OA = null;
        this.sM_RP_UI = null;
        this.moreMessagesToSend = false;
        this.extensionContainer = null;
        this.smDeliveryTimer = null;
        this.smDeliveryStartTime = null;
        this.smsOverIPOnlyIndicator = false;
        this.correlationID = null;
        this.maximumRetransmissionTime = null;
        this.smsGmscAddress = null;
        this.smsGmscDiameterAddress = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            switch (num) {
                case 0:
                    // SM_RP_DA
                    if (ais.getTagClass() != Tag.CLASS_CONTEXT_SPECIFIC || !ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ": Parameter 0 bad tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.sM_RP_DA = new SM_RP_DAImpl();
                    ((SM_RP_DAImpl) this.sM_RP_DA).decodeAll(ais);
                    break;

                case 1:
                    // SM_RP_OA
                    if (ais.getTagClass() != Tag.CLASS_CONTEXT_SPECIFIC || !ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ": Parameter 1 bad tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.sM_RP_OA = new SM_RP_OAImpl();
                    ((SM_RP_OAImpl) this.sM_RP_OA).decodeAll(ais);
                    break;

                case 2:
                    // sm-RP-UI
                    if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ": Parameter 2 bad tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    if (tag != Tag.STRING_OCTET)
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ": Parameter 2 tag must be STRING_OCTET, found: " + tag,
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.sM_RP_UI = new SmsSignalInfoImpl();
                    this.sM_RP_UI.decodeAll(ais);
                    break;

                default:
                    if (tag == Tag.SEQUENCE && ais.getTagClass() == Tag.CLASS_UNIVERSAL) {
                        if (ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ".extensionContainer: Parameter is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.extensionContainer = new MAPExtensionContainerImpl();
                        ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                    } else if (tag == Tag.NULL && ais.getTagClass() == Tag.CLASS_UNIVERSAL) {
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ".moreMessagesToSend: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        ais.readNull();
                        this.moreMessagesToSend = true;
                    } else if (tag == Tag.INTEGER && ais.getTagClass() == Tag.CLASS_UNIVERSAL) {
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ".smDeliveryTimer: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.smDeliveryTimer = (int) ais.readInteger();
                    } else if (ais.getTagClass() == Tag.CLASS_UNIVERSAL) {
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ".smDeliveryStartTime: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        smDeliveryStartTime = new TimeImpl();
                        ((TimeImpl) smDeliveryStartTime).decodeAll(ais);
                    } else if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                        if (tag == _TAG_smsOverIPOnlyIndicator) {
                            // smsOverIP-OnlyIndicator   [0] NULL   OPTIONAL
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".smsOverIPOnlyIndicator: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            ais.readNull();
                            this.smsOverIPOnlyIndicator = true;
                        } else if (tag == _TAG_correlationID) {
                            // correlationID   [1] CorrelationID   OPTIONAL
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".correlationID: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.correlationID = new CorrelationIDImpl();
                            ((CorrelationIDImpl) this.correlationID).decodeAll(ais);
                        } else if (tag == _TAG_maximumRetransmissionTime) {
                            // maximumRetransmissionTime  [2] Time  OPTIONAL
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".maximumRetransmissionTime: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.maximumRetransmissionTime = new TimeImpl();
                            ((TimeImpl) this.maximumRetransmissionTime).decodeAll(ais);
                        } else if (tag == _TAG_smsGmscAddress) {
                            // smsGmscAddress   [3] ISDN-AddressString   OPTIONAL
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".smsGmscAddress: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.smsGmscAddress = new ISDNAddressStringImpl();
                            ((ISDNAddressStringImpl) this.smsGmscAddress).decodeAll(ais);
                        } else if (tag == _TAG_smsGmscDiameterAddress) {
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".smsGmscDiameterAddress: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.smsGmscDiameterAddress = new NetworkNodeDiameterAddressImpl();
                            ((NetworkNodeDiameterAddressImpl) this.smsGmscDiameterAddress).decodeAll(ais);
                        }
                    } else {
                        ais.advanceElement();
                    }
                    break;
            }

            num++;
        }

        if (num < 3)
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": Needs at least 3 mandatory parameters, found " + num,
                    MAPParsingComponentExceptionReason.MistypedParameter);
    }

    public void encodeAll(AsnOutputStream asnOutputStream) throws MAPException {
        this.encodeAll(asnOutputStream, Tag.CLASS_UNIVERSAL, Tag.SEQUENCE);
    }

    public void encodeAll(AsnOutputStream asnOutputStream, int tagClass, int tag) throws MAPException {
        try {
            asnOutputStream.writeTag(tagClass, false, tag);
            int pos = asnOutputStream.StartContentDefiniteLength();
            this.encodeData(asnOutputStream);
            asnOutputStream.FinalizeContent(pos);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        if (this.sM_RP_DA == null || this.sM_RP_OA == null || this.sM_RP_UI == null)
            throw new MAPException("sm_RP_DA,sm_RP_OA and sm_RP_UI must not be null");

        try {
            ((SM_RP_DAImpl) this.sM_RP_DA).encodeAll(asnOutputStream);
            ((SM_RP_OAImpl) this.sM_RP_OA).encodeAll(asnOutputStream);
            this.sM_RP_UI.encodeAll(asnOutputStream);

            if (this.moreMessagesToSend)
                asnOutputStream.writeNull();

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream);

            if (this.smDeliveryTimer != null)
                asnOutputStream.writeInteger(smDeliveryTimer);

            if (smDeliveryStartTime != null)
                ((TimeImpl) this.smDeliveryStartTime).encodeAll(asnOutputStream);

            if (smsOverIPOnlyIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_smsOverIPOnlyIndicator);

            if (correlationID != null)
                ((CorrelationIDImpl) this.correlationID).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_correlationID);

            if (this.maximumRetransmissionTime != null)
                ((TimeImpl) this.maximumRetransmissionTime).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_maximumRetransmissionTime);

            if (smsGmscAddress != null)
                ((ISDNAddressStringImpl) this.smsGmscAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_smsGmscAddress);

            if (smsGmscDiameterAddress != null)
                ((NetworkNodeDiameterAddressImpl) this.smsGmscDiameterAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_smsGmscDiameterAddress);

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

        if (this.getMAPDialog() != null) {
            sb.append("DialogId=").append(this.getMAPDialog().getLocalDialogId());
        }

        if (this.sM_RP_DA != null) {
            sb.append(", sm_RP_DA=");
            sb.append(this.sM_RP_DA);
        }
        if (this.sM_RP_OA != null) {
            sb.append(", sm_RP_OA=");
            sb.append(this.sM_RP_OA);
        }
        if (this.sM_RP_UI != null) {
            sb.append(", sm_RP_UI=[");
            sb.append(this.sM_RP_UI);
            sb.append("]");
        }
        if (this.extensionContainer != null) {
            sb.append(", extensionContainer=");
            sb.append(this.extensionContainer);
        }
        if (this.moreMessagesToSend) {
            sb.append(", moreMessagesToSend");
        }
        if (smDeliveryTimer != null) {
            sb.append(", smDeliveryTimer=");
            sb.append(this.smDeliveryTimer);
        }
        if (smDeliveryStartTime != null) {
            sb.append(", smDeliveryStartTime=");
            sb.append(this.smDeliveryStartTime);
        }
        if (smsOverIPOnlyIndicator) {
            sb.append(", smsOverIPOnlyIndicator=");
        }
        if (correlationID != null) {
            sb.append(", correlationID=");
            sb.append(this.correlationID);
        }
        if (maximumRetransmissionTime != null) {
            sb.append(", maximumRetransmissionTime=");
            sb.append(this.maximumRetransmissionTime);
        }
        if (smsGmscAddress != null) {
            sb.append(", smsGmscAddress=");
            sb.append(this.smsGmscAddress);
        }
        if (smsGmscDiameterAddress != null) {
            sb.append(", smsGmscDiameterAddress=");
            sb.append(this.smsGmscDiameterAddress);
        }

        sb.append("]");

        return sb.toString();
    }
}
