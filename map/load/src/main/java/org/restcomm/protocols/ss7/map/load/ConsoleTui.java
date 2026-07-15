package org.restcomm.protocols.ss7.map.load;

import java.io.PrintStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.mobicents.protocols.asn.BerCursor;

/**
 * ANSI escape-based real-time TUI for load test monitoring.
 * Zero external dependencies — works on any modern terminal.
 *
 * <pre>{@code
 * ConsoleTui tui = ConsoleTui.client(csvWriter, targetTps, System.out);
 * tui.start();
 * // ... test runs ...
 * tui.close();
 * }</pre>
 */
public final class ConsoleTui implements Runnable, AutoCloseable {

    // ── ANSI codes for terminal control ──────────────────────────────
    private static final String CSI    = "\u001b[";
    private static final String CLEAR  = CSI + "2J";
    private static final String HOME   = CSI + "H";
    private static final String HIDE   = CSI + "?25l";
    private static final String SHOW   = CSI + "?25h";
    private static final String RESET  = CSI + "0m";
    private static final String BOLD   = CSI + "1m";
    private static final String DIM    = CSI + "2m";
    private static final String GREEN  = CSI + "32m";
    private static final String YELLOW = CSI + "33m";
    private static final String RED    = CSI + "31m";
    private static final String CYAN   = CSI + "36m";
    private static final String MAGENTA= CSI + "35m";

    // ── Factory ──────────────────────────────────────────────────────
    public static ConsoleTui client(CsvWriter csvWriter, long targetTps, PrintStream out) {
        return new ConsoleTui("CLIENT", csvWriter, targetTps, out);
    }

    public static ConsoleTui server(CsvWriter csvWriter, PrintStream out) {
        return new ConsoleTui("SERVER", csvWriter, -1, out);
    }

    // ── Fields ───────────────────────────────────────────────────────
    private final String role;
    private final long targetTps;
    private final CsvWriter csvWriter;
    private final AtomicLong created;
    private final AtomicLong success;
    private final AtomicLong error;
    private final PrintStream out;
    private final ScheduledExecutorService scheduler;
    private final long startNanos;

    private volatile boolean running;
    private long lastSuccess;
    private long lastTickNanos;
    private double currentTps;
    private double peakTps;
    // ASN.1 codec rate tracking (deltas of cumulative decode/encode op counters)
    private long lastDecode;
    private long lastEncode;
    private double decodeRate;
    private double encodeRate;

    /**
     * MAP operations (Invokes) per USSD dialog. One dialog carries two Invokes on the wire —
     * processUnstructuredSS-Request (client→server) and unstructuredSS-Request (server→client) —
     * so TPS is reported in operations/s to match the Invoke rate seen in Wireshark
     * (400 dialogs/s == 800 invokes/s). Set to 1 to report dialogs/s instead.
     */
    private static final int INVOKES_PER_DIALOG =
            Integer.getInteger("ss7.load.invokesPerDialog", 2);

