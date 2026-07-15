package org.restcomm.protocols.ss7.map.api.service.sms;

import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.Time;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.NetworkNodeDiameterAddress;

/**
 *
 MAP V1-2:

  MAP V2: alertServiceCentre OPERATION ::= { --Timer s ARGUMENT AlertServiceCentreArg RETURN RESULT TRUE ERRORS { systemFailure
  | dataMissing | unexpectedDataValue} CODE local:64 }

  MAP V1: alertServiceCentreWithoutResult ::= OPERATION --Timer s ARGUMENT alertServiceCentreArg AlertServiceCentreArg


  MAP V1-2: AlertServiceCentreArg ::= SEQUENCE { msisdn ISDN-AddressString, serviceCentreAddress AddressString, ...}

   AlertServiceCentreArg ::= SEQUENCE {
    msisdn                                  ISDN-AddressString,
    serviceCentreAddress                    AddressString,
    ...,
    imsi                                    IMSI                         OPTIONAL,
    correlationID                           CorrelationID                OPTIONAL,
    maximumUeAvailabilityTime           [0] Time                         OPTIONAL,
    smsGmscAlertEvent                   [1] SmsGmsc-Alert-Event          OPTIONAL,
    smsGmscDiameterAddress              [2] NetworkNodeDiameterAddress   OPTIONAL,
    newSGSNNumber                       [3] ISDN-AddressString           OPTIONAL,
    newSGSNDiameterAddress              [4] NetworkNodeDiameterAddress   OPTIONAL,
    newMMENumber                        [5] ISDN-AddressString           OPTIONAL,
    newMMEDiameterAddress               [6] NetworkNodeDiameterAddress   OPTIONAL,
    newMSCNumber                        [7] ISDN-AddressString           OPTIONAL
   }

 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface AlertServiceCentreRequest extends SmsMessage {

    ISDNAddressString getMsisdn();

    AddressString getServiceCentreAddress();

    IMSI getImsi();

    CorrelationID getCorrelationID();

    Time getMaximumUeAvailabilityTime();

    SmsGmscAlertEvent getSmsGmscAlertEvent();

    NetworkNodeDiameterAddress getSmsGmscDiameterAddress();

    ISDNAddressString getNewSGSNNumber();

    NetworkNodeDiameterAddress getNewSGSNDiameterAddress();

    ISDNAddressString getNewMMENumber();

    NetworkNodeDiameterAddress getNewMMEDiameterAddress();

    ISDNAddressString getNewMSCNumber();
}
