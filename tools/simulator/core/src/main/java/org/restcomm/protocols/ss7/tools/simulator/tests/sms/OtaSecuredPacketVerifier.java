package org.restcomm.protocols.ss7.tools.simulator.tests.sms;

import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Lab helper: undo the sender-side OTA packing so a received SMS-PP secured
 * packet can be compared byte-for-byte against the CAP that was pushed.
 *
 * <p>The reassembled SMS-PP body is <em>not</em> the CAP: the sender applies
 * three transformations, each of which changes both length and content.
 *
 * <pre>
 *   CAP                                      (e.g. 12849 octets)
 *     -&gt; GP RAM APDU script                  INSTALL[for load] | LOAD*N (C4 TLV) | INSTALL[for install]
 *     -&gt; TS 102.225 secured packet           CPL|CHL|SPI|KIc|KID|TAR | AES-CBC(CNTR|PCNTR|CC|script|pad)
 *     -&gt; TS 23.040 SMS-PP segments           140-octet TP-UD each, UDH per segment
 * </pre>
 *
 * <p>This class walks that chain backwards: parse the clear header, decipher
 * under KIc, verify the cryptographic checksum under KID, un-frame the APDU
 * script, and concatenate the {@code C4} Load File Data Block value. What comes
 * out is the original CAP, so its SHA-256 matches the pushed file.
 *
 * <p>Integrity uses AES-CMAC (RFC 4493) implemented on top of the JDK AES
 * primitive — the simulator deliberately carries no BouncyCastle dependency.
 *
 * <p>Stateless and immutable; safe to share across threads.
 */
public final class OtaSecuredPacketVerifier {

    /** Disable the whole verification step: {@code -Dota.verify.enabled=false}. */
    public static final String PROP_ENABLED = "ota.verify.enabled";
    /** Cipher key override, hex: {@code -Dota.verify.kic=000102...0f}. */
    public static final String PROP_KIC = "ota.verify.kic";
    /** Integrity key override, hex: {@code -Dota.verify.kid=101112...1f}. */
    public static final String PROP_KID = "ota.verify.kid";

    /** Octets of clear header after CPL: CHL(1)+SPI(2)+KIc(1)+KID(1)+TAR(3). */
    public static final int CLEAR_HEADER_AFTER_CPL = 8;
    /** TAR length (octets). */
    public static final int TAR_LEN = 3;
    /** Anti-replay counter length (octets). */
    public static final int CNTR_LEN = 5;
    /** Cryptographic checksum length after truncation (octets). */
    public static final int CC_LEN = 8;
    /** Redundancy check length when SPI1 selects RC instead of CC (octets). */
    public static final int RC_LEN = 4;

    private static final int AES_BLOCK = 16;
    private static final int AES_KEY_LEN = 16;

    /** SPI1 b3: ciphering applied to the packet body. */
    private static final int SPI1_CIPHERED = 0x04;
    /** SPI1 b2b1: integrity mechanism. */
    private static final int SPI1_INTEGRITY_MASK = 0x03;
    private static final int INTEGRITY_NONE = 0x00;
    private static final int INTEGRITY_RC = 0x01;
    private static final int INTEGRITY_CC = 0x02;
    private static final int INTEGRITY_DS = 0x03;

    /** GP LOAD instruction — its data field carries the Load File Data Block. */
    private static final int INS_LOAD = 0xE8;
    /** GP tag: Load File Data Block. */
    private static final int TAG_LOAD_FILE_DATA_BLOCK = 0xC4;
    private static final int APDU_HEADER_LEN = 5;

    private final byte[] kic;
    private final byte[] kid;

    /** Uses the LAB keys, honouring {@code ota.verify.kic} / {@code ota.verify.kid}. */
    public OtaSecuredPacketVerifier() {
        this(resolveKey(PROP_KIC, labKic()), resolveKey(PROP_KID, labKid()));
    }

    public OtaSecuredPacketVerifier(byte[] kic, byte[] kid) {
        this.kic = validate(kic, "KIc");
        this.kid = validate(kid, "KID");
    }

