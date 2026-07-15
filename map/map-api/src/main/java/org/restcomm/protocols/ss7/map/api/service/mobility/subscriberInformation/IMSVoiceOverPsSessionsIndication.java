package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation;

/**
 *
<code>
 IMS-VoiceOverPS-SessionsInd ::= ENUMERATED {
   imsVoiceOverPS-SessionsNotSupported (0),
   imsVoiceOverPS-SessionsSupported    (1),
   unknown                             (2)
 }
 -- "unknown" shall not be used within ProvideSubscriberInfoRes
</code>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public enum IMSVoiceOverPsSessionsIndication {

    imsVoiceOverPSSessionsNotSupported(0), imsVoiceOverPSSessionsSupported(1), unknown(2);

    private final int imsVoiceOverPsSessionsIndication;

    IMSVoiceOverPsSessionsIndication(int ind) {
        this.imsVoiceOverPsSessionsIndication = ind;
    }

    public int getCode() {
        return imsVoiceOverPsSessionsIndication;
    }

    public static IMSVoiceOverPsSessionsIndication getInstance(int ind) {
        switch (ind) {
            case 0:
                return imsVoiceOverPSSessionsNotSupported;
            case 1:
                return imsVoiceOverPSSessionsSupported;
            case 2:
                return unknown;
            default:
                return null;
        }
    }
}
