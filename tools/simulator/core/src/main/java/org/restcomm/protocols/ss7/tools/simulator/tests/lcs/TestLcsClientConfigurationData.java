package org.restcomm.protocols.ss7.tools.simulator.tests.lcs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSEvent;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
@JacksonXmlRootElement(localName = "testLcsClientConfigurationData")
public class TestLcsClientConfigurationData {

    protected static final String NA_ESRD_ADDRESS = "na_esrd_address";
    protected static final String ADDRESS_NATURE = "addressNature";
    protected static final String NUMBERING_PLAN_TYPE = "numberingPlanType";
    protected static final String IMSI = "imsi";
    protected static final String NETWORK_NODE_NUMBER_ADDRESS = "networkNodeNumberAddress";
    protected static final String IMEI = "imei";
    protected static final String HGMLC_ADDRESS = "hgmlcAddress";
    protected static final String MCC = "mcc";
    protected static final String MNC = "mnc";
    protected static final String LAC = "lac";
    protected static final String CELL_ID = "cellId";
    protected static final String LCS_REFERENCE_NUMBER = "lcsReferenceNumber";
    protected static final String AGE_OF_LOCATION_ESTIMATE = "ageOfLocationEstimate";
    protected static final String LCS_EVENT = "lcsEvent";
    protected static final String MSISDN = "msisdn";

    private String networkNodeNumberAddress = "598048";
    private String naESRDAddress = "11114444";
    private String naESRKAddress = "11115555";
    private AddressNature addressNature = AddressNature.international_number;
    private NumberingPlan numberingPlanType = NumberingPlan.ISDN;
    private String imsi = "748010192837465";
    private String msisdn = "59899077937";
    private String imei = "354449063537030";
    private String hgmlcAddress = "200.10.0.1";
    private Integer mcc = 748;
    private Integer mnc = 01;
    private Integer lac = 79010;
    private Integer cellId = 222;
    private Integer lcsReferenceNumber = 111;
    private Integer ageOfLocationEstimate = 1;
    private LCSEvent lcsEvent = LCSEvent.emergencyCallOrigination;

    @JsonProperty(IMEI)
    public String getIMEI() {
        return imei;
    }

    @JsonProperty(IMEI)
    public void setIMEI(String imei) {
        this.imei = imei;
    }

    @JsonProperty(HGMLC_ADDRESS)
    public String getHGMLCAddress() {
        return hgmlcAddress;
    }

    @JsonProperty(HGMLC_ADDRESS)
    public void setHGMLCAddress(String hgmlcAddress) {
        this.hgmlcAddress = hgmlcAddress;
    }

    @JsonProperty(MCC)
    public Integer getMCC() {
        return mcc;
    }

    @JsonProperty(MCC)
    public void setMCC(Integer mcc) {
        this.mcc = mcc;
    }

    @JsonProperty(MNC)
    public Integer getMNC() {
        return mnc;
    }

    @JsonProperty(MNC)
    public void setMNC(Integer mnc) {
        this.mnc = mnc;
    }

    @JsonProperty(LAC)
    public Integer getLAC() {
        return lac;
    }

    @JsonProperty(LAC)
    public void setLAC(Integer lac) {
        this.lac = lac;
    }

    public Integer getCellId() {
        return cellId;
    }

    public void setCellId(Integer cellId) {
        this.cellId = cellId;
    }

    @JsonProperty(LCS_REFERENCE_NUMBER)
    public Integer getLCSReferenceNumber() {
        return lcsReferenceNumber;
    }

    @JsonProperty(LCS_REFERENCE_NUMBER)
    public void setLCSReferenceNumber(Integer lcsReferenceNumber) {
        this.lcsReferenceNumber = lcsReferenceNumber;
    }

    public Integer getAgeOfLocationEstimate() {
        return ageOfLocationEstimate;
    }

    public void setAgeOfLocationEstimate(Integer ageOfLocationEstimate) {
        this.ageOfLocationEstimate = ageOfLocationEstimate;
    }

    @JsonProperty(LCS_EVENT)
    public LCSEvent getLCSEvent() {
        return lcsEvent;
    }

    @JsonProperty(LCS_EVENT)
    public void setLCSEvent(LCSEvent lcsEvent) {
        this.lcsEvent = lcsEvent;
    }

    public void setNetworkNodeNumberAddress(String data) {
        this.networkNodeNumberAddress = data;
    }

    public String getNetworkNodeNumberAddress() {
        return networkNodeNumberAddress;
    }

    public AddressNature getAddressNature() {
        return addressNature;
    }

    public NumberingPlan getNumberingPlanType() {
        return numberingPlanType;
    }

    public void setAddressNature(AddressNature addressNature) {
        this.addressNature = addressNature;
    }

    public void setNumberingPlanType(NumberingPlan numberingPlan) {
        this.numberingPlanType = numberingPlan;
    }

    @JsonProperty(MSISDN)
    public String getMSISDN() {
        return msisdn;
    }

    @JsonProperty(MSISDN)
    public void setMSISDN(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty(IMSI)
    public void setIMSI(String data) {
        this.imsi = data;
    }

    @JsonProperty(IMSI)
    public String getIMSI() {
        return imsi;
    }

    public String getNaESRDAddress() {
        return naESRDAddress;
    }

    public void setNaESRDAddress(String naESRDAddress) {
        this.naESRKAddress = naESRDAddress;
    }

    public String getNaESRKAddress() {
        return naESRDAddress;
    }

    public void setNaESRKAddress(String naESRKAddress) {
        this.naESRKAddress = naESRDAddress;
    }

}
