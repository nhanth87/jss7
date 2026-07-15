package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.EDRXCycleLengthValue;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class EDRXCycleLengthValueImpl extends OctetStringBase implements EDRXCycleLengthValue {

    public static final String _PrimitiveName = "EDRX-Cycle-Length-Value";

    public EDRXCycleLengthValueImpl (byte[] data) {
        super(1, 1, _PrimitiveName, data);
    }

    public EDRXCycleLengthValueImpl() {
        super(1, 1, _PrimitiveName);
    }

    @Override
    public byte[] getData() {
        return this.data;
    }
}
