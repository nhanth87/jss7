package org.restcomm.protocols.ss7.map;

import java.io.IOException;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnIndexPool;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnMessageIndex;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.AsnReaderHelper;
import org.mobicents.protocols.asn.FlatAsnParser;
import org.mobicents.protocols.asn.Jss7AsnConfig;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.datacoding.CBSDataCodingScheme;
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.sms.IpSmGwGuidance;
import org.restcomm.protocols.ss7.map.api.service.sms.LocationInfoWithLMSI;
import org.restcomm.protocols.ss7.map.api.service.sms.MWStatus;
import org.restcomm.protocols.ss7.map.api.service.sms.SMDeliveryOutcome;
import org.restcomm.protocols.ss7.map.api.service.lsm.AdditionalNumber;
import org.restcomm.protocols.ss7.map.datacoding.CBSDataCodingSchemeImpl;
import org.restcomm.protocols.ss7.map.primitives.AddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.AlertingPatternImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.LMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.USSDStringImpl;
import org.restcomm.protocols.ss7.map.service.lsm.AdditionalNumberImpl;
import org.restcomm.protocols.ss7.map.service.sms.IpSmGwGuidanceImpl;
import org.restcomm.protocols.ss7.map.service.sms.LocationInfoWithLMSIImpl;
import org.restcomm.protocols.ss7.map.service.sms.MWStatusImpl;
import org.restcomm.protocols.ss7.map.service.sms.SM_RP_DAImpl;
import org.restcomm.protocols.ss7.map.service.sms.SM_RP_OAImpl;
import org.restcomm.protocols.ss7.map.service.sms.SmsSignalInfoImpl;

/**
 * Shared zero-GC flat ASN.1 decode helpers for MAP messages.
 * <p>
 * Flag: {@code -Djss7.asn.flatIndexEnabled=true}
 */
public final class MapFlatAsnDecoder {

    /** Context-specific primitive tag [0] for MSISDN (full BER tag byte). */
    public static final int TAG_MSISDN_CTX = 0x80;

    private static final int TAG_CTX_0 = 0x80;
    private static final int TAG_CTX_1 = 0x81;
    private static final int TAG_CTX_2 = 0x82;
    private static final int TAG_CTX_3 = 0x83;
    private static final int TAG_CTX_4 = 0x84;
    private static final int TAG_CTX_5 = 0x85;

    public static final class UssdFields {
        public CBSDataCodingScheme dataCodingScheme;
        public USSDStringImpl ussdString;
        public ISDNAddressStringImpl msisdn;
        public AlertingPatternImpl alertingPattern;
    }

    public static final class ForwardSmFields {
        public SM_RP_DAImpl smRpDa;
        public SM_RP_OAImpl smRpOa;
        public SmsSignalInfoImpl smRpUi;
        public boolean moreMessagesToSend;
        public MAPExtensionContainer extensionContainer;
        public IMSI imsi;
    }

    public static final class MapOpenFields {
        public AddressString destReference;
        public AddressString origReference;
        public boolean ericssonStyle;
        public AddressString ericssonMsisdn;
        public AddressString ericssonVlrNo;
        public MAPExtensionContainer extensionContainer;
    }

    public static final class SriFields {
        public ISDNAddressString msisdn;
        public boolean smRpPri;
        public AddressString serviceCentreAddress;
        public MAPExtensionContainer extensionContainer;
        public boolean gprsSupportIndicator;
    }

    public static final class RsdsFields {
        public ISDNAddressString msisdn;
        public AddressString serviceCentreAddress;
        public SMDeliveryOutcome smDeliveryOutcome;
        public Integer absentSubscriberDiagnosticSm;
        public MAPExtensionContainer extensionContainer;
        public boolean gprsSupportIndicator;
        public boolean deliveryOutcomeIndicator;
    }

    public static final class InformFields {
        public ISDNAddressString storedMSISDN;
        public MWStatus mwStatus;
        public MAPExtensionContainer extensionContainer;
        public Integer absentSubscriberDiagnosticSM;
        public Integer additionalAbsentSubscriberDiagnosticSM;
    }

    public static final class AlertFields {
        public ISDNAddressString msisdn;
        public AddressString serviceCentreAddress;
    }

