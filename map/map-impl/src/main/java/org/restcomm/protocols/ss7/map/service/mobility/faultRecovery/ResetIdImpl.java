package org.restcomm.protocols.ss7.map.service.mobility.faultRecovery;

import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.ResetId;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ResetIdImpl extends OctetStringBase implements ResetId {

    public static final String _PrimitiveName = "ResetId";

    public ResetIdImpl(byte[] data) {
        super(1, 4, _PrimitiveName, data);
    }

    public ResetIdImpl() {
        super(1, 4, _PrimitiveName);
    }

    @Override
    public byte[] getData() {
        return this.data;
    }
}
