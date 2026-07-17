package org.restcomm.protocols.ss7.map.load;

import java.io.PrintStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.mobicents.protocols.asn.BerCursor;
import org.restcomm.protocols.ss7.map.MapBerSupport;

/**
 * Fixed-frame ANSI TUI for MAP load-test monitoring.
 * <p>
 * Stability rules (no scroll / no jump):
 * <ul>
 *   <li>Enter alternate screen buffer on start — load logs must not share this buffer.</li>
 *   <li>Redraw in-place with {@code CUP(1,1)} + erase-line; never {@code ED} full clear.</li>
 *   <li>Always emit exactly {@link #FRAME_LINES} lines (pad blanks) so height never changes.</li>
 * </ul>
 * CPU: no per-thread allocated-bytes scan; heap delta only; refresh every 2s by default.
 */
public final class ConsoleTui implements Runnable, AutoCloseable {

    private static final String CSI = "\u001b[";
    private static final String HOME = CSI + "H";
    private static final String EL = CSI + "K";           // erase to end of line
    private static final String ALT_ON = CSI + "?1049h";  // enter alternate screen
    private static final String ALT_OFF = CSI + "?1049l"; // leave alternate screen
    private static final String HIDE = CSI + "?25l";
    private static final String SHOW = CSI + "?25h";
    private static final String RESET = CSI + "0m";
    private static final String BOLD = CSI + "1m";
    private static final String DIM = CSI + "2m";
    private static final String GREEN = CSI + "32m";
    private static final String YELLOW = CSI + "33m";
    private static final String RED = CSI + "31m";
    private static final String CYAN = CSI + "36m";
    private static final String MAGENTA = CSI + "35m";

    private static final int WIDTH = 76;
    private static final int BAR_W = 24;
    /** Fixed frame height — must stay constant every refresh. */
    private static final int FRAME_LINES = 22;
    private static final int REFRESH_MS = Integer.getInteger("ss7.load.tuiRefreshMs", 2000);

    private static final int INVOKES_PER_DIALOG =
            Integer.getInteger("ss7.load.invokesPerDialog", 2);

    private static final MemoryMXBean MEMORY = ManagementFactory.getMemoryMXBean();
    private static final OperatingSystemMXBean OS = ManagementFactory.getOperatingSystemMXBean();
    private static final ThreadMXBean THREADS = ManagementFactory.getThreadMXBean();
    private static final List<GarbageCollectorMXBean> GCS = ManagementFactory.getGarbageCollectorMXBeans();
    private static final com.sun.management.OperatingSystemMXBean SUN_OS =
            (OS instanceof com.sun.management.OperatingSystemMXBean)
                    ? (com.sun.management.OperatingSystemMXBean) OS : null;

    public static ConsoleTui client(CsvWriter csvWriter, long targetTps, PrintStream out) {
        return new ConsoleTui("CLIENT", csvWriter, targetTps, out);
    }

    public static ConsoleTui server(CsvWriter csvWriter, PrintStream out) {
        return new ConsoleTui("SERVER", csvWriter, -1, out);
    }

    private final String role;
    private final long targetTps;
    private final AtomicLong created;
    private final AtomicLong success;
    private final AtomicLong error;
    private final PrintStream out;
    private final ScheduledExecutorService scheduler;
    private final long startNanos;
    private final int processors;

    private volatile boolean running;
    private boolean altScreen;

    private long lastSuccess;
    private long lastTickNanos;
    private long lastBerOk;
    private long lastBerFb;
    private long lastGcCount;
    private long lastGcTimeMs;
    private long lastHeapUsed;

    private double currentTps;
    private double peakTps;
    private double berOkRate;
    private double berFbRate;
    private double allocRateMb;
    private double processCpu;
    private double peakProcessCpu;

    public ConsoleTui(String role, AtomicLong created, AtomicLong success,
                      AtomicLong error, long targetTps, PrintStream out) {
        this.role = role;
        this.targetTps = targetTps;
        this.created = created;
        this.success = success;
        this.error = error;
        this.out = out;
        this.scheduler = newScheduler();
        this.startNanos = System.nanoTime();
        this.processors = Runtime.getRuntime().availableProcessors();
        this.lastTickNanos = this.startNanos;
        this.lastHeapUsed = MEMORY.getHeapMemoryUsage().getUsed();
        snapshotGc(true);
    }

