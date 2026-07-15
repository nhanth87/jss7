package org.mobicents.protocols.asn;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * CHOICE encode/decode benchmark — tests BerChoice descriptor, ChoiceDecoder,
 * and BerWriter/BerCursor zero-copy CHOICE pattern (no protocol-specific imports).
 *
 * <p>This test demonstrates the general CHOICE pattern that all protocol-specific
 * CHOICE codecs (OperationCodeCodec, ErrorCodeCodec, etc.) follow.
 */
public class BerChoiceBenchmark {

    // ── BerChoice descriptor tests ───────────────────────────────────

    @Test
    public void testBerChoiceContextPrimitive() {
        BerChoice ch = BerChoice.contextPrimitive(0, "localValue");
        Assert.assertEquals(ch.tagClass, BerTag.CONTEXT);
        Assert.assertTrue(ch.primitive);
        Assert.assertEquals(ch.tag, 0);
        Assert.assertEquals(ch.name, "localValue");
    }

    @Test
    public void testBerChoiceContextConstructed() {
        BerChoice ch = BerChoice.contextConstructed(2, "routeSelectFailure");
        Assert.assertEquals(ch.tagClass, BerTag.CONTEXT);
        Assert.assertFalse(ch.primitive);
        Assert.assertEquals(ch.tag, 2);
    }

    @Test
    public void testBerChoiceUniversalPrimitive() {
        BerChoice ch = BerChoice.universalPrimitive(BerTag.INTEGER, "intValue");
        Assert.assertEquals(ch.tagClass, BerTag.UNIVERSAL);
        Assert.assertTrue(ch.primitive);
    }

    @Test
    public void testBerChoiceApplicationConstructed() {
        BerChoice ch = BerChoice.applicationConstructed(1, "appChoice");
        Assert.assertEquals(ch.tagClass, BerTag.APPLICATION);
        Assert.assertFalse(ch.primitive);
        Assert.assertEquals(ch.tag, 1);
    }

    @Test
    public void testBerChoiceMatches() throws AsnException {
        // Encode a [0] IMPLICIT INTEGER = 42
        BerWriter w = BerWriter.get();
        w.writeInt32(BerTag.CONTEXT, 0, 42);
        BerCursor c = w.resultAsCursor();
        c.readTag();

        BerChoice choice0 = BerChoice.contextPrimitive(0, "alt0");
        BerChoice choice1 = BerChoice.contextPrimitive(1, "alt1");

        Assert.assertTrue(choice0.matches(c));
        Assert.assertFalse(choice1.matches(c));
        c.release();
    }

    // ── Generic CHOICE pattern: encode/decode via BerWriter/BerCursor ─

    @Test
    public void testChoiceEncodeDecodeContextPrimitive() throws AsnException {
        // Simulate: OperationCode CHOICE { localValue [0] IMPLICIT INTEGER }
        BerWriter w = BerWriter.get();
        w.writeInt32(BerTag.CONTEXT, 0, 99);  // [0] IMPLICIT INTEGER = 99
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrapHeap(encoded, 0, encoded.length);
        c.readTag();
        Assert.assertEquals(c.tagClass(), BerTag.CONTEXT);
        Assert.assertEquals(c.tag(), 0);
        int val = c.readInt32();
        c.release();

        Assert.assertEquals(val, 99);
    }

    @Test
    public void testChoiceEncodeDecodeOctetString() throws AsnException {
        // Simulate: CHOICE { bearerCap [0] IMPLICIT OCTET STRING }
        byte[] data = new byte[]{0x01, 0x02, 0x03};

        BerWriter w = BerWriter.get();
        w.writeOctetString(BerTag.CONTEXT, 0, data, 0, data.length);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrapHeap(encoded, 0, encoded.length);
        c.readTag();
        Assert.assertEquals(c.tagClass(), BerTag.CONTEXT);
        Assert.assertEquals(c.tag(), 0);
        BerSlice slice = c.getOctetStringSlice();
        c.release();

        Assert.assertEquals(slice.length(), 3);
        Assert.assertTrue(slice.equalsBytes(data));
    }

