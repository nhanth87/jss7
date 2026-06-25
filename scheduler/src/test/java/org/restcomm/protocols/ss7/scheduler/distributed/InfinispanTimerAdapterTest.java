package org.restcomm.protocols.ss7.scheduler.distributed;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.restcomm.protocols.ss7.scheduler.api.TimerCallback;
import org.restcomm.protocols.ss7.scheduler.api.TimerHandle;
import org.restcomm.protocols.ss7.scheduler.api.TimerRecord;
import org.restcomm.protocols.ss7.scheduler.api.TimerType;
import org.restcomm.protocols.ss7.scheduler.distributed.event.TimerEventBus;
import org.restcomm.protocols.ss7.scheduler.distributed.event.TimerExpiredEvent;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class InfinispanTimerAdapterTest {

    private TimerEventBus eventBus;
    private TtlMockCache<Long, TimerRecord> timerCache;
    private ConcurrentHashMap<Long, Set<Long>> indexCache;
    private InfinispanTimerAdapter adapter;

    @BeforeMethod
    public void setUp() {
        eventBus = new TimerEventBus();
        timerCache = new TtlMockCache<Long, TimerRecord>();
        indexCache = new ConcurrentHashMap<Long, Set<Long>>();
        adapter = new InfinispanTimerAdapter(timerCache.asResolvedCache(), indexCache, eventBus);
        adapter.start();
    }

    @AfterMethod
    public void tearDown() {
        adapter.stop();
        InfinispanTimerFactory.reset();
    }

    @Test
    public void scheduleStoresRecordWithTtlAndIndex() {
        TimerRecord record = new TimerRecord(101L, 1001L, TimerType.TCAP_INVOKE_TIMEOUT, 0L, "node-a", 1, 0L);
        TimerHandle handle = adapter.schedule(record, 500L, new TimerCallback() {
            @Override
            public void onTimerFire(TimerRecord firedRecord) {
            }
        });

        assertFalse(handle.isCancelled());
        assertFalse(handle.hasFired());
        assertTrue(adapter.isActive(101L));

        TimerRecord stored = timerCache.get(101L);
        assertNotNull(stored);
        assertEquals(stored.getDialogId(), 1001L);

        Set<Long> indexed = indexCache.get(1001L);
        assertNotNull(indexed);
        assertTrue(indexed.contains(101L));
    }

    @Test
    public void cancelRemovesTimerAndIndexEntry() {
        TimerRecord record = new TimerRecord(102L, 1002L, TimerType.TCAP_DIALOG_TIMEOUT, 0L, "node-a", 1, 0L);
        adapter.schedule(record, 1000L, new TimerCallback() {
            @Override
            public void onTimerFire(TimerRecord firedRecord) {
            }
        });

        adapter.cancel(102L);
        assertNull(timerCache.get(102L));
        assertNull(indexCache.get(1002L));
        assertFalse(adapter.isActive(102L));
    }

    @Test
    public void cancelAllForDialogRemovesAllTimers() {
        TimerRecord first = new TimerRecord(103L, 1003L, TimerType.MAP_T_GUARD_SHORT, 0L, "node-a", 1, 0L);
        TimerRecord second = new TimerRecord(104L, 1003L, TimerType.MAP_T_GUARD_LONG, 0L, "node-a", 1, 0L);
        TimerRecord other = new TimerRecord(105L, 1004L, TimerType.CAP_T_SHORT, 0L, "node-a", 1, 0L);

        adapter.schedule(first, 1000L, new TimerCallback() {
            @Override
            public void onTimerFire(TimerRecord firedRecord) {
            }
        });
        adapter.schedule(second, 1000L, new TimerCallback() {
            @Override
            public void onTimerFire(TimerRecord firedRecord) {
            }
        });
        adapter.schedule(other, 1000L, new TimerCallback() {
            @Override
            public void onTimerFire(TimerRecord firedRecord) {
            }
        });

        adapter.cancelAll(1003L);
        assertNull(timerCache.get(103L));
        assertNull(timerCache.get(104L));
        assertNull(indexCache.get(1003L));
        assertNotNull(timerCache.get(105L));
    }

    @Test
    public void expirationFiresCallbackAndPublishesEvent() throws Exception {
        final CountDownLatch fired = new CountDownLatch(1);
        final AtomicBoolean sawRecord = new AtomicBoolean(false);
        final AtomicReference<TimerExpiredEvent> captured = new AtomicReference<TimerExpiredEvent>();
        eventBus.subscribe(captured::set);

        TimerRecord record = new TimerRecord(106L, 1005L, TimerType.CAP_T_MEDIUM, 0L, "node-a", 1, 0L);
        TimerHandle handle = adapter.schedule(record, 50L, new TimerCallback() {
            @Override
            public void onTimerFire(TimerRecord firedRecord) {
                sawRecord.set(firedRecord.getTimerId() == 106L);
                fired.countDown();
            }
        });

        Thread.sleep(80L);
        timerCache.expireDueEntries();
        for (java.util.Map.Entry<Long, TimerRecord> entry : timerCache.drainExpiredEntries()) {
            adapter.notifyExpiredForTest(entry.getKey(), entry.getValue());
        }

        assertTrue(fired.await(2, TimeUnit.SECONDS));
        assertTrue(sawRecord.get());
        assertTrue(handle.hasFired());
        TimerExpiredEvent event = captured.get();
        assertNotNull(event);
        assertEquals(event.getTimerId(), 106L);
        assertEquals(event.getDialogId(), 1005L);
        assertNull(indexCache.get(1005L));
    }
}
