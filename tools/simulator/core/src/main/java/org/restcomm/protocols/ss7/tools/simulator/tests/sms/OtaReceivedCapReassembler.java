package org.restcomm.protocols.ss7.tools.simulator.tests.sms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Lab helper: reassemble concatenated SMS-PP (OTA install) payloads received on
 * MAP MT-ForwardSM and write the complete binary to disk under
 * {@code <persistDir>/received-caps/}.
 *
 * <p>What is written is the merged SMS-PP <em>secured packet</em> body (UDH
 * stripped, segments concatenated in order) — not a decrypted GlobalPlatform
 * CAP. Extension {@code .cap} is used for lab convenience.
 *
 * <p>Incomplete / timed-out assemblies are discarded; files are written
 * atomically ({@code .tmp} → rename) so partial content never lands as the
 * final name.
 */
public final class OtaReceivedCapReassembler {

    public static final String SUBDIR = "received-caps";
    /** Default drop incomplete assemblies after 5 minutes. */
    public static final long DEFAULT_TIMEOUT_MS = 5L * 60L * 1000L;

    /** TS 31.115 Command Packet Identifier IEI. */
    public static final int IEI_COMMAND_PACKET = 0x70;

    private static final Logger LOG = LogManager.getLogger(OtaReceivedCapReassembler.class);
    private static final DateTimeFormatter TS =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS").withZone(ZoneOffset.UTC);

    private final Path outputDir;
    private final long timeoutMs;
    private final LongSupplier clockMs;
    private final ConcurrentHashMap<String, Assembly> assemblies = new ConcurrentHashMap<>();
    /** IMSI → last MSISDN seen on SRI (lab naming). */
    private final ConcurrentHashMap<String, String> imsiToMsisdn = new ConcurrentHashMap<>();
    private final AtomicLong writtenCount = new AtomicLong();

    public OtaReceivedCapReassembler(Path persistDir) {
        this(persistDir, DEFAULT_TIMEOUT_MS, System::currentTimeMillis);
    }

    public OtaReceivedCapReassembler(Path persistDir, long timeoutMs, LongSupplier clockMs) {
        Objects.requireNonNull(persistDir, "persistDir");
        Objects.requireNonNull(clockMs, "clockMs");
        this.outputDir = persistDir.resolve(SUBDIR);
        this.timeoutMs = timeoutMs <= 0 ? DEFAULT_TIMEOUT_MS : timeoutMs;
        this.clockMs = clockMs;
    }

    public Path getOutputDir() {
        return outputDir;
    }

    public long getWrittenCount() {
        return writtenCount.get();
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
     * @return path of written file when assembly completes; empty otherwise
     */
    public Optional<Path> offer(String subscriberKey, Integer concatRef, boolean ref16bit,
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
            return Optional.ofNullable(writeComplete(sub, 0, false, 1, new byte[][] { payload }));
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
            Path written = writeComplete(asm.subscriber, asm.ref, asm.ref16bit, asm.total, asm.parts);
            return Optional.ofNullable(written);
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

    private Path writeComplete(String subscriber, int ref, boolean ref16bit, int total, byte[][] parts) {
        int size = 0;
        for (byte[] p : parts) {
            if (p == null) {
                LOG.warn("OTA CAP incomplete slot while writing subscriber={} — abort", subscriber);
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

        try {
            Files.createDirectories(outputDir);
            String msisdn = lookupMsisdn(subscriber);
            String label = (msisdn != null && !msisdn.isBlank()) ? msisdn : ("imsi" + subscriber);
            String ts = TS.format(Instant.ofEpochMilli(clockMs.getAsLong()));
            String name = label + "_" + ts + "_ref" + ref + (ref16bit ? "x16" : "")
                    + "_n" + total + ".cap";
            Path finalPath = outputDir.resolve(name);
            Path tmp = outputDir.resolve(name + ".tmp");
            Files.write(tmp, merged);
            try {
                Files.move(tmp, finalPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicFail) {
                Files.move(tmp, finalPath, StandardCopyOption.REPLACE_EXISTING);
            }
            writtenCount.incrementAndGet();
            String msg = "OTA CAP written path=" + finalPath.toAbsolutePath()
                    + " size=" + merged.length + " bytes subscriber=" + subscriber
                    + (msisdn != null ? " msisdn=" + msisdn : "")
                    + " ref=" + ref + " segments=" + total;
            LOG.info(msg);
            return finalPath;
        } catch (IOException ex) {
            LOG.error("OTA CAP write failed subscriber={} size={}: {}", subscriber, size, ex.toString(), ex);
            return null;
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
