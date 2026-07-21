
package org.restcomm.protocols.ss7.map.load.ussd;

import java.io.FileWriter;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.netty.NettySctpManagementImpl;
import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.m3ua.Asp;
import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.IPSPType;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.NetworkAppearance;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.restcomm.protocols.ss7.map.MAPStackImpl;
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
import org.restcomm.protocols.ss7.sccp.LoadSharingAlgorithm;
import org.restcomm.protocols.ss7.sccp.NetworkIdState;
import org.restcomm.protocols.ss7.sccp.OriginationType;
import org.restcomm.protocols.ss7.sccp.Router;
import org.restcomm.protocols.ss7.sccp.RuleType;
import org.restcomm.protocols.ss7.sccp.SccpResource;
import org.restcomm.protocols.ss7.sccp.impl.SccpStackImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.sccpext.impl.SccpExtModuleImpl;
import org.restcomm.protocols.ss7.sccpext.router.RouterExt;
import org.restcomm.protocols.ss7.ss7ext.Ss7ExtInterface;
import org.restcomm.protocols.ss7.ss7ext.Ss7ExtInterfaceImpl;
import org.restcomm.protocols.ss7.tcap.TCAPStackImpl;
import org.restcomm.protocols.ss7.tcap.api.TCAPStack;
import org.restcomm.protocols.ss7.tcap.asn.ApplicationContextName;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

