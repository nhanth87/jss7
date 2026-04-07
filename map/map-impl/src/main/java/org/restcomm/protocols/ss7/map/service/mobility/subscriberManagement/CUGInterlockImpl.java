
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CUGInterlock;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("cUGInterlockImpl")
public class CUGInterlockImpl extends OctetStringBase implements CUGInterlock {
    public CUGInterlockImpl() {
        super(4, 4, "CUGInterlock");
    }

    public CUGInterlockImpl(byte[] data) {
        super(4, 4, "CUGInterlock", data);
    }

    public byte[] getData() {
        return data;
    }

}
