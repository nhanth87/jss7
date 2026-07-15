package org.restcomm.protocols.ss7.map.load.ussd;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.restcomm.protocols.ss7.config.Ss7Stack;
import org.restcomm.protocols.ss7.config.Ss7StackBuilder;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.MAPProvider;
import org.restcomm.protocols.ss7.map.api.datacoding.CBSDataCodingScheme;
import org.restcomm.protocols.ss7.map.api.dialog.MAPAbortProviderReason;
import org.restcomm.protocols.ss7.map.api.dialog.MAPAbortSource;
import org.restcomm.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic;
import org.restcomm.protocols.ss7.map.api.dialog.MAPRefuseReason;
import org.restcomm.protocols.ss7.map.api.dialog.MAPUserAbortChoice;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.primitives.USSDString;
import org.restcomm.protocols.ss7.map.api.service.lsm.AdditionalNumber;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.InformServiceCentreRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.IpSmGwGuidance;
import org.restcomm.protocols.ss7.map.api.service.sms.LocationInfoWithLMSI;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPDialogSms;
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
import org.restcomm.protocols.ss7.map.api.service.supplementary.ActivateSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.ActivateSSResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.DeactivateSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.DeactivateSSResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.EraseSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.EraseSSResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.GetPasswordRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.GetPasswordResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.InterrogateSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.InterrogateSSResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.MAPDialogSupplementary;
import org.restcomm.protocols.ss7.map.api.service.supplementary.ProcessUnstructuredSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.ProcessUnstructuredSSResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.RegisterPasswordRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.RegisterPasswordResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.RegisterSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.RegisterSSResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.UnstructuredSSNotifyRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.UnstructuredSSNotifyResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.UnstructuredSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.UnstructuredSSResponse;
import org.restcomm.protocols.ss7.map.datacoding.CBSDataCodingSchemeImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.LMSIImpl;
import org.restcomm.protocols.ss7.map.service.sms.LocationInfoWithLMSIImpl;
import org.restcomm.protocols.ss7.tcap.asn.ApplicationContextName;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.restcomm.protocols.ss7.map.load.ConsoleTui;

