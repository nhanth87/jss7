package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.EDRXCycleLength;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.EDRXCycleLengthValue;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class EDRXCycleLengthImpl extends SequenceBase implements EDRXCycleLength {

    public static final String _PrimitiveName = "EDRX-Cycle-Length";
    public static final int _TAG_Used_RAT_Type = 0;
    public static final int _TAG_EDRX_Cycle_Length_Value = 1;

    private UsedRATType usedRATType = null;
    private EDRXCycleLengthValue eDRXCycleLengthValue = null;

    public EDRXCycleLengthImpl() {
        super(_PrimitiveName);
    }

    public EDRXCycleLengthImpl(UsedRATType usedRATType, EDRXCycleLengthValue edrxCycleLengthValue) {
        super(_PrimitiveName);
        this.usedRATType = usedRATType;
        this.eDRXCycleLengthValue = edrxCycleLengthValue;
    }

    @Override
    public UsedRATType getUsedRATType() {
        return this.usedRATType;
    }

    public EDRXCycleLengthValue getEDRXCycleLengthValue() {
        return this.eDRXCycleLengthValue;
    }

    protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.usedRATType = null;
        this.eDRXCycleLengthValue = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();
            switch (ais.getTagClass()) {
                case Tag.CLASS_CONTEXT_SPECIFIC: {
                    switch (tag) {
                        case _TAG_Used_RAT_Type:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".usedRATType: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            int raType = (int) ais.readInteger();
                            this.usedRATType = UsedRATType.getInstance(raType);
                            break;
                        case _TAG_EDRX_Cycle_Length_Value:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".eDRXCycleLengthValue: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.eDRXCycleLengthValue = new EDRXCycleLengthValueImpl();
                            ((EDRXCycleLengthValueImpl) this.eDRXCycleLengthValue).decodeAll(ais);
                            break;
                        default:
                            ais.advanceElement();
                            break;
                    }
                }
                break;
                default:
                    ais.advanceElement();
                    break;
            }
        }
        if (this.usedRATType == null) {
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": Parameter usedRATType is mandatory but is not found", MAPParsingComponentExceptionReason.MistypedParameter);
        }
        if (this.eDRXCycleLengthValue == null) {
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": Parameter eDRXCycleLengthValue is mandatory but is not found", MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        try {
            if (this.usedRATType == null) {
                throw new MAPException("Error while encoding " + _PrimitiveName + " the mandatory parameter usedRATType is not defined");
            }

            if (this.eDRXCycleLengthValue == null) {
                throw new MAPException("Error while encoding " + _PrimitiveName + " the mandatory parameter eDRXCycleLengthValue is not defined");
            }

            asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_Used_RAT_Type, this.usedRATType.getCode());

            ((EDRXCycleLengthValueImpl) this.eDRXCycleLengthValue).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_EDRX_Cycle_Length_Value);

        } catch (IOException e) {
            throw new MAPException("IOException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName + " [");

        if (this.usedRATType != null) {
            sb.append("usedRATType=");
            sb.append(this.usedRATType);
            sb.append(", ");
        }
        if (this.eDRXCycleLengthValue != null) {
            sb.append("edrxCycleLengthValue=");
            sb.append(this.eDRXCycleLengthValue);
            sb.append(", ");
        }

        sb.append("]");
        return sb.toString();
    }
}
