package org.restcomm.protocols.ss7.map.service.sms;

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
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.TeleserviceCode;
import org.restcomm.protocols.ss7.map.api.service.sms.CorrelationID;
import org.restcomm.protocols.ss7.map.api.service.sms.SMDeliveryNotIntended;
import org.restcomm.protocols.ss7.map.api.service.sms.SM_RP_MTI;
import org.restcomm.protocols.ss7.map.api.service.sms.SM_RP_SMEA;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMRequest;
import org.restcomm.protocols.ss7.map.primitives.AddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.TeleserviceCodeImpl;

import java.io.IOException;

/**
 *
 * @author sergey vetyutnev
 * @author eva ogallar
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class SendRoutingInfoForSMRequestImpl extends SmsMessageImpl implements SendRoutingInfoForSMRequest {

    protected static final int _TAG_msisdn = 0;
    protected static final int _TAG_sm_RP_PRI = 1;
    protected static final int _TAG_serviceCentreAddress = 2;
    protected static final int _TAG_teleservice = 5; // MAP V1 only
    protected static final int _TAG_extensionContainer = 6;
    protected static final int _TAG_gprsSupportIndicator = 7;
    protected static final int _TAG_sm_RP_MTI = 8;
    protected static final int _TAG_sm_RP_SMEA = 9;
    protected static final int _TAG_smDeliveryNotIntended = 10;
    protected static final int _TAG_ipSmGwGuidanceIndicator = 11;
    protected static final int _TAG_imsi = 12;
    protected static final int _TAG_singleAttemptDelivery = 13;
    protected static final int _TAG_t4TriggerIndicator = 14;
    protected static final int _TAG_correlationID = 15;
    protected static final int _TAG_smsf_supportIndicator = 16;

    protected String _PrimitiveName = "SendRoutingInfoForSMRequest";

    private ISDNAddressString msisdn;
    private boolean sm_RP_PRI;
    private AddressString serviceCentreAddress;
    private MAPExtensionContainer extensionContainer;
    private boolean gprsSupportIndicator;
    private SM_RP_MTI sM_RP_MTI;
    private SM_RP_SMEAImpl sM_RP_SMEA;
    private TeleserviceCode teleservice;
    private boolean ipSmGwGuidanceIndicator;
    private SMDeliveryNotIntended smDeliveryNotIntended;
    private boolean t4TriggerIndicator;
    private boolean singleAttemptDelivery;
    private IMSI imsi;
    private CorrelationID correlationID;
    private boolean smsfSupportIndicator;

    public SendRoutingInfoForSMRequestImpl() {
    }

    public SendRoutingInfoForSMRequestImpl(ISDNAddressString msisdn, boolean sm_RP_PRI, AddressString serviceCentreAddress,
            MAPExtensionContainer extensionContainer, boolean gprsSupportIndicator, SM_RP_MTI sM_RP_MTI, SM_RP_SMEA sM_RP_SMEA,
            SMDeliveryNotIntended smDeliveryNotIntended, boolean ipSmGwGuidanceIndicator, IMSI imsi, boolean t4TriggerIndicator,
            boolean singleAttemptDelivery, TeleserviceCode teleservice, CorrelationID correlationID, boolean smsfSupportIndicator) {
        this.msisdn = msisdn;
        this.sm_RP_PRI = sm_RP_PRI;
        this.serviceCentreAddress = serviceCentreAddress;
        this.extensionContainer = extensionContainer;
        this.gprsSupportIndicator = gprsSupportIndicator;
        this.sM_RP_MTI = sM_RP_MTI;
        this.sM_RP_SMEA = (SM_RP_SMEAImpl) sM_RP_SMEA;
        this.smDeliveryNotIntended = smDeliveryNotIntended;
        this.ipSmGwGuidanceIndicator = ipSmGwGuidanceIndicator;
        this.imsi = imsi;
        this.t4TriggerIndicator = t4TriggerIndicator;
        this.singleAttemptDelivery = singleAttemptDelivery;
        this.teleservice = teleservice;
        this.correlationID = correlationID;
        this.smsfSupportIndicator = smsfSupportIndicator;
    }

    public MAPMessageType getMessageType() {
        return MAPMessageType.sendRoutingInfoForSM_Request;
    }

    public int getOperationCode() {
        return MAPOperationCode.sendRoutingInfoForSM;
    }

    public ISDNAddressString getMsisdn() {
        return this.msisdn;
    }

    public boolean getSm_RP_PRI() {
        return this.sm_RP_PRI;
    }

    public AddressString getServiceCentreAddress() {
        return this.serviceCentreAddress;
    }

    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    public boolean getGprsSupportIndicator() {
        return this.gprsSupportIndicator;
    }

    public SM_RP_MTI getSM_RP_MTI() {
        return this.sM_RP_MTI;
    }

    public SM_RP_SMEA getSM_RP_SMEA() {
        return this.sM_RP_SMEA;
    }

    public TeleserviceCode getTeleservice() {
        return this.teleservice;
    }

    public boolean getIpSmGwGuidanceIndicator() {
        return ipSmGwGuidanceIndicator;
    }

    public boolean getT4TriggerIndicator() {
        return t4TriggerIndicator;
    }

    public boolean getSingleAttemptDelivery() {
        return singleAttemptDelivery;
    }

    public IMSI getImsi() {
        return imsi;
    }

    public SMDeliveryNotIntended getSmDeliveryNotIntended() {
        return smDeliveryNotIntended;
    }

    public CorrelationID getCorrelationID() {
        return correlationID;
    }

    public boolean getSmsfSupportIndicator() {
        return smsfSupportIndicator;
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
                MapBerSupport.logFallback("SendRoutingInfoForSMRequest", t);
                this.msisdn = null;
                this.sm_RP_PRI = false;
                this.serviceCentreAddress = null;
            }
        }
        this._decode(asnInputStream, length);
    }

    private void _decodeBer(byte[] buf, int offset, int length)
            throws AsnException, IOException, MAPParsingComponentException {
        BerCursor c = BerCursor.wrapHeap(buf, offset, length);
        // msisdn [0] ISDN-AddressString
        if (!c.hasMore())
            throw new AsnException("SendRoutingInfoForSMRequest: missing msisdn");
        c.readTag();
        if (c.tagClass() != BerTag.CONTEXT || !c.isPrimitive() || c.tag() != _TAG_msisdn)
            throw new AsnException("SendRoutingInfoForSMRequest: bad msisdn");
        ISDNAddressStringImpl m = new ISDNAddressStringImpl();
        m.decodeData(MapBerSupport.taggedValueStream(c), c.valueLength());
        this.msisdn = m;
        c.skipValue();

        // sm-RP-PRI [1] BOOLEAN
        if (!c.hasMore())
            throw new AsnException("SendRoutingInfoForSMRequest: missing sm-RP-PRI");
        c.readTag();
        if (c.tagClass() != BerTag.CONTEXT || !c.isPrimitive() || c.tag() != _TAG_sm_RP_PRI || c.valueLength() != 1)
            throw new AsnException("SendRoutingInfoForSMRequest: bad sm-RP-PRI");
        this.sm_RP_PRI = c.heapBuffer()[c.valueOffset()] != 0;
        c.skipValue();

        // serviceCentreAddress [2] AddressString
        if (!c.hasMore())
            throw new AsnException("SendRoutingInfoForSMRequest: missing serviceCentreAddress");
        c.readTag();
        if (c.tagClass() != BerTag.CONTEXT || !c.isPrimitive() || c.tag() != _TAG_serviceCentreAddress)
            throw new AsnException("SendRoutingInfoForSMRequest: bad serviceCentreAddress");
        ISDNAddressStringImpl sca = new ISDNAddressStringImpl();
        ((AddressStringImpl) sca).decodeData(MapBerSupport.taggedValueStream(c), c.valueLength());
        this.serviceCentreAddress = sca;
        c.skipValue();

        if (c.hasMore()) // optional extensionContainer / gprsSupportIndicator / ... -> fallback
            throw new AsnException("SendRoutingInfoForSMRequest: optional fields present, fallback");
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.msisdn = null;
        this.sm_RP_PRI = false;
        this.serviceCentreAddress = null;
        this.extensionContainer = null;
        this.gprsSupportIndicator = false;
        this.sM_RP_MTI = null;
        this.sM_RP_SMEA = null;
        this.smDeliveryNotIntended = null;
        this.ipSmGwGuidanceIndicator = false;
        this.imsi = null;
        this.t4TriggerIndicator = false;
        this.singleAttemptDelivery = false;
        this.correlationID = null;
        this.smsfSupportIndicator = false;
        // MAP V1 only
        // this.teleservice = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            switch (num) {
                case 0:
                    // msisdn
                    if (ais.getTagClass() != Tag.CLASS_CONTEXT_SPECIFIC || !ais.isTagPrimitive() || tag != _TAG_msisdn)
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".msisdn: Parameter 0 bad tag or tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.msisdn = new ISDNAddressStringImpl();
                    ((ISDNAddressStringImpl) this.msisdn).decodeAll(ais);
                    break;

                case 1:
                    // sm-RP-PRI
                    if (ais.getTagClass() != Tag.CLASS_CONTEXT_SPECIFIC || !ais.isTagPrimitive() || tag != _TAG_sm_RP_PRI)
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".sm-RP-PRI: Parameter 1 bad tag class or tag or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.sm_RP_PRI = ais.readBoolean();
                    break;

                case 2:
                    // serviceCentreAddress
                    if (ais.getTagClass() != Tag.CLASS_CONTEXT_SPECIFIC || !ais.isTagPrimitive()
                            || tag != _TAG_serviceCentreAddress)
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".serviceCentreAddress: Parameter 2 bad tag class or tag or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.serviceCentreAddress = new ISDNAddressStringImpl();
                    ((AddressStringImpl) this.serviceCentreAddress).decodeAll(ais);
                    break;

                default:
                    if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {

                        switch (tag) {
                            case _TAG_extensionContainer:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".extensionContainer: Parameter is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.extensionContainer = new MAPExtensionContainerImpl();
                                ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                                break;

                            case _TAG_gprsSupportIndicator:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".gprsSupportIndicator: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.gprsSupportIndicator = true;
                                break;

                            case _TAG_sm_RP_MTI:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".sM_RP_MTI: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                int i1 = (int) ais.readInteger();
                                this.sM_RP_MTI = SM_RP_MTI.getInstance(i1);
                                break;

                            case _TAG_sm_RP_SMEA:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".sM_RP_SMEA: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.sM_RP_SMEA = new SM_RP_SMEAImpl();
                                this.sM_RP_SMEA.decodeAll(ais);
                                break;

                            // MAP V1 only
                            case _TAG_teleservice:

                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".teleservice: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.teleservice = new TeleserviceCodeImpl();
                                ((TeleserviceCodeImpl) this.teleservice).decodeAll(ais);
                                break;

                            case _TAG_smDeliveryNotIntended:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".smDeliveryNotIntended: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                int i2 = (int) ais.readInteger();
                                this.smDeliveryNotIntended = SMDeliveryNotIntended.getInstance(i2);
                                break;

                            case _TAG_ipSmGwGuidanceIndicator:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".ipSmGwGuidanceIndicator: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.ipSmGwGuidanceIndicator = true;
                                break;

                            case _TAG_imsi:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".imsi: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.imsi = new IMSIImpl();
                                ((IMSIImpl) this.imsi).decodeAll(ais);
                                break;

                            case _TAG_t4TriggerIndicator:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".t4TriggerIndicator: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.t4TriggerIndicator = true;
                                break;

                            case _TAG_singleAttemptDelivery:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".singleAttemptDelivery: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.singleAttemptDelivery = true;
                                break;

                            case _TAG_correlationID:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".correlationID: Parameter is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.correlationID = new CorrelationIDImpl();
                                ((CorrelationIDImpl) this.correlationID).decodeAll(ais);
                                break;

                            case _TAG_smsf_supportIndicator:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".smsfSupportIndicator: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.smsfSupportIndicator = true;
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

        if (msisdn == null || serviceCentreAddress == null) {
            throw new MAPException("msisdn, sm_RP_PRI and serviceCentreAddress must not be null");
        }

        try {
            ((ISDNAddressStringImpl) this.msisdn).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_msisdn);

            asnOutputStream.writeBoolean(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_sm_RP_PRI, this.sm_RP_PRI);

            ((AddressStringImpl) this.serviceCentreAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                    _TAG_serviceCentreAddress);

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_extensionContainer);

            if (this.gprsSupportIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_gprsSupportIndicator);

            if (this.sM_RP_MTI != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_sm_RP_MTI, this.sM_RP_MTI.getCode());

            if (this.sM_RP_SMEA != null)
                this.sM_RP_SMEA.encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_sm_RP_SMEA);

            if (this.smDeliveryNotIntended != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_smDeliveryNotIntended, this.smDeliveryNotIntended.getCode());

            if (this.ipSmGwGuidanceIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_ipSmGwGuidanceIndicator);

            if (this.imsi != null)
                ((IMSIImpl) this.imsi).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,_TAG_imsi);

            if (this.t4TriggerIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_t4TriggerIndicator);

            if (this.singleAttemptDelivery)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_singleAttemptDelivery);

            if (this.correlationID != null) {
                ((CorrelationIDImpl) this.correlationID).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_correlationID);
                if (this.correlationID.getHlrId() != null || this.correlationID.getSipUriA() != null) {
                    throw new MAPException("SIP-URI-A and HLR-ID shall be absent from correlationID at " + _PrimitiveName);
                }
            }

            if (this.smsfSupportIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_smsf_supportIndicator);

            // MAP V1 only
            if (this.teleservice != null)
                ((TeleserviceCodeImpl) this.teleservice).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_teleservice);

        } catch (IOException e) {
            throw new MAPException("IOException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SendRoutingInfoForSMRequest [");

        /*if (this.getMAPDialog() != null) {
            sb.append("DialogId=").append(this.getMAPDialog().getLocalDialogId());
        }*/

        if (this.msisdn != null) {
            sb.append("msisdn=");
            sb.append(this.msisdn);
        }
        if (this.sm_RP_PRI)
            sb.append(", sm_RP_PRI");
        if (this.serviceCentreAddress != null) {
            sb.append(", serviceCentreAddress=");
            sb.append(this.serviceCentreAddress);
        }
        if (this.extensionContainer != null) {
            sb.append(", extensionContainer=");
            sb.append(this.extensionContainer);
        }
        if (this.gprsSupportIndicator) {
            sb.append(", gprsSupportIndicator");
        }
        if (this.sM_RP_MTI != null) {
            sb.append(", sM_RP_MTI=");
            sb.append(this.sM_RP_MTI);
        }
        if (this.sM_RP_SMEA != null) {
            sb.append(", sM_RP_SMEA=");
            sb.append(this.sM_RP_SMEA);
        }

        if (this.smDeliveryNotIntended != null) {
            sb.append(", smDeliveryNotIntended=");
            sb.append(this.smDeliveryNotIntended);
        }
        if (this.ipSmGwGuidanceIndicator) {
            sb.append(", ipSmGwGuidanceIndicator");
        }
        if (this.imsi != null) {
            sb.append(", imsi=");
            sb.append(this.imsi);
        }
        if (this.t4TriggerIndicator) {
            sb.append(", t4TriggerIndicator");
        }
        if (this.singleAttemptDelivery) {
            sb.append(", singleAttemptDelivery");
        }

        if (this.teleservice != null) {
            sb.append(", teleservice=");
            sb.append(this.teleservice);
        }

        if (this.smsfSupportIndicator) {
            sb.append(", smsfSupportIndicator");
            sb.append(this.smsfSupportIndicator);
        }

        sb.append("]");

        return sb.toString();
    }
}
