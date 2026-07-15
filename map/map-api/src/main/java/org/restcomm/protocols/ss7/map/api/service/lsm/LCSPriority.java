package org.restcomm.protocols.ss7.map.api.service.lsm;

/**
 *
 <code>
  LCS-Priority ::= OCTET STRING (SIZE (1))
   -- 0 = highest priority
   -- 1 = normal priority
   -- all other values treated as 1
 </code>
 *
 *
 * @author sergey vetyutnev
 *
 */
public enum LCSPriority {

    highestPriority(0), normalPriority(1);

    private int code;

    LCSPriority(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static LCSPriority getInstance(int code) {
        if (code == 0)
            return LCSPriority.highestPriority;
        else
            return LCSPriority.normalPriority;
    }
}
