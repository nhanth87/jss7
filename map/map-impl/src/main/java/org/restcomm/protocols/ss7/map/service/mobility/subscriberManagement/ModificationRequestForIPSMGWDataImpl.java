package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.NetworkNodeDiameterAddress;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationInstruction;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForIPSMGWData;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.NetworkNodeDiameterAddressImpl;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ModificationRequestForIPSMGWDataImpl extends SequenceBase implements ModificationRequestForIPSMGWData {

  private static final int _TAG_MODIFY_REGISTRATION_STATUS = 0;
  private static final int _TAG_EXTENSION_CONTAINER = 1;
  private static final int _TAG_IP_SM_GW_DIAMETER_ADDRESS = 2;

  public static final String _PrimitiveName = "ModificationRequestForODBData";

  private ModificationInstruction modifyRegistrationStatus;
  private MAPExtensionContainer extensionContainer;
  private NetworkNodeDiameterAddress ipSmGwDiameterAddress;

  public ModificationRequestForIPSMGWDataImpl() {
    super(_PrimitiveName);
  }

  public ModificationRequestForIPSMGWDataImpl(ModificationInstruction modifyRegistrationStatus, MAPExtensionContainer extensionContainer,
          NetworkNodeDiameterAddress ipSmGwDiameterAddress) {
    super(_PrimitiveName);
    this.modifyRegistrationStatus = modifyRegistrationStatus;
    this.extensionContainer = extensionContainer;
    this.ipSmGwDiameterAddress = ipSmGwDiameterAddress;
  }

  @Override
  public ModificationInstruction getModifyRegistrationStatus() {
    return this.modifyRegistrationStatus;
  }

  @Override
  public MAPExtensionContainer getExtensionContainer() {
    return this.extensionContainer;
  }

  @Override
  public NetworkNodeDiameterAddress getIpSmGwDiameterAddress() {
    return this.ipSmGwDiameterAddress;
  }

  @Override
  protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
    this.modifyRegistrationStatus = null;
    this.extensionContainer = null;
    this.ipSmGwDiameterAddress = null;

    AsnInputStream ais = asnInputStream.readSequenceStreamData(length);

    while (true) {
      if (ais.available() == 0) {
        break;
      }

      int tag = ais.readTag();
      if (ais.getTagClass() == Tag.CLASS_CONTEXT_SPECIFIC) {
        switch (tag) {
          case _TAG_MODIFY_REGISTRATION_STATUS:
            // modifyRegistrationStatus [0] ModificationInstruction OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modifyRegistrationStatus: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.modifyRegistrationStatus = ModificationInstruction.getInstance((int) ais.readInteger());
            break;
          case _TAG_EXTENSION_CONTAINER:
            // extensionContainer [1] ExtensionContainer OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".extensionContainer: parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.extensionContainer = new MAPExtensionContainerImpl();
            ((MAPExtensionContainerImpl) this.extensionContainer).decodeAll(ais);
            break;
          case _TAG_IP_SM_GW_DIAMETER_ADDRESS:
            // ip-sm-gw-DiameterAddress [2] NetworkNodeDiameterAddress OPTIONAL
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".extensionContainer: parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.ipSmGwDiameterAddress = new NetworkNodeDiameterAddressImpl();
            ((NetworkNodeDiameterAddressImpl) this.ipSmGwDiameterAddress).decodeAll(ais);
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

    if (this.modifyRegistrationStatus != null) {
      try {
        asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_MODIFY_REGISTRATION_STATUS, this.modifyRegistrationStatus.getCode());
      } catch (IOException e) {
        throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter modifyRegistrationStatus", e);
      } catch (AsnException e) {
        throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter modifyRegistrationStatus", e);
      }
    }

    if (this.extensionContainer != null)
      ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_EXTENSION_CONTAINER);

    if (this.ipSmGwDiameterAddress != null) {
      if (this.modifyRegistrationStatus != null) {
        if (this.modifyRegistrationStatus.getCode() != 1) {
          throw new MAPException("Error while encoding " + _PrimitiveName + " parameter ipSmGwDiameterAddress " +
              "may be present when modifyRegistrationStatus is \"activate\"");
        } else {
          ((NetworkNodeDiameterAddressImpl) this.ipSmGwDiameterAddress).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC,
              _TAG_IP_SM_GW_DIAMETER_ADDRESS);
        }
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_PrimitiveName);
    sb.append(" [");

    if (this.modifyRegistrationStatus != null) {
      sb.append("modifyRegistrationStatus=");
      sb.append(this.modifyRegistrationStatus);
    }
    if (this.extensionContainer != null) {
      sb.append(", extensionContainer=");
      sb.append(this.extensionContainer);
    }
    if (this.ipSmGwDiameterAddress != null) {
      sb.append(", ipSmGwDiameterAddress=");
      sb.append(this.ipSmGwDiameterAddress);
    }

    sb.append("]");
    return sb.toString();
  }
}
