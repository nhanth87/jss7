package org.restcomm.protocols.ss7.scheduler.w2;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.restcomm.protocols.ss7.scheduler.W2Priority;
import org.restcomm.protocols.ss7.scheduler.W2TrafficClass;
import org.restcomm.protocols.ss7.scheduler.W2Work;
import org.restcomm.protocols.ss7.scheduler.W2WorkMetadata;
import org.testng.annotations.Test;

public class W2AdmissionControllerTest {

    @Test
    public void shouldEnforceOrderingKeyThenTenantThenGlobalLimitsAndReleaseCapacity() {
        W2AdmissionController controller = new W2AdmissionController(new W2AdmissionLimits(3, 2, 1));
        W2Work<Runnable> tenantAKey1 = work("key-1", "tenant-a");
        W2Work<Runnable> tenantAKey2 = work("key-2", "tenant-a");
        W2Work<Runnable> tenantAKey3 = work("key-3", "tenant-a");
        W2Work<Runnable> tenantBKey1 = work("key-1", "tenant-b");
        W2Work<Runnable> tenantBKey2 = work("key-4", "tenant-b");

        assertTrue(controller.admit(tenantAKey1).accepted());
        assertEquals(controller.admit(tenantBKey1).decision(), W2AdmissionDecision.REJECTED_ORDERING_KEY_CAPACITY);
        assertTrue(controller.admit(tenantAKey2).accepted());
        assertEquals(controller.admit(work("key-2", "tenant-b")).decision(),
                W2AdmissionDecision.REJECTED_ORDERING_KEY_CAPACITY);
        assertEquals(controller.admit(tenantAKey3).decision(), W2AdmissionDecision.REJECTED_TENANT_CAPACITY);
        assertTrue(controller.admit(tenantBKey2).accepted());
        assertEquals(controller.globalDepth(), 3);

        controller.release(tenantAKey1);
        assertTrue(controller.admit(tenantBKey1).accepted());
        assertEquals(controller.globalDepth(), 3);
    }

    private static W2Work<Runnable> work(String orderingKey, String tenantKey) {
        return new W2Work<>(orderingKey,
                new W2WorkMetadata(tenantKey, W2TrafficClass.INTERACTIVE,
                        org.restcomm.protocols.ss7.scheduler.W2CostHint.SMALL,
                        org.restcomm.protocols.ss7.scheduler.W2ExecutionProfile.NETWORK_IO),
                W2Priority.NORMAL, Long.MAX_VALUE, () -> { });
    }
}
