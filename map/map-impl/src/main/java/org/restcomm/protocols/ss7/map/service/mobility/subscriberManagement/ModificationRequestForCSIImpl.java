package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AdditionalRequestedCAMELSubscriptionInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationInstruction;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForCSI;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.RequestedCAMELSubscriptionInfo;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ModificationRequestForCSIImpl extends SequenceBase implements ModificationRequestForCSI {

  private static final int _TAG_REQUESTED_CAMEL_SUBSCRIPTION_INFO = 0;
  private static final int _TAG_MODIFY_NOTIFICATION_TO_CSE = 1;
  private static final int _TAG_MODIFY_CSI_STATE = 2;
  private static final int _TAG_EXTENSION_CONTAINER = 3;
  private static final int _TAG_ADDITIONAL_REQUESTED_CAMEL_SUBSCRIPTION_INFO = 4;

  public static final String _PrimitiveName = "ModificationRequestForCSI";

  private RequestedCAMELSubscriptionInfo requestedCamelSubscriptionInfo;
  private ModificationInstruction modifyNotificationToCSE;
  private ModificationInstruction modifyCSIState;
  private MAPExtensionContainer extensionContainer;
  private AdditionalRequestedCAMELSubscriptionInfo additionalRequestedCamelSubscriptionInfo;

  public ModificationRequestForCSIImpl() {
    super(_PrimitiveName);
  }

  public ModificationRequestForCSIImpl(RequestedCAMELSubscriptionInfo requestedCAMELSubscriptionInfo,
          ModificationInstruction modifyNotificationToCSE, ModificationInstruction modifyCSIState, MAPExtensionContainer extensionContainer,
          AdditionalRequestedCAMELSubscriptionInfo additionalRequestedCAMELSubscriptionInfo) {
    super(_PrimitiveName);
    this.requestedCamelSubscriptionInfo = requestedCAMELSubscriptionInfo;
    this.modifyNotificationToCSE = modifyNotificationToCSE;
    this.modifyCSIState = modifyCSIState;
    this.extensionContainer = extensionContainer;
    this.additionalRequestedCamelSubscriptionInfo = additionalRequestedCAMELSubscriptionInfo;
  }

  @Override
  public RequestedCAMELSubscriptionInfo getRequestedCamelSubscriptionInfo() {
    return this.requestedCamelSubscriptionInfo;
  }

  @Override
  public ModificationInstruction getModifyNotificationToCSE() {
    return this.modifyNotificationToCSE;
  }

  @Override
  public ModificationInstruction getModifyCSIState() {
    return this.modifyCSIState;
  }

  @Override
  public MAPExtensionContainer getExtensionContainer() {
    return this.extensionContainer;
  }

  @Override
  public AdditionalRequestedCAMELSubscriptionInfo getAdditionalRequestedCamelSubscriptionInfo() {
    return this.additionalRequestedCamelSubscriptionInfo;
  }

  @Override
  protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {

    this.requestedCamelSubscriptionInfo = null;
    this.modifyNotificationToCSE = null;
    this.modifyCSIState = null;
    this.extensionContainer = null;
    this.additionalRequestedCamelSubscriptionInfo = null;

    AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

    while (true) {
      if (ais.available() == 0) {
        break;
      }

      int tag = ais.readTag();
      if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
        switch (tag) {
          case _TAG_REQUESTED_CAMEL_SUBSCRIPTION_INFO:
            // requestedCamel-SubscriptionInfo [0] RequestedCAMEL-SubscriptionInfo
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".requestedCamelSubscriptionInfo: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.requestedCamelSubscriptionInfo = RequestedCAMELSubscriptionInfo.getInstance((int) ais.readInteger());
            break;
          case _TAG_MODIFY_NOTIFICATION_TO_CSE:
            // modifyNotificationToCSE [1] ModificationInstruction OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modifyNotificationToCSE: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.modifyNotificationToCSE = ModificationInstruction.getInstance((int) ais.readInteger());
            break;
          case _TAG_MODIFY_CSI_STATE:
            // modifyCSI-State [2] ModificationInstruction OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modifyCSIState: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.modifyCSIState = ModificationInstruction.getInstance((int) ais.readInteger());
            break;
          case _TAG_EXTENSION_CONTAINER:
            // extensionContainer [3] ExtensionContainer OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".extensionContainer: parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.extensionContainer = new MAPExtensionContainerImpl();
            ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
            break;
          case _TAG_ADDITIONAL_REQUESTED_CAMEL_SUBSCRIPTION_INFO:
            // additionalRequestedCAMEL-SubscriptionInfo [4] AdditionalRequestedCAMEL-SubscriptionInfo OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".additionalRequestedCamelSubscriptionInfo: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.additionalRequestedCamelSubscriptionInfo = AdditionalRequestedCAMELSubscriptionInfo.getInstance((int) ais.readInteger());
            break;

          default:
            ais.advanceElement();
            break;
        }
      } else {
        ais.advanceElement();
      }
    }

    if (this.requestedCamelSubscriptionInfo == null) {
      throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
          + ": requestedCamelSubscriptionInfo is mandatory but it is absent", MAPParsingComponentExceptionReason.MistypedParameter);
    }
  }

  @Override
  public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
    try {
      if (this.requestedCamelSubscriptionInfo == null) {
        throw new MAPException("Error while encoding " + _PrimitiveName + ": mandatory parameter requestedCamelSubscriptionInfo is absent");
      } else {
        try {
          asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_REQUESTED_CAMEL_SUBSCRIPTION_INFO, this.requestedCamelSubscriptionInfo.getCode());
        } catch (IOException e) {
          throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter requestedCamelSubscriptionInfo", e);
        } catch (AsnException e) {
          throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter requestedCamelSubscriptionInfo", e);
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

      if (this.modifyCSIState != null) {
        try {
          asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_MODIFY_CSI_STATE, this.modifyCSIState.getCode());
        } catch (IOException e) {
          throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter modifyCSIState", e);
        } catch (AsnException e) {
          throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter modifyCSIState", e);
        }
      }

      if (this.extensionContainer != null)
        ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_EXTENSION_CONTAINER);

      if (additionalRequestedCamelSubscriptionInfo != null) {
        try {
          asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_ADDITIONAL_REQUESTED_CAMEL_SUBSCRIPTION_INFO,
              this.additionalRequestedCamelSubscriptionInfo.getCode());
        } catch (IOException e) {
          throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter additionalRequestedCamelSubscriptionInfo", e);
        } catch (AsnException e) {
          throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter additionalRequestedCamelSubscriptionInfo", e);
        }
      }

    } catch (MAPException e) {
      throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_PrimitiveName);
    sb.append(" [");

    if (this.requestedCamelSubscriptionInfo != null) {
      sb.append("requestedCamelSubscriptionInfo=");
      sb.append(this.requestedCamelSubscriptionInfo);
    }
    if (this.modifyNotificationToCSE != null) {
      sb.append(", modifyNotificationToCSE=");
      sb.append(this.modifyNotificationToCSE);
    }
    if (this.modifyCSIState != null) {
      sb.append(", modifyCSIState=");
      sb.append(this.modifyCSIState);
    }
    if (this.extensionContainer != null) {
      sb.append(", extensionContainer=");
      sb.append(this.extensionContainer);
    }
    if (this.additionalRequestedCamelSubscriptionInfo != null) {
      sb.append(", additionalRequestedCamelSubscriptionInfo=");
      sb.append(this.additionalRequestedCamelSubscriptionInfo);
    }

    sb.append("]");
    return sb.toString();
  }
}
