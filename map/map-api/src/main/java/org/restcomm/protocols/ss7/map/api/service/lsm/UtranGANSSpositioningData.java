package org.restcomm.protocols.ss7.map.api.service.lsm;

import com.google.common.collect.Multimap;
import org.restcomm.protocols.ss7.map.api.MAPException;

import java.io.Serializable;

/**
 <code>
 UtranGANSSpositioningData ::= OCTET STRING (SIZE (1..maxUtranGANSSpositioningData))
 -- Refers to the Position Data defined in 3GPP TS 25.413.
 -- This is composed of the GANSS-PositioningDataSet only, included in PositionData as defined in 3GPP TS 25.413.

 maxUtranGANSSpositioningData INTEGER ::= 9

 PositionData ::= SEQUENCE {
 positioningDataDiscriminator       PositioningDataDiscriminator,
 positioningDataSet                 PositioningDataSet   OPTIONAL,
 -- This IE shall be present if the PositioningDataDiscriminator IE is set to the value "0000" --
 iE-Extensions                      ProtocolExtensionContainer { {PositionData-ExtIEs} } OPTIONAL,
 ...
 }

 PositioningDataDiscriminator ::= BIT STRING (SIZE(4))
 PositioningDataSet ::= SEQUENCE(SIZE(1..maxSet)) OF PositioningMethodAndUsage
 maxSet INTEGER ::= 9
 PositioningMethodAndUsage ::= OCTET STRING (SIZE(1))


 PositionData-ExtIEs RANAP-PROTOCOL-EXTENSION ::= {
 { ID id-GANSS-PositioningDataSet       CRITICALITY ignore EXTENSION GANSS-PositioningDataSet      PRESENCE optional}|
 { ID id-Additional-PositioningDataSet  CRITICALITY ignore EXTENSION Additional-PositioningDataSet PRESENCE optional},
 ...
 }

 GANSS-PositioningDataSet ::= SEQUENCE(SIZE(1..maxGANSSSet)) OF GANSS-PositioningMethodAndUsage
 maxGANSSSet INTEGER ::= 9
 GANSS-PositioningMethodAndUsage ::= OCTET STRING (SIZE(1))

 </code>
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public interface UtranGANSSpositioningData extends Serializable {

    byte[] getData();

    Multimap<String, String> getUtranGANSSPositioningMethodsAndGANSSIds() throws MAPException;

    Multimap<String, String> getLocationGeneratedMethodsAndGANSSIds() throws MAPException;

    String getUtranGanssPositioningMethod(int code);

    String getGANSSId(int code) throws MAPException;

    String getUsage(byte[] data, int index);

    int getUsageCode(byte[] data, int index);
}
