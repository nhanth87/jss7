package org.restcomm.protocols.ss7.cap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.restcomm.protocols.ss7.cap.api.CAPApplicationContext;
import org.restcomm.protocols.ss7.cap.api.CAPDialog;
import org.restcomm.protocols.ss7.cap.api.CAPMessage;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPParsingComponentException;
import org.restcomm.protocols.ss7.cap.api.CAPProvider;
import org.restcomm.protocols.ss7.cap.api.CAPServiceBase;
import org.restcomm.protocols.ss7.cap.api.CAPServiceListener;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessage;
import org.restcomm.protocols.ss7.scheduler.Ss7ApplicationProtocol;
import org.restcomm.protocols.ss7.scheduler.W2PriorityClassifier;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.tcap.api.TCAPException;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.Dialog;
import org.restcomm.protocols.ss7.tcap.asn.comp.ComponentType;
import org.restcomm.protocols.ss7.tcap.asn.comp.Invoke;
import org.restcomm.protocols.ss7.tcap.asn.comp.OperationCode;
import org.restcomm.protocols.ss7.tcap.asn.comp.Parameter;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

/**
 * This class must be the super class of all CAP services
 *
 * @author sergey vetyutnev
 *
 */
public abstract class CAPServiceBaseImpl implements CAPServiceBase {

    protected Boolean _isActivated = false;
    protected List<CAPServiceListener> serviceListeners = new CopyOnWriteArrayList<CAPServiceListener>();
    private final Map<CAPServiceListener, CAPServiceListener> w2ListenerWrappers =
            Collections.synchronizedMap(new IdentityHashMap<CAPServiceListener, CAPServiceListener>());
    protected CAPProviderImpl capProviderImpl;

    protected CAPServiceBaseImpl(CAPProviderImpl capProviderImpl) {
        this.capProviderImpl = capProviderImpl;
    }

    @Override
    public CAPProvider getCAPProvider() {
        return this.capProviderImpl;
    }

    /**
     * Creation a CAP Dialog implementation for the specific service
     *
     * @param capApplicationContext
     * @param tcapDialog
     * @return CAPDialogImpl
     */
    protected abstract CAPDialogImpl createNewDialogIncoming(CAPApplicationContext capApplicationContext, Dialog tcapDialog);

    /**
     * Creating new outgoing TCAP Dialog. Used when creating a new outgoing CAP Dialog
     *
     * @param sccpCallingPartyAddress
     * @param sccpCalledPartyAddress
     * @param localTransactionId
     * @return Dialog
     * @throws CAPException
     */
    protected Dialog createNewTCAPDialog(SccpAddress sccpCallingPartyAddress, SccpAddress sccpCalledPartyAddress, Long localTransactionId) throws CAPException {
        try {
            return this.capProviderImpl.getTCAPProvider().getNewDialog(sccpCallingPartyAddress, sccpCalledPartyAddress, localTransactionId);
        } catch (TCAPException e) {
            throw new CAPException(e.getMessage(), e);
        }
    }

    public abstract void processComponent(ComponentType compType, OperationCode oc, Parameter parameter, CAPDialog capDialog,
            Long invokeId, Long linkedId, Invoke linkedInvoke) throws CAPParsingComponentException;

    /**
     * Returns a list of linked operations for operationCode operation
     *
     * @param operationCode
     * @return
     */
    public long[] getLinkedOperationList(long operationCode) {
        return null;
    }

    /**
     * Adding CAP Dialog into CAPProviderImpl.dialogs Used when creating a new outgoing CAP Dialog
     *
     * @param capDialog
     */
    protected void putCAPDialogIntoCollection(CAPDialogImpl capDialog) {
        this.capProviderImpl.addDialog((CAPDialogImpl) capDialog);
    }

    protected void addCAPServiceListener(CAPServiceListener capServiceListener) {
        this.serviceListeners.add(wrapServiceListener(capServiceListener));
    }

    protected void removeCAPServiceListener(CAPServiceListener capServiceListener) {
        CAPServiceListener wrapper = this.w2ListenerWrappers.remove(capServiceListener);
        this.serviceListeners.remove(wrapper != null ? wrapper : capServiceListener);
    }

