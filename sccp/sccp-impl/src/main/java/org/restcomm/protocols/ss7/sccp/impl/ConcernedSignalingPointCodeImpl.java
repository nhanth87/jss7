package org.restcomm.protocols.ss7.sccp.impl;

import org.restcomm.protocols.ss7.sccp.ConcernedSignalingPointCode;

/**
 *
 * @author sergey vetyutnev
 * @author Amit Bhayani
 *
 */
public class ConcernedSignalingPointCodeImpl implements ConcernedSignalingPointCode {

    private int remoteSpc;

    public ConcernedSignalingPointCodeImpl() {
    }

    public ConcernedSignalingPointCodeImpl(int remoteSpc) {
        this.remoteSpc = remoteSpc;
    }

    public int getRemoteSpc() {
        return this.remoteSpc;
    }

    /**
     * @param remoteSpc the remoteSpc to set
     */
    protected void setRemoteSpc(int remoteSpc) {
        this.remoteSpc = remoteSpc;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("rsp=").append(this.remoteSpc);
        return sb.toString();
    }
}
