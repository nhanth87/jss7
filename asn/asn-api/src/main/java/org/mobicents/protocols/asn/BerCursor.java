package org.mobicents.protocols.asn;

import java.util.concurrent.atomic.LongAdder;

/**
 * Cursor-based BER/DER decoder — zero-copy, minimal allocation.
 *
 * <h3>Performance design (v4 — fixbercursor.md):</h3>
 * <ul>
 *   <li><b>Dual-mode backing:</b> direct {@code heapBuf[]} fast-path (95% of usage)
 *       or {@link AsnBufferBackend} fallback (Netty ByteBuf).</li>
 *   <li><b>Array-based ThreadLocal pool:</b> 16 slots, O(1) acquire/release,
 *       no synchronized or CAS overhead.</li>
 *   <li><b>Inline nextByte()/byteAt():</b> single if-check on the heap path,
 *       JIT inlines both branches.</li>
 *   <li><b>Bulk copy:</b> {@link #getOctetString()} uses {@code System.arraycopy}
 *       on heap path and {@link AsnBufferBackend#copyTo} on ByteBuf path.</li>
 *   <li><b>Pre-allocated child cursor:</b> no ThreadLocal lookup for nested SEQUENCE.</li>
 * </ul>
 *
 * <pre>{@code
 * BerCursor c = BerCursor.wrapHeap(data, 0, data.length);
 * while (c.hasMore()) {
 *     c.readTag();
 *     switch (c.tag()) {
 *         case 0x02: int val = c.readInt32(); break;
 *         case 0x04: byte[] octets = c.getOctetString(); break;
 *         case 0x30: BerCursor sub = c.openConstructed(); ... sub.release(); break;
 *     }
 * }
 * c.release();
 * }</pre>
 */
public final class BerCursor {

    // ── Per-thread reusable scratch cursor (zero-alloc hot path) ─────
    // v5: array pool + boxed size counter removed — they churned the child
    // cursor (release() nulled child, forcing openConstructed() to re-alloc
    // every message) and cost ~6 ThreadLocal ops per wrap+release. A single
    // reused per-thread cursor keeps the child chain warm and allocates zero.
    private static final ThreadLocal<BerCursor> TL_SCRATCH =
        ThreadLocal.withInitial(BerCursor::new);

    // Telemetry gate — disabled by default (zero overhead on hot path)
    // Enable with: -Dasn.telemetry.enabled=true
    private static final boolean TELEMETRY =
        Boolean.parseBoolean(System.getProperty("asn.telemetry.enabled", "false"));

    // Observatory counters — cumulative since JVM start (read by monitoring/TUI, no hot-path
    // contention). These cover BOTH ASN.1 subsystems: the zero-copy BerCursor decode path
    // AND the legacy AsnInputStream/AsnOutputStream, which call recordDecode()/recordEncode().
    // Consumers should report per-second RATES from deltas of these cumulative values — there
    // is deliberately no "active/in-flight" gauge (the old one was unreliable: the hot path
    // wraps without release(), so it only ever grew and equalled the total).
    private static final LongAdder DECODED = new LongAdder();
    private static final LongAdder ENCODED = new LongAdder();

    /** Cumulative ASN.1 decode operations (BerCursor wraps + AsnInputStream root decodes). */
    public static long decodeCount() { return TELEMETRY ? DECODED.sum() : 0; }

    /** Cumulative ASN.1 encode operations (AsnOutputStream encodes). */
    public static long encodeCount() { return TELEMETRY ? ENCODED.sum() : 0; }

    /** Whether observatory counters are active. Check before reading decode/encodeCount. */
    public static boolean isTelemetryEnabled() { return TELEMETRY; }

    /** Record one decode op — called by the legacy AsnInputStream root decode entry points. */
    public static void recordDecode() { if (TELEMETRY) DECODED.increment(); }

    /** Record one encode op — called by the AsnOutputStream encode entry point. */
    public static void recordEncode() { if (TELEMETRY) ENCODED.increment(); }

    private static BerCursor acquire() {
        if (TELEMETRY) DECODED.increment();
        return TL_SCRATCH.get();
    }

    // ── Dual-mode backing ────────────────────────────────────────────
    // Khi heap-backed: dùng trực tiếp, KHÔNG qua interface
    private byte[]           heapBuf;   // non-null nếu heap path
    private AsnBufferBackend backend;   // non-null nếu ByteBuf path
    private int base;
    private int limit;
    private int pos;

