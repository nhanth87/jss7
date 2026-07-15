package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LocalGroupId;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class LocalGroupIdImpl extends OctetStringBase implements LocalGroupId {

    public static final String _PrimitiveName = "ResetId";

    public LocalGroupIdImpl(byte[] data) {
        super(1, 10, _PrimitiveName, data);
    }

    public LocalGroupIdImpl() {
        super(1, 10, _PrimitiveName);
    }

    @Override
    public byte[] getData() {
        return this.data;
    }
}
