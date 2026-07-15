package org.restcomm.protocols.ss7.map.api.service.lsm;

import org.restcomm.protocols.ss7.map.api.primitives.GSNAddress;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;

/**
 <code>
 SubscriberLocationReport-Res ::= SEQUENCE {
  extensionContainer        ExtensionContainer      OPTIONAL,
  ...,
  na-ESRK                    [0] ISDN-AddressString  OPTIONAL,
  na-ESRD                    [1] ISDN-AddressString  OPTIONAL,
  h-gmlc-Address             [2] GSN-Address         OPTIONAL,
  mo-lrShortCircuitIndicator [3] NULL                OPTIONAL,
  reportingPLMNList          [4] ReportingPLMNList   OPTIONAL,
  lcs-ReferenceNumber        [5] LCS-ReferenceNumber OPTIONAL }

  -- na-ESRK and na-ESRD are mutually exclusive
  --
  -- exception handling
  -- receipt of both na-ESRK and na-ESRD shall be treated the same as a return error
 </code>
 *
 * @author amit bhayani
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface SubscriberLocationReportResponse extends LsmMessage {

    MAPExtensionContainer getExtensionContainer();

    ISDNAddressString getNaESRK();

    ISDNAddressString getNaESRD();

    GSNAddress getHGMLCAddress();

    boolean getMolrShortCircuitIndicator();

    ReportingPLMNList getReportingPLMNList();

    Integer getLcsReferenceNumber();
}
