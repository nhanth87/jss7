package org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement;

import java.io.Serializable;

import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OfferedCamel4CSIs;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SupportedCamelPhases;

/**
 *
<code>
SGSN-Capability ::= SEQUENCE {
  solsaSupportIndicator                             NULL OPTIONAL,
  extensionContainer                                 [1] ExtensionContainer OPTIONAL,
  ...,
  superChargerSupportedInServingNetworkEntity        [2] SuperChargerInfo OPTIONAL,
  gprsEnhancementsSupportIndicator                   [3] NULL OPTIONAL,
  supportedCamelPhases                               [4] SupportedCamelPhases OPTIONAL,
  supportedLCS-CapabilitySets                        [5] SupportedLCS-CapabilitySets OPTIONAL,
  offeredCamel4CSIs                                  [6] OfferedCamel4CSIs OPTIONAL,
  smsCallBarringSupportIndicator                     [7] NULL OPTIONAL,
  supportedRAT-TypesIndicator                        [8] SupportedRAT-Types OPTIONAL,
  supportedFeatures                                  [9] SupportedFeatures OPTIONAL,
  t-adsDataRetrieval                                 [10] NULL OPTIONAL,
  homogeneousSupportOfIMSVoiceOverPSSessions         [11] BOOLEAN OPTIONAL
  -- "true" indicates homogeneous support, "false" indicates homogeneous non-support
  -- in the complete SGSN area
  cancellationTypeInitialAttach                      [12] NULL OPTIONAL,
  msisdn-lessOperation-Supported                     [14] NULL OPTIONAL,
  updateofHomogeneousSupportOfIMSVoiceOverPSSessions [15] NULL OPTIONAL,
  reset-ids-Supported                                [16] NULL OPTIONAL,
  ext-SupportedFeatures                              [17] Ext-SupportedFeatures OPTIONAL
 }
 -- the supportedFeatures, t-adsDataRetrieval,
 -- homogeneousSupportOfIMSVoiceOverPSSessions
 -- /updateofHomogeneousSupportOfIMSVoiceOverPSSessions and
 --ext-SupportedFeatures are also applied to the MME/IWF

</code>
 *
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public interface SGSNCapability extends Serializable {

    boolean getSolsaSupportIndicator();

    MAPExtensionContainer getExtensionContainer();

    SuperChargerInfo getSuperChargerSupportedInServingNetworkEntity();

    boolean getGprsEnhancementsSupportIndicator();

    SupportedCamelPhases getSupportedCamelPhases();

    SupportedLCSCapabilitySets getSupportedLCSCapabilitySets();

    OfferedCamel4CSIs getOfferedCamel4CSIs();

    boolean getSmsCallBarringSupportIndicator();

    SupportedRATTypes getSupportedRATTypesIndicator();

    SupportedFeatures getSupportedFeatures();

    boolean getTAdsDataRetrieval();

    Boolean getHomogeneousSupportOfIMSVoiceOverPSSessions();

    boolean getCancellationTypeInitialAttach();

    boolean getMsisdnlessOperationSupported();

    boolean getUpdateOfHomogeneousSupportOfIMSVoiceOverPSSessions();

    boolean getResetIdsSupported();

    ExtSupportedFeatures getExtSupportedFeatures();
}