/**
 * MAP USSD Load Test Server — uses Ss7StackBuilder for stack init.
 *
 * @author amit bhayani
 * @modified <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class Server extends TestHarnessUssd {

    private static final Logger log = LogManager.getLogger(Server.class);

    private Ss7Stack stack;
    private MAPProvider mapProvider;

    final AtomicLong endCount = new AtomicLong();
    volatile long start = System.currentTimeMillis();

    private final AtomicLong receivedCount = new AtomicLong();
    private final AtomicLong processedCount = new AtomicLong();
    private final AtomicLong errorCount = new AtomicLong();
    private ConsoleTui tui;

    /**
     * Build stack from JSON, register listeners, start SCTP server + ASP, launch TUI.
     */
    void start(String configPath) throws Exception {
        log.info("Building jSS7 stack from config: {}", configPath);
        stack = Ss7StackBuilder.build(Path.of(configPath));
        mapProvider = stack.mapProvider();

        // Register MAP listeners (same as before)
        mapProvider.addMAPDialogListener(this);
        mapProvider.getMAPServiceSupplementary().addMAPServiceListener(this);
        mapProvider.getMAPServiceSupplementary().activate();
        mapProvider.getMAPServiceSms().addMAPServiceListener(this);
        mapProvider.getMAPServiceSms().activate();
        log.info("MAP listeners registered");

        // Start SCTP server (link name from JSON: "serverLink")
        stack.sctpManagement().startServer("serverLink-srv");
        log.info("SCTP server started");

        // Start ASP (link name from JSON: "serverLink" -> ASP name: "serverLink-ASP")
        stack.m3uaManagement().startAsp("serverLink-ASP");
        log.info("ASP started");

        // Start TUI
        this.tui = new ConsoleTui("SERVER", receivedCount, processedCount, errorCount, -1, System.err);
        this.tui.start();
        System.out.println("[Server] TUI started on stderr. Check terminal for live stats.");

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try { if (tui != null) tui.close(); } catch (Exception ignored) {}
            if (stack != null) stack.stop();
            log.info("Server shutdown complete");
        }, "server-shutdown"));

        log.info("Server ready");
        Thread.currentThread().join();
    }

    // ═══════════════════════════════════════════════════════════
    // MAPDialogListener
    // ═══════════════════════════════════════════════════════════

    @Override
    public void onDialogDelimiter(MAPDialog mapDialog) {
        if (log.isDebugEnabled())
            log.debug("onDialogDelimiter DialogId={}", mapDialog.getLocalDialogId());
    }

    @Override
    public void onDialogRequest(MAPDialog mapDialog, AddressString destReference, AddressString origReference,
            MAPExtensionContainer extensionContainer) {
        if (log.isDebugEnabled())
            log.debug("onDialogRequest DialogId={} dest={} orig={}", mapDialog.getLocalDialogId(), destReference, origReference);
    }

    @Override
    public void onDialogRequestEricsson(MAPDialog mapDialog, AddressString destReference, AddressString origReference,
            AddressString imsi, AddressString vlr) {
        if (log.isDebugEnabled())
            log.debug("onDialogRequestEricsson DialogId={} dest={} orig={}", mapDialog.getLocalDialogId(), destReference, origReference);
    }

    @Override
    public void onDialogAccept(MAPDialog mapDialog, MAPExtensionContainer extensionContainer) {
        if (log.isDebugEnabled())
            log.debug("onDialogAccept DialogId={} ext={}", mapDialog.getLocalDialogId(), extensionContainer);
    }

    @Override
    public void onDialogReject(MAPDialog mapDialog, MAPRefuseReason refuseReason, ApplicationContextName alternativeApplicationContext,
                               MAPExtensionContainer extensionContainer) {
        errorCount.incrementAndGet();
        log.error("onDialogReject DialogId={} reason={} altCtx={} ext={}",
                mapDialog.getLocalDialogId(), refuseReason, alternativeApplicationContext, extensionContainer);
    }

    @Override
    public void onDialogUserAbort(MAPDialog mapDialog, MAPUserAbortChoice userReason, MAPExtensionContainer extensionContainer) {
        errorCount.incrementAndGet();
        log.error("onDialogUserAbort DialogId={} reason={} ext={}", mapDialog.getLocalDialogId(), userReason, extensionContainer);
    }

    @Override
    public void onDialogProviderAbort(MAPDialog mapDialog, MAPAbortProviderReason abortProviderReason, MAPAbortSource abortSource,
                                      MAPExtensionContainer extensionContainer) {
        errorCount.incrementAndGet();
        log.error("onDialogProviderAbort DialogId={} reason={} source={} ext={}",
                mapDialog.getLocalDialogId(), abortProviderReason, abortSource, extensionContainer);
    }

    @Override
    public void onDialogClose(MAPDialog mapDialog) {
        if (log.isDebugEnabled())
            log.debug("DialogClose for Dialog={}", mapDialog.getLocalDialogId());
    }

    @Override
    public void onDialogNotice(MAPDialog mapDialog, MAPNoticeProblemDiagnostic noticeProblemDiagnostic) {
        log.error("onDialogNotice DialogId={} diagnostic={}", mapDialog.getLocalDialogId(), noticeProblemDiagnostic);
    }

    @Override
    public void onDialogRelease(MAPDialog mapDialog) {
        if (log.isDebugEnabled())
            log.debug("onDialogRelease DialogId={}", mapDialog.getLocalDialogId());

        this.endCount.incrementAndGet();
        processedCount.incrementAndGet();

        if ((this.endCount.get() % 10000) == 0) {
            long currentTime = System.currentTimeMillis();
            long processingTime = currentTime - start;
            start = currentTime;
            log.info("Completed {} dialogs. Rate: {} in {}ms", processedCount.get(), 10000, processingTime);
        }
    }

    @Override
    public void onDialogTimeout(MAPDialog mapDialog) {
        errorCount.incrementAndGet();
        log.error("onDialogTimeout DialogId={}", mapDialog.getLocalDialogId());
    }

    // ═══════════════════════════════════════════════════════════
    // MAPServiceSupplementaryListener
    // ═══════════════════════════════════════════════════════════

    @Override
    public void onProcessUnstructuredSSRequest(ProcessUnstructuredSSRequest processUnstructuredSSRequest) {
        receivedCount.incrementAndGet();
        if (log.isDebugEnabled())
            log.debug("onProcessUnstructuredSSRequest DialogId={} invokeId={}",
                    processUnstructuredSSRequest.getMAPDialog().getLocalDialogId(), processUnstructuredSSRequest.getInvokeId());
        try {
            long invokeId = processUnstructuredSSRequest.getInvokeId();

            USSDString ussdStrObj = this.mapProvider.getMAPParameterFactory().createUSSDString(
                    "USSD String : Hello World <CR> 1. Balance <CR> 2. Texts Remaining");
            CBSDataCodingScheme ussdDataCodingScheme = new CBSDataCodingSchemeImpl(0x0F);
            MAPDialogSupplementary dialog = processUnstructuredSSRequest.getMAPDialog();

            dialog.setUserObject(invokeId);

            ISDNAddressString msisdn = this.mapProvider.getMAPParameterFactory().createISDNAddressString(
                    AddressNature.international_number, NumberingPlan.ISDN, "31628838002");

            dialog.addUnstructuredSSRequest(ussdDataCodingScheme, ussdStrObj, null, msisdn);
            dialog.send();
        } catch (MAPException e) {
            log.error("Error while sending UnstructuredSSRequest", e);
        }
    }

    @Override
    public void onProcessUnstructuredSSResponse(ProcessUnstructuredSSResponse processUnstructuredSSResponse) {
        log.error("onProcessUnstructuredSSResponse Dialog={} invokeId={} — unexpected on server",
                processUnstructuredSSResponse.getMAPDialog().getLocalDialogId(), processUnstructuredSSResponse.getInvokeId());
    }

    @Override
    public void onUnstructuredSSRequest(UnstructuredSSRequest unstructuredSSRequest) {
        if (log.isDebugEnabled())
            log.debug("Rx UnstructuredSSRequest USSD={}", unstructuredSSRequest.getUSSDString());
        MAPDialogSupplementary mapDialog = unstructuredSSRequest.getMAPDialog();

        try {
            CBSDataCodingScheme ussdDataCodingScheme = new CBSDataCodingSchemeImpl(0x0f);
            USSDString ussdString = this.mapProvider.getMAPParameterFactory().createUSSDString("1", null, null);
            AddressString msisdn = this.mapProvider.getMAPParameterFactory()
                    .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "31628838002");

            mapDialog.addUnstructuredSSResponse(unstructuredSSRequest.getInvokeId(), ussdDataCodingScheme, ussdString);
            mapDialog.send();
        } catch (MAPException e) {
            log.error("Error sending UnstructuredSSResponse Dialog={}", mapDialog.getLocalDialogId());
        }
    }

    @Override
    public void onUnstructuredSSResponse(UnstructuredSSResponse unstructuredSSResponse) {
        if (log.isDebugEnabled())
            log.debug("onUnstructuredSSResponse DialogId={}", unstructuredSSResponse.getMAPDialog().getLocalDialogId());
        try {
            USSDString ussdStrObj = this.mapProvider.getMAPParameterFactory().createUSSDString("Your balance is 500");
            CBSDataCodingScheme ussdDataCodingScheme = new CBSDataCodingSchemeImpl(0x0F);
            MAPDialogSupplementary dialog = unstructuredSSResponse.getMAPDialog();

            AddressString msisdn = this.mapProvider.getMAPParameterFactory().createAddressString(
                    AddressNature.international_number, NumberingPlan.ISDN, "31628838002");

            dialog.addProcessUnstructuredSSResponse(((Long) dialog.getUserObject()).longValue(), ussdDataCodingScheme, ussdStrObj);
            dialog.close(false);
        } catch (MAPException e) {
            log.error("Error sending UnstructuredSSRequest", e);
        }
    }

    @Override
    public void onUnstructuredSSNotifyRequest(UnstructuredSSNotifyRequest unstructuredSSNotifyRequest) {
        MAPDialogSupplementary mapDialog = unstructuredSSNotifyRequest.getMAPDialog();
        try {
            mapDialog.addUnstructuredSSNotifyResponse(unstructuredSSNotifyRequest.getInvokeId());
            mapDialog.send();
        } catch (MAPException e) {
            log.error("Error sending UnstructuredSSNotifyResponse Dialog={}", mapDialog.getLocalDialogId());
        }
    }

    @Override
    public void onUnstructuredSSNotifyResponse(UnstructuredSSNotifyResponse unstructuredSSNotifyResponse) {
        log.error("onUnstructuredSSNotifyResponse Dialog={} invokeId={} — unexpected on server",
                unstructuredSSNotifyResponse.getMAPDialog().getLocalDialogId(), unstructuredSSNotifyResponse.getInvokeId());
    }

    // ═══════════════════════════════════════════════════════════
    // MAPServiceListener
    // ═══════════════════════════════════════════════════════════

    @Override
    public void onErrorComponent(MAPDialog mapDialog, Long invokeId, MAPErrorMessage mapErrorMessage) {
        log.error("onErrorComponent Dialog={} invokeId={} error={}", mapDialog.getLocalDialogId(), invokeId, mapErrorMessage);
    }

    @Override
    public void onRejectComponent(MAPDialog mapDialog, Long invokeId, Problem problem, boolean isLocalOriginated) {
        log.error("onRejectComponent Dialog={} invokeId={} problem={} local={}",
                mapDialog.getLocalDialogId(), invokeId, problem, isLocalOriginated);
    }

    @Override
    public void onInvokeTimeout(MAPDialog mapDialog, Long invokeId) {
        log.error("onInvokeTimeout Dialog={} invokeId={}", mapDialog.getLocalDialogId(), invokeId);
    }

    // ═══════════════════════════════════════════════════════════
    // MAPServiceSmsListener — SRI-for-SM
    // ═══════════════════════════════════════════════════════════

    @Override
    public void onSendRoutingInfoForSMRequest(SendRoutingInfoForSMRequest sendRoutingInfoForSMRequestIndication) {
        if (log.isDebugEnabled())
            log.debug("onSendRoutingInfoForSMRequest DialogId={}", sendRoutingInfoForSMRequestIndication.getMAPDialog().getLocalDialogId());
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
                case 1: lmsiByte = new byte[]{114, 2, (byte) 233, (byte) 140}; break;
                case 2: lmsiByte = new byte[]{113, (byte) 255, (byte) 172, (byte) 206}; break;
                case 3: lmsiByte = new byte[]{114, 2, (byte) 235, 55}; break;
                case 4: lmsiByte = new byte[]{114, 2, (byte) 231, (byte) 213}; break;
            }
            LMSI lmsi = new LMSIImpl(lmsiByte);
            MAPExtensionContainer mapExtensionContainer = null;
            boolean gprsNodeIndicator = false;
            AdditionalNumber additionalNumber = null;
            LocationInfoWithLMSI locationInfoWithLMSI = new LocationInfoWithLMSIImpl(networkNodeNumber, lmsi, mapExtensionContainer,
                gprsNodeIndicator, additionalNumber, null, null, null, null, false, null, null, null, null, false, false);
            mapDialogSms.addSendRoutingInfoForSMResponse(invokeId, imsi, locationInfoWithLMSI, null, null, null);
            mapDialogSms.close(false);
        } catch (MAPException e) {
            log.error("Error sending SendRoutingInfoForSMResponse", e);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // Remaining stubs (unused for USSD load test)
    // ═══════════════════════════════════════════════════════════

    @Override public void onMAPMessage(MAPMessage mapMessage) {}
    @Override public void onRegisterSSRequest(RegisterSSRequest request) {}
    @Override public void onRegisterSSResponse(RegisterSSResponse response) {}
    @Override public void onEraseSSRequest(EraseSSRequest request) {}
    @Override public void onEraseSSResponse(EraseSSResponse response) {}
    @Override public void onActivateSSRequest(ActivateSSRequest request) {}
    @Override public void onActivateSSResponse(ActivateSSResponse response) {}
    @Override public void onDeactivateSSRequest(DeactivateSSRequest request) {}
    @Override public void onDeactivateSSResponse(DeactivateSSResponse response) {}
    @Override public void onInterrogateSSRequest(InterrogateSSRequest request) {}
    @Override public void onInterrogateSSResponse(InterrogateSSResponse response) {}
    @Override public void onGetPasswordRequest(GetPasswordRequest request) {}
    @Override public void onGetPasswordResponse(GetPasswordResponse response) {}
    @Override public void onRegisterPasswordRequest(RegisterPasswordRequest request) {}
    @Override public void onRegisterPasswordResponse(RegisterPasswordResponse response) {}
    @Override public void onForwardShortMessageRequest(ForwardShortMessageRequest forwardShortMessageRequestIndication) {}
    @Override public void onForwardShortMessageResponse(ForwardShortMessageResponse forwardShortMessageResponseIndication) {}
    @Override public void onMoForwardShortMessageRequest(MoForwardShortMessageRequest moForwardShortMessageRequestIndication) {}
    @Override public void onMoForwardShortMessageResponse(MoForwardShortMessageResponse moForwardShortMessageResponseIndication) {}
    @Override public void onMtForwardShortMessageRequest(MtForwardShortMessageRequest mtForwardShortMessageRequestIndication) {}
    @Override public void onMtForwardShortMessageResponse(MtForwardShortMessageResponse mtForwardShortMessageResponseIndication) {}
    @Override public void onSendRoutingInfoForSMResponse(SendRoutingInfoForSMResponse sendRoutingInfoForSMResponseIndication) {}
    @Override public void onReportSMDeliveryStatusRequest(ReportSMDeliveryStatusRequest reportSMDeliveryStatusRequestIndication) {}
    @Override public void onReportSMDeliveryStatusResponse(ReportSMDeliveryStatusResponse reportSMDeliveryStatusResponseIndication) {}
    @Override public void onInformServiceCentreRequest(InformServiceCentreRequest informServiceCentreRequestIndication) {}
    @Override public void onAlertServiceCentreRequest(AlertServiceCentreRequest alertServiceCentreRequestIndication) {}
    @Override public void onAlertServiceCentreResponse(AlertServiceCentreResponse alertServiceCentreResponseIndication) {}
    @Override public void onReadyForSMRequest(ReadyForSMRequest readyForSMRequest) {}
    @Override public void onReadyForSMResponse(ReadyForSMResponse readyForSMResponse) {}
    @Override public void onNoteSubscriberPresentRequest(NoteSubscriberPresentRequest noteSubscriberPresentRequest) {}

    // ═══════════════════════════════════════════════════════════
    // main
    // ═══════════════════════════════════════════════════════════

    public static void main(String[] args) {
        String configPath = args.length >= 1 ? args[0] : "ss7-server.json";
        Server server = new Server();
        try {
            server.start(configPath);
        } catch (Exception e) {
            log.error("Failed to start server", e);
            System.exit(1);
        }
    }
}
