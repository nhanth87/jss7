package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation;

import java.io.Serializable;

/**
 *
 <code>
 TimeZone ::= OCTET STRING (SIZE (2..3))
  -- Refer to the 3GPP TS 29.272 [144] for details.
 It contains the offset from UTC (Coordinated Universal Time) in units of 15 minutes, as defined in 3GPP TS 22.042 [42].
 It shall be expressed as positive (i.e. with the leading plus sign [+]) if the local time is ahead of or equal to UTC of day
 and as negative (i.e. with the leading minus sign [-]) if it is behind UTC of day.
 The value contained in the Time-Zone shall take into account daylight saving time,
 such that when the sending entity changes from regular (winter) time to daylight saving (summer) time,
 there is a change to the value in the Time-Zone.
 The contents of the Time-Zone shall be formatted as a character string with the following format:
 Basic format: ±n, with "n" being the number of units of 15 minutes from UTC.
 For example, if the offset is +2h=+8x15mn, the value of the Time-Zone AVP will be: "+8".

 </code>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface TimeZone extends Serializable {

    byte[] getData();
}
