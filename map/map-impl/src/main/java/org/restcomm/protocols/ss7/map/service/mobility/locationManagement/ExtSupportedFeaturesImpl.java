package org.restcomm.protocols.ss7.map.service.mobility.locationManagement;

import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.ExtSupportedFeatures;
import org.restcomm.protocols.ss7.map.primitives.BitStringBase;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ExtSupportedFeaturesImpl extends BitStringBase implements ExtSupportedFeatures {

    private static final int _INDEX_unlicensedSpectrumAsSecondaryRAT = 0;

    public ExtSupportedFeaturesImpl() {
        super(1,40,1,"ExtSupportedFeatures");
    }

    public ExtSupportedFeaturesImpl(boolean isUnlicensedSpectrumAsSecondaryRAT) {
        super(1,40,1,"ExtSupportedFeatures");

        if (isUnlicensedSpectrumAsSecondaryRAT)
            this.bitString.set(_INDEX_unlicensedSpectrumAsSecondaryRAT);
    }

    @Override
    public boolean isUnlicensedSpectrumAsSecondaryRAT() {
        return this.bitString.get(_INDEX_unlicensedSpectrumAsSecondaryRAT);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");
        if (this.isUnlicensedSpectrumAsSecondaryRAT())
            sb.append(" unlicensedSpectrumAsSecondaryRAT ");

        sb.append("]");
        return sb.toString();
    }
}
