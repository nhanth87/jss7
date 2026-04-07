
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

    /**
     * XML Serialization/Deserialization
     */
    protected static final XMLFormat<Ext4QoSSubscribedImpl> EXT4_QOS_SUBSCRIBED_XML = new XMLFormat<Ext4QoSSubscribedImpl>(Ext4QoSSubscribedImpl.class) {

        @Override
        public void read(javolution.xml.XMLFormat.InputElement xml, Ext4QoSSubscribedImpl qos4Subscribed) throws XMLStreamException {

            qos4Subscribed.data = xml.getAttribute(PRIORITY_LEVEL, DEFAULT_INT_VALUE);

        }

        @Override
        public void write(Ext4QoSSubscribedImpl qos4Subscribed, javolution.xml.XMLFormat.OutputElement xml) throws XMLStreamException {

            xml.setAttribute(PRIORITY_LEVEL, qos4Subscribed.data);

        }
    };
}
