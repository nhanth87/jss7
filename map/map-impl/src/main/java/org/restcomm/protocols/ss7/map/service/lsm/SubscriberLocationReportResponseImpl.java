package org.restcomm.protocols.ss7.map.service.lsm;

import java.io.IOException;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.BerCursor;
import org.mobicents.protocols.asn.BerTag;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.MapBerSupport;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPMessageType;
import org.restcomm.protocols.ss7.map.api.MAPOperationCode;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.GSNAddress;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.lsm.ReportingPLMNList;
import org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportResponse;
import org.restcomm.protocols.ss7.map.primitives.GSNAddressImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;

/**
 * @author amit bhayani
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class SubscriberLocationReportResponseImpl extends LsmMessageImpl implements SubscriberLocationReportResponse {

    private static final int _TAG_NA_ESRK = 0;
    private static final int _TAG_NA_ESRD = 1;
    private static final int _TAG_H_GMLC_ADDRESS = 2;
    private static final int _TAG_MO_LR_SHORT_CIC_INDICATOR = 3;
    private static final int _TAG_REP_PLMN_LIST = 4;
    private static final int _TAG_LCS_REF_NUMBER = 5;

    public static final String _PrimitiveName = "SubscriberLocationReportResponse";

    private ISDNAddressString naEsrd;
    private ISDNAddressString naEsrk;
    private MAPExtensionContainer extensionContainer;
    private GSNAddress hGMLCAddress;
    private boolean molrShortCircuitIndicator;
    private ReportingPLMNList reportingPLMNList;
    private Integer lcsReferenceNumber;

    /**
     *
     */
    public SubscriberLocationReportResponseImpl() {
        super();
    }

    /**
     * @param naEsrd OPTIONAL If the target MS has originated an emergency service call in North America,
     *               the NA-ESRK shall be provided by the VMSC if assigned
     * @param naEsrk OPTIONAL If the target MS has originated an emergency service call in North America,
     *               the NA-ESRD shall be provided by the VMSC if available
     *
     *               NA-ESRK and NA-ESRD are mutually exclusive:
     *               If the target MS has originated an emergency service call in North America and NA-ESRK Request or NA-ESDR
     *               is included in Subscriber_Location_Report-Arg, an NA-ESRK or NA-ESRD,
     *               but not both, may also be included in the response to the MSC, see 3GPP TS 23.271
     * @param extensionContainer OPTIONAL
     * @param hGmlcAddress OPTIONAL shall be included in a Subscriber Location Report response
     *                     if a deferred MO-LR TTTP procedure is initiated for a periodic positioning event.
     * @param molrShortCircuitIndicator OPTIONAL indicates whether MO-LR Short Circuit is permitted for periodic location
     * @param reportingPLMNList OPTIONAL indicates a list of PLMNs in which subsequent periodic MO-LR TTTP requests will be made
     * @param lcsReferenceNumber OPTIONAL shall be included if the Subscriber Location Report is the response to a deferred MT location request
     */
    public SubscriberLocationReportResponseImpl(ISDNAddressString naEsrd, ISDNAddressString naEsrk, MAPExtensionContainer extensionContainer,
            GSNAddress hGmlcAddress, boolean molrShortCircuitIndicator, ReportingPLMNList reportingPLMNList, Integer lcsReferenceNumber) {
        super();
        this.naEsrd = naEsrd;
        this.naEsrk = naEsrk;
        this.extensionContainer = extensionContainer;
        this.hGMLCAddress = hGmlcAddress;
        this.molrShortCircuitIndicator = molrShortCircuitIndicator;
        this.reportingPLMNList = reportingPLMNList;
        this.lcsReferenceNumber = lcsReferenceNumber;
    }

    public MAPMessageType getMessageType() {
        return MAPMessageType.subscriberLocationReport_Response;
    }

    public int getOperationCode() {
        return MAPOperationCode.subscriberLocationReport;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportResponseIndication#getExtensionContainer()
     */
    @Override
    public MAPExtensionContainer getExtensionContainer() {
        return this.extensionContainer;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportResponseIndication#getNaESRK()
     */
    @Override
    public ISDNAddressString getNaESRK() {
        return this.naEsrk;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportResponseIndication#getNaESRD()
     */
    @Override
    public ISDNAddressString getNaESRD() {
        return this.naEsrd;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportResponseIndication#getHGMLCAddress()
     */
    @Override
    public GSNAddress getHGMLCAddress() {
        return this.hGMLCAddress;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportResponseIndication#getMolrShortCircuitIndicator()
     */
    @Override
    public boolean getMolrShortCircuitIndicator() {
        return this.molrShortCircuitIndicator;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportResponseIndication#getReportingPLMNList()
     */
    @Override
    public ReportingPLMNList getReportingPLMNList() {
        return this.reportingPLMNList;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportResponseIndication#getLcsReferenceNumber()
     */
    @Override
    public Integer getLcsReferenceNumber() {
        return this.lcsReferenceNumber;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#getTag()
     */
    public int getTag() throws MAPException {
        return Tag.SEQUENCE;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#getTagClass ()
     */
    public int getTagClass() {
        return Tag.CLASS_UNIVERSAL;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#getIsPrimitive ()
     */
    public boolean getIsPrimitive() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#decodeAll
     * (org.mobicents.protocols.asn.AsnInputStream)
     */
    public void decodeAll(AsnInputStream asnInputStream) throws MAPParsingComponentException {
        try {
            int length = asnInputStream.readLength();
            MapBerSupport.decodeDispatch(_PrimitiveName, asnInputStream, length, this::_decodeBer, this::_clearFields,
                    this::_decode);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": ", e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": ", e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#decodeData
     * (org.mobicents.protocols.asn.AsnInputStream, int)
     */
    public void decodeData(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException {
        try {
            MapBerSupport.decodeDispatch(_PrimitiveName, asnInputStream, length, this::_decodeBer, this::_clearFields,
                    this::_decode);
        } catch (IOException e) {
            throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": ", e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": ", e,
                    MAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    private void _clearFields() {
        this.naEsrd = null;
        this.naEsrk = null;
        this.extensionContainer = null;
        this.hGMLCAddress = null;
        this.molrShortCircuitIndicator = false;
        this.reportingPLMNList = null;
        this.lcsReferenceNumber = null;
    }

    private void _decodeBer(byte[] buf, int offset, int length)
            throws AsnException, IOException, MAPParsingComponentException {
        _clearFields();
        BerCursor c = BerCursor.wrapHeap(buf, offset, length);
        while (c.hasMore()) {
            c.readTag();
            if (c.tagClass() == BerTag.UNIVERSAL) {
                if (c.tag() == BerTag.SEQUENCE && !c.isPrimitive()) {
                    MAPExtensionContainerImpl ext = new MAPExtensionContainerImpl();
                    MapBerSupport.decodeNested(c, ext);
                    this.extensionContainer = ext;
                } else {
                    MapBerSupport.unknownTag(_PrimitiveName, c);
                }
                continue;
            }
            if (c.tagClass() != BerTag.CONTEXT)
                MapBerSupport.unknownTag(_PrimitiveName, c);
            switch (c.tag()) {
                case _TAG_NA_ESRK -> {
                    if (!c.isPrimitive())
                        throw new AsnException(_PrimitiveName + ": naEsrk is not primitive");
                    ISDNAddressStringImpl esrk = new ISDNAddressStringImpl();
                    MapBerSupport.decodeNested(c, esrk);
                    this.naEsrk = esrk;
                }
                case _TAG_NA_ESRD -> {
                    if (!c.isPrimitive())
                        throw new AsnException(_PrimitiveName + ": naEsrd is not primitive");
                    ISDNAddressStringImpl esrd = new ISDNAddressStringImpl();
                    MapBerSupport.decodeNested(c, esrd);
                    this.naEsrd = esrd;
                }
                case _TAG_H_GMLC_ADDRESS -> {
                    if (!c.isPrimitive())
                        throw new AsnException(_PrimitiveName + ": hGMLCAddress is not primitive");
                    GSNAddressImpl hg = new GSNAddressImpl();
                    MapBerSupport.decodeNested(c, hg);
                    this.hGMLCAddress = hg;
                }
                case _TAG_MO_LR_SHORT_CIC_INDICATOR -> {
                    MapBerSupport.readNull(c);
                    this.molrShortCircuitIndicator = true;
                }
                case _TAG_REP_PLMN_LIST -> {
                    if (c.isPrimitive())
                        throw new AsnException(_PrimitiveName + ": reportingPLMNList is primitive");
                    ReportingPLMNListImpl rpl = new ReportingPLMNListImpl();
                    MapBerSupport.decodeNested(c, rpl);
                    this.reportingPLMNList = rpl;
                }
                case _TAG_LCS_REF_NUMBER -> {
                    if (!c.isPrimitive())
                        throw new AsnException(_PrimitiveName + ": lcsReferenceNumber is not primitive");
                    this.lcsReferenceNumber = MapBerSupport.firstOctet(c);
                }
                default -> MapBerSupport.unknownTag(_PrimitiveName, c);
            }
        }
    }

    private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
        _clearFields();

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            if (ais.getTagClass() == Tag.CLASS_UNIVERSAL) {
                switch (tag) {
                    case Tag.SEQUENCE:
                        // ExtensionContainer
                        if (ais.isTagPrimitive())
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter extensionContainer is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        this.extensionContainer = new MAPExtensionContainerImpl();
                        ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
                        break;

                    default:
                        ais.advanceElement();
                        break;
                }// switch
            } else if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
                switch (tag) {
                    case _TAG_NA_ESRK:
                        // na-ESRK [0] ISDN-AddressString OPTIONAL
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter [na-ESRK [0] ISDN-AddressString] is not Sequence",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.naEsrk = new ISDNAddressStringImpl();
                        ((ISDNAddressStringImpl) this.naEsrk).decodeAll(ais);
                        break;
                    case _TAG_NA_ESRD:
                        // na-ESRD [1] ISDN-AddressString OPTIONAL,
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter [na-ESRD [1] ISDN-AddressString] is not Sequence",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.naEsrd = new ISDNAddressStringImpl();
                        ((ISDNAddressStringImpl) this.naEsrd).decodeAll(ais);
                        break;
                    case _TAG_H_GMLC_ADDRESS:
                        // h-gmlc-Address [2] GSN-Address OPTIONAL
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter [h-gmlc-Address [2] GSN-Address] is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.hGMLCAddress = new GSNAddressImpl();
                        ((GSNAddressImpl) this.hGMLCAddress).decodeAll(ais);
                        break;
                    case _TAG_MO_LR_SHORT_CIC_INDICATOR:
                        // mo-lrShortCircuitIndicator [3] NULL OPTIONAL
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter [mo-lrShortCircuitIndicator [3] NULL] is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        ais.readNull();
                        this.molrShortCircuitIndicator = true;
                        break;
                    case _TAG_REP_PLMN_LIST:
                        // reportingPLMNList [4] ReportingPLMNList OPTIONAL
                        if (ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter [reportingPLMNList [4] ReportingPLMNList] is primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.reportingPLMNList = new ReportingPLMNListImpl();
                        ((ReportingPLMNListImpl) this.reportingPLMNList).decodeAll(ais);
                        break;
                    case _TAG_LCS_REF_NUMBER:
                        // lcs-ReferenceNumber [5] LCS-ReferenceNumber OPTIONAL
                        if (!ais.isTagPrimitive()) {
                            throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                                    + ": Parameter [lcs-ReferenceNumber [5] LCS-ReferenceNumber] is not primitive",
                                    MAPParsingComponentExceptionReason.MistypedParameter);
                        }
                        this.lcsReferenceNumber = (int) ais.readOctetString()[0];
                        break;
                    default:
                        ais.advanceElement();
                        break;
                }
            } else {
                ais.advanceElement();
            }
        }// while
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#encodeAll
     * (org.mobicents.protocols.asn.AsnOutputStream)
     */
    public void encodeAll(AsnOutputStream asnOutputStream) throws MAPException {
        this.encodeAll(asnOutputStream, this.getTagClass(), this.getTag());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#encodeAll
     * (org.mobicents.protocols.asn.AsnOutputStream, int, int)
     */
    public void encodeAll(AsnOutputStream asnOutputStream, int tagClass, int tag) throws MAPException {
        try {
            asnOutputStream.writeTag(tagClass, false, tag);
            int pos = asnOutputStream.StartContentDefiniteLength();
            this.encodeData(asnOutputStream);
            asnOutputStream.FinalizeContent(pos);
        } catch (AsnException e) {
            throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.primitives.MAPAsnPrimitive#encodeData
     * (org.mobicents.protocols.asn.AsnOutputStream)
     */
    public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
        if (this.extensionContainer != null) {
            // extensionContainer ExtensionContainer OPTIONAL
            ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream);
        }

        if (this.naEsrd != null && this.naEsrk != null)
            throw new MAPException("IOException while encoding " + _PrimitiveName + ": parameters naEsrd and naEsrd are mutually exclusive, " +
                    "naEsrd and naEsrd but NOT BOTH may be included in SLR response to the MSC");

        if (this.naEsrk != null) {
            // na-ESRK [0] ISDN-AddressString OPTIONAL
            ((ISDNAddressStringImpl) this.naEsrk).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_NA_ESRK);
        }

        if (this.naEsrd != null) {
            // na-ESRD [1] ISDN-AddressString OPTIONAL
            ((ISDNAddressStringImpl) this.naEsrd).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_NA_ESRD);
        }

        if (this.hGMLCAddress != null) {
            // h-gmlc-Address [2] GSN-Address OPTIONAL
            ((GSNAddressImpl) this.hGMLCAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_H_GMLC_ADDRESS);
        }

        if (this.molrShortCircuitIndicator) {
            // mo-lrShortCircuitIndicator [3] NULL OPTIONAL
            try {
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_MO_LR_SHORT_CIC_INDICATOR);
            } catch (IOException e) {
                throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter molrShortCircuitIndicator", e);
            } catch (AsnException e) {
                throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter molrShortCircuitIndicator", e);
            }
        }

        if (this.reportingPLMNList != null) {
            // reportingPLMNList [4] ReportingPLMNList OPTIONAL
            ((ReportingPLMNListImpl) this.reportingPLMNList).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                    _TAG_REP_PLMN_LIST);
        }

        if (this.lcsReferenceNumber != null) {
            // lcs-ReferenceNumber [5] LCS-ReferenceNumber OPTIONAL
            try {
                asnOutputStream.writeOctetString(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_LCS_REF_NUMBER,
                        new byte[] { this.lcsReferenceNumber.byteValue() });
            } catch (IOException e) {
                throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter lcsReferenceNumber", e);
            } catch (AsnException e) {
                throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter lcsReferenceNumber", e);
            }
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.extensionContainer != null) {
            sb.append("extensionContainer");
            sb.append(this.extensionContainer);
        }
        if (this.naEsrd != null) {
            sb.append(", naEsrd=");
            sb.append(this.naEsrd);
        }
        if (this.naEsrk != null) {
            sb.append(", naEsrk=");
            sb.append(this.naEsrk);
        }
        if (this.hGMLCAddress != null) {
            sb.append(", hGMLCAddress=");
            sb.append(this.hGMLCAddress);
        }
        if (this.molrShortCircuitIndicator) {
            sb.append(", molrShortCircuitIndicator");
        }
        if (this.reportingPLMNList != null) {
            sb.append(", reportingPLMNList=");
            sb.append(reportingPLMNList);
        }
        if (this.lcsReferenceNumber != null) {
            sb.append(", lcsReferenceNumber=");
            sb.append(lcsReferenceNumber);
        }

        sb.append("]");

        return sb.toString();
    }
}
