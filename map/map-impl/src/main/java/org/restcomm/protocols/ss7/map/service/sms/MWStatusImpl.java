package org.restcomm.protocols.ss7.map.service.sms;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnStreamPool;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;

import io.netty.buffer.ByteBuf;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.service.sms.MWStatus;
import org.restcomm.protocols.ss7.map.primitives.BitStringBase;

import java.io.IOException;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class MWStatusImpl extends BitStringBase implements MWStatus {

    private static final int _INDEX_ScAddressNotIncluded = 0;
    private static final int _INDEX_MnrfSet = 1;
    private static final int _INDEX_McefSet = 2;
    private static final int _INDEX_MnrgSet = 3;

    public MWStatusImpl() {
        super(4, 16, 6, "MWStatus");
    }

    public MWStatusImpl(boolean scAddressNotIncluded, boolean mnrfSet, boolean mcefSet, boolean mnrgSet) {
        super(4, 16, 6, "MWStatus");

        if (scAddressNotIncluded)
            this.bitString.set(_INDEX_ScAddressNotIncluded);
        if (mnrfSet)
            this.bitString.set(_INDEX_MnrfSet);
        if (mcefSet)
            this.bitString.set(_INDEX_McefSet);
        if (mnrgSet)
            this.bitString.set(_INDEX_MnrgSet);
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

    public void decodeFromBitStringView(byte[] buf, int off, int len) throws MAPParsingComponentException {
        try {
            AsnInputStream ais = AsnStreamPool.borrowSlice(buf, off, len);
            this._decode(ais, len);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding MWStatus view: " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding MWStatus view: " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    public void decodeFromBitStringByteBufView(ByteBuf buf, int off, int len) throws MAPParsingComponentException {
        try {
            AsnInputStream ais = AsnStreamPool.borrowByteBufSlice(buf, off, len);
            this._decode(ais, len);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding MWStatus view: " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding MWStatus view: " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
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
        sb.append("]");
        return sb.toString();
    }

}
