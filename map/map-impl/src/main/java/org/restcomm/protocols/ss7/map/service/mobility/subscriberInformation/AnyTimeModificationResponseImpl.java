package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPMessageType;
import org.restcomm.protocols.ss7.map.api.MAPOperationCode;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeModificationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.CAMELSubscriptionInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.CallHoldData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.CallWaitingData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ClipData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ClirData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.EctData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ExtSSInfoForCSE;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ODBInfo;
import org.restcomm.protocols.ss7.map.primitives.AddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.service.mobility.MobilityMessageImpl;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class AnyTimeModificationResponseImpl extends MobilityMessageImpl implements AnyTimeModificationResponse {

  private static final int _TAG_SS_INFO_FOR_CSE = 0;
  private static final int _TAG_CAMEL_SUBSCRIPTION_INFO = 1;
  private static final int _TAG_EXTENSION_CONTAINER = 2;
  private static final int _TAG_ODB_INFO = 3;
  private static final int _TAG_CW_DATA = 4;
  private static final int _TAG_CH_DATA = 5;
  private static final int _TAG_CLIP_DATA = 6;
  private static final int _TAG_CLIR_DATA = 7;
  private static final int _TAG_ECT_DATA = 8;
  private static final int _TAG_SERVICE_CENTRE_ADDRESS = 9;

  public static final String _PrimitiveName = "AnyTimeModificationResponse";

  private ExtSSInfoForCSE ssInfoForCSE;
  private CAMELSubscriptionInfo camelSubscriptionInfo;
  private MAPExtensionContainer extensionContainer;
  private ODBInfo odbInfo;
  private CallWaitingData cwData;
  private CallHoldData chData;
  private ClipData clipData;
  private ClirData clirData;
  private EctData ectData;
  private AddressString serviceCentreAddress;

  public AnyTimeModificationResponseImpl() {
  }

  public AnyTimeModificationResponseImpl(ExtSSInfoForCSE ssInfoForCSE, CAMELSubscriptionInfo camelSubscriptionInfo,
          MAPExtensionContainer extensionContainer, ODBInfo odbInfo, CallWaitingData cwData, CallHoldData chData,
          ClipData clipData, ClirData clirData, EctData ectData, AddressString serviceCentreAddress) {
    this.ssInfoForCSE = ssInfoForCSE;
    this.camelSubscriptionInfo = camelSubscriptionInfo;
    this.extensionContainer = extensionContainer;
    this.odbInfo = odbInfo;
    this.cwData = cwData;
    this.chData = chData;
    this.clipData = clipData;
    this.clirData = clirData;
    this.ectData = ectData;
    this.serviceCentreAddress = serviceCentreAddress;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeModificationResponse#getSsInfoForCSE()
   */
  @Override
  public ExtSSInfoForCSE getSsInfoForCSE() {
    return this.ssInfoForCSE;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeModificationResponse#getCamelSubscriptionInfo()
   */
  @Override
  public CAMELSubscriptionInfo getCamelSubscriptionInfo() {
    return this.camelSubscriptionInfo;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeModificationResponse#getExtensionContainer()
   */
  @Override
  public MAPExtensionContainer getExtensionContainer() {
    return this.extensionContainer;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeModificationResponse#getOdbInfo()
   */
  @Override
  public ODBInfo getOdbInfo() {
    return this.odbInfo;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeModificationResponse#getCwData()
   */
  @Override
  public CallWaitingData getCwData() {
    return this.cwData;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeModificationResponse#getChData()
   */
  @Override
  public CallHoldData getChData() {
    return this.chData;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeModificationResponse#getClipData()
   */
  @Override
  public ClipData getClipData() {
    return this.clipData;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeModificationResponse#getClirData()
   */
  @Override
  public ClirData getClirData() {
    return this.clirData;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeModificationResponse#getEctData()
   */
  @Override
  public EctData getEctData() {
    return this.ectData;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeModificationResponse#getServiceCentreAddress()
   */
  @Override
  public AddressString getServiceCentreAddress() {
    return this.serviceCentreAddress;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#getTag()
   */
  public int getTag() throws MAPException {
    return Tag.SEQUENCE;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#getTagClass()
   */
  public int getTagClass() {
    return Tag.CLASS_UNIVERSAL;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#getIsPrimitive()
   */
  public boolean getIsPrimitive() {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.MAPMessage#getMessageType()
   */
  public MAPMessageType getMessageType() {
    return MAPMessageType.anyTimeModification_Response;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.MAPMessage#getOperationCode()
   */
  public int getOperationCode() {
    return MAPOperationCode.anyTimeModification;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#decodeAll( org.mobicents.protocols.asn.AsnInputStream)
   */
  public void decodeAll(AsnInputStream ansIS) throws MAPParsingComponentException {
    try {
      int length = ansIS.readLength();
      this._decode(ansIS, length);
    } catch (IOException e) {
      throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
          MAPParsingComponentExceptionReason.MistypedParameter);
    } catch (AsnException e) {
      throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
          MAPParsingComponentExceptionReason.MistypedParameter);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#decodeData (org.mobicents.protocols.asn.AsnInputStream,
   * int)
   */
  public void decodeData(AsnInputStream ansIS, int length) throws MAPParsingComponentException {
    try {
      this._decode(ansIS, length);
    } catch (IOException e) {
      throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
          MAPParsingComponentExceptionReason.MistypedParameter);
    } catch (AsnException e) {
      throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
          MAPParsingComponentExceptionReason.MistypedParameter);
    }
  }

  private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
    this.ssInfoForCSE = null;
    this.camelSubscriptionInfo = null;
    this.extensionContainer = null;
    this.odbInfo = null;
    this.cwData = null;
    this.chData = null;
    this.clipData = null;
    this.clirData = null;
    this.ectData = null;
    this.serviceCentreAddress = null;

    AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

    while (true) {
      if (ais.available() == 0)
        break;

      int tag = ais.readTag();

      if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
        switch (tag) {
          case _TAG_SS_INFO_FOR_CSE:
            // ss-InfoFor-CSE [0] Ext-SS-InfoFor-CSE OPTIONAL
            AsnInputStream ais2 = ais.readSequenceStream();
            ais2.readTag();
            this.ssInfoForCSE = new ExtSSInfoForCSEImpl();
            ((ExtSSInfoForCSEImpl) this.ssInfoForCSE).decodeAll(ais2);
            break;

          case _TAG_CAMEL_SUBSCRIPTION_INFO:
            // camel-SubscriptionInfo [1] CAMEL-SubscriptionInfo OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".camelSubscriptionInfo: parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.camelSubscriptionInfo = new CAMELSubscriptionInfoImpl();
            ((CAMELSubscriptionInfoImpl)this.camelSubscriptionInfo).decodeAll(ais);
            break;

          case _TAG_EXTENSION_CONTAINER:
            // extensionContainer [2] ExtensionContainer OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".extensionContainer: Parameter extensionContainer is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.extensionContainer = new MAPExtensionContainerImpl();
            ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
            break;

          case _TAG_ODB_INFO:
            // odb-Info [3] ODB-Info OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".odbInfo: parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.odbInfo = new ODBInfoImpl();
            ((ODBInfoImpl)this.odbInfo).decodeAll(ais);
            break;

          case _TAG_CW_DATA:
            // cw-Data [4] CallWaitingData OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".cwData: parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.cwData = new CallWaitingDataImpl();
            ((CallWaitingDataImpl)this.cwData).decodeAll(ais);
            break;

          case _TAG_CH_DATA:
            // ch-Data [5] CallHoldData OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".chData: parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.chData = new CallHoldDataImpl();
            ((CallHoldDataImpl)this.chData).decodeAll(ais);
            break;

          case _TAG_CLIP_DATA:
            // clip-Data [6] ClipData OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".clipData: parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.clipData = new ClipDataImpl();
            ((ClipDataImpl)this.clipData).decodeAll(ais);
            break;

          case _TAG_CLIR_DATA:
            // clir-Data [7] ClirData OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".clirData: parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.clirData = new ClirDataImpl();
            ((ClirDataImpl)this.clirData).decodeAll(ais);
            break;

          case _TAG_ECT_DATA:
            // ect-data [8] EctData OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".ectData: parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.ectData = new EctDataImpl();
            ((EctDataImpl)this.ectData).decodeAll(ais);
            break;

          case _TAG_SERVICE_CENTRE_ADDRESS:
            // serviceCentreAddress [9] AddressString OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".serviceCentreAddress: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.serviceCentreAddress = new AddressStringImpl();
            ((AddressStringImpl) this.serviceCentreAddress).decodeAll(ais);
            break;

          default:
            ais.advanceElement();
            break;
        }
      } else {
        ais.advanceElement();
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#encodeAll( org.mobicents.protocols.asn.AsnOutputStream)
   */
  public void encodeAll(AsnOutputStream asnOutputStream) throws MAPException {
    this.encodeAll(asnOutputStream, this.getTagClass(), this.getTag());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#encodeAll( org.mobicents.protocols.asn.AsnOutputStream,
   * int, int)
   */
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

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#encodeData(org.mobicents.protocols.asn.AsnOutputStream)
   */
  public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
      try {
          // ss-InfoFor-CSE [0] Ext-SS-InfoFor-CSE OPTIONAL
          if (ssInfoForCSE != null) {
              asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_SS_INFO_FOR_CSE);
              int pos = asnOutputStream.StartContentDefiniteLength();
              ((ExtSSInfoForCSEImpl) this.ssInfoForCSE).encodeAll(asnOutputStream);
              asnOutputStream.FinalizeContent(pos);
          }

          // camel-SubscriptionInfo [1] CAMEL-SubscriptionInfo OPTIONAL
          if (camelSubscriptionInfo != null)
              ((CAMELSubscriptionInfoImpl) this.camelSubscriptionInfo).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_CAMEL_SUBSCRIPTION_INFO);

          // extensionContainer [2] ExtensionContainer OPTIONAL
          if (extensionContainer != null)
              ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_EXTENSION_CONTAINER);

          // // odb-Info [3] ODB-Info OPTIONAL
          if (this.odbInfo != null)
              ((ODBInfoImpl) this.odbInfo).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_ODB_INFO);

          // cw-Data [4] CallWaitingData OPTIONAL
          if (this.cwData != null)
              ((CallWaitingDataImpl) this.cwData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_CW_DATA);

          // ch-Data [5] CallHoldData OPTIONAL
          if (this.chData != null)
              ((CallHoldDataImpl) this.chData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_CH_DATA);

          // clip-Data [6] ClipData OPTIONAL
          if (this.clipData != null)
              ((ClipDataImpl) this.clipData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_CLIP_DATA);

          // clir-Data [7] ClirData OPTIONAL
          if (this.clirData != null)
              ((ClirDataImpl) this.clirData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_CLIR_DATA);

          // ect-data [8] EctData OPTIONAL
          if (this.ectData != null)
              ((EctDataImpl) this.ectData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_ECT_DATA);

          // serviceCentreAddress [9] AddressString OPTIONAL
          if (serviceCentreAddress != null)
              ((AddressStringImpl) this.serviceCentreAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SERVICE_CENTRE_ADDRESS);

      } catch (AsnException e) {
          throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
      }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_PrimitiveName);
    sb.append(" [");

    if (this.ssInfoForCSE != null) {
      sb.append("ssInfoForCSE=");
      sb.append(this.ssInfoForCSE);
    }
    if (this.camelSubscriptionInfo != null) {
      sb.append(", camelSubscriptionInfo=");
      sb.append(this.camelSubscriptionInfo);
    }
    if (this.extensionContainer != null) {
      sb.append(", extensionContainer=");
      sb.append(this.extensionContainer);
    }
    if (this.odbInfo != null) {
      sb.append(", odbInfo=");
      sb.append(this.odbInfo);
    }
    if (this.cwData != null) {
      sb.append(", cwData=");
      sb.append(this.cwData);
    }
    if (this.chData != null) {
      sb.append(", chData=");
      sb.append(this.chData);
    }
    if (this.clipData != null) {
      sb.append(", clipData=");
      sb.append(this.clipData);
    }
    if (this.clirData != null) {
      sb.append(", clirData=");
      sb.append(this.clirData);
    }
    if (this.ectData != null) {
      sb.append(", ectData=");
      sb.append(this.ectData);
    }
    if (this.serviceCentreAddress != null) {
      sb.append(", serviceCentreAddress=");
      sb.append(this.serviceCentreAddress);
    }

    sb.append("]");
    return sb.toString();
  }
}
