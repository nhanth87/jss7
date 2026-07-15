package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.EUtranCgi;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GeodeticInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation5GS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NRCellGlobalId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NRTAId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TAId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TimeZone;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.FQDN;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.PlmnIdImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.FQDNImpl;

import java.io.IOException;

@JacksonXmlRootElement(localName = "locationInformation5GSImpl")
public class LocationInformation5GSImpl extends SequenceBase implements LocationInformation5GS {

    public static final String _PrimitiveName = "LocationInformation5GS";

    public static final int _TAG_NR_CGI = 0;
    public static final int _TAG_EUTRAN_CGI = 1;
    public static final int _TAG_GEOGRAPHICAL_INFO = 2;
    public static final int _TAG_GEODETIC_INFO = 3;
    public static final int _TAG_AMF_ADDRESS = 4;
    public static final int _TAG_TAI = 5;
    public static final int _TAG_CURRENT_LOCATION_RETRIEVED = 6;
    public static final int _TAG_AGE_OF_LOCATION_INFO = 7;
    public static final int _TAG_VPLMN_ID = 8;
    public static final int _TAG_LOCAL_TIME_ZONE = 9;
    public static final int _TAG_RAT_TYPE = 10;
    public static final int _TAG_EXTENSION_CONTAINER = 11;
    public static final int _TAG_NR_TA_ID = 12;

    private static final String NR_CGI = "nrCellGlobalIdentity";
    private static final String EUTRAN_CGI = "e-utranCellGlobalIdentity";
    private static final String GEOGRAPHICAL_INFO = "geographicalInformation";
    private static final String GEODETIC_INFO = "geodeticInformation";
    private static final String AMF_ADDRESS = "amf-address";
    private static final String TAI = "trackingAreaIdentity";
    private static final String CURRENT_LOCATION_RETRIEVED = "currentLocationRetrieved";
    private static final String AGE_OF_LOCATION_INFO = "ageOfLocationInformation";
    private static final String VPLMN_ID = "vplmnId";
    private static final String LOCAL_TIME_ZONE = "localtimeZone";
    private static final String RAT_TYPE = "rat-Type";
    private static final String EXTENSION_CONTAINER = "extensionContainer";
    private static final String NR_TA_ID = "nrTrackingAreaIdentity";

    private NRCellGlobalId nrCellGlobalIdentity = null;
    private EUtranCgi eUtranCellGlobalIdentity = null;
    private GeographicalInformation geographicalInformation = null;
    private GeodeticInformation geodeticInformation = null;
    private FQDN amfAddress = null;
    private TAId trackingAreaIdentity = null;
    private boolean currentLocationRetrieved = false;
    private Integer ageOfLocationInformation = null;
    private PlmnId vplmnId = null;
    private TimeZone localTimeZone = null;
    private UsedRATType ratType = null;
    private MAPExtensionContainer extensionContainer = null;
    private NRTAId nrTrackingAreaIdentity = null;

    public LocationInformation5GSImpl() {
        super(_PrimitiveName);
    }

