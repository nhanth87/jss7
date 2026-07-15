package org.restcomm.protocols.ss7.map.load;

import java.util.concurrent.atomic.AtomicLong;

public class Counter {

    private final String name;
    private final AtomicLong counter;

    public Counter(String name) {
        this.name = name;
        this.counter = new AtomicLong();
    }

    public long incrementAndGet() {
        return this.counter.incrementAndGet();
    }

    public long get() {
        return this.counter.get();
    }

    /** Expose underlying AtomicLong for zero-contention reads in TUI. */
    public AtomicLong atomic() {
        return this.counter;
    }

    public String getName() {
        return this.name;
    }
}
