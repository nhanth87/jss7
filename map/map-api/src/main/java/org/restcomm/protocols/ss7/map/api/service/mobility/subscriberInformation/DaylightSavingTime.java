package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation;

/**
 *
 <code>
 DaylightSavingTime ::= ENUMERATED {
   noAdjustment           (0),
   plusOneHourAdjustment  (1),
   plusTwoHoursAdjustment (2)
 }
 -- Refer to the 3GPP TS 29.272 [144] for details.
 </code>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public enum DaylightSavingTime {

    noAdjustment(0), plusOneHourAdjustment(1), plusTwoHoursAdjustment(2);

    private final int daylightSavingTime;

    DaylightSavingTime(int daylightSavingTime) {
        this.daylightSavingTime = daylightSavingTime;
    }

    public int getCode() {
        return daylightSavingTime;
    }

    public static DaylightSavingTime getInstance(int daylightSavingTime) {
        switch (daylightSavingTime) {
            case 0:
                return noAdjustment;
            case 1:
                return plusOneHourAdjustment;
            case 2:
                return plusTwoHoursAdjustment;
            default:
                return null;
        }
    }
}