    /** Whether verification is enabled (default true). */
    public static boolean enabled() {
        String v = System.getProperty(PROP_ENABLED);
        return v == null || v.isBlank() || Boolean.parseBoolean(v.trim());
    }

    /**
     * LAB cipher key {@code 00 01 .. 0F}, matching the OTA app's demo key set.
     *
     * <p>NON-SECRET demonstration value. A real KIc lives in an HSM and must
     * never be hard-coded, logged, or persisted.
     */
    public static byte[] labKic() {
        byte[] k = new byte[AES_KEY_LEN];
        for (int i = 0; i < AES_KEY_LEN; i++) {
            k[i] = (byte) i;
        }
        return k;
    }

    /** LAB integrity key {@code 10 11 .. 1F} — see {@link #labKic()} warning. */
    public static byte[] labKid() {
        byte[] k = new byte[AES_KEY_LEN];
        for (int i = 0; i < AES_KEY_LEN; i++) {
            k[i] = (byte) (0x10 + i);
        }
        return k;
    }

    /**
     * Walks the packing chain backwards.
     *
     * <p>Never throws for malformed input: every failure is reported through
     * {@link Result#failure()} so a lab capture is still written to disk.
     *
     * @param packet the merged SMS-PP body (CPL..ciphertext), UDH already stripped
     * @return what could be recovered, including the CAP when the chain unwinds fully
     */
    public Result verify(byte[] packet) {
        if (packet == null || packet.length < 2 + CLEAR_HEADER_AFTER_CPL) {
            return Result.failed("packet too short to hold a TS 102.225 header");
        }

        int cpl = ((packet[0] & 0xFF) << 8) | (packet[1] & 0xFF);
        int chl = packet[2] & 0xFF;
        int spi1 = packet[3] & 0xFF;
        int spi2 = packet[4] & 0xFF;
        int kicCoding = packet[5] & 0xFF;
        int kidCoding = packet[6] & 0xFF;
        byte[] tar = Arrays.copyOfRange(packet, 7, 7 + TAR_LEN);
        Header header = new Header(cpl, chl, spi1, spi2, kicCoding, kidCoding, hex(tar));

        if (cpl != packet.length - 2) {
            return Result.failed(header,
                    "CPL " + cpl + " does not match packet body " + (packet.length - 2));
        }

        byte[] body = Arrays.copyOfRange(packet, 2 + CLEAR_HEADER_AFTER_CPL, packet.length);
        if (body.length == 0) {
            return Result.failed(header, "no secured body after clear header");
        }

        boolean ciphered = (spi1 & SPI1_CIPHERED) != 0;
        byte[] plain;
        if (ciphered) {
            if (body.length % AES_BLOCK != 0) {
                return Result.failed(header,
                        "ciphered body " + body.length + " is not a multiple of " + AES_BLOCK);
            }
            try {
                plain = aesCbcDecrypt(kic, body);
            } catch (GeneralSecurityException e) {
                return Result.failed(header, "AES-CBC decipher under KIc failed: " + e);
            }
        } else {
            plain = body;
        }

        int integrity = spi1 & SPI1_INTEGRITY_MASK;
        int ccLen = switch (integrity) {
            case INTEGRITY_NONE -> 0;
            case INTEGRITY_RC -> RC_LEN;
            case INTEGRITY_CC -> CC_LEN;
            default -> 0;
        };
        if (integrity == INTEGRITY_DS) {
            return Result.failed(header, "SPI1 asks for a digital signature — unsupported in lab");
        }

        int minLen = CNTR_LEN + 1 + ccLen;
        if (plain.length < minLen) {
            return Result.failed(header, "secured body shorter than CNTR|PCNTR|CC");
        }
        byte[] cntr = Arrays.copyOfRange(plain, 0, CNTR_LEN);
        int padLen = plain[CNTR_LEN] & 0xFF;
        byte[] cc = Arrays.copyOfRange(plain, CNTR_LEN + 1, CNTR_LEN + 1 + ccLen);

        int appStart = minLen;
        int appEnd = plain.length - padLen;
        if (appEnd <= appStart) {
            return Result.failed(header,
                    "PCNTR " + padLen + " leaves no application message in " + plain.length + " octets");
        }
        byte[] appMessage = Arrays.copyOfRange(plain, appStart, appEnd);

        boolean ccValid = false;
        if (integrity == INTEGRITY_CC) {
            byte[] macInput = macInput(header, tar, cntr, (byte) padLen, appMessage, padLen);
            try {
                byte[] expected = Arrays.copyOf(aesCmac(kid, macInput), CC_LEN);
                ccValid = MessageDigest.isEqual(expected, cc);
            } catch (GeneralSecurityException e) {
                return Result.failed(header, "AES-CMAC under KID failed: " + e);
            }
            if (!ccValid) {
                return new Result(header, "cryptographic checksum mismatch — wrong KID or altered packet",
                        ciphered, true, false, hex(cntr), padLen, appMessage, null, null, 0);
            }
        }

        Script script = unwrapScript(appMessage);
        if (script.failure != null) {
            return new Result(header, script.failure, ciphered, integrity == INTEGRITY_CC, ccValid,
                    hex(cntr), padLen, appMessage, null, null, script.loadBlocks);
        }

        return new Result(header, null, ciphered, integrity == INTEGRITY_CC, ccValid,
                hex(cntr), padLen, appMessage, script.cap, sha256Hex(script.cap), script.loadBlocks);
    }

