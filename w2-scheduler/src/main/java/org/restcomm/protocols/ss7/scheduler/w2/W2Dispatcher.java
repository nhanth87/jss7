package org.restcomm.protocols.ss7.scheduler.w2;

import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.restcomm.protocols.ss7.scheduler.W2PriorityQueue;
import org.restcomm.protocols.ss7.scheduler.W2QueueMetrics;
import org.restcomm.protocols.ss7.scheduler.W2Work;

/**
 * Case-1 local W2 dispatcher: bounded priority/deadline/FIFO work execution.
 *
 * <p>It is deliberately protocol-neutral. It does not schedule timers and does
 * not preserve per-dialog ordering; a later keyed-mailbox layer must provide
 * that invariant before this dispatcher is connected to TCAP/MAP/CAP.</p>
 */
public final class W2Dispatcher implements AutoCloseable {

    private final W2PriorityQueue<Runnable> queue;
    private final ReentrantLock lifecycleLock = new ReentrantLock();
    private final Condition workAvailable = lifecycleLock.newCondition();
    private final String workerName;
    private boolean accepting = true;
    private boolean running;
    private Thread worker;
    private long dispatched;
    private long failed;

    public W2Dispatcher(int capacity) {
        this(capacity, "w2-dispatcher");
    }

    public W2Dispatcher(int capacity, String workerName) {
        this.queue = new W2PriorityQueue<>(capacity);
        this.workerName = requireWorkerName(workerName);
    }

    /**
     * Admits an item without waiting for capacity. Items may be submitted before
     * {@link #start()} for deterministic burst preparation.
     *
     * @return true when the item is accepted; false when shutdown or full
     */
    public boolean submit(W2Work<Runnable> work) {
        Objects.requireNonNull(work, "work");
        lifecycleLock.lock();
        try {
            if (!accepting) {
                return false;
            }
            boolean accepted = queue.offer(work);
            if (accepted) {
                workAvailable.signal();
            }
            return accepted;
        } finally {
            lifecycleLock.unlock();
        }
    }

    /** Starts the single local worker. */
    public void start() {
        lifecycleLock.lock();
        try {
            if (running) {
                return;
            }
            if (!accepting) {
                throw new IllegalStateException("dispatcher has been stopped");
            }
            running = true;
            worker = Thread.ofPlatform().daemon().name(workerName).start(this::runWorker);
        } finally {
            lifecycleLock.unlock();
        }
    }

    /**
     * Stops accepting work and drains all work admitted before the call.
     * This method does not interrupt an executing payload.
     */
    public void stop() {
        Thread workerToJoin;
        lifecycleLock.lock();
        try {
            accepting = false;
            if (!running && queue.metrics().depth() > 0) {
                running = true;
                worker = Thread.ofPlatform().daemon().name(workerName).start(this::runWorker);
            }
            workAvailable.signalAll();
            workerToJoin = worker;
        } finally {
            lifecycleLock.unlock();
        }
        if (workerToJoin != null && workerToJoin != Thread.currentThread()) {
            joinUninterruptibly(workerToJoin);
        }
    }

    @Override
    public void close() {
        stop();
    }

    public W2DispatcherMetrics metrics() {
        lifecycleLock.lock();
        try {
            W2QueueMetrics queueMetrics = queue.metrics();
            return new W2DispatcherMetrics(queueMetrics.capacity(), queueMetrics.depth(), queueMetrics.admitted(),
                    queueMetrics.rejected(), dispatched, failed, accepting, running);
        } finally {
            lifecycleLock.unlock();
        }
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
                lifecycleLock.lock();
                try {
                    dispatched++;
                } finally {
                    lifecycleLock.unlock();
                }
            }
        }
    }

    private W2Work<Runnable> takeNext() {
        lifecycleLock.lock();
        try {
            for (;;) {
                W2Work<Runnable> work = queue.poll();
                if (work != null) {
                    return work;
                }
                if (!accepting) {
                    running = false;
                    worker = null;
                    return null;
                }
                try {
                    workAvailable.await();
                } catch (InterruptedException e) {
                    // stop() uses signaling, never interruption; preserve service availability.
                }
            }
        } finally {
            lifecycleLock.unlock();
        }
    }

    private static String requireWorkerName(String workerName) {
        if (workerName == null || workerName.isBlank()) {
            throw new IllegalArgumentException("workerName must not be blank");
        }
        return workerName;
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
