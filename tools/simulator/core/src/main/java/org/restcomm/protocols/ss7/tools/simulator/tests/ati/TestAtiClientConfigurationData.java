package org.restcomm.protocols.ss7.tools.simulator.tests.ati;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.DomainType;


/**
*
* @author sergey vetyutnev
*
*/
@JacksonXmlRootElement(localName = "testAtiClientConfigurationData")
public class TestAtiClientConfigurationData {

    protected static final String ADDRESS_NATURE = "addressNature";
    protected static final String NUMBERING_PLAN = "numberingPlan";
    protected static final String SUBSCRIBER_IDENTITY_TYPE = "subscriberIdentityType";
    protected static final String GET_LOCATION_INFORMATION = "getLocationInformation";
    protected static final String GET_SUBSCRIBER_STATE = "getSubscriberState";
    protected static final String GET_CURRENT_LOCATION = "getCurrentLocation";
    protected static final String GET_REQUESTED_DOMAIN = "getRequestedDomain";
    protected static final String GET_IMEI = "getImei";
    protected static final String GET_MS_CLASSMARK = "getMsClassmark";
    protected static final String GET_MNP_REQUESTED_INFO = "getMnpRequestedInfo";
    protected static final String GET_LOCATION_INFORMATION_EPS_SUPPORTED = "getLocationInformationEPSSupported";
    protected static final String GSM_SCF_ADDRESS = "gsmScfAddress";

    private AddressNature addressNature = AddressNature.international_number;
    private NumberingPlan numberingPlan = NumberingPlan.ISDN;
    private boolean subscriberIdentityTypeIsImsi = false;
    private boolean getLocationInformation = false;
    private boolean getSubscriberState = false;
    private boolean getCurrentLocation = false;
    private DomainType getRequestedDomain = null;
    private boolean getImei = false;
    private boolean getMsClassmark = false;
    private boolean getMnpRequestedInfo = false;
    private boolean getLocationInformationEPSSupported = false;
    private String gsmScfAddress = "000";

    public AddressNature getAddressNature() {
        return addressNature;
    }

    public void setAddressNature(AddressNature addressNature) {
        this.addressNature = addressNature;
    }

    public NumberingPlan getNumberingPlan() {
        return numberingPlan;
    }

    public void setNumberingPlan(NumberingPlan numberingPlan) {
        this.numberingPlan = numberingPlan;
    }

    public boolean isSubscriberIdentityTypeIsImsi() {
        return subscriberIdentityTypeIsImsi;
    }

    public void setSubscriberIdentityTypeIsImsi(boolean subscriberIdentityTypeIsImsi) {
        this.subscriberIdentityTypeIsImsi = subscriberIdentityTypeIsImsi;
    }

    public boolean isGetLocationInformation() {
        return getLocationInformation;
    }

    public void setGetLocationInformation(boolean getLocationInformation) {
        this.getLocationInformation = getLocationInformation;
    }

    public boolean isGetSubscriberState() {
        return getSubscriberState;
    }

    public void setGetSubscriberState(boolean getSubscriberState) {
        this.getSubscriberState = getSubscriberState;
    }

    public boolean isGetCurrentLocation() {
        return getCurrentLocation;
    }

    public void setGetCurrentLocation(boolean getCurrentLocation) {
        this.getCurrentLocation = getCurrentLocation;
    }

    public DomainType getGetRequestedDomain() {
        return getRequestedDomain;
    }

    public void setGetRequestedDomain(DomainType getRequestedDomain) {
        this.getRequestedDomain = getRequestedDomain;
    }

    public boolean isGetImei() {
        return getImei;
    }

    public void setGetImei(boolean getImei) {
        this.getImei = getImei;
    }

    public boolean isGetMsClassmark() {
        return getMsClassmark;
    }

    public void setGetMsClassmark(boolean getMsClassmark) {
        this.getMsClassmark = getMsClassmark;
    }

    public boolean isGetMnpRequestedInfo() {
        return getMnpRequestedInfo;
    }

    public void setGetMnpRequestedInfo(boolean getMnpRequestedInfo) {
        this.getMnpRequestedInfo = getMnpRequestedInfo;
    }

    public String getGsmScfAddress() {
        return gsmScfAddress;
    }

    public void setGsmScfAddress(String gsmScfAddress) {
        this.gsmScfAddress = gsmScfAddress;
    }

    public boolean isGetLocationInformationEPSSupported() {
        return getLocationInformationEPSSupported;
    }

    public void setGetLocationInformationEPSSupported(boolean getLocationInformationEPSSupported) {
        this.getLocationInformationEPSSupported = getLocationInformationEPSSupported;
    }


}
