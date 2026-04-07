
package org.restcomm.protocols.ss7.cap.service.sms.primitive;


import org.restcomm.protocols.ss7.cap.api.service.sms.primitive.FreeFormatDataSMS;
import org.restcomm.protocols.ss7.cap.primitives.OctetStringBase;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.ByteArrayContainer;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author Lasith Waruna Perera
 * @author alerant appngin
 *
 */
@XStreamAlias("freeFormatDataSMS")
 extends OctetStringBase implements FreeFormatDataSMS {

    private static final String DATA = "data";

    public FreeFormatDataSMSImpl() {
        super(1, 160, "FreeFormatDataSMS");
    }

    public FreeFormatDataSMSImpl(byte[] data) {
        super(1, 160, "FreeFormatDataSMS", data);
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
