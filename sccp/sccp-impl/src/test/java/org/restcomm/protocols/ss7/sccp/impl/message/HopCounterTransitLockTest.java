package org.restcomm.protocols.ss7.sccp.impl.message;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.sccp.impl.parameter.HopCounterImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.testng.annotations.Test;

/**
 * Locks the SCCP hop-counter decrement semantics on the Nextgen STP transit path.
 *
 * <p>The relayed (translated) hop counter MUST decrement once per transit and MUST
 * report a violation (false) when the value reaches zero — otherwise a misrouted GT
 * loop would circulate forever between STPs. These tests pin the behavior so a future
 * refactor cannot silently drop the guard.
 */
public class HopCounterTransitLockTest {

    private final ParameterFactoryImpl pf = new ParameterFactoryImpl();

    private SccpAddress addr(int pc, int ssn) {
        return new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null, pc, ssn);
    }

    private SccpConnCrMessageImpl msg(int hops) {
        return new SccpConnCrMessageImpl(1, 8, addr(2, 8), addr(1, 8), new HopCounterImpl(hops));
    }

    @Test(groups = { "stp-transit", "functional.route" })
    public void testHopCounterDecrementsOnEachTransit() {
        SccpConnCrMessageImpl m = msg(3);
        assertEquals(m.getHopCounter().getValue(), 3);

        assertTrue(m.reduceHopCounter(), "hop 3->2 must not violate");
        assertEquals(m.getHopCounter().getValue(), 2);

        assertTrue(m.reduceHopCounter(), "hop 2->1 must not violate");
        assertEquals(m.getHopCounter().getValue(), 1);
    }

    @Test(groups = { "stp-transit", "functional.route" })
    public void testHopCounterViolationAtZero() {
        SccpConnCrMessageImpl m = msg(1);
        // Last permitted hop: 1 -> 0 returns false (violation), value clamped at 0.
        assertFalse(m.reduceHopCounter(), "hop 1->0 must signal violation");
        assertEquals(m.getHopCounter().getValue(), 0);

        // Repeated calls stay clamped at zero and keep reporting violation.
        assertFalse(m.reduceHopCounter());
        assertEquals(m.getHopCounter().getValue(), 0);
    }

    @Test(groups = { "stp-transit", "functional.route" })
    public void testNullHopCounterNeverViolates() {
        // Unit-data messages (UDT/XUDT) carry no hop counter; the guard must be a no-op.
        SccpConnCrMessageImpl m = new SccpConnCrMessageImpl(1, 8, addr(2, 8), addr(1, 8), null);
        assertTrue(m.reduceHopCounter(), "null hop counter must not trigger a violation");
        assertTrue(m.reduceHopCounter());
    }

    @Test(groups = { "stp-transit", "functional.route" })
    public void testHopCounterTwoHopsThenViolation() {
        SccpConnCrMessageImpl m = msg(2);
        assertTrue(m.reduceHopCounter());  // 2 -> 1
        assertFalse(m.reduceHopCounter()); // 1 -> 0, violation
        assertEquals(m.getHopCounter().getValue(), 0);
    }
}
