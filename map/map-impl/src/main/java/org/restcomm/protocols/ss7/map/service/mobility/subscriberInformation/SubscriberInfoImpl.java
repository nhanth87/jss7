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
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.Time;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.DaylightSavingTime;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GPRSMSClass;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.IMSVoiceOverPsSessionsIndication;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation5GS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationEPS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationGPRS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MNPInfoRes;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MSClassmark2;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.PSSubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TimeZone;
import org.restcomm.protocols.ss7.map.primitives.IMEIImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.TimeImpl;

/**
 * @author amit bhayani
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
@JacksonXmlRootElement(localName = "subscriberInfoImpl")
public class SubscriberInfoImpl implements SubscriberInfo, MAPAsnPrimitive {

    public static final String _PrimitiveName = "SubscriberInfo";

    public static final int _TAG_locationInformation = 0;
    public static final int _TAG_subscriberState = 1;
    public static final int _TAG_extensionContainer = 2;
    public static final int _TAG_locationInformationGPRS = 3;
    public static final int _TAG_psSubscriberState = 4;
    public static final int _TAG_imei = 5;
    public static final int _TAG_msclassmark2 = 6;
    public static final int _TAG_gprsMSClass = 7;
    public static final int _TAG_mnpInfoRes = 8;
    public static final int _TAG_imsVoiceOverPSSessionsIndication = 9;
    public static final int _TAG_lastUEActivityTime =10;
    public static final int _TAG_lastRATType = 11;
    public static final int _TAG_epsSubscriberState = 12;
    public static final int _TAG_locationInformationEPS = 13;
    public static final int _TAG_timeZone = 14;
    public static final int _TAG_daylightSavingTime = 15;
    public static final int _TAG_locationInformation5GS = 16;

    private static final String LOCATION_INFORMATION = "locationInformation";
    private static final String SUBSCRIBER_STATE = "subscriberState";
    private static final String EXTENSION_CONTAINER = "extensionContainer";
    private static final String LOCATION_INFORMATION_GPRS = "locationInformationGRPS";
    private static final String PS_SUBSCRIBER_STATE = "psSubscriberState";
    private static final String IMEI = "imei";
    private static final String MS_CLASSMARK_2 = "msClassmark2";
    private static final String GPRS_MS_CLASS = "gprsMSClass";
    private static final String MNP_INFO_RES = "mnpInfoRes";
    private static final String IMS_VOICE_OVER_PS_SESSION_INDICATION = "imsVoiceOverPSSessionsIndication";
    private static final String LAST_UE_ACTIVITY_TIME = "lastUEActivityTime";
    private static final String LAST_RAT_TYPE = "lastRATType";
    private static final String EPS_SUBSCRIBER_STATE = "epsSubscriberState";
    private static final String LOCATION_INFORMATION_EPS = "locationInformationEPS";
    private static final String TIME_ZONE = "timeZone";
    private static final String DAYLIGHT_SAVING_TIME = "daylightSavingTime";
    private static final String LOCATION_INFORMATION_5GS = "locationInformation5GS";

    private LocationInformation locationInformation = null;
    private SubscriberState subscriberState = null;
    private MAPExtensionContainer extensionContainer = null;
    private LocationInformationGPRS locationInformationGPRS = null;
    private PSSubscriberState psSubscriberState = null;
    private IMEI imei = null;
    private MSClassmark2 msClassmark2 = null;
    private GPRSMSClass gprsMSClass = null;
    private MNPInfoRes mnpInfoRes = null;
    private IMSVoiceOverPsSessionsIndication imsVoiceOverPsSessionsIndication = null;
    private Time lastUEActivityTime = null;
    private UsedRATType lastRATType = null;
    private PSSubscriberState epsSubscriberState = null;
    private LocationInformationEPS locationInformationEPS = null;
    private TimeZone timeZone = null;
    private DaylightSavingTime daylightSavingTime = null;
    private LocationInformation5GS locationInformation5GS = null;

    public SubscriberInfoImpl() {
    }

    public SubscriberInfoImpl(LocationInformation locationInformation, SubscriberState subscriberState,
            MAPExtensionContainer extensionContainer, LocationInformationGPRS locationInformationGPRS,
            PSSubscriberState psSubscriberState, IMEI imei, MSClassmark2 msClassmark2, GPRSMSClass gprsMSClass,
            MNPInfoRes mnpInfoRes, IMSVoiceOverPsSessionsIndication imsVoiceOverPsSessionsIndication,
            Time lastUEActivityTime, UsedRATType lastRATType, PSSubscriberState epsSubscriberState,
            LocationInformationEPS locationInformationEPS, TimeZone timeZone, DaylightSavingTime daylightSavingTime,
            LocationInformation5GS locationInformation5GS) {
        this.locationInformation = locationInformation;
        this.subscriberState = subscriberState;
        this.extensionContainer = extensionContainer;
        this.locationInformationGPRS = locationInformationGPRS;
        this.psSubscriberState = psSubscriberState;
        this.imei = imei;
        this.msClassmark2 = msClassmark2;
        this.gprsMSClass = gprsMSClass;
        this.mnpInfoRes = mnpInfoRes;
        this.imsVoiceOverPsSessionsIndication = imsVoiceOverPsSessionsIndication;
        this.lastUEActivityTime = lastUEActivityTime;
        this.lastRATType = lastRATType;
        this.epsSubscriberState = epsSubscriberState;
        this.locationInformationEPS = locationInformationEPS;
        this.timeZone = timeZone;
        this.daylightSavingTime = daylightSavingTime;
        this.locationInformation5GS = locationInformation5GS;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getLocationInformation()
     */
    public LocationInformation getLocationInformation() {
        return this.locationInformation;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getSubscriberState()
     */
    public SubscriberState getSubscriberState() {
        return this.subscriberState;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getExtensionContainer()
     */
    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getLocationInformationGPRS()
     */
    public LocationInformationGPRS getLocationInformationGPRS() {
        return this.locationInformationGPRS;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getPSSubscriberState()
     */
    public PSSubscriberState getPSSubscriberState() {
        return this.psSubscriberState;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getIMEI()
     */
    public IMEI getIMEI() {
        return this.imei;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getMSClassmark2()
     */
    public MSClassmark2 getMSClassmark2() {
        return this.msClassmark2;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getGPRSMSClass()
     */
    public GPRSMSClass getGPRSMSClass() {
        return this.gprsMSClass;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getMNPInfoRes()
     */
    public MNPInfoRes getMNPInfoRes() {
        return this.mnpInfoRes;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getIMSVoiceOverPsSessionsIndication()
     */
    public IMSVoiceOverPsSessionsIndication getIMSVoiceOverPsSessionsIndication() {
        return this.imsVoiceOverPsSessionsIndication;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getLastUEActivityTime()
     */
    public Time getLastUEActivityTime() {
        return this.lastUEActivityTime;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getLastRATType()
     */
    public UsedRATType getLastRATType() {
        return this.lastRATType;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getEPSSubscriberState()
     */
    public PSSubscriberState getEPSSubscriberState() {
        return this.epsSubscriberState;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getEPSSubscriberState()
     */
    public LocationInformationEPS getLocationInformationEPS() {
        return this.locationInformationEPS;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getTimeZone()
     */
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getDaylightSavingTime()
     */
    public DaylightSavingTime getDaylightSavingTime() {
        return this.daylightSavingTime;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.
     * SubscriberInfo#getLocationInformation5GS()
     */
    public LocationInformation5GS getLocationInformation5GS() {
        return this.locationInformation5GS;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#getTag()
     */
    public int getTag() throws MAPException {
        return Tag.SEQUENCE;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#getTagClass()
     */
    public int getTagClass() {
        return Tag.CLASS_UNIVERSAL;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#getIsPrimitive
     * ()
     */
    public boolean getIsPrimitive() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#decodeAll(
     * org.mobicents.protocols.asn.AsnInputStream)
     */
    public void decodeAll(AsnInputStream ansIS) throws MAPParsingComponentException {
        try {
            int length = ansIS.readLength();
            this._decode(ansIS, length);
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
     * org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#decodeData
     * (org.mobicents.protocols.asn.AsnInputStream, int)
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

    private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.locationInformation = null;
        this.subscriberState = null;
        this.extensionContainer = null;
        this.locationInformationGPRS = null;
        this.psSubscriberState = null;
        this.imei = null;
        this.msClassmark2 = null;
        this.gprsMSClass = null;
        this.mnpInfoRes = null;
        this.imsVoiceOverPsSessionsIndication = null;
        this.lastUEActivityTime = null;
        this.lastRATType = null;
        this.epsSubscriberState = null;
        this.locationInformationEPS = null;
        this.timeZone = null;
        this.daylightSavingTime = null;
        this.locationInformation5GS = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();
            if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                switch (tag) {
                case _TAG_locationInformation:
                    if (ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".locationInformation: Parameter is primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.locationInformation = new LocationInformationImpl();
                    ((LocationInformationImpl) this.locationInformation).decodeAll(ais);
                    break;
                case _TAG_subscriberState:
                    this.subscriberState = new SubscriberStateImpl();
                    AsnInputStream ais2 = ais.readSequenceStream();
                    ais2.readTag();
                    ((SubscriberStateImpl) this.subscriberState).decodeAll(ais2);
                    break;
                case _TAG_extensionContainer:
                    if (ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".MAPExtensionContainer: Parameter is primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    extensionContainer = new MAPExtensionContainerImpl();
                    ((MAPExtensionContainerImpl) extensionContainer).decodeAll(ais);
                    break;
                case _TAG_locationInformationGPRS:
                    if (ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".locationInformationGPRS: Parameter is primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    locationInformationGPRS = new LocationInformationGPRSImpl();
                    ((LocationInformationGPRSImpl) locationInformationGPRS).decodeAll(ais);
                    break;
                case _TAG_psSubscriberState:
                    if (ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".psSubscriberState: Parameter is primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.psSubscriberState = new PSSubscriberStateImpl();
                    AsnInputStream ais3 = ais.readSequenceStream();
                    ais3.readTag();
                    ((PSSubscriberStateImpl) this.psSubscriberState).decodeAll(ais3);
                    break;
                case _TAG_imei:
                    if (!ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".imei: Parameter is not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    imei = new IMEIImpl();
                    ((IMEIImpl) imei).decodeAll(ais);
                    break;
                case _TAG_msclassmark2:
                    if (!ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".msClassmark2: Parameter is not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    msClassmark2 = new MSClassmark2Impl();
                    ((MSClassmark2Impl) msClassmark2).decodeAll(ais);
                    break;
                case _TAG_gprsMSClass:
                    if (ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".gprsMSClass: Parameter is primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    gprsMSClass = new GPRSMSClassImpl();
                    ((GPRSMSClassImpl) gprsMSClass).decodeAll(ais);
                    break;
                case _TAG_mnpInfoRes:
                    if (ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".mnpInfoRes: Parameter is primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    mnpInfoRes = new MNPInfoResImpl();
                    ((MNPInfoResImpl) mnpInfoRes).decodeAll(ais);
                    break;
                    case _TAG_imsVoiceOverPSSessionsIndication:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ".imsVoiceOverPsSessionsIndication: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        imsVoiceOverPsSessionsIndication = IMSVoiceOverPsSessionsIndication.getInstance((int) ais.readInteger());
                        break;
                    case _TAG_lastUEActivityTime:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ".lastUEActivityTime: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        lastUEActivityTime = new TimeImpl();
                        ((TimeImpl) lastUEActivityTime).decodeAll(ais);
                        break;
                    case _TAG_lastRATType:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ".lastRATType: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        lastRATType = UsedRATType.getInstance((int) ais.readInteger());
                        break;
                    case _TAG_epsSubscriberState:
                        if (ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ".epsSubscriberState: Parameter is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.epsSubscriberState = new PSSubscriberStateImpl();
                        AsnInputStream ais4 = ais.readSequenceStream();
                        ais4.readTag();
                        ((PSSubscriberStateImpl) this.epsSubscriberState).decodeAll(ais4);
                        break;
                    case _TAG_locationInformationEPS:
                        if (ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ".locationInformationEPS: Parameter is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        locationInformationEPS = new LocationInformationEPSImpl();
                        ((LocationInformationEPSImpl) locationInformationEPS).decodeAll(ais);
                        break;
                    case _TAG_timeZone:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ".timeZone: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        timeZone = new TimeZoneImpl();
                        ((TimeZoneImpl) timeZone).decodeAll(ais);
                        break;
                    case _TAG_daylightSavingTime:
                        if (!ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ".daylightSavingTime: Parameter is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        daylightSavingTime = DaylightSavingTime.getInstance((int) ais.readInteger());
                        break;
                    case _TAG_locationInformation5GS:
                        if (ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ".locationInformation5GS: Parameter is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        locationInformation5GS = new LocationInformation5GSImpl();
                        ((LocationInformation5GSImpl) locationInformation5GS).decodeAll(ais);
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
     * @see
     * org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#encodeAll(
     * org.mobicents.protocols.asn.AsnOutputStream)
     */
    public void encodeAll(AsnOutputStream asnOutputStream) throws MAPException {
        this.encodeAll(asnOutputStream, this.getTagClass(), this.getTag());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#encodeAll(
     * org.mobicents.protocols.asn.AsnOutputStream, int, int)
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
     * @see
     * org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#encodeData
     * (org.mobicents.protocols.asn.AsnOutputStream)
     */
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        try {

            if (this.locationInformation != null)
                ((LocationInformationImpl) this.locationInformation).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_locationInformation);

            if (this.subscriberState != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_subscriberState);
                int pos = asnOutputStream.StartContentDefiniteLength();
                ((SubscriberStateImpl) this.subscriberState).encodeAll(asnOutputStream);
                asnOutputStream.FinalizeContent(pos);
            }

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_extensionContainer);

            if (this.locationInformationGPRS != null)
                ((LocationInformationGPRSImpl) this.locationInformationGPRS).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_locationInformationGPRS);

            if (this.psSubscriberState != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_psSubscriberState);
                int pos = asnOutputStream.StartContentDefiniteLength();
                ((PSSubscriberStateImpl) this.psSubscriberState).encodeAll(asnOutputStream);
                asnOutputStream.FinalizeContent(pos);
            }

            if (this.imei != null)
                ((IMEIImpl) this.imei).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_imei);

            if (this.msClassmark2 != null)
                ((MSClassmark2Impl) this.msClassmark2).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_msclassmark2);

            if (this.gprsMSClass != null)
                ((GPRSMSClassImpl) this.gprsMSClass).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_gprsMSClass);

            if (this.mnpInfoRes != null)
                ((MNPInfoResImpl) this.mnpInfoRes).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_mnpInfoRes);

            if (this.imsVoiceOverPsSessionsIndication != null) {
                try {
                    asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_imsVoiceOverPSSessionsIndication,
                            this.imsVoiceOverPsSessionsIndication.getCode());
                } catch (IOException e) {
                    throw new MAPException("IOException while encoding " + _PrimitiveName + ".imsVoiceOverPsSessionsIndication parameter. ", e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException while encoding " + _PrimitiveName + ".imsVoiceOverPsSessionsIndication parameter. ", e);
                }
            }

            if (this.lastUEActivityTime != null)
                ((TimeImpl) this.lastUEActivityTime).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_lastUEActivityTime);

            if (this.lastRATType != null) {
                try {
                    asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_lastRATType,
                            this.lastRATType.getCode());
                } catch (IOException e) {
                    throw new MAPException("IOException while encoding " + _PrimitiveName + ".lastRATType parameter. ", e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException while encoding " + _PrimitiveName + ".lastRATType parameter. ", e);
                }
            }

            if (this.epsSubscriberState != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_epsSubscriberState);
                int pos = asnOutputStream.StartContentDefiniteLength();
                ((PSSubscriberStateImpl) this.epsSubscriberState).encodeAll(asnOutputStream);
                asnOutputStream.FinalizeContent(pos);
            }

            if (this.locationInformationEPS != null)
                ((LocationInformationEPSImpl) this.locationInformationEPS).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_locationInformationEPS);

            if (this.timeZone != null)
                ((TimeZoneImpl) this.timeZone).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_timeZone);

            if (daylightSavingTime != null) {
                try {
                    asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_daylightSavingTime,
                            this.daylightSavingTime.getCode());
                } catch (IOException e) {
                    throw new MAPException("IOException while encoding " + _PrimitiveName + ".daylightSavingTime parameter. ", e);
                } catch (AsnException e) {
                    throw new MAPException("AsnException while encoding " + _PrimitiveName + ".daylightSavingTime parameter. ", e);
                }
            }

            if (locationInformation5GS != null)
                ((LocationInformation5GSImpl) this.locationInformation5GS).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_locationInformation5GS);

        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.locationInformation != null) {
            sb.append(", locationInformation=");
            sb.append(this.locationInformation);
        }
        if (this.subscriberState != null) {
            sb.append(", subscriberState=");
            sb.append(this.subscriberState);
        }
        if (this.extensionContainer != null) {
            sb.append(", extensionContainer=");
            sb.append(this.extensionContainer);
        }
        if (this.locationInformationGPRS != null) {
            sb.append(", locationInformationGPRS=");
            sb.append(this.locationInformationGPRS);
        }
        if (this.psSubscriberState != null) {
            sb.append(", psSubscriberState=");
            sb.append(this.psSubscriberState);
        }
        if (this.imei != null) {
            sb.append(", imei=");
            sb.append(this.imei);
        }
        if (this.msClassmark2 != null) {
            sb.append(", msClassmark2=");
            sb.append(this.msClassmark2);
        }
        if (this.gprsMSClass != null) {
            sb.append(", gprsMSClass=");
            sb.append(this.gprsMSClass);
        }
        if (this.mnpInfoRes != null) {
            sb.append(", mnpInfoRes=");
            sb.append(this.mnpInfoRes);
        }
        if (this.imsVoiceOverPsSessionsIndication != null) {
            sb.append(", imsVoiceOverPsSessionsIndication=");
            sb.append(this.imsVoiceOverPsSessionsIndication);
        }
        if (this.lastUEActivityTime != null) {
            sb.append(", lastUEActivityTime=");
            sb.append(this.lastUEActivityTime);
        }
        if (this.lastRATType != null) {
            sb.append(", lastRATType=");
            sb.append(this.lastRATType);
        }
        if (this.epsSubscriberState != null) {
            sb.append(", epsSubscriberState=");
            sb.append(this.epsSubscriberState);
        }
        if (this.locationInformationEPS != null) {
            sb.append(", locationInformationEPS=");
            sb.append(this.locationInformationEPS);
        }
        if (this.timeZone != null) {
            sb.append(", timeZone=");
            sb.append(this.timeZone);
        }
        if (this.daylightSavingTime != null) {
            sb.append(", daylightSavingTime=");
            sb.append(this.daylightSavingTime);
        }
        if (this.locationInformation5GS != null) {
            sb.append(", locationInformation5GS=");
            sb.append(this.locationInformation5GS);
        }
        sb.append("]");
        return sb.toString();
    }

}
