package org.restcomm.protocols.ss7.map.service.lsm;

import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.lsm.PositioningDataInformation;
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
public class PositioningDataInformationImpl extends OctetStringBase implements PositioningDataInformation {

    public PositioningDataInformationImpl() {
        super(2, 10, "PositioningDataInformation");
    }

    public PositioningDataInformationImpl(byte[] data) {
        super(2, 10, "PositioningDataInformation", data);
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public int getPositioningDataDiscriminator() throws MAPException {
        if (data == null)
            throw new MAPException("PositioningDataInformation data must not be empty");
        if (data.length < 2)
            throw new MAPException("PositioningDataInformation data length must be at least 2");
        if (data.length > 10)
            throw new MAPException("PositioningDataInformation data length must not be higher than 10");
        /*
         * The positioning data discriminator (bits 4-1 of octet 3 of BSSAP-LE information element, 3GPP TS 49.031 § 10.20)
         * defines the type of data and number of octets m provided for each positioning method:
         * 0000 indicate usage of each positioning method that was attempted either successfully or unsuccessfully;
         * 1 octet of data is provided for each positioning method included
         * all other values are reserved
         */
        return data[0] & 0x0F;
    }


    @Override
    public ArrayList<String> getLocationGeneratedPositioningMethods() throws MAPException {
        if (data == null)
            throw new MAPException("PositioningDataInformation data must not be empty");
        if (data.length < 2)
            throw new MAPException("PositioningDataInformation data length must be at least 2");
        if (data.length > 10)
            throw new MAPException("PositioningDataInformation data length must not be higher than 10");
        if (getPositioningDataDiscriminator() != 0)
            throw new MAPException("PositioningDataInformation positioningDataDiscriminator indicates absence of positioningDataSet");

        ArrayList<String> locationGeneratedPosMethods = new ArrayList<>();

        for (int i=1; i<data.length; i++) {
            if ((data[i] & 0x07) == 3) {
                locationGeneratedPosMethods.add(getPositioningMethod((data[i] & 0xF8) >> 3));
            }
        }
        return locationGeneratedPosMethods;
    }

    @Override
    public HashMap<String, Integer> getPositioningDataSet() throws MAPException {
        if (data == null)
            throw new MAPException("PositioningDataInformation data must not be empty");
        if (data.length < 2)
            throw new MAPException("PositioningDataInformation data length must be at least 2");
        if (data.length > 10)
            throw new MAPException("PositioningDataInformation data length must not be higher than 10");
        if (getPositioningDataDiscriminator() != 0)
            throw new MAPException("PositioningDataInformation positioningDataDiscriminator indicates absence of positioningDataSet");

        LinkedHashMap<String, Integer> posMethodsAndUsage = new LinkedHashMap<>();
        String positioningMethod;
        int usage;

        for (int i=1; i<data.length; i++) {
            positioningMethod = getPositioningMethod((data[i] & 0xF8) >> 3);
            usage = data[i] & 0x07;
            posMethodsAndUsage.put(positioningMethod, usage);
        }
        return posMethodsAndUsage;
    }


    @Override
    public String getPositioningMethod(int code) {
        /*
         * Coding of positioning method (bits 8-4):
         * 00000    Timing Advance
         * 00001    Reserved (Note)
         * 00010    Reserved (Note)
         * 00011    Mobile Assisted E-OTD
         * 00100    Mobile Based E-OTD
         * 00101    Mobile Assisted GPS
         * 00110    Mobile Based GPS
         * 00111    Conventional GPS
         * 01000    U-TDOA
         * 01001    Reserved for UTRAN use only
         * 01010    Reserved for UTRAN use only
         * 01011    Reserved for UTRAN use only
         * 01100    Cell ID
         * 01101 to 01111 reserved for GSM
         * 10000 to 11111 reserved for network specific positioning methods
         *
         */
        String posMethod;
        switch (code) {
            case 0:
                posMethod = "Timing Advance";
                break;
            case 1:
            case 2:
                posMethod = "Reserved (not to be used)";
                break;
            case 3:
                posMethod = "Mobile Assisted E-OTD";
                break;
            case 4:
                posMethod = "Mobile Based E-OTD";
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
            case 10:
            case 11:
                posMethod = "Reserved for UTRAN use only";
                break;
            case 12:
                posMethod = "Cell ID";
                break;
            case 13:
            case 14:
            case 15:
                posMethod = "reserved for GSM";
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
         * Coding of usage (bits 3-1)
         *  000    Attempted unsuccessfully due to failure or interruption
         *  001    Attempted successfully: results not used to generate location
         *  010    Attempted successfully: results used to verify but not generate location
         *  011    Attempted successfully: results used to generate location
         *  100    Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods
         *         used by the MS cannot be determined
         */
        switch (u) {
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
}
