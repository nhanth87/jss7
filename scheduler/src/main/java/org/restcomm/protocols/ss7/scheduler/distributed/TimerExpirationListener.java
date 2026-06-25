package org.restcomm.protocols.ss7.scheduler.distributed;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryExpired;
import org.infinispan.notifications.cachelistener.event.CacheEntryExpiredEvent;
import org.restcomm.protocols.ss7.scheduler.api.TimerRecord;
import org.restcomm.protocols.ss7.scheduler.distributed.event.TimerEventBus;
import org.restcomm.protocols.ss7.scheduler.distributed.event.TimerExpiredEvent;

/**
 * Infinispan {@link CacheEntryExpired} listener that publishes {@link TimerExpiredEvent} and
 * maintains the dialog index cache.
 */
@Listener
public final class TimerExpirationListener {

    private static final Logger LOGGER = Logger.getLogger(TimerExpirationListener.class.getName());

    private final TimerEventBus eventBus;
    private final ConcurrentMap<Long, Set<Long>> indexCache;
    private final ExpirationHandler expirationHandler;

    public interface ExpirationHandler {
        void onExpired(long timerId, TimerRecord record);
    }

    public TimerExpirationListener(TimerEventBus eventBus, ConcurrentMap<Long, Set<Long>> indexCache,
            ExpirationHandler expirationHandler) {
        if (eventBus == null || indexCache == null || expirationHandler == null) {
            throw new IllegalArgumentException("eventBus, indexCache and expirationHandler are required");
        }
        this.eventBus = eventBus;
        this.indexCache = indexCache;
        this.expirationHandler = expirationHandler;
    }

    @CacheEntryExpired
    public void onTimerExpired(CacheEntryExpiredEvent<Long, TimerRecord> event) {
        if (event.isPre()) {
            return;
        }
        Long timerId = event.getKey();
        TimerRecord record = event.getValue();
        if (timerId == null) {
            return;
        }
        dispatch(timerId, record);
    }

    void dispatch(long timerId, TimerRecord record) {
        long dialogId = record != null ? record.getDialogId() : 0L;
        if (dialogId != 0L) {
            unindexTimer(dialogId, timerId);
        }
        expirationHandler.onExpired(timerId, record);
        eventBus.publish(new TimerExpiredEvent(timerId, dialogId, System.currentTimeMillis(), record));
        LOGGER.fine("Timer expired via Infinispan: " + timerId + " dialog=" + dialogId);
    }

    private void unindexTimer(long dialogId, long timerId) {
        indexCache.computeIfPresent(dialogId, (key, existing) -> {
            existing.remove(timerId);
            return existing.isEmpty() ? null : existing;
        });
    }
}
