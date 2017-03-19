package ru.skuptsov.robot.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
public class Completion {
    private static final int INITIAL_PHASE = 0;
    private final AtomicInteger phase = new AtomicInteger();

    public void register() {
        phase.incrementAndGet();
    }

    public void complete() {
        if (phase.intValue() == INITIAL_PHASE) {
            throw new IllegalArgumentException("Can't deregister not registered");
        }
        phase.decrementAndGet();
    }

    public boolean isAllCompleted() {
        return phase.intValue() == INITIAL_PHASE;
    }
}
