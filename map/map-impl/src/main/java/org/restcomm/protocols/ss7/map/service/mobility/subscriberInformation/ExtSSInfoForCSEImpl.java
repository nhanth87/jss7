package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentExceptionReason;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ExtCallBarringInfoForCSE;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ExtForwardingInfoForCSE;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ExtSSInfoForCSE;
import org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive;

import java.io.IOException;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ExtSSInfoForCSEImpl implements ExtSSInfoForCSE, MAPAsnPrimitive {

  private static final int _TAG_FORWARDING_INFO_FOR_CSE = 0;
  private static final int _TAG_CALL_BARRING_INFO_FOR_CSE = 1;

  private static final String _PrimitiveName = "ExtSSInfoForCSE";

  private ExtForwardingInfoForCSE forwardingInfoForCSE = null;
  private ExtCallBarringInfoForCSE callBarringInfoForCSE = null;

  public ExtSSInfoForCSEImpl() {
  }

  public ExtSSInfoForCSEImpl(ExtForwardingInfoForCSE forwardingInfoForCSE) {
    this.forwardingInfoForCSE = forwardingInfoForCSE;
  }

  public ExtSSInfoForCSEImpl(ExtCallBarringInfoForCSE callBarringInfoForCSE) {
    this.callBarringInfoForCSE = callBarringInfoForCSE;
  }

  @Override
  public ExtForwardingInfoForCSE getForwardingInfoForCSE() {
    return this.forwardingInfoForCSE;
  }

  @Override
  public ExtCallBarringInfoForCSE getCallBarringInfoForCSE() {
    return this.callBarringInfoForCSE;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#getTag()
   */
  public int getTag() throws MAPException {
    if (this.forwardingInfoForCSE != null)
      return _TAG_FORWARDING_INFO_FOR_CSE;
    else if (this.callBarringInfoForCSE != null)
      return _TAG_CALL_BARRING_INFO_FOR_CSE;
    else
      throw new MAPException("No choices are supplied");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#getTagClass()
   */
  public int getTagClass() {
    return Tag.CLASS_CONTEXT_SPECIFIC;
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
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#decodeAll(org.mobicents.protocols.asn.AsnInputStream)
   */
  public void decodeAll(AsnInputStream asnInputStream) throws MAPParsingComponentException {
    try {
      int length = asnInputStream.readLength();
      this._decode(asnInputStream, length);
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
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#decodeData(org.mobicents.protocols.asn.AsnInputStream,
   * int)
   */
  public void decodeData(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException {
    try {
      this._decode(asnInputStream, length);
    } catch (IOException e) {
      throw new MAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
          MAPParsingComponentExceptionReason.MistypedParameter);
    } catch (AsnException e) {
      throw new MAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
          MAPParsingComponentExceptionReason.MistypedParameter);
    }
  }

  private void _decode(AsnInputStream asnInputStream, int length) throws MAPParsingComponentException, IOException, AsnException {
    this.forwardingInfoForCSE = null;
    this.callBarringInfoForCSE = null;

    if (asnInputStream.getTagClass() != Tag.CLASS_CONTEXT_SPECIFIC || asnInputStream.isTagPrimitive())
      throw new MAPParsingComponentException("Error while decoding " + _PrimitiveName
          + ": bad tag class or is primitive: TagClass=" + asnInputStream.getTagClass(),
          MAPParsingComponentExceptionReason.MistypedParameter);

    switch (asnInputStream.getTag()) {
      // forwardingInfoFor-CSE [0] Ext-ForwardingInfoFor-CSE
      case _TAG_FORWARDING_INFO_FOR_CSE:
        this.forwardingInfoForCSE = new ExtForwardingInfoForCSEImpl();
        ((ExtForwardingInfoForCSEImpl) this.forwardingInfoForCSE).decodeData(asnInputStream, length);
        break;
      // callBarringInfoFor-CSE [1] Ext-CallBarringInfoFor-CSE
      case _TAG_CALL_BARRING_INFO_FOR_CSE:
        this.callBarringInfoForCSE = new ExtCallBarringInfoForCSEImpl();
        ((ExtCallBarringInfoForCSEImpl) this.callBarringInfoForCSE).decodeData(asnInputStream, length);
        break;

      default:
        throw new MAPParsingComponentException("Error while " + _PrimitiveName + ": bad tag: " + asnInputStream.getTag(),
            MAPParsingComponentExceptionReason.MistypedParameter);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#encodeAll(org.mobicents.protocols.asn.AsnOutputStream)
   */
  public void encodeAll(AsnOutputStream asnOutputStream) throws MAPException {
    this.encodeAll(asnOutputStream, this.getTagClass(), this.getTag());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive#encodeAll(org.mobicents.protocols.asn.AsnOutputStream,
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
    int cnt = 0;
    if (this.forwardingInfoForCSE != null)
      cnt++;
    if (this.callBarringInfoForCSE != null)
      cnt++;

    if (cnt != 1)
      throw new MAPException("Error while encoding " + _PrimitiveName + ": one and only one choice is required.");

    if (this.forwardingInfoForCSE != null) {
      ((ExtForwardingInfoForCSEImpl) this.forwardingInfoForCSE).encodeData(asnOutputStream);
    } else {
      ((ExtCallBarringInfoForCSEImpl) this.callBarringInfoForCSE).encodeData(asnOutputStream);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_PrimitiveName + " [");

    if (forwardingInfoForCSE != null) {
      sb.append("forwardingInfoForCSE=");
      sb.append(forwardingInfoForCSE);
    }

    if (callBarringInfoForCSE != null) {
      sb.append("callBarringInfoForCSE=");
      sb.append(callBarringInfoForCSE);
    }

    sb.append("]");

    return sb.toString();
  }
}
