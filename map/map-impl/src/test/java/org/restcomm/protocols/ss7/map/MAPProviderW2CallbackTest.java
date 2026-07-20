package org.restcomm.protocols.ss7.map;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class MAPProviderW2CallbackTest {

    @AfterMethod
    public void clearProperties() {
        System.clearProperty("ss7.map.w2Scheduler.enabled");
        System.clearProperty("ss7.map.w2Scheduler.workers");
    }

    @Test
    public void defaultDispatcherPreservesCallbackOrderForOneDialog() throws Exception {
        System.setProperty("ss7.map.w2Scheduler.workers", "2");
        MAPProviderImpl provider = new MAPProviderImpl("w2-test", null);
        Method start = MAPProviderImpl.class.getDeclaredMethod("startW2CallbackDispatcher");
        start.setAccessible(true);
        start.invoke(provider);

        MAPDialog dialog = dialog(42L);
        List<Integer> received = Collections.synchronizedList(new ArrayList<Integer>());
        CountDownLatch done = new CountDownLatch(3);
        provider.dispatchApplicationCallback(dialog, () -> add(received, done, 1));
        provider.dispatchApplicationCallback(dialog, () -> add(received, done, 2));
        provider.dispatchApplicationCallback(dialog, () -> add(received, done, 3));

        try {
            assertEquals(done.await(5, TimeUnit.SECONDS), true, "callbacks did not complete");
            assertEquals(received, java.util.Arrays.asList(1, 2, 3));
        } finally {
            stopDispatcher(provider);
        }
    }

    @Test
    public void disabledDispatcherRunsCallbackOnCallerThread() {
        System.setProperty("ss7.map.w2Scheduler.enabled", "false");
        MAPProviderImpl provider = new MAPProviderImpl("w2-test", null);
        String callerThread = Thread.currentThread().getName();
        List<String> callbackThreads = new ArrayList<String>();

        provider.dispatchApplicationCallback(dialog(43L), () -> callbackThreads.add(Thread.currentThread().getName()));

        assertEquals(callbackThreads, java.util.Collections.singletonList(callerThread));
    }

    private static void stopDispatcher(MAPProviderImpl provider) throws Exception {
        java.lang.reflect.Field dispatcher = MAPProviderImpl.class.getDeclaredField("w2CallbackDispatcher");
        dispatcher.setAccessible(true);
        ((org.restcomm.protocols.ss7.scheduler.w2.W2KeyedMailboxDispatcher) dispatcher.get(provider)).stop();
    }

    private static void add(List<Integer> received, CountDownLatch done, int value) {
        received.add(value);
        done.countDown();
    }

    private static MAPDialog dialog(Long id) {
        return (MAPDialog) Proxy.newProxyInstance(MAPDialog.class.getClassLoader(), new Class<?>[] { MAPDialog.class },
                (proxy, method, args) -> method.getName().equals("getLocalDialogId") ? id : null);
    }
}
