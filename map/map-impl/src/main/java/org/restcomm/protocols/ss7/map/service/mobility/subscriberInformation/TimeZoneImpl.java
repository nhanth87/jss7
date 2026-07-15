package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TimeZone;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class TimeZoneImpl extends OctetStringBase implements TimeZone {

    public static final String _PrimitiveName = "TimeZone";

    public TimeZoneImpl() {
        super(2, 3, _PrimitiveName);
    }

    public TimeZoneImpl(byte[] data) {
        super(2, 3, _PrimitiveName, data);
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
