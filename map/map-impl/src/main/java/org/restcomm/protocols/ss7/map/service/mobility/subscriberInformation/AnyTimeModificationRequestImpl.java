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
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.SubscriberIdentity;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeModificationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForCBInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForCFInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForCHInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForCLIPInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForCLIRInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForCSG;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForCSI;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForCWInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForECTInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForIPSMGWData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForODBData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.RequestedServingNode;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.SubscriberIdentityImpl;
import org.restcomm.protocols.ss7.map.service.mobility.MobilityMessageImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ModificationRequestForCBInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ModificationRequestForCFInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ModificationRequestForCHInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ModificationRequestForCLIPInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ModificationRequestForCLIRInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ModificationRequestForCSGImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ModificationRequestForCSIImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ModificationRequestForCWInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ModificationRequestForECTInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ModificationRequestForIPSMGWDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ModificationRequestForODBDataImpl;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class AnyTimeModificationRequestImpl extends MobilityMessageImpl implements AnyTimeModificationRequest, MAPAsnPrimitive {

  private static final int _TAG_SUBSCRIBER_IDENTITY = 0;
  private static final int _TAG_GSM_SCF_ADDRESS = 1;
  private static final int _TAG_MODIFICATION_REQUEST_FOR_CF_INFO = 2;
  private static final int _TAG_MODIFICATION_REQUEST_FOR_CB_INFO = 3;
  private static final int _TAG_MODIFICATION_REQUEST_FOR_CSI = 4;
  private static final int _TAG_EXTENSION_CONTAINER = 5;
  private static final int _TAG_LONG_FTN_SUPPORTED = 6;
  private static final int _TAG_MODIFICATION_REQUEST_FOR_ODB_DATA = 7;
  private static final int _TAG_MODIFICATION_REQUEST_FOR_IP_SM_GW_DATA = 8;
  private static final int _TAG_ACTIVATION_REQUEST_FOR_UE_REACHABILITY = 9;
  private static final int _TAG_MODIFICATION_REQUEST_FOR_CSG = 10;
  private static final int _TAG_MODIFICATION_REQUEST_FOR_CW_DATA = 11;
  private static final int _TAG_MODIFICATION_REQUEST_FOR_CLIP_INFO = 12;
  private static final int _TAG_MODIFICATION_REQUEST_FOR_CLIR_INFO = 13;
  private static final int _TAG_MODIFICATION_REQUEST_FOR_HOLD_DATA = 14;
  private static final int _TAG_MODIFICATION_REQUEST_FOR_ECT_DATA = 15;

  public static final String _PrimitiveName = "AnyTimeModificationRequest";

  private SubscriberIdentity subscriberIdentity;
  private ISDNAddressString gsmSCFAddress;
  private ModificationRequestForCFInfo modificationRequestForCFInfo;
  private ModificationRequestForCBInfo modificationRequestForCBInfo;
  private ModificationRequestForCSI modificationRequestForCSI;
  private MAPExtensionContainer extensionContainer;
  private boolean longFTNSupported;
  private ModificationRequestForODBData modificationRequestForODBData;
  private ModificationRequestForIPSMGWData modificationRequestForIPSMGWData;
  private RequestedServingNode activationRequestForUEReachability;
  private ModificationRequestForCSG modificationRequestForCSG;
  private ModificationRequestForCWInfo modificationRequestForCWData;
  private ModificationRequestForCLIPInfo modificationRequestForCLIPData;
  private ModificationRequestForCLIRInfo modificationRequestForCLIRData;
  private ModificationRequestForCHInfo modificationRequestForHOLDData;
  private ModificationRequestForECTInfo modificationRequestForECTData;

  public AnyTimeModificationRequestImpl() {
  }

  public AnyTimeModificationRequestImpl(SubscriberIdentity subscriberIdentity, ISDNAddressString gsmSCFAddress,
          ModificationRequestForCFInfo modificationRequestForCFInfo, ModificationRequestForCBInfo modificationRequestForCBInfo,
          ModificationRequestForCSI modificationRequestForCSI, MAPExtensionContainer extensionContainer,
          boolean longFTNSupported, ModificationRequestForODBData modificationRequestForODBData,
          ModificationRequestForIPSMGWData modificationRequestForIPSMGWData, RequestedServingNode activationRequestForUEReachability,
          ModificationRequestForCSG modificationRequestForCSG, ModificationRequestForCWInfo modificationRequestForCWData,
          ModificationRequestForCLIPInfo modificationRequestForCLIPData, ModificationRequestForCLIRInfo modificationRequestForCLIRData,
          ModificationRequestForCHInfo modificationRequestForHOLDData, ModificationRequestForECTInfo modificationRequestForECTData) {
    this.subscriberIdentity = subscriberIdentity;
    this.gsmSCFAddress = gsmSCFAddress;
    this.modificationRequestForCFInfo = modificationRequestForCFInfo;
    this.modificationRequestForCBInfo = modificationRequestForCBInfo;
    this.modificationRequestForCSI = modificationRequestForCSI;
    this.extensionContainer = extensionContainer;
    this.longFTNSupported = longFTNSupported;
    this.modificationRequestForODBData = modificationRequestForODBData;
    this.modificationRequestForIPSMGWData = modificationRequestForIPSMGWData;
    this.activationRequestForUEReachability = activationRequestForUEReachability;
    this.modificationRequestForCSG = modificationRequestForCSG;
    this.modificationRequestForCWData = modificationRequestForCWData;
    this.modificationRequestForCLIPData = modificationRequestForCLIPData;
    this.modificationRequestForCLIRData = modificationRequestForCLIRData;
    this.modificationRequestForHOLDData = modificationRequestForHOLDData;
    this.modificationRequestForECTData = modificationRequestForECTData;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getSubscriberIdentity()
   */
  public SubscriberIdentity getSubscriberIdentity() {
    return this.subscriberIdentity;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getGsmSCFAddress()
   */
  public ISDNAddressString getGsmSCFAddress() {
    return this.gsmSCFAddress;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getModificationRequestForCfInfo()
   */
  @Override
  public ModificationRequestForCFInfo getModificationRequestForCFInfo() {
    return this.modificationRequestForCFInfo;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getModificationRequestForCBInfo()
   */
  @Override
  public ModificationRequestForCBInfo getModificationRequestForCBInfo() {
    return this.modificationRequestForCBInfo;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getModificationRequestForCSI()
   */
  @Override
  public ModificationRequestForCSI getModificationRequestForCSI() {
    return this.modificationRequestForCSI;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getExtensionContainer()
   */
  public MAPExtensionContainer getExtensionContainer() {
    return this.extensionContainer;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#isLongFTNSupported()
   */
  public boolean isLongFTNSupported() {
    return this.longFTNSupported;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getModificationRequestForODBData()
   */
  @Override
  public ModificationRequestForODBData getModificationRequestForODBData() {
    return this.modificationRequestForODBData;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getModificationRequestForIPSMGWData()
   */
  @Override
  public ModificationRequestForIPSMGWData getModificationRequestForIPSMGWData() {
    return this.modificationRequestForIPSMGWData;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getActivationRequestForUEReachability()
   */
  @Override
  public RequestedServingNode getActivationRequestForUEReachability() {
    return this.activationRequestForUEReachability;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getModificationRequestForCSG()
   */
  @Override
  public ModificationRequestForCSG getModificationRequestForCSG() {
    return this.modificationRequestForCSG;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getModificationRequestForCWData()
   */
  @Override
  public ModificationRequestForCWInfo getModificationRequestForCWData() {
    return this.modificationRequestForCWData;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getModificationRequestForCLIPData()
   */
  @Override
  public ModificationRequestForCLIPInfo getModificationRequestForCLIPData() {
    return this.modificationRequestForCLIPData;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getModificationRequestForCLIRData()
   */
  @Override
  public ModificationRequestForCLIRInfo getModificationRequestForCLIRData() {
    return this.modificationRequestForCLIRData;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getModificationRequestForHOLDData()
   */
  @Override
  public ModificationRequestForCHInfo getModificationRequestForHOLDData() {
    return this.modificationRequestForHOLDData;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.api.service.subscriberInformation.
   * AnyTimeInterrogationRequestIndication#getModificationRequestForECTData()
   */
  @Override
  public ModificationRequestForECTInfo getModificationRequestForECTData() {
    return this.modificationRequestForECTData;
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
    return MAPMessageType.anyTimeModification_Request;
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
    this.subscriberIdentity = null;
    this.gsmSCFAddress = null;
    this.modificationRequestForCFInfo = null;
    this.modificationRequestForCBInfo = null;
    this.modificationRequestForCSI = null;
    this.extensionContainer = null;
    this.longFTNSupported = false;
    this.modificationRequestForODBData = null;
    this.modificationRequestForIPSMGWData = null;
    this.activationRequestForUEReachability = null;
    this.modificationRequestForCSG = null;
    this.modificationRequestForCWData = null;
    this.modificationRequestForCLIPData = null;
    this.modificationRequestForCLIRData = null;
    this.modificationRequestForHOLDData = null;
    this.modificationRequestForECTData = null;

    AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

    while (true) {
      if (ais.available() == 0)
        break;

      int tag = ais.readTag();

      if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
        switch (tag) {
          case _TAG_SUBSCRIBER_IDENTITY:
            // subscriberIdentity [0] SubscriberIdentity
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".subscriberIdentity: parameter is primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);

            this.subscriberIdentity = new SubscriberIdentityImpl();
            AsnInputStream ais0 = ais.readSequenceStream();
            ais0.readTag();
            ((SubscriberIdentityImpl) this.subscriberIdentity).decodeAll(ais0);
            break;
          case _TAG_GSM_SCF_ADDRESS:
            // gsmSCF-Address [1] ISDN-AddressString
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".gsmSCFAddress: parameter is not primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            this.gsmSCFAddress = new ISDNAddressStringImpl();
            ((ISDNAddressStringImpl) this.gsmSCFAddress).decodeAll(ais);
            break;
          case _TAG_MODIFICATION_REQUEST_FOR_CF_INFO:
            // modificationRequestFor-CF-Info [2] ModificationRequestFor-CF-Info OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modificationRequestForCFInfo: parameter is primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            this.modificationRequestForCFInfo = new ModificationRequestForCFInfoImpl();
            ((ModificationRequestForCFInfoImpl) this.modificationRequestForCFInfo).decodeAll(ais);
            break;
          case _TAG_MODIFICATION_REQUEST_FOR_CB_INFO:
            // modificationRequestFor-CB-Info [3] ModificationRequestFor-CB-Info OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modificationRequestForCBInfo: parameter is primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            this.modificationRequestForCBInfo = new ModificationRequestForCBInfoImpl();
            ((ModificationRequestForCBInfoImpl) this.modificationRequestForCBInfo).decodeAll(ais);
            break;
          case _TAG_MODIFICATION_REQUEST_FOR_CSI:
            // modificationRequestFor-CSI [4] ModificationRequestFor-CSI OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modificationRequestForCSI: parameter is primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            this.modificationRequestForCSI = new ModificationRequestForCSIImpl();
            ((ModificationRequestForCSIImpl) this.modificationRequestForCSI).decodeAll(ais);
            break;
          case _TAG_EXTENSION_CONTAINER:
            // extensionContainer [5] ExtensionContainer OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".extensionContainer: parameter is primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            this.extensionContainer = new MAPExtensionContainerImpl();
            ((MAPExtensionContainerImpl) extensionContainer).decodeAll(ais);
            break;
          case _TAG_LONG_FTN_SUPPORTED:
            // longFTN-Supported [6] NULL OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".longFTNSupported: parameter is not primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            ais.readNull();
            this.longFTNSupported = true;
            break;
          case _TAG_MODIFICATION_REQUEST_FOR_ODB_DATA:
            // modificationRequestFor-ODB-data [7] ModificationRequestFor-ODB-data
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modificationRequestForODBData: parameter is primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            this.modificationRequestForODBData = new ModificationRequestForODBDataImpl();
            ((ModificationRequestForODBDataImpl) this.modificationRequestForODBData).decodeAll(ais);
            break;
          case _TAG_MODIFICATION_REQUEST_FOR_IP_SM_GW_DATA:
            // modificationRequestFor-IP-SM-GW-Data [8] ModificationRequestFor-IP-SM-GW-Data
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modificationRequestForIPSMGWData: parameter is primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            this.modificationRequestForIPSMGWData = new ModificationRequestForIPSMGWDataImpl();
            ((ModificationRequestForIPSMGWDataImpl) this.modificationRequestForIPSMGWData).decodeAll(ais);
            break;
          case _TAG_ACTIVATION_REQUEST_FOR_UE_REACHABILITY:
            // activationRequestForUE-reachability [9] RequestedServingNode OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".activationRequestForUEReachability: parameter is not primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            this.activationRequestForUEReachability = new RequestedServingNodeImpl();
            ((RequestedServingNodeImpl) this.activationRequestForUEReachability).decodeAll(ais);
            break;
          case _TAG_MODIFICATION_REQUEST_FOR_CSG:
            // modificationRequestFor-CSG [10] ModificationRequestFor-CSG OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modificationRequestForCSG: parameter is primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            this.modificationRequestForCSG = new ModificationRequestForCSGImpl();
            ((ModificationRequestForCSGImpl) this.modificationRequestForCSG).decodeAll(ais);
            break;
          case _TAG_MODIFICATION_REQUEST_FOR_CW_DATA:
            // modificationRequestFor-CW-Data [11] ModificationRequestFor-CW-Info OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modificationRequestForCWData: parameter is primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            this.modificationRequestForCWData = new ModificationRequestForCWInfoImpl();
            ((ModificationRequestForCWInfoImpl) this.modificationRequestForCWData).decodeAll(ais);
            break;
          case _TAG_MODIFICATION_REQUEST_FOR_CLIP_INFO:
            // modificationRequestFor-CLIP-Data [12] ModificationRequestFor-CLIP-Info OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modificationRequestForCLIPData: parameter is not primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            this.modificationRequestForCLIPData = new ModificationRequestForCLIPInfoImpl();
            ((ModificationRequestForCLIPInfoImpl) this.modificationRequestForCLIPData).decodeAll(ais);
            break;
          case _TAG_MODIFICATION_REQUEST_FOR_CLIR_INFO:
            // modificationRequestFor-CLIR-Data [13] ModificationRequestFor-CLIR-Info OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modificationRequestForCLIRData: parameter is not primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            this.modificationRequestForCLIRData = new ModificationRequestForCLIRInfoImpl();
            ((ModificationRequestForCLIRInfoImpl) this.modificationRequestForCLIRData).decodeAll(ais);
            break;
          case _TAG_MODIFICATION_REQUEST_FOR_HOLD_DATA:
            // modificationRequestFor-HOLD-Data [14] ModificationRequestFor-CH-Info OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modificationRequestForHOLDData: parameter is primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            this.modificationRequestForHOLDData = new ModificationRequestForCHInfoImpl();
            ((ModificationRequestForCHInfoImpl) this.modificationRequestForHOLDData).decodeAll(ais);
            break;
          case _TAG_MODIFICATION_REQUEST_FOR_ECT_DATA:
            // modificationRequestFor-ECT-Data [15] ModificationRequestFor-ECT-Info OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modificationRequestForECTData: parameter is primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);
            this.modificationRequestForECTData = new ModificationRequestForECTInfoImpl();
            ((ModificationRequestForECTInfoImpl) this.modificationRequestForECTData).decodeAll(ais);
            break;

          default:
            ais.advanceElement();
            break;
        }
      } else {
        ais.advanceElement();
      }
    }

    if (this.subscriberIdentity == null || this.gsmSCFAddress == null)
      throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
          + ": subscriberIdentity and gsmSCFAddress parameters are mandatory but not all of them were found",
          MAPParsingComponentExceptionReason.MistypedParameter);
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
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#encodeData (org.mobicents.protocols.asn.AsnOutputStream)
   */
  public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {

    boolean modificationRequest = false;

    try {
        if (this.subscriberIdentity == null)
            throw new MAPException("Error while encoding " + _PrimitiveName + " the mandatory parameter subscriberIdentity is not defined");

        if (this.gsmSCFAddress == null)
            throw new MAPException("Error while encoding " + _PrimitiveName + " the mandatory parameter gsmSCF-Address is not defined");

        try {
            asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_SUBSCRIBER_IDENTITY);
            int pos = asnOutputStream.StartContentDefiniteLength();
            ((SubscriberIdentityImpl) this.subscriberIdentity).encodeAll(asnOutputStream);
            asnOutputStream.FinalizeContent(pos);
        } catch (AsnException e) {
            throw new MAPException("AsnException while encoding " + _PrimitiveName
                    + " parameter subscriberIdentity [0] SubscriberIdentity");
        }

        ((ISDNAddressStringImpl) this.gsmSCFAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_GSM_SCF_ADDRESS);

        if (this.modificationRequestForCFInfo != null) {
            ((ModificationRequestForCFInfoImpl) this.modificationRequestForCFInfo).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                    _TAG_MODIFICATION_REQUEST_FOR_CF_INFO);
            modificationRequest = true;
        }

        if (this.modificationRequestForCBInfo != null) {
            if (modificationRequest) {
                throw new MAPException("Error when encoding " + _PrimitiveName + " parameter modificationRequestForCBInfo: " +
                        "if this parameter is present then other modification requests shall not be present, " +
                        "but modificationRequestForCFInfo is already present ");
            } else {
                ((ModificationRequestForCBInfoImpl) this.modificationRequestForCBInfo).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_MODIFICATION_REQUEST_FOR_CB_INFO);
                modificationRequest = true;
            }
        }

        if (this.modificationRequestForCSI != null) {
            if (modificationRequest) {
                throw new MAPException("Error when encoding " + _PrimitiveName + " parameter modificationRequestForCSI: " +
                        "if this parameter is present then other modification requests shall not be present, but there is another one already");
            } else {
                ((ModificationRequestForCSIImpl) this.modificationRequestForCSI).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_MODIFICATION_REQUEST_FOR_CSI);
                modificationRequest = true;
            }
        }

        if (this.extensionContainer != null)
            ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_EXTENSION_CONTAINER);

        if (longFTNSupported) {
            try {
                asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_LONG_FTN_SUPPORTED);
            } catch (IOException e) {
                throw new MAPException("IOException when encoding " + _PrimitiveName + " parameter longFTNSupported: " + e.getMessage(), e);
            } catch (AsnException e) {
                throw new MAPException("AsnException when encoding " + _PrimitiveName + " parameter longFTNSupported: " + e.getMessage(), e);
            }
        }

        if (this.modificationRequestForODBData != null) {
            if (modificationRequest) {
                throw new MAPException("Error when encoding " + _PrimitiveName + " parameter modificationRequestForODBData: " +
                        "if this parameter is present then other modification requests shall not be present, but there is another one already");
            } else {
                ((ModificationRequestForODBDataImpl) this.modificationRequestForODBData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_MODIFICATION_REQUEST_FOR_ODB_DATA);
                modificationRequest = true;
            }
        }

        if (this.modificationRequestForIPSMGWData != null) {
            if (modificationRequest) {
                throw new MAPException("Error when encoding " + _PrimitiveName + " parameter modificationRequestForIPSMGWData: " +
                        "if this parameter is present then other modification requests shall not be present, but there is another one already");
            } else {
                ((ModificationRequestForIPSMGWDataImpl) this.modificationRequestForIPSMGWData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_MODIFICATION_REQUEST_FOR_IP_SM_GW_DATA);
                modificationRequest = true;
            }
        }

        if (this.activationRequestForUEReachability != null)
            ((RequestedServingNodeImpl) this.activationRequestForUEReachability).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                    _TAG_ACTIVATION_REQUEST_FOR_UE_REACHABILITY);

        if (this.modificationRequestForCSG != null) {
            if (modificationRequest) {
                throw new MAPException("Error when encoding " + _PrimitiveName + " parameter modificationRequestForCSG: " +
                        "if this parameter is present then other modification requests shall not be present, but there is another one already");
            } else {
                ((ModificationRequestForCSGImpl) this.modificationRequestForCSG).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_MODIFICATION_REQUEST_FOR_CSG);
                modificationRequest = true;
            }
        }

        if (this.modificationRequestForCWData != null) {
            if (modificationRequest) {
                throw new MAPException("Error when encoding " + _PrimitiveName + " parameter modificationRequestForCWData: " +
                        "if this parameter is present then other modification requests shall not be present, but there is another one already");
            } else {
                ((ModificationRequestForCWInfoImpl) this.modificationRequestForCWData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_MODIFICATION_REQUEST_FOR_CW_DATA);
                modificationRequest = true;
            }
        }

        if (this.modificationRequestForCLIPData != null) {
            if (modificationRequest) {
                throw new MAPException("Error when encoding " + _PrimitiveName + " parameter modificationRequestForCLIPData: " +
                        "if this parameter is present then other modification requests shall not be present, but there is another one already");
            } else {
                ((ModificationRequestForCLIPInfoImpl) this.modificationRequestForCLIPData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_MODIFICATION_REQUEST_FOR_CLIP_INFO);
                modificationRequest = true;
            }
        }

        if (this.modificationRequestForCLIRData != null) {
            if (modificationRequest) {
                throw new MAPException("Error when encoding " + _PrimitiveName + " parameter modificationRequestForCLIRData: " +
                        "if this parameter is present then other modification requests shall not be present, but there is another one already");
            } else {
                ((ModificationRequestForCLIRInfoImpl) this.modificationRequestForCLIRData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_MODIFICATION_REQUEST_FOR_CLIR_INFO);
                modificationRequest = true;
            }
        }

        if (this.modificationRequestForHOLDData != null) {
            if (modificationRequest) {
                throw new MAPException("Error when encoding " + _PrimitiveName + " parameter modificationRequestForHOLDData: " +
                        "if this parameter is present then other modification requests shall not be present, but there is another one already");
            } else {
                ((ModificationRequestForCHInfoImpl) this.modificationRequestForHOLDData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_MODIFICATION_REQUEST_FOR_HOLD_DATA);
                modificationRequest = true;
            }
        }

        if (this.modificationRequestForECTData != null) {
            if (modificationRequest) {
                throw new MAPException("Error when encoding " + _PrimitiveName + " parameter modificationRequestForECTData: " +
                        "if this parameter is present then other modification requests shall not be present, but there is another one already");
            } else {
                ((ModificationRequestForECTInfoImpl) this.modificationRequestForECTData).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
                        _TAG_MODIFICATION_REQUEST_FOR_ECT_DATA);
            }
        }

    } catch (MAPException e) {
        throw new MAPException("Exception when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
    }

  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_PrimitiveName);
    sb.append(" [");

    if (this.subscriberIdentity != null) {
      sb.append("subscriberIdentity=");
      sb.append(this.subscriberIdentity);
    }
    if (this.gsmSCFAddress != null) {
      sb.append(", gsmSCFAddress=");
      sb.append(this.gsmSCFAddress);
    }
    if (this.modificationRequestForCFInfo != null) {
      sb.append(", modificationRequestForCFInfo=");
      sb.append(this.modificationRequestForCFInfo);
    }
    if (this.modificationRequestForCBInfo != null) {
      sb.append(", modificationRequestForCBInfo=");
      sb.append(this.modificationRequestForCBInfo);
    }
    if (this.modificationRequestForCSI != null) {
      sb.append(", modificationRequestForCSI=");
      sb.append(this.modificationRequestForCSI);
    }
    if (this.extensionContainer != null) {
      sb.append(", extensionContainer=");
      sb.append(this.extensionContainer);
    }
    if (longFTNSupported) {
      sb.append(", longFTNSupported");
    }
    if (this.modificationRequestForODBData != null) {
      sb.append(", modificationRequestForODBData=");
      sb.append(this.modificationRequestForODBData);
    }
    if (this.modificationRequestForIPSMGWData != null) {
      sb.append(", modificationRequestForIPSMGWData=");
      sb.append(this.modificationRequestForIPSMGWData);
    }
    if (this.activationRequestForUEReachability != null) {
      sb.append(", activationRequestForUEReachability=");
      sb.append(this.activationRequestForUEReachability);
    }
    if (this.modificationRequestForCSG != null) {
      sb.append(", modificationRequestForCSG=");
      sb.append(this.modificationRequestForCSG);
    }
    if (this.modificationRequestForCWData != null) {
      sb.append(", modificationRequestForCWData=");
      sb.append(this.modificationRequestForCWData);
    }
    if (this.modificationRequestForCLIPData != null) {
      sb.append(", modificationRequestForCLIPData=");
      sb.append(this.modificationRequestForCLIPData);
    }
    if (this.modificationRequestForCLIRData != null) {
      sb.append(", modificationRequestForCLIRData=");
      sb.append(this.modificationRequestForCLIRData);
    }
    if (this.modificationRequestForCLIRData != null) {
      sb.append(", modificationRequestForCLIRData=");
      sb.append(this.modificationRequestForCLIRData);
    }
    if (this.modificationRequestForHOLDData != null) {
      sb.append(", modificationRequestForHOLDData=");
      sb.append(this.modificationRequestForHOLDData);
    }
    if (this.modificationRequestForECTData != null) {
      sb.append(", modificationRequestForECTData=");
      sb.append(this.modificationRequestForECTData);
    }

    sb.append("]");
    return sb.toString();
  }

}
