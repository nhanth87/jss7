package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import java.io.IOException;
import java.util.ArrayList;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientInternalID;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtSSStatus;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExternalClient;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LCSPrivacyClass;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.NotificationToMSUser;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ServiceType;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSCode;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;
import org.restcomm.protocols.ss7.map.service.supplementary.SSCodeImpl;

/**
 *
 * @author Lasith Waruna Perera
 *
 */
public class LCSPrivacyClassImpl extends SequenceBase implements LCSPrivacyClass {

    private static final int _TAG_NOTIFICATION_TO_MS_USER = 0;
    private static final int _TAG_EXTERNAL_CLIENT_LIST = 1;
    private static final int _TAG_PLMN_CLIENT_LIST = 2;
    private static final int _TAG_EXTENSION_CONTAINER = 3;
    private static final int _TAG_EXT_EXTERNAL_CLIENT_LIST = 4;
    private static final int _TAG_SERVICE_TYPE_LIST = 5;

    private SSCode ssCode;
    private ExtSSStatus ssStatus;
    private NotificationToMSUser notificationToMSUser;
    private ArrayList<ExternalClient> externalClientList;
    private ArrayList<LCSClientInternalID> plmnClientList;
    private MAPExtensionContainer extensionContainer;
    private ArrayList<ExternalClient> extExternalClientList;
    private ArrayList<ServiceType> serviceTypeList;

    public LCSPrivacyClassImpl() {
        super("LCSPrivacyClass");
    }

    public LCSPrivacyClassImpl(SSCode ssCode, ExtSSStatus ssStatus, NotificationToMSUser notificationToMSUser,
            ArrayList<ExternalClient> externalClientList, ArrayList<LCSClientInternalID> plmnClientList,
            MAPExtensionContainer extensionContainer, ArrayList<ExternalClient> extExternalClientList,
            ArrayList<ServiceType> serviceTypeList) {
        super("LCSPrivacyClass");
        this.ssCode = ssCode;
        this.ssStatus = ssStatus;
        this.notificationToMSUser = notificationToMSUser;
        this.externalClientList = externalClientList;
        this.plmnClientList = plmnClientList;
        this.extensionContainer = extensionContainer;
        this.extExternalClientList = extExternalClientList;
        this.serviceTypeList = serviceTypeList;
    }

    @Override
    public SSCode getSsCode() {
        return this.ssCode;
    }

    @Override
    public ExtSSStatus getSsStatus() {
        return this.ssStatus;
    }

    @Override
    public NotificationToMSUser getNotificationToMSUser() {
        return this.notificationToMSUser;
    }

    @Override
    public ArrayList<ExternalClient> getExternalClientList() {
        return this.externalClientList;
    }

    @Override
    public ArrayList<LCSClientInternalID> getPLMNClientList() {
        return this.plmnClientList;
    }

    @Override
    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    @Override
    public ArrayList<ExternalClient> getExtExternalClientList() {
        return this.extExternalClientList;
    }

    @Override
    public ArrayList<ServiceType> getServiceTypeList() {
        return this.serviceTypeList;
    }

    @Override
    protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        this.ssCode = null;
        this.ssStatus = null;
        this.notificationToMSUser = null;
        this.externalClientList = null;
        this.plmnClientList = null;
        this.extensionContainer = null;
        this.extExternalClientList = null;
        this.serviceTypeList = null;

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

