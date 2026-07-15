
package org.restcomm.protocols.ss7.tools.simulator.tests.ussd;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "testUssdServerConfigurationData")
public class TestUssdServerConfigurationData {

    protected static final String MSISDN_ADDRESS = "msisdnAddress";
    protected static final String MSISDN_ADDRESS_NATURE = "msisdnAddressNature";
    protected static final String MSISDN_NUMBERING_PLAN = "msisdnNumberingPlan";
    protected static final String DATA_CODING_SCHEME = "dataCodingScheme";
    protected static final String ALERTING_PATTERN = "alertingPattern";
    protected static final String PROCESS_SS_REQUEST_ACTION = "processSsRequestAction";
    protected static final String AUTO_RESPONSE_STRING = "autoResponseString";
    protected static final String AUTO_UNSTRUCTURED_SS_REQUEST_STRING = "autoUnstructured_SS_RequestString";
    protected static final String ONE_NOTIFICATION_FOR_100_DIALOGS = "oneNotificationFor100Dialogs";

    protected String msisdnAddress = "";
    protected AddressNature msisdnAddressNature = AddressNature.international_number;
    protected NumberingPlan msisdnNumberingPlan = NumberingPlan.ISDN;
    protected int dataCodingScheme = 0x0F;
    protected int alertingPattern = -1;
    protected ProcessSsRequestAction processSsRequestAction = new ProcessSsRequestAction(
            ProcessSsRequestAction.VAL_MANUAL_RESPONSE);
    protected String autoResponseString = "";
    protected String autoUnstructured_SS_RequestString = "";
    protected boolean oneNotificationFor100Dialogs = false;

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

    public ProcessSsRequestAction getProcessSsRequestAction() {
        return processSsRequestAction;
    }

    public void setProcessSsRequestAction(ProcessSsRequestAction processSsRequestAction) {
        this.processSsRequestAction = processSsRequestAction;
    }

    public String getAutoResponseString() {
        return autoResponseString;
    }

    public void setAutoResponseString(String autoResponseString) {
        this.autoResponseString = autoResponseString;
    }

    public String getAutoUnstructured_SS_RequestString() {
        return autoUnstructured_SS_RequestString;
    }

    public void setAutoUnstructured_SS_RequestString(String autoUnstructured_SS_RequestString) {
        this.autoUnstructured_SS_RequestString = autoUnstructured_SS_RequestString;
    }

    public boolean isOneNotificationFor100Dialogs() {
        return oneNotificationFor100Dialogs;
    }

    public void setOneNotificationFor100Dialogs(boolean oneNotificationFor100Dialogs) {
        this.oneNotificationFor100Dialogs = oneNotificationFor100Dialogs;
    }


}
