package org.restcomm.protocols.ss7.cap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.restcomm.protocols.ss7.cap.api.CAPDialog;
import org.restcomm.protocols.ss7.cap.api.CAPDialogListener;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPGeneralAbortReason;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPGprsReferenceNumber;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPNoticeProblemDiagnostic;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPUserAbortReason;
import org.restcomm.protocols.ss7.tcap.api.TCAPProvider;
import org.restcomm.protocols.ss7.tcap.asn.comp.PAbortCauseType;
import org.testng.annotations.Test;

public class CAPProviderImplW2Test {

    @Test
    public void shouldDispatchDialogCallbacksFifoByDialogId() throws Exception {
        String enabled = System.getProperty("ss7.cap.w2Scheduler.enabled");
        String workers = System.getProperty("ss7.cap.w2Scheduler.workers");
        System.setProperty("ss7.cap.w2Scheduler.enabled", "true");
        System.setProperty("ss7.cap.w2Scheduler.workers", "1");

        CAPProviderImpl provider = new CAPProviderImpl("W2Test", tcapProvider());
        CountDownLatch delivered = new CountDownLatch(3);
        List<Integer> order = new ArrayList<Integer>();
        CAPDialog dialog = dialog(42L);
        provider.addCAPDialogListener(new CAPDialogListenerAdapter() {
            @Override
            public void onDialogDelimiter(CAPDialog capDialog) {
                synchronized (order) {
                    order.add(order.size() + 1);
                }
                delivered.countDown();
            }
        });

        try {
            provider.start();
            Method deliver = CAPProviderImpl.class.getDeclaredMethod("deliverDialogDelimiter", CAPDialog.class);
            deliver.setAccessible(true);
            deliver.invoke(provider, dialog);
            deliver.invoke(provider, dialog);
            deliver.invoke(provider, dialog);

            assertTrue(delivered.await(5, TimeUnit.SECONDS), "all callbacks should be delivered");
            assertEquals(order, List.of(1, 2, 3));
        } finally {
            provider.stop();
            restore("ss7.cap.w2Scheduler.enabled", enabled);
            restore("ss7.cap.w2Scheduler.workers", workers);
        }
    }

    @Test
    public void shouldUseDirectDialogCallbackWhenW2IsDisabled() throws Exception {
        String enabled = System.getProperty("ss7.cap.w2Scheduler.enabled");
        System.setProperty("ss7.cap.w2Scheduler.enabled", "false");
        CAPProviderImpl provider = new CAPProviderImpl("W2DisabledTest", tcapProvider());
        String callerThread = Thread.currentThread().getName();
        List<String> callbackThreads = new ArrayList<String>();
        provider.addCAPDialogListener(new CAPDialogListenerAdapter() {
            @Override
            public void onDialogDelimiter(CAPDialog capDialog) {
                callbackThreads.add(Thread.currentThread().getName());
            }
        });

        try {
            provider.start();
            Method deliver = CAPProviderImpl.class.getDeclaredMethod("deliverDialogDelimiter", CAPDialog.class);
            deliver.setAccessible(true);
            deliver.invoke(provider, dialog(43L));
            assertEquals(callbackThreads, List.of(callerThread));
        } finally {
            provider.stop();
            restore("ss7.cap.w2Scheduler.enabled", enabled);
        }
    }

    private static TCAPProvider tcapProvider() {
        return (TCAPProvider) Proxy.newProxyInstance(TCAPProvider.class.getClassLoader(), new Class<?>[] { TCAPProvider.class },
                (proxy, method, args) -> null);
    }

    private static CAPDialog dialog(long id) {
        return (CAPDialog) Proxy.newProxyInstance(CAPDialog.class.getClassLoader(), new Class<?>[] { CAPDialog.class },
                (proxy, method, args) -> method.getName().equals("getLocalDialogId") ? id : null);
    }

    private static void restore(String property, String value) {
        if (value == null) {
            System.clearProperty(property);
        } else {
            System.setProperty(property, value);
        }
    }

    private abstract static class CAPDialogListenerAdapter implements CAPDialogListener {
        @Override
        public void onDialogRequest(CAPDialog capDialog, CAPGprsReferenceNumber capGprsReferenceNumber) {
        }

        @Override
        public void onDialogAccept(CAPDialog capDialog, CAPGprsReferenceNumber capGprsReferenceNumber) {
        }

        @Override
        public void onDialogUserAbort(CAPDialog capDialog, CAPGeneralAbortReason generalReason, CAPUserAbortReason userReason) {
        }

        @Override
        public void onDialogProviderAbort(CAPDialog capDialog, PAbortCauseType abortCause) {
        }

        @Override
        public void onDialogClose(CAPDialog capDialog) {
        }

        @Override
        public void onDialogRelease(CAPDialog capDialog) {
        }

        @Override
        public void onDialogTimeout(CAPDialog capDialog) {
        }

        @Override
        public void onDialogNotice(CAPDialog capDialog, CAPNoticeProblemDiagnostic noticeProblemDiagnostic) {
        }
    }
}
