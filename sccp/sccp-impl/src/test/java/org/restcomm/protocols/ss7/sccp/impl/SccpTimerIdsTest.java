package org.restcomm.protocols.ss7.sccp.impl;

import org.restcomm.protocols.ss7.scheduler.api.TimerType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SccpTimerIdsTest {

    @Test
    public void connectionTimerIdsAreUniquePerSlot() {
        long connId = 42L;
        long est = SccpTimerIds.connectionTimerId(connId, SccpTimerIds.SLOT_CONN_EST);
        long ias = SccpTimerIds.connectionTimerId(connId, SccpTimerIds.SLOT_IAS);
        Assert.assertNotEquals(est, ias);
        Assert.assertEquals(est >> 8, connId);
        Assert.assertEquals(ias >> 8, connId);
    }

    @Test
    public void connectionTimerIdsDifferAcrossConnections() {
        long estA = SccpTimerIds.connectionTimerId(1L, SccpTimerIds.SLOT_CONN_EST);
        long estB = SccpTimerIds.connectionTimerId(2L, SccpTimerIds.SLOT_CONN_EST);
        Assert.assertNotEquals(estA, estB);
    }

    @Test
    public void reassemblyTimerUsesDistinctScope() {
        int segRef = 7;
        long scopeId = SccpTimerIds.reassemblyScopeId(segRef);
        Assert.assertNotEquals(scopeId, SccpTimerIds.connectionScopeId(segRef));
        Assert.assertEquals(SccpTimerIds.reassemblyTimerId(segRef) >> 8, scopeId);
    }

    @Test
    public void newConnectionRecordCarriesTimerType() {
        long delay = 15000L;
        Assert.assertEquals(
                SccpTimerIds.newConnectionRecord(10L, SccpTimerIds.SLOT_REL, TimerType.SCCP_REL, delay).getTimerType(),
                TimerType.SCCP_REL);
        Assert.assertEquals(
                SccpTimerIds.newReassemblyRecord(3, delay).getTimerType(),
                TimerType.SCCP_REASSEMBLY);
    }
}
