package org.restcomm.protocols.ss7.map.api.service.sms;

import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;

/**
 *
 <code>
   MO-ForwardSM-Res ::= SEQUENCE {
     sm-RP-UI            SignalInfo            OPTIONAL,
     extensionContainer  ExtensionContainer    OPTIONAL,
   ...}
 </code>
 *
 * @author sergey vetyutnev
 *
 */
public interface MoForwardShortMessageResponse extends SmsMessage {

    SmsSignalInfo getSM_RP_UI();

    MAPExtensionContainer getExtensionContainer();

}