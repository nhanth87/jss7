package org.restcomm.protocols.ss7.tcap.api;

/**
 * Optional hook invoked when an inbound TC-CONTINUE arrives for a local OTID
 * that is not present in this provider's {@code dialogs} map.
 * <p>
 * Return a {@link TcapDialogSnapshot} to import before processing Continue;
 * return {@code null} to keep the default {@code UnrecognizedTxID} P-Abort.
 * <p>
 * <b>SPIKE / not production HA:</b> callers must still ensure multi-ASP routing,
 * OTID partitioning, and application-layer (MAP) state separately.
 *
 * @author Tran Nhan
 */
@FunctionalInterface
public interface TcapMissingDialogResolver {

    /**
     * @param localOtid destination transaction id from the CONTINUE
     * @return snapshot to {@link TCAPProvider#importDialog}, or {@code null}
     */
    TcapDialogSnapshot resolve(long localOtid);
}