    public static final class SriResponseFields {
        public IMSI imsi;
        public LocationInfoWithLMSI locationInfoWithLMSI;
        public MAPExtensionContainer extensionContainer;
        public Boolean mwdSet;
        public IpSmGwGuidance ipSmGwGuidance;
    }

    private MapFlatAsnDecoder() {
    }

    public static boolean isFlatIndexEnabled() {
        return Jss7AsnConfig.isFlatIndexEnabled();
    }

    // --- USSD supplementary ---

    public static UssdFields decodeUssdSequence(byte[] buffer, int offset, int length, boolean withOptionalFields,
            String messageName) throws MAPParsingComponentException {
        AsnMessageIndex index = AsnIndexPool.get();
        FlatAsnParser.parseAll(buffer, offset, length, index);

        int dcsIdx = AsnReaderHelper.findNthChildTag(index, -1, Tag.STRING_OCTET, 0);
        if (dcsIdx == -1 || index.valueLengths[dcsIdx] != 1) {
            throw mistyped(messageName, "Parameter ussd-DataCodingScheme missing or bad length");
        }

        int ussdIdx = AsnReaderHelper.findNthChildTag(index, -1, Tag.STRING_OCTET, 1);
        if (ussdIdx == -1) {
            throw mistyped(messageName, "Parameter ussd-String missing");
        }

        UssdFields fields = new UssdFields();
        fields.dataCodingScheme = new CBSDataCodingSchemeImpl(index.rawBuffer[index.valueOffsets[dcsIdx]]);
        fields.ussdString = new USSDStringImpl(fields.dataCodingScheme);
        AsnReaderHelper.OctetStringView ussdView = AsnReaderHelper.readOctetStringView(index, ussdIdx);
        fields.ussdString.setDataView(ussdView.buffer, ussdView.offset, ussdView.length);

        if (withOptionalFields) {
            decodeOptionalUssdFields(index, dcsIdx, ussdIdx, fields, messageName);
        }

        return fields;
    }

    public static UssdFields decodeUssdSequence(AsnInputStream asnInputStream, int length, boolean withOptionalFields,
            String messageName) throws MAPParsingComponentException {
        int offset = asnInputStream.getStartOffset() + asnInputStream.position();
        return decodeUssdSequence(asnInputStream.getBuffer(), offset, length, withOptionalFields, messageName);
    }

    // --- MAP-OPEN ---

    public static MapOpenFields decodeMapOpenInfo(byte[] buffer, int offset, int length, String messageName)
            throws MAPParsingComponentException {
        AsnMessageIndex index = AsnIndexPool.get();
        FlatAsnParser.parseAll(buffer, offset, length, index);

        MapOpenFields fields = new MapOpenFields();
        fields.ericssonStyle = hasChildTag(index, -1, TAG_CTX_2);

        for (int i = 0; i < index.tagCount; i++) {
            if (index.parents[i] != -1) {
                continue;
            }
            int tagByte = index.tags[i];
            int tagClass = AsnReaderHelper.getTagClass(tagByte);
            int tagNum = AsnReaderHelper.getTagNumber(tagByte);

            if (tagClass == Tag.CLASS_CONTEXT_SPECIFIC && AsnReaderHelper.isPrimitive(tagByte)) {
                AddressStringImpl addr = new AddressStringImpl();
                addr.decodeFromOctetView(index.rawBuffer, index.valueOffsets[i], index.valueLengths[i]);
                switch (tagNum) {
                    case 0:
                        fields.destReference = addr;
                        break;
                    case 1:
                        fields.origReference = addr;
                        break;
                    case 2:
                        fields.ericssonMsisdn = addr;
                        break;
                    case 3:
                        fields.ericssonVlrNo = addr;
                        break;
                    default:
                        break;
                }
            } else if (tagClass == Tag.CLASS_UNIVERSAL && tagNum == Tag.SEQUENCE
                    && AsnReaderHelper.isConstructed(tagByte)) {
                fields.extensionContainer = decodeExtensionContainer(index, i, messageName);
            }
        }

        return fields;
    }

    public static MapOpenFields decodeMapOpenInfo(AsnInputStream asnInputStream, int length, String messageName)
            throws MAPParsingComponentException {
        int offset = asnInputStream.getStartOffset() + asnInputStream.position();
        return decodeMapOpenInfo(asnInputStream.getBuffer(), offset, length, messageName);
    }

