package org.restcomm.protocols.ss7.tcap;

import static org.testng.Assert.assertEquals;

import org.restcomm.protocols.ss7.scheduler.W2Priority;
import org.restcomm.protocols.ss7.scheduler.W2PriorityClassifier;
import org.testng.annotations.Test;

public class TCAPProviderW2IngressTest {

    @Test
    public void shouldClassifyOuterTcapPackageWithoutDecodingPayload() {
        assertEquals(TCAPProviderImpl.w2IngressPriority(new byte[] { 0x62 }), W2Priority.CRITICAL);
        assertEquals(TCAPProviderImpl.w2IngressPriority(new byte[] { 0x65 }), W2Priority.NORMAL);
        assertEquals(TCAPProviderImpl.w2IngressPriority(new byte[] { 0x64 }), W2Priority.LOW);
        assertEquals(TCAPProviderImpl.w2IngressPriority(new byte[] { 0x67 }), W2Priority.LOW);
        assertEquals(TCAPProviderImpl.w2IngressPriority(new byte[] { 0x61 }), W2Priority.NORMAL);
    }

    @Test
    public void shouldUseNormalPriorityForMalformedOrUnknownPredecodeInput() {
        assertEquals(TCAPProviderImpl.w2IngressPriority(null), W2Priority.NORMAL);
        assertEquals(TCAPProviderImpl.w2IngressPriority(new byte[0]), W2Priority.NORMAL);
        assertEquals(TCAPProviderImpl.w2IngressPriority(new byte[] { 0x02 }), W2Priority.NORMAL);
    }

    @Test
    public void shouldClassifyOnlyLargeContinueAsHigh() {
        byte[] largeContinue = new byte[W2PriorityClassifier.DEFAULT_LARGE_CONTINUE_BYTES + 1];
        largeContinue[0] = 0x65;
        assertEquals(TCAPProviderImpl.w2IngressPriority(largeContinue), W2Priority.HIGH);
    }
}
