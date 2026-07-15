package org.restcomm.protocols.ss7.map.api.service.lsm;

import java.io.Serializable;

import org.restcomm.protocols.ss7.map.api.datacoding.CBSDataCodingScheme;
import org.restcomm.protocols.ss7.map.api.primitives.USSDString;

/**
 *
 <code>
  LCSRequestorID ::= SEQUENCE {
   dataCodingScheme     [0] USSD-DataCodingScheme,
   requestorIDString    [1] RequestorIDString,
   ...,
   lcs-FormatIndicator  [2] LCS-FormatIndicator OPTIONAL
 }

 </code>
 *
 * @author amit bhayani
 *
 */
public interface LCSRequestorID extends Serializable {

    CBSDataCodingScheme getDataCodingScheme();

    USSDString getRequestorIDString();

    LCSFormatIndicator getLCSFormatIndicator();
}
