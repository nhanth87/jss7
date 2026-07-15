package org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement;

import java.io.Serializable;

/**
 *
 *  SupportedFeatures::= BIT STRING {
 *  odb-all-apn (0),
 *  odb-HPLMN-APN (1),
 *  odb-VPLMN-APN (2),
 *  odb-all-og (3),
 *  odb-all-international-og (4),
 *  odb-all-int-og-not-to-HPLMN-country (5),
 *  odb-all-interzonal-og (6),
 *  odb-all-interzonal-og-not-to-HPLMN-country (7),
 *  odb-all-interzonal-og-and-internat-og-not-to-HPLMN-country (8),
 *  regSub (9),
 *  trace (10),
 *  lcs-all-PrivExcep (11),
 *  lcs-universal (12),
 *  lcs-CallSessionRelated (13),
 *  lcs-CallSessionUnrelated (14),
 *  lcs-PLMN-operator (15),
 *  lcs-ServiceType (16),
 *  lcs-all-MOLR-SS (17),
 *  lcs-basicSelfLocation (18),
 *  lcs-autonomousSelfLocation (19),
 *  lcs-transferToThirdParty (20),
 *  sm-mo-pp (21),
 *  barring-OutgoingCalls (22),
 *  baoc (23),
 *  boic (24),
 *  boicExHC (25),
 *  localTimeZoneRetrieval (26),
 *  additionalMsisdn (27),
 *  smsInMME (28),
 *  smsInSGSN (29),
 *  ue-Reachability-Notification (30),
 *  state-Location-Information-Retrieval (31),
 *  partialPurge (32),
 *  gddInSGSN (33),
 *  sgsnCAMELCapability (34),
 *  pcscf-Restoration (35),
 *  dedicatedCoreNetworks (36),
 *  non-IP-PDN-Type-APNs (37),
 *  non-IP-PDP-Type-APNs (38),
 *  nrAsSecondaryRAT (39) }
 *  (SIZE (26..40))
 *
 * @author sergey vetyutnev
 *
 */
public interface SupportedFeatures extends Serializable {

    boolean getOdbAllApn();

    boolean getOdbHPLMNApn();

    boolean getOdbVPLMNApn();

    boolean getOdbAllOg();

    boolean getOdbAllInternationalOg();

    boolean getOdbAllIntOgNotToHPLMNCountry();

    boolean getOdbAllInterzonalOg();

    boolean getOdbAllInterzonalOgNotToHPLMNCountry();

    boolean getOdbAllInterzonalOgandInternatOgNotToHPLMNCountry();

    boolean getRegSub();

    boolean getTrace();

    boolean getLcsAllPrivExcep();

    boolean getLcsUniversal();

    boolean getLcsCallSessionRelated();

    boolean getLcsCallSessionUnrelated();

    boolean getLcsPLMNOperator();

    boolean getLcsServiceType();

    boolean getLcsAllMOLRSS();

    boolean getLcsBasicSelfLocation();

    boolean getLcsAutonomousSelfLocation();

    boolean getLcsTransferToThirdParty();

    boolean getSmMoPp();

    boolean getBarringOutgoingCalls();

    boolean getBaoc();

    boolean getBoic();

    boolean getBoicExHC();

    boolean getLocalTimeZoneRetrieval();

    boolean getAdditionalMsisdn();

    boolean getSmsInMME();

    boolean getSmsInSGSN();

    boolean getUeReachabilityNotification();

    boolean getStateLocationInformationRetrieval();

    boolean getPartialPurge();

    boolean getGddInSGSN();

    boolean getSgsnCAMELCapability();

    boolean getPcscfRestoration();

    boolean getDedicatedCoreNetworks();

    boolean getNonIPPDNTypeAPNs();

    boolean getNonIPPDPTypeAPNs();

    boolean getNrAsSecondaryRAT();
}
