package org.restcomm.protocols.ss7.tcap.api;

import java.util.Objects;

import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * An inbound TCAP PDU that belongs to a dialog owned by another node (ADR 0007 M7).
 * <p>
 * Carries exactly what {@code TCAPProviderImpl} reads from the SCCP data message
 * so the owner can process it as if its own ASP had received it. The SCCP
 * addresses are kept as received: the first TC-CONTINUE updates the dialog's
 * remote address from the calling party, so they cannot be dropped.
 * <p>
 * Not {@code Serializable} on purpose — {@link SccpAddress} is a stack type.
 * The cluster layer owns the portable encoding.
 *
 * @param data             TCAP message bytes (SCCP user data)
 * @param calledParty      SCCP called party as received (the local side)
 * @param callingParty     SCCP calling party as received (the remote peer)
 * @param sls              signalling link selection of the received message
 * @param networkId        SCCP network id
 * @param incomingOpc      originating point code of the received message
 * @param preferredAspName ASP the message arrived on, or {@code null}
 *
 * @author Tran Nhan
 */
public record TcapForeignPdu(byte[] data, SccpAddress calledParty, SccpAddress callingParty, int sls,
        int networkId, int incomingOpc, String preferredAspName) {

    public TcapForeignPdu {
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(calledParty, "calledParty");
        Objects.requireNonNull(callingParty, "callingParty");
    }
}
