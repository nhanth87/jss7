package org.restcomm.protocols.ss7.map.service.mobility.locationManagement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.DiameterIdentity;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.NetworkNodeDiameterAddress;
import org.restcomm.protocols.ss7.map.primitives.DiameterIdentityImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;

import java.io.IOException;

@JacksonXmlRootElement(localName = "networkNodeDiameterAddressImpl")
public class NetworkNodeDiameterAddressImpl extends SequenceBase implements NetworkNodeDiameterAddress, MAPAsnPrimitive {

    public static final int _ID_diameterName = 0;
    public static final int _ID_diameterRealm = 1;

    public static final String _PrimitiveName = "NetworkNodeDiameterAddress";

    private static final String DIAMETER_NAME = "diameterName";
    private static final String DIAMETER_REALM = "diameterRealm";

    private DiameterIdentity diameterName;
    private DiameterIdentity diameterRealm;

    public NetworkNodeDiameterAddressImpl() {
        super("NetworkNodeDiameterAddress");
    }

    /**
     * @param diameterName (Diameter host name)
     * @param diameterRealm (Diameter host realm)
     */
    public NetworkNodeDiameterAddressImpl(DiameterIdentity diameterName, DiameterIdentity diameterRealm) {
        super("NetworkNodeDiameterAddress");
        this.diameterName = diameterName;
        this.diameterRealm = diameterRealm;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement NetworkNodeDiameterAddress#getDiameterName()
     */
    public DiameterIdentity getDiameterName() {
        return diameterName;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement NetworkNodeDiameterAddress#getDiameterRealm()
     */
    public DiameterIdentity getDiameterRealm() {
        return diameterRealm;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#getTag()
     */
    public int getTag() throws MAPException {
        return Tag.SEQUENCE;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#getTagClass()
     */
    public int getTagClass() {
        return Tag.CLASS_UNIVERSAL;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#getIsPrimitive()
     */
    public boolean getIsPrimitive() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#decodeAll(org.mobicents.protocols.asn.AsnInputStream)
     */
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

    /*
     * (non-Javadoc)
     *
     * @see
     * org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#decodeData(org.mobicents.protocols.asn.AsnInputStream,
     * int)
     */
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

    protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {

        this.diameterName = null;
        this.diameterRealm = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                switch (tag) {
                    case _ID_diameterName:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ".diameterName: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.diameterName = new DiameterIdentityImpl();
                        ((DiameterIdentityImpl) this.diameterName).decodeAll(ais);
                        break;
                    case _ID_diameterRealm:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ".diameterRealm: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.diameterRealm = new DiameterIdentityImpl();
                        ((DiameterIdentityImpl) this.diameterRealm).decodeAll(ais);
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
     * org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#encodeAll(org.mobicents.protocols.asn.AsnOutputStream)
     */
    public void encodeAll(AsnOutputStream asnOutputStream) throws MAPException {
        this.encodeAll(asnOutputStream, this.getTagClass(), this.getTag());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#encodeAll(org.mobicents.protocols.asn.AsnOutputStream,
     * int, int)
     */
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

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#encodeData (org.mobicents.protocols.asn.AsnOutputStream)
     */
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        try {
            if (this.diameterName != null) {
                ((DiameterIdentityImpl) this.diameterName).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_diameterName);
            }

            if (this.diameterRealm != null) {
                ((DiameterIdentityImpl) this.diameterRealm).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _ID_diameterRealm);
            }
        } catch (MAPException e) {
            throw new MAPException("MAPException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((diameterName == null) ? 0 : diameterName.hashCode());
        result = prime * result + ((diameterRealm == null) ? 0 : diameterRealm.hashCode());
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
        NetworkNodeDiameterAddressImpl other = (NetworkNodeDiameterAddressImpl) obj;
        if (diameterName == null) {
            if (other.diameterName != null)
                return false;
        } else if (!diameterName.equals(other.diameterName))
            return false;
        if (diameterRealm == null) {
            if (other.diameterRealm != null)
                return false;
        } else if (!diameterRealm.equals(other.diameterRealm))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.diameterName != null) {
            sb.append(", diameterName=");
            sb.append(this.diameterName);
        }
        if (this.diameterRealm != null) {
            sb.append(", diameterRealm=");
            sb.append(this.diameterRealm);
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * XML Serialization/Deserialization
     */

}
