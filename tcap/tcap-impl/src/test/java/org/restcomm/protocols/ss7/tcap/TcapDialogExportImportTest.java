package org.restcomm.protocols.ss7.tcap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.sccp.impl.SccpHarness;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.tcap.api.TCListener;
import org.restcomm.protocols.ss7.tcap.api.TcapDialogSnapshot;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.Dialog;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.TRPseudoState;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCBeginIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCContinueIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCEndIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCNoticeIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCPAbortIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCUniIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCUserAbortIndication;
import org.restcomm.protocols.ss7.tcap.asn.InvokeImpl;
import org.restcomm.protocols.ss7.tcap.asn.OperationCodeImpl;
import org.restcomm.protocols.ss7.tcap.asn.ReturnResultLastImpl;
import org.restcomm.protocols.ss7.tcap.asn.TcapFactory;
import org.restcomm.protocols.ss7.tcap.asn.Utils;
import org.restcomm.protocols.ss7.tcap.api.tc.component.InvokeClass;
import org.restcomm.protocols.ss7.tcap.api.tc.component.OperationState;
import org.restcomm.protocols.ss7.tcap.asn.comp.Invoke;
import org.restcomm.protocols.ss7.tcap.asn.comp.OperationCodeType;
import org.restcomm.protocols.ss7.tcap.asn.comp.PAbortCauseType;
import org.restcomm.protocols.ss7.tcap.asn.comp.TCContinueMessage;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * SPIKE: prove export -> detach -> import restores enough state for CONTINUE
 * without UnrecognizedTxID. Not production multi-node HA.
 *
 * @author Tran Nhan
 */
public class TcapDialogExportImportTest extends SccpHarness {

    private TCAPStackImpl stackA;
    private TCAPStackImpl stackB;
    private SccpAddress peer1Address;
    private SccpAddress peer2Address;

    @BeforeClass
    public void setUpClass() {
        this.sccpStack1Name = "TcapDialogExportImportTestSccpStack1";
        this.sccpStack2Name = "TcapDialogExportImportTestSccpStack2";
    }

