package org.restcomm.protocols.ss7.map.api.service.lsm;

import com.google.common.collect.Multimap;
import org.restcomm.protocols.ss7.map.api.MAPException;

import java.io.Serializable;

/**
 <code>
 GeranGANSSpositioningData ::= OCTET STRING (SIZE (2..maxGeranGANSSpositioningData))
 -- Refers to the GANSS Positioning Data defined in 3GPP TS 49.031.
 -- This is composed of 2 or more octets with an internal structure according to 3GPP TS 49.031

 maxGeranGANSSpositioningData INTEGER ::= 10

 3GPP TS 49.031
  10.32 GANSS Positioning Data
   This is a variable length information element providing positioning data associated with a successful or unsuccessful
   location attempt for a target MS using GANSS.

   Octets 3 to n: Method | GANSS Id | Usage

   Coding of Method (bits 8-7):
   00   MS-Based
   01   MS-Assisted
   10   Conventional
   11   Reserved

   Coding of the GANSS Id (bits 6-4) :
   000  Galileo
   001  Satellite Based Augmentation Systems (SBAS)
   010  Modernized GPS
   011  Quasi Zenith Satellite System (QZSS)
   100  GLONASS
   101  BDS

   Coding of usage (bits 3-1)
   000  Attempted unsuccessfully due to failure or interruption
   001  Attempted successfully: results not used to generate location
   010  Attempted successfully: results used to verify but not generate location
   011  Attempted successfully: results used to generate location
   100  Attempted successfully: case where MS supports multiple mobile based positioning methods and the actual method or methods
        used by the MS cannot be determined

 </code>
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface GeranGANSSpositioningData extends Serializable {

    byte[] getData();

    Multimap<String, String> getGeranGANSSPositioningMethodsAndGANSSIds() throws MAPException;

    Multimap<String, String> getLocationGeneratedMethodsAndGANSSIds() throws MAPException;

    String getGeranGanssPositioningMethod(int code);

    String getGANSSId(int code) throws MAPException;

    String getUsage(byte[] data, int index);

    int getUsageCode(byte[] data, int index);
}
