package org.restcomm.protocols.ss7.scheduler;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class W2PriorityQueueTest {

    @Test
    public void shouldSelectPriorityThenEarliestDeadlineThenFifo() {
        W2PriorityQueue<String> queue = new W2PriorityQueue<>(8);
        assertTrue(queue.offer(new W2Work<>("dialog-low", W2Priority.LOW, 1L, "low")));
        assertTrue(queue.offer(new W2Work<>("dialog-high-late", W2Priority.HIGH, 100L, "high-late")));
        assertTrue(queue.offer(new W2Work<>("dialog-high-early", W2Priority.HIGH, 10L, "high-early")));
        assertTrue(queue.offer(new W2Work<>("dialog-high-early-second", W2Priority.HIGH, 10L, "high-early-second")));

        assertEquals(queue.poll().payload(), "high-early");
        assertEquals(queue.poll().payload(), "high-early-second");
        assertEquals(queue.poll().payload(), "high-late");
        assertEquals(queue.poll().payload(), "low");
        assertNull(queue.poll());
    }

    @Test
    public void shouldRejectWhenBoundedQueueIsFullAndReportMetrics() {
        W2PriorityQueue<String> queue = new W2PriorityQueue<>(1);
        assertTrue(queue.offer(new W2Work<>("dialog-1", W2Priority.NORMAL, Long.MAX_VALUE, "accepted")));
        assertFalse(queue.offer(new W2Work<>("dialog-2", W2Priority.CRITICAL, 1L, "rejected")));

        W2QueueMetrics beforePoll = queue.metrics();
        assertEquals(beforePoll.capacity(), 1);
        assertEquals(beforePoll.depth(), 1);
        assertEquals(beforePoll.admitted(), 1L);
        assertEquals(beforePoll.rejected(), 1L);
        assertEquals(beforePoll.polled(), 0L);

        assertEquals(queue.poll().payload(), "accepted");
        W2QueueMetrics afterPoll = queue.metrics();
        assertEquals(afterPoll.depth(), 0);
        assertEquals(afterPoll.polled(), 1L);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldRejectNonPositiveCapacity() {
        new W2PriorityQueue<String>(0);
    }
}
