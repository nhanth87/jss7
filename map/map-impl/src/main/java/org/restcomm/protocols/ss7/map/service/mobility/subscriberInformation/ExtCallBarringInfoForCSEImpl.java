package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ExtCallBarringInfoForCSE;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtCallBarringFeature;
import org.restcomm.protocols.ss7.map.api.service.supplementary.Password;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSCode;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtCallBarringFeatureImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.PasswordImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.SSCodeImpl;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ExtCallBarringInfoForCSEImpl extends SequenceBase implements ExtCallBarringInfoForCSE {

  private static final int _TAG_SS_CODE = 0;
  private static final int _TAG_CALL_BARRING_FEATURE_LIST = 1;
  private static final int _TAG_PASSWORD = 2;
  private static final int _TAG_WRONG_PASSWORD_ATTEMPTS_COUNTER = 3;
  private static final int _TAG_NOTIFICATION_TO_CSE = 4;
  private static final int _TAG_EXTENSION_CONTAINER = 5;

  private static final String _PrimitiveName = "ExtCallBarringInfoForCSE";

  private SSCode ssCode;
  private ArrayList<ExtCallBarringFeature> callBarringFeatureList;
  private Password password;
  private Integer wrongPasswordAttemptsCounter;
  private boolean notificationToCSE;
  private MAPExtensionContainer extensionContainer;

  public ExtCallBarringInfoForCSEImpl() {
    super(_PrimitiveName);
  }

  public ExtCallBarringInfoForCSEImpl(SSCode ssCode, ArrayList<ExtCallBarringFeature> callBarringFeatureList,
          Password password, Integer wrongPasswordAttemptsCounter, boolean notificationToCSE,
          MAPExtensionContainer extensionContainer) {
    super(_PrimitiveName);
    this.ssCode = ssCode;
    this.callBarringFeatureList = callBarringFeatureList;
    this.password = password;
    this.wrongPasswordAttemptsCounter = wrongPasswordAttemptsCounter;
    this.notificationToCSE = notificationToCSE;
    this.extensionContainer = extensionContainer;
  }

  @Override
  public SSCode getSsCode() {
    return this.ssCode;
  }

  @Override
  public ArrayList<ExtCallBarringFeature> getCallBarringFeatureList() {
    return this.callBarringFeatureList;
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
  public boolean getNotificationToCSE() {
    return this.notificationToCSE;
  }

  @Override
  public MAPExtensionContainer getExtensionContainer() {
    return extensionContainer;
  }

  @Override
  protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
    ExtCallBarringFeatureImpl callBarringFeatureItem;
    this.ssCode = null;
    this.callBarringFeatureList = null;
    this.password = null;
    this.wrongPasswordAttemptsCounter = null;
    this.notificationToCSE = false;
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
          case _TAG_CALL_BARRING_FEATURE_LIST:
            // callBarringFeatureList [1] Ext-CallBarFeatureList
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".callBarringFeatureList: Parameter is primitive",
                  MAPParsingComponentExceptionReason.MistypedParameter);

            AsnInputStream ais2 = ais.readSequenceStream();
            this.callBarringFeatureList = new ArrayList<>();
            while (true) {
              if (ais2.available() == 0)
                break;

              if (ais2.readTag() != Tag.SEQUENCE || ais2.getTagClass() != Tag.CLASS_UNIVERSAL || ais2.isTagPrimitive())
                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ".callBarringFeature: bad element tag or tagClass or is primitive ",
                    MAPParsingComponentExceptionReason.MistypedParameter);
              callBarringFeatureItem = new ExtCallBarringFeatureImpl();
              (callBarringFeatureItem).decodeAll(ais2);
              this.callBarringFeatureList.add(callBarringFeatureItem);
            }
            if (this.callBarringFeatureList.isEmpty() || this.callBarringFeatureList.size() > 32) {
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + "callBarringFeatureList: parameter size must be from 1 to 32, found: "
                  + this.callBarringFeatureList.size(), MAPParsingComponentExceptionReason.MistypedParameter);
            }
            break;
          case _TAG_PASSWORD:
            // password [2] Password OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".password: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            this.password = new PasswordImpl();
            ((PasswordImpl) this.password).decodeAll(ais);
            break;
          case _TAG_WRONG_PASSWORD_ATTEMPTS_COUNTER:
            // wrongPasswordAttemptsCounter [3] WrongPasswordAttemptsCounter OPTIONAL
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
          case _TAG_NOTIFICATION_TO_CSE:
            // notificationToCSE [4] NULL OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".notificationToCSE: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            ais.readNull();
            this.notificationToCSE = true;
            break;
          case _TAG_EXTENSION_CONTAINER:
            // extensionContainer [5] ExtensionContainer OPTIONAL
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
    if (this.ssCode == null || this.callBarringFeatureList == null)
      throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
          + ": ssCode and callBarringFeatureList parameters are mandatory but not all of them were found",
          MAPParsingComponentExceptionReason.MistypedParameter);
  }

  @Override
  public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
    try {
      if (this.ssCode == null)
        throw new MAPException("Error while encoding " + _PrimitiveName + " the mandatory parameter ssCode is not defined");

      if (callBarringFeatureList == null)
          throw new MAPException("Error while encoding " + _PrimitiveName + " the mandatory parameter callBarringFeatureList is not defined");
      else if (this.callBarringFeatureList.isEmpty() || this.callBarringFeatureList.size() > 32)
        throw new MAPException("callBarringFeatureList size must be from 1 to 32, found: " + this.callBarringFeatureList.size());

      // ss-Code [0] SS-Code
      ((SSCodeImpl) this.ssCode).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SS_CODE);

      // callBarringFeatureList [1] Ext-CallBarFeatureList
      if (this.callBarringFeatureList != null) {
        try {
          asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_CALL_BARRING_FEATURE_LIST);
          int pos = asnOutputStream.StartContentDefiniteLength();
          for (ExtCallBarringFeature callBarringFeatureItem : this.callBarringFeatureList) {
            ((ExtCallBarringFeatureImpl) callBarringFeatureItem).encodeAll(asnOutputStream);
          }
          asnOutputStream.FinalizeContent(pos);
        } catch (AsnException e) {
          throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
      }

      // password [2] Password OPTIONAL
      if (this.password != null)
        ((PasswordImpl) this.password).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_PASSWORD);

      // wrongPasswordAttemptsCounter [3] WrongPasswordAttemptsCounter OPTIONAL
      if (this.wrongPasswordAttemptsCounter != null) {
        if (this.wrongPasswordAttemptsCounter < 0 || this.wrongPasswordAttemptsCounter > 4)
          throw new MAPException("istAlertTimer must be from 0 to 4, found: " + this.wrongPasswordAttemptsCounter);
        try {
          asnOutputStream.writeInteger(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_WRONG_PASSWORD_ATTEMPTS_COUNTER, this.wrongPasswordAttemptsCounter);
        } catch (IOException e) {
          throw new MAPException("IOException while encoding " + _PrimitiveName + " parameter wrongPasswordAttemptsCounter", e);
        } catch (AsnException e) {
          throw new MAPException("AsnException while encoding " + _PrimitiveName + " parameter wrongPasswordAttemptsCounter", e);
        }
      }

      // notificationToCSE [4] NULL OPTIONAL
      if (this.notificationToCSE) {
        try {
          asnOutputStream.writeNull(Tag.CLASS_CONTEXT_SPECIFIC, _TAG_NOTIFICATION_TO_CSE);
        } catch (IOException e) {
          throw new MAPException("IOException when encoding " + _PrimitiveName + " parameter notificationToCSE: "
              + e.getMessage(), e);
        } catch (AsnException e) {
          throw new MAPException("AsnException when encoding " + _PrimitiveName + " parameter notificationToCSE: "
              + e.getMessage(), e);
        }
      }

      // extensionContainer [5] ExtensionContainer OPTIONAL
      if (this.extensionContainer != null)
        ((MAPExtensionContainerImpl) this.extensionContainer).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_EXTENSION_CONTAINER);

    } catch (MAPException e) {
      throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_PrimitiveName + " [");

    if (ssCode != null) {
      sb.append("ssCode=");
      sb.append(ssCode);
    }

    if (this.callBarringFeatureList != null) {
      sb.append(", callBarringFeatureList=[");
      boolean firstItem = true;
      for (ExtCallBarringFeature callBarringFeatureItem : this.callBarringFeatureList) {
        if (firstItem)
          firstItem = false;
        else
          sb.append(", ");
        sb.append(callBarringFeatureItem);
      }
      sb.append("], ");
    }

    if (this.password != null) {
      sb.append(", password=");
      sb.append(this.password);
    }

    if (this.wrongPasswordAttemptsCounter != null) {
      sb.append(", wrongPasswordAttemptsCounter=");
      sb.append(this.wrongPasswordAttemptsCounter);
    }

    if (this.notificationToCSE) {
      sb.append(", notificationToCSE, ");
    }

    if (extensionContainer != null) {
      sb.append(", extensionContainer=");
      sb.append(extensionContainer);
    }

    sb.append("]");

    return sb.toString();
  }
}
