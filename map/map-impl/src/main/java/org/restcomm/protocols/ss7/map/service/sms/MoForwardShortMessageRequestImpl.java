package org.restcomm.protocols.ss7.map.service.sms;

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
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.sms.CorrelationID;
import org.restcomm.protocols.ss7.map.api.service.sms.MoForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.SMDeliveryOutcome;
import org.restcomm.protocols.ss7.map.api.service.sms.SM_RP_DA;
import org.restcomm.protocols.ss7.map.api.service.sms.SM_RP_OA;
import org.restcomm.protocols.ss7.map.api.service.sms.SmsSignalInfo;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;

/**
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class MoForwardShortMessageRequestImpl extends SmsMessageImpl implements MoForwardShortMessageRequest {

    protected static final int _TAG_correlationID = 0;
    protected static final int _TAG_smDeliveryOutcome = 1;

    protected String _PrimitiveName = "MoForwardShortMessageRequest";

    private SM_RP_DA sm_RP_DA;
    private SM_RP_OA sm_RP_OA;
    private SmsSignalInfoImpl sm_RP_UI;
    private MAPExtensionContainer extensionContainer;
    private IMSI imsi;

    private CorrelationID correlationID;
    private SMDeliveryOutcome smDeliveryOutcome;


    public MoForwardShortMessageRequestImpl() {
    }

    public MoForwardShortMessageRequestImpl(SM_RP_DA sm_RP_DA, SM_RP_OA sm_RP_OA, SmsSignalInfo sm_RP_UI,
            MAPExtensionContainer extensionContainer, IMSI imsi, CorrelationID correlationID, SMDeliveryOutcome smDeliveryOutcome) {
        this.sm_RP_DA = sm_RP_DA;
        this.sm_RP_OA = sm_RP_OA;
        this.sm_RP_UI = (SmsSignalInfoImpl) sm_RP_UI;
        this.extensionContainer = extensionContainer;
        this.imsi = imsi;
        this.correlationID = correlationID;
        this.smDeliveryOutcome = smDeliveryOutcome;
    }

    public MAPMessageType getMessageType() {
        return MAPMessageType.moForwardSM_Request;
    }

    public int getOperationCode() {
        return MAPOperationCode.mo_forwardSM;
    }

    public SM_RP_DA getSM_RP_DA() {
        return this.sm_RP_DA;
    }

    public SM_RP_OA getSM_RP_OA() {
        return this.sm_RP_OA;
    }

    public SmsSignalInfo getSM_RP_UI() {
        return this.sm_RP_UI;
    }

    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    public IMSI getIMSI() {
        return this.imsi;
    }

    @Override
    public CorrelationID getCorrelationID() {
        return this.correlationID;
    }

    public SMDeliveryOutcome getSmDeliveryOutcome() {
        return this.smDeliveryOutcome;
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

    /**
     * Dispatch: attempt zero-copy {@link BerCursor} decode of the SMSC hot path
     * (sm-RP-DA, sm-RP-OA, sm-RP-UI TPDU with no optionals), falling back to the classic
     * {@link AsnInputStream} object-tree on any failure, when optionals are present, or
     * for the ByteBuf-backed path. The BerCursor path reads from a snapshot of the
     * backing array without consuming the stream, so fallback is always safe.
     */
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
                MapBerSupport.logFallback(_PrimitiveName, t);
                // reset any partially-set fields so the legacy decoder starts clean
                this.sm_RP_DA = null;
                this.sm_RP_OA = null;
                this.sm_RP_UI = null;
                this.extensionContainer = null;
                this.imsi = null;
                this.correlationID = null;
                this.smDeliveryOutcome = null;
            }
        }
        this._decode(asnInputStream, length);
    }

    /**
     * Zero-copy decode of the common MO-ForwardSM arg: sm-RP-DA [CHOICE], sm-RP-OA
     * [CHOICE] and the sm-RP-UI TPDU (OCTET STRING, the large field — held as a view).
     * Throws (→ fallback) if any optional (extensionContainer / imsi / correlationID /
     * smDeliveryOutcome) is present, so those are handled by the classic decoder.
     * <p>
     * The small CHOICE address fields (a handful of octets) are copied and delegated to
     * their existing {@code decodeData} decoders — negligible per docs/ZEROCOPY_DECODE_DESIGN.md;
     * only the big TPDU is a zero-copy view.
     */
    private void _decodeBer(byte[] buf, int offset, int length)
            throws AsnException, IOException, MAPParsingComponentException {
        BerCursor c = BerCursor.wrapHeap(buf, offset, length);

        // sm-RP-DA : [CONTEXT] primitive CHOICE
        if (!c.hasMore())
            throw new AsnException(_PrimitiveName + ": missing sm-RP-DA");
        c.readTag();
        if (c.tagClass() != BerTag.CONTEXT || !c.isPrimitive())
            throw new AsnException(_PrimitiveName + ": bad sm-RP-DA tag");
        this.sm_RP_DA = new SM_RP_DAImpl();
        ((SM_RP_DAImpl) this.sm_RP_DA).decodeData(taggedValueStream(c), c.valueLength());
        c.skipValue();

        // sm-RP-OA : [CONTEXT] primitive CHOICE
        if (!c.hasMore())
            throw new AsnException(_PrimitiveName + ": missing sm-RP-OA");
        c.readTag();
        if (c.tagClass() != BerTag.CONTEXT || !c.isPrimitive())
            throw new AsnException(_PrimitiveName + ": bad sm-RP-OA tag");
        this.sm_RP_OA = new SM_RP_OAImpl();
        ((SM_RP_OAImpl) this.sm_RP_OA).decodeData(taggedValueStream(c), c.valueLength());
        c.skipValue();

        // sm-RP-UI : UNIVERSAL OCTET STRING — the large TPDU, held as a zero-copy view
        if (!c.hasMore())
            throw new AsnException(_PrimitiveName + ": missing sm-RP-UI");
        c.readTag();
        if (c.tagClass() != BerTag.UNIVERSAL || !c.isPrimitive() || c.tag() != BerTag.OCTET_STRING)
            throw new AsnException(_PrimitiveName + ": bad sm-RP-UI tag");
        SmsSignalInfoImpl ui = new SmsSignalInfoImpl();
        ui.setData(c.heapBuffer(), c.valueOffset(), c.valueLength());
        this.sm_RP_UI = ui;
        c.skipValue();

        if (c.hasMore()) // optional extensionContainer / imsi / correlationID / smDeliveryOutcome
            throw new AsnException(_PrimitiveName + ": optional fields present, fallback");
    }

    /**
     * Builds a tagged {@link AsnInputStream} over just the VALUE bytes of the current
     * TLV, carrying its tag metadata, so a CHOICE sub-decoder's {@code decodeData} sees
     * the same tag class/number/primitive flag it would from the classic path. The value
     * bytes are copied (small — a few octets for the address fields).
     */
    private static AsnInputStream taggedValueStream(BerCursor c) {
        byte[] v = java.util.Arrays.copyOfRange(c.heapBuffer(), c.valueOffset(), c.valueOffset() + c.valueLength());
        return new AsnInputStream(v, c.tagClass(), c.isPrimitive(), c.tag());
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.sm_RP_DA = null;
        this.sm_RP_OA = null;
        this.sm_RP_UI = null;
        this.extensionContainer = null;
        this.imsi = null;
        this.correlationID = null;
        this.smDeliveryOutcome = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            switch (num) {
                case 0:
                    // sm-RP-DA   SM-RP-DA
                    if (ais.getTagClass() != Tag.CLASS_CONTEXT_SPECIFIC || !ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ": Parameter 0 bad tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.sm_RP_DA = new SM_RP_DAImpl();
                    ((SM_RP_DAImpl) this.sm_RP_DA).decodeAll(ais);
                    break;

                case 1:
                    // sm-RP-OA   SM_RP_OA
                    if (ais.getTagClass() != Tag.CLASS_CONTEXT_SPECIFIC || !ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ": Parameter 1 bad tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.sm_RP_OA = new SM_RP_OAImpl();
                    ((SM_RP_OAImpl) this.sm_RP_OA).decodeAll(ais);
                    break;

                case 2:
                    // sm-RP-UI   SignalInfo
                    if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ": Parameter 2 bad tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    if (tag != Tag.STRING_OCTET)
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ": Parameter 2 tag must be STRING_OCTET, found: " + tag,
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.sm_RP_UI = new SmsSignalInfoImpl();
                    this.sm_RP_UI.decodeAll(ais);
                    break;

                default:
                    if (tag == Tag.SEQUENCE && ais.getTagClass() == Tag.CLASS_UNIVERSAL) {
                        // extensionContainer   ExtensionContainer   OPTIONAL
                        if (ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter extensionContainer is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.extensionContainer = new MAPExtensionContainerImpl();
                        ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                    } else if (tag == Tag.STRING_OCTET && ais.getTagClass() == Tag.CLASS_UNIVERSAL) {
                        // imsi   IMSI   OPTIONAL
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter imsi is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                        this.imsi = new IMSIImpl();
                        ((IMSIImpl) this.imsi).decodeAll(ais);
                    } else if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                        if (tag == _TAG_correlationID) {
                            // correlationID   [0] CorrelationID   OPTIONAL
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".correlationID: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.correlationID = new CorrelationIDImpl();
                            ((CorrelationIDImpl) this.correlationID).decodeAll(ais);
                        } else if (tag == _TAG_smDeliveryOutcome) {
                            // sm-DeliveryOutcome  [1] SM-DeliveryOutcome  OPTIONAL
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".sMDeliveryOutcome: Parameter bad tag or tag class or not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            int i1 = (int) ais.readInteger();
                            this.smDeliveryOutcome = SMDeliveryOutcome.getInstance(i1);
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

        try {

            if (this.sm_RP_DA == null || this.sm_RP_OA == null || this.sm_RP_UI == null)
                throw new MAPException("sm_RP_DA,sm_RP_OA and sm_RP_UI must not be null");

            ((SM_RP_DAImpl) this.sm_RP_DA).encodeAll(asnOutputStream);
            ((SM_RP_OAImpl) this.sm_RP_OA).encodeAll(asnOutputStream);
            this.sm_RP_UI.encodeAll(asnOutputStream);

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream);

            if (this.imsi != null)
                ((IMSIImpl) this.imsi).encodeAll(asnOutputStream);

            if (this.correlationID != null)
                ((CorrelationIDImpl) this.correlationID).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_correlationID);

            if (this.smDeliveryOutcome != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_smDeliveryOutcome, this.smDeliveryOutcome.getCode());

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

        if (this.sm_RP_DA != null) {
            sb.append(", sm_RP_DA=");
            sb.append(this.sm_RP_DA);
        }
        if (this.sm_RP_OA != null) {
            sb.append(", sm_RP_OA=");
            sb.append(this.sm_RP_OA);
        }
        if (this.sm_RP_UI != null) {
            sb.append(", sm_RP_UI=[");
            sb.append(this.sm_RP_UI);
            sb.append("]");
        }
        if (this.extensionContainer != null) {
            sb.append(", extensionContainer=");
            sb.append(this.extensionContainer);
        }
        if (this.imsi != null) {
            sb.append(", imsi=");
            sb.append(this.imsi);
        }
        if (this.correlationID != null) {
            sb.append(", correlationID=");
            sb.append(this.correlationID);
        }
        if (this.smDeliveryOutcome != null) {
            sb.append(", smDeliveryOutcome=");
            sb.append(this.smDeliveryOutcome);
        }

        sb.append("]");

        return sb.toString();
    }
}
