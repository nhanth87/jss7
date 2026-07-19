package org.restcomm.protocols.ss7.scheduler.w2.infinispan;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicLong;

import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.testng.annotations.Test;

public class W2ClusterCoordinatorTest {

    @Test
    public void shouldFenceAnExpiredOwnerAndRejectStaleRelease() throws Exception {
        AtomicLong clock = new AtomicLong(1_000L);
        try (DefaultCacheManager manager = clusteredManager()) {
            W2ClusterCoordinator nodeA = new W2ClusterCoordinator(manager, "test-leases", "node-a", 100L, clock::get);
            W2ClusterCoordinator nodeB = new W2ClusterCoordinator(manager, "test-leases", "node-b", 100L, clock::get);

            W2Lease first = nodeA.acquireOrRenew("account-42").lease();
            assertTrue(nodeA.owns("account-42", first.epoch()));
            assertFalse(nodeB.acquireOrRenew("account-42").acquired());

            clock.set(1_100L);
            W2Lease takeover = nodeB.acquireOrRenew("account-42").lease();
            assertEquals(takeover.epoch(), first.epoch() + 1L);
            assertTrue(nodeB.owns("account-42", takeover.epoch()));
            assertFalse(nodeA.owns("account-42", first.epoch()));
            assertFalse(nodeA.release("account-42", first.epoch()));
            assertTrue(nodeB.release("account-42", takeover.epoch()));
        }
    }

    @Test
    public void shouldRenewWithoutChangingItsFenceEpoch() throws Exception {
        AtomicLong clock = new AtomicLong(1_000L);
        try (DefaultCacheManager manager = clusteredManager()) {
            W2ClusterCoordinator node = new W2ClusterCoordinator(manager, "test-renew", "node-a", 100L, clock::get);
            W2Lease first = node.acquireOrRenew("session-1").lease();
            clock.set(1_050L);
            W2Lease renewed = node.acquireOrRenew("session-1").lease();

            assertEquals(renewed.epoch(), first.epoch());
            assertEquals(renewed.expiresAtEpochMs(), 1_150L);
        }
    }

    private static DefaultCacheManager clusteredManager() {
        return new DefaultCacheManager(GlobalConfigurationBuilder.defaultClusteredBuilder().build());
    }
}
