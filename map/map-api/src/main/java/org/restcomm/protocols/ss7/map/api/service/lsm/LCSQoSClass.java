package org.restcomm.protocols.ss7.map.api.service.lsm;

/**
 *
 <code>
  LCS-QoS-Class ::= ENUMERATED {
   bestEffort  (0),
   assured     (1),
   ... }
   -- exception handling:
   -- an unrecognized value shall be treated the same as value 0 (bestEffort)
 </code>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public enum LCSQoSClass {

    bestEffort (0), assured (1);

    private int code;

    LCSQoSClass(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static LCSQoSClass getInstance(int code) {
        if (code == 0)
            return LCSQoSClass.bestEffort;
        else
            return LCSQoSClass.assured;
    }
}
