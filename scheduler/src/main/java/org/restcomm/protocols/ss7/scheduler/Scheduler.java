package org.restcomm.protocols.ss7.scheduler;

import org.agrona.DeadlineTimerWheel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * Agrona {@link DeadlineTimerWheel}-based scheduler with multi-level priority queues.
 *
 * A single dedicated ticker thread drives an Agrona hashed timer wheel with one
 * recurring timer at {@link #TICK_NS} resolution (low-GC, allocation-free poll).
 * Each tick processes one priority queue round-robin (0..10); the heartbeat queue
 * is processed every 25th tick. Tasks are submitted via {@link #submit(Task, Integer)}
 * and executed on a configurable {@link ExecutorService}.
 *
 * <h3>Migration notes (j25)</h3>
 * <ul>
 *   <li>Replaced the legacy polling CpuThread and the interim Netty
 *       {@code HashedWheelTimer} with Agrona {@code DeadlineTimerWheel}.</li>
 *   <li>{@link Clock} / {@link #setClock(Clock)} retained as no-op for API
 *       compatibility — the timer wheel ignores it.</li>
 * </ul>
 *
 * @author oifa.yulian (original)
 * @author agrona-migration (j25)
 */
public class Scheduler implements SchedulerMBean {

    // ── queue constants (unchanged public API) ────────────────
    public static final Integer MANAGEMENT_QUEUE    = 0;
    public static final Integer L2READ_QUEUE        = 1;
    public static final Integer L3READ_QUEUE        = 2;
    public static final Integer L4READ_QUEUE        = 3;
    public static final Integer TCAP_READ_QUEUE     = 4;
    public static final Integer APP_READ_QUEUE      = 5;
    public static final Integer APP_WRITE_QUEUE     = 6;
    public static final Integer TCAP_WRITE_QUEUE    = 7;
    public static final Integer L4WRITE_QUEUE       = 8;
    public static final Integer L3WRITE_QUEUE       = 9;
    public static final Integer L2WRITE_QUEUE       = 10;
    public static final Integer INTERNETWORKING_QUEUE = 3;
    public static final Integer HEARTBEAT_QUEUE       = -1;

    private static final int QUEUE_COUNT      = 11;
    /**
     * Agrona {@code DeadlineTimerWheel} requires the tick resolution to be a
     * power of 2. 2^22 ns = 4_194_304 ns ≈ 4.19 ms is the closest power-of-2
     * to the original 4 ms tick; a non-power-of-2 value makes the wheel
     * constructor throw and silently kills the ticker thread.
     */
    private static final long TICK_NS         = 1L << 22;
    private static final int HEARTBEAT_EVERY  = 25;

    private final Logger logger = LogManager.getLogger(Scheduler.class);

    private final List<ConcurrentLinkedQueue<Task>> taskQueues = new ArrayList<>(QUEUE_COUNT);
    private final ConcurrentLinkedQueue<Task> heartBeatQueue = new ConcurrentLinkedQueue<>();

    private static final int TICKS_PER_WHEEL = 512;

    private volatile Thread ticker;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ExecutorService executor;
    private int executorThreads = -1;

    private final AtomicInteger activeTasksCount = new AtomicInteger();
    private final Object completionLock = new Object();

    private Clock clock;
    private int currQueue = 0;
    private int runIndex = 0;

    public Scheduler() {
        for (int i = 0; i < QUEUE_COUNT; i++) {
            taskQueues.add(new ConcurrentLinkedQueue<>());
        }
    }

    @Override
    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Clock getClock() {
        return clock;
    }

    public void submit(Task task, Integer index) {
        task.activate(false);
        if (index >= 0 && index < QUEUE_COUNT) {
            taskQueues.get(index).offer(task);
        }
    }

    public void submitHeatbeat(Task task) {
        task.activate(true);
        heartBeatQueue.offer(task);
    }

    public void start() {
        if (running.get()) return;

        if (executorThreads <= 0) {
            executorThreads = Runtime.getRuntime().availableProcessors() * 2;
        }

        ThreadFactory tf = r -> {
            Thread t = new Thread(r, "scheduler-worker");
            t.setDaemon(true);
            return t;
        };
        executor = Executors.newFixedThreadPool(executorThreads, tf);

        running.set(true);
        ticker = new Thread(this::runWheel, "scheduler-ticker");
        ticker.setDaemon(true);
        ticker.start();

        logger.info("Started Agrona DeadlineTimerWheel scheduler (tick={}ms, workers={})",
                     TICK_NS / 1_000_000, executorThreads);
    }

    public void stop() {
        if (!running.compareAndSet(true, false)) return;

        Thread t = ticker;
        if (t != null) {
            t.interrupt();
            try {
                t.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            ticker = null;
        }
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            executor = null;
        }

        for (ConcurrentLinkedQueue<Task> q : taskQueues) q.clear();
        heartBeatQueue.clear();

        logger.info("Stopped HashedWheelTimer scheduler");
    }

    @Override
    public double getMissRate() { return 0; }

    @Override
    public long getWorstExecutionTime() { return 0; }

    public void notifyCompletion() {
        int remaining = activeTasksCount.decrementAndGet();
        if (remaining == 0 && running.get()) {
            synchronized (completionLock) {
                completionLock.notifyAll();
            }
        }
    }

    // ── Agrona timer-wheel driver ────────────────────────────

    /**
     * Ticker-thread loop: drives an Agrona {@link DeadlineTimerWheel} with a
     * single recurring timer at {@link #TICK_NS} resolution. On each expiry the
     * next tick is rescheduled and {@link #tick()} runs one priority lane.
     */
    private void runWheel() {
        long now = System.nanoTime();
        final DeadlineTimerWheel wheel =
                new DeadlineTimerWheel(TimeUnit.NANOSECONDS, now, TICK_NS, TICKS_PER_WHEEL);
        wheel.scheduleTimer(now + TICK_NS);

        final DeadlineTimerWheel.TimerHandler handler = (timeUnit, expiryNow, timerId) -> {
            tick();
            wheel.scheduleTimer(System.nanoTime() + TICK_NS);
            return true;
        };

        while (running.get()) {
            now = System.nanoTime();
            int expired = wheel.poll(now, handler, 1);
            if (expired == 0) {
                // idle until close to the next deadline (bounded park, interrupt-aware)
                LockSupport.parkNanos(TICK_NS / 4);
                if (Thread.interrupted()) break;
            }
        }
    }

    /** Runs one scheduler tick: one priority lane + periodic heartbeat. */
    private void tick() {
        if (!running.get()) return;

        if (currQueue < QUEUE_COUNT) {
            drainToExecutor(taskQueues.get(currQueue));
            currQueue++;
        }

        runIndex++;
        if (runIndex % HEARTBEAT_EVERY == 0) {
            drainToExecutor(heartBeatQueue);
        }
        if (runIndex >= Integer.MAX_VALUE - 1) runIndex = 0;

        if (currQueue >= QUEUE_COUNT) currQueue = 0;
    }

    private void drainToExecutor(ConcurrentLinkedQueue<Task> q) {
        int batchSize = q.size();
        if (batchSize == 0) return;

        activeTasksCount.set(batchSize);
        for (int i = 0; i < batchSize; i++) {
            Task t = q.poll();
            if (t == null) break;
            executor.execute(t);
        }

        if (activeTasksCount.get() > 0) {
            synchronized (completionLock) {
                try {
                    completionLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