    public ConsoleTui(String role, CsvWriter csvWriter, long targetTps, PrintStream out) {
        this.role = role;
        this.targetTps = targetTps;
        this.created = csvWriter.getCounter("CreatedScenario").atomic();
        this.success = csvWriter.getCounter("CompletedScenario").atomic();
        this.error = csvWriter.getCounter("FailedScenario").atomic();
        this.out = out;
        this.scheduler = newScheduler();
        this.startNanos = System.nanoTime();
        this.processors = Runtime.getRuntime().availableProcessors();
        this.lastTickNanos = this.startNanos;
        this.lastHeapUsed = MEMORY.getHeapMemoryUsage().getUsed();
        snapshotGc(true);
    }

    private static ScheduledExecutorService newScheduler() {
        return Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "tui-refresh");
            t.setDaemon(true);
            return t;
        });
    }

    public void start() {
        start(Math.min(1500, REFRESH_MS), REFRESH_MS);
    }

    public void start(int initialDelayMs, int periodMs) {
        // Alternate screen isolates the dashboard from Ant/log4j scrollback.
        out.print(ALT_ON + HIDE + HOME);
        out.flush();
        altScreen = true;
        running = true;
        scheduler.scheduleAtFixedRate(this, initialDelayMs, periodMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        running = false;
        scheduler.shutdown();
        try {
            scheduler.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        // Leave alt screen first so the final summary lands in the normal buffer.
        if (altScreen) {
            out.print(SHOW + ALT_OFF);
            out.flush();
            altScreen = false;
        } else {
            out.print(SHOW);
        }
        renderSummary();
        out.print('\n');
        out.flush();
    }

    @Override
    public void run() {
        if (!running) {
            return;
        }
        try {
            refresh();
        } catch (Throwable t) {
            // stay quiet — println would break the fixed frame
        }
    }

    // ── Layout ───────────────────────────────────────────────────────

    private static String pad(String s) {
        // Strip ANSI for length check is expensive; keep content short (< WIDTH).
        if (s.length() > WIDTH + 48) { // room for a few color codes
            return s.substring(0, WIDTH + 48);
        }
        return s;
    }

    private static String bar(double ratio, int width, String fillColor) {
        double r = Math.max(0.0, Math.min(1.0, ratio));
        int filled = (int) Math.round(r * width);
        return fillColor + "█".repeat(filled) + DIM + "░".repeat(width - filled) + RESET;
    }

    private static String colorByRatio(double ratio, double good, double warn) {
        if (ratio >= good) return GREEN;
        if (ratio >= warn) return YELLOW;
        return RED;
    }

    private static String colorByLoad(double pct) {
        if (pct < 70) return GREEN;
        if (pct < 90) return YELLOW;
        return RED;
    }

    private static String fmtBytes(long bytes) {
        if (bytes < 1024L * 1024) return String.format("%.0fK", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1fM", bytes / (1024.0 * 1024));
        return String.format("%.2fG", bytes / (1024.0 * 1024 * 1024));
    }

    private static String fmtRate(double v) {
        if (v >= 1_000_000) return String.format("%.2fM/s", v / 1_000_000);
        if (v >= 10_000) return String.format("%.1fK/s", v / 1000);
        return String.format("%,.0f/s", v);
    }

    private static String elapsed(long elapsedSec) {
        long m = elapsedSec / 60;
        long s = elapsedSec % 60;
        return String.format("%02d:%02d", m, s);
    }

    private void snapshotGc(boolean init) {
        long count = 0, timeMs = 0;
        for (GarbageCollectorMXBean gc : GCS) {
            long c = gc.getCollectionCount();
            long t = gc.getCollectionTime();
            if (c > 0) count += c;
            if (t > 0) timeMs += t;
        }
        if (init) {
            lastGcCount = count;
            lastGcTimeMs = timeMs;
        }
    }

    private double readProcessCpu() {
        if (SUN_OS == null) return -1;
        double v = SUN_OS.getProcessCpuLoad();
        return v < 0 ? -1 : v * 100.0;
    }

    /**
     * Paint one fixed frame: HOME, then exactly FRAME_LINES lines each ending with EL+NL.
     */
    private void paint(List<String> lines) {
        while (lines.size() < FRAME_LINES) {
            lines.add("");
        }
        if (lines.size() > FRAME_LINES) {
            lines = lines.subList(0, FRAME_LINES);
        }
        StringBuilder sb = new StringBuilder(FRAME_LINES * (WIDTH + 16));
        sb.append(HOME);
        for (int i = 0; i < FRAME_LINES; i++) {
            sb.append(pad(lines.get(i))).append(EL);
            if (i < FRAME_LINES - 1) {
                sb.append('\n');
            }
        }
        out.print(sb);
        out.flush();
    }

    private void refresh() {
        long now = System.nanoTime();
        long createdNow = created.get();
        long successNow = success.get();
        long errorNow = error.get();
        long elapsedSec = TimeUnit.NANOSECONDS.toSeconds(now - startNanos);

        long tickNanos = now - lastTickNanos;
        long berOkNow = MapBerSupport.berOkCount();
        long berFbNow = MapBerSupport.berFallbackCount();

        MemoryUsage heap = MEMORY.getHeapMemoryUsage();
        long heapUsed = heap.getUsed();
        long heapMax = heap.getMax() > 0 ? heap.getMax() : heap.getCommitted();

        long gcCount = 0, gcTime = 0;
        for (GarbageCollectorMXBean gc : GCS) {
            long c = gc.getCollectionCount();
            long t = gc.getCollectionTime();
            if (c > 0) gcCount += c;
            if (t > 0) gcTime += t;
        }
        long gcDelta = gcCount - lastGcCount;
        long gcTimeDelta = gcTime - lastGcTimeMs;

        if (tickNanos > 0) {
            double sec = tickNanos / 1e9;
            currentTps = (successNow - lastSuccess) * (double) INVOKES_PER_DIALOG / sec;
            if (currentTps > peakTps) peakTps = currentTps;
            berOkRate = (berOkNow - lastBerOk) / sec;
            berFbRate = (berFbNow - lastBerFb) / sec;
            // Cheap alloc estimate from heap growth (no thread scan).
            if (heapUsed >= lastHeapUsed) {
                allocRateMb = (heapUsed - lastHeapUsed) / (1024.0 * 1024.0) / sec;
            }
            processCpu = readProcessCpu();
            if (processCpu > peakProcessCpu) peakProcessCpu = processCpu;
        }

        lastSuccess = successNow;
        lastBerOk = berOkNow;
        lastBerFb = berFbNow;
        lastHeapUsed = heapUsed;
        lastTickNanos = now;
        lastGcCount = gcCount;
        lastGcTimeMs = gcTime;

        long totalDone = successNow + errorNow;
        double succRate = totalDone > 0 ? successNow * 100.0 / totalDone : 0;
        long pending = Math.max(0, createdNow - totalDone);
        long targetInvokes = targetTps > 0 ? targetTps * INVOKES_PER_DIALOG : 0;
        double tpsRatio = targetInvokes > 0 ? currentTps / targetInvokes : 0;
        double heapRatio = heapMax > 0 ? (double) heapUsed / heapMax : 0;
        long berTotal = berOkNow + berFbNow;
        double fbPct = berTotal > 0 ? berFbNow * 100.0 / berTotal : 0;

        String tpsColor = targetInvokes > 0 ? colorByRatio(tpsRatio, 0.90, 0.50) : GREEN;
        String rateColor = succRate >= 99.0 ? GREEN : (succRate >= 85.0 ? YELLOW : RED);
        String fbColor = berFbNow == 0 ? GREEN : (fbPct < 0.1 ? YELLOW : RED);
        String heapColor = colorByLoad(heapRatio * 100);
        String cpuColor = processCpu < 0 ? DIM : colorByLoad(processCpu);

        List<String> lines = new ArrayList<>(FRAME_LINES);

        // 0-1 header
        lines.add(CYAN + BOLD + " MAP USSD LOAD — " + role + RESET
                + DIM + "  uptime " + elapsed(elapsedSec)
                + "  cores " + processors
                + "  refresh " + (REFRESH_MS / 1000) + "s" + RESET);
        lines.add(DIM + "─".repeat(WIDTH) + RESET);

        // 2-4 throughput (always 3 lines — keep height fixed even without target)
        lines.add(BOLD + " THROUGHPUT" + RESET);
        if (targetInvokes > 0) {
            lines.add("  TPS " + tpsColor + BOLD + String.format("%,8.0f", currentTps) + RESET
                    + " inv/s  peak " + MAGENTA + String.format("%,.0f", peakTps) + RESET
                    + "  tgt " + DIM + String.format("%,d", targetInvokes) + RESET
                    + "  " + tpsColor + String.format("%5.1f%%", tpsRatio * 100) + RESET);
            lines.add("  " + bar(tpsRatio, BAR_W, tpsColor));
        } else {
            lines.add("  TPS " + tpsColor + BOLD + String.format("%,8.0f", currentTps) + RESET
                    + " inv/s  peak " + MAGENTA + String.format("%,.0f", peakTps) + RESET);
            lines.add("  " + bar(Math.min(1.0, currentTps / 8000.0), BAR_W, tpsColor));
        }

        // 5-7 dialogs
        lines.add(BOLD + " DIALOGS" + RESET);
        lines.add(String.format("  created %,12d  success ", createdNow)
                + GREEN + String.format("%,12d", successNow) + RESET
                + "  error " + (errorNow > 0 ? RED : DIM) + String.format("%,10d", errorNow) + RESET);
        lines.add("  rate  " + rateColor + BOLD + String.format("%6.2f%%", succRate) + RESET
                + "  " + bar(succRate / 100.0, BAR_W, rateColor)
                + "  pend " + YELLOW + String.format("%,d", pending) + RESET);

        // 8-10 ASN
        lines.add(BOLD + " ASN.1" + RESET);
        lines.add("  Ber OK  " + GREEN + String.format("%,12d", berOkNow) + RESET
                + "  " + GREEN + fmtRate(berOkRate) + RESET
                + DIM + "  zero-copy" + RESET);
        lines.add("  Ber FB  " + fbColor + String.format("%,12d", berFbNow) + RESET
                + "  " + fbColor + fmtRate(berFbRate) + RESET
                + DIM + String.format("  %.3f%% → legacy", fbPct) + RESET);

        // 11-13 memory
        lines.add(BOLD + " MEMORY" + RESET);
        lines.add("  Heap " + heapColor + fmtBytes(heapUsed) + "/" + fmtBytes(heapMax) + RESET
                + "  " + bar(heapRatio, BAR_W, heapColor)
                + String.format(" %5.1f%%", heapRatio * 100)
                + "  Δ " + MAGENTA + String.format("%.1f MB/s", allocRateMb) + RESET);
        lines.add("  GC   " + (gcDelta > 0 ? YELLOW : DIM)
                + String.format("+%,d  +%,d ms", gcDelta, gcTimeDelta) + RESET
                + DIM + String.format("  Σ %,d / %,d ms", gcCount, gcTime) + RESET);

        // 14-16 CPU
        lines.add(BOLD + " CPU / THREADS" + RESET);
        if (processCpu >= 0) {
            lines.add("  Proc " + cpuColor + BOLD + String.format("%5.1f%%", processCpu) + RESET
                    + "  " + bar(processCpu / 100.0, BAR_W, cpuColor)
                    + DIM + String.format("  peak %4.1f%%", peakProcessCpu) + RESET);
        } else {
            lines.add("  Proc " + DIM + "n/a" + RESET);
        }
        double load = OS.getSystemLoadAverage();
        lines.add(String.format("  Thr  live %,d  daemon %,d  peak %,d",
                THREADS.getThreadCount(), THREADS.getDaemonThreadCount(), THREADS.getPeakThreadCount())
                + (load >= 0 ? DIM + String.format("  loadavg %.2f", load) + RESET : ""));

        // 17-21 footer / padding
        lines.add(DIM + "─".repeat(WIDTH) + RESET);
        lines.add(DIM + " logs → client.log / server.log  |  TUI alt-screen (no scroll)" + RESET);
        // remaining lines padded by paint()

        paint(lines);
    }

    private void renderSummary() {
        long now = System.nanoTime();
        long elapsedSec = Math.max(1, TimeUnit.NANOSECONDS.toSeconds(now - startNanos));
        long total = success.get() + error.get();
        long dps = success.get() / elapsedSec;
        MemoryUsage heap = MEMORY.getHeapMemoryUsage();

        out.println();
        out.println(CYAN + BOLD + "=== MAP USSD LOAD — " + role + " — FINAL ===" + RESET);
        out.printf("  Duration     %s%n", elapsed(elapsedSec));
        out.printf("  Created      %,d%n", created.get());
        out.printf("  Success      %,d%n", success.get());
        out.printf("  Error        %,d%n", error.get());
        if (total > 0) {
            out.printf("  Success %%    %.2f%%%n", success.get() * 100.0 / total);
        }
        out.printf("  Peak TPS     %,.0f inv/s%n", peakTps);
        out.printf("  Avg TPS      %,d inv/s%n", dps * INVOKES_PER_DIALOG);
        out.printf("  BerCursor OK %,d  FB %,d%n",
                MapBerSupport.berOkCount(), MapBerSupport.berFallbackCount());
        out.printf("  Peak CPU     %.1f%%%n", peakProcessCpu);
        out.printf("  Heap end     %s / %s%n",
                fmtBytes(heap.getUsed()),
                fmtBytes(heap.getMax() > 0 ? heap.getMax() : heap.getCommitted()));
        out.flush();
    }
}