    @AfterClass
    public void tearDownClass() {
    }

    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        peer1Address = super.parameterFactory.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null, 1, 8);
        peer2Address = super.parameterFactory.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null, 2, 8);

        this.stackA = new TCAPStackImpl("TcapDialogExportImport-A", this.sccpProvider1, 8);
        this.stackB = new TCAPStackImpl("TcapDialogExportImport-B", this.sccpProvider2, 8);
        this.stackA.start();
        this.stackB.start();
        this.stackA.setInvokeTimeout(0);
        this.stackB.setInvokeTimeout(0);
        this.stackA.setDialogIdleTimeout(60000);
        this.stackB.setDialogIdleTimeout(60000);
    }

    @AfterMethod
    public void tearDown() {
        if (this.stackA != null) {
            this.stackA.stop();
        }
        if (this.stackB != null) {
            this.stackB.stop();
        }
        super.tearDown();
    }

    @Test(groups = { "functional.flow" })
    public void exportImportSameProviderContinueAccepted() throws Exception {
        TCAPProviderImpl provider = (TCAPProviderImpl) this.stackA.getProvider();
        ContinueProbe probe = new ContinueProbe();
        provider.addTCListener(probe);

        Dialog dialog = provider.getNewDialog(peer1Address, peer2Address);
        long localOtid = dialog.getLocalDialogId();
        byte[] remoteOtid = Utils.encodeTransactionId(9999L, this.stackA.getSwapTcapIdBytes());

        DialogImpl live = (DialogImpl) dialog;
        live.setRemoteTransactionId(remoteOtid);
        live.setRemotePc(2);
        live.setNetworkId(0);
        live.setState(TRPseudoState.Active);

        TcapDialogSnapshot snapshot = provider.exportDialog(localOtid);
        assertNotNull(snapshot);
        assertEquals(snapshot.getLocalOtid(), localOtid);
        assertEquals(snapshot.getState(), TRPseudoState.Active);
        assertNotNull(snapshot.getRemoteOtid());

        provider.detachDialogForFailover(localOtid);
        assertNull(provider.exportDialog(localOtid));
        assertEquals(provider.getCurrentDialogsCount(), 0);

        Dialog imported = provider.importDialog(snapshot);
        assertNotNull(imported);
        assertEquals(imported.getLocalDialogId().longValue(), localOtid);
        assertEquals(imported.getState(), TRPseudoState.Active);
        assertEquals(imported.getRemoteDialogId().longValue(), 9999L);
        assertFalse(provider.exportDialog(localOtid) == null);

        TCContinueMessage continueMessage = TcapFactory.createTCContinueMessage();
        continueMessage.setOriginatingTransactionId(remoteOtid);
        continueMessage.setDestinationTransactionId(
                Utils.encodeTransactionId(localOtid, this.stackA.getSwapTcapIdBytes()));

        ((DialogImpl) imported).processContinue(continueMessage, peer1Address, peer2Address);

        assertEquals(probe.continueCount.get(), 1);
        assertEquals(probe.pAbortCount.get(), 0);
        assertEquals(imported.getState(), TRPseudoState.Active);
    }

    @Test(groups = { "functional.flow" })
    public void exportImportOntoSecondProviderContinueAccepted() throws Exception {
        TCAPProviderImpl providerA = (TCAPProviderImpl) this.stackA.getProvider();
        TCAPProviderImpl providerB = (TCAPProviderImpl) this.stackB.getProvider();
        ContinueProbe probeB = new ContinueProbe();
        providerB.addTCListener(probeB);

        Dialog dialog = providerA.getNewDialog(peer1Address, peer2Address, 42L);
        byte[] remoteOtid = Utils.encodeTransactionId(4242L, this.stackA.getSwapTcapIdBytes());
        DialogImpl live = (DialogImpl) dialog;
        live.setRemoteTransactionId(remoteOtid);
        live.setRemotePc(2);
        live.setState(TRPseudoState.Active);

        TcapDialogSnapshot snapshot = providerA.exportDialog(42L);
        assertNotNull(snapshot);

        providerA.detachDialogForFailover(42L);

        Dialog imported = providerB.importDialog(snapshot);
        assertEquals(imported.getLocalDialogId().longValue(), 42L);
        assertEquals(imported.getState(), TRPseudoState.Active);

        TCContinueMessage continueMessage = TcapFactory.createTCContinueMessage();
        continueMessage.setOriginatingTransactionId(remoteOtid);
        continueMessage.setDestinationTransactionId(
                Utils.encodeTransactionId(42L, this.stackB.getSwapTcapIdBytes()));
        ((DialogImpl) imported).processContinue(continueMessage, peer2Address, peer1Address);

        assertEquals(probeB.continueCount.get(), 1);
        assertEquals(probeB.pAbortCount.get(), 0);
    }

    @Test(groups = { "functional.flow" })
    public void missingDialogResolverImportsBeforeContinue() throws Exception {
        TCAPProviderImpl providerA = (TCAPProviderImpl) this.stackA.getProvider();
        TCAPProviderImpl providerB = (TCAPProviderImpl) this.stackB.getProvider();
        ContinueProbe probeB = new ContinueProbe();
        providerB.addTCListener(probeB);

        Dialog dialog = providerA.getNewDialog(peer1Address, peer2Address, 77L);
        byte[] remoteOtid = Utils.encodeTransactionId(7700L, this.stackA.getSwapTcapIdBytes());
        DialogImpl live = (DialogImpl) dialog;
        live.setRemoteTransactionId(remoteOtid);
        live.setRemotePc(2);
        live.setState(TRPseudoState.Active);

        TcapDialogSnapshot snapshot = providerA.exportDialog(77L);
        assertNotNull(snapshot);
        providerA.detachDialogForFailover(77L);

        providerB.setMissingDialogResolver(otid -> otid == 77L ? snapshot : null);

        assertNull(providerB.exportDialog(77L));
        DialogImpl imported = providerB.tryImportMissingDialog(77L);
        assertNotNull(imported);
        assertEquals(imported.getLocalDialogId().longValue(), 77L);

        TCContinueMessage continueMessage = TcapFactory.createTCContinueMessage();
        continueMessage.setOriginatingTransactionId(remoteOtid);
        continueMessage.setDestinationTransactionId(
                Utils.encodeTransactionId(77L, this.stackB.getSwapTcapIdBytes()));
        imported.processContinue(continueMessage, peer2Address, peer1Address);

        assertEquals(probeB.continueCount.get(), 1);
        assertEquals(probeB.pAbortCount.get(), 0);
    }

    /**
     * ADR 0007 P0 / M1 — the case every earlier test avoided: a dialog with an
     * <b>outstanding invoke</b>, exported and imported into another provider.
     *
     * <p>
     * The three original tests all used {@code setInvokeTimeout(0)} in
     * {@code setUp}, so the pending-invoke path was never exercised. Without this
     * test the take-over silently answers the real peer with
     * {@code Reject(UnrecognizedInvokeID)}.
     */
    @Test(groups = { "functional.flow" })
    public void exportImportPreservesPendingInvokeAcrossProviders() throws Exception {
        // A real operation timeout this time — the timer must be re-armed on import.
        this.stackA.setInvokeTimeout(30000);
        this.stackB.setInvokeTimeout(30000);

        TCAPProviderImpl providerA = (TCAPProviderImpl) this.stackA.getProvider();
        TCAPProviderImpl providerB = (TCAPProviderImpl) this.stackB.getProvider();
        ContinueProbe probeB = new ContinueProbe();
        providerB.addTCListener(probeB);

        Dialog dialog = providerA.getNewDialog(peer1Address, peer2Address, 5501L);
        DialogImpl live = (DialogImpl) dialog;
        byte[] remoteOtid = Utils.encodeTransactionId(5502L, this.stackA.getSwapTcapIdBytes());
        live.setRemoteTransactionId(remoteOtid);
        live.setRemotePc(2);
        live.setState(TRPseudoState.Active);

        // Send a real Invoke (Class 1) and remember its id — this is the operation
        // the peer will later answer with ReturnResult(Last).
        InvokeImpl sent = new InvokeImpl(InvokeClass.Class1);
        sent.setInvokeId(3L);
        sent.setProvider(providerA);
        sent.setDialog(live);
        OperationCodeImpl oc = new OperationCodeImpl();
        oc.setOperationType(OperationCodeType.Local);
        oc.setLocalOperationCode(45L); // MAP sendRoutingInfoForSM
        sent.setOperationCode(oc);
        sent.setTimeout(30000);
        live.putPendingInvokeForTest(3L, sent);
        sent.setState(OperationState.Sent);

        assertEquals(live.getPendingInvokeCount(), 1, "live dialog must report one outstanding operation");

        TcapDialogSnapshot snapshot = providerA.exportDialog(5501L);
        assertNotNull(snapshot);
        TcapDialogSnapshot.PendingInvoke[] pendings = snapshot.getPendingInvokes();
        assertNotNull(pendings, "snapshot must carry the pending invoke (M1)");
        assertEquals(pendings.length, 1);
        assertEquals(pendings[0].getInvokeId(), 3);
        assertEquals(pendings[0].getInvokeClass(), 1);
        assertEquals(pendings[0].getLocalOperationCode(), Long.valueOf(45L));

        providerA.detachDialogForFailover(5501L);
        Dialog imported = providerB.importDialog(snapshot);
        assertNotNull(imported);
        assertEquals(imported.getPendingInvokeCount(), 1, "imported dialog must restore the outstanding operation");

        // The peer's answer: ReturnResult(Last) for invokeId 3.
        TCContinueMessage continueMessage = TcapFactory.createTCContinueMessage();
        continueMessage.setOriginatingTransactionId(remoteOtid);
        continueMessage.setDestinationTransactionId(Utils.encodeTransactionId(5501L, this.stackB.getSwapTcapIdBytes()));

        continueMessage.setComponent(new org.restcomm.protocols.ss7.tcap.asn.comp.Component[] {
                returnResultLast(3L) });

        ((DialogImpl) imported).processContinue(continueMessage, peer2Address, peer1Address);

        assertEquals(probeB.pAbortCount.get(), 0, "must not P-Abort");
        assertEquals(probeB.rejectCount.get(), 0,
                "must NOT emit Reject(UnrecognizedInvokeID) at the real peer — the invoke was restored");
        assertEquals(probeB.continueCount.get(), 1, "the ReturnResult(Last) must be delivered upward");
        assertEquals(imported.getPendingInvokeCount(), 0, "the operation is complete after ReturnResult(Last)");
    }

    /**
     * ADR 0007 P0 / M2 — the idle deadline must cross the JVM boundary as wall
     * clock. Before M2 the snapshot carried {@code System.nanoTime()}, so an
     * import either got a fresh full window or expired instantly depending on
     * uptime delta.
     */
    @Test(groups = { "functional.flow" })
    public void snapshotIdleDeadlineIsWallClock() throws Exception {
        TCAPProviderImpl providerA = (TCAPProviderImpl) this.stackA.getProvider();
        Dialog dialog = providerA.getNewDialog(peer1Address, peer2Address, 6101L);
        DialogImpl live = (DialogImpl) dialog;
        live.setRemoteTransactionId(Utils.encodeTransactionId(6102L, this.stackA.getSwapTcapIdBytes()));
        live.setState(TRPseudoState.Active);

        TcapDialogSnapshot snapshot = providerA.exportDialog(6101L);
        assertNotNull(snapshot);
        long deadline = snapshot.getIdleDeadlineEpochMs();
        assertTrue(deadline > 0L, "idle deadline must be present as epoch ms");
        long now = System.currentTimeMillis();
        // A 60s idle timeout => deadline is now..now+60s in wall clock.
        assertTrue(deadline >= now, "deadline must not be in the past: " + deadline + " < " + now);
        assertTrue(deadline <= now + 60000L, "deadline must be within the idle window: " + deadline);
        // Cross-JVM sanity: nanoTime on a fresh JVM can be near 0 or huge; a wall
        // clock value is always ~1.7e12. Guard the regression explicitly.
        assertTrue(deadline > 1_600_000_000_000L, "deadline must look like epoch ms, not a monotonic clock");
    }

    /**
     * ADR 0007 P0 / M6 — if an operation genuinely cannot be restored, the dialog
     * must NOT answer the real peer with a Reject. The component passes through
     * and the upper layer decides.
     */
    @Test(groups = { "functional.flow" })
    public void unmatchableResponseOnImportedDialogDoesNotRejectPeer() throws Exception {
        TCAPProviderImpl providerA = (TCAPProviderImpl) this.stackA.getProvider();
        TCAPProviderImpl providerB = (TCAPProviderImpl) this.stackB.getProvider();
        ContinueProbe probeB = new ContinueProbe();
        providerB.addTCListener(probeB);

        Dialog dialog = providerA.getNewDialog(peer1Address, peer2Address, 6201L);
        DialogImpl live = (DialogImpl) dialog;
        live.setRemoteTransactionId(Utils.encodeTransactionId(6202L, this.stackA.getSwapTcapIdBytes()));
        live.setState(TRPseudoState.Active);

        TcapDialogSnapshot snapshot = providerA.exportDialog(6201L);
        assertNotNull(snapshot);
        providerA.detachDialogForFailover(6201L);

        Dialog imported = providerB.importDialog(snapshot);
        assertNotNull(imported);
        assertEquals(imported.getPendingInvokeCount(), 0);

        // A response for an invoke id this dialog never sent (snapshot carried none).
        TCContinueMessage continueMessage = TcapFactory.createTCContinueMessage();
        continueMessage.setOriginatingTransactionId(Utils.encodeTransactionId(6202L, this.stackB.getSwapTcapIdBytes()));
        continueMessage.setDestinationTransactionId(Utils.encodeTransactionId(6201L, this.stackB.getSwapTcapIdBytes()));
        continueMessage.setComponent(new org.restcomm.protocols.ss7.tcap.asn.comp.Component[] {
                returnResultLast(9L) });

        ((DialogImpl) imported).processContinue(continueMessage, peer2Address, peer1Address);

        assertEquals(probeB.rejectCount.get(), 0,
                "an imported dialog must not Reject the real peer for an operation it never had");
    }

    /** A minimal ReturnResult(Last) — the response that must not be Rejected. */
    private static org.restcomm.protocols.ss7.tcap.asn.comp.Component returnResultLast(long invokeId) {
        ReturnResultLastImpl rrl = new ReturnResultLastImpl();
        rrl.setInvokeId(invokeId);
        return rrl;
    }

    private static final class ContinueProbe implements TCListener {
        final AtomicInteger continueCount = new AtomicInteger();
        final AtomicInteger pAbortCount = new AtomicInteger();
        final AtomicInteger rejectCount = new AtomicInteger();
        final List<PAbortCauseType> pAbortCauses = new ArrayList<>();

        @Override
        public void onTCUni(TCUniIndication ind) {
        }

        @Override
        public void onTCBegin(TCBeginIndication ind) {
        }

        @Override
        public void onTCContinue(TCContinueIndication ind) {
            continueCount.incrementAndGet();
            org.restcomm.protocols.ss7.tcap.asn.comp.Component[] comps = ind.getComponents();
            if (comps != null) {
                for (org.restcomm.protocols.ss7.tcap.asn.comp.Component c : comps) {
                    if (c.getType() == org.restcomm.protocols.ss7.tcap.asn.comp.ComponentType.Reject) {
                        rejectCount.incrementAndGet();
                    }
                }
            }
        }

        @Override
        public void onTCEnd(TCEndIndication ind) {
        }

        @Override
        public void onTCUserAbort(TCUserAbortIndication ind) {
        }

        @Override
        public void onTCPAbort(TCPAbortIndication ind) {
            pAbortCount.incrementAndGet();
            pAbortCauses.add(ind.getPAbortCause());
        }

        @Override
        public void onTCNotice(TCNoticeIndication ind) {
        }

        @Override
        public void onDialogReleased(Dialog d) {
        }

        @Override
        public void onInvokeTimeout(Invoke invoke) {
        }

        @Override
        public void onDialogTimeout(Dialog d) {
        }
    }
}
