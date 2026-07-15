package org.mobicents.protocols.asn;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

/**
 * Unit tests for {@link BerCursor} — tag decoding, navigation,
 * primitive reads, and pool reuse.
 */
public class BerCursorTest {

    // ==================================================================
    // TAG DECODING
    // ==================================================================

    @Test
    public void testReadShortTag() throws Exception {
        // INTEGER (UNIVERSAL 2, primitive), length 1, value 42
        byte[] data = {0x02, 0x01, 0x2A};
        BerCursor c = BerCursor.wrap(data, 0, data.length);
        try {
            c.readTag();
            assertEquals(BerTag.UNIVERSAL, c.tagClass());
            assertEquals(BerTag.INTEGER, c.tag());
            assertTrue(c.isPrimitive());
            assertEquals(1, c.valueLength());
            c.skipValue();
            assertFalse(c.hasMore());
        } finally {
            c.release();
        }
    }

    @Test
    public void testReadConstructedTag() throws Exception {
        // SEQUENCE (UNIVERSAL 16, constructed), length 3
        byte[] data = {0x30, 0x03, 0x02, 0x01, 0x05};
        BerCursor c = BerCursor.wrap(data, 0, data.length);
        try {
            c.readTag();
            assertEquals(BerTag.UNIVERSAL, c.tagClass());
            assertEquals(BerTag.SEQUENCE, c.tag());
            assertFalse(c.isPrimitive());
            assertEquals(3, c.valueLength());
        } finally {
            c.release();
        }
    }

    @Test
    public void testReadContextTag() throws Exception {
        // [CONTEXT 1] IMPLICIT, primitive, length 0
        byte[] data = {(byte) 0x81, 0x00};
        BerCursor c = BerCursor.wrap(data, 0, data.length);
        try {
            c.readTag();
            assertEquals(BerTag.CONTEXT, c.tagClass());
            assertEquals(1, c.tag());
            assertTrue(c.isPrimitive());
            assertEquals(0, c.valueLength());
        } finally {
            c.release();
        }
    }

    @Test
    public void testReadLongTag() throws Exception {
        // Tag 255 (0x1F 0x81 0x7F), length 0
        byte[] data = {(byte) 0x1F, (byte) 0x81, 0x7F, 0x00};
        BerCursor c = BerCursor.wrap(data, 0, data.length);
        try {
            c.readTag();
            assertEquals(255, c.tag());
            assertEquals(0, c.valueLength());
        } finally {
            c.release();
        }
    }

    @Test
    public void testReadLongLength() throws Exception {
        // OCTET STRING, length 0x0100 (256) encoded as 82 01 00
        byte[] data = new byte[260];
        data[0] = 0x04;           // OCTET STRING
        data[1] = (byte) 0x82;    // long length, 2 bytes
        data[2] = 0x01;           // high byte
        data[3] = 0x00;           // low byte
        BerCursor c = BerCursor.wrap(data, 0, data.length);
        try {
            c.readTag();
            assertEquals(256, c.valueLength());
        } finally {
            c.release();
        }
    }

    // ==================================================================
    // NAVIGATION
    // ==================================================================

    @Test
    public void testOpenConstructed() throws Exception {
        // SEQUENCE containing INTEGER(5)
        byte[] data = {0x30, 0x03, 0x02, 0x01, 0x05};
        BerCursor c = BerCursor.wrap(data, 0, data.length);
        try {
            c.readTag();
            assertEquals(BerTag.SEQUENCE, c.tag());
            assertFalse(c.isPrimitive());

            BerCursor body = c.openConstructed();
            try {
                body.readTag();
                assertEquals(BerTag.INTEGER, body.tag());
                assertEquals(1, body.valueLength());
                assertEquals(5, body.readInt32());
            } finally {
                body.release();
            }
        } finally {
            c.release();
        }
    }