    /**
     * Splits a concatenated case-3 APDU script and rebuilds the {@code C4}
     * Load File Data Block value spread across its LOAD blocks.
     */
    private static Script unwrapScript(byte[] script) {
        ByteArrayOutputStream loadData = new ByteArrayOutputStream();
        int offset = 0;
        int loadBlocks = 0;
        while (offset < script.length) {
            if (offset + APDU_HEADER_LEN > script.length) {
                return Script.failed("truncated APDU header at offset " + offset, loadBlocks);
            }
            int ins = script[offset + 1] & 0xFF;
            int lc = script[offset + 4] & 0xFF;
            int dataStart = offset + APDU_HEADER_LEN;
            if (dataStart + lc > script.length) {
                return Script.failed("APDU at offset " + offset + " claims Lc " + lc
                        + " but only " + (script.length - dataStart) + " octets remain", loadBlocks);
            }
            if (ins == INS_LOAD) {
                loadData.write(script, dataStart, lc);
                loadBlocks++;
            }
            offset = dataStart + lc;
        }
        if (loadBlocks == 0) {
            return Script.failed("no GP LOAD (INS E8) block in the application message", 0);
        }

        byte[] value = loadData.toByteArray();
        if (value.length < 2 || (value[0] & 0xFF) != TAG_LOAD_FILE_DATA_BLOCK) {
            return Script.failed("LOAD data does not start with BER-TLV tag C4", loadBlocks);
        }
        int first = value[1] & 0xFF;
        int capLen;
        int capStart;
        if (first <= 0x7F) {
            capLen = first;
            capStart = 2;
        } else if (first == 0x81) {
            if (value.length < 3) {
                return Script.failed("truncated C4 length (81 form)", loadBlocks);
            }
            capLen = value[2] & 0xFF;
            capStart = 3;
        } else if (first == 0x82) {
            if (value.length < 4) {
                return Script.failed("truncated C4 length (82 form)", loadBlocks);
            }
            capLen = ((value[2] & 0xFF) << 8) | (value[3] & 0xFF);
            capStart = 4;
        } else if (first == 0x83) {
            if (value.length < 5) {
                return Script.failed("truncated C4 length (83 form)", loadBlocks);
            }
            capLen = ((value[2] & 0xFF) << 16) | ((value[3] & 0xFF) << 8) | (value[4] & 0xFF);
            capStart = 5;
        } else {
            return Script.failed("unsupported C4 length form 0x" + Integer.toHexString(first), loadBlocks);
        }
        if (capStart + capLen > value.length) {
            return Script.failed("C4 declares " + capLen + " octets but only "
                    + (value.length - capStart) + " were loaded", loadBlocks);
        }
        return Script.ok(Arrays.copyOfRange(value, capStart, capStart + capLen), loadBlocks);
    }

