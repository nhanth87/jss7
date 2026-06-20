/*
 * RestComm jSS7 - Flat ASN.1 Reader Helper
 */
package org.mobicents.protocols.asn;

/**
 * Zero-GC getters over a populated {@link AsnMessageIndex}.
 */
public final class AsnReaderHelper {

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
        int count = 0;
        for (int i = 0; i < index.tagCount; i++) {
            if (index.parents[i] == parentIndex && index.tags[i] == targetChildTag) {
                if (count == occurrence) {
                    return i;
                }
                count++;
            }
        }
        return -1;
    }

    public static long readInteger(AsnMessageIndex index, int tagIndex) {
        int offset = index.valueOffsets[tagIndex];
        int len = index.valueLengths[tagIndex];
        if (len <= 0) {
            return 0;
        }
        long value = 0;
        for (int i = 0; i < len; i++) {
            value = (value << 8) | (index.rawBuffer[offset + i] & 0xFF);
        }
        return value;
    }

    public static long readIntegerAtTag(AsnMessageIndex index, int targetTag) {
        int idx = findTagIndex(index, targetTag);
        if (idx == -1) {
            return -1;
        }
        return readInteger(index, idx);
    }

    public static OctetStringView readOctetStringView(AsnMessageIndex index, int tagIndex) {
        return new OctetStringView(index.rawBuffer, index.valueOffsets[tagIndex], index.valueLengths[tagIndex]);
    }

    public static OctetStringView readOctetStringViewAtTag(AsnMessageIndex index, int targetTag) {
        int idx = findTagIndex(index, targetTag);
        if (idx == -1) {
            return null;
        }
        return readOctetStringView(index, idx);
    }

    /** Returns the index of the Nth direct child (0-based) under {@code parentIndex}. */
    public static int findNthChild(AsnMessageIndex index, int parentIndex, int occurrence) {
        int count = 0;
        for (int i = 0; i < index.tagCount; i++) {
            if (index.parents[i] == parentIndex) {
                if (count == occurrence) {
                    return i;
                }
                count++;
            }
        }
        return -1;
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

    public static int getTagNumber(int firstTagByte) {
        return firstTagByte & Tag.TAG_MASK;
    }
}
