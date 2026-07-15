package org.restcomm.protocols.ss7.map.api.service.sms;

import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;

/**
 <code>
  MO-ForwardSM-Arg ::= SEQUENCE {
    sm-RP-DA            SM-RP-DA,
    sm-RP-OA            SM-RP-OA,
    sm-RP-UI            SignalInfo,
    extensionContainer  ExtensionContainer      OPTIONAL,
    ... ,
    imsi                IMSI                    OPTIONAL,
    correlationID       [0] CorrelationID       OPTIONAL,
    sm-DeliveryOutcome  [1] SM-DeliveryOutcome  OPTIONAL
  }
 </code>
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface MoForwardShortMessageRequest extends SmsMessage {

    SM_RP_DA getSM_RP_DA();

    SM_RP_OA getSM_RP_OA();

    SmsSignalInfo getSM_RP_UI();

    MAPExtensionContainer getExtensionContainer();

    IMSI getIMSI();

    CorrelationID getCorrelationID();

    SMDeliveryOutcome getSmDeliveryOutcome();
}
