package org.restcomm.protocols.ss7.map.service.lsm;

import java.io.IOException;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.service.lsm.PeriodicLDRInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.ReportingOptionMilliseconds;
import org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive;

/**
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class PeriodicLDRInfoImpl implements PeriodicLDRInfo, MAPAsnPrimitive {

    public static final String _PrimitiveName = "PeriodicLDRInfo";
    private static final int _TAG_Reporting_Option_Milliseconds = 0;

    private int reportingAmount;
    private int reportingInterval;
    private ReportingOptionMilliseconds reportingOptionMilliseconds;

    public PeriodicLDRInfoImpl() {
    }

    public PeriodicLDRInfoImpl(int reportingAmount, int reportingInterval, ReportingOptionMilliseconds reportingOptionMilliseconds) {
        this.reportingAmount = reportingAmount;
        this.reportingInterval = reportingInterval;
        this.reportingOptionMilliseconds = reportingOptionMilliseconds;
    }

    public int getReportingAmount() {
        return reportingAmount;
    }

    public int getReportingInterval() {
        return reportingInterval;
    }

    @Override
    public ReportingOptionMilliseconds getReportingOptionMilliseconds() {
        return reportingOptionMilliseconds;
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
            throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
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
        this.reportingAmount = 0;
        this.reportingInterval = 0;
        this.reportingOptionMilliseconds = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            switch (num) {
                case 0:
                    // reportingAmount
                    if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive() || tag != Tag.INTEGER)
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".reportingAmount: Parameter bad tag or tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.reportingAmount = (int) ais.readInteger();
                    break;

                case 1:
                    // reportingInterval
                    if (ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive() || tag != Tag.INTEGER)
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".reportingInterval: Parameter bad tag or tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.reportingInterval = (int) ais.readInteger();
                    break;

                default:
                    if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                        switch (tag) {
                            case _TAG_Reporting_Option_Milliseconds:
                                // reportingOptionMilliseconds
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".reportingOptionMilliseconds: Parameter is primitive",
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                this.reportingOptionMilliseconds = new ReportingOptionMillisecondsImpl();
                                ((ReportingOptionMillisecondsImpl) reportingOptionMilliseconds).decodeAll(ais);
                                break;

                            default:
                                ais.advanceElement();
                                break;
                        }
                    } else {
                        ais.advanceElement();
                    }
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

        try {
            asnOutputStream.writeInteger(reportingAmount);
            asnOutputStream.writeInteger(reportingInterval);

            if (reportingOptionMilliseconds != null)
                ((ReportingOptionMillisecondsImpl) reportingOptionMilliseconds).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_Reporting_Option_Milliseconds);

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

        sb.append("reportingAmount=");
        sb.append(this.reportingAmount);

        sb.append(", reportingInterval=");
        sb.append(this.reportingInterval);

        if (this.reportingOptionMilliseconds != null) {
            sb.append(", reportingOptionMilliseconds=");
            sb.append(reportingOptionMilliseconds);
        }

        sb.append("]");

        return sb.toString();
    }
}
