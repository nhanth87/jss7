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
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.sms.CorrelationID;
import org.restcomm.protocols.ss7.map.api.service.sms.ReportSMDeliveryStatusRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.SMDeliveryOutcome;
import org.restcomm.protocols.ss7.map.primitives.AddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;

/**
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ReportSMDeliveryStatusRequestImpl extends SmsMessageImpl implements ReportSMDeliveryStatusRequest {

    protected static final int _TAG_AbsentSubscriberDiagnosticSM = 0;
    protected static final int _TAG_ExtensionContainer = 1;
    protected static final int _TAG_GprsSupportIndicator = 2;
    protected static final int _TAG_DeliveryOutcomeIndicator = 3;
    protected static final int _TAG_AdditionalSMDeliveryOutcome = 4;
    protected static final int _TAG_AdditionalAbsentSubscriberDiagnosticSM = 5;
    protected static final int _TAG_IP_SM_GW_Indicator = 6;
    protected static final int _TAG_IP_SM_GW_DeliveryOutcome = 7;
    protected static final int _TAG_IP_SM_GW_AbsentSubscriberDiagnosticSM = 8;
    protected static final int _TAG_IMSI = 9;
    protected static final int _TAG_SingleAttemptDelivery = 10;
    protected static final int _TAG_correlationID = 11;
    protected static final int _TAG_SMSF_3GPP_DeliveryOutcomeIndicator = 12;
    protected static final int _TAG_SMSF_3GPP_DeliveryOutcome = 13;
    protected static final int _TAG_SMSF_3GPP_AbsentSubscriberDiagnosticSM = 14;
    protected static final int _TAG_SMSF_NON_3GPP_DeliveryOutcomeIndicator = 15;
    protected static final int _TAG_SMSF_NON_3GPP_DeliveryOutcome = 16;
    protected static final int _TAG_SMSF_NON_3GPP_AbsentSubscriberDiagnosticSM = 17;

    protected String _PrimitiveName = "ReportSMDeliveryStatusRequest";

    private ISDNAddressString msisdn;
    private AddressString serviceCentreAddress;
    private SMDeliveryOutcome sMDeliveryOutcome;
    private Integer absentSubscriberDiagnosticSM;
    private MAPExtensionContainer extensionContainer;
    private boolean gprsSupportIndicator;
    private boolean deliveryOutcomeIndicator;
    private SMDeliveryOutcome additionalSMDeliveryOutcome;
    private Integer additionalAbsentSubscriberDiagnosticSM;
    private boolean ipSmGwIndicator;
    private SMDeliveryOutcome ipSmGwSMDeliveryOutcome;
    private Integer ipSmGwAbsentSubscriberDiagnosticSM;
    private IMSI imsi;
    private boolean singleAttemptDelivery;
    private CorrelationID correlationID;
    private boolean smsf3gppDeliveryOutcomeIndicator;
    private SMDeliveryOutcome smsf3gppDeliveryOutcome;
    private Integer smsf3gppAbsentSubscriberDiagnosticSM;
    private boolean smsfNon3gppDeliveryOutcomeIndicator;
    private SMDeliveryOutcome smsfNon3gppDeliveryOutcome;
    private Integer smsfNon3gppAbsentSubscriberDiagnosticSM;
    private long mapProtocolVersion;

    public ReportSMDeliveryStatusRequestImpl(long mapProtocolVersion) {
        this.mapProtocolVersion = mapProtocolVersion;
    }

    public ReportSMDeliveryStatusRequestImpl(long mapProtocolVersion, ISDNAddressString msisdn,
            AddressString serviceCentreAddress, SMDeliveryOutcome sMDeliveryOutcome, Integer absentSubscriberDiagnosticSM,
            MAPExtensionContainer extensionContainer, boolean gprsSupportIndicator, boolean deliveryOutcomeIndicator,
            SMDeliveryOutcome additionalSMDeliveryOutcome, Integer additionalAbsentSubscriberDiagnosticSM,
            boolean ipSmGwIndicator, SMDeliveryOutcome ipSmGwSMDeliveryOutcome, Integer ipSmGwAbsentSubscriberDiagnosticSM,
            IMSI imsi, boolean singleAttemptDelivery, CorrelationID correlationID, boolean smsf3gppDeliveryOutcomeIndicator,
            SMDeliveryOutcome smsf3gppDeliveryOutcome, Integer smsf3gppAbsentSubscriberDiagnosticSM, boolean smsfNon3gppDeliveryOutcomeIndicator,
            SMDeliveryOutcome smsfNon3gppDeliveryOutcome, Integer smsfNon3gppAbsentSubscriberDiagnosticSM) {
        this.mapProtocolVersion = mapProtocolVersion;
        this.msisdn = msisdn;
        this.serviceCentreAddress = serviceCentreAddress;
        this.sMDeliveryOutcome = sMDeliveryOutcome;
        this.absentSubscriberDiagnosticSM = absentSubscriberDiagnosticSM;
        this.extensionContainer = extensionContainer;
        this.gprsSupportIndicator = gprsSupportIndicator;
        this.deliveryOutcomeIndicator = deliveryOutcomeIndicator;
        this.additionalSMDeliveryOutcome = additionalSMDeliveryOutcome;
        this.additionalAbsentSubscriberDiagnosticSM = additionalAbsentSubscriberDiagnosticSM;
        this.ipSmGwIndicator = ipSmGwIndicator;
        this.ipSmGwSMDeliveryOutcome = ipSmGwSMDeliveryOutcome;
        this.ipSmGwAbsentSubscriberDiagnosticSM = ipSmGwAbsentSubscriberDiagnosticSM;
        this.imsi = imsi;
        this.singleAttemptDelivery = singleAttemptDelivery;
        this.correlationID = correlationID;
        this.smsf3gppDeliveryOutcomeIndicator = smsf3gppDeliveryOutcomeIndicator;
        this.smsf3gppDeliveryOutcome = smsf3gppDeliveryOutcome;
        this.smsf3gppAbsentSubscriberDiagnosticSM = smsf3gppAbsentSubscriberDiagnosticSM;
        this.smsfNon3gppDeliveryOutcomeIndicator = smsfNon3gppDeliveryOutcomeIndicator;
        this.smsfNon3gppDeliveryOutcome = smsfNon3gppDeliveryOutcome;
        this.smsfNon3gppAbsentSubscriberDiagnosticSM = smsfNon3gppAbsentSubscriberDiagnosticSM;
    }

    public MAPMessageType getMessageType() {
        return MAPMessageType.reportSM_DeliveryStatus_Request;
    }

    public int getOperationCode() {
        return MAPOperationCode.reportSM_DeliveryStatus;
    }

    @Override
    public ISDNAddressString getMsisdn() {
        return this.msisdn;
    }

    @Override
    public AddressString getServiceCentreAddress() {
        return this.serviceCentreAddress;
    }

    @Override
    public SMDeliveryOutcome getSMDeliveryOutcome() {
        return this.sMDeliveryOutcome;
    }

    @Override
    public Integer getAbsentSubscriberDiagnosticSM() {
        return this.absentSubscriberDiagnosticSM;
    }

    @Override
    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    @Override
    public boolean getGprsSupportIndicator() {
        return this.gprsSupportIndicator;
    }

    @Override
    public boolean getDeliveryOutcomeIndicator() {
        return this.deliveryOutcomeIndicator;
    }

    @Override
    public SMDeliveryOutcome getAdditionalSMDeliveryOutcome() {
        return this.additionalSMDeliveryOutcome;
    }

    @Override
    public Integer getAdditionalAbsentSubscriberDiagnosticSM() {
        return this.additionalAbsentSubscriberDiagnosticSM;
    }

    @Override
    public boolean getIpSmGwIndicator() {
        return this.ipSmGwIndicator;
    }

    @Override
    public SMDeliveryOutcome getIpSmGwSMDeliveryOutcome() {
        return this.ipSmGwSMDeliveryOutcome;
    }

    @Override
    public Integer getIpSmGwAbsentSubscriberDiagnosticSM() {
        return this.ipSmGwAbsentSubscriberDiagnosticSM;
    }

    @Override
    public IMSI getImsi() {
        return this.imsi;
    }

    @Override
    public boolean getSingleAttemptDelivery() {
        return this.singleAttemptDelivery;
    }

    @Override
    public CorrelationID getCorrelationID() {
        return this.correlationID;
    }

    public boolean getSmsf3gppDeliveryOutcomeIndicator() {
        return this.smsf3gppDeliveryOutcomeIndicator;
    }

    @Override
    public SMDeliveryOutcome getSmsf3gppDeliveryOutcome() {
        return this.smsf3gppDeliveryOutcome;
    }

    @Override
    public Integer getSmsf3gppAbsentSubscriberDiagnosticSM() {
        return this.smsf3gppAbsentSubscriberDiagnosticSM;
    }

    public boolean getSmsfNon3gppDeliveryOutcomeIndicator() {
        return this.smsfNon3gppDeliveryOutcomeIndicator;
    }

    @Override
    public SMDeliveryOutcome getSmsfNon3gppDeliveryOutcome() {
        return this.smsfNon3gppDeliveryOutcome;
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
            try {
                this._decodeBer(asnInputStream.getBuffer(), MapBerSupport.absOffset(asnInputStream), length);
                asnInputStream.advance(length);
                return;
            } catch (Throwable t) {
                MapBerSupport.logFallback("ReportSMDeliveryStatusRequest", t);
                this.msisdn = null; this.serviceCentreAddress = null; this.sMDeliveryOutcome = null;
            }
        }
        this._decode(asnInputStream, length);
    }

    private void _decodeBer(byte[] buf, int offset, int length)
            throws AsnException, IOException, MAPParsingComponentException {
        BerCursor c = BerCursor.wrapHeap(buf, offset, length);
        if (!c.hasMore()) throw new AsnException(_PrimitiveName + ": missing msisdn");
        c.readTag();
        if (c.tagClass() != BerTag.UNIVERSAL || !c.isPrimitive() || c.tag() != BerTag.OCTET_STRING)
            throw new AsnException(_PrimitiveName + ": bad msisdn");
        ISDNAddressStringImpl m = new ISDNAddressStringImpl();
        m.decodeData(MapBerSupport.taggedValueStream(c), c.valueLength());
        this.msisdn = m; c.skipValue();

        if (!c.hasMore()) throw new AsnException(_PrimitiveName + ": missing serviceCentreAddress");
        c.readTag();
        if (c.tagClass() != BerTag.UNIVERSAL || !c.isPrimitive() || c.tag() != BerTag.OCTET_STRING)
            throw new AsnException(_PrimitiveName + ": bad serviceCentreAddress");
        ISDNAddressStringImpl sca = new ISDNAddressStringImpl();
        ((AddressStringImpl) sca).decodeData(MapBerSupport.taggedValueStream(c), c.valueLength());
        this.serviceCentreAddress = sca; c.skipValue();

        if (!c.hasMore()) throw new AsnException(_PrimitiveName + ": missing sMDeliveryOutcome");
        c.readTag();
        if (c.tagClass() != BerTag.UNIVERSAL || !c.isPrimitive() || c.tag() != 0x0A /* ENUMERATED */)
            throw new AsnException(_PrimitiveName + ": bad sMDeliveryOutcome");
        this.sMDeliveryOutcome = SMDeliveryOutcome.getInstance(c.readInt32());

        if (c.hasMore()) throw new AsnException(_PrimitiveName + ": optional fields present, fallback");
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.msisdn = null;
        this.serviceCentreAddress = null;
        this.sMDeliveryOutcome = null;
        this.absentSubscriberDiagnosticSM = null;
        this.extensionContainer = null;
        this.gprsSupportIndicator = false;
        this.deliveryOutcomeIndicator = false;
        this.additionalSMDeliveryOutcome = null;
        this.additionalAbsentSubscriberDiagnosticSM = null;
        this.ipSmGwIndicator = false;
        this.ipSmGwSMDeliveryOutcome = null;
        this.ipSmGwAbsentSubscriberDiagnosticSM = null;
        this.imsi = null;
        this.singleAttemptDelivery = false;
        this.correlationID = null;
        this.smsf3gppDeliveryOutcomeIndicator = false;
        this.smsf3gppDeliveryOutcome = null;
        this.smsf3gppAbsentSubscriberDiagnosticSM = null;
        this.smsfNon3gppDeliveryOutcomeIndicator = false;
        this.smsfNon3gppDeliveryOutcome = null;
        this.smsfNon3gppAbsentSubscriberDiagnosticSM = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            switch (num) {
                case 0:
                    // msisdn
                    if (tag != Tag.STRING_OCTET || ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".msisdn: Parameter bad tag or tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.msisdn = new ISDNAddressStringImpl();
                    ((ISDNAddressStringImpl) this.msisdn).decodeAll(ais);
                    break;

                case 1:
                    // serviceCentreAddress
                    if (tag != Tag.STRING_OCTET || ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".serviceCentreAddress: Parameter bad tag or tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.serviceCentreAddress = new AddressStringImpl();
                    ((AddressStringImpl) this.serviceCentreAddress).decodeAll(ais);
                    break;

                case 2:
                    // sMDeliveryOutcome
                    if (tag != Tag.ENUMERATED || ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".sMDeliveryOutcome: Parameter bad tag or tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    int i1 = (int) ais.readInteger();
                    this.sMDeliveryOutcome = SMDeliveryOutcome.getInstance(i1);
                    break;

                default:
                    if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {

                        switch (tag) {
                            case _TAG_AbsentSubscriberDiagnosticSM:
                                // absentSubscriberDiagnosticSM
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".absentSubscriberDiagnosticSM: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.absentSubscriberDiagnosticSM = (int) ais.readInteger();
                                break;

                            case _TAG_ExtensionContainer:
                                // extensionContainer
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".extensionContainer: Parameter is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.extensionContainer = new MAPExtensionContainerImpl();
                                ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                                break;

                            case _TAG_GprsSupportIndicator:
                                // gprsSupportIndicator
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".gprsSupportIndicator: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.gprsSupportIndicator = true;
                                break;

                            case _TAG_DeliveryOutcomeIndicator:
                                // deliveryOutcomeIndicator
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".deliveryOutcomeIndicator: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.deliveryOutcomeIndicator = true;
                                break;

                            case _TAG_AdditionalSMDeliveryOutcome:
                                // additionalSMDeliveryOutcome
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".additionalSMDeliveryOutcome: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                i1 = (int) ais.readInteger();
                                this.additionalSMDeliveryOutcome = SMDeliveryOutcome.getInstance(i1);
                                break;

                            case _TAG_AdditionalAbsentSubscriberDiagnosticSM:
                                // additionalAbsentSubscriberDiagnosticSM
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".additionalAbsentSubscriberDiagnosticSM: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.additionalAbsentSubscriberDiagnosticSM = (int) ais.readInteger();
                                break;

                            case _TAG_IP_SM_GW_Indicator:
                                // ip-sm-gw-Indicator
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".ipSmGwIndicator: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.ipSmGwIndicator = true;
                                break;

                            case _TAG_IP_SM_GW_DeliveryOutcome:
                                // ip-sm-gw-sm-deliveryOutcome
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".ipSmGwSMDeliveryOutcome: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                i1 = (int) ais.readInteger();
                                this.ipSmGwSMDeliveryOutcome = SMDeliveryOutcome.getInstance(i1);
                                break;

                            case _TAG_IP_SM_GW_AbsentSubscriberDiagnosticSM:
                                // ip-sm-gw-absentSubscriberDiagnosticSM
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".ipSmGwAbsentSubscriberDiagnosticSM: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.ipSmGwAbsentSubscriberDiagnosticSM = (int) ais.readInteger();
                                break;

                            case _TAG_IMSI:
                                // imsi
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".imsi: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.imsi = new IMSIImpl();
                                ((IMSIImpl) this.imsi).decodeAll(ais);
                                break;

                            case _TAG_SingleAttemptDelivery:
                                // singleAttemptDelivery
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".singleAttemptDelivery: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.singleAttemptDelivery = true;
                                break;

                            case _TAG_correlationID:
                                // correlationID
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".correlationID: Parameter is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.correlationID = new CorrelationIDImpl();
                                ((CorrelationIDImpl) this.correlationID).decodeAll(ais);
                                break;

                            case _TAG_SMSF_3GPP_DeliveryOutcomeIndicator:
                                // smsf-3gpp-deliveryOutcomeIndicator
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".smsf3gppDeliveryOutcomeIndicator: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.smsf3gppDeliveryOutcomeIndicator = true;
                                break;

                            case _TAG_SMSF_3GPP_DeliveryOutcome:
                                // smsf-3gpp-deliveryOutcome
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".smsf3gppDeliveryOutcome: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                i1 = (int) ais.readInteger();
                                this.smsf3gppDeliveryOutcome = SMDeliveryOutcome.getInstance(i1);
                                break;

                            case _TAG_SMSF_3GPP_AbsentSubscriberDiagnosticSM:
                                // smsf-3gpp-absentSubscriberDiagSM
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".smsf3gppAbsentSubscriberDiagnosticSM: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.smsf3gppAbsentSubscriberDiagnosticSM = (int) ais.readInteger();
                                break;

                            case _TAG_SMSF_NON_3GPP_DeliveryOutcomeIndicator:
                                // smsf-non-3gpp-deliveryOutcomeIndicator
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".smsfNon3gppDeliveryOutcomeIndicator: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                ais.readNull();
                                this.smsfNon3gppDeliveryOutcomeIndicator = true;
                                break;

                            case _TAG_SMSF_NON_3GPP_DeliveryOutcome:
                                // smsf-non-3gpp-deliveryOutcome
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".smsfNon3gppDeliveryOutcome: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                i1 = (int) ais.readInteger();
                                this.smsfNon3gppDeliveryOutcome = SMDeliveryOutcome.getInstance(i1);
                                break;

                            case _TAG_SMSF_NON_3GPP_AbsentSubscriberDiagnosticSM:
                                // smsf-non-3gpp-absentSubscriberDiagSM
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".smsfNon3gppAbsentSubscriberDiagnosticSM: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.smsfNon3gppAbsentSubscriberDiagnosticSM = (int) ais.readInteger();
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

        if (num < 3 && this.mapProtocolVersion >= 2 || num < 2 && this.mapProtocolVersion == 1)
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
        if (this.mapProtocolVersion == 1) {
            if (this.msisdn == null || this.serviceCentreAddress == null)
                throw new MAPException("msisdn and serviceCentreAddress must not be null");
        } else {
            if (this.msisdn == null || this.serviceCentreAddress == null || this.sMDeliveryOutcome == null)
                throw new MAPException("msisdn, serviceCentreAddress and sMDeliveryOutcome must not be null");
        }

        try {

            ((ISDNAddressStringImpl) this.msisdn).encodeAll(asnOutputStream);

            ((AddressStringImpl) this.serviceCentreAddress).encodeAll(asnOutputStream);

            if (this.mapProtocolVersion > 1)
                asnOutputStream.writeInteger(Tag.CLASS_UNIVERSAL, Tag.ENUMERATED, this.sMDeliveryOutcome.getCode());

            if (this.absentSubscriberDiagnosticSM != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_AbsentSubscriberDiagnosticSM,
                        this.absentSubscriberDiagnosticSM);

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_ExtensionContainer);

            if (this.gprsSupportIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_GprsSupportIndicator);

            if (this.deliveryOutcomeIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_DeliveryOutcomeIndicator);

            if (this.additionalSMDeliveryOutcome != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_AdditionalSMDeliveryOutcome,
                        this.additionalSMDeliveryOutcome.getCode());

            if (this.additionalAbsentSubscriberDiagnosticSM != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_AdditionalAbsentSubscriberDiagnosticSM,
                        this.additionalAbsentSubscriberDiagnosticSM);

            if (this.ipSmGwIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_IP_SM_GW_Indicator);

            if (this.ipSmGwSMDeliveryOutcome != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_IP_SM_GW_DeliveryOutcome,
                        this.ipSmGwSMDeliveryOutcome.getCode());

            if (this.ipSmGwAbsentSubscriberDiagnosticSM != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_IP_SM_GW_AbsentSubscriberDiagnosticSM,
                        this.ipSmGwAbsentSubscriberDiagnosticSM);

            if (this.imsi != null)
                ((IMSIImpl) this.imsi).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_IMSI);

            if (this.singleAttemptDelivery)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SingleAttemptDelivery);

            if (this.correlationID != null)
                ((CorrelationIDImpl) this.correlationID).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_correlationID);

            if (this.smsf3gppDeliveryOutcomeIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SMSF_3GPP_DeliveryOutcomeIndicator);

            if (this.smsf3gppDeliveryOutcome != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SMSF_3GPP_DeliveryOutcome,
                        this.smsf3gppDeliveryOutcome.getCode());

            if (this.smsf3gppAbsentSubscriberDiagnosticSM != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SMSF_3GPP_AbsentSubscriberDiagnosticSM,
                        this.smsf3gppAbsentSubscriberDiagnosticSM);

            if (this.smsfNon3gppDeliveryOutcomeIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SMSF_NON_3GPP_DeliveryOutcomeIndicator);

            if (this.smsfNon3gppDeliveryOutcome != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SMSF_NON_3GPP_DeliveryOutcome,
                        this.smsfNon3gppDeliveryOutcome.getCode());

            if (this.smsfNon3gppAbsentSubscriberDiagnosticSM != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SMSF_NON_3GPP_AbsentSubscriberDiagnosticSM,
                        this.smsfNon3gppAbsentSubscriberDiagnosticSM);

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

        if (this.msisdn != null) {
            sb.append(", msisdn=");
            sb.append(this.msisdn);
        }
        if (this.serviceCentreAddress != null) {
            sb.append(", serviceCentreAddress=");
            sb.append(this.serviceCentreAddress);
        }
        if (this.sMDeliveryOutcome != null) {
            sb.append(", sMDeliveryOutcome=");
            sb.append(this.sMDeliveryOutcome);
        }
        if (this.absentSubscriberDiagnosticSM != null) {
            sb.append(", absentSubscriberDiagnosticSM=");
            sb.append(this.absentSubscriberDiagnosticSM);
        }
        if (this.extensionContainer != null) {
            sb.append(", extensionContainer=");
            sb.append(this.extensionContainer);
        }
        if (this.gprsSupportIndicator) {
            sb.append(", gprsSupportIndicator");
        }
        if (this.deliveryOutcomeIndicator) {
            sb.append(", deliveryOutcomeIndicator");
        }
        if (this.additionalSMDeliveryOutcome != null) {
            sb.append(", additionalSMDeliveryOutcome=");
            sb.append(this.additionalSMDeliveryOutcome);
        }
        if (this.additionalAbsentSubscriberDiagnosticSM != null) {
            sb.append(", additionalAbsentSubscriberDiagnosticSM=");
            sb.append(this.additionalAbsentSubscriberDiagnosticSM);
        }
        if (this.ipSmGwIndicator) {
            sb.append(", ipSmGwIndicator");
        }
        if (this.ipSmGwSMDeliveryOutcome != null) {
            sb.append(", ipSmGwSMDeliveryOutcome=");
            sb.append(this.ipSmGwSMDeliveryOutcome);
        }
        if (this.ipSmGwAbsentSubscriberDiagnosticSM != null) {
            sb.append(", ipSmGwAbsentSubscriberDiagnosticSM=");
            sb.append(this.ipSmGwAbsentSubscriberDiagnosticSM);
        }
        if (this.imsi != null) {
            sb.append(", imsi=");
            sb.append(this.imsi);
        }
        if (this.singleAttemptDelivery) {
            sb.append(", singleAttemptDelivery");
        }
        if (this.correlationID != null) {
            sb.append(", correlationID=");
            sb.append(this.correlationID);
        }
        if (this.smsf3gppDeliveryOutcomeIndicator) {
            sb.append(", smsf3gppDeliveryOutcomeIndicator");
        }
        if (this.smsf3gppDeliveryOutcome != null) {
            sb.append(", smsf3gppDeliveryOutcome=");
            sb.append(this.smsf3gppDeliveryOutcome);
        }
        if (this.smsf3gppAbsentSubscriberDiagnosticSM != null) {
            sb.append(", smsf3gppAbsentSubscriberDiagnosticSM=");
            sb.append(this.smsf3gppAbsentSubscriberDiagnosticSM);
        }
        if (this.smsfNon3gppDeliveryOutcomeIndicator) {
            sb.append(", smsfNon3gppDeliveryOutcomeIndicator");
        }
        if (this.smsfNon3gppDeliveryOutcome != null) {
            sb.append(", smsfNon3gppDeliveryOutcome=");
            sb.append(this.smsfNon3gppDeliveryOutcome);
        }
        if (this.smsfNon3gppAbsentSubscriberDiagnosticSM != null) {
            sb.append(", smsfNon3gppAbsentSubscriberDiagnosticSM=");
            sb.append(this.smsfNon3gppAbsentSubscriberDiagnosticSM);
        }

        sb.append("]");

        return sb.toString();
    }
}
