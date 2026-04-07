
package org.restcomm.protocols.ss7.map.primitives;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.restcomm.protocols.ss7.map.api.primitives.IMSI;

/**
 *
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("imsi")
public class IMSIImpl extends TbcdString implements IMSI {

    public IMSIImpl() {
        super(3, 8, "IMSI");
    }

    public IMSIImpl(String data) {
        super(3, 8, "IMSI", data);
    }

    public String getData() {
        return this.data;
    }

}
