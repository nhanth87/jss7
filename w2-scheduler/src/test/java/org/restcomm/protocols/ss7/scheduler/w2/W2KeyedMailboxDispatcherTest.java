package org.restcomm.protocols.ss7.scheduler.w2;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.restcomm.protocols.ss7.scheduler.W2Priority;
import org.restcomm.protocols.ss7.scheduler.W2Work;
import org.testng.annotations.Test;

public class W2KeyedMailboxDispatcherTest {

    @Test
    public void shouldChooseBetweenMailboxHeadsButNeverReorderInsideDialog() throws Exception {
        List<String> execution = new ArrayList<>();
        CountDownLatch completed = new CountDownLatch(3);
        try (W2KeyedMailboxDispatcher dispatcher = new W2KeyedMailboxDispatcher(3, "w2-keyed-order-test")) {
            assertTrue(dispatcher.submit(work("dialog-a", "a-first", W2Priority.LOW, 1L, execution, completed)));
            assertTrue(dispatcher.submit(work("dialog-a", "a-second", W2Priority.CRITICAL, 1L, execution, completed)));
            assertTrue(dispatcher.submit(work("dialog-b", "b-first", W2Priority.HIGH, 1L, execution, completed)));
            dispatcher.start();
            assertTrue(completed.await(2, TimeUnit.SECONDS));
        }
        assertEquals(execution, List.of("b-first", "a-first", "a-second"));
    }

    @Test
    public void shouldDrainAllDialogMailboxesAndRejectAfterStop() throws Exception {
        CountDownLatch completed = new CountDownLatch(2);
        W2KeyedMailboxDispatcher dispatcher = new W2KeyedMailboxDispatcher(2, "w2-keyed-stop-test");
        assertTrue(dispatcher.submit(work("dialog-a", "a", W2Priority.NORMAL, 1L, new ArrayList<>(), completed)));
        assertTrue(dispatcher.submit(work("dialog-b", "b", W2Priority.NORMAL, 1L, new ArrayList<>(), completed)));
        dispatcher.stop();

        assertTrue(completed.await(100, TimeUnit.MILLISECONDS));
        assertFalse(dispatcher.submit(work("dialog-c", "c", W2Priority.CRITICAL, 1L, new ArrayList<>(), new CountDownLatch(1))));
        W2KeyedMailboxMetrics metrics = dispatcher.metrics();
        assertEquals(metrics.depth(), 0);
        assertEquals(metrics.activeMailboxes(), 0);
        assertEquals(metrics.dispatched(), 2L);
        assertFalse(metrics.accepting());
        assertFalse(metrics.running());
    }

    @Test
    public void shouldRejectAtGlobalCapacityAndContinueAfterFailure() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        try (W2KeyedMailboxDispatcher dispatcher = new W2KeyedMailboxDispatcher(2, "w2-keyed-failure-test")) {
            assertTrue(dispatcher.submit(new W2Work<>("dialog-a", W2Priority.HIGH, 1L,
                    () -> { throw new IllegalStateException("expected"); })));
            assertTrue(dispatcher.submit(work("dialog-b", "good", W2Priority.NORMAL, 2L, new ArrayList<>(), completed)));
            assertFalse(dispatcher.submit(work("dialog-c", "full", W2Priority.CRITICAL, 1L, new ArrayList<>(), new CountDownLatch(1))));
            dispatcher.start();
            assertTrue(completed.await(2, TimeUnit.SECONDS));
            dispatcher.stop();
            W2KeyedMailboxMetrics metrics = dispatcher.metrics();
            assertEquals(metrics.rejected(), 1L);
            assertEquals(metrics.dispatched(), 2L);
            assertEquals(metrics.failed(), 1L);
        }
    }

    @Test
    public void shouldDrainDifferentDialogMailboxesInParallelButKeepEachDialogSerial() throws Exception {
        CountDownLatch started = new CountDownLatch(2);
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch completed = new CountDownLatch(4);
        AtomicInteger active = new AtomicInteger();
        AtomicInteger peakActive = new AtomicInteger();
        List<String> execution = Collections.synchronizedList(new ArrayList<>());
        try (W2KeyedMailboxDispatcher dispatcher = new W2KeyedMailboxDispatcher(4, 2, "w2-keyed-parallel-test")) {
            assertTrue(dispatcher.submit(blockingWork("dialog-a", "a-first", started, release, completed, active, peakActive, execution)));
            assertTrue(dispatcher.submit(work("dialog-a", "a-second", W2Priority.CRITICAL, 1L, execution, completed)));
            assertTrue(dispatcher.submit(blockingWork("dialog-b", "b-first", started, release, completed, active, peakActive, execution)));
            assertTrue(dispatcher.submit(work("dialog-b", "b-second", W2Priority.CRITICAL, 1L, execution, completed)));
            dispatcher.start();
            assertTrue(started.await(2, TimeUnit.SECONDS));
            release.countDown();
            assertTrue(completed.await(2, TimeUnit.SECONDS));
        }
        assertEquals(peakActive.get(), 2);
        assertTrue(execution.indexOf("a-first") < execution.indexOf("a-second"));
        assertTrue(execution.indexOf("b-first") < execution.indexOf("b-second"));
    }

    private static W2Work<Runnable> blockingWork(String dialogKey, String name, CountDownLatch started,
            CountDownLatch release, CountDownLatch completed, AtomicInteger active, AtomicInteger peakActive,
            List<String> execution) {
        return new W2Work<>(dialogKey, W2Priority.NORMAL, 1L, () -> {
            int current = active.incrementAndGet();
            peakActive.accumulateAndGet(current, Math::max);
            started.countDown();
            try {
                assertTrue(release.await(2, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AssertionError(e);
            } finally {
                active.decrementAndGet();
            }
            execution.add(name);
            completed.countDown();
        });
    }

    private static W2Work<Runnable> work(String dialogKey, String name, W2Priority priority, long deadline,
            List<String> execution, CountDownLatch completed) {
        return new W2Work<>(dialogKey, priority, deadline, () -> {
            execution.add(name);
            completed.countDown();
        });
    }
}
