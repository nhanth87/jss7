package org.restcomm.protocols.ss7.scheduler.w2.infinispan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Replicated ownership lease. Epoch is a fencing token, not a wall-clock version.
 *
 * <p>Values are encoded with a compact binary layout (no Java serialization) so Infinispan
 * stores only opaque bytes via {@link #encode()} / {@link #decode(byte[])}.</p>
 */
public record W2Lease(String ownerNodeId, long epoch, long expiresAtEpochMs) {

    private static final byte VERSION = 1;

    public W2Lease {
        if (ownerNodeId == null || ownerNodeId.isBlank()) {
            throw new IllegalArgumentException("ownerNodeId must not be blank");
        }
        if (epoch <= 0 || expiresAtEpochMs < 0) {
            throw new IllegalArgumentException("invalid lease epoch or expiry");
        }
    }

    public boolean isHeldBy(String nodeId, long expectedEpoch, long nowEpochMs) {
        return ownerNodeId.equals(nodeId) && epoch == expectedEpoch && expiresAtEpochMs > nowEpochMs;
    }

    public boolean isExpired(long nowEpochMs) {
        return expiresAtEpochMs <= nowEpochMs;
    }

    /** Binary form stored in the Infinispan lease cache. */
    public byte[] encode() {
        byte[] ownerBytes = ownerNodeId.getBytes(StandardCharsets.UTF_8);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(1 + 2 + ownerBytes.length + 16);
                DataOutputStream out = new DataOutputStream(bos)) {
            out.writeByte(VERSION);
            out.writeShort(ownerBytes.length);
            out.write(ownerBytes);
            out.writeLong(epoch);
            out.writeLong(expiresAtEpochMs);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("encode W2Lease", e);
        }
    }

    public static W2Lease decode(byte[] encoded) {
        if (encoded == null || encoded.length < 1 + 2 + 16) {
            throw new IllegalArgumentException("encoded lease too short");
        }
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(encoded))) {
            int version = in.readUnsignedByte();
            if (version != VERSION) {
                throw new IllegalArgumentException("unsupported W2Lease encoding version: " + version);
            }
            int ownerLen = in.readUnsignedShort();
            byte[] ownerBytes = in.readNBytes(ownerLen);
            if (ownerBytes.length != ownerLen) {
                throw new IllegalArgumentException("truncated ownerNodeId in W2Lease encoding");
            }
            long epoch = in.readLong();
            long expiresAtEpochMs = in.readLong();
            return new W2Lease(new String(ownerBytes, StandardCharsets.UTF_8), epoch, expiresAtEpochMs);
        } catch (IOException e) {
            throw new UncheckedIOException("decode W2Lease", e);
        }
    }
}
