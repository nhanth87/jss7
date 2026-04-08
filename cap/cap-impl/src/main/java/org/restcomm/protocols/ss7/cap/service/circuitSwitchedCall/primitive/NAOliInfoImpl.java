package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;


import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.NAOliInfo;
import org.restcomm.protocols.ss7.cap.primitives.OctetStringLength1Base;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("nAOliInfo")
public class NAOliInfoImpl extends OctetStringLength1Base implements NAOliInfo {

    private static final String VALUE = "value";

    public NAOliInfoImpl() {
        super("NAOliInfo");
    }

    public NAOliInfoImpl(int data) {
        super("NAOliInfo", data);
    }

    @Override
    public int getData() {
        return data;
    }
}
