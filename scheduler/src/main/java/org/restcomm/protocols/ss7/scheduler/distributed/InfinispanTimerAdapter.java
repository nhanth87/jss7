package org.restcomm.protocols.ss7.scheduler.distributed;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.scheduler.api.TimerCallback;
import org.restcomm.protocols.ss7.scheduler.api.TimerHandle;
import org.restcomm.protocols.ss7.scheduler.api.TimerRecord;
import org.restcomm.protocols.ss7.scheduler.api.TimerScheduler;
import org.restcomm.protocols.ss7.scheduler.distributed.event.TimerEventBus;

/**
 * Distributed timer scheduler backed by WildFly-managed Infinispan caches.
 * <p>
 * Uses per-entry TTL on the {@code jss7-timers} cache and a {@code jss7-timer-index} cache
 * mapping dialogId to timer ids. Expiration is driven by Infinispan's {@code @CacheEntryExpired}
 * listener ({@link TimerExpirationListener}). Callbacks remain node-local (not stored in the cache).
 */
public final class InfinispanTimerAdapter implements TimerScheduler {

    private static final Logger logger = Logger.getLogger(InfinispanTimerAdapter.class);

    private final InfinispanCacheResolver.ResolvedCache<Long, TimerRecord> timerCache;
    private final ConcurrentMap<Long, Set<Long>> indexCache;
    private final TimerEventBus eventBus;
    private final ConcurrentMap<Long, TimerCallback> callbacks = new ConcurrentHashMap<Long, TimerCallback>();
    private final ConcurrentMap<Long, InfinispanTimerHandle> handles = new ConcurrentHashMap<Long, InfinispanTimerHandle>();
    private final TimerExpirationListener expirationListener;
    private volatile boolean started;

    public InfinispanTimerAdapter(String containerJndiName, String timersCacheName, String indexCacheName,
            TimerEventBus eventBus) throws Exception {
        if (eventBus == null) {
            throw new IllegalArgumentException("eventBus is required");
        }
        this.eventBus = eventBus;
        InfinispanCacheResolver.ResolvedCache<Long, TimerRecord> timers =
                InfinispanCacheResolver.resolve(containerJndiName, timersCacheName);
        InfinispanCacheResolver.ResolvedCache<Long, Set<Long>> index =
                InfinispanCacheResolver.resolve(containerJndiName, indexCacheName);
        this.timerCache = timers;
        this.indexCache = index.map;
        this.expirationListener = new TimerExpirationListener(eventBus, indexCache, this::fireExpired);
        InfinispanCacheResolver.registerListener(timers.map, expirationListener);
        logger.info("InfinispanTimerAdapter bound to " + containerJndiName
                + " caches [" + timersCacheName + ", " + indexCacheName + "]");
    }

    /**
     * Package-private constructor for tests with injected mock caches.
     */
    InfinispanTimerAdapter(InfinispanCacheResolver.ResolvedCache<Long, TimerRecord> timerCache,
            ConcurrentMap<Long, Set<Long>> indexCache, TimerEventBus eventBus) {
        this.timerCache = timerCache;
        this.indexCache = indexCache;
        this.eventBus = eventBus;
        this.expirationListener = new TimerExpirationListener(eventBus, indexCache, this::fireExpired);
    }

    @Override
    public TimerHandle schedule(TimerRecord record, long delayMillis, TimerCallback callback) {
        if (!started) {
            throw new IllegalStateException("TimerScheduler is not started");
        }
        if (record == null || callback == null) {
            throw new IllegalArgumentException("record and callback are required");
        }
        if (delayMillis <= 0L) {
            throw new IllegalArgumentException("delayMillis must be positive");
        }

        long timerId = record.getTimerId();
        InfinispanTimerHandle handle = new InfinispanTimerHandle();
        handles.put(timerId, handle);
        callbacks.put(timerId, callback);
        timerCache.putWithTtl(timerId, record, delayMillis);
        indexTimer(record.getDialogId(), timerId);
        return handle;
    }

    @Override
    public void cancel(long timerId) {
        InfinispanTimerHandle handle = handles.remove(timerId);
        callbacks.remove(timerId);
        TimerRecord removed = timerCache.map.remove(timerId);
        if (handle != null) {
            handle.cancel();
        }
        if (removed != null) {
            unindexTimer(removed.getDialogId(), timerId);
        }
    }

    @Override
    public void cancelAll(long dialogId) {
        Set<Long> timerIds = indexCache.remove(dialogId);
        if (timerIds == null || timerIds.isEmpty()) {
            return;
        }
        for (Long timerId : new HashSet<Long>(timerIds)) {
            cancel(timerId);
        }
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void stop() {
        started = false;
        callbacks.clear();
        handles.clear();
    }

    boolean isActive(long timerId) {
        return timerId > 0L && timerCache.map.containsKey(timerId);
    }

    void notifyExpiredForTest(long timerId, TimerRecord record) {
        expirationListener.dispatch(timerId, record);
    }

    private void fireExpired(long timerId, TimerRecord record) {
        InfinispanTimerHandle handle = handles.remove(timerId);
        TimerCallback callback = callbacks.remove(timerId);
        if (handle == null || callback == null) {
            return;
        }
        if (handle.markFired()) {
            try {
                callback.onTimerFire(record);
            } catch (Exception e) {
                logger.error("Timer callback failed for timerId=" + timerId, e);
            }
        }
    }

    private void indexTimer(long dialogId, long timerId) {
        indexCache.compute(dialogId, (key, existing) -> {
            Set<Long> set = existing;
            if (set == null) {
                set = ConcurrentHashMap.newKeySet();
            } else if (!(set instanceof java.util.concurrent.ConcurrentHashMap.KeySetView)) {
                Set<Long> copy = ConcurrentHashMap.newKeySet();
                copy.addAll(existing);
                set = copy;
            }
            set.add(timerId);
            return set;
        });
    }

    private void unindexTimer(long dialogId, long timerId) {
        indexCache.computeIfPresent(dialogId, (key, existing) -> {
            existing.remove(timerId);
            return existing.isEmpty() ? null : existing;
        });
    }
}
