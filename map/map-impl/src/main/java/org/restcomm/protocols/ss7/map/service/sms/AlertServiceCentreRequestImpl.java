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
import org.restcomm.protocols.ss7.map.api.primitives.Time;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.NetworkNodeDiameterAddress;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.CorrelationID;
import org.restcomm.protocols.ss7.map.api.service.sms.SmsGmscAlertEvent;
import org.restcomm.protocols.ss7.map.primitives.AddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.TimeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.NetworkNodeDiameterAddressImpl;

/**
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class AlertServiceCentreRequestImpl extends SmsMessageImpl implements AlertServiceCentreRequest {

    public static final String _PrimitiveName = "AlertServiceCentreRequest";

    protected static final int _TAG_maximumUeAvailabilityTime = 0;
    protected static final int _TAG_smsGmscAlertEvent = 1;
    protected static final int _TAG_smsGmscDiameterAddress = 2;
    protected static final int _TAG_newSGSNNumber = 3;
    protected static final int _TAG_newSGSNDiameterAddress = 4;
    protected static final int _TAG_newMMENumber = 5;
    protected static final int _TAG_newMMEDiameterAddress = 6;
    protected static final int _TAG_newMSCNumber = 7;

    private ISDNAddressString msisdn;
    private AddressString serviceCentreAddress;
    private IMSI imsi;
    private CorrelationID correlationID;
    private Time maximumUeAvailabilityTime;
    private SmsGmscAlertEvent smsGmscAlertEvent;
    private NetworkNodeDiameterAddress smsGmscDiameterAddress;
    private ISDNAddressString newSGSNNumber;
    private NetworkNodeDiameterAddress newSGSNDiameterAddress;
    private ISDNAddressString newMMENumber;
    private NetworkNodeDiameterAddress newMMEDiameterAddress;
    private ISDNAddressString newMSCNumber;
    private int operationCode;

    public AlertServiceCentreRequestImpl(int operationCode) {
        this.operationCode = operationCode;
    }

    public AlertServiceCentreRequestImpl(ISDNAddressString msisdn, AddressString serviceCentreAddress, IMSI imsi,
            CorrelationID correlationID, Time maximumUeAvailabilityTime, SmsGmscAlertEvent smsGmscAlertEvent,
            NetworkNodeDiameterAddress smsGmscDiameterAddress, ISDNAddressString newSGSNNumber,
            NetworkNodeDiameterAddress newSGSNDiameterAddress, ISDNAddressString newMMENumber,
            NetworkNodeDiameterAddress newMMEDiameterAddress, ISDNAddressString newMSCNumber) {
        this.msisdn = msisdn;
        this.serviceCentreAddress = serviceCentreAddress;
        this.imsi = imsi;
        this.correlationID = correlationID;
        this.maximumUeAvailabilityTime = maximumUeAvailabilityTime;
        this.smsGmscAlertEvent = smsGmscAlertEvent;
        this.smsGmscDiameterAddress = smsGmscDiameterAddress;
        this.newSGSNNumber = newSGSNNumber;
        this.newSGSNDiameterAddress = newSGSNDiameterAddress;
        this.newMMENumber = newMMENumber;
        this.newMMEDiameterAddress = newMMEDiameterAddress;
        this.newMSCNumber = newMSCNumber;
    }

    public MAPMessageType getMessageType() {
        if (this.operationCode == MAPOperationCode.alertServiceCentre)
            return MAPMessageType.alertServiceCentre_Request;
        else
            return MAPMessageType.alertServiceCentreWithoutResult_Request;
    }

    public int getOperationCode() {
        return this.operationCode;
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
    public IMSI getImsi() {
        return this.imsi;
    }

    @Override
    public CorrelationID getCorrelationID() {
        return this.correlationID;
    }

    @Override
    public Time getMaximumUeAvailabilityTime() {
        return this.maximumUeAvailabilityTime;
    }

    @Override
    public SmsGmscAlertEvent getSmsGmscAlertEvent() {
        return this.smsGmscAlertEvent;
    }

    @Override
    public NetworkNodeDiameterAddress getSmsGmscDiameterAddress() {
        return this.smsGmscDiameterAddress;
    }

    @Override
    public ISDNAddressString getNewSGSNNumber() {
        return this.newSGSNNumber;
    }

    @Override
    public NetworkNodeDiameterAddress getNewSGSNDiameterAddress() {
        return this.newSGSNDiameterAddress;
    }

    @Override
    public ISDNAddressString getNewMMENumber() {
        return this.newMMENumber;
    }

    @Override
    public NetworkNodeDiameterAddress getNewMMEDiameterAddress() {
        return this.newMMEDiameterAddress;
    }

    @Override
    public ISDNAddressString getNewMSCNumber() {
        return this.newMSCNumber;
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
            throw new MAPParsingComponentException("IOException when decoding AlertServiceCentreRequest: " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding AlertServiceCentreRequest: " + e.getMessage(),
                    e, MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    public void decodeData(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException {
        try {
            this._decodeDispatch(asnInputStream, length);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding AlertServiceCentreRequest: " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding AlertServiceCentreRequest: " + e.getMessage(),
                    e, MAPParsingComponentExceptionReason.MistypedParameter);
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
                MapBerSupport.logFallback("AlertServiceCentreRequest", t);
                this.msisdn = null; this.serviceCentreAddress = null;
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

        if (c.hasMore()) throw new AsnException(_PrimitiveName + ": optional fields present, fallback");
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.msisdn = null;
        this.serviceCentreAddress = null;
        this.imsi = null;
        this.correlationID = null;
        this.maximumUeAvailabilityTime = null;
        this.smsGmscAlertEvent = null;
        this.smsGmscDiameterAddress = null;
        this.newSGSNNumber = null;
        this.newSGSNDiameterAddress = null;
        this.newMMENumber = null;
        this.newMMEDiameterAddress = null;
        this.newMSCNumber = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();
            switch (num) {
                case 0:
                    if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive() || tag != Tag.STRING_OCTET)
                        throw new MAPParsingComponentException("Error while decoding" + _PrimitiveName + ".msisdn: " +
                                "bad tag or tag class or is not primitive: TagClass=" + ais.getTagClass() + ", tag=" + tag,
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.msisdn = new ISDNAddressStringImpl();
                    ((ISDNAddressStringImpl) this.msisdn).decodeAll(ais);
                    break;
                case 1:
                    if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive() || tag != Tag.STRING_OCTET)
                        throw new MAPParsingComponentException("Error while decoding AlertServiceCentreRequest.serviceCentreAddress: " +
                                "bad tag or tag class or is not primitive: TagClass=" + ais.getTagClass() + ", tag=" + tag,
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.serviceCentreAddress = new ISDNAddressStringImpl();
                    ((AddressStringImpl) this.serviceCentreAddress).decodeAll(ais);
                    break;
                default:
                    if (ais.getTagClass() == Tag.CLASS_UNIVERSAL) {
                        switch (tag) {
                            case Tag.STRING_OCTET:
                                if (ais.isTagPrimitive()) {
                                    this.imsi = new IMSIImpl();
                                    ((IMSIImpl) this.imsi).decodeAll(ais);
                                }
                                break;
                            case Tag.SEQUENCE:
                                if (!ais.isTagPrimitive()) {
                                    this.correlationID = new CorrelationIDImpl();
                                    ((CorrelationIDImpl) this.correlationID).decodeAll(ais);
                                }
                                break;
                        }
                    } else if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                        switch (tag) {
                            case _TAG_maximumUeAvailabilityTime:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".maximumUeAvailabilityTime: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                maximumUeAvailabilityTime = new TimeImpl();
                                ((TimeImpl) maximumUeAvailabilityTime).decodeAll(ais);
                                break;

                            case _TAG_smsGmscAlertEvent:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".serviceCentreAddress: Parameter not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                int i = (int) ais.readInteger();
                                this.smsGmscAlertEvent = SmsGmscAlertEvent.getInstance(i);
                                break;

                            case _TAG_smsGmscDiameterAddress:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".smsf3gppDiameterAddress: Parameter is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.smsGmscDiameterAddress = new NetworkNodeDiameterAddressImpl();
                                ((NetworkNodeDiameterAddressImpl) this.smsGmscDiameterAddress).decodeAll(ais);
                                break;

                            case _TAG_newSGSNNumber:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".newSGSNNumber: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.newSGSNNumber = new ISDNAddressStringImpl();
                                ((ISDNAddressStringImpl) this.newSGSNNumber).decodeAll(ais);
                                break;

                            case _TAG_newSGSNDiameterAddress:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".newSGSNDiameterAddress: Parameter is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.newSGSNDiameterAddress = new NetworkNodeDiameterAddressImpl();
                                ((NetworkNodeDiameterAddressImpl) this.newSGSNDiameterAddress).decodeAll(ais);
                                break;

                            case _TAG_newMMENumber:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".newMMENumber: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.newMMENumber = new ISDNAddressStringImpl();
                                ((ISDNAddressStringImpl) this.newMMENumber).decodeAll(ais);
                                break;

                            case _TAG_newMMEDiameterAddress:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".newMMEDiameterAddress: Parameter is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.newMMEDiameterAddress = new NetworkNodeDiameterAddressImpl();
                                ((NetworkNodeDiameterAddressImpl) this.newMMEDiameterAddress).decodeAll(ais);
                                break;

                            case _TAG_newMSCNumber:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".newMSCNumber: Parameter is not primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.newMSCNumber = new ISDNAddressStringImpl();
                                ((ISDNAddressStringImpl) this.newMSCNumber).decodeAll(ais);
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

        if (this.msisdn == null || this.serviceCentreAddress == null)
            throw new MAPParsingComponentException("Error while decoding AlertServiceCentreRequest: 2 parameters are mandatory, found " + num,
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
            throw new MAPException("AsnException when encoding AlertServiceCentreRequest: " + e.getMessage(), e);
        }
    }

    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {

        try {

            if (this.msisdn == null || this.serviceCentreAddress == null)
                throw new MAPException("Error when encoding AlertServiceCentreRequest: msisdn or serviceCentreAddress must not be empty");

            ((ISDNAddressStringImpl) this.msisdn).encodeAll(asnOutputStream);

            ((AddressStringImpl) this.serviceCentreAddress).encodeAll(asnOutputStream);

            if (this.imsi != null)
                ((IMSIImpl) this.imsi).encodeAll(asnOutputStream);

            if (this.correlationID != null)
                ((CorrelationIDImpl) this.correlationID).encodeAll(asnOutputStream);

            if (maximumUeAvailabilityTime != null)
                ((TimeImpl) this.maximumUeAvailabilityTime).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_maximumUeAvailabilityTime);

            if (smsGmscAlertEvent != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_smsGmscAlertEvent, this.smsGmscAlertEvent.getCode());

            if (smsGmscDiameterAddress != null)
                ((NetworkNodeDiameterAddressImpl) this.smsGmscDiameterAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_smsGmscDiameterAddress);

            if (newSGSNNumber != null)
                ((ISDNAddressStringImpl) this.newSGSNNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_newSGSNNumber);

            if (newSGSNDiameterAddress != null)
                ((NetworkNodeDiameterAddressImpl) this.newSGSNDiameterAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_newSGSNDiameterAddress);

            if (newMMENumber != null)
                ((ISDNAddressStringImpl) this.newMMENumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_newMMENumber);

            if (newMMEDiameterAddress != null)
                ((NetworkNodeDiameterAddressImpl) this.newMMEDiameterAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_newMMEDiameterAddress);

            if (newMSCNumber != null)
                ((ISDNAddressStringImpl) this.newMSCNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_newMSCNumber);

        } catch (IOException e) {
            throw new MAPException("IOException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AlertServiceCentreRequest [");

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

        sb.append("]");

        return sb.toString();
    }
}
