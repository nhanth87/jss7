
package org.restcomm.protocols.ss7.tools.simulator.tests.cap;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


import org.restcomm.protocols.ss7.isup.message.parameter.CalledPartyNumber;
import org.restcomm.protocols.ss7.tools.simulator.common.CapApplicationContextScf;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "testCapScfConfigurationData")
public class TestCapScfConfigurationData {

    private static final String CAP_APPLICATION_CONTEXT = "capApplicationContext";
    private static final String CON_DESTINATION_ROUTING_ADDRESS = "destinationRoutingAddress";
    private static final String CON_DESTINATION_ROUTING_ADDRESS_ADDRES = CON_DESTINATION_ROUTING_ADDRESS + "Address";
    private static final String CON_DESTINATION_ROUTING_ADDRESS_NAI = CON_DESTINATION_ROUTING_ADDRESS + "NAI";
    private static final String CON_DESTINATION_ROUTING_ADDRESS_NPI = CON_DESTINATION_ROUTING_ADDRESS + "NPI";
    private static final String REL_CAUSE_VALUE = "releaseCauseValue";
    private static final String REL_CODING_STANDARD_IND = "releaseCauseCodingStandardIndicator";
    private static final String REL_LOCATION_IND = "releaseCauseLocationIndicator";

    private CapApplicationContextScf capApplicationContext = new CapApplicationContextScf(
            CapApplicationContextScf.VAL_CAP_V4_capscf_ssfGeneric);

    private String conDestRouteAddrAddress = "77777777";
    private IsupNatureOfAddressIndicator conDestRouteAddrNatureOfAddress = IsupNatureOfAddressIndicator.getInstance(CalledPartyNumber._NAI_INTERNATIONAL_NUMBER);
    private IsupNumberingPlanIndicator conDestRouteAddrNumberingPlan = IsupNumberingPlanIndicator.getInstance(CalledPartyNumber._NPI_ISDN);

    private IsupCauseIndicatorCauseValue relCauseValue = IsupCauseIndicatorCauseValue.normalUnspecified;
    private IsupCauseIndicatorCodingStandard relCodingStandardInd = IsupCauseIndicatorCodingStandard.ITUT;
    private IsupCauseIndicatorLocation relLocationInd = IsupCauseIndicatorLocation.internationalNetwork;

    public CapApplicationContextScf getCapApplicationContext() {
        return capApplicationContext;
    }

    public void setCapApplicationContext(CapApplicationContextScf capApplicationContext) {
        this.capApplicationContext = capApplicationContext;
    }

    public String getConDestRouteAddrAddress() {
        return conDestRouteAddrAddress;
    }

    public void setConDestRouteAddrAddress(String conDestRouteAddrAddress) {
        this.conDestRouteAddrAddress = conDestRouteAddrAddress;
    }

    public IsupNatureOfAddressIndicator getConDestRouteAddrNatureOfAddress() {
        return conDestRouteAddrNatureOfAddress;
    }

    public void setConDestRouteAddrNatureOfAddress(IsupNatureOfAddressIndicator conDestRouteAddrNatureOfAddress) {
        this.conDestRouteAddrNatureOfAddress = conDestRouteAddrNatureOfAddress;
    }

    public IsupNumberingPlanIndicator getConDestRouteAddrNumberingPlan() {
        return conDestRouteAddrNumberingPlan;
    }

    public void setConDestRouteAddrNumberingPlan(IsupNumberingPlanIndicator conDestRouteAddrNumberingPlan) {
        this.conDestRouteAddrNumberingPlan = conDestRouteAddrNumberingPlan;
    }

    public IsupCauseIndicatorCauseValue getRelCauseValue() {
        return relCauseValue;
    }

    public void setRelCauseValue(IsupCauseIndicatorCauseValue relCauseValue) {
        this.relCauseValue = relCauseValue;
    }

    public IsupCauseIndicatorCodingStandard getRelCodingStandardInd() {
        return relCodingStandardInd;
    }

    public void setRelCodingStandardInd(IsupCauseIndicatorCodingStandard relCodingStandardInd) {
        this.relCodingStandardInd = relCodingStandardInd;
    }

    public IsupCauseIndicatorLocation getRelLocationInd() {
        return relLocationInd;
    }

    public void setRelLocationInd(IsupCauseIndicatorLocation relLocationInd) {
        this.relLocationInd = relLocationInd;
    }

}
