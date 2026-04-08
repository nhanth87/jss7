
package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TEID;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("tEIDImpl")
public class TEIDImpl extends OctetStringBase implements TEID {
    public TEIDImpl() {
        super(4, 4, "TEID");
    }

    public TEIDImpl(byte[] data) {
        super(4, 4, "TEID", data);
    }

    public byte[] getData() {
        return data;
    }
}
