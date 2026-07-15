package org.restcomm.protocols.ss7.map.api.service.lsm;

import java.io.Serializable;

import org.restcomm.protocols.ss7.map.api.datacoding.CBSDataCodingScheme;
import org.restcomm.protocols.ss7.map.api.primitives.USSDString;

/**
 *
 <code>
  LCSCodeword ::= SEQUENCE {
   dataCodingScheme  [0] USSD-DataCodingScheme,
   lcsCodewordString [1] LCSCodewordString,
  ...}
 </code>
 *
 * @author amit bhayani
 *
 */
public interface LCSCodeword extends Serializable {

    /**
     * USSD-DataCodingScheme ::= OCTET STRING (SIZE (1))
     * -- The structure of the USSD-DataCodingScheme is defined by the Cell Broadcast Data Coding Scheme as described in TS 3GPP TS 23.038
     */
    CBSDataCodingScheme getDataCodingScheme();

    /**
     * LCSCodewordString ::= USSD-String (SIZE (1..maxLCSCodewordStringLength))
     * maxLCSCodewordStringLength INTEGER ::= 20
     */
    USSDString getLCSCodewordString();
}