/**
 * @author amit bhayani
 * @modified <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class Client extends TestHarnessUssd {

    private static Logger logger = Logger.getLogger(Client.class);

    // TCAP
    private TCAPStack tcapStack;

    // MAP
    private MAPStackImpl mapStack;
    private MAPProvider mapProvider;

    // SCCP
    SccpExtModuleImpl sccpExtModule;
    private SccpStackImpl sccpStack;
    private Router router;
    private RouterExt routerExt;
    private SccpResource sccpResource;

    // M3UA
    private M3UAManagementImpl clientM3UAMgmt;

    // SCTP
    private NettySctpManagementImpl sctpManagement;

    // a ramp-up period is required for performance testing.
    int endCount;
    transient boolean endReportPrinted;

    // AtomicInteger nbConcurrentDialogs = new AtomicInteger(0);

    volatile long start = 0L;
    volatile long prev = 0L;
    volatile long loadStartMs = 0L;

    /** Zero-burst TPS gate (replaces Guava SmoothBursty which stamps after idle). */
    private StrictTpsLimiter rateLimiterObj = null;

    private CsvWriter csvWriter;

    private Semaphore inflightSem;
    /** Dialog ids that currently hold an inflight permit (prevents double-release). */
    private final ConcurrentHashMap<Long, Boolean> inflightDialogs = new ConcurrentHashMap<Long, Boolean>();
    /** Ensures CompletedScenario/endCount increment once per dialog (Close and/or Release). */
    private final ConcurrentHashMap<Long, Boolean> completedDialogs = new ConcurrentHashMap<Long, Boolean>();
    /** Think-delay via schedule (must NOT block pool threads with Thread.sleep). */
    private ScheduledExecutorService ussdReplyScheduler;
    private final AtomicInteger createdCount = new AtomicInteger(0);
    /** Dialogs that left inflight (success or error) — used to exit NDIALOGS mode without hanging. */
    private final AtomicInteger finishedCount = new AtomicInteger(0);
    // #region agent log
    private static final AtomicLong AGENT_REPLY_SCHEDULED = new AtomicLong();
    // #endregion

    private UssdMenuEngine menuEngine;
    private UssdMenuEngine.Profile menuProfile = UssdMenuEngine.Profile.RANDOM;
    private final Random thinkRandom = new Random();

    // #region agent log
    private static final String DEBUG_LOG_PATH = "/home/meodien/Desktop/ethiopia-working-dir/.cursor/debug-6dfc1e.log";

    private static void agentDebugLog(String hypothesisId, String location, String message, String dataJson) {
        // Default OFF: sync FileWriter per dialog dominates 1000 TPS runs. Enable: -DagentDebug=true
        if (!Boolean.parseBoolean(System.getProperty("agentDebug", "false"))) {
            return;
        }
        try (FileWriter fw = new FileWriter(DEBUG_LOG_PATH, true)) {
            fw.write(String.format(
                    "{\"sessionId\":\"6dfc1e\",\"runId\":\"post-fix\",\"hypothesisId\":\"%s\",\"location\":\"%s\",\"message\":\"%s\",\"data\":%s,\"timestamp\":%d}%n",
                    hypothesisId, location, message, dataJson, System.currentTimeMillis()));
        } catch (Exception ignored) {
        }
    }
    // #endregion

    protected void initializeStack(IpChannelType ipChannelType) throws Exception {

        this.rateLimiterObj = new StrictTpsLimiter(MAXCONCURRENTDIALOGS);
        // Backpressure: keep open dialogs near a few seconds of offered TPS.
        // Previous duration formula (tps*15*1.5 ≈ 22.5k) let create race far ahead of complete.
        // Override: -DmapLoadMaxInflight=N
        String inflightProp = System.getProperty("mapLoadMaxInflight");
        if (inflightProp != null && inflightProp.length() > 0) {
            MAX_INFLIGHT = Math.max(1, Integer.parseInt(inflightProp));
        } else if (DURATION_MINUTES <= 0) {
            MAX_INFLIGHT = Math.max(MAXCONCURRENTDIALOGS, Math.min(500, MAXCONCURRENTDIALOGS * 3));
        } else {
            // Measured: completion saturates ~100 dialogs/s without fireEvent; gate (adaptive)
            // releases MO before dialog/TCAP timeout under backlog. Allow ~1–1.5s backlog at
            // target TPS once capacity rises: pad to 1500 for 1000 TPS create pacing.
            MAX_INFLIGHT = Math.max(500, Math.min(1500, MAXCONCURRENTDIALOGS));
        }
        this.inflightSem = new Semaphore(MAX_INFLIGHT, true);
        // Schedule think-delay (do not sleep on worker threads — that queued ~30s/dialog at 3k inflight).
        int replyThreads = Math.max(32, Math.min(256, Math.max(SENDING_MESSAGE_THREAD_COUNT * 8, MAX_INFLIGHT / 20)));
        this.ussdReplyScheduler = Executors.newScheduledThreadPool(replyThreads);
        logger.warn("ussdReplyScheduler threads=" + replyThreads + " MAX_INFLIGHT=" + MAX_INFLIGHT);

        this.initSCTP(ipChannelType);

        // Initialize M3UA first
        this.initM3UA();

        // Initialize SCCP
        this.initSCCP();

        // Initialize TCAP
        this.initTCAP();

        // Initialize MAP
        this.initMAP();

        // Finally, start the ASP
        this.clientM3UAMgmt.startAsp("ASP1");

        // Wait for AS to become ACTIVE before starting load test
        logger.info("Waiting for AS1 to become ACTIVE...");
        int waitCount = 0;
        java.lang.reflect.Method getAsMethod = org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl.class.getDeclaredMethod("getAs", String.class);
        getAsMethod.setAccessible(true);
        while (waitCount < 60) {
            org.restcomm.protocols.ss7.m3ua.As as = (org.restcomm.protocols.ss7.m3ua.As) getAsMethod.invoke(this.clientM3UAMgmt, "AS1");
            if (as != null && as.getState() != null && "ACTIVE".equals(as.getState().getName())) {
                logger.info("AS1 is now ACTIVE! Starting load test.");
                break;
            }
            Thread.sleep(1000);
            waitCount++;
            logger.info("Waiting for AS1 to become ACTIVE... (" + waitCount + "s)");
        }
        if (waitCount >= 60) {
            logger.warn("Timeout waiting for AS1 to become ACTIVE. Proceeding anyway.");
        }

        this.csvWriter = new CsvWriter("map");
        this.csvWriter.addCounter(CREATED_DIALOGS);
        this.csvWriter.addCounter(SUCCESSFUL_DIALOGS);
        this.csvWriter.addCounter(ERROR_DIALOGS);
        this.csvWriter.start(TEST_START_DELAY, PRINT_WRITER_PERIOD);

        try {
            this.menuEngine = UssdMenuEngine.defaultEngine();
            this.menuProfile = UssdMenuEngine.Profile.valueOf(MENU_PROFILE.toUpperCase());
        } catch (Exception e) {
            throw new Exception("Failed to load USSD menu config", e);
        }
    }

    private void initSCTP(IpChannelType ipChannelType) throws Exception {
        this.sctpManagement = new NettySctpManagementImpl("Client");
        // this.sctpManagement.setSingleThread(false);
        this.sctpManagement.start();
        this.sctpManagement.setConnectDelay(10000);
        this.sctpManagement.removeAllResources();

        // 1. Create SCTP Association
        // Use HOST_PORT from args for fixed local port (e.g. 8011)
        if (EXTRA_HOST_ADDRESS.equals("-1"))
            sctpManagement.addAssociation(HOST_IP, HOST_PORT, PEER_IP, PEER_PORT, CLIENT_ASSOCIATION_NAME, ipChannelType, null);
        else
            sctpManagement.addAssociation(HOST_IP, HOST_PORT, PEER_IP, PEER_PORT, CLIENT_ASSOCIATION_NAME, ipChannelType, new String[] { EXTRA_HOST_ADDRESS });
    }

    private void initM3UA() throws Exception {
        this.clientM3UAMgmt = new M3UAManagementImpl("Client", null, new Ss7ExtInterfaceImpl());
        this.clientM3UAMgmt.setTransportManagement(this.sctpManagement);
        this.clientM3UAMgmt.setDeliveryMessageThreadCount(DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT);
        this.clientM3UAMgmt.start();
        this.clientM3UAMgmt.removeAllResources();

        // m3ua as create rc <rc> <ras-name>
        RoutingContext rc = factory.createRoutingContext(new long[] { ROUTING_CONTEXT });
        TrafficModeType trafficModeType = factory.createTrafficModeType(TrafficModeType.Loadshare);
        NetworkAppearance na = factory.createNetworkAppearance(NETWORK_APPEARANCE);

        IPSPType ipspType = null;
        if (AS_FUNCTIONALITY == Functionality.IPSP)
            ipspType = IPSPType.CLIENT;

        // Step 1 : Create AS
        this.clientM3UAMgmt.createAs("AS1", AS_FUNCTIONALITY, ExchangeType.SE, ipspType, rc, trafficModeType, 1, na);
        // Step 2 : Create ASP
        this.clientM3UAMgmt.createAspFactory("ASP1", CLIENT_ASSOCIATION_NAME);
        // Step 3 : Assign ASP to AS
        Asp asp = this.clientM3UAMgmt.assignAspToAs("AS1", "ASP1");
        // Step 4 : Add Route. Remote point code is 2
        this.clientM3UAMgmt.addRoute(DESTINATION_PC, ORIGINATING_PC, SERVICE_INDICATOR, "AS1");
    }

    private void initSCCP() throws Exception {
        Ss7ExtInterface ss7ExtInterface = new Ss7ExtInterfaceImpl();
        sccpExtModule = new SccpExtModuleImpl();
        ss7ExtInterface.setSs7ExtSccpInterface(sccpExtModule);
        this.sccpStack = new SccpStackImpl("MapLoadClientSccpStack", ss7ExtInterface);
        this.sccpStack.setMtp3UserPart(1, this.clientM3UAMgmt);

        // this.sccpStack.setCongControl_Algo(SccpCongestionControlAlgo.levelDepended);

        this.sccpStack.start();
        this.sccpStack.removeAllResources();

        this.router = this.sccpStack.getRouter();
        this.routerExt = sccpExtModule.getRouterExt();
        this.sccpResource = this.sccpStack.getSccpResource();

        this.sccpResource.addRemoteSpc(1, DESTINATION_PC, 0, 0);
        this.sccpResource.addRemoteSsn(1, DESTINATION_PC, USSD_SSN, 0, false);

        this.router.addMtp3ServiceAccessPoint(1, 1, ORIGINATING_PC, NETWORK_INDICATOR, 0, null);
        this.router.addMtp3Destination(1, 1, DESTINATION_PC, DESTINATION_PC, 0, 255, 255);

        ParameterFactoryImpl fact = new ParameterFactoryImpl();
        EncodingScheme ec = new BCDEvenEncodingScheme();
        GlobalTitle gt1 = fact.createGlobalTitle("-", 0, org.restcomm.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, ec,
                NatureOfAddress.INTERNATIONAL);
        GlobalTitle gt2 = fact.createGlobalTitle("-", 0, org.restcomm.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, ec,
                NatureOfAddress.INTERNATIONAL);
        SccpAddress localAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt1, ORIGINATING_PC, 0);
        this.routerExt.addRoutingAddress(1, localAddress);
        SccpAddress remoteAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt2, DESTINATION_PC, 0);
        this.routerExt.addRoutingAddress(2, remoteAddress);

        GlobalTitle gt = fact.createGlobalTitle("*", 0, org.restcomm.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, ec,
                NatureOfAddress.INTERNATIONAL);
        SccpAddress pattern = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, 0, 0);
        this.routerExt.addRule(1, RuleType.SOLITARY, LoadSharingAlgorithm.Bit0, OriginationType.REMOTE, pattern,
                "K", 1, -1, null, 0, null);
        this.routerExt.addRule(2, RuleType.SOLITARY, LoadSharingAlgorithm.Bit0, OriginationType.LOCAL, pattern, "K",
                2, -1, null, 0, null);
    }

    private void initTCAP() throws Exception {
        this.tcapStack = new TCAPStackImpl("Test", this.sccpStack.getSccpProvider(), MSC_SSN);
        this.tcapStack.start();
        // Dialog first, then invoke (invoke must be ≤ current dialogTimeout).
        this.tcapStack.setDialogIdleTimeout(45000);
        this.tcapStack.setInvokeTimeout(30000);
        this.tcapStack.setMaxDialogs(MAX_DIALOGS);
        this.tcapStack.setCongControl_ExecutorDelayThreshold_1(5.0);
        this.tcapStack.setCongControl_ExecutorDelayThreshold_2(15.0);
        this.tcapStack.setCongControl_ExecutorDelayThreshold_3(30.0);
    }

    private void releaseInflight(long dialogId) {
        if (this.inflightSem != null && this.inflightDialogs.remove(dialogId) != null) {
            this.finishedCount.incrementAndGet();
            this.inflightSem.release();
        }
    }

    /** Reserve a create slot for NDIALOGS mode (avoids multi-thread overshoot). */
    private boolean tryReserveCreateSlot() {
        if (DURATION_MINUTES > 0) {
            createdCount.incrementAndGet();
            return true;
        }
        while (true) {
            int cur = createdCount.get();
            if (cur >= NDIALOGS) {
                return false;
            }
            if (createdCount.compareAndSet(cur, cur + 1)) {
                return true;
            }
        }
    }

    /** Count a successful dialog once (TC-END may deliver Close without Release, or both). */
    private void markDialogCompleted(long dialogId) {
        if (this.completedDialogs.putIfAbsent(dialogId, Boolean.TRUE) != null) {
            return;
        }
        this.menuEngine.clearDialog(dialogId);
        releaseInflight(dialogId);
        this.csvWriter.incrementCounter(SUCCESSFUL_DIALOGS);
        this.endCount++;
        // #region agent log
        long elapsedMs = loadStartMs > 0 ? System.currentTimeMillis() - loadStartMs : -1;
        agentDebugLog("H4", "Client.markDialogCompleted", "dialog completed",
                String.format("{\"dialogId\":%d,\"endCount\":%d,\"elapsedSinceLoadMs\":%d}",
                        dialogId, endCount, elapsedMs));
        // #endregion

        if (this.endCount < NDIALOGS && !isDurationExpired()) {
            if ((this.endCount % 10000) == 0) {
                long current = System.currentTimeMillis();
                float sec = (float) (current - prev) / 1000f;
                prev = current;
                logger.warn("Completed 10000 Dialogs, dialogs per second: " + (float) (10000 / sec));
            }
        } else {
            if ((this.endCount >= NDIALOGS || isDurationExpired()) && !endReportPrinted) {
                endReportPrinted = true;
                long current = System.currentTimeMillis();
                logger.warn("Start Time = " + start);
                logger.warn("Current Time = " + current);
                float sec = (float) (current - start) / 1000f;
                int completedDialogsCount = this.endCount - (RAMP_UP_PERIOD < 0 ? RAMP_UP_PERIOD : 0);
                if (completedDialogsCount < 0) {
                    completedDialogsCount = this.endCount;
                }
                logger.warn("Total time in sec = " + sec);
                logger.warn("Total completed dialogs = " + completedDialogsCount);
                logger.warn("Throughput = " + (float) (completedDialogsCount / sec));
                if (isDurationMode()) {
                    logger.warn("[DURATION MODE] Test completed after " + DURATION_MINUTES + " minute(s)");
                }
            }
        }
    }

    private void initMAP() throws Exception {

        // this.mapStack = new MAPStackImpl(this.sccpStack.getSccpProvider(), SSN);
        this.mapStack = new MAPStackImpl("TestClient", this.tcapStack.getProvider());
        this.mapProvider = this.mapStack.getMAPProvider();

        this.mapProvider.addMAPDialogListener(this);
        this.mapProvider.getMAPServiceSupplementary().addMAPServiceListener(this);

        this.mapProvider.getMAPServiceSupplementary().activate();

        this.mapStack.start();
    }

    private void initiateUSSD() throws MAPException {
        if (Boolean.parseBoolean(System.getProperty("mapLoadDebug", "false"))) {
            System.out.println("[DEBUG] initiateUSSD() called");
        }
        Random r = new Random();
        NetworkIdState networkIdState = this.mapStack.getMAPProvider().getNetworkIdState(0);
        int executorCongestionLevel = this.mapStack.getMAPProvider().getExecutorCongestionLevel();
        int random = 0;
        if (!(networkIdState == null
                || networkIdState.isAvailable() && networkIdState.getCongLevel() <= 0 && executorCongestionLevel <= 0)) {
            // congestion or unavailable
            logger.warn("**** Outgoing congestion control: MAP load test client: networkIdState=" + networkIdState
                    + ", executorCongestionLevel=" + executorCongestionLevel);
            long backoffMs = Math.min(50L * (1L << Math.min(executorCongestionLevel, 4)), 1000L);
            // #region agent log
            agentDebugLog("H3", "Client.initiateUSSD", "congestion backoff",
                    String.format("{\"backoffMs\":%d,\"executorCongestionLevel\":%d,\"networkAvailable\":%b}",
                            backoffMs, executorCongestionLevel,
                            networkIdState != null && networkIdState.isAvailable()));
            // #endregion
            try {
                Thread.sleep(backoffMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (Boolean.parseBoolean(System.getProperty("mapLoadDebug", "false"))) {
            System.out.println("[DEBUG] Acquiring rate limiter...");
        }
        if (loadStartMs == 0L) {
            loadStartMs = System.currentTimeMillis();
        }
        long elapsed = System.currentTimeMillis() - loadStartMs;
        double desiredRate = WarmupRateHelper.tpsAt(elapsed, MAXCONCURRENTDIALOGS);
        if (Math.abs(this.rateLimiterObj.getRate() - desiredRate) > 0.01) {
            this.rateLimiterObj.setRate(desiredRate);
        }
        long waitedMs;
        try {
            waitedMs = this.rateLimiterObj.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        // #region agent log
        agentDebugLog("H7", "Client.initiateUSSD", "strict rate acquired",
                String.format("{\"waitedMs\":%d,\"rate\":%.3f,\"desiredRate\":%.3f}",
                        waitedMs, this.rateLimiterObj.getRate(), desiredRate));
        // #endregion
        try {
            this.inflightSem.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        if (!tryReserveCreateSlot()) {
            this.inflightSem.release();
            return;
        }
        // #region agent log
        agentDebugLog("H2", "Client.initiateUSSD", "dialog admitted",
                String.format("{\"inflightRemaining\":%d,\"maxInflight\":%d,\"endCount\":%d,\"createdCount\":%d}",
                        this.inflightSem.availablePermits(), MAX_INFLIGHT, endCount, createdCount.get()));
        // #endregion
        if (isMapLoadDebug()) {
            System.out.println("[DEBUG] Rate limiter acquired");
        }
        // System.out.println("initiateUSSD");

        // First create Dialog
        AddressString origRef = this.mapProvider.getMAPParameterFactory()
                .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "12345");
        AddressString destRef = this.mapProvider.getMAPParameterFactory()
                .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "67890");

        SccpAddress clientSccpAddress = createSccpAddress(ROUTING_INDICATOR, ORIGINATING_PC, MSC_SSN, SCCP_CLIENT_ADDRESS);
        SccpAddress serverSccpAddress = createSccpAddress(ROUTING_INDICATOR, DESTINATION_PC, USSD_SSN, SCCP_SERVER_ADDRESS);
        MAPDialogSupplementary mapDialog = this.mapProvider.getMAPServiceSupplementary().createNewDialog(MAPApplicationContext
                .getInstance(MAPApplicationContextName.networkUnstructuredSsContext, MAPApplicationContextVersion.version2),
                clientSccpAddress, origRef, serverSccpAddress, destRef);

        CBSDataCodingScheme ussdDataCodingScheme = new CBSDataCodingSchemeImpl(0x0f);

        USSDString ussdString = this.mapProvider.getMAPParameterFactory().createUSSDString(USSD_MESSAGE, null, null);

        // Unique MSISDN per dialog — shared MSISDN collides with bridge markActive / session locks
        // under concurrent load (and made failures harder to diagnose).
        String msisdnDigits = String.valueOf(8000000000L + (Math.abs(r.nextLong()) % 1000000000L));
        ISDNAddressString msisdn = this.mapProvider.getMAPParameterFactory()
                .createISDNAddressString(AddressNature.international_number, NumberingPlan.ISDN, msisdnDigits);

        mapDialog.addProcessUnstructuredSSRequest(ussdDataCodingScheme, ussdString, null, msisdn);

        // nbConcurrentDialogs.incrementAndGet();

        // This will initiate the TC-BEGIN with INVOKE component
        if (isMapLoadDebug()) {
            System.out.println("[DEBUG] Sending mapDialog...");
        }
        try {
            mapDialog.send();
            long dialogId = mapDialog.getLocalDialogId();
            this.inflightDialogs.put(dialogId, Boolean.TRUE);
            this.menuEngine.beginDialog(dialogId);
            this.csvWriter.incrementCounter(CREATED_DIALOGS);
        } catch (Exception e) {
            // Permit + create slot were reserved before send; free both if dialog was never tracked.
            createdCount.decrementAndGet();
            this.inflightSem.release();
            throw e;
        }
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
        } catch (InterruptedException e) {
            logger.error("an error occurred while stopping csvWriter", e);
        }
        if (this.ussdReplyScheduler != null) {
            this.ussdReplyScheduler.shutdownNow();
        }
    }

    public static void main(String[] args) {
        // #region agent log
        {
            Runtime rt = Runtime.getRuntime();
            agentDebugLog("H6", "Client.main", "jvm heap at start",
                    String.format("{\"maxMemoryMb\":%d,\"totalMemoryMb\":%d,\"freeMemoryMb\":%d,\"availableProcessors\":%d}",
                            rt.maxMemory() / (1024 * 1024),
                            rt.totalMemory() / (1024 * 1024),
                            rt.freeMemory() / (1024 * 1024),
                            rt.availableProcessors()));
        }
        // #endregion
        int i = 0;
        IpChannelType ipChannelType = IpChannelType.SCTP;

        if (args.length >= 23 && args.length <= 28) {
            NDIALOGS = Integer.parseInt(args[i++]);
            MAXCONCURRENTDIALOGS = Integer.parseInt(args[i++]);
            if (args[i++].toLowerCase().equals("tcp"))
                ipChannelType = IpChannelType.TCP;
            HOST_IP = args[i++];
            HOST_PORT = Integer.parseInt(args[i++]);
            EXTRA_HOST_ADDRESS = args[i++];
            PEER_IP = args[i++];
            PEER_PORT = Integer.parseInt(args[i++]);
            AS_FUNCTIONALITY = Functionality.valueOf(args[i++]);
            ROUTING_CONTEXT = Integer.parseInt(args[i++]);
            NETWORK_APPEARANCE = Integer.parseInt(args[i++]);
            ORIGINATING_PC = Integer.parseInt(args[i++]);
            DESTINATION_PC = Integer.parseInt(args[i++]);
            SERVICE_INDICATOR = Integer.parseInt(args[i++]);
            NETWORK_INDICATOR = Integer.parseInt(args[i++]);
            USSD_SSN = Integer.parseInt(args[i++]);
            HLR_SSN = Integer.parseInt(args[i++]);
            MSC_SSN = Integer.parseInt(args[i++]);
            SCCP_CLIENT_ADDRESS = args[i++];
            SCCP_SERVER_ADDRESS = args[i++];
            ROUTING_INDICATOR = RoutingIndicator.valueOf(Integer.parseInt(args[i++]));
            DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT = Integer.parseInt(args[i++]);
            RAMP_UP_PERIOD = Integer.parseInt(args[i++]);

            if (args.length >= 24) {
                DURATION_MINUTES = Integer.parseInt(args[i++]);
            }
            if (args.length >= 25) {
                USSD_MESSAGE = args[i++];
            }
            if (args.length >= 26) {
                MENU_PROFILE = args[i++];
            }
            if (args.length >= 27) {
                THINK_MIN_MS = Integer.parseInt(args[i++]);
            }
            if (args.length >= 28) {
                THINK_MAX_MS = Integer.parseInt(args[i++]);
            }

            System.out.println("IpChannelType = " + ipChannelType);
            System.out.println("DURATION_MINUTES = " + DURATION_MINUTES);
            System.out.println("USSD_MESSAGE = " + USSD_MESSAGE);
            System.out.println("MENU_PROFILE = " + MENU_PROFILE);
            System.out.println("THINK_MIN_MS = " + THINK_MIN_MS);
            System.out.println("THINK_MAX_MS = " + THINK_MAX_MS);
            System.out.println("HOST_IP = " + HOST_IP);
            System.out.println("HOST_PORT = " + HOST_PORT);
            System.out.println("EXTRA_HOST_ADDRESS = " + EXTRA_HOST_ADDRESS);
            System.out.println("PEER_IP = " + PEER_IP);
            System.out.println("PEER_PORT = " + PEER_PORT);
            System.out.println("AS_FUNCTIONALITY = " + AS_FUNCTIONALITY);
            System.out.println("ROUTING_CONTEXT = " + ROUTING_CONTEXT);
            System.out.println("NETWORK_APPEARANCE = " + NETWORK_APPEARANCE);
            System.out.println("ORIGINATING_PC = " + ORIGINATING_PC);
            System.out.println("DESTINATION_PC = " + DESTINATION_PC);
            System.out.println("SERVICE_INDICATOR = " + SERVICE_INDICATOR);
            System.out.println("NETWORK_INDICATOR = " + NETWORK_INDICATOR);
            System.out.println("USSD_SSN = " + USSD_SSN);
            System.out.println("HLR_SSN = " + HLR_SSN);
            System.out.println("MSC_SSN = " + MSC_SSN);
            System.out.println("SCCP_CLIENT_ADDRESS = " + SCCP_CLIENT_ADDRESS);
            System.out.println("SCCP_SERVER_ADDRESS = " + SCCP_SERVER_ADDRESS);
            System.out.println("ROUTING_INDICATOR = " + ROUTING_INDICATOR);
            System.out.println("DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT = " + DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT);
            System.out.println("RAMP_UP_PERIOD = " + RAMP_UP_PERIOD);
            System.out.println("SENDING_MESSAGE_THREAD_COUNT = " + SENDING_MESSAGE_THREAD_COUNT);
            System.out.println("NDIALOGS = " + NDIALOGS);
            System.out.println("MAXCONCURRENTDIALOGS = " + MAXCONCURRENTDIALOGS);
            System.out.println(WarmupRateHelper.summary(MAXCONCURRENTDIALOGS));
            // Avoid multi-thread stampede on NDIALOGS smoke; pace via rate limiter instead.
            if (DURATION_MINUTES <= 0) {
                SENDING_MESSAGE_THREAD_COUNT = Math.max(1, Math.min(SENDING_MESSAGE_THREAD_COUNT, MAXCONCURRENTDIALOGS));
                System.out.println("SENDING_MESSAGE_THREAD_COUNT capped to " + SENDING_MESSAGE_THREAD_COUNT
                        + " for NDIALOGS mode");
            }
        }

        final Client client = new Client();
        client.endCount = RAMP_UP_PERIOD;

        try {
            client.initializeStack(ipChannelType);
            client.initDuration();

            Thread.sleep(TEST_START_DELAY);

           // threads creating
            Thread[] threads = new Thread[SENDING_MESSAGE_THREAD_COUNT];
            for (int j = 0; j < SENDING_MESSAGE_THREAD_COUNT; j++) {
                threads[j] = new Thread(client.new DialogInitiator());
            }
            for (int j = 0; j < SENDING_MESSAGE_THREAD_COUNT; j++) {
                threads[j].start();
            }

            while (client.endCount < NDIALOGS && !client.isDurationExpired()
                    && (DURATION_MINUTES > 0 || client.createdCount.get() < NDIALOGS
                            || client.inflightDialogs.size() > 0)) {
                Thread.sleep(100);
                // NDIALOGS mode: stop waiting once all reserved dialogs finished (success or error).
                if (DURATION_MINUTES <= 0 && client.createdCount.get() >= NDIALOGS
                        && client.finishedCount.get() >= client.createdCount.get()) {
                    break;
                }
                if (DURATION_MINUTES <= 0 && client.createdCount.get() >= NDIALOGS
                        && client.inflightDialogs.isEmpty()) {
                    break;
                }
                // Absolute cap: do not hang forever if a dialog loses its release callback.
                if (DURATION_MINUTES <= 0 && client.loadStartMs > 0L
                        && System.currentTimeMillis() - client.loadStartMs > 120000L) {
                    System.err.println("[DEBUG] NDIALOGS absolute deadline reached; inflight="
                            + client.inflightDialogs.size() + " finished=" + client.finishedCount.get()
                            + " created=" + client.createdCount.get());
                    break;
                }
            }

            // Drain late completes: NDIALOGS always; duration mode after the offer window ends
            // (otherwise CSV stops with gap ≈ MAX_INFLIGHT and under-counts Completed).
            {
                long drainMs = DURATION_MINUTES > 0 ? 45000L : 15000L;
                long drainDeadline = System.currentTimeMillis() + drainMs;
                while (System.currentTimeMillis() < drainDeadline && client.inflightDialogs.size() > 0) {
                    Thread.sleep(200);
                }
                if (!client.inflightDialogs.isEmpty()) {
                    System.err.println("[DEBUG] Clearing " + client.inflightDialogs.size()
                            + " stuck inflight dialog(s) after drain");
                    for (Long stuckId : new java.util.ArrayList<Long>(client.inflightDialogs.keySet())) {
                        client.releaseInflight(stuckId);
                    }
                }
            }

            client.terminate();
            // Non-daemon SCTP/TCAP/M3UA threads otherwise keep the JVM alive after main returns.
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
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
        logger.error(String.format("onErrorComponent for Dialog=%d and invokeId=%d MAPErrorMessage=%s",
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
        logger.error(String.format("onRejectComponent for Dialog=%d and invokeId=%d Problem=%s isLocalOriginated=%s",
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
        logger.error(String.format("onInvokeTimeout for Dialog=%d and invokeId=%d", mapDialog.getLocalDialogId(), invokeId));
        // #region agent log
        agentDebugLog("H4", "Client.onInvokeTimeout", "invoke timeout",
                String.format("{\"dialogId\":%d,\"invokeId\":%d}", mapDialog.getLocalDialogId(), invokeId));
        // #endregion
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.supplementary. MAPServiceSupplementaryListener
     * #onProcessUnstructuredSSRequestIndication(org .mobicents.protocols.ss7.map.
     * api.service.supplementary.ProcessUnstructuredSSRequestIndication)
     */
    @Override
    public void onProcessUnstructuredSSRequest(ProcessUnstructuredSSRequest processUnstructuredSSRequest) {
        // This error condition. Client should never receive the
        // ProcessUnstructuredSSRequestIndication
        logger.error(String.format("onProcessUnstructuredSSRequestIndication for Dialog=%d and invokeId=%d",
            processUnstructuredSSRequest.getMAPDialog().getLocalDialogId(), processUnstructuredSSRequest.getInvokeId()));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.supplementary. MAPServiceSupplementaryListener
     * #onProcessUnstructuredSSResponseIndication( org.restcomm.protocols.ss7.map
     * .api.service.supplementary.ProcessUnstructuredSSResponseIndication)
     */
    @Override
    public void onProcessUnstructuredSSResponse(ProcessUnstructuredSSResponse processUnstructuredSSResponse) {
        // #region agent log
        agentDebugLog("H5", "Client.onProcessUnstructuredSSResponse", "final ussd response",
                String.format("{\"dialogId\":%d,\"invokeId\":%d}",
                        processUnstructuredSSResponse.getMAPDialog().getLocalDialogId(),
                        processUnstructuredSSResponse.getInvokeId()));
        // #endregion
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Rx ProcessUnstructuredSSResponseIndication. USSD String=%s",
                processUnstructuredSSResponse.getUSSDString()));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.supplementary. MAPServiceSupplementaryListener
     * #onUnstructuredSSRequestIndication(org.mobicents .protocols.ss7.map.api.service
     * .supplementary.UnstructuredSSRequestIndication)
     */
    @Override
    public void onUnstructuredSSRequest(UnstructuredSSRequest unstructuredSSRequest) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Rx UnstructuredSSRequestIndication. USSD String=%s ", unstructuredSSRequest.getUSSDString()));
        }
        final MAPDialogSupplementary mapDialog = unstructuredSSRequest.getMAPDialog();
        final long dialogId = mapDialog.getLocalDialogId();
        final long invokeId = unstructuredSSRequest.getInvokeId();

        if (THINK_MAX_MS <= 0) {
            // Keep MAP reply on the delivery thread when no think-delay (avoids dialog races).
            replyUnstructured(mapDialog, dialogId, invokeId);
            return;
        }

        int lo = Math.max(0, THINK_MIN_MS);
        int hi = Math.max(lo, THINK_MAX_MS);
        final int delayMs = lo + thinkRandom.nextInt(hi - lo + 1);
        // #region agent log
        long n = AGENT_REPLY_SCHEDULED.incrementAndGet();
        if (n == 1L || n % 500L == 0L) {
            agentDebugLog("H5", "Client.onUnstructuredSSRequest", "reply scheduled",
                    String.format("{\"scheduled\":%d,\"delayMs\":%d,\"dialogId\":%d}", n, delayMs, dialogId));
        }
        // #endregion
        this.ussdReplyScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                replyUnstructured(mapDialog, dialogId, invokeId);
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    private void replyUnstructured(MAPDialogSupplementary mapDialog, long dialogId, long invokeId) {
        try {
            String digit = menuEngine.nextInput(dialogId, menuProfile);
            // #region agent log
            agentDebugLog("H1", "Client.onUnstructuredSSRequest", "menu reply",
                    String.format("{\"dialogId\":%d,\"profile\":\"%s\",\"digit\":\"%s\"}",
                            dialogId, menuProfile, digit == null ? "null" : digit));
            // #endregion
            if (digit == null) {
                logger.warn("No menu input for dialog " + dialogId + ", skipping response");
                // #region agent log
                agentDebugLog("H1", "Client.onUnstructuredSSRequest", "null digit skip",
                        String.format("{\"dialogId\":%d}", dialogId));
                // #endregion
                return;
            }

            CBSDataCodingScheme ussdDataCodingScheme = new CBSDataCodingSchemeImpl(0x0f);
            USSDString ussdString = mapProvider.getMAPParameterFactory().createUSSDString(digit, null, null);
            mapDialog.addUnstructuredSSResponse(invokeId, ussdDataCodingScheme, ussdString);
            mapDialog.send();
        } catch (MAPException e) {
            logger.error(String.format("Error while sending UnstructuredSSResponse for Dialog=%d", dialogId));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.supplementary. MAPServiceSupplementaryListener
     * #onUnstructuredSSResponseIndication(org.mobicents .protocols.ss7.map.api.service
     * .supplementary.UnstructuredSSResponseIndication)
     */
    @Override
    public void onUnstructuredSSResponse(UnstructuredSSResponse unstructuredSSResponse) {
        // This is an error condition.
        // Client should never receive UnstructuredSSResponseIndication
        logger.error(String.format("onUnstructuredSSResponseIndication for Dialog=%d and invokeId=%d",
            unstructuredSSResponse.getMAPDialog().getLocalDialogId(), unstructuredSSResponse.getInvokeId()));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.service.supplementary. MAPServiceSupplementaryListener
     * #onUnstructuredSSNotifyRequestIndication(org .mobicents.protocols.ss7.map.api
     * .service.supplementary.UnstructuredSSNotifyRequestIndication)
     */
    @Override
    public void onUnstructuredSSNotifyRequest(UnstructuredSSNotifyRequest unstructuredSSNotifyRequest) {
        // This error condition. Client should never receive the
        // UnstructuredSSNotifyRequestIndication
        logger.error(String.format("onUnstructuredSSNotifyRequestIndication for Dialog=%d and invokeId=%d",
            unstructuredSSNotifyRequest.getMAPDialog().getLocalDialogId(), unstructuredSSNotifyRequest.getInvokeId()));
    }

    public void onUnstructuredSSNotifyResponseIndication(UnstructuredSSNotifyResponse unstructuredSSNotifyResponse) {
        // This error condition. Client should never receive the
        // UnstructuredSSNotifyRequestIndication
        logger.error(String.format("onUnstructuredSSNotifyResponseIndication for Dialog=%d and invokeId=%d",
            unstructuredSSNotifyResponse.getMAPDialog().getLocalDialogId(), unstructuredSSNotifyResponse.getInvokeId()));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogDelimiter
     * (org.restcomm.protocols.ss7.map.api.MAPDialog)
     */
    @Override
    public void onDialogDelimiter(MAPDialog mapDialog) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("onDialogDelimiter for DialogId=%d", mapDialog.getLocalDialogId()));
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
    public void onDialogRequest(MAPDialog mapDialog, AddressString destReference, AddressString origReference, MAPExtensionContainer extensionContainer) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "onDialogRequest for DialogId=%d DestinationReference=%s OriginReference=%s MAPExtensionContainer=%s",
                    mapDialog.getLocalDialogId(), destReference, origReference, extensionContainer));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogRequestEricsson
     * (org.restcomm.protocols.ss7.map.api.MAPDialog, org.restcomm.protocols.ss7.map.api.primitives.AddressString,
     * org.restcomm.protocols.ss7.map.api.primitives.AddressString, org.restcomm.protocols.ss7.map.api.primitives.IMSI,
     * org.restcomm.protocols.ss7.map.api.primitives.AddressString)
     */
    @Override
    public void onDialogRequestEricsson(MAPDialog mapDialog, AddressString destReference, AddressString origReference, AddressString arg3,
                                        AddressString arg4) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("onDialogRequest for DialogId=%d DestinationReference=%s OriginReference=%s ",
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
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("onDialogAccept for DialogId=%d MAPExtensionContainer=%s", mapDialog.getLocalDialogId(), extensionContainer));
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
        logger.error(String.format(
                "onDialogReject for DialogId=%d MAPRefuseReason=%s ApplicationContextName=%s MAPExtensionContainer=%s",
                mapDialog.getLocalDialogId(), refuseReason, alternativeApplicationContext, extensionContainer));
        this.menuEngine.clearDialog(mapDialog.getLocalDialogId());
        releaseInflight(mapDialog.getLocalDialogId());
        this.csvWriter.incrementCounter(ERROR_DIALOGS);
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
        logger.error(String.format("onDialogUserAbort for DialogId=%d MAPUserAbortChoice=%s MAPExtensionContainer=%s",
                mapDialog.getLocalDialogId(), userReason, extensionContainer));
        this.menuEngine.clearDialog(mapDialog.getLocalDialogId());
        releaseInflight(mapDialog.getLocalDialogId());
        this.csvWriter.incrementCounter(ERROR_DIALOGS);
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
        logger.error(String.format(
                "onDialogProviderAbort for DialogId=%d MAPAbortProviderReason=%s MAPAbortSource=%s MAPExtensionContainer=%s",
                mapDialog.getLocalDialogId(), abortProviderReason, abortSource, extensionContainer));
        this.menuEngine.clearDialog(mapDialog.getLocalDialogId());
        releaseInflight(mapDialog.getLocalDialogId());
        this.csvWriter.incrementCounter(ERROR_DIALOGS);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogClose(org .mobicents.protocols.ss7.map.api.MAPDialog)
     */
    @Override
    public void onDialogClose(MAPDialog mapDialog) {
        // #region agent log
        agentDebugLog("H5", "Client.onDialogClose", "dialog close",
                String.format("{\"dialogId\":%d}", mapDialog.getLocalDialogId()));
        // #endregion
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("DialogClose for Dialog=%d", mapDialog.getLocalDialogId()));
        }
        markDialogCompleted(mapDialog.getLocalDialogId());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogNotice( org.restcomm.protocols.ss7.map.api.MAPDialog,
     * org.restcomm.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic)
     */
    @Override
    public void onDialogNotice(MAPDialog mapDialog, MAPNoticeProblemDiagnostic noticeProblemDiagnostic) {
        logger.error(String.format("onDialogNotice for DialogId=%d MAPNoticeProblemDiagnostic=%s ",
                mapDialog.getLocalDialogId(), noticeProblemDiagnostic));
        this.menuEngine.clearDialog(mapDialog.getLocalDialogId());
        releaseInflight(mapDialog.getLocalDialogId());
        this.csvWriter.incrementCounter(ERROR_DIALOGS);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogResease
     * (org.restcomm.protocols.ss7.map.api.MAPDialog)
     */
    @Override
    public void onDialogRelease(MAPDialog mapDialog) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("onDialogRelease for DialogId=%d", mapDialog.getLocalDialogId()));
        }
        markDialogCompleted(mapDialog.getLocalDialogId());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.MAPDialogListener#onDialogTimeout
     * (org.restcomm.protocols.ss7.map.api.MAPDialog)
     */
    @Override
    public void onDialogTimeout(MAPDialog mapDialog) {
        long dialogId = mapDialog.getLocalDialogId();
        logger.error(String.format("onDialogTimeout for DialogId=%d", dialogId));
        // #region agent log
        agentDebugLog("H4", "Client.onDialogTimeout", "dialog timeout",
                String.format("{\"dialogId\":%d,\"endCount\":%d}", dialogId, endCount));
        // #endregion
        this.menuEngine.clearDialog(dialogId);
        // If already completed, do not double-count as Failed.
        if (this.completedDialogs.containsKey(dialogId)) {
            releaseInflight(dialogId);
            return;
        }
        releaseInflight(dialogId);
        this.csvWriter.incrementCounter(ERROR_DIALOGS);
    }

    @Override
    public void onUnstructuredSSNotifyResponse(UnstructuredSSNotifyResponse unstructuredSSNotifyResponse) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMAPMessage(MAPMessage mapMessage) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRegisterSSRequest(RegisterSSRequest request) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRegisterSSResponse(RegisterSSResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEraseSSRequest(EraseSSRequest request) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEraseSSResponse(EraseSSResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onActivateSSRequest(ActivateSSRequest request) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onActivateSSResponse(ActivateSSResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDeactivateSSRequest(DeactivateSSRequest request) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDeactivateSSResponse(DeactivateSSResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInterrogateSSRequest(InterrogateSSRequest request) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInterrogateSSResponse(InterrogateSSResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetPasswordRequest(GetPasswordRequest request) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetPasswordResponse(GetPasswordResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRegisterPasswordRequest(RegisterPasswordRequest request) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRegisterPasswordResponse(RegisterPasswordResponse response) {
        // TODO Auto-generated method stub

    }
    private static boolean isMapLoadDebug() {
        return Boolean.parseBoolean(System.getProperty("mapLoadDebug", "false"));
    }

    public class DialogInitiator implements Runnable {

        @Override
        public void run() {
            if (isMapLoadDebug()) {
                System.out.println("[DEBUG] DialogInitiator thread started, endCount=" + endCount + " NDIALOGS=" + NDIALOGS);
            }
            try {
                while (endCount < NDIALOGS && createdCount.get() < NDIALOGS && !isDurationExpired()) {
                    if (endCount < 0 && loadStartMs == 0L) {
                        loadStartMs = System.currentTimeMillis();
                        start = loadStartMs;
                        prev = start;
                    }
                    if (isMapLoadDebug()) {
                        System.out.println("[DEBUG] Calling initiateUSSD(), endCount=" + endCount);
                    }
                    initiateUSSD();
                }
                if (isMapLoadDebug()) {
                    System.out.println("[DEBUG] DialogInitiator loop ended, endCount=" + endCount + " expired=" + isDurationExpired());
                }
            } catch (MAPException ex) {
                System.err.println("[DEBUG] MAPException in DialogInitiator: " + ex.getMessage());
                logger.error("Exception when sending a new MAP dialog", ex);
            } catch (Exception ex) {
                System.err.println("[DEBUG] Exception in DialogInitiator: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

    }

    @Override
    public void onSendRoutingInfoForSMRequest(SendRoutingInfoForSMRequest sendRoutingInfoForSMRequestIndication) {
    }

    @Override
    public void onForwardShortMessageRequest(ForwardShortMessageRequest forwardShortMessageRequestIndication) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onForwardShortMessageResponse(ForwardShortMessageResponse forwardShortMessageResponseIndication) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMoForwardShortMessageRequest(MoForwardShortMessageRequest moForwardShortMessageRequestIndication) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMoForwardShortMessageResponse(MoForwardShortMessageResponse moForwardShortMessageResponseIndication) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMtForwardShortMessageRequest(MtForwardShortMessageRequest mtForwardShortMessageRequestIndication) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMtForwardShortMessageResponse(MtForwardShortMessageResponse mtForwardShortMessageResponseIndication) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendRoutingInfoForSMResponse(SendRoutingInfoForSMResponse sendRoutingInfoForSMResponseIndication) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReportSMDeliveryStatusRequest(ReportSMDeliveryStatusRequest reportSMDeliveryStatusRequestIndication) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReportSMDeliveryStatusResponse(ReportSMDeliveryStatusResponse reportSMDeliveryStatusResponseIndication) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInformServiceCentreRequest(InformServiceCentreRequest informServiceCentreRequestIndication) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAlertServiceCentreRequest(AlertServiceCentreRequest alertServiceCentreRequestIndication) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAlertServiceCentreResponse(AlertServiceCentreResponse alertServiceCentreResponseIndication) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadyForSMRequest(ReadyForSMRequest readyForSMRequest) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReadyForSMResponse(ReadyForSMResponse readyForSMResponse) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNoteSubscriberPresentRequest(NoteSubscriberPresentRequest noteSubscriberPresentRequest) {
        // TODO Auto-generated method stub

    }
}
