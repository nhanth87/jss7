
package org.restcomm.protocols.ss7.tools.simulator.level1;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class DialogicConfigurationData {

    protected static final String SOURCE_MODULE_ID = "SourceModuleId";
    protected static final String DESTINATION_MODULE_ID = "DestinationModuleId";

    private int sourceModuleId;
    private int destinationModuleId;

    @JsonProperty(SOURCE_MODULE_ID)
    @JsonAlias("sourceModuleId")
    public int getSourceModuleId() {
        return sourceModuleId;
    }

    @JsonProperty(SOURCE_MODULE_ID)
    public void setSourceModuleId(int val) {
        sourceModuleId = val;
    }

    @JsonProperty(DESTINATION_MODULE_ID)
    @JsonAlias("destinationModuleId")
    public int getDestinationModuleId() {
        return destinationModuleId;
    }

    @JsonProperty(DESTINATION_MODULE_ID)
    public void setDestinationModuleId(int val) {
        destinationModuleId = val;
    }

    protected static final XMLFormat<DialogicConfigurationData> XML = new XMLFormat<DialogicConfigurationData>(
            DialogicConfigurationData.class) {

        public void write(DialogicConfigurationData dial, OutputElement xml) throws XMLStreamException {
            xml.setAttribute(SOURCE_MODULE_ID, dial.sourceModuleId);
            xml.setAttribute(DESTINATION_MODULE_ID, dial.destinationModuleId);
        }

        public void read(InputElement xml, DialogicConfigurationData dial) throws XMLStreamException {
            dial.sourceModuleId = xml.getAttribute(SOURCE_MODULE_ID).toInt();
            dial.destinationModuleId = xml.getAttribute(DESTINATION_MODULE_ID).toInt();
        }
    };

}
