package org.mobicents.protocols.asn;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

@Test(groups = { "asn", "flat-index" })
public class FlatAsnParserTest {

    @Test
    public void testMapUssdSequence() {
        byte[] data = new byte[] { 0x30, 0x0a, 0x04, 0x01, 0x0f, 0x04, 0x05, 0x2a, (byte) 0xd9, (byte) 0x8c, 0x36, 0x02 };

        AsnMessageIndex index = new AsnMessageIndex();
        FlatAsnParser.parseAll(data, 0, data.length, index);

        assertEquals(index.tagCount, 3);
        assertEquals(index.tags[0], 0x30);
        assertEquals(index.depths[0], 0);
        assertEquals(index.parents[0], -1);

        assertEquals(index.tags[1], 0x04);
        assertEquals(index.parents[1], 0);
        assertEquals(index.valueLengths[1], 1);
        assertEquals(index.rawBuffer[index.valueOffsets[1]], 0x0f);

        assertEquals(index.tags[2], 0x04);
        assertEquals(index.parents[2], 0);
        assertEquals(index.valueLengths[2], 5);
    }

    @Test
    public void testNestedTcapMapFixture() {
        byte[] data = new byte[] {
                0x62, 0x1c,
                0x49, 0x04, 0x51, 0x00, 0x02, 0x38,
                0x6c, 0x14,
                (byte) 0xa1, 0x12,
                0x02, 0x01, 0x01,
                0x02, 0x01, 0x3b,
                0x30, 0x0a,
                0x04, 0x01, 0x0f,
                0x04, 0x05, 0x2a, (byte) 0xd9, (byte) 0x8c, 0x36, 0x02
        };

        AsnMessageIndex index = new AsnMessageIndex();
        FlatAsnParser.parseAll(data, 0, data.length, index);

        assertTrue(index.tagCount >= 7);

        int rootIdx = AsnReaderHelper.findChildTag(index, -1, 0x62);
        assertTrue(rootIdx >= 0);

        int componentIdx = AsnReaderHelper.findChildTag(index, rootIdx, 0x6c);
        assertTrue(componentIdx >= 0);

        int invokeIdx = AsnReaderHelper.findChildTag(index, componentIdx, 0xa1);
        assertTrue(invokeIdx >= 0);

        int sequenceIdx = AsnReaderHelper.findChildTag(index, invokeIdx, 0x30);
        assertTrue(sequenceIdx >= 0);

        int dcsIdx = AsnReaderHelper.findNthChildTag(index, sequenceIdx, 0x04, 0);
        int ussdIdx = AsnReaderHelper.findNthChildTag(index, sequenceIdx, 0x04, 1);
        assertTrue(dcsIdx >= 0);
        assertTrue(ussdIdx >= 0);
        assertEquals(index.rawBuffer[index.valueOffsets[dcsIdx]], 0x0f);
        assertEquals(index.valueLengths[ussdIdx], 5);
        assertEquals(index.depths[ussdIdx], index.depths[sequenceIdx] + 1);
    }

    @Test
    public void testOctetStringWithEmbeddedDoubleZeroBytes() {
        byte[] data = new byte[] { 0x30, 0x07, (byte) 0x81, 0x05, (byte) 0xC6, 0x00, 0x00, 0x11, 0x17 };

        AsnMessageIndex index = new AsnMessageIndex();
        FlatAsnParser.parseAll(data, 0, data.length, index);

        assertEquals(index.tagCount, 2);
        int addrIdx = AsnReaderHelper.findChildTag(index, 0, 0x81);
        assertTrue(addrIdx >= 0);
        assertEquals(index.valueLengths[addrIdx], 5);
        assertEquals(index.rawBuffer[index.valueOffsets[addrIdx] + 1], 0x00);
        assertEquals(index.rawBuffer[index.valueOffsets[addrIdx] + 2], 0x00);
    }

    @Test
    public void testInformServiceCentreFullSequenceIndexesAllTopLevelFields() {
        byte[] data = new byte[] { 48, 61, 4, 6, -111, 17, 33, 34, 51, -13, 3, 2, 2, 80, 48, 39, -96, 32, 48, 10, 6, 3,
                42, 3, 4, 11, 12, 13, 14, 15, 48, 5, 6, 3, 42, 3, 6, 48, 11, 6, 3, 42, 3, 5, 21, 22, 23, 24, 25, 26, -95,
                3, 31, 32, 33, 2, 2, 2, 43, -128, 2, 1, -68 };

        AsnMessageIndex index = new AsnMessageIndex();
        FlatAsnParser.parseAll(data, 2, 61, index);

        int extIdx = -1;
        int integerIdx = -1;
        int ctx0Idx = -1;
        for (int i = 0; i < index.tagCount; i++) {
            if (index.parents[i] != -1) {
                continue;
            }
            if (index.tags[i] == 0x30 && AsnReaderHelper.isConstructed(index.tags[i])) {
                extIdx = i;
            } else if (index.tags[i] == Tag.INTEGER) {
                integerIdx = i;
            } else if (index.tags[i] == 0x80) {
                ctx0Idx = i;
            }
        }

        assertTrue(extIdx >= 0, "extensionContainer SEQUENCE missing");
        assertTrue(integerIdx >= 0, "absentSubscriberDiagnosticSM INTEGER missing");
        assertTrue(ctx0Idx >= 0, "additionalAbsentSubscriberDiagnosticSM [0] missing");
        assertEquals(AsnReaderHelper.readInteger(index, integerIdx), 555L);
        assertEquals(AsnReaderHelper.readInteger(index, ctx0Idx), 444L);
    }
}
