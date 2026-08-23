package org.restcomm.protocols.ss7.sccp.impl.acl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * One per-peer inbound ACL rule for the Nextgen STP transit plane (SS7-firewall-lite).
 *
 * <p>Semantics — evaluated only when {@link SccpIncomingAcl#isEnabled()}:
 * <ol>
 *   <li>Lookup rule by incoming MTP3 OPC (exact match).</li>
 *   <li>No rule: {@code defaultDeny ? DENY : ALLOW}.</li>
 *   <li>Rule {@code action == DENY}: DENY.</li>
 *   <li>Rule with {@code calledGtPrefixes}: called-party GT digits must match at least
 *       one prefix ("*" suffix = wildcard remainder; no "*" = exact match), else DENY.</li>
 *   <li>Rule with {@code allowedSsns}: called-party SSN must be in the set, else DENY.</li>
 *   <li>Otherwise ALLOW.</li>
 * </ol>
 *
 * <p>Persisted via {@code SCCPJacksonXMLHelper}; keep bean-style getters/setters.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class IncomingAccessRule {

    public enum Action {
        ALLOW,
        DENY
    }

    private int incomingOpc;
    private String description;
    private Action action = Action.ALLOW;

    /** Null/empty = no GT restriction for this OPC. */
    private List<String> calledGtPrefixes;

    /** Null/empty = no SSN restriction for this OPC. */
    private Set<Integer> allowedSsns;

    public IncomingAccessRule() {
    }

    public IncomingAccessRule(int incomingOpc, Action action, List<String> calledGtPrefixes, Set<Integer> allowedSsns) {
        this.incomingOpc = incomingOpc;
        this.action = action;
        this.calledGtPrefixes = calledGtPrefixes;
        this.allowedSsns = allowedSsns;
    }

    public int getIncomingOpc() {
        return incomingOpc;
    }

    public void setIncomingOpc(int incomingOpc) {
        this.incomingOpc = incomingOpc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public List<String> getCalledGtPrefixes() {
        return calledGtPrefixes;
    }

    public void setCalledGtPrefixes(List<String> calledGtPrefixes) {
        this.calledGtPrefixes = calledGtPrefixes;
    }

    public Set<Integer> getAllowedSsns() {
        return allowedSsns;
    }

    public void setAllowedSsns(Set<Integer> allowedSsns) {
        this.allowedSsns = allowedSsns;
    }

    /**
     * @param calledGtDigits called-party GT digits, may be null (PC/SSN-routed message)
     * @param calledSsn      called-party SSN, 0 = absent
     * @return true if this rule permits the called-party address
     */
    public boolean matchesCalledParty(String calledGtDigits, int calledSsn) {
        return matchesGt(calledGtDigits) && matchesSsn(calledSsn);
    }

    /** True if the GT constraint (if any) is satisfied by {@code calledGtDigits}. */
    public boolean matchesGt(String calledGtDigits) {
        if (calledGtPrefixes == null || calledGtPrefixes.isEmpty()) {
            return true;
        }
        if (calledGtDigits == null) {
            return false;
        }
        for (String prefix : calledGtPrefixes) {
            if (prefix == null || prefix.isEmpty()) {
                continue;
            }
            if (prefix.endsWith("*")) {
                if (calledGtDigits.startsWith(prefix.substring(0, prefix.length() - 1))) {
                    return true;
                }
            } else if (calledGtDigits.equals(prefix)) {
                return true;
            }
        }
        return false;
    }

    /** True if the SSN constraint (if any) is satisfied by {@code calledSsn}. */
    public boolean matchesSsn(int calledSsn) {
        if (allowedSsns == null || allowedSsns.isEmpty()) {
            return true;
        }
        return calledSsn > 0 && allowedSsns.contains(calledSsn);
    }

    @Override
    public String toString() {
        return String.format("IncomingAccessRule [opc=%d, action=%s, gtPrefixes=%s, ssns=%s, desc=%s]", incomingOpc, action,
                calledGtPrefixes == null ? Collections.emptyList() : calledGtPrefixes,
                allowedSsns == null ? Collections.emptySet() : allowedSsns, description);
    }
}
