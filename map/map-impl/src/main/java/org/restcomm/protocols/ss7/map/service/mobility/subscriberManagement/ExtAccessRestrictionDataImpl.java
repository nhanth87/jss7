package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtAccessRestrictionData;
import org.restcomm.protocols.ss7.map.primitives.BitStringBase;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ExtAccessRestrictionDataImpl extends BitStringBase implements ExtAccessRestrictionData {

    public static final String _PrimitiveName = "ExtAdjacentRestrictionData";
    private static final int _INDEX_nrAsSecondaryRATNotAllowed = 0;
    private static final int _INDEX_unlicensedSpectrumAsSecondaryRATNotAllowed = 1;

    public ExtAccessRestrictionDataImpl() {
        super(1, 32, 32, _PrimitiveName);
    }

    public ExtAccessRestrictionDataImpl(boolean nrAsSecondaryRATNotAllowed, boolean unlicensedSpectrumAsSecondaryRATNotAllowed) {
        super(1, 32, 32, _PrimitiveName);
        if (nrAsSecondaryRATNotAllowed)
            this.bitString.set(_INDEX_nrAsSecondaryRATNotAllowed);
        if(unlicensedSpectrumAsSecondaryRATNotAllowed)
            this.bitString.set(_INDEX_unlicensedSpectrumAsSecondaryRATNotAllowed);
    }

    @Override
    public boolean getNrAsSecondaryRATNotAllowed() {
        return this.bitString.get(_INDEX_nrAsSecondaryRATNotAllowed);
    }

    @Override
    public boolean getUnlicensedSpectrumAsSecondaryRATNotAllowed() {
        return this.bitString.get(_INDEX_unlicensedSpectrumAsSecondaryRATNotAllowed);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");
        if (this.getNrAsSecondaryRATNotAllowed())
            sb.append("nrAsSecondaryRATNotAllowed, ");
        if (this.getUnlicensedSpectrumAsSecondaryRATNotAllowed())
            sb.append("unlicensedSpectrumAsSecondaryRATNotAllowed");
        sb.append("]");
        return sb.toString();
    }
}
