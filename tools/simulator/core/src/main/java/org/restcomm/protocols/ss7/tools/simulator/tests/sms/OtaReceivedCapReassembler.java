package org.restcomm.protocols.ss7.tools.simulator.tests.sms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jctools.maps.NonBlockingHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongSupplier;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Lab helper: reassemble concatenated SMS-PP (OTA install) payloads received on
 * MAP MT-ForwardSM and write them to disk under {@code <persistDir>/received-caps/}.
 *
 * <p>Two artefacts per completed assembly:
 *
 * <ul>
 *   <li><b>{@code .otapkt}</b> — the merged SMS-PP body exactly as it arrived
 *       (UDH stripped, segments concatenated in order). This is a TS 102.225
 *       <em>secured packet</em>, so it is longer than the pushed CAP and its
 *       payload is enciphered. Its hash will never match the CAP file.</li>
 *   <li><b>{@code .cap}</b> — written only when {@link OtaSecuredPacketVerifier}
 *       manages to decipher under KIc, verify the checksum under KID, and un-frame
 *       the GlobalPlatform LOAD blocks. This one <em>is</em> byte-identical to the
 *       pushed CAP, so its SHA-256 matches.</li>
 * </ul>
 *
 * <p>Set {@code -Dota.verify.reference-dir=<dir>} to have each recovered CAP
 * matched by hash against the CAP files that were pushed (typically
 * {@code dist/simmapps}); the result is logged as MATCH or NO-MATCH.
 *
 * <p>Incomplete / timed-out assemblies are discarded; files are written
 * atomically ({@code .tmp} → rename) so partial content never lands as the
 * final name.
 */
public final class OtaReceivedCapReassembler {

    public static final String SUBDIR = "received-caps";
    /** Extension for the merged secured packet — deliberately not {@code .cap}. */
    public static final String EXT_PACKET = ".otapkt";
    /** Extension for a fully recovered, hash-comparable CAP. */
    public static final String EXT_CAP = ".cap";
    /** Default drop incomplete assemblies after 5 minutes. */
    public static final long DEFAULT_TIMEOUT_MS = 5L * 60L * 1000L;
    /** Directory of pushed CAP files to hash-match recovered CAPs against. */
    public static final String PROP_REFERENCE_DIR = "ota.verify.reference-dir";

    /** TS 31.115 Command Packet Identifier IEI. */
    public static final int IEI_COMMAND_PACKET = 0x70;

    private static final Logger LOG = LogManager.getLogger(OtaReceivedCapReassembler.class);
    private static final DateTimeFormatter TS =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS").withZone(ZoneOffset.UTC);

    private final Path outputDir;
    private final long timeoutMs;
    private final LongSupplier clockMs;
    /** Null when verification is disabled or unavailable — capture still works. */
    private final OtaSecuredPacketVerifier verifier;
    private final NonBlockingHashMap<String, Assembly> assemblies = new NonBlockingHashMap<>();
    /** IMSI → last MSISDN seen on SRI (lab naming). */
    private final NonBlockingHashMap<String, String> imsiToMsisdn = new NonBlockingHashMap<>();
    private final AtomicLong writtenCount = new AtomicLong();
    private final AtomicLong verifiedCount = new AtomicLong();
    /** sha256 → pushed CAP name; built once, off the per-segment path. */
    private final AtomicReference<Map<String, String>> referenceIndex = new AtomicReference<>();

    public OtaReceivedCapReassembler(Path persistDir) {
        this(persistDir, DEFAULT_TIMEOUT_MS, System::currentTimeMillis);
    }

    public OtaReceivedCapReassembler(Path persistDir, long timeoutMs, LongSupplier clockMs) {
        this(persistDir, timeoutMs, clockMs, defaultVerifier());
    }

    public OtaReceivedCapReassembler(Path persistDir, long timeoutMs, LongSupplier clockMs,
            OtaSecuredPacketVerifier verifier) {
        Objects.requireNonNull(persistDir, "persistDir");
        Objects.requireNonNull(clockMs, "clockMs");
        this.outputDir = persistDir.resolve(SUBDIR);
        this.timeoutMs = timeoutMs <= 0 ? DEFAULT_TIMEOUT_MS : timeoutMs;
        this.clockMs = clockMs;
        this.verifier = verifier;
    }

