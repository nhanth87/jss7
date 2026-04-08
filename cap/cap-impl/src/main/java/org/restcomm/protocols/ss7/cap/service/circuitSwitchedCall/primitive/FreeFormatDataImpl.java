package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;


import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.FreeFormatData;
import org.restcomm.protocols.ss7.cap.primitives.OctetStringBase;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.ByteArrayContainer;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
*
* @author Lasith Waruna Perera
* @author sergey vetyutnev
*
*/
@XStreamAlias("freeFormatData")
public class FreeFormatDataImpl extends OctetStringBase implements FreeFormatData {

    private static final String DATA = "data";

    public FreeFormatDataImpl() {
        super(1, 160, "FreeFormatData");
    }

    public FreeFormatDataImpl(byte[] data) {
        super(1, 160, "FreeFormatData", data);
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
