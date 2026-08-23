package org.restcomm.protocols.ss7.sccp.impl.acl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.List;
import java.util.Set;

import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.sccp.impl.SCCPJacksonXMLHelper;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.testng.annotations.Test;

/**
 * Unit tests for the Nextgen STP transit-plane inbound ACL (SS7-firewall-lite).
 */
public class SccpIncomingAclTest {

    private final ParameterFactoryImpl pf = new ParameterFactoryImpl();

    private SccpAddress gtAddress(String digits, int ssn) {
        return new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE,
                pf.createGlobalTitle(digits, org.restcomm.protocols.ss7.indicator.NatureOfAddress.INTERNATIONAL), 0, ssn);
    }

    @Test(groups = { "stp-acl", "functional.acl" })
    public void testDisabledAclAlwaysAllows() {
        SccpIncomingAcl acl = new SccpIncomingAcl();
        assertEquals(acl.check(1234, null), SccpIncomingAcl.Decision.ALLOW);
        assertEquals(acl.getCheckedCount(), 0);
    }

    @Test(groups = { "stp-acl", "functional.acl" })
    public void testDefaultDenyWithoutRule() {
        SccpIncomingAcl acl = new SccpIncomingAcl();
        acl.setEnabled(true);
        assertEquals(acl.check(1234, gtAddress("12345", 8)), SccpIncomingAcl.Decision.DENY_NO_RULE);
        assertEquals(acl.getDeniedCount(), 1);
    }

    @Test(groups = { "stp-acl", "functional.acl" })
    public void testDefaultAllowWithoutRule() {
        SccpIncomingAcl acl = new SccpIncomingAcl();
        acl.setEnabled(true);
        acl.setDefaultDeny(false);
        assertEquals(acl.check(1234, gtAddress("12345", 8)), SccpIncomingAcl.Decision.ALLOW);
    }

    @Test(groups = { "stp-acl", "functional.acl" })
    public void testExplicitDenyRule() throws Exception {
        SccpIncomingAcl acl = new SccpIncomingAcl();
        acl.setEnabled(true);
        acl.setDefaultDeny(false);
        acl.addRule(new IncomingAccessRule(77, IncomingAccessRule.Action.DENY, null, null));
        assertEquals(acl.check(77, gtAddress("12345", 8)), SccpIncomingAcl.Decision.DENY_RULE_ACTION);
    }

    @Test(groups = { "stp-acl", "functional.acl" })
    public void testGtPrefixWildcard() throws Exception {
        SccpIncomingAcl acl = new SccpIncomingAcl();
        acl.setEnabled(true);
        acl.addRule(new IncomingAccessRule(100, IncomingAccessRule.Action.ALLOW, List.of("123*"), null));

        assertEquals(acl.check(100, gtAddress("123456", 8)), SccpIncomingAcl.Decision.ALLOW);
        assertEquals(acl.check(100, gtAddress("999", 8)), SccpIncomingAcl.Decision.DENY_GT_MISMATCH);
    }

    @Test(groups = { "stp-acl", "functional.acl" })
    public void testGtExactMatch() throws Exception {
        SccpIncomingAcl acl = new SccpIncomingAcl();
        acl.setEnabled(true);
        acl.addRule(new IncomingAccessRule(100, IncomingAccessRule.Action.ALLOW, List.of("555"), null));

        assertEquals(acl.check(100, gtAddress("555", 8)), SccpIncomingAcl.Decision.ALLOW);
        assertEquals(acl.check(100, gtAddress("5551", 8)), SccpIncomingAcl.Decision.DENY_GT_MISMATCH);
    }

    @Test(groups = { "stp-acl", "functional.acl" })
    public void testSsnAllowList() throws Exception {
        SccpIncomingAcl acl = new SccpIncomingAcl();
        acl.setEnabled(true);
        acl.addRule(new IncomingAccessRule(100, IncomingAccessRule.Action.ALLOW, null, Set.of(8, 145)));

        assertEquals(acl.check(100, gtAddress("123", 8)), SccpIncomingAcl.Decision.ALLOW);
        assertEquals(acl.check(100, gtAddress("123", 9)), SccpIncomingAcl.Decision.DENY_SSN_MISMATCH);
    }

    @Test(groups = { "stp-acl", "functional.acl" })
    public void testGtAndSsnCombined() throws Exception {
        SccpIncomingAcl acl = new SccpIncomingAcl();
        acl.setEnabled(true);
        acl.addRule(new IncomingAccessRule(100, IncomingAccessRule.Action.ALLOW, List.of("12*"), Set.of(8)));

        assertEquals(acl.check(100, gtAddress("123", 8)), SccpIncomingAcl.Decision.ALLOW);
        assertEquals(acl.check(100, gtAddress("123", 9)), SccpIncomingAcl.Decision.DENY_SSN_MISMATCH);
        assertEquals(acl.check(100, gtAddress("999", 8)), SccpIncomingAcl.Decision.DENY_GT_MISMATCH);
    }

    @Test(groups = { "stp-acl", "functional.acl" })
    public void testXmlRoundTrip() throws Exception {
        SccpIncomingAcl acl = new SccpIncomingAcl();
        acl.setEnabled(true);
        acl.setDefaultDeny(false);
        acl.addRule(new IncomingAccessRule(100, IncomingAccessRule.Action.ALLOW, List.of("123*"), Set.of(8)));

        SccpIncomingAcl.State state = acl.exportState();
        String xml = SCCPJacksonXMLHelper.toXML(state);
        SccpIncomingAcl.State restored = SCCPJacksonXMLHelper.fromXML(xml, SccpIncomingAcl.State.class);

        assertTrue(restored.isEnabled());
        assertFalse(restored.isDefaultDeny());
        assertEquals(restored.getRules().size(), 1);
        IncomingAccessRule rule = restored.getRules().get(0);
        assertEquals(rule.getIncomingOpc(), 100);
        assertEquals(rule.getCalledGtPrefixes(), List.of("123*"));
        assertEquals(rule.getAllowedSsns(), Set.of(8));
    }

    @Test(groups = { "stp-acl", "functional.acl" })
    public void testRuleManagement() throws Exception {
        SccpIncomingAcl acl = new SccpIncomingAcl();
        acl.addRule(new IncomingAccessRule(100, IncomingAccessRule.Action.ALLOW, null, null));

        try {
            acl.addRule(new IncomingAccessRule(100, IncomingAccessRule.Action.DENY, null, null));
            fail("expected duplicate rule exception");
        } catch (Exception e) {
            // expected
        }

        assertEquals(acl.getRules().size(), 1);
        acl.modifyRule(new IncomingAccessRule(100, IncomingAccessRule.Action.DENY, null, null));
        assertEquals(acl.getRule(100).getAction(), IncomingAccessRule.Action.DENY);
        acl.removeRule(100);
        assertEquals(acl.getRules().size(), 0);
    }

    // __MORE_TESTS__
}
