package org.restcomm.protocols.ss7.map.api.service.lsm;

import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.restcomm.protocols.ss7.map.api.primitives.GSNAddress;
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;

/**
 *
 <code>
 subscriberLocationReport OPERATION ::= { --Timer m ARGUMENT
  SubscriberLocationReport-Arg RESULT SubscriberLocationReport-Res
  ERRORS { systemFailure | dataMissing | resourceLimitation | unexpectedDataValue | unknownSubscriber |
  unauthorizedRequestingNetwork | unknownOrUnreachableLCSClient} CODE local:86 }

 SubscriberLocationReport-Arg ::= SEQUENCE {
 lcs-Event                              LCS-Event,
 lcs-ClientID                           LCS-ClientID,
 lcsLocationInfo                        LCSLocationInfo,
 msisdn                                 [0] ISDN-AddressString OPTIONAL,
 imsi                                   [1] IMSI  OPTIONAL,
 imei                                   [2] IMEI  OPTIONAL,
 na-ESRD                                [3] ISDN-AddressString OPTIONAL,
 na-ESRK                                [4] ISDN-AddressString OPTIONAL,
 locationEstimate                       [5] Ext-GeographicalInformation OPTIONAL,
 ageOfLocationEstimate                  [6] AgeOfLocationInformation OPTIONAL,
 slr-ArgExtensionContainer              [7] SLR-ArgExtensionContainer OPTIONAL,
 ...,
 add-LocationEstimate                   [8] Add-GeographicalInformation OPTIONAL,
 deferredmt-lrData                      [9] Deferredmt-lrData OPTIONAL,
 lcs-ReferenceNumber                    [10] LCS-ReferenceNumber OPTIONAL,
 geranPositioningData                   [11] PositioningDataInformation OPTIONAL,
 utranPositioningData                   [12] UtranPositioningDataInfo OPTIONAL,
 cellIdOrSai                            [13] CellGlobalIdOrServiceAreaIdOrLAI OPTIONAL,
 h-gmlc-Address                         [14] GSN-Address OPTIONAL,
 lcsServiceTypeID                       [15] LCSServiceTypeID OPTIONAL,
 sai-Present                            [17] NULL OPTIONAL,
 pseudonymIndicator                     [18] NULL  OPTIONAL,
 accuracyFulfilmentIndicator            [19] AccuracyFulfilmentIndicator OPTIONAL,
 velocityEstimate                       [20] VelocityEstimate OPTIONAL,
 sequenceNumber                         [21] SequenceNumber OPTIONAL,
 periodicLDRInfo                        [22] PeriodicLDRInfo OPTIONAL,
 mo-lrShortCircuitIndicator             [23] NULL  OPTIONAL,
 geranGANSSpositioningData              [24] GeranGANSSpositioningData OPTIONAL,
 utranGANSSpositioningData              [25] UtranGANSSpositioningData OPTIONAL,
 targetServingNodeForHandover           [26] ServingNodeAddress OPTIONAL,
 utranAdditionalPositioningData         [27] UtranAdditionalPositioningData OPTIONAL,
 utranBaroPressureMeas                  [28] UtranBaroPressureMeas OPTIONAL,
 utranCivicAddress                      [29] UtranCivicAddress OPTIONAL }

 -- one of msisdn or imsi is mandatory

 -- a location estimate that is valid for the locationEstimate parameter should
 -- be transferred in this parameter in preference to the add-LocationEstimate.

 -- the deferredmt-lrData parameter shall be included if and only if the lcs-Event
 -- indicates a deferredmt-lrResponse.

 -- if the lcs-Event indicates a deferredmt-lrResponse then the locationEstimate
 -- and the add-locationEstimate parameters shall not be sent if the
 -- supportedGADShapes parameter had been received in ProvideSubscriberLocation-Arg
 -- and the shape encoded in locationEstimate or add-LocationEstimate was not marked
 -- as supported in supportedGADShapes. In such a case terminationCause
 -- in deferredmt-lrData shall be present with value
 -- shapeOfLocationEstimateNotSupported.

 -- If a lcs event indicates deferred mt-lr response, the lcs-Reference number shall be
 -- included.

 -- sai-Present indicates that the cellIdOrSai parameter contains a Service Area Identity

 SequenceNumber ::= INTEGER (1..8639999)
 </code>
 *
 * @author amit bhayani
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public interface SubscriberLocationReportRequest extends LsmMessage {

    LCSEvent getLCSEvent();

    LCSClientID getLCSClientID();

    LCSLocationInfo getLCSLocationInfo();

    ISDNAddressString getMSISDN();

    IMSI getIMSI();

    IMEI getIMEI();

    ISDNAddressString getNaESRD();

    ISDNAddressString getNaESRK();

    ExtGeographicalInformation getLocationEstimate();

    Integer getAgeOfLocationEstimate();

    SLRArgExtensionContainer getSLRArgExtensionContainer();

    AddGeographicalInformation getAdditionalLocationEstimate();

    DeferredmtlrData getDeferredmtlrData();

    Integer getLCSReferenceNumber();

    PositioningDataInformation getGeranPositioningData();

    UtranPositioningDataInfo getUtranPositioningData();

    CellGlobalIdOrServiceAreaIdOrLAI getCellGlobalIdOrServiceAreaIdOrLAI();

    GSNAddress getHGMLCAddress();

    Integer getLCSServiceTypeID();

    boolean getSaiPresent();

    boolean getPseudonymIndicator();

    AccuracyFulfilmentIndicator getAccuracyFulfilmentIndicator();

    VelocityEstimate getVelocityEstimate();

    Integer getSequenceNumber();

    PeriodicLDRInfo getPeriodicLDRInfo();

    boolean getMoLrShortCircuitIndicator();

    GeranGANSSpositioningData getGeranGANSSpositioningData();

    UtranGANSSpositioningData getUtranGANSSpositioningData();

    ServingNodeAddress getTargetServingNodeForHandover();

    UtranAdditionalPositioningData getUtranAdditionalPositioningData();

    Integer getUtranBaroPressureMeas();

    UtranCivicAddress getUtranCivicAddress();
}
