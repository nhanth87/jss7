package org.restcomm.protocols.ss7.scheduler.w2;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.restcomm.protocols.ss7.scheduler.W2Priority;
import org.restcomm.protocols.ss7.scheduler.W2Work;
import org.testng.annotations.Test;

public class W2DispatcherTest {

    @Test
    public void shouldDispatchPriorityThenDeadlineThenFifo() throws Exception {
        List<String> execution = new ArrayList<>();
        CountDownLatch completed = new CountDownLatch(4);
        try (W2Dispatcher dispatcher = new W2Dispatcher(8, "w2-order-test")) {
            assertTrue(dispatcher.submit(work("low", W2Priority.LOW, 1L, execution, completed)));
            assertTrue(dispatcher.submit(work("high-late", W2Priority.HIGH, 100L, execution, completed)));
            assertTrue(dispatcher.submit(work("high-early", W2Priority.HIGH, 10L, execution, completed)));
            assertTrue(dispatcher.submit(work("high-early-second", W2Priority.HIGH, 10L, execution, completed)));
            dispatcher.start();
            assertTrue(completed.await(2, TimeUnit.SECONDS));
        }
        assertEquals(execution, List.of("high-early", "high-early-second", "high-late", "low"));
    }

    @Test
    public void shouldDrainAcceptedWorkThenRejectAfterStop() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        W2Dispatcher dispatcher = new W2Dispatcher(1, "w2-stop-test");
        assertTrue(dispatcher.submit(new W2Work<>("dialog", W2Priority.NORMAL, 1L, completed::countDown)));
        dispatcher.start();
        dispatcher.stop();

        assertTrue(completed.await(100, TimeUnit.MILLISECONDS));
        assertFalse(dispatcher.submit(new W2Work<>("dialog", W2Priority.CRITICAL, 1L, () -> { })));
        W2DispatcherMetrics metrics = dispatcher.metrics();
        assertFalse(metrics.accepting());
        assertFalse(metrics.running());
        assertEquals(metrics.dispatched(), 1L);
    }

    @Test
    public void shouldDrainAcceptedWorkWhenStoppedBeforeStart() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        W2Dispatcher dispatcher = new W2Dispatcher(1, "w2-pre-start-stop-test");
        assertTrue(dispatcher.submit(new W2Work<>("dialog", W2Priority.NORMAL, 1L, completed::countDown)));
        dispatcher.stop();

        assertTrue(completed.await(100, TimeUnit.MILLISECONDS));
        assertEquals(dispatcher.metrics().dispatched(), 1L);
        assertFalse(dispatcher.metrics().running());
    }

    @Test
    public void shouldRejectWhenCapacityIsExhausted() {
        try (W2Dispatcher dispatcher = new W2Dispatcher(1, "w2-capacity-test")) {
            assertTrue(dispatcher.submit(new W2Work<>("accepted", W2Priority.NORMAL, 1L, () -> { })));
            assertFalse(dispatcher.submit(new W2Work<>("rejected", W2Priority.CRITICAL, 1L, () -> { })));
            assertEquals(dispatcher.metrics().rejected(), 1L);
        }
    }

    @Test
    public void shouldContinueAfterPayloadFailure() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        try (W2Dispatcher dispatcher = new W2Dispatcher(2, "w2-failure-test")) {
            assertTrue(dispatcher.submit(new W2Work<>("bad", W2Priority.HIGH, 1L,
                    () -> { throw new IllegalStateException("expected"); })));
            assertTrue(dispatcher.submit(new W2Work<>("good", W2Priority.NORMAL, 2L, completed::countDown)));
            dispatcher.start();
            assertTrue(completed.await(2, TimeUnit.SECONDS));
            dispatcher.stop();
            W2DispatcherMetrics metrics = dispatcher.metrics();
            assertEquals(metrics.dispatched(), 2L);
            assertEquals(metrics.failed(), 1L);
        }
    }

    private static W2Work<Runnable> work(String name, W2Priority priority, long deadline, List<String> execution,
            CountDownLatch completed) {
        return new W2Work<>(name, priority, deadline, () -> {
            execution.add(name);
            completed.countDown();
        });
    }
}