    private static OtaSecuredPacketVerifier defaultVerifier() {
        if (!OtaSecuredPacketVerifier.enabled()) {
            LOG.info("OTA secured-packet verification disabled via {}",
                    OtaSecuredPacketVerifier.PROP_ENABLED);
            return null;
        }
        try {
            return new OtaSecuredPacketVerifier();
        } catch (RuntimeException ex) {
            LOG.warn("OTA secured-packet verifier unavailable ({}) — capture only", ex.toString());
            return null;
        }
    }

    public Path getOutputDir() {
        return outputDir;
    }

    public long getWrittenCount() {
        return writtenCount.get();
    }

    /** How many assemblies yielded a CAP that survived decipher + checksum. */
    public long getVerifiedCount() {
        return verifiedCount.get();
    }

    /** Remember MSISDN from SRI-for-SM so MT (IMSI-keyed) files get a friendly name. */
    public void rememberSubscriber(String imsi, String msisdn) {
        if (imsi == null || imsi.isBlank() || msisdn == null || msisdn.isBlank()) {
            return;
        }
        imsiToMsisdn.put(digits(imsi), digits(msisdn));
    }

    public String lookupMsisdn(String imsi) {
        if (imsi == null || imsi.isBlank()) {
            return null;
        }
        return imsiToMsisdn.get(digits(imsi));
    }

    /**
     * Offer one SMS user-data body (already UDH-stripped).
     *
     * @param subscriberKey IMSI preferred (digits), else any stable id
     * @param concatRef     concat reference, or {@code null} for single-part
     * @param ref16bit      true when IEI 0x08 (16-bit ref)
     * @param seq           1-based segment number (ignored for single-part)
     * @param total         total segments (ignored for single-part)
     * @param payload       binary body after UDH
     * @param otaHint       true when UDH carries IEI 0x70 or PID suggests SMS-PP
     * @return what was written when the assembly completes; empty otherwise
     */
    public Optional<Completed> offer(String subscriberKey, Integer concatRef, boolean ref16bit,
            int seq, int total, byte[] payload, boolean otaHint) {
        purgeExpired();
        if (payload == null || payload.length == 0) {
            return Optional.empty();
        }

        String sub = sanitize(subscriberKey == null || subscriberKey.isBlank() ? "unknown" : digits(subscriberKey));

        // Single-part: only persist when OTA-like (Command Packet IE / SMS-PP hint)
        if (concatRef == null) {
            if (!otaHint) {
                return Optional.empty();
            }
            return Optional.ofNullable(
                    writeComplete(sub, 0, false, 1, new byte[][] { payload }));
        }

        if (total < 1 || total > 255 || seq < 1 || seq > total) {
            LOG.warn("OTA CAP ignore bad concat subscriber={} ref={} seq={}/{}", sub, concatRef, seq, total);
            return Optional.empty();
        }

        String key = sub + "|r" + (ref16bit ? "16:" : "8:") + concatRef + "|t" + total;
        Assembly asm = assemblies.compute(key, (k, existing) -> {
            if (existing == null || existing.expired(clockMs.getAsLong(), timeoutMs)) {
                return new Assembly(sub, concatRef, ref16bit, total, clockMs.getAsLong());
            }
            if (existing.total != total) {
                LOG.warn("OTA CAP concat total mismatch key={} oldTotal={} newTotal={} — reset",
                        k, existing.total, total);
                return new Assembly(sub, concatRef, ref16bit, total, clockMs.getAsLong());
            }
            existing.touch(clockMs.getAsLong());
            return existing;
        });

        synchronized (asm) {
            if (asm.parts[seq - 1] != null) {
                // Retransmit: keep first copy unless empty
                if (asm.parts[seq - 1].length > 0) {
                    LOG.debug("OTA CAP duplicate seq {}/{} key={} — keep first", seq, total, key);
                } else {
                    asm.parts[seq - 1] = Arrays.copyOf(payload, payload.length);
                }
            } else {
                asm.parts[seq - 1] = Arrays.copyOf(payload, payload.length);
                asm.received++;
            }

            if (asm.received < asm.total) {
                LOG.debug("OTA CAP buffered {}/{} key={} partLen={}", asm.received, asm.total, key,
                        payload.length);
                return Optional.empty();
            }

            // Complete — remove before write so concurrent offers cannot double-write
            assemblies.remove(key, asm);
            return Optional.ofNullable(
                    writeComplete(asm.subscriber, asm.ref, asm.ref16bit, asm.total, asm.parts));
        }
    }

