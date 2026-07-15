package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.Ext4QoSSubscribed;
import org.restcomm.protocols.ss7.map.primitives.OctetStringLength1Base;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "ext4QoSSubscribedImpl")
public class Ext4QoSSubscribedImpl extends OctetStringLength1Base implements Ext4QoSSubscribed {

    private static final String PRIORITY_LEVEL = "priorityLevel";

    private static final int DEFAULT_INT_VALUE = 0;

    public Ext4QoSSubscribedImpl() {
        super("Ext4QoSSubscribed");
    }

    public Ext4QoSSubscribedImpl(int data) {
        super("Ext4QoSSubscribed", data);
    }

    public int getData() {
        return data;
    }

    /**
     * XML Serialization/Deserialization
     */

}
