package org.restcomm.protocols.ss7.scheduler;

/**
 * Default, dependency-free classification rules for decoded TCAP/MAP/CAP work.
 *
 * <p>Adapters at the TCAP/MAP/CAP boundary must pass the actual decoded package,
 * local operation code and payload length. Global TCAP operation OIDs must be
 * passed as {@code null}; they cannot safely be classified by local MAP/CAP
 * operation-code rules.</p>
 */
public final class W2PriorityClassifier {

    public static final int DEFAULT_LARGE_CONTINUE_BYTES = 100 * 1024;

    // 3GPP TS 29.002 MAP local operation codes.
    public static final long MAP_UPDATE_LOCATION = 2L;
    public static final long MAP_MT_FORWARD_SM = 44L;
    public static final long MAP_SEND_ROUTING_INFO_FOR_SM = 45L;
    public static final long MAP_SEND_AUTHENTICATION_INFO = 56L;

    // 3GPP TS 29.078 CAP local operation codes used for online charging.
    public static final long CAP_APPLY_CHARGING = 35L;
    public static final long CAP_APPLY_CHARGING_REPORT = 36L;
    public static final long CAP_APPLY_CHARGING_GPRS = 71L;
    public static final long CAP_APPLY_CHARGING_REPORT_GPRS = 72L;

    private W2PriorityClassifier() {
    }

    public static W2Priority classify(TcapPackageKind packageKind, int payloadBytes,
            Ss7ApplicationProtocol protocol, Long localOperationCode) {
        TcapPriority tcapPriority = classifyTcap(packageKind, payloadBytes);
        MapPriority applicationPriority = classifyApplication(protocol, localOperationCode);
        return higher(tcapPriority.priority(), applicationPriority.priority());
    }

    public static TcapPriority classifyTcap(TcapPackageKind packageKind, int payloadBytes) {
        if (payloadBytes < 0) {
            throw new IllegalArgumentException("payloadBytes must be non-negative");
        }
        if (packageKind == null) {
            return TcapPriority.OTHER_NORMAL;
        }
        return switch (packageKind) {
            case BEGIN -> TcapPriority.BEGIN_HIGH;
            case CONTINUE -> payloadBytes > DEFAULT_LARGE_CONTINUE_BYTES
                    ? TcapPriority.CONTINUE_LARGE : TcapPriority.CONTINUE_NORMAL;
            case END -> TcapPriority.END_LOW;
            case ABORT -> TcapPriority.ABORT_LOW;
            case UNIDIRECTIONAL, UNKNOWN -> TcapPriority.OTHER_NORMAL;
        };
    }

    public static MapPriority classifyApplication(Ss7ApplicationProtocol protocol, Long localOperationCode) {
        if (protocol == null || localOperationCode == null) {
            return MapPriority.UNSPECIFIED;
        }
        return switch (protocol) {
            case MAP -> classifyMap(localOperationCode);
            case CAP -> classifyCap(localOperationCode);
            case TCAP, UNKNOWN -> MapPriority.UNSPECIFIED;
        };
    }

    private static MapPriority classifyMap(long localOperationCode) {
        return switch ((int) localOperationCode) {
            case (int) MAP_SEND_AUTHENTICATION_INFO, (int) MAP_UPDATE_LOCATION -> MapPriority.CRITICAL;
            case (int) MAP_SEND_ROUTING_INFO_FOR_SM, (int) MAP_MT_FORWARD_SM -> MapPriority.NORMAL;
            default -> MapPriority.UNSPECIFIED;
        };
    }

    private static MapPriority classifyCap(long localOperationCode) {
        return switch ((int) localOperationCode) {
            case (int) CAP_APPLY_CHARGING, (int) CAP_APPLY_CHARGING_REPORT,
                    (int) CAP_APPLY_CHARGING_GPRS, (int) CAP_APPLY_CHARGING_REPORT_GPRS -> MapPriority.CRITICAL;
            default -> MapPriority.UNSPECIFIED;
        };
    }

    private static W2Priority higher(W2Priority left, W2Priority right) {
        return left.score() >= right.score() ? left : right;
    }
}
