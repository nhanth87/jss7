package org.restcomm.protocols.ss7.map.api.service.lsm;

import org.restcomm.protocols.ss7.map.api.primitives.GSNAddress;
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;

/**
 *
 <code>
  ProvideSubscriberLocation-Arg ::= SEQUENCE {
   locationType                  LocationType,
   mlc-Number                    ISDN-AddressString,
   lcs-ClientID                  [0] LCS-ClientID        OPTIONAL,
   privacyOverride               [1] NULL                OPTIONAL,
   imsi                          [2] IMSI                OPTIONAL,
   msisdn                        [3] ISDN-AddressString  OPTIONAL,
   lmsi                          [4] LMSI                OPTIONAL,
   imei                          [5] IMEI                OPTIONAL,
   lcs-Priority                  [6] LCS-Priority        OPTIONAL,
   lcs-QoS                       [7] LCS-QoS             OPTIONAL,
   extensionContainer            [8] ExtensionContainer  OPTIONAL,
   ... ,
   supportedGADShapes            [9] SupportedGADShapes  OPTIONAL,
   lcs-ReferenceNumber          [10] LCS-ReferenceNumber OPTIONAL,
   lcsServiceTypeID             [11] LCSServiceTypeID    OPTIONAL,
   lcsCodeword                  [12] LCSCodeword         OPTIONAL,
   lcs-PrivacyCheck             [13] LCS-PrivacyCheck    OPTIONAL,
   areaEventInfo                [14] AreaEventInfo       OPTIONAL,
   h-gmlc-Address               [15] GSN-Address         OPTIONAL,
   mo-lrShortCircuitIndicator   [16] NULL                OPTIONAL,
   periodicLDRInfo              [17] PeriodicLDRInfo     OPTIONAL,
   reportingPLMNList            [18] ReportingPLMNList   OPTIONAL
  }

   -- one of imsi or msisdn is mandatory
   -- If a location estimate type indicates activate deferred location or cancel deferred
   -- location, a lcs-Reference number shall be included.
 </code>
 *
 * @author amit bhayani
 *
 */
public interface ProvideSubscriberLocationRequest extends LsmMessage {

    LocationType getLocationType();

    ISDNAddressString getMlcNumber();

    LCSClientID getLCSClientID();

    boolean getPrivacyOverride();

    IMSI getIMSI();

    ISDNAddressString getMSISDN();

    LMSI getLMSI();

    LCSPriority getLCSPriority();

    LCSQoS getLCSQoS();

    IMEI getIMEI();

    MAPExtensionContainer getExtensionContainer();

    SupportedGADShapes getSupportedGADShapes();

    Integer getLCSReferenceNumber();

    LCSCodeword getLCSCodeword();

    Integer getLCSServiceTypeID();

    LCSPrivacyCheck getLCSPrivacyCheck();

    AreaEventInfo getAreaEventInfo();

    GSNAddress getHGMLCAddress();

    boolean getMoLrShortCircuitIndicator();

    PeriodicLDRInfo getPeriodicLDRInfo();

    ReportingPLMNList getReportingPLMNList();

}
