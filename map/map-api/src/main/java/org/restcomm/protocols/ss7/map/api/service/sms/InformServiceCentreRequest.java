package org.restcomm.protocols.ss7.map.api.service.sms;

import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;

/**
 *
 MAP V2-3:
 *
 * MAP V3: informServiceCentre OPERATION ::= { --Timer s ARGUMENT InformServiceCentreArg CODE local:63 }
 *
 * MAP V2: InformServiceCentre ::= OPERATION --Timer s ARGUMENT informServiceCentreArg InformServiceCentreArg
 *
 * MAP V2: InformServiceCentreArg ::= SEQUENCE { storedMSISDN ISDN-AddressString OPTIONAL, mw-Status MW-Status OPTIONAL, ...}
 *
 * MAP V3: InformServiceCentreArg ::= SEQUENCE {
 *  storedMSISDN                                       ISDN-AddressString                           OPTIONAL,
 *  mw-Status                                          MW-Status                                    OPTIONAL,
 *  extensionContainer                                 ExtensionContainer                           OPTIONAL,
 *  ...,
 *  absentSubscriberDiagnosticSM                       absentSubscriberDiagnosticSM                 OPTIONAL,
 *  additionalAbsentSubscriberDiagnosticSM         [0] AbsentSubscriberDiagnosticSM                 OPTIONAL,
 *  -- additionalAbsentSubscriberDiagnosticSM may be present only if absentSubscriberDiagnosticSM is present.
 *  -- if included, additionalAbsentSubscriberDiagnosticSM is for GPRS and absentSubscriberDiagnosticSM is for non-GPRS
 *  smsf3gppAbsentSubscriberDiagnosticSM           [1] AbsentSubscriberDiagnosticSM                OPTIONAL,
 *  smsfNon3gppAbsentSubscriberDiagnosticSM        [2] AbsentSubscriberDiagnosticSM                OPTIONAL
 *  }
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface InformServiceCentreRequest extends SmsMessage {

    ISDNAddressString getStoredMSISDN();

    MWStatus getMwStatus();

    MAPExtensionContainer getExtensionContainer();

    Integer getAbsentSubscriberDiagnosticSM();

    Integer getAdditionalAbsentSubscriberDiagnosticSM();

    Integer getSmsf3gppAbsentSubscriberDiagnosticSM();

    Integer getSmsfNon3gppAbsentSubscriberDiagnosticSM();
}
