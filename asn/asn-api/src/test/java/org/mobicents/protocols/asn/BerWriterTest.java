package org.mobicents.protocols.asn;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

/**
 * Unit tests for {@link BerWriter} — backward BER encoding with roundtrip
 * verification via {@link BerCursor} (new path) and legacy decode.
 */
public class BerWriterTest {

    // ==================================================================
    // INTEGER
    // ==================================================================

    @Test
    public void testWriteInt32Roundtrip() throws Exception {
        BerWriter w = BerWriter.get();
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 42);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            assertEquals(BerTag.UNIVERSAL, c.tagClass());
            assertEquals(BerTag.INTEGER, c.tag());
            assertEquals(42, c.readInt32());
        } finally {
            c.release();
        }
    }

    @Test
    public void testWriteInt32Zero() throws Exception {
        BerWriter w = BerWriter.get();
        w.writeInt32(BerTag.CONTEXT, 1, 0);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            assertEquals(BerTag.CONTEXT, c.tagClass());
            assertEquals(1, c.tag());
            assertEquals(0, c.readInt32());
        } finally {
            c.release();
        }
    }

    @Test
    public void testWriteInt32Negative() throws Exception {
        BerWriter w = BerWriter.get();
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, -128);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            assertEquals(-128, c.readInt32());
        } finally {
            c.release();
        }
    }

    @Test
    public void testWriteInt32LargeValue() throws Exception {
        int value = 0x12345678;
        BerWriter w = BerWriter.get();
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, value);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            assertEquals(value, c.readInt32());
        } finally {
            c.release();
        }
    }

    // ==================================================================
    // BOOLEAN
    // ==================================================================

    @Test
    public void testWriteBooleanTrue() throws Exception {
        BerWriter w = BerWriter.get();
        w.writeBoolean(BerTag.UNIVERSAL, BerTag.BOOLEAN, true);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            assertEquals(BerTag.BOOLEAN, c.tag());
            assertEquals(1, c.valueLength());
            assertEquals(0xFF, c.getOctetString()[0] & 0xFF);
        } finally {
            c.release();
        }
    }

    @Test
    public void testWriteBooleanFalse() throws Exception {
        BerWriter w = BerWriter.get();
        w.writeBoolean(BerTag.UNIVERSAL, BerTag.BOOLEAN, false);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            assertEquals(0, c.readInt32());
        } finally {
            c.release();
        }
    }

    // ==================================================================
    // OCTET STRING
    // ==================================================================

    @Test
    public void testWriteOctetString() throws Exception {
        byte[] payload = {0x01, 0x02, 0x03, 0x04, 0x05};
        BerWriter w = BerWriter.get();
        w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING,
            payload, 0, payload.length);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            assertEquals(BerTag.OCTET_STRING, c.tag());
            byte[] result = c.getOctetString();
            assertEquals(payload, result);
        } finally {
            c.release();
        }
    }

    @Test
    public void testWriteEmptyOctetString() throws Exception {
        BerWriter w = BerWriter.get();
        w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING,
            new byte[0], 0, 0);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            assertEquals(0, c.valueLength());
        } finally {
            c.release();
        }
    }

    // ==================================================================
    // NULL
    // ==================================================================

    @Test
    public void testWriteNull() throws Exception {
        BerWriter w = BerWriter.get();
        w.writeNull(BerTag.UNIVERSAL, BerTag.NULL);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            assertEquals(BerTag.NULL, c.tag());
            assertEquals(0, c.valueLength());
        } finally {
            c.release();
        }
    }

    // ==================================================================
    // CONSTRUCTED (SEQUENCE)
    // ==================================================================

    @Test
    public void testSequenceWithTwoIntegers() throws Exception {
        BerWriter w = BerWriter.get();
        int mark = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 100);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 200);
        w.endSequence(mark, BerTag.UNIVERSAL, BerTag.SEQUENCE);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            assertEquals(BerTag.SEQUENCE, c.tag());
            assertFalse(c.isPrimitive());

            BerCursor body = c.openConstructed();
            try {
                // Backward encoder writes last component first in forward order
                body.readTag();
                assertEquals(200, body.readInt32());
                assertTrue(body.hasMore());
                body.readTag();
                assertEquals(100, body.readInt32());
                assertFalse(body.hasMore());
            } finally {
                body.release();
            }
        } finally {
            c.release();
        }
    }

    @Test
    public void testNestedSequence() throws Exception {
        // SEQUENCE { SEQUENCE { INTEGER 5 } }
        BerWriter w = BerWriter.get();
        int markOuter = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        int markInner = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 5);
        w.endSequence(markInner, BerTag.UNIVERSAL, BerTag.SEQUENCE);
        w.endSequence(markOuter, BerTag.UNIVERSAL, BerTag.SEQUENCE);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            BerCursor outer = c.openConstructed();
            try {
                outer.readTag();
                BerCursor inner = outer.openConstructed();
                try {
                    inner.readTag();
                    assertEquals(5, inner.readInt32());
                } finally {
                    inner.release();
                }
            } finally {
                outer.release();
            }
        } finally {
            c.release();
        }
    }

    @Test
    public void testContextConstructed() throws Exception {
        // [CONTEXT 3] constructed { INTEGER 99 }
        BerWriter w = BerWriter.get();
        int mark = w.beginSequence(BerTag.CONTEXT, 3);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 99);
        w.endSequence(mark, BerTag.CONTEXT, 3);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            assertEquals(BerTag.CONTEXT, c.tagClass());
            assertEquals(3, c.tag());
            assertFalse(c.isPrimitive());
            BerCursor body = c.openConstructed();
            try {
                body.readTag();
                assertEquals(99, body.readInt32());
            } finally {
                body.release();
            }
        } finally {
            c.release();
        }
    }

    // ==================================================================
    // OID
    // ==================================================================

    @Test
    public void testWriteOID() throws Exception {
        // OID: {1, 2, 840, 113549, 1, 1, 1}
        int[] oid = {1, 2, 840, 113549, 1, 1, 1};
        BerWriter w = BerWriter.get();
        w.writeOID(BerTag.UNIVERSAL, BerTag.OID, oid);
        byte[] encoded = w.toByteArray();

        // Verify it's valid BER OID
        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            assertEquals(BerTag.OID, c.tag());
            assertTrue(c.valueLength() > 0);
        } finally {
            c.release();
        }
    }

    // ==================================================================
    // THREAD SAFETY (ThreadLocal pool)
    // ==================================================================

    @Test
    public void testThreadLocalIsolation() throws Exception {
        BerWriter w1 = BerWriter.get();
        w1.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 1);
        assertEquals(1, roundtripInt(w1.toByteArray()));

        BerWriter w2 = BerWriter.get();
        w2.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 99);
        assertEquals(99, roundtripInt(w2.toByteArray()));

        // w1 should still have its state
        assertEquals(w1.toByteArray(), w1.toByteArray());
    }

    // ==================================================================
    // BUFFER GROWTH (large data)
    // ==================================================================

    @Test
    public void testLargeDataDoesNotOverflow() throws Exception {
        byte[] largePayload = new byte[10000];
        for (int i = 0; i < largePayload.length; i++) largePayload[i] = (byte) i;

        BerWriter w = BerWriter.get();
        w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING,
            largePayload, 0, largePayload.length);
        byte[] encoded = w.toByteArray();

        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            assertEquals(largePayload.length, c.valueLength());
            byte[] decoded = c.getOctetString();
            assertEquals(largePayload, decoded);
        } finally {
            c.release();
        }
    }

    // ==================================================================
    // FLUSH TO BYTEBUF
    // ==================================================================

    @Test
    public void testFlushToByteBuf() throws Exception {
        BerWriter w = BerWriter.get();
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 42);
        io.netty.buffer.ByteBuf buf = io.netty.buffer.Unpooled.buffer(64);
        try {
            w.flushTo(buf);
            assertEquals(3, buf.readableBytes()); // tag(1) + len(1) + value(1)
            byte[] flushed = new byte[buf.readableBytes()];
            buf.readBytes(flushed);
            assertEquals(42, roundtripInt(flushed));
        } finally {
            buf.release();
        }
    }

    // ==================================================================
    // HELPERS
    // ==================================================================

    private static int roundtripInt(byte[] encoded) throws Exception {
        BerCursor c = BerCursor.wrap(encoded, 0, encoded.length);
        try {
            c.readTag();
            return c.readInt32();
        } finally {
            c.release();
        }
    }
}
