package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import java.io.IOException;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.BerCursor;
import org.mobicents.protocols.asn.BerTag;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.MapBerSupport;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPMessageType;
import org.restcomm.protocols.ss7.map.api.MAPOperationCode;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.EMLPPPriority;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.RequestedInfo;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.LMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.service.mobility.MobilityMessageImpl;

/**
*
* @author sergey vetyutnev
*
*/
public class ProvideSubscriberInfoRequestImpl extends MobilityMessageImpl implements ProvideSubscriberInfoRequest {

    protected static final int _TAG_imsi = 0;
    protected static final int _TAG_lmsi = 1;
    protected static final int _TAG_requestedInfo = 2;
    protected static final int _TAG_extensionContainer = 3;
    protected static final int _TAG_callPriority = 4;

    public static final String _PrimitiveName = "ProvideSubscriberInfoRequest";

    private IMSI imsi;
    private LMSI lmsi;
    private RequestedInfo requestedInfo;
    private MAPExtensionContainer extensionContainer;
    private EMLPPPriority callPriority;

    public ProvideSubscriberInfoRequestImpl() {
    }

    public ProvideSubscriberInfoRequestImpl(IMSI imsi, LMSI lmsi, RequestedInfo requestedInfo, MAPExtensionContainer extensionContainer,
            EMLPPPriority callPriority) {
        this.imsi = imsi;
        this.lmsi = lmsi;
        this.requestedInfo = requestedInfo;
        this.extensionContainer = extensionContainer;
        this.callPriority = callPriority;
    }

    @Override
    public MAPMessageType getMessageType() {
        return MAPMessageType.provideSubscriberInfo_Request;
    }

    @Override
    public int getOperationCode() {
        return MAPOperationCode.provideSubscriberInfo;
    }

    @Override
    public IMSI getImsi() {
        return imsi;
    }

    @Override
    public LMSI getLmsi() {
        return lmsi;
    }

    @Override
    public RequestedInfo getRequestedInfo() {
        return requestedInfo;
    }

    @Override
    public MAPExtensionContainer getExtensionContainer() {
        return extensionContainer;
    }

    @Override
    public EMLPPPriority getCallPriority() {
        return callPriority;
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
            MapBerSupport.decodeDispatch(_PrimitiveName, asnInputStream, length, this::_decodeBer, this::_clearFields,
                    this::_decode);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    @Override
    public void decodeData(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException {
        try {
            MapBerSupport.decodeDispatch(_PrimitiveName, asnInputStream, length, this::_decodeBer, this::_clearFields,
                    this::_decode);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    private void _clearFields() {
        this.imsi = null;
        this.lmsi = null;
        this.requestedInfo = null;
        this.extensionContainer = null;
        this.callPriority = null;
    }

    private void _decodeBer(byte[] buf, int offset, int length)
            throws AsnException, IOException, MAPParsingComponentException {
        _clearFields();
        BerCursor c = BerCursor.wrapHeap(buf, offset, length);
        while (c.hasMore()) {
            c.readTag();
            if (c.tagClass() != BerTag.CONTEXT)
                MapBerSupport.unknownTag(_PrimitiveName, c);
            switch (c.tag()) {
                case _TAG_imsi:
                    if (!c.isPrimitive())
                        throw new AsnException(_PrimitiveName + ": imsi is not primitive");
                    IMSIImpl im = new IMSIImpl();
                    MapBerSupport.decodeNested(c, im);
                    this.imsi = im;
                    break;
                case _TAG_lmsi:
                    if (!c.isPrimitive())
                        throw new AsnException(_PrimitiveName + ": lmsi is not primitive");
                    LMSIImpl lm = new LMSIImpl();
                    MapBerSupport.decodeNested(c, lm);
                    this.lmsi = lm;
                    break;
                case _TAG_requestedInfo:
                    if (c.isPrimitive())
                        throw new AsnException(_PrimitiveName + ": requestedInfo is primitive");
                    RequestedInfoImpl ri = new RequestedInfoImpl();
                    MapBerSupport.decodeNested(c, ri);
                    this.requestedInfo = ri;
                    break;
                case _TAG_extensionContainer:
                    if (c.isPrimitive())
                        throw new AsnException(_PrimitiveName + ": extensionContainer is primitive");
                    MAPExtensionContainerImpl ext = new MAPExtensionContainerImpl();
                    MapBerSupport.decodeNested(c, ext);
                    this.extensionContainer = ext;
                    break;
                case _TAG_callPriority:
                    if (!c.isPrimitive())
                        throw new AsnException(_PrimitiveName + ": callPriority is not primitive");
                    this.callPriority = EMLPPPriority.getEMLPPPriority(MapBerSupport.readInt(c));
                    break;
                default:
                    MapBerSupport.unknownTag(_PrimitiveName, c);
            }
        }
        if (this.imsi == null || this.requestedInfo == null)
            throw new AsnException(_PrimitiveName + ": missing mandatory field");
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        _clearFields();

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                switch (tag) {
                case _TAG_imsi:
                    if (!ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ".imsi: Parameter is not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.imsi = new IMSIImpl();
                    ((IMSIImpl) this.imsi).decodeAll(ais);
                    break;
                case _TAG_lmsi:
                    if (!ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ".lmsi: Parameter is not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.lmsi = new LMSIImpl();
                    ((LMSIImpl) this.lmsi).decodeAll(ais);
                    break;
                case _TAG_requestedInfo:
                    if (ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ".requestedInfo: Parameter is primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.requestedInfo = new RequestedInfoImpl();
                    ((RequestedInfoImpl) this.requestedInfo).decodeAll(ais);
                    break;
                case _TAG_extensionContainer:
                    if (ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".extensionContainer: Parameter extensionContainer is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                    this.extensionContainer = new MAPExtensionContainerImpl();
                    ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                    break;
                case _TAG_callPriority:
                    if (!ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ".callPriority: Parameter is not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    int i1 = (int)ais.readInteger();
                    this.callPriority = EMLPPPriority.getEMLPPPriority(i1);
                    break;

                default:
                    ais.advanceElement();
                    break;
                }
            } else {
                ais.advanceElement();
            }

            num++;
        }

        if (this.imsi == null)
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ": Parameter imsi is mandator but not found",
                    MAPParsingComponentExceptionReason.MistypedParameter);
        if (this.requestedInfo == null)
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ": Parameter requestedInfo is mandator but not found",
                    MAPParsingComponentExceptionReason.MistypedParameter);
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
        try {
            if (this.imsi == null)
                throw new MAPException("IMSI parameter must not be null");
            if (this.requestedInfo == null)
                throw new MAPException("requestedInfo parameter must not be null");

            ((IMSIImpl) this.imsi).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_imsi);

            if (this.lmsi != null)
                ((LMSIImpl) this.lmsi).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_lmsi);

            ((RequestedInfoImpl) this.requestedInfo).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_requestedInfo);

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_extensionContainer);

            if (this.callPriority != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_callPriority, this.callPriority.getCode());

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
        if (this.lmsi != null) {
            sb.append("lmsi=");
            sb.append(lmsi);
            sb.append(", ");
        }
        if (this.requestedInfo != null) {
            sb.append("requestedInfo=");
            sb.append(requestedInfo);
            sb.append(", ");
        }
        if (this.extensionContainer != null) {
            sb.append("extensionContainer=");
            sb.append(extensionContainer);
            sb.append(", ");
        }
        if (this.callPriority != null) {
            sb.append("callPriority=");
            sb.append(callPriority);
            sb.append(", ");
        }

        sb.append("]");

        return sb.toString();
    }

}
