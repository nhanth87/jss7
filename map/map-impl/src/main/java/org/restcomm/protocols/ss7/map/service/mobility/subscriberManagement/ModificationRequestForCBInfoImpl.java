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
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ModificationRequestForCBInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBasicServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtSSStatus;
import org.restcomm.protocols.ss7.map.api.service.supplementary.Password;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSCode;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;
import org.restcomm.protocols.ss7.map.service.supplementary.PasswordImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.SSCodeImpl;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ModificationRequestForCBInfoImpl extends SequenceBase implements ModificationRequestForCBInfo {

  private static final int _TAG_SS_CODE = 0;
  private static final int _TAG_BASIC_SERVICE = 1;
  private static final int _TAG_SS_STATUS = 2;
  private static final int _TAG_PASSWORD= 3;
  private static final int _TAG_WRONG_PASSWORD_ATTEMPTS_COUNTER = 4;
  private static final int _TAG_MODIFY_NOTIFICATION_TO_CSE = 5;
  private static final int _TAG_EXTENSION_CONTAINER = 6;

  public static final String _PrimitiveName = "ModificationRequestForCBInfo";

  private SSCode ssCode;
  private ExtBasicServiceCode basicService;
  private ExtSSStatus ssStatus;
  private Password password;
  private Integer wrongPasswordAttemptsCounter;
  private ModificationInstruction modifyNotificationToCSE;
  private MAPExtensionContainer extensionContainer;

  public ModificationRequestForCBInfoImpl() {
    super(_PrimitiveName);
  }

  public ModificationRequestForCBInfoImpl(SSCode ssCode, ExtBasicServiceCode basicService, ExtSSStatus ssStatus,
          Password password, Integer wrongPasswordAttemptsCounter, ModificationInstruction modifyNotificationToCSE,
          MAPExtensionContainer extensionContainer) {
    super(_PrimitiveName);
    this.ssCode = ssCode;
    this.basicService = basicService;
    this.ssStatus = ssStatus;
    this.password = password;
    this.wrongPasswordAttemptsCounter = wrongPasswordAttemptsCounter;
    this.modifyNotificationToCSE = modifyNotificationToCSE;
    this.extensionContainer = extensionContainer;
  }

  @Override
  public SSCode getSsCode() {
    return this.ssCode;
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
  public Password getPassword() {
    return this.password;
  }

  @Override
  public Integer getWrongPasswordAttemptsCounter() {
    return this.wrongPasswordAttemptsCounter;
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
    this.password = null;
    this.wrongPasswordAttemptsCounter = null;
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
          case _TAG_PASSWORD:
            // password [3] Password OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".password: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.password = new PasswordImpl();
            ((PasswordImpl) this.password).decodeAll(ais);
            break;
          case _TAG_WRONG_PASSWORD_ATTEMPTS_COUNTER:
            // wrongPasswordAttemptsCounter [4] WrongPasswordAttemptsCounter OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".wrongPasswordAttemptsCounter: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.wrongPasswordAttemptsCounter = (int) ais.readInteger();
            if (this.wrongPasswordAttemptsCounter < 0 || this.wrongPasswordAttemptsCounter > 4) {
              // WrongPasswordAttemptsCounter ::= INTEGER (0..4)
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ": parameter wrongPasswordAttemptsCounter must be from 0 to 4, parsed: " + this.wrongPasswordAttemptsCounter,
                  MAPParsingComponentExceptionReason.MistypedParameter);
            }
            break;
          case _TAG_MODIFY_NOTIFICATION_TO_CSE:
            // modifyNotificationToCSE [6] ModificationInstruction OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".modifyNotificationToCSE: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
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

      if (this.password != null)
        ((PasswordImpl) this.password).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_PASSWORD);

      if (this.wrongPasswordAttemptsCounter != null) {
        if (this.wrongPasswordAttemptsCounter < 0 || this.wrongPasswordAttemptsCounter > 4)
          throw new MAPException("wrongPasswordAttemptsCounter must be from 0 to 4, found: " + this.wrongPasswordAttemptsCounter);
        try {
          asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_WRONG_PASSWORD_ATTEMPTS_COUNTER, this.wrongPasswordAttemptsCounter);
        } catch (IOException e) {
          throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter wrongPasswordAttemptsCounter", e);
        } catch (AsnException e) {
          throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter wrongPasswordAttemptsCounter", e);
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
    if (this.password != null) {
      sb.append(", password=");
      sb.append(this.password);
    }
    if (this.wrongPasswordAttemptsCounter != null) {
      sb.append(", wrongPasswordAttemptsCounter=");
      sb.append(this.wrongPasswordAttemptsCounter);
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
