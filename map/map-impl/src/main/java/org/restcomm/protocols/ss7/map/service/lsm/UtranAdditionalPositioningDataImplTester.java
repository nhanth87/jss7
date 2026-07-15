package org.restcomm.protocols.ss7.map.service.lsm;

import com.google.common.collect.Multimap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;

import java.util.Map;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class UtranAdditionalPositioningDataImplTester {

    private static final Logger logger = LogManager.getLogger(UtranAdditionalPositioningDataImplTester.class.getName());

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

     *  PositionData-ExtIEs RANAP-PROTOCOL-EXTENSION ::= {
     *  { ID id-GANSS-PositioningDataSet       CRITICALITY ignore EXTENSION GANSS-PositioningDataSet      PRESENCE optional}|
     *  { ID id-Additional-PositioningDataSet  CRITICALITY ignore EXTENSION Additional-PositioningDataSet PRESENCE optional},
     *  ...
     *  }

     *  Additional-PositioningDataSet ::= SEQUENCE(SIZE(1..maxAddPosSet)) OF Additional-PositioningMethodAndUsage
     *  maxAddPosSet INTEGER ::= 8
     *  Additional-PositioningMethodAndUsage ::= OCTET STRING (SIZE(1))
     */

    /*
     * Coding of positioning method (bits 8-7):
     * 00 Reserved;
     * 01 MS-Assisted;
     * 10 Standalone;
     * 11 Reserved.

     * Coding of Additional Positioning ID (bits 6-4):
     *  000 Barometric Pressure;
     *  001 WLAN;01001011
     *  010 Bluetooth;
     *  011 MBS;
     *  other values reserved.

     * Coding of usage (bits 3-1):
     *  011 Attempted successfully: results used to generate location;
     *  100 Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods used by the MS cannot be determined.
     */

    public static void main(String[] args) throws MAPException {
        // 0x94=1001 0100 10=>Method=Standalone, 010=>GANSSId=Bluetooth, 100=>usage=4 (Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods used by the MS cannot be determined)
        // 0x4b=0100 1011 00=>Method=MS-Assisted, AddPosId=GANSSId=WLAN, 011=>usage=3 (Attempted successfully: results used to generate location)
        byte[] data = new byte[] {(byte) 0x94, 0x4b};
        UtranAdditionalPositioningDataImpl utranAdditionalPositioningData = new UtranAdditionalPositioningDataImpl(data);
        Multimap<String, String> methodsAndAddPosIds = utranAdditionalPositioningData.getUtranAdditionalPositioningMethodsAndIds();
        Multimap<String, String> locationMethodsAndPosIds = utranAdditionalPositioningData.getLocationGeneratedMethodsAndAddPosIds();

        int i = 0;
        for (Map.Entry<String, String> entry : methodsAndAddPosIds.entries()) {
            String key = entry.getKey();
            String value = entry.getValue();
            logger.debug("Method={}, AddPosId={}, usage={} ({})", key, value, utranAdditionalPositioningData.getUsageCode(utranAdditionalPositioningData.getData(), i), utranAdditionalPositioningData.getUsage(utranAdditionalPositioningData.getData(), i));
            i++;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\nMethods/AddPosId attempted successfully with results used to generate location: [");
        i = 0;
        for (Map.Entry<String, String> entry : locationMethodsAndPosIds.entries()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append("Method=").append(key).append(", AddPosId=").append(value);
            i++;
        }
        sb.append("]");
        logger.debug(sb);
    }
}
