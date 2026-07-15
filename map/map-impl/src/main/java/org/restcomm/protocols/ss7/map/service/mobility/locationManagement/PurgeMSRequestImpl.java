package org.restcomm.protocols.ss7.map.service.mobility.locationManagement;

import java.io.IOException;

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
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationEPS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationGPRS;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.service.mobility.MobilityMessageImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.LocationInformationEPSImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.LocationInformationGPRSImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.LocationInformationImpl;

/**
 *
 * @author Lasith Waruna Perera
 *
 */
public class PurgeMSRequestImpl extends MobilityMessageImpl implements PurgeMSRequest {

    protected static final int _TAG_vlrNumber = 0;
    protected static final int _TAG_sgsnNumber = 1;
    public static final int _TAG_locationInformation = 2;
    public static final int _TAG_locationInformationGPRS = 3;
    public static final int _TAG_locationInformationEPS = 4;
    public static final int _TAG_PurgeMSRequest = 3;

    public static final String _PrimitiveName = "PurgeMSRequest";

    private IMSI imsi;
    private ISDNAddressString vlrNumber;
    private ISDNAddressString sgsnNumber;
    private MAPExtensionContainer extensionContainer;
    private LocationInformation locationInformation;
    private LocationInformationGPRS locationInformationGPRS;
    private LocationInformationEPS locationInformationEPS;
    private long mapProtocolVersion;

    public PurgeMSRequestImpl(long mapProtocolVersion) {
        super();
        this.mapProtocolVersion = mapProtocolVersion;
    }

    public PurgeMSRequestImpl(IMSI imsi, ISDNAddressString vlrNumber, ISDNAddressString sgsnNumber,
            MAPExtensionContainer extensionContainer, LocationInformation locationInformation,
            LocationInformationGPRS locationInformationGPRS, LocationInformationEPS locationInformationEPS,
            long mapProtocolVersion) {
        super();
        this.imsi = imsi;
        this.vlrNumber = vlrNumber;
        this.sgsnNumber = sgsnNumber;
        this.extensionContainer = extensionContainer;
        this.locationInformation = locationInformation;
        this.locationInformationGPRS = locationInformationGPRS;
        this.locationInformationEPS = locationInformationEPS;
        this.mapProtocolVersion = mapProtocolVersion;
    }

    @Override
    public MAPMessageType getMessageType() {
        return MAPMessageType.purgeMS_Request;
    }

    @Override
    public int getOperationCode() {
        return MAPOperationCode.purgeMS;
    }

    @Override
    public IMSI getImsi() {
        return this.imsi;
    }

    @Override
    public ISDNAddressString getVlrNumber() {
        return this.vlrNumber;
    }

    @Override
    public ISDNAddressString getSgsnNumber() {
        return this.sgsnNumber;
    }

    @Override
    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    @Override
    public LocationInformation getLocationInformation() {
        return this.locationInformation;
    }

    @Override
    public LocationInformationGPRS getLocationInformationGPRS() {
        return this.locationInformationGPRS;
    }

    @Override
    public LocationInformationEPS getLocationInformationEPS() {
        return this.locationInformationEPS;
    }

    @Override
    public int getTag() throws MAPException {
        if (this.mapProtocolVersion >= 3) {
            return _TAG_PurgeMSRequest;
        } else {
            return Tag.SEQUENCE;
        }
    }

