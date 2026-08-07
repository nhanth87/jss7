package org.restcomm.protocols.ss7.map.load.ussd;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.util.concurrent.RateLimiter;

import org.restcomm.protocols.ss7.config.Ss7Stack;
import org.restcomm.protocols.ss7.config.Ss7StackBuilder;
import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContext;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextName;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextVersion;
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
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.primitives.USSDString;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.InformServiceCentreRequest;
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
import org.restcomm.protocols.ss7.map.load.CsvWriter;
import org.restcomm.protocols.ss7.sccp.NetworkIdState;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.tcap.asn.ApplicationContextName;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.restcomm.protocols.ss7.map.load.ConsoleTui;

/**
 * MAP USSD Load Test Client — uses Ss7StackBuilder for stack init.
 *
 * @author amit bhayani
 * @modified <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class Client extends TestHarnessUssd {

    private static final Logger log = LogManager.getLogger(Client.class);

    private Ss7Stack stack;
    private MAPProvider mapProvider;

    final AtomicLong endCount = new AtomicLong();
    transient boolean endReportPrinted;

    volatile long start = 0L;
    volatile long prev = 0L;

    private RateLimiter rateLimiterObj = null;

    private CsvWriter csvWriter;
    private ConsoleTui tui;

    /** Dialogs that already failed (timeout/abort/reject) — Success only on clean release. */
    private final Set<Long> failedDialogs = ConcurrentHashMap.newKeySet();

    /** Per-dialog index into {@link #DIGIT_SEQ} for UnstructuredSS-Request auto replies. */
    private final ConcurrentHashMap<Long, AtomicInteger> digitIndexByDialog = new ConcurrentHashMap<>();

    // Constant per-run request objects — built once, reused every message (avoids re-creating
    // addresses/global-titles/app-context on each initiateUSSD). Only the random MSISDN/USSD
    // string is built per message.
    private AddressString cOrigRef, cDestRef;
    private SccpAddress cClientAddr, cServerAddr;
    private MAPApplicationContext cAppCtx;
    private CBSDataCodingScheme cDcs;

    /** USSD MO string; if it contains {@code %}, formatted with a random int (load uniqueness). */
    private static String SHORT_CODE = System.getProperty("ss7.load.shortCode", "*125*+3162%06d#");
    /** Comma-separated DT replies on UnstructuredSS-Request (cycles / last-wins when exhausted). */
    private static String[] DIGIT_SEQ = parseDigitSeq(System.getProperty("ss7.load.digits", "1"));
    /** Empty = random MSISDN per dialog; else fixed. */
    private static String FIXED_MSISDN = System.getProperty("ss7.load.msisdn", "");

    /**
     * Build stack from JSON, register listeners, start ASP, launch DialogInitiator threads.
     */
    void start(String configPath) throws Exception {
        rateLimiterObj = RateLimiter.create(MAXCONCURRENTDIALOGS);

        log.info("Building jSS7 stack from config: {}", configPath);
        stack = Ss7StackBuilder.build(Path.of(configPath));
        mapProvider = stack.mapProvider();

        // Build constant request objects once (addresses, GT, app-context, DCS).
        var pf = mapProvider.getMAPParameterFactory();
        this.cOrigRef = pf.createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "12345");
        this.cDestRef = pf.createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "67890");
        this.cClientAddr = createSccpAddress(ROUTING_INDICATOR, ORIGINATING_PC, MSC_SSN, SCCP_CLIENT_ADDRESS);
        this.cServerAddr = createSccpAddress(ROUTING_INDICATOR, DESTINATION_PC, USSD_SSN, SCCP_SERVER_ADDRESS);
        this.cAppCtx = MAPApplicationContext.getInstance(
                MAPApplicationContextName.networkUnstructuredSsContext, MAPApplicationContextVersion.version2);
        this.cDcs = new CBSDataCodingSchemeImpl(0x0f);

        // Register MAP listeners
        mapProvider.addMAPDialogListener(this);
        mapProvider.getMAPServiceSupplementary().addMAPServiceListener(this);
        mapProvider.getMAPServiceSupplementary().activate();
        log.info("MAP listeners registered");

        // Start ASP (startAsp internally starts the SCTP association)
        stack.m3uaManagement().startAsp("clientLink-ASP");
        log.info("ASP started");

        // CSV writer
        this.csvWriter = new CsvWriter("map");
        this.csvWriter.addCounter(CREATED_DIALOGS);
        this.csvWriter.addCounter(SUCCESSFUL_DIALOGS);
        this.csvWriter.addCounter(ERROR_DIALOGS);
        this.csvWriter.start(TEST_START_DELAY, PRINT_WRITER_PERIOD);

        // Start live TUI
        this.tui = new ConsoleTui("CLIENT", this.csvWriter, MAXCONCURRENTDIALOGS, System.err);
        this.tui.start();

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            terminate();
            if (stack != null) stack.stop();
            log.info("Client shutdown complete");
        }, "client-shutdown"));

        // Wait for ramp-up
        Thread.sleep(TEST_START_DELAY);

        // Start DialogInitiator threads
        Thread[] threads = new Thread[SENDING_MESSAGE_THREAD_COUNT];
        for (int j = 0; j < SENDING_MESSAGE_THREAD_COUNT; j++) {
            threads[j] = new Thread(this.new DialogInitiator());
        }
        for (int j = 0; j < SENDING_MESSAGE_THREAD_COUNT; j++) {
            threads[j].start();
        }

        // Wait until target dialog count reached
        while (this.endCount.get() < NDIALOGS) {
            Thread.sleep(100);
        }

        terminate();
    }

    private void initiateUSSD() throws MAPException {
        NetworkIdState networkIdState = this.mapProvider.getNetworkIdState(0);
        int executorCongestionLevel = this.mapProvider.getExecutorCongestionLevel();
        if (!(networkIdState == null
                || networkIdState.isAvailable() && networkIdState.getCongLevel() <= 0 && executorCongestionLevel <= 0)) {
            log.warn("**** Outgoing congestion control: networkIdState={}, executorCongestionLevel={}",
                    networkIdState, executorCongestionLevel);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        this.rateLimiterObj.acquire();

        MAPDialogSupplementary mapDialog = this.mapProvider.getMAPServiceSupplementary()
                .createNewDialog(cAppCtx, cClientAddr, cOrigRef, cServerAddr, cDestRef);

        int random = 8000000 + java.util.concurrent.ThreadLocalRandom.current().nextInt(1000000);
        String ussdText = formatShortCode(SHORT_CODE, random);
        USSDString ussdString = this.mapProvider.getMAPParameterFactory().createUSSDString(ussdText, null, null);

        String msisdnDigits = (FIXED_MSISDN == null || FIXED_MSISDN.isBlank())
                ? ("3162" + random)
                : FIXED_MSISDN.trim();
        ISDNAddressString msisdn = this.mapProvider.getMAPParameterFactory()
                .createISDNAddressString(AddressNature.international_number, NumberingPlan.ISDN, msisdnDigits);

        mapDialog.addProcessUnstructuredSSRequest(cDcs, ussdString, null, msisdn);

        mapDialog.send();
        if (java.util.concurrent.ThreadLocalRandom.current().nextInt(100) == 0) {
            log.info("Sent USSD dialog to DPC={} SSN={}", DESTINATION_PC, USSD_SSN);
        }

        this.csvWriter.incrementCounter(CREATED_DIALOGS);
    }

    private SccpAddress createSccpAddress(RoutingIndicator ri, int dpc, int ssn, String address) {
        ParameterFactoryImpl fact = new ParameterFactoryImpl();
        GlobalTitle gt = fact.createGlobalTitle(address, 0, org.restcomm.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,
                BCDEvenEncodingScheme.INSTANCE, NatureOfAddress.INTERNATIONAL);
        return fact.createSccpAddress(ri, gt, dpc, ssn);
    }

    public void terminate() {
        try {
            this.csvWriter.stop(TEST_END_DELAY);
            if (tui != null) tui.close();
        } catch (InterruptedException e) {
            log.error("Error stopping csvWriter", e);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // DialogInitiator
    // ═══════════════════════════════════════════════════════════

    public class DialogInitiator implements Runnable {
        @Override
        public void run() {
            try {
                while (endCount.get() < NDIALOGS) {
                    if (endCount.get() < 0) {
                        start = System.currentTimeMillis();
                        prev = start;
                    }
                    initiateUSSD();
                }
            } catch (MAPException ex) {
                log.error("Exception when sending a new MAP dialog", ex);
            }
        }
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
            AddressString arg3, AddressString arg4) {
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
        log.error("onDialogReject DialogId={} reason={} altCtx={} ext={}",
                mapDialog.getLocalDialogId(), refuseReason, alternativeApplicationContext, extensionContainer);
        markDialogFailed(mapDialog);
    }

    @Override
    public void onDialogUserAbort(MAPDialog mapDialog, MAPUserAbortChoice userReason, MAPExtensionContainer extensionContainer) {
        log.error("onDialogUserAbort DialogId={} reason={} ext={}", mapDialog.getLocalDialogId(), userReason, extensionContainer);
        markDialogFailed(mapDialog);
    }

    @Override
    public void onDialogProviderAbort(MAPDialog mapDialog, MAPAbortProviderReason abortProviderReason, MAPAbortSource abortSource,
            MAPExtensionContainer extensionContainer) {
        log.error("onDialogProviderAbort DialogId={} reason={} source={} ext={}",
                mapDialog.getLocalDialogId(), abortProviderReason, abortSource, extensionContainer);
        markDialogFailed(mapDialog);
    }

    @Override
    public void onDialogClose(MAPDialog mapDialog) {
        if (log.isDebugEnabled())
            log.debug("DialogClose for Dialog={}", mapDialog.getLocalDialogId());
    }

    @Override
    public void onDialogNotice(MAPDialog mapDialog, MAPNoticeProblemDiagnostic noticeProblemDiagnostic) {
        log.error("onDialogNotice DialogId={} diagnostic={}", mapDialog.getLocalDialogId(), noticeProblemDiagnostic);
        markDialogFailed(mapDialog);
    }

    @Override
    public void onDialogRelease(MAPDialog mapDialog) {
        if (log.isDebugEnabled())
            log.debug("onDialogRelease DialogId={}", mapDialog.getLocalDialogId());
        // Success only if this dialog was not already marked failed (timeout/abort/reject).
        if (!failedDialogs.remove(mapDialog.getLocalDialogId())) {
            this.csvWriter.incrementCounter(SUCCESSFUL_DIALOGS);
        }
        digitIndexByDialog.remove(mapDialog.getLocalDialogId());
        this.endCount.incrementAndGet();

        if (this.endCount.get() < NDIALOGS) {
            if ((this.endCount.get() % 10000) == 0) {
                long current = System.currentTimeMillis();
                float sec = (float) (current - prev) / 1000f;
                prev = current;
                log.warn("Completed 10000 Dialogs, dialogs per second: {}", (float) (10000 / sec));
            }
        } else {
            if (this.endCount.get() >= NDIALOGS && !endReportPrinted) {
                endReportPrinted = true;
                if (tui != null) tui.close();
                long current = System.currentTimeMillis();
                log.warn("Start Time = {}", start);
                log.warn("Current Time = {}", current);
                float sec = (float) (current - start) / 1000f;
                log.warn("Total time in sec = {}", sec);
                log.warn("Throughput = {}", (float) (NDIALOGS / sec));
            }
        }
    }

    @Override
    public void onDialogTimeout(MAPDialog mapDialog) {
        log.error("onDialogTimeout DialogId={}", mapDialog.getLocalDialogId());
        markDialogFailed(mapDialog);
    }

    /** Count Error once per dialog; timeout+abort cascade must not double-count. */
    private void markDialogFailed(MAPDialog mapDialog) {
        if (failedDialogs.add(mapDialog.getLocalDialogId())) {
            this.csvWriter.incrementCounter(ERROR_DIALOGS);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // MAPServiceSupplementaryListener
    // ═══════════════════════════════════════════════════════════

    @Override
    public void onProcessUnstructuredSSRequest(ProcessUnstructuredSSRequest processUnstructuredSSRequest) {
        log.error("onProcessUnstructuredSSRequest Dialog={} invokeId={} — unexpected on client",
                processUnstructuredSSRequest.getMAPDialog().getLocalDialogId(), processUnstructuredSSRequest.getInvokeId());
    }

    @Override
    public void onProcessUnstructuredSSResponse(ProcessUnstructuredSSResponse processUnstructuredSSResponse) {
        if (log.isDebugEnabled())
            log.debug("Rx ProcessUnstructuredSSResponse USSD={}", processUnstructuredSSResponse.getUSSDString());
    }

    @Override
    public void onUnstructuredSSRequest(UnstructuredSSRequest unstructuredSSRequest) {
        if (log.isDebugEnabled())
            log.debug("Rx UnstructuredSSRequest USSD={}", unstructuredSSRequest.getUSSDString());
        MAPDialogSupplementary mapDialog = unstructuredSSRequest.getMAPDialog();

        try {
            CBSDataCodingScheme ussdDataCodingScheme = new CBSDataCodingSchemeImpl(0x0f);
            String digit = nextDigit(mapDialog.getLocalDialogId());
            USSDString ussdString = this.mapProvider.getMAPParameterFactory().createUSSDString(digit, null, null);

            mapDialog.addUnstructuredSSResponse(unstructuredSSRequest.getInvokeId(), ussdDataCodingScheme, ussdString);
            mapDialog.send();
            if (java.util.concurrent.ThreadLocalRandom.current().nextInt(100) == 0) {
                log.info("Sent USSD DT digit={} to DPC={} SSN={}", digit, DESTINATION_PC, USSD_SSN);
            }
        } catch (MAPException e) {
            log.error("Error sending UnstructuredSSResponse Dialog={}", mapDialog.getLocalDialogId());
        }
    }

    private static String formatShortCode(String template, int random) {
        if (template == null || template.isBlank()) {
            return "*125#";
        }
        if (template.indexOf('%') >= 0) {
            try {
                return String.format(template, random);
            } catch (Exception e) {
                return template;
            }
        }
        return template;
    }

    private static String[] parseDigitSeq(String csv) {
        if (csv == null || csv.isBlank()) {
            return new String[] { "1" };
        }
        String[] parts = csv.split(",");
        java.util.ArrayList<String> out = new java.util.ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) {
                out.add(t);
            }
        }
        return out.isEmpty() ? new String[] { "1" } : out.toArray(new String[0]);
    }

    private String nextDigit(long dialogId) {
        AtomicInteger idx = digitIndexByDialog.computeIfAbsent(dialogId, id -> new AtomicInteger(0));
        int i = idx.getAndIncrement();
        if (i >= DIGIT_SEQ.length) {
            return DIGIT_SEQ[DIGIT_SEQ.length - 1];
        }
        return DIGIT_SEQ[i];
    }

    @Override
    public void onUnstructuredSSResponse(UnstructuredSSResponse unstructuredSSResponse) {
        log.error("onUnstructuredSSResponse Dialog={} invokeId={} — unexpected on client",
                unstructuredSSResponse.getMAPDialog().getLocalDialogId(), unstructuredSSResponse.getInvokeId());
    }

    @Override
    public void onUnstructuredSSNotifyRequest(UnstructuredSSNotifyRequest unstructuredSSNotifyRequest) {
        log.error("onUnstructuredSSNotifyRequest Dialog={} invokeId={} — unexpected on client",
                unstructuredSSNotifyRequest.getMAPDialog().getLocalDialogId(), unstructuredSSNotifyRequest.getInvokeId());
    }

    @Override
    public void onUnstructuredSSNotifyResponse(UnstructuredSSNotifyResponse unstructuredSSNotifyResponse) {
        log.error("onUnstructuredSSNotifyResponse Dialog={} invokeId={} — unexpected on client",
                unstructuredSSNotifyResponse.getMAPDialog().getLocalDialogId(), unstructuredSSNotifyResponse.getInvokeId());
    }

    // ═══════════════════════════════════════════════════════════
    // MAPServiceListener
    // ═══════════════════════════════════════════════════════════

    @Override
    public void onErrorComponent(MAPDialog mapDialog, Long invokeId, MAPErrorMessage mapErrorMessage) {
        log.error("onErrorComponent Dialog={} invokeId={} error={}", mapDialog.getLocalDialogId(), invokeId, mapErrorMessage);
        markDialogFailed(mapDialog);
    }

    @Override
    public void onRejectComponent(MAPDialog mapDialog, Long invokeId, Problem problem, boolean isLocalOriginated) {
        log.error("onRejectComponent Dialog={} invokeId={} problem={} local={}",
                mapDialog.getLocalDialogId(), invokeId, problem, isLocalOriginated);
        markDialogFailed(mapDialog);
    }

    @Override
    public void onInvokeTimeout(MAPDialog mapDialog, Long invokeId) {
        log.error("onInvokeTimeout Dialog={} invokeId={}", mapDialog.getLocalDialogId(), invokeId);
        markDialogFailed(mapDialog);
    }

    // ═══════════════════════════════════════════════════════════
    // Remaining stubs
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
    @Override public void onSendRoutingInfoForSMRequest(SendRoutingInfoForSMRequest sendRoutingInfoForSMRequestIndication) {}
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
        String configPath = args.length >= 1 ? args[0] : "ss7-client.json";

        // Load test params from system properties (fallback to defaults from TestHarnessUssd)
        NDIALOGS = Integer.getInteger("ss7.load.ndialogs", 60000);
        MAXCONCURRENTDIALOGS = Integer.getInteger("ss7.load.rateLimit", 1000);
        RAMP_UP_PERIOD = Integer.getInteger("ss7.load.rampUp", 0);
        SENDING_MESSAGE_THREAD_COUNT = Integer.getInteger("ss7.load.senderThreads",
                Runtime.getRuntime().availableProcessors() * 2);
        SCCP_CLIENT_ADDRESS = System.getProperty("ss7.load.clientAddress", "1111112");
        SCCP_SERVER_ADDRESS = System.getProperty("ss7.load.serverAddress", "9960639999");
        ORIGINATING_PC = Integer.getInteger("ss7.load.origPc", ORIGINATING_PC);
        DESTINATION_PC = Integer.getInteger("ss7.load.destPc", DESTINATION_PC);
        USSD_SSN = Integer.getInteger("ss7.load.ussdSsn", USSD_SSN);
        MSC_SSN = Integer.getInteger("ss7.load.mscSsn", MSC_SSN);
        SHORT_CODE = System.getProperty("ss7.load.shortCode", SHORT_CODE);
        DIGIT_SEQ = parseDigitSeq(System.getProperty("ss7.load.digits", "1"));
        FIXED_MSISDN = System.getProperty("ss7.load.msisdn", FIXED_MSISDN);

        System.out.println("Config      : " + configPath);
        System.out.println("NDIALOGS    : " + NDIALOGS);
        System.out.println("Rate Limit  : " + MAXCONCURRENTDIALOGS);
        System.out.println("Ramp-up     : " + RAMP_UP_PERIOD);
        System.out.println("Sender threads: " + SENDING_MESSAGE_THREAD_COUNT);
        System.out.println("Client addr : " + SCCP_CLIENT_ADDRESS);
        System.out.println("Server addr : " + SCCP_SERVER_ADDRESS);
        System.out.println("shortCode   : " + SHORT_CODE);
        System.out.println("digits      : " + String.join(",", DIGIT_SEQ));
        System.out.println("msisdn      : " + (FIXED_MSISDN == null || FIXED_MSISDN.isBlank() ? "(random)" : FIXED_MSISDN));
        System.out.println("OPC/DPC/SSN : " + ORIGINATING_PC + "/" + DESTINATION_PC + "/ussd=" + USSD_SSN);

        final Client client = new Client();
        client.endCount.set(RAMP_UP_PERIOD);

        try {
            client.start(configPath);
        } catch (Exception e) {
            log.error("Failed to start client", e);
            System.exit(1);
        }
    }
}
