package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.IOException;


import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.DiameterIdentity;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.EUtranCgi;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GeodeticInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationEPS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TAId;
import org.restcomm.protocols.ss7.map.primitives.DiameterIdentityImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;

/**
 * @author amit bhayani
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
@JacksonXmlRootElement(localName = "locationInformationEPSImpl")
public class LocationInformationEPSImpl extends SequenceBase implements LocationInformationEPS {

    public static final String _PrimitiveName = "LocationInformationEPS";
    public static final int _TAG_eUtranCellGlobalIdentity = 0;
    public static final int _TAG_trackingAreaIdentity = 1;
    public static final int _TAG_extensionContainer = 2;
    public static final int _TAG_geographicalInformation = 3;
    public static final int _TAG_geodeticInformation = 4;
    public static final int _TAG_currentLocationRetrieved = 5;
    public static final int _TAG_ageOfLocationInformation = 6;
    public static final int _TAG_mme_Name = 7;

    private static final String E_UTRAN_CELL_GLOBAL_IDENTITY = "eUtranCellGlobalIdentity";
    private static final String TRACKING_AREA_IDENTITY = "trackingAreaIdentity";
    private static final String EXTENSION_CONTAINER = "extensionContainer";
    private static final String GEOGRAPHICAL_INFORMATION = "geographicalInformation";
    private static final String GEODETIC_INFORMATION = "geodeticInformation";
    private static final String CURRENT_LOCATION_RETRIEVED = "currentLocationRetrieved";
    private static final String AGE_OF_LOCATION_INFORMATION = "ageOfLocationInformation";
    private static final String MME_NAME = "mmeName";

    private EUtranCgi eUtranCellGlobalIdentity = null;
    private TAId trackingAreaIdentity = null;
    private MAPExtensionContainer extensionContainer = null;
    private GeographicalInformation geographicalInformation = null;
    private GeodeticInformation geodeticInformation = null;
    private boolean currentLocationRetrieved = false;
    private Integer ageOfLocationInformation = null;
    private DiameterIdentity mmeName = null;

    /**
     *
     */
    public LocationInformationEPSImpl() {
        super(_PrimitiveName);
    }

    /**
     * @param eUtranCellGlobalIdentity LTE Cell Global Identity of the target subscriber as described in 3GPP TS 29.118
     * @param trackingAreaIdentity Tracking Area Identity of the target subscriber as described in 3GPP TS 29.118
     * @param extensionContainer MAP extension container
     * @param geographicalInformation geographical information of the target subscriber as defined in 3GPP TS 23.032
     * @param geodeticInformation geodetic information of the target subscriber as defined in 3GPP TS 23.032
     * @param currentLocationRetrieved indication if the location has been retrieved after paging the of the target subscriber's user equipment
     * @param ageOfLocationInformation age of the retrieved location information in minutes
     * @param mmeName MME Diameter identity at which the target subscriber is attached to
     */
    public LocationInformationEPSImpl(EUtranCgi eUtranCellGlobalIdentity, TAId trackingAreaIdentity,
            MAPExtensionContainer extensionContainer, GeographicalInformation geographicalInformation,
            GeodeticInformation geodeticInformation, boolean currentLocationRetrieved, Integer ageOfLocationInformation,
            DiameterIdentity mmeName) {
        super(_PrimitiveName);

        this.eUtranCellGlobalIdentity = eUtranCellGlobalIdentity;
        this.trackingAreaIdentity = trackingAreaIdentity;
        this.extensionContainer = extensionContainer;
        this.geographicalInformation = geographicalInformation;
        this.geodeticInformation = geodeticInformation;
        this.currentLocationRetrieved = currentLocationRetrieved;
        this.ageOfLocationInformation = ageOfLocationInformation;
        this.mmeName = mmeName;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformationEPS#getEUtranCellGlobalIdentity()
     */
    public EUtranCgi getEUtranCellGlobalIdentity() {
        return this.eUtranCellGlobalIdentity;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation. LocationInformationEPS#getTrackingAreaIdentity()
     */
    public TAId getTrackingAreaIdentity() {
        return this.trackingAreaIdentity;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation. LocationInformationEPS#getExtensionContainer()
     */
    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformationEPS#getGeographicalInformation()
     */
    public GeographicalInformation getGeographicalInformation() {
        return this.geographicalInformation;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation. LocationInformationEPS#getGeodeticInformation()
     */
    public GeodeticInformation getGeodeticInformation() {
        return this.geodeticInformation;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformationEPS#getCurrentLocationRetrieved()
     */
    public boolean getCurrentLocationRetrieved() {
        return this.currentLocationRetrieved;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformationEPS#getAgeOfLocationInformation()
     */
    public Integer getAgeOfLocationInformation() {
        return this.ageOfLocationInformation;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation. LocationInformationEPS#getMmeName()
     */
    public DiameterIdentity getMmeName() {
        return this.mmeName;
    }

    protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.eUtranCellGlobalIdentity = null;
        this.trackingAreaIdentity = null;
        this.extensionContainer = null;
        this.geographicalInformation = null;
        this.geodeticInformation = null;
        this.currentLocationRetrieved = false;
        this.ageOfLocationInformation = null;
        this.mmeName = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {

                switch (tag) {
                    case _TAG_eUtranCellGlobalIdentity:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " eUtranCellGlobalIdentity: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.eUtranCellGlobalIdentity = new EUtranCgiImpl();
                        ((EUtranCgiImpl) this.eUtranCellGlobalIdentity).decodeAll(ais);
                        break;
                    case _TAG_trackingAreaIdentity:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " trackingAreaIdentity: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.trackingAreaIdentity = new TAIdImpl();
                        ((TAIdImpl) this.trackingAreaIdentity).decodeAll(ais);
                        break;
                    case _TAG_extensionContainer:
                        if (ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " extensionContainer: Parameter is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.extensionContainer = new MAPExtensionContainerImpl();
                        ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                        break;
                    case _TAG_geographicalInformation:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " geographicalInformation: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.geographicalInformation = new GeographicalInformationImpl();
                        ((GeographicalInformationImpl) this.geographicalInformation).decodeAll(ais);
                        break;
                    case _TAG_geodeticInformation:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " geodeticInformation: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.geodeticInformation = new GeodeticInformationImpl();
                        ((GeodeticInformationImpl) this.geodeticInformation).decodeAll(ais);
                        break;
                    case _TAG_currentLocationRetrieved:
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException(
                                    "Error while decoding LocationInformation: Parameter [currentLocationRetrieved    [8] NULL ] not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        ais.readNull();
                        this.currentLocationRetrieved = true;
                        break;
                    case _TAG_ageOfLocationInformation:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " ageOfLocationInformation: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.ageOfLocationInformation = (int) ais.readInteger();
                        break;
                    case _TAG_mme_Name:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " mmeName: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.mmeName = new DiameterIdentityImpl();
                        ((DiameterIdentityImpl) this.mmeName).decodeAll(ais);
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

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#encodeData (org.mobicents.protocols.asn.AsnOutputStream)
     */
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        try {

            if (this.eUtranCellGlobalIdentity != null)
                ((EUtranCgiImpl) this.eUtranCellGlobalIdentity).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_eUtranCellGlobalIdentity);

            if (this.trackingAreaIdentity != null) {
                ((TAIdImpl) this.trackingAreaIdentity).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_trackingAreaIdentity);
            }

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_extensionContainer);

            if (this.geographicalInformation != null)
                ((GeographicalInformationImpl) this.geographicalInformation).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_geographicalInformation);

            if (this.geodeticInformation != null)
                ((GeodeticInformationImpl) this.geodeticInformation).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_geodeticInformation);

            if (this.currentLocationRetrieved) {
                try {
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_currentLocationRetrieved);
                } catch (IOException e) {
                    throw new MAPException("IOException when encoding " + _PrimitiveName + "currentLocationRetrieved: ", e);
                } catch (AsnException e) {
                    throw new MAPException("ASNException when encoding " + _PrimitiveName + "currentLocationRetrieved: ", e);
                }
            }

            if (ageOfLocationInformation != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_ageOfLocationInformation, ageOfLocationInformation);

            if (this.mmeName != null) {
                ((DiameterIdentityImpl) this.mmeName).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_mme_Name);
            }

        } catch (IOException e) {
            throw new MAPException("IOException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.eUtranCellGlobalIdentity != null) {
            sb.append("eUtranCellGlobalIdentity=");
            sb.append(this.eUtranCellGlobalIdentity);
        }

        if (this.trackingAreaIdentity != null) {
            sb.append(", trackingAreaIdentity=");
            sb.append(this.trackingAreaIdentity);
        }

        if (this.extensionContainer != null) {
            sb.append(", extensionContainer=");
            sb.append(this.extensionContainer);
        }

        if (this.geographicalInformation != null) {
            sb.append(", geographicalInformation=");
            sb.append(this.geographicalInformation);
        }

        if (this.geodeticInformation != null) {
            sb.append(", geodeticInformation=");
            sb.append(this.geodeticInformation);
        }

        if (currentLocationRetrieved) {
            sb.append(", currentLocationRetrieved");
        }

        if (this.ageOfLocationInformation != null) {
            sb.append(", ageOfLocationInformation=");
            sb.append(this.ageOfLocationInformation);
        }

        if (this.mmeName != null) {
            sb.append(", mmeName=");
            sb.append(this.mmeName);
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * XML Serialization/Deserialization
     */


}