    /** Hard cap on constructed nesting (SEQUENCE-in-SEQUENCE). */
    public static final int MAX_NESTING = 32;
    /** Hard cap on TLV count at one cursor level. */
    public static final int MAX_TAGS = 256;
    /** High-tag-number encodings longer than this are treated as unterminated. */
    public static final int MAX_HIGH_TAG_OCTETS = 6;

    // ── Cached TLV ───────────────────────────────────────────────────
    private int     tagClass;
    private boolean primitive;
    private int     tag;
    private int     valueOffset;
    private int     valueLength;
    private int     nesting;
    private int     tagCount;

    // ── Pre-allocated child cursor (eliminates ThreadLocal on inner levels) ──
    private BerCursor child;

    // ==================================================================
    // PUBLIC FACTORY
    // ==================================================================

    /**
     * Wrap a heap {@code byte[]} — fast path, no virtual calls. Backed by the
     * per-thread scratch cursor (zero allocation after warmup).
     */
    public static BerCursor wrapHeap(byte[] data, int offset, int length) {
        return acquire().resetHeap(data, offset, length);
    }

    /**
     * Reset THIS cursor onto a heap {@code byte[]} — zero allocation, no
     * ThreadLocal lookup. Preferred for hot paths that own a reusable cursor
     * (e.g. a thread-local TCAP decode cursor). Returns {@code this} for chaining.
     */
    public BerCursor resetHeap(byte[] data, int offset, int length) {
        if (data == null)
            throw new NullPointerException("data");
        checkRegion(offset, length, data.length, "heap buffer");
        this.heapBuf = data;
        this.backend = null;
        this.base    = offset;
        this.limit   = offset + length;
        this.pos     = offset;
        this.nesting = 0;
        this.tagCount = 0;
        clearTlv();
        return this;
    }

    /**
     * Reset THIS cursor onto a {@link AsnBufferBackend} (Netty ByteBuf) region —
     * zero allocation, no ThreadLocal lookup. Returns {@code this} for chaining.
     */
    public BerCursor resetByteBuf(AsnBufferBackend backend, int offset, int length) {
        if (backend == null)
            throw new NullPointerException("backend");
        checkRegion(offset, length, backend.getWriterIndex(), "buffer backend");
        this.heapBuf = null;
        this.backend = backend;
        this.base    = offset;
        this.limit   = offset + length;
        this.pos     = offset;
        this.nesting = 0;
        this.tagCount = 0;
        clearTlv();
        return this;
    }

    private static void checkRegion(int offset, int length, int capacity, String source) {
        if (offset < 0 || length < 0 || offset > capacity || length > capacity - offset)
            throw new IndexOutOfBoundsException("Invalid " + source + " region: offset=" + offset
                    + ", length=" + length + ", limit=" + capacity);
    }

    private void clearTlv() {
        this.tagClass = 0;
        this.primitive = false;
        this.tag = 0;
        this.valueOffset = pos;
        this.valueLength = 0;
    }

    /**
     * Wrap a Netty-backed buffer via {@link AsnBufferBackend} — ByteBuf path.
     */
    public static BerCursor wrapByteBuf(AsnBufferBackend backend, int offset, int length) {
        return acquire().resetByteBuf(backend, offset, length);
    }

    /**
     * Convenience: wrap heap array with default offset=0.
     * @deprecated prefer {@link #wrapHeap(byte[], int, int)} for clarity
     */
    @Deprecated
    public static BerCursor wrap(byte[] data, int offset, int length) {
        return wrapHeap(data, offset, length);
    }

    /**
     * Convenience: wrap backend.
     * @deprecated prefer {@link #wrapByteBuf(AsnBufferBackend, int, int)} for clarity
     */
    @Deprecated
    public static BerCursor wrap(AsnBufferBackend backend, int offset, int length) {
        return wrapByteBuf(backend, offset, length);
    }

    /** Return this cursor to the thread-local pool. */
    public void release() {
        this.heapBuf = null;
        this.backend = null;
        // Keep `child`: the sub-cursor chain is reused across messages. Nulling
        // it here was the root cause of per-message re-allocation (v4 bug).
        // No pooling: the cursor is either the per-thread scratch (reused via
        // TL_SCRATCH) or a caller-owned instance (reused via resetHeap/reset).
    }

    // ==================================================================
    // INLINE BYTE ACCESS — no virtual call on heap path
    // ==================================================================

    @SuppressWarnings("nothing")
    private int nextByte() throws AsnException {
        if (pos >= limit)
            throw new AsnException("Unexpected end of BER input at offset " + pos);
        if (heapBuf != null)
            return heapBuf[pos++] & 0xFF;   // ← direct array, JIT inline
        return backend.readByte(pos++) & 0xFF;
    }

