package org.restcomm.protocols.ss7.scheduler;

import org.apache.log4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Implements scheduler with multi-level priority queue.
 *
 * This scheduler implementation follows to uniprocessor model with "super" thread. The "super" thread includes IO bound thread
 * and one or more CPU bound threads with equal priorities.
 *
 * The actual priority is assigned to task instead of process and can be changed dynamically at runtime using the initial
 * priority level, feedback and other parameters.
 *
 *
 * @author oifa.yulian
 */
public class Scheduler implements SchedulerMBean {

    // MANAGEMENT QUEUE SHOULD CONTAIN ONLY TASKS THAT ARE NOT TIME DEPENDENT
    public static final Integer MANAGEMENT_QUEUE = 0;

    // MTP2/SCTP READ
    public static final Integer L2READ_QUEUE = 1;
    // MTP3/M3UA READ
    public static final Integer L3READ_QUEUE = 2;
    // ISUP / SCCP READ
    public static final Integer L4READ_QUEUE = 3;
    // TCAP READ
    public static final Integer TCAP_READ_QUEUE = 4;
    // MAP/INUP and other APP LAYER READ
    public static final Integer APP_READ_QUEUE = 5;

    // MAP/INUP and other APP LAYER WRITE
    public static final Integer APP_WRITE_QUEUE = 6;
    // TCAP WRITE
    public static final Integer TCAP_WRITE_QUEUE = 7;
    // ISUP / SCCP WRITE
    public static final Integer L4WRITE_QUEUE = 8;
    // MTP3/M3UA WRITE
    public static final Integer L3WRITE_QUEUE = 9;
    // MTP2/SCTP WRITE
    public static final Integer L2WRITE_QUEUE = 10;

    // INTERNETWORKING OCCURES OVER L3 NO HIGHER LAYERS ARE USED
    // BASICALLY DOEST NOT MATTER WHAT QUEUE WE CHOOSE , IT SHOULD BE BETWEEN L3READ_QUEUE AND L3WRITE_QUEUE
    public static final Integer INTERNETWORKING_QUEUE = 3;

    public static final Integer HEARTBEAT_QUEUE = -1;

    private static final int DEFAULT_CYCLE_DURATION_MS = 4;

    // The clock for time measurement
    private Clock clock;

    // priority queue
    protected OrderedTaskQueue[] taskQueues = new OrderedTaskQueue[11];

    protected OrderedTaskQueue heartBeatQueue;
    // CPU bound threads
    private CpuThread cpuThread;

    // flag indicating state of the scheduler
    private boolean isActive;

    private final int cycleDurationMs;

    private Logger logger = Logger.getLogger(Scheduler.class);

    /**
     * Creates new instance of scheduler with the default 4ms cycle.
     */
    public Scheduler() {
        this(DEFAULT_CYCLE_DURATION_MS);
    }

    /**
     * Creates new instance of scheduler with a configurable cycle duration.
     *
     * @param cycleDurationMs scheduler tick duration in milliseconds
     */
    public Scheduler(int cycleDurationMs) {
        if (cycleDurationMs <= 0) {
            throw new IllegalArgumentException("cycleDurationMs must be positive");
        }
        this.cycleDurationMs = cycleDurationMs;
        for (int i = 0; i < taskQueues.length; i++)
            taskQueues[i] = new OrderedTaskQueue();

        heartBeatQueue = new OrderedTaskQueue();

        cpuThread = new CpuThread(String.format("Scheduler"), cycleDurationMs);
    }

    /**
     * Sets clock.
     *
     * @param clock the clock used for time measurement.
     */
    public void setClock(Clock clock) {
        this.clock = clock;
    }

    /**
     * Gets the clock used by this scheduler.
     *
     * @return the clock object.
     */
    public Clock getClock() {
        return clock;
    }

    /**
     * Queues task for execution according to its priority.
     *
     * @param task the task to be executed.
     */
    public void submit(Task task, Integer index) {
        task.activate(false);
        taskQueues[index].accept(task);
    }

