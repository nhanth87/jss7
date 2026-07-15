package org.restcomm.protocols.ss7.map.service.lsm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class UtranPositioningDataInfoImplTester {

    private static final Logger logger = LogManager.getLogger(UtranPositioningDataInfoImplTester.class.getName());

    /**
     * UtranAdditionalPositioningData ::= OCTET STRING (SIZE (1..maxUtranAdditionalPositioningData))
     *  -- Refers to the Position Data defined in 3GPP TS 25.413.
     *  -- This is composed of the Additional-PositioningDataSet only, included in PositionData as defined in 3GPP TS 25.413.
     *  maxUtranAdditionalPositioningData INTEGER ::= 8

     *  PositionData ::= SEQUENCE {
     *  positioningDataDiscriminator       PositioningDataDiscriminator,
     *  positioningDataSet                 PositioningDataSet   OPTIONAL,
     *  -- This IE shall be present if the PositioningDataDiscriminator IE is set to the value "0000" --
     *  iE-Extensions                      ProtocolExtensionContainer { {PositionData-ExtIEs} } OPTIONAL,
     *  ...
     *  }

     *  PositioningDataDiscriminator ::= BIT STRING (SIZE(4))
     *  PositioningDataSet ::= SEQUENCE(SIZE(1..maxSet)) OF PositioningMethodAndUsage
     *  maxSet INTEGER ::= 9
     *  PositioningMethodAndUsage ::= OCTET STRING (SIZE(1))
     */

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
     * All other values are reserved.
     *
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
     *
     * Coding of usage (bits 3-1):
     * 000 Attempted unsuccessfully due to failure or interruption - not used.
     * 001 Attempted successfully: results not used to generate location - not used.
     * 010 Attempted successfully: results used to verify but not generate location - not used.
     * 011 Attempted successfully: results used to generate location.
     * 100 Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods used by the MS cannot be determined.
     */

    public static void main(String[] args) throws MAPException {
        // 0x00=0000 0000 -> positioning data discriminator (BIT STRING (SIZE(4))): 0000 indicates the presence of the Positioning Data Set IE (that reports the usage of each non-GANSS method that was successfully used to obtain the location estimate) and the optional presence of the GANSS Positioning Data Set IE. It also indicates the optional presence of the Additional Positioning Data Set IE;
        // 0x00=0000 0000 -> C-ifDiscriminator=0
        // 0x28=0010 1000 -> 00101=>Method=Mobile Assisted GPS, usage=0 (Attempted unsuccessfully due to failure or interruption - not used)
        // 0x31=0011 0001 -> 00110=>Method=Mobile Based GPS, usage=1 (Attempted successfully: results not used to generate location - not used)
        // 0x40=0100 0000 -> 01000=>Method=U-TDOA, usage=0 (Attempted unsuccessfully due to failure or interruption - not used)
        // 0x51=0101 0001 -> 01010=>Method=IPDL, usage=1 (Attempted successfully: results not used to generate location - not used)
        // 0x5c=0101 1100 -> 01011=>Method=RTT, usage=4 (Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods used by the MS cannot be determined)
        // 0x4b=0100 1011 -> 01000=>Method=OTDOA, usage 3 (Attempted successfully: results used to generate location)
        // 0x3a=0011 1010 -> 00111=>Method=Conventional GPS, usage=2 (results used to verify but not generate location - not used)
        byte[] data = new byte[] {0x00, 0x00, 0x28, 0x31, 0x40, 0x51, 0x5c, 0x4b, 0x3a};
        UtranPositioningDataInfoImpl utranPositioningData = new UtranPositioningDataInfoImpl(data);
        HashMap<String, Integer> methodsAndUsage = utranPositioningData.getUtranPositioningDataSet();

        for (HashMap.Entry<String, Integer> entry : methodsAndUsage.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            logger.info("Method={}, Usage={}: {}", key, value, utranPositioningData.getUsage(value));
        }

        ArrayList<String> methods = utranPositioningData.getUtranLocationGeneratedPositioningMethods();
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
