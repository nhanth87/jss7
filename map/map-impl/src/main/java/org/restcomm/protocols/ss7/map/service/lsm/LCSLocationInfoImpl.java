package org.restcomm.protocols.ss7.map.service.lsm;

import java.io.IOException;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.DiameterIdentity;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.lsm.AdditionalNumber;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedLCSCapabilitySets;
import org.restcomm.protocols.ss7.map.primitives.DiameterIdentityImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.LMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SupportedLCSCapabilitySetsImpl;

/**
 *
 * @author amit bhayani
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class LCSLocationInfoImpl extends SequenceBase implements LCSLocationInfo {

    private static final int _TAG_LMSI = 0;
    private static final int _TAG_EXTENSION_CONTAINER = 1;
    private static final int _TAG_GPRS_NODE_IND = 2;
    private static final int _TAG_ADDITIONAL_NUMBER = 3;
    private static final int _TAG_SUPPORTED_LCS_CAPABILITY_SETS = 4;
    private static final int __TAG_ADDITIONAL_LCS_CAPABILITY_SETS = 5;
    private static final int _TAG_MME_NAME = 6;
    private static final int _TAG_AAA_SERVER_NAME = 8;
    private static final int _TAG_SGSN_NAME = 9;
    private static final int _TAG_SGSN_REALM = 10;

    private ISDNAddressString networkNodeNumber;
    private LMSI lmsi;
    private MAPExtensionContainer extensionContainer;
    private boolean gprsNodeIndicator;
    private AdditionalNumber additionalNumber;
    private SupportedLCSCapabilitySets supportedLCSCapabilitySets;
    private SupportedLCSCapabilitySets additionalLCSCapabilitySets;
    private DiameterIdentity mmeName;
    private DiameterIdentity aaaServerName;
    private DiameterIdentity sgsnName;
    private DiameterIdentity sgsnRealm;

    /**
     *
     */
    public LCSLocationInfoImpl() {
        super("LCSLocationInfo");
    }

    /**
     * @param networkNodeNumber NetworkNode-number can be msc-number, sgsn-number or a dummy value of "0"
     * @param lmsi optional
     * @param extensionContainer optional
     * @param gprsNodeIndicator optional
     * @param additionalNumber optional
     * @param supportedLCSCapabilitySets optional
     * @param additionalLCSCapabilitySets optional
     * @param mmeName optional
     * @param aaaServerName optional
     */
    public LCSLocationInfoImpl(ISDNAddressString networkNodeNumber, LMSI lmsi, MAPExtensionContainer extensionContainer,
            boolean gprsNodeIndicator, AdditionalNumber additionalNumber,
            SupportedLCSCapabilitySets supportedLCSCapabilitySets, SupportedLCSCapabilitySets additionalLCSCapabilitySets,
            DiameterIdentity mmeName, DiameterIdentity aaaServerName, DiameterIdentity sgsnName, DiameterIdentity sgsnRealm) {
        super("LCSLocationInfo");

        this.networkNodeNumber = networkNodeNumber;
        this.lmsi = lmsi;
        this.extensionContainer = extensionContainer;
        this.gprsNodeIndicator = gprsNodeIndicator;
        this.additionalNumber = additionalNumber;
        this.supportedLCSCapabilitySets = supportedLCSCapabilitySets;
        this.additionalLCSCapabilitySets = additionalLCSCapabilitySets;
        this.mmeName = mmeName;
        this.aaaServerName = aaaServerName;
        this.sgsnName = sgsnName;
        this.sgsnRealm = sgsnRealm;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo#getNetworkNodeNumber()
     */
    public ISDNAddressString getNetworkNodeNumber() {
        return this.networkNodeNumber;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo#getLMSI()
     */
    public LMSI getLMSI() {
        return this.lmsi;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo#getExtensionContainer()
     */
    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo#getGprsNodeIndicator()
     */
    public boolean getGprsNodeIndicator() {
        return this.gprsNodeIndicator;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo# getAdditionalNumber()
     */
    public AdditionalNumber getAdditionalNumber() {
        return this.additionalNumber;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo#getSupportedLCSCapabilitySets()
     */
    public SupportedLCSCapabilitySets getSupportedLCSCapabilitySets() {
        return this.supportedLCSCapabilitySets;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo#getAdditionalLCSCapabilitySets()
     */
    public SupportedLCSCapabilitySets getAdditionalLCSCapabilitySets() {
        return this.additionalLCSCapabilitySets;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo#getMmeName()
     */
    public DiameterIdentity getMmeName() {
        return mmeName;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo#getAaaServerName()
     */
    public DiameterIdentity getAaaServerName() {
        return aaaServerName;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo#getSgsnName()
     */
    public DiameterIdentity getSgsnName() {
        return sgsnName;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo#getSgsnRealm()
     */
    public DiameterIdentity getSgsnRealm() {
        return sgsnRealm;
    }

    protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.networkNodeNumber = null;
        this.lmsi = null;
        this.extensionContainer = null;
        this.gprsNodeIndicator = false;
        this.additionalNumber = null;
        this.supportedLCSCapabilitySets = null;
        this.additionalLCSCapabilitySets = null;
        this.mmeName = null;
        this.aaaServerName = null;
        this.sgsnName = null;
        this.sgsnRealm = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

        int tag = ais.readTag();
        if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive() || tag != Tag.STRING_OCTET) {
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": Parameter [networkNode-Number ISDN-AddressString] bad tag class, tag or not primitive",
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
        this.networkNodeNumber = new ISDNAddressStringImpl();
        ((ISDNAddressStringImpl) this.networkNodeNumber).decodeAll(ais);

        while (true) {
            if (ais.available() == 0)
                break;

            tag = ais.readTag();

            if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                switch (tag) {
                    case _TAG_LMSI:
                        // lmsi [0] LMSI OPTIONAL,
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter [lmsi [0] LMSI ] bad tag class, tag or not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.lmsi = new LMSIImpl();
                        ((LMSIImpl) this.lmsi).decodeAll(ais);

                        break;
                    case _TAG_EXTENSION_CONTAINER:
                        // extensionContainer [1] ExtensionContainer
                        if (ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter [extensionContainer [1] ExtensionContainer ] is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.extensionContainer = new MAPExtensionContainerImpl();
                        ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                        break;
                    case _TAG_GPRS_NODE_IND:
                        // gprsNodeIndicator [2] NULL
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter [gprsNodeIndicator [2] NULL ] is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        ais.readNull();
                        this.gprsNodeIndicator = true;
                        break;
                    case _TAG_ADDITIONAL_NUMBER:
                        // additional-Number [3] Additional-Number OPTIONAL
                        if (ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter [additional-Number [3] Additional-Number] is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.additionalNumber = new AdditionalNumberImpl();
                        AsnInputStream ais2 = ais.readSequenceStream();
                        ais2.readTag();
                        ((AdditionalNumberImpl) this.additionalNumber).decodeAll(ais2);
                        break;
                    case _TAG_SUPPORTED_LCS_CAPABILITY_SETS:
                        // supportedLCS-CapabilitySets   [4] SupportedLCS-CapabilitySets OPTIONAL
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException(
                                    "Error while decoding "
                                            + _PrimitiveName
                                            + ": Parameter [supportedLCS-CapabilitySets [4] SupportedLCS-CapabilitySets] is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.supportedLCSCapabilitySets = new SupportedLCSCapabilitySetsImpl();
                        ((SupportedLCSCapabilitySetsImpl) this.supportedLCSCapabilitySets).decodeAll(ais);
                        break;
                    case __TAG_ADDITIONAL_LCS_CAPABILITY_SETS:
                        // additional-LCS-CapabilitySets [5] SupportedLCS-CapabilitySets OPTIONAL
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException(
                                    "Error while decoding "
                                            + _PrimitiveName
                                            + ": Parameter [additional-LCS-CapabilitySets [5] SupportedLCS-CapabilitySets] is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.additionalLCSCapabilitySets = new SupportedLCSCapabilitySetsImpl();
                        ((SupportedLCSCapabilitySetsImpl) this.additionalLCSCapabilitySets).decodeAll(ais);
                        break;
                    case _TAG_MME_NAME:
                        // mme-Name [6] DiameterIdentity OPTIONAL
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter mmeName is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.mmeName = new DiameterIdentityImpl();
                        ((DiameterIdentityImpl) this.mmeName).decodeAll(ais);
                        break;
                    case _TAG_AAA_SERVER_NAME:
                        // aaa-Server-Name [8] DiameterIdentity OPTIONAL
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter aaaServerName is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.aaaServerName = new DiameterIdentityImpl();
                        ((DiameterIdentityImpl) this.aaaServerName).decodeAll(ais);
                        break;
                    case _TAG_SGSN_NAME:
                        // sgsn-Name [9] DiameterIdentity OPTIONAL
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter sgsnName is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.sgsnName = new DiameterIdentityImpl();
                        ((DiameterIdentityImpl) this.sgsnName).decodeAll(ais);
                        break;
                    case _TAG_SGSN_REALM:
                        // sgsn-Realm [10] DiameterIdentity OPTIONAL
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter sgsnRealm is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.sgsnRealm = new DiameterIdentityImpl();
                        ((DiameterIdentityImpl) this.sgsnRealm).decodeAll(ais);
                        break;
                    default:
                        ais.advanceElement();
                }
            } else {
                ais.advanceElement();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#encodeData(org.mobicents.protocols.asn.AsnOutputStream)
     */
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {

        if (this.networkNodeNumber == null) {
            throw new MAPException(
                    "Error while encoding LCSLocationInfo the mandatory parameter networkNode-Number ISDN-AddressString is not defined");
        }

        ((ISDNAddressStringImpl) this.networkNodeNumber).encodeAll(asnOutputStream);

        if (this.lmsi != null) {
            // lmsi [0] LMSI OPTIONAL,
            ((LMSIImpl) this.lmsi).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_LMSI);
        }

        if (this.extensionContainer != null) {
            // extensionContainer [1] ExtensionContainer OPTIONAL,
            ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                    _TAG_EXTENSION_CONTAINER);
        }

        if (this.gprsNodeIndicator) {
            // gprsNodeIndicator [2] NULL OPTIONAL,
            try {
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_GPRS_NODE_IND);
            } catch (IOException e) {
                throw new MAPException(
                        "Error while encoding LCSLocationInfo the optional parameter gprsNodeIndicator encoding failed ", e);
            } catch (AsnException e) {
                throw new MAPException(
                        "AsnException while encoding LCSLocationInfo the optional parameter gprsNodeIndicator encoding failed ", e);
            }
        }

        if (this.additionalNumber != null) {
            // additional-Number [3] Additional-Number OPTIONAL,
            try {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_ADDITIONAL_NUMBER);
            } catch (AsnException e) {
                throw new MAPException("AsnException while encoding parameter additional-Number");
            }

            int pos = asnOutputStream.StartContentDefiniteLength();
            ((AdditionalNumberImpl) this.additionalNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                    ((AdditionalNumberImpl) this.additionalNumber).getTag());
            asnOutputStream.FinalizeContent(pos);
        }

        if (this.supportedLCSCapabilitySets != null) {
            // supportedLCS-CapabilitySets [4] SupportedLCS-CapabilitySets OPTIONAL
            ((SupportedLCSCapabilitySetsImpl) this.supportedLCSCapabilitySets).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                    _TAG_SUPPORTED_LCS_CAPABILITY_SETS);
        }

        if (this.additionalLCSCapabilitySets != null) {
            // additional-LCS-CapabilitySets [5] SupportedLCS-CapabilitySets OPTIONAL
            ((SupportedLCSCapabilitySetsImpl) this.additionalLCSCapabilitySets).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                    __TAG_ADDITIONAL_LCS_CAPABILITY_SETS);
        }

        if (this.mmeName != null) {
            // mme-Name [6] DiameterIdentity OPTIONAL
            ((DiameterIdentityImpl) this.mmeName).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_MME_NAME);
        }

        if (this.aaaServerName != null) {
            // aaa-Server-Name [8] DiameterIdentity OPTIONAL
            ((DiameterIdentityImpl) this.aaaServerName).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_AAA_SERVER_NAME);
        }

        if (this.sgsnName != null) {
            // sgsn-Name [9] DiameterIdentity OPTIONAL
            ((DiameterIdentityImpl) this.sgsnName).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SGSN_NAME);
        }

        if (this.sgsnRealm != null) {
            // sgsn-Realm [10] DiameterIdentity OPTIONAL
            ((DiameterIdentityImpl) this.sgsnRealm).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SGSN_REALM);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((additionalLCSCapabilitySets == null) ? 0 : additionalLCSCapabilitySets.hashCode());
        result = prime * result + ((additionalNumber == null) ? 0 : additionalNumber.hashCode());
        result = prime * result + ((extensionContainer == null) ? 0 : extensionContainer.hashCode());
        result = prime * result + ((gprsNodeIndicator) ? 1 : 0);
        result = prime * result + ((lmsi == null) ? 0 : lmsi.hashCode());
        result = prime * result + ((networkNodeNumber == null) ? 0 : networkNodeNumber.hashCode());
        result = prime * result + ((supportedLCSCapabilitySets == null) ? 0 : supportedLCSCapabilitySets.hashCode());
        result = prime * result + ((mmeName == null) ? 0 : mmeName.hashCode());
        result = prime * result + ((aaaServerName == null) ? 0 : aaaServerName.hashCode());
        result = prime * result + ((sgsnName == null) ? 0 : sgsnName.hashCode());
        result = prime * result + ((sgsnRealm == null) ? 0 : sgsnRealm.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LCSLocationInfoImpl other = (LCSLocationInfoImpl) obj;
        if (additionalLCSCapabilitySets == null) {
            if (other.additionalLCSCapabilitySets != null)
                return false;
        } else if (!additionalLCSCapabilitySets.equals(other.additionalLCSCapabilitySets))
            return false;
        if (additionalNumber == null) {
            if (other.additionalNumber != null)
                return false;
        } else if (!additionalNumber.equals(other.additionalNumber))
            return false;
        if (extensionContainer == null) {
            if (other.extensionContainer != null)
                return false;
        } else if (!extensionContainer.equals(other.extensionContainer))
            return false;
        if (gprsNodeIndicator != other.gprsNodeIndicator) {
            return false;
        }
        if (lmsi == null) {
            if (other.lmsi != null)
                return false;
        } else if (!lmsi.equals(other.lmsi))
            return false;
        if (networkNodeNumber == null) {
            if (other.networkNodeNumber != null)
                return false;
        } else if (!networkNodeNumber.equals(other.networkNodeNumber))
            return false;
        if (supportedLCSCapabilitySets == null) {
            if (other.supportedLCSCapabilitySets != null)
                return false;
        } else if (!supportedLCSCapabilitySets.equals(other.supportedLCSCapabilitySets))
            return false;
        if (mmeName == null) {
            if (other.mmeName != null)
                return false;
        } else if (!mmeName.equals(other.mmeName))
            return false;
        if (aaaServerName == null) {
            if (other.aaaServerName != null)
                return false;
        } else if (!aaaServerName.equals(other.aaaServerName))
            return false;
        if (sgsnName == null) {
            if (other.sgsnName != null)
                return false;
        } else if (!sgsnName.equals(other.sgsnName))
            return false;
        if (sgsnRealm == null) {
            if (other.sgsnRealm != null)
                return false;
        } else if (!sgsnRealm.equals(other.sgsnRealm))
            return false;
        return true;
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
            sb.append(", gprsNodeIndicator=");
        }
        if (this.additionalNumber != null) {
            sb.append(", additionalNumber=");
            sb.append(this.additionalNumber);
        }
        if (this.supportedLCSCapabilitySets != null) {
            sb.append(", supportedLCSCapabilitySets=");
            sb.append(this.supportedLCSCapabilitySets);
        }
        if (this.additionalLCSCapabilitySets != null) {
            sb.append(", additionalLCSCapabilitySets=");
            sb.append(this.additionalLCSCapabilitySets);
        }
        if (this.mmeName != null) {
            sb.append(", mmeName=");
            sb.append(this.mmeName);
        }
        if (this.aaaServerName != null) {
            sb.append(", aaaServerName=");
            sb.append(this.aaaServerName);
        }
        if (this.sgsnName != null) {
            sb.append(", sgsnName=");
            sb.append(this.sgsnName);
        }
        if (this.sgsnRealm != null) {
            sb.append(", sgsnRealm=");
            sb.append(this.sgsnRealm);
        }

        sb.append("]");

        return sb.toString();
    }
}
