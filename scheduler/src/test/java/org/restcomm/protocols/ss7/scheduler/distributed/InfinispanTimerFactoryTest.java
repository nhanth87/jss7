package org.restcomm.protocols.ss7.scheduler.distributed;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.restcomm.protocols.ss7.scheduler.api.TimerScheduler;
import org.restcomm.protocols.ss7.scheduler.distributed.event.TimerEventBus;
import org.restcomm.protocols.ss7.scheduler.impl.LocalTimerAdapter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class InfinispanTimerFactoryTest {

    @AfterMethod
    public void tearDown() {
        TimerScheduler scheduler = InfinispanTimerFactory.getTimerPort();
        if (scheduler != null) {
            scheduler.stop();
        }
        InfinispanTimerFactory.reset();
    }

    @Test
    public void createFallsBackToLocalWhenJndiUnavailable() {
        TimerEventBus bus = new TimerEventBus();
        TimerScheduler scheduler = InfinispanTimerFactory.create(bus,
                "java:jboss/infinispan/container/jss7-missing-test", "jss7-timers", "jss7-timer-index",
                "test-timer");

        assertNotNull(scheduler);
        assertTrue(scheduler instanceof LocalTimerAdapter);
    }

    @Test
    public void getTimerPortReturnsSingleton() {
        TimerScheduler first = InfinispanTimerFactory.getTimerPort("singleton-test");
        TimerScheduler second = InfinispanTimerFactory.getTimerPort("ignored");
        assertTrue(first == second);
    }

    @Test
    public void setSchedulerOverridesSingleton() {
        TimerEventBus bus = new TimerEventBus();
        LocalTimerAdapter custom = new LocalTimerAdapter();
        custom.start();
        InfinispanTimerFactory.setScheduler(custom);
        assertTrue(InfinispanTimerFactory.getTimerPort() == custom);
    }
}
