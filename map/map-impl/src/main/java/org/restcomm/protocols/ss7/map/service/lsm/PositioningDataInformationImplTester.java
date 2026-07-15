package org.restcomm.protocols.ss7.map.service.lsm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class PositioningDataInformationImplTester {

    private static final Logger logger = LogManager.getLogger(PositioningDataInformationImplTester.class.getName());

    /**
     * PositioningDataInformation ::= OCTET STRING (SIZE (2..maxPositioningDataInformation))
     *   -- Refers to the Positioning Data defined in 3GPP TS 49.031.
     *   -- This is composed of 2 or more octets with an internal structure according to 3GPP TS 49.031.
     *  maxPositioningDataInformation INTEGER ::= 10
     */

    /*
     * The positioning data discriminator (bits 4-1 of octet 3 of BSSAP-LE information element, 3GPP TS 49.031 § 10.20)
     * defines the type of data and number of octets m provided for each positioning method:
     * 0000 indicate usage of each positioning method that was attempted either successfully or unsuccessfully;
     * 1 octet of data is provided for each positioning method included
     * all other values are reserved
     *
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
     * Coding of usage (bits 3-1)
     *  000    Attempted unsuccessfully due to failure or interruption
     *  001    Attempted successfully: results not used to generate location
     *  010    Attempted successfully: results used to verify but not generate location
     *  011    Attempted successfully: results used to generate location
     *  100    Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods
     *         used by the MS cannot be determined
     */

    public static void main(String[] args) throws MAPException {
        // 0x00=0000 0000 -> positioning data discriminator (bits 4-1): 0000 indicate usage of each positioning method that was attempted either successfully or unsuccessfully
        // 0x03=0000 0011 -> 00000=>Method=Timing Advance, 011=>Usage=3 (Attempted successfully: results used to generate location)
        // 0x1b=0001 1011 -> 00011=>Method=Mobile Assisted E-OTD, 011=>Usage=3 (Attempted successfully: results used to generate location)
        // 0x21=0010 0001 -> 00100=>Method=Mobile Based E-OTD, 001=>Usage=1 (Attempted successfully: results not used to generate location
        // 0x2b=0010 1011 -> 00101=>Method=Mobile Assisted GPS, 011=>Usage=3: Attempted successfully: results used to generate location
        // 0x3a=0011 1010 -> 00111=>Method=Conventional GPS, 010=>Usage=2: Attempted successfully: results used to verify but not generate location
        // 0x43=0100 0011 -> 01000=>Method=U-TDOA, 011=>Usage=3: Attempted successfully: results used to generate location
        // 0x60=0110 0000 -> 01100=>Method=Cell ID, 000=>Usage=0: Attempted unsuccessfully due to failure or interruption
        byte[] data = new byte[] {0x00, 0x03, 0x1b, 0x21, 0x2b, 0x3a, 0x43, 0x60};
        PositioningDataInformationImpl geranPositioningData = new PositioningDataInformationImpl(data);
        HashMap<String, Integer> methodsAndUsage = geranPositioningData.getPositioningDataSet();

        for (HashMap.Entry<String, Integer> entry : methodsAndUsage.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            logger.info("Method={}, Usage={}: {}", key, value, geranPositioningData.getUsage(value));
        }

        ArrayList<String> methods = geranPositioningData.getLocationGeneratedPositioningMethods();
        StringBuilder sb = new StringBuilder();
        int metCounter = 0;
        sb.append("\nMethods attempted successfully with results used to generate location: [");
        for (String met : methods) {
            metCounter++;
            sb.append(met);
            if (methods.size() != metCounter)
                sb.append(", ");
        }
        sb.append("]");
        logger.info(sb);
    }
}
