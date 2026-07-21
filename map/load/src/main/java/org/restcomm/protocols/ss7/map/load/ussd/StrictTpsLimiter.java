package org.restcomm.protocols.ss7.map.load.ussd;

/**
 * Zero-burst TPS limiter for MAP load tests.
 * <p>
 * Guava {@code RateLimiter} (SmoothBursty) keeps a full 1s permit bucket and
 * refills during idle (e.g. SCTP/M3UA stack init). That stampede overwhelms
 * the USSD gateway under concurrent&gt;1. This limiter schedules the next permit
 * strictly at {@code 1/rate} intervals with no catch-up burst.
 */
public final class StrictTpsLimiter {

    private double ratePerSecond;
    private long nextPermitNanos;

    public StrictTpsLimiter(double ratePerSecond) {
        setRate(ratePerSecond);
    }

    public synchronized void setRate(double ratePerSecond) {
        this.ratePerSecond = Math.max(0.001, ratePerSecond);
        // Do not reset nextPermitNanos — changing rate mid-test must not grant a burst.
    }

    public synchronized double getRate() {
        return this.ratePerSecond;
    }

    /**
     * Reserves the next ticket immediately, then blocks until that ticket is due.
     * Concurrent callers therefore get spaced tickets (T, T+I, T+2I, …) with no burst.
     *
     * @return planned wait in milliseconds before this permit (0 if immediate)
     */
    public synchronized long acquire() throws InterruptedException {
        long now = System.nanoTime();
        if (this.nextPermitNanos == 0L) {
            this.nextPermitNanos = now;
        }
        long ticket = this.nextPermitNanos;
        long intervalNanos = (long) (1_000_000_000.0d / this.ratePerSecond);
        if (intervalNanos < 1L) {
            intervalNanos = 1L;
        }
        this.nextPermitNanos = ticket + intervalNanos;

        long waitNanos = ticket - now;
        if (waitNanos <= 0L) {
            return 0L;
        }
        long plannedMs = waitNanos / 1_000_000L;
        while (true) {
            long remaining = ticket - System.nanoTime();
            if (remaining <= 0L) {
                break;
            }
            long millis = remaining / 1_000_000L;
            int nanos = (int) (remaining % 1_000_000L);
            wait(millis, nanos);
        }
        return plannedMs;
    }
}
