package org.restcomm.protocols.ss7.map.primitives;

import java.io.IOException;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;

import io.netty.buffer.ByteBuf;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class LMSIImpl implements LMSI, MAPAsnPrimitive {

    private byte[] data;

    private boolean dataViewActive;
    private byte[] viewBuffer;
    private ByteBuf viewByteBuf;
    private int viewOffset;
    private int viewLength;
    private boolean byteBufViewActive;

    public LMSIImpl() {
    }

    public LMSIImpl(byte[] data) {
        this.data = data;
    }

    public int getTag() {
        return Tag.STRING_OCTET;
    }

    public int getTagClass() {
        return Tag.CLASS_UNIVERSAL;
    }

    public boolean getIsPrimitive() {
        return true;
    }

    public byte[] getData() {
        if (this.dataViewActive) {
            byte[] copy = new byte[4];
            System.arraycopy(this.viewBuffer, this.viewOffset, copy, 0, 4);
            return copy;
        }
        return this.data;
    }

    public void decodeFromOctetView(byte[] buffer, int offset, int length) throws MAPParsingComponentException {
        if (length != 4) {
            throw new MAPParsingComponentException("Error decoding LMSI: the LMSI field must contain 4 octets. Contains: "
                    + length, MAPParsingComponentExceptionReason.MistypedParameter);
        }
        this.viewBuffer = buffer;
        this.viewByteBuf = null;
        this.viewOffset = offset;
        this.viewLength = 4;
        this.dataViewActive = true;
        this.byteBufViewActive = false;
        this.data = null;
    }

    public void decodeFromByteBufView(ByteBuf buffer, int offset, int length) throws MAPParsingComponentException {
        if (length != 4) {
            throw new MAPParsingComponentException("Error decoding LMSI: the LMSI field must contain 4 octets. Contains: "
                    + length, MAPParsingComponentExceptionReason.MistypedParameter);
        }
        this.viewByteBuf = buffer;
        this.viewBuffer = null;
        this.viewOffset = offset;
        this.viewLength = 4;
        this.byteBufViewActive = true;
        this.dataViewActive = false;
        this.data = null;
    }

    public void decodeAll(AsnInputStream asnInputStream) throws MAPParsingComponentException {
        try {
            int length = asnInputStream.readLength();
            this._decode(asnInputStream, length);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding LMSI: " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    public void decodeData(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException {
        try {
            this._decode(asnInputStream, length);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding LMSI: " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException {
        if (length != 4)
            throw new MAPParsingComponentException("Error decoding LMSI: the LMSI field must contain 4 octets. Contains: "
                    + length, MAPParsingComponentExceptionReason.MistypedParameter);

        try {
            this.dataViewActive = false;
            this.data = new byte[4];
            asnInputStream.read(this.data);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding LMSI: " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    public void encodeAll(AsnOutputStream asnOutputStream) throws MAPException {
        this.encodeAll(asnOutputStream, Tag.CLASS_UNIVERSAL, Tag.STRING_OCTET);
    }

    public void encodeAll(AsnOutputStream asnOutputStream, int tagClass, int tag) throws MAPException {
        try {
            asnOutputStream.writeTag(tagClass, true, tag);
            int pos = asnOutputStream.StartContentDefiniteLength();
            this.encodeData(asnOutputStream);
            asnOutputStream.FinalizeContent(pos);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding LMSI: " + e.getMessage(), e);
        }
    }

    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        if (this.dataViewActive) {
            asnOutputStream.write(this.viewBuffer, this.viewOffset, this.viewLength);
            return;
        }
        if (this.data == null)
            throw new MAPException("Error while encoding the LMSI: data is not defined");

        if (this.data.length != 4)
            throw new MAPException("Error while encoding the LMSI: data field length must equale 4");

        asnOutputStream.write(this.data);
    }

    @Override
    public String toString() {
        return "LMSI [Data= " + this.printDataArr() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LMSIImpl other = (LMSIImpl) obj;
        if (!Arrays.equals(data, other.data))
            return false;
        return true;
    }

    private String printDataArr() {
        StringBuilder sb = new StringBuilder();
        if (this.data != null) {
            for (int b : this.data) {
                sb.append(b);
                sb.append(" ");
            }
        }

        return sb.toString();
    }
}