    @SuppressWarnings("nothing")
    private int byteAt(int i) {
        if (heapBuf != null)
            return heapBuf[i] & 0xFF;
        return backend.readByte(i) & 0xFF;
    }

    // ==================================================================
    // READ TLV — fully inline when heap-backed
    // ==================================================================

    public void readTag() throws AsnException {
        if (++tagCount > MAX_TAGS)
            throw new AsnException("BER tag count exceeds " + MAX_TAGS);
        int b = nextByte();
        tagClass  = (b >> 6) & 0x03;
        primitive = (b & 0x20) == 0;
        tag       = b & 0x1F;
        if (tag == 0x1F) {
            tag = 0;
            int octets = 0;
            do {
                if (octets >= MAX_HIGH_TAG_OCTETS)
                    throw new AsnException("Unterminated BER high-tag-number encoding");
                b = nextByte();
                if (octets++ == 0 && (b & 0x7F) == 0)
                    throw new AsnException("Malformed BER high-tag-number encoding");
                if (tag > (Integer.MAX_VALUE >>> 7))
                    throw new AsnException("BER tag number overflow");
                tag = (tag << 7) | (b & 0x7F);
            } while ((b & 0x80) != 0);
            if (tag < 0x1F)
                throw new AsnException("Non-minimal BER high-tag-number encoding");
        }
        b = nextByte();
        if (b <= 0x7F) {
            valueLength = b;
        } else {
            int n = b & 0x7F;
            if (n == 0) {
                if (primitive)
                    throw new AsnException("Indefinite length is illegal for primitive BER values");
                throw new AsnException("Indefinite constructed BER values are not supported");
            }
            if (n > 4) throw new AsnException("BER length field too large: " + n + " bytes");
            long decodedLength = 0;
            for (int i = 0; i < n; i++)
                decodedLength = (decodedLength << 8) | nextByte();
            if (decodedLength > Integer.MAX_VALUE)
                throw new AsnException("BER value length exceeds supported range: " + decodedLength);
            valueLength = (int) decodedLength;
        }
        valueOffset = pos;
        if (valueLength > limit - valueOffset)
            throw new AsnException("BER value exceeds containing input: length=" + valueLength
                    + ", remaining=" + (limit - valueOffset));
    }

    // ==================================================================
    // ACCESSORS
    // ==================================================================

    public int tagClass()      { return tagClass; }
    public int tag()           { return tag; }
    public boolean isPrimitive() { return primitive; }
    public int valueLength()   { return valueLength; }
    public int valueOffset()   { return valueOffset; }
    public int position()      { return pos; }

    /** Backing heap buffer (non-null on heap path), for zero-copy value views. */
    public byte[] heapBuffer() { return heapBuf; }
    /** Backing ByteBuf backend (non-null on ByteBuf path), for zero-copy value views. */
    public AsnBufferBackend backend() { return backend; }

    // ==================================================================
    // ZERO-COPY PIPELINE — forward raw bytes without copying
    // ==================================================================

    /**
     * Returns a BerSlice covering just the VALUE bytes of the current TLV.
     * Zero-copy — points directly into the original backing buffer.
     *
     * <p>Call after {@link #readTag()}. The returned slice references the
     * underlying buffer with no allocation. Use for lazy decode or forwarding.</p>
     */
    public BerSlice getValueSlice() {
        pos = valueOffset + valueLength;
        if (heapBuf != null)
            return new BerSlice(heapBuf, valueOffset, valueLength);
        return new BerSlice(backend, valueOffset, valueLength);
    }

    /**
     * Returns a BerSlice from {@code startPos} to current position.
     * Use with {@link #position()} to capture raw bytes between markers.
     *
     * <pre>{@code
     * int start = c.position();
     * c.readTag();
     * // ... decode fields ...
     * BerSlice rawTlv = c.sliceFrom(start);  // captures entire TLV
     * }</pre>
     */
    public BerSlice sliceFrom(int startPos) {
        if (startPos < base || startPos > pos)
            throw new IndexOutOfBoundsException("Invalid BER slice start: " + startPos);
        int len = pos - startPos;
        if (heapBuf != null)
            return new BerSlice(heapBuf, startPos, len);
        return new BerSlice(backend, startPos, len);
    }



    /**
     * Skip the current value AND advance past it. Equivalent to skipValue()
     * but returns the cursor for chaining.
     */
    public BerCursor skip() { skipValue(); return this; }

    // ==================================================================
    // NAVIGATION
    // ==================================================================

    public boolean hasMore()    { return pos < limit; }
    public int remaining()      { return limit - pos; }

