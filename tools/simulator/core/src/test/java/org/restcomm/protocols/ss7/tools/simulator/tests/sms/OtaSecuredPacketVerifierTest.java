package org.restcomm.protocols.ss7.tools.simulator.tests.sms;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import junit.framework.TestCase;

/**
 * Proves the received-file hash story end to end.
 *
 * <p>The sender side (GP RAM script → TS 102.225 secured packet → SMS-PP
 * segments) is re-implemented here so the test is independent of the OTA app,
 * then fed through {@link OtaReceivedCapReassembler}. The recovered CAP must be
 * byte-identical to the pushed one — which is exactly why the {@code .otapkt}
 * capture is <em>not</em>.
 */
public class OtaSecuredPacketVerifierTest extends TestCase {

    /** Same size as the lab CAP, so the asserted byte accounting is the real one. */
    private static final int CAP_SIZE = 12849;

    private static final byte[] KIC = OtaSecuredPacketVerifier.labKic();
    private static final byte[] KID = OtaSecuredPacketVerifier.labKid();
    private static final byte[] TAR = { (byte) 0xB0, 0x00, 0x10 };
    private static final byte[] CNTR = { 0x00, 0x00, 0x00, 0x00, 0x01 };
    private static final byte SPI1 = (byte) 0x26;
    private static final byte SPI2 = (byte) 0x00;
    private static final byte KEY_CODING = (byte) 0x12;

    // ── RFC 4493 known-answer vectors for the hand-rolled AES-CMAC ──

    public void testAesCmacMatchesRfc4493() throws Exception {
        byte[] key = hex("2b7e151628aed2a6abf7158809cf4f3c");
        byte[] msg = hex("6bc1bee22e409f96e93d7e117393172a"
                + "ae2d8a571e03ac9c9eb76fac45af8e51"
                + "30c81c46a35ce411e5fbc1191a0a52ef"
                + "f69f2445df4f9b17ad2b417be66c3710");

        assertCmac("bb1d6929e95937287fa37d129b756746", key, new byte[0]);
        assertCmac("070a16b46b4d4144f79bdd9dd04a287c", key, Arrays.copyOf(msg, 16));
        assertCmac("dfa66747de9ae63030ca32611497c827", key, Arrays.copyOf(msg, 40));
        assertCmac("51f0bebf7e3b9d92fc49741779363cfe", key, msg);
    }

    private static void assertCmac(String expected, byte[] key, byte[] msg) throws Exception {
        assertEquals(expected, HexFormat.of().formatHex(OtaSecuredPacketVerifier.aesCmac(key, msg)));
    }

    // ── Round trip ──

    public void testRoundTripRecoversExactCap() throws Exception {
        byte[] cap = deterministicCap(CAP_SIZE);

        byte[] script = buildGpRamScript(cap);
        assertEquals("INSTALL[for load] 19 + LOAD 13178 + INSTALL[for install] 43",
                13240, script.length);

        byte[] packet = buildSecuredPacket(script);
        assertEquals("clear header 10 + CNTR|PCNTR|CC|script|pad", 13274, packet.length);

        List<byte[]> bodies = frameSmsPp(packet);
        assertEquals(100, bodies.size());

        OtaSecuredPacketVerifier verifier = new OtaSecuredPacketVerifier();
        OtaSecuredPacketVerifier.Result result = verifier.verify(packet);

        assertNull(result.failure());
        assertTrue(result.ciphered());
        assertTrue(result.ccPresent());
        assertTrue("CC must verify under KID", result.ccValid());
        assertTrue(result.ok());
        assertEquals(65, result.loadBlocks());
        assertTrue("recovered CAP must be byte-identical", Arrays.equals(cap, result.cap()));
        assertEquals(OtaSecuredPacketVerifier.sha256Hex(cap), result.capSha256());
    }

