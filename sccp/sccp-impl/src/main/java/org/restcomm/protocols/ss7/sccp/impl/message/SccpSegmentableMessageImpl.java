package org.restcomm.protocols.ss7.sccp.impl.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;

import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.sccp.impl.SccpStackImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SegmentationImpl;
import org.restcomm.protocols.ss7.sccp.parameter.HopCounter;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.sccp.parameter.Segmentation;

/**
 *
 * This interface represents a SCCP message for connectionless data transfer (UDT, XUDT and LUDT)
 *
 * @author sergey vetyutnev
 *
 */
public abstract class SccpSegmentableMessageImpl extends SccpAddressedMessageImpl {

    private static final Logger logger = Logger.getLogger(SccpSegmentableMessageImpl.class);

    protected byte[] data;
    protected ByteBuf dataBuf;
    protected SegmentationImpl segmentation;

    protected boolean isFullyReceived;
    protected int remainingSegments;
    protected ByteArrayOutputStream buffer;
    protected CompositeByteBuf reassemblyBuffer;

    protected SccpStackImpl.MessageReassemblyProcess mrp;

    protected SccpSegmentableMessageImpl(int maxDataLen, int type, int outgoingSls, int localSsn,
            SccpAddress calledParty, SccpAddress callingParty, byte[] data, HopCounter hopCounter) {
        super(maxDataLen,type, outgoingSls, localSsn, calledParty, callingParty, hopCounter);

        this.data = data;
        this.isFullyReceived = true;
    }

    protected SccpSegmentableMessageImpl(int maxDataLen, int type, int incomingOpc, int incomingDpc,
            int incomingSls, int networkId) {
        super(maxDataLen,type, incomingOpc, incomingDpc, incomingSls, networkId);
    }

    public Segmentation getSegmentation() {
        return segmentation;
    }

    public boolean getIsFullyReceived() {
        return this.isFullyReceived;
    }

    public int getRemainingSegments() {
        return remainingSegments;
    }

    public byte[] getData() {
        if (this.data == null && this.dataBuf != null && this.dataBuf.isReadable()) {
            this.data = new byte[this.dataBuf.readableBytes()];
            this.dataBuf.getBytes(this.dataBuf.readerIndex(), this.data);
        }
        return this.data;
    }

    public ByteBuf getDataBuf() {
        return this.dataBuf;
    }

    public void setData(byte[] data) {
        releaseDataBuf();
        this.data = data;
    }

    public void setDataBuf(ByteBuf dataBuf) {
        releaseDataBuf();
        this.data = null;
        this.dataBuf = dataBuf;
    }

    private void releaseDataBuf() {
        if (this.dataBuf != null) {
            this.dataBuf.release();
            this.dataBuf = null;
        }
    }

    public void setReceivedSingleSegment() {
        this.isFullyReceived = true;
    }

    public void setReceivedFirstSegment() {
        if (this.segmentation == null)
            // this can not occur
            return;

        this.remainingSegments = this.segmentation.getRemainingSegments();
        if (this.dataBuf != null) {
            this.reassemblyBuffer = PooledByteBufAllocator.DEFAULT.compositeBuffer(this.remainingSegments + 1);
            this.reassemblyBuffer.addComponent(true, this.dataBuf.retain());
            this.dataBuf = null;
        } else {
            this.buffer = new ByteArrayOutputStream(this.data.length * (this.remainingSegments + 1));
            try {
                this.buffer.write(this.data);
            } catch (IOException e) {
                // this can not occur
                e.printStackTrace();
            }
        }
    }

    public void setReceivedNextSegment(SccpSegmentableMessageImpl nextSegement) {
        if (this.reassemblyBuffer != null) {
            ByteBuf nextBuf = nextSegement.getDataBuf();
            if (nextBuf != null) {
                this.reassemblyBuffer.addComponent(true, nextBuf.retain());
            } else if (nextSegement.data != null) {
                this.reassemblyBuffer.addComponent(true, Unpooled.wrappedBuffer(nextSegement.data));
            } else {
                logger.error("setReceivedNextSegment: next segment has no dataBuf and no data");
            }

            if (--this.remainingSegments == 0) {
                this.dataBuf = this.reassemblyBuffer;
                this.reassemblyBuffer = null;
                this.isFullyReceived = true;
            }
            return;
        }

        try {
            this.buffer.write(nextSegement.data);
        } catch (IOException e) {
            // this can not occur
            e.printStackTrace();
        }

        if (--this.remainingSegments == 0) {
            this.data = this.buffer.toByteArray();
            this.isFullyReceived = true;
        }
    }

    public void cancelSegmentation() {
        this.remainingSegments = -1;
        this.isFullyReceived = false;
        if (this.reassemblyBuffer != null) {
            this.reassemblyBuffer.release();
            this.reassemblyBuffer = null;
        }
    }

    public SccpStackImpl.MessageReassemblyProcess getMessageReassemblyProcess() {
        return mrp;
    }

    public void setMessageReassemblyProcess(SccpStackImpl.MessageReassemblyProcess mrp) {
        this.mrp = mrp;
    }
}
