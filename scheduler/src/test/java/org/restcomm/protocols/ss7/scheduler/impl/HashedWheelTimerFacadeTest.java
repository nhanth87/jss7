package org.restcomm.protocols.ss7.scheduler.impl;

import static org.testng.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HashedWheelTimerFacadeTest {

    private HashedWheelTimerFacade facade;

    @BeforeMethod
    public void setUp() {
        facade = new HashedWheelTimerFacade(10L, TimeUnit.MILLISECONDS);
        facade.start();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        facade.stop();
        facade.awaitTermination(2, TimeUnit.SECONDS);
    }

    @Test
    public void scheduleRunsTaskAfterDelay() throws Exception {
        final CountDownLatch fired = new CountDownLatch(1);

        facade.schedule(new Runnable() {
            @Override
            public void run() {
                fired.countDown();
            }
        }, 50L, TimeUnit.MILLISECONDS);

        assertTrue(fired.await(2, TimeUnit.SECONDS));
    }
}
