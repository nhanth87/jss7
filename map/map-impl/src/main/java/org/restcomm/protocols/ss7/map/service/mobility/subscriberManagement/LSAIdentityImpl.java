
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAIdentity;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("lSAIdentityImpl")
public class LSAIdentityImpl extends OctetStringBase implements LSAIdentity {
    public LSAIdentityImpl() {
        super(3, 3, "LSAIdentity");
    }

    public LSAIdentityImpl(byte[] data) {
        super(3, 3, "LSAIdentity", data);
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public boolean isPlmnSignificantLSA() {
        return ((this.data[2] & 0x01) == 0x01);
    }

}
