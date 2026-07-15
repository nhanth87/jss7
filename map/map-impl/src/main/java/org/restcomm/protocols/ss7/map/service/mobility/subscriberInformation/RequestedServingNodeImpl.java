package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.RequestedServingNode;
import org.restcomm.protocols.ss7.map.primitives.BitStringBase;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class RequestedServingNodeImpl extends BitStringBase implements RequestedServingNode {

  private static final int _TAG_MME_AND_SGSN = 0;
  public static final String _PrimitiveName = "RequestedServingNode";

  public RequestedServingNodeImpl() {
    super(1, 8, 8, _PrimitiveName);
  }

  public RequestedServingNodeImpl(boolean mmeAndSgsn) {
    super(1, 8, 8, _PrimitiveName);

    if (mmeAndSgsn)
      this.bitString.set(_TAG_MME_AND_SGSN);
  }

  @Override
  public boolean getMmeAndSgsn() {
    return this.bitString.get(_TAG_MME_AND_SGSN);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_PrimitiveName);
    sb.append(" [");
    if (this.getMmeAndSgsn())
      sb.append("mmeAndSgsn");
    sb.append("]");
    return sb.toString();
  }
}