    // --- ForwardSM family (ForwardSM / MoForwardSM / MtForwardSM) ---

    public static ForwardSmFields decodeForwardSmSequence(byte[] buffer, int offset, int length, String messageName,
            boolean allowExtensionContainer, boolean allowImsi) throws MAPParsingComponentException {
        AsnMessageIndex index = AsnIndexPool.get();
        FlatAsnParser.parseAll(buffer, offset, length, index);

        int daIdx = AsnReaderHelper.findNthChild(index, -1, 0);
        int oaIdx = AsnReaderHelper.findNthChild(index, -1, 1);
        int uiIdx = AsnReaderHelper.findNthChild(index, -1, 2);

        if (daIdx == -1 || oaIdx == -1 || uiIdx == -1) {
            throw mistyped(messageName, "Needs at least 3 mandatory parameters (SM_RP_DA, SM_RP_OA, sm-RP-UI)");
        }

        ForwardSmFields fields = new ForwardSmFields();
        fields.smRpDa = decodeSmRpDa(index, daIdx, messageName);
        fields.smRpOa = decodeSmRpOa(index, oaIdx, messageName);
        fields.smRpUi = decodeSmsSignalInfo(index, uiIdx, messageName);

        for (int i = 0; i < index.tagCount; i++) {
            if (index.parents[i] != -1 || i == daIdx || i == oaIdx || i == uiIdx) {
                continue;
            }
            int tagByte = index.tags[i];
            int tagClass = AsnReaderHelper.getTagClass(tagByte);
            int tagNum = AsnReaderHelper.getTagNumber(tagByte);

            if (tagClass == Tag.CLASS_UNIVERSAL && tagNum == Tag.NULL && AsnReaderHelper.isPrimitive(tagByte)) {
                fields.moreMessagesToSend = true;
            } else if (allowExtensionContainer && tagClass == Tag.CLASS_UNIVERSAL && tagNum == Tag.SEQUENCE
                    && AsnReaderHelper.isConstructed(tagByte)) {
                fields.extensionContainer = decodeExtensionContainer(index, i, messageName);
            } else if (allowImsi && tagClass == Tag.CLASS_UNIVERSAL && tagNum == Tag.STRING_OCTET
                    && AsnReaderHelper.isPrimitive(tagByte)) {
                IMSIImpl imsi = new IMSIImpl();
                imsi.decodeFromOctetView(index.rawBuffer, index.valueOffsets[i], index.valueLengths[i]);
                fields.imsi = imsi;
            }
        }

        return fields;
    }

    public static ForwardSmFields decodeForwardSmSequence(AsnInputStream asnInputStream, int length, String messageName,
            boolean allowExtensionContainer, boolean allowImsi) throws MAPParsingComponentException {
        int offset = asnInputStream.getStartOffset() + asnInputStream.position();
        return decodeForwardSmSequence(asnInputStream.getBuffer(), offset, length, messageName, allowExtensionContainer,
                allowImsi);
    }

    // --- SendRoutingInfoForSM (SMSC hot path: mandatory msisdn + sm-RP-PRI + SCA) ---

    public static SriFields decodeSendRoutingInfoForSm(byte[] buffer, int offset, int length, String messageName)
            throws MAPParsingComponentException {
        AsnMessageIndex index = AsnIndexPool.get();
        FlatAsnParser.parseAll(buffer, offset, length, index);

        int msisdnIdx = AsnReaderHelper.findNthChild(index, -1, 0);
        int priIdx = AsnReaderHelper.findNthChild(index, -1, 1);
        int scaIdx = AsnReaderHelper.findNthChild(index, -1, 2);

        if (msisdnIdx == -1 || priIdx == -1 || scaIdx == -1) {
            throw mistyped(messageName, "Needs at least msisdn, sm-RP-PRI and serviceCentreAddress");
        }

        SriFields fields = new SriFields();
        fields.msisdn = decodeIsdnAddress(index, msisdnIdx, messageName);
        fields.smRpPri = readBoolean(index, priIdx);
        fields.serviceCentreAddress = decodeIsdnAddress(index, scaIdx, messageName);

        for (int i = 0; i < index.tagCount; i++) {
            if (index.parents[i] != -1 || i == msisdnIdx || i == priIdx || i == scaIdx) {
                continue;
            }
            int tagByte = index.tags[i];
            if (AsnReaderHelper.getTagClass(tagByte) != Tag.CLASS_CONTEXT_SPECIFIC) {
                continue;
            }
            int tagNum = AsnReaderHelper.getTagNumber(tagByte);
            if (tagNum == 6 && AsnReaderHelper.isConstructed(tagByte)) {
                fields.extensionContainer = decodeExtensionContainer(index, i, messageName);
            } else if (tagNum == 7 && AsnReaderHelper.isPrimitive(tagByte) && index.valueLengths[i] == 0) {
                fields.gprsSupportIndicator = true;
            }
        }

        return fields;
    }

