package org.restcomm.protocols.ss7.tcap;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import org.restcomm.protocols.ss7.tcap.api.TCAPException;

/**
 * Allocates and releases local transaction ids within a configured range.
 * <p>
 * Small ranges use a {@link BitSet}; large ranges use a sparse set so parallel
 * functional tests do not allocate multi-gigabit structures for the default range.
 */
public class DialogIdAllocator {

    private static final int BITSET_RANGE_LIMIT = 65536;

    private long rangeStart;
    private long rangeEnd;
    private long rangeSize;
    private BitSet usedBits;
    private Set<Long> usedSparse;
    private int usedCount;
    private long searchCursor;

    public DialogIdAllocator(long rangeStart, long rangeEnd) {
        configure(rangeStart, rangeEnd);
    }

    public synchronized void configure(long rangeStart, long rangeEnd) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.rangeSize = rangeEnd - rangeStart + 1;
        this.searchCursor = 0;
        this.usedCount = 0;
        if (this.rangeSize <= BITSET_RANGE_LIMIT) {
            this.usedBits = new BitSet((int) this.rangeSize);
            this.usedSparse = null;
        } else {
            this.usedBits = null;
            this.usedSparse = new HashSet<Long>();
        }
    }

    public synchronized Long allocate() throws TCAPException {
        if (usedCount >= rangeSize) {
            throw new TCAPException("No available transaction id in configured range");
        }

        if (usedBits != null) {
            int size = (int) rangeSize;
            for (int i = 0; i < size; i++) {
                int index = (int) ((searchCursor + i) % size);
                if (!usedBits.get(index)) {
                    usedBits.set(index);
                    usedCount++;
                    searchCursor = (index + 1L) % size;
                    return rangeStart + index;
                }
            }
        } else {
            for (long i = 0; i < rangeSize; i++) {
                long index = (searchCursor + i) % rangeSize;
                long id = rangeStart + index;
                if (usedSparse.add(id)) {
                    usedCount++;
                    searchCursor = (index + 1L) % rangeSize;
                    return id;
                }
            }
        }

        throw new TCAPException("No available transaction id in configured range");
    }

    public synchronized boolean tryReserve(long id) {
        int index = toIndex(id);
        if (index < 0 || isUsed(index)) {
            return false;
        }
        markIndexUsed(index);
        return true;
    }

    public synchronized void release(long id) {
        int index = toIndex(id);
        if (index >= 0 && isUsed(index)) {
            markIndexFree(index);
        }
    }

    public synchronized void markUsed(long id) {
        int index = toIndex(id);
        if (index >= 0 && !isUsed(index)) {
            markIndexUsed(index);
        }
    }

    public synchronized void reset() {
        if (usedBits != null) {
            usedBits.clear();
        }
        if (usedSparse != null) {
            usedSparse.clear();
        }
        usedCount = 0;
        searchCursor = 0;
    }

    public synchronized int getUsedCount() {
        return usedCount;
    }

    private int toIndex(long id) {
        if (id < rangeStart || id > rangeEnd) {
            return -1;
        }
        return (int) (id - rangeStart);
    }

    private boolean isUsed(int index) {
        if (usedBits != null) {
            return usedBits.get(index);
        }
        return usedSparse.contains(rangeStart + index);
    }

    private void markIndexUsed(int index) {
        if (usedBits != null) {
            usedBits.set(index);
        } else {
            usedSparse.add(rangeStart + index);
        }
        usedCount++;
    }

    private void markIndexFree(int index) {
        if (usedBits != null) {
            usedBits.clear(index);
        } else {
            usedSparse.remove(rangeStart + index);
        }
        usedCount--;
    }
}
