
package org.restcomm.protocols.ss7.map.service.callhandling;

import jakarta.xml.bind.DatatypeConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.callhandling.UUI;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
*
* @author sergey vetyutnev
*
*/
@XStreamAlias("uUIImpl")
public class UUIImpl extends OctetStringBase implements UUI {
    public UUIImpl() {
        super(1, 131, "UUI");
    }

    public UUIImpl(byte[] data) {
        super(1, 131, "UUI", data);
    }

    public byte[] getData() {
        return data;
    }

}
