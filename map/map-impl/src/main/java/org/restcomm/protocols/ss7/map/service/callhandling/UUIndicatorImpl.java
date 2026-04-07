
package org.restcomm.protocols.ss7.map.service.callhandling;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.callhandling.UUIndicator;
import org.restcomm.protocols.ss7.map.primitives.OctetStringLength1Base;

/**
*
* @author sergey vetyutnev
*
*/
@XStreamAlias("uUIndicatorImpl")
public class UUIndicatorImpl extends OctetStringLength1Base implements UUIndicator {
    public UUIndicatorImpl() {
        super("UUIndicator");
    }

    public UUIndicatorImpl(int data) {
        super("UUIndicator", data);
    }

    public int getData() {
        return data;
    }

}
