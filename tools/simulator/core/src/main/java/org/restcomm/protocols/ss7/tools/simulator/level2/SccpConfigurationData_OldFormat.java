
package org.restcomm.protocols.ss7.tools.simulator.level2;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.NumberingPlan;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "sccpConfigurationData_OldFormat")
public class SccpConfigurationData_OldFormat extends SccpConfigurationData {

    protected static final String EXTRA_LOCAL_ADDRESS_DIGITS = "extraLocalAddressDigits";

    // private String extraLocalAddressDigits = "";


}
