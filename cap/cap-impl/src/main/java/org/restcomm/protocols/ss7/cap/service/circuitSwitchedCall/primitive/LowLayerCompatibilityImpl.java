
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import jakarta.xml.bind.DatatypeConverter;


import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.LowLayerCompatibility;
import org.restcomm.protocols.ss7.cap.primitives.OctetStringBase;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
*
* @author sergey vetyutnev
*
*/
@XStreamAlias("lowLayerCompatibility")
 extends OctetStringBase implements LowLayerCompatibility {

    private static final String DATA = "data";

    private static final String DEFAULT_VALUE = null;

    public LowLayerCompatibilityImpl() {
        super(1, 16, "LowLayerCompatibility");
    }

    public LowLayerCompatibilityImpl(byte[] data) {
        super(1, 16, "LowLayerCompatibility", data);
    }

    public byte[] getData() {
        return data;
    }
}
