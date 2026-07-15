package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation;

import java.io.Serializable;

import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;

/**
<code>
RequestedInfo ::= SEQUENCE {
  locationInformation               [0] NULL OPTIONAL,
  subscriberState                   [1] NULL OPTIONAL,
  extensionContainer                [2] ExtensionContainer OPTIONAL,
  ...,
  currentLocation                   [3] NULL OPTIONAL,
  requestedDomain                   [4] DomainType OPTIONAL,
  imei                              [6] NULL OPTIONAL,
  ms-classmark                      [5] NULL OPTIONAL,
  mnpRequestedInfo                  [7] NULL OPTIONAL,
  locationInformationEPS-Supported  [11] NULL OPTIONAL,
  t-adsData                         [8] NULL OPTIONAL,
  requestedNodes                    [9] RequestedNodes OPTIONAL,
  servingNodeIndication             [10] NULL OPTIONAL,
  localTimeZoneRequest              [12] NULL OPTIONAL
}
 -- currentLocation and locationInformationEPS-Supported shall be absent if
 -- locationInformation is absent
 -- t-adsData shall be absent in messages sent to the VLR
 -- requestedNodes shall be absent if requestedDomain is "cs-Domain"
 -- servingNodeIndication shall be absent if locationInformation is absent;
 -- servingNodeIndication shall be absent if current location is present;
 -- servingNodeIndication indicates by its presence that only the serving node's
 -- address (MME-Name or SGSN-Number or VLR-Number) is requested.
</code>
 * @author abhayani
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface RequestedInfo extends Serializable {

    boolean getLocationInformation();

    boolean getSubscriberState();

    MAPExtensionContainer getExtensionContainer();

    boolean getCurrentLocation();

    DomainType getRequestedDomain();

    boolean getImei();

    boolean getMsClassmark();

    boolean getMnpRequestedInfo();

    boolean getLocationInformationEPSSupported();

    boolean getTadsData();

    RequestedNodes getRequestedNodes();

    boolean getServingNodeIndication();

    boolean getLocalTimeZoneRequest();
}

