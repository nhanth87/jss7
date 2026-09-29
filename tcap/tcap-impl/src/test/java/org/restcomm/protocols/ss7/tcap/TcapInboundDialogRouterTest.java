package org.restcomm.protocols.ss7.tcap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.sccp.impl.SccpHarness;
import org.restcomm.protocols.ss7.sccp.message.SccpDataMessage;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.tcap.api.TCListener;
import org.restcomm.protocols.ss7.tcap.api.TcapDialogSnapshot;
import org.restcomm.protocols.ss7.tcap.api.TcapForeignPdu;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.Dialog;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.TRPseudoState;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCBeginIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCContinueIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCEndIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCNoticeIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCPAbortIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCUniIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCUserAbortIndication;
import org.restcomm.protocols.ss7.tcap.asn.TcapFactory;
import org.restcomm.protocols.ss7.tcap.asn.Utils;
import org.restcomm.protocols.ss7.tcap.asn.comp.Invoke;
import org.restcomm.protocols.ss7.tcap.asn.comp.TCContinueMessage;
import org.restcomm.protocols.ss7.tcap.asn.comp.TCEndMessage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * ADR 0007 M7 — an inbound PDU whose DTID is not local is handed to the owning
 * node as raw bytes and processed there with the owner's dialog state.
 *
 * <p>
 * Stack A plays the owner, stack B the node the STP happened to deliver to.
 * The "cluster hop" is a direct {@code processForeignPdu} call; the cluster
 * transport itself is the RA's concern.
 *
 * @author Tran Nhan
 */
public class TcapInboundDialogRouterTest extends SccpHarness {

    private TCAPStackImpl stackA;
    private TCAPStackImpl stackB;
    private SccpAddress localAddress;
    private SccpAddress peerAddress;

    @BeforeClass
    public void setUpClass() {
        this.sccpStack1Name = "TcapInboundDialogRouterTestSccpStack1";
        this.sccpStack2Name = "TcapInboundDialogRouterTestSccpStack2";
    }

    private static final String W2_PROP = "ss7.tcap.w2Scheduler.enabled";
    private String previousW2;