    public static SriFields decodeSendRoutingInfoForSm(AsnInputStream asnInputStream, int length, String messageName)
            throws MAPParsingComponentException {
        int offset = asnInputStream.getStartOffset() + asnInputStream.position();
        return decodeSendRoutingInfoForSm(asnInputStream.getBuffer(), offset, length, messageName);
    }

    // --- ReportSMDeliveryStatus (SMSC hot path) ---

    public static RsdsFields decodeReportSmDeliveryStatus(byte[] buffer, int offset, int length, String messageName)
            throws MAPParsingComponentException {
        AsnMessageIndex index = AsnIndexPool.get();
        FlatAsnParser.parseAll(buffer, offset, length, index);

        int msisdnIdx = AsnReaderHelper.findNthChild(index, -1, 0);
        int scaIdx = AsnReaderHelper.findNthChild(index, -1, 1);
        int outcomeIdx = AsnReaderHelper.findNthChild(index, -1, 2);

        if (msisdnIdx == -1 || scaIdx == -1 || outcomeIdx == -1) {
            throw mistyped(messageName, "Needs msisdn, serviceCentreAddress and sMDeliveryOutcome");
        }

        RsdsFields fields = new RsdsFields();
        fields.msisdn = decodeIsdnAddress(index, msisdnIdx, messageName);
        fields.serviceCentreAddress = decodeAddressString(index, scaIdx, messageName);
        fields.smDeliveryOutcome = SMDeliveryOutcome.getInstance((int) AsnReaderHelper.readInteger(index, outcomeIdx));

        for (int i = 0; i < index.tagCount; i++) {
            if (index.parents[i] != -1 || i == msisdnIdx || i == scaIdx || i == outcomeIdx) {
                continue;
            }
            int tagByte = index.tags[i];
            if (AsnReaderHelper.getTagClass(tagByte) != Tag.CLASS_CONTEXT_SPECIFIC) {
                continue;
            }
            int tagNum = AsnReaderHelper.getTagNumber(tagByte);
            if (tagNum == 0 && AsnReaderHelper.isPrimitive(tagByte)) {
                fields.absentSubscriberDiagnosticSm = (int) AsnReaderHelper.readInteger(index, i);
            } else if (tagNum == 1 && AsnReaderHelper.isConstructed(tagByte)) {
                fields.extensionContainer = decodeExtensionContainer(index, i, messageName);
            } else if (tagNum == 2 && AsnReaderHelper.isPrimitive(tagByte) && index.valueLengths[i] == 0) {
                fields.gprsSupportIndicator = true;
            } else if (tagNum == 3 && AsnReaderHelper.isPrimitive(tagByte) && index.valueLengths[i] == 0) {
                fields.deliveryOutcomeIndicator = true;
            }
        }

        return fields;
    }

    public static RsdsFields decodeReportSmDeliveryStatus(AsnInputStream asnInputStream, int length, String messageName)
            throws MAPParsingComponentException {
        int offset = asnInputStream.getStartOffset() + asnInputStream.position();
        return decodeReportSmDeliveryStatus(asnInputStream.getBuffer(), offset, length, messageName);
    }

    // --- InformServiceCentre (SMSC medium priority) ---

