
package org.restcomm.protocols.ss7.map.load.sms.mo;

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
import org.restcomm.protocols.ss7.map.api.service.sms.SM_RP_DA;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.SmsSignalInfo;
import org.restcomm.protocols.ss7.map.api.smstpdu.AddressField;
import org.restcomm.protocols.ss7.map.api.smstpdu.DataCodingScheme;
import org.restcomm.protocols.ss7.map.api.smstpdu.NumberingPlanIdentification;
import org.restcomm.protocols.ss7.map.api.smstpdu.ProtocolIdentifier;
import org.restcomm.protocols.ss7.map.api.smstpdu.TypeOfNumber;
import org.restcomm.protocols.ss7.map.api.smstpdu.UserData;
import org.restcomm.protocols.ss7.map.api.smstpdu.UserDataHeader;
import org.restcomm.protocols.ss7.map.api.smstpdu.ValidityPeriod;
import org.restcomm.protocols.ss7.map.load.ConsoleTui;
import org.restcomm.protocols.ss7.map.load.CsvWriter;
import org.restcomm.protocols.ss7.map.primitives.AddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.LMSIImpl;
import org.restcomm.protocols.ss7.map.service.sms.LocationInfoWithLMSIImpl;
import org.restcomm.protocols.ss7.map.service.sms.SM_RP_DAImpl;
import org.restcomm.protocols.ss7.map.service.sms.SM_RP_OAImpl;
import org.restcomm.protocols.ss7.map.service.sms.SmsSignalInfoImpl;
import org.restcomm.protocols.ss7.map.smstpdu.AddressFieldImpl;
import org.restcomm.protocols.ss7.map.smstpdu.DataCodingSchemeImpl;
import org.restcomm.protocols.ss7.map.smstpdu.ProtocolIdentifierImpl;
import org.restcomm.protocols.ss7.map.smstpdu.SmsSubmitTpduImpl;
import org.restcomm.protocols.ss7.map.smstpdu.SmsTpduImpl;
import org.restcomm.protocols.ss7.map.smstpdu.UserDataHeaderImpl;
import org.restcomm.protocols.ss7.map.smstpdu.UserDataImpl;
import org.restcomm.protocols.ss7.map.smstpdu.ValidityPeriodImpl;
import org.restcomm.protocols.ss7.sccp.NetworkIdState;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.tcap.asn.ApplicationContextName;
import org.restcomm.protocols.ss7.tcap.asn.ReturnResultLastImpl;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;
import org.restcomm.protocols.ss7.tcap.asn.comp.ReturnResultLast;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MAP MO-SMS Load Test Client — uses Ss7StackBuilder for stack init.
 *
 * @modified <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class Client extends TestHarnessSmsMo {

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

    private final AtomicInteger messageReferenceForMo = new AtomicInteger(0);
    private final AtomicInteger counterIncrement = new AtomicInteger(0);

    // Constant per-run request objects — built once, reused every message.
    private AddressString cOrigRef, cDestRef;
    private SccpAddress cClientAddr, cServerAddr;
    private MAPApplicationContext cAppCtx;
    private AddressString cServiceCentreAddressDA;
    private SM_RP_DA cSmRpDa;

    /**
     * Build stack from JSON, register listeners, start ASP, launch DialogInitiator threads.
     */
    void start(String configPath) throws Exception {
        rateLimiterObj = RateLimiter.create(MAXCONCURRENTDIALOGS);

        log.info("Building jSS7 stack from config: {}", configPath);
        stack = Ss7StackBuilder.build(Path.of(configPath));
        mapProvider = stack.mapProvider();

        // Build constant request objects once (addresses, app-context, SM_RP_DA).
        this.cOrigRef = mapProvider.getMAPParameterFactory()
                .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "12345");
        this.cDestRef = mapProvider.getMAPParameterFactory()
                .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "67890");
        this.cClientAddr = createSccpAddress(ROUTING_INDICATOR, ORIGINATING_PC, SSN, SCCP_CLIENT_ADDRESS);
        this.cServerAddr = createSccpAddress(ROUTING_INDICATOR, DESTINATION_PC, SSN, SCCP_SERVER_ADDRESS);
        this.cAppCtx = MAPApplicationContext.getInstance(
                MAPApplicationContextName.shortMsgMORelayContext, MAPApplicationContextVersion.version3);
        this.cServiceCentreAddressDA = new AddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "5989900123");
        this.cSmRpDa = new SM_RP_DAImpl(cServiceCentreAddressDA);

        // Register MAP listeners
        mapProvider.addMAPDialogListener(this);
        mapProvider.getMAPServiceSms().addMAPServiceListener(this);
        mapProvider.getMAPServiceSms().activate();
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

    private void initiateSMS() throws MAPException {
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

        MAPDialogSms mapDialogSms = this.mapProvider.getMAPServiceSms()
                .createNewDialog(cAppCtx, cClientAddr, cOrigRef, cServerAddr, cDestRef);

        String uniqueMsisdn = "31" + String.format("%09d", this.getCounterIncrement());
        ISDNAddressString msisdn = this.mapProvider.getMAPParameterFactory()
                .createISDNAddressString(AddressNature.international_number, NumberingPlan.ISDN, uniqueMsisdn);
        SM_RP_OAImpl smRpOa = new SM_RP_OAImpl();
        smRpOa.setMsisdn(msisdn);

        boolean rejectDuplicates = true;
        boolean replyPathExists = false;
        boolean statusReportRequest = STATUS_REPORT_REQUEST;
        int messageReference = messageReferenceForMo.incrementAndGet();
        AddressField destinationAddress = new AddressFieldImpl(TypeOfNumber.InternationalNumber,
                NumberingPlanIdentification.ISDNTelephoneNumberingPlan, "59899077937");
        ProtocolIdentifier protocolIdentifier = new ProtocolIdentifierImpl(0);
        ValidityPeriod validityPeriod = new ValidityPeriodImpl(3);
        DataCodingScheme dataCodingScheme = new DataCodingSchemeImpl(0);
        UserDataHeader userDataHeader = new UserDataHeaderImpl();
        Charset gsm8Charset = Charset.defaultCharset();
        UserData userData = new UserDataImpl("SMS load test", dataCodingScheme, userDataHeader, gsm8Charset);
        SmsTpduImpl smsTpdu = new SmsSubmitTpduImpl(rejectDuplicates, replyPathExists, statusReportRequest, messageReference,
                destinationAddress, protocolIdentifier, validityPeriod, userData);
        SmsSignalInfo smsSignalInfo = new SmsSignalInfoImpl(smsTpdu, gsm8Charset);
        MAPExtensionContainer mapExtensionContainer = null;

        String uniqueImsi = "712345" + String.format("%09d", this.getCounterIncrement());
        IMSI imsi = new IMSIImpl(uniqueImsi);

        mapDialogSms.addMoForwardShortMessageRequest(cSmRpDa, smRpOa, smsSignalInfo, mapExtensionContainer, imsi, null, null);

        mapDialogSms.send();

        this.csvWriter.incrementCounter(CREATED_DIALOGS);
    }

    private int getCounterIncrement() {
        int x = counterIncrement.incrementAndGet();
        if (x > 147483647) {
            x = 1;
            counterIncrement.set(x);
        }
        return x;
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
            while (endCount.get() < NDIALOGS) {
                if (endCount.get() < 0) {
                    start = System.currentTimeMillis();
                    prev = start;
                }
                try {
                    initiateSMS();
                } catch (MAPException ex) {
                    // Transient (e.g. ASP not yet ACTIVE) — log and retry on the next iteration
                    // instead of letting one failed send permanently kill this sender thread.
                    log.error("Exception when sending a new MAP dialog", ex);
                }
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
    public void onDialogRequest(MAPDialog mapDialog, AddressString destReference, AddressString origReference, MAPExtensionContainer extensionContainer) {
        if (log.isDebugEnabled())
            log.debug("onDialogRequest DialogId={} dest={} orig={}", mapDialog.getLocalDialogId(), destReference, origReference);
    }

    @Override
    public void onDialogRequestEricsson(MAPDialog mapDialog, AddressString destReference, AddressString origReference, AddressString arg3,
                                        AddressString arg4) {
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
    // MAPServiceSmsListener — inbound (only exercised if used bidirectionally)
    // ═══════════════════════════════════════════════════════════

    @Override
    public void onMAPMessage(MAPMessage mapMessage) {}

    @Override
    public void onForwardShortMessageRequest(ForwardShortMessageRequest forwardShortMessageRequestIndication) {}

    @Override
    public void onForwardShortMessageResponse(ForwardShortMessageResponse forwardShortMessageResponseIndication) {}

    @Override
    public void onMoForwardShortMessageRequest(MoForwardShortMessageRequest moForwardShortMessageRequestIndication) {}

    @Override
    public void onMoForwardShortMessageResponse(MoForwardShortMessageResponse moForwardShortMessageResponseIndication) {
        if (log.isDebugEnabled())
            log.debug("Rx MoForwardShortMessageResponse DialogId={}",
                    moForwardShortMessageResponseIndication.getMAPDialog().getLocalDialogId());
    }

    @Override
    public void onMtForwardShortMessageRequest(MtForwardShortMessageRequest mtForwardShortMessageRequestIndication) {
        if (log.isDebugEnabled()) {
            log.debug("onMtForwardShortMessageRequest DialogId={}",
                    mtForwardShortMessageRequestIndication.getMAPDialog().getLocalDialogId());
        }
        try {
            long invokeId = mtForwardShortMessageRequestIndication.getInvokeId();
            MAPDialogSms mapDialogSms = mtForwardShortMessageRequestIndication.getMAPDialog();
            mapDialogSms.setUserObject(invokeId);

            ReturnResultLast returnResultLast = new ReturnResultLastImpl();
            returnResultLast.setInvokeId(invokeId);
            mapDialogSms.sendReturnResultLastComponent(returnResultLast);
            mapDialogSms.close(false);
        } catch (MAPException e) {
            log.error("Error while sending MtForwardShortMessageRequest result ", e);
        }
    }

    @Override
    public void onMtForwardShortMessageResponse(MtForwardShortMessageResponse mtForwardShortMessageResponseIndication) {}

    @Override
    public void onSendRoutingInfoForSMRequest(SendRoutingInfoForSMRequest sendRoutingInfoForSMRequestIndication) {
        if (log.isDebugEnabled()) {
            log.debug("onSendRoutingInfoForSMRequest DialogId={}",
                    sendRoutingInfoForSMRequestIndication.getMAPDialog().getLocalDialogId());
        }
        try {
            long invokeId = sendRoutingInfoForSMRequestIndication.getInvokeId();
            MAPDialogSms mapDialogSms = sendRoutingInfoForSMRequestIndication.getMAPDialog();
            mapDialogSms.setUserObject(invokeId);
            IMSI imsi = new IMSIImpl("748031234567890");
            ISDNAddressString networkNodeNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "598991900032");
            byte[] lmsiByte;
            Random rand = new Random();
            switch (rand.nextInt(4) + 1) {
                case 1: lmsiByte = new byte[]{114, 2, (byte) 233, (byte) 140}; break;
                case 2: lmsiByte = new byte[]{113, (byte) 255, (byte) 172, (byte) 206}; break;
                case 3: lmsiByte = new byte[]{114, 2, (byte) 235, 55}; break;
                default: lmsiByte = new byte[]{114, 2, (byte) 231, (byte) 213}; break;
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
    public void onSendRoutingInfoForSMResponse(SendRoutingInfoForSMResponse sendRoutingInfoForSMResponseIndication) {}

    @Override
    public void onReportSMDeliveryStatusRequest(ReportSMDeliveryStatusRequest reportSMDeliveryStatusRequestIndication) {}

    @Override
    public void onReportSMDeliveryStatusResponse(ReportSMDeliveryStatusResponse reportSMDeliveryStatusResponseIndication) {}

    @Override
    public void onInformServiceCentreRequest(InformServiceCentreRequest informServiceCentreRequestIndication) {}

    @Override
    public void onAlertServiceCentreRequest(AlertServiceCentreRequest alertServiceCentreRequestIndication) {}

    @Override
    public void onAlertServiceCentreResponse(AlertServiceCentreResponse alertServiceCentreResponseIndication) {}

    @Override
    public void onReadyForSMRequest(ReadyForSMRequest readyForSMRequest) {}

    @Override
    public void onReadyForSMResponse(ReadyForSMResponse readyForSMResponse) {}

    @Override
    public void onNoteSubscriberPresentRequest(NoteSubscriberPresentRequest noteSubscriberPresentRequest) {}

    // ═══════════════════════════════════════════════════════════
    // main
    // ═══════════════════════════════════════════════════════════

    public static void main(String[] args) {
        String configPath = args.length >= 1 ? args[0] : "ss7-mo-sms-client.json";

        NDIALOGS = Integer.getInteger("ss7.load.ndialogs", 60000);
        MAXCONCURRENTDIALOGS = Integer.getInteger("ss7.load.rateLimit", 400);
        RAMP_UP_PERIOD = Integer.getInteger("ss7.load.rampUp", 0);
        SENDING_MESSAGE_THREAD_COUNT = Integer.getInteger("ss7.load.senderThreads",
                Runtime.getRuntime().availableProcessors() * 2);
        SCCP_CLIENT_ADDRESS = System.getProperty("ss7.load.clientAddress", "1111112");
        SCCP_SERVER_ADDRESS = System.getProperty("ss7.load.serverAddress", "9960639999");

        System.out.println("Config      : " + configPath);
        System.out.println("NDIALOGS    : " + NDIALOGS);
        System.out.println("Rate Limit  : " + MAXCONCURRENTDIALOGS);
        System.out.println("Ramp-up     : " + RAMP_UP_PERIOD);
        System.out.println("Sender threads: " + SENDING_MESSAGE_THREAD_COUNT);
        System.out.println("Client addr : " + SCCP_CLIENT_ADDRESS);
        System.out.println("Server addr : " + SCCP_SERVER_ADDRESS);

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
