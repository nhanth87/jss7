package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.RequestedNodes;
import org.restcomm.protocols.ss7.map.primitives.BitStringBase;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class RequestedNodesImpl extends BitStringBase implements RequestedNodes {

    private static final int _INDEX_MME = 0;
    private static final int _INDEX_SGSN = 1;
    public static final String _PrimitiveName = "RequestedNodes";

    public RequestedNodesImpl() {
        super(1, 8, 8, _PrimitiveName);
    }

    public RequestedNodesImpl(boolean mme, boolean sgsn) {
        super(1, 8, 8, _PrimitiveName);

        if (mme)
            this.bitString.set(_INDEX_MME);
        if (sgsn)
            this.bitString.set(_INDEX_SGSN);
    }

    @Override
    public boolean getMme() {
        return this.bitString.get(_INDEX_MME);
    }

    @Override
    public boolean getSgsn() {
        return this.bitString.get(_INDEX_SGSN);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");
        if (this.getMme())
            sb.append("mme, ");
        if (this.getSgsn())
            sb.append("sgsn ");
        sb.append("]");
        return sb.toString();
    }
}
