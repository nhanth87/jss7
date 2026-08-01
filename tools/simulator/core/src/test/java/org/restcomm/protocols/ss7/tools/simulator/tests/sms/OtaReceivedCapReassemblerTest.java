package org.restcomm.protocols.ss7.tools.simulator.tests.sms;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

/**
 * Unit tests for {@link OtaReceivedCapReassembler} (out-of-order, timeout, atomic write).
 */
public class OtaReceivedCapReassemblerTest extends TestCase {

    public void testOutOfOrderMergeAndWrite() throws Exception {
        Path dir = Files.createTempDirectory("ota-cap-reasm");
        AtomicLong clock = new AtomicLong(1_000_000L);
        OtaReceivedCapReassembler r = new OtaReceivedCapReassembler(dir, 60_000L, clock::get);
        r.rememberSubscriber("246020000000001", "251911000001");

        byte[] p1 = new byte[] { 0x01, 0x02 };
        byte[] p2 = new byte[] { 0x03, 0x04, 0x05 };
        byte[] p3 = new byte[] { 0x06 };

        assertFalse(r.offer("246020000000001", 7, false, 2, 3, p2, true).isPresent());
        assertFalse(r.offer("246020000000001", 7, false, 3, 3, p3, true).isPresent());
        Optional<Path> done = r.offer("246020000000001", 7, false, 1, 3, p1, true);
        assertTrue(done.isPresent());

        byte[] merged = Files.readAllBytes(done.get());
        assertEquals(6, merged.length);
        assertEquals(0x01, merged[0] & 0xFF);
        assertEquals(0x06, merged[5] & 0xFF);
        assertTrue(done.get().getFileName().toString().contains("251911000001"));
        assertTrue(done.get().getFileName().toString().endsWith(".cap"));
        assertEquals(1L, r.getWrittenCount());
        assertFalse(Files.exists(dir.resolve(OtaReceivedCapReassembler.SUBDIR)
                .resolve(done.get().getFileName().toString() + ".tmp")));
    }

    public void testTimeoutDiscardsIncomplete() throws Exception {
        Path dir = Files.createTempDirectory("ota-cap-to");
        AtomicLong clock = new AtomicLong(0L);
        OtaReceivedCapReassembler r = new OtaReceivedCapReassembler(dir, 1000L, clock::get);

        assertFalse(r.offer("1", 1, false, 1, 2, new byte[] { 0x0A }, true).isPresent());
        clock.set(5000L);
        r.purgeExpired();
        // late segment after timeout starts a new incomplete assembly
        assertFalse(r.offer("1", 1, false, 2, 2, new byte[] { 0x0B }, true).isPresent());
        assertEquals(0L, r.getWrittenCount());
        Path out = dir.resolve(OtaReceivedCapReassembler.SUBDIR);
        if (Files.isDirectory(out)) {
            try (var stream = Files.list(out)) {
                assertEquals(0, stream.count());
            }
        }
    }

    public void testSinglePartRequiresOtaHint() throws Exception {
        Path dir = Files.createTempDirectory("ota-cap-single");
        OtaReceivedCapReassembler r = new OtaReceivedCapReassembler(dir);
        assertFalse(r.offer("1", null, false, 1, 1, new byte[] { 0x01 }, false).isPresent());
        Optional<Path> done = r.offer("1", null, false, 1, 1, new byte[] { 0x01, 0x02 }, true);
        assertTrue(done.isPresent());
        assertEquals(2, Files.readAllBytes(done.get()).length);
    }
}
