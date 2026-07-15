package org.restcomm.protocols.ss7.map.service.lsm;

import com.google.common.collect.Multimap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;

import java.util.Map;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class GeranGANSSpositioningDataImplTester {

    private static final Logger logger = LogManager.getLogger(GeranGANSSpositioningDataImplTester.class.getName());
    /**
     * GeranGANSSpositioningData ::= OCTET STRING (SIZE (2..maxGeranGANSSpositioningData))
     *  -- Refers to the GANSS Positioning Data defined in 3GPP TS 49.031.
     *  -- This is composed of 2 or more octets with an internal structure according to 3GPP TS 49.031
     *  maxGeranGANSSpositioningData INTEGER ::= 10
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
        // 0x06 Length Indicator?
        // 0x8c=1000 1100 -> 10=>Method=Conventional, 001=>GANSSId=SBAS, 100=>usage=4 (Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods used by the MS cannot be determined)
        // 0x02=0000 0010 -> 00=>Method=MS-Based, 000=>GANSSId=Galileo, 10=>usage=2 (Attempted successfully: results used to verify but not generate location)
        // 0x11=0001 0001 -> 00=>Method=MS-Based, 010=>GANSSId=Modernized GPS, 01=>usage=1 (Attempted successfully: results not used to generate location)
        // 0x58=0101 1000 -> 01=>Method=MS-Assisted, 011=>GANSSId=QZSS, 00=usage=0 (Attempted unsuccessfully due to failure or interruption)
        // 0xe8=1110 1000 -> 11=>Method=Reserved, 101=>GANSSId=BDS, 00=usage0 (Attempted unsuccessfully due to failure or interruption)
        // 0x63=0110 0011 -> 01=>MS-Assisted, 100=>GANSSId=GLONASS, 11=usage3 (Attempted successfully: results used to generate location)
        byte[] data = new byte[] {0x06, (byte) 0x8c, 0x02, 0x11, 0x58, (byte) 0xe8, 0x63};
        GeranGANSSpositioningDataImpl geranGANSSPositioningData = new GeranGANSSpositioningDataImpl(data);
        Multimap<String, String> methodsAndGanssIds = geranGANSSPositioningData.getGeranGANSSPositioningMethodsAndGANSSIds();
        Multimap<String, String> locationMethodsAndGanssIds = geranGANSSPositioningData.getLocationGeneratedMethodsAndGANSSIds();

        int i = 1;
        for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
            String key = entry.getKey();
            String value = entry.getValue();
            logger.info("Method={}, GANSSId={}, usage={} ({})", key, value, geranGANSSPositioningData.getUsageCode(geranGANSSPositioningData.getData(), i), geranGANSSPositioningData.getUsage(geranGANSSPositioningData.getData(), i));
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
