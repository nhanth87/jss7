
package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GPRSChargingID;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("gPRSChargingIDImpl")
public class GPRSChargingIDImpl extends OctetStringBase implements GPRSChargingID {
    public GPRSChargingIDImpl() {
        super(4, 4, "GPRSChargingID");
    }

    public GPRSChargingIDImpl(byte[] data) {
        super(4, 4, "GPRSChargingID", data);
    }

    public byte[] getData() {
        return data;
    }

}
