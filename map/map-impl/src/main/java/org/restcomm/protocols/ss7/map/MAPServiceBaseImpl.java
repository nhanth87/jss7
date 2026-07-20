package org.restcomm.protocols.ss7.map;

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

import org.restcomm.protocols.ss7.map.api.MAPApplicationContext;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPProvider;
import org.restcomm.protocols.ss7.map.api.MAPServiceBase;
import org.restcomm.protocols.ss7.map.api.MAPServiceListener;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
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
 * This class must be the super class of all MAP services
 *
 * @author sergey vetyutnev
 *
 */
public abstract class MAPServiceBaseImpl implements MAPServiceBase {

    protected Boolean _isActivated = false;
    // protected Set<MAPServiceListener> serviceListeners = new HashSet<MAPServiceListener>();
    protected List<MAPServiceListener> serviceListeners = new CopyOnWriteArrayList<MAPServiceListener>();
    private final Map<MAPServiceListener, MAPServiceListener> w2ListenerWrappers =
            Collections.synchronizedMap(new IdentityHashMap<MAPServiceListener, MAPServiceListener>());
    protected MAPProviderImpl mapProviderImpl;

    protected MAPServiceBaseImpl(MAPProviderImpl mapProviderImpl) {
        this.mapProviderImpl = mapProviderImpl;
    }

    public MAPProvider getMAPProvider() {
        return this.mapProviderImpl;
    }

    /**
     * Creation a MAP Dialog implementation for the specific service
     *
     * @param mapApplicationContext
     * @param tcapDialog
     * @return
     */
    protected abstract MAPDialogImpl createNewDialogIncoming(MAPApplicationContext mapApplicationContext, Dialog tcapDialog);

    /**
     * Creating new outgoing TCAP Dialog. Used when creating a new outgoing MAP Dialog
     *
     * @param sccpCallingPartyAddress
     * @param sccpCalledPartyAddress
     * @param localTransactionId
     * @return
     * @throws MAPException
     */
    protected Dialog createNewTCAPDialog(SccpAddress sccpCallingPartyAddress, SccpAddress sccpCalledPartyAddress, Long localTransactionId) throws MAPException {
        try {
            return this.mapProviderImpl.getTCAPProvider().getNewDialog(sccpCallingPartyAddress, sccpCalledPartyAddress, localTransactionId);
        } catch (TCAPException e) {
            throw new MAPException(e.getMessage(), e);
        }
    }

    public abstract void processComponent(ComponentType comp, OperationCode operationCode, Parameter parameter, MAPDialog mapDialog,
            Long invokeId, Long linkedId, Invoke linkedInvoke) throws MAPParsingComponentException;

    /**
     * Adding MAP Dialog into MAPProviderImpl.dialogs Used when creating a new outgoing MAP Dialog
     *
     * @param mapDialog
     */
    protected void putMAPDialogIntoCollection(MAPDialogImpl mapDialog) {
        this.mapProviderImpl.addDialog((MAPDialogImpl) mapDialog);
    }

    protected void addMAPServiceListener(MAPServiceListener mapServiceListener) {
        this.serviceListeners.add(wrapServiceListener(mapServiceListener));
    }

    protected void removeMAPServiceListener(MAPServiceListener mapServiceListener) {
        MAPServiceListener wrapper = this.w2ListenerWrappers.remove(mapServiceListener);
        this.serviceListeners.remove(wrapper != null ? wrapper : mapServiceListener);
    }

    private MAPServiceListener wrapServiceListener(MAPServiceListener listener) {
        if (!Boolean.parseBoolean(System.getProperty("ss7.map.w2Scheduler.enabled", "true"))) {
            return listener;
        }
        MAPServiceListener existing = this.w2ListenerWrappers.get(listener);
        if (existing != null) {
            return existing;
        }
        Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
        collectInterfaces(listener.getClass(), interfaces);
        interfaces.add(MAPServiceListener.class);
        MAPServiceListener wrapper = (MAPServiceListener) Proxy.newProxyInstance(listener.getClass().getClassLoader(),
                interfaces.toArray(new Class<?>[interfaces.size()]),
                (proxy, method, args) -> dispatchServiceCallback(listener, method, args));
        this.w2ListenerWrappers.put(listener, wrapper);
        return wrapper;
    }

    private Object dispatchServiceCallback(MAPServiceListener listener, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return invokeServiceListener(listener, method, args);
        }
        if (args != null) {
            for (Object arg : args) {
                if (arg instanceof MAPMessage) {
                    MAPMessage message = (MAPMessage) arg;
                    this.mapProviderImpl.dispatchApplicationCallback(message.getMAPDialog(),
                            W2PriorityClassifier.classifyApplication(Ss7ApplicationProtocol.MAP,
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

    private static Object invokeServiceListener(MAPServiceListener listener, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(listener, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private static void invokeServiceListenerUnchecked(MAPServiceListener listener, Method method, Object[] args) {
        try {
            invokeServiceListener(listener, method, args);
        } catch (Throwable e) {
            throw new RuntimeException("MAP service listener callback failed", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public MAPApplicationContext getMAPv1ApplicationContext(int operationCode, Invoke invoke) {
        return null;
    }

    /**
     *
     * Returns a list of linked operations for operationCode operation
     *
     * @param operationCode
     * @return
     */
    public long[] getLinkedOperationList(long operationCode) {
        return null;
    }

    /**
     * This method is invoked when MAPProviderImpl.onInvokeTimeOut() is invoked. An InvokeTimeOut may be a normal situation for
     * the component class 2, 3, or 4. In this case checkInvokeTimeOut() should return true and deliver to the MAP-user correct
     * indication
     *
     * @param mapDialog
     * @param invoke
     * @return
     */
    public boolean checkInvokeTimeOut(MAPDialog mapDialog, Invoke invoke) {
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

    protected void deliverErrorComponent(MAPDialog mapDialog, Long invokeId, MAPErrorMessage mapErrorMessage) {
        this.mapProviderImpl.dispatchApplicationCallback(mapDialog, () -> {
            for (MAPServiceListener mapServiceListener : this.serviceListeners) {
                mapServiceListener.onErrorComponent(mapDialog, invokeId, mapErrorMessage);
            }
        });
    }

    protected void deliverRejectComponent(MAPDialog mapDialog, Long invokeId, Problem problem, boolean isLocalOriginated) {
        this.mapProviderImpl.dispatchApplicationCallback(mapDialog, () -> {
            for (MAPServiceListener mapServiceListener : this.serviceListeners) {
                mapServiceListener.onRejectComponent(mapDialog, invokeId, problem, isLocalOriginated);
            }
        });
    }

    // protected void deliverProviderErrorComponent(MAPDialog mapDialog, Long invokeId, MAPProviderError providerError) {
    // for (MAPServiceListener serLis : this.serviceListeners) {
    // serLis.onProviderErrorComponent(mapDialog, invokeId, providerError);
    // }
    // }

    protected void deliverInvokeTimeout(MAPDialog mapDialog, Invoke invoke) {
        this.mapProviderImpl.dispatchApplicationCallback(mapDialog, () -> {
            for (MAPServiceListener mapServiceListener : this.serviceListeners) {
                mapServiceListener.onInvokeTimeout(mapDialog, invoke.getInvokeId());
            }
        });
    }

    protected void deliverMAPMessage(MAPMessage mapMessage) {
        this.mapProviderImpl.dispatchApplicationCallback(mapMessage.getMAPDialog(), () -> {
            for (MAPServiceListener mapServiceListener : this.serviceListeners) {
                mapServiceListener.onMAPMessage(mapMessage);
            }
        });
    }

}
