/*
 * RestComm jSS7 - Flat ASN.1 Reader Helper
 */
package org.mobicents.protocols.asn;

/**
 * Zero-GC getters over a populated {@link AsnMessageIndex}.
 */
public final class AsnReaderHelper {

    @Deprecated
    public static final class OctetStringView {
        public final byte[] buffer;
        public final int offset;
        public final int length;

        public OctetStringView(byte[] buffer, int offset, int length) {
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
        }
    }

    private AsnReaderHelper() {
    }

    public static int findTagIndex(AsnMessageIndex index, int targetTag) {
        int candidate = index.firstOccurrence[targetTag & 0xFF];
        if (candidate >= 0 && index.tags[candidate] == targetTag) {
            return candidate;
        }
        for (int i = 0; i < index.tagCount; i++) {
            if (index.tags[i] == targetTag) {
                return i;
            }
        }
        return -1;
    }

    public static int findChildTag(AsnMessageIndex index, int parentIndex, int targetChildTag) {
        return findNthChildTag(index, parentIndex, targetChildTag, 0);
    }

    public static int findNthChildTag(AsnMessageIndex index, int parentIndex, int targetChildTag, int occurrence) {
        int child = childHead(index, parentIndex);
        int count = 0;
        while (child >= 0) {
            if (index.tags[child] == targetChildTag) {
                if (count == occurrence) {
                    return child;
                }
                count++;
            }
            child = index.nextSibling[child];
        }
        return -1;
    }

    /** Returns the index of the Nth direct child (0-based) under {@code parentIndex}. */
    public static int findNthChild(AsnMessageIndex index, int parentIndex, int occurrence) {
        int child = childHead(index, parentIndex);
        int count = 0;
        while (child >= 0) {
            if (count == occurrence) {
                return child;
            }
            count++;
            child = index.nextSibling[child];
        }
        return -1;
    }

    private static int childHead(AsnMessageIndex index, int parentIndex) {
        return parentIndex < 0 ? index.rootFirstChild : index.firstChild[parentIndex];
    }

    public static long readInteger(AsnMessageIndex index, int tagIndex) {
        int offset = index.valueOffsets[tagIndex];
        int len = index.valueLengths[tagIndex];
        if (len <= 0) {
            return 0;
        }
        byte[] buf = index.rawBuffer;
        switch (len) {
            case 1:
                return buf[offset] & 0xFFL;
            case 2:
                return ((buf[offset] & 0xFFL) << 8) | (buf[offset + 1] & 0xFFL);
            case 4:
                return ((buf[offset] & 0xFFL) << 24)
                        | ((buf[offset + 1] & 0xFFL) << 16)
                        | ((buf[offset + 2] & 0xFFL) << 8)
                        | (buf[offset + 3] & 0xFFL);
            default:
                long value = 0;
                for (int i = 0; i < len; i++) {
                    value = (value << 8) | (buf[offset + i] & 0xFF);
                }
                return value;
        }
    }

    public static long readIntegerAtTag(AsnMessageIndex index, int targetTag) {
        int idx = findTagIndex(index, targetTag);
        if (idx == -1) {
            return -1;
        }
        return readInteger(index, idx);
    }

    public static int readOctetOffset(AsnMessageIndex index, int tagIndex) {
        return index.valueOffsets[tagIndex];
    }

    public static int readOctetLength(AsnMessageIndex index, int tagIndex) {
        return index.valueLengths[tagIndex];
    }

    public static void readOctetString(AsnMessageIndex index, int tagIndex, int[] out) {
        out[0] = index.valueOffsets[tagIndex];
        out[1] = index.valueLengths[tagIndex];
    }

    @Deprecated
    public static OctetStringView readOctetStringView(AsnMessageIndex index, int tagIndex) {
        return new OctetStringView(index.rawBuffer, readOctetOffset(index, tagIndex), readOctetLength(index, tagIndex));
    }

    @Deprecated
    public static OctetStringView readOctetStringViewAtTag(AsnMessageIndex index, int targetTag) {
        int idx = findTagIndex(index, targetTag);
        if (idx == -1) {
            return null;
        }
        return readOctetStringView(index, idx);
    }

    public static int getTagClass(int firstTagByte) {
        return (firstTagByte & Tag.CLASS_MASK) >> 6;
    }

    public static boolean isPrimitive(int firstTagByte) {
        return (firstTagByte & Tag.PC_MASK) == 0;
    }

    public static boolean isConstructed(int firstTagByte) {
        return !isPrimitive(firstTagByte);
    }

    /** Tag number from first tag byte only (single-byte tags). */
    public static int getTagNumber(int firstTagByte) {
        return firstTagByte & Tag.TAG_MASK;
    }

    /** Full tag number including multibyte BER tag encoding. */
    public static int getTagNumber(AsnMessageIndex index, int tagIndex) {
        return index.tagNumbers[tagIndex];
    }
}
