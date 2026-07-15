package org.restcomm.protocols.ss7.map.api.service.lsm;

import org.restcomm.protocols.ss7.map.api.MAPException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
<code>
 UtranPositioningDataInfo ::= OCTET STRING (SIZE (3..maxUtranPositioningDataInfo))
 -- Refers to the Position Data defined in 3GPP TS 25.413.
 -- This is composed of the positioningDataDiscriminator and the positioningDataSet
 -- included in positionData as defined in 3GPP TS 25.413.

 maxUtranPositioningDataInfo INTEGER ::= 11

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

 Additional-PositioningDataSet ::= SEQUENCE(SIZE(1..maxAddPosSet)) OF Additional-PositioningMethodAndUsage
 maxAddPosSet INTEGER ::= 8
 Additional-PositioningMethodAndUsage ::= OCTET STRING (SIZE(1))
 </code>
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface UtranPositioningDataInfo extends Serializable {

    byte[] getData();

    int getUtranPositioningDataDiscriminator() throws MAPException;

    HashMap<String, Integer> getUtranPositioningDataSet() throws MAPException;

    ArrayList<String> getUtranLocationGeneratedPositioningMethods() throws MAPException;

    String getPositioningMethod(int code);

    String getUsage(int u);
}
