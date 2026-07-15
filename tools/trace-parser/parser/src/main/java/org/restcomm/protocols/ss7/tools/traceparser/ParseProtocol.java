package org.restcomm.protocols.ss7.tools.traceparser;

import java.util.Map;

/**
 *
 * @author sergey vetyutnev
 *
 */
public enum ParseProtocol {
    Map(1), Cap(2), Isup(3);

    int code;

    ParseProtocol(int code) {
        this.code = code;
    }

}
