package org.restcomm.protocols.ss7.scheduler.w2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.restcomm.protocols.ss7.scheduler.W2PriorityQueue;
import org.restcomm.protocols.ss7.scheduler.W2Work;

/**
 * Local bounded dispatcher with FIFO mailboxes keyed by {@link W2Work#dialogKey()}.
 *
 * <p>Policy chooses only the next eligible mailbox. Each mailbox has at most one
 * active drainer and its events remain FIFO, regardless of priority/deadline
 * metadata on later events. Multiple workers may drain different mailboxes in
 * parallel. This class is protocol-neutral.</p>
 */
public final class W2KeyedMailboxDispatcher implements AutoCloseable {

    private static final class Mailbox {
        private final ArrayDeque<W2Work<Runnable>> events = new ArrayDeque<>();
        private boolean draining;
        private boolean eligible;
    }

    private final int capacity;
    private final W2PriorityQueue<String> eligibleMailboxes;
    private final Map<String, Mailbox> mailboxes = new HashMap<>();
    private final ReentrantLock lifecycleLock = new ReentrantLock();
    private final Condition workAvailable = lifecycleLock.newCondition();
    private final String workerName;
    private final int workerCount;
    private final List<Thread> workers = new ArrayList<>();
    private boolean accepting = true;
    private boolean running;
    private int liveWorkers;
    private int depth;
    private long admitted;
    private long rejected;
    private long dispatched;
    private long failed;

    public W2KeyedMailboxDispatcher(int capacity) {
        this(capacity, 1, "w2-keyed-mailbox-dispatcher");
    }

    public W2KeyedMailboxDispatcher(int capacity, String workerName) {
        this(capacity, 1, workerName);
    }

    public W2KeyedMailboxDispatcher(int capacity, int workerCount, String workerName) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        if (workerCount <= 0) {
            throw new IllegalArgumentException("workerCount must be positive");
        }
        if (workerName == null || workerName.isBlank()) {
            throw new IllegalArgumentException("workerName must not be blank");
        }
        this.capacity = capacity;
        this.eligibleMailboxes = new W2PriorityQueue<>(capacity);
        this.workerCount = workerCount;
        this.workerName = workerName;
    }

    /**
     * Admits an event to its ordering-key mailbox without blocking the caller. The key is a
     * TCAP dialog for current SS7 callers but is general application scheduling metadata.
     *
     * @return true when accepted; false if stopped or the global event capacity is full
     */
    public boolean submit(W2Work<Runnable> work) {
        Objects.requireNonNull(work, "work");
        lifecycleLock.lock();
        try {
            if (!accepting || depth == capacity) {
                rejected++;
                return false;
            }
            Mailbox mailbox = mailboxes.computeIfAbsent(work.orderingKey(), ignored -> new Mailbox());
            mailbox.events.addLast(work);
            depth++;
            admitted++;
            if (!mailbox.draining && !mailbox.eligible) {
                makeEligible(work.orderingKey(), mailbox);
            }
            workAvailable.signal();
            return true;
        } finally {
            lifecycleLock.unlock();
        }
    }

    /** Starts the local mailbox worker pool. Calling it more than once is harmless. */
    public void start() {
        lifecycleLock.lock();
        try {
            if (running) {
                return;
            }
            if (!accepting) {
                throw new IllegalStateException("dispatcher has been stopped");
            }
            startWorkers();
        } finally {
            lifecycleLock.unlock();
        }
    }

    /** Stops admission, then drains all previously accepted mailbox events. */
    public void stop() {
        List<Thread> workersToJoin;
        lifecycleLock.lock();
        try {
            accepting = false;
            if (!running && depth > 0) {
                startWorkers();
            }
            workAvailable.signalAll();
            workersToJoin = List.copyOf(workers);
        } finally {
            lifecycleLock.unlock();
        }
        for (Thread worker : workersToJoin) {
            if (worker != Thread.currentThread()) {
                joinUninterruptibly(worker);
            }
        }
    }

    @Override
    public void close() {
        stop();
    }

    public W2KeyedMailboxMetrics metrics() {
        lifecycleLock.lock();
        try {
            return new W2KeyedMailboxMetrics(capacity, depth, mailboxes.size(), admitted, rejected, dispatched, failed,
                    accepting, running);
        } finally {
            lifecycleLock.unlock();
        }
    }

    private void startWorkers() {
        running = true;
        for (int i = 0; i < workerCount; i++) {
            int workerIndex = i;
            Thread worker = Thread.ofPlatform().daemon().name(workerName + "-" + workerIndex).start(this::runWorker);
            workers.add(worker);
            liveWorkers++;
        }
    }

    private void makeEligible(String dialogKey, Mailbox mailbox) {
        W2Work<Runnable> head = mailbox.events.peekFirst();
        if (head == null || !eligibleMailboxes.offer(new W2Work<>(dialogKey, head.priority(), head.deadlineNanos(), dialogKey))) {
            throw new IllegalStateException("eligible mailbox queue must have capacity for every admitted event");
        }
        mailbox.eligible = true;
    }

    private void runWorker() {
        for (;;) {
            W2Work<Runnable> work = takeNext();
            if (work == null) {
                return;
            }
            try {
                work.payload().run();
            } catch (RuntimeException e) {
                lifecycleLock.lock();
                try {
                    failed++;
                } finally {
                    lifecycleLock.unlock();
                }
            } finally {
                complete(work);
            }
        }
    }

    private W2Work<Runnable> takeNext() {
        lifecycleLock.lock();
        try {
            for (;;) {
                W2Work<String> selected = eligibleMailboxes.poll();
                if (selected != null) {
                    Mailbox mailbox = mailboxes.get(selected.payload());
                    if (mailbox == null || !mailbox.eligible || mailbox.draining) {
                        throw new IllegalStateException("invalid mailbox eligibility state");
                    }
                    W2Work<Runnable> work = mailbox.events.peekFirst();
                    if (work == null) {
                        throw new IllegalStateException("eligible mailbox has no event");
                    }
                    mailbox.eligible = false;
                    mailbox.draining = true;
                    return work;
                }
                if (!accepting) {
                    if (--liveWorkers == 0) {
                        running = false;
                        workers.clear();
                    }
                    return null;
                }
                try {
                    workAvailable.await();
                } catch (InterruptedException e) {
                    // shutdown is signal-based; preserve service availability.
                }
            }
        } finally {
            lifecycleLock.unlock();
        }
    }

    private void complete(W2Work<Runnable> completedWork) {
        lifecycleLock.lock();
        try {
            Mailbox mailbox = mailboxes.get(completedWork.dialogKey());
            if (mailbox == null || !mailbox.draining || mailbox.events.peekFirst() != completedWork) {
                throw new IllegalStateException("mailbox completion order violated");
            }
            mailbox.events.removeFirst();
            mailbox.draining = false;
            depth--;
            dispatched++;
            if (mailbox.events.isEmpty()) {
                mailboxes.remove(completedWork.dialogKey());
            } else {
                makeEligible(completedWork.dialogKey(), mailbox);
                workAvailable.signal();
            }
        } finally {
            lifecycleLock.unlock();
        }
    }

    private static void joinUninterruptibly(Thread thread) {
        boolean interrupted = false;
        for (;;) {
            try {
                thread.join();
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
