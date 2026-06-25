package org.restcomm.ss7.service;

import org.jboss.logging.Logger;
import org.restcomm.protocols.ss7.scheduler.api.TimerScheduler;
import org.restcomm.protocols.ss7.scheduler.distributed.InfinispanTimerFactory;
import org.restcomm.protocols.ss7.scheduler.distributed.event.TimerEventBus;
import org.restcomm.protocols.ss7.scheduler.impl.LocalTimerAdapter;

/**
 * WildFly lifecycle hook for the jSS7 distributed timer subsystem.
 */
public final class Jss7TimerService {

    private static final Logger log = Logger.getLogger(Jss7TimerService.class);

    private Jss7TimerService() {
    }

    public static void start() {
        TimerScheduler scheduler = InfinispanTimerFactory.getTimerPort("jss7-timer");
        log.infof("jSS7 timer service started (%s)", scheduler.getClass().getSimpleName());
    }

    public static void stop() {
        InfinispanTimerFactory.shutdown();
        log.info("jSS7 timer service stopped");
    }

    public static TimerScheduler getTimerScheduler() {
        return InfinispanTimerFactory.getTimerPort();
    }

    public static TimerEventBus getEventBus() {
        return InfinispanTimerFactory.getEventBus();
    }
}
