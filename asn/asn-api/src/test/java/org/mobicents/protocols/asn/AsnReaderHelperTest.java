package org.mobicents.protocols.asn;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

@Test(groups = { "asn", "flat-index" })
public class AsnReaderHelperTest {

    @Test
    public void testFindChildAndReadInteger() {
        byte[] data = new byte[] { 0x30, 0x06, 0x02, 0x01, 0x2a, 0x04, 0x01, 0x0f };

        AsnMessageIndex index = new AsnMessageIndex();
        FlatAsnParser.parseAll(data, 0, data.length, index);

        int seqIdx = AsnReaderHelper.findChildTag(index, -1, 0x30);
        int intIdx = AsnReaderHelper.findChildTag(index, seqIdx, 0x02);
        int octIdx = AsnReaderHelper.findChildTag(index, seqIdx, 0x04);

        assertEquals(AsnReaderHelper.readInteger(index, intIdx), 42L);
        assertEquals(AsnReaderHelper.findTagIndex(index, 0x99), -1);

        AsnReaderHelper.OctetStringView view = AsnReaderHelper.readOctetStringView(index, octIdx);
        assertNotNull(view);
        assertEquals(view.length, 1);
        assertEquals(view.buffer[view.offset], 0x0f);
    }

    @Test
    public void testFindNthChildTag() {
        byte[] data = new byte[] { 0x30, 0x0a, 0x04, 0x01, 0x0f, 0x04, 0x05, 0x2a, (byte) 0xd9, (byte) 0x8c, 0x36, 0x02 };

        AsnMessageIndex index = new AsnMessageIndex();
        FlatAsnParser.parseAll(data, 0, data.length, index);

        int seqIdx = 0;
        int first = AsnReaderHelper.findNthChildTag(index, seqIdx, 0x04, 0);
        int second = AsnReaderHelper.findNthChildTag(index, seqIdx, 0x04, 1);

        assertEquals(index.valueLengths[first], 1);
        assertEquals(index.valueLengths[second], 5);
    }
}