    public static InformFields decodeInformServiceCentre(byte[] buffer, int offset, int length, String messageName)
            throws MAPParsingComponentException {
        AsnMessageIndex index = AsnIndexPool.get();
        FlatAsnParser.parseAll(buffer, offset, length, index);

        InformFields fields = new InformFields();
        for (int i = 0; i < index.tagCount; i++) {
            if (index.parents[i] != -1) {
                continue;
            }
            int tagByte = index.tags[i];
            int tagClass = AsnReaderHelper.getTagClass(tagByte);
            int tagNum = AsnReaderHelper.getTagNumber(tagByte);

            if (tagClass == Tag.CLASS_UNIVERSAL) {
                if (tagNum == Tag.STRING_OCTET && AsnReaderHelper.isPrimitive(tagByte)) {
                    fields.storedMSISDN = decodeIsdnAddress(index, i, messageName);
                } else if (tagNum == Tag.STRING_BIT && AsnReaderHelper.isPrimitive(tagByte)) {
                    fields.mwStatus = decodeMwStatus(index, i, messageName);
                } else if (tagNum == Tag.SEQUENCE && AsnReaderHelper.isConstructed(tagByte)) {
                    fields.extensionContainer = decodeExtensionContainer(index, i, messageName);
                } else if (tagNum == Tag.INTEGER && AsnReaderHelper.isPrimitive(tagByte)) {
                    fields.absentSubscriberDiagnosticSM = (int) AsnReaderHelper.readInteger(index, i);
                }
            } else if (tagClass == Tag.CLASS_CONTEXT_SPECIFIC && tagNum == 0
                    && AsnReaderHelper.isPrimitive(tagByte)) {
                fields.additionalAbsentSubscriberDiagnosticSM = (int) AsnReaderHelper.readInteger(index, i);
            }
        }

        return fields;
    }

    public static InformFields decodeInformServiceCentre(AsnInputStream asnInputStream, int length, String messageName)
            throws MAPParsingComponentException {
        int offset = asnInputStream.getStartOffset() + asnInputStream.position();
        return decodeInformServiceCentre(asnInputStream.getBuffer(), offset, length, messageName);
    }

    // --- AlertServiceCentre (SMSC medium priority) ---

    public static AlertFields decodeAlertServiceCentre(byte[] buffer, int offset, int length, String messageName)
            throws MAPParsingComponentException {
        AsnMessageIndex index = AsnIndexPool.get();
        FlatAsnParser.parseAll(buffer, offset, length, index);

        int msisdnIdx = AsnReaderHelper.findNthChild(index, -1, 0);
        int scaIdx = AsnReaderHelper.findNthChild(index, -1, 1);
        if (msisdnIdx == -1 || scaIdx == -1) {
            throw mistyped(messageName, "Needs msisdn and serviceCentreAddress");
        }

        AlertFields fields = new AlertFields();
        fields.msisdn = decodeIsdnAddress(index, msisdnIdx, messageName);
        fields.serviceCentreAddress = decodeAddressString(index, scaIdx, messageName);
        return fields;
    }

    public static AlertFields decodeAlertServiceCentre(AsnInputStream asnInputStream, int length, String messageName)
            throws MAPParsingComponentException {
        int offset = asnInputStream.getStartOffset() + asnInputStream.position();
        return decodeAlertServiceCentre(asnInputStream.getBuffer(), offset, length, messageName);
    }

    // --- SendRoutingInfoForSM Response (SMSC hot path) ---

    public static SriResponseFields decodeSendRoutingInfoForSmResponse(byte[] buffer, int offset, int length,
            String messageName) throws MAPParsingComponentException {
        AsnMessageIndex index = AsnIndexPool.get();
        FlatAsnParser.parseAll(buffer, offset, length, index);

        int imsiIdx = AsnReaderHelper.findNthChild(index, -1, 0);
        int liIdx = AsnReaderHelper.findNthChild(index, -1, 1);
        if (imsiIdx == -1 || liIdx == -1) {
            throw mistyped(messageName, "Needs imsi and locationInfoWithLMSI");
        }

        int tagByte = index.tags[liIdx];
        if (AsnReaderHelper.getTagClass(tagByte) != Tag.CLASS_CONTEXT_SPECIFIC
                || AsnReaderHelper.getTagNumber(tagByte) != 0 || !AsnReaderHelper.isConstructed(tagByte)) {
            throw mistyped(messageName, "locationInfoWithLMSI bad tag");
        }

        SriResponseFields fields = new SriResponseFields();
        IMSIImpl imsi = new IMSIImpl();
        imsi.decodeFromOctetView(index.rawBuffer, index.valueOffsets[imsiIdx], index.valueLengths[imsiIdx]);
        fields.imsi = imsi;
        fields.locationInfoWithLMSI = decodeLocationInfoWithLmsi(index, liIdx, messageName);

        for (int i = 0; i < index.tagCount; i++) {
            if (index.parents[i] != -1 || i == imsiIdx || i == liIdx) {
                continue;
            }
            tagByte = index.tags[i];
            if (AsnReaderHelper.getTagClass(tagByte) != Tag.CLASS_CONTEXT_SPECIFIC) {
                continue;
            }
            int tagNum = AsnReaderHelper.getTagNumber(tagByte);
            if (tagNum == 4 && AsnReaderHelper.isConstructed(tagByte)) {
                fields.extensionContainer = decodeExtensionContainer(index, i, messageName);
            } else if (tagNum == 2 && AsnReaderHelper.isPrimitive(tagByte)) {
                fields.mwdSet = readBoolean(index, i);
            } else if (tagNum == 5 && AsnReaderHelper.isConstructed(tagByte)) {
                fields.ipSmGwGuidance = decodeIpSmGwGuidance(index, i, messageName);
            }
        }

        return fields;
    }

