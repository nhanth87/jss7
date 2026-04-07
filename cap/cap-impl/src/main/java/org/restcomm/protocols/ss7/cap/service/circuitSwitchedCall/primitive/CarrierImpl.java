
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import jakarta.xml.bind.DatatypeConverter;


import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.Carrier;
import org.restcomm.protocols.ss7.cap.primitives.OctetStringBase;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
*
* @author sergey vetyutnev
*
*/
@XStreamAlias("carrier")
 extends OctetStringBase implements Carrier {

    private static final String DATA = "data";

    private static final String DEFAULT_VALUE = null;

    public CarrierImpl() {
        super(4, 4, "Carrier");
    }

    public CarrierImpl(byte[] data) {
        super(4, 4, "Carrier", data);
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
