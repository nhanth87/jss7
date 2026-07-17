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
import org.restcomm.protocols.ss7.map.api.service.sms.InformServiceCentreRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.MWStatus;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;

/**
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class InformServiceCentreRequestImpl extends SmsMessageImpl implements InformServiceCentreRequest {

    protected static final int _TAG_AdditionalAbsentSubscriberDiagnosticSM = 0;
    protected static final int _TAG_SMSF_3GPP_AbsentSubscriberDiagnosticSM = 1;
    protected static final int _TAG_SMSF_NON_3GPP_AbsentSubscriberDiagnosticSM = 2;

    private ISDNAddressString storedMSISDN;
    private MWStatus mwStatus;
    private MAPExtensionContainer extensionContainer;
    private Integer absentSubscriberDiagnosticSM;
    private Integer additionalAbsentSubscriberDiagnosticSM;
    private Integer smsf3gppAbsentSubscriberDiagnosticSM;
    private Integer smsfNon3gppAbsentSubscriberDiagnosticSM;

    public InformServiceCentreRequestImpl() {
    }

    public InformServiceCentreRequestImpl(ISDNAddressString storedMSISDN, MWStatus mwStatus,
            MAPExtensionContainer extensionContainer, Integer absentSubscriberDiagnosticSM,
            Integer additionalAbsentSubscriberDiagnosticSM, Integer smsf3gppAbsentSubscriberDiagnosticSM,
            Integer smsfNon3gppAbsentSubscriberDiagnosticSM) {
        this.storedMSISDN = storedMSISDN;
        this.mwStatus = mwStatus;
        this.extensionContainer = extensionContainer;
        this.absentSubscriberDiagnosticSM = absentSubscriberDiagnosticSM;
        this.additionalAbsentSubscriberDiagnosticSM = additionalAbsentSubscriberDiagnosticSM;
        this.smsf3gppAbsentSubscriberDiagnosticSM = smsf3gppAbsentSubscriberDiagnosticSM;
        this.smsfNon3gppAbsentSubscriberDiagnosticSM = smsfNon3gppAbsentSubscriberDiagnosticSM;
    }

    @Override
    public MAPMessageType getMessageType() {
        return MAPMessageType.InformServiceCentre_Request;
    }

    @Override
    public int getOperationCode() {
        return MAPOperationCode.informServiceCentre;
    }

    @Override
    public ISDNAddressString getStoredMSISDN() {
        return this.storedMSISDN;
    }

    @Override
    public MWStatus getMwStatus() {
        return this.mwStatus;
    }

    @Override
    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    @Override
    public Integer getAbsentSubscriberDiagnosticSM() {
        return this.absentSubscriberDiagnosticSM;
    }

    @Override
    public Integer getAdditionalAbsentSubscriberDiagnosticSM() {
        return this.additionalAbsentSubscriberDiagnosticSM;
    }

    @Override
    public Integer getSmsf3gppAbsentSubscriberDiagnosticSM() {
        return smsf3gppAbsentSubscriberDiagnosticSM;
    }

    @Override
    public Integer getSmsfNon3gppAbsentSubscriberDiagnosticSM() {
        return smsfNon3gppAbsentSubscriberDiagnosticSM;
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
            throw new MAPParsingComponentException("IOException when decoding InformServiceCentreRequest: " + e.getMessage(),
                    e, MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding InformServiceCentreRequest: " + e.getMessage(),
                    e, MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    public void decodeData(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException {
        try {
            this._decodeDispatch(asnInputStream, length);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding InformServiceCentreRequest: " + e.getMessage(),
                    e, MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding InformServiceCentreRequest: " + e.getMessage(),
                    e, MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    private void _decodeDispatch(AsnInputStream asnInputStream, int length)
            throws MAPParsingComponentException, IOException, AsnException {
        if (MapBerSupport.useBer(asnInputStream)) {
            try {
                this._decodeBer(asnInputStream.getBuffer(), MapBerSupport.absOffset(asnInputStream), length);
                asnInputStream.advance(length);
                MapBerSupport.recordBerOk();
                return;
            } catch (Throwable t) {
                MapBerSupport.logFallback("InformServiceCentreRequest", t);
                this.storedMSISDN = null;
            }
        }
        this._decode(asnInputStream, length);
    }

    private void _decodeBer(byte[] buf, int offset, int length)
            throws AsnException, IOException, MAPParsingComponentException {
        BerCursor c = BerCursor.wrapHeap(buf, offset, length);
        if (!c.hasMore()) throw new AsnException("InformServiceCentreRequest: empty");
        c.readTag();
        if (c.tagClass() != BerTag.UNIVERSAL || !c.isPrimitive() || c.tag() != BerTag.OCTET_STRING)
            throw new AsnException("InformServiceCentreRequest: bad storedMSISDN");
        ISDNAddressStringImpl m = new ISDNAddressStringImpl();
        m.decodeData(MapBerSupport.taggedValueStream(c), c.valueLength());
        this.storedMSISDN = m; c.skipValue();

        if (c.hasMore()) throw new AsnException("InformServiceCentreRequest: optional fields present, fallback");
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.storedMSISDN = null;
        this.mwStatus = null;
        this.extensionContainer = null;
        this.absentSubscriberDiagnosticSM = null;
        this.additionalAbsentSubscriberDiagnosticSM = null;
        this.smsf3gppAbsentSubscriberDiagnosticSM = null;
        this.smsfNon3gppAbsentSubscriberDiagnosticSM = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            if (ais.getTagClass() == Tag.CLASS_UNIVERSAL) {

                switch (tag) {
                    case Tag.STRING_OCTET:
                        // storedMSISDN
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException(
                                    "Error while decoding informServiceCentreRequest: Parameter storedMSISDN is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        storedMSISDN = new ISDNAddressStringImpl();
                        ((ISDNAddressStringImpl) storedMSISDN).decodeAll(ais);
                        break;

                    case Tag.STRING_BIT:
                        // mw-Status
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException(
                                    "Error while decoding informServiceCentreRequest: Parameter mw-Status is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        mwStatus = new MWStatusImpl();
                        ((MWStatusImpl) mwStatus).decodeAll(ais);
                        break;

                    case Tag.SEQUENCE:
                        // extensionContainer
                        if (ais.isTagPrimitive())
                            throw new MAPParsingComponentException(
                                    "Error while decoding reportSMDeliveryStatusRequest: Parameter extensionContainer is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        extensionContainer = new MAPExtensionContainerImpl();
                        ((MAPExtensionContainerImpl) extensionContainer).decodeAll(ais);
                        break;

                    case Tag.INTEGER:
                        // absentSubscriberDiagnosticSM
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException(
                                    "Error while decoding informServiceCentreRequest: Parameter absentSubscriberDiagnosticSM is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        absentSubscriberDiagnosticSM = (int) ais.readInteger();
                        break;

                    default:
                        ais.advanceElement();
                        break;
                }
            } else if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {

                switch (tag) {
                    case InformServiceCentreRequestImpl._TAG_AdditionalAbsentSubscriberDiagnosticSM:
                        // additionalAbsentSubscriberDiagnosticSM
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException(
                                    "Error while decoding informServiceCentreRequest: Parameter deliveryOutcomeIndicator is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        additionalAbsentSubscriberDiagnosticSM = (int) ais.readInteger();
                        break;

                    case InformServiceCentreRequestImpl._TAG_SMSF_3GPP_AbsentSubscriberDiagnosticSM:
                        // smsf3gppAbsentSubscriberDiagnosticSM
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException(
                                    "Error while decoding informServiceCentreRequest: Parameter smsf3gppAbsentSubscriberDiagnosticSM is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        smsf3gppAbsentSubscriberDiagnosticSM = (int) ais.readInteger();
                        break;

                    case InformServiceCentreRequestImpl._TAG_SMSF_NON_3GPP_AbsentSubscriberDiagnosticSM:
                        // smsfNon3gppAbsentSubscriberDiagnosticSM
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException(
                                    "Error while decoding informServiceCentreRequest: Parameter smsfNon3gppAbsentSubscriberDiagnosticSM is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        smsfNon3gppAbsentSubscriberDiagnosticSM = (int) ais.readInteger();
                        break;

                    default:
                        ais.advanceElement();
                        break;
                }
            } else {
                ais.advanceElement();
            }
        }
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
            throw new MAPException("AsnException when encoding InformServiceCentreRequest: " + e.getMessage(), e);
        }
    }

    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        try {
            if (this.storedMSISDN != null)
                ((ISDNAddressStringImpl) this.storedMSISDN).encodeAll(asnOutputStream);

            if (this.mwStatus != null)
                ((MWStatusImpl) this.mwStatus).encodeAll(asnOutputStream);

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream);

            if (this.absentSubscriberDiagnosticSM != null)
                asnOutputStream.writeInteger(this.absentSubscriberDiagnosticSM);

            if (this.additionalAbsentSubscriberDiagnosticSM != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC,
                        InformServiceCentreRequestImpl._TAG_AdditionalAbsentSubscriberDiagnosticSM,
                        this.additionalAbsentSubscriberDiagnosticSM);

            if (this.smsf3gppAbsentSubscriberDiagnosticSM != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC,
                        InformServiceCentreRequestImpl._TAG_SMSF_3GPP_AbsentSubscriberDiagnosticSM,
                        this.smsf3gppAbsentSubscriberDiagnosticSM);

            if (this.smsfNon3gppAbsentSubscriberDiagnosticSM != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC,
                        InformServiceCentreRequestImpl._TAG_SMSF_NON_3GPP_AbsentSubscriberDiagnosticSM,
                        this.smsfNon3gppAbsentSubscriberDiagnosticSM);
        } catch (IOException e) {
            throw new MAPException("IOException when encoding InformServiceCentreRequest: " + e.getMessage(), e);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding InformServiceCentreRequest: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("InformServiceCentreRequest [");

        if (this.getMAPDialog() != null) {
            sb.append("DialogId=").append(this.getMAPDialog().getLocalDialogId());
        }

        if (this.storedMSISDN != null) {
            sb.append(", storedMSISDN=");
            sb.append(this.storedMSISDN);
        }
        if (this.mwStatus != null) {
            sb.append(", mwStatus=");
            sb.append(this.mwStatus);
        }
        if (this.extensionContainer != null) {
            sb.append(", extensionContainer=");
            sb.append(this.extensionContainer);
        }
        if (this.absentSubscriberDiagnosticSM != null) {
            sb.append(", absentSubscriberDiagnosticSM=");
            sb.append(this.absentSubscriberDiagnosticSM);
        }
        if (this.additionalAbsentSubscriberDiagnosticSM != null) {
            sb.append(", additionalAbsentSubscriberDiagnosticSM=");
            sb.append(this.additionalAbsentSubscriberDiagnosticSM);
        }
        if (this.smsf3gppAbsentSubscriberDiagnosticSM != null) {
            sb.append(", smsf3gppAbsentSubscriberDiagnosticSM=");
            sb.append(this.smsf3gppAbsentSubscriberDiagnosticSM);
        }
        if (this.smsfNon3gppAbsentSubscriberDiagnosticSM != null) {
            sb.append(", smsfNon3gppAbsentSubscriberDiagnosticSM=");
            sb.append(this.smsfNon3gppAbsentSubscriberDiagnosticSM);
        }

        sb.append("]");

        return sb.toString();
    }

}
