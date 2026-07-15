
package org.restcomm.protocols.ss7.tools.simulator.level1;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "dialogicConfigurationData")
public class DialogicConfigurationData {

    protected static final String SOURCE_MODULE_ID = "SourceModuleId";
    protected static final String DESTINATION_MODULE_ID = "DestinationModuleId";

    private int sourceModuleId;
    private int destinationModuleId;

    public int getSourceModuleId() {
        return sourceModuleId;
    }

    public void setSourceModuleId(int val) {
        sourceModuleId = val;
    }

    public int getDestinationModuleId() {
        return destinationModuleId;
    }

    public void setDestinationModuleId(int val) {
        destinationModuleId = val;
    }


}
