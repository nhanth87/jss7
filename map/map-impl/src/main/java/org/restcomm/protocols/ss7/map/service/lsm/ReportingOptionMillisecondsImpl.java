package org.restcomm.protocols.ss7.map.service.lsm;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.service.lsm.ReportingOptionMilliseconds;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ReportingOptionMillisecondsImpl implements ReportingOptionMilliseconds {

    public static final String _PrimitiveName = "ReportingOptionMilliseconds";

    private int reportingAmountMilliseconds;
    private int reportingIntervalMilliseconds;

    public ReportingOptionMillisecondsImpl() {
    }

    public ReportingOptionMillisecondsImpl(int reportingAmountMilliseconds, int reportingIntervalMilliseconds) {
        this.reportingAmountMilliseconds = reportingAmountMilliseconds;
        this.reportingIntervalMilliseconds = reportingIntervalMilliseconds;
    }

    @Override
    public int getReportingAmountMilliseconds() {
        return reportingAmountMilliseconds;
    }

    @Override
    public int getReportingIntervalMilliseconds() {
        return reportingIntervalMilliseconds;
    }

    public int getTag() throws MAPException {
        return Tag.SEQUENCE;
    }

    public int getTagClass() {
        return Tag.CLASS_UNIVERSAL;
    }

    public boolean getIsPrimitive() {
        return false;
    }

    public void decodeAll(AsnInputStream asnInputStream) throws MAPParsingComponentException {
        try {
            int length = asnInputStream.readLength();
            this._decode(asnInputStream, length);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": ", e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": ", e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    public void decodeData(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException {
        try {
            this._decode(asnInputStream, length);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.reportingAmountMilliseconds = 0;
        this.reportingIntervalMilliseconds = 0;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            switch (num) {
                case 0:
                    // reportingAmountMilliseconds
                    if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive() || tag != Tag.INTEGER)
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".reportingAmountMilliseconds: Parameter bad tag or tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.reportingAmountMilliseconds = (int) ais.readInteger();
                    break;

                case 1:
                    // reportingIntervalMilliseconds
                    if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive() || tag != Tag.INTEGER)
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".reportingIntervalMilliseconds: Parameter bad tag or tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.reportingIntervalMilliseconds = (int) ais.readInteger();
                    break;

                default:
                    ais.advanceElement();
                    break;
            }

            num++;
        }

        if (num < 2)
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": Needs at least 2 mandatory parameters, found " + num,
                    MAPParsingComponentExceptionReason.MistypedParameter);
    }

    public void encodeAll(AsnOutputStream asnOutputStream) throws MAPException {

        this.encodeAll(asnOutputStream, this.getTagClass(), this.getTag());
    }

    public void encodeAll(AsnOutputStream asnOutputStream, int tagClass, int tag) throws MAPException {

        try {
            asnOutputStream.writeTag(tagClass, this.getIsPrimitive(), tag);
            int pos = asnOutputStream.StartContentDefiniteLength();
            this.encodeData(asnOutputStream);
            asnOutputStream.FinalizeContent(pos);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {

        if (((long) reportingAmountMilliseconds * reportingIntervalMilliseconds) > 8639999000L)
            throw new MAPException("Error while encoding " + _PrimitiveName + ", " +
                    "reportingAmountMilliseconds x reportingIntervalMilliseconds shall not exceed 8639999000 ms" +
                    "(99 days, 23 hours, 59 minutes and 59 seconds) for compatibility with OMA MLP and RLP.");

        try {
            asnOutputStream.writeInteger(reportingAmountMilliseconds);
            asnOutputStream.writeInteger(reportingIntervalMilliseconds);

        } catch (IOException e) {
            throw new MAPException("IOException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        sb.append("reportingAmountMilliseconds=");
        sb.append(this.reportingAmountMilliseconds);

        sb.append(", reportingIntervalMilliseconds=");
        sb.append(this.reportingIntervalMilliseconds);

        sb.append("]");

        return sb.toString();
    }
}
