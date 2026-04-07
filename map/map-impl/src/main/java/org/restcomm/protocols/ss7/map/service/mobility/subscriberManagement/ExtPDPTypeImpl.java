
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtPDPType;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("extPDPTypeImpl")
public class ExtPDPTypeImpl extends OctetStringBase implements ExtPDPType {
    public ExtPDPTypeImpl() {
        super(2, 2, "ExtPDPType");
    }

    public ExtPDPTypeImpl(byte[] data) {
        super(2, 2, "ExtPDPType", data);
    }

    public byte[] getData() {
        return data;
    }

}
