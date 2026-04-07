
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.mobicents.protocols.asn.BitSetStrictLength;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CSGId;
import org.restcomm.protocols.ss7.map.primitives.BitStringBase;

/**
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("cSGIdImpl")
public class CSGIdImpl extends BitStringBase implements CSGId {

    private static final int BIT_STRING_LENGTH = 27;

    public CSGIdImpl() {
        super(BIT_STRING_LENGTH, BIT_STRING_LENGTH, BIT_STRING_LENGTH, "CSGId");
    }

    public CSGIdImpl(BitSetStrictLength data) {
        super(BIT_STRING_LENGTH, BIT_STRING_LENGTH, BIT_STRING_LENGTH, "CSGId", data);
    }

    public BitSetStrictLength getData() {
        return bitString;
    }

    // TODO: add implementing of internal structure (?)

}
