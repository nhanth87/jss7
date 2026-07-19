package org.restcomm.protocols.ss7.scheduler;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bounded, deterministic W2 work-selection queue.
 *
 * <p>Selection is strict business priority descending, then earliest monotonic
 * deadline, then submission FIFO. It deliberately does not run work or reorder
 * events inside a TCAP dialog; the future keyed-mailbox adapter owns that
 * protocol-ordering responsibility.</p>
 */
public final class W2PriorityQueue<T> {

    private static final class Entry<T> {
        private final W2Work<T> work;
        private final long sequence;

        private Entry(W2Work<T> work, long sequence) {
            this.work = work;
            this.sequence = sequence;
        }
    }

    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final PriorityQueue<Entry<T>> queue = new PriorityQueue<>(new EntryComparator<>());
    private long sequence;
    private long admitted;
    private long rejected;
    private long polled;

    public W2PriorityQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
    }

    /**
     * Attempts admission without blocking an SS7 ingress thread.
     *
     * @return true when admitted; false when the bounded queue is full
     */
    public boolean offer(W2Work<T> work) {
        if (work == null) {
            throw new NullPointerException("work");
        }
        lock.lock();
        try {
            if (queue.size() == capacity) {
                rejected++;
                return false;
            }
            queue.offer(new Entry<>(work, sequence++));
            admitted++;
            return true;
        } finally {
            lock.unlock();
        }
    }

    /** Returns the next selected work item, or null if no item is ready. */
    public W2Work<T> poll() {
        lock.lock();
        try {
            Entry<T> entry = queue.poll();
            if (entry == null) {
                return null;
            }
            polled++;
            return entry.work;
        } finally {
            lock.unlock();
        }
    }

    public W2QueueMetrics metrics() {
        lock.lock();
        try {
            return new W2QueueMetrics(capacity, queue.size(), admitted, rejected, polled);
        } finally {
            lock.unlock();
        }
    }

    private static final class EntryComparator<T> implements Comparator<Entry<T>> {
        @Override
        public int compare(Entry<T> left, Entry<T> right) {
            int priority = Integer.compare(right.work.priority().score(), left.work.priority().score());
            if (priority != 0) {
                return priority;
            }
            int deadline = Long.compare(left.work.deadlineNanos(), right.work.deadlineNanos());
            if (deadline != 0) {
                return deadline;
            }
            return Long.compare(left.sequence, right.sequence);
        }
    }
}