    public void testReassemblerWritesBothArtefacts() throws Exception {
        byte[] cap = deterministicCap(CAP_SIZE);
        byte[] packet = buildSecuredPacket(buildGpRamScript(cap));
        List<byte[]> bodies = frameSmsPp(packet);

        Path dir = Files.createTempDirectory("ota-roundtrip");
        OtaReceivedCapReassembler r = new OtaReceivedCapReassembler(dir);
        r.rememberSubscriber("246020000000001", "251911000001");

        Optional<OtaReceivedCapReassembler.Completed> done = Optional.empty();
        int total = bodies.size();
        for (int i = 0; i < total; i++) {
            done = r.offer("246020000000001", 42, false, i + 1, total, bodies.get(i), true);
            if (i < total - 1) {
                assertFalse("completed too early at segment " + (i + 1), done.isPresent());
            }
        }
        assertTrue(done.isPresent());

        OtaReceivedCapReassembler.Completed c = done.get();
        assertTrue(c.packetPath().getFileName().toString()
                .endsWith(OtaReceivedCapReassembler.EXT_PACKET));
        assertNotNull("CAP should have been recovered", c.capPath());
        assertEquals(c.capPath(), c.primaryPath());

        byte[] capturedPacket = Files.readAllBytes(c.packetPath());
        byte[] recoveredCap = Files.readAllBytes(c.capPath());

        assertTrue(Arrays.equals(packet, capturedPacket));
        assertTrue(Arrays.equals(cap, recoveredCap));
        // The whole point: the capture is not the CAP, the recovered file is.
        assertFalse(Arrays.equals(cap, capturedPacket));
        assertEquals(1L, r.getVerifiedCount());
    }

    public void testWrongKidIsReportedNotSwallowed() throws Exception {
        byte[] cap = deterministicCap(512);
        byte[] packet = buildSecuredPacket(buildGpRamScript(cap));

        byte[] otherKid = OtaSecuredPacketVerifier.labKid();
        otherKid[0] ^= (byte) 0xFF;
        OtaSecuredPacketVerifier verifier = new OtaSecuredPacketVerifier(KIC, otherKid);
        OtaSecuredPacketVerifier.Result result = verifier.verify(packet);

        assertFalse(result.ok());
        assertFalse(result.ccValid());
        assertNotNull(result.failure());
        assertNull(result.cap());
    }

    public void testTruncatedPacketDoesNotThrow() {
        OtaSecuredPacketVerifier verifier = new OtaSecuredPacketVerifier();
        assertFalse(verifier.verify(new byte[] { 0x00, 0x01 }).ok());
        assertFalse(verifier.verify(new byte[0]).ok());
        assertFalse(verifier.verify(null).ok());
    }

    // ── Sender side, mirroring the OTA app ──

    private static byte[] deterministicCap(int size) {
        byte[] cap = new byte[size];
        for (int i = 0; i < size; i++) {
            cap[i] = (byte) ((i * 31 + 7) & 0xFF);
        }
        return cap;
    }

    /** INSTALL[for load] | LOAD × N (C4 TLV over 200-octet blocks) | INSTALL[for install]. */
    private static byte[] buildGpRamScript(byte[] cap) {
        byte[] loadFileAid = { (byte) 0xA0, 0x00, 0x00, 0x00, 0x62, 0x03, 0x01, 0x0C, 0x0D };
        byte[] moduleAid = { (byte) 0xA0, 0x00, 0x00, 0x00, 0x62, 0x03, 0x01, 0x0C, 0x0D, 0x01 };

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ByteArrayOutputStream forLoad = new ByteArrayOutputStream();
        writeLv(forLoad, loadFileAid);
        writeLv(forLoad, new byte[0]);
        writeLv(forLoad, new byte[0]);
        writeLv(forLoad, new byte[0]);
        writeLv(forLoad, new byte[0]);
        out.writeBytes(apdu((byte) 0x80, (byte) 0xE6, (byte) 0x02, (byte) 0x00,
                forLoad.toByteArray()));

        byte[] prefix = berTlvPrefixC4(cap.length);
        int totalValue = prefix.length + cap.length;
        int offset = 0;
        int blockIndex = 0;
        while (offset < totalValue) {
            int len = Math.min(200, totalValue - offset);
            byte[] chunk = new byte[len];
            for (int i = 0; i < len; i++) {
                int src = offset + i;
                chunk[i] = src < prefix.length ? prefix[src] : cap[src - prefix.length];
            }
            offset += len;
            byte p1 = offset >= totalValue ? (byte) 0x80 : (byte) 0x00;
            out.writeBytes(apdu((byte) 0x80, (byte) 0xE8, p1, (byte) (blockIndex & 0xFF), chunk));
            blockIndex++;
        }

        ByteArrayOutputStream forInstall = new ByteArrayOutputStream();
        writeLv(forInstall, loadFileAid);
        writeLv(forInstall, moduleAid);
        writeLv(forInstall, moduleAid);
        writeLv(forInstall, new byte[] { 0x00 });
        writeLv(forInstall, new byte[] { (byte) 0xC9, 0x00 });
        writeLv(forInstall, new byte[0]);
        out.writeBytes(apdu((byte) 0x80, (byte) 0xE6, (byte) 0x0C, (byte) 0x00,
                forInstall.toByteArray()));

        return out.toByteArray();
    }