    /**
     * Opens a sub-cursor sharing the same underlying data.
     * Uses pre-allocated child cursor — no ThreadLocal lookup on inner levels.
     */
    public BerCursor openConstructed() throws AsnException {
        if (this.nesting >= MAX_NESTING)
            throw new AsnException("BER nesting exceeds " + MAX_NESTING);
        BerCursor body = this.child;
        if (body == null) {
            body = new BerCursor();
            this.child = body;
        }
        body.heapBuf  = this.heapBuf;
        body.backend  = this.backend;
        body.base     = valueOffset;
        body.limit    = valueOffset + valueLength;
        body.pos      = valueOffset;
        body.nesting  = this.nesting + 1;
        body.tagCount = 0;
        body.clearTlv();
        return body;
    }

    public void skipValue() {
        pos = valueOffset + valueLength;
    }

    // ==================================================================
    // READ PRIMITIVES — INTEGER (switch-based fast path)
    // ==================================================================

    public int readInt32() {
        int len = valueLength;
        int end = valueOffset + len;
        int result;
        if (heapBuf != null) {
            int vo = valueOffset;
            switch (len) {
                case 4:
                    result = (heapBuf[vo] << 24)
                           | ((heapBuf[vo + 1] & 0xFF) << 16)
                           | ((heapBuf[vo + 2] & 0xFF) << 8)
                           |  (heapBuf[vo + 3] & 0xFF);
                    break;
                case 3: {
                    result = ((heapBuf[vo] & 0xFF) << 16)
                           | ((heapBuf[vo + 1] & 0xFF) << 8)
                           |  (heapBuf[vo + 2] & 0xFF);
                    if ((heapBuf[vo] & 0x80) != 0) result |= 0xFF000000;
                    break;
                }
                case 2: {
                    result = ((heapBuf[vo] & 0xFF) << 8) | (heapBuf[vo + 1] & 0xFF);
                    if ((heapBuf[vo] & 0x80) != 0) result |= 0xFFFF0000;
                    break;
                }
                case 1:
                    result = heapBuf[vo];
                    break;
                default:
                    result = 0;
                    for (int i = vo; i < end; i++)
                        result = (result << 8) | (heapBuf[i] & 0xFF);
                    if (len > 4 && (heapBuf[vo] & 0x80) != 0)
                        result |= (-1 << (len * 8));
                    break;
            }
        } else {
            result = 0;
            for (int i = valueOffset; i < end; i++)
                result = (result << 8) | (backend.readByte(i) & 0xFF);
            if (len > 0 && len <= 4) {
                if ((backend.readByte(valueOffset) & 0x80) != 0)
                    result |= (-1 << (len * 8));
            }
        }
        pos = end;
        return result;
    }

    // ==================================================================
    // READ PRIMITIVES — LONG INTEGER
    // ==================================================================

    public long readInt64() {
        long result = 0;
        int end = valueOffset + valueLength;
        if (heapBuf != null) {
            for (int i = valueOffset; i < end; i++)
                result = (result << 8) | (heapBuf[i] & 0xFF);
        } else {
            for (int i = valueOffset; i < end; i++)
                result = (result << 8) | (backend.readByte(i) & 0xFF);
        }
        if (valueLength > 0 && valueLength <= 8) {
            if (heapBuf != null) {
                if ((heapBuf[valueOffset] & 0x80) != 0)
                    result |= (-1L << (valueLength * 8));
            } else {
                if ((backend.readByte(valueOffset) & 0x80) != 0)
                    result |= (-1L << (valueLength * 8));
            }
        }
        pos = end;
        return result;
    }

    // ==================================================================
    // READ PRIMITIVES — OCTET STRING
    // ==================================================================

    /**
     * Lấy slice — heap path: trỏ trực tiếp vào heapBuf, không copy.
     */
    public BerSlice getOctetStringSlice() {
        BerSlice slice;
        if (heapBuf != null) {
            slice = new BerSlice(heapBuf, valueOffset, valueLength);
        } else {
            slice = new BerSlice(backend, valueOffset, valueLength);
        }
        pos = valueOffset + valueLength;
        return slice;
    }

    /**
     * getOctetString: System.arraycopy thay vì byte-by-byte.
     * Heap path: {@code System.arraycopy}. ByteBuf path: {@code backend.copyTo()}.
     */
    public byte[] getOctetString() {
        byte[] out = new byte[valueLength];
        if (heapBuf != null) {
            System.arraycopy(heapBuf, valueOffset, out, 0, valueLength);
        } else {
            backend.copyTo(valueOffset, out, 0, valueLength);
        }
        pos = valueOffset + valueLength;
        return out;
    }
}
