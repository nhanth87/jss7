package org.restcomm.protocols.ss7.tcap;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.restcomm.protocols.ss7.sccp.impl.SccpHarness;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Regression: stop must be safe when TCAP was never started (simulator MapMan after
 * MAP skipped TCAP start) and when stop is invoked twice (Ss7Stack map then tcap).
 */
public class TCAPProviderStopTest extends SccpHarness {

    private TCAPStackImpl tcapStack;

    @BeforeMethod
    public void setUp() throws Exception {
        this.sccpStack1Name = "TCAPProviderStopTestSccp1";
        this.sccpStack2Name = "TCAPProviderStopTestSccp2";
        super.setUp();
    }

    @AfterMethod
    public void tearDown() {
        if (tcapStack != null) {
            try {
                tcapStack.stop();
            } catch (Exception ignore) {
            }
            tcapStack = null;
        }
        try {
            super.tearDown();
        } catch (Exception ignore) {
        }
    }

    @Test
    public void stopWithoutStartDoesNotThrow() {
        tcapStack = new TCAPStackImpl("stop-no-start", super.sccpProvider1, 8);
        assertFalse(tcapStack.isStarted());
        tcapStack.stop();
        tcapStack.stop();
        assertFalse(tcapStack.isStarted());
    }

    @Test
    public void startThenDoubleStopIsIdempotent() throws Exception {
        tcapStack = new TCAPStackImpl("stop-double", super.sccpProvider1, 8);
        tcapStack.start();
        assertTrue(tcapStack.isStarted());
        tcapStack.stop();
        assertFalse(tcapStack.isStarted());
        tcapStack.stop();
        assertFalse(tcapStack.isStarted());
    }
}