    /**
     * Rebuilds the sender's CC input: {@code CPL|CHL|SPI|KIc|KID|TAR|CNTR|PCNTR|app|pad}.
     * The CC field itself is excluded, and the pad is zero-filled.
     */
    private static byte[] macInput(Header h, byte[] tar, byte[] cntr, byte pcntr, byte[] app,
            int padLen) {
        ByteArrayOutputStream m = new ByteArrayOutputStream(
                2 + 1 + 2 + 2 + tar.length + cntr.length + 1 + app.length + padLen);
        m.write((h.cpl() >> 8) & 0xFF);
        m.write(h.cpl() & 0xFF);
        m.write(h.chl() & 0xFF);
        m.write(h.spi1() & 0xFF);
        m.write(h.spi2() & 0xFF);
        m.write(h.kicCoding() & 0xFF);
        m.write(h.kidCoding() & 0xFF);
        m.write(tar, 0, tar.length);
        m.write(cntr, 0, cntr.length);
        m.write(pcntr & 0xFF);
        m.write(app, 0, app.length);
        for (int i = 0; i < padLen; i++) {
            m.write(0x00);
        }
        return m.toByteArray();
    }

    private static byte[] aesCbcDecrypt(byte[] key, byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"),
                new IvParameterSpec(new byte[AES_BLOCK])); // IV = all zeros (TS 102.225 lab profile)
        return cipher.doFinal(input);
    }

    /**
     * AES-CMAC per RFC 4493, built from the JDK AES block cipher so the
     * simulator needs no BouncyCastle provider.
     */
    static byte[] aesCmac(byte[] key, byte[] message) throws GeneralSecurityException {
        Cipher aes = Cipher.getInstance("AES/ECB/NoPadding");
        aes.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));

        byte[] k1 = shiftLeftXorRb(aes.doFinal(new byte[AES_BLOCK]));
        byte[] k2 = shiftLeftXorRb(k1);

        boolean lastBlockComplete = message.length > 0 && message.length % AES_BLOCK == 0;
        int blocks = lastBlockComplete ? message.length / AES_BLOCK
                : (message.length / AES_BLOCK) + 1;

        byte[] last = new byte[AES_BLOCK];
        int lastOffset = (blocks - 1) * AES_BLOCK;
        int lastLen = message.length - lastOffset;
        System.arraycopy(message, lastOffset, last, 0, lastLen);
        if (lastBlockComplete) {
            xorInto(last, k1);
        } else {
            last[lastLen] = (byte) 0x80;
            xorInto(last, k2);
        }

        byte[] x = new byte[AES_BLOCK];
        for (int i = 0; i < blocks - 1; i++) {
            byte[] block = Arrays.copyOfRange(message, i * AES_BLOCK, (i + 1) * AES_BLOCK);
            xorInto(block, x);
            x = aes.doFinal(block);
        }
        xorInto(last, x);
        return aes.doFinal(last);
    }

    /** One-bit left shift, conditionally XORed with Rb=0x87 (RFC 4493 subkey step). */
    private static byte[] shiftLeftXorRb(byte[] in) {
        byte[] out = new byte[in.length];
        int carry = 0;
        for (int i = in.length - 1; i >= 0; i--) {
            int v = (in[i] & 0xFF) << 1;
            out[i] = (byte) ((v | carry) & 0xFF);
            carry = (v & 0x100) != 0 ? 1 : 0;
        }
        if (carry != 0) {
            out[out.length - 1] ^= (byte) 0x87;
        }
        return out;
    }

    private static void xorInto(byte[] target, byte[] other) {
        for (int i = 0; i < target.length; i++) {
            target[i] ^= other[i];
        }
    }

    static String sha256Hex(byte[] data) {
        try {
            return hex(MessageDigest.getInstance("SHA-256").digest(data));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private static String hex(byte[] data) {
        return HexFormat.of().formatHex(data);
    }

    private static byte[] resolveKey(String prop, byte[] fallback) {
        String v = System.getProperty(prop);
        if (v == null || v.isBlank()) {
            return fallback;
        }
        try {
            return HexFormat.of().parseHex(v.trim().replace(" ", "").toLowerCase());
        } catch (IllegalArgumentException e) {
            // Never echo the value — it may be real key material.
            throw new IllegalArgumentException(prop + " is not valid hex");
        }
    }

    private static byte[] validate(byte[] key, String name) {
        if (key == null || key.length != AES_KEY_LEN) {
            throw new IllegalArgumentException(name + " must be " + AES_KEY_LEN + " bytes");
        }
        return key.clone();
    }

    /** Clear-text TS 102.225 command header fields, as they appeared on the wire. */
    public record Header(int cpl, int chl, int spi1, int spi2, int kicCoding, int kidCoding,
            String tarHex) {

        static Header unknown() {
            return new Header(-1, -1, -1, -1, -1, -1, "");
        }

        boolean parsed() {
            return cpl >= 0;
        }
    }

    /**
     * Outcome of unwinding one secured packet.
     *
     * @param header      clear header fields, or an unknown placeholder
     * @param failure     first thing that went wrong, or {@code null} on success
     * @param ciphered    whether SPI1 indicated ciphering
     * @param ccPresent   whether SPI1 asked for a cryptographic checksum
     * @param ccValid     whether the recomputed CC matched
     * @param cntrHex     replay counter as sent (safe to log — not key material)
     * @param padLen      PCNTR value
     * @param appMessage  the recovered GP RAM APDU script, or {@code null}
     * @param cap         the recovered CAP, or {@code null} when unwinding stopped early
     * @param capSha256   SHA-256 of {@code cap}, or {@code null}
     * @param loadBlocks  number of GP LOAD APDUs seen
     */
    public record Result(Header header, String failure, boolean ciphered, boolean ccPresent,
            boolean ccValid, String cntrHex, int padLen, byte[] appMessage, byte[] cap,
            String capSha256, int loadBlocks) {

        static Result failed(String failure) {
            return failed(Header.unknown(), failure);
        }

        static Result failed(Header header, String failure) {
            return new Result(header, failure, false, false, false, "", 0, null, null, null, 0);
        }

        /** True when the CAP was recovered and integrity (if requested) held. */
        public boolean ok() {
            return failure == null && cap != null && (!ccPresent || ccValid);
        }

        /** One-line, key-free description suitable for logs and the simulator GUI. */
        public String summary() {
            if (!header.parsed()) {
                return "verify FAILED (" + failure + ")";
            }
            StringBuilder b = new StringBuilder(160);
            b.append("spi=").append(String.format("%02X%02X", header.spi1(), header.spi2()))
                    .append(" tar=").append(header.tarHex())
                    .append(" cntr=").append(cntrHex)
                    .append(" ciphered=").append(ciphered)
                    .append(" cc=").append(ccPresent ? (ccValid ? "VALID" : "INVALID") : "absent");
            if (appMessage != null) {
                b.append(" script=").append(appMessage.length).append("B loadBlocks=").append(loadBlocks);
            }
            if (cap != null) {
                b.append(" cap=").append(cap.length).append("B sha256=").append(capSha256);
            }
            if (failure != null) {
                b.append(" failure=").append(failure);
            }
            return b.toString();
        }
    }

    /** Intermediate result of APDU-script un-framing. */
    private record Script(byte[] cap, int loadBlocks, String failure) {

        static Script ok(byte[] cap, int loadBlocks) {
            return new Script(cap, loadBlocks, null);
        }

        static Script failed(String failure, int loadBlocks) {
            return new Script(null, loadBlocks, failure);
        }
    }
}
