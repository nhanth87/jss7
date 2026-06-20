package org.mobicents.protocols.asn;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

@Test(groups = { "asn", "stream-pool" })
public class AsnStreamPoolTest {

    @Test
    public void testBorrowReusesThreadLocalInstance() {
        byte[] first = new byte[] { 0x02, 0x01, 0x2a };
        byte[] second = new byte[] { 0x04, 0x01, 0x0f };

        AsnInputStream ais1 = AsnStreamPool.borrow(first);
        AsnInputStream ais2 = AsnStreamPool.borrow(second);

        assertSame(ais1, ais2);
        assertEquals(ais2.getBuffer(), second);
        assertEquals(ais2.getStartOffset(), 0);
        assertEquals(ais2.getDataLength(), second.length);
    }

    @Test
    public void testBorrowSliceRebindsOffsetAndLength() {
        byte[] buffer = new byte[] { 0x00, 0x00, 0x02, 0x01, 0x2a, 0x00 };

        AsnInputStream ais = AsnStreamPool.borrowSlice(buffer, 2, 3);

        assertEquals(ais.getBuffer(), buffer);
        assertEquals(ais.getStartOffset(), 2);
        assertEquals(ais.getDataLength(), 3);
    }

    @Test
    public void testViewBytesUsesPool() {
        byte[] buffer = new byte[] { 0x00, 0x02, 0x01, 0x2a };

        AsnInputStream view = AsnInputStream.viewBytes(buffer, 1, 3);
        AsnInputStream borrowed = AsnStreamPool.borrowSlice(buffer, 1, 3);

        assertSame(view, borrowed);
        assertEquals(view.getStartOffset(), 1);
        assertEquals(view.getDataLength(), 3);
    }

    @Test
    public void testBorrowTaggedSetsTagMetadata() throws Exception {
        byte[] data = new byte[] { 0x2a };

        AsnInputStream ais = AsnStreamPool.borrowTagged(data, 2, true, 0);

        assertEquals(ais.getTagClass(), 2);
        assertEquals(ais.isTagPrimitive(), true);
        assertEquals(ais.getTag(), 0);
    }
}
