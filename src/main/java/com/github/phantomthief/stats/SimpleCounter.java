/**
 * 
 */
package com.github.phantomthief.stats;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * @author w.vela
 */
public class SimpleCounter {

    private final AtomicLong count = new AtomicLong();

    private final AtomicLong cost = new AtomicLong();

    private final long resetTime = System.currentTimeMillis();

    private void doStats(long cost) {
        this.count.incrementAndGet();
        this.cost.addAndGet(cost);
    }

    public long getCount() {
        return count.get();
    }

    public long getCost() {
        return cost.get();
    }

    public long getResetTime() {
        return resetTime;
    }

    public static Consumer<SimpleCounter> stats(long cost) {
        return counter -> counter.doStats(cost);
    }

    public static UnaryOperator<SimpleCounter> resetter() {
        return old -> new SimpleCounter();
    }

}
