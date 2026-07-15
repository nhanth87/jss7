package org.restcomm.protocols.ss7.map.api.service.sms;

import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.Time;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.NetworkNodeDiameterAddress;

/**
 <code>
  mt-ForwardSM  OPERATION ::= { --Timer ml
   -- the timer value may be subject to negotiation between GMSC and IP-SM-GW
   ARGUMENT
   MT-ForwardSM-Arg
   RESULT
   MT-ForwardSM-Res
   -- optional
   ERRORS {
    systemFailure | dataMissing | unexpectedDataValue | facilityNotSupported | unidentifiedSubscriber |
    illegalSubscriber | illegalEquipment | subscriberBusyForMT-SMS | sm-DeliveryFailure | absentSubscriberSM }
   CODE local:44
  }

  MT-ForwardSM-Arg ::= SEQUENCE {
   sm-RP-DA                     SM-RP-DA,
   sm-RP-OA                     SM-RP-OA,
   sm-RP-UI                     SignalInfo,
   moreMessagesToSend           NULL                            OPTIONAL,
   extensionContainer           ExtensionContainer              OPTIONAL,
   ...,
   smDeliveryTimer              SM-DeliveryTimerValue           OPTIONAL,
   smDeliveryStartTime          Time                            OPTIONAL,
   smsOverIP-OnlyIndicator      [0] NULL                        OPTIONAL,
   correlationID                [1] CorrelationID               OPTIONAL,
   maximumRetransmissionTime    [2] Time                        OPTIONAL,
   smsGmscAddress               [3] ISDN-AddressString          OPTIONAL,
   smsGmscDiameterAddress       [4] NetworkNodeDiameterAddress  OPTIONAL }
   -- SM-DeliveryTimerValue contains the value used by the SMS-GMSC

 </code>
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface MtForwardShortMessageRequest extends SmsMessage {

    SM_RP_DA getSM_RP_DA();

    SM_RP_OA getSM_RP_OA();

    SmsSignalInfo getSM_RP_UI();

    boolean getMoreMessagesToSend();

    MAPExtensionContainer getExtensionContainer();

    Integer getSmDeliveryTimer();

    Time getSmDeliveryStartTime();

    boolean getSmsOverIPOnlyIndicator();

    CorrelationID getCorrelationID();

    Time getMaximumRetransmissionTime();

    ISDNAddressString getSmsGmscAddress();

    NetworkNodeDiameterAddress getSmsGmscDiameterAddress();
}