        int num = 0;
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            switch (num) {
                case 0:
                    if (tag != Tag.STRING_OCTET || ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".ssCode: bad tag, tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.ssCode = new SSCodeImpl();
                    ((SSCodeImpl) this.ssCode).decodeAll(ais);
                    break;
                case 1:
                    if (tag != Tag.STRING_OCTET || ais.getTagClass() != Tag.CLASS_UNIVERSAL || !ais.isTagPrimitive())
                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                + ".ssStatus: bad tag, tag class or not primitive",
                                MAPParsingComponentExceptionReason.MistypedParameter);
                    this.ssStatus = new ExtSSStatusImpl();
                    ((ExtSSStatusImpl) this.ssStatus).decodeAll(ais);
                    break;
                default:
                    if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                        switch (tag) {
                            case _TAG_NOTIFICATION_TO_MS_USER:
                                if (!ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".notificationToMSUser: Parameter is not primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                                this.notificationToMSUser = NotificationToMSUser.getInstance((int) ais.readInteger());
                                break;
                            case _TAG_EXTERNAL_CLIENT_LIST:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".externalClientList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                                this.externalClientList = new ArrayList<>();

                                AsnInputStream ais2 = ais.readSequenceStream();

                                while (true) {
                                    if (ais2.available() == 0)
                                        break;

                                    int tag2 = ais2.readTag();

                                    if (tag2 != Tag.SEQUENCE || ais2.getTagClass() != Tag.CLASS_UNIVERSAL
                                        || ais2.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad tag or tagClass or is primitive when decoding externalClientList",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                    ExternalClientImpl elem = new ExternalClientImpl();
                                    elem.decodeAll(ais2);
                                    this.externalClientList.add(elem);

                                    if (this.externalClientList.size() > 5)
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".externalClientList: elements count must be from 1 to 5, found: "
                                            + this.externalClientList.size(),
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                }
                                break;
                            case _TAG_PLMN_CLIENT_LIST:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".plmnClientList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                                this.plmnClientList = new ArrayList<>();

                                AsnInputStream ais3 = ais.readSequenceStream();

                                while (true) {
                                    if (ais3.available() == 0)
                                        break;

                                    int tag2 = ais3.readTag();

                                    if (tag2 != Tag.ENUMERATED || ais3.getTagClass() != Tag.CLASS_UNIVERSAL
                                        || !ais3.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad tag or tagClass or is not primitive when decoding plmnClientList",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                    int lcsId = (int) ais3.readInteger();
                                    LCSClientInternalID elem = LCSClientInternalID.getLCSClientInternalID(lcsId);

                                    this.plmnClientList.add(elem);

                                    if (this.plmnClientList.size() > 5)
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".plmnClientList: elements count must be from 1 to 5, found: "
                                            + this.plmnClientList.size(),
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                }
                                break;
                            case _TAG_EXTENSION_CONTAINER:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".extensionContainer: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);
                                this.extensionContainer = new MAPExtensionContainerImpl();
                                ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                                break;
                            case _TAG_EXT_EXTERNAL_CLIENT_LIST:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".extExternalClientList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                                this.extExternalClientList = new ArrayList<>();

                                AsnInputStream ais4 = ais.readSequenceStream();

                                while (true) {
                                    if (ais4.available() == 0)
                                        break;

                                    int tag2 = ais4.readTag();

                                    if (tag2 != Tag.SEQUENCE || ais4.getTagClass() != Tag.CLASS_UNIVERSAL
                                        || ais4.isTagPrimitive())
                                        throw new MAPParsingComponentException(
                                            "Error while decoding "
                                                + _PrimitiveName
                                                + ": bad tag or tagClass or is primitive when decoding extExternalClientList",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                    ExternalClientImpl elem = new ExternalClientImpl();
                                    elem.decodeAll(ais4);

                                    this.extExternalClientList.add(elem);

                                    if (this.extExternalClientList.size() > 35)
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".extExternalClientList: elements count must be from 1 to 35, found: "
                                            + this.extExternalClientList.size(),
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                }
                                break;
                            case _TAG_SERVICE_TYPE_LIST:
                                if (ais.isTagPrimitive())
                                    throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                        + ".plmnClientList: Parameter is primitive",
                                        MAPParsingComponentExceptionReason.MistypedParameter);

                                this.serviceTypeList = new ArrayList<>();

                                AsnInputStream ais5 = ais.readSequenceStream();

                                while (true) {
                                    if (ais5.available() == 0)
                                        break;

                                    int tag2 = ais5.readTag();

                                    if (tag2 != Tag.SEQUENCE || ais5.getTagClass() != Tag.CLASS_UNIVERSAL
                                        || ais5.isTagPrimitive())
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ": bad tag or tagClass or is primitive when decoding serviceTypeList",
                                            MAPParsingComponentExceptionReason.MistypedParameter);

                                    ServiceTypeImpl elem = new ServiceTypeImpl();
                                    elem.decodeAll(ais5);

                                    this.serviceTypeList.add(elem);

                                    if (this.serviceTypeList.size() > 32)
                                        throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                            + ".serviceTypeList: elements count must be from 1 to 32, found: "
                                            + this.serviceTypeList.size(),
                                            MAPParsingComponentExceptionReason.MistypedParameter);
                                }
                                break;
                            default:
                                ais.advanceElement();
                                break;
                        }
                    } else {
                        ais.advanceElement();
                    }
            }

