package org.restcomm.protocols.ss7.map.service.lsm;

import com.google.common.collect.Multimap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;

import java.util.Map;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class UtranGANSSpositioningDataImplTester {

    private static final Logger logger = LogManager.getLogger(UtranGANSSpositioningDataImplTester.class.getName());
    /**
     * UtranGANSSpositioningData ::= OCTET STRING (SIZE (1..maxUtranGANSSpositioningData))
     *  -- Refers to the Position Data defined in 3GPP TS 25.413.
     *  -- This is composed of the GANSS-PositioningDataSet only, included in PositionData as defined in 3GPP TS 25.413.
     *  maxUtranGANSSpositioningData INTEGER ::= 9

     *  PositionData ::= SEQUENCE {
     *  positioningDataDiscriminator       PositioningDataDiscriminator,
     *  positioningDataSet                 PositioningDataSet   OPTIONAL,
     *  -- This IE shall be present if the PositioningDataDiscriminator IE is set to the value "0000" --
     *  iE-Extensions                      ProtocolExtensionContainer { {PositionData-ExtIEs} } OPTIONAL,
     *  ...
     *  }

     *  PositionData-ExtIEs RANAP-PROTOCOL-EXTENSION ::= {
     *  { ID id-GANSS-PositioningDataSet       CRITICALITY ignore EXTENSION GANSS-PositioningDataSet      PRESENCE optional}|
     *  { ID id-Additional-PositioningDataSet  CRITICALITY ignore EXTENSION Additional-PositioningDataSet PRESENCE optional},
     *  ...
     *  }

     *  GANSS-PositioningDataSet ::= SEQUENCE(SIZE(1..maxGANSSSet)) OF GANSS-PositioningMethodAndUsage
     *  maxGANSSSet INTEGER ::= 9
     *  GANSS-PositioningMethodAndUsage ::= OCTET STRING (SIZE(1))
     */

    /*
     * Coding of Method (bits 8-7):
     * 00   MS-Based
     * 01   MS-Assisted
     * 10   Conventional
     * 11   Reserved
     *
     * Coding of the GANSS Id (bits 6-4) :
     *  000  Galileo
     *  001  Satellite Based Augmentation Systems (SBAS)
     *  010  Modernized GPS
     *  011  Quasi Zenith Satellite System (QZSS)
     *  100  GLONASS
     *  101  BeiDou Navigation Satellite System (BDS)
     *  other values reserved.
     *
     * Coding of usage (bits 3-1):
     * 000 Attempted unsuccessfully due to failure or interruption - not used.
     * 001 Attempted successfully: results not used to generate location - not used.
     * 010 Attempted successfully: results used to verify but not generate location - not used.
     * 011 Attempted successfully: results used to generate location.
     * 100 Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods used by the MS cannot be determined.
     */

    public static void main(String[] args) throws MAPException {
        // 0x01=0000 0001 -> 00=>Method=MS-Based, 000=>GANSSId=Galileo, 01=>usage=1 (Attempted successfully: results used to generate location)
        // 0x4a=0100 1010 -> 01=>Method=MS-Assisted, 100=>GANSSId=SBAS, 010=>usage=2 (Attempted successfully: results used to verify but not generate location - not used)
        // 0x90=1001 0000 -> 10=>Method=Conventional, 010=>GANSSId=Modernized GPS, 000=>usage=0 (Attempted unsuccessfully due to failure or interruption - not used)
        // 0x18=0001 1000 -> 00=>Method=MS-Based, 000=>GANSSId=Modernized GPS, 000=>usage=0 (Attempted unsuccessfully due to failure or interruption - not used)
        // 0xdc=1110 1100 -> 11=>Method=Reserved, 101=>GANSSId=QZSS, usage=0 (Attempted unsuccessfully due to failure or interruption - not used)
        // 0x63=0110 0011 -> 01=>Method=MS-Assisted, 100=>GANSSId=GLONASS, usage=3 (Attempted successfully: results used to generate location)
        byte[] data = new byte[] {0x01, 0x4a, (byte) 0x90, 0x18, (byte) 0xec, 0x63};
        UtranGANSSpositioningDataImpl utranGANSSPositioningData = new UtranGANSSpositioningDataImpl(data);
        Multimap<String, String> methodsAndGanssIds = utranGANSSPositioningData.getUtranGANSSPositioningMethodsAndGANSSIds();
        Multimap<String, String> locationMethodsAndGanssIds = utranGANSSPositioningData.getLocationGeneratedMethodsAndGANSSIds();

        int i = 0;
        for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
            String key = entry.getKey();
            String value = entry.getValue();
            logger.info("Method={}, GANSSId={}, usage={} ({})", key, value, utranGANSSPositioningData.getUsageCode(utranGANSSPositioningData.getData(), i), utranGANSSPositioningData.getUsage(utranGANSSPositioningData.getData(), i));
            i++;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\nMethods/GanssIds attempted successfully with results used to generate location: [");
        i = 0;
        for (Map.Entry<String, String> entry : locationMethodsAndGanssIds.entries()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append("Method=").append(key).append(", GANSSId=").append(value);
            i++;
        }
        sb.append("]");
        logger.info(sb);
    }
}
