package org.restcomm.protocols.ss7.map.service.sms;

import java.io.IOException;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.lsm.AdditionalNumber;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.NetworkNodeDiameterAddress;
import org.restcomm.protocols.ss7.map.api.service.sms.LocationInfoWithLMSI;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.LMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;
import org.restcomm.protocols.ss7.map.service.lsm.AdditionalNumberImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.NetworkNodeDiameterAddressImpl;

/**
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public class LocationInfoWithLMSIImpl extends SequenceBase implements LocationInfoWithLMSI {

    private static final int _TAG_NetworkNodeNumber = 1;
    private static final int _TAG_GprsNodeIndicator = 5;
    private static final int _TAG_AdditionalNumber = 6;
    private static final int _TAG_NetworkNodeDiameterAddress = 7;
    private static final int _TAG_AdditionalNetworkNodeDiameterAddress = 8;
    private static final int _TAG_ThirdNumber = 9;
    private static final int _TAG_ThirdNetworkNodeDiameterAddress = 10;
    private static final int _TAG_ImsNodeIndicator = 11;
    private static final int _TAG_SMSF_3GPP_Number = 12;
    private static final int _TAG_SMSF_3GPP_DiameterAddress = 13;
    private static final int _TAG_SMSF_NON_3GPP_Number = 14;
    private static final int _TAG_SMSF_NON_3GPP_DiameterAddress = 15;
    private static final int _TAG_SMSF_3GPP_AddressIndicator = 16;
    private static final int _TAG_SMSF_NON_3GPP_AddressIndicator = 17;

    private ISDNAddressString networkNodeNumber;
    private LMSI lmsi;
    private MAPExtensionContainer extensionContainer;
    private boolean gprsNodeIndicator;
    private AdditionalNumber additionalNumber;
    private NetworkNodeDiameterAddress networkNodeDiameterAddress;
    private NetworkNodeDiameterAddress additionalNetworkNodeDiameterAddress;
    private AdditionalNumber thirdNumber;
    private NetworkNodeDiameterAddress thirdNetworkNodeDiameterAddress;
    private boolean imsNodeIndicator;
    private ISDNAddressString smsf3gppNumber;
    private NetworkNodeDiameterAddress smsf3gppDiameterAddress;
    private ISDNAddressString smsfNon3gppNumber;
    private NetworkNodeDiameterAddress smsfNon3gppDiameterAddress;
    private boolean smsf3gppAddressIndicator;
    private boolean smsfNon3gppAddressIndicator;

    public LocationInfoWithLMSIImpl() {
        super("LocationInfoWithLMSI");
    }

    public LocationInfoWithLMSIImpl(ISDNAddressString networkNodeNumber, LMSI lmsi, MAPExtensionContainer extensionContainer, boolean gprsNodeIndicator,
            AdditionalNumber additionalNumber, NetworkNodeDiameterAddress networkNodeDiameterAddress, NetworkNodeDiameterAddress additionalNetworkNodeDiameterAddress,
            AdditionalNumber thirdNumber, NetworkNodeDiameterAddress thirdNetworkNodeDiameterAddress, boolean imsNodeIndicator,
            ISDNAddressString smsf3gppNumber, NetworkNodeDiameterAddress smsf3gppDiameterAddress, ISDNAddressString smsfNon3gppNumber,
            NetworkNodeDiameterAddress smsfNon3gppDiameterAddress, boolean smsf3gppAddressIndicator, boolean smsfNon3gppAddressIndicator) {
        super("LocationInfoWithLMSI");

        this.networkNodeNumber = networkNodeNumber;
        this.lmsi = lmsi;
        this.extensionContainer = extensionContainer;
        this.gprsNodeIndicator = gprsNodeIndicator;
        this.additionalNumber = additionalNumber;
        this.networkNodeDiameterAddress = networkNodeDiameterAddress;
        this.additionalNetworkNodeDiameterAddress = additionalNetworkNodeDiameterAddress;
        this.thirdNumber = thirdNumber;
        this.thirdNetworkNodeDiameterAddress = thirdNetworkNodeDiameterAddress;
        this.imsNodeIndicator = imsNodeIndicator;
        this.smsf3gppNumber = smsf3gppNumber;
        this.smsf3gppDiameterAddress = smsf3gppDiameterAddress;
        this.smsfNon3gppNumber = smsfNon3gppNumber;
        this.smsfNon3gppDiameterAddress = smsfNon3gppDiameterAddress;
        this.smsf3gppAddressIndicator = smsf3gppAddressIndicator;
        this.smsfNon3gppAddressIndicator = smsfNon3gppAddressIndicator;
    }

    @Override
    public ISDNAddressString getNetworkNodeNumber() {
        return this.networkNodeNumber;
    }

    @Override
    public LMSI getLMSI() {
        return this.lmsi;
    }

    @Override
    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    @Override
    public boolean getGprsNodeIndicator() {
        return gprsNodeIndicator;
    }

    @Override
    public AdditionalNumber getAdditionalNumber() {
        return this.additionalNumber;
    }

    @Override
    public NetworkNodeDiameterAddress getNetworkNodeDiameterAddress() {
        return networkNodeDiameterAddress;
    }

    @Override
    public NetworkNodeDiameterAddress getAdditionalNetworkNodeDiameterAddress() {
        return additionalNetworkNodeDiameterAddress;
    }

    @Override
    public AdditionalNumber getThirdNumber() {
        return thirdNumber;
    }

    @Override
    public NetworkNodeDiameterAddress getThirdNetworkNodeDiameterAddress() {
        return thirdNetworkNodeDiameterAddress;
    }

    @Override
    public boolean getImsNodeIndicator() {
        return imsNodeIndicator;
    }

    @Override
    public ISDNAddressString getSmsf3gppNumber() {
        return smsf3gppNumber;
    }

    @Override
    public NetworkNodeDiameterAddress getSmsf3gppDiameterAddress() {
        return smsf3gppDiameterAddress;
    }

    @Override
    public ISDNAddressString getSmsfNon3gppNumber() {
        return smsfNon3gppNumber;
    }

    @Override
    public NetworkNodeDiameterAddress getSmsfNon3gppDiameterAddress() {
        return smsfNon3gppDiameterAddress;
    }

    public boolean getSmsf3gppAddressIndicator() {
        return smsf3gppAddressIndicator;
    }

    public boolean getSmsfNon3gppAddressIndicator() {
        return smsfNon3gppAddressIndicator;
    }

    protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.networkNodeNumber = null;
        this.lmsi = null;
        this.extensionContainer = null;
        this.gprsNodeIndicator = false;
        this.additionalNumber = null;
        this.networkNodeDiameterAddress = null;
        this.additionalNetworkNodeDiameterAddress = null;
        this.thirdNumber = null;
        this.thirdNetworkNodeDiameterAddress = null;
        this.imsNodeIndicator = false;
        this.smsf3gppNumber = null;
        this.smsf3gppDiameterAddress = null;
        this.smsfNon3gppNumber = null;
        this.smsfNon3gppDiameterAddress = null;
        this.smsf3gppAddressIndicator = false;
        this.smsfNon3gppAddressIndicator = false;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            if (num == 0) {
                // first parameter is mandatory - networkNode-Number
                if (ais.getTagClass() != Tag.CLASS_CONTEXT_SPECIFIC || !ais.isTagPrimitive() || tag != _TAG_NetworkNodeNumber)
                    throw new MAPParsingComponentException(
                            "Error when decoding LocationInfoWithLMSI: networkNode-Number: tagClass or tag is bad or element is not primitive: tagClass="
                                    + ais.getTagClass() + ", Tag=" + tag, MAPParsingComponentExceptionReason.MistypedParameter);
                this.networkNodeNumber = new ISDNAddressStringImpl();
                ((ISDNAddressStringImpl) this.networkNodeNumber).decodeAll(ais);
            } else {
                // optional parameters
                if (ais.getTagClass() == Tag.CLASS_UNIVERSAL) {

                    switch (tag) {
                    case Tag.STRING_OCTET:
                        if (!ais.isTagPrimitive() || this.lmsi != null)
                            throw new MAPParsingComponentException("Error when decoding " + _PrimitiveName
                                    + ": lmsi: double element or element is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                        this.lmsi = new LMSIImpl();
                        ((LMSIImpl) this.lmsi).decodeAll(ais);
                        break;

                    case Tag.SEQUENCE:
                        if (ais.isTagPrimitive() || this.extensionContainer != null)
                            throw new MAPParsingComponentException("Error when decoding " + _PrimitiveName
                                    + ": extensionContainer: double element or element is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
                        this.extensionContainer = new MAPExtensionContainerImpl();
                        ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                        break;

                        default:
                            ais.advanceElement();
                            break;
                    }
                } else if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {

                    switch (tag) {
                    case _TAG_GprsNodeIndicator:
                        if (!ais.isTagPrimitive() || this.gprsNodeIndicator)
                            throw new MAPParsingComponentException("Error when decoding " + _PrimitiveName
                                    + ":.gprsNodeIndicator: double element or element is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        ais.readNull();
                        this.gprsNodeIndicator = true;
                        break;

                    case _TAG_AdditionalNumber:
                        if (ais.isTagPrimitive() || this.additionalNumber != null)
                            throw new MAPParsingComponentException("Error when decoding " + _PrimitiveName
                                    + ": additionalNumber: double element or element is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        AsnInputStream ais2 = ais.readSequenceStream();
                        ais2.readTag();
                        this.additionalNumber = new AdditionalNumberImpl();
                        ((AdditionalNumberImpl) this.additionalNumber).decodeAll(ais2);
                        break;

                        case _TAG_NetworkNodeDiameterAddress:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".networkNodeDiameterAddress: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.networkNodeDiameterAddress = new NetworkNodeDiameterAddressImpl();
                            ((NetworkNodeDiameterAddressImpl) this.networkNodeDiameterAddress).decodeAll(ais);
                            break;

                        case _TAG_AdditionalNetworkNodeDiameterAddress:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".additionalNetworkNodeDiameterAddress: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.additionalNetworkNodeDiameterAddress = new NetworkNodeDiameterAddressImpl();
                            ((NetworkNodeDiameterAddressImpl) this.additionalNetworkNodeDiameterAddress).decodeAll(ais);
                            break;

                        case _TAG_ThirdNumber:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".thirdNumber: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            AsnInputStream ais3 = ais.readSequenceStream();
                            ais3.readTag();
                            this.thirdNumber = new AdditionalNumberImpl();
                            ((AdditionalNumberImpl) this.thirdNumber).decodeAll(ais3);
                            break;

                        case _TAG_ThirdNetworkNodeDiameterAddress:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".thirdNetworkNodeDiameterAddress: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.thirdNetworkNodeDiameterAddress = new NetworkNodeDiameterAddressImpl();
                            ((NetworkNodeDiameterAddressImpl) this.thirdNetworkNodeDiameterAddress).decodeAll(ais);
                            break;

                        case _TAG_ImsNodeIndicator:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error when decoding " + _PrimitiveName
                                        + ":.imsNodeIndicator: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            ais.readNull();
                            this.imsNodeIndicator = true;
                            break;

                        case _TAG_SMSF_3GPP_Number:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".smsf3gppNumber: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.smsf3gppNumber = new ISDNAddressStringImpl();
                            ((ISDNAddressStringImpl) this.smsf3gppNumber).decodeAll(ais);
                            break;

                        case _TAG_SMSF_3GPP_DiameterAddress:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".smsf3gppDiameterAddress: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.smsf3gppDiameterAddress = new NetworkNodeDiameterAddressImpl();
                            ((NetworkNodeDiameterAddressImpl) this.smsf3gppDiameterAddress).decodeAll(ais);
                            break;

                        case _TAG_SMSF_NON_3GPP_Number:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".smsfNon3gppNumber: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.smsfNon3gppNumber = new ISDNAddressStringImpl();
                            ((ISDNAddressStringImpl) this.smsfNon3gppNumber).decodeAll(ais);
                            break;

                        case _TAG_SMSF_NON_3GPP_DiameterAddress:
                            if (ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".smsfNon3gppDiameterAddress: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.smsfNon3gppDiameterAddress = new NetworkNodeDiameterAddressImpl();
                            ((NetworkNodeDiameterAddressImpl) this.smsfNon3gppDiameterAddress).decodeAll(ais);
                            break;

                        case _TAG_SMSF_3GPP_AddressIndicator:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error when decoding " + _PrimitiveName
                                        + ":.smsf3gppAddressIndicator: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            ais.readNull();
                            this.smsf3gppAddressIndicator = true;
                            break;

                        case _TAG_SMSF_NON_3GPP_AddressIndicator:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error when decoding " + _PrimitiveName
                                        + ":.smsfNon3gppAddressIndicator: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            ais.readNull();
                            this.smsfNon3gppAddressIndicator = true;
                            break;

                    default:
                        ais.advanceElement();
                        break;
                    }
                } else {
                    ais.advanceElement();
                }
            }

            num++;
        }

        if (this.networkNodeNumber == null)
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName + ": 1 parameter is mandatory, found " + num,
                    MAPParsingComponentExceptionReason.MistypedParameter);
    }

    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {

        try {
            if (this.networkNodeNumber == null)
                throw new MAPException("Error while encoding " + _PrimitiveName + ": networkNodeNumber must not be null");

            ((ISDNAddressStringImpl) this.networkNodeNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                    _TAG_NetworkNodeNumber);

            if (this.lmsi != null)
                ((LMSIImpl) this.lmsi).encodeAll(asnOutputStream);

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream);

            if (gprsNodeIndicator)
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_GprsNodeIndicator);

            if (this.additionalNumber != null) {
                try {
                    asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_AdditionalNumber);
                } catch (AsnException e) {
                    throw new MAPException("AsnException while encoding parameter additionalNumber");
                }
                int pos = asnOutputStream.StartContentDefiniteLength();
                ((AdditionalNumberImpl) this.additionalNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        ((AdditionalNumberImpl) this.additionalNumber).getTag());
                asnOutputStream.FinalizeContent(pos);
            }

            if (networkNodeDiameterAddress != null)
                ((NetworkNodeDiameterAddressImpl) this.networkNodeDiameterAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_NetworkNodeDiameterAddress);

            if (additionalNetworkNodeDiameterAddress != null)
                ((NetworkNodeDiameterAddressImpl) this.additionalNetworkNodeDiameterAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_AdditionalNetworkNodeDiameterAddress);

            if (this.thirdNumber != null) {
                if (this.thirdNumber != this.additionalNumber) {
                    try {
                        asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_ThirdNumber);
                    } catch (AsnException e) {
                        throw new MAPException("AsnException while encoding parameter thirdNumber");
                    }
                    int pos = asnOutputStream.StartContentDefiniteLength();
                    ((AdditionalNumberImpl) this.thirdNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                            ((AdditionalNumberImpl) this.thirdNumber).getTag());
                    asnOutputStream.FinalizeContent(pos);

                } else {
                    throw new MAPException("Exception when encoding " + _PrimitiveName + ".additionalNumber: " +
                            "additionalNumber and thirdNumber shall not both contain the same type of number");
                }
            }

            if (thirdNetworkNodeDiameterAddress != null)
                ((NetworkNodeDiameterAddressImpl) this.thirdNetworkNodeDiameterAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_ThirdNetworkNodeDiameterAddress);

            if (imsNodeIndicator) {
                if (!gprsNodeIndicator)
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_ImsNodeIndicator);
                else
                    throw new MAPException("Exception when encoding " + _PrimitiveName + ".imsNodeIndicator: " +
                            "gprsNodeIndicator and imsNodeIndicator shall not both be present");
            }

            if (smsf3gppNumber != null)
                ((ISDNAddressStringImpl) this.smsf3gppNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_SMSF_3GPP_Number);

            if (smsf3gppDiameterAddress != null)
                ((NetworkNodeDiameterAddressImpl) this.smsf3gppDiameterAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SMSF_3GPP_DiameterAddress);

            if (smsfNon3gppNumber != null)
                ((ISDNAddressStringImpl) this.smsfNon3gppNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_SMSF_NON_3GPP_Number);

            if (smsfNon3gppDiameterAddress != null)
                ((NetworkNodeDiameterAddressImpl) this.smsfNon3gppDiameterAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SMSF_NON_3GPP_DiameterAddress);

            if (smsf3gppAddressIndicator) {
                if (!gprsNodeIndicator && !imsNodeIndicator)
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SMSF_3GPP_AddressIndicator);
                else
                    throw new MAPException("Exception when encoding " + _PrimitiveName + ".smsf3gppAddressIndicator: " +
                            "at most one of gprsNodeIndicator, imsNodeIndicator, smsf3gppAddressIndicator " +
                            " and smsfNon3gppAddressIndicator shall be present");
            }

            if (smsfNon3gppAddressIndicator) {
                if (!gprsNodeIndicator && !imsNodeIndicator && !smsf3gppAddressIndicator)
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SMSF_NON_3GPP_AddressIndicator);
                else
                    throw new MAPException("Exception when encoding " + _PrimitiveName + ".smsfNon3gppAddressIndicator: " +
                            "at most one of gprsNodeIndicator, imsNodeIndicator, smsf3gppAddressIndicator" +
                            " and smsfNon3gppAddressIndicator shall be present");
            }

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

        if (this.networkNodeNumber != null) {
            sb.append("networkNodeNumber=");
            sb.append(this.networkNodeNumber);
        }
        if (this.lmsi != null) {
            sb.append(", lmsi=");
            sb.append(this.lmsi);
        }
        if (this.extensionContainer != null) {
            sb.append(", extensionContainer=");
            sb.append(this.extensionContainer);
        }
        if (this.gprsNodeIndicator) {
            sb.append(", gprsNodeIndicator");
        }
        if (this.additionalNumber != null) {
            sb.append(", additionalNumber=");
            sb.append(this.additionalNumber);
        }
        if (this.networkNodeDiameterAddress != null) {
            sb.append(", networkNodeDiameterAddress=");
            sb.append(this.networkNodeDiameterAddress);
        }
        if (this.additionalNetworkNodeDiameterAddress != null) {
            sb.append(", additionalNetworkNodeDiameterAddress=");
            sb.append(this.additionalNetworkNodeDiameterAddress);
        }
        if (this.thirdNumber != null) {
            sb.append(", thirdNumber=");
            sb.append(this.thirdNumber);
        }
        if (this.thirdNetworkNodeDiameterAddress != null) {
            sb.append(", thirdNetworkNodeDiameterAddress=");
            sb.append(this.thirdNetworkNodeDiameterAddress);
        }
        if (this.imsNodeIndicator) {
            sb.append(", imsNodeIndicator");
        }
        if (this.smsf3gppNumber != null) {
            sb.append(", smsf3gppNumber=");
            sb.append(this.smsf3gppNumber);
        }
        if (this.smsf3gppDiameterAddress != null) {
            sb.append(", smsf3gppDiameterAddress=");
            sb.append(this.smsf3gppDiameterAddress);
        }
        if (this.smsfNon3gppNumber != null) {
            sb.append(", smsfNon3gppNumber=");
            sb.append(this.smsfNon3gppNumber);
        }
        if (this.smsfNon3gppDiameterAddress != null) {
            sb.append(", smsfNon3gppDiameterAddress=");
            sb.append(this.smsfNon3gppDiameterAddress);
        }
        if (this.smsf3gppAddressIndicator) {
            sb.append(", smsf3gppAddressIndicator");
        }
        if (this.smsfNon3gppAddressIndicator) {
            sb.append(", smsfNon3gppAddressIndicator");
        }

        sb.append("]");

        return sb.toString();
    }
}
