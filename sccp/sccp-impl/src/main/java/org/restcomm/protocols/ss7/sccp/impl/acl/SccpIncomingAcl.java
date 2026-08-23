package org.restcomm.protocols.ss7.sccp.impl.acl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * Inbound ACL for the Nextgen STP transit plane (SS7-firewall-lite).
 *
 * <p>Default-off: when disabled, {@link #check} always returns {@link Decision#ALLOW}
 * and costs one volatile read. When enabled, each incoming SCCP message is matched by
 * its MTP3 incoming OPC against {@link IncomingAccessRule}s; unmatched traffic is
 * denied when {@code defaultDeny} is set (the STP default posture).
 *
 * <p>Thread-safe: rule map is a {@link ConcurrentHashMap}; counters are atomic.
 * Persistence is handled by the owner (SccpStackImpl) via SCCPJacksonXMLHelper.
 */
public final class SccpIncomingAcl {

    private static final Logger logger = LogManager.getLogger(SccpIncomingAcl.class);

    public enum Decision {
        ALLOW,
        DENY_NO_RULE,
        DENY_RULE_ACTION,
        DENY_GT_MISMATCH,
        DENY_SSN_MISMATCH
    }

    private volatile boolean enabled;
    private volatile boolean defaultDeny = true;

    private final ConcurrentHashMap<Integer, IncomingAccessRule> rulesByOpc = new ConcurrentHashMap<>();

    private final AtomicLong checkedCount = new AtomicLong();
    private final AtomicLong allowedCount = new AtomicLong();
    private final AtomicLong deniedCount = new AtomicLong();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDefaultDeny() {
        return defaultDeny;
    }

    public void setDefaultDeny(boolean defaultDeny) {
        this.defaultDeny = defaultDeny;
    }

    public void addRule(IncomingAccessRule rule) throws Exception {
        if (rule == null) {
            throw new IllegalArgumentException("rule must not be null");
        }
        IncomingAccessRule prev = rulesByOpc.putIfAbsent(rule.getIncomingOpc(), rule);
        if (prev != null) {
            throw new Exception(String.format("Incoming ACL rule for opc=%d already exists", rule.getIncomingOpc()));
        }
        logger.info("Incoming ACL rule added: {}", rule);
    }

    public void modifyRule(IncomingAccessRule rule) throws Exception {
        if (rule == null || rulesByOpc.get(rule.getIncomingOpc()) == null) {
            throw new Exception(String.format("Incoming ACL rule for opc=%d does not exist",
                    rule == null ? -1 : rule.getIncomingOpc()));
        }
        rulesByOpc.put(rule.getIncomingOpc(), rule);
        logger.info("Incoming ACL rule modified: {}", rule);
    }

    public void removeRule(int incomingOpc) throws Exception {
        if (rulesByOpc.remove(incomingOpc) == null) {
            throw new Exception(String.format("Incoming ACL rule for opc=%d does not exist", incomingOpc));
        }
        logger.info("Incoming ACL rule removed for opc={}", incomingOpc);
    }

    public void removeAllRules() {
        rulesByOpc.clear();
    }

    public IncomingAccessRule getRule(int incomingOpc) {
        return rulesByOpc.get(incomingOpc);
    }

    public Map<Integer, IncomingAccessRule> getRules() {
        return Collections.unmodifiableMap(rulesByOpc);
    }

    public long getCheckedCount() {
        return checkedCount.get();
    }

    public long getAllowedCount() {
        return allowedCount.get();
    }

    public long getDeniedCount() {
        return deniedCount.get();
    }

    /**
     * @param incomingOpc MTP3 OPC of the received MSU
     * @param calledParty called party address (null = malformed/unparseable → denied when enabled)
     * @return decision; never null
     */
    public Decision check(int incomingOpc, SccpAddress calledParty) {
        if (!enabled) {
            return Decision.ALLOW;
        }
        checkedCount.incrementAndGet();

        IncomingAccessRule rule = rulesByOpc.get(incomingOpc);
        Decision decision;
        if (rule == null) {
            decision = defaultDeny ? Decision.DENY_NO_RULE : Decision.ALLOW;
        } else if (rule.getAction() == IncomingAccessRule.Action.DENY) {
            decision = Decision.DENY_RULE_ACTION;
        } else if (calledParty == null) {
            decision = Decision.DENY_GT_MISMATCH;
        } else {
            String gtDigits = calledParty.getGlobalTitle() != null ? calledParty.getGlobalTitle().getDigits() : null;
            int ssn = calledParty.getSubsystemNumber();
            if (!rule.matchesGt(gtDigits)) {
                decision = Decision.DENY_GT_MISMATCH;
            } else if (!rule.matchesSsn(ssn)) {
                decision = Decision.DENY_SSN_MISMATCH;
            } else {
                decision = Decision.ALLOW;
            }
        }

        if (decision == Decision.ALLOW) {
            allowedCount.incrementAndGet();
        } else {
            deniedCount.incrementAndGet();
        }
        return decision;
    }

    /** Persistable snapshot (Jackson XML). Bean-style for SCCPJacksonXMLHelper. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class State {

        private boolean enabled;
        private boolean defaultDeny = true;
        private List<IncomingAccessRule> rules = new ArrayList<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isDefaultDeny() {
            return defaultDeny;
        }

        public void setDefaultDeny(boolean defaultDeny) {
            this.defaultDeny = defaultDeny;
        }

        public List<IncomingAccessRule> getRules() {
            return rules;
        }

        public void setRules(List<IncomingAccessRule> rules) {
            this.rules = rules == null ? new ArrayList<>() : rules;
        }
    }

    public State exportState() {
        State s = new State();
        s.setEnabled(enabled);
        s.setDefaultDeny(defaultDeny);
        s.setRules(new ArrayList<>(rulesByOpc.values()));
        return s;
    }

    public void importState(State s) {
        if (s == null) {
            return;
        }
        this.enabled = s.isEnabled();
        this.defaultDeny = s.isDefaultDeny();
        this.rulesByOpc.clear();
        if (s.getRules() != null) {
            for (IncomingAccessRule rule : s.getRules()) {
                rulesByOpc.put(rule.getIncomingOpc(), rule);
            }
        }
        logger.info("Incoming ACL state imported: {}", this);
    }

    @Override
    public String toString() {
        return String.format("SccpIncomingAcl [enabled=%s, defaultDeny=%s, rules=%d, checked=%d, allowed=%d, denied=%d]",
                enabled, defaultDeny, rulesByOpc.size(), checkedCount.get(), allowedCount.get(), deniedCount.get());
    }
}
