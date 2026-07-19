
package org.restcomm.protocols.ss7.map.load.sms.mt;

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
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.InformServiceCentreRequest;
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
import org.restcomm.protocols.ss7.map.api.service.sms.SM_RP_OA;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.SmsSignalInfo;
import org.restcomm.protocols.ss7.map.api.smstpdu.AbsoluteTimeStamp;
import org.restcomm.protocols.ss7.map.api.smstpdu.AddressField;
import org.restcomm.protocols.ss7.map.api.smstpdu.CharacterSet;
import org.restcomm.protocols.ss7.map.api.smstpdu.DataCodingScheme;
import org.restcomm.protocols.ss7.map.api.smstpdu.NumberingPlanIdentification;
import org.restcomm.protocols.ss7.map.api.smstpdu.ProtocolIdentifier;
import org.restcomm.protocols.ss7.map.api.smstpdu.SmsDeliverTpdu;
import org.restcomm.protocols.ss7.map.api.smstpdu.TypeOfNumber;
import org.restcomm.protocols.ss7.map.api.smstpdu.UserData;
import org.restcomm.protocols.ss7.map.api.smstpdu.UserDataHeader;
import org.restcomm.protocols.ss7.map.load.ConsoleTui;
import org.restcomm.protocols.ss7.map.load.CsvWriter;
import org.restcomm.protocols.ss7.map.primitives.AddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.smstpdu.AbsoluteTimeStampImpl;
import org.restcomm.protocols.ss7.map.smstpdu.AddressFieldImpl;
import org.restcomm.protocols.ss7.map.smstpdu.ApplicationPortAddressing16BitAddressImpl;
import org.restcomm.protocols.ss7.map.smstpdu.DataCodingSchemeImpl;
import org.restcomm.protocols.ss7.map.smstpdu.ProtocolIdentifierImpl;
import org.restcomm.protocols.ss7.map.smstpdu.SmsDeliverTpduImpl;
import org.restcomm.protocols.ss7.map.smstpdu.UserDataHeaderImpl;
import org.restcomm.protocols.ss7.map.smstpdu.UserDataImpl;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MAP MT-SMS Load Test Client — uses Ss7StackBuilder for stack init.
 * Client plays the SMSC role: sends SendRoutingInfoForSM to the HLR/MSC (Server),
 * then on the SRI response sends MtForwardShortMessageRequest to the returned network node.
 *
 * @modified <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class Client extends TestHarnessSmsMt {

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

    // Constant per-run request objects for the SRI-for-SM leg.
    private AddressString cOrigRef, cDestRef;
    private SccpAddress cClientAddr, cServerAddr;
    private MAPApplicationContext cSriAppCtx;

    /**
     * Build stack from JSON, register listeners, start ASP, launch DialogInitiator threads.
     */
    void start(String configPath) throws Exception {
        rateLimiterObj = RateLimiter.create(MAXCONCURRENTDIALOGS);

        log.info("Building jSS7 stack from config: {}", configPath);
        stack = Ss7StackBuilder.build(Path.of(configPath));
        mapProvider = stack.mapProvider();

        this.cOrigRef = mapProvider.getMAPParameterFactory()
                .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "598990012345");
        this.cDestRef = mapProvider.getMAPParameterFactory()
                .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "598990067890");
        this.cClientAddr = createSccpAddress(ROUTING_INDICATOR, ORIGINATING_PC, SMSC_SSN, SCCP_CLIENT_ADDRESS);
        this.cServerAddr = createSccpAddress(ROUTING_INDICATOR, DESTINATION_PC, HLR_SSN, SCCP_SERVER_ADDRESS);
        this.cSriAppCtx = MAPApplicationContext.getInstance(
                MAPApplicationContextName.shortMsgGatewayContext, MAPApplicationContextVersion.version3);

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

    /** Kick off one scenario: send SendRoutingInfoForSM to the HLR; the MT-FSM leg is sent from onSendRoutingInfoForSMResponse. */
    private void initiateSRI() throws MAPException {
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
                .createNewDialog(cSriAppCtx, cClientAddr, cOrigRef, cServerAddr, cDestRef);

        ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number,
                NumberingPlan.ISDN, "59899077937");
        boolean smRpPri = true;
        AddressString serviceCentreAddress = new AddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "5989900123");

        mapDialogSms.addSendRoutingInfoForSMRequest(msisdn, smRpPri, serviceCentreAddress, null,
                false, null, null, null, false, null, false, false, null, null, false);

        mapDialogSms.send();
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
                    initiateSRI();
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
    // MAPServiceSmsListener
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
    public void onMoForwardShortMessageResponse(MoForwardShortMessageResponse moForwardShortMessageResponseIndication) {}

    @Override
    public void onMtForwardShortMessageRequest(MtForwardShortMessageRequest mtForwardShortMessageRequestIndication) {}

    @Override
    public void onMtForwardShortMessageResponse(MtForwardShortMessageResponse mtForwardShortMessageResponseIndication) {
        if (log.isDebugEnabled()) {
            log.debug("onMtForwardShortMessageResponse DialogId={}",
                    mtForwardShortMessageResponseIndication.getMAPDialog().getLocalDialogId());
        }
    }

    @Override
    public void onSendRoutingInfoForSMRequest(SendRoutingInfoForSMRequest sendRoutingInfoForSMRequestIndication) {}

    /** SRI-for-SM answered by the HLR/MSC (Server) — send the actual MtForwardShortMessageRequest. */
    @Override
    public void onSendRoutingInfoForSMResponse(SendRoutingInfoForSMResponse sendRoutingInfoForSMResponseIndication) {
        if (log.isDebugEnabled()) {
            log.debug("onSendRoutingInfoForSMResponse DialogId={}",
                    sendRoutingInfoForSMResponseIndication.getMAPDialog().getLocalDialogId());
        }
        try {
            IMSI imsi = sendRoutingInfoForSMResponseIndication.getIMSI();
            LocationInfoWithLMSI locationInfoWithLMSI = sendRoutingInfoForSMResponseIndication.getLocationInfoWithLMSI();
            AddressString networkNodeNumber = locationInfoWithLMSI.getNetworkNodeNumber();

            AddressString originAddressString = this.mapProvider.getMAPParameterFactory()
                    .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "598990012345");

            SccpAddress clientSccpAddress = createSccpAddress(ROUTING_INDICATOR, ORIGINATING_PC, SMSC_SSN, SCCP_CLIENT_ADDRESS);
            SccpAddress serverSccpAddress = createSccpAddress(ROUTING_INDICATOR, DESTINATION_PC, MSC_SSN, networkNodeNumber.getAddress());

            MAPApplicationContext mapAppContext = MAPApplicationContext.getInstance(
                    MAPApplicationContextName.shortMsgMTRelayContext, MAPApplicationContextVersion.version3);

            MAPDialogSms mapDialogSms = this.mapProvider.getMAPServiceSms().createNewDialog(mapAppContext, clientSccpAddress,
                    originAddressString, serverSccpAddress, networkNodeNumber);

            SM_RP_DA da = mapProvider.getMAPParameterFactory().createSM_RP_DA(imsi);
            AddressString serviceCentreAddressOA = new AddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "5989900123");
            SM_RP_OA oa = mapProvider.getMAPParameterFactory().createSM_RP_OA_ServiceCentreAddressOA(serviceCentreAddressOA);

            AddressField originatingAddress = new AddressFieldImpl(TypeOfNumber.Alphanumeric, NumberingPlanIdentification.Unknown, "447");
            Calendar cld = new GregorianCalendar();
            int year = cld.get(Calendar.YEAR);
            int mon = cld.get(Calendar.MONTH);
            int day = cld.get(Calendar.DAY_OF_MONTH);
            int h = cld.get(Calendar.HOUR);
            int m = cld.get(Calendar.MINUTE);
            int s = cld.get(Calendar.SECOND);
            int tz = cld.get(Calendar.ZONE_OFFSET);
            AbsoluteTimeStamp serviceCentreTimeStamp = new AbsoluteTimeStampImpl(year - 2000, mon, day, h, m, s, tz / 1000 / 60 / 15);
            int dcsVal = 4; // 0 = GSM7, 4 = GSM8, 8 = UCS2
            DataCodingScheme dcs = new DataCodingSchemeImpl(dcsVal);
            UserDataHeader udh = null;
            if (dcs.getCharacterSet() == CharacterSet.GSM8) {
                ApplicationPortAddressing16BitAddressImpl apa16 = new ApplicationPortAddressing16BitAddressImpl(16020, 0);
                udh = new UserDataHeaderImpl();
                udh.addInformationElement(apa16);
            }
            Boolean moreMessagesToSend = false;
            Boolean forwardedOrSpawned = false;
            Boolean replyPathExists = false;
            Boolean statusReportIndication = true;
            Charset gsm8Charset = Charset.defaultCharset();
            UserData userData = new UserDataImpl("Load test MT-SMS text", dcs, udh, gsm8Charset);
            ProtocolIdentifier pi = new ProtocolIdentifierImpl(0);
            SmsDeliverTpdu tpdu = new SmsDeliverTpduImpl(moreMessagesToSend, forwardedOrSpawned, replyPathExists, statusReportIndication,
                    originatingAddress, pi, serviceCentreTimeStamp, userData);
            SmsSignalInfo si = mapProvider.getMAPParameterFactory().createSmsSignalInfo(tpdu, gsm8Charset);

            mapDialogSms.addMtForwardShortMessageRequest(da, oa, si, moreMessagesToSend, null, null, null, false, null, null, null, null);
            mapDialogSms.send();

            this.csvWriter.incrementCounter(CREATED_DIALOGS);
        } catch (MAPException e) {
            log.error("Error while sending MtForwardShortMessageRequest ", e);
        }
    }

    @Override
    public void onReportSMDeliveryStatusRequest(ReportSMDeliveryStatusRequest reportSMDeliveryStatusRequestIndication) {}

    @Override
    public void onReportSMDeliveryStatusResponse(ReportSMDeliveryStatusResponse reportSMDeliveryStatusResponseIndication) {}

    @Override
    public void onInformServiceCentreRequest(InformServiceCentreRequest informServiceCentreRequestIndication) {}

    /** SMSC receives AlertServiceCentre (subscriber became reachable again) — ack and retry the scenario. */
    @Override
    public void onAlertServiceCentreRequest(AlertServiceCentreRequest alertServiceCentreRequestIndication) {
        if (log.isDebugEnabled()) {
            log.debug("onAlertServiceCentreRequest DialogId={}",
                    alertServiceCentreRequestIndication.getMAPDialog().getLocalDialogId());
        }
        try {
            MAPDialogSms mapDialogSms = alertServiceCentreRequestIndication.getMAPDialog();
            ReturnResultLast returnResultLast = new ReturnResultLastImpl();
            returnResultLast.setInvokeId(alertServiceCentreRequestIndication.getInvokeId());
            mapDialogSms.sendReturnResultLastComponent(returnResultLast);
            mapDialogSms.close(false);
            initiateSRI();
        } catch (MAPException e) {
            log.error("Error handling AlertServiceCentreRequest ", e);
        }
    }

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
        String configPath = args.length >= 1 ? args[0] : "ss7-mt-sms-client.json";

        NDIALOGS = Integer.getInteger("ss7.load.ndialogs", 60000);
        MAXCONCURRENTDIALOGS = Integer.getInteger("ss7.load.rateLimit", 100);
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
