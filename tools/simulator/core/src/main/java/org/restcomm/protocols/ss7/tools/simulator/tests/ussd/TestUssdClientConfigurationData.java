
package org.restcomm.protocols.ss7.tools.simulator.tests.ussd;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.SRIReaction;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "testUssdClientConfigurationData")
public class TestUssdClientConfigurationData {

    protected static final String MSISDN_ADDRESS = "msisdnAddress";
    protected static final String MSISDN_ADDRESS_NATURE = "msisdnAddressNature";
    protected static final String MSISDN_NUMBERING_PLAN = "msisdnNumberingPlan";
    protected static final String DATA_CODING_SCHEME = "dataCodingScheme";
    protected static final String ALERTING_PATTERN = "alertingPattern";
    protected static final String USSD_CLIENT_ACTION = "ussdClientAction";
    protected static final String AUTO_REQUEST_STRING = "autoRequestString";
    protected static final String AUTO_RESPONSE_STRING = "autoResponseString";
    protected static final String MAX_CONCURENT_DIALOGS = "maxConcurrentDialogs";
    protected static final String ONE_NOTIFICATION_FOR_100_DIALOGS = "oneNotificationFor100Dialogs";
    protected static final String AUTO_RESPONSE_ON_UNSTRUCTURED_SS_REQUESTS = "autoResponseOnUnstructuredSSRequests";
    protected static final String SRI_RESPONSE_IMSI = "sriResponseImsi";
    protected static final String SRI_RESPONSE_VLR = "sriResponseVlr";
    protected static final String SRI_REACTION = "sriReaction";
    protected static final String RETURN_20_PERS_DELIVERY_ERRORS = "return20PersDeliveryErrors";

    protected String msisdnAddress = "";
    protected AddressNature msisdnAddressNature = AddressNature.international_number;
    protected NumberingPlan msisdnNumberingPlan = NumberingPlan.ISDN;
    protected int dataCodingScheme = 0x0F;
    protected int alertingPattern = -1;
    protected String sriResponseImsi = "";
    protected String sriResponseVlr = "";
    protected SRIReaction sriReaction = new SRIReaction(SRIReaction.VAL_RETURN_SUCCESS);
    protected boolean return20PersDeliveryErrors = false;

    protected UssdClientAction ussdClientAction = new UssdClientAction(UssdClientAction.VAL_MANUAL_OPERATION);
    protected String autoRequestString = "???";
    protected String autoResponseString = "";
    protected int maxConcurrentDialogs = 10;
    protected boolean oneNotificationFor100Dialogs = false;
    protected boolean autoResponseOnUnstructuredSSRequests = false;

    public String getMsisdnAddress() {
        return msisdnAddress;
    }

    public void setMsisdnAddress(String msisdnAddress) {
        this.msisdnAddress = msisdnAddress;
    }

    public AddressNature getMsisdnAddressNature() {
        return msisdnAddressNature;
    }

    public void setMsisdnAddressNature(AddressNature msisdnAddressNature) {
        this.msisdnAddressNature = msisdnAddressNature;
    }

    public NumberingPlan getMsisdnNumberingPlan() {
        return msisdnNumberingPlan;
    }

    public void setMsisdnNumberingPlan(NumberingPlan msisdnNumberingPlan) {
        this.msisdnNumberingPlan = msisdnNumberingPlan;
    }

    public int getDataCodingScheme() {
        return dataCodingScheme;
    }

    public void setDataCodingScheme(int dataCodingScheme) {
        this.dataCodingScheme = dataCodingScheme;
    }

    public int getAlertingPattern() {
        return alertingPattern;
    }

    public void setAlertingPattern(int alertingPattern) {
        this.alertingPattern = alertingPattern;
    }

    public UssdClientAction getUssdClientAction() {
        return ussdClientAction;
    }

    public void setUssdClientAction(UssdClientAction ussdClientAction) {
        this.ussdClientAction = ussdClientAction;
    }

    public String getAutoRequestString() {
        return autoRequestString;
    }

    public void setAutoRequestString(String autoRequestString) {
        this.autoRequestString = autoRequestString;
    }

    public String getAutoResponseString() {
        return autoResponseString;
    }

    public void setAutoResponseString(String autoResponseString) {
        this.autoResponseString = autoResponseString;
    }

    public int getMaxConcurrentDialogs() {
        return maxConcurrentDialogs;
    }

    public void setMaxConcurrentDialogs(int maxConcurrentDialogs) {
        this.maxConcurrentDialogs = maxConcurrentDialogs;
    }

    public boolean isOneNotificationFor100Dialogs() {
        return oneNotificationFor100Dialogs;
    }

    public void setOneNotificationFor100Dialogs(boolean oneNotificationFor100Dialogs) {
        this.oneNotificationFor100Dialogs = oneNotificationFor100Dialogs;
    }

    public boolean isAutoResponseOnUnstructuredSSRequests() {
        return autoResponseOnUnstructuredSSRequests;
    }

    public void setAutoResponseOnUnstructuredSSRequests(boolean autoResponseOnUnstructuredSSRequests) {
        this.autoResponseOnUnstructuredSSRequests = autoResponseOnUnstructuredSSRequests;
    }

    public String getSriResponseImsi() {
        return sriResponseImsi;
    }

    public void setSriResponseImsi(String sriResponseImsi) {
        this.sriResponseImsi = sriResponseImsi;
    }

    public String getSriResponseVlr() {
        return sriResponseVlr;
    }

    public void setSriResponseVlr(String sriResponseVlr) {
        this.sriResponseVlr = sriResponseVlr;
    }

    public SRIReaction getSRIReaction() {
        return sriReaction;
    }

    public void setSRIReaction(SRIReaction val) {
        sriReaction = val;
    }

    public boolean isReturn20PersDeliveryErrors() {
        return return20PersDeliveryErrors;
    }

    public void setReturn20PersDeliveryErrors(boolean val) {
        this.return20PersDeliveryErrors = val;
    }


}