    @BeforeMethod
    public void setUp() throws Exception {
        // Process inbound PDUs inline so "was NOT processed" assertions are exact.
        previousW2 = System.getProperty(W2_PROP);
        System.setProperty(W2_PROP, "false");
        super.setUp();
        localAddress = super.parameterFactory.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null, 1, 8);
        peerAddress = super.parameterFactory.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null, 2, 8);
        this.stackA = new TCAPStackImpl("TcapInboundDialogRouter-A", this.sccpProvider1, 8);
        this.stackB = new TCAPStackImpl("TcapInboundDialogRouter-B", this.sccpProvider2, 8);
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
        if (previousW2 == null) {
            System.clearProperty(W2_PROP);
        } else {
            System.setProperty(W2_PROP, previousW2);
        }
    }

    @Test(groups = { "functional.flow" })
    public void continueOnNonOwnerIsProcessedByOwner() throws Exception {
        TCAPProviderImpl providerA = (TCAPProviderImpl) this.stackA.getProvider();
        TCAPProviderImpl providerB = (TCAPProviderImpl) this.stackB.getProvider();
        Probe probeA = new Probe();
        Probe probeB = new Probe();
        providerA.addTCListener(probeA);
        providerB.addTCListener(probeB);

        DialogImpl owned = activeDialog(providerA, 8101L, 9101L);
        AtomicInteger forwarded = new AtomicInteger();
        providerB.setInboundDialogRouter((otid, pdu) -> {
            forwarded.incrementAndGet();
            providerA.processForeignPdu(pdu);
            return true;
        });

        providerB.onMessage(continueFromPeer(providerB, 9101L, 8101L, peerAddress));

        assertEquals(forwarded.get(), 1);
        assertEquals(probeA.continues.get(), 1, "the owner must see the CONTINUE");
        assertEquals(probeB.continues.get(), 0, "the receiving node must not process it");
        assertEquals(probeB.pAborts.get(), 0, "no UnrecognizedTxID P-Abort from the receiving node");
        assertEquals(providerB.getCurrentDialogsCount(), 0, "no takeover while the owner is alive");
        assertEquals(owned.getState(), TRPseudoState.Active);
    }

    /**
     * The first CONTINUE sets the dialog's remote address from the calling party,
     * so a forward that dropped the SCCP addresses would route the next message
     * to the wrong peer.
     */
    @Test(groups = { "functional.flow" })
    public void firstContinueForwardedKeepsPeerAddress() throws Exception {
        TCAPProviderImpl providerA = (TCAPProviderImpl) this.stackA.getProvider();
        TCAPProviderImpl providerB = (TCAPProviderImpl) this.stackB.getProvider();
        providerA.addTCListener(new Probe());

        DialogImpl owned = (DialogImpl) providerA.getNewDialog(localAddress, peerAddress, 8201L);
        owned.setState(TRPseudoState.InitialSent);
        SccpAddress answeringPeer = super.parameterFactory.createSccpAddress(
                RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null, 3, 8);
        providerB.setInboundDialogRouter((otid, pdu) -> {
            providerA.processForeignPdu(pdu);
            return true;
        });

        providerB.onMessage(continueFromPeer(providerB, 9201L, 8201L, answeringPeer));

        assertEquals(owned.getState(), TRPseudoState.Active);
        assertEquals(owned.getRemoteAddress(), answeringPeer, "remote address must come from the forwarded PDU");
        assertEquals(owned.getRemoteDialogId().longValue(), 9201L);
    }

    @Test(groups = { "functional.flow" })
    public void endOnNonOwnerIsProcessedByOwner() throws Exception {
        TCAPProviderImpl providerA = (TCAPProviderImpl) this.stackA.getProvider();
        TCAPProviderImpl providerB = (TCAPProviderImpl) this.stackB.getProvider();
        Probe probeA = new Probe();
        providerA.addTCListener(probeA);

        activeDialog(providerA, 8301L, 9301L);
        providerB.setInboundDialogRouter((otid, pdu) -> {
            providerA.processForeignPdu(pdu);
            return true;
        });

        TCEndMessage end = TcapFactory.createTCEndMessage();
        end.setDestinationTransactionId(Utils.encodeTransactionId(8301L, this.stackB.getSwapTcapIdBytes()));
        providerB.onMessage(dataMessage(providerB, end::encode, peerAddress));

        assertEquals(probeA.ends.get(), 1, "the owner must see the END");
        assertEquals(providerA.getCurrentDialogsCount(), 0, "the owner releases the dialog, no idle-window leak");
    }

    /** A forwarded PDU must never be forwarded again — no ping-pong between nodes. */
    @Test(groups = { "functional.flow" })
    public void foreignPduIsNeverRoutedAgain() throws Exception {
        TCAPProviderImpl providerA = (TCAPProviderImpl) this.stackA.getProvider();
        TCAPProviderImpl providerB = (TCAPProviderImpl) this.stackB.getProvider();
        AtomicInteger routedOnA = new AtomicInteger();
        providerA.setInboundDialogRouter((otid, pdu) -> {
            routedOnA.incrementAndGet();
            return true;
        });
        AtomicReference<TcapForeignPdu> captured = new AtomicReference<>();
        providerB.setInboundDialogRouter((otid, pdu) -> {
            captured.set(pdu);
            return true;
        });

        providerB.onMessage(continueFromPeer(providerB, 9401L, 8401L, peerAddress));
        assertNotNull(captured.get());

        // A does not have the dialog either (it moved or ended): it must fall
        // through to its own default handling instead of routing back.
        providerA.processForeignPdu(captured.get());
        assertEquals(routedOnA.get(), 0);
    }

    /** Declining the route keeps the P0 path: resolver import, then local processing. */
    @Test(groups = { "functional.flow" })
    public void declinedRouteFallsBackToResolver() throws Exception {
        TCAPProviderImpl providerA = (TCAPProviderImpl) this.stackA.getProvider();
        TCAPProviderImpl providerB = (TCAPProviderImpl) this.stackB.getProvider();
        Probe probeB = new Probe();
        providerB.addTCListener(probeB);

        activeDialog(providerA, 8501L, 9501L);
        TcapDialogSnapshot snapshot = providerA.exportDialog(8501L);
        providerA.detachDialogForFailover(8501L);

        providerB.setInboundDialogRouter((otid, pdu) -> false);   // owner is dead
        providerB.setMissingDialogResolver(otid -> otid == 8501L ? snapshot : null);

        providerB.onMessage(continueFromPeer(providerB, 9501L, 8501L, peerAddress));

        assertEquals(probeB.continues.get(), 1, "the survivor takes over via the resolver");
        assertEquals(probeB.pAborts.get(), 0);
    }

    /** A throwing router must degrade to single-node handling, never lose the PDU silently. */
    @Test(groups = { "functional.flow" })
    public void throwingRouterDegradesToDefault() throws Exception {
        TCAPProviderImpl providerB = (TCAPProviderImpl) this.stackB.getProvider();
        AtomicInteger resolverCalls = new AtomicInteger();
        providerB.setInboundDialogRouter((otid, pdu) -> {
            throw new IllegalStateException("cluster down");
        });
        providerB.setMissingDialogResolver(otid -> {
            resolverCalls.incrementAndGet();
            return null;
        });

        providerB.onMessage(continueFromPeer(providerB, 9601L, 8601L, peerAddress));

        assertTrue(resolverCalls.get() >= 1, "the resolver must still be consulted");
    }

    // ── helpers ──────────────────────────────────────────────────────────

    private DialogImpl activeDialog(TCAPProviderImpl provider, long localOtid, long remoteOtid) throws Exception {
        Dialog dialog = provider.getNewDialog(localAddress, peerAddress, localOtid);
        DialogImpl live = (DialogImpl) dialog;
        live.setRemoteTransactionId(Utils.encodeTransactionId(remoteOtid, this.stackA.getSwapTcapIdBytes()));
        live.setRemotePc(2);
        live.setState(TRPseudoState.Active);
        return live;
    }

    private SccpDataMessage continueFromPeer(TCAPProviderImpl receiver, long peerOtid, long ourOtid,
            SccpAddress callingPeer) throws Exception {
        TCContinueMessage cont = TcapFactory.createTCContinueMessage();
        cont.setOriginatingTransactionId(Utils.encodeTransactionId(peerOtid, this.stackB.getSwapTcapIdBytes()));
        cont.setDestinationTransactionId(Utils.encodeTransactionId(ourOtid, this.stackB.getSwapTcapIdBytes()));
        return dataMessage(receiver, cont::encode, callingPeer);
    }

    private SccpDataMessage dataMessage(TCAPProviderImpl receiver, Encoder encoder, SccpAddress callingPeer)
            throws Exception {
        AsnOutputStream aos = new AsnOutputStream();
        encoder.encode(aos);
        SccpDataMessage msg = super.sccpProvider2.getMessageFactory().createDataMessageClass1(localAddress, callingPeer,
                aos.toByteArray(), 0, 8, false, null, null);
        msg.setIncomingOpc(callingPeer.getSignalingPointCode());
        return msg;
    }

    @FunctionalInterface
    private interface Encoder {
        void encode(AsnOutputStream aos) throws Exception;
    }

    private static final class Probe implements TCListener {
        final AtomicInteger continues = new AtomicInteger();
        final AtomicInteger ends = new AtomicInteger();
        final AtomicInteger pAborts = new AtomicInteger();

        @Override
        public void onTCUni(TCUniIndication ind) {
        }

        @Override
        public void onTCBegin(TCBeginIndication ind) {
        }

        @Override
        public void onTCContinue(TCContinueIndication ind) {
            continues.incrementAndGet();
        }

        @Override
        public void onTCEnd(TCEndIndication ind) {
            ends.incrementAndGet();
        }

        @Override
        public void onTCUserAbort(TCUserAbortIndication ind) {
        }

        @Override
        public void onTCPAbort(TCPAbortIndication ind) {
            pAborts.incrementAndGet();
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
