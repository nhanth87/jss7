
package org.restcomm.protocols.ss7.map.load.sms.mt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContext;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextName;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextVersion;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.MAPProvider;
import org.restcomm.protocols.ss7.map.api.dialog.MAPAbortProviderReason;
import org.restcomm.protocols.ss7.map.api.dialog.MAPAbortSource;
import org.restcomm.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic;
import org.restcomm.protocols.ss7.map.api.dialog.MAPRefuseReason;
import org.restcomm.protocols.ss7.map.api.dialog.MAPUserAbortChoice;
import org.restcomm.protocols.ss7.map.api.dialog.ServingCheckData;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageAbsentSubscriberSM;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.service.lsm.AdditionalNumber;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.InformServiceCentreRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.IpSmGwGuidance;
import org.restcomm.protocols.ss7.map.api.service.sms.LocationInfoWithLMSI;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPDialogSms;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPServiceSmsListener;
import org.restcomm.protocols.ss7.map.api.service.sms.MoForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.MoForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.MtForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.MtForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.NoteSubscriberPresentRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ReadyForSMRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ReadyForSMResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.ReportSMDeliveryStatusRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ReportSMDeliveryStatusResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMResponse;
import org.restcomm.protocols.ss7.map.errors.MAPErrorMessageAbsentSubscriberSMImpl;
import org.restcomm.protocols.ss7.map.load.ConsoleTui;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.LMSIImpl;
import org.restcomm.protocols.ss7.map.service.sms.LocationInfoWithLMSIImpl;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.tcap.asn.ApplicationContextName;
import org.restcomm.protocols.ss7.tcap.asn.ReturnResultLastImpl;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;
import org.restcomm.protocols.ss7.tcap.asn.comp.ReturnResultLast;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @modified <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class Server extends TestHarnessSmsMt {

    private static final Logger log = LogManager.getLogger(Server.class);

    private int successRate = 100;

    // MAP
    private MAPProvider mapProvider;

    final AtomicLong endCount = new AtomicLong();
    volatile long start = System.currentTimeMillis();

    private final AtomicLong receivedCount = new AtomicLong();
    private final AtomicLong processedCount = new AtomicLong();
    private final AtomicLong errorCount = new AtomicLong();
    private final Set<Long> failedDialogs = ConcurrentHashMap.newKeySet();
    private ConsoleTui tui;

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogDelimiter
     * (org.restcomm.protocols.ss7.map.api.MAPDialog)
     */
    @Override
    public void onDialogDelimiter(MAPDialog mapDialog) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("onDialogDelimiter for DialogId=%d", mapDialog.getLocalDialogId()));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogRequest
     * (org.restcomm.protocols.ss7.map.api.MAPDialog, org.restcomm.protocols.ss7.map.api.primitives.AddressString,
     * org.restcomm.protocols.ss7.map.api.primitives.AddressString,
     * org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer)
     */
    @Override
    public void onDialogRequest(MAPDialog mapDialog, AddressString destReference, AddressString origReference,
            MAPExtensionContainer extensionContainer) {
        if (log.isDebugEnabled()) {
            log.debug(String.format(
                    "onDialogRequest for DialogId=%d DestinationReference=%s OriginReference=%s MAPExtensionContainer=%s",
                    mapDialog.getLocalDialogId(), destReference, origReference, extensionContainer));
        }
    }

    @Override
    public void onDialogRequestEricsson(MAPDialog mapDialog, AddressString destReference, AddressString origReference,
            AddressString imsi, AddressString vlr) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("onDialogRequest for DialogId=%d DestinationReference=%s OriginReference=%s ",
                    mapDialog.getLocalDialogId(), destReference, origReference));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogAccept( org.restcomm.protocols.ss7.map.api.MAPDialog,
     * org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer)
     */
    @Override
    public void onDialogAccept(MAPDialog mapDialog, MAPExtensionContainer extensionContainer) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("onDialogAccept for DialogId=%d MAPExtensionContainer=%s", mapDialog.getLocalDialogId(),
                    extensionContainer));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogReject( org.restcomm.protocols.ss7.map.api.MAPDialog,
     * org.restcomm.protocols.ss7.map.api.dialog.MAPRefuseReason, org.restcomm.protocols.ss7.map.api.dialog.MAPProviderError,
     * org.restcomm.protocols.ss7.tcap.asn.ApplicationContextName,
     * org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer)
     */
    @Override
    public void onDialogReject(MAPDialog mapDialog, MAPRefuseReason refuseReason, ApplicationContextName alternativeApplicationContext,
                               MAPExtensionContainer extensionContainer) {
        log.error(String.format(
                "onDialogReject for DialogId=%d MAPRefuseReason=%s ApplicationContextName=%s MAPExtensionContainer=%s",
                mapDialog.getLocalDialogId(), refuseReason, alternativeApplicationContext, extensionContainer));
        markDialogFailed(mapDialog);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogUserAbort
     * (org.restcomm.protocols.ss7.map.api.MAPDialog, org.restcomm.protocols.ss7.map.api.dialog.MAPUserAbortChoice,
     * org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer)
     */
    @Override
    public void onDialogUserAbort(MAPDialog mapDialog, MAPUserAbortChoice userReason, MAPExtensionContainer extensionContainer) {
        log.error(String.format("onDialogUserAbort for DialogId=%d MAPUserAbortChoice=%s MAPExtensionContainer=%s",
                mapDialog.getLocalDialogId(), userReason, extensionContainer));
        markDialogFailed(mapDialog);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogProviderAbort
     * (org.restcomm.protocols.ss7.map.api.MAPDialog, org.restcomm.protocols.ss7.map.api.dialog.MAPAbortProviderReason,
     * org.restcomm.protocols.ss7.map.api.dialog.MAPAbortSource,
     * org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer)
     */
    @Override
    public void onDialogProviderAbort(MAPDialog mapDialog, MAPAbortProviderReason abortProviderReason, MAPAbortSource abortSource,
                                      MAPExtensionContainer extensionContainer) {
        log.error(String.format(
                "onDialogProviderAbort for DialogId=%d MAPAbortProviderReason=%s MAPAbortSource=%s MAPExtensionContainer=%s",
                mapDialog.getLocalDialogId(), abortProviderReason, abortSource, extensionContainer));
        markDialogFailed(mapDialog);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogClose(org .mobicents.protocols.ss7.map.api.MAPDialog)
     */
    @Override
    public void onDialogClose(MAPDialog mapDialog) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("DialogClose for Dialog=%d", mapDialog.getLocalDialogId()));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogNotice( org.restcomm.protocols.ss7.map.api.MAPDialog,
     * org.restcomm.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic)
     */
    @Override
    public void onDialogNotice(MAPDialog mapDialog, MAPNoticeProblemDiagnostic noticeProblemDiagnostic) {
        log.error(String.format("onDialogNotice for DialogId=%d MAPNoticeProblemDiagnostic=%s ",
                mapDialog.getLocalDialogId(), noticeProblemDiagnostic));
        markDialogFailed(mapDialog);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogResease
     * (org.restcomm.protocols.ss7.map.api.MAPDialog)
     */
    @Override
    public void onDialogRelease(MAPDialog mapDialog) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("onDialogRelease for DialogId=%d", mapDialog.getLocalDialogId()));
        }

        this.endCount.incrementAndGet();
        if (!failedDialogs.remove(mapDialog.getLocalDialogId())) {
            processedCount.incrementAndGet();
        }

        if ((this.endCount.get() % 10000) == 0) {
            long currentTime = System.currentTimeMillis();
            long processingTime = currentTime - start;
            start = currentTime;
            log.warn("Completed 10000 Dialogs in " + processingTime + " milliseconds");
        }

    }

    private void markDialogFailed(MAPDialog mapDialog) {
        if (failedDialogs.add(mapDialog.getLocalDialogId())) {
            errorCount.incrementAndGet();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogTimeout
     * (org.restcomm.protocols.ss7.map.api.MAPDialog)
     */
    @Override
    public void onDialogTimeout(MAPDialog mapDialog) {
        log.error(String.format("onDialogTimeout for DialogId=%d", mapDialog.getLocalDialogId()));
        markDialogFailed(mapDialog);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPServiceListener#onErrorComponent
     * (org.restcomm.protocols.ss7.map.api.MAPDialog, java.lang.Long,
     * org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage)
     */
    @Override
    public void onErrorComponent(MAPDialog mapDialog, Long invokeId, MAPErrorMessage mapErrorMessage) {
        log.error(String.format("onErrorComponent for Dialog=%d and invokeId=%d MAPErrorMessage=%s",
                mapDialog.getLocalDialogId(), invokeId, mapErrorMessage));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPServiceListener#onRejectComponent
     * (org.restcomm.protocols.ss7.map.api.MAPDialog, java.lang.Long, org.restcomm.protocols.ss7.tcap.asn.comp.Problem)
     */
    @Override
    public void onRejectComponent(MAPDialog mapDialog, Long invokeId, Problem problem, boolean isLocalOriginated) {
        log.error(String.format("onRejectComponent for Dialog=%d and invokeId=%d Problem=%s isLocalOriginated=%s",
                mapDialog.getLocalDialogId(), invokeId, problem, isLocalOriginated));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPServiceListener#onInvokeTimeout
     * (org.restcomm.protocols.ss7.map.api.MAPDialog, java.lang.Long)
     */
    @Override
    public void onInvokeTimeout(MAPDialog mapDialog, Long invokeId) {
        log.error(String.format("onInvokeTimeout for Dialog=%d and invokeId=%d", mapDialog.getLocalDialogId(), invokeId));
    }

    public static void main(String[] args) {
        // JSON config path → use Ss7StackBuilder
        if (args.length == 1 && args[0].endsWith(".json")) {
            try {
                new Server().startWithJson(args[0]);
            } catch (Exception e) {
                log.error("Failed to start server", e);
                System.exit(1);
            }
            return;
        }
        log.error("Usage: Server <config.json>");
        System.exit(1);
    }

    @Override
    public void onMAPMessage(MAPMessage mapMessage) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onForwardShortMessageRequest(ForwardShortMessageRequest forwardShortMessageRequestIndication) {

    }

    @Override
    public void onForwardShortMessageResponse(ForwardShortMessageResponse forwardShortMessageResponseIndication) {

    }

    @Override
    public void onMoForwardShortMessageRequest(MoForwardShortMessageRequest moForwardShortMessageRequestIndication) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("onMoForwardShortMessageRequest for DialogId=%d", moForwardShortMessageRequestIndication
                .getMAPDialog().getLocalDialogId()));
        }
        try {
            long invokeId = moForwardShortMessageRequestIndication.getInvokeId();
            MAPDialogSms mapDialogSms = moForwardShortMessageRequestIndication.getMAPDialog();
            mapDialogSms.setUserObject(invokeId);
            mapDialogSms.close(false);

        } catch (MAPException e) {
            log.error("Error while sending MoForwardShortMessageRequest ", e);
        }
    }

    @Override
    public void onMoForwardShortMessageResponse(MoForwardShortMessageResponse moForwardShortMessageResponseIndication) {

    }

    @Override
    public void onMtForwardShortMessageRequest(MtForwardShortMessageRequest mtForwardShortMessageRequestIndication) {
        receivedCount.incrementAndGet();
        if (log.isDebugEnabled()) {
            log.debug(String.format("onMtForwardShortMessageRequest for DialogId=%d", mtForwardShortMessageRequestIndication
                .getMAPDialog().getLocalDialogId()));
        }
        try {
            long invokeId = mtForwardShortMessageRequestIndication.getInvokeId();
            MAPDialogSms mapDialogSms = mtForwardShortMessageRequestIndication.getMAPDialog();
            mapDialogSms.setUserObject(invokeId);

            Random rand = new Random();
            int responseChoice = rand.nextInt(4);
            MAPErrorMessageAbsentSubscriberSM errorMessageAbsentSubscriberSM = null;

            switch (successRate) {
                case 0: // NULL 0%
                    errorMessageAbsentSubscriberSM = new MAPErrorMessageAbsentSubscriberSMImpl();
                    mapDialogSms.sendErrorComponent(invokeId, errorMessageAbsentSubscriberSM);
                    mapDialogSms.close(false);
                    break;
                case 25: // LOWER 25%
                    switch (responseChoice) {
                        case 0:
                            ReturnResultLast returnResultLast = new ReturnResultLastImpl();
                            returnResultLast.setInvokeId(invokeId);
                            mapDialogSms.sendReturnResultLastComponent(returnResultLast);
                            mapDialogSms.close(false);
                            break;
                        case 1:
                        case 2:
                        case 3:
                            errorMessageAbsentSubscriberSM = new MAPErrorMessageAbsentSubscriberSMImpl();
                            mapDialogSms.sendErrorComponent(invokeId, errorMessageAbsentSubscriberSM);
                            mapDialogSms.close(false);
                            break;
                    }
                    break;
                case 50: // MEDIUM 50%
                    switch (responseChoice) {
                        case 0:
                        case 1:
                            ReturnResultLast returnResultLast = new ReturnResultLastImpl();
                            returnResultLast.setInvokeId(invokeId);
                            mapDialogSms.sendReturnResultLastComponent(returnResultLast);
                            mapDialogSms.close(false);
                            break;
                        case 2:
                        case 3:
                            errorMessageAbsentSubscriberSM = new MAPErrorMessageAbsentSubscriberSMImpl();
                            mapDialogSms.sendErrorComponent(invokeId, errorMessageAbsentSubscriberSM);
                            mapDialogSms.close(false);
                            break;
                    }
                    break;
                case 75: // HIGHER 75%
                    switch (responseChoice) {
                        case 0:
                        case 1:
                        case 2:
                            ReturnResultLast returnResultLast = new ReturnResultLastImpl();
                            returnResultLast.setInvokeId(invokeId);
                            mapDialogSms.sendReturnResultLastComponent(returnResultLast);
                            mapDialogSms.close(false);
                            break;
                        case 3:
                            errorMessageAbsentSubscriberSM = new MAPErrorMessageAbsentSubscriberSMImpl();
                            mapDialogSms.sendErrorComponent(invokeId, errorMessageAbsentSubscriberSM);
                            mapDialogSms.close(false);
                            break;
                    }
                    break;
                case 100: // ALL 100%
                    ReturnResultLast returnResultLast = new ReturnResultLastImpl();
                    returnResultLast.setInvokeId(invokeId);
                    mapDialogSms.sendReturnResultLastComponent(returnResultLast);
                    mapDialogSms.close(false);
                    break;
            }
        } catch (MAPException e) {
            log.error("Error while sending MtForwardShortMessageRequest result ", e);
        }


    }

    @Override
    public void onMtForwardShortMessageResponse(MtForwardShortMessageResponse mtForwardShortMessageResponseIndication) {

    }

    @Override
    public void onSendRoutingInfoForSMRequest(SendRoutingInfoForSMRequest sendRoutingInfoForSMRequestIndication) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("onSendRoutingInfoForSMRequest for DialogId=%d", sendRoutingInfoForSMRequestIndication
                .getMAPDialog().getLocalDialogId()));
        }
        try {
            long invokeId = sendRoutingInfoForSMRequestIndication.getInvokeId();
            MAPDialogSms mapDialogSms = sendRoutingInfoForSMRequestIndication.getMAPDialog();
            mapDialogSms.setUserObject(invokeId);
            IMSI imsi = new IMSIImpl("748031234567890");
            ISDNAddressString networkNodeNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "598991900032");
            byte[] lmsiByte = null;
            Random rand = new Random();
            int lmsiRandom = rand.nextInt(4) + 1;
            switch (lmsiRandom) {
                case 1:
                    // char packet_bytes[] = {0x72, 0x02, 0xe9, 0x8c};
                    lmsiByte = new byte[]{114, 2, (byte) 233, (byte) 140};
                    break;
                case 2:
                    // char packet_bytes[] = {0x71, 0xff, 0xac, 0xce};
                    lmsiByte = new byte[]{113, (byte) 255, (byte) 172, (byte) 206};
                    break;
                case 3:
                    // char packet_bytes[] = {0x72, 0x02, 0xeb, 0x37};
                    lmsiByte = new byte[]{114, 2, (byte) 235, 55};
                    break;
                case 4:
                    // char packet_bytes[] = {0x72, 0x02, 0xe7, 0xd5};
                    lmsiByte = new byte[]{114, 2, (byte) 231, (byte) 213};
                    break;
            }
            LMSI lmsi = new LMSIImpl(lmsiByte);
            MAPExtensionContainer mapExtensionContainer = null;
            boolean gprsNodeIndicator = false;
            AdditionalNumber additionalNumber = null;
            LocationInfoWithLMSI locationInfoWithLMSI = new LocationInfoWithLMSIImpl(networkNodeNumber, lmsi, mapExtensionContainer,
                gprsNodeIndicator, additionalNumber, null, null, null, null, false, null, null, null, null, false, false);
            Boolean mwdSet = null;
            IpSmGwGuidance ipSmGwGuidance = null;
            mapDialogSms.addSendRoutingInfoForSMResponse(invokeId, imsi, locationInfoWithLMSI, mapExtensionContainer, mwdSet, ipSmGwGuidance);

            mapDialogSms.close(false);

        } catch (MAPException e) {
            log.error("Error while sending SendRoutingInfoForSMRequest ", e);
        }
    }

    @Override
    public void onSendRoutingInfoForSMResponse(SendRoutingInfoForSMResponse sendRoutingInfoForSMResponseIndication) {

    }

    @Override
    public void onReportSMDeliveryStatusRequest(ReportSMDeliveryStatusRequest reportSMDeliveryStatusRequestIndication) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("onReportSMDeliveryStatusRequest for DialogId=%d", reportSMDeliveryStatusRequestIndication
                .getMAPDialog().getLocalDialogId()));
        }
        try {
            MAPDialogSms mapDialogSms = reportSMDeliveryStatusRequestIndication.getMAPDialog();
            mapDialogSms.setUserObject(reportSMDeliveryStatusRequestIndication.getInvokeId());
            ReturnResultLast returnResultLast = new ReturnResultLastImpl();
            returnResultLast.setInvokeId(reportSMDeliveryStatusRequestIndication.getInvokeId());
            mapDialogSms.sendReturnResultLastComponent(returnResultLast);
            mapDialogSms.close(false);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AddressString destinationAddressString = reportSMDeliveryStatusRequestIndication.getMAPDialog().getReceivedOrigReference();
            AddressString originAddressString = reportSMDeliveryStatusRequestIndication.getMAPDialog().getReceivedDestReference();
            /*AddressString originAddressString = this.mapProvider.getMAPParameterFactory()
                .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "598990012345");
            AddressString destinationAddressString = this.mapProvider.getMAPParameterFactory()
                .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "598990067890");*/

            SccpAddress clientSccpAddress = reportSMDeliveryStatusRequestIndication.getMAPDialog().getRemoteAddress();
            SccpAddress serverSccpAddress = reportSMDeliveryStatusRequestIndication.getMAPDialog().getLocalAddress();
            /*SccpAddress clientSccpAddress = createSccpAddress(TestHarnessSmsMt.ROUTING_INDICATOR, TestHarnessSmsMt.ORIGINATING_PC,
                TestHarnessSmsMt.SSN, TestHarnessSmsMt.SCCP_CLIENT_ADDRESS);
            SccpAddress serverSccpAddress = createSccpAddress(TestHarnessSmsMt.ROUTING_INDICATOR, TestHarnessSmsMt.DESTINATION_PC,
                TestHarnessSmsMt.SSN, TestHarnessSmsMt.SCCP_SERVER_ADDRESS);*/
            MAPDialogSms mapDialogSmsAlertServiceCentre = this.mapProvider.getMAPServiceSms().createNewDialog(MAPApplicationContext
                    .getInstance(MAPApplicationContextName.shortMsgAlertContext, MAPApplicationContextVersion.version2),
                serverSccpAddress, originAddressString, clientSccpAddress, destinationAddressString);

            ISDNAddressString msisdn = reportSMDeliveryStatusRequestIndication.getMsisdn();
            AddressString serviceCentreAddress = reportSMDeliveryStatusRequestIndication.getServiceCentreAddress();

            mapDialogSmsAlertServiceCentre.addAlertServiceCentreRequest(msisdn, serviceCentreAddress, null, null, null, null, null, null, null, null, null, null);

            mapDialogSmsAlertServiceCentre.send();

        } catch (MAPException e) {
            log.error("Error while sending SendRoutingInfoForSMRequest ", e);
        }
    }

    @Override
    public void onReportSMDeliveryStatusResponse(ReportSMDeliveryStatusResponse reportSMDeliveryStatusResponseIndication) {

    }

    @Override
    public void onInformServiceCentreRequest(InformServiceCentreRequest informServiceCentreRequestIndication) {

    }

    @Override
    public void onAlertServiceCentreRequest(AlertServiceCentreRequest alertServiceCentreRequestIndication) {

    }

    @Override
    public void onAlertServiceCentreResponse(AlertServiceCentreResponse alertServiceCentreResponseIndication) {

    }

    @Override
    public void onReadyForSMRequest(ReadyForSMRequest readyForSMRequest) {

    }

    @Override
    public void onReadyForSMResponse(ReadyForSMResponse readyForSMResponse) {

    }

    @Override
    public void onNoteSubscriberPresentRequest(NoteSubscriberPresentRequest noteSubscriberPresentRequest) {

    }

    /** Start server from JSON config via Ss7StackBuilder. */
    public void startWithJson(String configPath) throws Exception {
        log.info("Building jSS7 stack from config: {}", configPath);
        var stack = org.restcomm.protocols.ss7.config.Ss7StackBuilder.build(java.nio.file.Path.of(configPath));
        mapProvider = stack.mapProvider();
        mapProvider.getMAPServiceSms().addMAPServiceListener(this);
        mapProvider.getMAPServiceSms().activate();
        log.info("SMS listeners registered");
        stack.sctpManagement().startServer("serverLink-srv");
        log.info("SCTP server started");
        stack.m3uaManagement().startAsp("serverLink-ASP");
        log.info("ASP started");

        this.tui = new ConsoleTui("SERVER", receivedCount, processedCount, errorCount, -1, System.err);
        this.tui.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try { if (tui != null) tui.close(); } catch (Exception ignored) {}
            if (stack != null) stack.stop();
            log.info("Server shutdown complete");
        }, "server-shutdown"));

        log.info("MT-SMS Server started");
        Thread.currentThread().join();
    }

}