    /**
     * @param nrCellGlobalIdentity 5G NR Cell Global Identity of the target subscriber as described in 3GPP TS 38.413
     * @param eUtranCellGlobalIdentity LTE Cell Global Identity of the target subscriber as described in 3GPP TS 29.118
     * @param geographicalInformation Geographical information of the target subscriber as defined in 3GPP TS 23.032
     * @param geodeticInformation Geodetic information of the target subscriber as defined in 3GPP TS 23.032
     * @param amfAddress FQDN of the 5GS AMF at which the target subscriber is attached to
     * @param trackingAreaIdentity LTE Tracking Area Identity of the target subscriber as described in 3GPP TS 29.118
     * @param currentLocationRetrieved indication if the location has been retrieved after paging the of the target subscriber's user equipment
     * @param ageOfLocationInformation age of the retrieved location information in minutes
     * @param vplmnId PLMN identity of visited network the target subscriber is currently roaming at
     * @param localTimeZone time zone where the target subscriber is currently roaming at
     * @param ratType current Radio Access Technology providing service to the target subscriber
     * @param extensionContainer MAP extension container
     * @param nrTrackingAreaIdentity 5G NR Tracking Area Identity of the target subscriber as described in 3GPP TS 38.413
     */
    public LocationInformation5GSImpl(NRCellGlobalId nrCellGlobalIdentity, EUtranCgi eUtranCellGlobalIdentity,
            GeographicalInformation geographicalInformation, GeodeticInformation geodeticInformation, FQDN amfAddress,
            TAId trackingAreaIdentity, boolean currentLocationRetrieved, Integer ageOfLocationInformation, PlmnId vplmnId,
            TimeZone localTimeZone, UsedRATType ratType, MAPExtensionContainer extensionContainer, NRTAId nrTrackingAreaIdentity) {
        super(_PrimitiveName);
        this.nrCellGlobalIdentity = nrCellGlobalIdentity;
        this.eUtranCellGlobalIdentity = eUtranCellGlobalIdentity;
        this.geographicalInformation = geographicalInformation;
        this.geodeticInformation = geodeticInformation;
        this.amfAddress = amfAddress;
        this.trackingAreaIdentity = trackingAreaIdentity;
        this.currentLocationRetrieved = currentLocationRetrieved;
        this.ageOfLocationInformation = ageOfLocationInformation;
        this.vplmnId = vplmnId;
        this.localTimeZone = localTimeZone;
        this.ratType = ratType;
        this.extensionContainer = extensionContainer;
        this.nrTrackingAreaIdentity = nrTrackingAreaIdentity;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformation5GPS#getNRCellGlobalId()
     */
    public NRCellGlobalId getNRCellGlobalId() {
        return this.nrCellGlobalIdentity;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformation5GPS#getNRCellGlobalId()
     */
    public EUtranCgi getEUtranCgi() {
        return this.eUtranCellGlobalIdentity;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformation5GPS#getGeographicalInformation()
     */
    public GeographicalInformation getGeographicalInformation() {
        return this.geographicalInformation;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformation5GPS#getGeodeticInformation()
     */
    public GeodeticInformation getGeodeticInformation() {
        return this.geodeticInformation;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformation5GPS#getAMFAddress()
     */
    public FQDN getAMFAddress() {
        return this.amfAddress;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformation5GPS#getTAId()
     */
    public TAId getTAId() {
        return this.trackingAreaIdentity;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformation5GPS#isCurrentLocationRetrieved()
     */
    public boolean isCurrentLocationRetrieved() {
        return this.currentLocationRetrieved;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformation5GPS#getAgeOfLocationInformation()
     */
    public Integer getAgeOfLocationInformation() {
        return this.ageOfLocationInformation;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformation5GPS#getVPlmnId()
     */
    public PlmnId getVPlmnId() {
        return this.vplmnId;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformation5GPS#getLocalTimeZone()
     */
    public TimeZone getLocalTimeZone() {
        return this.localTimeZone;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformation5GPS#getUsedRATType()
     */
    public UsedRATType getUsedRATType() {
        return this.ratType;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformation5GPS#getExtensionContainer()
     */
    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
     * LocationInformation5GPS#getExtensionContainer()
     */
    public NRTAId getNRTAId() {
        return this.nrTrackingAreaIdentity;
    }

    protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.eUtranCellGlobalIdentity = null;
        this.trackingAreaIdentity = null;
        this.extensionContainer = null;
        this.geographicalInformation = null;
        this.geodeticInformation = null;
        this.currentLocationRetrieved = false;
        this.ageOfLocationInformation = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {

                switch (tag) {
                    case _TAG_NR_CGI:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " nrCellGlobalIdentity: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.nrCellGlobalIdentity = new NRCellGlobalIdImpl();
                        ((NRCellGlobalIdImpl) this.nrCellGlobalIdentity).decodeAll(ais);
                        break;
                    case _TAG_EUTRAN_CGI:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " eUtranCellGlobalIdentity: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.eUtranCellGlobalIdentity = new EUtranCgiImpl();
                        ((EUtranCgiImpl) this.eUtranCellGlobalIdentity).decodeAll(ais);
                        break;
                    case _TAG_GEOGRAPHICAL_INFO:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " geographicalInformation: Parameter is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.geographicalInformation = new GeographicalInformationImpl();
                        ((GeographicalInformationImpl) this.geographicalInformation).decodeAll(ais);
                        break;
                    case _TAG_GEODETIC_INFO:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " geodeticInformation: Parameter is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.geodeticInformation = new GeodeticInformationImpl();
                        ((GeodeticInformationImpl) this.geodeticInformation).decodeAll(ais);
                        break;
                    case _TAG_AMF_ADDRESS:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " amfAddress: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.amfAddress = new FQDNImpl();
                        ((FQDNImpl) this.amfAddress).decodeAll(ais);
                        break;
                    case _TAG_TAI:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " trackingAreaIdentity: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.trackingAreaIdentity = new TAIdImpl();
                        ((TAIdImpl) this.trackingAreaIdentity).decodeAll(ais);
                        break;
                    case _TAG_CURRENT_LOCATION_RETRIEVED:
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException(
                                    "Error while decoding LocationInformation: Parameter [currentLocationRetrieved    [8] NULL ] not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        ais.readNull();
                        this.currentLocationRetrieved = true;
                        break;
                    case _TAG_AGE_OF_LOCATION_INFO:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " ageOfLocationInformation: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.ageOfLocationInformation = (int) ais.readInteger();
                        break;
                    case _TAG_VPLMN_ID:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " mmeName: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.vplmnId = new PlmnIdImpl();
                        ((PlmnIdImpl) this.vplmnId).decodeAll(ais);
                        break;
                    case _TAG_LOCAL_TIME_ZONE:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " mmeName: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.localTimeZone = new TimeZoneImpl();
                        ((TimeZoneImpl) this.localTimeZone).decodeAll(ais);
                        break;
                    case _TAG_RAT_TYPE:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " mmeName: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.ratType = UsedRATType.getInstance((int) ais.readInteger());
                        break;
                    case _TAG_EXTENSION_CONTAINER:
                        if (ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " extensionContainer: Parameter is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.extensionContainer = new MAPExtensionContainerImpl();
                        ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                        break;
                    case _TAG_NR_TA_ID:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + " extensionContainer: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.nrTrackingAreaIdentity = new NRTAIdImpl();
                        ((NRTAIdImpl) this.nrTrackingAreaIdentity).decodeAll(ais);
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

            if (this.nrCellGlobalIdentity != null)
                ((NRCellGlobalIdImpl) this.nrCellGlobalIdentity).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_NR_CGI);

            if (this.eUtranCellGlobalIdentity != null)
                ((EUtranCgiImpl) this.eUtranCellGlobalIdentity).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_EUTRAN_CGI);

            if (this.geographicalInformation != null)
                ((GeographicalInformationImpl) this.geographicalInformation).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_GEOGRAPHICAL_INFO);

            if (this.geodeticInformation != null)
                ((GeodeticInformationImpl) this.geodeticInformation).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_GEODETIC_INFO);

            if (this.amfAddress != null)
                ((FQDNImpl) this.amfAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_AMF_ADDRESS);

            if (this.trackingAreaIdentity != null)
                ((TAIdImpl) this.trackingAreaIdentity).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_TAI);

            if (this.currentLocationRetrieved) {
                try {
                    asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_CURRENT_LOCATION_RETRIEVED);
                } catch (IOException e) {
                    throw new MAPException("IOException when encoding " + _PrimitiveName + "currentLocationRetrieved: ", e);
                } catch (AsnException e) {
                    throw new MAPException("ASNException when encoding " + _PrimitiveName + "currentLocationRetrieved: ", e);
                }
            }

            if (ageOfLocationInformation != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_AGE_OF_LOCATION_INFO, ageOfLocationInformation);

            if (vplmnId != null)
                ((PlmnIdImpl) this.vplmnId).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_VPLMN_ID);

            if (localTimeZone != null)
                ((TimeZoneImpl) this.localTimeZone).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_LOCAL_TIME_ZONE);

            if (this.ratType != null) {
                try {
                    asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_RAT_TYPE, this.ratType.getCode());
                } catch (IOException e) {
                    throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter ratType", e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter ratType", e);
                }
            }

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_EXTENSION_CONTAINER);

            if (nrTrackingAreaIdentity != null)
                ((NRTAIdImpl) this.nrTrackingAreaIdentity).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_NR_TA_ID);

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

        if (this.nrCellGlobalIdentity != null) {
            sb.append("nrCellGlobalIdentity=");
            sb.append(this.nrCellGlobalIdentity);
        }

        if (this.eUtranCellGlobalIdentity != null) {
            sb.append(", eUtranCellGlobalIdentity=");
            sb.append(this.eUtranCellGlobalIdentity);
        }

        if (this.geographicalInformation != null) {
            sb.append(", geographicalInformation=");
            sb.append(this.geographicalInformation);
        }

        if (this.geodeticInformation != null) {
            sb.append(", geodeticInformation=");
            sb.append(this.geodeticInformation);
        }

        if (this.amfAddress != null) {
            sb.append(", amfAddress=");
            sb.append(this.amfAddress);
        }

        if (this.trackingAreaIdentity != null) {
            sb.append(", trackingAreaIdentity=");
            sb.append(this.trackingAreaIdentity);
        }

        if (currentLocationRetrieved) {
            sb.append(", currentLocationRetrieved");
        }

        if (this.ageOfLocationInformation != null) {
            sb.append(", ageOfLocationInformation=");
            sb.append(this.ageOfLocationInformation);
        }

        if (this.vplmnId != null) {
            sb.append(", vplmnId=");
            sb.append(this.vplmnId);
        }

        if (this.localTimeZone != null) {
            sb.append(", localTimeZone=");
            sb.append(this.localTimeZone);
        }

        if (this.ratType != null) {
            sb.append(", ratType=");
            sb.append(this.ratType);
        }

        if (this.extensionContainer != null) {
            sb.append(", extensionContainer=");
            sb.append(this.extensionContainer);
        }

        if (this.nrTrackingAreaIdentity != null) {
            sb.append(", nrTrackingAreaIdentity=");
            sb.append(this.nrTrackingAreaIdentity);
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * XML Serialization/Deserialization
     */

}
