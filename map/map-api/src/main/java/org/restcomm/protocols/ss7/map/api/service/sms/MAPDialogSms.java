package org.restcomm.protocols.ss7.map.api.service.sms;

import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.Time;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.NetworkNodeDiameterAddress;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.TeleserviceCode;

/**
 *
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface MAPDialogSms extends MAPDialog {

    /**
     * Sending MAP-FORWARD-SHORT-MESSAGE request
     *
     * @param sm_RP_DA (M)
     * @param sm_RP_OA (M)
     * @param sm_RP_UI (M)
     * @param moreMessagesToSend optional, default: false
     *
     * @return invokeId
     */
    Long addForwardShortMessageRequest(SM_RP_DA sm_RP_DA, SM_RP_OA sm_RP_OA, SmsSignalInfo sm_RP_UI, boolean moreMessagesToSend) throws MAPException;

    Long addForwardShortMessageRequest(int customInvokeTimeout, SM_RP_DA sm_RP_DA, SM_RP_OA sm_RP_OA,
            SmsSignalInfo sm_RP_UI, boolean moreMessagesToSend) throws MAPException;

    /**
     * Sending MAP-FORWARD-SHORT-MESSAGE response
     */
    void addForwardShortMessageResponse(long invokeId) throws MAPException;

    /**
     * Sending MAP-MO-FORWARD-SHORT-MESSAGE request
     *
     * @param sm_RP_DA (M) Contains the Service Centre address received from the mobile station.
     * @param sm_RP_OA (M) The MSISDN received from the VLR or the SGSN is inserted in this parameter in the MO SM transfer.
     *                 The MSISDN received from the VLR or from the SGSN is inserted in this parameter in the mobile originated SM transfer.
     *                 A Dummy MSISDN value is used for MSISDN-less SMS in IMS.
     *                 In this case the originating user is identified by SIP-URI-A of the correlationID parameter.
     * @param sm_RP_UI (M) The SM transfer protocol data unit received from the SC is inserted in this parameter
     * @param extensionContainer (C)
     * @param imsi (C) The IMSI of the originating subscriber shall be inserted in this parameter in the MO SM transfer
     * @param correlationID (C) Composed of an HLR-Id identifying the destination user's HLR,
     *                      a SIP-URI-B identifying the MSISDN-less destination user,
     *                      and a SIP-URI-A identifying the originating user.
     *                      The Correlation ID indicates by its presence that the request
     *                      is sent in the context of MSISDN-less SMS delivery in IMS,
     *                      and that a Report-SM-Delivery status needs to be sent to the HLR to add the SC address to the MWD.
     * @param smDeliveryOutcome (C) Indicates the status of the mobile terminated SM delivery
     *                          (three possible values: memoryCapacityExceeded(0), absentSubscriber(1), successfulTransfer(2)).
     *                          Shall be present if Correlation ID is present and shall take one of the unsuccessful outcome values.
     * @return invokeId
     */
    Long addMoForwardShortMessageRequest(SM_RP_DA sm_RP_DA, SM_RP_OA sm_RP_OA, SmsSignalInfo sm_RP_UI, MAPExtensionContainer extensionContainer,
            IMSI imsi, CorrelationID correlationID, SMDeliveryOutcome smDeliveryOutcome) throws MAPException;

    Long addMoForwardShortMessageRequest(int customInvokeTimeout, SM_RP_DA sm_RP_DA, SM_RP_OA sm_RP_OA, SmsSignalInfo sm_RP_UI,
            MAPExtensionContainer extensionContainer, IMSI imsi, CorrelationID correlationID, SMDeliveryOutcome smDeliveryOutcome) throws MAPException;

    /**
     * Sending MAP-MO-FORWARD-SHORT-MESSAGE response
     *
     * @param sm_RP_UI (C)
     * @param extensionContainer (C)
     */
    void addMoForwardShortMessageResponse(long invokeId, SmsSignalInfo sm_RP_UI, MAPExtensionContainer extensionContainer) throws MAPException;

    /**
     * Sending MAP-MT-FORWARD-SHORT-MESSAGE request
     *
     * @param sm_RP_DA (M) This parameter can contain either an IMSI or a LMSI. The use of the LMSI is an operator option.
     *                 The LMSI can be provided if it is received from the HLR. The IMSI is used if the use of the LMSI is not available.
     *                 This parameter is omitted (i.e. is present and takes the value "noSM-RP-DA") in the mobile terminated subsequent SM transfers.
     *                 When a Correlation ID is present, the IMSI parameter within SM RP DA shall be populated
     *                 with the HLR-ID and the destination user is identified by the SIP-URI-B within the Correlation ID.
     * @param sm_RP_OA (M) The Service Centre (SC) address received from the originating SC is inserted in this parameter.
     *                 Omitted in the MT subsequent SM transfers.
     * @param sm_RP_UI (M) The SM transfer protocol data unit received from the SC is inserted in this parameter.
     *                 A short message transfer protocol data unit may also be inserted in this parameter
     *                 in the message delivery acknowledgement from the MSC or from the SGSN to the SC.
     * @param moreMessagesToSend (C) Indicates whether or not the service centre has more short messages to send.
     *                           The information from the MMS indication received from the Service Centre is inserted in this parameter.
     * @param extensionContainer (C)
     * @param smDeliveryTimer (C) Indicates the SM Delivery Timer value (in seconds) set in the SMS-GMSC to the IP-SM-GW, SGSN or MSC/VLR.
     *                        It may be taken into account by the domain selection procedure in the IP-SM-GW.
     * @param smDeliveryStartTime (C) Indicates the timestamp (in UTC) at which the SM Delivery Supervision Timer was started in the SMS-GMSC.
     * @param smsOverIPOnlyIndicator (C) Indicates by its presence that the IP-SM-GW shall try to deliver
     *                               the short message via IMS without retrying to other domains.
     *                               It shall be present in messages sent to the IP-SM-GW following
     *                               a T4-Submit Trigger message (see 3GPP TS 23.682)
     *                               but not in messages sent to MSC or SGSN (possibly transiting an SMS-Router).
     * @param correlationID (C) contains the SIP-URI-B identifying the (MSISDN-less) destination user
     *                      and the SIP-URI-A identifying the (MSISDN-less) originating user.
     *                      HLR-ID shall be absent from this parameter.
     *                      When a Correlation ID is present, the IMSI parameter within SM RP DA shall be populated
     *                      with the HLR-ID and the destination user is identified by the SIP-URI-B within the Correlation ID.
     * @param maximumRetransmissionTime (C) Indicates the maximum retransmission time (in UTC)
     *                                  until which the SMS-GMSC is capable to retransmit the MT Short Message.
     * @param smsGmscAddress (C) Contains the E.164 number of the SMS-GMSC or SMS Router,
     *                       in international number format as described in ITU-T Recommendation E.164.
     * @param smsGmscDiameterAddress (C) Contains the Diameter Identity of the SMS-GMSC or SMS Router.
     *
     * @return invokeId
     */
    Long addMtForwardShortMessageRequest(SM_RP_DA sm_RP_DA, SM_RP_OA sm_RP_OA, SmsSignalInfo sm_RP_UI,
            boolean moreMessagesToSend, MAPExtensionContainer extensionContainer, Integer smDeliveryTimer,
            Time smDeliveryStartTime, boolean smsOverIPOnlyIndicator, CorrelationID correlationID,
            Time maximumRetransmissionTime, ISDNAddressString smsGmscAddress,
            NetworkNodeDiameterAddress smsGmscDiameterAddress) throws MAPException;

    Long addMtForwardShortMessageRequest(int customInvokeTimeout, SM_RP_DA sm_RP_DA, SM_RP_OA sm_RP_OA,
            SmsSignalInfo sm_RP_UI, boolean moreMessagesToSend, MAPExtensionContainer extensionContainer,
            Integer smDeliveryTimer, Time smDeliveryStartTime, boolean smsOverIPOnlyIndicator,
            CorrelationID correlationID, Time maximumRetransmissionTime, ISDNAddressString smsGmscAddress,
            NetworkNodeDiameterAddress smsGmscDiameterAddress) throws MAPException;

    /**
     * Sending MAP-MT-FORWARD-SHORT-MESSAGE response
     *
     * @param sm_RP_UI (C)
     * @param extensionContainer (C)
     */
    void addMtForwardShortMessageResponse(long invokeId, SmsSignalInfo sm_RP_UI, MAPExtensionContainer extensionContainer) throws MAPException;

    /**
     * Sending MAP-SEND-ROUTING-INFO-FOR-SM request
     *
     * @param msisdn (M) Refers to one of the ISDN numbers assigned to a mobile subscriber in accordance with CCITT Recommendation E.213.
     *               When SRISM is sent by the SMS-GMSC to the HLR following an T4 Submit Trigger (see 3GPP TS 23.682),
     *               MSISDN may not be available. In this case the UE shall be identified by the IMSI
     *               and the MSISDN shall take the dummy MSISDN value (see clause 3 of 3GPP TS 23.003).
     *               When SRISM is sent by the SMS-GMSC to the HLR in a retry context of SMS for IMS UE to IMS UE without MSISDN (see 3GPP TS 23.204),
     *               MSISDN may not be available. In this case the UE shall be identified by a Correlation ID (SIP-URI-B)
     *               and the MSISDN shall take the dummy MSISDN value (see clause 3 of 3GPP TS 23.003).
     * @param sm_RP_PRI (M) Used to indicate whether or not delivery of the SM shall be attempted when an SC address is already contained in the MWD file.
     * @param serviceCentreAddress (M) Represents the address of a Short Message Service Centre (SMSC).
     * @param extensionContainer (C)
     * @param gprsSupportIndicator (C) Indicates that the SMS-GMSC supports GPRS specific procedure of
     *                             combine delivery of Short Message via MSC and/or via the SGSN.
     *                             The presence of this parameter is mandatory if the SMS-GMSC supports receiving of the two numbers from the HLR.
     * @param sM_RP_MTI (C) Represents the RP-Message Type Indicator of the Short Message.
     *                  It is used to distinguish an SM sent to the MS in order to acknowledge an MO-SM initiated by the mobile from a normal MT-SM.
     *                  This parameter is formatted according to the formatting rules of address fields as described in 3GPP TS 23.040.
     *                  This parameter shall be present when the feature «SM filtering by the HPLMN»
     *                  is supported by the SMS-GMSC and when the equivalent parameter
     *                  is received from the short message service relay sub-layer protocol.
     * @param sM_RP_SMEA (C) Represents the RP-Originating SME-address of the Short Message Entity (SME) that has originated the SM.
     *                   This parameter is used by the short message service relay sub-layer protocol
     *                   and is formatted according to the formatting rules of address fields as described in 3GPP TS 23.040.
     *                   This parameter shall be present when the feature «SM filtering by the HPLMN»
     *                   is supported by the SMS-GMSC and when the equivalent parameter
     *                   is received from the short message service relay sub-layer protocol.
     * @param smDeliveryNotIntended (C) indicates by its presence that delivery of a short message is not intended.
     *                              It further indicates whether only IMSI or only MCC+MNC are requested.
     * @param ipSmGwGuidanceIndicator (C) Indicates whether or not the SMS-GMSC is prepared to receive IP-SM-GW Guidance in the response.
     * @param imsi (C) Contains the International Mobile Subscriber Identity defined in 3GPP TS 23.003.
     *             IMSI shall be present if MSISDN is not available.
     *             When SRISM is sent by the SMS-GMSC to the HLR in a retry context of SMS for IMS UE to IMS UE without MSISDN (see 3GPP TS 23.204),
     *             IMSI may not be available. In this case the IMSI parameter shall be populated with the HLR-ID value.
     * @param t4TriggerIndicator (C) indicates by its presence that the request is sent in the context of T4 device triggering (see 3GPP TS 23.682).
     *                           When received, the HLR may return up to three serving node numbers
     *                           and shall not forward the request to an IP-SM-GW or SMS Router.
     * @param singleAttemptDelivery (C) indicates the short message is only valid for delivering once,
     *                              and the HLR/HSS does not need to add the received SC address into MWD list
     *                              in the case there is no serving node available to provide SMS to the user.
     * @param teleserviceCode (only in MAP V1)
     * @param correlationID (C) Contains the SIP-URI-B identifying the (MSISDN-less) destination user.
     *                      SIP-URI-A and HLR-ID shall be absent from this parameter.
     *                      The Correlation ID indicates by its presence that the request is sent
     *                      in the context of MSISDN-less SMS delivery in IMS (see 3GPP TS 23.204).
     *                      When received, the HLR shall return the IP-SM-GW number and shall not forward the request to an IP-SM-GW.
     * @param smsfSupportIndicator (C) indicates that the requesting node is capable of receiving ISDN numbers
     *                             and/or Diameter addresses of the SMSF as target of MT-SMS.
     *
     * @return invokeId
     */
    Long addSendRoutingInfoForSMRequest(ISDNAddressString msisdn, boolean sm_RP_PRI, AddressString serviceCentreAddress,
            MAPExtensionContainer extensionContainer, boolean gprsSupportIndicator, SM_RP_MTI sM_RP_MTI, SM_RP_SMEA sM_RP_SMEA,
            SMDeliveryNotIntended smDeliveryNotIntended, boolean ipSmGwGuidanceIndicator, IMSI imsi, boolean t4TriggerIndicator,
            boolean singleAttemptDelivery, TeleserviceCode teleserviceCode, CorrelationID correlationID, boolean smsfSupportIndicator) throws MAPException;

    Long addSendRoutingInfoForSMRequest(int customInvokeTimeout, ISDNAddressString msisdn, boolean sm_RP_PRI,
            AddressString serviceCentreAddress, MAPExtensionContainer extensionContainer, boolean gprsSupportIndicator,
            SM_RP_MTI sM_RP_MTI, SM_RP_SMEA sM_RP_SMEA, SMDeliveryNotIntended smDeliveryNotIntended,
            boolean ipSmGwGuidanceIndicator, IMSI imsi, boolean t4TriggerIndicator, boolean singleAttemptDelivery,
            TeleserviceCode teleserviceCode, CorrelationID correlationID, boolean smsfSupportIndicator) throws MAPException;

    /**
     * Sending MAP-SEND-ROUTING-INFO-FOR-SM response
     *
     * @param invokeId (M)
     * @param imsi (M) Contains the International Mobile Subscriber Identity defined in 3GPP TS 23.003.
     *             If enforcement of routing a SM via the HPLMN of the receiving MS is deployed,
     *             this parameter contains an MT Correlation ID instead of an IMSI when the service is used between SMS-GMSC and SMS Router
     *             (see 3GPP TS 23.040 for more information).
     *             If the "SM-Delivery Not Intended" parameter was present in the Indication with a value of "only MCC+MNC requested",
     *             then this parameter may contain MCC+MNC+dummy MSIN.
     *             The presence of this parameter is mandatory in a successful case.
     * @param locationInfoWithLMSI (M) composed of:
     *                             networkNode-Number                   [1] ISDN-AddressString,
     *                             lmsi                                     LMSI                       OPTIONAL,
     *                             extensionContainer                       ExtensionContainer         OPTIONAL,
     *                             ...,
     *                             gprsNodeIndicator                    [5] NULL                       OPTIONAL,
     *                             -- gprsNodeIndicator is set only if the SGSN number is sent as the Network Node Number
     *                             additional-Number                    [6] Additional-Number          OPTIONAL,
     *                             networkNodeDiameterAddress           [7] NetworkNodeDiameterAddress OPTIONAL,
     *                             additionalNetworkNodeDiameterAddress [8] NetworkNodeDiameterAddress OPTIONAL,
     *                             thirdNumber                          [9] Additional-Number          OPTIONAL,
     *                             thirdNetworkNodeDiameterAddress     [10] NetworkNodeDiameterAddress OPTIONAL,
     *                             imsNodeIndicator                    [11] NULL                       OPTIONAL,
     *                             -- gprsNodeIndicator and imsNodeIndicator shall not both be present.
     *                             -- additionalNumber and thirdNumber shall not both contain the same type of number.
     *                             smsf-3gpp-Number                    [12] ISDN-AddressString         OPTIONAL,
     *                             smsf-3gpp-DiameterAddress           [13] NetworkNodeDiameterAddress OPTIONAL,
     *                             smsf-non-3gpp-Number                [14] ISDN-AddressString         OPTIONAL,
     *                             smsf-non-3gpp-DiameterAddress       [15] NetworkNodeDiameterAddress OPTIONAL,
     *                             smsf-3gpp-address-indicator         [16] NULL                       OPTIONAL,
     *                             smsf-non-3gpp-address-indicator     [17] NULL                       OPTIONAL
     *                             --
     *                             -- If smsf-supportIndicator was not included in the request, in RoutingInfoForSM-Arg,
     *                             -- then smsf-3gpp Number/DiameterAddress, smsf-non-3gpp Number/DiameterAddress and
     *                             -- smsf-address-indicator and smsf-non-3gpp-address-indicator shall be absent.
     *                             --
     *                             -- If smsf-3gpp-address-indicator is present, it indicates that the networkNode-Number
     *                             -- (and networkNodeDiameterAddress, if present) contains the address of an SMSF for 3GPP access.
     *                             --
     *                             -- If smsf-non-3gpp-address-indicator is present, it indicates that the
     *                             -- networkNode-Number (and networkNodeDiameterAddress, if present) contains the
     *                             -- address of an SMSF for non 3GPP access.
     *                             --
     *                             -- At most one of gprsNodeIndicator, imsNodeIndicator, smsf-3gpp-address-indicator
     *                             -- and smsf-non-3gpp-address-indicator shall be present. Absence of all these
     *                             -- indicators indicate that the networkNode-Number (and networkNodeDiameterAddress,
     *                             -- if present) contains the address of an MSC/MME.
     * @param extensionContainer (O)
     * @param mwdSet (C) (must be absent in MAP version greater than 1)
     * @param ipSmGwGuidance (U) Contains the recommended and the minimum timer values for supervision of MT-FSM response.
     *                       Shall be absent if the IP-SM-GW-Guidance Support Indicator in the request is absent.
     *                       This parameter is only used by IP-SM-GW and SMS-GMSC
     */
    void addSendRoutingInfoForSMResponse(long invokeId, IMSI imsi, LocationInfoWithLMSI locationInfoWithLMSI,
            MAPExtensionContainer extensionContainer, Boolean mwdSet, IpSmGwGuidance ipSmGwGuidance) throws MAPException;

    /**
     * Sending MAP-REPORT-SM-DELIVERY-STATUS request
     *
     * @param msisdn (M) Refers to one of the ISDN numbers assigned to a mobile subscriber in accordance with CCITT Recommendation E.213.
     *               When RSMDS is sent by the SMS-GMSC to the HLR following an T4 Submit Trigger (see 3GPP TS 23.682), MSISDN may not be available.
     *               In this case the UE shall be identified by the IMSI and the MSISDN shall take the dummy MSISDN value (see clause 3 of 3GPP TS 23.003).
     *               When RSMDS is sent by the SMS-GMSC to the HLR in a retry context of SMS for IMS UE to IMS UE without MSISDN (see 3GPP TS 23.204),
     *               MSISDN may not be available. In this case the UE shall be identified by a Correlation ID (SIP-URI-B) and
     *               the MSISDN shall take the dummy MSISDN value (see clause 3 of 3GPP TS 23.003).
     * @param serviceCentreAddress (M) Represents the address of a Short Message Service Centre (SMSC).
     * @param sMDeliveryOutcome (M) Indicates the status of the mobile terminated SM delivery
     *                          (three possible values: memoryCapacityExceeded(0), absentSubscriber(1), successfulTransfer(2)).
     * @param absentSubscriberDiagnosticSM (M) used to indicate the reason why the subscriber is absent.
     *                                     For the values for this parameter see 3GPP TS 23.040.
     * @param extensionContainer (C)
     * @param gprsSupportIndicator (C) Indicates that the SMS-GMSC supports GPRS specific procedure of
     *                             combine delivery of Short Message via MSC and/or via the SGSN.
     *                             The presence of this parameter is mandatory if the SMS-GMSC supports handling of two delivery outcomes.
     * @param deliveryOutcomeIndicator (C) Indicates that the delivery outcome sent to the HLR is for GPRS.
     * @param additionalSMDeliveryOutcome (C) Used to indicate the GPRS delivery outcome in case a combination
     *                                    between delivery outcome for GPRS and non-GPRS are sent to the HLR.
     * @param additionalAbsentSubscriberDiagnosticSM (C) Indicates the reason of the additional SM Delivery Outcome.
     * @param ipSmGwIndicator (C) Indicates by its presence that sm-deliveryOutcome is for delivery via IMS.
     * @param ipSmGwSMDeliveryOutcome (C) Used to indicate the delivery outcome for the IMS domain.
     * @param ipSmGwAbsentSubscriberDiagnosticSM (C) Indicates the reason of the IP-SM-GW SM Delivery Outcome.
     * @param imsi (C) Contains the International Mobile Subscriber Identity defined in 3GPP TS 23.003.
     *             When RSMDS is sent by the SMS-GMSC to the HLR in a retry context of SMS for IMS UE to IMS UE without MSISDN (see 3GPP TS 23.204),
     *             IMSI may not be available. In this case the IMSI parameter shall be populated with an HLR-ID).
     * @param singleAttemptDelivery (C) Indicates the short message is only valid for delivering once,
     *                              and the HLR/HSS does not need to add the received SC address into MWD list.
     *                              It may only be present in the case the delivery of the short message failed
     *                              due to absent subscriber or MS memory capacity exceeded.
     * @param correlationID (C) Contains the SIP-URI-B identifying the (MSISDN-less) destination user.
     *                      SIP-URI-A and HLR-ID shall be absent from this parameter.
     * @param smsf3gppDeliveryOutcomeIndicator (C) Indicates that the delivery outcome IE is associated to the SM delivery via the SMSF for 3GPP access.
     * @param smsf3gppDeliveryOutcome (C) Used to indicate the delivery outcome at the SMSF for 3GPP access.
     * @param smsf3gppAbsentSubscriberDiagnosticSM (C) Used to indicate the reason why the subscriber is absent for 5G 3GPP access.
     *                                             For the values for this parameter see 3GPP TS 23.040.
     * @param smsfNon3gppDeliveryOutcomeIndicator (C) Used to indicate the delivery outcome at the SMSF for non-3GPP access.
     * @param smsfNon3gppDeliveryOutcome (C) Indicates that the delivery outcome IE is associated to the SM delivery via the SMSF for Non-3GPP access.
     * @param smsfNon3gppAbsentSubscriberDiagnosticSM (C) Used to indicate the reason why the subscriber is absent for 5G Non 3GPP access.
     *                                                For the values for this parameter see 3GPP TS 23.040.
     *
     * @return invokeId
     */
    Long addReportSMDeliveryStatusRequest(ISDNAddressString msisdn, AddressString serviceCentreAddress, SMDeliveryOutcome sMDeliveryOutcome,
            Integer absentSubscriberDiagnosticSM, MAPExtensionContainer extensionContainer, boolean gprsSupportIndicator, boolean deliveryOutcomeIndicator,
            SMDeliveryOutcome additionalSMDeliveryOutcome, Integer additionalAbsentSubscriberDiagnosticSM,
            boolean ipSmGwIndicator, SMDeliveryOutcome ipSmGwSMDeliveryOutcome, Integer ipSmGwAbsentSubscriberDiagnosticSM,
            IMSI imsi, boolean singleAttemptDelivery, CorrelationID correlationID, boolean smsf3gppDeliveryOutcomeIndicator,
            SMDeliveryOutcome smsf3gppDeliveryOutcome, Integer smsf3gppAbsentSubscriberDiagnosticSM, boolean smsfNon3gppDeliveryOutcomeIndicator,
            SMDeliveryOutcome smsfNon3gppDeliveryOutcome, Integer smsfNon3gppAbsentSubscriberDiagnosticSM) throws MAPException;

    Long addReportSMDeliveryStatusRequest(int customInvokeTimeout, ISDNAddressString msisdn, AddressString serviceCentreAddress,
            SMDeliveryOutcome sMDeliveryOutcome, Integer absentSubscriberDiagnosticSM, MAPExtensionContainer extensionContainer, boolean gprsSupportIndicator,
            boolean deliveryOutcomeIndicator, SMDeliveryOutcome additionalSMDeliveryOutcome, Integer additionalAbsentSubscriberDiagnosticSM,
            boolean ipSmGwIndicator, SMDeliveryOutcome ipSmGwSMDeliveryOutcome, Integer ipSmGwAbsentSubscriberDiagnosticSM,
            IMSI imsi, boolean singleAttemptDelivery, CorrelationID correlationID, boolean smsf3gppDeliveryOutcomeIndicator,
            SMDeliveryOutcome smsf3gppDeliveryOutcome, Integer smsf3gppAbsentSubscriberDiagnosticSM, boolean smsfNon3gppDeliveryOutcomeIndicator,
            SMDeliveryOutcome smsfNon3gppDeliveryOutcome, Integer smsfNon3gppAbsentSubscriberDiagnosticSM)
            throws MAPException;

    /**
     * Sending MAP-REPORT-SM-DELIVERY-STATUS response
     *
     * @param storedMSISDN (C) (a.k.a MSIsdn-Alert) Shall be present in case of unsuccessful delivery,
     *                    when the MSISDN received in the operation is different from the stored MSIsdn-Alert;
     *                    the stored MSIsdn-Alert is the value that is returned to the gateway MSC.
     * @param extensionContainer (C)
     */
    void addReportSMDeliveryStatusResponse(long invokeId, ISDNAddressString storedMSISDN,
            MAPExtensionContainer extensionContainer) throws MAPException;

    /**
     * Sending MAP-INFORM-SERVICE-CENTRE request
     *
     * @param storedMSISDN (C) Refers to the MSISDN stored in a MWD file in the HLR.
     * @param mwStatus (C) Indicates the status of the MCEF, MNRF, MNRG, MNR5G and MNR5GN3G flags
     *                 and the status of the particular SC address presence in the Message Waiting Data list.
     * @param extensionContainer optional
     * @param absentSubscriberDiagnosticSM (C) used to indicate the reason why the subscriber is absent.
     *                                     For the values for this parameter see 3GPP TS 23.040.
     *                                     If the HLR has stored a single MNRR,
     *                                     the value is included in the absentSubscriberDiagnosticSM parameter
     * @param additionalAbsentSubscriberDiagnosticSM (C) Indicates the reason of the additional SM Delivery Outcome.
     *                                               If the HLR has stored a second MNRR,
     *                                               the value of the MNRR for the MSC is included in the absentSubscriberDiagnosticSM parameter
     *                                               and the value of the MNRR for the SGSN is included in the
     *                                               additionalAbsentSubscriberDiagnosticSM parameter
     * @param smsf3gppAbsentSubscriberDiagnosticSM (C) Used to indicate the reason why the subscriber is absent for 5G 3GPP access.
     *                                             For the values for this parameter see 3GPP TS 23.040.
     * @param smsfNon3gppAbsentSubscriberDiagnosticSM (C) used to indicate the reason why the subscriber is absent for 5G Non 3GPP access.
     *                                                For the values for this parameter see 3GPP TS 23.040.
     */
    Long addInformServiceCentreRequest(ISDNAddressString storedMSISDN, MWStatus mwStatus,
            MAPExtensionContainer extensionContainer, Integer absentSubscriberDiagnosticSM,
            Integer additionalAbsentSubscriberDiagnosticSM, Integer smsf3gppAbsentSubscriberDiagnosticSM,
            Integer smsfNon3gppAbsentSubscriberDiagnosticSM) throws MAPException;

    Long addInformServiceCentreRequest(int customInvokeTimeout, ISDNAddressString storedMSISDN, MWStatus mwStatus,
            MAPExtensionContainer extensionContainer, Integer absentSubscriberDiagnosticSM,
            Integer additionalAbsentSubscriberDiagnosticSM, Integer smsf3gppAbsentSubscriberDiagnosticSM,
            Integer smsfNon3gppAbsentSubscriberDiagnosticSM) throws MAPException;

    /**
     * Sending MAP-ALERT-SERVICE-CENTRE request
     *
     * @param msisdn (M) Refers to one of the ISDN numbers assigned to a mobile subscriber in accordance with CCITT Recommendation E.213.
     *               When the service is used between the HLR and the SMS-IWMSC, the provided MSISDN shall be the one which is stored in the MWD file.
     *               If no MSISDN is available, the dummy MSISDN value (see clause 3 of 3GPP TS 23.003)
     *               shall be sent and an IMSI or Correlation ID (SIP-URI-B) shall be present.
     *               When the service is used between an MME (via an IWF), SGSN or MSC and the SMS-GMSC,
     *               the dummy MSISDN value (see clause 3 of 3GPP TS 23.003) shall be sent and an IMSI shall be present.
     * @param serviceCentreAddress (M) Represents the address of a Short Message Service Centre (SMSC).
     * @param imsi (C) Contains the International Mobile Subscriber Identity defined in 3GPP TS 23.003.
     *             When the service is used between the HLR and the SMS-IWMSC,
     *             the provided IMSI shall be the identifier which is stored in the MWD file
     *             if no MSISDN is available in the context of T4 device triggering (see 3GPP TS 23.682).
     *             When the service is used between an MME (via an IWF), SGSN or MSC and the SMS-GMSC,
     *             this information element shall contain the IMSI in the request sent from the MME (via an IWF), SGSN or MSC,
     *             or the User Identifier Alert previously sent in the MT Forward Short Message response,
     *             when the request is sent from the SMS Router to the SMS-GMSC.
     * @param correlationID (C) When the service is used between the HLR and the SMS-IWMSC,
     *                      the provided SIP-URI-B within the Correlation ID parameter shall be the identifier
     *                      which is stored in the MWD file if no MSISDN is available
     *                      in a retry context of SMS for IMS UE to IMS UE without MSISDN (see 3GPP TS 23.204).
     *                      HLR-ID and SIP-URI-A shall be absent.
     * @param maximumUeAvailabilityTime (C) Indicates the timestamp (in UTC) until which a UE using a power saving mechanism
     *                                  (such as extended idle mode DRX) is expected to be reachable for SM Delivery.
     *                                  It may be included by the SGSN or MSC when notifying the HLR that the MS is reachable.
     * @param smsGmscAlertEvent (C) Indicates the event that causes the MME (via an IWF) or the SGSN
     *                          to alert the SMS-GMSC for retransmitting an MT Short Message.
     * @param smsGmscDiameterAddress (C) Shall contain, if available, the Diameter Identity of the SMS-GMSC (or SMS Router)
     *                               previously received in the SMS-GMSC Diameter Address IE in the MT Forward Short Message Request.
     * @param newSGSNNumber (C) May be included if the SMS-GMSC Alert Event indicates that the MS has moved under the coverage of another MME.
     *                      When present, it shall contain the E.164 number of the new MME serving the MS
     * @param newSGSNDiameterAddress (C) Shall be included if available and if the SMS-GMSC Alert Event
     *                               indicates that the MS has moved under the coverage of another SGSN.
     *                               When present, it shall contain the Diameter Identity of the new SGSN serving the MS.
     * @param newMMENumber (C) May be included if the SMS-GMSC Alert Event indicates that the MS has moved under the coverage of another MME.
     *                     When present, it shall contain the E.164 number of the new MME serving the MS.
     * @param newMMEDiameterAddress (C) Shall be included if available and if the SMS-GMSC Alert Event
     *                              indicates that the MS has moved under the coverage of another MME.
     *                              When present, it shall contain the Diameter Identity of the new MME serving the MS.
     * @param newMSCNumber (C) May be included if the SMS-GMSC Alert Event indicates that the MS has moved under the coverage of another MSC.
     *                     When present, it shall contain the E.164 number of the new MSC serving the MS.
     */
    Long addAlertServiceCentreRequest(ISDNAddressString msisdn, AddressString serviceCentreAddress, IMSI imsi,
            CorrelationID correlationID, Time maximumUeAvailabilityTime, SmsGmscAlertEvent smsGmscAlertEvent,
            NetworkNodeDiameterAddress smsGmscDiameterAddress, ISDNAddressString newSGSNNumber,
            NetworkNodeDiameterAddress newSGSNDiameterAddress, ISDNAddressString newMMENumber,
            NetworkNodeDiameterAddress newMMEDiameterAddress, ISDNAddressString newMSCNumber) throws MAPException;

    Long addAlertServiceCentreRequest(int customInvokeTimeout, ISDNAddressString msisdn, AddressString serviceCentreAddress, IMSI imsi,
            CorrelationID correlationID, Time maximumUeAvailabilityTime, SmsGmscAlertEvent smsGmscAlertEvent,
            NetworkNodeDiameterAddress smsGmscDiameterAddress, ISDNAddressString newSGSNNumber,
            NetworkNodeDiameterAddress newSGSNDiameterAddress, ISDNAddressString newMMENumber,
            NetworkNodeDiameterAddress newMMEDiameterAddress, ISDNAddressString newMSCNumber) throws MAPException;

    void addAlertServiceCentreResponse(long invokeId) throws MAPException;

    /**
     * Sending MAP-READY-FOR-SM request
     *
     * @param imsi (C) Contains the International Mobile Subscriber Identity defined in 3GPP TS 23.003.
     *             The IMSI is used always between the VLR and the HLR and between the SGSN and the HLR and between the HSS and the IWF.
     *             Between the MSC and the VLR the identification can be either IMSI or TMSI.
     * @param alertReason (M) Indicates if the mobile subscriber is present or the MS has memory available.
     * @param alertReasonIndicator (C) Indicates by its presence that the message is sent from SGSN,
     *                             and by its absence that the message is sent from VLR or MME via IWF.
     * @param extensionContainer (C)
     * @param additionalAlertReasonIndicator (C) Indicates that the alert reason is sent to the HLR due to IMS activity.
     * @param maximumUeAvailabilityTime (C) indicates the timestamp (in UTC) until which a UE using a power saving mechanism
     *                                  (such as extended idle mode DRX or eDRX) is expected to be reachable for SM Delivery.
     *                                  It may be included by the SGSN or MSC when notifying the HLR that the MS is reachable.
     *
     * @return invokeId
     */
    Long addReadyForSMRequest(IMSI imsi, AlertReason alertReason, boolean alertReasonIndicator, MAPExtensionContainer extensionContainer,
            boolean additionalAlertReasonIndicator, Time maximumUeAvailabilityTime) throws MAPException;

    Long addReadyForSMRequest(int customInvokeTimeout, IMSI imsi, AlertReason alertReason, boolean alertReasonIndicator,
            MAPExtensionContainer extensionContainer, boolean additionalAlertReasonIndicator, Time maximumUeAvailabilityTime) throws MAPException;

    /**
     * Sending MAP-READY-FOR-SM response
     *
     * @param invokeId (M)
     * @param extensionContainer (C)
     */
    void addReadyForSMResponse(long invokeId, MAPExtensionContainer extensionContainer) throws MAPException;

    /**
     * @param imsi (M)
     * @return invokeId
     */
    Long addNoteSubscriberPresentRequest(IMSI imsi) throws MAPException;

    Long addNoteSubscriberPresentRequest(int customInvokeTimeout, IMSI imsi) throws MAPException;

}