    /** CPL|CHL|SPI|KIc|KID|TAR | AES-CBC(CNTR|PCNTR|CC|payload|pad). */
    private static byte[] buildSecuredPacket(byte[] payload) throws Exception {
        int chl = 21;
        int ccLen = 8;
        int withoutPad = CNTR.length + 1 + ccLen + payload.length;
        int padLen = (16 - (withoutPad % 16)) % 16;
        int ciphertextLen = withoutPad + padLen;
        int cpl = 8 + ciphertextLen;

        ByteArrayOutputStream mac = new ByteArrayOutputStream();
        mac.write((cpl >> 8) & 0xFF);
        mac.write(cpl & 0xFF);
        mac.write(chl);
        mac.write(SPI1);
        mac.write(SPI2);
        mac.write(KEY_CODING);
        mac.write(KEY_CODING);
        mac.writeBytes(TAR);
        mac.writeBytes(CNTR);
        mac.write(padLen);
        mac.writeBytes(payload);
        mac.writeBytes(new byte[padLen]);
        byte[] cc = Arrays.copyOf(OtaSecuredPacketVerifier.aesCmac(KID, mac.toByteArray()), ccLen);

        ByteArrayOutputStream clear = new ByteArrayOutputStream();
        clear.writeBytes(CNTR);
        clear.write(padLen);
        clear.writeBytes(cc);
        clear.writeBytes(payload);
        clear.writeBytes(new byte[padLen]);

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KIC, "AES"),
                new IvParameterSpec(new byte[16]));
        byte[] ciphertext = cipher.doFinal(clear.toByteArray());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write((cpl >> 8) & 0xFF);
        out.write(cpl & 0xFF);
        out.write(chl);
        out.write(SPI1);
        out.write(SPI2);
        out.write(KEY_CODING);
        out.write(KEY_CODING);
        out.writeBytes(TAR);
        out.writeBytes(ciphertext);
        return out.toByteArray();
    }

    /**
     * Splits into TP-UD bodies using the sender's capacities: segment 1 reserves
     * the concat IE plus the command-packet IE (8-octet UDH), the rest only the
     * concat IE (6-octet UDH). UDH itself is not built — the reassembler is fed
     * post-UDH bodies, as {@code TestSmsServerMan} does.
     */
    private static List<byte[]> frameSmsPp(byte[] packet) {
        int firstCapacity = 140 - 8;
        int restCapacity = 140 - 6;
        List<byte[]> bodies = new ArrayList<>();
        int offset = 0;
        while (offset < packet.length) {
            int capacity = bodies.isEmpty() ? firstCapacity : restCapacity;
            int len = Math.min(capacity, packet.length - offset);
            bodies.add(Arrays.copyOfRange(packet, offset, offset + len));
            offset += len;
        }
        return bodies;
    }

    private static byte[] berTlvPrefixC4(int valueLen) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(5);
        out.write(0xC4);
        if (valueLen <= 0x7F) {
            out.write(valueLen);
        } else if (valueLen <= 0xFF) {
            out.write(0x81);
            out.write(valueLen);
        } else if (valueLen <= 0xFFFF) {
            out.write(0x82);
            out.write((valueLen >> 8) & 0xFF);
            out.write(valueLen & 0xFF);
        } else {
            out.write(0x83);
            out.write((valueLen >> 16) & 0xFF);
            out.write((valueLen >> 8) & 0xFF);
            out.write(valueLen & 0xFF);
        }
        return out.toByteArray();
    }

    private static byte[] apdu(byte cla, byte ins, byte p1, byte p2, byte[] data) {
        byte[] a = new byte[5 + data.length];
        a[0] = cla;
        a[1] = ins;
        a[2] = p1;
        a[3] = p2;
        a[4] = (byte) data.length;
        System.arraycopy(data, 0, a, 5, data.length);
        return a;
    }

    private static void writeLv(ByteArrayOutputStream out, byte[] value) {
        out.write(value.length & 0xFF);
        out.writeBytes(value);
    }

    private static byte[] hex(String s) {
        return HexFormat.of().parseHex(s);
    }
}
