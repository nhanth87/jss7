package org.restcomm.protocols.ss7.scheduler.distributed.event;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Simple in-process pub/sub for {@link TimerExpiredEvent} notifications.
 */
public final class TimerEventBus {

    private final CopyOnWriteArrayList<Consumer<TimerExpiredEvent>> subscribers =
            new CopyOnWriteArrayList<Consumer<TimerExpiredEvent>>();

    public void subscribe(Consumer<TimerExpiredEvent> listener) {
        if (listener != null) {
            subscribers.addIfAbsent(listener);
        }
    }

    public void unsubscribe(Consumer<TimerExpiredEvent> listener) {
        if (listener != null) {
            subscribers.remove(listener);
        }
    }

    public void publish(TimerExpiredEvent event) {
        if (event == null) {
            return;
        }
        for (Consumer<TimerExpiredEvent> subscriber : subscribers) {
            try {
                subscriber.accept(event);
            } catch (RuntimeException ignored) {
                // isolate subscriber failures
            }
        }
    }
}