    public static SriResponseFields decodeSendRoutingInfoForSmResponse(AsnInputStream asnInputStream, int length,
            String messageName) throws MAPParsingComponentException {
        int offset = asnInputStream.getStartOffset() + asnInputStream.position();
        return decodeSendRoutingInfoForSmResponse(asnInputStream.getBuffer(), offset, length, messageName);
    }

    // --- internal helpers ---

    private static SM_RP_DAImpl decodeSmRpDa(AsnMessageIndex index, int elemIdx, String messageName)
            throws MAPParsingComponentException {
        int tagByte = index.tags[elemIdx];
        if (AsnReaderHelper.getTagClass(tagByte) != Tag.CLASS_CONTEXT_SPECIFIC || !AsnReaderHelper.isPrimitive(tagByte)) {
            throw mistyped(messageName, "SM_RP_DA bad tag class or not primitive");
        }

        int off = index.valueOffsets[elemIdx];
        int len = index.valueLengths[elemIdx];
        byte[] buf = index.rawBuffer;

        switch (AsnReaderHelper.getTagNumber(tagByte)) {
            case 0: {
                IMSIImpl imsi = new IMSIImpl();
                imsi.decodeFromOctetView(buf, off, len);
                return new SM_RP_DAImpl(imsi);
            }
            case 1: {
                LMSIImpl lmsi = new LMSIImpl();
                lmsi.decodeFromOctetView(buf, off, len);
                return new SM_RP_DAImpl(lmsi);
            }
            case 4: {
                AddressStringImpl sca = new AddressStringImpl();
                sca.decodeFromOctetView(buf, off, len);
                return new SM_RP_DAImpl(sca);
            }
            case 5:
                return new SM_RP_DAImpl();
            default:
                throw mistyped(messageName, "SM_RP_DA bad tag: " + AsnReaderHelper.getTagNumber(tagByte));
        }
    }

    private static SM_RP_OAImpl decodeSmRpOa(AsnMessageIndex index, int elemIdx, String messageName)
            throws MAPParsingComponentException {
        int tagByte = index.tags[elemIdx];
        if (AsnReaderHelper.getTagClass(tagByte) != Tag.CLASS_CONTEXT_SPECIFIC || !AsnReaderHelper.isPrimitive(tagByte)) {
            throw mistyped(messageName, "SM_RP_OA bad tag class or not primitive");
        }

        int off = index.valueOffsets[elemIdx];
        int len = index.valueLengths[elemIdx];
        byte[] buf = index.rawBuffer;
        SM_RP_OAImpl oa = new SM_RP_OAImpl();

        switch (AsnReaderHelper.getTagNumber(tagByte)) {
            case 2: {
                ISDNAddressStringImpl msisdn = new ISDNAddressStringImpl();
                msisdn.decodeFromOctetView(buf, off, len);
                oa.setMsisdn(msisdn);
                return oa;
            }
            case 4: {
                AddressStringImpl sca = new AddressStringImpl();
                sca.decodeFromOctetView(buf, off, len);
                oa.setServiceCentreAddressOA(sca);
                return oa;
            }
            case 5:
                return oa;
            default:
                throw mistyped(messageName, "SM_RP_OA bad tag: " + AsnReaderHelper.getTagNumber(tagByte));
        }
    }

