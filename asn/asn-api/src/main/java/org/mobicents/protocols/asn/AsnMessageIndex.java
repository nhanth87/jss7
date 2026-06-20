/*
 * RestComm jSS7 - Flat ASN.1 Message Index
 *
 * Zero-GC metadata pool: primitive int[] arrays record TLV coordinates
 * without allocating per-tag Java objects.
 */
package org.mobicents.protocols.asn;

/**
 * Flat index of BER/DER TLV tags in a raw byte buffer.
 * Instances are intended for reuse via {@link AsnIndexPool}.
 */
public class AsnMessageIndex {

    public static final int MAX_TAGS = 256;

    public final int[] tags = new int[MAX_TAGS];
    public final int[] valueOffsets = new int[MAX_TAGS];
    public final int[] valueLengths = new int[MAX_TAGS];
    public final int[] depths = new int[MAX_TAGS];
    public final int[] parents = new int[MAX_TAGS];

    public int tagCount = 0;
    public byte[] rawBuffer;

    public void reset(byte[] buffer) {
        this.rawBuffer = buffer;
        this.tagCount = 0;
    }
}
