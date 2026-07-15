package org.restcomm.protocols.ss7.map.api.service.lsm;

import org.restcomm.protocols.ss7.map.api.MAPException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
<code>
 PositioningDataInformation ::= OCTET STRING (SIZE (2..maxPositioningDataInformation))
  -- Refers to the Positioning Data defined in 3GPP TS 49.031.
  -- This is composed of 2 or more octets with an internal structure according to 3GPP TS 49.031.

 maxPositioningDataInformation INTEGER ::= 10

 3GPP TS 49.031 § 10.20 Positioning Data
 This is a variable length information element providing positioning data associated with a successful or unsuccessful location attempt for a target MS

 The positioning data discriminator (bits 4-1 of octet 3) defines the type of data and number of octets m provided for each positioning method:
 0000 indicate usage of each positioning method that was attempted either successfully or unsuccessfully; 1 octet of data is provided for each positioning method included all other values are reserved.

 Coding of the positioning method octets for positioning data discriminator = 0000:

 Coding of positioning method (bits 8-4):
 00000  Timing Advance
 00001  Reserved (Note)
 00010  Reserved (Note)
 00011  Mobile Assisted E-OTD
 00100  Mobile Based E-OTD
 00101  Mobile Assisted GPS
 00110  Mobile Based GPS
 00111  Conventional GPS
 01000  U-TDOA
 01001  Reserved for UTRAN use only
 01010  Reserved for UTRAN use only
 01011  Reserved for UTRAN use only
 01100  Cell ID
 01101 to 01111 reserved for GSM
 10000 to 11111 reserved for network specific positioning methods

 Coding of usage (bits 3-1)
 000    Attempted unsuccessfully due to failure or interruption
 001    Attempted successfully: results not used to generate location
 010    Attempted successfully: results used to verify but not generate location
 011    Attempted successfully: results used to generate location
 100    Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods used by the MS cannot be determined
 NOTE:  These values of the codepoints shall not be used as they were used in an earlier version of the protocol.

 </code>
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public interface PositioningDataInformation extends Serializable {

    byte[] getData();

    int getPositioningDataDiscriminator() throws MAPException;

    HashMap<String, Integer> getPositioningDataSet() throws MAPException;

    ArrayList<String> getLocationGeneratedPositioningMethods() throws MAPException;

    String getPositioningMethod(int code);

    String getUsage(int u);
}