    @Test
    public void testHasMoreAndSkipValue() throws Exception {
        // SEQUENCE with two INTEGERS: {5, 10}
        byte[] data = {0x30, 0x06,
            0x02, 0x01, 0x05,
            0x02, 0x01, 0x0A};
        BerCursor c = BerCursor.wrap(data, 0, data.length);
        try {
            c.readTag();
            BerCursor body = c.openConstructed();
            try {
                body.readTag();
                assertEquals(5, body.readInt32());

                // After readInt32, pos should be at end of value, hasMore should be true
                assertTrue(body.hasMore());

                body.readTag();
                assertEquals(10, body.readInt32());
                assertFalse(body.hasMore());
            } finally {
                body.release();
            }
        } finally {
            c.release();
        }
    }

    // ==================================================================
    // PRIMITIVE READS
    // ==================================================================

    @Test
    public void testReadInt32Positive() throws Exception {
        // INTEGER 127
        byte[] data = {0x02, 0x01, 0x7F};
        BerCursor c = BerCursor.wrap(data, 0, data.length);
        try {
            c.readTag();
            assertEquals(127, c.readInt32());
        } finally {
            c.release();
        }
    }

    @Test
    public void testReadInt32Negative() throws Exception {
        // INTEGER -1 (0xFF)
        byte[] data = {0x02, 0x01, (byte) 0xFF};
        BerCursor c = BerCursor.wrap(data, 0, data.length);
        try {
            c.readTag();
            assertEquals(-1, c.readInt32());
        } finally {
            c.release();
        }
    }

    @Test
    public void testReadInt32MultiByte() throws Exception {
        // INTEGER 0x12345678 = 305419896
        byte[] data = {0x02, 0x04, 0x12, 0x34, 0x56, 0x78};
        BerCursor c = BerCursor.wrap(data, 0, data.length);
        try {
            c.readTag();
            assertEquals(0x12345678, c.readInt32());
        } finally {
            c.release();
        }
    }

    @Test
    public void testGetOctetString() throws Exception {
        byte[] payload = {0x01, 0x02, 0x03, 0x04};
        // OCTET STRING tag 4, length 4
        byte[] data = new byte[2 + 4];
        data[0] = 0x04;
        data[1] = 0x04;
        System.arraycopy(payload, 0, data, 2, 4);

        BerCursor c = BerCursor.wrap(data, 0, data.length);
        try {
            c.readTag();
            byte[] result = c.getOctetString();
            assertEquals(payload, result);
        } finally {
            c.release();
        }
    }

    @Test
    public void testGetOctetStringSlice() throws Exception {
        byte[] payload = {(byte) 0xAA, (byte) 0xBB, (byte) 0xCC};
        byte[] data = new byte[2 + 3];
        data[0] = 0x04;
        data[1] = 0x03;
        System.arraycopy(payload, 0, data, 2, 3);

        BerCursor c = BerCursor.wrap(data, 0, data.length);
        try {
            c.readTag();
            BerSlice slice = c.getOctetStringSlice();
            assertEquals(3, slice.length());
            assertEquals(payload, slice.toByteArray());
        } finally {
            c.release();
        }
    }

    // ==================================================================
    // POOL REUSE
    // ==================================================================

    @Test
    public void testPoolReuse() throws Exception {
        byte[] data = {0x02, 0x01, 0x2A};
        BerCursor c1 = BerCursor.wrap(data, 0, data.length);
        c1.readTag();
        assertEquals(BerTag.INTEGER, c1.tag());
        c1.release();

        // Same thread should get the same cursor back from pool
        BerCursor c2 = BerCursor.wrap(data, 0, data.length);
        c2.readTag();
        assertEquals(BerTag.INTEGER, c2.tag());
        c2.release();
    }

    // ==================================================================
    // LONG INTEGER
    // ==================================================================

    @Test
    public void testReadInt64() throws Exception {
        long value = 0x1234567890ABCDEFL;
        // 8 bytes, big-endian
        byte[] intBytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            intBytes[7 - i] = (byte) (value >>> (i * 8));
        }
        byte[] data = new byte[2 + 8];
        data[0] = 0x02;
        data[1] = 0x08;
        System.arraycopy(intBytes, 0, data, 2, 8);

        BerCursor c = BerCursor.wrap(data, 0, data.length);
        try {
            c.readTag();
            assertEquals(value, c.readInt64());
        } finally {
            c.release();
        }
    }
}