    @Test
    public void testChoiceEncodeDecodeNull() throws AsnException {
        // Simulate: CHOICE { noDA [5] IMPLICIT NULL }
        BerWriter w = BerWriter.get();
        w.writeNull(BerTag.CONTEXT, 5);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrapHeap(encoded, 0, encoded.length);
        c.readTag();
        Assert.assertEquals(c.tagClass(), BerTag.CONTEXT);
        Assert.assertEquals(c.tag(), 5);
        Assert.assertEquals(c.valueLength(), 0);
        c.release();
    }

    @Test
    public void testChoiceEncodeDecodeConstructed() throws AsnException {
        // Simulate: CHOICE { event [2] IMPLICIT SEQUENCE { cause [0] OCTET STRING } }
        BerWriter w = BerWriter.get();
        int mark = w.beginSequence(BerTag.CONTEXT, 2);
        w.writeOctetString(BerTag.CONTEXT, 0, new byte[]{'C', 'A', 'U', 'S', 'E'}, 0, 5);
        w.endSequence(mark, BerTag.CONTEXT, 2);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrapHeap(encoded, 0, encoded.length);
        c.readTag();
        Assert.assertEquals(c.tagClass(), BerTag.CONTEXT);
        Assert.assertEquals(c.tag(), 2);
        Assert.assertFalse(c.isPrimitive());

        // Open constructed content
        BerCursor sub = c.openConstructed();
        sub.readTag();
        Assert.assertEquals(sub.tag(), 0);
        Assert.assertEquals(sub.tagClass(), BerTag.CONTEXT);
        BerSlice causeSlice = sub.getOctetStringSlice();
        Assert.assertEquals(causeSlice.length(), 5);
        sub.release();
        c.release();
    }

    // ── Multi-choice dispatch pattern ─────────────────────────────────

    @Test
    public void testChoicedispatch() throws AsnException {
        // Dispatch table pattern (what protocol codecs use internally)
        BerChoice[] choices = {
            BerChoice.contextPrimitive(0, "localValue"),
            BerChoice.contextPrimitive(1, "globalValue"),
        };

        // Encode as [1] (globalValue path)
        BerWriter w = BerWriter.get();
        w.writeOctetString(BerTag.CONTEXT, 1, new byte[]{0x55, 0x01, 0x01}, 0, 3);
        BerCursor c = w.resultAsCursor();
        c.readTag();

        int matchedIndex = -1;
        for (int i = 0; i < choices.length; i++) {
            if (choices[i].matches(c)) {
                matchedIndex = i;
                break;
            }
        }
        Assert.assertEquals(matchedIndex, 1, "should match globalValue (tag=1)");

        BerSlice oidSlice = c.getOctetStringSlice();
        Assert.assertEquals(oidSlice.length(), 3);
        c.release();
    }

    // ── Benchmark: CHOICE encode/decode via BerWriter/BerCursor ───────

