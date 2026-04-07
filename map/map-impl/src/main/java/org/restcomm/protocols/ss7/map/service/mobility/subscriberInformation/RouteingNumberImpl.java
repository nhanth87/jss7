package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.RouteingNumber;
import org.restcomm.protocols.ss7.map.primitives.TbcdString;

/**
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("routeingNumberImpl")
public class RouteingNumberImpl extends TbcdString implements RouteingNumber {
    public RouteingNumberImpl() {
        super(1, 5, "RouteingNumber");
    }

    public RouteingNumberImpl(String data) {
        super(1, 5, "RouteingNumber", data);
    }

    public String getRouteingNumber() {
        return data;
    }

    /**
     * XML Serialization/Deserialization
     */
    protected static final XMLFormat<RouteingNumberImpl> ROUTEING_NUMBER_XML = new XMLFormat<RouteingNumberImpl>(RouteingNumberImpl.class) {

        @Override
        public void read(javolution.xml.XMLFormat.InputElement xml, RouteingNumberImpl routeingNumber) throws XMLStreamException {
            routeingNumber.data = xml.getAttribute(NUMBER, "");
        }

        @Override
        public void write(RouteingNumberImpl routeingNumber, javolution.xml.XMLFormat.OutputElement xml) throws XMLStreamException {
            xml.setAttribute(NUMBER, routeingNumber.data);
        }
    };
}
