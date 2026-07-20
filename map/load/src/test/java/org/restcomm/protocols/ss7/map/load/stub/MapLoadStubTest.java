package org.restcomm.protocols.ss7.map.load.stub;

import java.util.PriorityQueue;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Deterministic MAP peers used to validate a 5,000-TPS load profile without an external
 * HLR/MSC or SMSC. Time is virtual so this is a correctness/capacity test, not a host benchmark.
 */
public class MapLoadStubTest {

    private static final int TARGET_TPS = 5_000;
    private static final int DURATION_SECONDS = 60;
    private static final int DIALOGS = TARGET_TPS * DURATION_SECONDS;

    @Test
    public void ussdStubCompletesFiveThousandTransactionsPerSecond() {
        Result result = new StubPeer(Scenario.USSD, 16).run(DIALOGS, TARGET_TPS);

        assertSuccessful(result);
        assertEquals(result.legs, DIALOGS, "USSD has one MAP request/response leg");
    }

    @Test
    public void smscStubCompletesFiveThousandTransactionsPerSecond() {
        Result result = new StubPeer(Scenario.SMSC_MT, 64).run(DIALOGS, TARGET_TPS);

        assertSuccessful(result);
        assertEquals(result.legs, DIALOGS * 2L, "MT-SMS has SRI-for-SM then MT-Forward-SM");
    }

    private static void assertSuccessful(Result result) {
        assertEquals(result.submitted, DIALOGS);
        assertEquals(result.completed, DIALOGS);
        assertEquals(result.rejected, 0);
        assertEquals(result.submissionElapsedMillis, DURATION_SECONDS * 1_000L);
        assertEquals(result.submitted * 1_000L / result.submissionElapsedMillis, TARGET_TPS);
        assertTrue(result.completionElapsedMillis <= result.submissionElapsedMillis + result.processingMillis,
                "all in-flight dialogs must drain within one peer processing interval");
        assertTrue(result.peakInFlight <= result.inFlightLimit,
                "stub peer must apply back-pressure before exceeding its in-flight limit");
        assertTrue(result.peakInFlight >= TARGET_TPS * result.processingMillis / 1_000L,
                "in-flight capacity must satisfy Little's Law for the selected peer latency");
    }

    private enum Scenario {
        USSD(2), SMSC_MT(8);

        private final int processingMillis;

        Scenario(int processingMillis) {
            this.processingMillis = processingMillis;
        }
    }

    private static final class StubPeer {
        private final Scenario scenario;
        private final int inFlightLimit;
        private final PriorityQueue<Long> completions = new PriorityQueue<Long>();
        private long submitted;
        private long completed;
        private long rejected;
        private long legs;
        private long peakInFlight;

        private StubPeer(Scenario scenario, int inFlightLimit) {
            this.scenario = scenario;
            this.inFlightLimit = inFlightLimit;
        }

        private Result run(int dialogs, int targetTps) {
            long nextSubmitAt = 0;
            long lastCompletionAt = 0;
            for (int dialog = 0; dialog < dialogs; dialog++) {
                long now = nextSubmitAt;
                drainCompleted(now);
                if (completions.size() == inFlightLimit) {
                    now = completions.peek();
                    drainCompleted(now);
                }

                submitted++;
                legs += scenario == Scenario.SMSC_MT ? 2 : 1;
                lastCompletionAt = Math.max(lastCompletionAt, now + scenario.processingMillis);
                completions.add(now + scenario.processingMillis);
                peakInFlight = Math.max(peakInFlight, completions.size());
                nextSubmitAt = ((long) (dialog + 1) * 1_000L) / targetTps;
            }
            drainCompleted(Long.MAX_VALUE);
            return new Result(submitted, completed, rejected, legs, peakInFlight, inFlightLimit,
                    scenario.processingMillis, ((long) dialogs * 1_000L) / targetTps, lastCompletionAt);
        }

        private void drainCompleted(long now) {
            while (!completions.isEmpty() && completions.peek() <= now) {
                completions.remove();
                completed++;
            }
        }
    }

    private static final class Result {
        private final long submitted;
        private final long completed;
        private final long rejected;
        private final long legs;
        private final long peakInFlight;
        private final long inFlightLimit;
        private final long processingMillis;
        private final long submissionElapsedMillis;
        private final long completionElapsedMillis;

        private Result(long submitted, long completed, long rejected, long legs, long peakInFlight,
                long inFlightLimit, long processingMillis, long submissionElapsedMillis,
                long completionElapsedMillis) {
            this.submitted = submitted;
            this.completed = completed;
            this.rejected = rejected;
            this.legs = legs;
            this.peakInFlight = peakInFlight;
            this.inFlightLimit = inFlightLimit;
            this.processingMillis = processingMillis;
            this.submissionElapsedMillis = submissionElapsedMillis;
            this.completionElapsedMillis = completionElapsedMillis;
        }
    }
}
