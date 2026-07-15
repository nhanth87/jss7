package org.restcomm.protocols.ss7.map.service.sms;

import org.restcomm.protocols.ss7.map.api.service.sms.MWStatus;
import org.restcomm.protocols.ss7.map.primitives.BitStringBase;

/**
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class MWStatusImpl extends BitStringBase implements MWStatus {

    private static final int _INDEX_ScAddressNotIncluded = 0;
    private static final int _INDEX_MnrfSet = 1;
    private static final int _INDEX_McefSet = 2;
    private static final int _INDEX_MnrgSet = 3;
    private static final int _INDEX_Mnr5gSet = 4;
    private static final int _INDEX_Mnr5gn3gSet = 5;

    public MWStatusImpl() {
        super(4, 16, 6, "MWStatus");
    }

    public MWStatusImpl(boolean scAddressNotIncluded, boolean mnrfSet, boolean mcefSet, boolean mnrgSet, boolean mnr5gSet, boolean mnr5gn3gSet) {
        super(4, 16, 6, "MWStatus");

        if (scAddressNotIncluded)
            this.bitString.set(_INDEX_ScAddressNotIncluded);
        if (mnrfSet)
            this.bitString.set(_INDEX_MnrfSet);
        if (mcefSet)
            this.bitString.set(_INDEX_McefSet);
        if (mnrgSet)
            this.bitString.set(_INDEX_MnrgSet);
        if (mnr5gSet)
            this.bitString.set(_INDEX_Mnr5gSet);
        if (mnr5gn3gSet)
            this.bitString.set(_INDEX_Mnr5gn3gSet);
    }

    public boolean getScAddressNotIncluded() {
        return this.bitString.get(_INDEX_ScAddressNotIncluded);
    }

    public boolean getMnrfSet() {
        return this.bitString.get(_INDEX_MnrfSet);
    }

    public boolean getMcefSet() {
        return this.bitString.get(_INDEX_McefSet);
    }

    public boolean getMnrgSet() {
        return this.bitString.get(_INDEX_MnrgSet);
    }

    public boolean getMnr5gSet() {
        return this.bitString.get(_INDEX_Mnr5gSet);
    }

    public boolean getMnr5gn3gSet() {
        return this.bitString.get(_INDEX_Mnr5gn3gSet);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");
        if (this.getScAddressNotIncluded())
            sb.append("ScAddressNotIncluded, ");
        if (this.getMnrfSet())
            sb.append("MnrfSet, ");
        if (this.getMcefSet())
            sb.append("McefSet, ");
        if (this.getMnrgSet())
            sb.append("MnrgSet, ");
        if (this.getMnr5gSet())
            sb.append("Mnr5gSet, ");
        if (this.getMnr5gn3gSet())
            sb.append("Mnr5gn3gSet, ");
        sb.append("]");
        return sb.toString();
    }

}
