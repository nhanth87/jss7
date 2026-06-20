
package org.restcomm.protocols.ss7.m3ua.impl.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import org.restcomm.protocols.ss7.m3ua.parameter.ProtocolData;

/**
 * Implements Protocol Data parameter.
 *
 * @author amit bhayani
 * @author kulikov
 */
@JacksonXmlRootElement(localName = "protocolData")
public class ProtocolDataImpl extends ParameterImpl implements ProtocolData {

    @JsonProperty("opc")
    private int opc;
    @JsonProperty("dpc")
    private int dpc;
    @JsonProperty("si")
    private int si;
    @JsonProperty("ni")
    private int ni;
    @JsonProperty("messagePriority")
    private int messagePriority;
    @JsonProperty("sls")
    private int sls;
    @JsonProperty("data")
    private byte[] data;

    /** Zero-copy user payload slice; released by {@link #releaseResources()}. */
    private ByteBuf dataBuf;

    protected ProtocolDataImpl() {
        this.tag = ParameterImpl.Protocol_Data;
    }

    protected ProtocolDataImpl(int opc, int dpc, int si, int ni, int messagePriority, int sls, byte[] data) {
        this();
        this.opc = opc;
        this.dpc = dpc;
        this.si = si;
        this.ni = ni;
        this.messagePriority = messagePriority;
        this.sls = sls;
        this.data = data;
        encode();
    }

    /**
     * Zero-copy variant: {@code userDataBuf} is a retained slice of SCCP PDU bytes
     * (after the 12-byte M3UA routing header).
     */
    protected ProtocolDataImpl(int opc, int dpc, int si, int ni, int messagePriority, int sls, ByteBuf userDataBuf) {
        this();
        this.opc = opc;
        this.dpc = dpc;
        this.si = si;
        this.ni = ni;
        this.messagePriority = messagePriority;
        this.sls = sls;
        this.dataBuf = userDataBuf.retain();
    }

    /**
     * Parses routing fields and retains a slice of user data from a full Protocol Data value buffer.
     */
    public static ProtocolDataImpl fromProtocolDataByteBuf(ByteBuf valueData) {
        if (valueData.readableBytes() < 12) {
            throw new IllegalArgumentException("Protocol Data value shorter than 12-byte routing header");
        }
        int opc = ((valueData.getByte(0) & 0xff) << 24) | ((valueData.getByte(1) & 0xff) << 16)
                | ((valueData.getByte(2) & 0xff) << 8) | (valueData.getByte(3) & 0xff);
        int dpc = ((valueData.getByte(4) & 0xff) << 24) | ((valueData.getByte(5) & 0xff) << 16)
                | ((valueData.getByte(6) & 0xff) << 8) | (valueData.getByte(7) & 0xff);
        int si = valueData.getByte(8) & 0xff;
        int ni = valueData.getByte(9) & 0xff;
        int messagePriority = valueData.getByte(10) & 0xff;
        int sls = valueData.getByte(11) & 0xff;
        ByteBuf userData = valueData.slice(12, valueData.readableBytes() - 12).retain();
        return new ProtocolDataImpl(opc, dpc, si, ni, messagePriority, sls, userData);
    }

    /**
     * Creates new parameter with specified value.
     *
     * @param valueData the value of this parameter
     */
    protected ProtocolDataImpl(byte[] valueData) {
        this();
        decodeHeader(valueData, 0);
        this.data = new byte[valueData.length - 12];
        System.arraycopy(valueData, 12, data, 0, valueData.length - 12);
    }

    /**
     * Zero-copy decode from parameter value buffer (ownership transferred here).
     */
    protected ProtocolDataImpl(ByteBuf valueBuf) {
        this();
        int idx = valueBuf.readerIndex();
        decodeHeader(valueBuf, idx);
        int dataLen = valueBuf.readableBytes() - 12;
        if (dataLen > 0) {
            this.dataBuf = valueBuf.slice(idx + 12, dataLen).retain();
        }
        ReferenceCountUtil.release(valueBuf);
    }

    private void decodeHeader(byte[] valueData, int offset) {
        this.opc = ((valueData[offset] & 0xff) << 24) | ((valueData[offset + 1] & 0xff) << 16)
                | ((valueData[offset + 2] & 0xff) << 8) | (valueData[offset + 3] & 0xff);
        this.dpc = ((valueData[offset + 4] & 0xff) << 24) | ((valueData[offset + 5] & 0xff) << 16)
                | ((valueData[offset + 6] & 0xff) << 8) | (valueData[offset + 7] & 0xff);
        this.si = valueData[offset + 8] & 0xff;
        this.ni = valueData[offset + 9] & 0xff;
        this.messagePriority = valueData[offset + 10] & 0xff;
        this.sls = valueData[offset + 11] & 0xff;
    }

    private void decodeHeader(ByteBuf buf, int idx) {
        this.opc = buf.getInt(idx);
        this.dpc = buf.getInt(idx + 4);
        this.si = buf.getUnsignedByte(idx + 8);
        this.ni = buf.getUnsignedByte(idx + 9);
        this.messagePriority = buf.getUnsignedByte(idx + 10);
        this.sls = buf.getUnsignedByte(idx + 11);
    }

    private byte[] encode() {
        int payloadLen = data != null ? data.length : (dataBuf != null ? dataBuf.readableBytes() : 0);
        byte[] value = new byte[payloadLen + 12];
        writeHeader(value);
        if (data != null) {
            System.arraycopy(data, 0, value, 12, data.length);
        } else if (dataBuf != null) {
            dataBuf.getBytes(dataBuf.readerIndex(), value, 12, payloadLen);
        }
        return value;
    }

    private void writeHeader(byte[] value) {
        value[0] = (byte) (opc >> 24);
        value[1] = (byte) (opc >> 16);
        value[2] = (byte) (opc >> 8);
        value[3] = (byte) (opc);
        value[4] = (byte) (dpc >> 24);
        value[5] = (byte) (dpc >> 16);
        value[6] = (byte) (dpc >> 8);
        value[7] = (byte) (dpc);
        value[8] = (byte) (si);
        value[9] = (byte) (ni);
        value[10] = (byte) (messagePriority);
        value[11] = (byte) (sls);
    }

    public int getOpc() {
        return opc;
    }

    public int getDpc() {
        return dpc;
    }

    public int getSI() {
        return si;
    }

    public int getNI() {
        return ni;
    }

    public int getMP() {
        return messagePriority;
    }

    public int getSLS() {
        return sls;
    }

    public byte[] getData() {
        if (data != null) {
            return data;
        }
        if (dataBuf != null) {
            byte[] copy = new byte[dataBuf.readableBytes()];
            dataBuf.getBytes(dataBuf.readerIndex(), copy);
            return copy;
        }
        return null;
    }

    public ByteBuf getDataBuf() {
        return dataBuf;
    }

    @Override
    public void releaseResources() {
        if (dataBuf != null) {
            ReferenceCountUtil.release(dataBuf);
            dataBuf = null;
        }
        super.releaseResources();
    }

    @Override
    protected byte[] getValue() {
        return this.encode();
    }

    @Override
    public String toString() {
        return String.format("Protocol opc=%d dpc=%d si=%d ni=%d sls=%d", opc, dpc, si, ni, sls);
    }

}