    private static SmsSignalInfoImpl decodeSmsSignalInfo(AsnMessageIndex index, int elemIdx, String messageName)
            throws MAPParsingComponentException {
        int tagByte = index.tags[elemIdx];
        if (AsnReaderHelper.getTagClass(tagByte) != Tag.CLASS_UNIVERSAL || !AsnReaderHelper.isPrimitive(tagByte)
                || AsnReaderHelper.getTagNumber(tagByte) != Tag.STRING_OCTET) {
            throw mistyped(messageName, "sm-RP-UI bad tag class or not STRING_OCTET");
        }
        SmsSignalInfoImpl ui = new SmsSignalInfoImpl();
        ui.setDataView(index.rawBuffer, index.valueOffsets[elemIdx], index.valueLengths[elemIdx]);
        return ui;
    }

    private static ISDNAddressStringImpl decodeIsdnAddress(AsnMessageIndex index, int elemIdx, String messageName)
            throws MAPParsingComponentException {
        ISDNAddressStringImpl addr = new ISDNAddressStringImpl();
        addr.decodeFromOctetView(index.rawBuffer, index.valueOffsets[elemIdx], index.valueLengths[elemIdx]);
        return addr;
    }

    private static AddressStringImpl decodeAddressString(AsnMessageIndex index, int elemIdx, String messageName)
            throws MAPParsingComponentException {
        AddressStringImpl addr = new AddressStringImpl();
        addr.decodeFromOctetView(index.rawBuffer, index.valueOffsets[elemIdx], index.valueLengths[elemIdx]);
        return addr;
    }

    private static MWStatusImpl decodeMwStatus(AsnMessageIndex index, int elemIdx, String messageName)
            throws MAPParsingComponentException {
        MWStatusImpl mw = new MWStatusImpl();
        mw.decodeFromBitStringView(index.rawBuffer, index.valueOffsets[elemIdx], index.valueLengths[elemIdx]);
        return mw;
    }

    private static LocationInfoWithLMSIImpl decodeLocationInfoWithLmsi(AsnMessageIndex index, int elemIdx,
            String messageName) throws MAPParsingComponentException {
        AsnMessageIndex inner = new AsnMessageIndex();
        FlatAsnParser.parseAll(index.rawBuffer, index.valueOffsets[elemIdx], index.valueLengths[elemIdx], inner);

        ISDNAddressStringImpl networkNodeNumber = null;
        LMSIImpl lmsi = null;
        MAPExtensionContainer extensionContainer = null;
        boolean gprsNodeIndicator = false;
        AdditionalNumber additionalNumber = null;

        for (int i = 0; i < inner.tagCount; i++) {
            if (inner.parents[i] != -1) {
                continue;
            }
            int tagByte = inner.tags[i];
            int tagClass = AsnReaderHelper.getTagClass(tagByte);
            int tagNum = AsnReaderHelper.getTagNumber(tagByte);

            if (tagClass == Tag.CLASS_CONTEXT_SPECIFIC && tagNum == 1 && AsnReaderHelper.isPrimitive(tagByte)) {
                networkNodeNumber = decodeIsdnAddress(inner, i, messageName);
            } else if (tagClass == Tag.CLASS_UNIVERSAL && tagNum == Tag.STRING_OCTET
                    && AsnReaderHelper.isPrimitive(tagByte)) {
                lmsi = new LMSIImpl();
                lmsi.decodeFromOctetView(inner.rawBuffer, inner.valueOffsets[i], inner.valueLengths[i]);
            } else if (tagClass == Tag.CLASS_UNIVERSAL && tagNum == Tag.SEQUENCE
                    && AsnReaderHelper.isConstructed(tagByte)) {
                extensionContainer = decodeExtensionContainer(inner, i, messageName);
            } else if (tagClass == Tag.CLASS_CONTEXT_SPECIFIC && tagNum == 5 && AsnReaderHelper.isPrimitive(tagByte)
                    && inner.valueLengths[i] == 0) {
                gprsNodeIndicator = true;
            } else if (tagClass == Tag.CLASS_CONTEXT_SPECIFIC && tagNum == 6
                    && AsnReaderHelper.isConstructed(tagByte)) {
                additionalNumber = decodeAdditionalNumber(inner, i, messageName);
            }
        }

        if (networkNodeNumber == null) {
            throw mistyped(messageName, "locationInfoWithLMSI missing networkNodeNumber");
        }

        return new LocationInfoWithLMSIImpl(networkNodeNumber, lmsi, extensionContainer, gprsNodeIndicator,
                additionalNumber);
    }

