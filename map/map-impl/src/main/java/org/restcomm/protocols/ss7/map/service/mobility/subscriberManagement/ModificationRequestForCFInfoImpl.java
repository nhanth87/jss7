package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNSubaddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationInstruction;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForCFInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBasicServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtSSStatus;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSCode;
import org.restcomm.protocols.ss7.map.primitives.AddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNSubaddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;
import org.restcomm.protocols.ss7.map.service.supplementary.SSCodeImpl;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ModificationRequestForCFInfoImpl extends SequenceBase implements ModificationRequestForCFInfo {

  private static final int _TAG_SS_CODE = 0;
  private static final int _TAG_BASIC_SERVICE = 1;
  private static final int _TAG_SS_STATUS = 2;
  private static final int _TAG_FORWARDED_TO_NUMBER = 3;
  private static final int _TAG_FORWARDED_TO_SUB_ADDRESS = 4;
  private static final int _TAG_NO_REPLY_CONDITION_TIME = 5;
  private static final int _TAG_MODIFY_NOTIFICATION_TO_CSE = 6;
  private static final int _TAG_EXTENSION_CONTAINER = 7;

  public static final String _PrimitiveName = "ModificationRequestForCFInfo";

  private SSCode ssCode;
  private ExtBasicServiceCode basicService;
  private ExtSSStatus ssStatus;
  private AddressString forwardedToNumber;
  private ISDNSubaddressString forwardedToSubaddress;
  private Integer noReplyConditionTime;
  private ModificationInstruction modifyNotificationToCSE;
  private MAPExtensionContainer extensionContainer;


  public ModificationRequestForCFInfoImpl() {
    super(_PrimitiveName);
  }

  public ModificationRequestForCFInfoImpl(SSCode ssCode, ExtBasicServiceCode basicService,
          ExtSSStatus ssStatus, AddressString forwardedToNumber, ISDNSubaddressString forwardedToSubaddress,
          Integer noReplyConditionTime, ModificationInstruction modifyNotificationToCSE, MAPExtensionContainer extensionContainer) {
    super(_PrimitiveName);
    this.ssCode = ssCode;
    this.basicService = basicService;
    this.ssStatus = ssStatus;
    this.forwardedToNumber = forwardedToNumber;
    this.forwardedToSubaddress = forwardedToSubaddress;
    this.noReplyConditionTime = noReplyConditionTime;
    this.modifyNotificationToCSE = modifyNotificationToCSE;
    this.extensionContainer = extensionContainer;
  }

  @Override
  public SSCode getSsCode() {
    return this.ssCode;
  }

  public ExtBasicServiceCode getBasicService() {
    return this.basicService;
  }

  @Override
  public ExtSSStatus getSsStatus() {
    return this.ssStatus;
  }

  @Override
  public AddressString getForwardedToNumber() {
    return this.forwardedToNumber;
  }

  @Override
  public ISDNSubaddressString getForwardedToSubaddress() {
    return this.forwardedToSubaddress;
  }

  @Override
  public Integer getNoReplyConditionTime() {
    return this.noReplyConditionTime;
  }

  @Override
  public ModificationInstruction getModifyNotificationToCSE() {
    return this.modifyNotificationToCSE;
  }

  @Override
  public MAPExtensionContainer getExtensionContainer() {
    return this.extensionContainer;
  }

  @Override
  protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {

    this.ssCode = null;
    this.basicService = null;
    this.ssStatus = null;
    this.forwardedToNumber = null;
    this.forwardedToSubaddress = null;
    this.noReplyConditionTime = null;
    this.modifyNotificationToCSE = null;
    this.extensionContainer = null;

    AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

    while (true) {
      if (ais.available() == 0) {
        break;
      }

      int tag = ais.readTag();
      if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
        switch (tag) {
          case _TAG_SS_CODE:
            // ss-Code [0] SS-Code
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".ssCode: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.ssCode = new SSCodeImpl();
            ((SSCodeImpl) this.ssCode).decodeAll(ais);
            break;
          case _TAG_BASIC_SERVICE:
            // basicService [1] Ext-BasicServiceCode OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".basicService: parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            AsnInputStream ais2 = ais.readSequenceStream();
            ais2.readTag();
            this.basicService = new ExtBasicServiceCodeImpl();
            ((ExtBasicServiceCodeImpl) this.basicService).decodeAll(ais2);
            break;
          case _TAG_SS_STATUS:
            // ss-Status [2] Ext-SS-Status OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".ssStatus: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.ssStatus = new ExtSSStatusImpl();
            ((ExtSSStatusImpl) this.ssStatus).decodeAll(ais);
            break;
          case _TAG_FORWARDED_TO_NUMBER:
            // forwardedToNumber [3] AddressString OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".forwardedToNumber: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.forwardedToNumber = new AddressStringImpl();
            ((AddressStringImpl) this.forwardedToNumber).decodeAll(ais);
            break;
          case _TAG_FORWARDED_TO_SUB_ADDRESS:
            // forwardedToSubaddress [4] ISDN-SubaddressString OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".forwardedToSubaddress: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.forwardedToSubaddress = new ISDNSubaddressStringImpl();
            ((ISDNSubaddressStringImpl) this.forwardedToSubaddress).decodeAll(ais);
            break;
          case _TAG_NO_REPLY_CONDITION_TIME:
            // noReplyConditionTime [5] Ext-NoRepCondTime OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".noReplyConditionTime: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.noReplyConditionTime = (int) ais.readInteger();
            if (this.noReplyConditionTime < 1 || this.noReplyConditionTime > 100) {
              // Ext-NoRepCondTime ::= INTEGER (1..100)
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ": parameter noReplyConditionTime must be from 1 to 100, parsed: " + this.noReplyConditionTime,
                  MAPParsingComponentExceptionReason.MistypedParameter);
            } else {
              // Only values 5-30 are used.
              // Values in the ranges 1-4 and 31-100 are reserved for future use
              if (this.noReplyConditionTime <= 4)
                this.noReplyConditionTime = 5; // values 1-4 shall be mapped on to value 5
              if (this.noReplyConditionTime > 30)
                this.noReplyConditionTime = 30; // values 31-100 shall be mapped on to value 30
            }
            break;
          case _TAG_MODIFY_NOTIFICATION_TO_CSE:
            // modifyNotificationToCSE [6] ModificationInstruction OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modifyNotificationToCSE: Parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.modifyNotificationToCSE = ModificationInstruction.getInstance((int) ais.readInteger());
            break;
          case _TAG_EXTENSION_CONTAINER:
            // extensionContainer [7] ExtensionContainer OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".extensionContainer: parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.extensionContainer = new MAPExtensionContainerImpl();
            ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
            break;

          default:
            ais.advanceElement();
            break;
        }
      } else {
        ais.advanceElement();
      }
    }

    if (this.ssCode == null) {
      throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
          + ": ssCode is mandatory but it is absent", MAPParsingComponentExceptionReason.MistypedParameter);
    }
  }

  @Override
  public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
    try {
      if (this.ssCode == null) {
        throw new MAPException("Error while encoding " + _PrimitiveName + ": mandatory parameter ssCode is absent");
      } else {
        ((SSCodeImpl) this.ssCode).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SS_CODE);
      }

      if (this.basicService != null) {
          asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_BASIC_SERVICE);
          int pos = asnOutputStream.StartContentDefiniteLength();
          ((ExtBasicServiceCodeImpl) this.basicService).encodeAll(asnOutputStream);
          asnOutputStream.FinalizeContent(pos);
      }

      if (this.ssStatus != null)
        ((ExtSSStatusImpl) this.ssStatus).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SS_STATUS);

      if (this.forwardedToNumber != null)
        ((AddressStringImpl) this.forwardedToNumber).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_FORWARDED_TO_NUMBER);

      if (this.forwardedToSubaddress != null)
        ((ISDNSubaddressStringImpl) this.forwardedToSubaddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_FORWARDED_TO_SUB_ADDRESS);

      if (this.noReplyConditionTime != null) {
        if (this.noReplyConditionTime < 1 || this.noReplyConditionTime > 100)
          throw new MAPException("istAlertTimer must be from 1 to 100, found: " + this.noReplyConditionTime);
        try {
          asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_NO_REPLY_CONDITION_TIME, this.noReplyConditionTime);
        } catch (IOException e) {
          throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter noReplyConditionTime", e);
        } catch (AsnException e) {
          throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter noReplyConditionTime", e);
        }
      }

      if (this.modifyNotificationToCSE != null) {
        try {
          asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_MODIFY_NOTIFICATION_TO_CSE, this.modifyNotificationToCSE.getCode());
        } catch (IOException e) {
          throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter modifyNotificationToCSE", e);
        } catch (AsnException e) {
          throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter modifyNotificationToCSE", e);
        }
      }

      if (this.extensionContainer != null)
        ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_EXTENSION_CONTAINER);

    } catch (AsnException e) {
        throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
    } catch (MAPException e) {
        throw new MAPException("Exception when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_PrimitiveName);
    sb.append(" [");

    if (this.ssCode != null) {
      sb.append("ssCode=");
      sb.append(this.ssCode);
    }
    if (this.basicService != null) {
      sb.append(", basicService=");
      sb.append(this.basicService);
    }
    if (this.ssStatus != null) {
      sb.append(", ssStatus=");
      sb.append(this.ssStatus);
    }
    if (this.forwardedToNumber != null) {
      sb.append(", forwardedToNumber=");
      sb.append(this.forwardedToNumber);
    }
    if (this.forwardedToSubaddress != null) {
      sb.append(", forwardedToSubaddress=");
      sb.append(this.forwardedToSubaddress);
    }
    if (this.noReplyConditionTime != null) {
      sb.append(", noReplyConditionTime=");
      sb.append(this.noReplyConditionTime);
    }
    if (this.modifyNotificationToCSE != null) {
      sb.append(", modifyNotificationToCSE=");
      sb.append(this.modifyNotificationToCSE);
    }
    if (this.extensionContainer != null) {
      sb.append(", extensionContainer=");
      sb.append(this.extensionContainer);
    }

    sb.append("]");
    return sb.toString();
  }
}
