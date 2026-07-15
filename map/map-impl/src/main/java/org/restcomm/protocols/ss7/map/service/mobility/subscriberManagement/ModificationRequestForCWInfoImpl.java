package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationInstruction;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForCWInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBasicServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtSSStatus;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ModificationRequestForCWInfoImpl extends SequenceBase implements ModificationRequestForCWInfo {

  private static final int _TAG_BASIC_SERVICE = 0;
  private static final int _TAG_SS_STATUS = 1;
  private static final int _TAG_MODIFY_NOTIFICATION_TO_CSE = 2;
  private static final int _TAG_EXTENSION_CONTAINER = 3;

  public static final String _PrimitiveName = "ModificationRequestForCWInfo";

  private ExtBasicServiceCode basicService;
  private ExtSSStatus ssStatus;
  private ModificationInstruction modifyNotificationToCSE;
  private MAPExtensionContainer extensionContainer;

  public ModificationRequestForCWInfoImpl() {
    super(_PrimitiveName);
  }

  public ModificationRequestForCWInfoImpl(ExtBasicServiceCode basicService, ExtSSStatus ssStatus,
          ModificationInstruction modifyNotificationToCSE, MAPExtensionContainer extensionContainer) {
    super(_PrimitiveName);
    this.basicService = basicService;
    this.ssStatus = ssStatus;
    this.modifyNotificationToCSE = modifyNotificationToCSE;
    this.extensionContainer = extensionContainer;
  }

  @Override
  public ExtBasicServiceCode getBasicService() {
    return this.basicService;
  }

  @Override
  public ExtSSStatus getSsStatus() {
    return this.ssStatus;
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
    this.basicService = null;
    this.ssStatus = null;
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
          case _TAG_BASIC_SERVICE:
            // basicService [0] Ext-BasicServiceCode OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".basicService: parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            AsnInputStream ais2 = ais.readSequenceStream();
            ais2.readTag();
            this.basicService = new ExtBasicServiceCodeImpl();
            ((ExtBasicServiceCodeImpl) this.basicService).decodeAll(ais2);
            break;
          case _TAG_SS_STATUS:
            // ss-Status [1] Ext-SS-Status OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".ssStatus: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.ssStatus = new ExtSSStatusImpl();
            ((ExtSSStatusImpl) this.ssStatus).decodeAll(ais);
            break;
          case _TAG_MODIFY_NOTIFICATION_TO_CSE:
            // modifyNotificationToCSE [2] ModificationInstruction OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modifyNotificationToCSE: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.modifyNotificationToCSE = ModificationInstruction.getInstance((int) ais.readInteger());
            break;
          case _TAG_EXTENSION_CONTAINER:
            // extensionContainer [3] ExtensionContainer OPTIONAL
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
  }

  @Override
  public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
    try {
        if (this.basicService != null) {
            asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_BASIC_SERVICE);
            int pos = asnOutputStream.StartContentDefiniteLength();
            ((ExtBasicServiceCodeImpl) this.basicService).encodeAll(asnOutputStream);
            asnOutputStream.FinalizeContent(pos);
        }

        if (this.ssStatus != null)
            ((ExtSSStatusImpl) this.ssStatus).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SS_STATUS);

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

    if (this.basicService != null) {
      sb.append("basicService=");
      sb.append(this.basicService);
    }
    if (this.ssStatus != null) {
      sb.append(", ssStatus=");
      sb.append(this.ssStatus);
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