    private CAPServiceListener wrapServiceListener(CAPServiceListener listener) {
        if (!Boolean.parseBoolean(System.getProperty("ss7.cap.w2Scheduler.enabled", "true"))) {
            return listener;
        }
        CAPServiceListener existing = this.w2ListenerWrappers.get(listener);
        if (existing != null) {
            return existing;
        }
        Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
        collectInterfaces(listener.getClass(), interfaces);
        interfaces.add(CAPServiceListener.class);
        CAPServiceListener wrapper = (CAPServiceListener) Proxy.newProxyInstance(listener.getClass().getClassLoader(),
                interfaces.toArray(new Class<?>[interfaces.size()]),
                (proxy, method, args) -> dispatchServiceCallback(listener, method, args));
        this.w2ListenerWrappers.put(listener, wrapper);
        return wrapper;
    }

    private Object dispatchServiceCallback(CAPServiceListener listener, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return invokeServiceListener(listener, method, args);
        }
        if (args != null) {
            for (Object arg : args) {
                if (arg instanceof CAPMessage) {
                    CAPMessage message = (CAPMessage) arg;
                    this.capProviderImpl.dispatchApplicationCallback(message.getCAPDialog(),
                            W2PriorityClassifier.classifyApplication(Ss7ApplicationProtocol.CAP,
                                    (long) message.getOperationCode()).priority(),
                            () -> invokeServiceListenerUnchecked(listener, method, args));
                    return null;
                }
            }
        }
        return invokeServiceListener(listener, method, args);
    }

    private static void collectInterfaces(Class<?> type, Set<Class<?>> interfaces) {
        while (type != null) {
            for (Class<?> iface : type.getInterfaces()) {
                interfaces.add(iface);
            }
            type = type.getSuperclass();
        }
    }

    private static Object invokeServiceListener(CAPServiceListener listener, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(listener, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private static void invokeServiceListenerUnchecked(CAPServiceListener listener, Method method, Object[] args) {
        try {
            invokeServiceListener(listener, method, args);
        } catch (Throwable e) {
            throw new RuntimeException("CAP service listener callback failed", e);
        }
    }

    /**
     * This method is invoked when CAPProviderImpl.onInvokeTimeOut() is invoked. An InvokeTimeOut may be a normal situation for
     * the component class 2, 3, or 4. In this case checkInvokeTimeOut() should return true and deliver to the CAP-user correct
     * indication
     *
     * @param dialog
     * @param invoke
     * @return
     */
    public boolean checkInvokeTimeOut(CAPDialog dialog, Invoke invoke) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActivated() {
        return this._isActivated;
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
        this._isActivated = true;
    }

    /**
     * {@inheritDoc}
     */
    public void deactivate() {
        this._isActivated = false;

        // TODO: abort all active dialogs ?
    }

    protected void deliverErrorComponent(CAPDialog capDialog, Long invokeId, CAPErrorMessage capErrorMessage) {
        this.capProviderImpl.dispatchApplicationCallback(capDialog, () -> {
            for (CAPServiceListener serLis : this.serviceListeners) {
                serLis.onErrorComponent(capDialog, invokeId, capErrorMessage);
            }
        });
    }

    protected void deliverRejectComponent(CAPDialog capDialog, Long invokeId, Problem problem, boolean isLocalOriginated) {
        this.capProviderImpl.dispatchApplicationCallback(capDialog, () -> {
            for (CAPServiceListener serLis : this.serviceListeners) {
                serLis.onRejectComponent(capDialog, invokeId, problem, isLocalOriginated);
            }
        });
    }

    protected void deliverInvokeTimeout(CAPDialog capDialog, Invoke invoke) {
        this.capProviderImpl.dispatchApplicationCallback(capDialog, () -> {
            for (CAPServiceListener serLis : this.serviceListeners) {
                serLis.onInvokeTimeout(capDialog, invoke.getInvokeId());
            }
        });
    }
}
