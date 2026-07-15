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
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.SM_RP_DA;
import org.restcomm.protocols.ss7.map.api.service.sms.SM_RP_OA;
import org.restcomm.protocols.ss7.map.api.service.sms.SmsSignalInfo;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class ForwardShortMessageRequestImpl extends SmsMessageImpl implements ForwardShortMessageRequest {

    private SM_RP_DA sM_RP_DA;
    private SM_RP_OA sM_RP_OA;
    private SmsSignalInfoImpl sM_RP_UI;
    private boolean moreMessagesToSend;

    public ForwardShortMessageRequestImpl() {
    }

    public ForwardShortMessageRequestImpl(SM_RP_DA sM_RP_DA, SM_RP_OA sM_RP_OA, SmsSignalInfo sM_RP_UI,
            boolean moreMessagesToSend) {
        this.sM_RP_DA = sM_RP_DA;
        this.sM_RP_OA = sM_RP_OA;
        this.sM_RP_UI = (SmsSignalInfoImpl) sM_RP_UI;
        this.moreMessagesToSend = moreMessagesToSend;
    }

    public MAPMessageType getMessageType() {
        return MAPMessageType.forwardSM_Request;
    }

    public int getOperationCode() {
        return MAPOperationCode.mo_forwardSM;
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
            throw new MAPParsingComponentException("IOException when decoding forwardShortMessageRequest: " + e.getMessage(),
                    e, MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding forwardShortMessageRequest: " + e.getMessage(),
                    e, MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    public void decodeData(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException {
        try {
            this._decodeDispatch(asnInputStream, length);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding forwardShortMessageRequest: " + e.getMessage(),
                    e, MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding forwardShortMessageRequest: " + e.getMessage(),
                    e, MAPParsingComponentExceptionReason.MistypedParameter);
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
                return;
            } catch (Throwable t) {
                MapBerSupport.logFallback("ForwardShortMessageRequest", t);
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
            throw new AsnException("ForwardShortMessageRequest" + ": missing sm-RP-DA");
        c.readTag();
        if (c.tagClass() != BerTag.CONTEXT || !c.isPrimitive())
            throw new AsnException("ForwardShortMessageRequest" + ": bad sm-RP-DA tag");
        SM_RP_DAImpl da = new SM_RP_DAImpl();
        da.decodeData(MapBerSupport.taggedValueStream(c), c.valueLength());
        this.sM_RP_DA = da;
        c.skipValue();

        if (!c.hasMore())
            throw new AsnException("ForwardShortMessageRequest" + ": missing sm-RP-OA");
        c.readTag();
        if (c.tagClass() != BerTag.CONTEXT || !c.isPrimitive())
            throw new AsnException("ForwardShortMessageRequest" + ": bad sm-RP-OA tag");
        SM_RP_OAImpl oa = new SM_RP_OAImpl();
        oa.decodeData(MapBerSupport.taggedValueStream(c), c.valueLength());
        this.sM_RP_OA = oa;
        c.skipValue();

        if (!c.hasMore())
            throw new AsnException("ForwardShortMessageRequest" + ": missing sm-RP-UI");
        c.readTag();
        if (c.tagClass() != BerTag.UNIVERSAL || !c.isPrimitive() || c.tag() != BerTag.OCTET_STRING)
            throw new AsnException("ForwardShortMessageRequest" + ": bad sm-RP-UI tag");
        SmsSignalInfoImpl ui = new SmsSignalInfoImpl();
        ui.setData(c.heapBuffer(), c.valueOffset(), c.valueLength());
        this.sM_RP_UI = ui;
        c.skipValue();

        if (c.hasMore())
            throw new AsnException("ForwardShortMessageRequest" + ": optional fields present, fallback");
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.sM_RP_DA = null;
        this.sM_RP_OA = null;
        this.sM_RP_UI = null;
        this.moreMessagesToSend = false;

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
                        throw new MAPParsingComponentException(
                                "Error while decoding forwardShortMessageRequest: Parameter 0 bad tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.sM_RP_DA = new SM_RP_DAImpl();
                    ((SM_RP_DAImpl) this.sM_RP_DA).decodeAll(ais);
                    break;

                case 1:
                    // SM_RP_OA
                    if (ais.getTagClass() != Tag.CLASS_CONTEXT_SPECIFIC || !ais.isTagPrimitive())
                        throw new MAPParsingComponentException(
                                "Error while decoding forwardShortMessageRequest: Parameter 1 bad tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.sM_RP_OA = new SM_RP_OAImpl();
                    ((SM_RP_OAImpl) this.sM_RP_OA).decodeAll(ais);
                    break;

                case 2:
                    // sm-RP-UI
                    if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive())
                        throw new MAPParsingComponentException(
                                "Error while decoding forwardShortMessageRequest: Parameter 2 bad tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    if (tag != Tag.STRING_OCTET)
                        throw new MAPParsingComponentException(
                                "Error while decoding forwardShortMessageRequest: Parameter 2 tag must be STRING_OCTET, found: "
                                        + tag, MAPParsingComponentExceptionReason.MistypedParameter);
                    this.sM_RP_UI = new SmsSignalInfoImpl();
                    this.sM_RP_UI.decodeAll(ais);
                    break;

                default:
                    if (tag == Tag.NULL && ais.getTagClass() == Tag.CLASS_UNIVERSAL) {
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException(
                                    "Error while decoding forwardShortMessageRequest: Parameter moreMessagesToSend is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        ais.readNull();
                        this.moreMessagesToSend = true;
                    } else {
                        ais.advanceElement();
                    }
                    break;
            }

            num++;
        }

        if (num < 3)
            throw new MAPParsingComponentException(
                    "Error while decoding forwardShortMessageRequest: Needs at least 3 mandatory parameters, found " + num,
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
            throw new MAPException("AsnException when encoding forwardShortMessageRequest: " + e.getMessage(), e);
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
        } catch (IOException e) {
            throw new MAPException("IOException when encoding forwardShortMessageRequest: " + e.getMessage(), e);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding forwardShortMessageRequest: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ForwardShortMessageRequest [");

        if (this.getMAPDialog() != null) {
            sb.append("DialogId=").append(this.getMAPDialog().getLocalDialogId());
        }

        if (this.sM_RP_DA != null) {
            sb.append(", sm_RP_DA=");
            sb.append(this.sM_RP_DA.toString());
        }
        if (this.sM_RP_OA != null) {
            sb.append(", sm_RP_OA=");
            sb.append(this.sM_RP_OA.toString());
        }
        if (this.sM_RP_UI != null) {
            sb.append(", sm_RP_UI=[");
            sb.append(this.sM_RP_UI.toString());
            sb.append("]");
        }
        if (this.moreMessagesToSend) {
            sb.append(", moreMessagesToSend");
        }

        sb.append("]");

        return sb.toString();
    }

}
