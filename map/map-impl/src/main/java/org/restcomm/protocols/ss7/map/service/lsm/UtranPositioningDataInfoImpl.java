package org.restcomm.protocols.ss7.map.service.lsm;

import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranPositioningDataInfo;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public class UtranPositioningDataInfoImpl extends OctetStringBase implements UtranPositioningDataInfo {

    public UtranPositioningDataInfoImpl() {
        super(3, 11, "UtranPositioningDataInfo");
    }

    public UtranPositioningDataInfoImpl(byte[] data) {
        super(3, 11, "UtranPositioningDataInfo", data);
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public HashMap<String, Integer> getUtranPositioningDataSet() throws MAPException {
        if (data == null)
            throw new MAPException("UtranPositioningDataInfo data must not be empty");
        if (data.length < 3)
            throw new MAPException("UtranPositioningDataInfo data length must be at least 3");
        if (data.length > 11)
            throw new MAPException("UtranPositioningDataInfo data length must not be higher than 11");

        if (getUtranPositioningDataDiscriminator() != 0) {
            throw new MAPException("UtranPositioningDataInfo positioningDataDiscriminator indicates absence of positioningDataSet");
        } else {
            LinkedHashMap<String, Integer> posMethodsAndUsage = new LinkedHashMap<>();
            String positioningMethod;
            int usage;

            for (int i = 2; i < data.length; i++) {
                positioningMethod = getPositioningMethod((data[i] & 0xF8) >> 3);
                usage = data[i] & 0x07;
                posMethodsAndUsage.put(positioningMethod, usage);
            }
            return posMethodsAndUsage;
        }
    }

    @Override
    public ArrayList<String> getUtranLocationGeneratedPositioningMethods() throws MAPException {
        if (data == null)
            throw new MAPException("UtranPositioningDataInfo data must not be empty");
        if (data.length < 3)
            throw new MAPException("UtranPositioningDataInfo data length must be at least 3");
        if (data.length > 11)
            throw new MAPException("UtranPositioningDataInfo data length must not be higher than 11");

        if (getUtranPositioningDataDiscriminator() != 0) {
            throw new MAPException("UtranPositioningDataInfo positioningDataDiscriminator indicates absence of positioningDataSet");
        } else {
            ArrayList<String> locationGenPosMethods = new ArrayList<>();

            for (int i=2; i<data.length; i++) {
                if ((data[i] & 0x07) == 3) {
                    locationGenPosMethods.add(getPositioningMethod((data[i] & 0xF8) >> 3));
                }
            }
            return locationGenPosMethods;
        }
    }

    @Override
    public int getUtranPositioningDataDiscriminator() throws MAPException {
        if (data == null)
            throw new MAPException("UtranPositioningDataInfo data must not be empty");
        if (data.length < 3)
            throw new MAPException("UtranPositioningDataInfo data length must be at least 3");
        if (data.length > 11)
            throw new MAPException("UtranPositioningDataInfo data length must not be higher than 11");
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

        return data[0] & 0x0F;
    }

    @Override
    public String getPositioningMethod(int code) {
        /*
         * Coding of positioning method (bits 8-4):
         * 00000 Reserved for GERAN use only;
         * 00001 Reserved for GERAN use only;
         * 00010 Reserved for GERAN use only;
         * 00011 Reserved for GERAN use only;
         * 00100 Reserved for GERAN use only;
         * 00101 Mobile Assisted GPS;
         * 00110 Mobile Based GPS;
         * 00111 Conventional GPS;
         * 01000 U-TDOA;
         * 01001 OTDOA;
         * 01010 IPDL;
         * 01011 RTT;
         * 01100 Cell ID;
         * 01101 to 01111 reserved for other location technologies;
         * 10000 to 11111 reserved for network specific positioning methods.
         * NOTE: Reserved because of GERAN use only
         */
        String posMethod;
        switch (code) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                posMethod = "Reserved (GERAN use only)";
                break;
            case 5:
                posMethod = "Mobile Assisted GPS";
                break;
            case 6:
                posMethod = "Mobile Based GPS";
                break;
            case 7:
                posMethod = "Conventional GPS";
                break;
            case 8:
                posMethod = "U-TDOA";
                break;
            case 9:
                posMethod = "OTDOA";
                break;
            case 10:
                posMethod = "IPDL";
                break;
            case 11:
                posMethod = "RTT";
                break;
            case 12:
                posMethod = "Cell ID";
                break;
            case 13:
            case 14:
            case 15:
                posMethod = "reserved for other location technologies";
                break;
            default:
                posMethod = "reserved for network specific positioning methods";
                break;
        }
        return posMethod;
    }

    @Override
    public String getUsage(int u) {
        String usage = null;
        /*
         * Coding of usage (bits 3-1):
         * 000 Attempted unsuccessfully due to failure or interruption - not used.
         * 001 Attempted successfully: results not used to generate location - not used.
         * 010 Attempted successfully: results used to verify but not generate location - not used.
         * 011 Attempted successfully: results used to generate location.
         * 100 Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods used by the MS cannot be determined.
         *
         */
        switch (u) {
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
}
