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

    private FlatAsnParser() {
    }

    public static void parseAll(byte[] buffer, int offset, int length, AsnMessageIndex index) throws AsnException {
        index.reset(buffer);
        int limit = offset + length;
        int cursor = offset;

        int[] parentStack = index.parentStack;
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
                throw new AsnException("ASN.1 tag count exceeds MAX_TAGS (" + AsnMessageIndex.MAX_TAGS + ")");
            }

            int currentTagIndex = index.tagCount;
            if (cursor >= limit) {
                throw new AsnException("Truncated ASN.1 buffer reading tag");
            }

            int firstTagByte = buffer[cursor++] & 0xFF;
            boolean isConstructed = (firstTagByte & Tag.PC_MASK) != 0;

            int tagNum = firstTagByte & Tag.TAG_MASK;
            if (tagNum == Tag.TAG_MASK) {
                int temp;
                tagNum = 0;
                do {
                    if (cursor >= limit) {
                        throw new AsnException("Truncated ASN.1 buffer reading multibyte tag");
                    }
                    temp = buffer[cursor++] & 0xFF;
                    tagNum = (tagNum << 7) | (temp & 0x7F);
                } while ((temp & 0x80) != 0);
            }

            if (cursor >= limit) {
                throw new AsnException("Truncated ASN.1 buffer reading length");
            }

            long lenPacked = readLengthAndSize(buffer, cursor, limit);
            int lengthFieldSize = (int) (lenPacked & 0xFFFFFFFFL);
            int valueLength = (int) (lenPacked >>> 32);
            boolean indefinite = valueLength == Tag.Indefinite_Length;
            cursor += lengthFieldSize;

            int parent;
            int depth;
            if (stackPointer == 0) {
                parent = -1;
                depth = 0;
            } else {
                parent = parentStack[stackPointer - 1];
                depth = stackPointer;
            }

            writeTagEntry(index, currentTagIndex, firstTagByte, tagNum, cursor,
                    indefinite ? Tag.Indefinite_Length : valueLength, parent, depth);
            linkChild(index, currentTagIndex, parent);
            index.tagCount++;

            if (indefinite) {
                if (isConstructed) {
                    pushParent(parentStack, stackPointer++, currentTagIndex);
                }
            } else if (isConstructed && valueLength > 0) {
                pushParent(parentStack, stackPointer++, currentTagIndex);
            } else {
                cursor += valueLength;
            }

            cursor = clampCursorToDefiniteParents(index, parentStack, stackPointer, cursor);
            stackPointer = popDefiniteParents(index, parentStack, stackPointer, cursor);
        }
    }

    private static void writeTagEntry(AsnMessageIndex index, int tagIndex, int firstTagByte, int tagNum,
            int valueOffset, int valueLength, int parent, int depth) {
        int base = tagIndex * AsnMessageIndex.STRIDE;
        index.entries[base + AsnMessageIndex.E_TAG] = firstTagByte;
        index.entries[base + AsnMessageIndex.E_TAG_NUM] = tagNum;
        index.entries[base + AsnMessageIndex.E_OFF] = valueOffset;
        index.entries[base + AsnMessageIndex.E_LEN] = valueLength;
        index.entries[base + AsnMessageIndex.E_PARENT] = parent;
        index.entries[base + AsnMessageIndex.E_DEPTH] = depth;

        index.tags[tagIndex] = firstTagByte;
        index.tagNumbers[tagIndex] = tagNum;
        index.valueOffsets[tagIndex] = valueOffset;
        index.valueLengths[tagIndex] = valueLength;
        index.parents[tagIndex] = parent;
        index.depths[tagIndex] = depth;

        int firstByte = firstTagByte & 0xFF;
        if (index.firstOccurrence[firstByte] < 0) {
            index.firstOccurrence[firstByte] = tagIndex;
        }
    }

    private static void linkChild(AsnMessageIndex index, int tagIndex, int parent) {
        if (parent >= 0) {
            int prev = index.lastChild[parent];
            if (prev < 0) {
                index.firstChild[parent] = tagIndex;
            } else {
                index.nextSibling[prev] = tagIndex;
            }
            index.lastChild[parent] = tagIndex;
            return;
        }

        if (index.rootFirstChild < 0) {
            index.rootFirstChild = tagIndex;
        } else {
            index.nextSibling[index.rootLastChild] = tagIndex;
        }
        index.rootLastChild = tagIndex;
    }

    private static void pushParent(int[] parentStack, int stackPointer, int tagIndex) throws AsnException {
        if (stackPointer >= AsnMessageIndex.MAX_DEPTH) {
            throw new AsnException("ASN.1 nesting depth exceeds MAX_DEPTH (" + AsnMessageIndex.MAX_DEPTH + ")");
        }
        parentStack[stackPointer] = tagIndex;
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
     * @return packed long: value length in high 32 bits, length-field size in low 32 bits.
     *         High 32 bits are {@link Tag#Indefinite_Length} for indefinite form.
     */
    private static long readLengthAndSize(byte[] buffer, int offset, int limit) throws AsnException {
        int b = buffer[offset] & 0xFF;
        if ((b & 0x80) == 0) {
            return ((long) b << 32) | 1L;
        }
        int numLengthBytes = b & 0x7F;
        if (numLengthBytes == 0) {
            return ((long) Tag.Indefinite_Length << 32) | 1L;
        }
        if (offset + numLengthBytes >= limit) {
            throw new AsnException("Truncated ASN.1 buffer reading long length");
        }
        int valueLength = 0;
        for (int i = 0; i < numLengthBytes; i++) {
            valueLength = (valueLength << 8) | (buffer[offset + 1 + i] & 0xFF);
        }
        return ((long) valueLength << 32) | (1L + numLengthBytes);
    }
}
