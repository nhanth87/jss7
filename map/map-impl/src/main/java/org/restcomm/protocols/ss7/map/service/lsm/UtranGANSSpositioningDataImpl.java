package org.restcomm.protocols.ss7.map.service.lsm;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranGANSSpositioningData;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class UtranGANSSpositioningDataImpl extends OctetStringBase implements UtranGANSSpositioningData {

    public UtranGANSSpositioningDataImpl() {
        super(1, 9, "UtranGANSSpositioningData");
    }

    public UtranGANSSpositioningDataImpl(byte[] data) {
        super(1, 9, "UtranGANSSpositioningData", data);
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public Multimap<String, String> getUtranGANSSPositioningMethodsAndGANSSIds() throws MAPException {
        if (data == null)
            throw new MAPException("UtranGANSSpositioningData data must not be empty");
        if (data.length < 1)
            throw new MAPException("UtranGANSSpositioningData data length must be at least 1");
        if (data.length > 9)
            throw new MAPException("UtranGANSSpositioningData data length must not be higher than 9");

        /*if (getUtranGanssPositioningDataDiscriminator() != 1) {
            throw new MAPException("positioningDataDiscriminator indicates GANSS-PositioningDataSet is absence or not unique");
        } else {
            for (int i=1; i<data.length-1; i++) {*/
        Multimap<String, String> methodsAndGANSSId = LinkedHashMultimap.create();
        String method;
        String ganssId;

        for (byte dataByte : data) {
            method = getUtranGanssPositioningMethod((dataByte & 0xC0) >> 6);
            ganssId = getGANSSId((dataByte & 0x38) >> 3);
            methodsAndGANSSId.put(method, ganssId);
        }
        return methodsAndGANSSId;
        /*}*/
    }

    @Override
    public Multimap<String, String> getLocationGeneratedMethodsAndGANSSIds() throws MAPException {
        if (data == null)
            throw new MAPException("UtranGANSSpositioningData data must not be empty");
        if (data.length < 1)
            throw new MAPException("UtranGANSSpositioningData data length must be at least 1");
        if (data.length > 9)
            throw new MAPException("UtranGANSSpositioningData data length must not be higher than 9");

        if (getUtranGanssPositioningDataDiscriminator() != 1) {
            throw new MAPException("positioningDataDiscriminator indicates GANSS-PositioningDataSet is absence or not unique");
        } else {
            Multimap<String, String> methodsAndGANSSId = LinkedHashMultimap.create();
            String method;
            String ganssId;

            for (byte dataByte : data) {
                if ((dataByte & 0x07) == 3) {
                    method = getUtranGanssPositioningMethod((dataByte & 0xC0) >> 6);
                    ganssId = getGANSSId((dataByte & 0x38) >> 3);
                    methodsAndGANSSId.put(method, ganssId);
                }
            }
            return methodsAndGANSSId;
        }
    }

    public int getUtranGanssPositioningDataDiscriminator() throws MAPException {
        if (data == null)
            throw new MAPException("UtranGANSSpositioningData data must not be empty");
        if (data.length < 1)
            throw new MAPException("UtranGANSSpositioningData data length must be at least 1");
        if (data.length > 9)
            throw new MAPException("UtranGANSSpositioningData data length must not be higher than 9");
        /*
         * The positioning data discriminator defines the type of data provided for each positioning method:
         *
         * 0000 indicates:
         *      the presence of
         *        the Positioning Data Set IE (that reports the usage of each non-GANSS method that was successfully used to obtain the location estimate)
         *        and the optional presence of the GANSS Positioning Data Set IE.
         *      It also indicates the optional presence of the Additional Positioning Data Set IE;
         *
         * 0001 indicates:
         *      the presence of:
         *        the GANSS Positioning Data Set IE (that reports the usage of each GANSS method that was successfully used to obtain the location estimate)
         *      the absence of:
         *        the Positioning Data Set IE.
         *      It also indicates the optional presence of the Additional Positioning Data Set IE;
         *
         * 0010 indicates:
         *      Additional Positioning Data Set IE and
         *      the absence of
         *       the Positioning Data Set and
         *       the GANSS Positioning Data Set IEs;
         *
         * 1 octet of data is provided for each positioning method included.
         *
         * All other values are reserved.
         */
        return data[0] & 0x0F; // This doesn't seem to be the case here (otherwise, min/maxLength should be 2/10 instead of 1/9)
    }

    @Override
    public String getUtranGanssPositioningMethod(int code) {
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
         *  other values reserved.
         */
        if (code > 7)
            throw new MAPException("UtranGANSSpositioningData GANSS Id must be an integer value between 0 and 7");

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
    public String getUsage(byte[] utranGanssPositioningData, int index) {
        String usage = null;
        /*
         * Coding of usage (bits 3-1):
         *  000 Attempted unsuccessfully due to failure or interruption - not used.
         *  001 Attempted successfully: results not used to generate location - not used.
         *  010 Attempted successfully: results used to verify but not generate location - not used.
         *  011 Attempted successfully: results used to generate location.
         *  100 Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods used by the MS cannot be determined.
         *
         */
        switch (getUsageCode(utranGanssPositioningData, index)) {
            case 0:
                usage = "Attempted unsuccessfully due to failure or interruption - not used";
                break;
            case 1:
                usage = "Attempted successfully: results not used to generate location - not used";
                break;
            case 2:
                usage = "Attempted successfully: results used to verify but not generate location - not used";
                break;
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
    public int getUsageCode(byte[] utranGanssPositioningData, int index) {
        return utranGanssPositioningData[index] & 0x07;
    }
}
