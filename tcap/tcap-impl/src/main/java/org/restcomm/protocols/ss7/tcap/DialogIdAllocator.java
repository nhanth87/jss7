package org.restcomm.protocols.ss7.tcap;

import java.util.BitSet;

import org.restcomm.protocols.ss7.tcap.api.TCAPException;

/**
 * Allocates and releases local transaction ids within a configured range using a BitSet.
 */
public class DialogIdAllocator {

    private long rangeStart;
    private long rangeEnd;
    private int rangeSize;
    private BitSet used;
    private long searchCursor;

    public DialogIdAllocator(long rangeStart, long rangeEnd) {
        configure(rangeStart, rangeEnd);
    }

    public synchronized void configure(long rangeStart, long rangeEnd) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.rangeSize = (int) (rangeEnd - rangeStart + 1);
        this.used = new BitSet(rangeSize);
        this.searchCursor = 0;
    }

    public synchronized Long allocate() throws TCAPException {
        if (used.cardinality() >= rangeSize) {
            throw new TCAPException("No available transaction id in configured range");
        }

        for (int i = 0; i < rangeSize; i++) {
            int index = (int) ((searchCursor + i) % rangeSize);
            if (!used.get(index)) {
                used.set(index);
                searchCursor = (index + 1L) % rangeSize;
                return rangeStart + index;
            }
        }

        throw new TCAPException("No available transaction id in configured range");
    }

    public synchronized boolean tryReserve(long id) {
        int index = toIndex(id);
        if (index < 0 || used.get(index)) {
            return false;
        }
        used.set(index);
        return true;
    }

    public synchronized void release(long id) {
        int index = toIndex(id);
        if (index >= 0) {
            used.clear(index);
        }
    }

    public synchronized void markUsed(long id) {
        int index = toIndex(id);
        if (index >= 0) {
            used.set(index);
        }
    }

    public synchronized void reset() {
        used.clear();
        searchCursor = 0;
    }

    public synchronized int getUsedCount() {
        return used.cardinality();
    }

    private int toIndex(long id) {
        if (id < rangeStart || id > rangeEnd) {
            return -1;
        }
        return (int) (id - rangeStart);
    }
}
