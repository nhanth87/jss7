
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.Ext4QoSSubscribed;
import org.restcomm.protocols.ss7.map.primitives.OctetStringLength1Base;

/**
 *
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("ext4QoSSubscribedImpl")
public class Ext4QoSSubscribedImpl extends OctetStringLength1Base implements Ext4QoSSubscribed {
    public Ext4QoSSubscribedImpl() {
        super("Ext4QoSSubscribed");
    }

    public Ext4QoSSubscribedImpl(int data) {
        super("Ext4QoSSubscribed", data);
    }

    public int getData() {
        return data;
    }

}
