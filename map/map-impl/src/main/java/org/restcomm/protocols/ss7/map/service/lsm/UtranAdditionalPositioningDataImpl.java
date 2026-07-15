package org.restcomm.protocols.ss7.map.service.lsm;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranAdditionalPositioningData;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class UtranAdditionalPositioningDataImpl extends OctetStringBase implements UtranAdditionalPositioningData {

    public UtranAdditionalPositioningDataImpl() {
        super(1, 8, "UtranAdditionalPositioningData");
    }

    public UtranAdditionalPositioningDataImpl(byte[] data) {
        super(1, 8, "UtranAdditionalPositioningData", data);
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public Multimap<String, String> getUtranAdditionalPositioningMethodsAndIds() throws MAPException {
        if (data == null)
            throw new MAPException("UtranAdditionalPositioningData data must not be null");
        if (data.length < 1)
            throw new MAPException("UtranAdditionalPositioningData data length must be at least 1");
        if (data.length > 8)
            throw new MAPException("UtranAdditionalPositioningData data length must not be higher than 8");

        Multimap<String, String> posMethodsAndAddPosId = LinkedHashMultimap.create();
        String positioningMethod;
        String additionalPosId;

        for (byte dataByte : data) {
            positioningMethod = getAdditionalPositioningMethod((dataByte & 0xC0) >> 6);
            additionalPosId = getAdditionalPositioningId((dataByte & 0x38) >> 3);
            posMethodsAndAddPosId.put(positioningMethod, additionalPosId);
        }
        return posMethodsAndAddPosId;
    }

    @Override
    public Multimap<String, String> getLocationGeneratedMethodsAndAddPosIds() throws MAPException {
        if (data == null)
            throw new MAPException("UtranAdditionalPositioningData data must not be null");
        if (data.length < 1)
            throw new MAPException("UtranAdditionalPositioningData data length must be at least 1");
        if (data.length > 8)
            throw new MAPException("UtranAdditionalPositioningData data length must not be higher than 8");

        Multimap<String, String> methodsAndGANSSId = LinkedHashMultimap.create();
        String method;
        String ganssId;

        for (byte dataByte : data) {
            if ((dataByte & 0x07) == 3) {
                method = getAdditionalPositioningMethod((dataByte & 0xC0) >> 6);
                ganssId = getAdditionalPositioningId((dataByte & 0x38) >> 3);
                methodsAndGANSSId.put(method, ganssId);
            }
        }
        return methodsAndGANSSId;

    }

    @Override
    public String getAdditionalPositioningMethod(int code) {
        /*
         * Coding of positioning method (bits 8-7):
         *  00 Reserved;
         *  01 MS-Assisted;
         *  10 Standalone;
         *  11 Reserved.
         */
        String posMethod;
        switch (code) {
            case 1:
                posMethod = "MS-Assisted";
                break;
            case 2:
                posMethod = "Standalone";
                break;
            default:
                posMethod = "Reserved";
                break;
        }
        return posMethod;
    }

    @Override
    public String getAdditionalPositioningId(int id) {
        /*
         * Coding of Additional Positioning ID (bits 6-4):
         *  000 Barometric Pressure;
         *  001 WLAN;01001011
         *  010 Bluetooth;
         *  011 MBS;
         *  other values reserved.
         */
        String additionalPositioningId;
        switch (id) {
            case 0:
                additionalPositioningId = "Barometric Pressure";
                break;
            case 1:
                additionalPositioningId = "WLAN";
                break;
            case 2:
                additionalPositioningId = "Bluetooth";
                break;
            case 3:
                additionalPositioningId = "MBS";
                break;
            default:
                additionalPositioningId = "reserved";
                break;
        }
        return additionalPositioningId;
    }

    @Override
    public String getUsage(byte[] utranAdditionalPositioningData, int index) {
        String usage = null;
        /*
         * Coding of usage (bits 3-1):
         *  011 Attempted successfully: results used to generate location;
         *  100 Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods used by the MS cannot be determined.
         */
        switch (getUsageCode(utranAdditionalPositioningData, index)) {
            case 3:
                usage = "Attempted successfully: results used to generate location";
                break;
            case 4:
                usage = "Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods used by the MS cannot be determined";
                break;
        }
        return usage;
    }

    @Override
    public int getUsageCode(byte[] utranAdditionalPositioningData, int index) {
        return utranAdditionalPositioningData[index] & 0x07;
    }
}
