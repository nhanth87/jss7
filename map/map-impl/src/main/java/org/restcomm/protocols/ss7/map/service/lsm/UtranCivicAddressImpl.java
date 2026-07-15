package org.restcomm.protocols.ss7.map.service.lsm;

import org.restcomm.protocols.ss7.map.api.service.lsm.UtranCivicAddress;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public class UtranCivicAddressImpl extends OctetStringBase implements UtranCivicAddress {

    @Override
    public byte[] getData() {
        return data;
    }

    public UtranCivicAddressImpl() {
        super(1, 10000, "UtranCivicAddress");
    }

    public UtranCivicAddressImpl(byte[] data) {
        super(1, 10000, "UtranCivicAddress", data);
    }
}