    /** Create with explicit AtomicLong counters (legacy compat). */
    public ConsoleTui(String role, AtomicLong created, AtomicLong success,
                      AtomicLong error, long targetTps, PrintStream out) {
        this.role = role;
        this.targetTps = targetTps;
        this.csvWriter = null;
        this.created = created;
        this.success = success;
        this.error = error;
        this.out = out;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "tui-refresh");
            t.setDaemon(true);
            return t;
        });
        this.startNanos = System.nanoTime();
    }

    /** Create from CsvWriter — looks up counters by name. */
    public ConsoleTui(String role, CsvWriter csvWriter, long targetTps, PrintStream out) {
        this.role = role;
        this.targetTps = targetTps;
        this.csvWriter = csvWriter;
        this.created = csvWriter.getCounter("CreatedScenario").atomic();
        this.success = csvWriter.getCounter("CompletedScenario").atomic();
        this.error   = csvWriter.getCounter("FailedScenario").atomic();
        this.out = out;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "tui-refresh");
            t.setDaemon(true);
            return t;
        });
        this.startNanos = System.nanoTime();
    }

    /** Start with default 1000ms refresh. */
    public void start() {
        start(2000, 1000);
    }

    public void start(int initialDelayMs, int periodMs) {
        out.print(HIDE);
        running = true;
        scheduler.scheduleAtFixedRate(this, initialDelayMs, periodMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        running = false;
        scheduler.shutdown();
        try { scheduler.awaitTermination(1, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
        // Final summary before cursor restore
        renderSummary();
        out.print(SHOW + "\n");
    }

    // ── Box-drawing helpers (dynamic padding so borders always align) ──
    private static final int INNER = 62;

    /** Horizontal rule of the given corner chars, INNER columns wide. */
    private static String rule(char left, char right) {
        return left + "═".repeat(INNER) + right;
    }

    /** A boxed row. {@code text} must be plain (no ANSI, no double-width glyphs) so the
     *  right border stays aligned; it is left-padded 1 space and right-padded to INNER. */
    private static String row(String text) {
        String body = " " + text;
        if (body.length() > INNER) body = body.substring(0, INNER - 1) + "…";
        return "║" + body + " ".repeat(INNER - body.length()) + "║";
    }

    private void renderSummary() {
        long now = System.nanoTime();
        long elapsedSec = TimeUnit.NANOSECONDS.toSeconds(now - startNanos);
        long total = success.get() + error.get();
        // Avg throughput = COMPLETED dialogs (Success)/s, matching the live TPS metric.
        long dps   = elapsedSec > 0 ? success.get() / elapsedSec : 0;

        out.print(CLEAR + HOME);
        out.println(CYAN + BOLD + rule('╔', '╗'));
        out.println(row("MAP USSD Load Test — " + role + " — FINAL SUMMARY"));
        out.println(rule('╠', '╣'));
        out.println(row(String.format("Duration   : %dm %02ds", elapsedSec / 60, elapsedSec % 60)));
        out.println(row(String.format("Created    : %,d dialogs", created.get())));
        out.println(row(String.format("Success    : %,d", success.get())));
        out.println(row(String.format("Error      : %,d", error.get())));
        if (total > 0)
            out.println(row(String.format("Success %%   : %.2f%%", success.get() * 100.0 / total)));
        out.println(row(String.format("Peak TPS   : %,d inv/s", (long) peakTps)));
        if (elapsedSec > 0)
            out.println(row(String.format("Avg TPS    : %,d inv/s  (%,d dialogs/s)",
                    dps * INVOKES_PER_DIALOG, dps)));
        out.println(rule('╚', '╝') + RESET);
    }

    @Override
    public void run() {
        if (!running) return;
        refresh();
    }

    private void refresh() {
        long now = System.nanoTime();
        long createdNow = created.get();
        long successNow = success.get();
        long errorNow   = error.get();

        long elapsedSec   = TimeUnit.NANOSECONDS.toSeconds(now - startNanos);
        long elapsedMin   = elapsedSec / 60;
        long elapsedRem   = elapsedSec % 60;
        String elapsedStr = String.format("%02d:%02d", elapsedMin, elapsedRem);

        // TPS delta — real throughput: COMPLETED dialogs (Success)/s × INVOKES_PER_DIALOG,
        // not sends (Created). A completed dialog == 2 invokes on the wire, same as a sent one.
        long tickNanos = now - lastTickNanos;
        long decodeNow = BerCursor.decodeCount();
        long encodeNow = BerCursor.encodeCount();
        if (tickNanos > 0) {
            currentTps = (successNow - lastSuccess) * (double) INVOKES_PER_DIALOG * 1e9 / tickNanos;
            if (currentTps > peakTps) peakTps = currentTps;
            decodeRate = (decodeNow - lastDecode) * 1e9 / tickNanos;
            encodeRate = (encodeNow - lastEncode) * 1e9 / tickNanos;
        }
        lastSuccess   = successNow;
        lastDecode    = decodeNow;
        lastEncode    = encodeNow;
        lastTickNanos = now;

        // Heap
        Runtime rt = Runtime.getRuntime();
        long heapUsed = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        long heapMax  = rt.maxMemory() / (1024 * 1024);

        StringBuilder sb = new StringBuilder(1024);
        sb.append(CLEAR).append(HOME);

        // ═══ Title bar ═══
        sb.append(CYAN).append(BOLD)
          .append(rule('╔', '╗')).append('\n')
          .append(row("MAP USSD Load Test — " + role)).append('\n')
          .append(rule('╚', '╝')).append(RESET).append("\n\n");

        // ═══ Time & TPS ═══
        sb.append(BOLD).append(" ⏱ ").append(elapsedStr).append(RESET);
        sb.append("    │    ");

        // TPS reported in MAP operations (Invokes)/s; target (a dialog rate limit) scaled to match.
        long targetInvokes = targetTps * INVOKES_PER_DIALOG;
        String tpsColor = targetInvokes > 0
            ? (currentTps >= targetInvokes * 0.9 ? GREEN : (currentTps >= targetInvokes * 0.5 ? YELLOW : RED))
            : GREEN;
        sb.append(tpsColor).append(BOLD)
          .append("TPS: ").append(String.format("%,6.0f", currentTps)).append(" inv/s")
          .append(RESET);
        sb.append("    │    ");
        sb.append(MAGENTA)
          .append("Peak: ").append(String.format("%,6d", (long) peakTps))
          .append(RESET);

        if (targetInvokes > 0) {
            double pct = currentTps * 100.0 / targetInvokes;
            sb.append("    │    ").append(DIM)
              .append("Target: ").append(String.format("%,d", targetInvokes))
              .append(" (").append(String.format("%.0f", pct)).append("%)")
              .append(RESET);
        }
        sb.append("\n\n");

        // ═══ Dialogs ═══
        long totalDone  = successNow + errorNow;
        double succRate = totalDone > 0 ? successNow * 100.0 / totalDone : 0;
        String rateColor = succRate >= 99.9 ? GREEN : (succRate >= 99.0 ? YELLOW : RED);

        sb.append(BOLD).append(" 📊 Dialogs").append(RESET).append("\n");
        sb.append("    Created: ").append(String.format("%,9d", createdNow))
          .append("    Success: ").append(GREEN).append(String.format("%,9d", successNow)).append(RESET)
          .append("    Error: ").append(errorNow > 0 ? RED : DIM).append(String.format("%,9d", errorNow)).append(RESET)
          .append("\n");
        sb.append("    Rate:   ").append(rateColor).append(String.format("%10.1f%%", succRate)).append(RESET);
        if (totalDone > 0 && createdNow > totalDone) {
            sb.append("    Pending: ").append(String.format("%,9d", createdNow - totalDone));
        }
        sb.append("\n\n");

        // ═══ ASN.1 Codec — decode/encode ops per second (cumulative Σ in parens) ═══
        sb.append(BOLD).append(" 🔬 ASN.1 Codec").append(RESET).append("\n");
        if (BerCursor.isTelemetryEnabled()) {
            sb.append("    Decode: ").append(GREEN).append(String.format("%,8.0f/s", decodeRate)).append(RESET)
              .append(DIM).append(String.format("  (Σ %,d)", decodeNow)).append(RESET)
              .append("    │    Encode: ").append(GREEN).append(String.format("%,8.0f/s", encodeRate)).append(RESET)
              .append(DIM).append(String.format("  (Σ %,d)", encodeNow)).append(RESET);
        } else {
            sb.append("    ").append(DIM).append("🔒 Telemetry off — add -Dasn.telemetry.enabled=true").append(RESET);
        }
        sb.append("\n\n");

        // ═══ System ═══
        sb.append(BOLD).append(" 💻 System").append(RESET).append("\n");
        sb.append("    Heap: ").append(String.format("%dM/%,dM", heapUsed, heapMax));
        sb.append("    Threads: ").append(Thread.activeCount());
        sb.append("    Uptime: ").append(elapsedStr);
        sb.append("\n");

        out.print(sb.toString());
    }
}