    private static AdditionalNumber decodeAdditionalNumber(AsnMessageIndex index, int elemIdx, String messageName)
            throws MAPParsingComponentException {
        try {
            AsnInputStream ais = AsnInputStream.viewBytes(index.rawBuffer, index.valueOffsets[elemIdx],
                    index.valueLengths[elemIdx]);
            ais.readTag();
            AdditionalNumberImpl an = new AdditionalNumberImpl();
            an.decodeAll(ais);
            return an;
        } catch (IOException e) {
            throw mistyped(messageName, "decoding additionalNumber: " + e.getMessage());
        } catch (MAPParsingComponentException e) {
            throw mistyped(messageName, "decoding additionalNumber: " + e.getMessage());
        }
    }

    private static IpSmGwGuidance decodeIpSmGwGuidance(AsnMessageIndex index, int elemIdx, String messageName)
            throws MAPParsingComponentException {
        try {
            AsnInputStream ais = sliceAsElement(index, elemIdx);
            IpSmGwGuidanceImpl guidance = new IpSmGwGuidanceImpl();
            guidance.decodeAll(ais);
            return guidance;
        } catch (MAPParsingComponentException e) {
            throw mistyped(messageName, "decoding ipSmGwGuidance: " + e.getMessage());
        }
    }

    private static boolean readBoolean(AsnMessageIndex index, int elemIdx) {
        return index.rawBuffer[index.valueOffsets[elemIdx]] != 0;
    }

    private static boolean hasChildTag(AsnMessageIndex index, int parentIndex, int targetTagByte) {
        for (int i = 0; i < index.tagCount; i++) {
            if (index.parents[i] == parentIndex && index.tags[i] == targetTagByte) {
                return true;
            }
        }
        return false;
    }

    private static MAPExtensionContainer decodeExtensionContainer(AsnMessageIndex index, int elemIdx, String messageName)
            throws MAPParsingComponentException {
        try {
            AsnInputStream ais = sliceAsElement(index, elemIdx);
            MAPExtensionContainerImpl ec = new MAPExtensionContainerImpl();
            ec.decodeAll(ais);
            return ec;
        } catch (MAPParsingComponentException e) {
            throw mistyped(messageName, "decoding extensionContainer: " + e.getMessage());
        }
    }

    private static void decodeOptionalUssdFields(AsnMessageIndex index, int dcsIdx, int ussdIdx, UssdFields fields,
            String messageName) throws MAPParsingComponentException {
        for (int i = 0; i < index.tagCount; i++) {
            if (index.parents[i] != -1 || i == dcsIdx || i == ussdIdx) {
                continue;
            }

            int tagByte = index.tags[i];
            if (tagByte == TAG_MSISDN_CTX) {
                fields.msisdn = new ISDNAddressStringImpl();
                fields.msisdn.decodeFromOctetView(index.rawBuffer, index.valueOffsets[i], index.valueLengths[i]);
            } else if (tagByte == Tag.STRING_OCTET) {
                fields.alertingPattern = new AlertingPatternImpl();
                fields.alertingPattern.decodeFromOctetView(index.rawBuffer, index.valueOffsets[i], index.valueLengths[i]);
            }
        }
    }

    private static MAPParsingComponentException mistyped(String messageName, String detail) {
        return new MAPParsingComponentException("Error while flat-decoding " + messageName + ": " + detail,
                MAPParsingComponentExceptionReason.MistypedParameter);
    }

    /**
     * Wraps the value bytes of an indexed element for {@code decodeAll()} on {@link org.restcomm.protocols.ss7.map.primitives.SequenceBase}
     * subclasses. Those decoders expect the stream positioned after the outer tag (at the length byte), not at the tag.
     */
    private static AsnInputStream sliceAsElement(AsnMessageIndex index, int elemIdx) throws MAPParsingComponentException {
        try {
            int off = index.valueOffsets[elemIdx];
            int len = index.valueLengths[elemIdx];
            AsnOutputStream aos = new AsnOutputStream();
            aos.writeLength(len);
            aos.write(index.rawBuffer, off, len);
            return new AsnInputStream(aos.toByteArray());
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException wrapping ASN element: " + e.getMessage(), e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }
}
