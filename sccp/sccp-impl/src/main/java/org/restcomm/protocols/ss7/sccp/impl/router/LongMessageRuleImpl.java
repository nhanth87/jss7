package org.restcomm.protocols.ss7.sccp.impl.router;

import org.restcomm.protocols.ss7.sccp.LongMessageRule;
import org.restcomm.protocols.ss7.sccp.LongMessageRuleType;

/**
 *
 * @author sergey vetyutnev
 * @author Amit Bhayani
 *
 */
public class LongMessageRuleImpl implements LongMessageRule {

    private int firstSpc;
    private int lastSpc;
    private LongMessageRuleType ruleType;

    public LongMessageRuleImpl() {
    }

    public LongMessageRuleImpl(int firstSpc, int lastSpc, LongMessageRuleType ruleType) {
        this.firstSpc = firstSpc;
        this.lastSpc = lastSpc;
        this.ruleType = ruleType;
    }

    public LongMessageRuleType getLongMessageRuleType() {
        return this.ruleType;
    }

    public int getFirstSpc() {
        return this.firstSpc;
    }

    public int getLastSpc() {
        return this.lastSpc;
    }

    public boolean matches(int dpc) {
        if (dpc >= this.firstSpc && dpc <= this.lastSpc)
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("firstSpc=").append(this.firstSpc).append(", lastSpc=").append(this.lastSpc).append(", ruleType=")
                .append(this.ruleType);
        return sb.toString();
    }
}
