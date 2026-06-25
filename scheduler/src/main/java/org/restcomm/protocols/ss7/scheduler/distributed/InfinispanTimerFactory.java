package org.restcomm.protocols.ss7.scheduler.distributed;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.restcomm.protocols.ss7.scheduler.api.TimerScheduler;
import org.restcomm.protocols.ss7.scheduler.distributed.event.TimerEventBus;
import org.restcomm.protocols.ss7.scheduler.impl.HashedWheelTimerFacade;
import org.restcomm.protocols.ss7.scheduler.impl.LocalTimerAdapter;

/**
 * Resolves the process-wide {@link TimerScheduler}. Prefers an Infinispan-backed scheduler when
 * the WildFly cache container is reachable; otherwise falls back to {@link LocalTimerAdapter}.
 */
public final class InfinispanTimerFactory {

    private static final Logger LOGGER = Logger.getLogger(InfinispanTimerFactory.class.getName());

    /** JNDI container name configured by the Infinispan subsystem in standalone.xml. */
    public static final String DEFAULT_CONTAINER_JNDI = "java:jboss/infinispan/container/jss7";
    public static final String TIMERS_CACHE_NAME = "jss7-timers";
    public static final String INDEX_CACHE_NAME = "jss7-timer-index";

    private static volatile TimerScheduler instance;
    private static volatile TimerEventBus eventBus;

    private InfinispanTimerFactory() {
    }

    public static TimerScheduler getTimerPort() {
        return getTimerPort("ss7-timer");
    }

    public static TimerScheduler getTimerPort(String threadNamePrefix) {
        TimerScheduler local = instance;
        if (local == null) {
            synchronized (InfinispanTimerFactory.class) {
                local = instance;
                if (local == null) {
                    local = create(defaultEventBus(), threadNamePrefix);
                    instance = local;
                }
            }
        }
        return local;
    }

    public static TimerScheduler create(TimerEventBus bus) {
        return create(bus, "ss7-timer");
    }

    public static TimerScheduler create(TimerEventBus bus, String threadNamePrefix) {
        return create(bus, DEFAULT_CONTAINER_JNDI, TIMERS_CACHE_NAME, INDEX_CACHE_NAME, threadNamePrefix);
    }

    public static TimerScheduler create(TimerEventBus bus, String containerJndi, String timersCache,
            String indexCache, String threadNamePrefix) {
        if (bus == null) {
            throw new IllegalArgumentException("eventBus is required");
        }
        eventBus = bus;
        try {
            InfinispanTimerAdapter adapter = new InfinispanTimerAdapter(containerJndi, timersCache, indexCache, bus);
            adapter.start();
            LOGGER.info("Using Infinispan-backed TimerScheduler");
            return adapter;
        } catch (Throwable t) {
            LOGGER.log(Level.WARNING,
                    "Infinispan timer cache unavailable, falling back to LocalTimerAdapter: " + t.getMessage());
            LocalTimerAdapter local = new LocalTimerAdapter(
                    new HashedWheelTimerFacade(new io.netty.util.concurrent.DefaultThreadFactory(threadNamePrefix),
                            10L, java.util.concurrent.TimeUnit.MILLISECONDS));
            local.start();
            return local;
        }
    }

    public static TimerEventBus getEventBus() {
        return eventBus;
    }

    private static TimerEventBus defaultEventBus() {
        TimerEventBus bus = eventBus;
        if (bus == null) {
            synchronized (InfinispanTimerFactory.class) {
                bus = eventBus;
                if (bus == null) {
                    bus = new TimerEventBus();
                    eventBus = bus;
                }
            }
        }
        return bus;
    }

    /** Test/seam hook to inject a specific scheduler implementation. */
    public static void setScheduler(TimerScheduler scheduler) {
        instance = scheduler;
    }

    /** Resets the singleton for tests. */
    public static void reset() {
        instance = null;
        eventBus = null;
    }

    /** Stops and clears the singleton without recreating it. */
    public static void shutdown() {
        TimerScheduler scheduler = instance;
        if (scheduler != null) {
            scheduler.stop();
        }
        reset();
    }
}
