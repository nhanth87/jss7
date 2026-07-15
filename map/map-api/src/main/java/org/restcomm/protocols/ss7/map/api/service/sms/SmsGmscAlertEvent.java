package org.restcomm.protocols.ss7.map.api.service.sms;

/**
 <code>
  SmsGmsc-Alert-Event ::= ENUMERATED {
   msAvailableForMtSms    (0),
   msUnderNewServingNode  (1)
  }

 </code>

 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public enum SmsGmscAlertEvent {

    msAvailableForMtSms (0), msUnderNewServingNode(1);

    private int code;

    SmsGmscAlertEvent(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static SmsGmscAlertEvent getInstance(int code) {
        switch (code) {
            case 0:
                return msAvailableForMtSms;
            case 1:
                return msUnderNewServingNode;
            default:
                return null;
        }
    }
}
