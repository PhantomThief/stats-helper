/**
 * 
 */
package com.github.phantomthief.stats.n.counter;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

/**
 * @author w.vela
 */
public class SimpleCounter implements Duration {

    private final AtomicLong count = new AtomicLong();
    private final AtomicLong cost = new AtomicLong();
    private long duration;

    public SimpleCounter(long duration) {
        this.duration = duration;
    }

    public static Consumer<SimpleCounter> stats(long cost) {
        return counter -> counter.doStats(cost);
    }

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

    public double getQPS() {
        return (double) count.get() / duration * 1000;
    }

    @Override
    public String toString() {
        return "count:" + count + ", cost:" + cost + ", avgCost:" + (double) (cost.get())
                / count.get() + ", duration:"
                + PeriodFormat.getDefault().print(new Period(duration));
    }

    @Override
    public long duration() {
        return duration;
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

}
