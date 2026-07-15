package org.restcomm.protocols.ss7.map.api.service.sms;

import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;

/**

 MAP V1-2-3:

  MAP V2: ReportSM-DeliveryStatus ::= OPERATION --Timer s ARGUMENT reportSM-DeliveryStatusArg ReportSM-DeliveryStatusArg RESULT
  storedMSISDN ISDN-AddressString -- optional -- storedMSISDN must be absent in version 1 -- storedMSISDN must be present in
  version greater 1 ERRORS { DataMissing, -- DataMissing must not be used in version 1 UnexpectedDataValue, UnknownSubscriber,
  MessageWaitingListFull}

  MAP V2: ReportSM-DeliveryStatusArg ::= SEQUENCE { msisdn ISDN-AddressString, serviceCentreAddress AddressString,
  sm-DeliveryOutcome SM-DeliveryOutcome OPTIONAL, -- sm-DeliveryOutcome must be absent in version 1 -- sm-DeliveryOutcome must
  be present in version greater 1 ...}

  MAP V3: reportSM-DeliveryStatus OPERATION ::= { --Timer s ARGUMENT ReportSM-DeliveryStatusArg RESULT
  ReportSM-DeliveryStatusRes -- optional ERRORS { dataMissing | unexpectedDataValue | unknownSubscriber |
  messageWaitingListFull} CODE local:47 }

  MAP V3: ReportSM-DeliveryStatusArg ::= SEQUENCE {
   msisdn                                 ISDN-AddressString,
   serviceCentreAddress                   AddressString,
   sm-DeliveryOutcome                     SM-DeliveryOutcome,
   absentSubscriberDiagnosticSM           [0] AbsentSubscriberDiagnosticSM               OPTIONAL,
   extensionContainer                     [1] ExtensionContainer                         OPTIONAL,
   ...,
   gprsSupportIndicator                   [2] NULL                                       OPTIONAL,
   -- gprsSupportIndicator is set only if the SMS-GMSC supports handling of two delivery outcomes

   deliveryOutcomeIndicator               [3] NULL                                       OPTIONAL,
   -- DeliveryOutcomeIndicator is set when the SM-DeliveryOutcome is for GPRS

   additionalSM-DeliveryOutcome           [4] SM-DeliveryOutcome                         OPTIONAL,
   -- If received, additionalSM-DeliveryOutcome is for GPRS
   -- If DeliveryOutcomeIndicator is set, then AdditionalSM-DeliveryOutcome shall be absent

   additionalAbsentSubscriberDiagnosticSM  [5] AbsentSubscriberDiagnosticSM              OPTIONAL,
   -- If received additionalAbsentSubscriberDiagnosticSM is for GPRS
   -- If DeliveryOutcomeIndicator is set, then AdditionalAbsentSubscriberDiagnosticSM shall be absent

   ip-sm-gw-Indicator                      [6] NULL                                      OPTIONAL,
   -- the ip-sm-gw indicator indicates by its presence that sm-deliveryOutcome is for delivery via IMS
   -- If present, deliveryOutcomeIndicator shall be absent.

   ip-sm-gw-sm-deliveryOutcome             [7] SM-DeliveryOutcome                        OPTIONAL,
   -- If received ip-sm-gw-sm-deliveryOutcome is for delivery via IMS
   -- If ip-sm-gw-Indicator is set, then ip-sm-gw-sm-deliveryOutcome shall be absent

   ip-sm-gw-absentSubscriberDiagnosticSM   [8] AbsentSubscriberDiagnosticSM             OPTIONAL,
   -- If received ip-sm-gw-sm-absentSubscriberDiagnosticSM is for delivery via IMS
   -- If ip-sm-gw-Indicator is set, then ip-sm-gw-sm-absentSubscriberDiagnosticSM shall be absent

   imsi                                    [9] IMSI                                     OPTIONAL,
   singleAttemptDelivery                  [10] NULL                                     OPTIONAL,
   correlationID                          [11] CorrelationID                            OPTIONAL,
   smsf-3gpp-deliveryOutcomeIndicator     [12] NULL                                     OPTIONAL,
   -- smsf-3gpp-deliveryOutcome is set when the SM-DeliveryOutcome is for 3GPP-SMSF

   smsf-3gpp-deliveryOutcome              [13] SM-DeliveryOutcome                       OPTIONAL,
   -- If smsf-3gpp-deliveryOutcomeIndicator is set, then smsf-3gpp-deliveryOutcome shall be absent

   smsf-3gpp-absentSubscriberDiagSM       [14] AbsentSubscriberDiagnosticSM             OPTIONAL,
   -- If smsf-3gpp-deliveryOutcomeIndicator is set, then smsf-3gpp-absentSubscriberDiagSM shall be absent

   smsf-non-3gpp-deliveryOutcomeIndicator [15] NULL                                     OPTIONAL,
   -- smsf-non-3gpp-deliveryOutcomeIndicator is set when the SM-DeliveryOutcome is for non-3GPP-SMSF

   smsf-non-3gpp-deliveryOutcome          [16] SM-DeliveryOutcome                       OPTIONAL,
   -- If smsf-non-3gpp-deliveryOutcomeIndicator is set, then smsf-non-3gpp-deliveryOutcome shall be absent

   smsf-non-3gpp-absentSubscriberDiagSM   [17] AbsentSubscriberDiagnosticSM             OPTIONAL
   -- If smsf-non-3gpp-deliveryOutcomeIndicator is set, then smsf-non-3gpp-absentSubscriberDiagSM shall be absent
  }

 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface ReportSMDeliveryStatusRequest extends SmsMessage {

    ISDNAddressString getMsisdn();

    AddressString getServiceCentreAddress();

    SMDeliveryOutcome getSMDeliveryOutcome();

    Integer getAbsentSubscriberDiagnosticSM();

    MAPExtensionContainer getExtensionContainer();

    boolean getGprsSupportIndicator();

    boolean getDeliveryOutcomeIndicator();

    SMDeliveryOutcome getAdditionalSMDeliveryOutcome();

    Integer getAdditionalAbsentSubscriberDiagnosticSM();

    boolean getIpSmGwIndicator();

    SMDeliveryOutcome getIpSmGwSMDeliveryOutcome();

    Integer getIpSmGwAbsentSubscriberDiagnosticSM();

    IMSI getImsi();

    boolean getSingleAttemptDelivery();

    CorrelationID getCorrelationID();

    boolean getSmsf3gppDeliveryOutcomeIndicator();

    SMDeliveryOutcome getSmsf3gppDeliveryOutcome();

    Integer getSmsf3gppAbsentSubscriberDiagnosticSM();

    boolean getSmsfNon3gppDeliveryOutcomeIndicator();

    SMDeliveryOutcome getSmsfNon3gppDeliveryOutcome();

    Integer getSmsfNon3gppAbsentSubscriberDiagnosticSM();
}
