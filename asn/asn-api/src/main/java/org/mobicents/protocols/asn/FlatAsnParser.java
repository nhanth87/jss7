/*
 * RestComm jSS7 - Flat ASN.1 Parser
 *
 * Linear BER/DER scan with parent stack for constructed tags,
 * multi-byte tags, and indefinite-length EOC handling.
 */
package org.mobicents.protocols.asn;

/**
 * Global zero-GC ASN.1 indexer: one linear pass populates an {@link AsnMessageIndex}.
 */
public final class FlatAsnParser {

    private static final int MAX_DEPTH = 16;

    private FlatAsnParser() {
    }

    public static void parseAll(byte[] buffer, int offset, int length, AsnMessageIndex index) {
        index.reset(buffer);
        int limit = offset + length;
        int cursor = offset;

        int[] parentStack = new int[MAX_DEPTH];
        int stackPointer = 0;

        while (cursor < limit) {
            // EOC (0x00 0x00) is only valid inside indefinite-length constructed values.
            // Primitive octet strings (e.g. ISDN addresses) may contain 0x00 0x00 bytes.
            if (stackPointer > 0 && index.valueLengths[parentStack[stackPointer - 1]] == Tag.Indefinite_Length
                    && isEndOfContents(buffer, cursor, limit)) {
                cursor += 2;
                stackPointer = popIndefiniteParents(index, parentStack, stackPointer, cursor);
                continue;
            }

            if (index.tagCount >= AsnMessageIndex.MAX_TAGS) {
                return;
            }

            int currentTagIndex = index.tagCount;
            int firstTagByte = buffer[cursor++] & 0xFF;
            boolean isConstructed = (firstTagByte & Tag.PC_MASK) != 0;

            int tagNum = firstTagByte & Tag.TAG_MASK;
            if (tagNum == Tag.TAG_MASK) {
                int temp;
                tagNum = 0;
                do {
                    if (cursor >= limit) {
                        return;
                    }
                    temp = buffer[cursor++] & 0xFF;
                    tagNum = (tagNum << 7) | (temp & 0x7F);
                } while ((temp & 0x80) != 0);
            }

            if (cursor >= limit) {
                return;
            }

            int lenResult = readLength(buffer, cursor, limit);
            int valueLength = lenResult & 0x7FFFFFFF;
            boolean indefinite = (lenResult & 0x80000000) != 0;
            cursor += lengthFieldSize(buffer, cursor, limit);

            index.tags[currentTagIndex] = firstTagByte;
            index.valueOffsets[currentTagIndex] = cursor;
            index.valueLengths[currentTagIndex] = indefinite ? Tag.Indefinite_Length : valueLength;

            if (stackPointer == 0) {
                index.parents[currentTagIndex] = -1;
                index.depths[currentTagIndex] = 0;
            } else {
                index.parents[currentTagIndex] = parentStack[stackPointer - 1];
                index.depths[currentTagIndex] = stackPointer;
            }
            index.tagCount++;

            if (indefinite) {
                if (isConstructed) {
                    parentStack[stackPointer++] = currentTagIndex;
                }
            } else if (isConstructed && valueLength > 0) {
                parentStack[stackPointer++] = currentTagIndex;
            } else {
                cursor += valueLength;
            }

            cursor = clampCursorToDefiniteParents(index, parentStack, stackPointer, cursor);

            stackPointer = popDefiniteParents(index, parentStack, stackPointer, cursor);
        }
    }

    /**
     * MAP PrivateExtension and similar structures may embed raw non-TLV octets inside a constructed
     * value. Clamp cursor so a misread inner tag cannot desync indexing of outer siblings.
     */
    private static int clampCursorToDefiniteParents(AsnMessageIndex index, int[] parentStack, int stackPointer,
            int cursor) {
        while (stackPointer > 0) {
            int parentIdx = parentStack[stackPointer - 1];
            if (index.valueLengths[parentIdx] == Tag.Indefinite_Length) {
                break;
            }
            int parentEnd = index.valueOffsets[parentIdx] + index.valueLengths[parentIdx];
            if (cursor > parentEnd) {
                cursor = parentEnd;
            } else {
                break;
            }
        }
        return cursor;
    }

    private static boolean isEndOfContents(byte[] buffer, int cursor, int limit) {
        return cursor + 1 < limit && buffer[cursor] == 0 && buffer[cursor + 1] == 0;
    }

    private static int popIndefiniteParents(AsnMessageIndex index, int[] parentStack, int stackPointer, int cursor) {
        while (stackPointer > 0) {
            int parentIdx = parentStack[stackPointer - 1];
            if (index.valueLengths[parentIdx] == Tag.Indefinite_Length) {
                stackPointer--;
            } else {
                int parentValueEnd = index.valueOffsets[parentIdx] + index.valueLengths[parentIdx];
                if (cursor >= parentValueEnd) {
                    stackPointer--;
                } else {
                    break;
                }
            }
        }
        return stackPointer;
    }

    private static int popDefiniteParents(AsnMessageIndex index, int[] parentStack, int stackPointer, int cursor) {
        while (stackPointer > 0) {
            int parentIdx = parentStack[stackPointer - 1];
            if (index.valueLengths[parentIdx] == Tag.Indefinite_Length) {
                break;
            }
            int parentValueEnd = index.valueOffsets[parentIdx] + index.valueLengths[parentIdx];
            if (cursor >= parentValueEnd) {
                stackPointer--;
            } else {
                break;
            }
        }
        return stackPointer;
    }

    /**
     * @return length in low 31 bits; bit 31 set for indefinite form
     */
    private static int readLength(byte[] buffer, int offset, int limit) {
        if (offset >= limit) {
            return 0;
        }
        int b = buffer[offset] & 0xFF;
        if ((b & 0x80) == 0) {
            return b;
        }
        int numLengthBytes = b & 0x7F;
        if (numLengthBytes == 0) {
            return 0x80000000;
        }
        int valueLength = 0;
        for (int i = 0; i < numLengthBytes; i++) {
            if (offset + 1 + i >= limit) {
                return 0;
            }
            valueLength = (valueLength << 8) | (buffer[offset + 1 + i] & 0xFF);
        }
        return valueLength;
    }

    private static int lengthFieldSize(byte[] buffer, int offset, int limit) {
        if (offset >= limit) {
            return 0;
        }
        int b = buffer[offset] & 0xFF;
        if ((b & 0x80) == 0) {
            return 1;
        }
        int numLengthBytes = b & 0x7F;
        if (numLengthBytes == 0) {
            return 1;
        }
        return 1 + numLengthBytes;
    }
}
