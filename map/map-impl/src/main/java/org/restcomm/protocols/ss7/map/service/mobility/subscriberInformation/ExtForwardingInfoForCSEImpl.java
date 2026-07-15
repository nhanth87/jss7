package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ExtForwardingInfoForCSE;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtForwFeature;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSCode;
import org.restcomm.protocols.ss7.map.primitives.MAPExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.primitives.SequenceBase;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtForwFeatureImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.SSCodeImpl;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ExtForwardingInfoForCSEImpl extends SequenceBase implements ExtForwardingInfoForCSE {

  private static final int _TAG_SS_CODE = 0;
  private static final int _TAG_FORWARDING_FEATURE_LIST = 1;
  private static final int _TAG_NOTIFICATION_TO_CSE = 2;
  private static final int _TAG_EXTENSION_CONTAINER = 3;

  private static final String _PrimitiveName = "ExtSSInfoForCSE";

  private SSCode ssCode;
  private ArrayList<ExtForwFeature> forwardingFeatureList;
  private boolean notificationToCSE;
  private MAPExtensionContainer extensionContainer;

  public ExtForwardingInfoForCSEImpl() {
    super(_PrimitiveName);
  }

  public ExtForwardingInfoForCSEImpl(SSCode ssCode, ArrayList<ExtForwFeature> forwardingFeatureList, boolean notificationToCSE,
          MAPExtensionContainer extensionContainer) {
    super(_PrimitiveName);
    this.ssCode = ssCode;
    this.forwardingFeatureList = forwardingFeatureList;
    this.notificationToCSE = notificationToCSE;
    this.extensionContainer = extensionContainer;
  }

  @Override
  public SSCode getSsCode() {
    return this.ssCode;
  }

  @Override
  public ArrayList<ExtForwFeature> getForwardingFeatureList() {
    return this.forwardingFeatureList;
  }

  @Override
  public boolean getNotificationToCSE() {
    return this.notificationToCSE;
  }

  @Override
  public MAPExtensionContainer getExtensionContainer() {
    return this.extensionContainer;
  }

  @Override
  protected void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
    ExtForwFeatureImpl forwardingFeatureItem;
    this.ssCode = null;
    this.forwardingFeatureList = null;
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
          case _TAG_FORWARDING_FEATURE_LIST:
            // forwardingFeatureList [1] Ext-ForwFeatureList
            if (ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".forwardingFeatureList: Parameter is primitive", MAPParsingComponentExceptionReason.MistypedParameter);

            AsnInputStream ais2 = ais.readSequenceStream();
            this.forwardingFeatureList = new ArrayList<>();
            while (true) {
              if (ais2.available() == 0)
                break;

              if (ais2.readTag() != Tag.SEQUENCE || ais2.getTagClass() != Tag.CLASS_UNIVERSAL || ais2.isTagPrimitive())
                throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ".forwardingFeature: bad element tag or tagClass or is not primitive ",
                    MAPParsingComponentExceptionReason.MistypedParameter);

              forwardingFeatureItem = new ExtForwFeatureImpl();
              (forwardingFeatureItem).decodeAll(ais2);
              this.forwardingFeatureList.add(forwardingFeatureItem);
            }
            if (this.forwardingFeatureList.isEmpty() || this.forwardingFeatureList.size() > 32) {
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".forwardingFeatureList: parameter size must be from 1 to 32, found: "
                  + this.forwardingFeatureList.size(), MAPParsingComponentExceptionReason.MistypedParameter);
            }
            break;
          case _TAG_NOTIFICATION_TO_CSE:
            // notificationToCSE [2] NULL OPTIONAL
            if (!ais.isTagPrimitive())
              throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
                  + ".notificationToCSE: parameter is not primitive", MAPParsingComponentExceptionReason.MistypedParameter);
            ais.readNull();
            this.notificationToCSE = true;
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

    if (this.ssCode == null || this.forwardingFeatureList == null)
      throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
          + ": ssCode and forwardingFeatureList parameters are mandatory but not all of them were found",
          MAPParsingComponentExceptionReason.MistypedParameter);
  }

  @Override
  public void encodeData(AsnOutputStream asnOutputStream) throws MAPException {
    try {
      if (this.ssCode == null)
        throw new MAPException("Error while encoding " + _PrimitiveName + " the mandatory parameter ssCode is not defined");

      if (this.forwardingFeatureList == null)
          throw new MAPException("Error while encoding " + _PrimitiveName + " the mandatory parameter forwardingFeatureList is not defined");
      else if (this.forwardingFeatureList.isEmpty() || this.forwardingFeatureList.size() > 32)
        throw new MAPException("forwardingFeatureList size must be from 1 to 32, found: " + this.forwardingFeatureList.size());

      // ss-Code [0] SS-Code
      ((SSCodeImpl) this.ssCode).encodeAll(asnOutputStream, Tag.CLASS_CONTEXT_SPECIFIC, _TAG_SS_CODE);

      // forwardingFeatureList [1] Ext-ForwFeatureList
      if (this.forwardingFeatureList != null) {
        try {
          asnOutputStream.writeTag(Tag.CLASS_CONTEXT_SPECIFIC, false, _TAG_FORWARDING_FEATURE_LIST);
          int pos = asnOutputStream.StartContentDefiniteLength();
          for (ExtForwFeature forwardingFeatureItem : this.forwardingFeatureList) {
            ((ExtForwFeatureImpl) forwardingFeatureItem).encodeAll(asnOutputStream);
          }
          asnOutputStream.FinalizeContent(pos);
        } catch (AsnException e) {
          throw new MAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
      }

      // notificationToCSE [2] NULL OPTIONAL
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

      // extensionContainer [3] ExtensionContainer OPTIONAL
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
      sb.append(", ");
    }

    if (this.forwardingFeatureList != null) {
      sb.append(", forwardingFeatureList=[");
      boolean firstItem = true;
      for (ExtForwFeature forwardingFeatureItem : this.forwardingFeatureList) {
        if (firstItem)
          firstItem = false;
        else
          sb.append(", ");
        sb.append(forwardingFeatureItem);
      }
      sb.append("], ");
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