    public void clear() {
        assemblies.clear();
    }

    public void purgeExpired() {
        long now = clockMs.getAsLong();
        for (Map.Entry<String, Assembly> e : assemblies.entrySet()) {
            Assembly a = e.getValue();
            if (a != null && a.expired(now, timeoutMs)) {
                if (assemblies.remove(e.getKey(), a)) {
                    LOG.warn("OTA CAP assembly timed out key={} got={}/{} — discarded (no file written)",
                            e.getKey(), a.received, a.total);
                }
            }
        }
    }

    private Completed writeComplete(String subscriber, int ref, boolean ref16bit, int total,
            byte[][] parts) {
        int size = 0;
        for (byte[] p : parts) {
            if (p == null) {
                LOG.warn("OTA capture incomplete slot while writing subscriber={} — abort", subscriber);
                return null;
            }
            size += p.length;
        }
        byte[] merged = new byte[size];
        int off = 0;
        for (byte[] p : parts) {
            System.arraycopy(p, 0, merged, off, p.length);
            off += p.length;
        }

        String msisdn = lookupMsisdn(subscriber);
        String label = (msisdn != null && !msisdn.isBlank()) ? msisdn : ("imsi" + subscriber);
        String ts = TS.format(Instant.ofEpochMilli(clockMs.getAsLong()));
        String base = label + "_" + ts + "_ref" + ref + (ref16bit ? "x16" : "") + "_n" + total;

        Path packetPath;
        try {
            Files.createDirectories(outputDir);
            packetPath = writeAtomic(base + EXT_PACKET, merged);
        } catch (IOException ex) {
            LOG.error("OTA capture write failed subscriber={} size={}: {}", subscriber, size,
                    ex.toString(), ex);
            return null;
        }
        writtenCount.incrementAndGet();
        LOG.info("OTA secured packet written path={} size={} bytes (NOT the CAP — enciphered "
                        + "TS 102.225 packet) subscriber={}{} ref={} segments={}",
                packetPath.toAbsolutePath(), merged.length, subscriber,
                msisdn != null ? " msisdn=" + msisdn : "", ref, total);

        OtaSecuredPacketVerifier v = this.verifier;
        if (v == null) {
            return new Completed(packetPath, null, null, null);
        }

        OtaSecuredPacketVerifier.Result result = v.verify(merged);
        if (!result.ok()) {
            LOG.warn("OTA secured packet NOT unwound path={} — {}", packetPath.getFileName(),
                    result.summary());
            return new Completed(packetPath, null, result, null);
        }

        Path capPath;
        try {
            capPath = writeAtomic(base + EXT_CAP, result.cap());
        } catch (IOException ex) {
            LOG.error("OTA CAP write failed subscriber={}: {}", subscriber, ex.toString(), ex);
            return new Completed(packetPath, null, result, null);
        }
        verifiedCount.incrementAndGet();

        String match = matchAgainstReference(result.capSha256());
        LOG.info("OTA CAP recovered path={} {}{}", capPath.toAbsolutePath(), result.summary(),
                match != null ? " reference=" + match : "");
        return new Completed(packetPath, capPath, result, match);
    }

