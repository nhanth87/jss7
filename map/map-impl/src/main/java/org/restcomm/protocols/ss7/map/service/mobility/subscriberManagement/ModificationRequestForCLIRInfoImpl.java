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
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForCLIRInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtSSStatus;
import org.restcomm.protocols.ss7.map.api.service.supplementary.CliRestrictionOption;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ModificationRequestForCLIRInfoImpl extends SequenceBase implements ModificationRequestForCLIRInfo {

  private static final int _TAG_SS_STATUS = 0;
  private static final int _TAG_CLI_RESTRICTION_OPTION = 1;
  private static final int _TAG_MODIFY_NOTIFICATION_TO_CSE = 2;
  private static final int _TAG_EXTENSION_CONTAINER = 3;

  public static final String _PrimitiveName = "ModificationRequestForCLIRInfo";

  private ExtSSStatus ssStatus;
  private CliRestrictionOption cliRestrictionOption;
  private ModificationInstruction modifyNotificationToCSE;
  private MAPExtensionContainer extensionContainer;

  public ModificationRequestForCLIRInfoImpl() {
    super(_PrimitiveName);
  }

  public ModificationRequestForCLIRInfoImpl(ExtSSStatus ssStatus, CliRestrictionOption cliRestrictionOption,
          ModificationInstruction modifyNotificationToCSE, MAPExtensionContainer extensionContainer) {
    super(_PrimitiveName);
    this.ssStatus = ssStatus;
    this.cliRestrictionOption = cliRestrictionOption;
    this.modifyNotificationToCSE = modifyNotificationToCSE;
    this.extensionContainer = extensionContainer;
  }

  @Override
  public ExtSSStatus getSsStatus() {
    return this.ssStatus;
  }

  @Override
  public CliRestrictionOption getCliRestrictionOption() {
    return this.cliRestrictionOption;
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
    this.ssStatus = null;
    this.cliRestrictionOption = null;
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
          case _TAG_SS_STATUS:
            // ss-Status [0] Ext-SS-Status OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".ssStatus: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.ssStatus = new ExtSSStatusImpl();
            ((ExtSSStatusImpl) this.ssStatus).decodeAll(ais);
            break;
          case _TAG_CLI_RESTRICTION_OPTION:
            // cliRestrictionOption [1] CliRestrictionOption OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".cliRestrictionOption: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.cliRestrictionOption = CliRestrictionOption.getInstance((int) ais.readInteger());
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
      if (this.ssStatus != null)
        ((ExtSSStatusImpl) this.ssStatus).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SS_STATUS);

      if (this.cliRestrictionOption != null) {
        try {
          asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_CLI_RESTRICTION_OPTION, this.cliRestrictionOption.getCode());
        } catch (IOException e) {
          throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter cliRestrictionOption", e);
        } catch (AsnException e) {
          throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter cliRestrictionOption", e);
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

    } catch (MAPException e) {
      throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_PrimitiveName);
    sb.append(" [");

    if (this.ssStatus != null) {
      sb.append("ssStatus=");
      sb.append(this.ssStatus);
    }
    if (this.cliRestrictionOption != null) {
      sb.append(", cliRestrictionOption=");
      sb.append(this.cliRestrictionOption);
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
