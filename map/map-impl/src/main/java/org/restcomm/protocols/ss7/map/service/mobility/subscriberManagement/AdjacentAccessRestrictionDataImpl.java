package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.AccessRestrictionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.AdjacentAccessRestrictionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtAccessRestrictionData;
import org.restcomm.protocols.ss7.map.primitives.PlmnIdImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class AdjacentAccessRestrictionDataImpl extends SequenceBase implements AdjacentAccessRestrictionData {

    public static final String _PrimitiveName = "AdjacentAccessRestrictionData";
    public static final int _TAG_PLMN_ID = 0;
    public static final int _TAG_Access_Restriction_Data = 1;
    public static final int _TAG_Ext_Access_Restriction_Data = 2;

    private PlmnId plmnId;
    private AccessRestrictionData accessRestrictionData;
    private ExtAccessRestrictionData extAccessRestrictionData;

    public AdjacentAccessRestrictionDataImpl() {
        super(_PrimitiveName);
    }

    public AdjacentAccessRestrictionDataImpl(PlmnId plmnId, AccessRestrictionData accessRestrictionData,
            ExtAccessRestrictionData extAccessRestrictionData) {
        super(_PrimitiveName);
        this.plmnId = plmnId;
        this.accessRestrictionData = accessRestrictionData;
        this.extAccessRestrictionData = extAccessRestrictionData;
    }

    @Override
    public PlmnId getPLMNId() {
        return this.plmnId;
    }

    @Override
    public AccessRestrictionData getAccessRestrictionData() {
        return this.accessRestrictionData;
    }

    @Override
    public ExtAccessRestrictionData getExtAccessRestrictionData() {
        return this.extAccessRestrictionData;
    }

    protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.plmnId = null;
        this.accessRestrictionData = null;
        this.extAccessRestrictionData = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();
            switch (ais.getTagClass()) {
                case Tag.CLASS_CONTEXT_SPECIFIC: {
                    switch (tag) {
                        case _TAG_PLMN_ID:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".plmnId: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.plmnId = new PlmnIdImpl();
                            ((PlmnIdImpl) this.plmnId).decodeAll(ais);
                            break;
                        case _TAG_Access_Restriction_Data:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".accessRestrictionData: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.accessRestrictionData = new AccessRestrictionDataImpl();
                            ((AccessRestrictionDataImpl) this.accessRestrictionData).decodeAll(ais);
                            break;
                        case _TAG_Ext_Access_Restriction_Data:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".extAccessRestrictionData: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.extAccessRestrictionData = new ExtAccessRestrictionDataImpl();
                            ((ExtAccessRestrictionDataImpl) this.extAccessRestrictionData).decodeAll(ais);
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
        if (this.plmnId == null) {
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": Parameter plmnId is mandatory but is not found", MAPParsingComponentExceptionReason.MistypedParameter);
        }
        if (this.accessRestrictionData == null) {
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": Parameter accessRestrictionData is mandatory but is not found", MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        if (this.plmnId == null) {
            throw new MAPException("Error while encoding " + _PrimitiveName + " the mandatory parameter plmnId is not defined");
        }

        if (this.accessRestrictionData == null) {
            throw new MAPException("Error while encoding " + _PrimitiveName + " the mandatory parameter accessRestrictionData is not defined");
        }

        ((PlmnIdImpl) this.plmnId).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_PLMN_ID);

        ((AccessRestrictionDataImpl) this.accessRestrictionData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                _TAG_Access_Restriction_Data);

        ((ExtAccessRestrictionDataImpl) this.extAccessRestrictionData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                _TAG_Ext_Access_Restriction_Data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName + " [");

        if (this.plmnId != null) {
            sb.append("plmnId=");
            sb.append(this.plmnId);
            sb.append(", ");
        }
        if (this.accessRestrictionData != null) {
            sb.append("accessRestrictionData=");
            sb.append(this.accessRestrictionData);
            sb.append(", ");
        }
        if (this.extAccessRestrictionData != null) {
            sb.append("extAccessRestrictionData=");
            sb.append(this.extAccessRestrictionData);
            sb.append(" ");
        }

        sb.append("]");
        return sb.toString();
    }
}