            num++;
        }

        if (this.ssCode == null) {
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": Parameter ssCode is mandatory but not found", MAPParsingComponentExceptionReason.MistypedParameter);
        }
        if (this.ssStatus == null) {
            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": Parameter ssStatus is mandatory but not found",
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }

    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {

        if (this.ssCode == null)
            throw new MAPException("Error while encoding" + _PrimitiveName + ": ssCode must not be null");

        if (this.ssStatus == null)
            throw new MAPException("Error while encoding" + _PrimitiveName + ": ssStatus must not be null");

        if (this.externalClientList != null && this.externalClientList.size() > 5) {
            throw new MAPException("Error while encoding " + _PrimitiveName
                    + ": Parameter externalClientList size must be from 1 to 5, found: " + this.externalClientList.size());
        }

        if (this.plmnClientList != null && (this.plmnClientList.size() < 1 || this.plmnClientList.size() > 5)) {
            throw new MAPException("Error while encoding " + _PrimitiveName
                    + ": Parameter plmnClientList size must be from 1 to 5, found: " + this.plmnClientList.size());
        }

        if (this.extExternalClientList != null
                && (this.extExternalClientList.size() < 1 || this.extExternalClientList.size() > 35)) {
            throw new MAPException("Error while encoding " + _PrimitiveName
                    + ": Parameter extExternalClientList size must be from 1 to 35, found: "
                    + this.extExternalClientList.size());
        }

        if (this.serviceTypeList != null && (this.serviceTypeList.size() < 1 || this.serviceTypeList.size() > 32)) {
            throw new MAPException("Error while encoding " + _PrimitiveName
                    + ": Parameter serviceTypeList size must be from 1 to 32, found: " + this.serviceTypeList.size());
        }

        try {

            ((SSCodeImpl) this.ssCode).encodeAll(asnOutputStream);

            ((ExtSSStatusImpl) this.ssStatus).encodeAll(asnOutputStream);

            if (this.notificationToMSUser != null)
                asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_NOTIFICATION_TO_MS_USER, this.notificationToMSUser.getCode());

            if (externalClientList != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_EXTERNAL_CLIENT_LIST);
                int pos = asnOutputStream.StartContentDefiniteLength();
                for (ExternalClient be : this.externalClientList) {
                    ExternalClientImpl bee = (ExternalClientImpl) be;
                    bee.encodeAll(asnOutputStream);
                }
                asnOutputStream.FinalizeContent(pos);
            }

            if (plmnClientList != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_PLMN_CLIENT_LIST);
                int pos = asnOutputStream.StartContentDefiniteLength();
                for (LCSClientInternalID be : this.plmnClientList) {
                    asnOutputStream.writeInteger(Tag.CLASS_UNIVERSAL, Tag.ENUMERATED, be.getId());
                }
                asnOutputStream.FinalizeContent(pos);
            }

            if (this.extensionContainer != null)
                ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                    _TAG_EXTENSION_CONTAINER);

            if (extExternalClientList != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_EXT_EXTERNAL_CLIENT_LIST);
                int pos = asnOutputStream.StartContentDefiniteLength();
                for (ExternalClient be : this.extExternalClientList) {
                    ExternalClientImpl bee = (ExternalClientImpl) be;
                    bee.encodeAll(asnOutputStream);
                }
                asnOutputStream.FinalizeContent(pos);
            }

            if (serviceTypeList != null) {
                asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_SERVICE_TYPE_LIST);
                int pos = asnOutputStream.StartContentDefiniteLength();
                for (ServiceType be : this.serviceTypeList) {
                    ServiceTypeImpl bee = (ServiceTypeImpl) be;
                    bee.encodeAll(asnOutputStream);
                }
                asnOutputStream.FinalizeContent(pos);
            }
        } catch (IOException e) {
            throw new MAPException("IOException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName).append(" [");

        if (this.ssCode != null) {
            sb.append("ssCode=");
            sb.append(this.ssCode);
            sb.append(", ");
        }

        if (this.ssStatus != null) {
            sb.append("ssStatus=");
            sb.append(this.ssStatus);
            sb.append(", ");
        }

        if (this.notificationToMSUser != null) {
            sb.append("notificationToMSUser=");
            sb.append(this.notificationToMSUser);
            sb.append(", ");
        }

        if (this.externalClientList != null) {
            sb.append("externalClientList=[");
            boolean firstItem = true;
            for (ExternalClient be : this.externalClientList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be.toString());
            }
            sb.append("], ");
        }

        if (this.plmnClientList != null) {
            sb.append("plmnClientList=[");
            boolean firstItem = true;
            for (LCSClientInternalID be : this.plmnClientList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be.toString());
            }
            sb.append("], ");
        }

        if (this.extensionContainer != null) {
            sb.append("extensionContainer=");
            sb.append(this.extensionContainer);
            sb.append(", ");
        }

        if (this.extExternalClientList != null) {
            sb.append("extExternalClientList=[");
            boolean firstItem = true;
            for (ExternalClient be : this.extExternalClientList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be.toString());
            }
            sb.append("], ");
        }

        if (this.serviceTypeList != null) {
            sb.append("serviceTypeList=[");
            boolean firstItem = true;
            for (ServiceType be : this.serviceTypeList) {
                if (firstItem)
                    firstItem = false;
                else
                    sb.append(", ");
                sb.append(be.toString());
            }
            sb.append("] ");
        }

        sb.append("]");

        return sb.toString();
    }
}