    private Path writeAtomic(String name, byte[] content) throws IOException {
        Path finalPath = outputDir.resolve(name);
        Path tmp = outputDir.resolve(name + ".tmp");
        Files.write(tmp, content);
        try {
            Files.move(tmp, finalPath, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException atomicFail) {
            Files.move(tmp, finalPath, StandardCopyOption.REPLACE_EXISTING);
        }
        return finalPath;
    }

    /**
     * Reports which pushed CAP the recovered one equals. Returns {@code null} when
     * no reference dir is configured, so the caller can stay quiet about it.
     *
     * <p>This runs on the MAP callback thread, before the simulator sends the
     * MT-ForwardSM response, and only on the final segment of an assembly. The
     * SHA-256 index is therefore built once and reused: re-reading the directory
     * per assembly would put unbounded disk IO in front of that last response, and
     * a slow response there makes the sender time out and re-send the final segment.
     */
    private String matchAgainstReference(String capSha256) {
        String dir = System.getProperty(PROP_REFERENCE_DIR);
        if (dir == null || dir.isBlank() || capSha256 == null) {
            return null;
        }
        Map<String, String> index = referenceIndex.get();
        if (index == null) {
            index = buildReferenceIndex(Path.of(dir.trim()));
            referenceIndex.set(index);
        }
        String name = index.get(capSha256.toLowerCase());
        if (name != null) {
            return "MATCH " + name;
        }
        return index.isEmpty()
                ? "NO-MATCH (no readable CAP in " + dir + ")"
                : "NO-MATCH (none of " + index.size() + " reference CAPs has this hash)";
    }

    /** sha256 → file name for every {@code *.cap} in {@code refDir}. Never throws. */
    private static Map<String, String> buildReferenceIndex(Path refDir) {
        Map<String, String> index = new HashMap<>();
        if (!Files.isDirectory(refDir)) {
            LOG.warn("OTA verify reference dir not found: {}", refDir);
            return index;
        }
        try (Stream<Path> files = Files.list(refDir)) {
            for (Path f : files.filter(Files::isRegularFile).toList()) {
                if (!f.getFileName().toString().endsWith(EXT_CAP)) {
                    continue;
                }
                try {
                    index.put(OtaSecuredPacketVerifier.sha256Hex(Files.readAllBytes(f)).toLowerCase(),
                            f.getFileName().toString());
                } catch (IOException perFile) {
                    LOG.warn("OTA verify cannot hash reference {}: {}", f.getFileName(), perFile.toString());
                }
            }
        } catch (IOException ex) {
            LOG.warn("OTA verify reference scan failed for {}: {}", refDir, ex.toString());
        }
        LOG.info("OTA verify reference index built from {} — {} CAP(s)", refDir, index.size());
        return index;
    }

    /**
     * What landed on disk for one completed assembly.
     *
     * @param packetPath   the merged secured packet, always present
     * @param capPath      the recovered CAP, or {@code null} when unwinding failed
     * @param verification verifier outcome, or {@code null} when verification is off
     * @param referenceMatch result of the hash comparison, or {@code null} when not configured
     */
    public record Completed(Path packetPath, Path capPath,
            OtaSecuredPacketVerifier.Result verification, String referenceMatch) {

        /** The most useful artefact: the CAP when we have it, else the raw packet. */
        public Path primaryPath() {
            return capPath != null ? capPath : packetPath;
        }

        /** One-line, key-free description for logs and the simulator GUI. */
        public String summary() {
            StringBuilder b = new StringBuilder(200);
            b.append("packet=").append(packetPath.getFileName());
            if (capPath != null) {
                b.append(" cap=").append(capPath.getFileName());
            }
            if (verification != null) {
                b.append(' ').append(verification.summary());
            }
            if (referenceMatch != null) {
                b.append(" reference=").append(referenceMatch);
            }
            return b.toString();
        }
    }

    private static String digits(String s) {
        StringBuilder b = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                b.append(c);
            }
        }
        return b.length() == 0 ? s.trim() : b.toString();
    }

    private static String sanitize(String s) {
        StringBuilder b = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '-' || c == '_') {
                b.append(c);
            } else {
                b.append('_');
            }
        }
        return b.length() == 0 ? "unknown" : b.toString();
    }

    private static final class Assembly {
        final String subscriber;
        final int ref;
        final boolean ref16bit;
        final int total;
        final byte[][] parts;
        int received;
        volatile long lastMs;

        Assembly(String subscriber, int ref, boolean ref16bit, int total, long nowMs) {
            this.subscriber = subscriber;
            this.ref = ref;
            this.ref16bit = ref16bit;
            this.total = total;
            this.parts = new byte[total][];
            this.received = 0;
            this.lastMs = nowMs;
        }

        void touch(long nowMs) {
            this.lastMs = nowMs;
        }

        boolean expired(long nowMs, long timeoutMs) {
            return nowMs - lastMs > timeoutMs;
        }
    }
}
