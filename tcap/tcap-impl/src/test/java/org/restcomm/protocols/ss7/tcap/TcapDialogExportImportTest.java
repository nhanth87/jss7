package org.restcomm.protocols.ss7.tcap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

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
import org.restcomm.protocols.ss7.tcap.asn.TcapFactory;
import org.restcomm.protocols.ss7.tcap.asn.Utils;
import org.restcomm.protocols.ss7.tcap.asn.comp.Invoke;
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

    private static final class ContinueProbe implements TCListener {
        final AtomicInteger continueCount = new AtomicInteger();
        final AtomicInteger pAbortCount = new AtomicInteger();
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
