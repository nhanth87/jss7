package org.restcomm.protocols.ss7.map.service.lsm;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.lsm.GeranGANSSpositioningData;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class GeranGANSSpositioningDataImpl extends OctetStringBase implements GeranGANSSpositioningData {

    public GeranGANSSpositioningDataImpl() {
        super(2, 10, "GeranGANSSpositioningData");
    }

    public GeranGANSSpositioningDataImpl(byte[] data) {
        super(2, 10, "GeranGANSSpositioningData", data);
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public Multimap<String, String> getGeranGANSSPositioningMethodsAndGANSSIds() throws MAPException {
        if (data == null)
            throw new MAPException("GeranGANSSpositioningData data must not be empty");
        if (data.length < 2)
            throw new MAPException("GeranGANSSpositioningData data length must be at least 2");
        if (data.length > 10)
            throw new MAPException("GeranGANSSpositioningData data length must not be higher than 10");

        Multimap<String, String> methodsAndGANSSId = LinkedHashMultimap.create();
        String method;
        String ganssId;

        for (int i=1; i<data.length; i++) {
            method = getGeranGanssPositioningMethod((data[i] & 0xC0) >> 6);
            ganssId = getGANSSId((data[i] & 0x38) >> 3);
            methodsAndGANSSId.put(method, ganssId);
        }
        return methodsAndGANSSId;
    }

    @Override
    public Multimap<String, String> getLocationGeneratedMethodsAndGANSSIds() throws MAPException {
        if (data == null)
            throw new MAPException("GeranGANSSpositioningData data must not be empty");
        if (data.length < 2)
            throw new MAPException("GeranGANSSpositioningData data length must be at least 2");
        if (data.length > 10)
            throw new MAPException("GeranGANSSpositioningData data length must not be higher than 10");

        Multimap<String, String> methodsAndGANSSId = LinkedHashMultimap.create();
        String method;
        String ganssId;

        for (int i=1; i<data.length; i++) {
            if ((data[i] & 0x07) == 3) {
                method = getGeranGanssPositioningMethod((data[i] & 0xC0) >> 6);
                ganssId = getGANSSId((data[i] & 0x38) >> 3);
                methodsAndGANSSId.put(method, ganssId);
            }
        }
        return methodsAndGANSSId;
    }

    @Override
    public String getGeranGanssPositioningMethod(int code) {
        /*
         * Coding of Method (bits 8-7):
         *  00   MS-Based
         *  01   MS-Assisted
         *  10   Conventional
         *  11   Reserved
         */
        String method;
        switch (code) {
            case 0:
                method = "MS-Based";
                break;
            case 1:
                method = "MS-Assisted";
                break;
            case 2:
                method = "Conventional";
                break;
            default:
                method = "Reserved";
                break;
        }
        return method;
    }

    @Override
    public String getGANSSId(int code) throws MAPException {
        /*
         * Coding of the GANSS Id (bits 6-4) :
         *  000  Galileo
         *  001  Satellite Based Augmentation Systems (SBAS)
         *  010  Modernized GPS
         *  011  Quasi Zenith Satellite System (QZSS)
         *  100  GLONASS
         *  101  BeiDou Navigation Satellite System (BDS)
         */
        if (code > 7)
            throw new MAPException("GeranGANSSpositioningData GANSS Id must be an integer value between 0 and 7");

        String ganssId;
        switch (code) {
            case 0:
                ganssId = "Galileo";
                break;
            case 1:
                ganssId = "SBAS";
                break;
            case 2:
                ganssId = "Modernized GPS";
                break;
            case 3:
                ganssId = "QZSS";
                break;
            case 4:
                ganssId = "GLONASS";
                break;
            case 5:
                ganssId = "BDS";
                break;
            default:
                ganssId = "Reserved";
                break;
        }
        return ganssId;
    }

    @Override
    public String getUsage(byte[] geranGanssPositioningData, int index) {
        String usage = null;
        /*
         * Coding of usage (bits 3-1)
         *  000    Attempted unsuccessfully due to failure or interruption
         *  001    Attempted successfully: results not used to generate location
         *  010    Attempted successfully: results used to verify but not generate location
         *  011    Attempted successfully: results used to generate location
         *  100    Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods
         *         used by the MS cannot be determined
         */
        switch (getUsageCode(geranGanssPositioningData, index)) {
            case 0:
                usage = "Attempted unsuccessfully due to failure or interruption";
                break;
            case 1:
                usage = "Attempted successfully: results not used to generate location";
                break;
            case 2:
                usage = "Attempted successfully: results used to verify but not generate location";
                break;
            case 3:
                usage = "Attempted successfully: results used to generate location";
                break;
            case 4:
                usage = "Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods " +
                        "used by the MS cannot be determined";
                break;
        }
        return usage;
    }

    @Override
    public int getUsageCode(byte[] utranGanssPositioningData, int index) {
        return utranGanssPositioningData[index] & 0x07;
    }
}