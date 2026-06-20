package org.mobicents.protocols.asn;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.testng.annotations.Test;

/**
 * Ensures pooled/quick-win encode helpers remain wire-compatible with legacy {@link #toByteArray()}.
 */
@Test(groups = { "asn", "asn-compat" })
public class AsnOutputStreamCompatibilityTest {

    @Test
    public void copyEncodedBytesMatchesToByteArray() throws Exception {
        AsnOutputStream aos = new AsnOutputStream();
        aos.writeTag(Tag.CLASS_UNIVERSAL, true, Tag.STRING_OCTET);
        int pos = aos.StartContentDefiniteLength();
        aos.write(0x0f);
        aos.FinalizeContent(pos);

        byte[] legacy = aos.toByteArray();
        byte[] copy = aos.copyEncodedBytes();

        assertTrue(Arrays.equals(legacy, copy));
        assertEquals(aos.getEncodedLength(), legacy.length);
        assertTrue(Arrays.equals(legacy, aos.getEncodedBytes()));
    }

    @Test
    public void resetReusesBufferWithoutCorruptingEncode() throws Exception {
        AsnOutputStream aos = new AsnOutputStream();
        aos.writeTag(Tag.CLASS_UNIVERSAL, true, Tag.STRING_OCTET);
        int pos = aos.StartContentDefiniteLength();
        aos.write(0x0f);
        aos.FinalizeContent(pos);
        byte[] first = aos.toByteArray();

        aos.reset();
        aos.writeTag(Tag.CLASS_UNIVERSAL, true, Tag.STRING_OCTET);
        pos = aos.StartContentDefiniteLength();
        aos.write(0x0f);
        aos.FinalizeContent(pos);
        byte[] second = aos.toByteArray();

        assertTrue(Arrays.equals(first, second));
    }
}
