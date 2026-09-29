package org.restcomm.protocols.ss7.tcap.api;

/**
 * Optional hook consulted when an inbound TC-CONTINUE, TC-END or TC-ABORT
 * carries a destination transaction id that is not in the local dialog map
 * (ADR 0007 M7).
 * <p>
 * In an active/active cluster every node has a disjoint OTID range, so the DTID
 * names the node that owns the dialog. When the STP delivers the message to a
 * different node, this router hands the raw PDU to the owner, which processes
 * it through {@link TCAPProvider#processForeignPdu(TcapForeignPdu)} with its
 * own dialog state intact — no decoded MAP object crosses the cluster and no
 * takeover happens while the owner is alive.
 * <p>
 * Resolution order on a local miss: this router, then
 * {@link TcapMissingDialogResolver} (import / takeover), then the default
 * {@code UnrecognizedTxID} P-Abort. A PDU injected via
 * {@code processForeignPdu} never reaches the router again, so a forward cannot
 * loop between nodes.
 * <p>
 * Called on the TCAP ingress thread: implementations must not block.
 *
 * @author Tran Nhan
 */
@FunctionalInterface
public interface TcapInboundDialogRouter {

    /**
     * @param localOtid destination transaction id of the message
     * @param pdu       the received message
     * @return {@code true} when the PDU was handed to another node and must not
     *         be processed locally; {@code false} to continue with the resolver
     *         and default handling
     */
    boolean routeForeign(long localOtid, TcapForeignPdu pdu);
}
