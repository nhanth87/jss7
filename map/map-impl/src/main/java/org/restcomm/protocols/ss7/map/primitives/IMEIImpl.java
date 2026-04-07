
package org.restcomm.protocols.ss7.map.primitives;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;

/**
 *
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("imei")
public class IMEIImpl extends TbcdString implements IMEI {

    public IMEIImpl() {
        // There are some fake mobiles that IMEI length != 15
        super(1, 8, "IMEI");
    }

    public IMEIImpl(String data) {
        // There are some fake mobiles that IMEI length != 15
        super(1, 8, "IMEI", data);
    }

    public String getIMEI() {
        return this.data;
    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        if (this.data == null)
            throw new MAPException("Error while encoding the IMEI: IMEI must not be null");

        super.encodeData(asnOutputStream);
    }

}