    /**
     * Queues task for execution according to its queue identifier.
     *
     * @param task the task to be executed.
     * @param queueId the target queue
     */
    public void submit(Task task, QueueId queueId) {
        submit(task, queueId.getIndex());
    }

    /**
     * Queues task for execution according to its priority.
     *
     * @param task the task to be executed.
     */
    public void submitHeatbeat(Task task) {
        task.activate(true);
        heartBeatQueue.accept(task);
    }

    /**
     * Starts scheduler.
     */
    public void start() {
        if (this.isActive)
            return;

        if (clock == null) {
            throw new IllegalStateException("Clock is not set");
        }

        this.isActive = true;

        logger.info("Starting ");

        cpuThread.activate();

        logger.info("Started ");
    }

    /**
     * Stops scheduler.
     */
    public void stop() {
        if (!this.isActive) {
            return;
        }

        this.isActive = false;
        cpuThread.shutdown();
        cpuThread.shutdownExecutor();

        try {
            cpuThread.awaitExecutorTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            cpuThread.join(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (int i = 0; i < taskQueues.length; i++)
            taskQueues[i].clear();

        heartBeatQueue.clear();
    }

    // removed statistics to increase perfomance
    /**
     * Shows the miss rate.
     *
     * @return the miss rate value;
     */
    public double getMissRate() {
        return 0;
    }

    public long getWorstExecutionTime() {
        return 0;
    }

    public void notifyCompletion() {
        cpuThread.notifyCompletion();
    }

    /**
     * Executor thread.
     */
    private class CpuThread extends Thread {
        private volatile boolean active;
        private int currQueue = MANAGEMENT_QUEUE;
        private volatile CountDownLatch batchLatch;
        private long cycleStart = 0;
        private int runIndex = 0;
        private ExecutorService eservice;
        private final long cycleDurationNanos;

        public CpuThread(String name, int cycleDurationMs) {
            super(name);
            this.cycleDurationNanos = cycleDurationMs * 1_000_000L;
            int size = Runtime.getRuntime().availableProcessors() * 2;
            eservice = new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS, new ConcurrentLinkedList<Runnable>());
        }

        public void activate() {
            this.active = true;
            this.start();
        }

        public void notifyCompletion() {
            CountDownLatch latch = batchLatch;
            if (latch != null) {
                latch.countDown();
            }
        }

        @Override
        public void run() {
            long cycleDuration;
            cycleStart = System.nanoTime();

            while (active) {
                while (currQueue <= L2WRITE_QUEUE) {
                    executeQueue(taskQueues[currQueue]);
                    currQueue++;
                }

                runIndex = (runIndex + 1) % 25;
                if (runIndex == 0) {
                    executeQueue(heartBeatQueue);
                }

                cycleDuration = System.nanoTime() - cycleStart;
                if (cycleDuration < cycleDurationNanos) {
                    LockSupport.parkNanos(cycleDurationNanos - cycleDuration);
                }

                cycleStart = cycleStart + cycleDurationNanos;
                currQueue = MANAGEMENT_QUEUE;
            }
        }

        private void executeQueue(OrderedTaskQueue currQueue) {
            Task t;
            currQueue.changePool();
            int currQueueSize = currQueue.size();
            if (currQueueSize == 0) {
                return;
            }

            CountDownLatch latch = new CountDownLatch(currQueueSize);
            batchLatch = latch;
            t = currQueue.poll();
            while (t != null) {
                eservice.execute(t);
                t = currQueue.poll();
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                batchLatch = null;
            }
        }

        /**
         * Terminates thread.
         */
        private void shutdown() {
            this.active = false;
        }

        private void shutdownExecutor() {
            eservice.shutdown();
        }

        private void awaitExecutorTermination(long timeout, TimeUnit unit) throws InterruptedException {
            eservice.awaitTermination(timeout, unit);
        }
    }
}
