package org.restcomm.protocols.ss7.scheduler;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class W2PriorityClassifierTest {

    @Test
    public void shouldClassifyTcapPackageAndLargeContinue() {
        assertEquals(W2PriorityClassifier.classifyTcap(TcapPackageKind.BEGIN, 0), TcapPriority.BEGIN_HIGH);
        assertEquals(W2PriorityClassifier.classifyTcap(TcapPackageKind.CONTINUE,
                W2PriorityClassifier.DEFAULT_LARGE_CONTINUE_BYTES), TcapPriority.CONTINUE_NORMAL);
        assertEquals(W2PriorityClassifier.classifyTcap(TcapPackageKind.CONTINUE,
                W2PriorityClassifier.DEFAULT_LARGE_CONTINUE_BYTES + 1), TcapPriority.CONTINUE_LARGE);
        assertEquals(W2PriorityClassifier.classifyTcap(TcapPackageKind.END, 0), TcapPriority.END_LOW);
        assertEquals(W2PriorityClassifier.classifyTcap(TcapPackageKind.ABORT, 0), TcapPriority.ABORT_LOW);
    }

    @Test
    public void shouldClassifyMapCriticalAndNormalOperationCodes() {
        assertEquals(W2PriorityClassifier.classifyApplication(Ss7ApplicationProtocol.MAP,
                W2PriorityClassifier.MAP_SEND_AUTHENTICATION_INFO), MapPriority.CRITICAL);
        assertEquals(W2PriorityClassifier.classifyApplication(Ss7ApplicationProtocol.MAP,
                W2PriorityClassifier.MAP_UPDATE_LOCATION), MapPriority.CRITICAL);
        assertEquals(W2PriorityClassifier.classifyApplication(Ss7ApplicationProtocol.MAP,
                W2PriorityClassifier.MAP_SEND_ROUTING_INFO_FOR_SM), MapPriority.NORMAL);
        assertEquals(W2PriorityClassifier.classifyApplication(Ss7ApplicationProtocol.MAP,
                W2PriorityClassifier.MAP_MT_FORWARD_SM), MapPriority.NORMAL);
        assertEquals(W2PriorityClassifier.classifyApplication(Ss7ApplicationProtocol.MAP, 46L),
                MapPriority.UNSPECIFIED);
    }

    @Test
    public void shouldClassifyCapChargingAsCriticalAndHandleGlobalOperationFallback() {
        assertEquals(W2PriorityClassifier.classifyApplication(Ss7ApplicationProtocol.CAP,
                W2PriorityClassifier.CAP_APPLY_CHARGING), MapPriority.CRITICAL);
        assertEquals(W2PriorityClassifier.classifyApplication(Ss7ApplicationProtocol.CAP,
                W2PriorityClassifier.CAP_APPLY_CHARGING_REPORT_GPRS), MapPriority.CRITICAL);
        assertEquals(W2PriorityClassifier.classifyApplication(Ss7ApplicationProtocol.MAP, null),
                MapPriority.UNSPECIFIED);
    }

    @Test
    public void shouldAllowCriticalApplicationToRaiseEndPriority() {
        assertEquals(W2PriorityClassifier.classify(TcapPackageKind.END, 0,
                Ss7ApplicationProtocol.MAP, W2PriorityClassifier.MAP_SEND_AUTHENTICATION_INFO),
                W2Priority.CRITICAL);
    }
}
