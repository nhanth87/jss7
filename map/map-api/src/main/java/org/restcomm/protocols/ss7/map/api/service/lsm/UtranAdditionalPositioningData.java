package org.restcomm.protocols.ss7.map.api.service.lsm;

import com.google.common.collect.Multimap;
import org.restcomm.protocols.ss7.map.api.MAPException;

import java.io.Serializable;

/**
 <code>
 UtranAdditionalPositioningData ::= OCTET STRING (SIZE (1..maxUtranAdditionalPositioningData))
 -- Refers to the Position Data defined in 3GPP TS 25.413.
 -- This is composed of the Additional-PositioningDataSet only, included in PositionData as defined in 3GPP TS 25.413.

 maxUtranAdditionalPositioningData INTEGER ::= 8

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

 Additional-PositioningDataSet ::= SEQUENCE(SIZE(1..maxAddPosSet)) OF Additional-PositioningMethodAndUsage
 maxAddPosSet INTEGER ::= 8
 Additional-PositioningMethodAndUsage ::= OCTET STRING (SIZE(1))

 </code>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface UtranAdditionalPositioningData extends Serializable {

    byte[] getData();

    Multimap<String, String> getUtranAdditionalPositioningMethodsAndIds() throws MAPException;

    Multimap<String, String> getLocationGeneratedMethodsAndAddPosIds() throws MAPException;

    String getAdditionalPositioningMethod(int code);

    String getAdditionalPositioningId(int id);

    String getUsage(byte[] data, int index);

    int getUsageCode(byte[] data, int index);
}
