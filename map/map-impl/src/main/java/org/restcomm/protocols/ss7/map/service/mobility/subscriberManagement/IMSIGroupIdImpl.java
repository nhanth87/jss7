package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.IMSIGroupId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LocalGroupId;
import org.restcomm.protocols.ss7.map.primitives.PlmnIdImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class IMSIGroupIdImpl extends SequenceBase implements IMSIGroupId {

    public static final String _PrimitiveName = "IMSI-GroupId";

    public static final int _TAG_Group_Service_ID = 0;
    public static final int _TAG_PLMN_ID = 1;
    public static final int _TAG_Local_GroupID = 2;

    private Long groupServiceId = null;
    private PlmnId plmnId = null;
    private LocalGroupId localGroupId = null;

    public IMSIGroupIdImpl() {
        super(_PrimitiveName);
    }

    public IMSIGroupIdImpl(Long groupServiceId, PlmnId plmnId, LocalGroupId localGroupId) {
        super(_PrimitiveName);
        this.groupServiceId = groupServiceId;
        this.plmnId = plmnId;
        this.localGroupId = localGroupId;
    }

    @Override
    public Long getGroupServiceId() {
        return this.groupServiceId;
    }

    @Override
    public PlmnId getPLMNId() {
        return this.plmnId;
    }

    @Override
    public LocalGroupId getLocalGroupId() {
        return this.localGroupId;
    }

    protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.groupServiceId = null;
        this.plmnId = null;
        this.localGroupId = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();
            switch (ais.getTagClass()) {
                case Tag.CLASS_CONTEXT_SPECIFIC: {
                    switch (tag) {
                        case _TAG_Group_Service_ID:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".groupServiceId: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            this.groupServiceId = ais.readInteger();
                            break;
                        case _TAG_PLMN_ID:
                            if (!ais.isTagPrimitive())
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".plmnId: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            this.plmnId = new PlmnIdImpl();
                            ((PlmnIdImpl) this.plmnId).decodeAll(ais);
                            break;
                        case _TAG_Local_GroupID:
                            if (!ais.isTagPrimitive()) {
                                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".localGroupId: is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                            }
                            this.localGroupId = new LocalGroupIdImpl();
                            ((LocalGroupIdImpl) this.localGroupId).decodeAll(ais);
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
        if (this.groupServiceId == null) {
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": Parameter groupServiceId is mandatory but is not found", MAPParsingComponentExceptionReason.MistypedParameter);
        }
        if (this.plmnId == null) {
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": Parameter plmnId is mandatory but is not found", MAPParsingComponentExceptionReason.MistypedParameter);
        }
        if (this.localGroupId == null) {
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": Parameter localGroupId is mandatory but is not found", MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        if (this.groupServiceId == null) {
            throw new MAPException("Error while encoding " + _PrimitiveName + " the mandatory parameter groupServiceId is not defined");
        }
        if (this.plmnId == null) {
            throw new MAPException("Error while encoding " + _PrimitiveName + " the mandatory parameter plmnId is not defined");
        }
        if (this.localGroupId == null) {
            throw new MAPException("Error while encoding " + _PrimitiveName + " the mandatory parameter localGroupId is not defined");
        }

        try {
            asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_Group_Service_ID,
                    this.groupServiceId);
        } catch (IOException e) {
            throw new MAPException("IOException while encoding " + _PrimitiveName
                    + " parameter groupServiceId", e);
        } catch (AsnException e) {
            throw new MAPException("AsnException while encoding " + _PrimitiveName
                    + " parameter groupServiceId", e);
        }

        ((PlmnIdImpl) this.plmnId).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_PLMN_ID);

        ((LocalGroupIdImpl) this.localGroupId).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_Local_GroupID);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName + " [");

        if (this.groupServiceId != null) {
            sb.append("groupServiceId=");
            sb.append(this.groupServiceId);
            sb.append(", ");
        }
        if (this.plmnId != null) {
            sb.append("plmnId=");
            sb.append(this.plmnId);
            sb.append(", ");
        }
        if (this.localGroupId != null) {
            sb.append("localGroupId=");
            sb.append(this.localGroupId);
            sb.append(" ");
        }

        sb.append("]");
        return sb.toString();
    }
}
