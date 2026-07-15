package org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement;

/**
 *
 * SMSRegisterRequest::= ENUMERATED {
 *  sms-registration-required (0),
 *  sms-registration-not-preferred (1),
 *  no-preference (2),
 *  ...}
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public enum SMSRegisterRequest {

    isSmsRegistrationRequired(0), isSmsRegistrationNotPreferred(1), isNoPreference(2);

    private int code;

    private SMSRegisterRequest(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static SMSRegisterRequest getInstance(int code) {
        switch (code) {
            case 0:
                return SMSRegisterRequest.isSmsRegistrationRequired;
            case 1:
                return SMSRegisterRequest.isSmsRegistrationNotPreferred;
            case 2:
                return SMSRegisterRequest.isNoPreference;
            default:
                return null;
        }
    }
}