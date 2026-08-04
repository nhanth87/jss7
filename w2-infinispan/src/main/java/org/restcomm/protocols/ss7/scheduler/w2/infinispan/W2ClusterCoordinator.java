package org.restcomm.protocols.ss7.scheduler.w2.infinispan;

import java.util.Objects;
import java.util.function.LongSupplier;

import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * Infinispan-backed control plane for W2 ordering-key ownership.
 *
 * <p>This coordinator persists only small lease metadata as opaque bytes (see {@link W2Lease#encode()}).
 * Work payloads and local mailboxes remain local and are deliberately never used as a distributed
 * priority queue. Lease updates use Infinispan conditional compare-and-set operations. An execution
 * integration must reject side effects made with a stale lease epoch.</p>
 */
public final class W2ClusterCoordinator {

    public static final String DEFAULT_LEASE_CACHE = "w2-leases";

    private final String nodeId;
    private final long leaseDurationMs;
    private final LongSupplier clockEpochMs;
    private final AdvancedCache<String, byte[]> leases;

    public W2ClusterCoordinator(EmbeddedCacheManager cacheManager, String nodeId, long leaseDurationMs) {
        this(cacheManager, DEFAULT_LEASE_CACHE, nodeId, leaseDurationMs, System::currentTimeMillis);
    }

    public W2ClusterCoordinator(EmbeddedCacheManager cacheManager, String cacheName, String nodeId,
            long leaseDurationMs, LongSupplier clockEpochMs) {
        Objects.requireNonNull(cacheManager, "cacheManager");
        if (cacheName == null || cacheName.isBlank()) {
            throw new IllegalArgumentException("cacheName must not be blank");
        }
        if (nodeId == null || nodeId.isBlank()) {
            throw new IllegalArgumentException("nodeId must not be blank");
        }
        if (leaseDurationMs <= 0) {
            throw new IllegalArgumentException("leaseDurationMs must be positive");
        }
        this.nodeId = nodeId;
        this.leaseDurationMs = leaseDurationMs;
        this.clockEpochMs = Objects.requireNonNull(clockEpochMs, "clockEpochMs");
        if (cacheManager.getCacheConfiguration(cacheName) == null) {
            cacheManager.defineConfiguration(cacheName, new ConfigurationBuilder()
                    .clustering().cacheMode(CacheMode.REPL_SYNC).build());
        }
        Cache<String, byte[]> cache = cacheManager.getCache(cacheName);
        this.leases = cache.getAdvancedCache();
    }

    /** Acquires an expired/unowned key or renews this node's current lease. */
    public W2LeaseResult acquireOrRenew(String orderingKey) {
        requireOrderingKey(orderingKey);
        for (;;) {
            long now = clockEpochMs.getAsLong();
            byte[] currentBytes = leases.get(orderingKey);
            W2Lease current = currentBytes == null ? null : W2Lease.decode(currentBytes);
            if (current != null && !current.isExpired(now) && !current.ownerNodeId().equals(nodeId)) {
                return W2LeaseResult.denied(current);
            }
            long epoch = current != null && current.ownerNodeId().equals(nodeId) ? current.epoch()
                    : current == null ? 1L : current.epoch() + 1L;
            W2Lease next = new W2Lease(nodeId, epoch, Math.addExact(now, leaseDurationMs));
            byte[] nextBytes = next.encode();
            boolean ok = currentBytes == null
                    ? leases.putIfAbsent(orderingKey, nextBytes) == null
                    : leases.replace(orderingKey, currentBytes, nextBytes);
            if (ok) {
                return W2LeaseResult.acquired(next);
            }
        }
    }

    /** Returns true only when this node still holds the supplied fencing epoch. */
    public boolean owns(String orderingKey, long epoch) {
        requireOrderingKey(orderingKey);
        byte[] encoded = leases.get(orderingKey);
        if (encoded == null) {
            return false;
        }
        W2Lease lease = W2Lease.decode(encoded);
        return lease.isHeldBy(nodeId, epoch, clockEpochMs.getAsLong());
    }

    /** Removes only this node's current epoch; it cannot erase a newer owner's lease. */
    public boolean release(String orderingKey, long epoch) {
        requireOrderingKey(orderingKey);
        byte[] currentBytes = leases.get(orderingKey);
        if (currentBytes == null) {
            return false;
        }
        W2Lease current = W2Lease.decode(currentBytes);
        return current.ownerNodeId().equals(nodeId) && current.epoch() == epoch
                && leases.remove(orderingKey, currentBytes);
    }

    public W2Lease currentLease(String orderingKey) {
        requireOrderingKey(orderingKey);
        byte[] encoded = leases.get(orderingKey);
        return encoded == null ? null : W2Lease.decode(encoded);
    }

    private static void requireOrderingKey(String orderingKey) {
        if (orderingKey == null || orderingKey.isBlank()) {
            throw new IllegalArgumentException("orderingKey must not be blank");
        }
    }
}