    @Override
    public int getTagClass() {
        if (this.mapProtocolVersion >= 3) {
            return Tag.CLASS_CONTEXT_SPECIFIC;
        } else {
            return Tag.CLASS_UNIVERSAL;
        }
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

    @Override
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
        this.imsi = null;
        this.vlrNumber = null;
        this.sgsnNumber = null;
        this.extensionContainer = null;
        this.locationInformation = null;
        this.locationInformationGPRS = null;
        this.locationInformationEPS = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            if (num == 0) {
                if (tag != Tag.STRING_OCTET || ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive()) {
                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                            + ".imsi: Parameter 0 bad tag or tag class or not primitive",
                            MAPParsingComponentExceptionReason.MistypedParameter);

                }
                this.imsi = new IMSIImpl();
                ((IMSIImpl) this.imsi).decodeAll(ais);

            } else if (num == 1 && this.mapProtocolVersion < 3) {
                if (tag != Tag.STRING_OCTET || ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive()) {
                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                            + ".imsi: Parameter 0 bad tag or tag class or not primitive",
                            MAPParsingComponentExceptionReason.MistypedParameter);

                }
                this.vlrNumber = new ISDNAddressStringImpl();
                ((ISDNAddressStringImpl) this.vlrNumber).decodeAll(ais);

            } else {

                if (this.mapProtocolVersion >= 3) {

                    switch (ais.getTagClass()) {
                        case Tag.CLASS_UNIVERSAL:
                            switch (tag) {
                                case Tag.SEQUENCE:
                                    if (ais.isTagPrimitive()) {
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + ".extensionContainer: is primitive",
                                                MAPParsingComponentExceptionReason.MistypedParameter);
                                    }
                                    this.extensionContainer = new MAPExtensionContainerImpl();
                                    ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                                    break;
                                default:
                                    ais.advanceElement();
                                    break;
                            }
                            break;

                        case Tag.CLASS_CONTEXT_SPECIFIC:
                            switch (tag) {
                                case _TAG_vlrNumber:
                                    if (!ais.isTagPrimitive()) {
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + ".vlrNumber: is not primitive",
                                                MAPParsingComponentExceptionReason.MistypedParameter);
                                    }
                                    this.vlrNumber = new ISDNAddressStringImpl();
                                    ((ISDNAddressStringImpl) this.vlrNumber).decodeAll(ais);
                                    break;
                                case _TAG_sgsnNumber:
                                    if (!ais.isTagPrimitive()) {
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + ".sgsnNumber: is not primitive",
                                                MAPParsingComponentExceptionReason.MistypedParameter);
                                    }
                                    this.sgsnNumber = new ISDNAddressStringImpl();
                                    ((ISDNAddressStringImpl) this.sgsnNumber).decodeAll(ais);
                                    break;
                                case _TAG_locationInformation:
                                    if (ais.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + " locationInformation: Parameter is primitive",
                                                MAPParsingComponentExceptionReason.MistypedParameter);
                                    this.locationInformation = new LocationInformationImpl();
                                    ((LocationInformationImpl) this.locationInformation).decodeAll(ais);
                                    break;
                                case _TAG_locationInformationGPRS:
                                    if (ais.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + " locationInformationGPRS: Parameter is primitive",
                                                MAPParsingComponentExceptionReason.MistypedParameter);
                                    locationInformationGPRS = new LocationInformationGPRSImpl();
                                    ((LocationInformationGPRSImpl) locationInformationGPRS).decodeAll(ais);
                                    break;
                                case _TAG_locationInformationEPS:
                                    if (ais.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                                + " locationInformationEPS: Parameter is primitive",
                                                MAPParsingComponentExceptionReason.MistypedParameter);
                                    this.locationInformationEPS = new LocationInformationEPSImpl();
                                    ((LocationInformationEPSImpl) this.locationInformationEPS).decodeAll(ais);
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
                } else {

                }
            }

            num++;
        }

        if (this.mapProtocolVersion < 3) {
            if (this.vlrNumber == null) {
                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                        + ": vlrNumber is mandatory for MAP V2 but not found ",
                        MAPParsingComponentExceptionReason.MistypedParameter);
            }

        }

    }

    @Override
    public void encodeAll(AsnOutputStream asnOutputStream) throws MAPException {
        this.encodeAll(asnOutputStream, this.getTagClass(), this.getTag());
    }

    @Override
    public void encodeAll(AsnOutputStream asnOutputStream, int tagClass, int tag) throws MAPException {
        try {
            asnOutputStream.writeTag(tagClass, getIsPrimitive(), tag);
            int pos = asnOutputStream.StartContentDefiniteLength();
            this.encodeData(asnOutputStream);
            asnOutputStream.FinalizeContent(pos);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        if (this.imsi == null) {
            throw new MAPException("Error while encoding " + _PrimitiveName + " the mandatory parameter imsi is not defined");
        }

        if (mapProtocolVersion >= 3) {

            ((IMSIImpl) this.imsi).encodeAll(asnOutputStream);

            if (this.vlrNumber != null)
                ((ISDNAddressStringImpl) this.vlrNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_vlrNumber);

            if (this.sgsnNumber != null)
                ((ISDNAddressStringImpl) this.sgsnNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_sgsnNumber);

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream);

            if (this.locationInformation != null)
                ((LocationInformationImpl) this.locationInformation).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_locationInformation);

            if (this.locationInformationGPRS != null)
                ((LocationInformationGPRSImpl) this.locationInformationGPRS).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_locationInformationGPRS);

            if (this.locationInformationEPS != null)
                ((LocationInformationEPSImpl) this.locationInformationEPS).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_locationInformationEPS);

        } else {

            if (this.vlrNumber == null) {
                throw new MAPException("Error while encoding " + _PrimitiveName
                        + " the mandatory parameter vlrNumber is not defined");
            }

            ((IMSIImpl) this.imsi).encodeAll(asnOutputStream);

            ((ISDNAddressStringImpl) this.vlrNumber).encodeAll(asnOutputStream);

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

        if (this.vlrNumber != null) {
            sb.append("vlrNumber=");
            sb.append(vlrNumber);
            sb.append(", ");
        }

        if (this.sgsnNumber != null) {
            sb.append("sgsnNumber=");
            sb.append(sgsnNumber);
            sb.append(", ");
        }

        if (this.extensionContainer != null) {
            sb.append("extensionContainer=");
            sb.append(extensionContainer);
            sb.append(", ");
        }

        if (this.locationInformation != null) {
            sb.append("locationInformation=");
            sb.append(locationInformation);
            sb.append(", ");
        }

        if (this.locationInformationGPRS != null) {
            sb.append("locationInformationGPRS=");
            sb.append(locationInformationGPRS);
            sb.append(", ");
        }

        if (this.locationInformationEPS != null) {
            sb.append("locationInformationEPS=");
            sb.append(locationInformationEPS);
            sb.append(", ");
        }

        sb.append("mapProtocolVersion=");
        sb.append(mapProtocolVersion);

        sb.append("]");

        return sb.toString();
    }

}
