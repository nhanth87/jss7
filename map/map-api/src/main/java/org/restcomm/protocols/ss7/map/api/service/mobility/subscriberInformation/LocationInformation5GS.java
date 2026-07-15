package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation;

import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.FQDN;

import java.io.Serializable;

/**
 *
 <code>
 LocationInformation5GS ::= SEQUENCE {
   nrCellGlobalIdentity          [0] NR-CGI OPTIONAL,
   e-utranCellGlobalIdentity     [1] E-UTRAN-CGI OPTIONAL,
   geographicalInformation       [2] GeographicalInformation OPTIONAL,
   geodeticInformation           [3] GeodeticInformation OPTIONAL,
   amf-address                   [4] FQDN OPTIONAL,
   trackingAreaIdentity          [5] TA-Id OPTIONAL,
   currentLocationRetrieved      [6] NULL OPTIONAL,
   ageOfLocationInformation      [7] AgeOfLocationInformation OPTIONAL,
   vplmnId                       [8] PLMN-Id OPTIONAL,
   localtimeZone                 [9] TimeZone OPTIONAL,
   rat-Type                      [10] Used-RAT-Type OPTIONAL,
   extensionContainer            [11] ExtensionContainer OPTIONAL,
   ...,
   nrTrackingAreaIdentity        [12] NR-TA-Id OPTIONAL
 }
 -- currentLocationRetrieved shall be present if the location information was retrieved after successful paging.

 </code>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public interface LocationInformation5GS extends Serializable {

    NRCellGlobalId getNRCellGlobalId();

    EUtranCgi getEUtranCgi();

    GeographicalInformation getGeographicalInformation();

    GeodeticInformation getGeodeticInformation();

    FQDN getAMFAddress();

    TAId getTAId();

    boolean isCurrentLocationRetrieved();

    Integer getAgeOfLocationInformation();

    PlmnId getVPlmnId();

    TimeZone getLocalTimeZone();

    UsedRATType getUsedRATType();

    MAPExtensionContainer getExtensionContainer();

    NRTAId getNRTAId();
}