    @Test
    public void benchmarkChoicePrimitive() throws AsnException {
        int WARMUP = 500;
        int ITERATIONS = 50000;

        // Warmup
        for (int i = 0; i < WARMUP; i++) {
            BerWriter w = BerWriter.get();
            w.writeInt32(BerTag.CONTEXT, 0, i);
            BerCursor c = w.resultAsCursor();
            c.readTag();
            int val = c.readInt32();
            c.release();
        }

        // Benchmark
        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            BerWriter w = BerWriter.get();
            w.writeInt32(BerTag.CONTEXT, 0, i);
            BerCursor c = w.resultAsCursor();
            c.readTag();
            int val = c.readInt32();
            c.release();
        }
        long elapsed = System.nanoTime() - start;
        System.out.printf("[CHOICE] CONTEXT[0] INTEGER encode+decode: %,d ns/op (%,d iterations)%n",
            elapsed / ITERATIONS, ITERATIONS);
    }

    @Test
    public void benchmarkChoiceOctetString() throws AsnException {
        int WARMUP = 500;
        int ITERATIONS = 50000;
        byte[] data = new byte[]{0x01, 0x02, 0x03, 0x04};

        for (int i = 0; i < WARMUP; i++) {
            BerWriter w = BerWriter.get();
            w.writeOctetString(BerTag.CONTEXT, 0, data, 0, data.length);
            BerCursor c = w.resultAsCursor();
            c.readTag();
            BerSlice s = c.getOctetStringSlice();
            c.release();
        }

        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            BerWriter w = BerWriter.get();
            w.writeOctetString(BerTag.CONTEXT, 0, data, 0, data.length);
            BerCursor c = w.resultAsCursor();
            c.readTag();
            BerSlice s = c.getOctetStringSlice();
            c.release();
        }
        long elapsed = System.nanoTime() - start;
        System.out.printf("[CHOICE] CONTEXT[0] OCTET STRING(4B) encode+decode: %,d ns/op (%,d iterations)%n",
            elapsed / ITERATIONS, ITERATIONS);
    }

    @Test
    public void benchmarkChoiceConstructed() throws AsnException {
        int WARMUP = 500;
        int ITERATIONS = 50000;

        for (int i = 0; i < WARMUP; i++) {
            BerWriter w = BerWriter.get();
            int mark = w.beginSequence(BerTag.CONTEXT, 2);
            w.writeInt32(BerTag.CONTEXT, 0, i);
            w.endSequence(mark, BerTag.CONTEXT, 2);
            BerCursor c = w.resultAsCursor();
            c.readTag();
            BerCursor sub = c.openConstructed();
            sub.readTag();
            int val = sub.readInt32();
            sub.release();
            c.release();
        }

        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            BerWriter w = BerWriter.get();
            int mark = w.beginSequence(BerTag.CONTEXT, 2);
            w.writeInt32(BerTag.CONTEXT, 0, i);
            w.endSequence(mark, BerTag.CONTEXT, 2);
            BerCursor c = w.resultAsCursor();
            c.readTag();
            BerCursor sub = c.openConstructed();
            sub.readTag();
            int val = sub.readInt32();
            sub.release();
            c.release();
        }
        long elapsed = System.nanoTime() - start;
        System.out.printf("[CHOICE] CONTEXT[2] SEQUENCE{INT} encode+decode: %,d ns/op (%,d iterations)%n",
            elapsed / ITERATIONS, ITERATIONS);
    }

    @Test
    public void benchmarkChoiceDispatch() throws AsnException {
        int WARMUP = 500;
        int ITERATIONS = 50000;

        BerChoice[] choices = {
            BerChoice.contextPrimitive(0, "local"),
            BerChoice.contextPrimitive(1, "global"),
            BerChoice.contextPrimitive(2, "generic"),
            BerChoice.contextConstructed(3, "nested"),
        };

        for (int i = 0; i < WARMUP; i++) {
            BerWriter w = BerWriter.get();
            int tag = i % 4;
            w.writeInt32(BerTag.CONTEXT, tag, i);
            BerCursor c = w.resultAsCursor();
            c.readTag();
            for (BerChoice ch : choices) {
                if (ch.matches(c)) {
                    c.readInt32();
                    break;
                }
            }
            c.release();
        }

        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            BerWriter w = BerWriter.get();
            int tag = i % 4;
            w.writeInt32(BerTag.CONTEXT, tag, i);
            BerCursor c = w.resultAsCursor();
            c.readTag();
            for (BerChoice ch : choices) {
                if (ch.matches(c)) {
                    c.readInt32();
                    break;
                }
            }
            c.release();
        }
        long elapsed = System.nanoTime() - start;
        System.out.printf("[CHOICE] 4-alt dispatch encode+decode: %,d ns/op (%,d iterations)%n",
            elapsed / ITERATIONS, ITERATIONS);
    }

    // ── Concurrency stress test ──────────────────────────────────────

    @Test(threadPoolSize = 4, invocationCount = 10, timeOut = 30000)
    public void testConcurrentChoicePattern() throws AsnException {
        BerWriter w = BerWriter.get();
        w.writeInt32(BerTag.CONTEXT, 0, 42);
        BerCursor c = w.resultAsCursor();
        c.readTag();
        Assert.assertEquals(c.tag(), 0);
        int val = c.readInt32();
        c.release();
        Assert.assertEquals(val, 42);
    }
}
